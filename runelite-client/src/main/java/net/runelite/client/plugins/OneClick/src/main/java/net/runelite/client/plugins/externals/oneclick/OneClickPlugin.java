/*
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, TomC <https://github.com/tomcylke>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package net.runelite.client.plugins.OneClick.src.main.java.net.runelite.client.plugins.externals.oneclick;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.DynamicObject;
import net.runelite.api.Entity;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import static net.runelite.api.ObjectID.DWARF_MULTICANNON;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.apache.commons.lang3.tuple.Pair;

@PluginDescriptor(
	name = "One Click",
	description = "OP One Click methods.",
	enabledByDefault = false,
	type = PluginType.EXTERNAL
)
public class OneClickPlugin extends Plugin
{
	private static final Set<Integer> BOLTS = ImmutableSet.of(
		ItemID.BRONZE_BOLTS_UNF, ItemID.IRON_BOLTS_UNF, ItemID.STEEL_BOLTS_UNF,
		ItemID.MITHRIL_BOLTS_UNF, ItemID.ADAMANT_BOLTSUNF, ItemID.RUNITE_BOLTS_UNF,
		ItemID.DRAGON_BOLTS_UNF, ItemID.UNFINISHED_BROAD_BOLTS
	);
	private static final Set<Integer> DART_TIPS = ImmutableSet.of(
		ItemID.BRONZE_DART_TIP, ItemID.IRON_DART_TIP, ItemID.STEEL_DART_TIP,
		ItemID.MITHRIL_DART_TIP, ItemID.ADAMANT_DART_TIP, ItemID.RUNE_DART_TIP,
		ItemID.DRAGON_DART_TIP
	);
	private static final Set<Integer> LOG_ID = ImmutableSet.of(
		ItemID.LOGS, ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.TEAK_LOGS,
		ItemID.MAPLE_LOGS, ItemID.MAHOGANY_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS,
		ItemID.REDWOOD_LOGS
	);
	private static final Set<Integer> HOPS_SEED = ImmutableSet.of(
		ItemID.BARLEY_SEED, ItemID.HAMMERSTONE_SEED, ItemID.ASGARNIAN_SEED,
		ItemID.JUTE_SEED, ItemID.YANILLIAN_SEED, ItemID.KRANDORIAN_SEED, ItemID.WILDBLOOD_SEED
	);
	private static final Set<Integer> HERBS = ImmutableSet.of(
		ItemID.GUAM_LEAF, ItemID.MARRENTILL, ItemID.TARROMIN, ItemID.HARRALANDER
	);
	private static final Set<Integer> BONE_SET = ImmutableSet.of(
		ItemID.BONES, ItemID.WOLF_BONE, ItemID.BURNT_BONES, ItemID.MONKEY_BONES, ItemID.BAT_BONES,
		ItemID.JOGRE_BONE, ItemID.BIG_BONES, ItemID.ZOGRE_BONE, ItemID.SHAIKAHAN_BONES, ItemID.BABYDRAGON_BONES,
		ItemID.WYRM_BONES, ItemID.DRAGON_BONES, ItemID.DRAKE_BONES, ItemID.FAYRG_BONES, ItemID.LAVA_DRAGON_BONES,
		ItemID.RAURG_BONES, ItemID.HYDRA_BONES, ItemID.DAGANNOTH_BONES, ItemID.OURG_BONES, ItemID.SUPERIOR_DRAGON_BONES,
		ItemID.WYVERN_BONES
	);
	private static final Set<String> BIRD_HOUSES_NAMES = ImmutableSet.of(
		"<col=ffff>Bird house (empty)", "<col=ffff>Oak birdhouse (empty)", "<col=ffff>Willow birdhouse (empty)",
		"<col=ffff>Teak birdhouse (empty)", "<col=ffff>Maple birdhouse (empty)", "<col=ffff>Mahogany birdhouse (empty)",
		"<col=ffff>Yew birdhouse (empty)", "<col=ffff>Magic birdhouse (empty)", "<col=ffff>Redwood birdhouse (empty)"
	);
	private static final Set<Integer> DRAGON_LEATHER = ImmutableSet.of(
			ItemID.GREEN_DRAGON_LEATHER, ItemID.BLUE_DRAGON_LEATHER, ItemID.RED_DRAGON_LEATHER, ItemID.BLACK_DRAGON_LEATHER
	);
	private static final Set<Integer> NOTED_BONES = ImmutableSet.of(
			ItemID.NOTED_DRAGON_BONES, ItemID.NOTED_BABYDRAGON_BONES, ItemID.NOTED_BIG_BONES, ItemID.NOTED_JOGRE_BONES,
			ItemID.NOTED_SUPERIOR_DRAGON_BONES, ItemID.NOTED_WYVERN_BONES, ItemID.NOTED_DAGANNOTH_BONES
	);
	private static final Set<Integer> TB_BUCKETS = ImmutableSet.of(
			ItemID.BUCKET, ItemID.BUCKET_OF_WATER
	);
	private static final Set<Integer> SH_MEDPACK = ImmutableSet.of(
			ItemID.SHAYZIEN_MEDPACK
	);
	private static final Set<Integer> TH_FARM = ImmutableSet.of(
			ItemID.GOLOVANOVA_SEED, ItemID.BOLOGANO_SEED, ItemID.LOGAVANO_SEED
	);
	private static final String MAGIC_IMBUE_EXPIRED_MESSAGE = "Your Magic Imbue charge has ended.";
	private static final String MAGIC_IMBUE_MESSAGE = "You are charged to combine runes!";

	@Inject
	private Client client;
	@Inject
	private OneClickConfig config;
	@Inject
	private EventBus eventBus;
	@Inject
	private MenuManager menuManager;

	private final Map<Integer, String> targetMap = new HashMap<>();

	private AlchItem alchItem;
	private SuperHeatItem heatItem;
	private GameObject cannon;
	private Types type = Types.NONE;
	private boolean cannonFiring;
	private boolean enableImbue;
	private boolean imbue;
	private boolean tick;
	private boolean enableFertiliser;
	private boolean enableHumidity;
	private boolean fertiliser;
	private boolean humidity;
	private int prevCannonAnimation = 514;

	@Provides
	OneClickConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OneClickConfig.class);
	}

	@Override
	protected void startUp()
	{
		addSubscriptions();
		type = config.getType();
		enableImbue = config.isUsingImbue();
		enableFertiliser = config.isUsingFertiliser();
		enableHumidity = config.isUsinghHumidity();
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(this);
	}

	private void addSubscriptions()
	{
		eventBus.subscribe(ChatMessage.class, this, this::onChatMessage);
		eventBus.subscribe(ConfigChanged.class, this, this::onConfigChanged);
		eventBus.subscribe(GameObjectSpawned.class, this, this::onGameObjectSpawned);
		eventBus.subscribe(GameStateChanged.class, this, this::onGameStateChanged);
		eventBus.subscribe(GameTick.class, this, this::onGameTick);
		eventBus.subscribe(MenuEntryAdded.class, this, this::onMenuEntryAdded);
		eventBus.subscribe(MenuOpened.class, this, this::onMenuOpened);
		eventBus.subscribe(MenuOptionClicked.class, this, this::onMenuOptionClicked);
	}

	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN && imbue)
		{
			imbue = false;
		}
	}

	private void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("oneclick"))
		{
			type = config.getType();
			enableImbue = config.isUsingImbue();
			enableFertiliser = config.isUsingFertiliser();
			enableHumidity = config.isUsinghHumidity();
		}
	}

	private void onChatMessage(ChatMessage event)
	{
		switch (event.getMessage())
		{
			case "You pick up the cannon. It's really heavy.":
				cannonFiring = false;
				cannon = null;
				break;
			case MAGIC_IMBUE_MESSAGE:
				imbue = true;
				break;
			case MAGIC_IMBUE_EXPIRED_MESSAGE:
				imbue = false;
				break;
		}
	}

	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		final GameObject gameObject = event.getGameObject();
		final Player localPlayer = client.getLocalPlayer();
		if (gameObject.getId() == DWARF_MULTICANNON && cannon == null && localPlayer != null &&
			localPlayer.getWorldLocation().distanceTo(gameObject.getWorldLocation()) <= 2 &&
			localPlayer.getAnimation() == AnimationID.BURYING_BONES)
		{
			cannon = gameObject;
		}
	}

	private void onGameTick(GameTick event)
	{
		if (cannon != null)
		{
			final Entity entity = cannon.getEntity();
			if (entity instanceof DynamicObject)
			{
				final int anim = ((DynamicObject) entity).getAnimationID();
				if (anim == 514 && prevCannonAnimation == 514)
				{
					cannonFiring = false;
				}
				else if (anim != prevCannonAnimation)
				{
					cannonFiring = true;
				}
				prevCannonAnimation = ((DynamicObject) entity).getAnimationID();
			}
		}
		tick = false;
	}

	private void onMenuOpened(MenuOpened event)
	{
		final MenuEntry firstEntry = event.getFirstEntry();

		if (firstEntry == null)
		{
			return;
		}
		final MenuEntry secondEntry = event.getFirstEntry();

		final int widgetId = firstEntry.getParam1();
		final int swidgetId = secondEntry.getParam1();

		if (widgetId == WidgetInfo.INVENTORY.getId() && type == Types.HIGH_ALCH)
		{
			final Widget spell = client.getWidget(WidgetInfo.SPELL_HIGH_LEVEL_ALCHEMY);

			if (spell == null)
			{
				return;
			}

			if (spell.getSpriteId() != SpriteID.SPELL_HIGH_LEVEL_ALCHEMY ||
				spell.getSpriteId() == SpriteID.SPELL_HIGH_LEVEL_ALCHEMY_DISABLED ||
				client.getBoostedSkillLevel(Skill.MAGIC) < 55 ||
				client.getVar(Varbits.SPELLBOOK) != 0)
			{
				alchItem = null;
				return;
			}

			final int itemId = firstEntry.getIdentifier();

			if (itemId == -1)
			{
				return;
			}

			final MenuEntry[] menuList = new MenuEntry[event.getMenuEntries().length + 1];

			for (int i = event.getMenuEntries().length - 1; i >= 0; i--)
			{
				if (i == 0)
				{
					menuList[i] = event.getMenuEntries()[i];
				}
				else
				{
					menuList[i + 1] = event.getMenuEntries()[i];
				}
			}

			final MenuEntry setHighAlchItem = new MenuEntry();
			final boolean set = alchItem != null && alchItem.getId() == firstEntry.getIdentifier();
			setHighAlchItem.setOption(set ? "Unset" : "Set");
			setHighAlchItem.setTarget("<col=00ff00>High Alchemy Item <col=ffffff> -> " + firstEntry.getTarget());
			setHighAlchItem.setIdentifier(set ? -1 : firstEntry.getIdentifier());
			setHighAlchItem.setOpcode(MenuOpcode.RUNELITE.getId());
			setHighAlchItem.setParam1(widgetId);
			setHighAlchItem.setForceLeftClick(false);
			menuList[1] = setHighAlchItem;
			event.setMenuEntries(menuList);
			event.setModified();
		}

		if (swidgetId == WidgetInfo.INVENTORY.getId() && type == Types.SUPER_HEAT )
		{
			final Widget spell = client.getWidget(WidgetInfo.SPELL_SUPERHEAT_ITEM);

			if (spell == null)
			{
				return;
			}

			if (spell.getSpriteId() != SpriteID.SPELL_SUPERHEAT_ITEM ||
					spell.getSpriteId() == SpriteID.SPELL_SUPERHEAT_ITEM_DISABLED ||
					client.getBoostedSkillLevel(Skill.MAGIC) < 43 ||
					client.getVar(Varbits.SPELLBOOK) != 0)
			{
				heatItem = null;
				return;
			}

			final int oreId = secondEntry.getIdentifier();

			if (oreId == -1)
			{
				return;
			}

			final MenuEntry[] menuList = new MenuEntry[event.getMenuEntries().length + 1];

			for (int i = event.getMenuEntries().length - 1; i >= 0; i--)
			{
				if (i == 0)
				{
					menuList[i] = event.getMenuEntries()[i];
				}
				else
				{
					menuList[i + 1] = event.getMenuEntries()[i];
				}
			}

			final MenuEntry setHeatItem = new MenuEntry();
			final boolean set = heatItem != null && heatItem.getId() == secondEntry.getIdentifier();
			setHeatItem.setOption(set ? "Unset" : "Set");
			setHeatItem.setTarget("<col=00ff00>Superheat Item <col=ffffff> -> " + secondEntry.getTarget());
			setHeatItem.setIdentifier(set ? -1 : secondEntry.getIdentifier());
			setHeatItem.setOpcode(MenuOpcode.RUNELITE.getId());
			setHeatItem.setParam1(swidgetId);
			setHeatItem.setForceLeftClick(false);
			menuList[1] = setHeatItem;
			event.setMenuEntries(menuList);
			event.setModified();
		}
	}


	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		final int id = event.getIdentifier();
		final int opcode = event.getOpcode();
		targetMap.put(id, event.getTarget());

		switch (type)
		{
			case BRUMA_ROOT:
			{
				if (opcode == MenuOpcode.ITEM_USE.getId() && id == ItemID.BRUMA_ROOT)
				{
					if (findItem(ItemID.BRUMA_ROOT).getLeft() == -1)
					{
						return;
					}
					event.setTarget("<col=ff9040>Knife<col=ffffff> -> " + targetMap.get(id));
					event.setForceLeftClick(true);
					event.setModified();
				}
			}
			break;
			case DARTS:
				if (opcode == MenuOpcode.ITEM_USE.getId() && (DART_TIPS.contains(id) || BOLTS.contains(id)))
				{
					if (findItem(ItemID.FEATHER).getLeft() == -1)
					{
						return;
					}
					event.setTarget("<col=ff9040>Feather<col=ffffff> -> " + targetMap.get(id));
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case FIREMAKING:
				if (opcode == MenuOpcode.ITEM_USE.getId() && LOG_ID.contains(id))
				{
					if (findItem(ItemID.TINDERBOX).getLeft() == -1)
					{
						return;
					}
					event.setTarget("<col=ff9040>Tinderbox<col=ffffff> -> " + targetMap.get(id));
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case BIRDHOUSES:
				if (opcode == MenuOpcode.GAME_OBJECT_SECOND_OPTION.getId() && BIRD_HOUSES_NAMES.contains(event.getTarget()))
				{
					if (findItem(HOPS_SEED).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Hops seed<col=ffffff> -> " + targetMap.get(id));
					event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case HERB_TAR:
				if (opcode == MenuOpcode.ITEM_USE.getId() && HERBS.contains(id))
				{
					if (findItem(ItemID.SWAMP_TAR).getLeft() == -1 || findItem(ItemID.PESTLE_AND_MORTAR).getLeft() == -1)
					{
						return;
					}
					event.setTarget("<col=ff9040>Swamp tar<col=ffffff> -> " + targetMap.get(id));
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case LAVA_RUNES:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() && event.getOption().equals("Craft-rune") && event.getTarget().equals("<col=ffff>Altar"))
				{
					if (findItem(ItemID.EARTH_RUNE).getLeft() == -1)
					{
						return;
					}

					if (!imbue && enableImbue)
					{
						event.setOption("Use");
						event.setTarget("<col=ff9040>Magic Imbue<col=ffffff> -> <col=ffff>Yourself");
						event.setForceLeftClick(true);
						event.setModified();
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Earth rune<col=ffffff> -> <col=ffff>Altar");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case STEAM_RUNES:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						event.getOption().equals("Craft-rune") &&
						event.getTarget().equals("<col=ffff>Altar"))
				{
					if (findItem(ItemID.WATER_RUNE).getLeft() == -1)
					{
						return;
					}

					if (!imbue && enableImbue)
					{
						event.setOption("Use");
						event.setTarget("<col=ff9040>Magic Imbue<col=ffffff> -> <col=ffff>Yourself");
						event.setForceLeftClick(true);
						event.setModified();
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Water rune<col=ffffff> -> <col=ffff>Altar");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case SMOKE_RUNES:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						event.getOption().equals("Craft-rune") &&
						event.getTarget().equals("<col=ffff>Altar"))
				{
					if (findItem(ItemID.AIR_RUNE).getLeft() == -1)
					{
						return;
					}

					if (!imbue && enableImbue)
					{
						event.setOption("Use");
						event.setTarget("<col=ff9040>Magic Imbue<col=ffffff> -> <col=ffff>Yourself");
						event.setForceLeftClick(true);
						event.setModified();
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Air rune<col=ffffff> -> <col=ffff>Altar");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case HIGH_ALCH:
				if (opcode == MenuOpcode.WIDGET_TYPE_2.getId() && alchItem != null && event.getOption().equals("Cast") && event.getTarget().equals("<col=00ff00>High Level Alchemy</col>"))
				{
					event.setOption("Cast");
					event.setTarget("<col=00ff00>High Level Alchemy</col><col=ffffff> -> " + alchItem.getName());
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case DWARF_CANNON:
				if (cannonFiring && event.getIdentifier() == DWARF_MULTICANNON && opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId())
				{
					if (findItem(ItemID.CANNONBALL).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Cannonball<col=ffffff> -> <col=ffff>Dwarf multicannon");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case BONES:
				if (opcode == MenuOpcode.NPC_FIRST_OPTION.getId() && event.getOption().toLowerCase().contains("talk-to") && event.getTarget().toLowerCase().contains("phials"))
				{
					if (findItem(NOTED_BONES).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Dragon Bones<col=ffffff> -> <col=ffff>Phials" );
					event.setForceLeftClick(true);
					event.setModified();
				}
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() && event.getOption().toLowerCase().contains("pray") && event.getTarget().toLowerCase().contains("altar"))
				{
					if (findItem(BONE_SET).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Bones<col=ffffff> -> " + event.getTarget());
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case KARAMBWANS:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() && event.getOption().equals("Cook"))
				{
					if (findItem(ItemID.RAW_KARAMBWAN).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Raw karambwan<col=ffffff> -> " + event.getTarget());
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case DARK_ESSENCE:
				if (opcode == MenuOpcode.ITEM_USE.getId() && id == ItemID.CHISEL)
				{
					if (findItem(ItemID.DARK_ESSENCE_BLOCK).getLeft() == -1)
					{
						return;
					}
					event.setTarget("<col=ff9040>Chisel<col=ffffff> -> <col=ff9040>Dark essence block");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case TIARA:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						event.getOption().equals("Craft-rune") &&
						event.getTarget().equals("<col=ffff>Altar"))
				{
					if (findItem(ItemID.TIARA).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Tiara<col=ffffff> -> <col=ffff>Altar");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case LEATHER:
				if (opcode == MenuOpcode.ITEM_USE.getId() && DRAGON_LEATHER.contains(id))
				{
					if (findItem(ItemID.NEEDLE).getLeft() == -1) {
						return;
					}
					event.setTarget("<col=ff9040>Needle<col=ffffff> -> " + targetMap.get(id));
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case SUPER_HEAT:
				if (opcode == MenuOpcode.WIDGET_TYPE_2.getId() && heatItem != null && event.getOption().equals("Cast") && event.getTarget().equals("<col=00ff00>Superheat Item</col>"))
				{
					event.setOption("Cast");
					event.setTarget("<col=00ff00>Superheat Item</col><col=ffffff> -> " + heatItem.getName());
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case MEDPACK:
				if (opcode == MenuOpcode.NPC_FIRST_OPTION.getId() && event.getOption().toLowerCase().contains("talk-to") && event.getTarget().toLowerCase().contains("wounded soldier"))
				{
					if (findItem(SH_MEDPACK).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Shayzien medpack<col=ffffff> -> <col=ffff>Wounded soldier" );
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case TROUBLEBREWING:
				if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() && event.getTarget().toLowerCase().contains("water pump"))
				{
					if (findItem(TB_BUCKETS).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Bucket<col=ffffff> -> " + event.getTarget());
					menuManager.addPriorityEntry("Use");
					event.setForceLeftClick(true);
					event.setModified();
				}
				if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() && event.getTarget().toLowerCase().contains("hopper"))
				{
					if (findItem(TB_BUCKETS).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Bucket<col=ffffff> -> " + event.getTarget());
					menuManager.addPriorityEntry("Use");
					event.setForceLeftClick(true);
					event.setModified();
				}
				break;
			case TITHE_FARM:
				if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() && event.getTarget().toLowerCase().contains("tithe patch"))
				{
					if (findItem(TH_FARM).getLeft() == -1)
					{
						return;
					}
					event.setOption("Use");
					event.setTarget("<col=ff9040>Seed<col=ffffff> -> " + event.getTarget());
					menuManager.addPriorityEntry("Use");
					event.setForceLeftClick(true);
					event.setModified();
				}
				if (!humidity && enableHumidity)
				{
					if (opcode == MenuOpcode.ITEM_USE.getId() && event.getTarget().toLowerCase().contains("watering can"))
					{
						if (findItem(ItemID.ASTRAL_RUNE).getLeft() == -1)
						{
							return;
						}
						event.setOption("Use");
						event.setTarget("<col=ff9040>Humidify<col=ffffff> -> <col=ffff>Yourself");
						event.setForceLeftClick(true);
						event.setModified();
					}
				}
				if (!fertiliser && enableFertiliser)
				{
					if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() && event.getTarget().toLowerCase().contains("bologano seedling"))
					{
						if (findItem(ItemID.GRICOLLERS_FERTILISER).getLeft() == -1)
						{
							return;
						}
						event.setOption("Use");
						event.setTarget("<col=ff9040>Gricoller's fertiliser<col=ffffff> -> <col=ffff>Plant");
						menuManager.addPriorityEntry("Water");
						event.setForceLeftClick(true);
						event.setModified();
					}
				}
				break;
		}
	}

	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		final String target = event.getTarget();
		final int opcode = event.getOpcode();

		if (tick)
		{
			event.consume();
			return;
		}

		if (event.getTarget() == null)
		{
			return;
		}

		switch (type)
		{
			case BRUMA_ROOT:
				if (opcode == MenuOpcode.ITEM_USE.getId() && target.contains("<col=ff9040>Knife<col=ffffff> -> "))
				{
					if (updateSelectedItem(ItemID.KNIFE))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId());
					}
				}
				break;
			case DARTS:
				if (opcode == MenuOpcode.ITEM_USE.getId() && target.contains("<col=ff9040>Feather<col=ffffff> -> "))
				{
					if (updateSelectedItem(ItemID.FEATHER))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId());
					}
				}
				break;
			case FIREMAKING:
				if (opcode == MenuOpcode.ITEM_USE.getId() && target.contains("<col=ff9040>Tinderbox<col=ffffff> -> "))
				{
					if (updateSelectedItem(ItemID.TINDERBOX))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId());
					}
				}
				break;
			case BIRDHOUSES:
				if (opcode == MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId() && target.contains("<col=ff9040>Hops seed<col=ffffff> -> "))
				{
					updateSelectedItem(HOPS_SEED);
				}
				break;
			case HERB_TAR:
				if (opcode == MenuOpcode.ITEM_USE.getId() && target.contains("<col=ff9040>Swamp tar<col=ffffff> -> "))
				{
					if (updateSelectedItem(ItemID.SWAMP_TAR))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId());
					}
				}
				break;
			case LAVA_RUNES:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
					target.equals("<col=ff9040>Earth rune<col=ffffff> -> <col=ffff>Altar"))
				{
					if (updateSelectedItem(ItemID.EARTH_RUNE))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				else if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
					target.equals("<col=ff9040>Magic Imbue<col=ffffff> -> <col=ffff>Yourself"))
				{
					event.setIdentifier(1);
					event.setOpcode(MenuOpcode.WIDGET_DEFAULT.getId());
					event.setParam0(-1);
					event.setParam1(WidgetInfo.SPELL_MAGIC_IMBUE.getId());
				}
				break;
			case STEAM_RUNES:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						target.equals("<col=ff9040>Water rune<col=ffffff> -> <col=ffff>Altar"))
				{
					if (updateSelectedItem(ItemID.WATER_RUNE))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				else if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						target.equals("<col=ff9040>Magic Imbue<col=ffffff> -> <col=ffff>Yourself"))
				{
					event.setIdentifier(1);
					event.setOpcode(MenuOpcode.WIDGET_DEFAULT.getId());
					event.setParam0(-1);
					event.setParam1(WidgetInfo.SPELL_MAGIC_IMBUE.getId());
				}
				break;
			case SMOKE_RUNES:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						target.equals("<col=ff9040>Air rune<col=ffffff> -> <col=ffff>Altar"))
				{
					if (updateSelectedItem(ItemID.AIR_RUNE))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				else if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						target.equals("<col=ff9040>Magic Imbue<col=ffffff> -> <col=ffff>Yourself"))
				{
					event.setIdentifier(1);
					event.setOpcode(MenuOpcode.WIDGET_DEFAULT.getId());
					event.setParam0(-1);
					event.setParam1(WidgetInfo.SPELL_MAGIC_IMBUE.getId());
				}
				break;
			case HIGH_ALCH:
				if (opcode == MenuOpcode.WIDGET_TYPE_2.getId() && event.getOption().equals("Cast") && target.contains("<col=00ff00>High Level Alchemy</col><col=ffffff> -> "))
				{
					final Pair<Integer, Integer> pair = findItem(alchItem.getId());
					if (pair.getLeft() != -1)
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET.getId());
						event.setIdentifier(pair.getLeft());
						event.setParam0(pair.getRight());
						event.setParam1(WidgetInfo.INVENTORY.getId());
						client.setSelectedSpellName("<col=00ff00>High Level Alchemy</col><col=ffffff>");
						client.setSelectedSpellWidget(WidgetInfo.SPELL_HIGH_LEVEL_ALCHEMY.getId());
					}
				}
				else if (opcode == MenuOpcode.RUNELITE.getId() && event.getIdentifier() == -1)
				{
					alchItem = null;
				}
				else if (type == Types.HIGH_ALCH && opcode == MenuOpcode.RUNELITE.getId())
				{
					final String itemName = event.getTarget().split("<col=00ff00>High Alchemy Item <col=ffffff> -> ")[1];
					alchItem = new AlchItem(itemName, event.getIdentifier());
				}
				break;
			case DWARF_CANNON:
				if (cannonFiring && event.getIdentifier() == DWARF_MULTICANNON && opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId())
				{
					if (updateSelectedItem(ItemID.CANNON_BALL))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				break;
			case BONES:
				if (opcode == MenuOpcode.NPC_FIRST_OPTION.getId() &&
						event.getTarget().contains("<col=ff9040>Dragon Bones<col=ffffff> -> ") && target.toLowerCase().contains("phials"))
				{
					if (updateSelectedItem(NOTED_BONES))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_NPC.getId());
					}
				}
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						event.getTarget().contains("<col=ff9040>Bones<col=ffffff> -> ") && target.toLowerCase().contains("altar"))
				{
					if (updateSelectedItem(BONE_SET))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				break;
			case KARAMBWANS:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() && event.getTarget().contains("<col=ff9040>Raw karambwan<col=ffffff> -> "))
				{
					if (updateSelectedItem(ItemID.RAW_KARAMBWAN))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
						tick = true;
					}
				}
				break;
			case DARK_ESSENCE:
				if (opcode == MenuOpcode.ITEM_USE.getId() && target.contains("<col=ff9040>Chisel<col=ffffff> ->"))
				{
					if (updateSelectedItem(ItemID.DARK_ESSENCE_BLOCK))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId());
					}
				}
				break;
			case TIARA:
				if (opcode == MenuOpcode.GAME_OBJECT_FIRST_OPTION.getId() &&
						target.equals("<col=ff9040>Tiara<col=ffffff> -> <col=ffff>Altar"))
				{
					if (updateSelectedItem(ItemID.TIARA))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				break;
			case LEATHER:
				if (opcode == MenuOpcode.ITEM_USE.getId() && target.contains("<col=ff9040>Needle<col=ffffff> -> "))
				{
					if (updateSelectedItem(ItemID.NEEDLE))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET_ITEM.getId());
					}
				}
				break;
			case SUPER_HEAT:
				if (opcode == MenuOpcode.WIDGET_TYPE_2.getId() && event.getOption().equals("Cast") && target.contains("<col=00ff00>Superheat Item</col><col=ffffff> -> "))
				{
					final Pair<Integer, Integer> pair = findItem(heatItem.getId());
					if (pair.getLeft() != -1)
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_WIDGET.getId());
						event.setIdentifier(pair.getLeft());
						event.setParam0(pair.getRight());
						event.setParam1(WidgetInfo.INVENTORY.getId());
						client.setSelectedSpellName("<col=00ff00>Superheat Item</col><col=ffffff>");
						client.setSelectedSpellWidget(WidgetInfo.SPELL_SUPERHEAT_ITEM.getId());
					}
				}
				else if (opcode == MenuOpcode.RUNELITE.getId() && event.getIdentifier() == -1)
				{
					heatItem = null;
				}
				else if (type == Types.SUPER_HEAT && opcode == MenuOpcode.RUNELITE.getId())
				{
					final String itemName = event.getTarget().split("<col=00ff00>Superheat Item <col=ffffff> -> ")[1];
					heatItem = new SuperHeatItem(itemName, event.getIdentifier());
				}
				break;
			case MEDPACK:
				if (opcode == MenuOpcode.NPC_FIRST_OPTION.getId() &&
						event.getTarget().contains("<col=ff9040>Shayzien medpack<col=ffffff> -> ") && target.toLowerCase().contains("wounded soldier"))
				{
					if (updateSelectedItem(SH_MEDPACK))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_NPC.getId());
					}
				}
				break;
			case TROUBLEBREWING:
				if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() &&
						event.getTarget().contains("<col=ff9040>Bucket<col=ffffff> -> ") && target.toLowerCase().contains("water pump"))
				{
					if (updateSelectedItem(TB_BUCKETS))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() &&
						event.getTarget().contains("<col=ff9040>Bucket<col=ffffff> -> ") && target.toLowerCase().contains("hopper"))
				{
					if (updateSelectedItem(TB_BUCKETS))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				break;
			case TITHE_FARM:
				if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() &&
						event.getTarget().contains("<col=ff9040>Seed<col=ffffff> -> ") && target.toLowerCase().contains("tithe patch"))
				{
					if (updateSelectedItem(TH_FARM))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				else if (opcode == MenuOpcode.ITEM_USE.getId() &&
						target.equals("<col=ff9040>Humidify<col=ffffff> -> <col=ffff>Yourself"))
				{
					event.setIdentifier(1);
					event.setOpcode(MenuOpcode.WIDGET_DEFAULT.getId());
					event.setParam0(-1);
					event.setParam1(WidgetInfo.SPELL_HUMIDIFY.getId());
				}
				else if (opcode == MenuOpcode.EXAMINE_OBJECT.getId() &&
						event.getTarget().contains("<col=ff9040>Gricoller's fertiliser<col=ffffff> -> <col=ffff>Plant"))
				{
					if (updateSelectedItem(ItemID.GRICOLLERS_FERTILISER))
					{
						event.setOpcode(MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId());
					}
				}
				break;
		}
	}

	private boolean updateSelectedItem(int id)
	{
		final Pair<Integer, Integer> pair = findItem(id);
		if (pair.getLeft() != -1)
		{
			client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
			client.setSelectedItemSlot(pair.getRight());
			client.setSelectedItemID(pair.getLeft());
			return true;
		}
		return false;
	}

	private boolean updateSelectedItem(Collection<Integer> ids)
	{
		final Pair<Integer, Integer> pair = findItem(ids);
		if (pair.getLeft() != -1)
		{
			client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
			client.setSelectedItemSlot(pair.getRight());
			client.setSelectedItemID(pair.getLeft());
			return true;
		}
		return false;
	}
	private Pair<Integer, Integer> findItem(int id)
	{
		final Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		final List<WidgetItem> itemList = (List<WidgetItem>) inventoryWidget.getWidgetItems();

		for (int i = itemList.size() - 1; i >= 0; i--)
		{
			final WidgetItem item = itemList.get(i);
			if (item.getId() == id)
			{
				return Pair.of(item.getId(), item.getIndex());
			}
		}

		return Pair.of(-1, -1);
	}

	private Pair<Integer, Integer> findItem(Collection<Integer> ids)
	{
		final Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		final List<WidgetItem> itemList = (List<WidgetItem>) inventoryWidget.getWidgetItems();

		for (int i = itemList.size() - 1; i >= 0; i--)
		{
			final WidgetItem item = itemList.get(i);
			if (ids.contains(item.getId()))
			{
				return Pair.of(item.getId(), item.getIndex());
			}
		}

		return Pair.of(-1, -1);
	}
}
