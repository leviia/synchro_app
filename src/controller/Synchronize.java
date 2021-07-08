package controller;

import application.Main;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.DirectoryChooser;
import synchro.Cacheobj;
import synchro.FileLoadBar;
import synchro.Job;
import synchro.Synchro;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.github.sardine.DavResource;

public class Synchronize {

    @FXML
    private Circle profile_picture;

    @FXML
    private Button btn_local_directory;

    @FXML
    private ComboBox<String> remoteDirectory;

    @FXML
    private Button synchronization;

    @FXML
    private Button upload;

    @FXML
    private Button notification;

    @FXML
    private Button settings;

    @FXML
    private Label folderSelection_msgLabel;

    @FXML
    private TableView<?> file_tableView;

    @FXML
    private TableColumn<?, ?> file_column;

    @FXML
    private TableColumn<?, ?> size_column;

    @FXML
    private TableColumn<?, ?> status_column;

    @FXML
    private LineChart<String, Number> upload_chart;

    @FXML
    private LineChart<String, Number> download_chart;

    @FXML
    private Arc storage_ProgressIndicator;

    @FXML
    private Label storage_value;

    @FXML
    private Label storage_metaSize;

    @FXML
    private Label used_storage;

    @FXML
    private Label free_storage;

    @FXML
    private Label storage_percentage;

    @FXML
    public VBox file_scroll;

    @FXML
    private Pane uploadChart_Container;

    @FXML
    private Pane downloadChart_container;

    private double chartX = 19, chartY = 69;

    @FXML
    void initialize(){
    	
    	controller.Connect.sync.sync_controller = this;

        initLineCarts();

        try {
            Synchro.user.retreive_info();
            update_StorageIndicator(synchro.Synchro.user.quota_relative);
            update_Avatar();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	void initLineCarts(){ // this is for chart testing purpose.

        download_chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        upload_chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");

        setData(download_chart,"-fx-stroke: #f2cb0a");
        setData(upload_chart, "-fx-stroke: #00BC73");
        
        
    }

    private void setData(LineChart<String, Number> lineChart, String lineColor) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();


        for(int i = 0; i< 10; i++){
            XYChart.Data<String, Number> data = new XYChart.Data<>();
            Rectangle rect = new Rectangle();
            rect.setVisible(false);
            data.setNode(rect);
            data.setXValue(""+i);
            data.setYValue(new Random().nextInt(100));
            series.getData().add(data);
        }
        lineChart.getData().add(series);
        series.getNode().setStyle(lineColor);
    }

    private void update_Avatar() {

    	profile_picture.setFill(new ImagePattern(SwingFXUtils.toFXImage(Synchro.user.avatar, null)));
	}

	/**
     * @Syed Suleman Shah
     * by changing the length of Arc you can see the effect of progress Indicator.
     * @param percentageValue
     */
    void update_StorageIndicator(float percentageValue){
    	storage_value.setText(String.valueOf(Synchro.user.quota_free/1000000000));
    	used_storage.setText(String.valueOf(Synchro.user.quota_used/1000000000));
    	free_storage.setText(String.valueOf(Synchro.user.quota_total/1000000000));
        storage_percentage.setText(percentageValue+"%");
        double value = (double)(percentageValue * 360) / 100;
        storage_ProgressIndicator.setLength(-value);
    }

    @FXML
    void copy_archive_path(ActionEvent event) {

    }

    @FXML
    void copy_officeWork_path(ActionEvent event) {

    }

    @FXML
    void select_local_directory(ActionEvent event) {
    	
        String folder = System.getProperty("user.home");
    	
    	if (!btn_local_directory.getText().equals("Select Local Directory")) {
    		folder = btn_local_directory.getText();
    	}
    	
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Local Directory");
        File defaultDirectory = new File(folder);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.getInstance().stage);
        btn_local_directory.setText(selectedDirectory.getPath());
    }

    @FXML
    void select_remote_directory() {
    	
    	String url = "/";
    	
    	if (remoteDirectory.getSelectionModel().getSelectedItem() != null) {
    		url = remoteDirectory.getSelectionModel().getSelectedItem();
    	}
    	remoteDirectory.getItems().clear();
    	// just here to make the dropdow good size 
    	remoteDirectory.hide();
    	remoteDirectory.setVisibleRowCount(1);
    	remoteDirectory.show();
    	remoteDirectory.hide();
    	// really annoying !!
    	remoteDirectory.getItems().add("/");
    	
    	List<DavResource> folders = controller.Connect.sync.listFolders(url);
    	for (DavResource folder : folders) {
    		if(folder.getPath().replace(controller.Connect.sync.remote_path, "").equals("/")) {
    			continue;
    		}
    		
    		remoteDirectory.getItems().add(folder.toString().replace(controller.Connect.sync.remote_path, "").replaceAll("/$", ""));
    		remoteDirectory.setVisibleRowCount(remoteDirectory.getVisibleRowCount()+1);
    	}   	
    	
    	remoteDirectory.show();
    	

    }

    @FXML
    void start_synchronization(ActionEvent event) {
    	
    	controller.Connect.sync.halt = false;
    	controller.Connect.sync.run();

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
