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
public class StandardizedTest {
    private String url; //the url of the standardized test file
    private String userRespInterface; // the name of the interface for getting the responses of users.
    private String userPerformanceInterface; //the name of the interface for validating the responses of users.
    
    public StandardizedTest(String url, String userResp, String userPerf){
        this.url = url;
        this.userRespInterface = userResp;
        this.userPerformanceInterface = userPerf;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserRespInterface() {
        return userRespInterface;
    }

    public void setUserRespInterface(String userRespInterface) {
        this.userRespInterface = userRespInterface;
    }

    public String getUserPerformanceInterface() {
        return userPerformanceInterface;
    }

    public void setUserPerformanceInterface(String userPerformanceInterface) {
        this.userPerformanceInterface = userPerformanceInterface;
    }
    
    
}
