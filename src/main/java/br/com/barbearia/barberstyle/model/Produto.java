/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;



/**
 *
 * @author FantasmaDeZion
 */


public class Produto {
    
    private int id;
    private String nome;
    private int quantidade;
    private int estoqueMinimo;
    private String unidade;
    private boolean ativo;
    
    public Produto(){
    
    }
   
    
    public Produto(int id, String nome, int quantidade, 
            int estoqueMinimo, String unidade, boolean ativo){
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
        this.unidade = unidade;
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
    
    public int getQuantidade(){
        return quantidade;
    }
    
    public void setQuantidade(int quantidade){
        this.quantidade = quantidade;
    }
    
    public int getEstoqueMinimo(){
        return estoqueMinimo;
    }
    
    public void setEstoqueMinimo(int estoqueMinimo){
        this.estoqueMinimo = estoqueMinimo;
    }
    
    public String getUnidade(){
        return unidade;
    }
    
    public void setUnidade(String unidade){
        this.unidade = unidade;
    }
    
    public boolean isAtivo(){ // convenção especial para getter de atributos booleanos
        return ativo;
    }
     
    public void setAtivo(boolean ativo){
        this.ativo = ativo;
     }
    
}

