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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
@WebServlet(name = "UserAccount", urlPatterns = {"/UserAccount"})
public class UserAccount extends HttpServlet {

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
        try {

            HttpSession session = request.getSession();

            String command = request.getParameter("command").toString();

            /* System.out.println("command is: " + command); */
            if (command.equalsIgnoreCase("createNewAccount")) {
                //get the username
                //get the password
                //save this to an xml file               

                String username = request.getParameter("username");
                String firstname = "default";
                String lastname = "default";
                String password = request.getParameter("password");

      
                
                if (checkUserNameAvailability(username)) {
                    System.out.println("Account exists: " + username + ", " + password);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    //out.print("false");
                } else {
                    System.out.println("Creating account: " + username + ", " + password);
                     
                    ///set everything up everything else
                    
                    session.setAttribute("username", username);                
                
                    File userDir = new File(getServletContext()
                            .getRealPath("users" + File.separator + username));
                    userDir.mkdir();
                    
                    File configDir = new File(getServletContext()
                            .getRealPath("users" + File.separator + username + File.separator + "_config_files"));
                    configDir.mkdir();

                    String[] dirs = {"datasets", "intros", "studies", "viewers", "tests", "tasks", "taskInstances", "taskInstanceFiles"};
                    for (int i=0; i<dirs.length; i++){
                        File dir = new File(getServletContext()
                                .getRealPath("users" + File.separator + username + File.separator + "_config_files" + File.separator + dirs[i]));
                        dir.mkdir();
                    }
                    
                    
                    //save the password
                    File passFile = new File(getServletContext()
                            .getRealPath("users" + File.separator + username + 
                            File.separator + "_config_files" + File.separator + username + ".password"));

                    BufferedWriter bw1 = new BufferedWriter(new FileWriter(passFile));

                    PrintWriter pw1 = new PrintWriter(bw1);
                    pw1.println(password);
                    pw1.close();
                    bw1.close();

                    out.print("true");
                }
                
            
            } else if (command.equalsIgnoreCase("getUserId")) {
                String username = "";
                if (session.getAttribute("username") != null) {
                    username = session.getAttribute("username").toString();
                }

                if(!username.isEmpty())
                    out.print(username);
                else{
                   out.print("");
                }
            } else if (command.equalsIgnoreCase("login")) {

                //check the login detils
                String username = request.getParameter("username").toLowerCase();
                String password = request.getParameter("password");
                
                System.out.println("Attempting log in: " + username + "," + password);

                String filename = getServletContext()
                        .getRealPath("users" + File.separator + username + 
                                File.separator + "_config_files" + File.separator + username + ".password");
                
                try{
                BufferedReader br = new BufferedReader(new FileReader(filename));

                String truePassword = br.readLine().trim();
                System.out.println(password + " === " + truePassword);
		if (username.toLowerCase().equals("demo") || password.equals(truePassword)){
                    System.out.println("login success: setting session username to " + username);
                        session.setAttribute("username", username); 
                        out.print("true");
                         
                }
                else
                       response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                catch(Exception e){
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            out.close();
        }
    }

    public boolean checkUserNameAvailability(String username) {
        boolean exist = false;
        
        File f = new File(getServletContext().getRealPath("users"));

        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
             if (files[i].isDirectory() && files[i].getName().trim().equalsIgnoreCase(username)){
                exist = true;
                break;
            }
        }
        return exist;
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
