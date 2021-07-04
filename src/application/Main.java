package application;
	
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {
			
			//Parent root = FXMLLoader.load(getClass().getResource("/resources/view/syncronize.fxml"));
			Parent root = FXMLLoader.load(getClass().getResource("/resources/view/login_page.fxml"));
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                xOffset = event.getSceneX();
	                yOffset = event.getSceneY();
	            }
	        });
	        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                primaryStage.setX(event.getScreenX() - xOffset);
	                primaryStage.setY(event.getScreenY() - yOffset);
	            }
	        });
			Scene primaryScene = new Scene(root);
			primaryScene.setFill(Color.TRANSPARENT);
	        primaryStage.setScene(primaryScene);
	        
	        primaryStage.initStyle(StageStyle.TRANSPARENT);
	        primaryStage.show();
//			Parent root = FXMLLoader.load(getClass().getResource("/resources/view/login_page.fxml"));
//	        primaryStage.setTitle("Hello World");
//	        primaryStage.setScene(new Scene(root, 300, 275));
//	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
