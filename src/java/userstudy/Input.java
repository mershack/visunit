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
public class Input {
    private String typeName;
    private String description; 
    private String specifyInVis;
    private String showInVis;
   
    
    public Input(String type, String description, String specifyInVis, String showInVis){
        this.typeName = type;
        this.description = description;
        this.showInVis = showInVis;
        this.specifyInVis = specifyInVis;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String type) {
        this.typeName = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShowInVis() {
        return showInVis;
    }

    public void setShowInVis(String showInVis) {
        this.showInVis = showInVis;
    }
//
    public String getSpecifyInVis() {
        return specifyInVis;
    }

    public void setSpecifyInVis(String specifyInVis) {
        this.specifyInVis = specifyInVis;
    }
    
    
    
    
}
