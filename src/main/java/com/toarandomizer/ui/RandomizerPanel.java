package com.toarandomizer.ui;

import javax.swing.*;
import java.awt.*;

public class RandomizerPanel extends JPanel {
	
	private final ToAPluginPanel panel;
	private final GridBagConstraints c;
	
	protected RandomizerPanel(ToAPluginPanel panel) {
		super();
		
		this.panel = panel;
		
		setLayout(new GridBagLayout());
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
		RandomizerButton btn = new RandomizerButton("Randomize");
		btn.addActionListener(e -> {
			btn.setText("changed");
		});
		
	}
	
}
