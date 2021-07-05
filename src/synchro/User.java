package synchro;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpHeaders;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.ImagePattern;

public class User {
	
	public Sardine sardine;
	
	public String username;
	public String pass;
	public String domain;
	public BufferedImage avatar;
	public long quota_free;
    public long quota_used;
    public long quota_total;
    public float quota_relative;
	
	public User(String username, String password, String hostname) {
		
		this.username = username;
		this.pass = password;
		this.domain = hostname;
		
		this.sardine = SardineFactory.begin(this.username, this.pass);
		this.sardine.enablePreemptiveAuthentication(this.domain);
		
	}
	
	public void retreive_info(){
		retreive_avatar();
		retreive_quota();
	}

	private void retreive_quota() {
		
		try {
		    var client = HttpClient.newBuilder()
		            .version(HttpClient.Version.HTTP_1_1)
		            .build();
			HttpRequest request = HttpRequest.newBuilder()
		            .GET()
		            .header("Authorization", basicAuth(this.username, this.pass))
		            .headers("OCS-APIRequest", "true")
		            .uri(new URI("https://"+this.domain+"/ocs/v1.php/cloud/users/"+this.username))
		            .build();
			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(response.body());
			Element rootElement = doc.getDocumentElement();
			quota_free = Long.parseLong(getString("free", rootElement));
			quota_used = Long.parseLong(getString("used", rootElement));
			quota_total = Long.parseLong(getString("total", rootElement));
			quota_relative = Float.parseFloat(getString("relative", rootElement));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
	private static String basicAuth(String username, String password) {
	    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}
	private void retreive_avatar() {
		
		try {
			URL url = new URL("https://"+domain+"/avatar/"+username+"/32?v=0");
			InputStream in = new BufferedInputStream(url.openStream());
	    	avatar = ImageIO.read(in);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
