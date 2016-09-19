package userstudy;

import com.amazon.mturk.requester.MTurkRequestsMgr;
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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
                System.out.println("username is: " + username);
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
                userid = "mershack";

                //load all the study names.
                String studiesURL = "users" + File.separator + userid + File.separator
                        + "studies";

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

                System.out.println(jsonOfAllStudies);

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonOfAllStudies);

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

            String studyDetailsUrl = "users" + File.separator + userid + File.separator
                    + "studies" + File.separator + studyname
                    + File.separator + "data" + File.separator
                    + "quantitativeTasks.xml";

            File xmlFile = new File(getServletContext().getRealPath(studyDetailsUrl));
            DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
            Document doc = dBuilder2.parse(xmlFile);
            doc.getDocumentElement().normalize();
            // NodeList taskNodes = doc.getElementsByTagName("task");

            ArrayList<TaskDetails> tds = new ArrayList<TaskDetails>();
            String experimentType_vis = "";
            String experimentType_ds = "";
            ArrayList<String> conditionurl = new ArrayList<String>();
            ArrayList<String> conditionShortNames = new ArrayList<String>();
            ArrayList<String> datasets = new ArrayList<String>();

            String viewerWidth = "";
            String viewerHeight = "";
            // String dataset = "";
            //String datasetFormat = "";
            String trainingSize = "";

            //get the dataset
            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList taskNodes = doc.getElementsByTagName("task");
            //NodeList datasetNode = doc.getElementsByTagName("dataset");
            //NodeList datasetFormatNode = doc.getElementsByTagName("datasetFormat");
            //NodeList datasetTypeNode = doc.getElementsByTagName("datasetType");
            NodeList experimentTypeNode_vis = doc.getElementsByTagName("experimenttype_vis");
            NodeList experimentTypeNode_ds = doc.getElementsByTagName("experimenttype_ds");

            NodeList conditionNode = doc.getElementsByTagName("condition");
            NodeList viewerwidthNode = doc.getElementsByTagName("viewerwidth");
            NodeList viewerheightNode = doc.getElementsByTagName("viewerheight");
            NodeList trainingSizeNode = doc.getElementsByTagName("trainingsize");
            NodeList datasetConditionsNode = doc.getElementsByTagName("datasetCondition");
            NodeList introFilesNode = doc.getElementsByTagName("introFile");
            NodeList standardTestsNode = doc.getElementsByTagName("standardTest");

            /* do the datasets */
            ArrayList<String> datasetNames = new ArrayList<String>();
            ArrayList<String> datasetFormats = new ArrayList<String>();

            //if there are datasets do the following:
            if (datasetConditionsNode != null) {
                ArrayList<String> durl = new ArrayList<String>();
                for (int i = 0; i < datasetConditionsNode.getLength(); i++) {
                    Node nNode = datasetConditionsNode.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        String datasetName = eElement.getElementsByTagName("dataset").item(0).getTextContent();
                        String datasetFormat = eElement.getElementsByTagName("datasetFormat").item(0).getTextContent();
                        String datasetType = eElement.getElementsByTagName("datasetType").item(0).getTextContent();

                        datasetNames.add(datasetName);
                        datasetFormats.add(datasetFormat);
                    }
                }
            }

            //do the intro Files.
            ArrayList<String> introFiles = new ArrayList<String>();
            ArrayList<String> introDirectories = new ArrayList<String>();

            ArrayList<String> introConds = new ArrayList<String>();
            ArrayList<String> introNames = new ArrayList<String>();
            ArrayList<String> introDesc = new ArrayList<String>();
            //if there are intro files do the following.
            if (introFilesNode != null) {
                for (int i = 0; i < introFilesNode.getLength(); i++) {

                    Node nNode = introFilesNode.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        String introName = eElement.getElementsByTagName("name").item(0).getTextContent();
                        String introUrl = eElement.getElementsByTagName("introURL").item(0).getTextContent();

                        String introDir = introUrl.split("/")[0];
                        String introFile = introUrl.split("/")[1];

                        String introCond = eElement.getElementsByTagName("introCond").item(0).getTextContent();
                        String desc = eElement.getElementsByTagName("description").item(0).getTextContent();

                        introNames.add(introName);
                        introDirectories.add(introDir);
                        introFiles.add(introFile);
                        introConds.add(introCond);
                        introDesc.add(desc);

                    }

                }
            }

            //do the standard tests
            ArrayList<String> testNames = new ArrayList<String>();
            ArrayList<String> testDescriptions = new ArrayList<String>();
            ArrayList<String> testDirectories = new ArrayList<String>();
            ArrayList<String> testFiles = new ArrayList<String>();
            if (standardTestsNode != null) {
                for (int i = 0; i < standardTestsNode.getLength(); i++) {

                    Node nNode = standardTestsNode.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        String testName = eElement.getElementsByTagName("standardTestName").item(0).getTextContent();
                        String testDescription = eElement.getElementsByTagName("standardTestDescription").item(0).getTextContent();

                        String url = eElement.getElementsByTagName("standardTestURL").item(0).getTextContent();

                        String testDirectory = url.split("/")[0];
                        String testFile = url.split("/")[1];

                        testNames.add(testName);
                        testDescriptions.add(testDescription);
                        testDirectories.add(testDirectory);
                        testFiles.add(testFile);
                    }
                }
            }

            experimentType_vis = ((Element) experimentTypeNode_vis.item(0)).getTextContent();
            experimentType_ds = ((Element) experimentTypeNode_ds.item(0)).getTextContent();

            viewerWidth = ((Element) viewerwidthNode.item(0)).getTextContent();
            viewerHeight = ((Element) viewerheightNode.item(0)).getTextContent();
            trainingSize = ((Element) trainingSizeNode.item(0)).getTextContent();

            // dataset = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node tNode = taskNodes.item(i);
                String tname = "";
                String question = "";
                String size = "";
                String time = "";
                if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tNode;
                    //get the details of the task.
                    tname = eElement.getElementsByTagName("name").item(0).getTextContent();
                    question = eElement.getElementsByTagName("question").item(0).getTextContent();
                    size = eElement.getElementsByTagName("size").item(0).getTextContent();
                    time = eElement.getElementsByTagName("time").item(0).getTextContent();
                }

                //create and add the tasks.                      
                TaskDetails td = new TaskDetails(tname, question, size, time);
                tds.add(td);

            }
            //conditions
            for (int i = 0; i < conditionNode.getLength(); i++) {
                Node cNode = conditionNode.item(i);

                String url = "";
                String shortname = "";

                if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) cNode;

                    // System.out.println(eElement.getElementsByTagName("conditionurl").getLength());
                    //get the details of the task.
                    url = eElement.getElementsByTagName("conditionurl").item(0).getTextContent();

                    shortname = eElement.getElementsByTagName("conditionshortname").item(0).getTextContent();
                }
                conditionurl.add(url);
                conditionShortNames.add(shortname);
            }

            //now compose a json object and send it to the viewer.
            jsonStr = "{";
            jsonStr += "\"name\":\"" + studyname + "\"";
            /*  jsonStr += ",\"dataset\":\"" + dataset + "\"";
             jsonStr += ",\"datasetFormat\":\"" + datasetFormat + "\"";   */
            jsonStr += ",\"viewerDesign\":\"" + experimentType_vis + "\"";
            jsonStr += ",\"dataDesign\":\"" + experimentType_ds + "\"";
            jsonStr += ",\"viewerWidth\":\"" + viewerWidth + "\"";
            jsonStr += ",\"viewerHeight\":\"" + viewerHeight + "\"";
            jsonStr += ",\"trainingSize\":\"" + trainingSize + "\"";

            //get the conditions also
            jsonStr += ",\"conditions\": [";
            for (int i = 0; i < conditionurl.size(); i++) {
                if (i == 0) {
                    jsonStr += "{\"url\": \"" + conditionurl.get(i) + "\""
                            + ",\"shortname\": \"" + conditionShortNames.get(i) + "\"}";

                } else {
                    jsonStr += ",{\"url\": \"" + conditionurl.get(i) + "\""
                            + ",\"shortname\": \"" + conditionShortNames.get(i) + "\"}";

                }
            }
            jsonStr += "]";

            //get the datasets also
            jsonStr += ",\"datasets\": [";
            for (int i = 0; i < datasetNames.size(); i++) {

                if (i == 0) {
                    jsonStr += "{\"name\": \"" + datasetNames.get(i) + "\""
                            + ",\"format\": \"" + datasetFormats.get(i) + "\"}";
                } else {
                    jsonStr += ",{\"name\": \"" + datasetNames.get(i) + "\""
                            + ",\"format\": \"" + datasetFormats.get(i) + "\"}";
                }
            }
            jsonStr += "]";

            //get the introductions also
           jsonStr += ",\"intros\": [";
           System.out.println(introNames.size());
           System.out.println(introDesc.size());
           
            for (int i = 0; i < introNames.size(); i++) {
                if (i == 0) {
                    jsonStr += "{\"name\": \"" + introNames.get(i) + "\""
                            + ",\"directory\": \"" + introDirectories.get(i) + "\""
                            + ",\"file\": \"" + introFiles.get(i) + "\""
                            + ",\"cond\": \"" + introConds.get(i) + "\""
                            + ", \"description\": \"" + introDesc.get(i).trim() + "\"}";
                } else {
                    jsonStr += ",{\"name\": \"" + introNames.get(i) + "\""
                            + ",\"directory\": \"" + introDirectories.get(i) + "\""
                            + ",\"file\": \"" + introFiles.get(i) + "\""
                            + ",\"cond\": \"" + introConds.get(i) + "\""
                            + ", \"description\": \"" + introDesc.get(i).trim() + "\"}";
                }
            }
            jsonStr += "]"; 

            //get standardTest 
          jsonStr += ",\"standardTests\": [";
            for (int i = 0; i < testNames.size(); i++) {
                if (i == 0) {
                    jsonStr += "{\"name\": \"" + testNames.get(i) + "\""
                            + ",\"description\": \"" + testDescriptions.get(i) + "\""
                            + ",\"directory\": \"" + testDirectories.get(i) + "\""
                            + ",\"file\": \"" + testFiles.get(i) + "\"}";
                } else {
                    jsonStr += ",{\"name\": \"" + testNames.get(i) + "\""
                            + ",\"description\": \"" + testDescriptions.get(i) + "\""
                            + ",\"directory\": \"" + testDirectories.get(i) + "\""
                            + ",\"file\": \"" + testFiles.get(i) + "\"}";
                }
            }
            jsonStr += "]"; 

            //get the tasks also
            jsonStr += ",\"tasks\": [";
            for (int i = 0; i < tds.size(); i++) {
                TaskDetails td = tds.get(i);
                if (i == 0) {
                    //name, question, size, time
                    jsonStr += "{\"name\": \"" + td.getTaskname() + "\""
                            + ",\"question\": \"" + td.getTaskQuestion() + "\""
                            + ",\"size\": \"" + td.getQuestionSize() + "\""
                            + ",\"time\": \"" + td.getTime() + "\""
                            + "}";

                } else {
                    jsonStr += ",{\"name\": \"" + td.getTaskname() + "\""
                            + ",\"question\": \"" + td.getTaskQuestion() + "\""
                            + ",\"size\": \"" + td.getQuestionSize() + "\""
                            + ",\"time\": \"" + td.getTime() + "\""
                            + "}";

                }
            }
            jsonStr += "]";

            jsonStr += "}";

                    //we will be sending the json along
                   /* String jsonFileName2 = getServerUrl(request) + "/users/" + userid
             + "/studies/" + spmts.studyname
             + "/data/" + "jsonFile.json";   */
            //   System.out.println(getServletContext().getRealPath(jsonFileName2));
                   /* pw.close();
             bw.close();  */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonStr;
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
