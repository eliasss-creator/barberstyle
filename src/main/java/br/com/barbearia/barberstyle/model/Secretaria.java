/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package br.com.barbearia.barberstyle.model;

/**
 * Representa o perfil de Secretaria / Recepcao da barbearia.
 * @author FantasmaDeZion
 */
public class Secretaria extends Usuario {

    public Secretaria() {
    }

    public Secretaria(int id, String nome, String email, String senhaHash, boolean ativo) {
        super(id, nome, email, senhaHash, ativo);
    }

    @Override
    public String getPerfil() {
        return "SECRETARIA";
    }
}