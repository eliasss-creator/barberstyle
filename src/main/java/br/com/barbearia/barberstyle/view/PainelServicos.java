/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.ServicoDAO;
import br.com.barbearia.barberstyle.model.Servico;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Painel de cadastro, consulta, edicao e exclusao de Servicos da barbearia.
 * @author FantasmaDeZion
 */

public class PainelServicos extends JPanel {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtPreco;
    private JTextField txtDuracao;
    private JTextArea txtDescricao;
    private JRadioButton rbAtivo;
    private JRadioButton rbInativo;
    private JTable tabelaServicos;
    private DefaultTableModel modeloTabela;
    private final ServicoDAO servicoDAO = new ServicoDAO();

    public PainelServicos() {
        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Catálogo de Serviços");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(new TitledBorder("Dados do Serviço"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(5);
        txtId.setEditable(false);
        txtNome = new JTextField(20);
        txtPreco = new JTextField(10);
        txtDuracao = new JTextField(8);
        txtDescricao = new JTextArea(2, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        // Uso de JRadioButton + ButtonGroup conforme aula da Prof. Gizelle
        rbAtivo = new JRadioButton("Ativo", true);
        rbInativo = new JRadioButton("Inativo", false);
        ButtonGroup grupoStatus = new ButtonGroup();
        grupoStatus.add(rbAtivo);
        grupoStatus.add(rbInativo);

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelFormulario.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.15;
        painelFormulario.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Nome do Serviço:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.85;
        painelFormulario.add(txtNome, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Preço (R$):*"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.25;
        painelFormulario.add(txtPreco, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Duração (min):*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.75;
        JPanel painelDurStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelDurStatus.add(txtDuracao);
        painelDurStatus.add(new JLabel("Status:"));
        painelDurStatus.add(rbAtivo);
        painelDurStatus.add(rbInativo);
        painelFormulario.add(painelDurStatus, gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        painelFormulario.add(new JScrollPane(txtDescricao), gbc);
        gbc.gridwidth = 1;

        // Linha 3: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnSalvar = new JButton("Cadastrar Novo");
        JButton btnAtualizar = new JButton("Salvar Alterações");
        JButton btnExcluir = new JButton("Excluir Selecionado");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnFechar = new JButton("Fechar Painel");

        btnSalvar.setBackground(new Color(40, 167, 69));
        btnSalvar.setForeground(Color.WHITE);
        btnAtualizar.setBackground(new Color(0, 123, 255));
        btnAtualizar.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(220, 53, 69));
        btnExcluir.setForeground(Color.WHITE);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnFechar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        painelFormulario.add(painelBotoes, gbc);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Descrição", "Preço (R$)", "Duração (min)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaServicos = new JTable(modeloTabela);
        tabelaServicos.setRowHeight(24);
        tabelaServicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaServicos);
        scrollTabela.setBorder(new TitledBorder("Serviços Disponíveis"));

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        painelCentro.add(painelFormulario, BorderLayout.NORTH);
        painelCentro.add(scrollTabela, BorderLayout.CENTER);
        add(painelCentro, BorderLayout.CENTER);

        // Eventos
        btnSalvar.addActionListener(e -> salvarServico());
        btnAtualizar.addActionListener(e -> atualizarServico());
        btnExcluir.addActionListener(e -> excluirServico());
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> {
            this.removeAll();
            this.setVisible(false);
        });

        tabelaServicos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaServicos.getSelectedRow() != -1) {
                int linha = tabelaServicos.getSelectedRow();
                txtId.setText(modeloTabela.getValueAt(linha, 0).toString());
                txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
                Object desc = modeloTabela.getValueAt(linha, 2);
                txtDescricao.setText(desc != null ? desc.toString() : "");
                txtPreco.setText(modeloTabela.getValueAt(linha, 3).toString());
                txtDuracao.setText(modeloTabela.getValueAt(linha, 4).toString());
                boolean ativo = "Ativo".equals(modeloTabela.getValueAt(linha, 5).toString());
                rbAtivo.setSelected(ativo);
                rbInativo.setSelected(!ativo);
            }
        });
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Servico> lista = servicoDAO.listarTodos();
            for (Servico s : lista) {
                modeloTabela.addRow(new Object[]{
                    s.getId(), s.getNome(), s.getDescricao(), s.getPreco(), s.getDuracaoMinutos(), s.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar serviços: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarServico() {
        try {
            String nome = txtNome.getText().trim();
            BigDecimal preco = new BigDecimal(txtPreco.getText().trim().replace(",", "."));
            int duracao = Integer.parseInt(txtDuracao.getText().trim());

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do serviço!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Servico s = new Servico(0, nome, txtDescricao.getText().trim(), preco, duracao, rbAtivo.isSelected());
            servicoDAO.inserir(s);
            JOptionPane.showMessageDialog(this, "Serviço cadastrado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço ou Duração inválidos! Use números (ex: 45.00 e 30).", "Erro de Formato", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar serviço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarServico() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Servico s = new Servico(
                Integer.parseInt(txtId.getText()),
                txtNome.getText().trim(),
                txtDescricao.getText().trim(),
                new BigDecimal(txtPreco.getText().trim().replace(",", ".")),
                Integer.parseInt(txtDuracao.getText().trim()),
                rbAtivo.isSelected()
            );
            servicoDAO.atualizar(s);
            JOptionPane.showMessageDialog(this, "Serviço atualizado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço ou Duração inválidos!", "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar serviço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirServico() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este serviço?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                servicoDAO.excluir(Integer.parseInt(txtId.getText()));
                JOptionPane.showMessageDialog(this, "Serviço excluído com sucesso!");
                limparCampos();
                carregarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir serviço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtPreco.setText("");
        txtDuracao.setText("");
        txtDescricao.setText("");
        rbAtivo.setSelected(true);
        tabelaServicos.clearSelection();
    }
}