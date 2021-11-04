import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTagController implements Initializable {

    @FXML private ComboBox<String> tagsComboBox;
    @FXML private TextField tagName;
    @FXML private TextArea tagDesc;
    @FXML private Label error;

    private static App appApp;
    private static Archive archive;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setArchive(Archive archive){
        this.archive = archive;
    }

    public void setMainApp(App appApp){
        this.appApp = appApp;
    }

    @FXML
    private void add(){
        String parent = tagsComboBox.getValue();
        String name = tagName.getText();

        if(parent!=null && !archive.getTagTree().nodeExists(parent))
            error.setText("Parent tag doesn't exist");
        else if(archive.getTagTree().nodeExists(name))
            error.setText("Tag already exist");
        else
            archive.addTag(null, name, parent, tagDesc.getText());

    }

    @FXML
    private void goBack(){
        appApp.showFileOverview();
    }
}
