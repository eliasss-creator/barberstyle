/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

/**
 *
 * @author FantasmaDeZion
 */
public class Barbeiro extends Usuario{
    
    private String especialidade;
    private String descricao;
    
    public Barbeiro(){
        
    }
    
    public Barbeiro(int id, String nome, String email,
            String senhaHash, boolean ativo, String especialidade,
            String descricao){
        
        super(id, nome, email, senhaHash, ativo);
        this.especialidade = especialidade;
        this.descricao = descricao;
    }
    
    @Override
    public String getPerfil(){
        return "BARBEIRO";
    }
    
    @Override
    public String toString() {
        return getNome() + " - " + especialidade;
    }
    
    public String getEspecialidade(){
        return especialidade;
    }
    
    public void setEspecialidade(String especialidade){
        this.especialidade = especialidade;
    }
    
    public String getDescricao(){
        return descricao;
    }
    
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
}
