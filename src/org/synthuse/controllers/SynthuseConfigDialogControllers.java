package org.synthuse.controllers;

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

}
