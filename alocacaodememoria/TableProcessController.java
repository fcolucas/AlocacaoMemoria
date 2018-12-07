package alocacaodememoria;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableProcessController implements Initializable {

    @FXML
    private TableView<Processo> tabProcessos;

    @FXML
    private TableColumn<Processo, Integer> colId;

    @FXML
    private TableColumn<Processo, Integer> colTamanho;

    @FXML
    private TableColumn<Processo, Float> colPorcent;

    @FXML
    private TableColumn<Processo, Integer> colDuracao;

    @FXML
    private TableColumn<Processo, Integer> colInstCriado;

    @FXML
    private TableColumn<Processo, Integer> colInstAlocado;

    @FXML
    private TableColumn<Processo, Integer> colInstConcluido;

    @FXML
    private TableColumn<Processo, Integer> colTEspera;

    @FXML
    private TableColumn<Processo, String> colStatus;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(
            new PropertyValueFactory<>("id"));
        colTamanho.setCellValueFactory(
            new PropertyValueFactory<>("tamanho"));
        colStatus.setCellValueFactory(
            new PropertyValueFactory<>("status"));
        colPorcent.setCellValueFactory(
            new PropertyValueFactory<>("porcentagem"));
        colInstCriado.setCellValueFactory(
            new PropertyValueFactory<>("instCriado"));
        colInstAlocado.setCellValueFactory(
            new PropertyValueFactory<>("instAlocado"));
        colInstConcluido.setCellValueFactory(
            new PropertyValueFactory<>("instConclusao"));
        colDuracao.setCellValueFactory(
            new PropertyValueFactory<>("tempoDuracao"));
        colTEspera.setCellValueFactory(
            new PropertyValueFactory<>("tempoEspera"));
        
        tabProcessos.setItems(FXMLDocumentController.procCriados);
        
        refresh_table();
    }
    
    public void refresh_table(){
        Timer tempo = new Timer();
            tempo.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    tabProcessos.refresh();
                }
            },1000, 1000);
    }
}
