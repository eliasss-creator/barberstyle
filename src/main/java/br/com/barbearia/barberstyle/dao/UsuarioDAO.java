/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.config.SegurancaUtil;
import br.com.barbearia.barberstyle.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean isSenhaPadraoInsegura(String senhaDigitada) {
        if (senhaDigitada == null) return false;
        String s = senhaDigitada.trim();
        return s.equals("123456") || s.equalsIgnoreCase("admin123") || s.equalsIgnoreCase("admin");
    }

    public Usuario autenticar(String email, String senhaDigitada) throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash, ativo, perfil "
                   + "FROM usuarios WHERE email = ? AND senha_hash = ? AND ativo = TRUE";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim().toLowerCase());
            stmt.setString(2, SegurancaUtil.gerarHashSenha(senhaDigitada));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int usuarioId = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String mail = rs.getString("email");
                    String hash = rs.getString("senha_hash");
                    boolean ativo = rs.getBoolean("ativo");
                    String perfil = rs.getString("perfil");

                    return switch (perfil) {
                        case "ADMINISTRADOR" -> new Admin(usuarioId, nome, mail, hash, ativo);
                        case "SECRETARIA" -> new Secretaria(usuarioId, nome, mail, hash, ativo);
                        case "BARBEIRO" -> buscarDadosBarbeiro(conn, usuarioId, nome, mail, hash, ativo);
                        default -> buscarDadosCliente(conn, usuarioId, nome, mail, hash, ativo);
                    };
                }
            }
        }
        return null;
    }

    public void alterarSenhaUsuario(int usuarioId, String novaSenhaPura) throws SQLException {
        String sql = "UPDATE usuarios SET senha_hash = ? WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, SegurancaUtil.gerarHashSenha(novaSenhaPura.trim()));
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        }
    }

    public void inserirUsuarioEquipe(String nome, String email, String senhaPura, String perfil, boolean ativo, String especialidade) throws SQLException {
        String sqlUser = "INSERT INTO usuarios (nome, email, senha_hash, perfil, ativo) VALUES (?, ?, ?, ?, ?)";
        String sqlBarb = "INSERT INTO barbeiros (usuario_id, especialidade, descricao) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            int usuarioId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nome.trim());
                stmt.setString(2, email.trim().toLowerCase());
                stmt.setString(3, SegurancaUtil.gerarHashSenha(senhaPura));
                stmt.setString(4, perfil);
                stmt.setBoolean(5, ativo);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) usuarioId = rs.getInt(1);
                }
            }

            if ("BARBEIRO".equals(perfil) && usuarioId > 0) {
                try (PreparedStatement stmtB = conn.prepareStatement(sqlBarb)) {
                    stmtB.setInt(1, usuarioId);
                    stmtB.setString(2, (especialidade != null && !especialidade.isBlank()) ? especialidade.trim() : "Corte e Barba");
                    stmtB.setString(3, "Profissional da equipe BarberStyle");
                    stmtB.executeUpdate();
                }
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

    public List<Object[]> listarEquipeInterna() throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id, nome, email, perfil, ativo FROM usuarios "
                   + "WHERE perfil IN ('ADMINISTRADOR', 'SECRETARIA', 'BARBEIRO') ORDER BY id ASC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("perfil"),
                    rs.getBoolean("ativo") ? "Ativo" : "Inativo"
                });
            }
        }
        return lista;
    }

    public void atualizarUsuarioEquipe(int id, String nome, String email, String perfil, boolean ativo, String novaSenhaOpcional) throws SQLException {
        boolean alteraSenha = (novaSenhaOpcional != null && !novaSenhaOpcional.trim().isEmpty());
        String sql = alteraSenha
                ? "UPDATE usuarios SET nome = ?, email = ?, perfil = ?, ativo = ?, senha_hash = ? WHERE id = ?"
                : "UPDATE usuarios SET nome = ?, email = ?, perfil = ?, ativo = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            stmt.setString(2, email.trim().toLowerCase());
            stmt.setString(3, perfil);
            stmt.setBoolean(4, ativo);
            if (alteraSenha) {
                stmt.setString(5, SegurancaUtil.gerarHashSenha(novaSenhaOpcional.trim()));
                stmt.setInt(6, id);
            } else {
                stmt.setInt(5, id);
            }
            stmt.executeUpdate();
        }
    }

    public void excluirUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Barbeiro buscarDadosBarbeiro(Connection conn, int usuarioId, String nome, String email, String hash, boolean ativo) throws SQLException {
        String sql = "SELECT id, especialidade, descricao FROM barbeiros WHERE usuario_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Barbeiro(
                        rs.getInt("id"),
                        nome, email, hash, ativo,
                        rs.getString("especialidade"),
                        rs.getString("descricao")
                    );
                }
            }
        }
        return new Barbeiro(usuarioId, nome, email, hash, ativo, "Geral", "");
    }

    private Cliente buscarDadosCliente(Connection conn, int usuarioId, String nome, String email, String hash, boolean ativo) throws SQLException {
        String sql = "SELECT id, telefone FROM clientes WHERE usuario_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(rs.getInt("id"), nome, email, hash, ativo, rs.getString("telefone"));
                }
            }
        }
        return new Cliente(usuarioId, nome, email, hash, ativo, "");
    }
}