/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.AgendamentoDAO;
import br.com.barbearia.barberstyle.dao.ClienteDAO;
import br.com.barbearia.barberstyle.dao.ServicoDAO;
import br.com.barbearia.barberstyle.model.Agendamento;
import br.com.barbearia.barberstyle.model.Cliente;
import br.com.barbearia.barberstyle.model.Servico;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PainelAreaCliente extends JPanel {

    private final Cliente clienteLogado;
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JLabel lblProximoHorario;
    private JLabel lblTotalConcluidos;
    private JTabbedPane abasCliente;

    public PainelAreaCliente(Cliente clienteLogado) {
        this.clienteLogado = clienteLogado;
        inicializarComponentes();
        atualizarResumoCliente();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));

        JLabel lblSaudacao = new JLabel("Área do Cliente — Olá, " + clienteLogado.getNome());
        lblSaudacao.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSaudacao.setForeground(new Color(218, 165, 32));
        painelTopo.add(lblSaudacao, BorderLayout.NORTH);

        JPanel painelResumo = new JPanel(new GridLayout(1, 2, 15, 0));
        lblProximoHorario = new JLabel("Carregando próximo horário...");
        lblTotalConcluidos = new JLabel("Carregando histórico...");

        painelResumo.add(criarCardResumo("Próximo Atendimento Agendado", lblProximoHorario));
        painelResumo.add(criarCardResumo("Atendimentos Concluídos na Barbearia", lblTotalConcluidos));
        painelTopo.add(painelResumo, BorderLayout.CENTER);

        add(painelTopo, BorderLayout.NORTH);

        abasCliente = new JTabbedPane();
        abasCliente.setFont(new Font("Segoe UI", Font.BOLD, 13));

        PainelAgendamentos painelAgendamentosCliente = new PainelAgendamentos(clienteLogado);
        abasCliente.addTab("Meus Agendamentos & Avaliações", painelAgendamentosCliente);
        abasCliente.addTab("Catálogo de Serviços & Preços", criarAbaServicos());
        abasCliente.addTab("Meus Dados & Senha", criarAbaMeusDados());

        abasCliente.addChangeListener(e -> atualizarResumoCliente());
        add(abasCliente, BorderLayout.CENTER);
    }

    private JPanel criarCardResumo(String titulo, JLabel lblValor) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(new Color(24, 24, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 58, 30), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(218, 165, 32));

        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblValor.setForeground(Color.WHITE);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private void atualizarResumoCliente() {
        try {
            List<Agendamento> todos = agendamentoDAO.listarTodos();
            LocalDateTime agora = LocalDateTime.now();

            Agendamento proximo = todos.stream()
                    .filter(a -> a.getCliente() != null && (a.getCliente().getId() == clienteLogado.getId()
                            || a.getCliente().getNome().equalsIgnoreCase(clienteLogado.getNome())))
                    .filter(a -> "AGENDADO".equalsIgnoreCase(a.getStatus()) && a.getDataHora().isAfter(agora))
                    .min(Comparator.comparing(Agendamento::getDataHora))
                    .orElse(null);

            long concluidos = todos.stream()
                    .filter(a -> a.getCliente() != null && (a.getCliente().getId() == clienteLogado.getId()
                            || a.getCliente().getNome().equalsIgnoreCase(clienteLogado.getNome())))
                    .filter(a -> "CONCLUIDO".equalsIgnoreCase(a.getStatus()))
                    .count();

            if (proximo != null) {
                lblProximoHorario.setText(proximo.getDataHora().format(formatter) + " — "
                        + proximo.getServico().getNome() + " com " + proximo.getBarbeiro().getNome());
            } else {
                lblProximoHorario.setText("Nenhum horário futuro marcado no momento.");
            }

            lblTotalConcluidos.setText(concluidos + (concluidos == 1 ? " atendimento realizado" : " atendimentos realizados"));
        } catch (SQLException ex) {
            lblProximoHorario.setText("Não foi possível carregar resumo.");
        }
    }

    private JPanel criarAbaServicos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(12, 12, 12, 12));

        DefaultTableModel modelo = new DefaultTableModel(new String[]{"Serviço", "Descrição", "Duração", "Valor (R$)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            for (Servico s : servicoDAO.listarTodos()) {
                if (!s.isAtivo()) continue;
                modelo.addRow(new Object[]{
                    s.getNome(),
                    s.getDescricao(),
                    s.getDuracaoMinutos() + " min",
                    String.format("R$ %.2f", s.getPreco())
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar serviços: " + ex.getMessage());
        }

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(26);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnIrAgendar = new JButton("Agendar um Serviço Agora");
        btnIrAgendar.setBackground(new Color(218, 165, 32));
        btnIrAgendar.setForeground(Color.BLACK);
        btnIrAgendar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIrAgendar.addActionListener(e -> abasCliente.setSelectedIndex(0));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnIrAgendar);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarAbaMeusDados() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Atualizar Meus Dados Cadastrais e Senha"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNome = new JTextField(clienteLogado.getNome(), 28);
        JTextField txtTelefone = new JTextField(clienteLogado.getTelefone(), 28);
        JTextField txtEmail = new JTextField(clienteLogado.getEmail(), 28);
        JPasswordField txtNovaSenha = new JPasswordField(28);
        JPasswordField txtConfirmaSenha = new JPasswordField(28);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nome Completo *:"), gbc);
        gbc.gridx = 1;
        form.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Telefone / WhatsApp *:"), gbc);
        gbc.gridx = 1;
        form.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("E-mail de Acesso *:"), gbc);
        gbc.gridx = 1;
        form.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Nova Senha (deixe em branco para manter):"), gbc);
        gbc.gridx = 1;
        form.add(txtNovaSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Confirmar Nova Senha:"), gbc);
        gbc.gridx = 1;
        form.add(txtConfirmaSenha, gbc);

        JButton btnSalvarDados = new JButton("Salvar Alterações do Perfil");
        btnSalvarDados.setBackground(new Color(40, 167, 69));
        btnSalvarDados.setForeground(Color.WHITE);
        btnSalvarDados.setFont(new Font("Segoe UI", Font.BOLD, 13));

        gbc.gridx = 1; gbc.gridy = 5;
        form.add(btnSalvarDados, gbc);

        btnSalvarDados.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String tel = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim().toLowerCase();
            String novaSenha = new String(txtNovaSenha.getPassword()).trim();
            String confSenha = new String(txtConfirmaSenha.getPassword()).trim();

            if (nome.isEmpty() || tel.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha Nome, Telefone e E-mail.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String digitos = tel.replaceAll("\\D", "");
            if (!tel.matches("[+\\d()\\s-]+") || digitos.length() < 8 || digitos.length() > 18) {
                JOptionPane.showMessageDialog(this, "Telefone inválido! Informe entre 8 e 18 dígitos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!novaSenha.isEmpty()) {
                if (novaSenha.length() < 6) {
                    JOptionPane.showMessageDialog(this, "A nova senha deve ter pelo menos 6 caracteres.", "Segurança", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!novaSenha.equals(confSenha)) {
                    JOptionPane.showMessageDialog(this, "A confirmação de senha não confere.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            try {
                clienteDAO.atualizarPerfilCliente(clienteLogado.getId(), nome, email, tel, novaSenha);
                clienteLogado.setNome(nome);
                clienteLogado.setTelefone(tel);
                clienteLogado.setEmail(email);
                txtNovaSenha.setText("");
                txtConfirmaSenha.setText("");
                JOptionPane.showMessageDialog(this, "Seus dados foram atualizados com sucesso!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar perfil: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        wrapper.add(form, BorderLayout.NORTH);
        return wrapper;
    }
}