package feedbacksystem;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FeedbackManager {
    private static final String FILE = "feedbacks.xml";

    public FeedbackManager() {
        try { ensureFile(); } catch (Exception e) { e.printStackTrace(); }
    }

    private void ensureFile() throws Exception {
        File f = new File(FILE);
        if (!f.exists()) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement("feedbacks");
            doc.appendChild(root);
            saveDoc(doc);
        }
    }

    private void saveDoc(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(FILE)));
    }

    public synchronized void saveFeedback(Feedback fb) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File f = new File(FILE);
            Document doc = db.parse(f);
            Element root = doc.getDocumentElement();

            Element e = doc.createElement("feedback");
            e.appendChild(create(doc, "student", fb.studentId));
            e.appendChild(create(doc, "faculty", fb.facultyName));
            e.appendChild(create(doc, "course", fb.courseName));
            e.appendChild(create(doc, "rating", String.valueOf(fb.rating)));
            e.appendChild(create(doc, "mcq", fb.mcq));
            e.appendChild(create(doc, "comments", fb.comments));
            e.appendChild(create(doc, "date", fb.date));

            root.appendChild(e);
            saveDoc(doc);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private Element create(Document doc, String tag, String val) {
        Element el = doc.createElement(tag);
        el.setTextContent(val==null?"":val);
        return el;
    }

    public List<Feedback> readAll() {
        List<Feedback> list = new ArrayList<>();
        try {
            File f = new File(FILE);
            if (!f.exists()) return list;
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(f);
            NodeList nodes = doc.getElementsByTagName("feedback");
            for (int i=0;i<nodes.getLength();i++) {
                Element e = (Element) nodes.item(i);
                Feedback fb = new Feedback();
                fb.studentId = getText(e, "student");
                fb.facultyName = getText(e, "faculty");
                fb.courseName = getText(e, "course");
                fb.rating = Integer.parseInt(getText(e, "rating").isEmpty()?"0":getText(e, "rating"));
                fb.mcq = getText(e, "mcq");
                fb.comments = getText(e, "comments");
                fb.date = getText(e, "date");
                list.add(fb);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private String getText(Element e, String tag) {
        NodeList nl = e.getElementsByTagName(tag);
        if (nl.getLength()==0) return "";
        return nl.item(0).getTextContent();
    }

    public void clearAll() {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement("feedbacks"); doc.appendChild(root);
            saveDoc(doc);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
