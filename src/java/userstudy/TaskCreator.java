/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mershack
 */
@WebServlet(name = "TaskCreator", urlPatterns = {"/TaskCreator"})
public class TaskCreator extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            String command = request.getParameter("command");

            if (command.equalsIgnoreCase("checkTaskNameAvailability")) {

                String msg = "notExists";

                //read the quanttask file to see if the name exists
                String taskShortName = request.getParameter("taskShortName").toString();

                String userid = request.getParameter("userid").toString();

                if (!taskShortName.trim().isEmpty()) {
                    //read the quant-task-files.
                    String tasklistFilename = getServletContext().getRealPath(
                            "users" + File.separator + userid + File.separator
                            + "quanttasks" + File.separator + "quanttasklist.txt");
                    File taskListFile = new File(tasklistFilename);

                    String line = "";

                    if (taskListFile.exists()) {
                        BufferedReader br = new BufferedReader(new FileReader(taskListFile));

                        boolean exists = false;
                        br.readLine(); //read the header.
                        while ((line = br.readLine()) != null) {
                            String shortname = line.split(":::")[0];
                            if (taskShortName.trim().equalsIgnoreCase(shortname.trim())) {
                                // System.out.println("--Exists;");
                                msg = "exists";
                                break;
                            }
                        }
                    }

                    //now check the main existing files in VisUnit.
                    String sysTasklistFilename = getServletContext().getRealPath(
                            "quanttasks" + File.separator + "quanttasklist.txt");
                    File sysTaskListFile = new File(sysTasklistFilename);

                    BufferedReader br2 = new BufferedReader(new FileReader(sysTaskListFile));
                    line = "";
                    br2.readLine();
                    while ((line = br2.readLine()) != null) {
                        //System.out.println("&&&---name is "+taskShortName + " and the line is " + line);
                        String shortname = line.split(":::")[0];
                        if (taskShortName.trim().equalsIgnoreCase(shortname.trim())) {

                            // System.out.println("*** Exists");
                            msg = "exists";
                            break;
                        }
                    }                    
                    br2.close();
                }

                out.write(msg);

            } else if (command.equalsIgnoreCase("saveNewTask")) {
                //First Get the parameters of the tasks
                String taskQuestion = request.getParameter("taskQuestion").toString();
                String taskDescription = request.getParameter("taskDescription").toString();
                String taskShortName = request.getParameter("taskShortName").toString();
                String answerType = request.getParameter("answerType").toString();
                //String inputInterface = request.getParameter("inputInterface").toString();
                String outputType = request.getParameter("outputType").toString();
                String outputTypeDescription = request.getParameter("outputTypeDescription").toString();
                String numberOfInputs = request.getParameter("numberOfInputs").toString();
                String answerOptions = request.getParameter("answerOptions").toString();
                String inputTypeShortNames = request.getParameter("inputTypeShortNames").toString();
                String inputTypeDescriptions = request.getParameter("inputTypeDescriptions").toString();
                String inputMediums = request.getParameter("inputMediums").toString();
                String userid = request.getParameter("userid").toString();
                String accuracyCheckingInterface = request.getParameter("accuracyCheckingInterface");
                //String questionInputInfos = request.getParameter("questionInputs").toString();

                String inputTypeShortNamesArr[] = inputTypeShortNames.split(":::");
                String inputTypeDescriptionsArr[] = inputTypeDescriptions.split(":::");
                String inputMediumsArr[] = inputMediums.split(":::");

                String hasCorrectAnswer = request.getParameter("hasCorrectAnswer").toString();

                //String questionInputInfosArr[] = questionInputInfos.split(":::");
                //conjugate the answertype
                if (answerType.equalsIgnoreCase("interface")) {
                    answerType = "interface";
                } else {

                    if (answerType.equalsIgnoreCase("options-fixed")) {
                        answerType = answerType + ":::" + answerOptions;
                    } else {
                        answerType = answerType;
                    }

                }
                  //if the 

                //if user is admin or any of our admin id's we will save the task in the main task
                //directory otherwise we will save it in the respective user directory.
                if (!userid.equalsIgnoreCase("admin")) {
                    //write the xml file in that respective user directory.

                    File userdir = new File(getServletContext().getRealPath("users"
                            + File.separator + userid));

                    File quantTaskDir = new File(getServletContext().getRealPath("users"
                            + File.separator + userid + File.separator + "quanttasks"));

                    //check if the user directory exists.
                    //if it does not exist, create it and create the other respective directories,
                    //even if it exists, check if the other respective directories exist and create them too.
                    if (userdir.exists()) {
                        //System.out.println("The directory exists");
                        //check if the quanttasks directory exists, if it does not exist, create it.

                        if (!quantTaskDir.exists()) {
                            //create the quanttasks sub directory
                            quantTaskDir.mkdir();
                        }
                    } else {
                        //create the user directory and create the quanttask subdirectory.
                        userdir.mkdir();
                        quantTaskDir.mkdir();
                    }

                    File userQuantTaskFile = new File(getServletContext().getRealPath("users" + File.separator
                            + userid + File.separator + "quanttasks" + File.separator + taskShortName + ".xml"));

                    BufferedWriter bw1 = new BufferedWriter(new FileWriter(userQuantTaskFile));

                    PrintWriter pw1 = new PrintWriter(bw1);

                    pw1.println("<?xml version=\"1.0\"?>");   //first line of the xml
                    pw1.println("<task_details>");
                    pw1.println("\t<taskname>" + taskShortName + "</taskname>");
                    pw1.println("\t<accuracyCheckingInterface>" + accuracyCheckingInterface + "</accuracyCheckingInterface>");
                    pw1.println("\t<outputtype>" + outputType + "</outputtype>");
                    pw1.println("\t<outputTypeDescription>" + outputTypeDescription + "</outputTypeDescription>");
                    pw1.println("\t<answertype>" + answerType + "</answertype>");
                    pw1.println("\t<taskquestion>" + taskQuestion + "</taskquestion>");
                    pw1.println("\t<taskDescription>" + taskDescription + "</taskDescription>");
                    pw1.println("\t<inputsize>" + numberOfInputs + "</inputsize>");
                    pw1.println("<hasCorrectAnswer>" + hasCorrectAnswer + "</hasCorrectAnswer>");

                    //create the input elements
                    for (int i = 0; i < Integer.parseInt(numberOfInputs); i++) {
                        pw1.println("\t<input>"); //beginning of the input tag

                        //System.out.println(questionInputInfosArr[i]);                        
                        pw1.println("\t\t<inputtype>" + inputTypeShortNamesArr[i]
                                + "</inputtype>");
                        pw1.println("\t\t<inputdescription>" + inputTypeDescriptionsArr[i]
                                + "</inputdescription>");

                        pw1.println("\t\t<inputmedium>" + inputMediumsArr[i]
                                + "</inputmedium>");
                        /* pw1.println("\t\t<partofaquestion>" + questionInputInfosArr[i] 
                         + "</partofaquestion>");   */

                        pw1.println("\t</input>"); //end of the input tag                     
                    }

                    pw1.println("</task_details>");

                    pw1.close();
                    bw1.close();

                    //also append the short name to the end of the task list file
                    File taskListFile = new File(getServletContext().getRealPath("users"
                            + File.separator + userid + File.separator
                            + "quanttasks" + File.separator + "quanttasklist.txt"));

                    boolean newFile = false;
                    if (!taskListFile.exists()) {
                        taskListFile.createNewFile();
                        //write the header too.
                        newFile = true;
                    }

                    FileWriter fw = new FileWriter(taskListFile, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);

                    if (newFile) {
                        pw.println("shortname ::: taskquestion ::: taskgroup");
                    }

                    pw.println(taskShortName + " ::: " + taskQuestion + " ::: " + " noGroup");  //noGroup now, but later we can let the user specify their own goup.

                    //close the streams.
                    pw.close();
                    fw.close();
                    bw.close();

                } else {

                    //Write an xml file with it.
                    String quanTaskFileName = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");

                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document doc = documentBuilder.parse(quanTaskFileName);
                    //Get the root element
                    Node root = doc.getFirstChild();

                    //create the task element
                    Element taskElem = doc.createElement("task");
                    //create the elements that will be below the task element
                    Element tasknameElem = doc.createElement("taskname");
                    //Element inpInterfaceElem = doc.createElement("inputinterface");
                    Element outInterfaceElem = doc.createElement("outputtype");
                    Element outputTypeDescripElem = doc.createElement("outputTypeDescription");
                    Element answertypeElem = doc.createElement("answertype");
                    Element taskquestionElem = doc.createElement("taskquestion");
                    Element taskDescriptionElem = doc.createElement("taskDescription");
                    Element inputsizeElem = doc.createElement("inputsize");

                    tasknameElem.appendChild(doc.createTextNode(taskShortName));
                    taskquestionElem.appendChild(doc.createTextNode(taskQuestion));
                    taskDescriptionElem.appendChild(doc.createTextNode(taskDescription));
                    answertypeElem.appendChild(doc.createTextNode(answerType));
                    // inpInterfaceElem.appendChild(doc.createTextNode(inputInterface));
                    outInterfaceElem.appendChild(doc.createTextNode(outputType));
                    outputTypeDescripElem.appendChild(doc.createTextNode(outputTypeDescription));
                    inputsizeElem.appendChild(doc.createTextNode(numberOfInputs));

                    //append the elements to the task element
                    taskElem.appendChild(tasknameElem);
                    //taskElem.appendChild(inpInterfaceElem);
                    taskElem.appendChild(outInterfaceElem);
                    taskElem.appendChild(outputTypeDescripElem);
                    taskElem.appendChild(answertypeElem);
                    taskElem.appendChild(taskquestionElem);
                    taskElem.appendChild(taskDescriptionElem);
                    taskElem.appendChild(inputsizeElem);

                    //create the input elements
                    for (int i = 0; i < Integer.parseInt(numberOfInputs); i++) {
                        Element inputElem = doc.createElement("input");

                        Element inputShortNameElem = doc.createElement("inputtype");
                        inputShortNameElem.appendChild(doc.createTextNode(inputTypeShortNamesArr[i]));

                        Element inputDescriptionElem = doc.createElement("inputdescription");
                        inputDescriptionElem.appendChild(doc.createTextNode(inputTypeDescriptionsArr[i]));

                        inputElem.appendChild(inputShortNameElem);
                        inputElem.appendChild(inputDescriptionElem);
                        //append the input Elem to the task Elem.
                        taskElem.appendChild(inputElem);
                    }

                    //append the task node to the root node.
                    root.appendChild(taskElem);

                    //write it to file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File(quanTaskFileName));
                    transformer.transform(source, result);
                }

            } else if (command.equalsIgnoreCase("uploadNewTaskXMLFile")) {

                String userid = request.getParameter("userid").toString();
                String taskShortname = request.getParameter("taskShortName").toString();

                String taskDirUrl = "users" + File.separator + userid + File.separator
                        + "quanttasks";

                if (ServletFileUpload.isMultipartContent(request)) {
                    //  System.out.println("here now");
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {

                                String name = new File(item.getName()).getName();

                                //get the name for the dataset without the file extensions
                                String taskName = taskShortname + ".xml";

                                //  System.out.println("dataset name is " + dsName);
                                String taskDirPath = getServletContext()
                                        .getRealPath(taskDirUrl + File.separator + taskName);

                                //File taskDir = new File(taskDirPath);
                                //  System.out.println(taskDirPath);
                                //now write the dataset in that directory
                                item.write(new File(taskDirPath));

                                System.out.println("here");

                                //now read the file to get the task question
                                File fXmlFile = new File(taskDirPath);
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(fXmlFile);

                                //  System.out.println("here2");
                                boolean validFile = true;

                                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                                String taskQn = "";

                                NodeList taskNode = doc.getElementsByTagName("task_details");
                                if (taskNode != null) {
                                    System.out.println("here3");
                                    Node nNode = taskNode.item(0);
                                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eElement = (Element) nNode;
                                        taskQn = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();

                                    } else {
                                        validFile = false;
                                    }
                                } else {
                                    validFile = false;
                                }

                                if (validFile) {
                                    //now write this to the quantlist 
                                    //also append the short name to the end of the task list file
                                    File taskListFile = new File(getServletContext().getRealPath("users"
                                            + File.separator + userid + File.separator
                                            + "quanttasks" + File.separator + "quanttasklist.txt"));

                                    boolean newFile = false;
                                    if (!taskListFile.exists()) {
                                        taskListFile.createNewFile();
                                        //write the header too.
                                        newFile = true;
                                    }

                                    FileWriter fw = new FileWriter(taskListFile, true);
                                    BufferedWriter bw = new BufferedWriter(fw);
                                    PrintWriter pw = new PrintWriter(bw);

                                    if (newFile) {
                                        pw.println("shortname ::: taskquestion ::: taskgroup");
                                    }

                                    pw.println(taskShortname + " ::: " + taskQn + " ::: " + " noGroup");  //noGroup now, but later we can let the user specify their own goup.

                                    //System.out.println("finish printing");
                                    //close the streams.
                                    pw.close();
                                    fw.close();
                                    bw.close();

                                    out.print("successful");
                                } else {
                                    out.print("failed");
                                }

                            }

                        }
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }

                }
            } else if (command.equalsIgnoreCase("uploadNewTaskInstancesXMLFile")) {
                String userid = request.getParameter("userid").toString();
                String task = request.getParameter("task").toString();
                String viewer = request.getParameter("viewer").toString();
                String dataset = request.getParameter("dataset").toString();

                // if(viewer.trim()===)
                String name = dataset.trim();

                if (name.isEmpty()) {
                    name = viewer.trim();
                }

                //We will be adding this to the task instances rather.
                String taskInstanceDirUrl = "users" + File.separator + userid + File.separator
                        + "taskInstances" + File.separator + name;

                //create the directory that the task instance will be saved in if 
                File taskInstanceDir = new File(getServletContext()
                        .getRealPath(taskInstanceDirUrl));

                if (!taskInstanceDir.exists()) {
                    taskInstanceDir.mkdir();
                }

                //get the taskShort name too.
                String taskName = getTaskCode(task, userid);

                if (ServletFileUpload.isMultipartContent(request)) {
                    //  System.out.println("here now");
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {

                                //String name = new File(item.getName()).getName();
                                //get the name for the dataset without the file extensions
                                taskName = taskName + ".xml";

                                //  System.out.println("dataset name is " + dsName);
                                String taskInstanceFilePath = getServletContext()
                                        .getRealPath(taskInstanceDirUrl + File.separator + taskName);

                                //now write the dataset in that directory
                                item.write(new File(taskInstanceFilePath));

                                //now read the file to get the task question
                                File fXmlFile = new File(taskInstanceFilePath);
                                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                                Document doc = dBuilder.parse(fXmlFile);

                                //  System.out.println("here2");
                                boolean validFile = true;

                                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                                String taskQn = "";

                                NodeList taskNode = doc.getElementsByTagName("taskFile");
                                if (taskNode != null) {
                                    // System.out.println("here3");
                                    Node nNode = taskNode.item(0);
                                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eElement = (Element) nNode;
                                        taskQn = eElement.getElementsByTagName("question").item(0).getTextContent();

                                    } else {
                                        validFile = false;
                                    }
                                } else {
                                    validFile = false;
                                }
                                
                                //System.out.println(taskInstanceFilePath + " :: ");
                                
                                

                                if (validFile) {
                                    out.print("successful");
                                } else {
                                    out.print("failed");
                                }

                            }

                        }
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            out.close();
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
