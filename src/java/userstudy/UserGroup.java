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
public class UserGroup {
    Condition[] conditions;
    public UserGroup(Condition[] conditions){
      
        this.conditions = conditions;
    }
    
    public Condition[] getConditions(){
        return this.conditions;
    }
    
    public void setCondition(Condition[] cond){
        this.conditions = cond;
    }
    
    public String toString(){
        String str = "";
        for (int  i=0; i<conditions.length; i++){
            if (i!=0) str += ";";
            str += conditions[i].getViewer() + "," + conditions[i].getDataset() + "," + conditions[i].getTask();
        }
        return str;
    }
}
