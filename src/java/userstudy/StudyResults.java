package userstudy;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
public class StudyResults extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    HashMap<String, ResultParameters> resultParameters = new HashMap<String, ResultParameters>();

    boolean windows = true;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String msg = "";
            HttpSession session = request.getSession();

            String studyname = request.getParameter("studyName");
            String userid = session.getAttribute("userid").toString();
            String command = request.getParameter("command").toString();
            if (command.equalsIgnoreCase("getResultUrls")) {
                //get the studyname and userid and saved them in sessions to be accessed later.

                session.setAttribute("studyname", studyname);
                session.setAttribute("userid", userid);

                Gson gson = new Gson();
                ResultsUrls resultUrls = new ResultsUrls();

                resultUrls.setBasicResultsUrl(
                        getServerUrl(request) + "/users/" + userid + "/_config_files/studies/"
                        + studyname + "/data/basicResultsData.json");

                resultUrls.setAccuracyStatsUrl(
                        getServerUrl(request) + "/users/" + userid + "/_config_files/studies/"
                        + studyname + "/data/accuracy_stats.json"
                );

                resultUrls.setTimeStatsUrl(
                        getServerUrl(request) + "/users/" + userid + "/_config_files/studies/"
                        + studyname + "/data/time_stats.json"
                );

                String json = gson.toJson(resultUrls);

                //System.out.println(json);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out2 = response.getWriter();
                out2.print(json);//return an empty object. NB: this is because the ajax expects to receive a json object (i.e. dataType).

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
