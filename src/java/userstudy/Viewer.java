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
public class Viewer {
    private String name;
    private String description;
    private String source;
    private String url;
    private String introduction;
    
    public Viewer(){
        
    }
    
    public Viewer(String name, String description, String source,
            String url, String introduction){
        this.name = name;
        this.description = description;
        this.source= source;
        this.introduction = introduction;
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIntroduction(){
        return this.introduction;
    }
    public void setIntroduction(String intro){
        this.introduction = intro;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
