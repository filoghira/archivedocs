package GUI;

import GhiraUtils.FileAlreadyInArchiveException;
import GhiraUtils.FileNotFoundException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;

import Main.Archive;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private MainController controller;
    private Archive archive;

    @Override
    public void start(Stage primaryStage){
        controller = new MainController();
        archive = new Archive("test");

        archive.removeDocument(archive.getDocument(1));

        controller.setArchive(archive);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Archive");

        initRootLayout();
        showFileOverview();
    }

    /**
     * Initialize root layout
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the file overview inside the root layout.
     */
    public void showFileOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/FileOverview.fxml"));
            AnchorPane fileOverview = loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(fileOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the main stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

