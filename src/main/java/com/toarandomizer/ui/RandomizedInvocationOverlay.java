package com.toarandomizer.ui;

import com.toarandomizer.ToARandomizerPlugin;
import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.tooltip.Tooltip;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class RandomizedInvocationOverlay extends Overlay {
	
	@Inject
	private Client client;
	
	@Inject
	private ToARandomizerPlugin plugin;
	
	@Inject
	private ToolTipManager toolTipManager;
	
	RandomizedInvocationOverlay() {
		drawAfterLayer(WidgetInfo.TOA_PARTY_LAYER);
		setLayer(OverlayLayer.MANUAL);
		setPosition(OverlayPosition.DYNAMIC);
		setResizable(false);
		setMovable(false);
		setPreferredSize(new Dimension( 30, 30 ));
		
	}
	
	private Tooltip tooltip = null;
	
	@Override
	public Dimension render(Graphics2D graphics) {
		
		return null;
	}
}
