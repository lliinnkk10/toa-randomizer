package com.toarandomizer.ui;

import net.runelite.client.ui.PluginPanel;

import java.awt.*;

public class ToAPluginPanel extends PluginPanel {
	
	private final DisplayPanel displayPanel = new DisplayPanel();
	
	public ToAPluginPanel() {
		super();
		
		RandomizerPanel randomizerPanel = new RandomizerPanel(this);
		
		setLayout(new BorderLayout( 5, 5 ));
		
		add(displayPanel, BorderLayout.NORTH);
		add(randomizerPanel, BorderLayout.CENTER);
		
	}

	
	
}