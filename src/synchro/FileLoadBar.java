package synchro;

import java.io.IOException;
import java.io.Serializable;

import controller.FileBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class FileLoadBar {

	public Node node;
	public FileBox controller;
	
	public FileLoadBar() {
		super();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/fileBox.fxml"));
		try {
			this.node = fxmlLoader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.controller = fxmlLoader.getController();
	}
	
	

}
