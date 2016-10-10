/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package userstudy;

import org.w3c.dom.DOMException;

/**
 *
 * @author Mershack
 */
public class StandardTestDetail extends UserFile{
    private String responseInterface;
    private String responseValidationInterface;
    
    public StandardTestDetail(String name, String description, String sourceDirectory, String sourceFile
    ,String responseInterface, String responseValidationInterface, String url){
        super(name, description, sourceDirectory, sourceFile, url);
        
        this.responseInterface = responseInterface;
        this.responseValidationInterface = responseValidationInterface;
    }
    
}
