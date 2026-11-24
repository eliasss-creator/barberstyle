/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.*;
import br.com.barbearia.barberstyle.model.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

/**
 * Vitrine publica da barbearia: identidade visual lateral com foto e icones Ikonli,
 * catalogo ilustrado de servicos, avaliacoes em estrelas e agendamento rapido.
 * @author FantasmaDeZion
 */
public class PainelVitrinePublica extends JPanel {

    private final Runnable acaoAbrirLogin;
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    private JPanel painelGridServicos;
    private DefaultListModel<String> modeloComentarios;

    public PainelVitrinePublica(Runnable acaoAbrirLogin) {
        this.acaoAbrirLogin = acaoAbrirLogin;
        setLayout(new BorderLayout(16, 14));
        setBackground(new Color(14, 15, 22));
        setBorder(new EmptyBorder(18, 22, 18, 22));

        montarTopo();
        montarLateralIdentidade();
        montarCentro();
        montarRodapeAvaliacoes();
        carregarDados();
    }

    private void montarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("BARBER STYLE — CATÁLOGO DE SERVIÇOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(218, 165, 32));

        JLabel lblSub = new JLabel("Escolha seu estilo e agende seu horário na hora, sem precisar de cadastro prévio.");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(new Color(235, 240, 248));

        titulos.add(lblTitulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(lblSub);

        JButton btnEntrar = new JButton("Entrar / Área Restrita");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEntrar.setBackground(new Color(218, 165, 32));
        btnEntrar.setForeground(new Color(14, 15, 22));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setBorder(new EmptyBorder(10, 18, 10, 18));
        btnEntrar.addActionListener(e -> acaoAbrirLogin.run());

        topo.add(titulos, BorderLayout.WEST);
        topo.add(btnEntrar, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);
    }

    private void montarLateralIdentidade() {
        BannerIdentidadePanel lateral = new BannerIdentidadePanel();
        lateral.setPreferredSize(new Dimension(235, 0));
        add(lateral, BorderLayout.WEST);
    }

    private void montarCentro() {
        painelGridServicos = new JPanel(new GridLayout(0, 2, 16, 16));
        painelGridServicos.setBackground(new Color(14, 15, 22));
        painelGridServicos.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(painelGridServicos);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBackground(new Color(14, 15, 22));
        scroll.getViewport().setBackground(new Color(14, 15, 22));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(45, 48, 68)),
                " Serviços Disponíveis "
        ));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void montarRodapeAvaliacoes() {
        modeloComentarios = new DefaultListModel<>();
        JList<String> listaAvaliacoes = new JList<>(modeloComentarios);
        listaAvaliacoes.setBackground(new Color(22, 24, 36));
        listaAvaliacoes.setForeground(new Color(240, 220, 145));
        listaAvaliacoes.setSelectionBackground(new Color(45, 48, 68));
        listaAvaliacoes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        listaAvaliacoes.setBorder(new EmptyBorder(6, 10, 6, 10));

        JScrollPane scrollAvaliacoes = new JScrollPane(listaAvaliacoes);
        scrollAvaliacoes.setBackground(new Color(22, 24, 36));
        scrollAvaliacoes.getViewport().setBackground(new Color(22, 24, 36));
        scrollAvaliacoes.setPreferredSize(new Dimension(0, 95));
        scrollAvaliacoes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(45, 48, 68)),
                " Avaliações Recentes dos Clientes "
        ));
        add(scrollAvaliacoes, BorderLayout.SOUTH);
    }

    public final void carregarDados() {
        painelGridServicos.removeAll();
        modeloComentarios.clear();

        try {
            List<Servico> servicos = servicoDAO.listarTodos();
            for (Servico s : servicos) {
                if (!s.isAtivo()) continue;
                double media = avaliacaoDAO.calcularMediaPorServico(s.getId());
                painelGridServicos.add(criarCardServico(s, media));
            }

            List<String> comentarios = avaliacaoDAO.listarUltimosComentarios(8);
            if (comentarios.isEmpty()) {
                modeloComentarios.addElement("• Ainda não há avaliações registradas. Seja o primeiro a avaliar após seu atendimento!");
            } else {
                comentarios.forEach(modeloComentarios::addElement);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar vitrine: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        painelGridServicos.revalidate();
        painelGridServicos.repaint();
    }

    private JPanel criarCardServico(Servico servico, double mediaEstrelas) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(22, 24, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 52, 42), 1),
                new EmptyBorder(0, 0, 12, 0)
        ));

        BannerServicoPanel bannerCorte = new BannerServicoPanel(servico.getNome());
        bannerCorte.setPreferredSize(new Dimension(0, 86));
        card.add(bannerCorte, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(4, 6));
        corpo.setOpaque(false);
        corpo.setBorder(new EmptyBorder(4, 14, 0, 14));

        int cheias = (int) Math.round(mediaEstrelas);
        String estrelasTexto = mediaEstrelas > 0
                ? "★".repeat(cheias) + "☆".repeat(5 - cheias) + String.format(" (%.1f)", mediaEstrelas)
                : "☆☆☆☆☆ (Sem avaliações)";

        JLabel lblTopo = new JLabel("<html><b style='font-size:14px; color:#DAA520;'>" + servico.getNome() + "</b><br>"
                + "<span style='color:#FFD700; font-size:12px;'>" + estrelasTexto + "</span></html>");

        String desc = (servico.getDescricao() != null && !servico.getDescricao().isBlank())
                ? servico.getDescricao()
                : "Atendimento profissional personalizado.";

        JLabel lblDesc = new JLabel("<html><div style='color:#E6ECF5; width:260px; font-size:12px; font-weight:bold; line-height:1.35;'>"
                + desc + "</div></html>");

        corpo.add(lblTopo, BorderLayout.NORTH);
        corpo.add(lblDesc, BorderLayout.CENTER);
        card.add(corpo, BorderLayout.CENTER);

        JPanel rodapeCard = new JPanel(new BorderLayout(5, 6));
        rodapeCard.setOpaque(false);
        rodapeCard.setBorder(new EmptyBorder(4, 14, 0, 14));

        JLabel lblPreco = new JLabel(String.format("R$ %.2f  •  %d min", servico.getPreco(), servico.getDuracaoMinutos()));
        lblPreco.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPreco.setForeground(new Color(46, 204, 113));

        JButton btnAgendar = new JButton("Agendar Agora");
        btnAgendar.setBackground(new Color(34, 37, 54));
        btnAgendar.setForeground(new Color(218, 165, 32));
        btnAgendar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgendar.setFocusPainted(false);
        btnAgendar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 108, 28), 1),
                new EmptyBorder(7, 12, 7, 12)
        ));
        btnAgendar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgendar.addActionListener(e -> abrirDialogoAgendamentoRapido(servico));

        rodapeCard.add(lblPreco, BorderLayout.NORTH);
        rodapeCard.add(btnAgendar, BorderLayout.SOUTH);
        card.add(rodapeCard, BorderLayout.SOUTH);

        return card;
    }

    private void abrirDialogoAgendamentoRapido(Servico servico) {
        JTextField txtNome = new JTextField(20);
        JTextField txtTelefone = new JTextField(18);
        txtTelefone.putClientProperty("JTextField.placeholderText", "Ex: 55(21)97935-2189 ou +1 (555) 123-4567");
        JTextField txtEmail = new JTextField(20);
        JPasswordField txtSenha = new JPasswordField(20);
        JComboBox<Barbeiro> cbBarbeiro = new JComboBox<>();
        JTextField txtDataHora = new JTextField(
                LocalDateTime.now().plusHours(2).withMinute(0).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                16
        );

        try {
            for (Barbeiro b : barbeiroDAO.listarTodos()) {
                if (b.isAtivo()) cbBarbeiro.addItem(b);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar barbeiros: " + ex.getMessage());
            return;
        }

        if (cbBarbeiro.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum barbeiro ativo disponível no momento.");
            return;
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Serviço escolhido:"));
        form.add(new JLabel(servico.getNome() + " (R$ " + servico.getPreco() + ")"));
        form.add(new JLabel("Seu Nome Completo *:"));
        form.add(txtNome);
        form.add(new JLabel("Seu Telefone / WhatsApp *:"));
        form.add(txtTelefone);
        form.add(new JLabel("Seu E-mail (opcional):"));
        form.add(txtEmail);
        form.add(new JLabel("Crie uma Senha (mín. 6 caracteres) *:"));
        form.add(txtSenha);
        form.add(new JLabel("Profissional (Barbeiro) *:"));
        form.add(cbBarbeiro);
        form.add(new JLabel("Data e Hora (dd/MM/yyyy HH:mm) *:"));
        form.add(txtDataHora);

        int opcao = JOptionPane.showConfirmDialog(
                this, form, "Agendamento Rápido — Crie seu Acesso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (opcao == JOptionPane.OK_OPTION) {
            if (txtNome.getText().isBlank() || txtTelefone.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe seu nome e telefone para confirmar o agendamento.");
                return;
            }

            String telefone = txtTelefone.getText().trim();
            String apenasDigitos = telefone.replaceAll("\\D", "");
            if (!telefone.matches("[+\\d()\\s-]+") || apenasDigitos.length() < 8 || apenasDigitos.length() > 18) {
                JOptionPane.showMessageDialog(this,
                        "Telefone inválido! Informe um número válido com código do país e DDD.\nExemplos: 55(21)97935-2189 ou +351 912 345 678",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String senhaEscolhida = new String(txtSenha.getPassword()).trim();
            if (senhaEscolhida.length() < 6) {
                JOptionPane.showMessageDialog(this,
                        "Crie uma senha pessoal com pelo menos 6 caracteres para acessar e avaliar seus agendamentos.",
                        "Segurança", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDateTime dataHora = LocalDateTime.parse(txtDataHora.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                String[] dadosCliente = clienteDAO.buscarOuCriarVisitante(
                        txtNome.getText().trim(), telefone, txtEmail.getText().trim(), senhaEscolhida
                );
                int clienteId = Integer.parseInt(dadosCliente[0]);
                String emailLogin = dadosCliente[1];

                Cliente cli = new Cliente();
                cli.setId(clienteId);

                Barbeiro barb = (Barbeiro) cbBarbeiro.getSelectedItem();

                Agendamento ag = new Agendamento();
                ag.setCliente(cli);
                ag.setBarbeiro(barb);
                ag.setServico(servico);
                ag.setDataHora(dataHora);
                ag.setPrecoContratado(servico.getPreco());
                ag.setDuracaoContratada(servico.getDuracaoMinutos());
                ag.setStatus("AGENDADO");

                agendamentoDAO.inserir(ag);

                String mensagemConfirmacao = String.format(
                        "Agendamento confirmado com sucesso!\nServiço: %s\nProfissional: %s\nData/Hora: %s\n\nPara acompanhar ou avaliar com estrelas após o corte, clique em 'Entrar / Área Restrita' usando:\n• E-mail de login: %s\n• Senha: a senha pessoal que você acabou de criar",
                        servico.getNome(), barb.getNome(), txtDataHora.getText(), emailLogin
                );

                JOptionPane.showMessageDialog(this, mensagemConfirmacao, "Horário Reservado", JOptionPane.INFORMATION_MESSAGE);

            } catch (DateTimeParseException dtEx) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy HH:mm (ex: 22/11/2026 15:30).");
            } catch (SQLException sqlEx) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar agendamento: " + sqlEx.getMessage());
            }
        }
    }

    private static class BannerIdentidadePanel extends JPanel {

        private Image imagemLateral;

        public BannerIdentidadePanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(218, 22, 16, 22));

            String[] candidatos = {
                "/imgs/barbearia_lateral.jpg",
                "/imgs/barbearia_lateral.jpg.jpg",
                "/imgs/barbearia_lateral.png",
                "/imgs/barbearia_lateral.jpeg",
                "/imgs/images.jpg",
                "/imgs/images.jpg.jpg",
                "/imgs/images.png"
            };

            for (String caminho : candidatos) {
                java.net.URL url = getClass().getResource(caminho);
                if (url != null) {
                    Image img = new ImageIcon(url).getImage();
                    if (img.getWidth(null) > 0) {
                        imagemLateral = img;
                        break;
                    }
                }
            }

            JPanel conteudoTexto = new JPanel();
            conteudoTexto.setOpaque(false);
            conteudoTexto.setLayout(new BoxLayout(conteudoTexto, BoxLayout.Y_AXIS));

            JLabel lblMarca = new JLabel("BARBER STYLE", SwingConstants.CENTER);
            lblMarca.setFont(new Font("Segoe UI", Font.BOLD, 17));
            lblMarca.setForeground(new Color(218, 165, 32));
            lblMarca.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblSubMarca = new JLabel("EST. 2026 • CLUB & GROOMING", SwingConstants.CENTER);
            lblSubMarca.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblSubMarca.setForeground(new Color(225, 230, 242));
            lblSubMarca.setAlignmentX(Component.CENTER_ALIGNMENT);

            conteudoTexto.add(lblMarca);
            conteudoTexto.add(Box.createVerticalStrut(3));
            conteudoTexto.add(lblSubMarca);
            conteudoTexto.add(Box.createVerticalStrut(14));

            Color dourado = new Color(218, 165, 32);
            conteudoTexto.add(criarItemDiferencial("Visagismo & Fade", FontIcon.of(FontAwesomeSolid.CUT, 14, dourado)));
            conteudoTexto.add(Box.createVerticalStrut(8));
            conteudoTexto.add(criarItemDiferencial("Toalha Quente & Ozônio", FontIcon.of(FontAwesomeSolid.SPA, 14, dourado)));
            conteudoTexto.add(Box.createVerticalStrut(8));
            conteudoTexto.add(criarItemDiferencial("Atendimento com Hora", FontIcon.of(FontAwesomeSolid.CLOCK, 14, dourado)));
            conteudoTexto.add(Box.createVerticalStrut(8));
            conteudoTexto.add(criarItemDiferencial("Ambiente Climatizado", FontIcon.of(FontAwesomeSolid.SNOWFLAKE, 14, dourado)));

            add(conteudoTexto, BorderLayout.CENTER);

            JPanel boxHorario = new JPanel();
            boxHorario.setBackground(new Color(32, 35, 52));
            boxHorario.setLayout(new BoxLayout(boxHorario, BoxLayout.Y_AXIS));
            boxHorario.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 62, 36), 1),
                    new EmptyBorder(8, 8, 8, 8)
            ));

            JLabel lblHorTit = new JLabel("HORÁRIO DE FUNCIONAMENTO");
            lblHorTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblHorTit.setForeground(dourado);
            lblHorTit.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblHorVal = new JLabel("Seg a Sáb • 08:00 às 20:00");
            lblHorVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblHorVal.setForeground(new Color(238, 242, 250));
            lblHorVal.setAlignmentX(Component.CENTER_ALIGNMENT);

            boxHorario.add(lblHorTit);
            boxHorario.add(Box.createVerticalStrut(4));
            boxHorario.add(lblHorVal);

            add(boxHorario, BorderLayout.SOUTH);
        }

        private JLabel criarItemDiferencial(String texto, Icon icone) {
            JLabel lbl = new JLabel("  " + texto, icone, SwingConstants.LEFT);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(238, 242, 250));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            lbl.setMaximumSize(new Dimension(210, 22));
            return lbl;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            GradientPaint fundo = new GradientPaint(0, 0, new Color(26, 28, 44), 0, h, new Color(16, 17, 26));
            g2.setPaint(fundo);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);

            g2.setColor(new Color(140, 108, 28));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(new Color(65, 55, 32));
            g2.drawRoundRect(5, 5, w - 11, h - 11, 10, 10);

            int imgX = 16;
            int imgY = 16;
            int imgW = w - 32;
            int imgH = 190;

            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Double(imgX, imgY, imgW, imgH, 10, 10));

            if (imagemLateral != null && imagemLateral.getWidth(null) > 0) {
                int srcW = imagemLateral.getWidth(null);
                int srcH = imagemLateral.getHeight(null);
                double escala = Math.max((double) imgW / srcW, (double) imgH / srcH);
                int drawW = (int) Math.round(srcW * escala);
                int drawH = (int) Math.round(srcH * escala);
                int drawX = imgX + (imgW - drawW) / 2;
                int drawY = imgY + (imgH - drawH) / 2;
                g2.drawImage(imagemLateral, drawX, drawY, drawW, drawH, null);
            } else {
                g2.setColor(new Color(32, 35, 52));
                g2.fillRect(imgX, imgY, imgW, imgH);
            }

            g2.setClip(oldClip);
            g2.setColor(new Color(218, 165, 32));
            g2.drawRoundRect(imgX, imgY, imgW, imgH, 10, 10);

            g2.dispose();
        }
    }

    private static class BannerServicoPanel extends JPanel {

        private final String nomeServico;

        public BannerServicoPanel(String nomeServico) {
            this.nomeServico = nomeServico != null ? nomeServico.toLowerCase() : "";
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color c1, c2, acento;
            String badge;
            if (nomeServico.contains("combo")) {
                c1 = new Color(68, 48, 20);
                c2 = new Color(28, 26, 38);
                acento = new Color(245, 196, 56);
                badge = "EXPERIÊNCIA VIP";
            } else if (nomeServico.contains("degrad") || nomeServico.contains("fade")) {
                c1 = new Color(49, 34, 88);
                c2 = new Color(24, 26, 40);
                acento = new Color(168, 85, 247);
                badge = "FADE & NAVALHADO";
            } else if (nomeServico.contains("barba")) {
                c1 = new Color(74, 30, 38);
                c2 = new Color(24, 26, 40);
                acento = new Color(244, 114, 182);
                badge = "BARBOTERAPIA";
            } else if (nomeServico.contains("sobrancelha")) {
                c1 = new Color(22, 62, 68);
                c2 = new Color(24, 26, 40);
                acento = new Color(45, 212, 191);
                badge = "DESIGN & PRECISÃO";
            } else {
                c1 = new Color(30, 48, 82);
                c2 = new Color(24, 26, 40);
                acento = new Color(96, 165, 250);
                badge = "CORTE CLÁSSICO";
            }

            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 14));
            g2.fillOval(w - 95, -25, 130, 130);
            g2.drawOval(w - 110, -35, 150, 150);

            int iconX = w - 54;
            int iconY = h / 2;
            g2.setColor(acento);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (nomeServico.contains("barba")) {
                g2.drawLine(iconX - 18, iconY + 10, iconX + 14, iconY - 12);
                g2.fillRoundRect(iconX - 20, iconY + 7, 24, 6, 4, 4);
                g2.drawArc(iconX - 6, iconY - 16, 22, 14, 190, 160);
            } else if (nomeServico.contains("degrad") || nomeServico.contains("fade")) {
                g2.drawRoundRect(iconX - 10, iconY - 14, 18, 28, 6, 6);
                g2.drawLine(iconX - 12, iconY - 16, iconX + 10, iconY - 16);
                g2.drawLine(iconX - 8, iconY - 20, iconX - 8, iconY - 16);
                g2.drawLine(iconX - 1, iconY - 20, iconX - 1, iconY - 16);
                g2.drawLine(iconX + 6, iconY - 20, iconX + 6, iconY - 16);
            } else if (nomeServico.contains("combo")) {
                Polygon coroa = new Polygon();
                coroa.addPoint(iconX - 16, iconY + 10);
                coroa.addPoint(iconX - 18, iconY - 8);
                coroa.addPoint(iconX - 7, iconY + 1);
                coroa.addPoint(iconX, iconY - 13);
                coroa.addPoint(iconX + 7, iconY + 1);
                coroa.addPoint(iconX + 18, iconY - 8);
                coroa.addPoint(iconX + 16, iconY + 10);
                g2.drawPolygon(coroa);
            } else {
                g2.drawOval(iconX - 16, iconY + 4, 9, 9);
                g2.drawOval(iconX - 4, iconY + 8, 9, 9);
                g2.drawLine(iconX - 9, iconY + 5, iconX + 14, iconY - 14);
                g2.drawLine(iconX - 1, iconY + 8, iconX + 6, iconY - 17);
            }

            g2.setColor(new Color(14, 15, 22, 175));
            g2.fillRoundRect(12, 12, 126, 22, 8, 8);
            g2.setColor(acento);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString(badge, 20, 27);

            g2.setColor(new Color(85, 72, 34));
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
        }
    }
}    
