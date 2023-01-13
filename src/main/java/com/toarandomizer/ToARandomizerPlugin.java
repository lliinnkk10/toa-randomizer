package com.toarandomizer;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.toarandomizer.ui.*;
import com.toarandomizer.utils.*;
import com.toarandomizer.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.*;

import java.awt.*;

import static com.toarandomizer.ui.DisplayPanel.*;
import static com.toarandomizer.utils.Invocations.*;

@PluginDescriptor(
	name = "ToA Invocation Randomizer",
	description = "Tombs of Amascut randomized invocations minigame."
)
@Slf4j
public class ToARandomizerPlugin extends Plugin {
	
	public static final int WIDGET_ID_INVOCATIONS_PARENT = 774;
	public static final int WIDGET_ID_INVOCATIONS_CHILD = 52;
	
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private ClientToolbar clientToolbar;
	
	@Inject
	private ToARandomizerConfig config;
	
	private NavigationButton navButton;
	private ToAPluginPanel panel;
	
	@Override
	protected void startUp() throws Exception {
		
		panel = new ToAPluginPanel(config);
		
		navButton = NavigationButton.builder()
				.tooltip(Constants.PLUGIN_NAME)
				.priority(Constants.DEFAULT_PRIORITY)
				.icon(Icons.NAV_BUTTON)
				.panel(panel)
				.build();
		
		clientToolbar.addNavigation(navButton);
		
	}
	
	@Subscribe
	public void onClientTick(ClientTick e) throws Exception {
		
		try {
		
			final Widget toaOverlay = client.getWidget(WIDGET_ID_INVOCATIONS_PARENT, WIDGET_ID_INVOCATIONS_CHILD);
			if (!toaOverlay.isHidden()) {
				final Widget[] toaInvocations = toaOverlay.getChildren( );
				
				activeInvocations.forEach(i -> {
					int id = i.getWidgetIx();
					toaInvocations[id].setFilled(false);
					toaInvocations[id].setTextColor(Color.orange.getRGB());
					toaInvocations[id].setOpacity(0);
					switch (i) {
						case PERSISTENCE -> toaInvocations[TRY_AGAIN.getWidgetIx()].setOpacity(230);
						case SOFTCORE -> toaInvocations[PERSISTENCE.getWidgetIx()].setOpacity(230);
						case HARDCORE -> toaInvocations[SOFTCORE.getWidgetIx()].setOpacity(230);
						case JOG_FOR_IT -> toaInvocations[WALK_FOR_IT.getWidgetIx()].setOpacity(230);
						case RUN_FOR_IT -> toaInvocations[JOG_FOR_IT.getWidgetIx()].setOpacity(230);
						case SPRINT_FOR_IT -> toaInvocations[RUN_FOR_IT.getWidgetIx()].setOpacity(230);
						case NEED_LESS_HELP -> toaInvocations[NEED_SOME_HELP.getWidgetIx()].setOpacity(230);
						case NO_HELP_NEEDED -> toaInvocations[NEED_LESS_HELP.getWidgetIx()].setOpacity(230);
						case PATH_FINDER -> toaInvocations[PATH_SEEKER.getWidgetIx()].setOpacity(230);
					}
				});
				
				blockedInvocations.forEach(i -> {
					int id = i.getWidgetIx();
					toaInvocations[id].setFilled(false);
					toaInvocations[id].setTextColor(Color.red.getRGB());
					toaInvocations[id].setOpacity(0);
				});
				
			}
		} catch (NullPointerException exc) {
		
		}
		
	}
	
	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(navButton);
		
		log.info("Example stopped!");
	}
	
	@Provides
	ToARandomizerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ToARandomizerConfig.class);
	}
}