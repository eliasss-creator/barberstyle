/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.com.barbearia.barberstyle.view;

import br.com.barbearia.barberstyle.dao.AvaliacaoDAO;
import br.com.barbearia.barberstyle.model.Agendamento;
import br.com.barbearia.barberstyle.model.Avaliacao;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Dialogo interativo de avaliacao por estrelas (1 a 5) para atendimentos concluidos.
 * @author FantasmaDeZion
 */
public class DialogoAvaliacao extends JDialog {

    private int estrelasSelecionadas = 5;
    private final JButton[] botoesEstrelas = new JButton[5];
    private final JTextArea txtComentario = new JTextArea(3, 25);
    private final Agendamento agendamento;
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();

    public DialogoAvaliacao(Frame owner, Agendamento agendamento) {
        super(owner, "Avaliar Atendimento", true);
        this.agendamento = agendamento;
        setSize(400, 290);
        setLocationRelativeTo(owner);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(15, 18, 15, 18));

        JLabel lblTitulo = new JLabel("<html><center>Como foi seu atendimento?<br><b style='color:#DAA520;'>"
                + agendamento.getServico().getNome() + "</b></center></html>", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel painelEstrelas = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        for (int i = 0; i < 5; i++) {
            final int nota = i + 1;
            JButton btn = new JButton("★");
            btn.setFont(new Font("SansSerif", Font.BOLD, 28));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> atualizarEstrelas(nota));
            botoesEstrelas[i] = btn;
            painelEstrelas.add(btn);
        }
        atualizarEstrelas(5);

        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        scrollComentario.setBorder(BorderFactory.createTitledBorder("Comentário (opcional)"));

        JPanel centro = new JPanel(new BorderLayout(5, 8));
        centro.add(painelEstrelas, BorderLayout.NORTH);
        centro.add(scrollComentario, BorderLayout.CENTER);

        JButton btnEnviar = new JButton("Enviar Avaliação");
        btnEnviar.setBackground(new Color(218, 165, 32));
        btnEnviar.setForeground(Color.BLACK);
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEnviar.addActionListener(e -> salvarAvaliacao());

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(centro, BorderLayout.CENTER);
        painel.add(btnEnviar, BorderLayout.SOUTH);

        add(painel);
    }

    private void atualizarEstrelas(int nota) {
        this.estrelasSelecionadas = nota;
        for (int i = 0; i < 5; i++) {
            botoesEstrelas[i].setForeground(i < nota ? new Color(255, 215, 0) : new Color(90, 90, 95));
        }
    }

    private void salvarAvaliacao() {
        try {
            if (avaliacaoDAO.jaAvaliado(agendamento.getId())) {
                JOptionPane.showMessageDialog(this, "Este atendimento já foi avaliado anteriormente.");
                dispose();
                return;
            }
            Avaliacao av = new Avaliacao();
            av.setAgendamento(agendamento);
            av.setEstrelas(estrelasSelecionadas);
            av.setComentario(txtComentario.getText().trim());

            avaliacaoDAO.inserir(av);
            JOptionPane.showMessageDialog(this, "Obrigado pela sua avaliação de " + estrelasSelecionadas + " estrela(s)!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar avaliação: " + ex.getMessage());
        }
    }
}