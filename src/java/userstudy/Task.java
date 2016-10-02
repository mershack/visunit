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
    private Answer answer;
    private String question;
    private String description;
    private Input[] inputs;
    private String count;
    private String time;
    private String training;       
  
    public Task(String name, Answer answer, String question, String description, Input[] inputs
                , String count, String time, String trainingSize) {
        this.name = name;
        this.answer = answer;
        this.question = question;
        this.description = description;        
        this.inputs = inputs;        
        this.count = count;
        this.time = time;
        this.training = trainingSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answerType) {
        this.answer = answerType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    public Input[] getInputs() {
        return inputs;
    }

    public void setInputs(Input[] inputs) {
        this.inputs = inputs;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrainingSize() {
        return training;
    }

    public void setTrainingSize(String trainingSize) {
        this.training = trainingSize;
    }

   

}
