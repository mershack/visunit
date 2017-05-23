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
public class BasicResult {
    String taskName;
    String taskType;
    String responseType;
    String viewer;
    String dataset;
    String group;
    Response[][] responses;
    
    public BasicResult(String taskName, String taskType, String responseType,
            String group, String viewer, String dataset, Response[][] responses){
        this.taskName = taskName;
        this.taskType = taskType;
        this.responseType = responseType;
        this.viewer = viewer;
        this.dataset = dataset;
        this.responses = responses;
        this.group = group;
    }
    
    public String getTaskName(){
        return taskName;
    }
    public void setTaskName(String taskName){
        this.taskName = taskName;
    }
    
    public String getTaskType(){
        return taskType;
    }
    public void setTaskType(String taskType){
        this.taskType = taskType;
    }

    
    public String getResponseType(){
        return responseType;
    }
    public void setResponseType(String rt){
        this.responseType= rt;
    }

     public String getGroup(){
        return group;
    }
    public void setGroup(String g){
        this.group = g;
    }    
    
     public String getViewer(){
        return viewer;
    }
    public void setViewer(String viewer){
        this.viewer = viewer;
    }
    
    public String getDataset(){
        return dataset;
    }
    public void setDataset(String d){
        this.dataset = d;
    }
    
    public Response[][] getResponses(){
        return responses;
    }
    public void setResponses(Response[][] r){
        this.responses= r;
    }
    
    public void addUserResponses(Response[] r){
        Response[][] newr = new Response[responses.length+1][];
        for (int i=0; i<responses.length; i++)
            newr[i] = responses[i];
        newr[responses.length] = r;
        responses = newr;
    }




}
