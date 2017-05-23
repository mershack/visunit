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
public class BasicResults {
    BasicResult[] basicResults;
    public BasicResults(BasicResult[] basicResults){
        this.basicResults = basicResults;
    }
    
    public BasicResults(){
        basicResults = new BasicResult[]{};
    }
    public void addBasicResult(String group, String viewer, String dataset, String task, String taskType, String responseType){
        BasicResult[] newbr = new BasicResult[basicResults.length+1];
        for (int i=0; i<basicResults.length; i++)
            newbr[i] = basicResults[i];
        newbr[basicResults.length] = new BasicResult(task, taskType, responseType, group, viewer, dataset, new Response[][]{});
        basicResults = newbr;
    }
    
    public BasicResult getBasicResult(String group, String viewer, String dataset, String task){
        for (int i=0; i<basicResults.length; i++)
            if (basicResults[i].getGroup().equals(group) && 
                    basicResults[i].getViewer().equals(viewer) &&
                    basicResults[i].getDataset().equals(dataset) &&
                    basicResults[i].getTaskName().equals(task))
                return basicResults[i];
        return null;
    }
}
