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

    public static final String defaultFolder = "\\archivedocs\\";

    public App(){}

    @Override
    public void start(Stage primaryStage){

        Settings settings = new Settings(General.homePath() + defaultFolder, "user.properties");

        archive = new Archive("test", "", settings);

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

    /**
     * Set the scene to the add document scene
     * @throws IOException if the scene could not be loaded
     */
    public void setAddDocumentScene() throws IOException {
        // Load AddDocument scene
        FXMLLoader loader = new FXMLLoader(App.class.getResource("AddDocument.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    /**
     * Set the scene to the add tag scene
     * @throws IOException if the scene could not be loaded
     */
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

    public void setEditTagScene(Tag selectedTag) throws IOException {
        EditTagController.init(selectedTag, this, archive);
        FXMLLoader loader = new FXMLLoader(App.class.getResource("EditTag.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }
}

