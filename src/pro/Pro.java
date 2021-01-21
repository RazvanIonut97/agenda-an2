package pro;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Pro extends Application {
    private static Pro instance;
    private Stage mainWindow;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.mainWindow = primaryStage;
        showMainFxml();
    }

    private void showMainFxml() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Scene scene = new Scene(root);
            mainWindow.setScene(scene);
            mainWindow.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Pro getInstance() {
        return instance;
    }

    public Stage getMainWindow() {
        return mainWindow;
    }
}
