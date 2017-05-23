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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
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
            if (command.equals("getResults")){
                this.generateBasicResults(userid, studyname);
            }
            else if (command.equalsIgnoreCase("getResultUrls")) {
                System.out.println("StudyResults, " + command + ", " + userid + "," + studyname );
                //get the studyname and userid and saved them in sessions to be accessed later.

                session.setAttribute("studyname", studyname);
                session.setAttribute("userid", userid);

                this.generateBasicResults(userid, studyname);
                this.generateAccuracyStats(userid, studyname); //not implemented yet!
                this.generateTimeStats(userid,studyname); // not implemented yet!
                
                Gson gson = new Gson();
                ResultsUrls resultUrls = new ResultsUrls();

                resultUrls.setBasicResultsUrl(
                        getServerUrl(request) + "/users/" + userid + "/_config_files/studies/"
                        + studyname + "/basicResultsData.json");

                resultUrls.setAccuracyStatsUrl(
                        getServerUrl(request) + "/users/" + userid + "/_config_files/studies/"
                        + studyname + "/accuracy_stats.json"
                );

                resultUrls.setTimeStatsUrl(
                        getServerUrl(request) + "/users/" + userid + "/_config_files/studies/"
                        + studyname + "/time_stats.json"
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
    
    

    
    //puts the individual participant data in the basic results format
    private void generateBasicResults(String experimenterId, String study){
        System.out.println("generated results: " + experimenterId + " " + study);
        try{
            BasicResults basicResults = new BasicResults();

            String studyFolder = "users" + File.separator + experimenterId + File.separator
                    + "_config_files" + File.separator + "studies" + File.separator + study;

            System.out.println("studydir: " + studyFolder); 
            //load all final results
            File[] files = new File(getServletContext().getRealPath(studyFolder)).listFiles();
            for (int i=0; i<files.length; i++){
                if (!files[i].isDirectory() && files[i].getName().startsWith("final")){
                    
                    Results results = new Gson().fromJson(new BufferedReader(
                            new FileReader(new File(files[i].getAbsolutePath()))), 
                            Results.class);

                    //the key captures: task+viewer+dataset; the arraylist are the instances
                    Hashtable<String, ArrayList<Response>> table = new Hashtable<String, ArrayList<Response>>();

                    for (int j=0; j<results.getResults().length; j++){
                        Result r = results.getResults()[j]; //one user's one response
                        String type = r.getType();
                        String viewer = r.getViewer() != null ? r.getViewer() : " ";
                        String dataset = r.getDataset() != null ? r.getDataset() : " ";
                        String group = r.getGroup() != null ? r.getGroup() : " ";
                        String task = r.getTask() != null ? r.getTask().getName() : " ";
                        String answerType = r.getTask() != null ? r.getTask().getAnswer().getType() : " ";
                        String key = type + ";;" + group + ";;" + viewer + ";;" +  dataset + ";;" + task+   ";;" + answerType;
                        ArrayList<Response> responses = table.get(key);
                        if (responses == null){
                            responses = new ArrayList<Response>();
                            table.put(key, responses);
                        }                        
                        responses.add(new Response(r.getResponse(), r.getAccuracy(), r.getTime()));
                    }

                    Enumeration<String> keys = table.keys();
                    while (keys.hasMoreElements()){
                        String key = keys.nextElement();
                        ArrayList<Response> responses = table.get(key);
                        Response[] responses2 = new Response[responses.size()];
                        for (int j=0; j<responses.size(); j++)
                            responses2[j] = responses.get(j);
                        System.out.println("key: " + key);
                        String[] split = key.split(";;"); 
                        System.out.println("splitle " + split.length);
                        System.out.println("keysplit: " + split[0] + " ---- " + split[1] + " ---- " + split[2] + " ---- " + split[3]);
                        BasicResult br = basicResults.getBasicResult(split[1], split[2], split[3], split[4]);
                        System.out.println("group is: " + split[1].substring(0,3));
                        
                        if (br == null){
                            basicResults.addBasicResult(split[1], split[2], split[3], split[4], split[0], split[5]);
                            br = basicResults.getBasicResult(split[1], split[2], split[3], split[4]);
                        }    
                        System.out.println("add is: " + br.getGroup());
                        br.addUserResponses(responses2);
                    }
                }
            }

            //done; let's save it
            String resultsJson = new Gson().toJson(basicResults);
            String resultsFilename = studyFolder + File.separator + "basicResultsData.json";               

            FileWriter writer = new FileWriter(getServletContext().getRealPath(resultsFilename));
            writer.write(resultsJson);
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
               
 
    }

    private void generateAccuracyStats(String userid, String studyname) throws Exception{
        
        String studyFolder = "users" + File.separator + userid + File.separator
                    + "_config_files" + File.separator + "studies" + File.separator + studyname;
        
        String filename = studyFolder + File.separator + "accuracy_stats.json";               

        FileWriter writer = new FileWriter(getServletContext().getRealPath(filename));
        writer.write("[]");
        writer.close();
        
        
    }

    private void generateTimeStats(String userid, String studyname) throws Exception {
        String studyFolder = "users" + File.separator + userid + File.separator
                    + "_config_files" + File.separator + "studies" + File.separator + studyname;
        
        String filename = studyFolder + File.separator + "time_stats.json";               

        FileWriter writer = new FileWriter(getServletContext().getRealPath(filename));
        writer.write("[]");
        writer.close();        
        
    }

}
