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
public class TaskInstancesDetails {
    private String taskproto;
    private String viewer;
    private String dataset;
    private String instanceCount;
    
    public TaskInstancesDetails(String task, String viewer, String dataset, String instanceCount){
        this.taskproto = task;
        this.viewer= viewer;
        this.dataset = dataset;
        this.instanceCount = instanceCount;
    }

    public String getTaskproto() {
        return taskproto;
    }

    public void setTask(String task) {
        this.taskproto = task;
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

    public String getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(String instanceCount) {
        this.instanceCount = instanceCount;
    }
    
    
    
    
}
