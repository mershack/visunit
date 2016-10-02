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
    private String width;
    private String height;
    private UserFile[] viewers;
    private UserFile[] datasets;
    private Introduction[] intros;
    private StandardTestDetail[] tests;
    private Task[] tasks;
    private Task[] entryTasks;
    private Task[] exitTasks;
    
    public Study(String name, String description, String viewerDesign,
            String dataDesign, String width, String height, UserFile[] viewers, UserFile[] datasets,
            Introduction[] intros, StandardTestDetail[] tests,Task[] tasks,
            Task[] entryTasks, Task[] exitTasks){
        this.name = name;
        this.description = description;
        this.viewerDesign = viewerDesign;
        this.dataDesign = dataDesign;
        this.width = width;
        this.height = height;
        this.viewers = viewers;
        this.datasets = datasets;
        this.intros = intros;
        this.tests = tests;
        this.tasks = tasks;
        this.entryTasks = entryTasks;
        this.exitTasks = exitTasks;        
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

    public String getDataDesign() {
        return dataDesign;
    }

    public void setDataDesign(String dataDesign) {
        this.dataDesign = dataDesign;
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

    public UserFile[] getViewers() {
        return viewers;
    }

    public void setViewers(UserFile[] viewers) {
        this.viewers = viewers;
    }

    public UserFile[] getDatasets() {
        return datasets;
    }

    public void setDatasets(UserFile[] datasets) {
        this.datasets = datasets;
    }

    public Introduction[] getIntros() {
        return intros;
    }

    public void setIntros(Introduction[] intros) {
        this.intros = intros;
    }

    public StandardTestDetail[] getTests() {
        return tests;
    }

    public void setTests(StandardTestDetail[] tests) {
        this.tests = tests;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }

    public Task[] getEntryTasks() {
        return entryTasks;
    }

    public void setEntryTasks(Task[] entryTasks) {
        this.entryTasks = entryTasks;
    }

    public Task[] getExitTasks() {
        return exitTasks;
    }

    public void setExitTasks(Task[] exitTasks) {
        this.exitTasks = exitTasks;
    }
}
