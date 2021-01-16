package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class BuiltInLootTables {
   private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
   private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS;
   public static final ResourceLocation EMPTY;
   public static final ResourceLocation SPAWN_BONUS_CHEST;
   public static final ResourceLocation END_CITY_TREASURE;
   public static final ResourceLocation SIMPLE_DUNGEON;
   public static final ResourceLocation VILLAGE_WEAPONSMITH;
   public static final ResourceLocation VILLAGE_TOOLSMITH;
   public static final ResourceLocation VILLAGE_ARMORER;
   public static final ResourceLocation VILLAGE_CARTOGRAPHER;
   public static final ResourceLocation VILLAGE_MASON;
   public static final ResourceLocation VILLAGE_SHEPHERD;
   public static final ResourceLocation VILLAGE_BUTCHER;
   public static final ResourceLocation VILLAGE_FLETCHER;
   public static final ResourceLocation VILLAGE_FISHER;
   public static final ResourceLocation VILLAGE_TANNERY;
   public static final ResourceLocation VILLAGE_TEMPLE;
   public static final ResourceLocation VILLAGE_DESERT_HOUSE;
   public static final ResourceLocation VILLAGE_PLAINS_HOUSE;
   public static final ResourceLocation VILLAGE_TAIGA_HOUSE;
   public static final ResourceLocation VILLAGE_SNOWY_HOUSE;
   public static final ResourceLocation VILLAGE_SAVANNA_HOUSE;
   public static final ResourceLocation ABANDONED_MINESHAFT;
   public static final ResourceLocation NETHER_BRIDGE;
   public static final ResourceLocation STRONGHOLD_LIBRARY;
   public static final ResourceLocation STRONGHOLD_CROSSING;
   public static final ResourceLocation STRONGHOLD_CORRIDOR;
   public static final ResourceLocation DESERT_PYRAMID;
   public static final ResourceLocation JUNGLE_TEMPLE;
   public static final ResourceLocation JUNGLE_TEMPLE_DISPENSER;
   public static final ResourceLocation IGLOO_CHEST;
   public static final ResourceLocation WOODLAND_MANSION;
   public static final ResourceLocation UNDERWATER_RUIN_SMALL;
   public static final ResourceLocation UNDERWATER_RUIN_BIG;
   public static final ResourceLocation BURIED_TREASURE;
   public static final ResourceLocation SHIPWRECK_MAP;
   public static final ResourceLocation SHIPWRECK_SUPPLY;
   public static final ResourceLocation SHIPWRECK_TREASURE;
   public static final ResourceLocation PILLAGER_OUTPOST;
   public static final ResourceLocation BASTION_TREASURE;
   public static final ResourceLocation BASTION_OTHER;
   public static final ResourceLocation BASTION_BRIDGE;
   public static final ResourceLocation BASTION_HOGLIN_STABLE;
   public static final ResourceLocation RUINED_PORTAL;
   public static final ResourceLocation SHEEP_WHITE;
   public static final ResourceLocation SHEEP_ORANGE;
   public static final ResourceLocation SHEEP_MAGENTA;
   public static final ResourceLocation SHEEP_LIGHT_BLUE;
   public static final ResourceLocation SHEEP_YELLOW;
   public static final ResourceLocation SHEEP_LIME;
   public static final ResourceLocation SHEEP_PINK;
   public static final ResourceLocation SHEEP_GRAY;
   public static final ResourceLocation SHEEP_LIGHT_GRAY;
   public static final ResourceLocation SHEEP_CYAN;
   public static final ResourceLocation SHEEP_PURPLE;
   public static final ResourceLocation SHEEP_BLUE;
   public static final ResourceLocation SHEEP_BROWN;
   public static final ResourceLocation SHEEP_GREEN;
   public static final ResourceLocation SHEEP_RED;
   public static final ResourceLocation SHEEP_BLACK;
   public static final ResourceLocation FISHING;
   public static final ResourceLocation FISHING_JUNK;
   public static final ResourceLocation FISHING_TREASURE;
   public static final ResourceLocation FISHING_FISH;
   public static final ResourceLocation CAT_MORNING_GIFT;
   public static final ResourceLocation ARMORER_GIFT;
   public static final ResourceLocation BUTCHER_GIFT;
   public static final ResourceLocation CARTOGRAPHER_GIFT;
   public static final ResourceLocation CLERIC_GIFT;
   public static final ResourceLocation FARMER_GIFT;
   public static final ResourceLocation FISHERMAN_GIFT;
   public static final ResourceLocation FLETCHER_GIFT;
   public static final ResourceLocation LEATHERWORKER_GIFT;
   public static final ResourceLocation LIBRARIAN_GIFT;
   public static final ResourceLocation MASON_GIFT;
   public static final ResourceLocation SHEPHERD_GIFT;
   public static final ResourceLocation TOOLSMITH_GIFT;
   public static final ResourceLocation WEAPONSMITH_GIFT;
   public static final ResourceLocation PIGLIN_BARTERING;

   private static ResourceLocation register(String var0) {
      return register(new ResourceLocation(var0));
   }

   private static ResourceLocation register(ResourceLocation var0) {
      if (LOCATIONS.add(var0)) {
         return var0;
      } else {
         throw new IllegalArgumentException(var0 + " is already a registered built-in loot table");
      }
   }

   public static Set<ResourceLocation> all() {
      return IMMUTABLE_LOCATIONS;
   }

   static {
      IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
      EMPTY = new ResourceLocation("empty");
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
      RUINED_PORTAL = register("chests/ruined_portal");
      SHEEP_WHITE = register("entities/sheep/white");
      SHEEP_ORANGE = register("entities/sheep/orange");
      SHEEP_MAGENTA = register("entities/sheep/magenta");
      SHEEP_LIGHT_BLUE = register("entities/sheep/light_blue");
      SHEEP_YELLOW = register("entities/sheep/yellow");
      SHEEP_LIME = register("entities/sheep/lime");
      SHEEP_PINK = register("entities/sheep/pink");
      SHEEP_GRAY = register("entities/sheep/gray");
      SHEEP_LIGHT_GRAY = register("entities/sheep/light_gray");
      SHEEP_CYAN = register("entities/sheep/cyan");
      SHEEP_PURPLE = register("entities/sheep/purple");
      SHEEP_BLUE = register("entities/sheep/blue");
      SHEEP_BROWN = register("entities/sheep/brown");
      SHEEP_GREEN = register("entities/sheep/green");
      SHEEP_RED = register("entities/sheep/red");
      SHEEP_BLACK = register("entities/sheep/black");
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
      PIGLIN_BARTERING = register("gameplay/piglin_bartering");
   }
}
