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
public class TaskInstance {

    private String[] inputs;
    private String answer;
    private String[] options;  //For task instances that have dynamic options

    public TaskInstance() {

    }

    public TaskInstance(String inputs[], String answer, String[] options) {
        this.inputs = inputs;
        this.answer = answer;
        this.options = options;
        
    }

    public String[] getInputs() {
        return inputs;
    }

    public void setInputs(String[] inputs) {
        this.inputs = inputs;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }  
    
    @Override
    public String toString() {
        return "nodes: " + this.inputs + "  "
                + "answer: " + this.answer + "  ";
    }
    
}
