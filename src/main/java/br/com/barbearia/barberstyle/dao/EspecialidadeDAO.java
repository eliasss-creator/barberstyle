/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.model.Especialidade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeDAO {

    public void inserir(Especialidade e) throws SQLException {
        String sql = "INSERT INTO especialidades (nome, descricao, ativo) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNome().trim());
            stmt.setString(2, e.getDescricao() != null ? e.getDescricao().trim() : "");
            stmt.setBoolean(3, e.isAtivo());
            stmt.executeUpdate();
        }
    }

    public List<Especialidade> listarTodas() throws SQLException {
        List<Especialidade> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, ativo FROM especialidades ORDER BY nome ASC";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Especialidade(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getBoolean("ativo")
                ));
            }
        }
        return lista;
    }

    public void atualizar(Especialidade e) throws SQLException {
        String sql = "UPDATE especialidades SET nome = ?, descricao = ?, ativo = ? WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNome().trim());
            stmt.setString(2, e.getDescricao() != null ? e.getDescricao().trim() : "");
            stmt.setBoolean(3, e.isAtivo());
            stmt.setInt(4, e.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM especialidades WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}