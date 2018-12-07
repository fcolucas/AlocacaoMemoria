package alocacaodememoria;

import java.util.logging.Logger;

/**
 *
 * @author Samuel
 */
public class Frame {
    int posicaoInicio, posicaoFim, tamanhoDesenho, tamanhoFisico;
    boolean frameOcupado;

    public Frame(int posicaoInicio, int posicaoFim, int tamanho) {
        this.posicaoInicio = posicaoInicio;
        this.posicaoFim = posicaoFim;
        this.tamanhoDesenho = posicaoFim - posicaoInicio;
        this.tamanhoFisico = tamanho;
    }
    private static final Logger LOG = Logger.getLogger(Frame.class.getName());
    
    
    
    
}
