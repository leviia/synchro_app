package synchro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import controller.Synchronize;
import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Synchro {

	private ArrayList<Cacheobj> remote_cache = new ArrayList<Cacheobj>();
	private ArrayList<Cacheobj> local_cache = new ArrayList<Cacheobj>();
	private ArrayList<Cacheobj> list_diff = new ArrayList<Cacheobj>();

	private String remote_folder = "/test";
	public String remote_path;

	private String local_folder = "";
	private String local_path = "/home/arnaud/Documents/project";
	public boolean halt = true;
	public Synchronize sync_controller;

	public static User user;
	
	Queue<Cacheobj> fifo = new CircularFifoQueue<Cacheobj>(4);
	


	public Synchro(String username, String password, String hostname) {

		user = new User(username, password, hostname);
		remote_path = "/remote.php/dav/files/"+user.username;
	}

	private void load_remote_cache() {
		System.out.println("load_remote_cache");
		FileInputStream fis;
		try {
			fis = new FileInputStream("/tmp/test.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			remote_cache = (ArrayList<Cacheobj>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void upload() {
		System.out.println("upload");

		for (Cacheobj co : list_diff) {
			System.out.println(co.path);
			try {
				URL url1= new URL("https://" + user.domain + remote_path + remote_folder+ co.path);
				URI uri = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost(), url1.getPort(), url1.getPath(), url1.getQuery(), url1.getRef());
				if (co.isdir) {
					System.out.println("dir" + uri.toASCIIString());
					user.sardine.createDirectory(uri.toASCIIString());
					fifo.add(co);
					update_file_scroll();
				} else {
					InputStream fis = new FileInputStream(new File("/home/arnaud/Documents/project" + co.path));
					System.out.println(uri.toASCIIString());
					user.sardine.put(uri.toASCIIString(), fis);
					fifo.add(co);
					System.out.println("done2");
					update_file_scroll();
					System.out.println("done3");
				}
				remote_cache.add(co);
				save_remote_cache();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void wait(int s) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void create_local_cache() {
		// TODO Auto-generated method stub

		local_cache.clear();

		List<Path> files = null;
		try {

			files = Files.find(Paths.get("/home/arnaud/Documents/project"), 999,
					(p, bfa) -> (bfa.isDirectory() || bfa.isRegularFile())).collect(Collectors.toList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Path res : files) {
			Cacheobj aux = null;
			try {
				aux = new Cacheobj(Normalizer.normalize(res.toAbsolutePath().toString().replace(local_path, ""),
						Normalizer.Form.NFD), Files.size(res), Files.isDirectory(res));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			local_cache.add(aux);
		}

	}

	private void compare() {
		System.out.println("compare");
		list_diff.clear();
		for (Cacheobj lco : local_cache) {
			if (!remote_cache.stream().anyMatch(o -> o.path.equals(lco.path))) {
				list_diff.add(lco);
			}
			if ((!remote_cache.stream().anyMatch(o -> o.size == lco.size) && (!lco.isdir))) {
				list_diff.add(lco);
			}

		}
	}

	private void create_remote_cache() {
		System.out.println("create remote cache");

		List<DavResource> files = null;
		try {
			files = user.sardine.list("https://" + user.domain + remote_path + remote_folder, -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (DavResource res : files) {
			Cacheobj aux = new Cacheobj(Normalizer
					.normalize(res.toString().replace(remote_path, "").replaceAll("/$", ""), Normalizer.Form.NFD),
					res.getContentLength(), res.isDirectory());
			remote_cache.add(aux);
		}

		save_remote_cache();

	}
	
	public List<DavResource> listFolders(String url) {
		
		List<DavResource> files = null;
		List<DavResource> folders = new ArrayList<>();
		try {
			URL url1= new URL("https://" + user.domain + remote_path+url);
			URI uri = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost(), url1.getPort(), url1.getPath(), url1.getQuery(), url1.getRef());
			files = user.sardine.list(uri.toString(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (DavResource res : files) {
			if(res.isDirectory()) {
				folders.add(res);
			}
		}
		return folders;
		
	}

	private void save_remote_cache() {

		FileOutputStream fos;
		try {
			fos = new FileOutputStream("/tmp/test.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(remote_cache);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		
		new Thread(new Runnable() {
		     @Override
		     public void run() {
		          
		    	 if (!new File("/tmp/test.txt").isFile()) {
		 			create_remote_cache();
		 		}
		 		load_remote_cache();
		 		while (!halt ) {
		 			create_local_cache();
		 			compare();
		 			upload();
		 			try {
						wait(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 		}
		    	 
		    	 
		     }
		}).start();

		
	}

	public boolean test_sync() {

		try {
			if(user.sardine.list("https://" + user.domain + remote_path, 0) != null) {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void update_file_scroll() {
		// TODO Auto-generated method stub
		Platform.runLater(
				  () -> {
					  List<Cacheobj> fifoList = new ArrayList<>( fifo );
						
						sync_controller.file_scroll.getChildren().clear();
						
						for (Cacheobj co : fifoList) {
							co.fileLoadBar.controller.file_name.setText(co.path);
							sync_controller.file_scroll.getChildren().add(co.fileLoadBar.node);
							
						}
				  }
				);
		
		
		
	}

}