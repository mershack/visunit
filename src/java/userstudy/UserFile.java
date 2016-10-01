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
public class UserFile {
    private String name;
    private String description;
    private String sourceDirectory;
    private String sourceFile;
    
    public UserFile(String name, String description, String sourceDirectory, String sourceFile){
        this.name = name;
        this.description = description;
        this.sourceDirectory = sourceDirectory;
        this.sourceFile = sourceFile;
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

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }    
}
