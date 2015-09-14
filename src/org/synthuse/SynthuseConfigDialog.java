package org.synthuse;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.synthuse.views.SynthuseConfigPanel;

public class SynthuseConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4877764256323621418L;
	
	private Config theConfig; //Model
	private final SynthuseConfigPanel theSynthuseConfigPanel; //View

	public SynthuseConfigDialog(JFrame aParentFrame, Config aConfig) {
		super(aParentFrame);
		this.setTitle("Synthuse Properties");
		theSynthuseConfigPanel = new SynthuseConfigPanel();
		this.setConfig(aConfig);
		this.getContentPane().add(theSynthuseConfigPanel);
		this.setSize(492, 260);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SynthuseConfigDialog.this.initializeUI();
			}
		});
	}

	synchronized private void initializeUI() {
		theSynthuseConfigPanel.getAlwaysOnTopCheckBox().setSelected(theConfig.isAlwaysOnTop());
		theSynthuseConfigPanel.getDisableFiltersUiaCheckBox().setSelected(theConfig.isFilterUiaDisabled());
		theSynthuseConfigPanel.getDisableUiaBridgeCheckBox().setSelected(theConfig.isUiaBridgeDisabled());
		theSynthuseConfigPanel.getRefreshKeyTextField().setText(Integer.toString(theConfig.getRefreshKeyCode()));
		theSynthuseConfigPanel.getStrongTextMatchingCheckBox().setSelected(theConfig.isUseStrongTextMatching());
		theSynthuseConfigPanel.getTargetKeyTextField().setText(Integer.toString(theConfig.getTargetKeyCode()));
		theSynthuseConfigPanel.getXPathHighlightTextField().setText(theConfig.getXpathHighlight());
		theSynthuseConfigPanel.getXPathListTextField().setText(theConfig.getXpathList());
	}

	synchronized private void setConfig(Config aConfig) {
		theConfig = aConfig;
	}

}
