package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class MapDecorationTypes {
   public static final Holder<MapDecorationType> PLAYER = register("player", "player", false, true);
   public static final Holder<MapDecorationType> FRAME = register("frame", "frame", true, true);
   public static final Holder<MapDecorationType> RED_MARKER = register("red_marker", "red_marker", false, true);
   public static final Holder<MapDecorationType> BLUE_MARKER = register("blue_marker", "blue_marker", false, true);
   public static final Holder<MapDecorationType> TARGET_X = register("target_x", "target_x", true, false);
   public static final Holder<MapDecorationType> TARGET_POINT = register("target_point", "target_point", true, false);
   public static final Holder<MapDecorationType> PLAYER_OFF_MAP = register("player_off_map", "player_off_map", false, true);
   public static final Holder<MapDecorationType> PLAYER_OFF_LIMITS = register("player_off_limits", "player_off_limits", false, true);
   public static final Holder<MapDecorationType> WOODLAND_MANSION = register("mansion", "woodland_mansion", true, false);
   public static final Holder<MapDecorationType> OCEAN_MONUMENT = register("monument", "ocean_monument", true, false);
   public static final Holder<MapDecorationType> WHITE_BANNER = register("banner_white", "white_banner", true, true);
   public static final Holder<MapDecorationType> ORANGE_BANNER = register("banner_orange", "orange_banner", true, true);
   public static final Holder<MapDecorationType> MAGENTA_BANNER = register("banner_magenta", "magenta_banner", true, true);
   public static final Holder<MapDecorationType> LIGHT_BLUE_BANNER = register("banner_light_blue", "light_blue_banner", true, true);
   public static final Holder<MapDecorationType> YELLOW_BANNER = register("banner_yellow", "yellow_banner", true, true);
   public static final Holder<MapDecorationType> LIME_BANNER = register("banner_lime", "lime_banner", true, true);
   public static final Holder<MapDecorationType> PINK_BANNER = register("banner_pink", "pink_banner", true, true);
   public static final Holder<MapDecorationType> GRAY_BANNER = register("banner_gray", "gray_banner", true, true);
   public static final Holder<MapDecorationType> LIGHT_GRAY_BANNER = register("banner_light_gray", "light_gray_banner", true, true);
   public static final Holder<MapDecorationType> CYAN_BANNER = register("banner_cyan", "cyan_banner", true, true);
   public static final Holder<MapDecorationType> PURPLE_BANNER = register("banner_purple", "purple_banner", true, true);
   public static final Holder<MapDecorationType> BLUE_BANNER = register("banner_blue", "blue_banner", true, true);
   public static final Holder<MapDecorationType> BROWN_BANNER = register("banner_brown", "brown_banner", true, true);
   public static final Holder<MapDecorationType> GREEN_BANNER = register("banner_green", "green_banner", true, true);
   public static final Holder<MapDecorationType> RED_BANNER = register("banner_red", "red_banner", true, true);
   public static final Holder<MapDecorationType> BLACK_BANNER = register("banner_black", "black_banner", true, true);
   public static final Holder<MapDecorationType> RED_X = register("red_x", "red_x", true, false);
   public static final Holder<MapDecorationType> DESERT_VILLAGE = register("village_desert", "desert_village", true, false);
   public static final Holder<MapDecorationType> PLAINS_VILLAGE = register("village_plains", "plains_village", true, false);
   public static final Holder<MapDecorationType> SAVANNA_VILLAGE = register("village_savanna", "savanna_village", true, false);
   public static final Holder<MapDecorationType> SNOWY_VILLAGE = register("village_snowy", "snowy_village", true, false);
   public static final Holder<MapDecorationType> TAIGA_VILLAGE = register("village_taiga", "taiga_village", true, false);
   public static final Holder<MapDecorationType> JUNGLE_TEMPLE = register("jungle_temple", "jungle_temple", true, false);
   public static final Holder<MapDecorationType> SWAMP_HUT = register("swamp_hut", "swamp_hut", true, false);
   public static final Holder<MapDecorationType> TRIAL_CHAMBERS = register("trial_chambers", "trial_chambers", true, false);
   public static final Holder<MapDecorationType> ABANDONED_CAMP = register("abandoned_camp", "abandoned_camp", true, false);
   public static final Holder<MapDecorationType> ANCIENT_CITY = register("ancient_city", "ancient_city", true, false);
   public static final Holder<MapDecorationType> DESERT_PYRAMID = register("desert_pyramid", "desert_pyramid", true, false);
   public static final Holder<MapDecorationType> MINESHAFT = register("mineshaft", "mineshaft", true, false);
   public static final Holder<MapDecorationType> OCEAN_RUIN_WARM = register("ocean_ruin_warm", "warm_ocean_ruins", true, false);

   public MapDecorationTypes() {
      super();
   }

   public static Holder<MapDecorationType> bootstrap(final Registry<MapDecorationType> registry) {
      return PLAYER;
   }

   private static Holder<MapDecorationType> register(final String name, final String assetName, final boolean showOnItemFrame, final boolean trackCount) {
      ResourceKey<MapDecorationType> key = ResourceKey.create(Registries.MAP_DECORATION_TYPE, Identifier.withDefaultNamespace(name));
      MapDecorationType type = new MapDecorationType(Identifier.withDefaultNamespace(assetName), showOnItemFrame, trackCount);
      return Registry.registerForHolder(BuiltInRegistries.MAP_DECORATION_TYPE, key, type);
   }
}
