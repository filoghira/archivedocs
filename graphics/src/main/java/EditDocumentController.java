import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditDocumentController implements Initializable {

    private static Archive archive;
    @FXML private TextField docName;
    @FXML private Label docPath;
    @FXML private TextArea docDesc;
    @FXML private CheckComboBox<String> tagsComboBox;
    private static Document selectedDocument;
    private static App app;
    private static javafx.collections.ObservableList<String> selectedTags;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the document name
        docName.setText(selectedDocument.getName());
        // Set the document description
        docDesc.setText(selectedDocument.getDescription());
        // Set the document path
        docPath.setText(String.valueOf(selectedDocument.getPath()));

        // Set the tags list
        for (Node t : archive.getTagTree().getNodes())
            tagsComboBox.getItems().add(t.getData().getName());

        // Set the selected tags
        for (Tag t : selectedDocument.getTags())
            tagsComboBox.getCheckModel().check(t.getName());

        // Add listener to the tags list
        tagsComboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<String>) c -> selectedTags = tagsComboBox.getCheckModel().getCheckedItems()
        );
    }

    /**
     * Save the changes made to the document
     */
    @FXML
    public void save() {
        archive.editDocument(selectedDocument, docName.getText(), docDesc.getText(), selectedTags);
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
     * Initialize the controller
     * @param document The document to edit
     * @param app The application
     * @param archive The archive
     */
    public static void init(Document document, App app, Archive archive) {
        EditDocumentController.selectedDocument = document;
        EditDocumentController.app = app;
        EditDocumentController.archive = archive;
    }
}
