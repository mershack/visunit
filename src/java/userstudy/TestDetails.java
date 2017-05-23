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
public class TestDetails {
    private String name; 
    private double cutoff;
   
    
    public TestDetails(String name, double cutoff){
        this.name = name;
        this.cutoff = cutoff;
    }

    public String getName() {
        return name;
    }

    public void setSource(String name) {
        this.name = name;
    }
    
    public double getCutoff() {
        return cutoff;
    }

    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }
}
