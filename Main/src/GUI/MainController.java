package GUI;

import GhiraUtils.FileAlreadyInArchiveException;
import GhiraUtils.FileNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import Main.Archive;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Main.Node;

import java.io.File;
import java.net.URL;
import java.util.*;


public class MainController implements Initializable
{

    @FXML
    private Stage primaryStage;
    @FXML
    private TreeView<Node> tagTree;

    private static Archive archive;

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(tagTree!=null)
            setTagTree(archive.getTagTree());
    }

    public void setTagTree(Node tags){
        TreeItem rootItem = new TreeItem("Tags");
        if(tags.getChildren()!=null)
            for (Node n:tags.getChildren())
                addNodes(rootItem, n);
        tagTree.setRoot(rootItem);
    }

    // Recursively creates a TreeItem for each tag and adds it to its root
    void addNodes(TreeItem rootItem, Node tags){
        List<Node> children = tags.getChildren();
        // If it's the last item just add it
        if(children.isEmpty())
            rootItem.getChildren().add(new TreeItem(tags.getData().getName()));
        else{
            // Otherwise, add each child to the root
            TreeItem son = new TreeItem(tags.getData().getName());
            for (Node n : children)
                addNodes(son, n);
            rootItem.getChildren().add(son);
        }
    }

    public void setArchive(Archive archive){
        MainController.archive = archive;
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
                System.out.println("File già presente nell'archivio");
            }
        }
    }

}