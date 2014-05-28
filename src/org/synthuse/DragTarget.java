/*
 * Copyright 2014, Synthuse.org
 * Released under the Apache Version 2.0 License.
 *
 * last modified by ejakubowski
*/

package org.synthuse;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;


public class DragTarget extends JLabel  {

	public static String CUSTOM_ICON_RESOURCE = "/org/synthuse/img/bullseye-logo-th32x32.png";
	
	private static final long serialVersionUID = 1L;
	
	public static interface dragEvents {
		void dragStarted(JComponent c);
		void dragMouseMoved(int x, int y);
		void dropped(JComponent c);
	}
	public dragEvents events = new dragEvents() {
		public void dragStarted(JComponent c) {
			
		}
		public void dragMouseMoved(int x, int y) {
			
		}
		public void dropped(JComponent c) {
			
		}
	};
	
	public DragTarget() {
		
		this.setTransferHandler(new CustomTransferHandler());
		
		this.addMouseListener(new MouseAdapter(){
	        public void mousePressed(MouseEvent e)
	        {
	            JComponent comp = (JComponent)e.getSource();
	            TransferHandler handler = comp.getTransferHandler();
	            //c.setOpaque(true);
	            handler.exportAsDrag(comp, e, TransferHandler.MOVE);
	        }			
		});
	}
	
	@SuppressWarnings("serial")
	class CustomTransferHandler extends TransferHandler {
		//private final JWindow window = new JWindow();
		private final DataFlavor localObjectFlavor;
		private final JLabel label = new JLabel();
		//public Cursor blankCursor = null;
		public Cursor customCursor = null;
		
		public CustomTransferHandler () {
			//System.out.println("CustomTransferHandler");
			localObjectFlavor = new ActivationDataFlavor(DragTarget.class, DataFlavor.javaJVMLocalObjectMimeType, "JLabel");
			//label = DragTarget.this;
			//window.add(label);
			//window.setAlwaysOnTop(true);
			//window.setBackground(new Color(0,true));
			// Create a new blank cursor.
			//BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			//blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
			try {
				BufferedImage image = ImageIO.read(SynthuseDlg.class.getResourceAsStream(CUSTOM_ICON_RESOURCE));
				customCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(15, 16), "custom cursor");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			
			DragSource.getDefaultDragSource().addDragSourceMotionListener(new DragSourceMotionListener() {
				@Override
				public void dragMouseMoved(DragSourceDragEvent e) {
					events.dragMouseMoved(e.getLocation().x, e.getLocation().y);
					//Point pt = e.getLocation();
					//pt.translate(-9, -9); // offset
					//window.setLocation(pt);
					//e.getDragSourceContext().setCursor(customCursor);//DragSource.DefaultLinkDrop
				}
			});
			
			DragSource.getDefaultDragSource().addDragSourceListener(new DragSourceAdapter() {
				@Override
				public void dragEnter(DragSourceDragEvent e) {
					e.getDragSourceContext().setCursor(customCursor);//DragSource.DefaultLinkDrop
					//System.out.println("dragEnter");
				}
			});
			
		}
		
		@Override 
		protected Transferable createTransferable(JComponent c) {
			//System.out.println("createTransferable");
			//JLabel l = (JLabel)c;
			//String text = l.getText();
			final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
			return dh;
			//return new StringSelection("");
		}
		
		@Override 
		public boolean canImport(TransferSupport support) {
			return true;
		}
		
		@Override 
		public boolean importData(TransferSupport support) {
			//System.out.println("importData");
			return true;
		}
		
		@Override 
		public int getSourceActions(JComponent c) {
			//System.out.println("getSourceActions");
			events.dragStarted(c);
			JLabel p = (JLabel)c;
			label.setIcon(p.getIcon());
			//label.setText(p.draggingLabel.getText());
			//window.pack();
			//Point pt = p.getLocation();
			//SwingUtilities.convertPointToScreen(pt, p);
			//window.setLocation(pt);
			//window.setVisible(true);
			return MOVE;
		}
		
		@Override 
		protected void exportDone(JComponent c, Transferable data, int action) {
			//System.out.println("exportDone");
			events.dropped(c);
			JLabel src = (JLabel)c;
			if(action == TransferHandler.MOVE) {
				src.remove(src);
				src.revalidate();
				src.repaint();
			}
			src = null;
			//window.setVisible(false);
			//window.dispose();
		}
	}
}
