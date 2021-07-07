package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import synchro.Synchro;

import application.Main;

public class Connect {
	
	private static boolean cont = false;

    @FXML
    private ComboBox<String> language_box; // comboBox for language selection.

    @FXML
    private TextField username_input; // user name input field

    @FXML
    private PasswordField pass_input; // password input field

    @FXML
    private TextField hostName_input; // host name input
    
    @FXML
    private Button connect_btn; // host name input

    @FXML
    private Label msg_label; // msg label

    @FXML
    void initialize(){
        //TODO: do something when you call this view.
    	hostName_input.setText("cloud.leviia.com");
    }

    @FXML
    void connect(ActionEvent event) {
        //TODO: when you press Test Connection Button.
    	if (cont) {
    		Main.getInstance().replaceSceneContent("/resources/view/syncronize.fxml");
    		return;
    	}
    	
    	
    	Synchro sync = new synchro.Synchro(username_input.getText(),pass_input.getText(),hostName_input.getText());
    	
    	if (sync.test_sync()) {
    	connect_btn.setText("continue");
    	msg_label.setTextFill(Color.rgb(0, 188, 115));
    	msg_label.setText("Success !");
    	msg_label.setVisible(true);
    	cont = true;
    	}
    	
    	else {
    		msg_label.setText("Error, check inputs");
    		msg_label.setTextFill(Color.RED);
        	msg_label.setVisible(true);
    	}
    }

    @FXML
    void sign_up(ActionEvent event) {
        //TODO: sign_up hyperlink text on click.
    	
//    	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//    	    try {
//				Desktop.getDesktop().browse(new URI("https://www.leviia.com"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
    	
    }
    @FXML
    void minimize_app(ActionEvent event) {
        //Stage stage = (Stage) msg_label.getScene().getWindow();
        Main.getInstance().stage.toBack();
        //stage.toBack();
    }
    @FXML
    void close_application(ActionEvent event) {
        System.exit(0);
    }
}
