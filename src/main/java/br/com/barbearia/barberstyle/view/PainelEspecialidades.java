/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.EspecialidadeDAO;
import br.com.barbearia.barberstyle.model.Especialidade;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PainelEspecialidades extends JPanel {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtDescricao;
    private JCheckBox chkAtivo;
    private JTable tabela;
    private DefaultTableModel modelo;
    private final EspecialidadeDAO dao = new EspecialidadeDAO();

    public PainelEspecialidades() {
        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Catálogo de Especialidades dos Barbeiros");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Cadastrar ou Editar Especialidade"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(4);
        txtId.setEditable(false);
        txtNome = new JTextField(22);
        txtDescricao = new JTextField(30);
        chkAtivo = new JCheckBox("Ativa", true);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        form.add(txtId, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Especialidade *:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4;
        form.add(txtNome, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        form.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.6;
        form.add(txtDescricao, gbc);

        gbc.gridx = 6; gbc.weightx = 0;
        form.add(chkAtivo, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        JButton btnSalvar = new JButton("Salvar Nova");
        JButton btnAtualizar = new JButton("Atualizar Selecionada");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnFechar = new JButton("Fechar Painel");

        btnSalvar.setBackground(new Color(40, 167, 69));
        btnSalvar.setForeground(Color.WHITE);
        btnAtualizar.setBackground(new Color(0, 123, 255));
        btnAtualizar.setForeground(Color.WHITE);
        btnExcluir.setBackground(new Color(220, 53, 69));
        btnExcluir.setForeground(Color.WHITE);

        botoes.add(btnSalvar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);
        botoes.add(btnLimpar);
        botoes.add(btnFechar);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 7;
        form.add(botoes, gbc);

        modelo = new DefaultTableModel(new String[]{"ID", "Especialidade", "Descrição", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.add(form, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        btnSalvar.addActionListener(e -> salvar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());
        btnFechar.addActionListener(e -> {
            this.removeAll();
            this.setVisible(false);
        });

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                int r = tabela.getSelectedRow();
                txtId.setText(modelo.getValueAt(r, 0).toString());
                txtNome.setText(modelo.getValueAt(r, 1).toString());
                txtDescricao.setText(modelo.getValueAt(r, 2).toString());
                chkAtivo.setSelected("Ativa".equals(modelo.getValueAt(r, 3).toString()));
            }
        });
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            for (Especialidade esp : dao.listarTodas()) {
                modelo.addRow(new Object[]{
                    esp.getId(), esp.getNome(), esp.getDescricao(), esp.isAtivo() ? "Ativa" : "Inativa"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage());
        }
    }

    private void salvar() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da especialidade.");
            return;
        }
        try {
            dao.inserir(new Especialidade(0, txtNome.getText(), txtDescricao.getText(), chkAtivo.isSelected()));
            JOptionPane.showMessageDialog(this, "Especialidade cadastrada!");
            limpar();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void atualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma especialidade na tabela.");
            return;
        }
        try {
            dao.atualizar(new Especialidade(
                Integer.parseInt(txtId.getText()), txtNome.getText(), txtDescricao.getText(), chkAtivo.isSelected()
            ));
            JOptionPane.showMessageDialog(this, "Especialidade atualizada!");
            limpar();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Excluir esta especialidade?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                dao.excluir(Integer.parseInt(txtId.getText()));
                limpar();
                carregarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    private void limpar() {
        txtId.setText("");
        txtNome.setText("");
        txtDescricao.setText("");
        chkAtivo.setSelected(true);
        tabela.clearSelection();
    }
}