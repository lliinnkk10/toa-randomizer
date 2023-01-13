package com.toarandomizer.utils;

import lombok.*;

@RequiredArgsConstructor
public enum Invocations {
	
	TRY_AGAIN(5, "Try Again"),
	PERSISTENCE(5, "Persistence"),
	SOFTCORE(5, "Softcore"),
	HARDCORE(10, "Hardcore"),
	
	WALK_FOR_IT(10, "Walk For It"),
	JOG_FOR_IT(5, "Jog For It"),
	RUN_FOR_IT(5, "Run For It"),
	SPRINT_FOR_IT(5, "Sprint For It"),
	
	NEED_SOME_HELP(15, "Need Some Help?"),
	NEED_LESS_HELP(10, "Need Less Help?"),
	NO_HELP_NEEDED(15, "No Help Needed"),
	// Path
	WALK_THE_PATH(50, "Walk The Path"),
	PATH_SEEKER(15, "Pathseeker"),
	PATH_FINDER(25, "Pathfinder"),
	PATH_MASTER(10, "Pathmaster"),
	
	// Prayers
	QUIET_PRAYER(20, "Quiet Prayers"),
	DEADLY_PRAYER(20, "Deadly Prayers"),
	
	// Restoration
	ON_A_DIET(15, "On A Diet"),
	DEHYDRATION(30, "Dehydration"),
	OVERLY_DRAINING(15, "Overly Draining"),
	
	// Kephri
	LIVELY_LARVAE(5, "Lively Larvae"),
	MORE_OVERLORDS(15, "More Overlords"),
	BLOWING_MUD(10, "Blowing Mud"),
	MEDIC(15, "Medic!"),
	AERIAL_ASSAULT(10, "Aerial Assault"),
	
	// Zebak
	NOT_JUST_A_HEAD(15, "Not Just A Head"),
	ARTERIAL_SPRAY(10, "Arterial Spray"),
	BLOOD_THINNERS(5, "Blood Thinners"),
	UPSET_STOMACH(15, "Upset Stomach"),
	
	// Akkha
	DOUBLE_TROUBLE(20, "Double Trouble"),
	KEEP_BACK(10, "Keep Back"),
	STAY_VIGILANT(15, "Stay Vigilant"),
	FEELING_SPECIAL(20, "Feeling Special"),
	
	// Ba-Ba
	MIND_THE_GAP(10, "Mind The Gap!"),
	GOTTA_HAVE_FAITH(10, "Gotta Have Faith"),
	JUNGLE_JAPES(5, "Jungle Japes"),
	SHAKING_THINGS_UP(10, "Shaking Things Up"),
	BOULDER_DASH(10, "Boulderdash"),
	
	// Wardens
	ANCIENT_HASTE(10, "Ancient Haste"),
	ACCELERATION(10, "Acceleration"),
	PENETRATION(10, "Penetration"),
	OVERCLOCKED_ONE(10, "Overclocked"),
	OVERCLOCKED_TWO(10, "Overclocked Two"),
	INSANITY(50, "Insanity"),
	
	ZEBAK(0, "Zebak"),
	PATH_LEVEL(0, "Path Level"),
	
	OVERCLOCKED(0, "Overclock"),
	
	ATTEMPTS(0, "Attempts"),
	
	TIME(0, "Time"),
	
	HELP(0, "Help");
	
	@Getter
	private final int raidLevel;
	
	@Getter
	private final String name;
	
	public int getWidgetIx() {
		return ordinal() * 3;
	}
	
	@RequiredArgsConstructor
	public enum Zebak {
		NOT_JUST_A_HEAD(15, "Not Just A Head"),
		ARTERIAL_SPRAY(10, "Arterial Spray"),
		BLOOD_THINNERS(5, "Blood Thinners");
		
		@Getter
		private final int raidLevel;
		
		@Getter
		private final String name;
		
	}
	
	@RequiredArgsConstructor
	public enum Attempts {
		TRY_AGAIN(5, "Try Again"),
		PERSISTENCE(5, "Persistence"),
		SOFTCORE(5, "Softcore"),
		HARDCORE(10, "Hardcore");
		
		@Getter
		private final int raidLevel;
		
		@Getter
		public final String id;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Time {
		WALK_FOR_IT(10, "Walk For It"),
		JOG_FOR_IT(5, "Jog For It"),
		RUN_FOR_IT(5, "Run For It"),
		SPRINT_FOR_IT(5, "Sprint For It");
		
		@Getter
		private final int raidLevel;
		
		@Getter
		private final String name;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Help {
		NEED_SOME_HELP(15, "Need Some Help?"),
		NEED_LESS_HELP(10, "Need Less Help?"),
		NO_HELP_NEEDED(25, "No Help Needed");
		
		@Getter
		private final int raidLevel;
		
		@Getter
		private final String name;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Path {
		PATH_SEEKER(15, "Pathseeker"),
		PATH_FINDER(25, "Pathfinder"),
		PATH_MASTER(10, "Pathmaster");
		
		@Getter
		private final int raidLevel;
		
		@Getter
		private final String name;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Overclocked {
		OVERCLOCKED_ONE(10, "Overclocked"),
		OVERCLOCKED_TWO(10, "Overclocked Two"),
		INSANITY(50, "Insanity");
		
		@Getter
		private final int raidLevel;
		
		@Getter
		private final String name;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
}