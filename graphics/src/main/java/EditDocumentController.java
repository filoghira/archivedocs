import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
        docName.setText(selectedDocument.getName());
        docDesc.setText(selectedDocument.getDescription());
        docPath.setText(String.valueOf(selectedDocument.getPath()));

        for (Tag t : selectedDocument.getTags()) {
            tagsComboBox.getItems().add(t.getName());
            tagsComboBox.getCheckModel().check(t.getName());
        }

        tagsComboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<String>) c -> selectedTags = tagsComboBox.getCheckModel().getCheckedItems()
        );
    }

    @FXML
    public void save() {
        archive.editDocument(
                selectedDocument,
                docName.getText(),
                docDesc.getText(),
                new ArrayList<>(selectedTags)
        );
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

    public static void init(Document document, App app, Archive archive) {
        EditDocumentController.selectedDocument = document;
        EditDocumentController.app = app;
        EditDocumentController.archive = archive;
    }
}
