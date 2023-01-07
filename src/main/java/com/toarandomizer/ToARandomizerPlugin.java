package com.toarandomizer;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import com.toarandomizer.ui.ToAPluginPanel;
import com.toarandomizer.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.*;

@Slf4j
@PluginDescriptor(
	name = "ToA Invocation Randomizer"
)
public class ToARandomizerPlugin extends Plugin {
	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ToARandomizerConfig config;
	
	private NavigationButton navButton;
	private ToAPluginPanel panel;
	
	@Override
	protected void startUp() throws Exception {
		log.info("Randomizer started!");
		
		panel = new ToAPluginPanel();
		
		navButton = NavigationButton.builder()
				.tooltip(Constants.PLUGIN_NAME)
				.priority(Constants.DEFAULT_PRIORITY)
				.icon(Icons.NAV_BUTTON)
				.panel(panel)
				.build();
		
		clientToolbar.addNavigation(navButton);
		
	}

	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(navButton);
		
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {

	}
	
	@Provides
	ToARandomizerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ToARandomizerConfig.class);
	}
	
	
}
