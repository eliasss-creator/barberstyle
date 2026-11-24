/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author FantasmaDeZion
 */
public class Agendamento {
    
    private int id;
    private Cliente cliente;
    private Barbeiro barbeiro;
    private Servico servico;
    private LocalDateTime dataHora;
    private BigDecimal precoContratado;
    private int duracaoContratada;
    private String status;
    
    public Agendamento(){

    }
    
    public Agendamento(int id, Cliente cliente, Barbeiro barbeiro,
            Servico servico, LocalDateTime dataHora, BigDecimal precoContratado,
            int duracaoContratada, String status){
        this.id = id;
        this.cliente = cliente;
        this.barbeiro = barbeiro;
        this.servico = servico;
        this.dataHora = dataHora;
        this.precoContratado = precoContratado;
        this.duracaoContratada = duracaoContratada;
        this.status = status;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public Cliente getCliente(){
        return cliente;
    }
    
    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }
    
    public Barbeiro getBarbeiro(){
        return barbeiro;
    }
    
    public void setBarbeiro(Barbeiro barbeiro){
        this.barbeiro = barbeiro;
    }
    
    public Servico getServico(){
        return servico;
    }
    
    public void setServico(Servico servico){
        this.servico = servico;
    }
    
    public LocalDateTime getDataHora(){
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora){
        this.dataHora = dataHora;
    }
    
    public BigDecimal getPrecoContratado(){
        return precoContratado;
    }
    
    public void setPrecoContratado(BigDecimal precoContratado){
        this.precoContratado = precoContratado;
    }
    
    public int getDuracaoContratada(){
        return duracaoContratada;    
    }
    
    public void setDuracaoContratada(int duracaoContratada){
        this.duracaoContratada = duracaoContratada;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
}
