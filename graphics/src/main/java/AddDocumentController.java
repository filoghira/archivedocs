import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    private static App appApp;
    private static Archive archive;
    private File selectedFile;
    private ObservableList<String> selectedTags;

    public void setArchive(Archive archive){
        AddDocumentController.archive = archive;
    }

    public void setMainApp(App appApp){
        AddDocumentController.appApp = appApp;
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(appApp.getPrimaryStage());
        if(selectedFile!=null) {
            try {
                // TODO: 01/11/2021 Implementare la gestione delle impostazioni del programma
                if(archive.documentExists(General.checksum(selectedFile.getPath(), "SHA-512")))
                    error.setText("Il documento \u00e8 gi\u00e0 presente in archivio");
                else{
                    error.setText("");
                    this.selectedFile = selectedFile;
                    docName.setText(selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf(".")));
                    docPath.setText(selectedFile.getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void add(){
        try {
            List<Tag> tags = new ArrayList<>();
            if(selectedTags != null)
                for(String t : selectedTags)
                    tags.add(archive.getTagTree().getNode(t).getData());

            archive.addDocument(tags, selectedFile.getName(), Path.of(selectedFile.getPath()), docDesc.getText());
        } catch (FileNotFoundException e) {
            error.setText("Il documento \u00e8 stato rimosso dalla cartella d'origine");
        } catch (FileAlreadyInArchiveException e) {
            error.setText("Il documento \u00e8 gi\u00e0 presente in archivio");
        }
    }

    @FXML
    private void goBack(){
        appApp.showFileOverview();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // If the combobox has been initialized
        if(tagsComboBox!=null)
            initTagsComboBox();
    }

    void initTagsComboBox(){
        List<Node> tags = archive.getTagTree().getLeaves();
        if(tags.size() <= 1)
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
