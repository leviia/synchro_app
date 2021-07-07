package controller;

import application.Main;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.DirectoryChooser;
import synchro.FileLoadBar;
import synchro.Job;
import synchro.Synchro;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    private VBox file_scroll;

    @FXML
    private Pane uploadChart_Container;

    @FXML
    private Pane downloadChart_container;

    private double chartX = 19, chartY = 69;

    @FXML
    void initialize(){

//        remoteDirectory.getItems().add("Test 1");
//        remoteDirectory.getItems().add("Test 2");

//    	for(int i=0; i< 4; i++) {
//    		Node node = null;
//			try {
//				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/fileBox.fxml"));
//    			node = fxmlLoader.load();
//    			((FileBox) fxmlLoader.getController()).file_name.setText("test.txt");
//    		} catch (IOException e1) {
//    			// TODO Auto-generated catch block
//    			e1.printStackTrace();
//    		}
//
//			file_scroll.getChildren().add(node);
//
//    	}



//    	Node node2 = null;
//		try {
//			node2 = (Node)FXMLLoader.load(getClass().getResource("/resources/view/fileBox.fxml"));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	file_scroll.getChildren().add(node2);



    	    FileLoadBar flb1 = new FileLoadBar();
    	    flb1.controller.file_name.setText("test.txt");
    	    file_scroll.getChildren().add(flb1.node);

    	    FileLoadBar flb2 = new FileLoadBar();
    	    flb2.controller.file_name.setText("test.txt");
    	    file_scroll.getChildren().add(flb2.node);

        initLineCarts();

        try {
            Synchro.user.retreive_info();
            update_StorageIndicator(synchro.Synchro.user.quota_relative);
            update_Avatar();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void initLineCarts(){
        // we need to add some data first before we set the properties of graph.
        setData(upload_chart);
        setData(download_chart);

        setupChart(upload_chart,Color.web("#00bc73"),uploadChart_Container, Color.rgb(201, 255, 210, .3));
        setupChart(download_chart,Color.web("#f2cb0a"),downloadChart_container, Color.rgb(255, 245, 198, .3));
    }

    public void setupChart(LineChart<String, Number> lineChart, Color lineColor,Pane container, Color linearColor){
        LineChart<String, Number> chart = new LineChart<>(lineChart.getXAxis(), lineChart.getYAxis(), lineChart.getData()) {
            @Override
            protected void layoutPlotChildren() {
                super.layoutPlotChildren();
                getPlotChildren().clear();
                Series<String, Number> series = getData().get(0);
                ObservableList<Data<String, Number>> listOfData = series.getData();
                for (int i = 0; i < listOfData.size() - 1; i++) {
                    double x1 = getXAxis().getDisplayPosition(listOfData.get(i).getXValue());
                    double y1 = getYAxis().getDisplayPosition(0);
                    double x2 = getXAxis().getDisplayPosition(listOfData.get((i + 1)).getXValue());
                    double y2 = getYAxis().getDisplayPosition(0);
                    Polygon polygon = new Polygon();
                    LinearGradient linearGrad = new LinearGradient(0, 0, 1, 1,
                            true, // proportional
                            CycleMethod.NO_CYCLE, // cycle colors
                            new Stop(0.1f, linearColor));

                    polygon.getPoints().addAll(x1, y1,
                            x1, getYAxis().getDisplayPosition(listOfData.get(i).getYValue()),
                            x2, getYAxis().getDisplayPosition(listOfData.get((i + 1)).getYValue()),
                            x2, y2);
                    CubicCurve curve = new CubicCurve();
                    curve.setStrokeWidth(4);
                    curve.setFill(Color.TRANSPARENT);
                    curve.setStroke(lineColor);
                    curve.setStartX(x1);
                    curve.setStartY(getYAxis().getDisplayPosition(listOfData.get(i).getYValue()));
                    curve.setEndX(x2);
                    curve.setEndY(getYAxis().getDisplayPosition(listOfData.get((i + 1)).getYValue()));
                    double cy1 = getYAxis().getDisplayPosition(listOfData.get(i).getYValue());
                    double cy2 = getYAxis().getDisplayPosition(listOfData.get((i + 1)).getYValue());
                    double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
                    distance = distance / 1.5;
                    curve.setControlX1(x1 + 18 * (x2 - x1) / distance);
                    curve.setControlY1(cy1 + 13 * (cy2 - cy1) / distance);
                    curve.setControlX2(x2 - 22 * (x2 - x1) / distance);
                    curve.setControlY2(cy2 - 14 * (cy2 - cy1) / distance);

                    getPlotChildren().add(curve);
                    getPlotChildren().add(polygon);
                    polygon.toFront();
                    polygon.setFill(linearGrad);
                }
            }
        };
        chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        chart.setLayoutX(lineChart.getLayoutX());
        chart.setLayoutY(lineChart.getLayoutY());
        chart.setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        chart.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        chart.setPrefSize(lineChart.getPrefWidth(),lineChart.getPrefHeight());
        chart.setLegendVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setVerticalZeroLineVisible(false);
        container.getChildren().remove(lineChart);
        container.getChildren().add(chart);
    }

    private void setData(LineChart<String, Number> chart) {
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
        chart.getData().add(series);
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
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Local Directory");
        File defaultDirectory = new File("/home/arnaud/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.getInstance().stage);
        btn_local_directory.setText(selectedDirectory.getPath());
    }

    @FXML
    void select_remote_directory() {
    	
    	List<DavResource> folders = controller.Connect.sync.listFolders("/");
    	for (DavResource folder : folders) {
    		
    		remoteDirectory.getItems().add(folder.getPath().replace(controller.Connect.sync.remote_path, ""));
    	}
    	

    }

    @FXML
    void start_synchronization(ActionEvent event) {

    	Job job1 = new Job();

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
