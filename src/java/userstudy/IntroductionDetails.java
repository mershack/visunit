package userstudy;

/**
 *
 * @author Mershack
 */
public class IntroductionDetails {    

    private String name;
    private String match;
           
  
    public IntroductionDetails(String name, String match) {
        this.name = name;       
        this.match = match;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }
}
