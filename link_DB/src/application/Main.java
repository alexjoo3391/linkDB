package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	@Override

	public void start(Stage primaryStage) {
		try {
			System.setProperty("prism.lcdtext", "false");
			Font.loadFont(getClass().getResourceAsStream("/resources/NanumSquareRoundEB.ttf"), 10);
			AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getResource("/view/MainLayout.fxml"));
			Scene scene = new Scene(ap);
			scene.getStylesheets().add(getClass().getResource("application.css").toString());

			primaryStage.setTitle("link");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}