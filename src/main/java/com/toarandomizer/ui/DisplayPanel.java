package com.toarandomizer.ui;

import com.toarandomizer.utils.Invocations;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import net.runelite.api.Client;
import net.runelite.api.widgets.*;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static com.toarandomizer.utils.Icons.*;
import static com.toarandomizer.utils.Invocations.*;
import static com.toarandomizer.utils.Invocations.Attempts.*;
import static com.toarandomizer.utils.Invocations.Help.*;
import static com.toarandomizer.utils.Invocations.Overclocked.*;
import static com.toarandomizer.utils.Invocations.Path.*;
import static com.toarandomizer.utils.Invocations.Time.*;
import static com.toarandomizer.utils.Invocations.Zebak.*;

public class DisplayPanel extends JPanel {
	
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private Client client;
	
	/**
	 * Miscellaneous
	 */
	private final Font font = new Font("Arial", Font.BOLD, 15);
	
	/**
	 * Overlay
	 */
	public static final int WIDGET_ID_INVOCATIONS_PARENT = 790;
	public static final int WIDGET_ID_INVOCATIONS_CHILD = 65;
	@Getter
	private Set<Invocations> activeInvocations = EnumSet.noneOf(Invocations.class);
	
	/**
	 * Labels with the logo, re-rolls and block information
	 */
	private final JLabel jlLogo = new JLabel( "ToA Randomizer" );
	
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
	private final ArrayList<Invocations> invocationsList = new ArrayList<>();
	private final ArrayList<Invocations.Attempts> attemptsList = new ArrayList<>();
	private final ArrayList<Invocations.Time> timeList = new ArrayList<>();
	private final ArrayList<Invocations.Help> helpList = new ArrayList<>();
	private final ArrayList<Invocations.Path> pathList = new ArrayList<>();
	private final ArrayList<Invocations.Overclocked> overclockList = new ArrayList<>();
	private final ArrayList<Invocations.Zebak> zebakList = new ArrayList<>();
	private final ArrayList<String> blockedList = new ArrayList<>();
	
	/**
	 * Buttons for re-rolling or blocking the most recent invocation
	 */
	private final RandomizerButton jbReroll = new RandomizerButton( "Re-roll", new Dimension( 60, 30 ) );
	private final RandomizerButton jbBlock = new RandomizerButton( "Block", new Dimension( 60, 30 ) );
	
	/**
	 * Panels where the randomized invocations get added to Runelite
	 */
	private final JPanel jpInvocations = new JPanel(  );
	private final JScrollPane jspInvocations = new JScrollPane( jpInvocations );
	private final JPanel jpNewInvocation = new JPanel(  );
	private final JPanel jpBlockInvocation = new JPanel(  );
	
	
	@Getter
	private final RandomizerButton jbReset = new RandomizerButton("New Run", new Dimension(50, 30 ));

	
	public DisplayPanel() {
		super();
		
		setLayout(new MigLayout( "", "", "5[]5[]10[]10[]5[]15[]10[]20" ));
		setBorder(new EmptyBorder(1, 1, 1, 1));
		
		// Logo, re-roll and block properties
		jlLogo.setFont(font);
		jlRerolls.setFont(font);
		jbReroll.addActionListener(e -> {
			if (rerolls == 0)
				return;
			
			try {
				invocationsList.remove(lastRerolled);
				helpList.remove(lastHelp);
				timeList.remove(lastTime);
				pathList.remove(lastPath);
				overclockList.remove(lastOverclocked);
				zebakList.remove(lastZebak);
				attemptsList.remove(lastAttempts);
			} catch (Exception ex) {
				System.out.println(ex);
			}
			
			InvocationPanel panel = (InvocationPanel) jpNewInvocation.getComponent(0);
			removeLevel(panel);
			jpNewInvocation.removeAll();
			addRaidLevel(randomizeInvocation());
			rerolls--;
			setRerolls(rerolls);
			updateUI();
			
		});
		jlBlocks.setFont(font);
		jbBlock.addActionListener(e -> {
			if (blocks == 0)
				return;
			
			InvocationPanel panel = (InvocationPanel) jpNewInvocation.getComponent(0);
			removeLevel(panel);
			blockFollowing(panel);
			panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.RED, Color.RED));
			jpNewInvocation.remove(panel);
			jpBlockInvocation.add(panel);
			blocks--;
			setBlocks(blocks);
			updateUI();
		});
		jlLevel.setFont(font);
		
		
		// Combo box function and properties
		jcbPresets.setEditable(false);
		jcbPresets.setSelectedIndex(0);
		
		
		// Randomizer button function and properties
		jbRandomize.addActionListener(e -> {
			addRaidLevel(randomizeInvocation());
		});
		jbRandomize.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                      Color.GREEN,
		                                                      new Color(0, 127, 14)));
		
		// Reset button function and properties
		jbReset.addActionListener(e -> {
			reset();
			addRaidLevel(jcbPresets.getItemAt(jcbPresets.getSelectedIndex()));
			setRerollsAndBlocks(jcbPresets.getItemAt(jcbPresets.getSelectedIndex()));
		});
		jbReset.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
		                                                  Color.RED,
		                                                  new Color(124, 18, 6)));
		
		// Invocation panels properties
		jspInvocations.setPreferredSize(new Dimension( 218, 450 ));
		jspInvocations.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
	
	private void displayInvocation() {
		Widget parent = client.getWidget(WIDGET_ID_INVOCATIONS_PARENT, WIDGET_ID_INVOCATIONS_CHILD);
		if (parent == null || parent.isHidden() || parent.getChildren() == null) {
			return;
		}
		for (Invocations invoc : Invocations.values()) {
			Widget invocW = parent.getChild(invoc.getWidgetIx());
			invocW.setFilled(false);
			invocW.setTextColor(Color.green.getRGB());
			invocW.setOpacity(0);
		}
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
				attemptsList.remove(TRY_AGAIN);
				blockedList.add("Persistence");
				blockedList.add("Softcore");
				blockedList.add("Hardcore");
			}
			case "Softcore" -> {
				attemptsList.remove(PERSISTENCE);
				blockedList.add("Softcore");
				blockedList.add("Hardcore");
			}
			case "Hardcore" -> {
				attemptsList.remove(SOFTCORE);
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
				timeList.remove(WALK);
				blockedList.add("Jog for it");
				blockedList.add("Run for it");
				blockedList.add("Sprint for it");
			}
			case "Run for it" -> {
				timeList.remove(JOG);
				blockedList.add("Run for it");
				blockedList.add("Sprint for it");
			}
			case "Sprint for it" -> {
				timeList.remove(RUN);
				blockedList.add("Sprint for it");
			}
			// Help
			case "Need Some Help?" -> {
				blockedList.add("Need Some Help?");
				blockedList.add("Need Less Help?");
				blockedList.add("No Help Needed");
			}
			case "Need Less Help?" -> {
				helpList.remove(SOME);
				blockedList.add("Need Less Help?");
				blockedList.add("No Help Needed");
			}
			case "No Help Needed" -> {
				helpList.remove(LESS);
				blockedList.add("No Help Needed");
			}
			// Path level
			case "Pathseeker" -> {
				blockedList.add("Pathseeker");
				blockedList.add("Pathfinder");
				blockedList.add("Pathmaster");
			}
			case "Pathfinder" -> {
				pathList.remove(SEEKER);
				blockedList.add("Pathfinder");
				blockedList.add("Pathmaster");
			}
			case "Pathmaster" -> {
				pathList.remove(FINDER);
				blockedList.add("Pathmaster");
			}
			// Overclocked
			case "Overclocked" -> {
				blockedList.add("Overclocked");
				blockedList.add("Overclocked 2");
				blockedList.add("Insanity");
			}
			case "Overclocked 2" -> {
				overclockList.remove(OVERCLOCKED_ONE);
				blockedList.add("Overclocked 2");
				blockedList.add("Insanity");
			}
			case "Insanity" -> {
				overclockList.remove(OVERCLOCKED_TWO);
				blockedList.add("Insanity");
			}
			case "Not Just a Head" -> {
				blockedList.add("Not Just a Head");
				blockedList.add("Arterial Spray");
				blockedList.add("Blood Thinners");
			}
			case "Arterial Spray" -> {
				zebakList.remove(NOT_JUST_A_HEAD);
				blockedList.add("Arterial Spray");
				blockedList.add("Blood Thinners");
			}
			case "Blood Thinners" -> {
				zebakList.remove(ARTERIAL_SPRAY);
				blockedList.add("Blood Thinners");
			}
		}
	}
	
	private Invocations addInvocation(Invocations invocation) {
		System.out.println("Randomized " + invocation.ordinal() + " with invocation: " + invocation);
		
		if (!invocationsList.contains(invocation)) {
			
			switch (invocation) {
				case ATTEMPTS -> {
					switch (attemptsList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							attemptsList.add(TRY_AGAIN);
							setLastAttempts(TRY_AGAIN);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							attemptsList.add(PERSISTENCE);
							setLastAttempts(PERSISTENCE);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							attemptsList.add(SOFTCORE);
							setLastAttempts(SOFTCORE);
							return invocation;
						}
						case 3 -> {
							addInvocationPanel(invocation);
							invocationsList.add(ATTEMPTS);
							attemptsList.add(HARDCORE);
							setLastAttempts(HARDCORE);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case TIME -> {
					switch (timeList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							timeList.add(WALK);
							setLastTime(WALK);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							timeList.add(JOG);
							setLastTime(JOG);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							timeList.add(RUN);
							setLastTime(RUN);
							return invocation;
						}
						case 3 -> {
							addInvocationPanel(invocation);
							invocationsList.add(TIME);
							timeList.add(SPRINT);
							setLastTime(SPRINT);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case HELP -> {
					switch (helpList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							helpList.add(SOME);
							setLastHelp(SOME);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							helpList.add(LESS);
							setLastHelp(LESS);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							invocationsList.add(HELP);
							helpList.add(NONE);
							setLastHelp(NONE);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case PATH_LEVEL -> {
					switch (pathList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							pathList.add(SEEKER);
							setLastPath(SEEKER);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							pathList.add(FINDER);
							setLastPath(FINDER);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							invocationsList.add(PATH_LEVEL);
							pathList.add(MASTER);
							setLastPath(MASTER);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case OVERCLOCKED -> {
					switch (overclockList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							overclockList.add(OVERCLOCKED_ONE);
							setLastOverclocked(OVERCLOCKED_ONE);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							overclockList.add(OVERCLOCKED_TWO);
							setLastOverclocked(OVERCLOCKED_TWO);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							invocationsList.add(OVERCLOCKED);
							overclockList.add(INSANITY);
							setLastOverclocked(INSANITY);
							return invocation;
						}
						default -> randomizeInvocation();
					}
				}
				case ZEBAK -> {
					switch (zebakList.size()) {
						case 0 -> {
							addInvocationPanel(invocation);
							zebakList.add(NOT_JUST_A_HEAD);
							setLastZebak(NOT_JUST_A_HEAD);
							return invocation;
						}
						case 1 -> {
							addInvocationPanel(invocation);
							zebakList.add(ARTERIAL_SPRAY);
							setLastZebak(ARTERIAL_SPRAY);
							return invocation;
						}
						case 2 -> {
							addInvocationPanel(invocation);
							zebakList.add(BLOOD_THINNERS);
							setLastZebak(BLOOD_THINNERS);
							return invocation;
						}
					}
				}
			}
			
			addInvocationPanel(invocation);
			invocationsList.add(invocation);
			setLastRerolled(invocation);
		} else {
			randomizeInvocation();
		}
		
		updateUI();
		return invocation;
	}
	
	
	
	private void addInvocationPanel(Invocations invocation) {
		InvocationPanel panel = createInvocationPanel(invocation);
		System.out.println(panel.text);
		if (blockedList.contains(panel.text)) {
			System.out.println("Blocked panel: " + panel.text);
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

			jpNewInvocation.add(panel, BorderLayout.NORTH);
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
		
		setLastNull();
		
		rerolls = 0;
		blocks = 0;
		raidLevel = 0;
		
		System.out.println("Reset invocations!");
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
					case 1 -> raidLevel += TRY_AGAIN.getRaidLevel();
					case 2 -> raidLevel += PERSISTENCE.getRaidLevel();
					case 3 -> raidLevel += SOFTCORE.getRaidLevel();
					case 4 -> raidLevel += HARDCORE.getRaidLevel();
				}
			}
			case TIME -> {
				switch (timeList.size()) {
					case 1 -> raidLevel += WALK.getRaidLevel();
					case 2 -> raidLevel += JOG.getRaidLevel();
					case 3 -> raidLevel += RUN.getRaidLevel();
					case 4 -> raidLevel += SPRINT.getRaidLevel();
				}
			}
			case HELP -> {
				switch (helpList.size()) {
					case 1 -> raidLevel += SOME.getRaidLevel();
					case 2 -> raidLevel += LESS.getRaidLevel();
					case 3 -> raidLevel += NONE.getRaidLevel();
				}
			}
			case PATH_LEVEL -> {
				switch (pathList.size()) {
					case 1 -> raidLevel += SEEKER.getRaidLevel();
					case 2 -> raidLevel += FINDER.getRaidLevel();
					case 3 -> raidLevel += MASTER.getRaidLevel();
				}
			}
			case OVERCLOCKED -> {
				switch (overclockList.size()) {
					case 1 -> raidLevel += OVERCLOCKED_ONE.getRaidLevel();
					case 2 -> raidLevel += OVERCLOCKED_TWO.getRaidLevel();
					case 3 -> raidLevel += INSANITY.getRaidLevel();
				}
			}
			case ZEBAK -> {
				switch (zebakList.size()) {
					case 1 -> raidLevel += NOT_JUST_A_HEAD.getRaidLevel();
					case 2 -> raidLevel += ARTERIAL_SPRAY.getRaidLevel();
					case 3 -> raidLevel += BLOOD_THINNERS.getRaidLevel();
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
			case "Try Again" -> TRY_AGAIN.getRaidLevel();
			case "Persistence" -> PERSISTENCE.getRaidLevel();
			case "Softcore" -> SOFTCORE.getRaidLevel();
			case "Hardcore" -> HARDCORE.getRaidLevel();
			// Time
			case "Walk for it" -> WALK.getRaidLevel();
			case "Jog for it" -> JOG.getRaidLevel();
			case "Run for it" -> RUN.getRaidLevel();
			case "Sprint for it" -> SPRINT.getRaidLevel();
			// Help
			case "Need Some Help?" -> SOME.getRaidLevel();
			case "Need Less Help?" -> LESS.getRaidLevel();
			case "No Help Needed" -> NONE.getRaidLevel();
			// Restoration
			case "Quiet Prayers" -> QUIET_PRAYER.getRaidLevel();
			case "Deadly Prayers" -> DEADLY_PRAYER.getRaidLevel();
			case "On a Diet" -> ON_A_DIET.getRaidLevel();
			case "Dehydration" -> DEHYDRATION.getRaidLevel();
			case "Overly Draining" -> OVERLY_DRAINING.getRaidLevel();
			// Path level
			case "Pathseeker" -> SEEKER.getRaidLevel();
			case "Pathfinder" -> FINDER.getRaidLevel();
			case "Pathmaster" -> MASTER.getRaidLevel();
			case "Walk the Path" -> WALK_THE_PATH.getRaidLevel();
			// Overclocked
			case "Ancient Haste" -> ANCIENT_HASTE.getRaidLevel();
			case "Acceleration" -> ACCELERATION.getRaidLevel();
			case "Penetration" -> PENETRATION.getRaidLevel();
			case "Overclocked" -> OVERCLOCKED_ONE.getRaidLevel();
			case "Overclocked 2" -> OVERCLOCKED_TWO.getRaidLevel();
			case "Insanity" -> INSANITY.getRaidLevel();
			// Zebak
			case "Not Just a Head" -> NOT_JUST_A_HEAD.getRaidLevel();
			case "Arterial Spray" -> ARTERIAL_SPRAY.getRaidLevel();
			case "Blood Thinners" -> BLOOD_THINNERS.getRaidLevel();
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
		int raidLevel = 0;
		while (raidLevel < goal) {
			Invocations invoc = randomizeInvocation( );
			
			raidLevel = addRaidLevel(invoc);
			
			System.out.println("Total: " + raidLevel + " adding:  " + invoc.getRaidLevel( ));
			if (raidLevel > goal) {
				reset( );
				raidLevel = 0;
			}
		}
	}
}