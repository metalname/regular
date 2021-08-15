/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShellLink;

/**
 *
 * @author metataro
 */
public enum ShellFileAttributes {
    
    FILE_ATTRIBUTE_READONLY(1),
    FILE_ATTRIBUTE_HIDDEN(2),
    FILE_ATTRIBUTE_SYSTEM(4),
    Reserved1(8),
    FILE_ATTRIBUTE_DIRECTORY(16),
    FILE_ATTRIBUTE_ARCHIVE(32),
    Reserved2(64),
    FILE_ATTRIBUTE_NORMAL(128),
    FILE_ATTRIBUTE_TEMPORARY(256),
    FILE_ATTRIBUTE_SPARSE_FILE(512),
    FILE_ATTRIBUTE_REPARSE_POINT(1024),
    FILE_ATTRIBUTE_COMPRESSED(2048),
    FILE_ATTRIBUTE_OFFLINE(4096),
    FILE_ATTRIBUTE_NOT_CONTENT_INDEXED(8192),
    FILE_ATTRIBUTE_ENCRYPTED(16384);
    
    public final int mask;

    private ShellFileAttributes(int mask) {
        this.mask = mask;
    }
    
    public int getMask() {
        return(mask);
    }

    
}
