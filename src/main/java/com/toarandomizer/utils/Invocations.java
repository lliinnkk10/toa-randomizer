package com.toarandomizer.utils;

import lombok.*;

@RequiredArgsConstructor
public enum Invocations {
	
	ATTEMPTS(0),
	
	TIME(0),
	
	HELP(0),
	
	// Path
	WALK_THE_PATH(50),
	PATH_LEVEL(0),
	
	// Prayers
	QUIET_PRAYER(20),
	DEADLY_PRAYER(20),
	
	// Restoration
	ON_A_DIET(15),
	DEHYDRATION(30),
	OVERLY_DRAINING(15),
	
	// Kephri
	LIVELY_LARVAE(5),
	MORE_OVERLORDS(15),
	BLOWING_MUD(10),
	MEDIC(15),
	AERIAL_ASSAULT(10),
	
	// Zebak
	UPSET_STOMACH(15),
	ZEBAK(0),
	
	// Akkha
	DOUBLE_TROUBLE(20),
	KEEP_BACK(10),
	STAY_VIGILANT(15),
	FEELING_SPECIAL(20),
	
	// Ba-Ba
	MIND_THE_GAP(10),
	GOTTA_HAVE_FAITH(10),
	JUNGLE_JAPES(5),
	SHAKING_THINGS_UP(10),
	BOULDER_DASH(10),
	
	// Wardens
	ANCIENT_HASTE(10),
	ACCELERATION(10),
	PENETRATION(10),
	OVERCLOCKED(0);
	
	@Getter
	private final int raidLevel;
	
	public int getWidgetIx() {
		return ordinal() * 3;
	}
	
	@RequiredArgsConstructor
	public enum Zebak {
		NOT_JUST_A_HEAD(15),
		ARTERIAL_SPRAY(10),
		BLOOD_THINNERS(5);
		
		@Getter
		private final int raidLevel;
		
		
	}
	
	@RequiredArgsConstructor
	public enum Attempts {
		TRY_AGAIN(5),
		PERSISTENCE(5),
		SOFTCORE(5),
		HARDCORE(10);
		
		@Getter
		private final int raidLevel;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Time {
		WALK(10),
		JOG(5),
		RUN(5),
		SPRINT(5);
		
		@Getter
		private final int raidLevel;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Help {
		SOME(15),
		LESS(10),
		NONE(15);
		
		@Getter
		private final int raidLevel;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Path {
		SEEKER(15),
		FINDER(25),
		MASTER(10);
		
		@Getter
		private final int raidLevel;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	
	@RequiredArgsConstructor
	public enum Overclocked {
		OVERCLOCKED_ONE(10),
		OVERCLOCKED_TWO(10),
		INSANITY(50);
		
		@Getter
		private final int raidLevel;
		
		public int getWidgetIx() {
			return ordinal() * 3;
		}
	}
	

	
}
