/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.dao;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.model.Agendamento;
import br.com.barbearia.barberstyle.model.Barbeiro;
import br.com.barbearia.barberstyle.model.Cliente;
import br.com.barbearia.barberstyle.model.Servico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsavel pelas operacoes de banco de dados da entidade Agendamento.
 * @author FantasmaDeZion
 */
public class AgendamentoDAO {

    public void inserir(Agendamento a) throws SQLException {
        // 1. Impede agendamentos no passado
        if (a.getDataHora().isBefore(LocalDateTime.now())) {
            throw new SQLException("Não é possível agendar para uma data ou horário que já passou.");
        }

        // 2. Impede que o cliente agende mais de um corte/serviço ativo no mesmo dia
        if (clienteJaTemAgendamentoNoDia(a.getCliente().getId(), a.getDataHora())) {
            throw new SQLException(
                "Você já possui um agendamento ativo nesta data ("
                + a.getDataHora().toLocalDate()
                + "). Cancele o horário anterior ou escolha outro dia."
            );
        }

        // 3. Impede choque de horário na agenda do barbeiro (considerando a duração em minutos)
        if (barbeiroOcupadoNoHorario(a.getBarbeiro().getId(), a.getDataHora(), a.getDuracaoContratada())) {
            throw new SQLException(
                "Conflito de agenda: o barbeiro selecionado já possui outro atendimento nesse intervalo de horário."
            );
        }

        String sql = "INSERT INTO agendamentos "
                   + "(cliente_id, barbeiro_id, servico_id, data_hora, preco_contratado, duracao_contratada, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, a.getCliente().getId());
            stmt.setInt(2, a.getBarbeiro().getId());
            stmt.setInt(3, a.getServico().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(a.getDataHora()));
            stmt.setBigDecimal(5, a.getPrecoContratado());
            stmt.setInt(6, a.getDuracaoContratada());
            stmt.setString(7, a.getStatus());

            stmt.executeUpdate();
        }
    }

    private boolean clienteJaTemAgendamentoNoDia(int clienteId, LocalDateTime dataHora) throws SQLException {
        String sql = "SELECT COUNT(*) FROM agendamentos "
                   + "WHERE cliente_id = ? "
                   + "AND DATE(data_hora) = DATE(?) "
                   + "AND status IN ('AGENDADO', 'CONCLUIDO')";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean barbeiroOcupadoNoHorario(int barbeiroId, LocalDateTime inicioNovo, int duracaoMinutos) throws SQLException {
        LocalDateTime fimNovo = inicioNovo.plusMinutes(duracaoMinutos);

        String sql = "SELECT COUNT(*) FROM agendamentos "
                   + "WHERE barbeiro_id = ? "
                   + "AND status = 'AGENDADO' "
                   + "AND data_hora < ? "
                   + "AND DATE_ADD(data_hora, INTERVAL duracao_contratada MINUTE) > ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, barbeiroId);
            stmt.setTimestamp(2, Timestamp.valueOf(fimNovo));
            stmt.setTimestamp(3, Timestamp.valueOf(inicioNovo));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Lista todos os agendamentos com os dados de cliente, barbeiro e servico.
     * @return lista de agendamentos
     * @throws SQLException caso ocorra erro na consulta
     */
    public List<Agendamento> listarTodos() throws SQLException {
        List<Agendamento> lista = new ArrayList<>();

        String sql = "SELECT "
                + "a.id AS Agendamento, "
                + "c.id AS cliente_id, "
                + "u.Nome AS Cliente, "
                + "br.id AS barbeiro_id, "
                + "b.Nome AS Barbeiro, "
                + "s.id AS servico_id, "
                + "s.Nome AS Servico, "
                + "a.data_hora, "
                + "a.preco_contratado, "
                + "a.duracao_contratada AS Duracao, "
                + "a.status "
                + "FROM agendamentos AS a "
                + "INNER JOIN clientes AS c ON a.cliente_id = c.Id "
                + "INNER JOIN usuarios AS u ON c.usuario_id = u.Id "
                + "INNER JOIN barbeiros AS br ON a.barbeiro_id = br.Id "
                + "INNER JOIN usuarios AS b ON br.usuario_id = b.Id "
                + "INNER JOIN servicos AS s ON a.servico_id = s.Id "
                + "ORDER BY a.data_hora DESC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Agendamento a = new Agendamento();
                a.setId(rs.getInt("Agendamento"));

                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNome(rs.getString("Cliente"));
                a.setCliente(cliente);

                Barbeiro barbeiro = new Barbeiro();
                barbeiro.setId(rs.getInt("barbeiro_id"));
                barbeiro.setNome(rs.getString("Barbeiro"));
                a.setBarbeiro(barbeiro);

                Servico servico = new Servico();
                servico.setId(rs.getInt("servico_id"));
                servico.setNome(rs.getString("Servico"));
                a.setServico(servico);

                a.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                a.setPrecoContratado(rs.getBigDecimal("preco_contratado"));
                a.setDuracaoContratada(rs.getInt("Duracao"));
                a.setStatus(rs.getString("status"));

                lista.add(a);
            }
        }

        return lista;
    }

    /**
     * Atualiza o status de um agendamento existente.
     * @param id identificador do agendamento
     * @param novoStatus novo status (AGENDADO, CONCLUIDO ou CANCELADO)
     * @throws SQLException caso ocorra erro na atualizacao
     */
    public void atualizarStatus(int id, String novoStatus) throws SQLException {
        String sql = "UPDATE agendamentos SET status = ? WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);

            stmt.executeUpdate();
        }
    }

    /**
     * Exclui um agendamento pelo ID.
     * @param id identificador do agendamento
     * @throws SQLException caso ocorra erro na exclusao
     */
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM agendamentos WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }
}