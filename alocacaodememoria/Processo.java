/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alocacaodememoria;

import javafx.scene.layout.StackPane;

public class Processo {
    int id, tamanho, instCriado, tempoDuracao, instAlocado, instConclusao;
    int posicaoInicio = 0, posicaoFim = 0, tamDesenho;
    int tempoEspera = (instConclusao - instCriado);
    float porcentagem = 0;
    String status;
    Frame frame;
    StackPane desenho;

    public Processo(int id, int tamanho, int criado, int duracao, int tamDesenho, float porcentagem, String status) {
        this.id = id;
        this.tamanho = tamanho;
        this.instCriado = criado;
        this.tempoDuracao = duracao;
        this.tamDesenho = tamDesenho;
        this.porcentagem = porcentagem;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getInstCriado() {
        return instCriado;
    }

    public void setInstCriado(int criado) {
        this.instCriado = criado;
    }

    public int getTempoDuracao() {
        return tempoDuracao;
    }

    public void setTempoDuracao(int duracao) {
        this.tempoDuracao = duracao;
    }

    public int getInstAlocado() {
        return instAlocado;
    }

    public void setInstAlocado(int alocado) {
        this.instAlocado = alocado;
    }

    public int getInstConclusao() {
        return instConclusao;
    }

    public void setInstConclusao(int conclusao) {
        this.instConclusao = conclusao;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public float getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(float porc) {
        this.porcentagem = porc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPosicaoInicio(int posInicio) {
        this.posicaoInicio = posInicio;
    }

    public void setPosicaoFim(int posFim) {
        this.posicaoFim = posFim;
    }
    
    
    
    
}
