package synchro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Synchro {

	private ArrayList<Cacheobj> remote_cache = new ArrayList<Cacheobj>();
	private ArrayList<Cacheobj> local_cache = new ArrayList<Cacheobj>();
	private ArrayList<Cacheobj> list_diff = new ArrayList<Cacheobj>();

	private String remote_folder = "";
	public String remote_path;

	private String local_folder = "";
	private String local_path = "/home/arnaud/Documents/project";

	public static User user;



	public Synchro(String username, String password, String hostname) {

		user = new User(username, password, hostname);
		remote_path = "/remote.php/dav/files/"+user.username+remote_folder;
	}

	private void load_remote_cache() {
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
				if (co.isdir) {

					user.sardine.createDirectory("https://" + user.domain + remote_path + co.path);
				} else {
					InputStream fis = new FileInputStream(new File("/home/arnaud/Documents/project" + co.path));
					user.sardine.put("https://" + user.domain + remote_path + co.path.replaceAll(" ", "%20"), fis);
				}
				remote_cache.add(co);
				save_remote_cache();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
			files = user.sardine.list("https://" + user.domain + remote_path, -1);
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
			files = user.sardine.list("https://" + user.domain + remote_path, 1);
		} catch (IOException e) {
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
		// TODO Auto-generated method stub

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

	public void run(String[] args) {

		//setup_webdav();
		if (!new File("/tmp/test.txt").isFile()) {
			create_remote_cache();
		}
		load_remote_cache();
		while (true) {
			create_local_cache();
			compare();
			upload();
			wait(1000);
		}
	}

	public boolean test_sync() {

		try {
			System.out.println("https://" + user.domain + remote_path);
			if(user.sardine.list("https://" + user.domain + remote_path, 0) != null) {
				return true;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}