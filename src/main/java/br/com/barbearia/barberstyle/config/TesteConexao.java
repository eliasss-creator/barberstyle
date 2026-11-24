/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.config;

import java.sql.Connection;
import java.sql.SQLException;


/**
 *
 * @author FantasmaDeZion
 */
public class TesteConexao {
    
    public static void main(String[] args){
        
        try (Connection conexao = Conexao.conectar()){
            if (conexao != null && !conexao.isClosed()){
                System.out.println("Conexão realizada com sucesso! ");
                System.out.println("Teste:a e");
                System.out.println("\u00E3");
                System.out.println("CODIGO UNICODE " +(int)'\u00E3');
            }
        }catch(SQLException e){
            
            System.out.println("Erro ao conectar ao MySQL:");
            e.printStackTrace();
        }
    }
    
}
