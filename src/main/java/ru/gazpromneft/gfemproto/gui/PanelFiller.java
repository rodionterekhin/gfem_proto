package ru.gazpromneft.gfemproto.gui;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.logging.Logger;

public class PanelFiller {
    private final JPanel panel;
    private JLabel previousLabel;
    private final NumberFormat floatFormatter = new DecimalFormat("#0.00");
    private final NumberFormat integerFormatter = new DecimalFormat("#0");
    private final int ROW_HEIGHT = 30;
    private final Dimension labelDimension = new Dimension(200, 20);
    private final Dimension numericDimension = new Dimension(50, 30);

    public PanelFiller(JPanel panel) {
        this.panel = panel;
    }


    public void addNumericEntry(String name, Number value) {
        String valueString;
        if (value != null) {
            boolean isFloatingPointValue = (value instanceof Double) || (value instanceof Float);
            valueString = isFloatingPointValue ?
                    (floatFormatter.format(value.doubleValue())) :
                    (integerFormatter.format(value.intValue()));
        } else {
            valueString = null;
        }
        addEntry(name, valueString);
    }

    private void addEntry(String labelText, String value) {
        JLabel l = new JLabel(labelText);
        l.setMaximumSize(labelDimension);
        l.setMinimumSize(labelDimension);
        l.setSize(labelDimension);
        l.setPreferredSize(labelDimension);
        panel.add(l);

        JTextField textField = new JTextField();
        if (value != null)
            textField.setText(value);
        else
            textField.setVisible(false);
        l.setLabelFor(textField);
        textField.setEditable(false);
        textField.setMaximumSize(numericDimension);
        textField.setMinimumSize(numericDimension);
        textField.setSize(numericDimension);
        textField.setPreferredSize(numericDimension);
        panel.add(textField);
        Component target = previousLabel != null ? previousLabel : panel;
        String targetDirection = target == previousLabel ? SpringLayout.SOUTH : SpringLayout.NORTH;
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.WEST, l,
                5,
                SpringLayout.WEST, panel);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.NORTH, l,
                10,
                targetDirection, target);

        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.WEST, textField,
                5,
                SpringLayout.EAST, l);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.EAST, textField,
                -5,
                SpringLayout.EAST, panel);
        ((SpringLayout) panel.getLayout()).putConstraint(SpringLayout.NORTH, textField,
                5,
                targetDirection, target);
        previousLabel = l;
        panel.revalidate();
        Dimension dims = new Dimension(-1, panel.getComponentCount() / 2 * (ROW_HEIGHT) + 20);
        this.panel.setSize(dims);
        this.panel.setMinimumSize(dims);
        this.panel.setMaximumSize(dims);
        this.panel.setPreferredSize(dims);
        panel.revalidate();
    }

    public void addArrayEntry(String name, HashMap<Number, Number> value) {
        addEntry(name, String.valueOf(value));
    }

    public void complete() {
        assert panel != null;
        panel.revalidate();
    }

    public void clearEntries() {
        previousLabel = null;
        while(panel.getComponentCount() > 0) {
            panel.remove(0);
        }
        panel.setLayout(new SpringLayout());
    }
}
