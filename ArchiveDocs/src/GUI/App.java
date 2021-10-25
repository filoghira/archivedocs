package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import Main.Archive;

public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private MainController mainController;
    private AddDocumentController addDocumentController;
    private Archive archive;

    public App(){}

    @Override
    public void start(Stage primaryStage){
        archive = new Archive("test");

        mainController = new MainController();
        mainController.setArchive(archive);

        addDocumentController = new AddDocumentController();
        addDocumentController.setArchive(archive);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Archive");

        initRootLayout();
        mainController.setMainApp(this);
        addDocumentController.setMainApp(this);

        showFileOverview();
    }

    /**
     * Initialize root layout
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader .load();

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
            loader.setLocation(App.class.getResource("view/FileOverview.fxml"));
            AnchorPane fileOverview = loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(fileOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddDocument(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("view/AddDocument.fxml"));
            AnchorPane addDocumentOverview = loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(addDocumentOverview);
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
