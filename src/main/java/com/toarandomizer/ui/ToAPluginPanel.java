package com.toarandomizer.ui;

import com.toarandomizer.ToARandomizerConfig;
import net.runelite.client.ui.PluginPanel;

import java.awt.*;

public class ToAPluginPanel extends PluginPanel {
	
	public DisplayPanel displayPanel;
	
	public ToAPluginPanel(ToARandomizerConfig config) {
		super();
		displayPanel = new DisplayPanel(config);
		
		RandomizerPanel randomizerPanel = new RandomizerPanel(this);
		
		setLayout(new BorderLayout( 5, 5 ));
		
		add(displayPanel, BorderLayout.NORTH);
		add(randomizerPanel, BorderLayout.CENTER);
		
	}
	
	
}