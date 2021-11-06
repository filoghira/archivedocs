import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

public class MainController implements Initializable
{

    @FXML private TreeView<String> tagTree;
    @FXML private TableView<Document> fileTable;
    @FXML private TableColumn<Document, String> name;
    @FXML private TableColumn<Document, String> lastDate;
    @FXML private TableColumn<Document, String> size;
    @FXML private TableColumn<Document, Image> icon;

    private static Archive archive;
    private static App app;

    public void setMainApp(App appApp){
        MainController.app = appApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(tagTree!=null){
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            lastDate.setCellValueFactory(new PropertyValueFactory<>("lastEdit"));
            size.setCellValueFactory(new PropertyValueFactory<>("size"));
            // icon.setCellValueFactory(new PropertyValueFactory<>("icon"));
            init();
        }
    }

    public void setTagTree(Node tags){
        TreeItem<String> rootItem = new TreeItem<>("Tags");
        if(tags.getChildren()!=null)
            for (Node n:tags.getChildren())
                addNodes(rootItem, n);
        tagTree.setRoot(rootItem);
    }

    // Recursively creates a TreeItem for each tag and adds it to its root
    public static void addNodes(TreeItem<String> rootItem, Node tags){
        List<Node> children = tags.getChildren();
        // If it's the last item just add it
        if(children.isEmpty())
            rootItem.getChildren().add(new TreeItem<>(tags.getData().getName().replace("_", " ")));
        else{
            // Otherwise, add each child to the root
            TreeItem<String> son = new TreeItem<>(tags.getData().getName().replace("_", " "));
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

    void init(){
        setTagTree(archive.getTagTree());
        updateDocumentTable();
    }

    private void updateDocumentTable(){
        List<Document> documents = archive.getDocuments();
        for (Document d:documents){
            fileTable.getItems().add(d);
        }
    }

}