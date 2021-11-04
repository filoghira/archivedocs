import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.nio.file.Path;
import java.util.Date;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainController implements Initializable
{

    @FXML private TreeView<String> tagTree;
    @FXML private TableView<Document> fileTable;
    @FXML private TableColumn<Document, String> name;
    @FXML private TableColumn<Document, Date> date;
    @FXML private TableColumn<Document, Integer> size;
    @FXML private TableColumn<Document, Path> path;

    private static Archive archive;
    private static App app;

    public void setMainApp(App appApp){
        MainController.app = appApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(tagTree!=null)
            setTagTree(archive.getTagTree());
    }

    public void setTagTree(Node tags){
        TreeItem<String> rootItem = new TreeItem<>("Tags");
        if(tags.getChildren()!=null)
            for (Node n:tags.getChildren())
                addNodes(rootItem, n);
        tagTree.setRoot(rootItem);
    }

    // Recursively creates a TreeItem for each tag and adds it to its root
    void addNodes(TreeItem<String> rootItem, Node tags){
        List<Node> children = tags.getChildren();
        // If it's the last item just add it
        if(children.isEmpty())
            rootItem.getChildren().add(new TreeItem<>(tags.getData().getName()));
        else{
            // Otherwise, add each child to the root
            TreeItem<String> son = new TreeItem<>(tags.getData().getName());
            for (Node n : children)
                addNodes(son, n);
            rootItem.getChildren().add(son);
        }
    }

    public void setArchive(Archive archive){
        MainController.archive = archive;
    }

    @FXML
    private void logout(){
        archive.logout();
        System.exit(0);
    }

    @FXML
    private void addDocument(){
        app.showAddDocument();
    }

    @FXML
    private void addTag(){
        app.showAddTag();
    }

}