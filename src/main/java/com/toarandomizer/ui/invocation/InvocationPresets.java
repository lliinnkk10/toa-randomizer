package com.toarandomizer.ui.invocation;

import com.toarandomizer.utils.Invocations;
import lombok.Value;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

@Value
public class InvocationPresets {

	public static InvocationPresets parse(String serialized) {
		String[] parts = serialized.trim().split(";");
		String name = parts[0];
		
		if (parts.length != 2) {
			if (serialized.endsWith(";")) {
				return new InvocationPresets(parts[0], Collections.emptySet());
			}
			throw new IllegalArgumentException( "Invalid Format" );
		}
		
		Set<Invocations> invocations = Arrays.stream(parts[1].split(","))
				.map(Invocations::valueOf)
				.collect(Collectors.toSet());
		return new InvocationPresets(name, invocations);
	}
	
	private final String name;
	private final Set<Invocations> invocations;
	
	public String serialize() {
		return name + ";" + invocations.stream().map(Invocations::name).collect(Collectors.joining(","));
	}
	
	public String toStringDecorated() {
		String nameColorTag = ColorUtil.colorTag(new Color(255, 152, 31));
		return nameColorTag + name;
	}

}