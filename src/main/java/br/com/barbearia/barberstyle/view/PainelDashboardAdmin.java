/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.DashboardDAO;
import br.com.barbearia.barberstyle.model.Usuario;
import java.awt.*;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PainelDashboardAdmin extends JPanel {

    private final Usuario adminLogado;
    private final Consumer<JPanel> navegador;
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private JLabel lblFaturamentoMes;
    private JLabel lblAtendimentosHoje;
    private JLabel lblTicketMedio;
    private JLabel lblBarbeiroDestaque;
    private JLabel lblServicoMaisPedido;

    private GraficoFaturamentoPanel graficoFaturamento;
    private GraficoRankingPanel graficoServicos;

    public PainelDashboardAdmin(Usuario adminLogado, Consumer<JPanel> navegador) {
        this.adminLogado = adminLogado;
        this.navegador = navegador;
        inicializarComponentes();
        carregarIndicadores();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(14, 14));
        setBackground(new Color(14, 15, 22));
        setBorder(new EmptyBorder(18, 22, 16, 22));

        // 1. TOPO: Título + Botão Atualizar
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel lblTitulo = new JLabel("DASHBOARD GERENCIAL — BARBERSTYLE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(218, 165, 32));

        JLabel lblSub = new JLabel("Bem-vindo(a), " + adminLogado.getNome() + " • Indicadores em tempo real");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(170, 175, 190));

        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 2));
        titulos.setOpaque(false);
        titulos.add(lblTitulo);
        titulos.add(lblSub);

        JButton btnAtualizar = new JButton("↻ Atualizar Indicadores");
        btnAtualizar.setBackground(new Color(38, 41, 58));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(e -> carregarIndicadores());

        topo.add(titulos, BorderLayout.WEST);
        topo.add(btnAtualizar, BorderLayout.EAST);

        // 2. CARDS DE KPI (5 indicadores reais)
        JPanel gridKpis = new JPanel(new GridLayout(1, 5, 12, 0));
        gridKpis.setOpaque(false);
        gridKpis.setBorder(new EmptyBorder(12, 0, 4, 0));

        lblFaturamentoMes = new JLabel("R$ 0,00");
        lblAtendimentosHoje = new JLabel("0");
        lblTicketMedio = new JLabel("R$ 0,00");
        lblBarbeiroDestaque = new JLabel("-");
        lblServicoMaisPedido = new JLabel("-");

        gridKpis.add(criarCardKpi("FATURAMENTO DO MÊS", lblFaturamentoMes, new Color(46, 204, 113)));
        gridKpis.add(criarCardKpi("ATENDIMENTOS HOJE", lblAtendimentosHoje, new Color(52, 152, 219)));
        gridKpis.add(criarCardKpi("TICKET MÉDIO", lblTicketMedio, new Color(155, 89, 182)));
        gridKpis.add(criarCardKpi("BARBEIRO DESTAQUE", lblBarbeiroDestaque, new Color(218, 165, 32)));
        gridKpis.add(criarCardKpi("SERVIÇO MAIS PEDIDO", lblServicoMaisPedido, new Color(231, 76, 60)));

        JPanel norteCompleto = new JPanel(new BorderLayout());
        norteCompleto.setOpaque(false);
        norteCompleto.add(topo, BorderLayout.NORTH);
        norteCompleto.add(gridKpis, BorderLayout.CENTER);
        add(norteCompleto, BorderLayout.NORTH);

        // 3. CENTRO: Gráficos Analytics (Barras + Linha de Tendência e Ranking)
        JPanel gridGraficos = new JPanel(new GridLayout(1, 2, 16, 0));
        gridGraficos.setOpaque(false);

        graficoFaturamento = new GraficoFaturamentoPanel();
        graficoServicos = new GraficoRankingPanel();

        gridGraficos.add(enveloparGrafico("Faturamento dos Últimos 7 Dias (R$)", graficoFaturamento));
        gridGraficos.add(enveloparGrafico("Demanda por Serviço (Atendimentos)", graficoServicos));
        add(gridGraficos, BorderLayout.CENTER);

        // 4. RODAPÉ: Atalhos Rápidos de Gestão
        JPanel barraAtalhos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        barraAtalhos.setBackground(new Color(21, 23, 34));
        barraAtalhos.setBorder(BorderFactory.createLineBorder(new Color(45, 48, 68), 1));

        barraAtalhos.add(criarBotaoAtalho("Agenda", () -> navegador.accept(new PainelAgendamentos(adminLogado))));
        barraAtalhos.add(criarBotaoAtalho("Clientes", () -> navegador.accept(new PainelCliente())));
        barraAtalhos.add(criarBotaoAtalho("Barbeiros", () -> navegador.accept(new PainelBarbeiro())));
        barraAtalhos.add(criarBotaoAtalho("Serviços", () -> navegador.accept(new PainelServicos())));
        barraAtalhos.add(criarBotaoAtalho("Especialidades", () -> navegador.accept(new PainelEspecialidades())));
        barraAtalhos.add(criarBotaoAtalho("Equipe & Acessos", () -> navegador.accept(new PainelUsuarios(adminLogado.getId()))));

        add(barraAtalhos, BorderLayout.SOUTH);
    }

    private JPanel criarCardKpi(String titulo, JLabel lblValor, Color corDestaque) {
        JPanel card = new JPanel(new BorderLayout(4, 6));
        card.setBackground(new Color(22, 24, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, corDestaque),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTit.setForeground(new Color(160, 165, 185));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValor.setForeground(Color.WHITE);

        card.add(lblTit, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private JPanel enveloparGrafico(String titulo, JPanel conteudoGrafico) {
        JPanel box = new JPanel(new BorderLayout(0, 8));
        box.setBackground(new Color(22, 24, 36));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 48, 68), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(220, 225, 240));
        box.add(lbl, BorderLayout.NORTH);
        box.add(conteudoGrafico, BorderLayout.CENTER);
        return box;
    }

    private JButton criarBotaoAtalho(String texto, Runnable acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(34, 37, 54));
        btn.setForeground(new Color(218, 165, 32));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> acao.run());
        return btn;
    }

    private void carregarIndicadores() {
        try {
            BigDecimal fatMes = dashboardDAO.getFaturamentoMesAtual();
            int hoje = dashboardDAO.getAtendimentosHoje();
            BigDecimal ticket = dashboardDAO.getTicketMedio();
            String barbeiro = dashboardDAO.getBarbeiroDestaque();
            String servico = dashboardDAO.getServicoMaisPedido();

            lblFaturamentoMes.setText(moedaBR.format(fatMes));
            lblAtendimentosHoje.setText(hoje + (hoje == 1 ? " cliente" : " clientes"));
            lblTicketMedio.setText(moedaBR.format(ticket));
            lblBarbeiroDestaque.setText(barbeiro);
            lblServicoMaisPedido.setText(servico);

            graficoFaturamento.setDados(dashboardDAO.getFaturamentoUltimos7Dias());
            graficoServicos.setDados(dashboardDAO.getRankingServicos());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar indicadores do Dashboard: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Painel customizado com Graphics2D: Barras com degradê roxo + linha de tendência
    private static class GraficoFaturamentoPanel extends JPanel {
        private Map<String, Double> dados;

        public GraficoFaturamentoPanel() {
            setOpaque(false);
        }

        public void setDados(Map<String, Double> dados) {
            this.dados = dados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dados == null || dados.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padLeft = 45, padRight = 20, padTop = 25, padBottom = 35;
            int areaW = w - padLeft - padRight;
            int areaH = h - padTop - padBottom;

            double maxVal = dados.values().stream().mapToDouble(Double::doubleValue).max().orElse(100.0);
            if (maxVal <= 0) maxVal = 100.0;

            // Linhas horizontais de grade
            g2.setColor(new Color(45, 48, 68));
            for (int i = 0; i <= 4; i++) {
                int y = padTop + (areaH * i / 4);
                g2.drawLine(padLeft, y, w - padRight, y);
            }

            List<String> labels = new ArrayList<>(dados.keySet());
            List<Double> valores = new ArrayList<>(dados.values());
            int n = labels.size();
            int slotW = areaW / n;
            int barW = Math.max(18, slotW / 2);

            Path2D linhaTendencia = new Path2D.Double();

            for (int i = 0; i < n; i++) {
                double val = valores.get(i);
                int barH = (int) ((val / maxVal) * areaH);
                int x = padLeft + i * slotW + (slotW - barW) / 2;
                int y = padTop + areaH - barH;

                GradientPaint gp = new GradientPaint(x, y, new Color(168, 85, 247), x, padTop + areaH, new Color(79, 70, 229, 90));
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barW, barH, 8, 8);

                // Ponto central para linha de tendência
                int cx = x + barW / 2;
                if (i == 0) linhaTendencia.moveTo(cx, y);
                else linhaTendencia.lineTo(cx, y);

                // Label do dia no eixo X
                g2.setColor(new Color(170, 175, 195));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(labels.get(i), x, h - 12);

                // Valor acima da barra
                if (val > 0) {
                    g2.setColor(new Color(218, 165, 32));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.drawString(String.format("R$%.0f", val), x - 2, Math.max(14, y - 5));
                }
            }

            // Linha de tendência suave branca/dourada
            g2.setColor(new Color(245, 208, 254, 210));
            g2.setStroke(new BasicStroke(2.2f));
            g2.draw(linhaTendencia);

            g2.dispose();
        }
    }

    // Painel customizado de barras horizontais para Ranking de Serviços
    private static class GraficoRankingPanel extends JPanel {
        private Map<String, Double> dados;

        public GraficoRankingPanel() {
            setOpaque(false);
        }

        public void setDados(Map<String, Double> dados) {
            this.dados = dados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dados == null || dados.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            double maxVal = dados.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            if (maxVal <= 0) maxVal = 1.0;

            int y = 16;
            int rowH = Math.max(36, (h - 20) / Math.max(1, dados.size()));

            for (Map.Entry<String, Double> entry : dados.entrySet()) {
                String nome = entry.getKey();
                double val = entry.getValue();

                g2.setColor(new Color(210, 215, 230));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(nome + " (" + (int) val + ")", 6, y + 12);

                int barMaxW = w - 30;
                int barW = (int) ((val / maxVal) * barMaxW);

                // Fundo da barra
                g2.setColor(new Color(35, 38, 54));
                g2.fillRoundRect(6, y + 18, barMaxW, 12, 8, 8);

                // Preenchimento degradê
                GradientPaint gp = new GradientPaint(6, y + 18, new Color(218, 165, 32), 6 + Math.max(10, barW), y + 18, new Color(236, 72, 153));
                g2.setPaint(gp);
                g2.fillRoundRect(6, y + 18, Math.max(8, barW), 12, 8, 8);

                y += rowH;
            }
            g2.dispose();
        }
    }
}