/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    public BigDecimal getFaturamentoMesAtual() throws SQLException {
        String sql = "SELECT COALESCE(SUM(preco_contratado), 0) FROM agendamentos "
                   + "WHERE status = 'CONCLUIDO' "
                   + "AND MONTH(data_hora) = MONTH(CURDATE()) "
                   + "AND YEAR(data_hora) = YEAR(CURDATE())";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    public int getAtendimentosHoje() throws SQLException {
        String sql = "SELECT COUNT(*) FROM agendamentos "
                   + "WHERE DATE(data_hora) = CURDATE() AND status <> 'CANCELADO'";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public BigDecimal getTicketMedio() throws SQLException {
        String sql = "SELECT COALESCE(AVG(preco_contratado), 0) FROM agendamentos "
                   + "WHERE status = 'CONCLUIDO'";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    public String getBarbeiroDestaque() throws SQLException {
        String sql = "SELECT u.nome, COUNT(a.id) AS total "
                   + "FROM agendamentos a "
                   + "INNER JOIN barbeiros b ON a.barbeiro_id = b.id "
                   + "INNER JOIN usuarios u ON b.usuario_id = u.id "
                   + "WHERE a.status = 'CONCLUIDO' "
                   + "GROUP BY u.id, u.nome "
                   + "ORDER BY total DESC LIMIT 1";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nome") + " (" + rs.getInt("total") + " cortes)";
            }
        }
        return "Sem dados ainda";
    }

    public String getServicoMaisPedido() throws SQLException {
        String sql = "SELECT s.nome, COUNT(a.id) AS total "
                   + "FROM agendamentos a "
                   + "INNER JOIN servicos s ON a.servico_id = s.id "
                   + "WHERE a.status <> 'CANCELADO' "
                   + "GROUP BY s.id, s.nome "
                   + "ORDER BY total DESC LIMIT 1";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nome") + " (" + rs.getInt("total") + "x)";
            }
        }
        return "Sem dados ainda";
    }

    /**
     * Retorna o faturamento dos ultimos 7 dias (incluindo dias zerados para o grafico ficar continuo).
     */
    public Map<String, Double> getFaturamentoUltimos7Dias() throws SQLException {
        Map<String, Double> mapa = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        LocalDate hoje = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            mapa.put(hoje.minusDays(i).format(fmt), 0.0);
        }

        String sql = "SELECT DATE(data_hora) AS dia, COALESCE(SUM(preco_contratado), 0) AS total "
                   + "FROM agendamentos "
                   + "WHERE status = 'CONCLUIDO' "
                   + "AND DATE(data_hora) >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) "
                   + "GROUP BY DATE(data_hora)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LocalDate dia = rs.getDate("dia").toLocalDate();
                String chave = dia.format(fmt);
                if (mapa.containsKey(chave)) {
                    mapa.put(chave, rs.getDouble("total"));
                }
            }
        }
        return mapa;
    }

    /**
     * Retorna quantidade de agendamentos por servico para grafico de ranking.
     */
    public Map<String, Double> getRankingServicos() throws SQLException {
        Map<String, Double> mapa = new LinkedHashMap<>();
        String sql = "SELECT s.nome, COUNT(a.id) AS total "
                   + "FROM servicos s "
                   + "LEFT JOIN agendamentos a ON a.servico_id = s.id AND a.status <> 'CANCELADO' "
                   + "WHERE s.ativo = TRUE "
                   + "GROUP BY s.id, s.nome "
                   + "ORDER BY total DESC LIMIT 6";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("nome"), rs.getDouble("total"));
            }
        }
        return mapa;
    }
}