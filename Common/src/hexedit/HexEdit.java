/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hexedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import utils.NumFormat;

/**
 *
 * @author 
 */
public class HexEdit extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1264007031748918073L;

    private byte[] buffer;
    private int bytesPerRow = 16;
    private int bytesPerPage = 256;
    private int bufferLength = 0; // buffer maximum length
    private int currPage = -1; // offset into buffer
    private int pageLength = 0; // length of current page
    private InputType inputType = InputType.I_HEX;
    private int digitCnt = 0;
    private int row = 0, column = 0;  // current row and column of selected cell
    
    //*************************
    // font mertics and offsets
    //*************************
    private int charWidth, charHeight;  // font height and width
    private int vspace, // vertical spacing
            hspace; //horizontal spacing 
    private int dividerx;   // x offset of divider
    private int xmargin, ymargin;
    private int xadjust;

    private final char[] cbuf = new char[2];
    private static final Font font = new Font("Monospaced", Font.PLAIN, 12);

    private enum InputType {

        I_HEX, I_CHAR
    };

    public HexEdit(final byte[] buffer) {
        init(buffer);
        setLayout(new GridLayout(1, 1));
        addKeyListener(this);
    }

    public HexEdit(final byte[] buffer, int bytesPerRow, int bytesPerPage) {
        this(buffer);
        this.bytesPerRow = bytesPerRow;
        this.bytesPerPage = bytesPerPage;
    }
    
    private void init(byte[] buffer) {
        this.buffer = buffer;
        bufferLength = buffer.length;
        setBackground(Color.WHITE);
        setFont(font);
        calcMetrics();
        setPreferredSize(new Dimension(preferredWidth(), (charHeight + vspace) * ((bytesPerPage / bytesPerRow)) + ymargin + vspace));
        setFocusTraversalKeysEnabled(false);
        currPage = -1; // offset into buffer
        pageLength = 0; // length of current page
        inputType = InputType.I_HEX;
        digitCnt = 0;
        row = 0; 
        column = 0;  // current row and column of selected cell  
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    }

    public void reset(byte[] buffer) {
        init(buffer);
        repaint();
    }
    
    private void calcMetrics() {
        FontMetrics fontMetrics = getFontMetrics(font);
        charWidth = fontMetrics.charWidth('0');
        charHeight = fontMetrics.getHeight() - fontMetrics.getDescent();
        hspace = (int) ((float) charWidth * 0.3) + 1;
        vspace = (int) ((float) charHeight * 0.2) + 1;
        xmargin = charWidth * 4 + hspace;
        ymargin = (charHeight + vspace) * 2;
        xadjust = charWidth / 2;
        dividerx = (charWidth * 2 * bytesPerRow) + (hspace * bytesPerRow) + xmargin + xadjust;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    protected byte getByte(int index) {
        if (currPage == -1) {
            nextPage();
        }
        return (buffer[(currPage * bytesPerPage) + index]);
    }

    protected void nextPage() {
        if (((currPage + 1) * bytesPerPage) < bufferLength) {
            currPage++;
            setPage();
        }
    }

    protected void prevPage() {
        if (currPage > 0) {
            currPage--;
            setPage();
        }
    }

    protected void setPage() {
        pageLength = (bufferLength - (currPage * bytesPerPage));
        if (pageLength > bytesPerPage) {
            pageLength = bytesPerPage;
        }
    }

    public int getPageLength() {
        if (currPage == -1) {
            nextPage();
        }
        return (pageLength);
    }

    public int getBytesPerRow() {
        return (bytesPerRow);
    }

    private int preferredWidth() {
        return (dividerx + (xmargin * 2) + ((charWidth + hspace) * getBytesPerRow()));
    }

    public int getPreferredWidth() {
        return (preferredWidth());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(dividerx, ymargin - charHeight, dividerx, (bytesPerPage / bytesPerRow) * (charHeight + vspace + 1));
        paintPanel(g);
        paintStatus(g);
    }

    protected int calcOffset() {
        return ((currPage * bytesPerPage) + (row * bytesPerRow) + column);
    }

    protected void paintStatus(Graphics g) {
        String s = "Offset: " + NumFormat.numToHex(calcOffset());
        Point p = new Point(2, ymargin + ((bytesPerPage / bytesPerRow) * (charHeight + vspace)));
        g.drawString(s, p.x, p.y);
    }

    protected void drawHeader(Graphics g) {
        g.setColor(Color.BLUE);
        for (int i = 0; i < bytesPerRow; i++) {
            Point p = new Point(i * (charWidth * 2 + hspace) + xmargin + xadjust, ymargin - charHeight - vspace);
            g.drawString(NumFormat.numToHex((byte) i), p.x, p.y);
        }
        g.setColor(Color.BLACK);
    }

    protected void paintPanel(Graphics g) {
        int x = 0;
        int y = 0;
        drawHeader(g);
        for (int i = 0; i < getPageLength(); i++) {
            if (x == 0) {
                // draw offset markers
                g.setColor(Color.BLUE);
                Point p = new Point(2, y * (charHeight + vspace) + ymargin);
                g.drawString(NumFormat.numToHex((short) (y * bytesPerRow)), p.x, p.y);
                g.setColor(Color.BLACK);
            }
            drawHex(i, x, y, g);
            drawChar(i, x, y, g);
            if (++x >= getBytesPerRow()) {
                x = 0;
                y++;
            }
        }
    }

    protected Point getPointHex(int x, int y) {
        return (new Point(x * (charWidth * 2 + hspace) + xmargin + xadjust, y * (charHeight + vspace) + ymargin));
    }

    protected void drawHex(int i, int x, int y, Graphics g) {
        byte b = getByte(i);
        byteToHex(b, cbuf);
        Point charPoint = getPointHex(x, y);
        if ((x == column) && (y == row)) {
            g.setColor(Color.BLUE);
            if (inputType == InputType.I_HEX) {
                g.fillRect(charPoint.x, charPoint.y + 2 - charHeight, (charWidth + hspace * 2), charHeight);
                g.setColor(Color.WHITE);
            } else {
                g.drawRect(charPoint.x, charPoint.y + 2 - charHeight, (charWidth + hspace * 2), charHeight);
            }
        } else {
            g.setColor(Color.BLACK);
        }
        g.drawChars(cbuf, 0, 2, charPoint.x, charPoint.y);
    }

    protected Point getPointChar(int x, int y) {
        return (new Point(x * (charWidth + hspace) + dividerx + xadjust, y * (charHeight + vspace) + ymargin));
    }

    protected void drawChar(int i, int x, int y, Graphics g) {
        byte b = getByte(i);
        cbuf[0] = convertChar(b);
        Point charPoint = getPointChar(x, y);
        if ((x == column) && (y == row)) {
            g.setColor(Color.BLUE);
            if (inputType == InputType.I_CHAR) {
                g.fillRect(charPoint.x, charPoint.y + 2 - charHeight, charWidth + hspace, charHeight);
                g.setColor(Color.WHITE);
            } else {
                g.drawRect(charPoint.x, charPoint.y + 2 - charHeight, charWidth + hspace, charHeight);
            }
        } else {
            g.setColor(Color.BLACK);
        }
        g.drawChars(cbuf, 0, 1, charPoint.x, charPoint.y);
    }

    public void paintCharPanel(Graphics g) {
        super.paintComponent(g);
        int x = 0;
        int y = 0;
        for (int i = 0; i < getPageLength(); i++) {
            byte b = getByte(i);
            cbuf[0] = convertChar(b);
            int xofs = x * hspace + dividerx;
            int yofs = y * vspace;
            if ((x == row) && (y == column)) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.BLACK);
            }
            g.drawString(new String(cbuf), xofs + xmargin, yofs + ymargin);
            if (x++ >= getBytesPerRow()) {
                x = 0;
                y++;
            }
        }
    }

    private static final String hexChars = "0123456789abcdef";

    private void byteToHex(byte b, char[] buffer) {
        buffer[1] = hexChars.charAt(b & 0x0f);
        buffer[0] = hexChars.charAt((b & 0xf0) >> 4);
    }

    protected char convertChar(byte b) {
        if (b > 27 && b < 128) {
            return ((char) b);
        } else {
            return ('.');
        }
    }

    @Override
    public void keyReleased(KeyEvent evt) {

    }

    @Override
    public void keyPressed(KeyEvent evt) {
        int kc = evt.getKeyCode();
        switch (kc) {
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_UP:
                moveUp();
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                nextPage();
                repaint();
                break;
            case KeyEvent.VK_PAGE_UP:
                prevPage();
                repaint();
                break;
            case KeyEvent.VK_ENTER:
                moveNext();
                break;
            case KeyEvent.VK_TAB:
                if (inputType == InputType.I_HEX) {
                    inputType = InputType.I_CHAR;
                } else {
                    inputType = InputType.I_HEX;
                }
                repaint();
                break;
            default:
                handleChar(evt.getKeyChar());
        }
    }

    public void handleChar(char ch) {
        switch (inputType) {
            case I_HEX:
                char c = Character.toLowerCase(ch);
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
                    byte b = buffer[calcOffset()];
                    b = (byte) ((byte) ((b & 0x0f) << 4) + (byte) (hexChars.indexOf(c) & 0x0f));
                    buffer[calcOffset()] = b;
                    if (digitCnt++ >= 1) {
                        moveNext();
                        digitCnt = 0;
                    } else {
                        repaint();
                    }
                }
                break;
            case I_CHAR:
                buffer[calcOffset()] = (byte) ch;
                moveNext();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {
    }

    protected void moveLeft() {
        if (row * bytesPerRow + column > 0) {
            if (column > 0) {
                column--;
                repaint();
            } else {
                row--;
                column = bytesPerRow - 1;
                repaint();
            }
        }
    }

    protected void moveRight() {
        if (row * bytesPerRow + column < pageLength - 1) {
            if (column < bytesPerRow - 1) {
                column++;
                repaint();
            } else {
                row++;
                column = 0;
                repaint();
            }
        }
    }

    protected void moveDown() {
        if ((row * bytesPerRow) + column + bytesPerRow < pageLength) {
            row++;
            repaint();
        }
    }

    protected void moveUp() {
        if (row > 0) {
            row--;
            repaint();
        }
    }

    protected void moveNext() {
        if ((row * bytesPerRow) + column < pageLength - 1) {
            moveRight();
        } else {
            if (calcOffset() < bufferLength) {
                row = 0;
                column = 0;
                nextPage();
                repaint();
            }
        }
    }

}
