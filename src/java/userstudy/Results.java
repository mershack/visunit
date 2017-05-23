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
public class Results {
    Result[] results;
    
    public Results(){
        this.results = new Result[0];
    }
    public Results(Result[] results){
        this.results = results;
    }
    
    public Result[] getResults(){
        return results;
    }
    public void setResults(Result[] results){
        this.results = results;
    }
}
