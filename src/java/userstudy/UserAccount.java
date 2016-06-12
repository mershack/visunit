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
                //get the first name
                //get the last name
                //save this to an xml file   

                String username = request.getParameter("username");
                String firstname = request.getParameter("firstname");
                String lastname = request.getParameter("lastname");
                String password = request.getParameter("password");

                if (checkUserNameAvailability(username)) {
                    out.print("false");
                } else {
                    File userAccountfile = new File(getServletContext().getRealPath("user-accounts" + File.separator + username + ".xml"));

                    BufferedWriter bw1 = new BufferedWriter(new FileWriter(userAccountfile));

                    PrintWriter pw1 = new PrintWriter(bw1);

                    pw1.println("<?xml version=\"1.0\"?>");   //first line of the xml
                    pw1.println("\t<user_account>");

                    pw1.println("\t\t<username>" + username + "</username>");
                    pw1.println("\t\t<password>" + password + "</password>");
                    pw1.println("\t\t<firstname>" + firstname + "</firstname>");
                    pw1.println("\t\t<lastname>" + lastname + "</lastname>");

                    pw1.println("\t</user_account>");

                    pw1.close();
                    bw1.close();

                    out.print("true");
                }
                session.setAttribute("username", username);
                
                //prepare user directories
               File userDir = new File(getServletContext()
                       .getRealPath("users" + File.separator + username));
               userDir.mkdir();
               
               //make the other dirs
               File dsDir = new File(getServletContext()
                       .getRealPath("users" + File.separator + username  +File.separator+ "datasets"));
               dsDir.mkdir();
               File vDir = new File(getServletContext()
                       .getRealPath("users" + File.separator + username  +File.separator+ "viewers"));
               vDir.mkdir();
               
                File sDir = new File(getServletContext()
                       .getRealPath("users" + File.separator + username  +File.separator+ "studies"));
               sDir.mkdir();
               
                File qtDir = new File(getServletContext()
                       .getRealPath("users" + File.separator + username  +File.separator+ "quanttasks"));
               qtDir.mkdir();
               File inDir = new File(getServletContext()
                       .getRealPath("users" + File.separator + username  +File.separator+ "taskInstances"));
               inDir.mkdir();
            
            } else if (command.equalsIgnoreCase("getUserId")) {
                String username = "";
                if (session.getAttribute("username") != null) {
                    username = session.getAttribute("username").toString();
                }

                if(!username.isEmpty())
                out.print(username);
                else{
                   response.sendRedirect("login.html");
                }
            } else if (command.equalsIgnoreCase("login")) {

                //check the login detils
                String username = request.getParameter("username");
                String password = request.getParameter("password");

               // System.out.println("givenUsername: " + username);

                String filename = getServletContext().getRealPath("user-accounts" + File.separator + username + ".xml");
                File fXmlFile = new File(filename);

                String accountExists = "false";

                if (fXmlFile.exists()) {

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(fXmlFile);

                    //optional, but recommended
                    //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                    doc.getDocumentElement().normalize();

                    //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                    NodeList userNameNode = doc.getElementsByTagName("username");

                    NodeList passwordNode = doc.getElementsByTagName("password");

                    String userNameInFile = userNameNode.item(0).getTextContent();
                    String passwordInFile = passwordNode.item(0).getTextContent();

                    //System.out.println("username ON file: "+userNameInFile);
                    //System.out.println("password on file: "+ passwordInFile);
                    //compare the two now.
                    username = username.trim();
                    password = password.trim();
                    userNameInFile = userNameInFile.trim();
                    passwordInFile = passwordInFile.trim();

                    if (username.equalsIgnoreCase(userNameInFile)
                            && password.equalsIgnoreCase(passwordInFile)) {

                        accountExists = "true";
                        session.setAttribute("username", username);
                    }

                }

                out.print(accountExists);

            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            out.close();
        }
    }

    public boolean checkUserNameAvailability(String username) {
        boolean exist = false;

        String userAccounts = "user-accounts";

        username = username.trim() + ".xml";

        File f = new File(getServletContext().getRealPath(userAccounts));

        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            //System.out.println(files[i].getName());

            if (username.equalsIgnoreCase(files[i].getName().trim())) {
                exist = true;
                break;
            }

        }

        //System.out.println(exist);

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
