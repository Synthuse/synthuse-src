/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JSplitPane;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;

/*
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
*/
import com.sun.jna.platform.win32.WinDef.HWND;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;

import org.synthuse.Api.User32;
import org.synthuse.DragTarget.dragEvents;



//public class SynthuseDlg extends JDialog {
public class SynthuseDlg extends JFrame {
	/**
	 * 
	 */
	public static String VERSION_STR = "1.0.8";

	public static String RES_STR_MAIN_ICON = "/org/synthuse/img/gnome-robots.png";
	public static String RES_STR_REFRESH_IMG = "/org/synthuse/img/rapidsvn.png";
	public static String RES_STR_TESTIDE_IMG = "/org/synthuse/img/applications-education.png";
	public static String RES_STR_HELP_IMG = "/org/synthuse/img/help-3.png";
	public static String RES_STR_TARGET_IMG = "/org/synthuse/img/bullseye-logo-th18x18.png";
	public static String RES_STR_FIND_IMG = "/org/synthuse/img/edit-find-3.png";
	public static String RES_STR_SETACTION_IMG = "/org/synthuse/img/document-new-5.png";
	public static String RES_STR_CANCEL_IMG = "/org/synthuse/img/document-close-2.png";
	
	public static List<String> actionListQueue = new ArrayList<String>();
	private static final long serialVersionUID = 1L;
	private XpathManager.Events xpathEvents = null;
	private JPanel contentPane;
	private JLabel lblStatus;
	private JButton btnRefresh;
	private JTextPane textPane;
	private JButton btnSetAction;
	private JButton btnCancel;
	private JButton btnFind;
	public static Config config = new Config(Config.DEFAULT_PROP_FILENAME);
	private String dialogResult = "";
	private String lastDragHwnd = "";
	private JComboBox<String> cmbXpath;
	private JButton btnTestIde;
	
	private TestIdeFrame testIde = null;
	private int targetX;
	private int targetY;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SynthuseDlg frame = new SynthuseDlg();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SynthuseDlg() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SynthuseDlg.class.getResource(RES_STR_MAIN_ICON)));
		//setModal(true);
		setTitle("Synthuse");
		setBounds(100, 100, 827, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		JRadioButton rdbtnWindows = new JRadioButton("Windows Enumerated Xml");
		rdbtnWindows.setSelected(true);
		toolBar.add(rdbtnWindows);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);
		
		JRadioButton rdbtnUrl = new JRadioButton("Url: ");
		rdbtnUrl.setEnabled(false);
		toolBar.add(rdbtnUrl);
		
		// Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnWindows);
	    group.add(rdbtnUrl);
		
		JComboBox<String> cmbUrl = new JComboBox<String>();
		cmbUrl.setEnabled(false);
		cmbUrl.setEditable(true);
		toolBar.add(cmbUrl);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut_1);
		
		btnRefresh = new JButton(" Refresh ");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//lblStatus.setText("Loading Windows Enumerated Xml...");
				WindowsEnumeratedXml.getXmlThreaded(textPane, lblStatus);
				//appendToPane(WindowsEnumeratedXml.getXml());
				//lblStatus.setText("Windows Enumerated Xml loaded");
			} 
		});
		btnRefresh.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_REFRESH_IMG)));
		toolBar.add(btnRefresh);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut_3);
		
		btnTestIde = new JButton("Test IDE");
		btnTestIde.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (testIde == null) {
					testIde = new TestIdeFrame();
					if (SynthuseDlg.actionListQueue.size() > 0) { // if stuff is already in the queue add it to the test ide
						for (String action : SynthuseDlg.actionListQueue) {
							testIde.txtTest.append(action + "\n");
						}
					}
				}
				testIde.setVisible(true);
			}
		});
		btnTestIde.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_TESTIDE_IMG)));
		toolBar.add(btnTestIde);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut_2);
		
		JButton helpBtn = new JButton("Help");
		helpBtn.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_HELP_IMG)));
		helpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Version " + VERSION_STR + " create by Edward Jakubowski ejakubowski7@gmail.com", "About", JOptionPane.QUESTION_MESSAGE);
			}
		});
		toolBar.add(helpBtn);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(20, 35));
		splitPane.setLeftComponent(panel);
		/*
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:max(11dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		*/
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(3,3,3,3); // add padding around objects
		
		DragTarget lblTarget = new DragTarget();
		
		lblTarget.setHorizontalAlignment(SwingConstants.CENTER);
		lblTarget.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_TARGET_IMG)));
		panel.add(lblTarget, c);
		
		btnFind = new JButton("Find");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String xpathItem = cmbXpath.getSelectedItem().toString();
				int matches = XpathManager.nextXpathMatch(xpathItem, textPane, lblStatus, false);
				if (matches < 0) //check for an error
					return; //don't save bad xpath to combobox
				if (config.xpathList == null)
					config.xpathList = "";
				if (!config.xpathList.contains(xpathItem + "\u00ba")){
					config.xpathList += xpathItem + "\u00ba";
					refreshDatabinding();
					cmbXpath.setSelectedItem(xpathItem);
				}
			}
		});
		
		cmbXpath = new JComboBox<String>();
		cmbXpath.setPreferredSize(new Dimension(440, 20));//fix the width of the combobox
		cmbXpath.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");//fix the width of the combobox
		cmbXpath.setEditable(true);
		cmbXpath.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyReleased(KeyEvent event) {
		        if (event.getKeyChar() == KeyEvent.VK_ENTER) {
		            btnFind.doClick();
		        }
		    }
		});
		c.gridwidth = 3;
		c.gridx = 1;
		panel.add(cmbXpath, c);
		btnFind.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_FIND_IMG)));
		c.gridwidth = 1;
		c.gridx = 4;
		panel.add(btnFind, c);

		
		btnSetAction = new JButton("Set Action");
		btnSetAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommandPopupMenu menu = new CommandPopupMenu();
				final String xpathItem = cmbXpath.getSelectedItem().toString();
				menu.events = new CommandPopupMenu.menuEvents() {
					@Override
					public void menuItemClicked(String command, int paramCount, boolean useXpath, ActionEvent e) {
						String actionStr = CommandPopupMenu.buildSkeletonCommand(command, paramCount, xpathItem, useXpath);
						SynthuseDlg.actionListQueue.add(actionStr);
						lblStatus.setText("Setting Action: " + actionStr);
						if (testIde != null)
							testIde.txtTest.append(actionStr + "\n");
					}
				};
				Component c = (Component) e.getSource();
                menu.show(c, -1, c.getHeight());
			}
		});
		btnSetAction.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_SETACTION_IMG)));
		c.gridwidth = 1;
		c.gridx = 5;
		panel.add(btnSetAction, c);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialogResult = "";
				SynthuseDlg.this.dispose();
			}
		});
		btnCancel.setIcon(new ImageIcon(SynthuseDlg.class.getResource(RES_STR_CANCEL_IMG)));
		c.gridwidth = 1;
		c.gridx = 6;
		panel.add(btnCancel, c);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setEditorKitForContentType("text/xml", new XmlEditorKit());
		textPane.setContentType("text/xml");

		scrollPane.setViewportView(textPane);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.setMinimumSize(new Dimension(20, 20));
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		lblStatus = new JLabel("Status:");
		lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatus.setVerticalAlignment(SwingConstants.TOP);
		panel_1.add(lblStatus);
		xpathEvents = new XpathManager.Events() {
			@Override
			public void statusChanged(String status) {
				lblStatus.setText(status);
			}
			@Override
			public void executionCompleted(Object input, String results) { //buildXpathStatementThreaded finished, with results being the xpath statement
				if (input instanceof HWND) { // in case thread takes long time to process
					lastDragHwnd = Api.GetHandleAsString((HWND)input);
				}
				XpathManager.nextXpathMatch(results, textPane, lblStatus, true);
				cmbXpath.setSelectedItem(results);
			}
		};
		
		lblTarget.events = new dragEvents() {
			public void dragStarted(JComponent c) {
				//might be nice to minimize this window, if we weren't displaying realtime information about each window
			}
			public void dragMouseMoved(int x, int y) {
				targetX = x;
				targetY = y;
				targetDragged();
			}
			public void dropped(JComponent c) {
				lastDragHwnd = ""; //sometimes with multithreaded the order becomes incorrect, so we may want to refresh this on dropped.
				targetDragged();
			}
		};

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				
				config.save();
				SynthuseDlg.this.dispose(); // force app to close
			}
		});
		
		btnRefresh.doClick();
		refreshDatabinding();
	}
	
	/*
	@Override
	public void setVisible(final boolean visible) {
		// make sure that frame is marked as not disposed if it is asked to be visible
		if (visible) {
	      //setDisposed(false);
		}
		// let's handle visibility...
		if (!visible || !isVisible()) { // have to check this condition simply because super.setVisible(true) invokes toFront if frame was already visible
			super.setVisible(visible);
		}
		// ...and bring frame to the front.. in a strange and weird way
		if (visible) {
			int state = super.getExtendedState();
			state &= ~JFrame.ICONIFIED;
			super.setExtendedState(state);
			super.setAlwaysOnTop(true);
			super.toFront();
			super.requestFocus();
			super.setAlwaysOnTop(false);
		}
	}
	*/
	
	public String showDialog() {
		//this.setModal(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setVisible(true);
		return dialogResult;
	}
	
	public void refreshDatabinding() {
		if (config.xpathList != null)
			cmbXpath.setModel(new DefaultComboBoxModel<String>(config.xpathList.split("\u00ba")));
		if (config.xpathHightlight != null)
			XmlEditorKit.TAG_HIGHLIGHTED = config.xpathHightlight;
	}
	
	public void targetDragged() {
		HWND hwnd = Api.GetWindowFromPoint(new Point(targetX,targetY));
		String handleStr = Api.GetHandleAsString(hwnd);
		String classStr = WindowsEnumeratedXml.escapeXmlAttributeValue(Api.GetWindowClassName(hwnd));
		String parentStr = Api.GetHandleAsString(User32.instance.GetParent(hwnd));

		lblStatus.setText("class: " + classStr + " hWnd: " + handleStr + " parent: " + parentStr + "  X,Y: " + targetX + ", " + targetY);
		if (!lastDragHwnd.equals(handleStr)) {
			if (!lastDragHwnd.isEmpty()) {
				Api.refreshWindow(Api.GetHandleFromString(lastDragHwnd));
			}
			lastDragHwnd = handleStr;
			//lastDragHwnd = (hwnd + "");
			Api.highlightWindow(hwnd);
			XpathManager.buildXpathStatementThreaded(hwnd, textPane, xpathEvents);
		}
	}
}
