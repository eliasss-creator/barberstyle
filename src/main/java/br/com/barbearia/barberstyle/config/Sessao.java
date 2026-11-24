/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.config;

import br.com.barbearia.barberstyle.model.Usuario;

/**
 *
 * @author FantasmaDeZion
 */
public class Sessao {
    
    private static Usuario usuarioLogado;
    
    public static void iniciar(Usuario usuario){
        usuarioLogado = usuario;
    }
    
    public static Usuario getUsuarioLogado(){
        return usuarioLogado;
    }
    
    public static boolean exiteUsuarioLogado(){
         return usuarioLogado != null;
    }
    
    public static void encerrar(){
        usuarioLogado = null;
    }
    
     
}
