package userstudy;

/**
 *
 * @author Mershack
 */
public class TaskDetails {    

    private String name;
    private String count;
    private String time;
    private String training;       
  
    public TaskDetails(String name, String count, String time, String trainingSize) {
        this.name = name;       
        this.count = count;
        this.time = time;
        this.training = trainingSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTrainingSize() {
        return Integer.parseInt(training);
    }

    public void setTrainingSize(int trainingSize) {
        this.training = "" + trainingSize;
    }
    
}
