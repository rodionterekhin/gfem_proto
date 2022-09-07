package ru.gazpromneft.gfemproto.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PanelFiller {
    private final JPanel panel;
    private JLabel previousLabel;
    private JTextField previousTextField;

    public PanelFiller(JPanel panel) {
        this.panel = panel;
    }

    public void addNumericEntry(String name, String value) {
        JLabel l = new JLabel(name, JLabel.TRAILING);
        panel.add(l);
        JTextField textField = new JTextField();
        textField.setText(value);
        l.setLabelFor(textField);
        textField.setEditable(false);
        panel.add(textField);
        Component target = previousLabel != null ? previousLabel : panel;
        String targetDirection = target == previousLabel ? SpringLayout.SOUTH : SpringLayout.NORTH;
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.WEST, l,
                5,
                SpringLayout.WEST, panel);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.NORTH, l,
                7,
                targetDirection, target);

        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.WEST, textField,
                5,
                SpringLayout.EAST, l);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.NORTH, textField,
                5,
                targetDirection, target);
        previousLabel = l;
        previousTextField = textField;
    }

    public void addArrayEntry(String name, HashMap<Double, Double> value) {
        JLabel l = new JLabel(name, JLabel.TRAILING);
        panel.add(l);
        JTextField textField = new JTextField();
        textField.setText(String.valueOf(value));
        l.setLabelFor(textField);
        textField.setEditable(false);
        panel.add(textField);
        Component target = previousLabel != null ? previousLabel : panel;
        String targetDirection = target == previousLabel ? SpringLayout.SOUTH : SpringLayout.NORTH;
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.WEST, l,
                5,
                SpringLayout.WEST, panel);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.NORTH, l,
                7,
                targetDirection, target);

        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.WEST, textField,
                5,
                SpringLayout.EAST, l);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.NORTH, textField,
                5,
                targetDirection, target);
        previousLabel = l;
        previousTextField = textField;
    }

    public void clearEntries() {
        previousLabel = null;
        previousTextField = null;
        for (int i = 0; i < panel.getComponentCount(); i++) {
            panel.remove(i);
        }
    }
}