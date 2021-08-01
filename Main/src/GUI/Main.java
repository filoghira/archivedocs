package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import Main.Tag;

import Main.Archive;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Controller controller;
    private Archive archive;

    @Override
    public void start(Stage primaryStage){
        controller = new Controller();
        archive = new Archive("test");
        controller.setArchive(archive);

        archive.addDocument(new String[] {"gigi"}, "a", Path.of("C:\\"));

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

