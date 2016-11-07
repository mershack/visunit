package userstudy;

import com.amazon.mturk.requester.MTurkRequestsMgr;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mershack
 */
public class StudySetup extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    HashMap<String, StudySetupParameters> setupParameters = new HashMap<String, StudySetupParameters>();
    private final String DATA_DIR = "data";
    private final String DEFAULT_USER = "mershack";
    private final String CONFIG_DIR = "_config_files";

    String questionTemplateName = "graphQuestionForm.xml";
    MyUtils utils = new MyUtils();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            HttpSession session = request.getSession();

            if (session.getAttribute("username") != null) {
                String username = session.getAttribute("username").toString();
                // System.out.println("username is: " + username);
            }

            //printTheURL(request);
            // String nameofstudy = session.getAttribute("studyname").toString();
            //  String studyId = session.getId()+ nameofstudy;
            StudySetupParameters spmts = (StudySetupParameters) setupParameters.get(session.getId()); //get the object of that user session if it exists to avoid global variables being changed erratically

            // System.out.println("***----***" + session.getId());
            if (spmts == null) {
                spmts = new StudySetupParameters();
            }

            String command = request.getParameter("command");
            String userid = request.getParameter("userid");

            //System.out.println("user id is :: " + userid);
            // System.out.println("command is :: " + command);
            // printTheURL(request);
            if (command.equalsIgnoreCase("loadDetailsOfAllStudies")) {
                /**
                 * We will load all the details of a study and send a JSON
                 * object back. The format of the object we will send is as
                 * follows: [{"name" : "study1", "viewers" : ["viewer1",
                 * "viewer2"], "viewerDesign" : "Within", "datasets" :
                 * ["dataset1", "dataset2"], "datasetDesign" : "Within", "tasks"
                 * : [{"name" : "Degree", "count" : "5", "time" : "60",
                 * "training" : "2"}, {...}], "intros" : [{"name" : "Intro1",
                 * "match" : "viewer1"}], "tests" : [{"name" : "Test1",
                 * "interface1" : "inter1", "interface2" : "inter2"}], "width" :
                 * "100", "height" : "100"} {"name":..., "height": "100"} ]
                 *
                 */

                //use a default Login for now. 
                //TODO: userid's should be taken from session ids.
                // System.out.println("I'm here");
                userid = DEFAULT_USER;

                //load all the study names.
                String studiesURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "studies";

                File f = new File(getServletContext().getRealPath(studiesURL));

                File[] files = f.listFiles();

                String jsonOfAllStudies = "[";  //beginning of the json array.

                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (i == 0) {
                            jsonOfAllStudies += "\n\t" + loadExistingStudyDetails(files[i].getName(), userid);
                        } else {
                            jsonOfAllStudies += ",\n\t" + loadExistingStudyDetails(files[i].getName(), userid);
                        }
                    }
                }
                jsonOfAllStudies += "\n]";  //end of the json array.

                //System.out.println(jsonOfAllStudies);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonOfAllStudies);
                
                //save the sesssion id 
                session.setAttribute("userid", userid);

            } else if (command.equalsIgnoreCase("loadDirectories")) {
                /*We will load all the directories of the user, and return a JSON
                 file that contains all the existing directories.
                    
                 the format of the json is as follows:
                
                 [ {"name": "name1", files: [{"name": "file1"}, {"name":"file2"}, ...]
                
                
                 */

                userid = DEFAULT_USER;
                //load all the directories the user has created.
                String userDirsURL = "users" + File.separator + userid;

                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                int dirCount = 0;

                String jsonOfAllDirectories = "[";  //beginning of the json array.

                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        String jsonOfADir = "";
                        //check if it is a directory and not the _config_files directory.
                        if (files[i].isDirectory() && !files[i].getName().equalsIgnoreCase("_config_files")) {
                            //System.out.println(loadDirectories(files[i].getName(), userid));

                            //    System.out.println("*** "+ loadDirectories(files[i].getName(), userid));
                            if (dirCount == 0) { //if this is the first directory we've found
                                jsonOfAllDirectories += "\n\t" + loadDirectories(files[i].getName(), userid);

                            } else {
                                jsonOfAllDirectories += ",\n\t" + loadDirectories(files[i].getName(), userid);
                            }

                            dirCount++;
                        }

                    }
                }

                jsonOfAllDirectories += "\n]";  //end of the json array.

                // System.out.println(jsonOfAllDirectories);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonOfAllDirectories);

            } else if (command.equalsIgnoreCase("loadViewers")) {
                /*
                 We will load all the viewers of the user
                 and return a JSON object that represents all the viewers.
                   
                 the format of the JSON object is as follows.
                
                 [ {"name": "name1", "description": "descrip1", "sourceDirectory": "srcDir1", "sourceFile": "sourceFile1"}, ...]
                 */

                //System.out.println("Loading Viewers");
                userid = DEFAULT_USER;
                //load all the viewers that the user has created.
                String userDirsURL = "users" + File.separator + userid
                        + File.separator + CONFIG_DIR + File.separator + "viewers";

                //  System.out.println("The viewer directory is : "+ userDirsURL);
                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                //compose the object for all the viewers
                String jsonAllViewers = "[";

                for (int i = 0; i < files.length; i++) {

                    if (i == 0) {
                        jsonAllViewers += "\n\t" + loadAViewer(files[i].getName(), userid, request);
                    } else {
                        jsonAllViewers += ",\n\t" + loadAViewer(files[i].getName(), userid, request);
                    }

                }
                
                

                jsonAllViewers += "\n]";

                // System.out.println(jsonAllViewers);
                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonAllViewers);

            } else if (command.equalsIgnoreCase("loadDatasets")) {
                /*
                 We will load all the datasets of the user
                 and return a JSON object that represents all the datasets
                   
                 the format of the JSON object is as follows.
                
                 [ {"name": "name1", "description": "descrip1", "sourceDirectory": "srcDir1", "sourceFile": "sourceFile1"}, ...]
                 */

                userid = DEFAULT_USER;
                //load all the datasets that the user has created.
                String userDirsURL = "users" + File.separator + userid
                        + File.separator + CONFIG_DIR + File.separator + "datasets";

                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                //compose the object for all the datasets
                String jsonAllDatasets = "[";

                for (int i = 0; i < files.length; i++) {
                    if (i == 0) {
                        jsonAllDatasets += "\n\t" + loadADataset(files[i].getName(), userid, request);
                    } else {
                        jsonAllDatasets += ",\n\t" + loadADataset(files[i].getName(), userid, request);
                    }

                }
                jsonAllDatasets += "\n]";

                //System.out.println(jsonAllDatasets);
                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonAllDatasets);

            } else if (command.equalsIgnoreCase("loadTasks")) {
                //we will be loading the tasks.

                userid = DEFAULT_USER;
                //load all the datasets that the user has created.
                String userDirsURL = "users" + File.separator + userid
                        + File.separator + CONFIG_DIR + File.separator + "tasks";

                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                //compose the object for all the datasets
                String jsonAllTasks = "[";

                for (int i = 0; i < files.length; i++) {
                    //System.out.println(files[i].getName());
                    if (i == 0) {
                        jsonAllTasks += "\n\t" + loadTask(files[i].getName(), userid);
                    } else {
                        jsonAllTasks += ",\n\t" + loadTask(files[i].getName(), userid);
                    }

                }
                jsonAllTasks += "\n]";

                //   System.out.println(jsonAllTasks);
                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonAllTasks);

            } else if (command.equalsIgnoreCase("loadTaskInstances")) {
                //we will be loading the tasks Instances

                userid = DEFAULT_USER;
                //load all the datasets that the user has created.
                String userDirsURL = "users" + File.separator + userid
                        + File.separator + CONFIG_DIR + File.separator + "taskInstances";

                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                //compose the object for all 
                String jsonAllTaskIntances = "[";

                for (int i = 0; i < files.length; i++) {
                    if (i == 0) {
                        jsonAllTaskIntances += "\n\t" + loadTaskInstance(files[i].getName(), userid);
                    } else {
                        jsonAllTaskIntances += ",\n\t" + loadTaskInstance(files[i].getName(), userid);
                    }
                }
                jsonAllTaskIntances += "\n]";

                // System.out.println(jsonAllTaskIntances);
//                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print(jsonAllTaskIntances);
            } else if (command.equalsIgnoreCase("loadIntros")) {

                userid = DEFAULT_USER;
                //load all the datasets that the user has created.
                String userDirsURL = "users" + File.separator + userid
                        + File.separator + CONFIG_DIR + File.separator + "intros";

                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                //compose the object for all 
                String jsonAllIntros = "[";

                for (int i = 0; i < files.length; i++) {
                    //System.out.println(files[i].getName());
                    if (i == 0) {
                        jsonAllIntros += "\n\t" + loadIntro(files[i].getName(), userid);
                    } else {
                        jsonAllIntros += ",\n\t" + loadIntro(files[i].getName(), userid);
                    }
                }
                jsonAllIntros += "\n]";
//
                //System.out.println(jsonAllIntros);
                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print(jsonAllIntros);

            } else if (command.equalsIgnoreCase("loadTests")) {
                userid = DEFAULT_USER;
                //load all the datasets that the user has created.
                String userDirsURL = "users" + File.separator + userid
                        + File.separator + CONFIG_DIR + File.separator + "tests";

                File f = new File(getServletContext().getRealPath(userDirsURL));

                File[] files = f.listFiles();

                //compose the object for all 
                String jsonAllTests = "[";

                for (int i = 0; i < files.length; i++) {
                    //System.out.println(files[i].getName());
                    if (i == 0) {
                        jsonAllTests += "\n\t" + loadTest(files[i].getName(), userid);
                    } else {
                        jsonAllTests += ",\n\t" + loadTest(files[i].getName(), userid);
                    }
                }
                jsonAllTests += "\n]";
//
                //System.out.println(jsonAllTests);
                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print(jsonAllTests);
            } else if (command.equalsIgnoreCase("getViewerDatasetTask")) {
                //load the viewer and dataset

                userid = DEFAULT_USER;

                String viewerName = request.getParameter("viewerName");
                String datasetName = request.getParameter("datasetName");
                String taskName = request.getParameter("taskName");

                String viewerJson, dsJson, taskJson;

                viewerJson = loadAViewer(viewerName + ".json", userid, request);

                if (!datasetName.equalsIgnoreCase("no data")) {
                    dsJson = loadADataset(datasetName + ".json", userid, request);
                } else { //no data
                    dsJson = "{\"name\" : \"no data\"}";
                }

                taskJson = loadTask(taskName + ".json", userid);

                String allJsons = "{";
                allJsons += "\"viewer\": " + viewerJson;
                allJsons += ",\"dataset\": " + dsJson;
                allJsons += ",\"task\": " + taskJson;
                allJsons += "}";

                // System.out.println("allJson is :\n" + allJsons);
//
                //System.out.println(jsonAllTests);
                //now we will be sending the json object to the client
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print(allJsons);
            } else if (command.equalsIgnoreCase("updateStudyData")) {
                /*We will get the json object of the study from the request, and save it 
                 on the server.
                 */
                String studyData = request.getParameter("studyData");

                //now create the Gson object 
                Gson gson = new Gson();

                Study study = (Study) gson.fromJson(studyData.toString(), Study.class);
                //now we will write the study object to file

                String studyJson = gson.toJson(study);

                userid = DEFAULT_USER;

                //load all the study names.
                String studiesURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "studies";

                String jsonFilename = studiesURL + File.separator + study.getName()
                        + File.separator + "data" + File.separator + "quantitativeTasks.json";

                System.out.println("The filename is : " + jsonFilename);

                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
                writer.write(studyJson);
                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).

            } else if (command.equalsIgnoreCase("updateViewerData")) {
                //we will get the viewerData json object and save it.

                String studyData = request.getParameter("viewerData");

                //now create the Gson object 
                Gson gson = new Gson();

                UserFile viewer = gson.fromJson(studyData.toString(), UserFile.class);
                //now we will write the study object to file
                String viewerJson = gson.toJson(viewer);

                userid = DEFAULT_USER;

                String viewersURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "viewers";

                String jsonFilename = viewersURL + File.separator + viewer.getName()
                        + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
                writer.write(viewerJson);
                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
            } else if (command.equalsIgnoreCase("updateDatasetData")) {
                String studyData = request.getParameter("datasetData");

                //now create the Gson object 
                Gson gson = new Gson();

                UserFile dataset = gson.fromJson(studyData.toString(), UserFile.class);
                //now we will write the study object to file
                String datasetJson = gson.toJson(dataset);

                userid = DEFAULT_USER;

                String datasetsURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "datasets";

                String jsonFilename = datasetsURL + File.separator + dataset.getName()
                        + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
                writer.write(datasetJson);
                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
            } else if (command.equalsIgnoreCase("updateTaskData")) {
                String studyData = request.getParameter("taskData");

                //now create the Gson object 
                Gson gson = new Gson();

                Task task = gson.fromJson(studyData.toString(), Task.class);
                //now we will write the study object to file
                String taskJson = gson.toJson(task);

                userid = DEFAULT_USER;

                String datasetsURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "tasks";

                String jsonFilename = datasetsURL + File.separator + task.getName()
                        + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
                writer.write(taskJson);
                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
            } else if (command.equalsIgnoreCase("updateIntroData")) {
                String studyData = request.getParameter("introData");

                //now create the Gson object 
                Gson gson = new Gson();

                Introduction intro = gson.fromJson(studyData.toString(), Introduction.class);
                //now we will write the study object to file
                String introJson = gson.toJson(intro);

                userid = DEFAULT_USER;

                String introURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "intros";

                String jsonFilename = introURL + File.separator + intro.getName()
                        + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
                writer.write(introJson);
                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
            } else if (command.equalsIgnoreCase("updateTestData")) {
                String testData = request.getParameter("testData");

                //now create the Gson object 
                Gson gson = new Gson();

                StandardTestDetail test = gson.fromJson(testData.toString(), StandardTestDetail.class);
                //now we will write the test object to file
                String testJson = gson.toJson(test);

                userid = DEFAULT_USER;

                String testURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator + "tests";

                String jsonFilename = testURL + File.separator + test.getName()
                        + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
                writer.write(testJson);
                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).

            } else if (command.equalsIgnoreCase("addNewFiles")) {
                String dirName = request.getParameter("directory");

                userid = DEFAULT_USER;

                String dirURL = "users" + File.separator + userid + File.separator
                        + dirName;

                String dirPath = getServletContext()
                        .getRealPath(dirURL);

                // File viewerDir = new File(dirPath);
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        // System.out.println("Hey");
                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {

                                String name = new File(item.getName()).getName();
                                //System.out.println("Filename: "+name);
                                //now write the fi in that directory
                                item.write(new File(dirPath + File.separator + name));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }
                }
            } else if (command.equalsIgnoreCase("createNewDirectory")) {
                String dirName = request.getParameter("directoryName");

                //we will create the directory if it does not exist
                userid = DEFAULT_USER;

                String dirURL = "users" + File.separator + userid + File.separator
                        + dirName;

                String dirPath = getServletContext()
                        .getRealPath(dirURL);

                File file = new File(dirPath);

                if (!file.exists()) {
                    file.mkdir();
                }

            } else if (command.equalsIgnoreCase("getManagementCommand")) {
                String mc = spmts.getManagementCommand();

                spmts.setManagementCommand("");
                out.print(mc);
            } else if (command.equalsIgnoreCase("getStudyName")) {
                String studyname = getStudyName(userid);
                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + studyname + File.separator + "data";

                spmts.studydataurl = studydataurl;
                out.print(studyname);
            } else if (command.equalsIgnoreCase("getCurrentStudyName")) {
                out.print(spmts.studyname);
            } else if (command.equalsIgnoreCase("getAllStudyNames")) {
                //load all the study names.
                String studiesURL = "users" + File.separator + userid + File.separator
                        + "studies";
                File f = new File(getServletContext().getRealPath(studiesURL));

                File[] files = f.listFiles();

                String allNames = "";

                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (i == 0) {
                            allNames = files[i].getName();
                        } else {
                            allNames += "::" + files[i].getName();
                        }

                    }
                }

                out.print(allNames);

            } else if (command.equalsIgnoreCase("removeViewerFile")) {
                //we will remove the viewer

                String viewerName = request.getParameter("viewerName");

                userid = DEFAULT_USER;

                String viewerFilePath = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator
                        + "viewers" + File.separator + viewerName + ".json";

                File viewerFile = new File(getServletContext().getRealPath(viewerFilePath));
                System.out.println(getServletContext().getRealPath(viewerFilePath));
                //delete the file if it exists
                if (viewerFile.exists()) {
                    viewerFile.delete();
                    System.out.println("viewer deleted");
                    BufferedReader br = new BufferedReader(new FileReader(viewerFile));

                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("__" + line);
                    }
                    br.close();
                }

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
            } else if (command.equalsIgnoreCase("removeTestFile")) {

                String testName = request.getParameter("testName");

                userid = DEFAULT_USER;

                String testFilePath = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator
                        + "tests" + File.separator + testName + ".json";

                File testFile = new File(getServletContext().getRealPath(testFilePath));
                System.out.println(getServletContext().getRealPath(testFilePath));

                //delete the file if it exists
                if (testFile.exists()) {
                    System.out.println("tests have been deleted");
                    testFile.delete();
                }

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");
            } else if (command.equalsIgnoreCase("deleteStudy")) {
                //get the study name and delete it.
                String studyname = request.getParameter("studyname").toString();

                //delete the directory and its files now.
                if (!studyname.trim().isEmpty()) {
                    File study_dir = new File(getServletContext().getRealPath("users"
                            + File.separator + userid + File.separator + "studies"
                            + File.separator + studyname));

                    deleteFile(study_dir);
                }

            } else if (command.equalsIgnoreCase("editStudy")) {

                //System.out.println("hey");
                //set the edit command                
                spmts.setManagementCommand("edit");

                //System.out.println("**setup-command + " + spmts.getManagementCommand());
                String studyname = request.getParameter("studyname").toString();

                spmts.studyname = studyname;

                // spmts.setManagementCommand("");
                setupParameters.put(session.getId(), spmts);
                RequestDispatcher view = request.getRequestDispatcher("userstudy_setup.html");
                view.forward(request, response);
            } else if (command.equalsIgnoreCase("copyStudy")) {
                spmts.setManagementCommand("copy");
                String studyname = request.getParameter("studyname").toString();
                spmts.studyname = studyname;
                setupParameters.put(session.getId(), spmts);

                RequestDispatcher view = request.getRequestDispatcher("userstudy_setup.html");
                view.forward(request, response);
            } else if (command.equalsIgnoreCase("newStudy")) {
                spmts.setManagementCommand("copy");
                RequestDispatcher view = request.getRequestDispatcher("userstudy_setup.html");
                view.forward(request, response);
            } else if (command.equalsIgnoreCase("publishStudy")) {

                String studyname = request.getParameter("studyname").toString();
                spmts.studyname = studyname;
                setupParameters.put(session.getId(), spmts);

                RequestDispatcher view = request.getRequestDispatcher("publishstudy.html");
                view.forward(request, response);

            } else if (command.equalsIgnoreCase("loadExistingStudyDetails")) {
                //load the xml file of the study, read it and convert it into 
                // a json file and return the file name.

                String jsonFilename = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + spmts.studyname
                        + File.separator + "data" + File.separator + "jsonFile.json";

                File jsonFile = new File(
                        getServletContext().getRealPath(jsonFilename));
                FileWriter fw = new FileWriter(jsonFile);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);

                try {
                    //get the study details as a json string.
                    String jsonString = loadExistingStudyDetails(spmts.studyname, userid);
                    pw.print(jsonString);

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    pw.close();
                    fw.close();
                }
                out.print(jsonFile);

            } else if (command.equalsIgnoreCase("Demo") || command.equalsIgnoreCase("Submit")) {
                //get the ViewerConditions
                spmts.studyname = request.getParameter("studyname");

                String vc[] = request.getParameterValues("conditions");
                spmts.viewerConditions = new ArrayList<String>();
                if (vc != null) {
                    for (int i = 0; i < vc.length; i++) {
                        if (!vc[i].isEmpty()) {
                            spmts.viewerConditions.add(vc[i]);
                        }
                    }
                }

                String vcsn[] = request.getParameterValues("condition-shortnames");
                spmts.viewerConditionShortNames = new ArrayList<String>();
                if (vcsn != null) {
                    for (int i = 0; i < vcsn.length; i++) {
                        if (!vcsn[i].isEmpty()) {
                            spmts.viewerConditionShortNames.add(vcsn[i]);
                        }
                    }
                }

                //get the viewer width and height
                String viewerWidth = request.getParameter("viewerWidth");
                String viewerHeight = request.getParameter("viewerHeight");

                spmts.viewerHeight = viewerHeight;
                spmts.viewerWidth = viewerWidth;

                //get the datasets
                String datasets[] = request.getParameterValues("datasets");
                spmts.datasets = new ArrayList<String>();
                if (datasets != null) {
                    for (int i = 0; i < datasets.length; i++) {
                        spmts.datasets.add(datasets[i]);
                    }
                }

                //get the datasetFormat.
                String datasetFormats[] = request.getParameterValues("datasetFormats");
                spmts.datasetFormats = new ArrayList<String>();
                if (datasetFormats != null) {
                    for (int i = 0; i < datasetFormats.length; i++) {
                        spmts.datasetFormats.add(datasetFormats[i]);
                    }
                }
                //get the datasetTypes
                String datasetTypes[] = request.getParameterValues("datasetTypes");
                spmts.datasetTypes = new ArrayList<String>();
                if (datasetTypes != null) {
                    for (int i = 0; i < datasetTypes.length; i++) {
                        spmts.datasetTypes.add(datasetTypes[i]);
                    }
                }

                //spmts.datasetFormat = request.getParameter("datasetFormat");
                //get the experiment type vis
                spmts.expType_vis = request.getParameter("expType_vis");
                //get the experiment type ds
                spmts.expType_ds = request.getParameter("expType_ds");
                //get the quantitative tasks, task sizes, and task times            
                String qt[] = request.getParameterValues("quantitativeTasks");
                String qts[] = request.getParameterValues("quantitativeTaskSize");
                String qtt[] = request.getParameterValues("quantitativeTaskTime");

                spmts.quantitativeQuestions = new ArrayList<String>();
                spmts.quantQuestionSizes = new ArrayList<String>();
                spmts.quantQuestionTime = new ArrayList<String>();
                spmts.qualitativeQuestions = new ArrayList<String>();
                spmts.qualitativeQuestionsPositions = new ArrayList<String>();

                if (qt != null && qts != null & qtt != null) {
                    for (int i = 0; i < qt.length; i++) {
                        //add the task if task, size and time are not empty.
                        if (!qt[i].isEmpty() && !qts[i].isEmpty() && !qtt[i].isEmpty()) {
                            //  System.out.println("-- Task: "+ qt[i] + "   ---Size: "+ "  --Time: "+qtt[i]); 
                            spmts.quantitativeQuestions.add(qt[i]);
                            spmts.quantQuestionSizes.add(qts[i]);
                            spmts.quantQuestionTime.add(qtt[i]);
                        }
                    }
                }
                //get the trainingSize
                if (!request.getParameter("trainingSize").isEmpty()) //NB: if it is empty we will use the default training size
                {
                    spmts.trainingSize = Integer.parseInt(request.getParameter("trainingSize"));
                }
                //System.out.println("The training size is: "+ spmts.trainingSize);

                //qet the qualitative task details as well                       
                String qlt[] = request.getParameterValues("qualitativeTasks");
                String qltPos[] = request.getParameterValues("qualitativeTasksPositions");

                if (qlt != null && !qlt[0].isEmpty()) {
                    for (int i = 0; i < qlt.length; i++) {
                        spmts.qualitativeQuestions.add(qlt[i]);
                        // System.out.println("QUAL:: " + qlt[i] + " ---- POS:: "+qltPos[i]);
                    }
                    if (qltPos != null) {
                        for (int i = 0; i < qltPos.length; i++) {
                            spmts.qualitativeQuestionsPositions.add(qltPos[i]);
                        }
                    }
                }

                //get the introductions
                spmts.introductionFiles = new ArrayList<introductionFileParameters>();
                String intro_urls[] = request.getParameterValues("introURLs");
                String intro_conds[] = request.getParameterValues("introConds");

                if (intro_urls != null && intro_conds != null) {

                    for (int i = 0; i < intro_urls.length; i++) {
                        spmts.introductionFiles
                                .add(new introductionFileParameters(intro_urls[i], intro_conds[i]));
                    }
                }

                //get the standardTests
                spmts.standardTests = new ArrayList<StandardTestParam>();
                String st_urls[] = request.getParameterValues("standardTestURLs");
                String st_resps[] = request.getParameterValues("standardTestUserRespInterface");
                String st_perfs[] = request.getParameterValues("standardTestUserPerfInterface");

                if (st_urls != null && st_resps != null) {

                    for (int i = 0; i < st_urls.length; i++) {
                        spmts.standardTests.add(new StandardTestParam(st_urls[i],
                                st_resps[i], st_perfs[i]));
                    }
                }

                //get the prestudytasks
                spmts.preStudyQuestions = new ArrayList<String>();
                String preSts[] = request.getParameterValues("preStudyTasks");

                if (preSts != null) {
                    //do something here.
                    for (int i = 0; i < preSts.length; i++) {
                        spmts.preStudyQuestions.add(preSts[i]);
                    }
                }

                //get the poststudytasks
                spmts.postStudyQuestions = new ArrayList<String>();
                String postSts[] = request.getParameterValues("postStudyTasks");

                if (postSts != null) {
                    for (int i = 0; i < postSts.length; i++) {
                        spmts.postStudyQuestions.add(postSts[i]);
                    }
                }

                writeTasksToFile(spmts, userid); //writing the tasks to file
                deleteAllExistingResultsFile(spmts); //delete the existing results file

                //get the command: if it is demo, we will not do anything extra otherwise
                // we will delete any existing result files and do other cleanups before the study starts.    
                if (command.equalsIgnoreCase("Submit")) {  //demo
                    String hitTitle = request.getParameter("hitTitle");
                    String awsAccessKey = request.getParameter("awsAccessKey");
                    String awsSecretKey = request.getParameter("awsSecretKey");

                    String maxAssignments = request.getParameter("maxAssignments");
                    String hitReward = request.getParameter("hitReward");

                    String questionTemplatePath = getServletContext().getRealPath(DATA_DIR + File.separator + questionTemplateName);

                    out.print("Finished");

                    //put the study on Mechanical Turk
                    // createMTurkHIT(hitTitle, awsAccessKey, awsSecretKey, hitReward.trim(), Integer.parseInt(maxAssignments), questionTemplatePath);
                    //redirect to the setup-completed page
                    //TODO: Check if the HIT was successfully created otherwise redirect to an error acknowledgement page.
                    //write the url for the study and the url for getting the results
                    //response.sendRedirect("setup-completed.html");
                    //System.out.println("-- Redirect successful");
                }

                setupParameters.put(session.getId(), spmts); //reput the object to the hashtable for future reference

            } else if (command.equalsIgnoreCase("getTaskInterfaceMethods")) {
                String taskQuestion = request.getParameter("taskQuestion").toString();
                out.print(getTaskInterfaceMethods(taskQuestion, request));
            }

        } finally {
            out.close();
        }
    }

    /**
     * This method will delete all existing result files if any, before the
     * study starts
     */
    public void deleteAllExistingResultsFile(StudySetupParameters spmts) {
        //delete the accuracy and time files for each of the given conditions
        //get the filenames

        System.out.println("Viewer Conditions size in Delete:: " + spmts.viewerConditions.size());

        ArrayList<String> accuracyFilenames = utils.getAccuracyFileNames(spmts.viewerConditions.size());
        ArrayList<String> timeFilenames = utils.getTimeFileNames(spmts.viewerConditions.size());
        String filename;
        File file;

        //delete the files if they exist.
        for (int i = 0; i < accuracyFilenames.size(); i++) {
            filename = accuracyFilenames.get(i);

            file = new File(getServletContext().getRealPath(spmts.studydataurl + File.separator + filename));
            if (file.exists()) {
                file.delete();
            }

            filename = timeFilenames.get(i);
            file = new File(getServletContext().getRealPath(spmts.studydataurl + File.separator + filename));

            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * This method will go through the study folders and return a symbolic name
     * for the current study e.g study1, study2, etc. by counting the number of
     * files in the directory
     *
     * @return
     */
    public String getStudyName(String userid) {
        String studyname = "";
        File f = new File(getServletContext().getRealPath("users"
                + File.separator + userid + File.separator + "studies"));
        int count = 0;
        File[] files = f.listFiles();

        count = files.length;

        studyname = "study" + (count + 1);

        //if this studyname already exists, let's append "_2 to it.
        File study_dir = new File(getServletContext().getRealPath("users"
                + File.separator + userid + File.separator + "studies"
                + File.separator + studyname));

        if (study_dir.exists()) {
            studyname = studyname + "_2";
        }

        return studyname;
    }

    public String loadExistingStudyDetails(String studyname, String userid) {

        String jsonStr = "";
        try {

            String filePath = "users" + File.separator + userid + File.separator
                    + "_config_files" + File.separator
                    + "studies" + File.separator + studyname
                    + File.separator + "data" + File.separator
                    + "quantitativeTasks.json";

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);

            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(reader);

            Study studyObj = gson.fromJson(br, Study.class);

            br.close();

            int resultsCount = 0;

            //now let's read the result temporarily
            filePath = "users" + File.separator + userid + File.separator
                    + "_config_files" + File.separator
                    + "studies" + File.separator + studyname
                    + File.separator + "data" + File.separator
                    + "basicResultsData.json";

            f = new File(getServletContext().getRealPath(filePath));

            if (f.exists()) {
                reader = new FileReader(f);

                gson = new Gson();

                br = new BufferedReader(reader);
                ResultsData resultsObj = gson.fromJson(br, ResultsData.class);

                System.out.println("the length of the results is "+ resultsObj.getResults().length);
                
                
                if (resultsObj.getResults().length > 0) {
                    resultsCount = resultsObj.getResults()[0].getBasicData().length;
                }
                br.close();
            }
            
            studyObj.setResultsCount(resultsCount);
            jsonStr = gson.toJson(studyObj);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonStr;
    }

    public String loadDirectories(String dirname, String userid) {

        //now check if there are files in that directory.
        //load all the directories the user has created.
        String dirURL = "users" + File.separator + userid + File.separator
                + dirname;

        File file = new File(getServletContext().getRealPath(dirURL));

        File[] dirFiles = file.listFiles();

        ///  int fileCount = 0;
        ArrayList<String> files = new ArrayList<String>();

        for (int i = 0; i < dirFiles.length; i++) {
            //make sure it is not a directory.                                
            if (!dirFiles[i].isDirectory()) {
                files.add(dirFiles[i].getName());
            }
        }

        //compose the json for this directory
        String jsonOfADir = " { \"name\": \"" + dirname + "\"";
        jsonOfADir += ", \"files\":[";

        for (int i = 0; i < files.size(); i++) {
            if (i == 0) {
                jsonOfADir += " {\"name\": \"" + files.get(i) + "\"}";
            } else {
                jsonOfADir += ", {\"name\": \"" + files.get(i) + "\"}";
            }
        }
        //now append the ]}

        jsonOfADir += "]}";

        return jsonOfADir;

    }

    public String loadAViewer(String filename, String userid, HttpServletRequest request) {
        String viewerJSON = "";

        try {
            String filePath = "users" + File.separator + userid
                    + File.separator + CONFIG_DIR + File.separator + "viewers"
                    + File.separator + filename;

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);

            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(new FileReader(f));

            UserFile viewerObj = gson.fromJson(br, UserFile.class);
            br.close();

            //compose the url of the viewerObject.
            String url = getServerUrl(request) + "/users/" + userid + "/" + viewerObj.getSourceDirectory()
                    + "/" + viewerObj.getSourceFile();
            viewerObj.setUrl(url);

            viewerJSON = gson.toJson(viewerObj);

            //close the bufferedreader 
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return viewerJSON;
    }

    public String loadTask(String filename, String userid) {

        String taskJSON = "";

        try {
            String filePath = "users" + File.separator + userid
                    + File.separator + CONFIG_DIR + File.separator + "tasks"
                    + File.separator + filename;

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);

            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(reader);

            Task taskObj = gson.fromJson(br, Task.class);

            taskJSON = gson.toJson(taskObj);

            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return taskJSON;

    }

    public String loadTaskInstance(String filename, String userid) {
        String taskInstJSON = "";

        try {
            String filePath = "users" + File.separator + userid
                    + File.separator + CONFIG_DIR + File.separator + "taskInstances"
                    + File.separator + filename;

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);

            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(reader);

            TaskInstancesDetails taskInstObj = gson.fromJson(br, TaskInstancesDetails.class);

            taskInstJSON = gson.toJson(taskInstObj);

            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return taskInstJSON;
    }

    public String loadIntro(String filename, String userid) {
        String introJSON = "";

        try {
            String filePath = "users" + File.separator + userid
                    + File.separator + CONFIG_DIR + File.separator + "intros"
                    + File.separator + filename;

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);

            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(reader);

            UserFile introObj = gson.fromJson(br, UserFile.class);

            introJSON = gson.toJson(introObj);

            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return introJSON;
    }

    public String loadTest(String filename, String userid) {
        String testJSON = "";

        try {
            String filePath = "users" + File.separator + userid
                    + File.separator + CONFIG_DIR + File.separator + "tests"
                    + File.separator + filename;

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);
            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(reader);
            StandardTestDetail testObj = gson.fromJson(br, StandardTestDetail.class);
            testJSON = gson.toJson(testObj);

            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return testJSON;
    }

    public String loadADataset(String filename, String userid, HttpServletRequest request) {
        String datasetJSON = "";

        try {
            String filePath = "users" + File.separator + userid
                    + File.separator + CONFIG_DIR + File.separator + "datasets"
                    + File.separator + filename;

            File f = new File(getServletContext().getRealPath(filePath));

            FileReader reader = new FileReader(f);

            Gson gson = new Gson();

            BufferedReader br = new BufferedReader(reader);

            //compose the url of the viewerObject.            
            UserFile dsObj = gson.fromJson(br, UserFile.class);

            String url = getServerUrl(request) + "/users/" + userid + "/" + dsObj.getSourceDirectory()
                    + "/" + dsObj.getSourceFile();

            dsObj.setUrl(url);

            datasetJSON += gson.toJson(dsObj);
            //close the bufferedreader 
            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return datasetJSON;
    }

    public void writeTasksToFile(StudySetupParameters spmts, String userid) {
        // System.out.println("##over here");
        String quantTaskFilename = "quantitativeQuestions.txt";
        try {
            //////////////////////////////////////////////////////////////////////////////////
            //write the xml file now "eventually it might be the only one we will be using
            quantTaskFilename = "quantitativeTasks.xml";

            // File quantTaskFile = new File()
            // quanTaskFile = newFile(getServletContext().getRealPath(DATA_DIR + File.separator + quantTaskFilename));
            String studyurl = "users" + File.separator + userid + File.separator
                    + "studies" + File.separator + spmts.studyname;

            File studydir = new File(getServletContext().getRealPath(studyurl));
            //create the study directory if it does not exist.
            if (!studydir.exists()) {
                studydir.mkdir();
            }

            File datadir = new File(getServletContext().getRealPath(
                    studyurl + File.separator + DATA_DIR));
            if (!datadir.exists()) {
                // System.out.println("The study directory does not exist");
                datadir.mkdir();
            } else {
                System.out.println("-The study-directory exists!");
            }
            File quanTaskFile = new File(getServletContext().getRealPath(studyurl + File.separator + DATA_DIR + File.separator + quantTaskFilename));

            BufferedWriter bw1 = new BufferedWriter(new FileWriter(quanTaskFile));

            PrintWriter pw1 = new PrintWriter(bw1);

            pw1.println("<?xml version=\"1.0\"?>");   //first line of the xml
            pw1.println("\t<study_specification>");

            //studyname            
            pw1.println("\t<studyname>" + spmts.studyname + "</studyname>");
            //experiment type vis
            pw1.println("\t<experimenttype_vis>" + spmts.expType_vis + "</experimenttype_vis>");
            //experiment type ds
            pw1.println("\t<experimenttype_ds>" + spmts.expType_ds + "</experimenttype_ds>");
            //training size
            pw1.println("\t<trainingsize>" + spmts.trainingSize + "</trainingsize>");

            //the conditions
            for (int i = 0; i < spmts.viewerConditions.size(); i++) {
                pw1.println("\t<condition>");
                pw1.println("\t\t<conditionurl>" + spmts.viewerConditions.get(i) + "</conditionurl>");
                pw1.println("\t\t<conditionshortname>" + spmts.viewerConditionShortNames.get(i) + "</conditionshortname>");
                pw1.println("\t</condition>");
            }
            //the datasets and formats
            for (int i = 0; i < spmts.datasets.size(); i++) {
                String datasetType = spmts.datasetTypes.get(i);
                pw1.println("\t<datasetCondition>");
                pw1.println("\t\t<dataset>" + spmts.datasets.get(i) + "</dataset>");
                pw1.println("\t\t<datasetType>" + datasetType + "</datasetType>");

                pw1.println("\t\t<datasetFormat>" + spmts.datasetFormats.get(i) + "</datasetFormat>");
                pw1.println("\t</datasetCondition>");
            }

            //the viewer dimensions
            pw1.println("\t<viewerwidth>" + spmts.viewerWidth + "</viewerwidth>");
            pw1.println("\t<viewerheight>" + spmts.viewerHeight + "</viewerheight>");

            //the tasks
            String taskName = "";
            for (int i = 0; i < spmts.quantitativeQuestions.size(); i++) {
                taskName = getTaskCode(spmts.quantitativeQuestions.get(i), userid);

                pw1.println("\t<task>");
                pw1.println("\t\t<name>" + taskName + "</name>");
                pw1.println("\t\t<question>" + spmts.quantitativeQuestions.get(i) + "</question>");
                pw1.println("\t\t<size>" + spmts.quantQuestionSizes.get(i) + "</size>");
                pw1.println("\t\t<time>" + spmts.quantQuestionTime.get(i) + "</time>");
                pw1.println("\t</task>");
            }
            //write the qualitative tasks also
            for (int i = 0; i < spmts.qualitativeQuestions.size(); i++) {
                taskName = getQualitativeTaskCode(spmts.qualitativeQuestions.get(i));
                pw1.println("\t<qualtask>");
                pw1.println("\t\t<name>" + taskName + "</name>");
                pw1.println("\t\t<question>" + spmts.qualitativeQuestions.get(i) + "</question>");

                String qualtaskPos = spmts.qualitativeQuestionsPositions.get(i);
                if (!qualtaskPos.equalsIgnoreCase("before")) {
                    qualtaskPos = "after";
                }
                pw1.println("\t\t<qualtaskPos>" + qualtaskPos + "</qualtaskPos>");
                pw1.println("\t</qualtask>");
            }
            //write the introduction Files to file
            for (int i = 0; i < spmts.introductionFiles.size(); i++) {

                introductionFileParameters ifp = spmts.introductionFiles.get(i);
                pw1.println("\t<introFile>");
                pw1.println("\t\t<introURL>" + ifp.getUrl() + "</introURL>");
                pw1.println("\t\t<introCond>" + ifp.getCondition() + "</introCond>");
                pw1.println("\t</introFile>");
            }

            //write the standard tests to file
            for (int i = 0; i < spmts.standardTests.size(); i++) {
                //System.out.println("standardTest &&&");
                StandardTestParam stp = spmts.standardTests.get(i);
                pw1.println("\t<standardTest>");
                pw1.println("\t\t<standardTestURL>" + stp.getUrl() + "</standardTestURL>");
                pw1.println("\t\t<standardTestUserResponse>" + stp.getUserResponseInterface() + "</standardTestUserResponse>");
                pw1.println("\t\t<standardTestUserPerformance>" + stp.getUserPerformanceInterface() + "</standardTestUserPerformance>");
                pw1.println("\t</standardTest>");
            }

            //write the preStudytasks
            for (int i = 0; i < spmts.preStudyQuestions.size(); i++) {
                String preStudyTaskName = getTaskCode(spmts.preStudyQuestions.get(i), userid);
                pw1.println("\t<preStudyTask>");
                pw1.println("\t\t<name>" + preStudyTaskName + "</name>");
                pw1.println("\t\t<question>" + spmts.preStudyQuestions.get(i) + "</question>");
                pw1.println("\t</preStudyTask>");
            }

            //write the postStudytasks
            for (int i = 0; i < spmts.postStudyQuestions.size(); i++) {
                String postStudyTaskName = getTaskCode(spmts.postStudyQuestions.get(i), userid);
                pw1.println("\t<postStudyTask>");
                pw1.println("\t\t<name>" + postStudyTaskName + "</name>");
                pw1.println("\t\t<question>" + spmts.postStudyQuestions.get(i) + "</question>");
                pw1.println("\t</postStudyTask>");
            }

            //write the post study tasks
            pw1.println("</study_specification>");
            // pw1.println("Finished");            
            pw1.close();

            //copy the userstudy.html and result-graphs.html files to that folder as well
           /* Path FROM = Paths.get(getServletContext().getRealPath("userstudy.html"));
             Path TO = Paths.get(getServletContext().getRealPath("studies" + File.separator + studyname + File.separator + "userstudy.html"));
             //overwrite existing file, if exists
             CopyOption[] options = new CopyOption[]{
             StandardCopyOption.REPLACE_EXISTING,
             StandardCopyOption.COPY_ATTRIBUTES
             };
             Files.copy(FROM, TO, options);

             FROM = Paths.get(getServletContext().getRealPath("result-graphs.html"));
             TO = Paths.get(getServletContext().getRealPath("studies" + File.separator + studyname + File.separator + "result-graphs.html"));

             Files.copy(FROM, TO, options);  */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getTaskCode(String task, String userid) {

        String shortname = "";

        //check if we can find the task among the existing tasks, 
        //otherwise check the tasks in the user directory
        try {
            File qlFile = new File(getServletContext().getRealPath("quanttasks" + File.separator + "quanttasklist.txt"));

            BufferedReader br = new BufferedReader(new FileReader(qlFile));
            String line = "";
            String sn = "";
            while ((line = br.readLine()) != null) {
                sn = line.split(":::")[0].trim();
                String t = line.split(":::")[1].trim();
                if (t.equalsIgnoreCase(task.trim())) {
                    shortname = sn;
                    break;
                }
            }
            br.close();

            //now check if the shortname has already been found ampong the system's files
            if (shortname.trim().equalsIgnoreCase("")) {
                //get the shortname among the user's files
                qlFile = new File(getServletContext().getRealPath(
                        "users" + File.separator + userid + File.separator
                        + "quanttasks" + File.separator + "quanttasklist.txt"));

                br = new BufferedReader(new FileReader(qlFile));
                line = "";
                sn = "";
                while ((line = br.readLine()) != null) {
                    sn = line.split(":::")[0].trim();
                    String t = line.split(":::")[1].trim();
                    if (t.equalsIgnoreCase(task.trim())) {
                        shortname = sn;
                        break;
                    }
                }
                br.close();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("The short name is: " + shortname);

        return shortname;
    }

    public String getQualitativeTaskCode(String task) {
        String taskCode = "";
        if (task.equalsIgnoreCase("Rate the easiness of the visualization tasks  from 1-Not easy to 5-Very Easy")) {
            taskCode = "rate_vis_easiness";
        } else if (task.equalsIgnoreCase("Have you worked with this type of visualization before?")) {
            taskCode = "worked_with_vis_before";
        } else if (task.equalsIgnoreCase("How will you Rate your familiarity with this type of visualization prior to this study?")) {
            taskCode = "rate_vis_familiarity";
        } else if (task.equalsIgnoreCase("Please enter your Mechanical Turk ID")) {
            taskCode = "enter_turk_id";
        } else if (task.equalsIgnoreCase("Do you have any feedback,comment or what issue did you had in this study?")) {
            taskCode = "feedback_comment_issues";
        }

        return taskCode;
    }

    public String getTaskInterfaceMethods(String taskQn, HttpServletRequest request) {

        //read the quantitative tasks file and get the taskNode
        Node tasknode = getTaskNodeFromTaskFile(request, taskQn);
        //get the interface methods
        String inInterface = ((Element) tasknode).getElementsByTagName("inputinterface").item(0).getTextContent();
        String outInterface = ((Element) tasknode).getElementsByTagName("outputinterface").item(0).getTextContent();

        return inInterface + "::" + outInterface;
    }

    public Node getTaskNodeFromTaskFile(HttpServletRequest request, String taskname) {
        String taskFilename = "quanttasks.xml";
        String taskFileDir = "quanttasks";

        Node node = null;
        try {

            File xmlFile = new File(getServletContext().getRealPath(taskFileDir + File.separator + taskFilename));
            DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
            Document doc = dBuilder2.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList taskNodes = doc.getElementsByTagName("task");

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node tNode = taskNodes.item(i);
                if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tNode;

                    String curTaskName = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();

                    if (taskname.trim().equals(curTaskName.trim())) {
                        node = tNode;
                        break;
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return node;

    }

    public void printTheURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        String completeURL = requestURL.toString();

        System.out.println(" ---- " + completeURL + " --- ");
    }

    public static void deleteFile(File file)
            throws IOException {

        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            } else {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteFile(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        } else {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }

    public String getServerUrl(HttpServletRequest request) {
        String uri = request.getScheme() + "://" + // "http" + "://
                request.getServerName() + // "myhost"
                ":" + // ":"
                request.getServerPort() + // "8080"
                request.getRequestURI();//+       // "/people"

        int lastbackslash = uri.lastIndexOf("/");
        return uri.substring(0, lastbackslash);
    }

    /**
     * prepare and call a method to create the mturk HIT
     */
    public void createMTurkHIT(String title, String awsAccessKey, String secretKey, String reward, int maxAssignments, String questionTemplatePath) {

        /*  if (dataPath.indexOf("\\") >= 0) {
         dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
         }*/
        //System.out.println("THE FILE PATH IS ::: "+questionTemplatePath);
        MTurkRequestsMgr mturkrequestMgr = new MTurkRequestsMgr(title, awsAccessKey, secretKey, reward, maxAssignments, questionTemplatePath);
        //MTurkRequestsMgr mturkrequestMgr = new MTurkRequestsMgr(title, awsAccessKey, secretKey, reward, maxAssignments, questionTemplatePath, "");
        //MTurkRequestsMgr mturkrequestMgr = new MTurkRequestsMgr(questionTemplatePath);
        //request the hit to e created
        mturkrequestMgr.createHITRequest();

        //set the other variables if they are not null or empty
    /*    if (!title.isEmpty()) {
         mturkrequestMgr.setTitle(title);
         }
         if (reward >= 0.0) {
         mturkrequestMgr.setReward(reward + "");
         }
         if (!awsAccessKey.isEmpty()) {
         mturkrequestMgr.setAwsAccessKey(awsAccessKey);
         }
         if (!secretKey.isEmpty()) {
         mturkrequestMgr.setSecretKey(secretKey);
         }
         //request the hit to e created
         mturkrequestMgr.createHITRequest();
         */
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
