package com.toarandomizer.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InvocationPanel extends JPanel {
	
	private final BufferedImage icon;
	public final String text;
	private final JLabel image = new JLabel(  );
	private final JLabel textField = new JLabel(  );
	
	public InvocationPanel(BufferedImage icon, String text) {
		this.icon = icon;
		this.text = text;
		
		image.setIcon(new ImageIcon(icon ));
		textField.setText(text);
		textField.setFont(new Font("Arial", Font.BOLD, 15));
		textField.setPreferredSize(new Dimension( 130, 18 ));
		
		this.setMinimumSize(new Dimension( 190, 50 ));
		this.setLayout(new MigLayout( "align center", "[]5[]" ));
		
		this.add(image, BorderLayout.WEST);
		this.add(textField, BorderLayout.EAST);
		
	}
	
	public InvocationPanel getPanel(String text) {
		return this.text.equals(text) ? this : null;
	}
	
}