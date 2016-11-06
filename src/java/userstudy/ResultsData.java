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
public class ResultsData {
    
    private RegularResult[] regular;
    private EntryExitResult[] entry;
    private EntryExitResult[] exit;
    private StandardizedTestResult[]  standardized; 
    
    public ResultsData(){
       
    }

    public RegularResult[] getRegular() {
        return regular;
    }

    public void setRegular(RegularResult[] regular) {
        this.regular = regular;
    }

    public EntryExitResult[] getEntry() {
        return entry;
    }

    public void setEntry(EntryExitResult[] entry) {
        this.entry = entry;
    }

    public EntryExitResult[] getExit() {
        return exit;
    }

    public void setExit(EntryExitResult[] exit) {
        this.exit = exit;
    }

    public StandardizedTestResult[] getStandardized() {
        return standardized;
    }

    public void setStandardized(StandardizedTestResult[] standardized) {
        this.standardized = standardized;
    }    
}




