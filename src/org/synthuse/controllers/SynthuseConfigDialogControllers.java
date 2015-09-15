package org.synthuse.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.synthuse.Config;
import org.synthuse.views.SynthuseConfigPanel;

public class SynthuseConfigDialogControllers {

	public static void initializeUI(SynthuseConfigPanel aSynthuseConfigPanel, Config aConfig) {
		aSynthuseConfigPanel.getAlwaysOnTopCheckBox().setSelected(aConfig.isAlwaysOnTop());
		aSynthuseConfigPanel.getDisableFiltersUiaCheckBox().setSelected(aConfig.isFilterUiaDisabled());
		aSynthuseConfigPanel.getDisableUiaBridgeCheckBox().setSelected(aConfig.isUiaBridgeDisabled());
		aSynthuseConfigPanel.getRefreshKeyTextField().setText(Integer.toString(aConfig.getRefreshKeyCode()));
		aSynthuseConfigPanel.getStrongTextMatchingCheckBox().setSelected(aConfig.isUseStrongTextMatching());
		aSynthuseConfigPanel.getTargetKeyTextField().setText(Integer.toString(aConfig.getTargetKeyCode()));
		aSynthuseConfigPanel.getXPathHighlightTextField().setText(aConfig.getXpathHighlight());
		aSynthuseConfigPanel.getXPathListTextField().setText(aConfig.getXpathList());
	}

	public static void bindActionControllers(final SynthuseConfigPanel aSynthuseConfigPanel, final Config aConfig) {
		aSynthuseConfigPanel.getAlwaysOnTopCheckBox().addActionListener(alwaysOnTopCheckboxActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getDisableFiltersUiaCheckBox().addActionListener(disableFiltersUiaCheckboxActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getDisableUiaBridgeCheckBox().addActionListener(disableUiaBridgeCheckboxActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getRefreshKeyTextField().addActionListener(refreshKeyCodeTextFieldActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getStrongTextMatchingCheckBox().addActionListener(strongTextMatchingCheckboxActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getTargetKeyTextField().addActionListener(targetKeyCodeTextFieldActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getXPathHighlightTextField().addActionListener(xpathHighlightTextFieldActionHandler(aSynthuseConfigPanel, aConfig));
		aSynthuseConfigPanel.getXPathListTextField().addActionListener(xpathListTextFieldActionHandler(aSynthuseConfigPanel, aConfig));
	}

	private static ActionListener xpathListTextFieldActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setXPathList(aSynthuseConfigPanel.getXPathListTextField().getText());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}

	private static ActionListener xpathHighlightTextFieldActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setXPathHighlight(aSynthuseConfigPanel.getXPathHighlightTextField().getText());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}

	private static ActionListener targetKeyCodeTextFieldActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setTargetKeyCode(aSynthuseConfigPanel.getTargetKeyTextField().getText());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}

	private static ActionListener strongTextMatchingCheckboxActionHandler(
			final SynthuseConfigPanel aSynthuseConfigPanel, final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setUseStrongTextMatching(aSynthuseConfigPanel.getStrongTextMatchingCheckBox().isSelected());
			}
		};
	}

	private static ActionListener refreshKeyCodeTextFieldActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setRefreshKeyCode(aSynthuseConfigPanel.getRefreshKeyTextField().getText());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}

	private static ActionListener disableUiaBridgeCheckboxActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setDisableUiaBridge(aSynthuseConfigPanel.getDisableUiaBridgeCheckBox().isSelected());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}

	private static ActionListener disableFiltersUiaCheckboxActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setDisableFiltersUia(aSynthuseConfigPanel.getDisableFiltersUiaCheckBox().isSelected());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}

	private static ActionListener alwaysOnTopCheckboxActionHandler(final SynthuseConfigPanel aSynthuseConfigPanel,
			final Config aConfig) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				aConfig.setAlwaysOnTop(aSynthuseConfigPanel.getAlwaysOnTopCheckBox().isSelected());
				JOptionPane.showMessageDialog(aSynthuseConfigPanel, "May require restart to be effective");
			}
		};
	}
}
