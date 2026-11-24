/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.config.Conexao;
import br.com.barbearia.barberstyle.dao.ClienteDAO;
import br.com.barbearia.barberstyle.dao.UsuarioDAO;
import br.com.barbearia.barberstyle.model.Cliente;
import br.com.barbearia.barberstyle.model.Usuario;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class JanelaPrincipal extends JFrame {

    private JPanel painelConteudo;
    private JLabel lblStatusBanco;
    private Usuario usuarioLogado;

    private JMenuItem itemPainel;
    private JMenu menuCadastros;
    private JMenuItem itemClientes;
    private JMenuItem itemBarbeiros;
    private JMenuItem itemServicos;
    private JMenuItem itemEspecialidades;
    private JMenuItem itemEquipe;
    private JMenu menuAgendamentos;
    private JMenuItem itemSessao;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public JanelaPrincipal() {
        Color corFundo = new Color(14, 15, 22);     // #0E0F16
        Color corCard = new Color(22, 24, 36);      // #161824
        Color corBorda = new Color(45, 48, 68);     // #2D3044
        Color corDourado = new Color(218, 165, 32); // #DAA520

        getRootPane().putClientProperty("JRootPane.titleBarBackground", corFundo);
        getRootPane().putClientProperty("JRootPane.titleBarForeground", corDourado);

        UIManager.put("MenuBar.background", corFundo);
        UIManager.put("MenuBar.borderColor", new Color(34, 37, 54));
        UIManager.put("Menu.background", corFundo);
        UIManager.put("Menu.foreground", new Color(235, 240, 250));
        UIManager.put("MenuItem.background", corCard);
        UIManager.put("MenuItem.foreground", new Color(225, 230, 240));
        UIManager.put("PopupMenu.background", corCard);
        UIManager.put("PopupMenu.borderColor", corBorda);

        UIManager.put("Panel.background", corFundo);
        UIManager.put("ScrollPane.background", corFundo);
        UIManager.put("Viewport.background", corFundo);
        UIManager.put("TitledBorder.titleColor", corDourado);
        UIManager.put("TitledBorder.borderColor", corBorda);

        UIManager.put("Table.background", corCard);
        UIManager.put("Table.foreground", new Color(225, 230, 240));
        UIManager.put("Table.gridColor", corBorda);
        UIManager.put("Table.selectionBackground", new Color(79, 70, 229));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", new Color(28, 31, 46));
        UIManager.put("TableHeader.foreground", corDourado);

        UIManager.put("TextField.background", corCard);
        UIManager.put("PasswordField.background", corCard);
        UIManager.put("ComboBox.background", corCard);
        UIManager.put("TabbedPane.background", corFundo);
        UIManager.put("TabbedPane.contentAreaColor", corFundo);

        setTitle("BarberStyle - Sistema de Gerenciamento de Barbearia");
        setSize(1120, 720);
        setMinimumSize(new Dimension(940, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarMenus();

        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(corFundo);
        setContentPane(painelConteudo);

        atualizarPermissoesEAbrirTela();
    }

    private void inicializarMenus() {
        Color corFundo = new Color(14, 15, 22);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(corFundo);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(34, 37, 54)));

        JMenu menuArquivo = new JMenu("Início");
        JMenuItem itemVitrine = new JMenuItem("Vitrine Pública (Serviços & Avaliações)");
        itemVitrine.addActionListener(e -> mostrarVitrinePublica());

        itemPainel = new JMenuItem("Meu Painel");
        itemPainel.addActionListener(e -> atualizarPermissoesEAbrirTela());

        JMenuItem itemTestarConexao = new JMenuItem("Testar Conexão MySQL");
        itemTestarConexao.addActionListener(e -> testarConexaoComAlerta());

        itemSessao = new JMenuItem("Entrar / Área Restrita");
        itemSessao.addActionListener(e -> {
            if (usuarioLogado == null) {
                abrirDialogoLogin();
            } else {
                usuarioLogado = null;
                JOptionPane.showMessageDialog(this, "Você saiu da sua conta.");
                atualizarPermissoesEAbrirTela();
            }
        });

        JMenuItem itemSair = new JMenuItem("Fechar Sistema");
        itemSair.addActionListener(e -> System.exit(0));

        menuArquivo.add(itemVitrine);
        menuArquivo.add(itemPainel);
        menuArquivo.addSeparator();
        menuArquivo.add(itemTestarConexao);
        menuArquivo.add(itemSessao);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        menuCadastros = new JMenu("Cadastros");
        itemClientes = new JMenuItem("Clientes");
        itemClientes.addActionListener(e -> abrirPainel(new PainelCliente()));

        itemBarbeiros = new JMenuItem("Barbeiros");
        itemBarbeiros.addActionListener(e -> abrirPainel(new PainelBarbeiro()));

        itemServicos = new JMenuItem("Serviços");
        itemServicos.addActionListener(e -> abrirPainel(new PainelServicos()));

        itemEspecialidades = new JMenuItem("Especialidades");
        itemEspecialidades.addActionListener(e -> abrirPainel(new PainelEspecialidades()));

        itemEquipe = new JMenuItem("Equipe & Acessos (Secretária / Admin)");
        itemEquipe.addActionListener(e -> abrirPainel(new PainelUsuarios(usuarioLogado.getId())));

        menuCadastros.add(itemClientes);
        menuCadastros.add(itemBarbeiros);
        menuCadastros.add(itemServicos);
        menuCadastros.add(itemEspecialidades);
        menuCadastros.addSeparator();
        menuCadastros.add(itemEquipe);

        menuAgendamentos = new JMenu("Agendamentos");
        JMenuItem itemGerenciarAgenda = new JMenuItem("Gerenciar Agenda");
        itemGerenciarAgenda.addActionListener(e -> abrirPainel(new PainelAgendamentos(usuarioLogado)));
        menuAgendamentos.add(itemGerenciarAgenda);

        menuBar.add(menuArquivo);
        menuBar.add(menuCadastros);
        menuBar.add(menuAgendamentos);

        Color corTextoMenu = new Color(235, 240, 250);
        for (JMenu menu : new JMenu[]{menuArquivo, menuCadastros, menuAgendamentos}) {
            menu.setFont(menu.getFont().deriveFont(Font.BOLD, 14f));
            menu.setForeground(corTextoMenu);
            menu.setMargin(new Insets(4, 10, 4, 10));
            menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        for (JMenuItem item : new JMenuItem[]{
            itemVitrine, itemPainel, itemTestarConexao, itemSessao, itemSair,
            itemClientes, itemBarbeiros, itemServicos, itemEspecialidades, itemEquipe, itemGerenciarAgenda
        }) {
            item.setFont(item.getFont().deriveFont(Font.PLAIN, 13.5f));
        }

        setJMenuBar(menuBar);
    }

    private void atualizarPermissoesEAbrirTela() {
        if (usuarioLogado == null) {
            setTitle("BarberStyle — Vitrine Pública");
            itemPainel.setVisible(false);
            menuCadastros.setVisible(false);
            menuAgendamentos.setVisible(false);
            itemSessao.setText("Entrar / Área Restrita");
            mostrarVitrinePublica();
            return;
        }

        String perfil = usuarioLogado.getPerfil();
        setTitle("BarberStyle — " + usuarioLogado.getNome() + " [" + perfil + "]");
        itemPainel.setVisible(true);
        itemSessao.setText("Sair (" + usuarioLogado.getNome() + ")");

        switch (perfil) {
            case "ADMINISTRADOR" -> {
                menuCadastros.setVisible(true);
                itemClientes.setVisible(true);
                itemBarbeiros.setVisible(true);
                itemServicos.setVisible(true);
                itemEspecialidades.setVisible(true);
                itemEquipe.setVisible(true);
                menuAgendamentos.setVisible(true);
                mostrarDashboardLogado();
            }
            case "SECRETARIA" -> {
                menuCadastros.setVisible(true);
                itemClientes.setVisible(true);
                itemBarbeiros.setVisible(false);
                itemServicos.setVisible(false);
                itemEspecialidades.setVisible(false);
                itemEquipe.setVisible(false);
                menuAgendamentos.setVisible(true);
                mostrarDashboardLogado();
            }
            case "BARBEIRO" -> {
                menuCadastros.setVisible(false);
                menuAgendamentos.setVisible(true);
                abrirPainel(new PainelAgendamentos(usuarioLogado));
            }
            default -> { // CLIENTE
                menuCadastros.setVisible(false);
                menuAgendamentos.setVisible(true);
                if (usuarioLogado instanceof Cliente cli) {
                    abrirPainel(new PainelAreaCliente(cli));
                } else {
                    abrirPainel(new PainelAgendamentos(usuarioLogado));
                }
            }
        }
    }

    private void mostrarVitrinePublica() {
        abrirPainel(new PainelVitrinePublica(() -> {
            if (usuarioLogado == null) {
                abrirDialogoLogin();
            } else {
                atualizarPermissoesEAbrirTela();
            }
        }));
    }

    private boolean exigirTrocaSenhaPadrao(Usuario u) {
        JPasswordField txtNova = new JPasswordField(20);
        JPasswordField txtConf = new JPasswordField(20);

        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.add(new JLabel("<html><b>Segurança Obrigatória:</b> Você entrou com uma senha padrão.<br>Defina uma nova senha pessoal (mín. 6 caracteres) para continuar:</html>"));
        p.add(new JLabel("Nova Senha *:"));
        p.add(txtNova);
        p.add(new JLabel("Confirme a Nova Senha *:"));
        p.add(txtConf);

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, p, "Redefinir Senha Padrão", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (op != JOptionPane.OK_OPTION) {
                return false;
            }
            String nova = new String(txtNova.getPassword()).trim();
            String conf = new String(txtConf.getPassword()).trim();

            if (nova.length() < 6) {
                JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (usuarioDAO.isSenhaPadraoInsegura(nova)) {
                JOptionPane.showMessageDialog(this, "Escolha uma senha diferente das senhas padrão (123456 / admin123).", "Segurança", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (!nova.equals(conf)) {
                JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Aviso", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            try {
                usuarioDAO.alterarSenhaUsuario(u.getId(), nova);
                JOptionPane.showMessageDialog(this, "Senha atualizada com segurança!");
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar senha: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    private void abrirDialogoLogin() {
        JTabbedPane abas = new JTabbedPane();
        abas.setPreferredSize(new Dimension(360, 290));

        JTextField txtEmailLogin = new JTextField(25);
        JPasswordField txtSenhaLogin = new JPasswordField(25);

        JPanel camposLogin = new JPanel(new GridLayout(0, 1, 6, 6));
        camposLogin.add(new JLabel("E-mail cadastrado:"));
        camposLogin.add(txtEmailLogin);
        camposLogin.add(Box.createVerticalStrut(4));
        camposLogin.add(new JLabel("Senha:"));
        camposLogin.add(txtSenhaLogin);

        JPanel painelLogin = new JPanel(new BorderLayout());
        painelLogin.setBorder(new EmptyBorder(16, 14, 14, 14));
        painelLogin.add(camposLogin, BorderLayout.NORTH);

        JTextField txtNomeCad = new JTextField(25);
        JTextField txtTelCad = new JTextField(25);
        txtTelCad.putClientProperty("JTextField.placeholderText", "Ex: 55(21)97935-2189 ou +351 912 345 678");
        JTextField txtEmailCad = new JTextField(25);
        JPasswordField txtSenhaCad = new JPasswordField(25);
        JPasswordField txtConfirmaSenhaCad = new JPasswordField(25);

        JPanel camposCadastro = new JPanel(new GridLayout(0, 1, 4, 4));
        camposCadastro.add(new JLabel("Nome Completo *:"));
        camposCadastro.add(txtNomeCad);
        camposCadastro.add(new JLabel("Telefone / WhatsApp *:"));
        camposCadastro.add(txtTelCad);
        camposCadastro.add(new JLabel("E-mail *:"));
        camposCadastro.add(txtEmailCad);
        camposCadastro.add(new JLabel("Crie uma Senha (mín. 6 caracteres) *:"));
        camposCadastro.add(txtSenhaCad);
        camposCadastro.add(new JLabel("Confirme a Senha *:"));
        camposCadastro.add(txtConfirmaSenhaCad);

        JPanel painelCadastro = new JPanel(new BorderLayout());
        painelCadastro.setBorder(new EmptyBorder(12, 14, 12, 14));
        painelCadastro.add(camposCadastro, BorderLayout.NORTH);

        abas.addTab("Já tenho conta", painelLogin);
        abas.addTab("Criar Conta (Cliente)", painelCadastro);

        int op = JOptionPane.showConfirmDialog(
                this, abas, "Acesso ao Sistema — BarberStyle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (op == JOptionPane.OK_OPTION) {
            if (abas.getSelectedIndex() == 0) {
                String senhaDigitada = new String(txtSenhaLogin.getPassword());
                try {
                    Usuario u = usuarioDAO.autenticar(txtEmailLogin.getText(), senhaDigitada);
                    if (u != null) {
                        if (usuarioDAO.isSenhaPadraoInsegura(senhaDigitada)) {
                            if (!exigirTrocaSenhaPadrao(u)) {
                                return;
                            }
                        }
                        this.usuarioLogado = u;
                        atualizarPermissoesEAbrirTela();
                    } else {
                        JOptionPane.showMessageDialog(this, "E-mail ou senha inválidos (ou usuário inativo).", "Falha no Login", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao autenticar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String nome = txtNomeCad.getText().trim();
                String telefone = txtTelCad.getText().trim();
                String email = txtEmailCad.getText().trim().toLowerCase();
                String senha = new String(txtSenhaCad.getPassword()).trim();
                String confirma = new String(txtConfirmaSenhaCad.getPassword()).trim();

                if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha Nome, Telefone e E-mail!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String apenasDigitos = telefone.replaceAll("\\D", "");
                if (!telefone.matches("[+\\d()\\s-]+") || apenasDigitos.length() < 8 || apenasDigitos.length() > 18) {
                    JOptionPane.showMessageDialog(this,
                            "Telefone inválido! Informe com código do país e DDD.\nExemplos: 55(21)97935-2189 ou +351 912 345 678",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (senha.length() < 6) {
                    JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.", "Segurança", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!senha.equals(confirma)) {
                    JOptionPane.showMessageDialog(this, "As senhas digitadas não coincidem.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    Cliente novoCliente = new Cliente(0, nome, email, senha, true, telefone);
                    clienteDAO.inserir(novoCliente);

                    Usuario u = usuarioDAO.autenticar(email, senha);
                    if (u != null) {
                        this.usuarioLogado = u;
                        JOptionPane.showMessageDialog(this, "Conta criada com sucesso! Bem-vindo(a), " + nome + "!");
                        atualizarPermissoesEAbrirTela();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Não foi possível criar a conta (verifique se o e-mail já está em uso):\n" + ex.getMessage(), "Erro no Cadastro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void mostrarDashboardLogado() {
        String perfil = usuarioLogado.getPerfil();
        if ("ADMINISTRADOR".equals(perfil)) {
            abrirPainel(new PainelDashboardAdmin(usuarioLogado, this::abrirPainel));
            return;
        }

        // Painel da Secretaria
        JPanel painelHome = new JPanel(new BorderLayout(15, 15));
        painelHome.setBorder(new EmptyBorder(30, 40, 25, 40));

        JPanel painelTopo = new JPanel();
        painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("PAINEL DE ATENDIMENTO — " + perfil);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(218, 165, 32));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Bem-vindo(a), " + usuarioLogado.getNome());
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTopo.add(lblTitulo);
        painelTopo.add(Box.createVerticalStrut(6));
        painelTopo.add(lblSubtitulo);
        painelHome.add(painelTopo, BorderLayout.NORTH);

        JPanel painelCards = new JPanel(new GridLayout(1, 3, 20, 20));
        painelCards.setBorder(new EmptyBorder(30, 20, 30, 20));

        JButton cardAgenda = criarCardAtalho("Agendamentos", "Marcar horários, consultar agenda e atualizar status");
        cardAgenda.addActionListener(e -> abrirPainel(new PainelAgendamentos(usuarioLogado)));
        painelCards.add(cardAgenda);

        JButton cardClientes = criarCardAtalho("Clientes", "Cadastrar, editar e consultar clientes da barbearia");
        cardClientes.addActionListener(e -> abrirPainel(new PainelCliente()));
        painelCards.add(cardClientes);

        JButton cardVitrine = criarCardAtalho("Vitrine Pública", "Visualizar catálogo público e avaliações em estrelas");
        cardVitrine.addActionListener(e -> mostrarVitrinePublica());
        painelCards.add(cardVitrine);

        painelHome.add(painelCards, BorderLayout.CENTER);

        lblStatusBanco = new JLabel("● Conectado ao MySQL (barber_style)");
        lblStatusBanco.setForeground(new Color(46, 204, 113));
        lblStatusBanco.setFont(new Font("Segoe UI", Font.BOLD, 12));
        painelHome.add(lblStatusBanco, BorderLayout.SOUTH);

        abrirPainel(painelHome);
    }

    private JButton criarCardAtalho(String titulo, String descricao) {
        JButton btn = new JButton("<html><center><h2 style='margin:0; color:#DAA520;'>" + titulo + "</h2><p style='margin-top:8px; color:#CCCCCC;'>" + descricao + "</p></center></html>");
        btn.setBackground(new Color(22, 24, 36));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 48, 68), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void abrirPainel(JPanel novoPainel) {
        aplicarTemaEscuroRecursivo(novoPainel);
        painelConteudo.removeAll();
        painelConteudo.add(novoPainel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void aplicarTemaEscuroRecursivo(Container container) {
        Color corFundo = new Color(14, 15, 22);
        Color corCard = new Color(22, 24, 36);
        Color corBorda = new Color(45, 48, 68);
        Color corDourado = new Color(218, 165, 32);

        if (container instanceof JPanel painel) {
            if (painel.getBorder() instanceof TitledBorder tb) {
                painel.setBackground(corCard);
                tb.setTitleColor(corDourado);
                tb.setBorder(BorderFactory.createLineBorder(corBorda, 1));
            } else if (painel.isOpaque()) {
                Color bg = painel.getBackground();
                if (bg == null || (bg.getRed() > 40 && bg.getGreen() > 40 && bg.getBlue() > 40)) {
                    painel.setBackground(corFundo);
                }
            }
        } else if (container instanceof JScrollPane sp) {
            sp.setBackground(corFundo);
            sp.getViewport().setBackground(corCard);
            sp.setBorder(BorderFactory.createLineBorder(corBorda, 1));
        } else if (container instanceof JTable tabela) {
            tabela.setBackground(corCard);
            tabela.setForeground(new Color(225, 230, 240));
            tabela.setGridColor(corBorda);
            if (tabela.getTableHeader() != null) {
                tabela.getTableHeader().setBackground(new Color(28, 31, 46));
                tabela.getTableHeader().setForeground(corDourado);
            }
        } else if (container instanceof JLabel lbl && lbl.getFont() != null && lbl.getFont().getSize() >= 18) {
            lbl.setForeground(corDourado);
        }

        for (Component comp : container.getComponents()) {
            if (comp instanceof Container filho) {
                aplicarTemaEscuroRecursivo(filho);
            }
        }
    }

    private void testarConexaoComAlerta() {
        try (Connection conn = Conexao.conectar()) {
            if (conn != null && !conn.isClosed()) {
                JOptionPane.showMessageDialog(this, "Conexão com o banco 'barber_style' realizada com sucesso!", "Status MySQL", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar no MySQL:\n" + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");
        FlatDarkLaf.setup();
        EventQueue.invokeLater(() -> new JanelaPrincipal().setVisible(true));
    }
}