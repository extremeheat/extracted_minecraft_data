package net.minecraft.stats;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Stats {
   public static final StatType<Block> BLOCK_MINED;
   public static final StatType<Item> ITEM_CRAFTED;
   public static final StatType<Item> ITEM_USED;
   public static final StatType<Item> ITEM_BROKEN;
   public static final StatType<Item> ITEM_PICKED_UP;
   public static final StatType<Item> ITEM_DROPPED;
   public static final StatType<EntityType<?>> ENTITY_KILLED;
   public static final StatType<EntityType<?>> ENTITY_KILLED_BY;
   public static final StatType<ResourceLocation> CUSTOM;
   public static final ResourceLocation LEAVE_GAME;
   public static final ResourceLocation PLAY_ONE_MINUTE;
   public static final ResourceLocation TIME_SINCE_DEATH;
   public static final ResourceLocation TIME_SINCE_REST;
   public static final ResourceLocation SNEAK_TIME;
   public static final ResourceLocation WALK_ONE_CM;
   public static final ResourceLocation CROUCH_ONE_CM;
   public static final ResourceLocation SPRINT_ONE_CM;
   public static final ResourceLocation WALK_ON_WATER_ONE_CM;
   public static final ResourceLocation FALL_ONE_CM;
   public static final ResourceLocation CLIMB_ONE_CM;
   public static final ResourceLocation FLY_ONE_CM;
   public static final ResourceLocation WALK_UNDER_WATER_ONE_CM;
   public static final ResourceLocation MINECART_ONE_CM;
   public static final ResourceLocation BOAT_ONE_CM;
   public static final ResourceLocation PIG_ONE_CM;
   public static final ResourceLocation HORSE_ONE_CM;
   public static final ResourceLocation AVIATE_ONE_CM;
   public static final ResourceLocation SWIM_ONE_CM;
   public static final ResourceLocation JUMP;
   public static final ResourceLocation DROP;
   public static final ResourceLocation DAMAGE_DEALT;
   public static final ResourceLocation DAMAGE_DEALT_ABSORBED;
   public static final ResourceLocation DAMAGE_DEALT_RESISTED;
   public static final ResourceLocation DAMAGE_TAKEN;
   public static final ResourceLocation DAMAGE_BLOCKED_BY_SHIELD;
   public static final ResourceLocation DAMAGE_ABSORBED;
   public static final ResourceLocation DAMAGE_RESISTED;
   public static final ResourceLocation DEATHS;
   public static final ResourceLocation MOB_KILLS;
   public static final ResourceLocation ANIMALS_BRED;
   public static final ResourceLocation PLAYER_KILLS;
   public static final ResourceLocation FISH_CAUGHT;
   public static final ResourceLocation TALKED_TO_VILLAGER;
   public static final ResourceLocation TRADED_WITH_VILLAGER;
   public static final ResourceLocation EAT_CAKE_SLICE;
   public static final ResourceLocation FILL_CAULDRON;
   public static final ResourceLocation USE_CAULDRON;
   public static final ResourceLocation CLEAN_ARMOR;
   public static final ResourceLocation CLEAN_BANNER;
   public static final ResourceLocation CLEAN_SHULKER_BOX;
   public static final ResourceLocation INTERACT_WITH_BREWINGSTAND;
   public static final ResourceLocation INTERACT_WITH_BEACON;
   public static final ResourceLocation INSPECT_DROPPER;
   public static final ResourceLocation INSPECT_HOPPER;
   public static final ResourceLocation INSPECT_DISPENSER;
   public static final ResourceLocation PLAY_NOTEBLOCK;
   public static final ResourceLocation TUNE_NOTEBLOCK;
   public static final ResourceLocation POT_FLOWER;
   public static final ResourceLocation TRIGGER_TRAPPED_CHEST;
   public static final ResourceLocation OPEN_ENDERCHEST;
   public static final ResourceLocation ENCHANT_ITEM;
   public static final ResourceLocation PLAY_RECORD;
   public static final ResourceLocation INTERACT_WITH_FURNACE;
   public static final ResourceLocation INTERACT_WITH_CRAFTING_TABLE;
   public static final ResourceLocation OPEN_CHEST;
   public static final ResourceLocation SLEEP_IN_BED;
   public static final ResourceLocation OPEN_SHULKER_BOX;
   public static final ResourceLocation OPEN_BARREL;
   public static final ResourceLocation INTERACT_WITH_BLAST_FURNACE;
   public static final ResourceLocation INTERACT_WITH_SMOKER;
   public static final ResourceLocation INTERACT_WITH_LECTERN;
   public static final ResourceLocation INTERACT_WITH_CAMPFIRE;
   public static final ResourceLocation INTERACT_WITH_CARTOGRAPHY_TABLE;
   public static final ResourceLocation INTERACT_WITH_LOOM;
   public static final ResourceLocation INTERACT_WITH_STONECUTTER;
   public static final ResourceLocation BELL_RING;
   public static final ResourceLocation RAID_TRIGGER;
   public static final ResourceLocation RAID_WIN;

   private static ResourceLocation makeCustomStat(String var0, StatFormatter var1) {
      ResourceLocation var2 = new ResourceLocation(var0);
      Registry.register(Registry.CUSTOM_STAT, (String)var0, var2);
      CUSTOM.get(var2, var1);
      return var2;
   }

   private static <T> StatType<T> makeRegistryStatType(String var0, Registry<T> var1) {
      return (StatType)Registry.register(Registry.STAT_TYPE, (String)var0, new StatType(var1));
   }

   static {
      BLOCK_MINED = makeRegistryStatType("mined", Registry.BLOCK);
      ITEM_CRAFTED = makeRegistryStatType("crafted", Registry.ITEM);
      ITEM_USED = makeRegistryStatType("used", Registry.ITEM);
      ITEM_BROKEN = makeRegistryStatType("broken", Registry.ITEM);
      ITEM_PICKED_UP = makeRegistryStatType("picked_up", Registry.ITEM);
      ITEM_DROPPED = makeRegistryStatType("dropped", Registry.ITEM);
      ENTITY_KILLED = makeRegistryStatType("killed", Registry.ENTITY_TYPE);
      ENTITY_KILLED_BY = makeRegistryStatType("killed_by", Registry.ENTITY_TYPE);
      CUSTOM = makeRegistryStatType("custom", Registry.CUSTOM_STAT);
      LEAVE_GAME = makeCustomStat("leave_game", StatFormatter.DEFAULT);
      PLAY_ONE_MINUTE = makeCustomStat("play_one_minute", StatFormatter.TIME);
      TIME_SINCE_DEATH = makeCustomStat("time_since_death", StatFormatter.TIME);
      TIME_SINCE_REST = makeCustomStat("time_since_rest", StatFormatter.TIME);
      SNEAK_TIME = makeCustomStat("sneak_time", StatFormatter.TIME);
      WALK_ONE_CM = makeCustomStat("walk_one_cm", StatFormatter.DISTANCE);
      CROUCH_ONE_CM = makeCustomStat("crouch_one_cm", StatFormatter.DISTANCE);
      SPRINT_ONE_CM = makeCustomStat("sprint_one_cm", StatFormatter.DISTANCE);
      WALK_ON_WATER_ONE_CM = makeCustomStat("walk_on_water_one_cm", StatFormatter.DISTANCE);
      FALL_ONE_CM = makeCustomStat("fall_one_cm", StatFormatter.DISTANCE);
      CLIMB_ONE_CM = makeCustomStat("climb_one_cm", StatFormatter.DISTANCE);
      FLY_ONE_CM = makeCustomStat("fly_one_cm", StatFormatter.DISTANCE);
      WALK_UNDER_WATER_ONE_CM = makeCustomStat("walk_under_water_one_cm", StatFormatter.DISTANCE);
      MINECART_ONE_CM = makeCustomStat("minecart_one_cm", StatFormatter.DISTANCE);
      BOAT_ONE_CM = makeCustomStat("boat_one_cm", StatFormatter.DISTANCE);
      PIG_ONE_CM = makeCustomStat("pig_one_cm", StatFormatter.DISTANCE);
      HORSE_ONE_CM = makeCustomStat("horse_one_cm", StatFormatter.DISTANCE);
      AVIATE_ONE_CM = makeCustomStat("aviate_one_cm", StatFormatter.DISTANCE);
      SWIM_ONE_CM = makeCustomStat("swim_one_cm", StatFormatter.DISTANCE);
      JUMP = makeCustomStat("jump", StatFormatter.DEFAULT);
      DROP = makeCustomStat("drop", StatFormatter.DEFAULT);
      DAMAGE_DEALT = makeCustomStat("damage_dealt", StatFormatter.DIVIDE_BY_TEN);
      DAMAGE_DEALT_ABSORBED = makeCustomStat("damage_dealt_absorbed", StatFormatter.DIVIDE_BY_TEN);
      DAMAGE_DEALT_RESISTED = makeCustomStat("damage_dealt_resisted", StatFormatter.DIVIDE_BY_TEN);
      DAMAGE_TAKEN = makeCustomStat("damage_taken", StatFormatter.DIVIDE_BY_TEN);
      DAMAGE_BLOCKED_BY_SHIELD = makeCustomStat("damage_blocked_by_shield", StatFormatter.DIVIDE_BY_TEN);
      DAMAGE_ABSORBED = makeCustomStat("damage_absorbed", StatFormatter.DIVIDE_BY_TEN);
      DAMAGE_RESISTED = makeCustomStat("damage_resisted", StatFormatter.DIVIDE_BY_TEN);
      DEATHS = makeCustomStat("deaths", StatFormatter.DEFAULT);
      MOB_KILLS = makeCustomStat("mob_kills", StatFormatter.DEFAULT);
      ANIMALS_BRED = makeCustomStat("animals_bred", StatFormatter.DEFAULT);
      PLAYER_KILLS = makeCustomStat("player_kills", StatFormatter.DEFAULT);
      FISH_CAUGHT = makeCustomStat("fish_caught", StatFormatter.DEFAULT);
      TALKED_TO_VILLAGER = makeCustomStat("talked_to_villager", StatFormatter.DEFAULT);
      TRADED_WITH_VILLAGER = makeCustomStat("traded_with_villager", StatFormatter.DEFAULT);
      EAT_CAKE_SLICE = makeCustomStat("eat_cake_slice", StatFormatter.DEFAULT);
      FILL_CAULDRON = makeCustomStat("fill_cauldron", StatFormatter.DEFAULT);
      USE_CAULDRON = makeCustomStat("use_cauldron", StatFormatter.DEFAULT);
      CLEAN_ARMOR = makeCustomStat("clean_armor", StatFormatter.DEFAULT);
      CLEAN_BANNER = makeCustomStat("clean_banner", StatFormatter.DEFAULT);
      CLEAN_SHULKER_BOX = makeCustomStat("clean_shulker_box", StatFormatter.DEFAULT);
      INTERACT_WITH_BREWINGSTAND = makeCustomStat("interact_with_brewingstand", StatFormatter.DEFAULT);
      INTERACT_WITH_BEACON = makeCustomStat("interact_with_beacon", StatFormatter.DEFAULT);
      INSPECT_DROPPER = makeCustomStat("inspect_dropper", StatFormatter.DEFAULT);
      INSPECT_HOPPER = makeCustomStat("inspect_hopper", StatFormatter.DEFAULT);
      INSPECT_DISPENSER = makeCustomStat("inspect_dispenser", StatFormatter.DEFAULT);
      PLAY_NOTEBLOCK = makeCustomStat("play_noteblock", StatFormatter.DEFAULT);
      TUNE_NOTEBLOCK = makeCustomStat("tune_noteblock", StatFormatter.DEFAULT);
      POT_FLOWER = makeCustomStat("pot_flower", StatFormatter.DEFAULT);
      TRIGGER_TRAPPED_CHEST = makeCustomStat("trigger_trapped_chest", StatFormatter.DEFAULT);
      OPEN_ENDERCHEST = makeCustomStat("open_enderchest", StatFormatter.DEFAULT);
      ENCHANT_ITEM = makeCustomStat("enchant_item", StatFormatter.DEFAULT);
      PLAY_RECORD = makeCustomStat("play_record", StatFormatter.DEFAULT);
      INTERACT_WITH_FURNACE = makeCustomStat("interact_with_furnace", StatFormatter.DEFAULT);
      INTERACT_WITH_CRAFTING_TABLE = makeCustomStat("interact_with_crafting_table", StatFormatter.DEFAULT);
      OPEN_CHEST = makeCustomStat("open_chest", StatFormatter.DEFAULT);
      SLEEP_IN_BED = makeCustomStat("sleep_in_bed", StatFormatter.DEFAULT);
      OPEN_SHULKER_BOX = makeCustomStat("open_shulker_box", StatFormatter.DEFAULT);
      OPEN_BARREL = makeCustomStat("open_barrel", StatFormatter.DEFAULT);
      INTERACT_WITH_BLAST_FURNACE = makeCustomStat("interact_with_blast_furnace", StatFormatter.DEFAULT);
      INTERACT_WITH_SMOKER = makeCustomStat("interact_with_smoker", StatFormatter.DEFAULT);
      INTERACT_WITH_LECTERN = makeCustomStat("interact_with_lectern", StatFormatter.DEFAULT);
      INTERACT_WITH_CAMPFIRE = makeCustomStat("interact_with_campfire", StatFormatter.DEFAULT);
      INTERACT_WITH_CARTOGRAPHY_TABLE = makeCustomStat("interact_with_cartography_table", StatFormatter.DEFAULT);
      INTERACT_WITH_LOOM = makeCustomStat("interact_with_loom", StatFormatter.DEFAULT);
      INTERACT_WITH_STONECUTTER = makeCustomStat("interact_with_stonecutter", StatFormatter.DEFAULT);
      BELL_RING = makeCustomStat("bell_ring", StatFormatter.DEFAULT);
      RAID_TRIGGER = makeCustomStat("raid_trigger", StatFormatter.DEFAULT);
      RAID_WIN = makeCustomStat("raid_win", StatFormatter.DEFAULT);
   }
}
