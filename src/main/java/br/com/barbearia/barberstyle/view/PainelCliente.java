/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.config.SegurancaUtil;
import br.com.barbearia.barberstyle.dao.ClienteDAO;
import br.com.barbearia.barberstyle.model.Cliente;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Painel de cadastro, consulta, edicao e exclusao de Clientes.
 * @author FantasmaDeZion
 */
public class PainelCliente extends JPanel {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JCheckBox chkAtivo;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public PainelCliente() {
        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Cabeçalho
        JLabel lblTitulo = new JLabel("Gerenciamento de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel Superior (Formulário + Botões)
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(new TitledBorder("Dados do Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(5);
        txtId.setEditable(false);
        txtNome = new JTextField(25);
        txtEmail = new JTextField(25);

        txtTelefone = new JTextField(16);
        txtTelefone.putClientProperty("JTextField.placeholderText", "Ex: 55(21)97935-2189 ou +1 (555) 123-4567");

        chkAtivo = new JCheckBox("Cliente Ativo", true);

        // Linha 0: ID e Nome
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelFormulario.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.2;
        painelFormulario.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        painelFormulario.add(new JLabel("Nome Completo:*"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        painelFormulario.add(txtNome, gbc);

        // Linha 1: E-mail e Telefone
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelFormulario.add(new JLabel("E-mail:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.55;
        painelFormulario.add(txtEmail, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 3; gbc.weightx = 0.45;
        JPanel painelTelAtivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelTelAtivo.add(new JLabel("Telefone:*"));
        painelTelAtivo.add(txtTelefone);
        painelTelAtivo.add(chkAtivo);
        painelFormulario.add(painelTelAtivo, gbc);

        // Linha 2: Botões de Ação
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

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        painelFormulario.add(painelBotoes, gbc);

        // Tabela de Consulta
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "E-mail", "Telefone", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setRowHeight(24);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabela = new JScrollPane(tabelaClientes);
        scrollTabela.setBorder(new TitledBorder("Clientes Cadastrados (Clique em uma linha para editar ou excluir)"));

        // Container Central unindo Formulário e Tabela
        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        painelCentro.add(painelFormulario, BorderLayout.NORTH);
        painelCentro.add(scrollTabela, BorderLayout.CENTER);
        add(painelCentro, BorderLayout.CENTER);

        // Eventos
        btnSalvar.addActionListener(e -> salvarCliente());
        btnAtualizar.addActionListener(e -> atualizarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> {
            this.removeAll();
            this.setVisible(false);
        });

        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaClientes.getSelectedRow() != -1) {
                int linha = tabelaClientes.getSelectedRow();
                txtId.setText(modeloTabela.getValueAt(linha, 0).toString());
                txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
                txtEmail.setText(modeloTabela.getValueAt(linha, 2).toString());
                Object telObj = modeloTabela.getValueAt(linha, 3);
                txtTelefone.setText(telObj != null ? telObj.toString() : "");
                chkAtivo.setSelected("Ativo".equals(modeloTabela.getValueAt(linha, 4).toString()));
            }
        });
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Cliente> lista = clienteDAO.listarTodos();
            for (Cliente c : lista) {
                modeloTabela.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    c.getEmail(),
                    c.getTelefone(),
                    c.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar clientes: " + ex.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha pelo menos Nome e E-mail!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String telefone = txtTelefone.getText().trim();
        String apenasDigitos = telefone.replaceAll("\\D", "");

        if (!telefone.matches("[+\\d()\\s-]+") || apenasDigitos.length() < 8 || apenasDigitos.length() > 18) {
            JOptionPane.showMessageDialog(this,
                    "Telefone inválido! Informe um número válido com código do país e DDD.\nExemplos: 55(21)97935-2189 ou +351 912 345 678",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            txtTelefone.requestFocus();
            return false;
        }
        return true;
    }

    private void salvarCliente() {
        if (!validarCampos()) return;
        try {
            String senhaProvisoria = SegurancaUtil.gerarSenhaAleatoria(8);
            Cliente c = new Cliente(0, txtNome.getText().trim(), txtEmail.getText().trim(), senhaProvisoria, chkAtivo.isSelected(), txtTelefone.getText().trim());
            clienteDAO.inserir(c);
            JOptionPane.showMessageDialog(this,
                    "Cliente cadastrado com sucesso!\nSenha provisória de acesso: " + senhaProvisoria,
                    "Cadastro Concluído", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCliente() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;
        try {
            Cliente c = new Cliente(
                Integer.parseInt(txtId.getText()),
                txtNome.getText().trim(),
                txtEmail.getText().trim(),
                null,
                chkAtivo.isSelected(),
                txtTelefone.getText().trim()
            );
            clienteDAO.atualizar(c);
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCliente() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) {
            try {
                clienteDAO.excluir(Integer.parseInt(txtId.getText()));
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                limparCampos();
                carregarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        chkAtivo.setSelected(true);
        tabelaClientes.clearSelection();
    }
}