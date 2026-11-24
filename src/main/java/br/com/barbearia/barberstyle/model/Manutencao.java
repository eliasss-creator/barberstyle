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

public class Manutencao {
    
    private int id;
    private Equipamento equipamento;
    private Usuario usuario;
    private String descricao;
    private String status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataConclusao;
    
    public Manutencao(){

    }
    
    public Manutencao(int id, Equipamento equipamento, Usuario usuario, String
            descricao, String status, LocalDateTime dataAbertura, LocalDateTime
                    dataConclusao){
        this.id = id;
        this.equipamento = equipamento;
        this.usuario = usuario;
        this.descricao = descricao;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataConclusao = dataConclusao;
    }
    
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public Equipamento getEquipamento(){
        return equipamento;
    }
    
    public void setEquipamento(Equipamento equipamento){
        this.equipamento = equipamento;
    }
    
    
    public Usuario getUsuario(){
        return usuario;
    }
    
    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }
    
    public String getDescricao(){
        return descricao;
    }
    
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    
    
     public String getStatus(){
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public LocalDateTime getDataAbertura(){
        return dataAbertura;
    }
    
    public void setDataAbertura(LocalDateTime dataAbertura){
        this.dataAbertura = dataAbertura;
    }
    
    public LocalDateTime getDataConclusao(){
        return dataConclusao;
    }
    
    public void setDataConclusao(LocalDateTime dataConclusao){
        this.dataConclusao = dataConclusao;
    }
}