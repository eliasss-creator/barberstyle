/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.model.Avaliacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsavel pelas avaliacoes por estrelas dos servicos concluidos.
 * @author FantasmaDeZion
 */
public class AvaliacaoDAO {

    public void inserir(Avaliacao av) throws SQLException {
        String sql = "INSERT INTO avaliacoes (agendamento_id, estrelas, comentario, data_avaliacao) VALUES (?, ?, ?, NOW())";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, av.getAgendamento().getId());
            stmt.setInt(2, av.getEstrelas());
            stmt.setString(3, av.getComentario());
            stmt.executeUpdate();
        }
    }

    public boolean jaAvaliado(int agendamentoId) throws SQLException {
        String sql = "SELECT 1 FROM avaliacoes WHERE agendamento_id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agendamentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public double calcularMediaPorServico(int servicoId) throws SQLException {
        String sql = "SELECT COALESCE(AVG(av.estrelas), 0) AS media "
                   + "FROM avaliacoes av "
                   + "INNER JOIN agendamentos ag ON av.agendamento_id = ag.id "
                   + "WHERE ag.servico_id = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("media");
                }
            }
        }
        return 0.0;
    }

    public List<String> listarUltimosComentarios(int limite) throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT u.nome AS cliente, s.nome AS servico, av.estrelas, av.comentario "
                   + "FROM avaliacoes av "
                   + "INNER JOIN agendamentos ag ON av.agendamento_id = ag.id "
                   + "INNER JOIN clientes c ON ag.cliente_id = c.id "
                   + "INNER JOIN usuarios u ON c.usuario_id = u.id "
                   + "INNER JOIN servicos s ON ag.servico_id = s.id "
                   + "ORDER BY av.data_avaliacao DESC "
                   + "LIMIT ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int estrelas = rs.getInt("estrelas");
                    String iconeEstrelas = "★".repeat(estrelas) + "☆".repeat(5 - estrelas);
                    String comentario = rs.getString("comentario");
                    String texto = iconeEstrelas + " (" + rs.getString("servico") + ") - "
                            + rs.getString("cliente")
                            + (comentario != null && !comentario.isBlank() ? ": \"" + comentario + "\"" : "");
                    lista.add(texto);
                }
            }
        }
        return lista;
    }
}