package com.petcemetery.petcemetery.model;

import com.petcemetery.petcemetery.model.Jazigo.PlanoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Transient;

@Entity(name = "Servico")
@Table(name = "Servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Adicionado para permitir auto incremento do id
    @Column(name = "id_servico")
    private int idServico;

    @Enumerated(EnumType.STRING)
    private ServicoEnum servico;

    @ManyToOne
    @JoinColumn(name = "carrinho_cpf_cliente")
    private Carrinho carrinho;

    @OneToOne
    @JoinColumn(name = "jazigo_id_jazigo")
    private Jazigo jazigo;

    @Column(name = "valor")
    private double valor;

    @Column(name = "cliente")
    private Cliente cliente;

    @Column(name = "plano")
    private PlanoEnum plano;

    @Transient
    private static double manutencao = 1300.0; 

    @Transient 
    private static double exumacao = 200.0;

    @Transient 
    private static double enterro = 400.0;

    public enum ServicoEnum {
        COMPRA,
        ALUGUEL,
        PERSONALIZACAO,
        MANUTENCAO,
        EXUMACAO,
        ENTERRO;
        public double getPreco() {
            switch (this) {
                case COMPRA:
                    return Jazigo.precoJazigo;
                case ALUGUEL:
                    return Jazigo.aluguelJazigo;
                case PERSONALIZACAO:
                    PlanoEnum[] valores = PlanoEnum.values();
                    
                    for (PlanoEnum tipo : valores) {
                        switch(tipo) {
                            case BASIC:
                                return PlanoEnum.BASIC.getPreco();
                            case SILVER:
                                return PlanoEnum.SILVER.getPreco();
                            case GOLD:
                                return PlanoEnum.GOLD.getPreco();
                        }
                    }
                 
                case MANUTENCAO:
                    return manutencao;
                case EXUMACAO:
                    return exumacao;
                case ENTERRO:
                    return enterro;
                default:
                    return 0.0;
            }
        }
    }
    

    public Servico(ServicoEnum servico, double valor, Cliente cliente, Jazigo jazigo, PlanoEnum plano) {
        this.servico = servico;
        this.valor = valor + plano.getPreco(); //* O valor do servico vai englobar o valor do jazigo + o valor do seu plano (caso seja compra ou aluguel, senão esses valores serão null) */
        this.cliente = cliente;
        this.jazigo = jazigo;
        this.plano = plano;
    }

    public PlanoEnum getPlano(){
        return plano;
    }

    public int getIdServico() {
        return idServico;
    }

    public ServicoEnum getServico() {
        return servico;
    }

    public void setServico(ServicoEnum servico) {
        this.servico = servico;
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public Jazigo getJazigo() {
        return jazigo;
    }

    public void setJazigo(Jazigo jazigo) {
        this.jazigo = jazigo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}
