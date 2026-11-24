/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.config.SegurancaUtil;
import br.com.barbearia.barberstyle.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsavel pelas operacoes de banco de dados da entidade Cliente.
 * @author FantasmaDeZion
 */
public class ClienteDAO {

    /**
     * Insere um novo usuario e seu respectivo cliente no banco de dados com senha criptografada (SHA-256).
     */
    public void inserir(Cliente cliente) throws SQLException {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha_hash, perfil, ativo) VALUES (?, ?, ?, 'CLIENTE', ?)";
        String sqlCliente = "INSERT INTO clientes (usuario_id, telefone) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            String hashFinal = (cliente.getSenhaHash() != null && cliente.getSenhaHash().length() == 64)
                    ? cliente.getSenhaHash()
                    : SegurancaUtil.gerarHashSenha(
                        (cliente.getSenhaHash() != null && !cliente.getSenhaHash().isBlank())
                            ? cliente.getSenhaHash()
                            : SegurancaUtil.gerarSenhaAleatoria(8)
                    );

            int usuarioId = 0;
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, cliente.getNome().trim());
                stmtUser.setString(2, cliente.getEmail().trim().toLowerCase());
                stmtUser.setString(3, hashFinal);
                stmtUser.setBoolean(4, cliente.isAtivo());
                stmtUser.executeUpdate();

                try (ResultSet rs = stmtUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuarioId = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement stmtCli = conn.prepareStatement(sqlCliente)) {
                stmtCli.setInt(1, usuarioId);
                stmtCli.setString(2, cliente.getTelefone().trim());
                stmtCli.executeUpdate();
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

    /**
     * Lista todos os clientes cadastrados ordenados por ID crescente.
     */
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT c.id AS cliente_id, u.nome, u.email, u.senha_hash, u.ativo, c.telefone "
                   + "FROM clientes c INNER JOIN usuarios u ON c.usuario_id = u.id ORDER BY c.id ASC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente(
                    rs.getInt("cliente_id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha_hash"),
                    rs.getBoolean("ativo"),
                    rs.getString("telefone")
                );
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * Atualiza os dados de um cliente existente (sem sobrescrever a senha pessoal do usuario).
     */
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE usuarios u INNER JOIN clientes c ON c.usuario_id = u.id "
                   + "SET u.nome = ?, u.email = ?, u.ativo = ?, c.telefone = ? WHERE c.id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome().trim());
            stmt.setString(2, cliente.getEmail().trim().toLowerCase());
            stmt.setBoolean(3, cliente.isAtivo());
            stmt.setString(4, cliente.getTelefone().trim());
            stmt.setInt(5, cliente.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Exclui um cliente e seu registro de usuario vinculado.
     */
    public void excluir(int clienteId) throws SQLException {
        String sqlBuscaUser = "SELECT usuario_id FROM clientes WHERE id = ?";
        String sqlDeleteUser = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.conectar()) {
            int usuarioId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscaUser)) {
                stmt.setInt(1, clienteId);
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

    /**
     * Atualiza nome, e-mail, telefone e opcionalmente a senha do proprio cliente logado.
     */
    public void atualizarPerfilCliente(int clienteId, String nome, String email, String telefone, String novaSenhaOpcional) throws SQLException {
        String sqlBuscarUser = "SELECT usuario_id FROM clientes WHERE id = ?";
        boolean alteraSenha = (novaSenhaOpcional != null && !novaSenhaOpcional.trim().isEmpty());
        String sqlUser = alteraSenha
                ? "UPDATE usuarios SET nome = ?, email = ?, senha_hash = ? WHERE id = ?"
                : "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";
        String sqlCliente = "UPDATE clientes SET telefone = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false);

            int usuarioId = 0;
            try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBuscarUser)) {
                stmtBusca.setInt(1, clienteId);
                try (ResultSet rs = stmtBusca.executeQuery()) {
                    if (rs.next()) {
                        usuarioId = rs.getInt("usuario_id");
                    }
                }
            }

            if (usuarioId == 0) {
                throw new SQLException("Usuário vinculado ao cliente não encontrado.");
            }

            try (PreparedStatement stmtU = conn.prepareStatement(sqlUser)) {
                stmtU.setString(1, nome.trim());
                stmtU.setString(2, email.trim().toLowerCase());
                if (alteraSenha) {
                    stmtU.setString(3, SegurancaUtil.gerarHashSenha(novaSenhaOpcional.trim()));
                    stmtU.setInt(4, usuarioId);
                } else {
                    stmtU.setInt(3, usuarioId);
                }
                stmtU.executeUpdate();
            }

            try (PreparedStatement stmtC = conn.prepareStatement(sqlCliente)) {
                stmtC.setString(1, telefone.trim());
                stmtC.setInt(2, clienteId);
                stmtC.executeUpdate();
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

    /**
     * Busca um cliente pelo telefone ou e-mail; caso nao exista,
     * cria automaticamente o registro em 'usuarios' (perfil CLIENTE) e 'clientes'
     * com senha criptografada em SHA-256.
     */
    public String[] buscarOuCriarVisitante(String nome, String telefone, String email, String senhaEscolhida) throws SQLException {
        String telLimpo = telefone.replaceAll("[^0-9]", "");
        String emailFinal = (email == null || email.trim().isEmpty())
                ? "visitante_" + telLimpo + "@barberstyle.local"
                : email.trim().toLowerCase();

        String sqlBusca = "SELECT c.id, u.email FROM clientes c "
                + "INNER JOIN usuarios u ON c.usuario_id = u.id "
                + "WHERE c.telefone = ? OR u.email = ? LIMIT 1";

        try (Connection conn = Conexao.conectar()) {
            // 1. Verifica se o visitante ja possui cadastro
            try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca)) {
                stmtBusca.setString(1, telefone.trim());
                stmtBusca.setString(2, emailFinal);
                try (ResultSet rs = stmtBusca.executeQuery()) {
                    if (rs.next()) {
                        return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("email"),
                            "EXISTENTE"
                        };
                    }
                }
            }

            // 2. Se nao existir, cria em usuarios + clientes dentro de uma transacao
            String hashSenha = SegurancaUtil.gerarHashSenha(senhaEscolhida);
            conn.setAutoCommit(false);
            try {
                String sqlUser = "INSERT INTO usuarios (nome, email, senha_hash, ativo, perfil) VALUES (?, ?, ?, TRUE, 'CLIENTE')";
                int usuarioId;
                try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                    stmtUser.setString(1, nome.trim());
                    stmtUser.setString(2, emailFinal);
                    stmtUser.setString(3, hashSenha);
                    stmtUser.executeUpdate();
                    try (ResultSet keys = stmtUser.getGeneratedKeys()) {
                        keys.next();
                        usuarioId = keys.getInt(1);
                    }
                }

                String sqlCli = "INSERT INTO clientes (usuario_id, telefone) VALUES (?, ?)";
                int clienteId;
                try (PreparedStatement stmtCli = conn.prepareStatement(sqlCli, Statement.RETURN_GENERATED_KEYS)) {
                    stmtCli.setInt(1, usuarioId);
                    stmtCli.setString(2, telefone.trim());
                    stmtCli.executeUpdate();
                    try (ResultSet keys = stmtCli.getGeneratedKeys()) {
                        keys.next();
                        clienteId = keys.getInt(1);
                    }
                }

                conn.commit();
                return new String[]{
                    String.valueOf(clienteId),
                    emailFinal,
                    "NOVO"
                };
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}