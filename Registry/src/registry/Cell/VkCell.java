package registry.Cell;

import registry.RegistryBuffer;
import registry.element.DataElement;
import registry.element.DataElementCollection;
import registry.element.DataElementMap;
import registry.element.ElementType;
import registry.element.IntDataElement;
import registry.element.NumberDataElement;
import utils.NumFormat;

/**
 *
 * Defines a key value list (vk) cell
 */
public class VkCell extends RegistryCell {

    protected enum Element { E_NAME_LENGTH, E_DATA_LENGTH, E_OFFSET_DATA, E_DATA_TYPE, E_COMPRESSED, E_VALUE_NAME; }
    protected boolean inline;
    protected DataElementMap elements;

    /**
     * Constructor
     * 
     * @param offset 
     */
    public VkCell(int offset) {
        super(offset);
    }

    @Override
    public RegistryCellType getCellType() {
        return (RegistryCellType.T_VK);
    }

    @Override
    public DataElementCollection elements() {
        return(elements);
    }
    
    @Override
    public void elements(DataElementCollection collection) {
        this.elements = (DataElementMap) collection;
    }
        
    /**
     * Creates and loads cell attributes
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        // call superclass to load cell size and signature
        super.loadHeader(buffer);
        
        // create and load attribute elements
        elements = new DataElementMap(Element.values());
        elements.setElement(Element.E_NAME_LENGTH, DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x06, "Value Name Length", false)).read(offset, buffer);
        elements.setElement(Element.E_DATA_LENGTH, DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x08, "Data Length", false)).read(offset, buffer);
        elements.setElement(Element.E_OFFSET_DATA, DataElement.makeDataElement(ElementType.E_INT, 0, 0x0c, "Offset to Data", true)).read(offset, buffer);
        elements.setElement(Element.E_DATA_TYPE, DataElement.makeDataElement(ElementType.E_INT, 0, 0x10, "Data Type", false)).read(offset, buffer);
        elements.setElement(Element.E_COMPRESSED, DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x14, "Flags", false)).read(offset, buffer);

        // check if value name is ASCII or UNICODE
        int l = (elementValue(Element.E_NAME_LENGTH) != 0 ? elementValue(Element.E_NAME_LENGTH) : 0);
        if ((elementValue(Element.E_COMPRESSED) & 0x01) != 0) {
            elements.setElement(Element.E_VALUE_NAME, DataElement.makeDataElement(ElementType.E_ASCIISZ, l, 0x18, "Value Name", false)).read(offset, buffer);
        } else {
            elements.setElement(Element.E_VALUE_NAME, DataElement.makeDataElement(ElementType.E_UNICODESZ, l, 0x18, "Value Name", false)).read(offset, buffer);
        }
        
        // if data length is 4 bytes or less, offset contains actual data
        inline = elementValue(Element.E_DATA_LENGTH) <= 4;
        if (inline) {
            elements.get(Element.E_OFFSET_DATA).setFollow(false);
            elements.get(Element.E_OFFSET_DATA).setLabel("Inline Data");
        }
        return(true);
    }

    /**
     * Get specified attribute as int
     * 
     * @param e
     * @return 
     */
    protected int elementValue(Element e) {
        return (((NumberDataElement) elements.get(e)).getValueI());
    }

    @Override
    public String toString() {
        return ("[offset=" + NumFormat.numToHex(offset) + ",size=" + NumFormat.numToHex(size) + "] Value Key (vk) Cell: " + getValueName());
    }

    /**
     * Get value name
     * 
     * @return 
     */
    public String getValueName() {
        return (elements.get(Element.E_VALUE_NAME).toString());
    }
    
    /**
     * Set value name
     * 
     * @param name 
     */
    public void setValueName(String name) {
        elements.get(Element.E_VALUE_NAME).setData(name);
        ((NumberDataElement) elements.get(Element.E_NAME_LENGTH)).setValue(name.length());
    }    
    
    /**
     * Return amount of unused space in the cell
     * 
     * @return 
     */
    public int getSlack() {
        return(Math.abs(size) - 0x18 - getNameLength() - 1);
    }
    
    /**
     * Get pointer to data cell
     * 
     * @return 
     */
    public int getOffsetData() {
        return (elementValue(Element.E_OFFSET_DATA));
    }
    
    /**
     * Set pointer to data cell
     * 
     * @param value 
     */
    public void setOffsetData(int value) {
        ((IntDataElement) elements.get(Element.E_OFFSET_DATA)).setValueI(value);
    }

    /**
     * Get data length
     * 
     * @return 
     */
    public int getDataLength() {
        return (elementValue(Element.E_DATA_LENGTH));
    }

    /**
     * Get data type
     * 
     * @return 
     */
    public int getDataType() {
        return (elementValue(Element.E_DATA_TYPE));
    }

    /**
     * Get name length
     * 
     * @return 
     */
    public int getNameLength() {
        return (elementValue(Element.E_NAME_LENGTH));
    }

    /**
     * Get inline flag
     * 
     * @return 
     */
    public boolean isInline() {
        return (inline);
    }
    
    /**
     * Get array of child pointers
     * 
     * @return 
     */
    @Override
    public int[] getChildIndexes() {
        if (isInline()) {
            return(new int[0]);
        } else {
            return(new int[]{getOffsetData()});
        }
    }
    
    /**
     * Get child cells
     * 
     * @param buffer
     * @return 
     */
    @Override
    public RegistryCell[] getChildCells(RegistryBuffer buffer) {
        if (isInline()) {
            return(new RegistryCell[0]);
        } else {
            return(new RegistryCell[]{RegistryCell.makeDataCell(getOffsetData(), buffer)});
        }        
    }
       
}
