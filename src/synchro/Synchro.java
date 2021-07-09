package synchro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.github.sardine.DavResource;

import controller.Synchronize;
import javafx.application.Platform;

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

	private Queue<Cacheobj> fifo = new CircularFifoQueue<Cacheobj>(4);
	public Queue<Float> upload_fifo = new CircularFifoQueue<Float>(30);
	public Queue<Long> download_fifo = new CircularFifoQueue<Long>(30);

	public Synchro(String username, String password, String hostname) {

		user = new User(username, password, hostname);
		remote_path = "/remote.php/dav/files/" + user.username;
	}

	private void create_remote_cache() throws IOException {
		System.out.println("create remote cache");

		List<DavResource> files = null;
		files = user.sardine.list("https://" + user.domain + remote_path + remote_folder, -1);

		for (DavResource res : files) {
			String path = Normalizer.normalize(res.toString().replace(remote_path + remote_folder, "").replaceAll("/$", ""),Normalizer.Form.NFD);
			Cacheobj aux = new Cacheobj(path,res.getContentLength(), res.isDirectory());
			remote_cache.add(aux);
		}

		save_remote_cache();

	}
	
	private void load_remote_cache() throws IOException, ClassNotFoundException {
		System.out.println("load_remote_cache");
		remote_cache.clear();
		FileInputStream fis = new FileInputStream("/tmp/test.txt");
		ObjectInputStream ois = new ObjectInputStream(fis);
		remote_cache = (ArrayList<Cacheobj>) ois.readObject();
		ois.close();

	}

	private void upload() throws IOException, URISyntaxException {
		System.out.println("upload");

		for (Cacheobj co : list_diff) {
			//System.out.println(co.path);
			URL url1 = new URL("https://" + user.domain + remote_path + remote_folder + co.path);
			URI uri = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost(), url1.getPort(),
					url1.getPath(), url1.getQuery(), url1.getRef());
			if (co.isdir && (co.path != null)) {
				//System.out.println(co.path);
				//System.out.println("dir" + uri.toASCIIString());
				fifo.add(co);
				update_file_scroll();
				user.sardine.createDirectory(uri.toASCIIString());
				Platform.runLater(() -> {

					co.fileLoadBar.controller.finishedProgress();

					});
				
				
			} else {
				InputStream fis = new FileInputStream(new File("/home/arnaud/Documents/project" + co.path));
				//System.out.println(uri.toASCIIString());
				fifo.add(co);
				update_file_scroll();
				long startTime = System.nanoTime();
				user.sardine.put(uri.toASCIIString(), fis);
				float time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
				upload_fifo.add(co.size/time);
				Platform.runLater(() -> {

					co.fileLoadBar.controller.finishedProgress();

					});
				
			}
			remote_cache.add(co);
			save_remote_cache();

		}
	}

	private void create_local_cache() throws IOException {

		local_cache.clear();

		List<Path> files = Files.find(Paths.get("/home/arnaud/Documents/project"), 999,
				(p, bfa) -> (bfa.isDirectory() || bfa.isRegularFile())).collect(Collectors.toList());


		for (Path res : files) {
			Cacheobj aux = new Cacheobj(Normalizer.normalize(res.toAbsolutePath().toString().replace(local_path, ""),
					Normalizer.Form.NFD), Files.size(res), Files.isDirectory(res));
		
			if (aux.path != null && !aux.path.isEmpty()) {
				local_cache.add(aux);
			}

		}

	}

	private void compare() {
		System.out.println("compare");
		list_diff.clear();
		for (Cacheobj lco : local_cache) {
			if (!remote_cache.stream().anyMatch(o -> o.path.equals(lco.path))) {
				list_diff.add(lco);
				continue;
			}
			if ((!remote_cache.stream().anyMatch(o -> o.size == lco.size) && (!lco.isdir))) {
				list_diff.add(lco);
				continue;
			}

		}
	}

	public List<DavResource> listFolders(String url) {

		List<DavResource> files = null;
		List<DavResource> folders = new ArrayList<>();
		try {
			URL url1 = new URL("https://" + user.domain + remote_path + url);
			URI uri = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost(), url1.getPort(), url1.getPath(),
					url1.getQuery(), url1.getRef());
			files = user.sardine.list(uri.toString(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (DavResource res : files) {
			if (res.isDirectory()) {
				folders.add(res);
			}
		}
		return folders;

	}

	private void save_remote_cache() throws IOException {

		FileOutputStream fos = new FileOutputStream("/tmp/test.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(remote_cache);
		oos.close();

	}
	
	public boolean test_sync() {

		try {
			if (user.sardine.list("https://" + user.domain + remote_path, 0) != null) {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void update_file_scroll() {
		// TODO Auto-generated method stub

		Set<Cacheobj> fifoList = new LinkedHashSet<>(fifo);
		
//		System.out.println("###############1");
//		for(Cacheobj co : fifoList) {
//			System.out.println(co.path);	
//		}
//		System.out.println("###############2");

		Platform.runLater(() -> {

			sync_controller.file_scroll.getChildren().clear();
			for (Cacheobj co : fifoList) {
				//System.out.println(co.path);
				co.fileLoadBar.controller.file_name.setText(co.path);

				sync_controller.file_scroll.getChildren().add(co.fileLoadBar.node);

			}

		});

	}
	
	private void printLists() {
		for (Cacheobj co : remote_cache) {
			System.out.println("path: " + co.path);
		}
		for (Cacheobj co : local_cache) {
			System.out.println("path: " + co.path);
		}
		System.out.println("diff ");
		for (Cacheobj co : list_diff) {
			System.out.println("path: " + co.path);
		}
	}

	private void mainLoop() throws IOException, InterruptedException, ClassNotFoundException, URISyntaxException {

		if (!new File("/tmp/test.txt").isFile()) {
			create_remote_cache();
		}
		load_remote_cache();
		while (!halt) {
			create_local_cache();
			compare();
			printLists();
			upload();
			Thread.sleep(1000);
		}

	}

	public void run() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mainLoop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

}