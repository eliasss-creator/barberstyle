/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

/**
 *
 * @author FantasmaDeZion
 */
public class Cliente extends Usuario{
    
    private String telefone;
    
    public Cliente(){
        
    }
    
    public Cliente(int id, String nome, String email,
            String senhaHash, boolean ativo,
            String telefone){
        
        super(id, nome, email, senhaHash, ativo);
        this.telefone = telefone;
    }
    
    
    @Override
    public String toString() {
        return getNome() + " (" + telefone + ")";
    }
    
    @Override
    public String getPerfil(){
        return "CLIENTE";
    }
    
    public String getTelefone(){
        return telefone;
    }
    
    public void setTelefone(String telefone){
        this.telefone = telefone;
    }
}
