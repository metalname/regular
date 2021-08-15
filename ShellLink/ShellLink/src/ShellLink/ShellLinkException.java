/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShellLink;

import java.io.IOException;

/**
 *
 * @author metataro
 */
public class ShellLinkException extends IOException {
    private String message;
    
    public ShellLinkException(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return (message);
    }
}
