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
public class Response {
    String response;
    double accuracy;
    int time;
    
    public Response(String r, double a, int t){
        response = r;
        accuracy = a;
        time = t;
    }
    
    public String getResponse(){
        return response;
    }
    public void setResponse(String r){
        response = r;
    }
    
    public double getAccuracy(){
        return accuracy;
    }
    public void setAccuracy(double a){
        this.accuracy = a;
    }

    public int getTime(){
        return time;
    }
    public void setTime(int time){
        this.time = time;
    }

}
