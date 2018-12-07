
package alocacaodememoria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Pane paneMem;
        
    @FXML
    private TextField tamMem;

    @FXML
    private TextField tamSO;

    @FXML
    private TextField tamProc1;

    @FXML
    private TextField tamProc2;

    @FXML
    private TextField tCriacao1;

    @FXML
    private TextField tCriacao2;

    @FXML
    private TextField tDuracao1;

    @FXML
    private TextField tDuracao2;
    
    @FXML
    private TextArea txtLog;

    @FXML
    private Button iniciarSimulacao;

    @FXML
    private Label instante;

    @FXML
    private Label media;

    @FXML
    private Label cpu;

    @FXML
    private ImageView imgMemoria;

    @FXML
    private TextField qteProcessos;

    @FXML
    private RadioButton firstFit;

    @FXML
    private RadioButton bestFit;

    @FXML
    private RadioButton worstFit;
    
    ObservableList<Frame> framesLivres = FXCollections.observableArrayList();
    ObservableList<Frame> framesOcupados = FXCollections.observableArrayList();
    ObservableList<Processo> processos = FXCollections.observableArrayList();
    static ObservableList<Processo> procCriados = FXCollections.observableArrayList();
    ObservableList<Processo> procAlocados = FXCollections.observableArrayList();
    ObservableList<Processo> procFinalizados = FXCollections.observableArrayList();

    ToggleGroup metodos = new ToggleGroup();
    int totMemoria, tam_SO, m1, m2, criac1, criac2, dur1, dur2, qtdProc;
    float memCPU = 0;
    Processo sistOp;
    String metodo;
    Timer tempo = new Timer();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bestFit.setToggleGroup(metodos);
        firstFit.setToggleGroup(metodos);
        worstFit.setToggleGroup(metodos);
    }
    
    public void logMsg(String msg){
        Platform.runLater(() -> {
            txtLog.appendText("\n["+LocalDateTime.now().getHour()+":" + LocalDateTime.now().getMinute()+":"
            + LocalDateTime.now().getSecond()+"] - " + msg);
        });
    }
    
    public void calculaMedia(){
        int soma = 0;
        for(Processo proc: procFinalizados){
            soma += proc.tempoEspera;
        }
        float m = soma/qtdProc;
        media.setText(Float.toString(m));
        iniciarSimulacao.setDisable(false);
    }
    
    public void atualizaCPU(){
        cpu.setText(Float.toString(memCPU));
    }
    
    public void desenhaSO(){
        sistOp.posicaoFim = (650 * tam_SO) / totMemoria;
        StackPane paneSO;
        paneSO = new StackPane();
        paneSO.setStyle("-fx-background-color: #ff0000; -fx-border-color: black;");
        paneSO.setMinHeight(137);
        paneSO.setMinWidth(sistOp.posicaoFim);
        paneSO.setOpacity(0.7);
        paneSO.setLayoutY(0.0);
        Text id = new Text("SO");
        id.setFont(Font.font("Comic Sans MS", sistOp.tamDesenho/2));
        id.setFill(Color.WHITE);
        paneSO.getChildren().add(id);
        
        Frame root = framesLivres.get(0);
        Frame SO = new Frame(0, sistOp.posicaoFim, tam_SO);
        Frame resto = new Frame(sistOp.posicaoFim, root.posicaoFim, totMemoria-tam_SO);
        framesOcupados.add(SO);
        framesLivres.set(0,resto);
        sistOp.frame = SO;
        sistOp.desenho = paneSO;
        Platform.runLater(()->{
            paneMem.getChildren().add(paneSO);
            paneSO.relocate(sistOp.posicaoInicio, 0);
        });
    }
    
    public void desenhaProcesso(Processo p){
        StackPane paneProc;
        paneProc = new StackPane();
        paneProc.setStyle("-fx-background-color: #0000ff; -fx-border-color:black");
        paneProc.setMinHeight(137);
        paneProc.setMinWidth(p.tamDesenho);
        paneProc.setOpacity(0.7);
        paneProc.setLayoutY(0.0);
        Text id = new Text(Integer.toString(p.id));
        id.setFont(Font.font("Comic Sans MS", p.tamDesenho/2));
        id.setFill(Color.WHITE);
        paneProc.getChildren().add(id);
        
        p.desenho = paneProc;
        Platform.runLater(()->{
            paneMem.getChildren().add(paneProc);
            paneProc.relocate(p.posicaoInicio, 0);
        });
    }
    
    public Frame getMaisPerto(List<Frame> lista){
        Frame menor = lista.get(0);
        for(int i=0; i<framesLivres.size();i++){
            Frame frame = framesLivres.get(i);
            if(frame.posicaoInicio <= menor.posicaoInicio){
                menor = frame;
            }
        }
        return menor;
    }
    
    public Frame getMenorTam(Frame f, Processo p){ //Captura o frame de menor tamanho - método Best Fit
        Frame menor = f;
        for (Frame frame : framesLivres) {
            if(frame.tamanhoDesenho < menor.tamanhoDesenho && frame.tamanhoDesenho >= p.tamDesenho){
                menor = frame;
            }
        }
        return menor;
    }  
    
    public void ordenaFrames(){
        ObservableList<Frame> result = FXCollections.observableArrayList();
        while( !framesLivres.isEmpty() ){
            Frame menor = getMaisPerto(framesLivres);
            framesLivres.remove(menor);
            result.add(menor);
        }
        framesLivres = result;
    }
    
    public void juntaRestos(){
        for(int i=1; i<framesLivres.size(); i++){
            Frame anterior = framesLivres.get(i-1);
            Frame atual = framesLivres.get(i);
            if(anterior.posicaoFim == atual.posicaoInicio){
                Frame novo = new Frame(anterior.posicaoInicio, atual.posicaoFim, anterior.tamanhoFisico+atual.tamanhoFisico);
                framesLivres.remove(atual);
                framesLivres.set(framesLivres.indexOf(anterior), novo);
                i--;
            }
        }
    }
    
    public void alocaProcesso(Processo p, int t){
        tempo.cancel();
        switch(metodo){
            case "First Fit":
                firstFit(p, t);
                break;
            case "Best Fit":
                bestFit(p, t);
                break;
            case "Worst Fit":
                worstFit(p, t);
                break;
        }
        tempo=new Timer();
        tempo.scheduleAtFixedRate( new TimerTask(){
            @Override
            public void run(){
                rotina();
            }
        },1000, 1000);
    }   
    
    public void desalocaProcesso(Processo p, int t){
        tempo.cancel();
        try{
            p.status = "Finalizado";
            p.desenho.setVisible(false);
            paneMem.getChildren().remove(p.desenho);
            p.desenho = null;
            p.instConclusao = t;
            p.tempoEspera = p.instConclusao - p.instCriado;
            procFinalizados.add(p);
            procAlocados.remove(p);
            framesOcupados.remove(p.frame);
            framesLivres.add(p.frame);
            ordenaFrames();
            juntaRestos();
            memCPU -= p.porcentagem;
            atualizaCPU();
        }
        catch(Exception e){
            desalocaProcesso(p, t);
        }
        
        tempo = new Timer();
        tempo.scheduleAtFixedRate( new TimerTask(){
            @Override
            public void run(){
                rotina();
            }
        },1000, 1000);
    }
    
     public void gerarProcessos(){
        int anterior = 0;
        for(int i=0; i<qtdProc; i++){
            String status = "Fila";
            int tamFisico = (int)(Math.random() * (m2-m1) + m1);
            int tc = (int)((Math.random() * (criac2-criac1)) + criac1 ) + anterior;
            int td = (int)(Math.random() * (dur2-dur1)) + dur1;
            int tamDesen =(650 * tamFisico) / totMemoria;
            float porc = 100*((float)tamFisico/(float)totMemoria);
            
            Processo p = new Processo(i, tamFisico, tc, td, tamDesen, porc, status);
            processos.add(p);
            logMsg(" ID: "+p.id+" Criação: "+p.instCriado);
            anterior = tc;
        }
    }
    
    public void firstFit(Processo p, int t){
        int i = 0, j = framesLivres.size();
        while(i < j){
            Frame frame = framesLivres.get(i);
            if(frame.tamanhoFisico > p.tamanho){
                p.tamDesenho = (frame.tamanhoDesenho*p.tamanho)/frame.tamanhoFisico;
                int indice = framesLivres.indexOf(frame);
                Frame alocado = new Frame(frame.posicaoInicio, frame.posicaoInicio + p.tamDesenho, p.tamanho);
                Frame resto = new Frame(alocado.posicaoFim, frame.posicaoFim, frame.tamanhoFisico-p.tamanho);
                p.frame = alocado;
                framesOcupados.add(alocado);
                framesLivres.set(indice, resto);
                
                p.posicaoInicio = alocado.posicaoInicio;
                p.posicaoFim = alocado.posicaoFim;
                i = j;
            }
            else if(frame.tamanhoFisico == p.tamanho){
                framesOcupados.add(frame);
                framesLivres.remove(frame);
                p.posicaoInicio = frame.posicaoInicio;
                p.posicaoFim = frame.posicaoFim;
                i = j;
            }
            p.instAlocado = t;
            p.status = "Executando";
            procAlocados.add(p);

            desenhaProcesso(p);
            memCPU += p.porcentagem;
            atualizaCPU();
            i++;
        }
    }
    
    public void bestFit(Processo p, int t){
        Frame best;
        int i = 0, j = framesLivres.size();
        while(i < j) {
            Frame f = framesLivres.get(i);
            if (f.tamanhoDesenho >= p.tamDesenho) {
                best = f;
                best = getMenorTam(best, p);
                if (best.tamanhoFisico > p.tamanho) {
                    p.tamDesenho = (best.tamanhoDesenho * p.tamanho) / best.tamanhoFisico;
                    int indice = framesLivres.indexOf(best);
                    Frame alocado = new Frame(best.posicaoInicio, best.posicaoInicio + p.tamDesenho, p.tamanho);
                    Frame resto = new Frame(alocado.posicaoFim, best.posicaoFim, best.tamanhoFisico - p.tamanho);
                    p.frame = alocado;
                    framesOcupados.add(alocado);
                    framesLivres.set(indice, resto);

                    p.posicaoInicio = alocado.posicaoInicio;
                    p.posicaoFim = alocado.posicaoFim;
                    p.instAlocado = t;
                    p.status = "Executando";
                    procAlocados.add(p);

                    desenhaProcesso(p);
                    memCPU += p.porcentagem;
                    atualizaCPU();

                    
                } else if (best.tamanhoFisico == p.tamanho) {
                    framesOcupados.add(best);
                    framesLivres.remove(best);
                    p.posicaoInicio = best.posicaoInicio;
                    p.posicaoFim = best.posicaoFim;
                    p.instAlocado = t;
                    p.status = "Executando";
                    procAlocados.add(p);

                    desenhaProcesso(p);
                    memCPU += p.porcentagem;
                    atualizaCPU();
                    i = j;
                }
                i = j;
            }
            i++;
        }
    }
    
    public void worstFit(Processo p, int t){
        Frame maior = null;
        boolean igual = false;
        int i = 0;
        int j = framesLivres.size();
        while(i < j){
            Frame f = framesLivres.get(i);
            if(f.tamanhoDesenho >= p.tamDesenho){
                maior = f;
                i = j;
            }
            i++;
        }
        if(maior != null){
            i = 0;
            j = framesLivres.size();
            while(i < j){
                Frame f = framesLivres.get(i);
                p.tamDesenho = (f.tamanhoDesenho * p.tamanho) / f.tamanhoFisico;
                if(f.tamanhoDesenho > maior.tamanhoDesenho && f.tamanhoDesenho > p.tamDesenho){
                    maior = f;
                }
                else if(f.tamanhoDesenho >= maior.tamanhoDesenho && f.tamanhoDesenho == p.tamDesenho){
                    maior = f;
                    igual = true;
                    i = j;
                }
                i++;
            }
            if(!igual){
                int indice = framesLivres.indexOf(maior);
                Frame alocado = new Frame(maior.posicaoInicio, maior.posicaoInicio+p.tamDesenho, p.tamanho);
                Frame resto = new Frame(alocado.posicaoFim, maior.posicaoFim, maior.tamanhoFisico-p.tamanho);
                p.frame = alocado;
                framesOcupados.add(alocado);
                framesLivres.set(indice, resto);
                p.posicaoInicio = alocado.posicaoInicio;
                p.posicaoFim = alocado.posicaoFim;
                p.instAlocado = t;
                p.status = "Executando";
                procAlocados.add(p);
                
                desenhaProcesso(p);
                memCPU += p.porcentagem;
                atualizaCPU();
            }
            else{
                framesOcupados.add(maior);
                framesLivres.remove(maior);
                p.posicaoInicio = maior.posicaoInicio;
                p.posicaoFim = maior.posicaoFim;
                p.instAlocado = t;
                p.status = "Executando";
                procAlocados.add(p);
                
                desenhaProcesso(p);
                memCPU += p.porcentagem;
                atualizaCPU();
            }
        }   
    }
    
    public void rotina(){
        Platform.runLater(() -> {
            int instAtual = Integer.parseInt(instante.getText()) + 1;
            instante.setText(Integer.toString(instAtual));
            processos.forEach(p -> {
                if (p.instCriado <= instAtual) {
                    procCriados.add(p);
                    Platform.runLater(() -> {
                        processos.remove(p);
                    });
                }
            });
            procCriados.forEach(p -> {
                if (p.instAlocado <= instAtual && p.status.equals("Fila")) {
                    alocaProcesso(p, instAtual);
                    logMsg("Processo ID " + p.id + " alocado");
                }
            });
            procAlocados.forEach(p -> {
                if ( p.status.equals("Executando") && (p.tempoDuracao + p.instAlocado) <= instAtual) {
                    Platform.runLater(()->{
                        desalocaProcesso(p, instAtual);
                        logMsg("Processo ID " + p.id + " desalocado");
                    });
                }
            });
            if(procAlocados.isEmpty() && processos.isEmpty()){
                tempo.cancel();
                calculaMedia();
            }
        });
    }
    
    void render_process_table() throws IOException {
        Parent root;
        root = FXMLLoader.load(getClass().getResource("TableProcess.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Tabela de processos");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 684, 539));
        stage.setX(500);
        stage.show();
    }
    
    void clean_stage(){
        txtLog.clear();
        media.setText("0");
        memCPU = 0;
        atualizaCPU();
        processos.clear();
        procCriados.clear();
        procAlocados.clear();
        procFinalizados.clear();
        framesLivres.clear();
        framesOcupados.clear();
        framesLivres.add(new Frame(0,650, totMemoria));
    }
    
    @FXML
    public int iniciar() throws IOException {
        try{
            qtdProc = Integer.parseInt(qteProcessos.getText());
            totMemoria = Integer.parseInt(tamMem.getText());
            tam_SO = Integer.parseInt(tamSO.getText());
            m1 = Integer.parseInt(tamProc1.getText());
            m2 = Integer.parseInt(tamProc2.getText());
            criac1 = Integer.parseInt(tCriacao1.getText());
            criac2 = Integer.parseInt(tCriacao2.getText());
            dur1 = Integer.parseInt(tDuracao1.getText());
            dur2 = Integer.parseInt(tDuracao2.getText());
            RadioButton selected = (RadioButton) metodos.getSelectedToggle();
            metodo = selected.getText();
        }
        catch(Exception err){
            logMsg("Preencha todos os campos e apenas com números!");
            return -1;
        }
        if(tam_SO >= totMemoria){
            logMsg("SO deve ser menor que a memória");
            return -1;
        }else if(m2 > totMemoria){
            logMsg("M2 não pode ser maior que a memória");
            return -1;
        }else if(qtdProc > 30){
            logMsg("Máximo de processos é 30");
            return -1;
        }else if(m1 >= m2 || criac1 >= criac2 || dur1 >= dur2){
            logMsg("Valores inválidos. O 1º valor deve ser menor que o 2º valor");
            return -1;
        }else{
            render_process_table();
            clean_stage();
            iniciarSimulacao.setDisable(true);
            logMsg(metodo);
            int tamDesenho = (650 * tam_SO)/totMemoria;
            float porc = ((float)tam_SO/(float)totMemoria) * 100;
            if(sistOp != null){
                sistOp.desenho.setVisible(false);
                paneMem.getChildren().remove(sistOp.desenho);
            }
            sistOp = new Processo(99, tam_SO, 0, 0, tamDesenho, porc, "infinito");
            desenhaSO();
            memCPU += sistOp.porcentagem;
            atualizaCPU();
            gerarProcessos();
            instante.setText(Integer.toString(0));
            tempo = new Timer();
            tempo.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    rotina();
                }
            },1000, 1000);
        }
        return 0;
    }
}
