/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Misc;

/**
 *
 * @author 
 */
public class StringEx {
    
    public static boolean isInt(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (!((name.charAt(i) >= '0') && (name.charAt(i) <= '9'))) {
                return (false);
            }
        }
        return (true);
    }
    
}
