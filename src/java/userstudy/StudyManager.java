/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Radu
 */
@WebServlet(name = "StudyManager_1", urlPatterns = {"/StudyManager_1"})
public class StudyManager extends HttpServlet {
    
     private final String DATA_DIR = "data";

    private final String QUANT_QNS_FILENAME = "quantitativeQuestions.txt";
    private final String TASKS_NODES_FILENAME = "taskNodesIndexes.txt";//"tasksNodes.txt";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        
        PrintWriter out = response.getWriter();
        
              try {

            HttpSession session = request.getSession();
            String message = "";
            
            String study = request.getParameter("study");
            String user = request.getParameter("user");
            String command = request.getParameter("command");
            boolean debug = request.getParameter("debug") != null ? true : false;
            
            String studyFolder = "users" + File.separator + user + File.separator
                        + "_config_files" + File.separator + "studies" + File.separator + study;           
            
            ArrayList<StudyScript> studyScripts = (ArrayList<StudyScript>)session.getAttribute("scripts");
            
            if (studyScripts == null){
                System.out.println("no scripts exist");
                studyScripts = new ArrayList<StudyScript>();
                session.setAttribute("scripts", studyScripts);
            } 
            else
                System.out.println(studyScripts.size() + " scripts exist");
            
            this.cleanupPartials(studyFolder, studyScripts);
            
            StudyScript script = null;
            for (int i=0;  i<studyScripts.size(); i++)
                if (studyScripts.get(i).getStudyName().equals(study) && 
                        studyScripts.get(i).getExperimenterId().equals(user))
                    script = studyScripts.get(i);
            
            if (script == null){    //this user is only now accessing this study; we need to create a script for them
 
                //get a unique user id
                System.out.println("getting a unique ID");
                File[] files = new File(getServletContext().getRealPath(studyFolder)).listFiles();
                int id = -1;
                for (int i=0; i<files.length; i++){
                    if (files[i].isDirectory()) continue;
                    else if (files[i].getName().startsWith("id")){
                        id = Integer.parseInt(files[i].getName().split("_")[1]);           
                        files[i].renameTo(new File(getServletContext().getRealPath(studyFolder + File.separator + "id_"+(id+1))));
                        break;
                    }
                }
                if (id < 0){
                    System.out.println(" found no id file");
                    new File(getServletContext().getRealPath(studyFolder + File.separator + "id_1")).createNewFile();
                    id = 0;
                }
                
                script = new StudyScript(id+"", study, user);
                ArrayList<String> errors = new ArrayList<String>();
                populateScript(script,debug,errors);
                System.out.println("errors? " + errors.size());
                if (errors.size() > 0 && debug)
                    script.addErrors(errors);
                studyScripts.add(script);
            }
            
            String json = "{}";
            if (command.equals("init"))
                json = "{'step':" +  script.current() + 
                        ",'results':" +  new Gson().toJson(script.getResults()) +  "}";
            else if (command.equals("finish")){
                json = script.finish(); //this will also ensure the script is cleaned away immediately
            }
            if (command.equals("step")){
                
                String resultData = request.getParameter("results");
                System.out.println("resultData: "  + resultData);
                Results results = new Gson().fromJson(resultData, Results.class);
                json = script.next();
                
                if (json.substring(9).startsWith("thankyou"))
                    saveFinalResults(studyFolder, script.getUserId(), script.getGroupIndex(), results);
                else savePartialResults(studyFolder, script.getUserId(), script.getGroupIndex(), results);
                
            }

            json = json.replace("'", "\"");
            System.out.println();
            System.out.println("|" + json + "|");
            System.out.println();
            
            response.getWriter().print(json);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void populateScript(StudyScript script, boolean debug, ArrayList<String> errors){
        
        System.out.println("populate script");
        
        String studyFolder = "users" + File.separator + script.getExperimenterId() + File.separator
                        + "_config_files" + File.separator + "studies" + File.separator + script.getStudyName();
        
        //get the study description
        try{
            File f = new File(getServletContext().getRealPath(studyFolder + File.separator + "study.json"));
            BufferedReader br = new BufferedReader(new FileReader(f));
            Study study = new Gson().fromJson(br, Study.class);
            
            //getting the full study task descriptions            
            Task[] tasks = new Task[study.getTasks().length];
            for (int i=0; i<tasks.length; i++){
                String taskFile = "users" + File.separator + script.getExperimenterId() + File.separator + "_config_files"
                      +  File.separator + "tasks" + File.separator + study.getTasks()[i].getName() + ".json";
                tasks[i] = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(taskFile)))), 
                        Task.class);
            }
            
            //assign user to a group
            //get the assignment file: how many users where assigned to each group
            int[][] ret = getAssignments(studyFolder, study);
            int[] partial = ret[0];  int[] completed = ret[1];
            int min = 999999;
            int minIndex = -1;
            for (int i=0; i<partial.length; i++)
                if (partial[i] + completed[i] < min){
                    minIndex = i;
                    min = partial[i]+completed[i];
                }                            
            UserGroup userGroup = study.getUserGroups()[minIndex];
            
            script.setGroupIndex(minIndex);
            
            //get actual viewers and datasets  (rather than their names) involved in this experiment 
            Hashtable<String, Viewer> allViewers = new Hashtable<String,Viewer>();
            Hashtable<String, Dataset> allDatasets = new Hashtable<String,Dataset>();
            Hashtable<String, Task> allTasks = new Hashtable<String,Task>();
            Hashtable<String, Test> allTests = new Hashtable<String,Test>();
            for (int i=0; i<study.getTests().length; i++){
                String test = study.getTests()[i].getName();
                String testFile = "users" + File.separator + script.getExperimenterId() + File.separator
                                + "_config_files" + File.separator + "tests" + File.separator + test + ".json";
                Test t = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(testFile)))),Test.class);
                allTests.put(test, t);
            }
            for (int i=0; i<study.getViewers().length; i++){
            
                String viewer = study.getViewers()[i];
                String viewerFile = "users" + File.separator + script.getExperimenterId() + File.separator
                                + "_config_files" + File.separator + "viewers" + File.separator + viewer + ".json";
                Viewer v = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(viewerFile)))),Viewer.class);
                allViewers.put(viewer, v);
            }
            for (int i=0; i<study.getDatasets().length; i++){               
                String data = study.getDatasets()[i];               
                String dataFile = "users" + File.separator + script.getExperimenterId() + File.separator
                                + "_config_files" + File.separator + "datasets" + File.separator + data + ".json";
                Dataset d = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(dataFile)))),Dataset.class);
                allDatasets.put(data, d);
            }
            for (int i=0; i<study.getTasks().length; i++){
                String task = study.getTasks()[i].getName();
                String taskFile = "users" + File.separator + script.getExperimenterId() + File.separator
                                + "_config_files" + File.separator + "tasks" + File.separator + task + ".json";
                Task t = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(taskFile)))),Task.class);
                allTasks.put(task, t);
            }
            
            
            //create the debug section (This one creates a list of all resources and functions needed)
            if (debug){
                ArrayList<String> res = new ArrayList<String>();
                for (int i=0; i<study.getViewers().length; i++){
                    res.add("{'type':'resource','url':'" + allViewers.get(study.getViewers()[i]).getSource() + "'}");
                    res.add("{'type':'function', 'name':'setDataset', 'url':'" + allViewers.get(study.getViewers()[i]).getSource() + "'}");
                    res.add("{'type':'function', 'name':'resetVisualization', 'url':'" + allViewers.get(study.getViewers()[i]).getSource() + "'}");
                }
                for (int i=0; i<study.getDatasets().length; i++){
                    System.out.println("studyman debug: " + allDatasets.get(study.getDatasets()[i]));
                    res.add("{'type':'resource','url':'" + allDatasets.get(study.getDatasets()[i]).getSource() + "'}");
                }

                for (int i=0; i<study.getTests().length; i++){
                    res.add("{'type':'resource','url':'" + allTests.get(study.getTests()[i].getName()).getSource() + "'}");
                    res.add("{'type':'function','name':'getResponse','url':'" + allTests.get(study.getTests()[i].getName()).getSource() + "'}");
                    res.add("{'type':'function','name':'getAccuracy','url':'" + allTests.get(study.getTests()[i].getName()).getSource() + "'}");
                }

                for (int i=0; i<study.getTasks().length; i++){
                    //what are all the viewers that this task will be used with?
                    ArrayList<String> urls = new ArrayList<String>();
                    for (int j=0; j<study.getUserGroups().length; j++)
                        for (int k=0; k<study.getUserGroups()[j].getConditions().length; k++){
                            Condition c = study.getUserGroups()[j].getConditions()[k]; 
                            if (c.getTask().equals(study.getTasks()[i].getName()) && 
                                   urls.indexOf(allViewers.get(c.getViewer()).getSource()) < 0)
                                urls.add(allViewers.get(c.getViewer()).getSource());
                        }

                    Task task = allTasks.get(study.getTasks()[i].getName());
                    for (int j=0; j<urls.size(); j++){
                        for (int k=0; k<task.getInputs().length; k++){
                            if (task.getInputs()[k].getShowInVis().equals("yes"))
                                res.add("{'type':'function','name':'set" + task.getInputs()[k].getTypeName() + 
                                        "','url':'" + urls.get(j) + "'}");
                            if (task.getInputs()[k].getSpecifyInVis().equals("yes"))
                                res.add("{'type':'function','name':'get" + task.getInputs()[k].getTypeName() + 
                                        "','url':'" + urls.get(j) + "'}");
                        }
                        if (task.getAnswer().getType().startsWith("Interface"))
                            res.add("{'type':'function','name':'get" + task.getAnswer().getCustomTypeName() + 
                                    "','url':'" + urls.get(j) + "'}," + 
                                    "{'type':'function','name':'set" + task.getAnswer().getCustomTypeName() + 
                                    "','url':'" + urls.get(j) + "'}");
                        if (task.getAnswer().getType().startsWith("Interface") &&
                                task.getAnswer().getCorrectness().equals("yes"))
                            res.add("{'type':'function','name':'getAccuracy" + task.getAnswer().getCustomTypeName() + 
                                    "','url':'" + urls.get(j) + "'}");
                    }
                }

                String debugJson = "{'type':'debug','width':'" + study.getWidth() + "','height':'" + study.getHeight() + "','resources':[";
                for (int i=0; i<res.size(); i++)
                    if (i == res.size()-1)
                        debugJson += res.get(i);
                    else
                        debugJson += res.get(i) + ",";
                debugJson += "]}";
                script.add(debugJson);
            }
                    
            
            //create the intro section
            for (int i=0; i<study.getIntros().length; i++){
                String match = study.getIntros()[i].getMatch();
                if (match.equals("All") || Integer.parseInt(match) == minIndex){
                    //get actual intro
                    String introFile = "users" + File.separator + script.getExperimenterId() + File.separator + "_config_files"
                      +  File.separator + "intros" + File.separator + study.getIntros()[i].getName() + ".json";
                    Introduction intro = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(introFile)))), 
                        Introduction.class);

                    this.createIntro(study, intro, script);
                }    
            }
            
            //create the standardized test section
            for (int i=0; i<study.getTests().length; i++){
                //get actual test
                String testFile = "users" + File.separator + script.getExperimenterId() + File.separator + "_config_files"
                      +  File.separator + "tests" + File.separator + study.getTests()[i].getName() + ".json";
                Test t = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(testFile)))), 
                        Test.class);
                this.createTest(study, minIndex + "_" + userGroup.toString(), t, study.getTests()[i].getCutoff(), script);
            }
            
            //create the entry task section
            for (int i=0; i<study.getEntryTasks().length; i++){
                //get actual task
                String taskFile = "users" + File.separator + script.getExperimenterId() + File.separator + "_config_files"
                      +  File.separator + "tasks" + File.separator + study.getEntryTasks()[i] + ".json";
                Task t = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(taskFile)))), 
                        Task.class);
                this.createEntryExitTask(study, minIndex + "_" + userGroup.toString(), "entry", t, script, errors);
            }
            
            //creating the training + task section
            ArrayList<String> alreadyTrainedViewers = new ArrayList<String>();
            String lastViewer = null;
            for (int i=0; i<userGroup.getConditions().length; i++){
                Condition condition = userGroup.getConditions()[i];
                
                Viewer viewer = allViewers.get(condition.getViewer());
                Dataset dataset = allDatasets.get(condition.getDataset());
                Task task = allTasks.get(condition.getTask());
                
                if (!condition.getViewer().equals(lastViewer)){   //switching viewers;
                    boolean hasTraining = false;
                    for (int j=0; j<study.getTasks().length; j++)
                        if (study.getTasks()[j].getTrainingSize() > 0)
                            hasTraining = true;
                    System.out.println("create viewer switch");
                    createViewerSwitch(study, viewer, hasTraining, script);                    
                }
                
                lastViewer = viewer.getName();
                
                
                TaskDetails td = null;
                for (int j=0; j<study.getTasks().length; j++)
                    if (study.getTasks()[j].getName().equals(condition.getTask()))
                        td = study.getTasks()[j];
                    
                //get training tasks; but only if we haven't already trained this viewer and task 
                //(might have, on another dataset)
                    if (alreadyTrainedViewers.indexOf(condition.getViewer() + condition.getTask()) < 0){
                        this.createTrainingTaskInstances(study, minIndex + "_" + userGroup.toString(),
                                viewer, task, dataset, td.getTrainingSize(), script, errors);
                    }
                    
                    alreadyTrainedViewers.add(condition.getViewer() + condition.getTask());
                    System.out.println("added " + condition.getViewer() + " to already viewed " + alreadyTrainedViewers.size());
                    
                    //get actual tasks
                    this.createTaskInstances(study, minIndex + "_" + userGroup.toString(), viewer, task, dataset, 
                            Integer.parseInt(td.getCount()), Integer.parseInt(td.getTime()), script, errors);
                                   
            }
            
            //create the entry task section
            for (int i=0; i<study.getExitTasks().length; i++){
                //get actual task
                String taskFile = "users" + File.separator + script.getExperimenterId() + File.separator + "_config_files"
                      +  File.separator + "tasks" + File.separator + study.getExitTasks()[i] + ".json";
                Task t = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(taskFile)))), 
                        Task.class);
                this.createEntryExitTask(study, minIndex + "_" + userGroup.toString(),"exit", t, script, errors);
            }
            
            ///create the thank you section
            if (study.getThankyou().equals("Default"))
                script.add("{'type':'thankyou','thankyou':null}");
            else{
                try{
                    String file = "users" + File.separator + script.getExperimenterId() + File.separator + "_config_files"
                          +  File.separator + "intros" + File.separator + study.getThankyou() + ".json";
                    Introduction t = new Gson().fromJson(new BufferedReader(new FileReader(new File(getServletContext().getRealPath(file)))), 
                            Introduction.class); 
                    String v = "'thankyou':" + new Gson().toJson(t);
                    script.add("{" + t + "," + v + "}");
                }
                catch(Exception e){
                    errors.add("Something went wrong on the server when trying to load the thank you.");
                }
            }
            
            
            
        }
        catch(Exception e){
            e.printStackTrace();
            errors.add("Something went wrong on the server when trying to configure the study.");
        }
    }

    /**
     * Looks up nrTasks in the task instance file for the corresponding data,
     * sets them up in a runnable json form, and adds them to the script.
     * Returns a list of errors if opeartion was not successful, null otherwise
     */
    public void createTaskInstances(Study study, String group, 
            Viewer viewer, Task task, Dataset data, int nrTasks, int maxTime, StudyScript script, ArrayList<String> errors){
        
        String user = script.getExperimenterId();

        String filePath = "users" + File.separator + user
                    + File.separator + "_config_files" + File.separator + "taskInstanceFiles"
                    + File.separator + task.getName() + "_" + data.getName() + ".json";

            File f = new File(getServletContext().getRealPath(filePath));
            
            System.out.println("Loading training task instances from: " + f.getAbsolutePath());
            
            if (!f.exists()){
                errors.add("No task instances exist for task " + task.getName() + " and dataset " + data.getName());
                return;
            }

            try{
                BufferedReader br = new BufferedReader(new FileReader(f));

                TaskInstances taskInstances = new Gson().fromJson(br, TaskInstances.class);
                br.close();
                
                int cnt = 0;
                for (int i=0; i<taskInstances.getInstances().length && cnt < nrTasks; i++){
                    if (script.taskInstanceWasUsedInTraining(task.getName(), data.getName(),i))
                        continue;
                    
                    cnt++;
                    String runnableJsonTask = createRunnableTask(task, taskInstances.getInstances()[i], maxTime);
                    script.add("{'type':'task', " 
                            + "'group':'" + group + "'"
                            + ",'viewer':" + new Gson().toJson(viewer)
                            + ",'dataset':" + new Gson().toJson(data)
                            + ",'task':" +  runnableJsonTask 
                            + ",'width':'" + study.getWidth()
                            + "','height':'" + study.getHeight() + "'}");
                }
                if (cnt < nrTasks){
                    errors.add("Not enough task instances exist for task " + task.getName() + " and dataset " + data.getName());
                    return;
                }                    
            }
            catch(Exception e){
                e.printStackTrace();
                errors.add("Some error occured on the server while trying to read tasks instances for task " + task.getName() + " and dataset " + data.getName());
            }
    }
    
    
     /**
     * Looks up nrTasks in the task instance file for the corresponding data,
     * sets them up in a runnable json form, and adds them to the script.
     * Returns a list of errors if operation was not successful, null otherwise
     */
    public void createTrainingTaskInstances(Study study, String group, Viewer viewer, 
            Task task, Dataset data, int nrTasks, StudyScript script, ArrayList<String> errors){
        String user = script.getExperimenterId();
        
        String filePath = "users" + File.separator + user
                    + File.separator + "_config_files" + File.separator + "taskInstanceFiles"
                    + File.separator + task.getName() + "_" + data.getName() + ".json";

            File f = new File(getServletContext().getRealPath(filePath));
            
            System.out.println("Loading training task instances from: " + f.getAbsolutePath());
            
            if (!f.exists()){
                errors.add("No task instances exist for task " + task.getName() + " and dataset " + data.getName());
                return;
            }

            try{
                BufferedReader br = new BufferedReader(new FileReader(f));

                TaskInstances taskInstances = new Gson().fromJson(br, TaskInstances.class);
                br.close();
                
                int cnt = 0;
                for (int i=0; i<taskInstances.getInstances().length && cnt < nrTasks; i++){                    
                    cnt++;
                    String runnableJsonTask = createRunnableTask(task, taskInstances.getInstances()[i], 60);
                    script.add("{'type':'training', "
                            + "'group':'" + group + "'"
                            + ",'viewer':" + new Gson().toJson(viewer)
                            + ",'dataset':" + new Gson().toJson(data)
                            + "," +  "'task':" +  runnableJsonTask 
                            + ",'width':'" + study.getWidth()
                            + "','height':'" + study.getHeight() + "'}");
                    
                    script.markTaskInstanceAsUsedInTraining(task.getName(), data.getName(),i);
                }
                if (cnt < nrTasks){
                    errors.add("No enough task instances exist for task " + task.getName() + " and dataset " + data); 
                    return;
                }
               
            }
            catch(Exception e){
                e.printStackTrace();
                errors.add("Some error occured on the server while trying to read tasks instances for task " + task.getName() + " and dataset " + data);
            }
    }
    
    
    public void createViewerSwitch(Study study, Viewer viewer, boolean training, StudyScript script){
        
        String user = script.getExperimenterId();
        String t = "'type':'viewerSwitch'";
        String v = "'viewer':" + new Gson().toJson(viewer);
        String tr = "'training':" + (training ? "'yes'" : "'no'");
        script.add("{" + t + "," + v + "," + tr + ",'width':'" + study.getWidth()
                            + "','height':'" + study.getHeight() + "'}");
    }
    
    public void createIntro(Study study, Introduction intro, StudyScript script){
        String t = "'type':'intro'";
        String v = "'intro':" + new Gson().toJson(intro);
        script.add("{" + t + "," + v + ",'width':'" + study.getWidth()
                            + "','height':'" + study.getHeight() + "'}");
    }

    public void createTest(Study study, String group, Test test, double cutoff, StudyScript script){
        String user = script.getExperimenterId();
        String t = "'type':'test'";
        String v = "'test':" + new Gson().toJson(test);
        String c = "'cutoff':'" + cutoff + "'";
        script.add("{" + t + "," + v + "," + c + "," + "'group':'" + group + "','width':'" + study.getWidth()
                            + "','height':'" + study.getHeight() + "'}");
    }
    
    
    public void createEntryExitTask(Study study, String group, 
            String which, Task task, StudyScript script, ArrayList<String> errors){
       
        String user = script.getExperimenterId();
        
        String filePath = "users" + File.separator + user
                    + File.separator + "_config_files" + File.separator + "taskInstanceFiles"
                    + File.separator + task.getName() + "_no data.json";

            File f = new File(getServletContext().getRealPath(filePath));
            
            System.out.println("Loading task instances from: " + f.getAbsolutePath());
            
            if (!f.exists()){ 
                errors.add("No task instances exist for task " + task.getName());
            }

            try{
                BufferedReader br = new BufferedReader(new FileReader(f));

                TaskInstances taskInstances = new Gson().fromJson(br, TaskInstances.class);
                
                String runnableJsonTask = createRunnableTask(task, taskInstances.getInstances()[0], 60);
                script.add("{'type':'" + which + "','task':" +  runnableJsonTask + ",'group':'" + group 
                        + "','width':'" + study.getWidth()  + "','height':'" + study.getHeight() + "'}");
                
            }
            catch(Exception e){
                e.printStackTrace();
                errors.add("Some error occured on the server while trying to read tasks instances for task " + task.getName());
            }
    }

    
    
    private String createRunnableTask(Task t, TaskInstance ti, int time){
        
        String json = "";
        
        String question = t.getQuestion();
        
        //replace the $N$ fields in the question
        for (int i=0; i < t.getInputs().length; i++){
            int index = question.indexOf("$"+(i+1) + "$");
            if (index >=0)
                question = question.replace("$" + (i+1) + "$", ti.getInputs()[i]);
        }
        
        //vis setup commands
        String visSetup = "";
        for (int i=0; i<t.getInputs().length; i++){
            if (t.getInputs()[i].getShowInVis().equals("no"))
                continue;
            
            if (visSetup.length() != 0) visSetup += ",";
            
            visSetup += "'set" +  t.getInputs()[i].getTypeName() + "(\\\"" + ti.getInputs()[i] + "\\\")'";
        }
        visSetup = "[" + visSetup + "]";
        
        String getAnswer = "";
        //System.out.println("answer " + t.getAnswer().getCustomTypeName());
        if (t.getAnswer().getCustomTypeName() != null)
            getAnswer = "['vis_get" + t.getAnswer().getCustomTypeName() + "()'," + 
                    "'vis_getAccuracy" + t.getAnswer().getCustomTypeName()  + "'";
        else{
            String options = "";
            if (t.getAnswer().getType().startsWith("Options(fixed")){
                for (int i=0; i<t.getAnswer().getOptions().length; i++){
                    options += t.getAnswer().getOptions()[i];
                    if (i != t.getAnswer().getOptions().length-1) options += "|,|";
                }
            }
            if (t.getAnswer().getType().startsWith("Options(d")){
                for (int i=0; i<ti.getOptions().length; i++){
                    options += ti.getOptions()[i];
                    if (i != ti.getOptions().length-1) options += "|,|";
                }
            }            
            getAnswer = "['gui_get" + t.getAnswer().getType() + "(" + options + ")'," + 
                    "'gui_getAccuracy" + t.getAnswer().getType() + "'";
        }
        
        //deal with correctness: if there is correctness we add it to the getAnswer array; else not.
        if (t.getAnswer().getCorrectness().equals("yes"))
            getAnswer += ",'" + ti.getAnswer() + "']";
        else
            getAnswer += "]";
        
 
        json = "{'question':'" + question + "', 'visSetup':" + visSetup + 
                ",'answer':" + getAnswer + ",'maxTime':'" + time + "'," + 
                "'task':" + new Gson().toJson(t) + ",'taskInstance':" + 
                new Gson().toJson(ti) + "}"; 
        
        return json;
    }
        
    
    public int[][] getAssignments(String path, Study study) throws Exception{
        System.out.println("getting assignments from " + path + " \t " + study.getUserGroups().length);
            int[] assigned = new int[study.getUserGroups().length];
            int[] completed = new int[study.getUserGroups().length];  
            for (int i=0; i<completed.length; i++){
                assigned[i] = 0;
                completed[i] = 0;
            }
            
            File f = new File(getServletContext().getRealPath(path));            
            File[] files = f.listFiles();
            for (int i=0; i<files.length; i++){
                if (files[i].isDirectory()) continue;
                if (!files[i].getName().startsWith("final") && !files[i].getName().startsWith("partial"))
                    continue;
                
                System.out.println("file: " + files[i].getName());
                String group = files[i].getName().split("_")[2];
                System.out.println("group1: " + group);
                group = group.substring(0, group.length() - 5);
                System.out.println("group2: " + group);
                if (files[i].getName().startsWith("final"))
                    completed[Integer.parseInt(group)]++;
                else
                    assigned[Integer.parseInt(group)]++;
            }
                       
            return new int[][]{assigned, completed};
    }
   
     
    public void savePartialResults(String dir, String userId, int groupIndex, Results r) throws Exception{
        String json = new Gson().toJson(r);
        FileWriter writer = new FileWriter(getServletContext().getRealPath(
                dir + File.separator + "partial_" + userId + "_" + groupIndex + ".json"));
        writer.write(json);
        writer.close();
    }
    
    public void saveFinalResults(String dir, String userId, int groupIndex, Results r) throws Exception{
        String json = new Gson().toJson(r);
        FileWriter writer = new FileWriter(getServletContext().getRealPath(
                dir + File.separator + "final_" + userId + "_" + groupIndex + ".json"));
        writer.write(json);
        writer.close();
        System.out.println("removing partial results: " + dir + File.separator + "partial_" + userId + ".json");
        new File(getServletContext().getRealPath(dir + File.separator + "partial_" + userId + ".json")).delete();    
    }
    
    public void cleanupPartials(String dir, ArrayList<StudyScript> scripts){
        long now = new Date().getTime();
        for (int i=0; i<scripts.size(); i++){
            if (now - scripts.get(i).getLastAccessed() > 600000){
                File f = new File(getServletContext().getRealPath(
                        dir + "partial_" + scripts.get(i).getUserId() + "_" + 
                                scripts.get(i).getGroupIndex() + ".json"));
                if (f.exists())
                    f.delete();
                scripts.remove(i);
            }
        }
    }
    
   
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
