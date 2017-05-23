/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

/**
 *
 * @author Radu
 */
public class Condition {
    String viewer;
    String dataset;
    String task;
    
    public Condition(String viewer, String dataset, String task){
        this.viewer = viewer;
        this.dataset = dataset;
        this.task = task;
    }
    
    public String getViewer(){
        return viewer;
    }
    public void setViewer(String viewer){
        this.viewer= viewer;
    }

    public String getTask(){
        return task;
    }
    public void setTask(String task){
        this.task= task;
    }
    
    public String getDataset(){
        return dataset;
    }
    public void setDataset(String dataset){
        this.dataset= dataset;
    }
    
}
