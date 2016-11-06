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
public class RegularResult {
    private String taskName;
    private String viewer;
    private String dataset;
    private BasicResponse[] basicData;
    
    public RegularResult(){
        
    }
    
    public RegularResult(String taskName, String viewer, String dataset, BasicResponse[] basicData){
        this.taskName = taskName;
        this.viewer = viewer;
        this.dataset = dataset;
        this.basicData = basicData;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getViewer() {
        return viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public BasicResponse[] getBasicData() {
        return basicData;
    }

    public void setBasicData(BasicResponse[] basicData) {
        this.basicData = basicData;
    }
    
    
}
