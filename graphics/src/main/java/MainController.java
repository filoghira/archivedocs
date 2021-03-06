import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable
{

    @FXML private TreeView<String> tagTree;
    @FXML private TableView<Document> fileTable;
    @FXML private TableColumn<Document, String> name;
    @FXML private TableColumn<Document, String> lastDate;
    @FXML private TableColumn<Document, String> size;
    @FXML private TableColumn<Document, ImageView> icon;

    private static Archive archive;
    private static App app;
    private static Tag selectedTag;
    private static Icons icons;

    public static void setMainApp(App appApp){
        MainController.app = appApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(tagTree!=null){

            fileTable.setOnMouseClicked(event -> {
                if (event.getButton()== MouseButton.PRIMARY && event.getClickCount() == 2) {
                    open(fileTable.getSelectionModel().getSelectedItem());
                }
            });

            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            lastDate.setCellValueFactory(new PropertyValueFactory<>("lastEdit"));
            size.setCellValueFactory(new PropertyValueFactory<>("size"));
            icon.setCellValueFactory(
                    p -> new ReadOnlyObjectWrapper<>(new ImageView(icons.getIcon(p.getValue().getExt()))));
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

        TreeItem<String> son = new TreeItem<>(
                tags.getData().getName().replace("_", " ")
        );

        // If it's the last item just add it
        if(children.isEmpty())
            rootItem.getChildren().add(son);
        else{
            // Otherwise, add each child to the root
            for (Node n : children)
                addNodes(son, n);
            rootItem.getChildren().add(son);
        }
    }

    public static void setup(Archive archive){
        MainController.archive = archive;
    }

    @FXML
    private void logout(){
        archive.logout();
        System.exit(0);
    }

    @FXML
    private void addDocument(){
        try {
            app.setAddDocumentScene();
        } catch (IOException e) {
            System.out.println("Problem while loading the AddDocument scene");
        }
    }

    @FXML
    private void addTag(){
        try {
            app.setAddTagScene();
        } catch (IOException e) {
            System.out.println("Problem while loading the AddTag scene");
        }
    }

    @FXML
    private void select(){
        try {
            String tagName = tagTree.getSelectionModel().getSelectedItem().getValue();
            tagName = tagName.replace(" ", "_");
            selectedTag = archive.getTagTree().getNode(tagName).getData();
            updateDocumentTable(archive.getTagTree().getNode(tagName).getData());
        }catch (NullPointerException e){
            System.out.println("No tag selected");
        }
    }

    @FXML
    private void removeDocument(){
        Document d = fileTable.getSelectionModel().getSelectedItem();
        if(d!=null)
            archive.removeDocument(d);
        updateDocumentTable(selectedTag);
    }

    @FXML
    private void editDocument(){
        Document d = fileTable.getSelectionModel().getSelectedItem();
        if(d!=null) {
            try {
                app.setEditDocumentScene(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void removeTag(){
        select();
        if(selectedTag!=null)
            archive.removeTag(selectedTag);
        selectedTag = null;
        init();
    }

    @FXML
    private void editTag(){
        select();
        if(selectedTag!=null) {
            try {
                app.setEditTagScene(selectedTag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void openDocument(){
        open(fileTable.getSelectionModel().getSelectedItem());

    }

    private void open(Document d){
        if(d!=null) {
            try {
                Desktop.getDesktop().open(d.getPath().toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void init(){
        setTagTree(archive.getTagTree());
        updateDocumentTable(null);
        icons = new Icons(archive.settings.getFileIconsLocation(),archive.settings.getIconLinks());
    }

    public void updateDocumentTable(Tag tag){
        fileTable.getItems().clear();
        List<Document> documents = tag == null ? archive.getDocuments() : tag.getDocuments();
        if(documents!=null)
            for (Document d:documents)
                fileTable.getItems().add(d);
    }

    public void resetSelection(){
        tagTree.getSelectionModel().clearSelection();
        selectedTag = null;
        updateDocumentTable(null);
    }

}