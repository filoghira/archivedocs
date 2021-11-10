import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

// TODO: 01/11/2021 Correggere il warning Unsupported JavaFX configuration: classes were loaded from 'unnamed module'

public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private MainController mainController;
    private AddDocumentController addDocumentController;
    private AddTagController addTagController;
    private Archive archive;

    public App(){}

    @Override
    public void start(Stage primaryStage){
        archive = new Archive("test", "");

        mainController = new MainController();
        mainController.setArchive(archive);

        addDocumentController = new AddDocumentController();
        addDocumentController.setArchive(archive);

        addTagController = new AddTagController();
        addTagController.setArchive(archive);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Archive");
        this.primaryStage.getIcons().add(new Image("icon.png"));

        initRootLayout();
        mainController.setMainApp(this);
        addDocumentController.setMainApp(this);
        addTagController.setMainApp(this);

        showFileOverview();
    }

    /**
     * Initialize root layout
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("RootLayout.fxml"));
            rootLayout = loader.load();
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
            loader.setLocation(App.class.getResource("FileOverview.fxml"));
            AnchorPane fileOverview = loader.load();

            Scene scene = new Scene(fileOverview);

            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.E)
                    mainController.updateDocumentTable(null);
            });

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddDocument(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("AddDocument.fxml"));
            AnchorPane addDocumentOverview = loader.load();

            Scene scene = new Scene(addDocumentOverview);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddTag(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("AddTag.fxml"));
            AnchorPane addTagOverview = loader.load();

            Scene scene = new Scene(addTagOverview);
            primaryStage.setScene(scene);
            primaryStage.show();
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
        Application.launch(args);
    }
}

