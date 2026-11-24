/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.BarbeiroDAO;
import br.com.barbearia.barberstyle.model.Barbeiro;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author FantasmaDeZion
 */

/**
 * Painel de cadastro, consulta, edicao e exclusao de Barbeiros.
 * @author FantasmaDeZion
 */
public class PainelBarbeiro extends JPanel {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JComboBox<String> comboEspecialidade;
    private JTextArea txtDescricao;
    private JCheckBox chkAtivo;
    private JTable tabelaBarbeiros;
    private DefaultTableModel modeloTabela;
    private final BarbeiroDAO barbeiroDAO = new BarbeiroDAO();

    public PainelBarbeiro() {
        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Gerenciamento de Barbeiros");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(new TitledBorder("Dados do Profissional"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(5);
        txtId.setEditable(false);
        txtNome = new JTextField(22);
        txtEmail = new JTextField(22);
        comboEspecialidade = new JComboBox<>(new String[]{
            "Corte Degradê e Visagismo",
            "Barba Tradicional e Navalha",
            "Corte Clássico na Tesoura",
            "Colorimetria e Platinado",
            "Barboterapia Completa"
        });
        comboEspecialidade.setEditable(true);
        txtDescricao = new JTextArea(2, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        chkAtivo = new JCheckBox("Barbeiro Ativo", true);

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelFormulario.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.15;
        painelFormulario.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Nome:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.85;
        painelFormulario.add(txtNome, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelFormulario.add(new JLabel("E-mail:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.5;
        painelFormulario.add(txtEmail, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 3; gbc.weightx = 0.5;
        JPanel painelEsp = new JPanel(new BorderLayout(5, 0));
        painelEsp.add(new JLabel("Especialidade:* "), BorderLayout.WEST);
        painelEsp.add(comboEspecialidade, BorderLayout.CENTER);
        painelEsp.add(chkAtivo, BorderLayout.EAST);
        painelFormulario.add(painelEsp, gbc);

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
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "E-mail", "Especialidade", "Descrição", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaBarbeiros = new JTable(modeloTabela);
        tabelaBarbeiros.setRowHeight(24);
        tabelaBarbeiros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaBarbeiros);
        scrollTabela.setBorder(new TitledBorder("Barbeiros Cadastrados"));

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        painelCentro.add(painelFormulario, BorderLayout.NORTH);
        painelCentro.add(scrollTabela, BorderLayout.CENTER);
        add(painelCentro, BorderLayout.CENTER);

        // Eventos
        btnSalvar.addActionListener(e -> salvarBarbeiro());
        btnAtualizar.addActionListener(e -> atualizarBarbeiro());
        btnExcluir.addActionListener(e -> excluirBarbeiro());
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> {
            this.removeAll();
            this.setVisible(false);
        });

        tabelaBarbeiros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaBarbeiros.getSelectedRow() != -1) {
                int linha = tabelaBarbeiros.getSelectedRow();
                txtId.setText(modeloTabela.getValueAt(linha, 0).toString());
                txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
                txtEmail.setText(modeloTabela.getValueAt(linha, 2).toString());
                comboEspecialidade.setSelectedItem(modeloTabela.getValueAt(linha, 3).toString());
                Object desc = modeloTabela.getValueAt(linha, 4);
                txtDescricao.setText(desc != null ? desc.toString() : "");
                chkAtivo.setSelected("Ativo".equals(modeloTabela.getValueAt(linha, 5).toString()));
            }
        });
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Barbeiro> lista = barbeiroDAO.listarTodos();
            for (Barbeiro b : lista) {
                modeloTabela.addRow(new Object[]{
                    b.getId(), b.getNome(), b.getEmail(), b.getEspecialidade(), b.getDescricao(), b.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar barbeiros: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarBarbeiro() {
        if (txtNome.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha Nome e E-mail!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String esp = comboEspecialidade.getSelectedItem() != null ? comboEspecialidade.getSelectedItem().toString() : "";
            Barbeiro b = new Barbeiro(0, txtNome.getText().trim(), txtEmail.getText().trim(), "123456", chkAtivo.isSelected(), esp, txtDescricao.getText().trim());
            barbeiroDAO.inserir(b);
            JOptionPane.showMessageDialog(this, "Barbeiro cadastrado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar barbeiro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarBarbeiro() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um barbeiro na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String esp = comboEspecialidade.getSelectedItem() != null ? comboEspecialidade.getSelectedItem().toString() : "";
            Barbeiro b = new Barbeiro(Integer.parseInt(txtId.getText()), txtNome.getText().trim(), txtEmail.getText().trim(), "123456", chkAtivo.isSelected(), esp, txtDescricao.getText().trim());
            barbeiroDAO.atualizar(b);
            JOptionPane.showMessageDialog(this, "Barbeiro atualizado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar barbeiro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirBarbeiro() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um barbeiro na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este barbeiro?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                barbeiroDAO.excluir(Integer.parseInt(txtId.getText()));
                JOptionPane.showMessageDialog(this, "Barbeiro excluído com sucesso!");
                limparCampos();
                carregarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir barbeiro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        comboEspecialidade.setSelectedIndex(0);
        txtDescricao.setText("");
        chkAtivo.setSelected(true);
        tabelaBarbeiros.clearSelection();
    }
}
