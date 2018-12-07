package alocacaodememoria;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AlocacaoDeMemoria extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Alocação do Leão");
        stage.setX(100);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
