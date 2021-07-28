package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.*;

import static GhiraUtils.General.getDirTree;
import static GhiraUtils.General.getParentDir;

public class Controller implements Initializable
{

    @FXML
    private ListView fileList;
    @FXML
    private URL url;
    @FXML
    private  ResourceBundle resourceBundle;

    private ObservableList<String> items = FXCollections.observableArrayList();
    private String defaultPath = "C:\\Users\\Filippo Ghirardini\\Desktop";
/*
    If I want a personalized Controller I need to search for a ControllerFactory (?)
    public Controller(String defaultPath){
        this.defaultPath = defaultPath;
    }
*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.url = url;
        this.resourceBundle = resourceBundle;

        fileList.setItems(items);

        String parentDir = getParentDir(defaultPath);
        String[] temp = parentDir.split("\\\\");

        ArrayList<String> pathnames = new ArrayList<>() {{add(temp[temp.length-1]);}};
        pathnames.addAll(Arrays.asList(getDirTree(defaultPath)));

        if(pathnames!=null)
            items.addAll(pathnames);
    }

}
