package feedbacksystem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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

public class UserManager {
    private static final String USER_FILE = "users.xml";

    public UserManager() {
        try { ensureUserFile(); } catch (Exception e) { e.printStackTrace(); }
    }

    private void ensureUserFile() throws Exception {
        File f = new File(USER_FILE);
        if (!f.exists()) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement("users");
            doc.appendChild(root);
            root.appendChild(createUserElement(doc, "admin", "admin", "admin", "Administrator"));
            root.appendChild(createUserElement(doc, "f_mehta", "password", "faculty", "Mehta"));
            root.appendChild(createUserElement(doc, "23bcs10601", "password", "student", "StudentA"));
            saveDoc(doc);
        }
    }

    private Element createUserElement(Document doc, String u, String p, String role, String display) {
        Element el = doc.createElement("user");
        Element un = doc.createElement("username"); un.setTextContent(u); el.appendChild(un);
        Element pw = doc.createElement("password"); pw.setTextContent(p); el.appendChild(pw);
        Element r = doc.createElement("role"); r.setTextContent(role); el.appendChild(r);
        Element d = doc.createElement("display"); d.setTextContent(display); el.appendChild(d);
        return el;
    }

    private void saveDoc(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(USER_FILE)));
    }

    public List<User> getAllUsers() throws Exception {
        List<User> list = new ArrayList<>();
        File f = new File(USER_FILE);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(f);
        NodeList nodes = doc.getElementsByTagName("user");
        for (int i=0;i<nodes.getLength();i++) {
            Element e = (Element) nodes.item(i);
            User u = new User();
            u.username = e.getElementsByTagName("username").item(0).getTextContent();
            u.password = e.getElementsByTagName("password").item(0).getTextContent();
            u.role = e.getElementsByTagName("role").item(0).getTextContent();
            u.displayName = e.getElementsByTagName("display").item(0).getTextContent();
            list.add(u);
        }
        return list;
    }

    public User authenticate(String username, String password) throws Exception {
        for (User u: getAllUsers()) {
            if (u.username.equals(username) && u.password.equals(password)) return u;
        }
        return null;
    }

    public void setPassword(String username, String newPassword, String role, String displayName) throws Exception {
        File f = new File(USER_FILE);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(f);
        NodeList nodes = doc.getElementsByTagName("user");
        for (int i=0;i<nodes.getLength();i++) {
            Element e = (Element) nodes.item(i);
            String un = e.getElementsByTagName("username").item(0).getTextContent();
            if (un.equals(username)) {
                e.getElementsByTagName("password").item(0).setTextContent(newPassword);
                saveDoc(doc);
                return;
            }
        }
        Element root = doc.getDocumentElement();
        root.appendChild(createUserElement(doc, username, newPassword, role, displayName));
        saveDoc(doc);
    }
}
