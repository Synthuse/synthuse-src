package org.synthuse;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.synthuse.controllers.SynthuseConfigDialogControllers;
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
		
		this.setConfig(aConfig);

		this.setTitle("Synthuse Properties");
		
		theSynthuseConfigPanel = new SynthuseConfigPanel();
		
		SynthuseConfigDialogControllers.bindActionControllers(theSynthuseConfigPanel,theConfig);
		
		this.getContentPane().add(theSynthuseConfigPanel);
		this.setSize(492, 260);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SynthuseConfigDialogControllers.initializeUI(theSynthuseConfigPanel,theConfig);
			}
		});
	}

	synchronized private void setConfig(Config aConfig) {
		theConfig = aConfig;
	}
}
