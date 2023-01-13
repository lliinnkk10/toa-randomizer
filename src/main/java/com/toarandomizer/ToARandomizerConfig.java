package com.toarandomizer;

import net.runelite.client.config.*;

@ConfigGroup("toarandomizer")
public interface ToARandomizerConfig extends Config
{
	/**
	 * Config Sections
	 */
	@ConfigSection(name = "Suggested Rules", description = "These are the rules that this minigame was designed around", position = 0)
	String suggestedRulesSection = "suggestedRulesSection";
	
	@ConfigSection(name = "Attempt Invocations", description = "Attempts invocation on by default", position = 1, closedByDefault = true)
	String attemptsInvocationSection = "attemptsInvocationSection";
	
	@ConfigSection(name = "Timer Invocations", description = "Time invocation on by default", position = 2, closedByDefault = true)
	String timeInvocationSection = "timeInvocationSection";
	
	@ConfigSection(name = "Help Invocations", description = "Help invocation on by default", position = 3, closedByDefault = true)
	String helpInvocationSection = "helpInvocationSection";
	
	@ConfigSection(name = "Path Invocations", description = "Path invocation on by default", position = 4, closedByDefault = true)
	String pathInvocationSection = "pathInvocationSection";
	
	@ConfigSection(name = "Prayer Invocations", description = "Prayer invocations on by default", position = 5, closedByDefault = true)
	String prayersInvocationSection = "prayersInvocationSection";
	
	@ConfigSection(name = "Restoration Invocations", description = "Restoration invocations on by default", position = 6, closedByDefault = true)
	String restorationInvocationSettings = "restorationInvocationSettings";
	
	@ConfigSection(name = "Akkha Invocations", description = "Akkha invocations on by default", position = 7, closedByDefault = true)
	String akkhaInvocationSettings = "akkhaInvocationSettings";
	
	@ConfigSection(name = "Ba-Ba Invocations", description = "Ba-Ba invocations on by default", position = 8, closedByDefault = true)
	String babaInvocationsSection = "babaInvocationsSection";
	
	@ConfigSection(name = "Kephri Invocations", description = "Kephri invocations on by default", position = 9, closedByDefault = true)
	String kephriInvocationSettings = "kephriInvocationSettings";
	
	@ConfigSection(name = "Zebak invocations", description = "Zebak invocations on by default", position = 10, closedByDefault = true)
	String zebakInvocationSection = "zebakInvocationSection";
	
	@ConfigSection(name = "Wardens Invocations", description = "Wardens invocations on by default", position = 11, closedByDefault = true)
	String wardenInvocationSection = "wardenInvocationSection";
	
	/**
	 * Suggested Rules Section
	 */
	@ConfigItem(
			keyName = "suggestedRules",
			name = "Suggested Rules",
			description = "Do whatever you want but I recommend these rules per run",
			section = suggestedRulesSection,
			position = 0
	)
	default String suggestedRules() {
		return  "1. Select any invocations you want on by default with below options.\n" +
				"\n2. In the side panel, choose the starting difficulty and click 'New Run'\n" +
				"\n3. For every completed kill, randomize another invocation.\n" +
				"\n4. Should your team wipe then it's game over and you start a new run.\n" +
				"\n5. Have fun, of course!";
	}
	
	/**
	 * Attempts Invocation Settings
	 */
	enum Attempts {
		NONE,
		TRY_AGAIN,
		PERSISTENCE,
		SOFTCORE,
		HARDCORE
	}
	@ConfigItem(
			keyName = "attempts",
			name = "Attempts",
			description = "What 'Attempts' invocation will always be active?",
			section = attemptsInvocationSection,
			position = 0
	)
	default Attempts attempts() {
		return Attempts.NONE;
	}
	
	/**
	 * Time Invocation Settings
	 */
	enum Time {
		NONE,
		WALK_FOR_IT,
		JOG_FOR_IT,
		RUN_FOR_IT,
		SPRINT_FOR_IT
	}
	@ConfigItem(
			keyName = "time",
			name = "Time",
			description = "What 'Time' invocation will always be active?",
			section = timeInvocationSection,
			position = 0
	)
	default Time time() {
		return Time.NONE;
	}
	
	/**
	 * Help Invocation Settings
	 */
	enum Help {
		NONE,
		NEED_SOME_HELP,
		NEED_LESS_HELP,
		NO_HELP_NEEDED
	}
	@ConfigItem(
			keyName = "Help",
			name = "Help",
			description = "What 'Help' invocation will always be active?",
			section = helpInvocationSection,
			position = 0
	)
	default Help help() {
		return Help.NONE;
	}
	
	/**
	 * Path Invocation Settings
	 */
	enum Path {
		NONE,
		PATH_SEEKER,
		PATH_FINDER,
		PATH_MASTER
	}
	@ConfigItem(
			keyName = "path",
			name = "Path Level",
			description = "What 'Path Level' invocation will always be active?",
			section = pathInvocationSection,
			position = 0
	)
	default Path path() {
		return Path.NONE;
	}
	@ConfigItem(
			keyName = "pathWalk",
			name = "Walk The Path",
			description = "Should the invocation 'Walk The Path' always be active?",
			section = pathInvocationSection,
			position = 1
	)
	default boolean walkThePath() {
		return false;
	}
	
	/**
	 * Warden Invocation Settings
	 */
	enum Overclocked {
		NONE,
		OVERCLOCKED_ONE,
		OVERCLOCKED_TWO,
		INSANITY
	}
	@ConfigItem(
			keyName = "overclocked",
			name = "Overclocked",
			description = "What 'Overclocked' invocation will always be active?",
			section = wardenInvocationSection,
			position = 0
	)
	default Overclocked overclocked() {
		return Overclocked.NONE;
	}
	@ConfigItem(
			keyName = "ancientHaste",
			name = "Ancient Haste",
			description = "Should the invocation 'Ancient Haste' always be active?",
			section = wardenInvocationSection,
			position = 1
	)
	default boolean ancientHaste() {
		return false;
	}
	@ConfigItem(
			keyName = "acceleration",
			name = "Acceleration",
			description = "Should the invocation 'Acceleration' always be active?",
			section = wardenInvocationSection,
			position = 2
	)
	default boolean acceleration() {
		return false;
	}
	@ConfigItem(
			keyName = "penetration",
			name = "Penetration",
			description = "Should the invocation 'Penetration' always be active?",
			section = wardenInvocationSection,
			position = 3
	)
	default boolean penetration() {
		return false;
	}
	
	/**
	 * Zebak Invocation Settings
	 */
	enum Zebak {
		NONE,
		NOT_JUST_A_HEAD,
		HEAD_AND_SPRAY,
		HEAD_AND_THINNERS,
		ALL_THREE
	}
	@ConfigItem(
			keyName = "notJustAHead",
			name = "Not Just A Head",
			description = "Which 'Not Just A Head' will always be active?",
			section = zebakInvocationSection,
			position = 0
	)
	default Zebak notJustAHead() {
		return Zebak.NONE;
	}
	
	@ConfigItem(
			keyName = "upsetStomach",
			name = "Upset Stomach",
			description = "Should the invocation 'Upset Stomach' always be active?",
			section = zebakInvocationSection,
			position = 1
	)
	default boolean upsetStomach() {
		return false;
	}
	
	/**
	 * Ba-Ba Invocation Settings
	 */
	
	@ConfigItem(
			keyName = "mindTheGap",
			name = "Mind The Gap!",
			description = "Should the invocation 'Mind The Gap' always be active?",
			section = babaInvocationsSection,
			position = 1
	)
	default boolean mindTheGap() {
		return false;
	}
	
	@ConfigItem(
			keyName = "gottaHaveFaith",
			name = "Gotta Have Faith",
			description = "Should the invocation 'Gotta Have Faith' always be active?",
			section = babaInvocationsSection,
			position = 2
	)
	default boolean gottaHaveFaith() {
		return false;
	}
	
	@ConfigItem(
			keyName = "jungleJapes",
			name = "Jungle Japes",
			description = "Should the invocation 'Jungle Japes' always be active?",
			section = babaInvocationsSection,
			position = 3
	)
	default boolean jungleJapes() {
		return false;
	}
	
	@ConfigItem(
			keyName = "shakingThingsUp",
			name = "Shaking Things Up",
			description = "Should the invocation 'Shaking Things Up' always be active?",
			section = babaInvocationsSection,
			position = 4
	)
	default boolean shakingThingsUp() {
		return false;
	}
	
	@ConfigItem(
			keyName = "boulderdash",
			name = "Boulderdash",
			description = "Should the invocation 'Boulderdash' always be active?",
			section = babaInvocationsSection,
			position = 5
	)
	default boolean boulderdash() {
		return false;
	}
	
	/**
	 * Prayers Invocations Settings
	 */
	
	@ConfigItem(
			keyName = "quietPrayers",
			name = "Quiet Prayers",
			description = "Should the invocation 'Quiet Prayers' always be active?",
			section = prayersInvocationSection,
			position = 0
	)
	default boolean quietPrayers() {
		return false;
	}
	
	@ConfigItem(
			keyName = "deadlyPrayers",
			name = "Deadly Prayers",
			description = "Should the invocation 'Deadly Prayers' always be active?",
			section = prayersInvocationSection,
			position = 1
	)
	default boolean deadlyPrayers() {
		return false;
	}
	
	/**
	 * Restoration Invocation Settings
	 */
	
	@ConfigItem(
			keyName = "onADiet",
			name = "On A Diet",
			description = "Should the invocation 'On A Diet' always be active?",
			section = restorationInvocationSettings,
			position = 0
	)
	default boolean onADiet() {
		return false;
	}
	
	@ConfigItem(
			keyName = "dehydration",
			name = "Dehydration",
			description = "Should the invocation 'Dehydration' always be active?",
			section = restorationInvocationSettings,
			position = 1
	)
	default boolean dehydration() {
		return false;
	}
	
	@ConfigItem(
			keyName = "overlyDraining",
			name = "Overly Draining",
			description = "Should the invocation 'Overly Draining' always be active?",
			section = restorationInvocationSettings,
			position = 2
	)
	default boolean overlyDraining() {
		return false;
	}
	
	/**
	 * Kephri Invocation Settings
	 */
	@ConfigItem(
			keyName = "livelyLarvae",
			name = "Lively Larvae",
			description = "Should the invocation 'Lively Larvae' always be active?",
			section = kephriInvocationSettings,
			position = 0
	)
	default boolean livelyLarvae() {
		return false;
	}
	@ConfigItem(
			keyName = "moreOverlords",
			name = "More Overlords",
			description = "Should the invocation 'More Overlords' always be active?",
			section = kephriInvocationSettings,
			position = 1
	)
	default boolean moreOverlords() {
		return false;
	}
	@ConfigItem(
			keyName = "blowingMud",
			name = "Blowing Mud",
			description = "Should the invocation 'Blowing Mud' always be active?",
			section = kephriInvocationSettings,
			position = 2
	)
	default boolean blowingMud() {
		return false;
	}
	@ConfigItem(
			keyName = "medic",
			name = "Medic!",
			description = "Should the invocation 'Medic!' always be active?",
			section = kephriInvocationSettings,
			position = 3
	)
	default boolean medic() {
		return false;
	}
	@ConfigItem(
			keyName = "aerialAssault",
			name = "Aerial Assault",
			description = "Should the invocation 'Aerial Assault' always be active?",
			section = kephriInvocationSettings,
			position = 4
	)
	default boolean aerialAssault() {
		return false;
	}
	
	/**
	 * Akkha Invocation Settings
	 */
	@ConfigItem(
			keyName = "doubleTrouble",
			name = "Double Trouble",
			description = "Should the invocation 'Double Trouble' always be active?",
			section = akkhaInvocationSettings,
			position = 0
	)
	default boolean doubleTrouble() {
		return false;
	}
	@ConfigItem(
			keyName = "keepBack",
			name = "Keep Back",
			description = "Should the invocation 'Keep Back' always be active?",
			section = akkhaInvocationSettings,
			position = 1
	)
	default boolean keepBack() {
		return false;
	}
	@ConfigItem(
			keyName = "stayVigilant",
			name = "Stay Vigilant",
			description = "Should the invocation 'Stay Vigilant' always be active?",
			section = akkhaInvocationSettings,
			position = 2
	)
	default boolean stayVigilant() {
		return false;
	}
	@ConfigItem(
			keyName = "feelingSpecial",
			name = "Feeling Special",
			description = "Should the invocation 'Feeling Special' always be active?",
			section = akkhaInvocationSettings,
			position = 3
	)
	default boolean feelingSpecial() {
		return false;
	}
}