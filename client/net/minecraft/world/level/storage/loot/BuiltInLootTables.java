package net.minecraft.world.level.storage.loot;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class BuiltInLootTables {
   private static final Set<ResourceKey<LootTable>> LOCATIONS = new HashSet();
   private static final Set<ResourceKey<LootTable>> IMMUTABLE_LOCATIONS;
   public static final ResourceKey<LootTable> SPAWN_BONUS_CHEST;
   public static final ResourceKey<LootTable> END_CITY_TREASURE;
   public static final ResourceKey<LootTable> SIMPLE_DUNGEON;
   public static final ResourceKey<LootTable> VILLAGE_WEAPONSMITH;
   public static final ResourceKey<LootTable> VILLAGE_TOOLSMITH;
   public static final ResourceKey<LootTable> VILLAGE_ARMORER;
   public static final ResourceKey<LootTable> VILLAGE_CARTOGRAPHER;
   public static final ResourceKey<LootTable> VILLAGE_MASON;
   public static final ResourceKey<LootTable> VILLAGE_SHEPHERD;
   public static final ResourceKey<LootTable> VILLAGE_BUTCHER;
   public static final ResourceKey<LootTable> VILLAGE_FLETCHER;
   public static final ResourceKey<LootTable> VILLAGE_FISHER;
   public static final ResourceKey<LootTable> VILLAGE_TANNERY;
   public static final ResourceKey<LootTable> VILLAGE_TEMPLE;
   public static final ResourceKey<LootTable> VILLAGE_DESERT_HOUSE;
   public static final ResourceKey<LootTable> VILLAGE_PLAINS_HOUSE;
   public static final ResourceKey<LootTable> VILLAGE_TAIGA_HOUSE;
   public static final ResourceKey<LootTable> VILLAGE_SNOWY_HOUSE;
   public static final ResourceKey<LootTable> VILLAGE_SAVANNA_HOUSE;
   public static final ResourceKey<LootTable> ABANDONED_MINESHAFT;
   public static final ResourceKey<LootTable> NETHER_BRIDGE;
   public static final ResourceKey<LootTable> STRONGHOLD_LIBRARY;
   public static final ResourceKey<LootTable> STRONGHOLD_CROSSING;
   public static final ResourceKey<LootTable> STRONGHOLD_CORRIDOR;
   public static final ResourceKey<LootTable> DESERT_PYRAMID;
   public static final ResourceKey<LootTable> JUNGLE_TEMPLE;
   public static final ResourceKey<LootTable> JUNGLE_TEMPLE_DISPENSER;
   public static final ResourceKey<LootTable> IGLOO_CHEST;
   public static final ResourceKey<LootTable> WOODLAND_MANSION;
   public static final ResourceKey<LootTable> UNDERWATER_RUIN_SMALL;
   public static final ResourceKey<LootTable> UNDERWATER_RUIN_BIG;
   public static final ResourceKey<LootTable> BURIED_TREASURE;
   public static final ResourceKey<LootTable> SHIPWRECK_MAP;
   public static final ResourceKey<LootTable> SHIPWRECK_SUPPLY;
   public static final ResourceKey<LootTable> SHIPWRECK_TREASURE;
   public static final ResourceKey<LootTable> PILLAGER_OUTPOST;
   public static final ResourceKey<LootTable> BASTION_TREASURE;
   public static final ResourceKey<LootTable> BASTION_OTHER;
   public static final ResourceKey<LootTable> BASTION_BRIDGE;
   public static final ResourceKey<LootTable> BASTION_HOGLIN_STABLE;
   public static final ResourceKey<LootTable> ANCIENT_CITY;
   public static final ResourceKey<LootTable> ANCIENT_CITY_ICE_BOX;
   public static final ResourceKey<LootTable> RUINED_PORTAL;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_COMMON;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_RARE;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_UNIQUE;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_RARE;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_SUPPLY;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CORRIDOR;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_INTERSECTION;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_INTERSECTION_BARREL;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_ENTRANCE;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_DISPENSER;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CHAMBER_DISPENSER;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_WATER_DISPENSER;
   public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_POT;
   public static final ResourceKey<LootTable> EQUIPMENT_TRIAL_CHAMBER;
   public static final ResourceKey<LootTable> EQUIPMENT_TRIAL_CHAMBER_RANGED;
   public static final ResourceKey<LootTable> EQUIPMENT_TRIAL_CHAMBER_MELEE;
   public static final Map<DyeColor, ResourceKey<LootTable>> SHEEP_BY_DYE;
   public static final ResourceKey<LootTable> FISHING;
   public static final ResourceKey<LootTable> FISHING_JUNK;
   public static final ResourceKey<LootTable> FISHING_TREASURE;
   public static final ResourceKey<LootTable> FISHING_FISH;
   public static final ResourceKey<LootTable> CAT_MORNING_GIFT;
   public static final ResourceKey<LootTable> ARMORER_GIFT;
   public static final ResourceKey<LootTable> BUTCHER_GIFT;
   public static final ResourceKey<LootTable> CARTOGRAPHER_GIFT;
   public static final ResourceKey<LootTable> CLERIC_GIFT;
   public static final ResourceKey<LootTable> FARMER_GIFT;
   public static final ResourceKey<LootTable> FISHERMAN_GIFT;
   public static final ResourceKey<LootTable> FLETCHER_GIFT;
   public static final ResourceKey<LootTable> LEATHERWORKER_GIFT;
   public static final ResourceKey<LootTable> LIBRARIAN_GIFT;
   public static final ResourceKey<LootTable> MASON_GIFT;
   public static final ResourceKey<LootTable> SHEPHERD_GIFT;
   public static final ResourceKey<LootTable> TOOLSMITH_GIFT;
   public static final ResourceKey<LootTable> WEAPONSMITH_GIFT;
   public static final ResourceKey<LootTable> UNEMPLOYED_GIFT;
   public static final ResourceKey<LootTable> BABY_VILLAGER_GIFT;
   public static final ResourceKey<LootTable> SNIFFER_DIGGING;
   public static final ResourceKey<LootTable> PANDA_SNEEZE;
   public static final ResourceKey<LootTable> CHICKEN_LAY;
   public static final ResourceKey<LootTable> ARMADILLO_SHED;
   public static final ResourceKey<LootTable> PIGLIN_BARTERING;
   public static final ResourceKey<LootTable> SPAWNER_TRIAL_CHAMBER_KEY;
   public static final ResourceKey<LootTable> SPAWNER_TRIAL_CHAMBER_CONSUMABLES;
   public static final ResourceKey<LootTable> SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY;
   public static final ResourceKey<LootTable> SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES;
   public static final ResourceKey<LootTable> SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS;
   public static final ResourceKey<LootTable> BOGGED_SHEAR;
   public static final ResourceKey<LootTable> SHEAR_MOOSHROOM;
   public static final ResourceKey<LootTable> SHEAR_RED_MOOSHROOM;
   public static final ResourceKey<LootTable> SHEAR_BROWN_MOOSHROOM;
   public static final ResourceKey<LootTable> SHEAR_SNOW_GOLEM;
   public static final ResourceKey<LootTable> SHEAR_SHEEP;
   public static final Map<DyeColor, ResourceKey<LootTable>> SHEAR_SHEEP_BY_DYE;
   public static final ResourceKey<LootTable> DESERT_WELL_ARCHAEOLOGY;
   public static final ResourceKey<LootTable> DESERT_PYRAMID_ARCHAEOLOGY;
   public static final ResourceKey<LootTable> TRAIL_RUINS_ARCHAEOLOGY_COMMON;
   public static final ResourceKey<LootTable> TRAIL_RUINS_ARCHAEOLOGY_RARE;
   public static final ResourceKey<LootTable> OCEAN_RUIN_WARM_ARCHAEOLOGY;
   public static final ResourceKey<LootTable> OCEAN_RUIN_COLD_ARCHAEOLOGY;

   public BuiltInLootTables() {
      super();
   }

   private static void makeDyeKeyMap(EnumMap<DyeColor, ResourceKey<LootTable>> var0, String var1) {
      DyeColor[] var2 = DyeColor.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DyeColor var5 = var2[var4];
         var0.put(var5, register(var1 + "/" + var5.getName()));
      }

   }

   private static ResourceKey<LootTable> register(String var0) {
      return register(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace(var0)));
   }

   private static ResourceKey<LootTable> register(ResourceKey<LootTable> var0) {
      if (LOCATIONS.add(var0)) {
         return var0;
      } else {
         throw new IllegalArgumentException(String.valueOf(var0.location()) + " is already a registered built-in loot table");
      }
   }

   public static Set<ResourceKey<LootTable>> all() {
      return IMMUTABLE_LOCATIONS;
   }

   static {
      IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
      SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
      END_CITY_TREASURE = register("chests/end_city_treasure");
      SIMPLE_DUNGEON = register("chests/simple_dungeon");
      VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
      VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
      VILLAGE_ARMORER = register("chests/village/village_armorer");
      VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
      VILLAGE_MASON = register("chests/village/village_mason");
      VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
      VILLAGE_BUTCHER = register("chests/village/village_butcher");
      VILLAGE_FLETCHER = register("chests/village/village_fletcher");
      VILLAGE_FISHER = register("chests/village/village_fisher");
      VILLAGE_TANNERY = register("chests/village/village_tannery");
      VILLAGE_TEMPLE = register("chests/village/village_temple");
      VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
      VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
      VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
      VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
      VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
      ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
      NETHER_BRIDGE = register("chests/nether_bridge");
      STRONGHOLD_LIBRARY = register("chests/stronghold_library");
      STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
      STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
      DESERT_PYRAMID = register("chests/desert_pyramid");
      JUNGLE_TEMPLE = register("chests/jungle_temple");
      JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
      IGLOO_CHEST = register("chests/igloo_chest");
      WOODLAND_MANSION = register("chests/woodland_mansion");
      UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
      UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
      BURIED_TREASURE = register("chests/buried_treasure");
      SHIPWRECK_MAP = register("chests/shipwreck_map");
      SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
      SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
      PILLAGER_OUTPOST = register("chests/pillager_outpost");
      BASTION_TREASURE = register("chests/bastion_treasure");
      BASTION_OTHER = register("chests/bastion_other");
      BASTION_BRIDGE = register("chests/bastion_bridge");
      BASTION_HOGLIN_STABLE = register("chests/bastion_hoglin_stable");
      ANCIENT_CITY = register("chests/ancient_city");
      ANCIENT_CITY_ICE_BOX = register("chests/ancient_city_ice_box");
      RUINED_PORTAL = register("chests/ruined_portal");
      TRIAL_CHAMBERS_REWARD = register("chests/trial_chambers/reward");
      TRIAL_CHAMBERS_REWARD_COMMON = register("chests/trial_chambers/reward_common");
      TRIAL_CHAMBERS_REWARD_RARE = register("chests/trial_chambers/reward_rare");
      TRIAL_CHAMBERS_REWARD_UNIQUE = register("chests/trial_chambers/reward_unique");
      TRIAL_CHAMBERS_REWARD_OMINOUS = register("chests/trial_chambers/reward_ominous");
      TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON = register("chests/trial_chambers/reward_ominous_common");
      TRIAL_CHAMBERS_REWARD_OMINOUS_RARE = register("chests/trial_chambers/reward_ominous_rare");
      TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE = register("chests/trial_chambers/reward_ominous_unique");
      TRIAL_CHAMBERS_SUPPLY = register("chests/trial_chambers/supply");
      TRIAL_CHAMBERS_CORRIDOR = register("chests/trial_chambers/corridor");
      TRIAL_CHAMBERS_INTERSECTION = register("chests/trial_chambers/intersection");
      TRIAL_CHAMBERS_INTERSECTION_BARREL = register("chests/trial_chambers/intersection_barrel");
      TRIAL_CHAMBERS_ENTRANCE = register("chests/trial_chambers/entrance");
      TRIAL_CHAMBERS_CORRIDOR_DISPENSER = register("dispensers/trial_chambers/corridor");
      TRIAL_CHAMBERS_CHAMBER_DISPENSER = register("dispensers/trial_chambers/chamber");
      TRIAL_CHAMBERS_WATER_DISPENSER = register("dispensers/trial_chambers/water");
      TRIAL_CHAMBERS_CORRIDOR_POT = register("pots/trial_chambers/corridor");
      EQUIPMENT_TRIAL_CHAMBER = register("equipment/trial_chamber");
      EQUIPMENT_TRIAL_CHAMBER_RANGED = register("equipment/trial_chamber_ranged");
      EQUIPMENT_TRIAL_CHAMBER_MELEE = register("equipment/trial_chamber_melee");
      SHEEP_BY_DYE = (Map)Util.make(new EnumMap(DyeColor.class), (var0) -> {
         makeDyeKeyMap(var0, "entities/sheep");
      });
      FISHING = register("gameplay/fishing");
      FISHING_JUNK = register("gameplay/fishing/junk");
      FISHING_TREASURE = register("gameplay/fishing/treasure");
      FISHING_FISH = register("gameplay/fishing/fish");
      CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
      ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
      BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
      CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
      CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
      FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
      FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
      FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
      LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
      LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
      MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
      SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
      TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
      WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");
      UNEMPLOYED_GIFT = register("gameplay/hero_of_the_village/unemployed_gift");
      BABY_VILLAGER_GIFT = register("gameplay/hero_of_the_village/baby_gift");
      SNIFFER_DIGGING = register("gameplay/sniffer_digging");
      PANDA_SNEEZE = register("gameplay/panda_sneeze");
      CHICKEN_LAY = register("gameplay/chicken_lay");
      ARMADILLO_SHED = register("gameplay/armadillo_shed");
      PIGLIN_BARTERING = register("gameplay/piglin_bartering");
      SPAWNER_TRIAL_CHAMBER_KEY = register("spawners/trial_chamber/key");
      SPAWNER_TRIAL_CHAMBER_CONSUMABLES = register("spawners/trial_chamber/consumables");
      SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY = register("spawners/ominous/trial_chamber/key");
      SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES = register("spawners/ominous/trial_chamber/consumables");
      SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS = register("spawners/trial_chamber/items_to_drop_when_ominous");
      BOGGED_SHEAR = register("shearing/bogged");
      SHEAR_MOOSHROOM = register("shearing/mooshroom");
      SHEAR_RED_MOOSHROOM = register("shearing/mooshroom/red");
      SHEAR_BROWN_MOOSHROOM = register("shearing/mooshroom/brown");
      SHEAR_SNOW_GOLEM = register("shearing/snow_golem");
      SHEAR_SHEEP = register("shearing/sheep");
      SHEAR_SHEEP_BY_DYE = (Map)Util.make(new EnumMap(DyeColor.class), (var0) -> {
         makeDyeKeyMap(var0, "shearing/sheep");
      });
      DESERT_WELL_ARCHAEOLOGY = register("archaeology/desert_well");
      DESERT_PYRAMID_ARCHAEOLOGY = register("archaeology/desert_pyramid");
      TRAIL_RUINS_ARCHAEOLOGY_COMMON = register("archaeology/trail_ruins_common");
      TRAIL_RUINS_ARCHAEOLOGY_RARE = register("archaeology/trail_ruins_rare");
      OCEAN_RUIN_WARM_ARCHAEOLOGY = register("archaeology/ocean_ruin_warm");
      OCEAN_RUIN_COLD_ARCHAEOLOGY = register("archaeology/ocean_ruin_cold");
   }
}
