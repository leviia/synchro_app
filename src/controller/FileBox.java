package controller;

import javafx.fxml.FXML;
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
    private Label file_size;

    @FXML
    private Rectangle progress;

    @FXML
    private AnchorPane progress_background;

    @FXML
    void initialize(){
        updateProgress(10);
    }

    void updateProgress(int percentageValue){
        double max_size = 150;
        progress.setWidth((percentageValue*max_size)/100);
        if(percentageValue == 100)
            onFinishedProgress();
    }
    
    public void setFileName(String name) {
    	file_name.setText(name);
    }

    void onFinishedProgress(){
        char tick = (char)0x2713;
        root.getChildren().remove(progress_background);
        Label label = new Label(tick+" Done");
        label.setId("status");
        root.getChildren().add(label);
    }

}
