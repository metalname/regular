/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.System.USBDrives;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import utils.ChunkList;

/**
 *
 * @author 
 */
public class LogFile {

    private final String name;
    private ChunkList<String> buffer;
    private final ArrayList<Section> sections = new ArrayList<>();
    private final ArrayList<Section> deletedSections = new ArrayList<>();
    private final File file;

    public LogFile(String name) {
        this.name = name;
        file = new File(name);
    }

    public File file() {
        return (file);
    }

    public boolean changed() {
        return (deletedSections.size() > 0);
    }

    private enum State {E_START, E_INSECTION, E_ENDSECTION; }
    public void read() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(name));
        buffer = new ChunkList<>();
        String text;
        State state = State.E_START;
        Section section = null;
        int line = 0;
        while ((text = reader.readLine()) != null) {
            switch (state) {
                case E_START:
                if (text.length() > 0) {
                    section = new Section();
                    section.start(line);
                    section.scanLine(text);
                    state = State.E_INSECTION;
                }
                break;
                case E_INSECTION:
                    if (text.length() > 0) {
                        section.scanLine(text);
                    } else {
                        state = State.E_ENDSECTION;
                    }
                break;
                case E_ENDSECTION:
                    if (text.length() > 0) {
                        section.end(line - 1);
                        sections.add(section);
                        section = new Section();
                        section.start(line);
                        section.scanLine(text);
                        state = State.E_INSECTION;
                    }
            }
            line++;        
            buffer.add(text);        
        }
        reader.close();
    }

    public void save() throws IOException {
        // write out log file, skipping deleted entries
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        int line = 0;
        for (Section range : deletedSections) {
            while (line < buffer.size()) {
                if (line < range.start()) {
                    writer.write(buffer.get(line));
                    writer.newLine();
                } else if (line > range.end()) {
                    break;
                }
                line++;
            }
        }
        // write out any remaning lines
        while (line < buffer.size()) {
            writer.write(buffer.get(line++));
            writer.write("\r\n");
        }
        writer.close();
        deletedSections.clear();
    }

    public boolean exists() {
        return (file.exists());
    }

    public Section deleteSection(Section section) {
        deletedSections.add(section);
        return (section);
    }
    
    public ArrayList<Section> sections() {
        return(sections);
    }

    public String name() {
        return (name);
    }

    public String get(int index) {
        return (buffer.get(index));
    }

    public int size() {
        return (buffer.size());
    }
}
