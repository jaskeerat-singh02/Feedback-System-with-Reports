package feedbacksystem;

import java.util.*;

public class ReportGenerator {
    private FeedbackManager fm;

    public ReportGenerator(FeedbackManager fm) { this.fm = fm; }

    public void facultyReport(String facultyName) {
        List<Feedback> all = fm.readAll();
        int count = 0; double sum=0;
        List<String> comments = new ArrayList<>();
        Map<String,Integer> courseCount = new HashMap<>();
        for (Feedback f: all) {
            if (f.facultyName.equalsIgnoreCase(facultyName)) {
                count++; sum += f.rating;
                if (f.comments!=null && !f.comments.isEmpty()) comments.add(f.comments);
                courseCount.put(f.courseName, courseCount.getOrDefault(f.courseName,0)+1);
            }
        }
        if (count==0) { System.out.println("No feedback for faculty: " + facultyName); return; }
        System.out.printf("\nFaculty: %s | Responses: %d | Avg Rating: %.2f\n", facultyName, count, sum/count);
        System.out.println("Courses taught and feedback count: " + courseCount);
        System.out.println("Comments:");
        for (String c: comments) System.out.println(" - " + c);
    }

    public void adminReport() {
        List<Feedback> all = fm.readAll();
        if (all.isEmpty()) { System.out.println("No feedback data."); return; }
        Map<String, double[]> fac = new HashMap<>();
        Map<String, Integer> courseFreq = new HashMap<>();
        List<String> allComments = new ArrayList<>();
        for (Feedback f: all) {
            fac.putIfAbsent(f.facultyName, new double[2]);
            double[] arr = fac.get(f.facultyName);
            arr[0] += f.rating; arr[1] += 1;
            courseFreq.put(f.courseName, courseFreq.getOrDefault(f.courseName,0)+1);
            if (f.comments!=null && !f.comments.isEmpty()) allComments.add(f.comments.toLowerCase());
        }
        System.out.println("\n=== Admin Summary ===");
        Map<String, Double> avgPerFaculty = new HashMap<>();
        for (String facName: fac.keySet()) {
            double[] v = fac.get(facName);
            double avg = v[0]/v[1];
            avgPerFaculty.put(facName, avg);
            System.out.printf("Faculty: %s | Avg: %.2f | Responses: %.0f\n", facName, avg, v[1]);
        }
        String topFac = Collections.max(avgPerFaculty.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Top-performing faculty: " + topFac + " (Avg: " + String.format("%.2f", avgPerFaculty.get(topFac)) + ")");
        String topCourse = Collections.max(courseFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Most-reviewed course: " + topCourse + " (Reviews: " + courseFreq.get(topCourse) + ")");
        Map<String,Integer> keywordCount = new HashMap<>();
        String[] keywords = new String[]{"unclear","fast","slow","boring","difficult","not","hard","confusing"};
        for (String c: allComments) {
            for (String k: keywords) if (c.contains(k)) keywordCount.put(k, keywordCount.getOrDefault(k,0)+1);
        }
        System.out.println("\nRecurring issues (keyword counts):");
        for (String k: keywordCount.keySet()) System.out.println(k + ": " + keywordCount.get(k));
        System.out.println("\nFaculty needing improvement (avg < 3):");
        for (String facName: avgPerFaculty.keySet()) {
            if (avgPerFaculty.get(facName) < 3.0) System.out.println(" - " + facName + " (Avg: " + String.format("%.2f", avgPerFaculty.get(facName)) + ")");
        }
    }
}
