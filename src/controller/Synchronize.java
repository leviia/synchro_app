package controller;

import application.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import synchro.Synchro;

import java.util.Random;

public class Synchronize {

    @FXML
    private Circle profile_picture;

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
    void initialize(){
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

    }

    @FXML
    void select_remote_directory(ActionEvent event) {

    }

    @FXML
    void start_synchronization(ActionEvent event) {

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
