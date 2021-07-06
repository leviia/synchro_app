package application;

import java.io.IOException;

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
	public Stage stage;

	private static Main instance;

	public Main() {
		instance = this;
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		stage = primaryStage;

		replaceSceneContent("/resources/view/login_page.fxml");
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public boolean replaceSceneContent(String fxml) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(fxml));
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
					stage.setX(event.getScreenX() - xOffset);
					stage.setY(event.getScreenY() - yOffset);
				}
			});
			Scene primaryScene = new Scene(root);
			primaryScene.setFill(Color.TRANSPARENT);
			stage.setScene(primaryScene);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}