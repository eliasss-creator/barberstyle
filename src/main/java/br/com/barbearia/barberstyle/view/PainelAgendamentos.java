/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.*;
import br.com.barbearia.barberstyle.model.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

/**
 * Painel de criacao, consulta, alteracao de status, avaliacao e exclusao de Agendamentos.
 * @author FantasmaDeZion
 */
public class PainelAgendamentos extends JPanel {

    private JComboBox<Cliente> comboCliente;
    private JComboBox<Barbeiro> comboBarbeiro;
    private JComboBox<Servico> comboServico;
    private JFormattedTextField txtDataHora;
    private JComboBox<String> comboStatus;
    private JTable tabelaAgendamentos;
    private DefaultTableModel modeloTabela;

    private final Usuario usuarioLogado;
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelAgendamentos() {
        this(null);
    }

    public PainelAgendamentos(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        inicializarComponentes();
        carregarCombos();
        carregarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Agenda e Controle de Atendimentos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(new TitledBorder("Novo Agendamento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboCliente = new JComboBox<>();
        comboBarbeiro = new JComboBox<>();
        comboServico = new JComboBox<>();
        comboStatus = new JComboBox<>(new String[]{"AGENDADO", "CONCLUIDO", "CANCELADO"});

        try {
            MaskFormatter mascaraDataHora = new MaskFormatter("##/##/#### ##:##");
            mascaraDataHora.setPlaceholderCharacter('_');
            txtDataHora = new JFormattedTextField(mascaraDataHora);
            txtDataHora.setText(LocalDateTime.now().plusHours(1).withMinute(0).format(formatter));
        } catch (ParseException e) {
            txtDataHora = new JFormattedTextField();
        }

        // Linha 0: Cliente e Barbeiro
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Cliente:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        painelFormulario.add(comboCliente, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Barbeiro:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        painelFormulario.add(comboBarbeiro, gbc);

        // Linha 1: Serviço, Data/Hora e Status
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Serviço:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        painelFormulario.add(comboServico, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Data e Hora:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        JPanel painelDataStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        txtDataHora.setColumns(12);
        painelDataStatus.add(txtDataHora);
        painelDataStatus.add(new JLabel("Status:"));
        painelDataStatus.add(comboStatus);
        painelFormulario.add(painelDataStatus, gbc);

        // Linha 2: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnAgendar = new JButton("Confirmar Agendamento");
        JButton btnAlterarStatus = new JButton("Atualizar Status da Linha");
        JButton btnAvaliar = new JButton("Avaliar Atendimento (★)");
        JButton btnExcluir = new JButton("Cancelar/Excluir");
        JButton btnFechar = new JButton("Fechar Painel");

        btnAgendar.setBackground(new Color(40, 167, 69));
        btnAgendar.setForeground(Color.WHITE);
        btnAlterarStatus.setBackground(new Color(0, 123, 255));
        btnAlterarStatus.setForeground(Color.WHITE);
        btnAvaliar.setBackground(new Color(218, 165, 32));
        btnAvaliar.setForeground(Color.BLACK);
        btnAvaliar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExcluir.setBackground(new Color(220, 53, 69));
        btnExcluir.setForeground(Color.WHITE);

        painelBotoes.add(btnAgendar);
        painelBotoes.add(btnAlterarStatus);
        painelBotoes.add(btnAvaliar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnFechar);

        // Regras de interface por perfil
        if (usuarioLogado != null) {
            if ("CLIENTE".equals(usuarioLogado.getPerfil())) {
                comboStatus.setEnabled(false);
                btnAlterarStatus.setVisible(false);
            } else if ("BARBEIRO".equals(usuarioLogado.getPerfil())) {
                btnAvaliar.setVisible(false);
            }
        }

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        painelFormulario.add(painelBotoes, gbc);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Cliente", "Barbeiro", "Serviço", "Data e Hora", "Valor (R$)", "Duração", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaAgendamentos = new JTable(modeloTabela);
        tabelaAgendamentos.setRowHeight(24);
        tabelaAgendamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaAgendamentos);
        scrollTabela.setBorder(new TitledBorder("Agendamentos Registrados"));

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        painelCentro.add(painelFormulario, BorderLayout.NORTH);
        painelCentro.add(scrollTabela, BorderLayout.CENTER);
        add(painelCentro, BorderLayout.CENTER);

        // Eventos
        btnAgendar.addActionListener(e -> salvarAgendamento());
        btnAlterarStatus.addActionListener(e -> alterarStatusSelecionado());
        btnAvaliar.addActionListener(e -> avaliarSelecionado());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnFechar.addActionListener(e -> {
            this.removeAll();
            this.setVisible(false);
        });
    }

    private boolean pertenceAoBarbeiroLogado(Agendamento a) {
        if (usuarioLogado == null || !"BARBEIRO".equals(usuarioLogado.getPerfil())) return false;
        boolean mesmoId = (a.getBarbeiro() != null && a.getBarbeiro().getId() == usuarioLogado.getId());
        boolean mesmoNome = (a.getBarbeiro() != null && a.getBarbeiro().getNome() != null
                && a.getBarbeiro().getNome().trim().equalsIgnoreCase(usuarioLogado.getNome().trim()));
        return mesmoId || mesmoNome;
    }

    private void carregarCombos() {
        try {
            // 1. Carrega Barbeiros
            comboBarbeiro.removeAllItems();
            for (Barbeiro b : barbeiroDAO.listarTodos()) {
                if (!b.isAtivo()) continue;
                comboBarbeiro.addItem(b);
                if (usuarioLogado != null && "BARBEIRO".equals(usuarioLogado.getPerfil())) {
                    if (b.getId() == usuarioLogado.getId() || b.getNome().trim().equalsIgnoreCase(usuarioLogado.getNome().trim())) {
                        comboBarbeiro.setSelectedItem(b);
                        comboBarbeiro.setEnabled(false);
                    }
                }
            }

            // 2. Se for Barbeiro logado, coleta os IDs dos clientes que tem agendamento marcado com ele
            Set<Integer> idsClientesDoBarbeiro = new HashSet<>();
            if (usuarioLogado != null && "BARBEIRO".equals(usuarioLogado.getPerfil())) {
                for (Agendamento a : agendamentoDAO.listarTodos()) {
                    if (pertenceAoBarbeiroLogado(a) && a.getCliente() != null) {
                        idsClientesDoBarbeiro.add(a.getCliente().getId());
                    }
                }
            }

            // 3. Carrega Clientes conforme o perfil
            comboCliente.removeAllItems();
            for (Cliente c : clienteDAO.listarTodos()) {
                if (!c.isAtivo()) continue;

                if (usuarioLogado != null) {
                    String perfil = usuarioLogado.getPerfil();
                    if ("CLIENTE".equals(perfil) && c.getId() != usuarioLogado.getId()) {
                        continue;
                    }
                    if ("BARBEIRO".equals(perfil)) {
                        // Nunca mostra o proprio barbeiro como cliente e filtra apenas clientes marcados com ele
                        boolean eOProprioBarbeiro = c.getEmail().equalsIgnoreCase(usuarioLogado.getEmail())
                                || c.getNome().trim().equalsIgnoreCase(usuarioLogado.getNome().trim());
                        if (eOProprioBarbeiro || !idsClientesDoBarbeiro.contains(c.getId())) {
                            continue;
                        }
                    }
                }
                comboCliente.addItem(c);
            }

            // 4. Carrega Servicos
            comboServico.removeAllItems();
            for (Servico s : servicoDAO.listarTodos()) {
                if (s.isAtivo()) comboServico.addItem(s);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar listas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Agendamento> lista = agendamentoDAO.listarTodos();
            for (Agendamento a : lista) {
                if (usuarioLogado != null) {
                    String perfil = usuarioLogado.getPerfil();
                    if ("CLIENTE".equals(perfil) && a.getCliente().getId() != usuarioLogado.getId()
                            && !a.getCliente().getNome().trim().equalsIgnoreCase(usuarioLogado.getNome().trim())) {
                        continue;
                    }
                    if ("BARBEIRO".equals(perfil) && !pertenceAoBarbeiroLogado(a)) {
                        continue;
                    }
                }

                modeloTabela.addRow(new Object[]{
                    a.getId(),
                    a.getCliente().getNome(),
                    a.getBarbeiro().getNome(),
                    a.getServico().getNome(),
                    a.getDataHora().format(formatter),
                    a.getPrecoContratado(),
                    a.getDuracaoContratada() + " min",
                    a.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao listar agendamentos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarAgendamento() {
        Cliente cli = (Cliente) comboCliente.getSelectedItem();
        Barbeiro barb = (Barbeiro) comboBarbeiro.getSelectedItem();
        Servico serv = (Servico) comboServico.getSelectedItem();

        if (cli == null || barb == null || serv == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente, barbeiro e serviço válidos!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDateTime dataHora = LocalDateTime.parse(txtDataHora.getText().trim(), formatter);
            String status = comboStatus.getSelectedItem().toString();

            Agendamento agendamento = new Agendamento(
                    0, cli, barb, serv, dataHora,
                    serv.getPreco(), serv.getDuracaoMinutos(), status
            );

            agendamentoDAO.inserir(agendamento);
            JOptionPane.showMessageDialog(this, "Agendamento registrado com sucesso!");
            carregarTabela();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data/Hora inválida! Use DD/MM/AAAA HH:MM.", "Erro de Data", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar agendamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarStatusSelecionado() {
        int linha = tabelaAgendamentos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela!");
            return;
        }

        int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
        String novoStatus = comboStatus.getSelectedItem().toString();

        try {
            agendamentoDAO.atualizarStatus(id, novoStatus);
            JOptionPane.showMessageDialog(this, "Status atualizado com sucesso!");
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status: " + ex.getMessage());
        }
    }

    private void avaliarSelecionado() {
        int linha = tabelaAgendamentos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela primeiro.");
            return;
        }
        String status = tabelaAgendamentos.getValueAt(linha, 7).toString();
        if (!"CONCLUIDO".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Apenas atendimentos com status CONCLUIDO podem ser avaliados.");
            return;
        }

        int idAgendamento = Integer.parseInt(tabelaAgendamentos.getValueAt(linha, 0).toString());
        String nomeServico = tabelaAgendamentos.getValueAt(linha, 3).toString();

        Agendamento ag = new Agendamento();
        ag.setId(idAgendamento);
        Servico s = new Servico();
        s.setNome(nomeServico);
        ag.setServico(s);

        Frame janelaPai = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoAvaliacao(janelaPai, ag).setVisible(true);
    }

    private void excluirSelecionado() {
        int linha = tabelaAgendamentos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela!");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este agendamento?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());

        try {
            agendamentoDAO.excluir(id);
            JOptionPane.showMessageDialog(this, "Agendamento excluído com sucesso!");
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir agendamento: " + ex.getMessage());
        }
    }
}