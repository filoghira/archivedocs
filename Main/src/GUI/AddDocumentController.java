package GUI;

import GhiraUtils.FileAlreadyInArchiveException;
import GhiraUtils.FileNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.shape.Arc;
import javafx.stage.FileChooser;

import java.io.File;
import Main.Archive;

public class AddDocumentController {

    private static Main mainApp;
    private static Archive archive;

    public void setArchive(Archive archive){
        this.archive = archive;
    }

    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
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
        mainApp.showFileOverview();
    }

}
