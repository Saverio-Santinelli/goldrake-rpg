package it.unicam.cs.mpgc.rpg129548;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

/** Pulsante Swing con una cornice e una superficie da plancia arcade. */
final class PulsanteArcade extends JButton {

    private final Color accento;

    PulsanteArcade(String testo, Color accento) {
        super(testo);
        this.accento = accento;
        setUI(new BasicButtonUI());
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        setForeground(new Color(0xF3F7FF));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics grafica) {
        Graphics2D g = (Graphics2D) grafica.create();
        try {
            int larghezza = getWidth();
            int altezza = getHeight();
            var stato = getModel();
            Color bordo = isEnabled() ? accento : new Color(0x46546D);
            Color alto = new Color(0x243551);
            Color basso = new Color(0x121B2D);
            if (stato.isPressed() && stato.isArmed()) {
                alto = new Color(0x111827);
                basso = new Color(0x293952);
            } else if (stato.isRollover()) {
                alto = new Color(0x354A68);
            }
            g.setPaint(new GradientPaint(0, 0, alto, 0, Math.max(1, altezza), basso));
            g.fillRect(1, 1, larghezza - 2, altezza - 2);
            g.setColor(new Color(255, 255, 255, 12));
            for (int y = 4; y < altezza - 4; y += 3) {
                g.drawLine(2, y, larghezza - 3, y);
            }
            g.setColor(bordo);
            g.drawRect(0, 0, larghezza - 1, altezza - 1);
            g.fillRect(0, 0, larghezza, 2);
            g.fillRect(0, altezza - 4, 6, 4);
            g.fillRect(larghezza - 6, altezza - 4, 6, 4);
            if (isFocusOwner()) {
                g.setColor(Color.WHITE);
                g.drawRect(3, 3, larghezza - 7, altezza - 7);
            }
        } finally {
            g.dispose();
        }
        super.paintComponent(grafica);
    }
}
