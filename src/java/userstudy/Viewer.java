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
    
    private String name;   // name of the viewer
    private String description;  // a description of the viewer
    private String sourceDirectory;  // the source directory of the viewer
    private String sourceFile; // the source file of the directory
    
    public Viewer(String name, String description, String srcdir, String srcFile){
        this.name = name;
        this.description = description;
        this.sourceDirectory = srcdir;
        this.sourceFile = srcFile;
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
