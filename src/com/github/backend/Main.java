package com.github.backend;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private BorderPane bp;
	private static Main instance;
	private Parent root;
	
	public static Main getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void setCentre(Node n) {
		this.bp.setCenter(n);
	}

	@Override
	public void start(Stage stage) throws Exception {
		if (instance != null) {
			System.out.println("ERR");
		}
		instance = this;
		try {
			root = FXMLLoader.load(getClass().getResource("/com/github/display/Display.fxml"));
			Scene scene = new Scene(root);
			bp = (BorderPane) root;
			stage.setScene(scene);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		stage.setTitle("Stock Price Examination!");
		stage.show();
	}
}
