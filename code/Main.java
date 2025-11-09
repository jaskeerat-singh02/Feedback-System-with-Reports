package feedbacksystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static UserManager um = new UserManager();
    static FeedbackManager fm = new FeedbackManager();
    static ReportGenerator rg = new ReportGenerator(fm);

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("\n--- Feedback System ---");
            System.out.println("1) Sign Up");
            System.out.println("2) Login");
            System.out.println("3) View Faculty Usernames");
            System.out.println("4) View Top Performing Faculty / Analytics");
            System.out.println("5) Clear All Feedback Data (Admin only)");
            System.out.println("6) Exit");
            System.out.print("Choice: "); int ch = Integer.parseInt(sc.nextLine());
            switch (ch) {
                case 1 -> signUp();
                case 2 -> login();
                case 3 -> listFaculty();
                case 4 -> rg.adminReport();
                case 5 -> clearDataOption();
                case 6 -> { System.out.println("Goodbye."); return; }
                default -> System.out.println("Invalid choice."); 
            }
        }
    }

    static void signUp() throws Exception {
        System.out.print("Choose account type (student/faculty/admin): "); String role = sc.nextLine().toLowerCase();
        if (!role.equals("student") && !role.equals("faculty") && !role.equals("admin")) { System.out.println("Invalid role."); return; }
        System.out.print("Enter username: "); String username = sc.nextLine();
        System.out.print("Enter display name: "); String display = sc.nextLine();
        System.out.print("Enter password: "); String pass = sc.nextLine();
        um.setPassword(username, pass, role, display);
        System.out.println("Account created/updated for " + username);
    }

    static void login() throws Exception {
        System.out.print("Username: "); String user = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        User u = um.authenticate(user, pass);
        if (u==null) { System.out.println("Invalid credentials."); return; }
        switch (u.role) {
            case "student" -> studentFlow(u);
            case "faculty" -> facultyFlow(u);
            case "admin" -> adminFlow(u);
            default -> System.out.println("Unknown role."); 
        }
    }

    static void listFaculty() throws Exception {
        List<User> users = um.getAllUsers();
        System.out.println("\nFaculty usernames:");
        for (User u: users) if ("faculty".equals(u.role)) System.out.println(" - " + u.username + " (" + u.displayName + ")");
    }

    static void studentFlow(User u) {
        try {
            System.out.println("\n-- Student: " + u.displayName + " --");
            System.out.print("Faculty name: "); String faculty = sc.nextLine();
            System.out.print("Course name: "); String course = sc.nextLine();
            int rating = getInt("Overall rating (1-5): ",1,5);
            System.out.print("MCQ (Was class clear? Yes/No): "); String mcq = sc.nextLine();
            System.out.print("Comments: "); String comments = sc.nextLine();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Feedback fb = new Feedback(u.username, faculty, course, rating, mcq, comments, date);
            fm.saveFeedback(fb);
            System.out.println("Submission successful. Thank you!"); 
        } catch (Exception e) { e.printStackTrace(); }
    }

    static void facultyFlow(User u) {
        System.out.println("\n-- Faculty: " + u.displayName + " --");
        rg.facultyReport(u.displayName);
    }

    static void adminFlow(User u) throws Exception {
        while (true) {
            System.out.println("\n-- Admin Menu --");
            System.out.println("1) View All Users");
            System.out.println("2) Set/Add User Password");
            System.out.println("3) Generate Admin Report");
            System.out.println("4) Clear All Feedback Data");
            System.out.println("5) Logout");
            System.out.print("Choice: "); int c = Integer.parseInt(sc.nextLine());
            if (c==5) break;
            switch (c) {
                case 1 -> viewUsers();
                case 2 -> setPassword();
                case 3 -> rg.adminReport();
                case 4 -> { fm.clearAll(); System.out.println("All feedback cleared."); }
                default -> System.out.println("Invalid choice."); 
            }
        }
    }

    static void viewUsers() throws Exception {
        List<User> users = um.getAllUsers();
        System.out.println("\nUsers:" );
        for (User u: users) System.out.println(u.username + " | " + u.role + " | " + u.displayName);
    }

    static void setPassword() throws Exception {
        System.out.print("Enter username to set/add: "); String user = sc.nextLine();
        System.out.print("Enter new password: "); String pass = sc.nextLine();
        System.out.print("Enter role for this user: "); String role = sc.nextLine();
        System.out.print("Enter display name: "); String display = sc.nextLine();
        um.setPassword(user, pass, role, display);
        System.out.println("Password set/updated for " + user);
    }

    static void clearDataOption() throws Exception {
        System.out.print("Admin username: "); String user = sc.nextLine();
        System.out.print("Admin password: "); String pass = sc.nextLine();
        User u = um.authenticate(user, pass);
        if (u==null || !"admin".equals(u.role)) { System.out.println("Admin authentication failed."); return; }
        System.out.print("Are you sure you want to clear all feedback data? (y/n): "); String ans = sc.nextLine();
        if (ans.equalsIgnoreCase("y")) { fm.clearAll(); System.out.println("All feedback cleared."); }
        else System.out.println("Cancelled."); 
    }

    static int getInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int v = Integer.parseInt(sc.nextLine());
                if (v>=min && v<=max) return v;
            } catch (Exception e) {}
            System.out.println("Please enter a valid number."); 
        }
    }
}
