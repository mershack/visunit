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
public class EntryExitResult {
    private String taskName;    
    private BasicResponse[] basicData;
    
    public EntryExitResult(){
        
    }

    public EntryExitResult(String taskName, BasicResponse[] basicData) {
        this.taskName = taskName;
        this.basicData = basicData;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public BasicResponse[] getBasicData() {
        return basicData;
    }

    public void setBasicData(BasicResponse[] basicData) {
        this.basicData = basicData;
    }
}
