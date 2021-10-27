package GUI;

import GhiraUtils.FileAlreadyInArchiveException;
import GhiraUtils.FileNotFoundException;
import Main.Archive;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;

public class AddDocumentController {

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

}
