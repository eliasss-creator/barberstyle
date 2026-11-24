/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

import java.time.LocalDateTime;

/**
 *
 * @author FantasmaDeZion
 */


public class Avaliacao {
    
    private int id;
    private Agendamento agendamento;
    private int estrelas;
    private String comentario;
    private LocalDateTime dataAvaliacao;
    
    public Avaliacao(){
    
    }
    
    public Avaliacao(int id, Agendamento agendamento, int estrelas, 
            String comentario, LocalDateTime dataAvaliacao){
        this.id = id;
        this.agendamento = agendamento;
        this.estrelas = estrelas;
        this.comentario = comentario;
        this.dataAvaliacao = dataAvaliacao;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public Agendamento getAgendamento(){
        return agendamento;
    }
    
    public void setAgendamento(Agendamento agendamento){
        this.agendamento = agendamento;
    }
    
    public int getEstrelas(){
        return estrelas;
    }
    
    public void setEstrelas(int estrelas){
        if(estrelas < 1 || estrelas > 5){
            throw new IllegalArgumentException("A avaliação deve ser de 1 a 5 "
                    + "estrelas.");
        }
        
        this.estrelas = estrelas;
    }
    
    public String getComentario(){
        return comentario;
    }
    
    public void setComentario(String comentario){
        this.comentario = comentario;
    }
    
    public LocalDateTime getDataAvaliacao(){
        return dataAvaliacao;
    }
    
    public void setDataAvaliacao(LocalDateTime dataAvaliacao){
        this.dataAvaliacao = dataAvaliacao;
    }
    
}
