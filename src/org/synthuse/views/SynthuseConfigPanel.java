/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synthuse.views;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author ux29sy
 */
public class SynthuseConfigPanel extends javax.swing.JPanel {

    /**
     * Creates new form SynthuseConfigPanel
     */
    public SynthuseConfigPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        theStrongTextMatchingCheckBox = new javax.swing.JCheckBox();
        theAlwaysOnTopCheckBox = new javax.swing.JCheckBox();
        theDisableUiaBridgeCheckBox = new javax.swing.JCheckBox();
        theDisableFiltersUiaCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(90, 0), new java.awt.Dimension(90, 0), new java.awt.Dimension(32767, 0));
        theRefreshKeyTextField = new javax.swing.JTextField();
        theTargetKeyTextField = new javax.swing.JTextField();
        theXPathListTextField = new javax.swing.JTextField();
        theXPathHighlightTextField = new javax.swing.JTextField();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 0), new java.awt.Dimension(50, 32767));

        setLayout(new java.awt.GridBagLayout());

        theStrongTextMatchingCheckBox.setText("Use Strong Text Matching");
        theStrongTextMatchingCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(theStrongTextMatchingCheckBox, gridBagConstraints);

        theAlwaysOnTopCheckBox.setText("Always On Top");
        theAlwaysOnTopCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(theAlwaysOnTopCheckBox, gridBagConstraints);

        theDisableUiaBridgeCheckBox.setText("Disable Uia Bridge");
        theDisableUiaBridgeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(theDisableUiaBridgeCheckBox, gridBagConstraints);

        theDisableFiltersUiaCheckBox.setText("Disable Filters Uia");
        theDisableFiltersUiaCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(theDisableFiltersUiaCheckBox, gridBagConstraints);

        jLabel1.setText("Refresh Key:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText("Target Key:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("XPath List:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(jLabel3, gridBagConstraints);

        jLabel4.setText("XPath Highlight:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        add(filler2, gridBagConstraints);

        theRefreshKeyTextField.setText("XXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(theRefreshKeyTextField, gridBagConstraints);

        theTargetKeyTextField.setText("XXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(theTargetKeyTextField, gridBagConstraints);

        theXPathListTextField.setText("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(theXPathListTextField, gridBagConstraints);

        theXPathHighlightTextField.setText("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(theXPathHighlightTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        add(filler3, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JCheckBox theAlwaysOnTopCheckBox;
    private javax.swing.JCheckBox theDisableFiltersUiaCheckBox;
    private javax.swing.JCheckBox theDisableUiaBridgeCheckBox;
    private javax.swing.JTextField theRefreshKeyTextField;
    private javax.swing.JCheckBox theStrongTextMatchingCheckBox;
    private javax.swing.JTextField theTargetKeyTextField;
    private javax.swing.JTextField theXPathHighlightTextField;
    private javax.swing.JTextField theXPathListTextField;
    // End of variables declaration//GEN-END:variables

    public JCheckBox getAlwaysOnTopCheckBox() {
        return theAlwaysOnTopCheckBox;
    }

    public JCheckBox getDisableFiltersUiaCheckBox() {
        return theDisableFiltersUiaCheckBox;
    }

    public JCheckBox getDisableUiaBridgeCheckBox() {
        return theDisableUiaBridgeCheckBox;
    }

    public JTextField getRefreshKeyTextField() {
        return theRefreshKeyTextField;
    }

    public JCheckBox getStrongTextMatchingCheckBox() {
        return theStrongTextMatchingCheckBox;
    }

    public JTextField getTargetKeyTextField() {
        return theTargetKeyTextField;
    }

    public JTextField getXPathHighlightTextField() {
        return theXPathHighlightTextField;
    }

    public JTextField getXPathListTextField() {
        return theXPathListTextField;
    }
}
