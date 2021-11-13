import javafx.application.Application;
import javafx.event.EventHandler;
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

        mainController.setMainApp(this);
        addDocumentController.setMainApp(this);
        addTagController.setMainApp(this);

        try {
            showFileOverview();
        } catch (IOException e) {
            System.out.println("Problem while loading the main scene");
        }
    }

    /**
     * Shows the file overview inside the root layout.
     */
    public void showFileOverview() throws IOException {
        // Load main scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("FileOverview.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        // KeyEvent listener for ESC -> Remove tag selection
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                MainController temp = loader.getController();
                temp.resetSelection();
            }
        });

        primaryStage.show();
    }

    public void showAddDocument() throws IOException {
        // Load AddDocument scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("AddDocument.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showAddTag() throws IOException {
        // Load AddTag scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("AddTag.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
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

