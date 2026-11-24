/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.model.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FantasmaDeZion
 */

public class ServicoDAO {

    public void inserir(Servico servico) throws SQLException {
        String sql = "INSERT INTO servicos (nome, descricao, preco, duracao_minutos, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setBigDecimal(3, servico.getPreco());
            stmt.setInt(4, servico.getDuracaoMinutos());
            stmt.setBoolean(5, servico.isAtivo());
            stmt.executeUpdate();
        }
    }

    public List<Servico> listarTodos() throws SQLException {
        List<Servico> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, duracao_minutos, ativo FROM servicos ORDER BY nome";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servico s = new Servico(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getBigDecimal("preco"),
                    rs.getInt("duracao_minutos"),
                    rs.getBoolean("ativo")
                );
                lista.add(s);
            }
        }
        return lista;
    }

    public void atualizar(Servico servico) throws SQLException {
        String sql = "UPDATE servicos SET nome = ?, descricao = ?, preco = ?, duracao_minutos = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setBigDecimal(3, servico.getPreco());
            stmt.setInt(4, servico.getDuracaoMinutos());
            stmt.setBoolean(5, servico.isAtivo());
            stmt.setInt(6, servico.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servicos WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}