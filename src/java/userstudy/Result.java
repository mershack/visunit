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
public class Result {
    String type;
    Task task;
    TaskInstance taskInstance;
    String response;
    double accuracy;
    String viewer;
    String dataset;
    String group;
    int time;
    
    public Result(String type, Task task, TaskInstance taskInstance, String group, String viewer, String dataset, String response,
            double accuracy, int time){
        this.type = type;
        this.task = task;
        this.taskInstance = taskInstance;
        this.group = group;
        this.viewer = viewer;
        this.dataset = dataset;
        this.response = response;
        this.accuracy = accuracy;
        this.time = time;
    }
    
    public String getType(){
        return this.type;
    }
    public void setType(String t){
        this.type = t;
    }
    
    public Task getTask(){
        return this.task;
    }
    public void setTask(Task t){
        this.task = t;
    }

    public TaskInstance getTaskInstance(){
        return this.taskInstance;
    }
    public void setTaskInstance(TaskInstance t){
        this.taskInstance = t;
    }
    public String getGroup(){
        return group;
    }
    public void setGroup(String v){
        this.group = v;
    }    
    public String getViewer(){
        return viewer;
    }
    public void setViewer(String v){
        this.viewer = v;
    }
    public String getDataset(){
        return dataset;
    }
    public void setDataset(String v){
        this.dataset = v;
    }    
    
    public String getResponse(){
        return this.response;
    }
    public void setResponse(String t){
        this.response = t;
    }

    public double getAccuracy(){
        return this.accuracy;
    }
    public void setAccuracy(double t){
        this.accuracy = t;
    }
    
    public int getTime(){
        return this.time;
    }
    public void setTime(int t){
        this.time = t;
    }
    
}
