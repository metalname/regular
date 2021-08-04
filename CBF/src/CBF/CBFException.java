/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF;

/**
 *
 * @author metataro
 */
public class CBFException extends Exception {
    String message;
    
    public CBFException(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return(this.message);
    }
    
}
