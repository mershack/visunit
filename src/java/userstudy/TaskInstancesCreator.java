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
                
               
                //now create the Gson object 
//                Gson gson = new Gson();
//
//                Study study = (Study) gson.fromJson(instanceData.toString(), Study.class);
//                //now we will write the study object to file
//
//                String studyJson = gson.toJson(study);
//
//                userid = DEFAULT_USER;
//
//                //load all the study names.
//                String studiesURL = "users" + File.separator + userid + File.separator
//                        + "_config_files" + File.separator + "studies";
//
//                String jsonFilename = studiesURL + File.separator + study.getName()
//                        + File.separator + "data" + File.separator + "quantitativeTasks.json";
//
//                System.out.println("The filename is : " + jsonFilename);
//
//                FileWriter writer = new FileWriter(getServletContext().getRealPath(jsonFilename));
//                writer.write(studyJson);
//                writer.close();

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).

            } 
            
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
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
