/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import java.util.ArrayList;

/**
 *
 * @author Mershack
 */
public class Task {
    
    private String name;
    private String question;
    private String description;
    private Input[] inputs;
    private Answer answer;
    
 
    public Task(String name, String question, String description, Input[] inputs, Answer answer) {

        this.name = name;
        this.question = question;
        this.description = description;
        this.answer = answer;
        this.inputs = inputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String taskname) {
        this.name = taskname;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String taskQuestion) {
        this.question = taskQuestion;
    }

    public Input[] getInputs() {
        return inputs;
    }

    public void setInputTypes(Input[] inputs) {
        this.inputs = inputs;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String taskDescription) {
        this.description = taskDescription;
    }

    //getting the answertype
    public Answer getAnswer() {
        return answer;
    }

    //setting the answer type
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
