package com.toarandomizer.ui;

import javax.swing.*;
import java.awt.*;

public class RandomizerButton extends JButton {

	private Font font = new Font("Arial", Font.BOLD, 15);
	
	public RandomizerButton(String text) {
		
		super(text);
		
		setPreferredSize(new Dimension( 45, 45 ));
		
		setFont(font);
		
	}
	
	public RandomizerButton(String text, Dimension dimension) {
		
		super(text);
		
		setPreferredSize(dimension);
		
		setFont(font);
		
	}

}