/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.model;

/**
 *
 * @author FantasmaDeZion
 */
public abstract class Usuario {
    
    private int id;
    private String nome;
    private String email;
    private String senhaHash;
    private boolean ativo;
    
    public Usuario(){
        
    }
    
    public Usuario(int id, String nome, String email,
            String senhaHash, boolean ativo){
        
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.ativo = ativo;
        
    }
    
        public abstract String getPerfil();
        
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
        
        public String getEmail(){
            return email;
        }
        
        public void setEmail(String email){
            this.email = email;
        }
        
        public String getSenhaHash(){
            return senhaHash;
        }
        
        /**
         * Define o hash da semha do usuario.
         * @param senhaHash hash da senha que sera armazenado
         */
        public void setSenhaHash(String senhaHash){
            this.senhaHash = senhaHash;
        }
        
        public boolean isAtivo(){
            return ativo;
        }
        
        public void setAtivo(boolean ativo){
            this.ativo = ativo;
        }
    }

