package com.eightpsman.funnyds.java.client.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class ControllerView extends JScrollPane implements ActionListener, MouseListener, MouseMotionListener{

	ControllerCallback callback;
	JCheckBox keepView;
	JCheckBox autoLocation;

	public ControllerView(final ControllerCallback callback){
		super();

		this.callback = callback;

		JPanel container = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets.set(2,2 ,2 ,2);
		setFocusable(true);

		gbc.ipadx = 3;
		gbc.ipady = 3;

		container.add(new JLabel("||||"));
		container.add(newButton("+"), gbc);
		container.add(newButton("-"), gbc);
		container.add(newButton("i"), gbc);
		container.add(newButton("m"), gbc);
		container.add(newButton("r"), gbc);
		container.add(keepView = new JCheckBox("KMV"), gbc);
		container.add(autoLocation = new JCheckBox("ATL"), gbc);
		keepView.setToolTipText("Keep my view");
		autoLocation.setToolTipText("Auto change location by device's position");

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		container.add(new JLabel(), gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		container.add(newButton("x"), gbc);

		setViewportView(container);
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
		getHorizontalScrollBar().setPreferredSize(new Dimension(0, 3));

		addMouseListener(this);
		addMouseMotionListener(this);

		keepView.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					callback.setKeepView(true);
				}else if (e.getStateChange() == ItemEvent.DESELECTED){
					callback.setKeepView(false);
				}
			}
		});
		autoLocation.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					callback.setAutoLocation(true);
				}else if (e.getStateChange() == ItemEvent.DESELECTED){
					callback.setAutoLocation(false);
				}
			}
		});
	}

	private JButton newButton(String text){
		JButton jButton = new JButton(text);
		jButton.setFocusable(false);
		jButton.setName(text);
		jButton.addActionListener(this);
		return jButton;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton jButton = (JButton) event.getSource();
		if (jButton.getName().equals("+")){
			callback.onFullscreen();
		}else if (jButton.getName().equals("-")){
			callback.onFillscreen();
		}else if (jButton.getName().equals("i")){
			callback.showOrHideInfo();
		}else if (jButton.getName().equals("m")){
			callback.onShowMiniView();
		}else if (jButton.getName().equals("x")){
			callback.onExit();
		}else if (jButton.getName().equals("r")){
			callback.onRotate();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		
	}

	@Override
	public void mouseExited(MouseEvent event) {
		setVisible(false);
	}

	private int deltaX;
	private int deltaY;

	@Override
	public void mousePressed(MouseEvent event) {
		deltaX = event.getXOnScreen() - callback.getLocationX();
		deltaY = event.getYOnScreen() - callback.getLocationY();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		callback.onMoveTo(e.getXOnScreen() - deltaX, e.getYOnScreen() - deltaY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	public interface ControllerCallback{
		void onExit();
		void onShowClientView();
		void onShowMiniView();
		void onFullscreen();
		void onFillscreen();
		void onRotate();
		void onMoveTo(int x, int y);
		int getLocationX();
		int getLocationY();
		void showOrHideInfo();
		void setKeepView(boolean isKeep);
		void setAutoLocation(boolean isAutoLocation);
	}
	
}
