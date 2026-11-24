/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

/**
 *
 * @author FantasmaDeZion
 */
public class Admin extends Usuario{
    public Admin(){
        
    }
    
    public Admin(int id, String nome, String email,
            String senhaHash, boolean ativo){
        super(id, nome, email, senhaHash, ativo);
    }
    
    @Override
    public String getPerfil(){
        return "ADMINISTRADOR";
    }
}
