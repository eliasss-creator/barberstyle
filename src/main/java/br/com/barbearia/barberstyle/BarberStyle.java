/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package br.com.barbearia.barberstyle;

import br.com.barbearia.barberstyle.view.JanelaPrincipal;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.EventQueue;


/**
 * Classe principal de inicializacao do sistema BarberStyle.
 * @author FantasmaDeZion
 */
public class BarberStyle {

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        EventQueue.invokeLater(() -> {
            new JanelaPrincipal().setVisible(true);
        });
    }
}