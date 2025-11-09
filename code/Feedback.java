package feedbacksystem;

public class Feedback {
    public String studentId;
    public String facultyName;
    public String courseName;
    public int rating;
    public String mcq;
    public String comments;
    public String date;

    public Feedback() {}

    public Feedback(String studentId, String facultyName, String courseName, int rating, String mcq, String comments, String date) {
        this.studentId = studentId;
        this.facultyName = facultyName;
        this.courseName = courseName;
        this.rating = rating;
        this.mcq = mcq;
        this.comments = comments;
        this.date = date;
    }
}
