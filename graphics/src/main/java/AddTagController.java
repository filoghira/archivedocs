import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddTagController implements Initializable {

    @FXML private TreeView<String> tagTree;
    @FXML private ComboBox<String> tagsComboBox;
    @FXML private TextField tagName;
    @FXML private TextArea tagDesc;
    @FXML private Label error;

    private static App appApp;
    private static Archive archive;
    private String parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTagsComboBox();
        update();
    }

    public void setTagTree(Node tags){
        TreeItem<String> rootItem = new TreeItem<>("Tags");
        if(tags.getChildren()!=null)
            for (Node n:tags.getChildren())
                MainController.addNodes(rootItem, n);
        tagTree.setRoot(rootItem);
    }

    public static void setArchive(Archive archive){
        AddTagController.archive = archive;
    }

    public static void setMainApp(App appApp){
        AddTagController.appApp = appApp;
    }

    @FXML
    private void add(){
        String name = tagName.getText();

        if(parent!=null && !archive.getTagTree().nodeExists(parent))
            error.setText("Parent tag doesn't exist");
        else if(archive.getTagTree().nodeExists(name))
            error.setText("Tag already exist");
        else
            archive.addTag(null, name, parent, tagDesc.getText());

        clear();
        update();
    }

    private void clear(){
        tagName.clear();
        tagDesc.clear();
        tagsComboBox.setValue(null);
    }

    @FXML
    private void goBack(){
        try {
            appApp.showFileOverview();
        } catch (IOException e) {
            System.out.println("Problem while loading main scene");
        }
    }

    void update(){
        setTagTree(archive.getTagTree());
        initTagsComboBox();
    }

    void initTagsComboBox(){
        //noinspection DuplicatedCode
        List<Node> tags = archive.getTagTree().getLeaves();
        if(tags.size() < 1)
            return;
        // Get the tags that are leaves and convert them to string
        ObservableList<String> items = FXCollections.observableArrayList();
        for(Node n : tags)
            items.add(n.getTagName());
        // Add them to the combobox
        tagsComboBox.getItems().addAll(items);

        tagsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> parent = newValue);
    }
}
