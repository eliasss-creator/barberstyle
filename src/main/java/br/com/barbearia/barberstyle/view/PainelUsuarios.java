/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.UsuarioDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PainelUsuarios extends JPanel {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JComboBox<String> cbPerfil;
    private JTextField txtEspecialidade;
    private JCheckBox chkAtivo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final int adminLogadoId;

    public PainelUsuarios(int adminLogadoId) {
        this.adminLogadoId = adminLogadoId;
        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Equipe e Acessos (Secretária / Barbeiro / Administrador)");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Cadastrar ou Redefinir Senha de Usuário Interno"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(4);
        txtId.setEditable(false);
        txtNome = new JTextField(22);
        txtEmail = new JTextField(22);
        txtSenha = new JPasswordField(18);
        cbPerfil = new JComboBox<>(new String[]{"SECRETARIA", "BARBEIRO", "ADMINISTRADOR"});
        txtEspecialidade = new JTextField(18);
        txtEspecialidade.putClientProperty("JTextField.placeholderText", "Especialidade (apenas se Barbeiro)");
        chkAtivo = new JCheckBox("Acesso Ativo", true);

        // Linha 0
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.15;
        form.add(txtId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(new JLabel("Nome Completo *:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        form.add(txtNome, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        form.add(new JLabel("Cargo / Perfil *:"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.35;
        form.add(cbPerfil, gbc);

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("E-mail *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.45;
        form.add(txtEmail, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 3; gbc.weightx = 0;
        JPanel painelSenha = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelSenha.add(new JLabel("Nova Senha *:"));
        painelSenha.add(txtSenha);
        form.add(painelSenha, gbc);

        gbc.gridx = 4; gbc.gridwidth = 2; gbc.weightx = 0.4;
        JPanel painelEspAtivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelEspAtivo.add(txtEspecialidade);
        painelEspAtivo.add(chkAtivo);
        form.add(painelEspAtivo, gbc);
        gbc.gridwidth = 1;

        // Linha 2: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnSalvar = new JButton("Cadastrar Novo Membro");
        JButton btnAtualizar = new JButton("Atualizar / Resetar Senha");
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

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        form.add(painelBotoes, gbc);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "E-mail", "Perfil", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(new TitledBorder("Usuários Internos (Selecione uma linha e preencha 'Nova Senha' para resetar a senha de qualquer membro ou do Admin)"));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.add(form, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        btnSalvar.addActionListener(e -> salvar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());
        btnFechar.addActionListener(e -> {
            this.removeAll();
            this.setVisible(false);
        });

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                int r = tabela.getSelectedRow();
                txtId.setText(modeloTabela.getValueAt(r, 0).toString());
                txtNome.setText(modeloTabela.getValueAt(r, 1).toString());
                txtEmail.setText(modeloTabela.getValueAt(r, 2).toString());
                cbPerfil.setSelectedItem(modeloTabela.getValueAt(r, 3).toString());
                chkAtivo.setSelected("Ativo".equals(modeloTabela.getValueAt(r, 4).toString()));
                txtSenha.setText("");
            }
        });
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            for (Object[] row : usuarioDAO.listarEquipeInterna()) {
                modeloTabela.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar equipe: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        String perfil = cbPerfil.getSelectedItem().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "Preencha Nome, E-mail e uma Senha forte (mín. 6 caracteres).", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuarioDAO.isSenhaPadraoInsegura(senha)) {
            JOptionPane.showMessageDialog(this, "Não utilize senhas padrão fracas (como 123456 ou admin123).", "Segurança", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            usuarioDAO.inserirUsuarioEquipe(nome, email, senha, perfil, chkAtivo.isSelected(), txtEspecialidade.getText());
            JOptionPane.showMessageDialog(this, perfil + " cadastrado(a) com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para atualizar ou resetar a senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String senha = new String(txtSenha.getPassword()).trim();
        if (!senha.isEmpty()) {
            if (senha.length() < 6) {
                JOptionPane.showMessageDialog(this, "A nova senha deve ter pelo menos 6 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (usuarioDAO.isSenhaPadraoInsegura(senha)) {
                JOptionPane.showMessageDialog(this, "Não utilize senhas padrão fracas (como 123456 ou admin123).", "Segurança", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            usuarioDAO.atualizarUsuarioEquipe(
                Integer.parseInt(txtId.getText()),
                txtNome.getText().trim(),
                txtEmail.getText().trim(),
                cbPerfil.getSelectedItem().toString(),
                chkAtivo.isSelected(),
                senha
            );
            JOptionPane.showMessageDialog(this, "Dados e/ou senha atualizados com sucesso!");
            limparCampos();
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idAlvo = Integer.parseInt(txtId.getText());
        if (idAlvo == adminLogadoId) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir sua própria conta de Administrador.", "Bloqueado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(this, "Deseja excluir este usuário interno?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                usuarioDAO.excluirUsuario(idAlvo);
                JOptionPane.showMessageDialog(this, "Usuário excluído!");
                limparCampos();
                carregarTabela();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtEmail.setText("");
        txtSenha.setText("");
        txtEspecialidade.setText("");
        cbPerfil.setSelectedIndex(0);
        chkAtivo.setSelected(true);
        tabela.clearSelection();
    }
}