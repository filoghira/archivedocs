package GUI;

import GhiraUtils.FileAlreadyInArchiveException;
import GhiraUtils.FileNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import Main.Archive;
import javafx.scene.shape.Arc;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;


public class MainController implements Initializable
{

    @FXML
    private ListView fileList;
    @FXML
    private URL url;
    @FXML
    private  ResourceBundle resourceBundle;
    @FXML
    private Stage primaryStage;

    private static Archive archive;
    private ObservableList<String> items = FXCollections.observableArrayList();

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.url = url;
        this.resourceBundle = resourceBundle;

    }

    public void setArchive(Archive archive){
        this.archive = archive;
    }

    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if(selectedFile!=null) {
            try {
                archive.addDocument(selectedFile.getName(), selectedFile.toPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (FileAlreadyInArchiveException e) {
                e.printStackTrace();
            }
        }
    }

}