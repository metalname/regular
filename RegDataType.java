/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package registry.value;

/**
 *
 * @author 
 */
public enum RegDataType {
    REG_UNKNOWN, 
    REG_SZ, 
    REG_EXPAND_SZ, 
    REG_BINARY, 
    REG_DWORD, 
    REG_DWORD_BE, 
    REG_LINK, 
    REG_MULTI_SZ, 
    REG_RESOURCE_LIST, 
    REG_FULL_RESOURCE_DESCRIPTOR, 
    REG_RESOURCE_REQUIREMENTS_LIST,
    REG_QWORD;
}
