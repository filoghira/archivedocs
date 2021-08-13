package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import Main.Archive;

import java.net.URL;
import java.util.*;


public class Controller implements Initializable
{

    @FXML
    private ListView fileList;
    @FXML
    private URL url;
    @FXML
    private  ResourceBundle resourceBundle;

    private ObservableList<String> items = FXCollections.observableArrayList();
    private Archive archive;

    public void setArchive(Archive archive){
        this.archive = archive;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.url = url;
        this.resourceBundle = resourceBundle;

        fileList.setItems(items);

    }

}