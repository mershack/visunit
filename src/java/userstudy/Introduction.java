/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package userstudy;

/**
 *
 * @author Mershack
 */
public class Introduction extends UserFile{
    
    private String match;
    public Introduction(String name, String description, String sourceDirectory, String sourceFile, String match, String url){
        super(name, description, sourceDirectory, sourceFile, url);
        this.match = match;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }
    
}
