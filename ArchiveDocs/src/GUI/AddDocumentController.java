package GUI;

import GhiraUtils.FileAlreadyInArchiveException;
import GhiraUtils.FileNotFoundException;
import Main.Archive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.net.URL;
import Main.Node;
import java.util.ResourceBundle;

public class AddDocumentController implements Initializable {

    @FXML private CheckComboBox<String> tagsComboBox;

    private static App appApp;
    private static Archive archive;



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
                archive.addDocument(selectedFile.getName(), selectedFile.toPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (FileAlreadyInArchiveException e) {
                System.out.println("File già presente nell'archivio");
            }
        }
    }

    @FXML
    private void goBack(){
        appApp.showFileOverview();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(tagsComboBox!=null){
            // Ottengo tutte le foglie dell'albero tags e le converto in stringhe per poi aggiungerle alla comboBox
            ObservableList<String> items = FXCollections.observableArrayList();
            for(Node n : archive.getTagTree().getLeaves())
                items.add(n.toString());

            tagsComboBox.getItems().addAll(items);
        }
    }
}
