import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTagController implements Initializable {

    @FXML private TreeView<String> tagTree;
    @FXML private ComboBox<String> tagsComboBox;
    @FXML private TextField tagName;
    @FXML private TextArea tagDesc;
    @FXML private Label error;

    private static App appApp;
    private static Archive archive;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(tagTree!=null)
            update();
    }

    public void setTagTree(Node tags){
        TreeItem<String> rootItem = new TreeItem<>("Tags");
        if(tags.getChildren()!=null)
            for (Node n:tags.getChildren())
                MainController.addNodes(rootItem, n);
        tagTree.setRoot(rootItem);
    }

    public void setArchive(Archive archive){
        AddTagController.archive = archive;
    }

    public void setMainApp(App appApp){
        AddTagController.appApp = appApp;
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

        update();
    }

    @FXML
    private void goBack(){
        appApp.showFileOverview();
    }

    void update(){
        setTagTree(archive.getTagTree());
    }
}
