package controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class FileBox {

    @FXML
    private HBox root;

    @FXML
    private ImageView icon_view;

    @FXML
    public Label file_name;

    @FXML
    public Label file_size;

    @FXML
    private Rectangle progress;

    @FXML
    private AnchorPane progress_background;
    
    private Label status;

    @FXML
    void initialize(){
        //updateProgress(100);
    	root.getChildren().remove(progress_background);
    	
    	status = new Label("Uploading ...");
    	status.setId("status");
    	status.setAlignment(Pos.CENTER);
        root.getChildren().add(status);
    	
    }

    void updateProgress(int percentageValue){
        double max_size = 150;
        progress.setWidth((percentageValue*max_size)/100);
        if(percentageValue == 100)
            finishedProgress();
    }
    
    public void setFileName(String name) {
    	file_name.setText(name);
    }

    public void finishedProgress(){
        char tick = (char)0x2713;
        status.setText(tick+" Done");
//        root.getChildren().remove("status");
//        Label label = new Label(tick+" Done");
//        label.setId("status");
//        label.setAlignment(Pos.CENTER);
//        root.getChildren().add(label);
    }

}
