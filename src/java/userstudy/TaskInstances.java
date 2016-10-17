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
public class TaskInstances {
    private TaskInstance[] instances;
    
    public TaskInstances(){
        instances = new TaskInstance[0];
    }
    
    public TaskInstances(TaskInstance[] instances){
        this.instances = instances;
    }

    public TaskInstance[] getInstances() {
        return instances;
    }

    public void setInstances(TaskInstance[] instances) {
        this.instances = instances;
    }
    
    public void addNewInstance(TaskInstance instance){
        //increment the size of the array by 1 and append the item to the end of the array.
        
        TaskInstance[] instances2 = instances;
        
        instances = new TaskInstance[instances2.length + 1];
        
        for(int i=0; i<instances2.length; i++){
            instances[i] = instances2[i];
        }
        
        instances[instances.length-1] = instance; //append the last item.       
        
    }
    
    
}
