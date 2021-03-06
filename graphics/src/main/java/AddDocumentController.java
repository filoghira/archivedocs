import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddDocumentController implements Initializable {

    @FXML private CheckComboBox<String> tagsComboBox;
    @FXML private TextField docName;
    @FXML private Label docPath;
    @FXML private TextArea docDesc;
    @FXML private Label error;

    private static App app;
    private static Archive archive;
    private File selectedFile;
    private ObservableList<String> selectedTags;

    public static void setArchive(Archive archive){
        AddDocumentController.archive = archive;
    }

    public static void setMainApp(App appApp){
        AddDocumentController.app = appApp;
    }

    /**
     * Opens a file chooser and sets the selected file
     */
    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(app.getPrimaryStage());
        if(selectedFile!=null) {
            try {
                if(archive.documentExists(General.checksum(selectedFile.getPath(), "SHA-512")))
                    error.setText("The document is already in archive");
                else{
                    error.setText("");
                    this.selectedFile = selectedFile;

                    // Filter name with regex
                    String fileName = selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf("."));
                    fileName = fileName.replaceAll("[^a-zA-Z_0-9\s]", "_");

                    docName.setText(fileName);
                    docPath.setText(selectedFile.getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Adds the document to the archive
     */
    @FXML
    private void add(){
        try {
            List<Tag> tags = new ArrayList<>();
            if(selectedTags != null)
                for(String t : selectedTags)
                    tags.add(archive.getTagTree().getNode(t).getData());

            if(selectedFile == null) throw new IOException("No file selected");

            archive.addDocument(tags, selectedFile.getName(), Path.of(selectedFile.getPath()), docPath.getText());
        } catch (FileNotFoundException e) {
            error.setText("The document has been moved");
        } catch (FileAlreadyInArchiveException e) {
            error.setText("The document is already in the archive");
        } catch (IOException e) {
            error.setText("The document is not found");
        }
    }

    @FXML
    private void goBack(){
        try {
            app.setFileOverviewScene();
        } catch (IOException e) {
            System.out.println("Problem while loading main scene");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTagsComboBox();

        // Text validation: letter, number or underscore
        docName.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("[a-zA-Z_0-9\s]{0,260}$")) ? change : null));
    }

    /**
     * Initializes the tag's combo box
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
            items.add(n.getTagName());
        // Add them to the combobox
        tagsComboBox.getItems().addAll(items);

        tagsComboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<String>) c -> selectedTags = tagsComboBox.getCheckModel().getCheckedItems()
        );
    }
}
