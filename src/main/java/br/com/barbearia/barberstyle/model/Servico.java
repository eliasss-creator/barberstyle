/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

import java.math.BigDecimal;

/**
 *
 * @author FantasmaDeZion
 */
public class Servico {
    
    private int id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int duracaoMinutos;
    private boolean ativo;
    
    @Override
    public String toString() {
        return nome + " (R$ " + preco + " - " + duracaoMinutos + " min)";
    }
    public Servico(){
        
    }
    
    public Servico(int id, String nome, String descricao,
            BigDecimal preco, int duracaoMinutos,
            boolean ativo){
        
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMinutos = duracaoMinutos;
        this.ativo = ativo;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getNome(){
        return nome;
    }
    
    public void setNome(String nome){
        this.nome = nome;
    }
    
    public String getDescricao(){
        return descricao;
    }
    
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    
    public BigDecimal getPreco(){
        return preco;
    }
    
    public void setPreco(BigDecimal preco){
        this.preco = preco;
    }
    
    public int getDuracaoMinutos(){
        return duracaoMinutos;
    }
    
    public void setDuracaoMinutos(int duracaoMinutos){
        this.duracaoMinutos = duracaoMinutos;
    }
    
    public boolean isAtivo(){
        return ativo;
    }
    
    public void setAtivo(boolean ativo){
        this.ativo = ativo;
    }
}
