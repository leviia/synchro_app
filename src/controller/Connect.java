package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import synchro.Synchro;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Connect {

    @FXML
    private ComboBox<String> language_box; // comboBox for language selection.

    @FXML
    private TextField username_input; // user name input field

    @FXML
    private PasswordField pass_input; // password input field

    @FXML
    private TextField hostName_input; // host name input

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
    	
    	Synchro sync = new synchro.Synchro(username_input.getText(),pass_input.getText(),hostName_input.getText());
    	
    	System.out.println(pass_input.getText());
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
}
