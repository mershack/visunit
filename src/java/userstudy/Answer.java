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
public class Answer {
     String type;
     String[] options;
     String correctness;
     String customTypeName;
     String correctnessCheckingInterface;
     
     public Answer(String type, String[] options, String correctness, String customTypeName, String correctnessCheckingInterface){
         this.type = type;
         this.options = options;
         this.correctness = correctness;
         this.customTypeName = customTypeName;
         this.correctnessCheckingInterface = correctnessCheckingInterface;
     }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }    

    public String getCorrectness() {
        return correctness;
    }

    public void setCorrectness(String correctness) {
        this.correctness = correctness;
    }
    
    public boolean isCorrect(){
        if (this.correctness.toLowerCase().equals("yes"))
            return true;
        return false;
    }

    public String getCustomTypeName() {
        return customTypeName;
    }

    public void setCustomTypeName(String customTypeName) {
        this.customTypeName = customTypeName;
    }

    public String getCorrectnessCheckingInterface() {
        return correctnessCheckingInterface;
    }

    public void setCorrectnessCheckingInterface(String correctnessCheckingInterface) {
        this.correctnessCheckingInterface = correctnessCheckingInterface;
    }   
    
}

