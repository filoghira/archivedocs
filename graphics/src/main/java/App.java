import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private Stage primaryStage;
    private Archive archive;

    public App(){}

    @Override
    public void start(Stage primaryStage){
        archive = new Archive("test", "");

        MainController.setArchive(archive);
        MainController.setMainApp(this);

        AddDocumentController.setArchive(archive);
        AddDocumentController.setMainApp(this);

        AddTagController.setArchive(archive);
        AddTagController.setMainApp(this);

        EditDocumentController.init(null, this, archive);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Archive");
        this.primaryStage.getIcons().add(new Image("icon.png"));

        try {
            setFileOverviewScene();
            this.primaryStage.show();
        } catch (IOException e) {
            System.out.println("Problem while loading the main scene");
        }
    }

    /**
     * Shows the file overview inside the root layout.
     */
    public void setFileOverviewScene() throws IOException {
        // Load main scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("FileOverview.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);

        // KeyEvent listener for ESC -> Remove tag selection
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE)
                ((MainController)loader.getController()).resetSelection();
        });
    }

    public void setAddDocumentScene() throws IOException {
        // Load AddDocument scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("AddDocument.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public void setAddTagScene() throws IOException {
        // Load AddTag scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("AddTag.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
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

    public void setEditDocumentScene(Document d) throws IOException {
        EditDocumentController.init(d, this, archive);
        FXMLLoader loader = new FXMLLoader(App.class.getResource("EditDocument.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }
}

