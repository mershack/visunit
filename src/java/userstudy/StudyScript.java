package userstudy;


import java.util.ArrayList;
import java.util.Date;
import userstudy.Dataset;
import userstudy.Task;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Radu
 */
public class StudyScript {
    
    ArrayList<String> script;   //contains a list of json strings that represent
                        //steps in the study (e.g., showIntroduction, showTest - which, etc.)
    
    String study;
    String experimenter; //who's study is it?
    
    String userId;
    
    Results results;
    
    ArrayList<String> usedInTraining;
    
    int step;
    
    long lastAccessed = 0;
    
    int groupIndex;
    
    StudyScript(String userId, String study, String experimenter){
       script = new ArrayList<String>();
       step = -1;
       
       this.userId = userId;
       this.study = study;
       this.experimenter = experimenter;
       this.groupIndex = groupIndex;
       results = new Results();
       
       usedInTraining = new ArrayList();
       
       lastAccessed = new Date().getTime();
    }
    
    public String getStudyName(){
        return study;
    }
    
    public String getExperimenterId(){
        return experimenter;
    }
    
    public void add(String json){
        script.add(json);
    }
    
    public String current(){
        lastAccessed = new Date().getTime();
        
        if (step < 0) step = 0;
        if (step < script.size())            
            return script.get(step);
        return script.get(script.size()-1);
    }
    
    public String next(){
        lastAccessed = new Date().getTime();
        
        step++;
        System.out.println("getting script item " + step);
        if (step < script.size())            
            return script.get(step);
       return script.get(script.size()-1);
    }
    
    public boolean done(){
        lastAccessed = new Date().getTime();
        
        if (step >= script.size()-1) return true;
        return false;
    }
    
    public String finish(){
        lastAccessed = 0;
        
        step = script.size()-1;
        return script.get(step);
    }
    
    public int length(){
        return script.size();
    }
    public void markTaskInstanceAsUsedInTraining(String task, String data, int instance){
        usedInTraining.add(task + "-" + data + "-" + instance);
    }
    
    public boolean taskInstanceWasUsedInTraining(String task, String data, int instance){
        if (usedInTraining.indexOf(task + "-" + data + "-" + instance) >= 0)
            return true;
        return false;
    }
    
    public Results getResults(){
        return results;
    }
    
    public void setResults(){
        this.results = results;
    }
    
    public String getUserId(){
        return userId;
    }
    
    public void setUserId(String userId){
        this.userId = userId;
    }
    
    public void addErrors(ArrayList<String> errs){
        if (errs.size() == 0) return;
        
        //remove duplicate errors:
        for (int i=0; i<errs.size()-1; i++)
            for (int j=i+1; j<errs.size(); j++)
                if (errs.get(i).equals(errs.get(j))){
                    errs.remove(j);
                    j--;
                }
        System.out.println("adding errors " + errs.size());
        for (int i=0; i<script.size(); i++){
            if (script.get(i).indexOf("type':'debug") == 2){
                String sub = script.get(i).substring(0, script.get(i).length()-2);
                System.out.println("found debug: " + sub);
                if (sub.charAt(sub.length()-1) != '[') sub += ",";
                for (int j=0; j<errs.size(); j++){
                    sub += "{'type':'error','message':'" + errs.get(j) + "'}";
                    if (j != errs.size()-1) sub+=",";
                }
                sub += "]}";
                script.remove(i);
                script.add(i,sub);
                System.out.println("debug changed to: " + sub);
                return;
            }
        }
    }
    
    public long getLastAccessed(){
        return lastAccessed;
    }
    
    public int getGroupIndex(){
        return groupIndex;
    }
    public void setGroupIndex(int gr){
        this.groupIndex = gr;
    }
}
