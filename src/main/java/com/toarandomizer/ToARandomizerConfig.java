package com.toarandomizer;

import net.runelite.client.config.*;
import net.runelite.client.ui.components.ColorJButton;

import java.awt.*;

@ConfigGroup("toarandomizer")
public interface ToARandomizerConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}
	
	@ConfigItem(
			keyName = "randomize",
			name = "Randomize invocations",
			description = "Click this to randomize next invocation"
	)
	default ColorJButton randomize() {
		return new ColorJButton("Randomize", Color.GREEN);
	}
	
}