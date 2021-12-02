import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditTagController implements Initializable {

    private static Archive archive;
    private static App app;
    private static Tag tag;
    @FXML private TreeView<String> tagTree;
    @FXML private ComboBox<String> tagsComboBox;
    @FXML private TextField tagName;
    @FXML private TextArea tagDesc;
    @FXML private Label error;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tagName.setText(tag.getName().replace("_", " "));
        tagDesc.setText(tag.getDescription());
        update();
        Tag parentTag = tag.getParent();
        if(parentTag != null)
            tagsComboBox.getSelectionModel().select(parentTag.getName());
    }

    static void init(Tag selectedTag, App app, Archive archive) {
        EditTagController.tag = selectedTag;
        EditTagController.app = app;
        EditTagController.archive = archive;
    }

    /**
     * Update the tag tree and the combo box
     */
    void update(){
        setTagTree(archive.getTagTree());
        initTagsComboBox();
    }

    @FXML
    public void save() {
        archive.editTag(tag, tagName.getText(), tagDesc.getText(), tagsComboBox.getSelectionModel().getSelectedItem());
        // Go back to the file overview scene
        goBack();
    }

    @FXML
    public void goBack() {
        try {
            app.setFileOverviewScene();
        } catch (IOException e) {
            System.out.println("Unable to load FileOverview scene");
            e.printStackTrace();
        }
    }

    /**
     * Set the tag tree
     * @param tags Tag's list
     */
    public void setTagTree(Node tags){
        TreeItem<String> rootItem = new TreeItem<>("Tags");
        if(tags.getChildren()!=null)
            for (Node n:tags.getChildren())
                MainController.addNodes(rootItem, n);
        tagTree.setRoot(rootItem);
    }

    /**
     * Initialize the combo box
     */
    void initTagsComboBox(){
        // Clear
        //noinspection DuplicatedCode
        tagsComboBox.getItems().clear();

        List<Node> tags = archive.getTagTree().getNodes();
        if(tags.size() < 1)
            return;
        // Get the tags that are leaves and convert them to string
        ObservableList<String> items = FXCollections.observableArrayList();
        for(Node n : tags)
            if(n.getData() != tag)
                items.add(n.getTagName());

        // Add them to the combobox
        tagsComboBox.getItems().addAll(items);
    }
}
