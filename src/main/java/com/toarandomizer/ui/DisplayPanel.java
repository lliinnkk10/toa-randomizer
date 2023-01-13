package com.toarandomizer.ui;

import com.toarandomizer.ToARandomizerConfig;
import com.toarandomizer.utils.Invocations;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static com.toarandomizer.utils.Icons.*;
import static com.toarandomizer.utils.Invocations.*;

@Slf4j
public class DisplayPanel extends JPanel {
	
	/**
	 * Overlay
	 */
	@Getter
	public static Set<Invocations> activeInvocations = EnumSet.noneOf(Invocations.class);
	@Getter
	public static Set<Invocations> blockedInvocations = EnumSet.noneOf(Invocations.class);
	
	/**
	 * Miscellaneous
	 */
	private final ToARandomizerConfig config;
	
	/**
	 * Labels with the logo, re-rolls and block information
	 */
	private final String rerollText = "Rerolls: ";
	private final JLabel jlRerolls = new JLabel( rerollText + 0 );
	private int rerolls = 0;
	
	private final String blockText = "Blocks: ";
	private final JLabel jlBlocks = new JLabel( blockText + 0 );
	private int blocks = 0;
	
	private final String levelText = "Raid Level: ";
	private final JLabel jlLevel = new JLabel( levelText + 0 );
	private int raidLevel = 0;
	
	/**
	 * Combo box with presets of invocations
	 */
	private final String[] presets = { "Beginner - RL 50", "Very Easy - RL 75", "Easy - RL 100", "Normal - RL 150", "Hard - RL 200", "Very Hard - RL 250", "Impossible - RL 300" };
	private final JComboBox<String> jcbPresets = new JComboBox<>(presets);
	
	/**
	 * Button for getting a new random invocation
	 */
	@Getter
	private final RandomizerButton jbRandomize = new RandomizerButton("Randomize an invocation", new Dimension(200, 30 ));
	private Invocations lastRerolled;
	private Attempts lastAttempts;
	private Time lastTime;
	private Help lastHelp;
	private Path lastPath;
	private Overclocked lastOverclocked;
	private Zebak lastZebak;
	
	/**
	 * Lists of invocations and their tiered counterparts
	 */
	public final ArrayList<Invocations> invocationsList = new ArrayList<>();
	public final ArrayList<Invocations.Attempts> attemptsList = new ArrayList<>();
	public final ArrayList<Invocations.Time> timeList = new ArrayList<>();
	public final ArrayList<Invocations.Help> helpList = new ArrayList<>();
	public final ArrayList<Invocations.Path> pathList = new ArrayList<>();
	public final ArrayList<Invocations.Overclocked> overclockList = new ArrayList<>();
	public final ArrayList<Invocations.Zebak> zebakList = new ArrayList<>();
	public final ArrayList<String> blockedList = new ArrayList<>();
	
	/**
	 * Panels where the randomized invocations get added to Runelite
	 */
	private final JPanel jpInvocations = new JPanel(  );
	private final JPanel jpNewInvocation = new JPanel(  );
	private final JPanel jpBlockInvocation = new JPanel(  );
	
	
	@Getter
	private final RandomizerButton jbReset = new RandomizerButton("New Run", new Dimension(50, 30 ));

	
	public DisplayPanel(ToARandomizerConfig config) {
		super();
		
		this.config = config;
		
		Font font = new Font("Arial", Font.BOLD, 15);
		
		setLayout(new MigLayout( "", "", "5[]5[]10[]10[]5[]15[]10[]20" ));
		setBorder(new EmptyBorder(1, 1, 1, 1));
		
		// Logo, re-roll and block properties
		JLabel jlLogo = new JLabel("ToA Randomizer");
		jlLogo.setFont(font);
		jlRerolls.setFont(font);
		
		RandomizerButton jbReroll = new RandomizerButton("Re-roll", new Dimension(60, 30));
		jbReroll.addActionListener(e -> {
			if (rerolls == 0)
				return;
			
			reroll();
			
			rerolls--;
			setRerolls(rerolls);
			updateUI();
			
		});
		
		jlBlocks.setFont(font);
		RandomizerButton jbBlock = new RandomizerButton("Block", new Dimension(60, 30));
		jbBlock.addActionListener(e -> {
			if (blocks == 0)
				return;
			
			InvocationPanel panel = (InvocationPanel) jpNewInvocation.getComponent(0);
			
			blockFollowing(panel);
			reroll();
			
			panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.RED, Color.RED));
			jpBlockInvocation.add(panel);
			blocks--;
			blockedInvocations.add(getInvocation(panel));
			setBlocks(blocks);
			
			updateUI();
		});
		jlLevel.setFont(font);
		
		
		// Combo box function and properties
		jcbPresets.setEditable(false);
		jcbPresets.setSelectedIndex(0);
		
		
		// Randomizer button function and properties
		jbRandomize.addActionListener(e -> addRaidLevel(randomizeInvocation()));
		jbRandomize.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                      Color.GREEN,
		                                                      new Color(0, 127, 14)));
		
		// Reset button function and properties
		jbReset.addActionListener(e -> {
			if (JOptionPane.showConfirmDialog(this, "This will reset all current invocations.", "Are you sure you want to start a new run?", JOptionPane.YES_NO_OPTION) == 0) {
				reset( );
				addRaidLevel(jcbPresets.getItemAt(jcbPresets.getSelectedIndex( )));
				setRerollsAndBlocks(jcbPresets.getItemAt(jcbPresets.getSelectedIndex( )));
			}
		});
		jbReset.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                  Color.ORANGE,
		                                                  new Color(124, 18, 6)));
		
		// Invocation panels properties
		JScrollPane jspInvocations = new JScrollPane(jpInvocations);
		jspInvocations.setPreferredSize(new Dimension(218, 450 ));
		jspInvocations.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jspInvocations.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jspInvocations.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                         new Color(255, 127, 0),
		                                                         new Color(197, 255, 81)));
		
		jpInvocations.setLayout(new MigLayout( "align center, wrap", "", "[]10[]" ));
		
		// New Invocation panel
		jpNewInvocation.setPreferredSize(new Dimension( 220, 60 ));
		jpNewInvocation.setLayout(new MigLayout( "align center" ));
		jpNewInvocation.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
                new Color(0, 140, 255),
                new Color(12, 190, 255)));
		
		// Blocked Invocation panel
		jpBlockInvocation.setPreferredSize(new Dimension( 220, 190 ));
		jpBlockInvocation.setLayout(new MigLayout( "align center, wrap", "", "[]10[]" ));
		jpBlockInvocation.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                            new Color(119, 0, 0),
		                                                            Color.RED));
		
		// Add content to this panel
		add(jlLogo, "align center, wrap");
		add(jlRerolls, "align center, split 2");
		add(jlBlocks, "wrap");
		add(jlLevel, "align center, wrap");
		
		add(jcbPresets, "align center, wrap");
		
		add(jbRandomize, "align center, wrap");
		add(jpNewInvocation, "align center, wrap");
		
		add(jbReroll, "align center, split 2");
		add(jbBlock, "wrap");
		
		add(jspInvocations, "align center, wrap");
		
		add(jpBlockInvocation, "align center, wrap");
		
		add(jbReset, "align center, wrap");
	}
	
	private void reroll() {
		try {
			invocationsList.remove(lastRerolled);
			helpList.remove(lastHelp);
			timeList.remove(lastTime);
			pathList.remove(lastPath);
			overclockList.remove(lastOverclocked);
			zebakList.remove(lastZebak);
			attemptsList.remove(lastAttempts);
		} catch (Exception ignored) {
		}
		
		InvocationPanel panel = (InvocationPanel) jpNewInvocation.getComponent(0);
		removeLevel(panel);
		activeInvocations.remove(getInvocation(panel));
		jpNewInvocation.removeAll();
		addRaidLevel(randomizeInvocation( ));
	}
	
	private int getRaidLevel(Invocations invoc) {
		int newRaidLevel = 0;
		switch (invoc) {
			case ATTEMPTS -> newRaidLevel = switch (attemptsList.size()) {
				case 1 -> Attempts.TRY_AGAIN.getRaidLevel();
				case 2 -> Attempts.PERSISTENCE.getRaidLevel();
				case 3 -> Attempts.SOFTCORE.getRaidLevel();
				case 4 -> Attempts.HARDCORE.getRaidLevel();
				default -> 0;
			};
			case TIME -> newRaidLevel = switch (timeList.size()) {
				case 1 -> Time.WALK_FOR_IT.getRaidLevel();
				case 2 -> Time.JOG_FOR_IT.getRaidLevel();
				case 3 -> Time.RUN_FOR_IT.getRaidLevel();
				case 4 -> Time.SPRINT_FOR_IT.getRaidLevel();
				default -> 0;
			};
			case HELP -> newRaidLevel = switch (helpList.size()) {
				case 1 -> Help.NEED_SOME_HELP.getRaidLevel();
				case 2 -> Help.NEED_LESS_HELP.getRaidLevel();
				case 3 -> Help.NO_HELP_NEEDED.getRaidLevel();
				default -> 0;
			};
			case PATH_LEVEL -> newRaidLevel = switch (pathList.size()) {
				case 1 -> Path.PATH_SEEKER.getRaidLevel();
				case 2 -> Path.PATH_FINDER.getRaidLevel();
				case 3 -> Path.PATH_MASTER.getRaidLevel();
				default -> 0;
			};
			case OVERCLOCKED -> newRaidLevel = switch (overclockList.size()) {
				case 1 -> Overclocked.OVERCLOCKED_ONE.getRaidLevel();
				case 2 -> Overclocked.OVERCLOCKED_TWO.getRaidLevel();
				case 3 -> Overclocked.INSANITY.getRaidLevel();
				default -> 0;
			};
			case ZEBAK -> newRaidLevel = switch (zebakList.size()) {
				case 1 -> Zebak.NOT_JUST_A_HEAD.getRaidLevel();
				case 2 -> Zebak.ARTERIAL_SPRAY.getRaidLevel();
				case 3 -> Zebak.BLOOD_THINNERS.getRaidLevel();
				default -> 0;
			};
			default -> newRaidLevel = invoc.getRaidLevel();
		}
		return newRaidLevel;
	}
	
	private Invocations randomizeInvocation() {
		return switch (new Random().nextInt( 30 )) {
			case 0 -> addInvocation(ATTEMPTS);
			case 1 -> addInvocation(TIME);
			case 2 -> addInvocation(HELP);
			case 3 -> addInvocation(WALK_THE_PATH);
			case 4 -> addInvocation(PATH_LEVEL);
			case 5 -> addInvocation(QUIET_PRAYER);
			case 6 -> addInvocation(DEADLY_PRAYER);
			case 7 -> addInvocation(ON_A_DIET);
			case 8 -> addInvocation(DEHYDRATION);
			case 9 -> addInvocation(OVERLY_DRAINING);
			case 10 -> addInvocation(LIVELY_LARVAE);
			case 11 -> addInvocation(MORE_OVERLORDS);
			case 12 -> addInvocation(BLOWING_MUD);
			case 13 -> addInvocation(MEDIC);
			case 14 -> addInvocation(AERIAL_ASSAULT);
			case 15 -> addInvocation(ACCELERATION);
			case 16 -> addInvocation(PENETRATION);
			case 17 -> addInvocation(OVERCLOCKED);
			case 18 -> addInvocation(UPSET_STOMACH);
			case 19 -> addInvocation(DOUBLE_TROUBLE);
			case 20 -> addInvocation(KEEP_BACK);
			case 21 -> addInvocation(STAY_VIGILANT);
			case 22 -> addInvocation(FEELING_SPECIAL);
			case 23 -> addInvocation(MIND_THE_GAP);
			case 24 -> addInvocation(GOTTA_HAVE_FAITH);
			case 25 -> addInvocation(JUNGLE_JAPES);
			case 26 -> addInvocation(SHAKING_THINGS_UP);
			case 27 -> addInvocation(BOULDER_DASH);
			case 28 -> addInvocation(ANCIENT_HASTE);
			case 29 -> addInvocation(ZEBAK);
			default -> throw new IllegalStateException("Unexpected value: " + new Random( ).nextInt(32));
		};
	}
	
	private void blockFollowing(InvocationPanel panel) {
		String text = panel.text;
		switch (text) {
			// Attempts
			case "Try Again" -> {
				blockedList.add("Try Again");
				blockedList.add("Persistence");
				blockedList.add("Softcore");
				blockedList.add("Hardcore");
			}
			case "Persistence" -> {
				attemptsList.remove(Attempts.TRY_AGAIN);
				blockedList.add("Persistence");
				blockedList.add("Softcore");
				blockedList.add("Hardcore");
			}
			case "Softcore" -> {
				attemptsList.remove(Attempts.PERSISTENCE);
				blockedList.add("Softcore");
				blockedList.add("Hardcore");
			}
			case "Hardcore" -> {
				attemptsList.remove(Attempts.SOFTCORE);
				blockedList.add("Hardcore");
			}
			// Time
			case "Walk for it" -> {
				blockedList.add("Walk for it");
				blockedList.add("Jog for it");
				blockedList.add("Run for it");
				blockedList.add("Sprint for it");
			}
			case "Jog for it" -> {
				timeList.remove(Time.WALK_FOR_IT);
				blockedList.add("Jog for it");
				blockedList.add("Run for it");
				blockedList.add("Sprint for it");
			}
			case "Run for it" -> {
				timeList.remove(Time.JOG_FOR_IT);
				blockedList.add("Run for it");
				blockedList.add("Sprint for it");
			}
			case "Sprint for it" -> {
				timeList.remove(Time.RUN_FOR_IT);
				blockedList.add("Sprint for it");
			}
			// Help
			case "Need Some Help?" -> {
				blockedList.add("Need Some Help?");
				blockedList.add("Need Less Help?");
				blockedList.add("No Help Needed");
			}
			case "Need Less Help?" -> {
				helpList.remove(Help.NEED_SOME_HELP);
				blockedList.add("Need Less Help?");
				blockedList.add("No Help Needed");
			}
			case "No Help Needed" -> {
				helpList.remove(Help.NEED_LESS_HELP);
				blockedList.add("No Help Needed");
			}
			// Path level
			case "Pathseeker" -> {
				blockedList.add("Pathseeker");
				blockedList.add("Pathfinder");
				blockedList.add("Pathmaster");
			}
			case "Pathfinder" -> {
				pathList.remove(Path.PATH_SEEKER);
				blockedList.add("Pathfinder");
				blockedList.add("Pathmaster");
			}
			case "Pathmaster" -> {
				pathList.remove(Path.PATH_FINDER);
				blockedList.add("Pathmaster");
			}
			// Overclocked
			case "Overclocked" -> {
				blockedList.add("Overclocked");
				blockedList.add("Overclocked 2");
				blockedList.add("Insanity");
			}
			case "Overclocked 2" -> {
				overclockList.remove(Overclocked.OVERCLOCKED_ONE);
				blockedList.add("Overclocked 2");
				blockedList.add("Insanity");
			}
			case "Insanity" -> {
				overclockList.remove(Overclocked.OVERCLOCKED_TWO);
				blockedList.add("Insanity");
			}
			case "Not Just a Head" -> {
				blockedList.add("Not Just a Head");
				blockedList.add("Arterial Spray");
				blockedList.add("Blood Thinners");
			}
			case "Arterial Spray" -> {
				zebakList.remove(Zebak.NOT_JUST_A_HEAD);
				blockedList.add("Arterial Spray");
				blockedList.add("Blood Thinners");
			}
			case "Blood Thinners" -> {
				zebakList.remove(Zebak.ARTERIAL_SPRAY);
				blockedList.add("Blood Thinners");
			}
		}
	}
	
	private Invocations addInvocation(Invocations invocation) {
		System.out.println("Adding: " + invocation + " With Raid Level: " + invocation.getRaidLevel());
		if (invocationsList.contains(invocation)) {
			System.out.println("Already added! Trying again!");
			randomizeInvocation();
		} else {
				
			switch (invocation) {
				case ATTEMPTS -> {
					switch (attemptsList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							attemptsList.add(Attempts.TRY_AGAIN);
							setLastAttempts(Attempts.TRY_AGAIN);
							activeInvocations.add(TRY_AGAIN);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							attemptsList.add(Attempts.PERSISTENCE);
							setLastAttempts(Attempts.PERSISTENCE);
							activeInvocations.add(PERSISTENCE);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							attemptsList.add(Attempts.SOFTCORE);
							setLastAttempts(Attempts.SOFTCORE);
							activeInvocations.add(SOFTCORE);
							return invocation;
						}
						case 3 -> {
							addInvocationPanel(invocation);
							invocationsList.add(ATTEMPTS);
							attemptsList.add(Attempts.HARDCORE);
							setLastAttempts(Attempts.HARDCORE);
							activeInvocations.add(HARDCORE);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case TIME -> {
					switch (timeList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							timeList.add(Time.WALK_FOR_IT);
							setLastTime(Time.WALK_FOR_IT);
							activeInvocations.add(WALK_FOR_IT);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							timeList.add(Time.JOG_FOR_IT);
							setLastTime(Time.JOG_FOR_IT);
							activeInvocations.add(JOG_FOR_IT);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							timeList.add(Time.RUN_FOR_IT);
							setLastTime(Time.RUN_FOR_IT);
							activeInvocations.add(RUN_FOR_IT);
							return invocation;
						}
						case 3 -> {
							addInvocationPanel(invocation);
							invocationsList.add(TIME);
							timeList.add(Time.SPRINT_FOR_IT);
							setLastTime(Time.SPRINT_FOR_IT);
							activeInvocations.add(SPRINT_FOR_IT);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case HELP -> {
					switch (helpList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							helpList.add(Help.NEED_SOME_HELP);
							setLastHelp(Help.NEED_SOME_HELP);
							activeInvocations.add(NEED_SOME_HELP);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							helpList.add(Help.NEED_LESS_HELP);
							setLastHelp(Help.NEED_LESS_HELP);
							activeInvocations.add(NEED_LESS_HELP);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							invocationsList.add(HELP);
							helpList.add(Help.NO_HELP_NEEDED);
							setLastHelp(Help.NO_HELP_NEEDED);
							activeInvocations.add(NO_HELP_NEEDED);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case PATH_LEVEL -> {
					switch (pathList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							pathList.add(Path.PATH_SEEKER);
							setLastPath(Path.PATH_SEEKER);
							activeInvocations.add(PATH_SEEKER);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							pathList.add(Path.PATH_FINDER);
							setLastPath(Path.PATH_FINDER);
							activeInvocations.add(PATH_FINDER);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							invocationsList.add(PATH_LEVEL);
							pathList.add(Path.PATH_MASTER);
							setLastPath(Path.PATH_MASTER);
							activeInvocations.add(PATH_MASTER);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case OVERCLOCKED -> {
					switch (overclockList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							overclockList.add(Overclocked.OVERCLOCKED_ONE);
							setLastOverclocked(Overclocked.OVERCLOCKED_ONE);
							activeInvocations.add(OVERCLOCKED_ONE);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							overclockList.add(Overclocked.OVERCLOCKED_TWO);
							setLastOverclocked(Overclocked.OVERCLOCKED_TWO);
							activeInvocations.add(OVERCLOCKED_TWO);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							invocationsList.add(OVERCLOCKED);
							overclockList.add(Overclocked.INSANITY);
							setLastOverclocked(Overclocked.INSANITY);
							activeInvocations.add(INSANITY);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case ZEBAK -> {
					switch (zebakList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							zebakList.add(Zebak.NOT_JUST_A_HEAD);
							setLastZebak(Zebak.NOT_JUST_A_HEAD);
							activeInvocations.add(NOT_JUST_A_HEAD);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							zebakList.add(Zebak.ARTERIAL_SPRAY);
							setLastZebak(Zebak.ARTERIAL_SPRAY);
							activeInvocations.add(ARTERIAL_SPRAY);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							zebakList.add(Zebak.BLOOD_THINNERS);
							setLastZebak(Zebak.BLOOD_THINNERS);
							activeInvocations.add(BLOOD_THINNERS);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
			}
			
			addInvocationPanel(invocation);
			invocationsList.add(invocation);
			setLastRerolled(invocation);
			activeInvocations.add(invocation);
		}
		
		updateUI();
		return invocation;
	}

	/**
	 * Getting the invocation from panel
	 */
	private Invocations getInvocation(InvocationPanel panel) {
		String text = panel.text;
		return switch (text) {
			// Attempts
			case "Try Again" -> TRY_AGAIN;
			case "Persistence" -> PERSISTENCE;
			case "Softcore" -> SOFTCORE;
			case "Hardcore" -> HARDCORE;
			// Time
			case "Walk For It" -> WALK_FOR_IT;
			case "Jog For It" -> JOG_FOR_IT;
			case "Run For It" -> RUN_FOR_IT;
			case "Sprint For It" -> SPRINT_FOR_IT;
			// Help
			case "Need Some Help?" -> NEED_SOME_HELP;
			case "Need Less Help?" -> NEED_LESS_HELP;
			case "No Help Needed" -> NO_HELP_NEEDED;
			// Restoration
			case "Quiet Prayers" -> QUIET_PRAYER;
			case "Deadly Prayers" -> DEADLY_PRAYER;
			case "On a Diet" -> ON_A_DIET;
			case "Dehydration" -> DEHYDRATION;
			case "Overly Draining" -> OVERLY_DRAINING;
			// Path level
			case "Walk the Path" -> WALK_THE_PATH;
			case "Pathseeker" -> PATH_SEEKER;
			case "Pathfinder" -> PATH_FINDER;
			case "Pathmaster" -> PATH_MASTER;
			// Overclocked
			case "Ancient Haste" -> ANCIENT_HASTE;
			case "Acceleration" -> ACCELERATION;
			case "Penetration" -> PENETRATION;
			case "Overclocked" -> OVERCLOCKED_ONE;
			case "Overclocked Two" -> OVERCLOCKED_TWO;
			case "Insanity" -> INSANITY;
			// Zebak
			case "Not Just A Head" -> NOT_JUST_A_HEAD;
			case "Arterial Spray" -> ARTERIAL_SPRAY;
			case "Blood Thinners" -> BLOOD_THINNERS;
			case "Upset Stomach" -> UPSET_STOMACH;
			// Kephri
			case "Lively Larvae" -> LIVELY_LARVAE;
			case "More Overlords" -> MORE_OVERLORDS;
			case "Blowing Mud" -> BLOWING_MUD;
			case "Medic!" -> MEDIC;
			case "Aerial Assault" -> AERIAL_ASSAULT;
			// Akkha
			case "Double Trouble" -> DOUBLE_TROUBLE;
			case "Keep Back" -> KEEP_BACK;
			case "Stay Vigilant" -> STAY_VIGILANT;
			case "Feeling Special?" -> FEELING_SPECIAL;
			// Ba-Ba
			case "Mind the Gap!" -> MIND_THE_GAP;
			case "Gotta Have Faith" -> GOTTA_HAVE_FAITH;
			default -> JUNGLE_JAPES;
			case "Shaking Things Up" -> SHAKING_THINGS_UP;
			case "Boulderdash" -> BOULDER_DASH;
		};
	}
	
	private void addInvocationPanel(Invocations invocation) {
		InvocationPanel panel = createInvocationPanel(invocation);
		// Check if the list of blocked panels contains the newly created, then randomize a new instead
		if (blockedList.contains(panel.text)) {
			randomizeInvocation();
			return;
		}
		
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                new Color(0, 128, 255),
		                                                new Color(89, 255, 246)));
		if (jpNewInvocation.getComponents().length == 0) {
			jpNewInvocation.add(panel, BorderLayout.NORTH);
			updateUI();
		} else {
			InvocationPanel oldPanel = (InvocationPanel) jpNewInvocation.getComponent(0);
			oldPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.ORANGE, Color.YELLOW));
			jpInvocations.add(oldPanel);
			final boolean[] blockedFlag = { false };
			
			oldPanel.addMouseListener(new MouseAdapter( ) {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!blockedFlag[0] && blocks > 0) {
						if (e.getClickCount() == 2) {
							if (JOptionPane.showConfirmDialog(e.getComponent(), "Are you sure you want to spend a block to block this invocation?") == 0) {
								blockFollowing(oldPanel);
								blocks--;
								setBlocks(blocks);
								oldPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.RED, Color.RED));
								jpInvocations.remove(oldPanel);
								removeLevel(oldPanel);
								jpBlockInvocation.add(oldPanel);
								blockedFlag[0] = true;
								addRaidLevel(randomizeInvocation());
								blockedInvocations.add(getInvocation(oldPanel));
								activeInvocations.remove(getInvocation(oldPanel));
								updateUI( );
							}
						}
					}
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					if (!blockedFlag[0])
						oldPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.RED, Color.RED));
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if (!blockedFlag[0])
						oldPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.ORANGE, Color.YELLOW));
				}
		
			});
			
			jpNewInvocation.removeAll();

			jpNewInvocation.add(panel);
			
			updateUI();
		}
		updateUI();
	}
	
	private InvocationPanel createInvocationPanel(Invocations invocation) {
		switch (invocation) {
			case ATTEMPTS :
				switch (attemptsList.size()) {
					case 0 : return new InvocationPanel(INVOCATION_ICON_LIFE, "Try Again");
					case 1 : return new InvocationPanel(INVOCATION_ICON_LIFE, "Persistence");
					case 2 : return new InvocationPanel(INVOCATION_ICON_LIFE, "Softcore");
					case 3 : return new InvocationPanel(INVOCATION_ICON_LIFE, "Hardcore");
				}
				
			case TIME :
				switch (timeList.size()) {
					case 0 : return new InvocationPanel(INVOCATION_ICON_TIME, "Walk for it");
					case 1 : return new InvocationPanel(INVOCATION_ICON_TIME, "Jog for it");
					case 2 : return new InvocationPanel(INVOCATION_ICON_TIME, "Run for it");
					case 3 : return new InvocationPanel(INVOCATION_ICON_TIME, "Sprint for it");
				}
			
			case HELP :
				switch (helpList.size()) {
					case 0 : return new InvocationPanel(INVOCATION_ICON_HELP, "Need Some Help?");
					case 1 : return new InvocationPanel(INVOCATION_ICON_HELP, "Need Less Help?");
					case 2 : return new InvocationPanel(INVOCATION_ICON_HELP, "No Help Needed");
				}
				
			// Increasing difficulty
			case WALK_THE_PATH : return new InvocationPanel(INVOCATION_ICON_PATH_WALK, "Walk the Path");
			case PATH_LEVEL :
				switch (pathList.size()) {
					case 0 : return new InvocationPanel(INVOCATION_ICON_PATH_LEVEL, "Pathseeker");
					case 1 : return new InvocationPanel(INVOCATION_ICON_PATH_LEVEL, "Pathfinder");
					case 2 : return new InvocationPanel(INVOCATION_ICON_PATH_LEVEL, "Pathmaster");
				}
				
			// Wardens
			case ANCIENT_HASTE : return new InvocationPanel(INVOCATION_ICON_WARDEN, "Ancient Haste");
			case ACCELERATION : return new InvocationPanel(INVOCATION_ICON_WARDEN, "Acceleration");
			case PENETRATION : return new InvocationPanel(INVOCATION_ICON_WARDEN, "Penetration");
			case OVERCLOCKED :
				switch (overclockList.size()) {
					case 0 : return new InvocationPanel(INVOCATION_ICON_WARDEN, "Overclocked");
					case 1 : return new InvocationPanel(INVOCATION_ICON_WARDEN, "Overclocked 2");
					case 2 : return new InvocationPanel(INVOCATION_ICON_WARDEN, "Insanity");
				}
				
			// Prayers
			case QUIET_PRAYER : return new InvocationPanel(INVOCATION_ICON_PRAYER, "Quiet Prayers");
			case DEADLY_PRAYER : return new InvocationPanel(INVOCATION_ICON_PRAYER, "Deadly Prayers");
			
			// Kephri
			case LIVELY_LARVAE : return new InvocationPanel(INVOCATION_ICON_KEPHRI, "Lively Larvae");
			case MORE_OVERLORDS : return new InvocationPanel(INVOCATION_ICON_KEPHRI, "More Overlords");
			case BLOWING_MUD : return new InvocationPanel(INVOCATION_ICON_KEPHRI, "Blowing Mud");
			case MEDIC : return new InvocationPanel(INVOCATION_ICON_KEPHRI, "Medic!");
			case AERIAL_ASSAULT : return new InvocationPanel(INVOCATION_ICON_KEPHRI, "Aerial Assault");
			
			// Zebak
			case UPSET_STOMACH : return new InvocationPanel(INVOCATION_ICON_ZEBAK, "Upset Stomach");
			case ZEBAK :
				switch (zebakList.size()) {
					case 0 : return new InvocationPanel(INVOCATION_ICON_ZEBAK, "Not Just a Head");
					case 1 : return new InvocationPanel(INVOCATION_ICON_ZEBAK, "Arterial Spray");
					case 2 : return new InvocationPanel(INVOCATION_ICON_ZEBAK, "Blood Thinners");
				}
				
			// Akkha
			case DOUBLE_TROUBLE : return new InvocationPanel(INVOCATION_ICON_AKKHA, "Double Trouble");
			case KEEP_BACK : return new InvocationPanel(INVOCATION_ICON_AKKHA, "Keep Back");
			case STAY_VIGILANT : return new InvocationPanel(INVOCATION_ICON_AKKHA, "Stay Vigilant");
			case FEELING_SPECIAL : return new InvocationPanel(INVOCATION_ICON_AKKHA, "Feeling Special");
			
			// Ba-Ba
			case MIND_THE_GAP : return new InvocationPanel(INVOCATION_ICON_BABA, "Mind the Gap!");
			case GOTTA_HAVE_FAITH : return new InvocationPanel(INVOCATION_ICON_BABA, "Gotta Have Faith");
			case JUNGLE_JAPES : return new InvocationPanel(INVOCATION_ICON_BABA, "Jungle Japes");
			case SHAKING_THINGS_UP : return new InvocationPanel(INVOCATION_ICON_BABA, "Shaking Things Up");
			case BOULDER_DASH : return new InvocationPanel(INVOCATION_ICON_BABA, "Boulderdash");
			
			// Restoration
			case ON_A_DIET : return new InvocationPanel(INVOCATION_ICON_DIET, "On a Diet");
			case DEHYDRATION : return new InvocationPanel(INVOCATION_ICON_DEHYDRATION, "Dehydration");
			case OVERLY_DRAINING : return new InvocationPanel(INVOCATION_ICON_DRAINING, "Overly Draining");
			
			default : return new InvocationPanel(INVOCATION_ICON_PATH_WALK, "Walk the Path");
		}
	}
	
	private void reset() {
		invocationsList.clear();
		attemptsList.clear();
		timeList.clear();
		helpList.clear();
		pathList.clear();
		zebakList.clear();
		blockedList.clear();
		overclockList.clear();
		
		jpInvocations.removeAll();
		jpNewInvocation.removeAll();
		jpBlockInvocation.removeAll();
		
		activeInvocations.clear();
		blockedInvocations.clear();
		
		setLastNull();
		
		rerolls = 0;
		blocks = 0;
		raidLevel = 0;
		
		updateUI();
	}
	
	/**
	 * Making sure the last rerolled is set and used correctly
	 */
	private void setLastRerolled(Invocations invocations) {
		lastRerolled = invocations;
		lastAttempts = null;
		lastOverclocked = null;
		lastPath = null;
		lastHelp = null;
		lastTime = null;
		lastZebak = null;
	}
	private void setLastAttempts(Attempts attempts) {
		lastAttempts = attempts;
		lastRerolled = null;
		lastOverclocked = null;
		lastPath = null;
		lastZebak = null;
		lastHelp = null;
		lastTime = null;
	}
	private void setLastTime(Time time) {
		lastTime = time;
		lastRerolled = null;
		lastOverclocked = null;
		lastZebak = null;
		lastPath = null;
		lastHelp = null;
		lastAttempts = null;
	}
	private void setLastOverclocked(Overclocked overclocked) {
		lastOverclocked = overclocked;
		lastPath = null;
		lastHelp = null;
		lastAttempts = null;
		lastZebak = null;
		lastRerolled = null;
		lastTime = null;
	}
	private void setLastHelp(Help help) {
		lastHelp = help;
		lastAttempts = null;
		lastRerolled = null;
		lastTime = null;
		lastOverclocked = null;
		lastZebak = null;
		lastPath = null;
	}
	private void setLastPath(Path path) {
		lastPath = path;
		lastAttempts = null;
		lastRerolled = null;
		lastTime = null;
		lastHelp = null;
		lastZebak = null;
		lastOverclocked = null;
	}
	private void setLastZebak(Zebak zebak) {
		lastZebak = zebak;
		lastPath = null;
		lastAttempts = null;
		lastRerolled = null;
		lastTime = null;
		lastHelp = null;
		lastOverclocked = null;
	}
	private void setLastNull() {
		lastPath = null;
		lastAttempts = null;
		lastRerolled = null;
		lastTime = null;
		lastHelp = null;
		lastOverclocked = null;
		lastZebak = null;
	}
	
	/**
	 * Setting the reroll and block amounts
	 */
	private void setRerolls(int rerolls) {
		this.rerolls = rerolls;
		jlRerolls.setText(rerollText + rerolls);
		updateUI();
	}
	
	private void setBlocks(int blocks) {
		this.blocks = blocks;
		jlBlocks.setText(blockText + blocks);
		updateUI();
	}
	
	private void setRerollsAndBlocks(int rerolls, int blocks) {
		setRerolls(rerolls);
		setBlocks(blocks);
		updateUI();
	}
	
	private void setRerollsAndBlocks(String input) {
		if (input.equals(presets[0])) setRerollsAndBlocks(5, 3);
		if (input.equals(presets[1])) setRerollsAndBlocks(3, 3);
		if (input.equals(presets[2])) setRerollsAndBlocks(3, 2);
		if (input.equals(presets[3])) setRerollsAndBlocks(2, 2);
		if (input.equals(presets[4])) setRerollsAndBlocks(2, 1);
		if (input.equals(presets[5])) setRerollsAndBlocks(1, 1);
		if (input.equals(presets[6])) setRerollsAndBlocks(0, 0);
	}
	
	/**
	 * Rolling the invocations randomly depending on what difficulty
	 */
	private void addRaidLevel(String input) {
		if (input.equals(presets[0])) loopRaidLevel(50);
		if (input.equals(presets[1])) loopRaidLevel(75);
		if (input.equals(presets[2])) loopRaidLevel(100);
		if (input.equals(presets[3])) loopRaidLevel(150);
		if (input.equals(presets[4])) loopRaidLevel(200);
		if (input.equals(presets[5])) loopRaidLevel(250);
		if (input.equals(presets[6])) loopRaidLevel(300);
	}
	
	private int addRaidLevel(Invocations invoc) {
		switch (invoc) {
			case ATTEMPTS -> {
				switch (attemptsList.size()) {
					case 1 -> raidLevel += Attempts.TRY_AGAIN.getRaidLevel();
					case 2 -> raidLevel += Attempts.PERSISTENCE.getRaidLevel();
					case 3 -> raidLevel += Attempts.SOFTCORE.getRaidLevel();
					case 4 -> raidLevel += Attempts.HARDCORE.getRaidLevel();
				}
			}
			case TIME -> {
				switch (timeList.size()) {
					case 1 -> raidLevel += Time.WALK_FOR_IT.getRaidLevel();
					case 2 -> raidLevel += Time.JOG_FOR_IT.getRaidLevel();
					case 3 -> raidLevel += Time.RUN_FOR_IT.getRaidLevel();
					case 4 -> raidLevel += Time.SPRINT_FOR_IT.getRaidLevel();
				}
			}
			case HELP -> {
				switch (helpList.size()) {
					case 1 -> raidLevel += Help.NEED_SOME_HELP.getRaidLevel();
					case 2 -> raidLevel += Help.NEED_LESS_HELP.getRaidLevel();
					case 3 -> raidLevel += Help.NO_HELP_NEEDED.getRaidLevel();
				}
			}
			case PATH_LEVEL -> {
				switch (pathList.size()) {
					case 1 -> raidLevel += Path.PATH_SEEKER.getRaidLevel();
					case 2 -> raidLevel += Path.PATH_FINDER.getRaidLevel();
					case 3 -> raidLevel += Path.PATH_MASTER.getRaidLevel();
				}
			}
			case OVERCLOCKED -> {
				switch (overclockList.size()) {
					case 1 -> raidLevel += Overclocked.OVERCLOCKED_ONE.getRaidLevel();
					case 2 -> raidLevel += Overclocked.OVERCLOCKED_TWO.getRaidLevel();
					case 3 -> raidLevel += Overclocked.INSANITY.getRaidLevel();
				}
			}
			case ZEBAK -> {
				switch (zebakList.size()) {
					case 1 -> raidLevel += Zebak.NOT_JUST_A_HEAD.getRaidLevel();
					case 2 -> raidLevel += Zebak.ARTERIAL_SPRAY.getRaidLevel();
					case 3 -> raidLevel += Zebak.BLOOD_THINNERS.getRaidLevel();
				}
			}
			default -> raidLevel += invoc.getRaidLevel( );
		}
		jlLevel.setText(levelText + raidLevel);
		return raidLevel;
	}
	
	private void removeLevel(InvocationPanel panel) {
		String text = panel.text;
		this.raidLevel -= switch (text) {
			// Attempts
			case "Try Again" -> Attempts.TRY_AGAIN.getRaidLevel();
			case "Persistence" -> Attempts.PERSISTENCE.getRaidLevel();
			case "Softcore" -> Attempts.SOFTCORE.getRaidLevel();
			case "Hardcore" -> Attempts.HARDCORE.getRaidLevel();
			// Time
			case "Walk for it" -> Time.WALK_FOR_IT.getRaidLevel();
			case "Jog for it" -> Time.JOG_FOR_IT.getRaidLevel();
			case "Run for it" -> Time.RUN_FOR_IT.getRaidLevel();
			case "Sprint for it" -> Time.SPRINT_FOR_IT.getRaidLevel();
			// Help
			case "Need Some Help?" -> Help.NEED_SOME_HELP.getRaidLevel();
			case "Need Less Help?" -> Help.NEED_LESS_HELP.getRaidLevel();
			case "No Help Needed" -> Help.NO_HELP_NEEDED.getRaidLevel();
			// Restoration
			case "Quiet Prayers" -> QUIET_PRAYER.getRaidLevel();
			case "Deadly Prayers" -> DEADLY_PRAYER.getRaidLevel();
			case "On a Diet" -> ON_A_DIET.getRaidLevel();
			case "Dehydration" -> DEHYDRATION.getRaidLevel();
			case "Overly Draining" -> OVERLY_DRAINING.getRaidLevel();
			// Path level
			case "Pathseeker" -> Path.PATH_SEEKER.getRaidLevel();
			case "Pathfinder" -> Path.PATH_FINDER.getRaidLevel();
			case "Pathmaster" -> Path.PATH_MASTER.getRaidLevel();
			case "Walk the Path" -> WALK_THE_PATH.getRaidLevel();
			// Overclocked
			case "Ancient Haste" -> ANCIENT_HASTE.getRaidLevel();
			case "Acceleration" -> ACCELERATION.getRaidLevel();
			case "Penetration" -> PENETRATION.getRaidLevel();
			case "Overclocked" -> Overclocked.OVERCLOCKED_ONE.getRaidLevel();
			case "Overclocked 2" -> Overclocked.OVERCLOCKED_TWO.getRaidLevel();
			case "Insanity" -> Overclocked.INSANITY.getRaidLevel();
			// Zebak
			case "Not Just a Head" -> Zebak.NOT_JUST_A_HEAD.getRaidLevel();
			case "Arterial Spray" -> Zebak.ARTERIAL_SPRAY.getRaidLevel();
			case "Blood Thinners" -> Zebak.BLOOD_THINNERS.getRaidLevel();
			case "Upset Stomach" -> UPSET_STOMACH.getRaidLevel();
			// Kephri
			case "Lively Larvae" -> LIVELY_LARVAE.getRaidLevel();
			case "More Overlords" -> MORE_OVERLORDS.getRaidLevel();
			case "Blowing Mud" -> BLOWING_MUD.getRaidLevel();
			case "Medic!" -> MEDIC.getRaidLevel();
			case "Aerial Assault" -> AERIAL_ASSAULT.getRaidLevel();
			// Akkha
			case "Double Trouble" -> DOUBLE_TROUBLE.getRaidLevel();
			case "Keep Back" -> KEEP_BACK.getRaidLevel();
			case "Stay Vigilant" -> STAY_VIGILANT.getRaidLevel();
			case "Feeling Special?" -> FEELING_SPECIAL.getRaidLevel();
			// Ba-Ba
			case "Mind the Gap!" -> MIND_THE_GAP.getRaidLevel();
			case "Gotta Have Faith" -> GOTTA_HAVE_FAITH.getRaidLevel();
			case "Jungle Japes" -> JUNGLE_JAPES.getRaidLevel();
			case "Shaking Things Up" -> SHAKING_THINGS_UP.getRaidLevel();
			case "Boulderdash" -> BOULDER_DASH.getRaidLevel();
			default -> 0;
		};
		jlLevel.setText(levelText + raidLevel);
	}
	
	private void loopRaidLevel(int goal) {
		System.out.println("STARTING A NEW RUN");
		System.out.println("---------------------------------------------");
		addConfigInvocations();
		if (raidLevel > goal) {
			reset();
			JOptionPane.showMessageDialog(this,
			                              "Please try disabling some preset invocations in the config",
			                              "Too many invocations on by default for this raid level preset",
			                              JOptionPane.WARNING_MESSAGE);
			return;
		}
		int attempts = 0;
		while (raidLevel != goal) {
			if (raidLevel > goal) {
				System.out.println("Reset!");
				System.out.println("---------------------------------------------");
				attempts++;
				reset( );
				addConfigInvocations();
			}
			if (goal - raidLevel == 5) {
				if (invocationsList.contains(JUNGLE_JAPES)) addRaidLevel(addInvocation(LIVELY_LARVAE));
				else if (invocationsList.contains(LIVELY_LARVAE)) addRaidLevel(addInvocation(JUNGLE_JAPES));
			}
			if (raidLevel == goal) {
				break;
			}
			
			if (attempts >= 150) {
				reset();
				JOptionPane.showMessageDialog(this,
				                              "Please try disabling some preset invocations in the config",
				                              "Could not find an invocation with right amount of raid levels",
				                              JOptionPane.WARNING_MESSAGE);
				addConfigInvocations();
				break;
			}
			
			System.out.println();
			System.out.println("Attempts: " + attempts);
			System.out.println("Raid level: " + raidLevel);
			
			int invoc = addRaidLevel(randomizeInvocation());
			
			System.out.println("New Raid level: " + raidLevel + " With: " + invoc);
			System.out.println("Invocations: " + invocationsList);
			System.out.println("Zebak: " + zebakList);
			System.out.println("Help: " + helpList);
			System.out.println("Time: " + timeList);
			System.out.println("Attempts: " + attemptsList);
			System.out.println("Paths: " + pathList);
			System.out.println("Overclocks: " + overclockList);
			System.out.println();
		}
	}
	
	private void addConfigInvocations() {
		// Attempts
		if (config.attempts().ordinal() != 0) {
			switch (config.attempts().ordinal()) {
				case 1 -> repeatAdd(1, ATTEMPTS);
				case 2 -> repeatAdd(2, ATTEMPTS);
				case 3 -> repeatAdd(3, ATTEMPTS);
				case 4 -> repeatAdd(4, ATTEMPTS);
			}
		}
		// Time
		if (config.time().ordinal() != 0) {
			switch (config.attempts().ordinal()) {
				case 1 -> repeatAdd(1, TIME);
				case 2 -> repeatAdd(2, TIME);
				case 3 -> repeatAdd(3, TIME);
				case 4 -> repeatAdd(4, TIME);
			}
		}
		// Help
		if (config.help().ordinal() != 0) {
			switch (config.help().ordinal()) {
				case 1 -> repeatAdd(1, HELP);
				case 2 -> repeatAdd(2, HELP);
				case 3 -> repeatAdd(3, HELP);
			}
		}
		// Path
		if (config.walkThePath()) addRaidLevel(addInvocation(WALK_THE_PATH));
		if (config.path().ordinal() != 0) {
			switch (config.path().ordinal()) {
				case 1 -> repeatAdd(1, PATH_LEVEL);
				case 2 -> repeatAdd(2, PATH_LEVEL);
				case 3 -> repeatAdd(3, PATH_LEVEL);
			}
		}
		// Wardens
		if (config.ancientHaste()) addRaidLevel(addInvocation(ANCIENT_HASTE));
		if (config.acceleration()) addRaidLevel(addInvocation(ACCELERATION));
		if (config.penetration()) addRaidLevel(addInvocation(PENETRATION));
		if (config.overclocked().ordinal() != 0) {
			switch (config.overclocked().ordinal()) {
				case 1 -> repeatAdd(1, OVERCLOCKED);
				case 2 -> repeatAdd(2, OVERCLOCKED);
				case 3 -> repeatAdd(3, OVERCLOCKED);
			}
		}
		// Ba-Ba
		if (config.mindTheGap()) addRaidLevel(addInvocation(MIND_THE_GAP));
		if (config.gottaHaveFaith()) addRaidLevel(addInvocation(GOTTA_HAVE_FAITH));
		if (config.jungleJapes()) addRaidLevel(addInvocation(JUNGLE_JAPES));
		if (config.shakingThingsUp()) addRaidLevel(addInvocation(SHAKING_THINGS_UP));
		if (config.boulderdash()) addRaidLevel(addInvocation(BOULDER_DASH));
		// Akkha
		if (config.doubleTrouble()) addRaidLevel(addInvocation(DOUBLE_TROUBLE));
		if (config.keepBack()) addRaidLevel(addInvocation(KEEP_BACK));
		if (config.stayVigilant()) addRaidLevel(addInvocation(STAY_VIGILANT));
		if (config.feelingSpecial()) addRaidLevel(addInvocation(FEELING_SPECIAL));
		// Kephri
		if (config.livelyLarvae()) addRaidLevel(addInvocation(LIVELY_LARVAE));
		if (config.moreOverlords()) addRaidLevel(addInvocation(MORE_OVERLORDS));
		if (config.blowingMud()) addRaidLevel(addInvocation(BLOWING_MUD));
		if (config.medic()) addRaidLevel(addInvocation(MEDIC));
		if (config.aerialAssault()) addRaidLevel(addInvocation(AERIAL_ASSAULT));
		// Prayers
		if (config.quietPrayers()) addRaidLevel(addInvocation(QUIET_PRAYER));
		if (config.deadlyPrayers()) addRaidLevel(addInvocation(DEADLY_PRAYER));
		// Restoration
		if (config.onADiet()) addRaidLevel(addInvocation(ON_A_DIET));
		if (config.dehydration()) addRaidLevel(addInvocation(DEHYDRATION));
		if (config.overlyDraining()) addRaidLevel(addInvocation(OVERLY_DRAINING));
		// Zebak
		if (config.upsetStomach()) addRaidLevel(addInvocation(UPSET_STOMACH));
		if (config.notJustAHead().ordinal() != 0) {
			switch (config.notJustAHead().ordinal()) {
				case 1 -> repeatAdd(1, ZEBAK);
				case 2 -> repeatAdd(2, ZEBAK);
				case 3 -> repeatAdd(3, ZEBAK);
			}
		}
	}
	
	private void repeatAdd(int amount, Invocations invocation) {
		for (int i = 0; i < amount; i++) {
			addRaidLevel(addInvocation(invocation));
		}
		
	}
}