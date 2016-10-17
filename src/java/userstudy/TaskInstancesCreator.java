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
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
@WebServlet(name = "TaskInstancesCreator", urlPatterns = {"/TaskInstancesCreator"})
public class TaskInstancesCreator extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private final String DEFAULT_USER = "mershack";
    private final String CONFIG_DIR = "_config_files";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        try {

            String command = request.getParameter("command");

            String userid;
            if (command.equalsIgnoreCase("updateTaskInstanceData")) {
                /*We will get the json object of the study from the request, and save it 
                 on the server.
                 */

                String instanceData = request.getParameter("instanceData");
                String taskName = request.getParameter("taskName");
                String datasetName = request.getParameter("datasetName");
                String viewerName = request.getParameter("viewerName");
                userid = DEFAULT_USER;
                TaskInstances taskInstances = getTaskInstanceData(taskName, viewerName, datasetName, userid);

                Gson gson = new Gson();
                TaskInstance taskInstance = gson.fromJson(instanceData, TaskInstance.class);

                //append the new taskInstance to the existing taskInstances; 
                taskInstances.addNewInstance(taskInstance);
                String jsonStr = "";

                jsonStr = gson.toJson(taskInstances);

                //System.out.println("The task instances are : " + jsonStr);
                //now write to file 
                //we will compose the task instance name using the following format:  
                // instanceName = taskName_datasetName if there is a dataset or taskName_viewerName.
                String taskInstanceName = taskName;
                taskInstanceName += datasetName.trim().isEmpty() ? "_" + viewerName : "_" + datasetName;

                String filePath = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator
                        + "taskInstanceFiles" + File.separator + taskInstanceName + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(filePath));
                writer.write(jsonStr);
                writer.close();

                //we will compose the task instance name using the following format:  
                //now update task instance counter in the taskinstancedetails object
                String taskInstancesFilename = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator
                        + "taskInstances" + File.separator + taskInstanceName + ".json";

                File f = new File(getServletContext().getRealPath(taskInstancesFilename));

                TaskInstancesDetails taskinstancedetails;
                if (f.exists()) {
                    FileReader reader = new FileReader(f);

                    BufferedReader br = new BufferedReader(reader);

                    //now create the Gson object 
                    taskinstancedetails = gson.fromJson(br, TaskInstancesDetails.class);

                    //now increment the taskCounter by 1.
                    taskinstancedetails.incrementInstanceCount();
                } else {
                    //create new task instance details.
                    taskinstancedetails = new TaskInstancesDetails(taskName, viewerName, datasetName, "1");
                }
                //now we will write the instance detail  object to file
                String taskInstanceDetailsJson = gson.toJson(taskinstancedetails);

                writer = new FileWriter(getServletContext().getRealPath(taskInstancesFilename));
                writer.write(taskInstanceDetailsJson);
                writer.close();

            } else if (command.equalsIgnoreCase("getCountOfTaskInstanceData")) {
                //we will get the count of the existing task instance
                String taskName = request.getParameter("taskName");
                String datasetName = request.getParameter("datasetName");
                String viewerName = request.getParameter("viewerName");

                userid = DEFAULT_USER;
                TaskInstances taskInstances = getTaskInstanceData(taskName, viewerName, datasetName, userid);

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{\"count\":\"" + taskInstances.getInstances().length + "\" }");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).

            } else if (command.equalsIgnoreCase("updateTaskInstanceWithAFile")) {
                //we will be saving the file there. 

                String taskName = request.getParameter("taskName");
                String viewerName = request.getParameter("viewerName");
                String datasetName = request.getParameter("datasetName");

                userid = DEFAULT_USER;

                String taskInstanceName = taskName;
                taskInstanceName += datasetName.trim().isEmpty() ? "_" + viewerName : "_" + datasetName;

                String dirURL = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator
                        + "taskInstanceFiles";

                String dirPath = getServletContext().getRealPath(dirURL);
                File f = null;
                //write the file. 
                if (ServletFileUpload.isMultipartContent(request)) {

                    List<FileItem> multiparts = new ServletFileUpload(
                            new DiskFileItemFactory()).parseRequest(request);

                    // System.out.println("Hey");
                    for (FileItem item : multiparts) {
                        if (!item.isFormField()) {
                            f = new File(dirPath + File.separator + taskInstanceName + "_temp");//a temp file name;
                            item.write(f);
                        }
                    }

                }

                //read the saved file to know the count.
                int numOfInstances = 0;
                if (f != null) {
                    FileReader reader = new FileReader(f);
                    BufferedReader br = new BufferedReader(reader);
                    Gson gson = new Gson();

                    //now create the Gson object 
                    TaskInstances newInstances = gson.fromJson(br, TaskInstances.class);
                    br.close();
                    if (newInstances != null && newInstances.getInstances().length > 0) {
                        mergeTaskInstanceFiles(userid, taskName, viewerName, datasetName, newInstances);
                        
                        numOfInstances = newInstances.getInstances().length;
                    }
                    //now delete the file
                    f.delete();
                }

                //now we will return the count of instances that were saved.
                out.println(numOfInstances);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    public void mergeTaskInstanceFiles(String userid, String taskName, String viewerName, String datasetName, TaskInstances newInstances) {
        try {
            Gson gson = new Gson();
            String taskInstanceName = taskName;
            taskInstanceName += datasetName.trim().isEmpty() ? "_" + viewerName : "_" + datasetName;

            //doing the merging by first reading the existing task instances.
            TaskInstances existingTaskInstances = getTaskInstanceData(taskName, viewerName, datasetName, userid);
            
            System.out.println("Size of Existing is : "+ existingTaskInstances.getInstances().length);
            System.out.println("Size of new is : "+ newInstances.getInstances().length);
            //now add the newinstances to the old instances.{
            for(int i=0; i<newInstances.getInstances().length; i++){
                existingTaskInstances.addNewInstance(newInstances.getInstances()[i]);
            }
            
            //now write the merged instances to file. 
             String jsonStr = "";

                jsonStr = gson.toJson(existingTaskInstances);

               String filePath = "users" + File.separator + userid + File.separator
                        + "_config_files" + File.separator
                        + "taskInstanceFiles" + File.separator + taskInstanceName + ".json";

                FileWriter writer = new FileWriter(getServletContext().getRealPath(filePath));
                writer.write(jsonStr);
                writer.close();
            
            //Updating task instance counter in the taskinstancedetails object
            String taskInstanceDetailsFilename = "users" + File.separator + userid + File.separator
                    + "_config_files" + File.separator
                    + "taskInstances" + File.separator + taskInstanceName + ".json";

            File f2 = new File(getServletContext().getRealPath(taskInstanceDetailsFilename));

            TaskInstancesDetails taskinstancedetails;
            if (f2.exists()) {
                FileReader reader = new FileReader(f2);

                BufferedReader br = new BufferedReader(reader);
                //now create the Gson object 
                
                taskinstancedetails = gson.fromJson(br, TaskInstancesDetails.class);
                br.close();

                int instcount = Integer.parseInt(taskinstancedetails.getInstanceCount())
                        + newInstances.getInstances().length;

                taskinstancedetails.setInstanceCount(instcount + "");

            } else {
                //create new task instance details.
                taskinstancedetails = new TaskInstancesDetails(taskName, viewerName, datasetName, "" + newInstances.getInstances().length);
            }

            //now we will write the instance detail  object to file
            String taskInstanceDetailsJson = gson.toJson(taskinstancedetails);

            writer = new FileWriter(getServletContext().getRealPath(taskInstanceDetailsFilename));
            writer.write(taskInstanceDetailsJson);
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TaskInstances getTaskInstanceData(String taskName, String viewerName, String datasetName, String userid) {
        TaskInstances taskInstances = new TaskInstances();
        try {
            //we will compose the task instance name using the following format:  
            // instanceName = taskName_datasetName if there is a dataset or taskName_viewerName.
            String taskInstanceName = taskName;
            taskInstanceName += datasetName.trim().isEmpty() ? "_" + viewerName : "_" + datasetName;

            //we will load the task instance file
            String filePath = "users" + File.separator + userid + File.separator
                    + "_config_files" + File.separator
                    + "taskInstanceFiles" + File.separator + taskInstanceName + ".json";

            Gson gson = new Gson();
            File f = new File(getServletContext().getRealPath(filePath));

            //check if file exists and read the current content
            if (f.exists()) {
                FileReader reader = new FileReader(f);

                BufferedReader br = new BufferedReader(reader);

                taskInstances = gson.fromJson(br, TaskInstances.class);
                br.close();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return taskInstances;
    }

    public String getServerUrl(HttpServletRequest request) {
        String uri = request.getScheme() + "://" + // "http" + "://
                request.getServerName() + // "myhost"
                ":" + // ":"
                request.getServerPort() + // "8080"
                request.getRequestURI();//+       // "/people"
        // "?" +                           // "?"
        // request.getQueryString(); 

        int lastbackslash = uri.lastIndexOf("/");
        return uri.substring(0, lastbackslash);
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
