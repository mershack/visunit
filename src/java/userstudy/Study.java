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
public class Study {
    private String name;
    private String description;
    private String viewerDesign;
    private String dataDesign;
    private String taskDesign;
    private String width;
    private String height;
    private String[] viewers;
    private String[] datasets;
    private IntroductionDetails[] intros;
    private String thankyou;
    private TestDetails[] tests;
    private TaskDetails[] tasks;
    private String[] entryTasks;
    private String[] exitTasks;
    private int resultsCount;
    private String order;
    
    private Condition[] conditions;
    private UserGroup[] userGroups;
    private int nrUsers;            //these two arrays are parallel
    
    public Study(String name, String description, String viewerDesign,
            String dataDesign, String taskDesign, String order, String width, String height, String[] viewers, String[] datasets,
            IntroductionDetails[] intros, String thankyou, TestDetails[] tests, TaskDetails[] tasks,
            String[] entryTasks, String[] exitTasks, Condition[] conditions, UserGroup[] userGroups, int nrUsers){
        this.name = name;
        this.description = description;
        this.viewerDesign = viewerDesign;
        this.dataDesign = dataDesign;
        this.taskDesign = taskDesign;
        this.width = width;
        this.height = height;
        this.viewers = viewers;
        this.datasets = datasets;
        this.intros = intros;
        this.tests = tests;
        this.tasks = tasks;
        this.entryTasks = entryTasks;
        this.exitTasks = exitTasks;        
        this.userGroups = userGroups;
        this.conditions = conditions;
        this.nrUsers = nrUsers;
        this.thankyou = thankyou;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getViewerDesign() {
        return viewerDesign;
    }

    public void setViewerDesign(String viewerDesign) {
        this.viewerDesign = viewerDesign;
    }
    
    public String getTaskDesign() {
        return taskDesign;
    }

    public void setTaskDesign(String taskDesign) {
        this.taskDesign = taskDesign;
    }    

    public String getDataDesign() {
        return dataDesign;
    }

    public void setDataDesign(String dataDesign) {
        this.dataDesign = dataDesign;
    }
    
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String[] getViewers() {
        return viewers;
    }

    public void setViewers(String[] viewers) {
        this.viewers = viewers;
    }

    public String[] getDatasets() {
        return datasets;
    }

    public void setDatasets(String[] datasets) {
        this.datasets = datasets;
    }

    public IntroductionDetails[] getIntros() {
        return intros;
    }

    public void setIntros(IntroductionDetails[] intros) {
        this.intros = intros;
    }
    
    public String getThankyou(){
        return thankyou;
    }
    
    public void setThankyou(String t){
        this.thankyou = t;
    }

    public TestDetails[] getTests() {
        return tests;
    }

    public void setTests(TestDetails[] tests) {
        this.tests = tests;
    }

    public TaskDetails[] getTasks() {
        return tasks;
    }

    public void setTasks(TaskDetails[] tasks) {
        this.tasks = tasks;
    }

    public String[] getEntryTasks() {
        return entryTasks;
    }

    public void setEntryTasks(String[] entryTasks) {
        this.entryTasks = entryTasks;
    }

    public String[] getExitTasks() {
        return exitTasks;
    }

    public void setExitTasks(String[] exitTasks) {
        this.exitTasks = exitTasks;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(int resultsCount) {
        this.resultsCount = resultsCount;
    }
    
    public UserGroup[] getUserGroups(){
        return this.userGroups;
    }
    
    public void setUserGroups(UserGroup[] userGroups){
        this.userGroups = userGroups;
    }
    
    public int getNrUsers(){
        return nrUsers;
    }
    
    public void setNrUsers(int users){
        this.nrUsers = nrUsers;
    }
    
    public Condition[] getConditions(){
        return this.conditions;
    }
    
    public void setConditions(Condition[] cons){
        this.conditions = cons;
    }
    
    
}
