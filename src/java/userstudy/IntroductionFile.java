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
public class IntroductionFile {

    private String fileURL;   //the url of the introduction file
    private String fileCondition;  //the viewer condition that the file will be used for

    /**
     *
     * @param url the url of the introduction file
     * @param cond the viewer condition that the introduction file will be used
     * for
     */
    public IntroductionFile(String url, String cond) {
        fileURL = url;
        fileCondition = cond;
    }

    /**
     * getting the file url
     *
     * @return the url of the file
     */
    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getFileCondition() {
        return fileCondition;
    }

    public void setFileCondition(String fileCondition) {
        this.fileCondition = fileCondition;
    }

}
