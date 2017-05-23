/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Radu
 */
public class FileManagement extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    private final String DATA_DIR = "data";
    private final String DEFAULT_USER = "mershack";
    private final String CONFIG_DIR = "_config_files";
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
           
            HttpSession session = request.getSession();

            String userid = null;
            if (session.getAttribute("username") != null) {
                userid = session.getAttribute("username").toString();
                System.out.println("FileManagement: " + userid);
            }
            //else
              //  userid = DEFAULT_USER;

            String command = request.getParameter("command");
           

            if (command.equalsIgnoreCase("load")) {
                /*We will load all the directories of the user, and return a JSON
                 file that contains all the existing directories.                    
                 The format of the json is as follows:
                
                 {name:"root", isDir:true, children:[
                    {name : "dir1", isDir:true, children:[]},
                    {name : "dir2", isDir:true, children:[
                            {name : "f1.txt", isDir:false, children:[]}
                    ]},
                {name : "f1.txt", isDir:false, children:[]},
                {name : "f2.txt", isDir:false, children:[]}]};
                 */

               // userid = DEFAULT_USER;
                //load all the directories the user has created.
                String userDirsURL = "users" + File.separator + userid;

                File f = new File(getServletContext().getRealPath(userDirsURL));
                
                String jsonDirStruct = createJsonDirStructString(f);

                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();

                out2.print(jsonDirStruct);                
            }
            else if (command.equalsIgnoreCase("upload")) {
                String dirName = request.getParameter("path");


                String dirURL = "users" + File.separator + userid + File.separator + dirName;
                

                String dirPath = getServletContext().getRealPath(dirURL);
                
                System.out.println("upload: " + dirPath);

                // File viewerDir = new File(dirPath);
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {

                                String name = new File(item.getName()).getName();
                                //now write the fi in that directory
                                item.write(new File(dirPath + File.separator + name));
                            }
                        }
                        
                        response.setContentType("application/json;charset=UTF-8");
                        PrintWriter out2 = response.getWriter();
                        out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }
                }
            } else if (command.equalsIgnoreCase("mkDir")) {
                String dirName = request.getParameter("path"); //should be a path
               
                 String dirURL = "users" + File.separator + userid + File.separator + dirName;
                String dirPath = getServletContext().getRealPath(dirURL);
                
                System.out.println("mkdir: " + dirPath);

                File file = new File(dirPath);

                if (!file.exists()) {
                    file.mkdir();
                }
                
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).

            } else if (command.equalsIgnoreCase("rm")) { 
                
                String what = request.getParameter("path");
                String dirURL = "users" + File.separator + userid + File.separator + what;
                String dirPath = getServletContext().getRealPath(dirURL);
                
                System.out.println("remove: " + dirPath);
                File file = new File(dirPath);
                
                if (file.isDirectory())
                    deleteDirectory(file);
                else
                    file.delete();
                
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print("{}");//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).
            }          
            else{
                
            }
            
        } catch(Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            out.close();
        }
    }
    
    
       public String createJsonDirStructString(File f){
        /*the format of the json is as follows:
                
                 {name:"root", isDir:true, children:[
                    {name : "dir1", isDir:true, children:[]},
                    {name : "dir2", isDir:true, children:[
                            {name : "f1.txt", isDir:false, children:[]},
                            {name : "f2.txt", isDir:false, children:[]}
                    ]},
                {name : "f1.txt", isDir:false, children:[]},
                {name : "f2.txt", isDir:false, children:[]}]};*/
                 
        File[] files = f.listFiles();

        String json = "{";  //beginning of the json array.
        json += "\"name\" : \"" + f.getName() + "\"";
        json += ", \"isDir\" : true";
        json += ", \"children\" : [";

        for (int i = 0; files != null && i < files.length; i++) {
            //check if it is a directory and not the _config_files directory.
            if (files[i].getName().equalsIgnoreCase("_config_files")) continue;
            
            if ( i != 0) json += ",";
            if (files[i].isDirectory())
                json += createJsonDirStructString(files[i]);
            else
                json += "{\"name\" :\"" + files[i].getName() + "\", \"isDir\" : false, \"children\" : []}";
               
        }        
        json += "]}";
        return json;
    }
    
    private static boolean deleteDirectory(File directory) {
    if(directory.exists()){
        File[] files = directory.listFiles();
        if(null!=files){
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
    }
    return(directory.delete());
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
