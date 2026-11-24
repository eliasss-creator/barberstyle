/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.model.Barbeiro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author FantasmaDeZion
 */


public class BarbeiroDAO {

    public void inserir(Barbeiro barbeiro) throws SQLException {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha_hash, perfil, ativo) VALUES (?, ?, ?, 'BARBEIRO', ?)";
        String sqlBarbeiro = "INSERT INTO barbeiros (usuario_id, especialidade, descricao) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            int usuarioId = 0;
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, barbeiro.getNome());
                stmtUser.setString(2, barbeiro.getEmail());
                stmtUser.setString(3, barbeiro.getSenhaHash() != null ? barbeiro.getSenhaHash() : "123456");
                stmtUser.setBoolean(4, barbeiro.isAtivo());
                stmtUser.executeUpdate();

                try (ResultSet rs = stmtUser.getGeneratedKeys()) {
                    if (rs.next()) usuarioId = rs.getInt(1);
                }
            }

            try (PreparedStatement stmtBarb = conn.prepareStatement(sqlBarbeiro)) {
                stmtBarb.setInt(1, usuarioId);
                stmtBarb.setString(2, barbeiro.getEspecialidade());
                stmtBarb.setString(3, barbeiro.getDescricao());
                stmtBarb.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Barbeiro> listarTodos() throws SQLException {
        List<Barbeiro> lista = new ArrayList<>();
        String sql = "SELECT b.id AS barbeiro_id, u.nome, u.email, u.senha_hash, u.ativo, b.especialidade, b.descricao "
                   + "FROM barbeiros b INNER JOIN usuarios u ON b.usuario_id = u.id ORDER BY u.nome";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Barbeiro b = new Barbeiro(
                    rs.getInt("barbeiro_id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha_hash"),
                    rs.getBoolean("ativo"),
                    rs.getString("especialidade"),
                    rs.getString("descricao")
                );
                lista.add(b);
            }
        }
        return lista;
    }

    public void atualizar(Barbeiro barbeiro) throws SQLException {
        String sql = "UPDATE usuarios u INNER JOIN barbeiros b ON b.usuario_id = u.id "
                   + "SET u.nome = ?, u.email = ?, u.ativo = ?, b.especialidade = ?, b.descricao = ? WHERE b.id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barbeiro.getNome());
            stmt.setString(2, barbeiro.getEmail());
            stmt.setBoolean(3, barbeiro.isAtivo());
            stmt.setString(4, barbeiro.getEspecialidade());
            stmt.setString(5, barbeiro.getDescricao());
            stmt.setInt(6, barbeiro.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int barbeiroId) throws SQLException {
        String sqlBuscaUser = "SELECT usuario_id FROM barbeiros WHERE id = ?";
        String sqlDeleteUser = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.conectar()) {
            int usuarioId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscaUser)) {
                stmt.setInt(1, barbeiroId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) usuarioId = rs.getInt("usuario_id");
                }
            }
            if (usuarioId > 0) {
                try (PreparedStatement stmtDel = conn.prepareStatement(sqlDeleteUser)) {
                    stmtDel.setInt(1, usuarioId);
                    stmtDel.executeUpdate();
                }
            }
        }
    }
}