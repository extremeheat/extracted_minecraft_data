package net.minecraft.world.item.trading;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.component.predicates.VillagerTypePredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.PotionTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.DiscardItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FilteredFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.functions.SetRandomDyesFunction;
import net.minecraft.world.level.storage.loot.functions.SetRandomPotionFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.Sum;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class VillagerTrades {
   public static final ResourceKey<VillagerTrade> FARMER_1_WHEAT_EMERALD = resourceKey("farmer/1/wheat_emerald");
   public static final ResourceKey<VillagerTrade> FARMER_1_POTATO_EMERALD = resourceKey("farmer/1/potato_emerald");
   public static final ResourceKey<VillagerTrade> FARMER_1_CARROT_EMERALD = resourceKey("farmer/1/carrot_emerald");
   public static final ResourceKey<VillagerTrade> FARMER_1_BEETROOT_EMERALD = resourceKey("farmer/1/beetroot_emerald");
   public static final ResourceKey<VillagerTrade> FARMER_1_EMERALD_BREAD = resourceKey("farmer/1/emerald_bread");
   public static final ResourceKey<VillagerTrade> FARMER_2_PUMPKIN_EMERALD = resourceKey("farmer/2/pumpkin_emerald");
   public static final ResourceKey<VillagerTrade> FARMER_2_EMERALD_PUMPKIN_PIE = resourceKey("farmer/2/emerald_pumpkin_pie");
   public static final ResourceKey<VillagerTrade> FARMER_2_EMERALD_APPLE = resourceKey("farmer/2/emerald_apple");
   public static final ResourceKey<VillagerTrade> FARMER_3_EMERALD_COOKIE = resourceKey("farmer/3/emerald_cookie");
   public static final ResourceKey<VillagerTrade> FARMER_3_MELON_EMERALD = resourceKey("farmer/3/melon_emerald");
   public static final ResourceKey<VillagerTrade> FARMER_4_EMERALD_CAKE = resourceKey("farmer/4/emerald_cake");
   public static final ResourceKey<VillagerTrade> FARMER_4_EMERALD_SUSPICIOUS_STEW = resourceKey("farmer/4/emerald_suspicious_stew");
   public static final ResourceKey<VillagerTrade> FARMER_5_EMERALD_GOLDEN_CARROT = resourceKey("farmer/5/emerald_golden_carrot");
   public static final ResourceKey<VillagerTrade> FARMER_5_EMERALD_GLISTENING_MELON_SLICE = resourceKey("farmer/5/emerald_glistening_melon_slice");
   public static final ResourceKey<VillagerTrade> FISHERMAN_1_STRING_EMERALD = resourceKey("fisherman/1/string_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_1_COAL_EMERALD = resourceKey("fisherman/1/coal_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_1_RAW_COD_AND_EMERALD_COOKED_COD = resourceKey("fisherman/1/raw_cod_and_emerald_cooked_cod");
   public static final ResourceKey<VillagerTrade> FISHERMAN_1_EMERALD_COD_BUCKET = resourceKey("fisherman/1/emerald_cod_bucket");
   public static final ResourceKey<VillagerTrade> FISHERMAN_2_COD_EMERALD = resourceKey("fisherman/2/cod_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_2_SALMON_AND_EMERALD_COOKED_SALMON = resourceKey("fisherman/2/salmon_and_emerald_cooked_salmon");
   public static final ResourceKey<VillagerTrade> FISHERMAN_2_EMERALD_CAMPFIRE = resourceKey("fisherman/2/emerald_campfire");
   public static final ResourceKey<VillagerTrade> FISHERMAN_3_SALMON_EMERALD = resourceKey("fisherman/3/salmon_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_3_EMERALD_ENCHANTED_FISHING_ROD = resourceKey("fisherman/3/emerald_enchanted_fishing_rod");
   public static final ResourceKey<VillagerTrade> FISHERMAN_4_TROPICAL_FISH_EMERALD = resourceKey("fisherman/4/tropical_fish_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_5_PUFFERFISH_EMERALD = resourceKey("fisherman/5/pufferfish_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_5_OAK_BOAT_EMERALD = resourceKey("fisherman/5/oak_boat_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_5_SPRUCE_BOAT_EMERALD = resourceKey("fisherman/5/spruce_boat_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_5_JUNGLE_BOAT_EMERALD = resourceKey("fisherman/5/jungle_boat_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_5_ACACIA_BOAT_EMERALD = resourceKey("fisherman/5/acacia_boat_emerald");
   public static final ResourceKey<VillagerTrade> FISHERMAN_5_DARK_OAK_BOAT_EMERALD = resourceKey("fisherman/5/dark_oak_boat_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_1_WHITE_WOOL_EMERALD = resourceKey("shepherd/1/white_wool_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_1_BROWN_WOOL_EMERALD = resourceKey("shepherd/1/brown_wool_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_1_GRAY_WOOL_EMERALD = resourceKey("shepherd/1/gray_wool_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_1_BLACK_WOOL_EMERALD = resourceKey("shepherd/1/black_wool_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_1_EMERALD_SHEARS = resourceKey("shepherd/1/emerald_shears");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_WHITE_DYE_EMERALD = resourceKey("shepherd/2/white_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_GRAY_DYE_EMERALD = resourceKey("shepherd/2/gray_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_BLACK_DYE_EMERALD = resourceKey("shepherd/2/black_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_LIGHT_BLUE_DYE_EMERALD = resourceKey("shepherd/2/light_blue_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_LIME_DYE_EMERALD = resourceKey("shepherd/2/lime_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_WHITE_WOOL = resourceKey("shepherd/2/emerald_white_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_ORANGE_WOOL = resourceKey("shepherd/2/emerald_orange_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_MAGENTA_WOOL = resourceKey("shepherd/2/emerald_magenta_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_BLUE_WOOL = resourceKey("shepherd/2/emerald_blue_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_LIGHT_BLUE_WOOL = resourceKey("shepherd/2/emerald_light_blue_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_YELLOW_WOOL = resourceKey("shepherd/2/emerald_yellow_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_LIME_WOOL = resourceKey("shepherd/2/emerald_lime_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_PINK_WOOL = resourceKey("shepherd/2/emerald_pink_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_GRAY_WOOL = resourceKey("shepherd/2/emerald_gray_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_LIGHT_GRAY_WOOL = resourceKey("shepherd/2/emerald_light_gray_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_CYAN_WOOL = resourceKey("shepherd/2/emerald_cyan_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_PURPLE_WOOL = resourceKey("shepherd/2/emerald_purple_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_BROWN_WOOL = resourceKey("shepherd/2/emerald_brown_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_GREEN_WOOL = resourceKey("shepherd/2/emerald_green_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_RED_WOOL = resourceKey("shepherd/2/emerald_red_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_BLACK_WOOL = resourceKey("shepherd/2/emerald_black_wool");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_WHITE_CARPET = resourceKey("shepherd/2/emerald_white_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_ORANGE_CARPET = resourceKey("shepherd/2/emerald_orange_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_MAGENTA_CARPET = resourceKey("shepherd/2/emerald_magenta_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_BLUE_CARPET = resourceKey("shepherd/2/emerald_blue_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_LIGHT_BLUE_CARPET = resourceKey("shepherd/2/emerald_light_blue_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_YELLOW_CARPET = resourceKey("shepherd/2/emerald_yellow_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_LIME_CARPET = resourceKey("shepherd/2/emerald_lime_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_PINK_CARPET = resourceKey("shepherd/2/emerald_pink_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_GRAY_CARPET = resourceKey("shepherd/2/emerald_gray_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_LIGHT_GRAY_CARPET = resourceKey("shepherd/2/emerald_light_gray_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_CYAN_CARPET = resourceKey("shepherd/2/emerald_cyan_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_PURPLE_CARPET = resourceKey("shepherd/2/emerald_purple_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_BROWN_CARPET = resourceKey("shepherd/2/emerald_brown_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_GREEN_CARPET = resourceKey("shepherd/2/emerald_green_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_RED_CARPET = resourceKey("shepherd/2/emerald_red_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_2_EMERALD_BLACK_CARPET = resourceKey("shepherd/2/emerald_black_carpet");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_YELLOW_DYE_EMERALD = resourceKey("shepherd/3/yellow_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_LIGHT_GRAY_DYE_EMERALD = resourceKey("shepherd/3/light_gray_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_ORANGE_DYE_EMERALD = resourceKey("shepherd/3/orange_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_RED_DYE_EMERALD = resourceKey("shepherd/3/red_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_PINK_DYE_EMERALD = resourceKey("shepherd/3/pink_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_WHITE_BED = resourceKey("shepherd/3/emerald_white_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_ORANGE_BED = resourceKey("shepherd/3/emerald_orange_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_MAGENTA_BED = resourceKey("shepherd/3/emerald_magenta_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_BLUE_BED = resourceKey("shepherd/3/emerald_blue_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_LIGHT_BLUE_BED = resourceKey("shepherd/3/emerald_light_blue_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_YELLOW_BED = resourceKey("shepherd/3/emerald_yellow_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_LIME_BED = resourceKey("shepherd/3/emerald_lime_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_PINK_BED = resourceKey("shepherd/3/emerald_pink_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_GRAY_BED = resourceKey("shepherd/3/emerald_gray_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_LIGHT_GRAY_BED = resourceKey("shepherd/3/emerald_light_gray_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_CYAN_BED = resourceKey("shepherd/3/emerald_cyan_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_PURPLE_BED = resourceKey("shepherd/3/emerald_purple_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_BROWN_BED = resourceKey("shepherd/3/emerald_brown_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_GREEN_BED = resourceKey("shepherd/3/emerald_green_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_RED_BED = resourceKey("shepherd/3/emerald_red_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_EMERALD_BLACK_BED = resourceKey("shepherd/3/emerald_black_bed");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_BROWN_DYE_EMERALD = resourceKey("shepherd/4/brown_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_PURPLE_DYE_EMERALD = resourceKey("shepherd/4/purple_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_BLUE_DYE_EMERALD = resourceKey("shepherd/4/blue_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_GREEN_DYE_EMERALD = resourceKey("shepherd/4/green_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_MAGENTA_DYE_EMERALD = resourceKey("shepherd/4/magenta_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_CYAN_DYE_EMERALD = resourceKey("shepherd/4/cyan_dye_emerald");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_WHITE_BANNER = resourceKey("shepherd/4/emerald_white_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_ORANGE_BANNER = resourceKey("shepherd/4/emerald_orange_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_MAGENTA_BANNER = resourceKey("shepherd/4/emerald_magenta_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_BLUE_BANNER = resourceKey("shepherd/4/emerald_blue_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_LIGHT_BLUE_BANNER = resourceKey("shepherd/4/emerald_light_blue_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_YELLOW_BANNER = resourceKey("shepherd/4/emerald_yellow_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_LIME_BANNER = resourceKey("shepherd/4/emerald_lime_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_PINK_BANNER = resourceKey("shepherd/4/emerald_pink_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_GRAY_BANNER = resourceKey("shepherd/4/emerald_gray_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_LIGHT_GRAY_BANNER = resourceKey("shepherd/4/emerald_light_gray_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_CYAN_BANNER = resourceKey("shepherd/4/emerald_cyan_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_PURPLE_BANNER = resourceKey("shepherd/4/emerald_purple_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_BROWN_BANNER = resourceKey("shepherd/4/emerald_brown_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_GREEN_BANNER = resourceKey("shepherd/4/emerald_green_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_RED_BANNER = resourceKey("shepherd/4/emerald_red_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_EMERALD_BLACK_BANNER = resourceKey("shepherd/4/emerald_black_banner");
   public static final ResourceKey<VillagerTrade> SHEPHERD_5_EMERALD_PAINTING = resourceKey("shepherd/5/emerald_painting");
   public static final ResourceKey<VillagerTrade> FLETCHER_1_STICK_EMERALD = resourceKey("fletcher/1/stick_emerald");
   public static final ResourceKey<VillagerTrade> FLETCHER_1_EMERALD_ARROW = resourceKey("fletcher/1/emerald_arrow");
   public static final ResourceKey<VillagerTrade> FLETCHER_1_GRAVEL_AND_EMERALD_ARROW = resourceKey("fletcher/1/gravel_and_emerald_arrow");
   public static final ResourceKey<VillagerTrade> FLETCHER_2_FLINT_EMERALD = resourceKey("fletcher/2/flint_emerald");
   public static final ResourceKey<VillagerTrade> FLETCHER_2_EMERALD_BOW = resourceKey("fletcher/2/emerald_bow");
   public static final ResourceKey<VillagerTrade> FLETCHER_3_STRING_EMERALD = resourceKey("fletcher/3/string_emerald");
   public static final ResourceKey<VillagerTrade> FLETCHER_3_EMERALD_CROSSBOW = resourceKey("fletcher/3/emerald_crossbow");
   public static final ResourceKey<VillagerTrade> FLETCHER_4_FEATHER_EMERALD = resourceKey("fletcher/4/feather_emerald");
   public static final ResourceKey<VillagerTrade> FLETCHER_4_EMERALD_ENCHANTED_BOW = resourceKey("fletcher/4/emerald_enchanted_bow");
   public static final ResourceKey<VillagerTrade> FLETCHER_5_TRIPWIRE_HOOK_EMERALD = resourceKey("fletcher/5/tripwire_hook_emerald");
   public static final ResourceKey<VillagerTrade> FLETCHER_5_EMERALD_ENCHANTED_CROSSBOW = resourceKey("fletcher/5/emerald_enchanted_crossbow");
   public static final ResourceKey<VillagerTrade> FLETCHER_5_ARROW_AND_EMERALD_TIPPED_ARROW = resourceKey("fletcher/5/arrow_and_emerald_tipped_arrow");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_1_PAPER_EMERALD = resourceKey("librarian/1/paper_emerald");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_1_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/1/emerald_and_book_enchanted_book");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_1_EMERALD_BOOKSHELF = resourceKey("librarian/1/emerald_bookshelf");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_2_BOOK_EMERALD = resourceKey("librarian/2/book_emerald");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_2_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/2/emerald_and_book_enchanted_book");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_2_EMERALD_LANTERN = resourceKey("librarian/2/emerald_lantern");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_3_INK_SAC_EMERALD = resourceKey("librarian/3/ink_sac_emerald");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_3_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/3/emerald_and_book_enchanted_book");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_3_EMERALD_GLASS = resourceKey("librarian/3/emerald_glass");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_WRITABLE_BOOK_EMERALD = resourceKey("librarian/4/writable_book_emerald");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/4/emerald_book_and_enchanted_book");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_EMERALD_CLOCK = resourceKey("librarian/4/emerald_clock");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_EMERALD_COMPASS = resourceKey("librarian/4/emerald_compass");
   public static final ResourceKey<VillagerTrade> LIBRARIAN_5_EMERALD_NAME_TAG = resourceKey("librarian/5/emerald_name_tag");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_1_PAPER_EMERALD = resourceKey("cartographer/1/paper_emerald");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_1_EMERALD_MAP = resourceKey("cartographer/1/emerald_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_GLASS_PANE_EMERALD = resourceKey("cartographer/2/glass_pane_emerald");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_TAIGA_MAP = resourceKey("cartographer/2/emerald_and_compass_village_taiga_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_SWAMP_MAP = resourceKey("cartographer/2/emerald_and_compass_explorer_swamp_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SNOWY_MAP = resourceKey("cartographer/2/emerald_and_compass_village_snowy_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SAVANNA_MAP = resourceKey("cartographer/2/emerald_and_compass_village_savanna_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_PLAINS_MAP = resourceKey("cartographer/2/emerald_and_compass_village_plains_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_JUNGLE_MAP = resourceKey("cartographer/2/emerald_and_compass_explorer_jungle_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_DESERT_MAP = resourceKey("cartographer/2/emerald_and_compass_village_desert_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_COMPASS_EMERALD = resourceKey("cartographer/3/compass_emerald");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_EMERALD_AND_COMPASS_OCEAN_EXPLORER_MAP = resourceKey("cartographer/3/emerald_and_compass_ocean_explorer_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_EMERALD_AND_COMPASS_TRIAL_CHAMBER_MAP = resourceKey("cartographer/3/emerald_and_compass_trial_chamber_map");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_ITEM_FRAME = resourceKey("cartographer/4/emerald_item_frame");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_WHITE_BANNER = resourceKey("cartographer/4/emerald_white_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_ORANGE_BANNER = resourceKey("cartographer/4/emerald_orange_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_MAGENTA_BANNER = resourceKey("cartographer/4/emerald_magenta_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_BLUE_BANNER = resourceKey("cartographer/4/emerald_blue_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_LIGHT_BLUE_BANNER = resourceKey("cartographer/4/emerald_light_blue_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_YELLOW_BANNER = resourceKey("cartographer/4/emerald_yellow_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_LIME_BANNER = resourceKey("cartographer/4/emerald_lime_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_PINK_BANNER = resourceKey("cartographer/4/emerald_pink_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_GRAY_BANNER = resourceKey("cartographer/4/emerald_gray_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_CYAN_BANNER = resourceKey("cartographer/4/emerald_cyan_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_PURPLE_BANNER = resourceKey("cartographer/4/emerald_purple_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_BROWN_BANNER = resourceKey("cartographer/4/emerald_brown_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_GREEN_BANNER = resourceKey("cartographer/4/emerald_green_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_RED_BANNER = resourceKey("cartographer/4/emerald_red_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_BLACK_BANNER = resourceKey("cartographer/4/emerald_black_banner");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_5_EMERALD_GLOBE_BANNER_PATTERN = resourceKey("cartographer/5/emerald_globe_banner_pattern");
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_5_EMERALD_AND_COMPASS_WOODLAND_MANSION_MAP = resourceKey("cartographer/5/emerald_and_compass_woodland_mansion_map");
   public static final ResourceKey<VillagerTrade> CLERIC_1_ROTTEN_FLESH_EMERALD = resourceKey("cleric/1/rotten_flesh_emerald");
   public static final ResourceKey<VillagerTrade> CLERIC_1_EMERALD_REDSTONE = resourceKey("cleric/1/emerald_redstone");
   public static final ResourceKey<VillagerTrade> CLERIC_2_GOLD_INGOT_EMERALD = resourceKey("cleric/2/gold_ingot_emerald");
   public static final ResourceKey<VillagerTrade> CLERIC_2_EMERALD_LAPIS_LAZULI = resourceKey("cleric/2/emerald_lapis_lazuli");
   public static final ResourceKey<VillagerTrade> CLERIC_3_RABBIT_FOOT_EMERALD = resourceKey("cleric/3/rabbit_foot_emerald");
   public static final ResourceKey<VillagerTrade> CLERIC_3_EMERALD_GLOWSTONE = resourceKey("cleric/3/emerald_glowstone");
   public static final ResourceKey<VillagerTrade> CLERIC_4_TURTLE_SCUTE_EMERALD = resourceKey("cleric/4/turtle_scute_emerald");
   public static final ResourceKey<VillagerTrade> CLERIC_4_GLASS_BOTTLE_EMERALD = resourceKey("cleric/4/glass_bottle_emerald");
   public static final ResourceKey<VillagerTrade> CLERIC_4_EMERALD_ENDER_PEARL = resourceKey("cleric/4/emerald_ender_pearl");
   public static final ResourceKey<VillagerTrade> CLERIC_5_NETHER_WART_EMERALD = resourceKey("cleric/5/nether_wart_emerald");
   public static final ResourceKey<VillagerTrade> CLERIC_5_EMERALD_EXPERIENCE_BOTTLE = resourceKey("cleric/5/emerald_experience_bottle");
   public static final ResourceKey<VillagerTrade> COMMON_SMITH_1_COAL_EMERALD = resourceKey("smith/1/coal_emerald");
   public static final ResourceKey<VillagerTrade> COMMON_SMITH_2_IRON_INGOT_EMERALD = resourceKey("smith/2/iron_ingot_emerald");
   public static final ResourceKey<VillagerTrade> COMMON_SMITH_2_EMERALD_BELL = resourceKey("smith/2/emerald_bell");
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_LEGGINGS = resourceKey("armorer/1/emerald_iron_leggings");
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_BOOTS = resourceKey("armorer/1/emerald_iron_boots");
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_HELMET = resourceKey("armorer/1/emerald_iron_helmet");
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_CHESTPLATE = resourceKey("armorer/1/emerald_iron_chestplate");
   public static final ResourceKey<VillagerTrade> ARMORER_2_EMERALD_CHAINMAIL_BOOTS = resourceKey("armorer/2/emerald_chainmail_boots");
   public static final ResourceKey<VillagerTrade> ARMORER_2_EMERALD_CHAINMAIL_LEGGINGS = resourceKey("armorer/2/emerald_chainmail_leggings");
   public static final ResourceKey<VillagerTrade> ARMORER_3_LAVA_BUCKET_EMERALD = resourceKey("armorer/3/lava_bucket_emerald");
   public static final ResourceKey<VillagerTrade> ARMORER_3_EMERALD_CHAINMAIL_HELMET = resourceKey("armorer/3/emerald_chainmail_helmet");
   public static final ResourceKey<VillagerTrade> ARMORER_3_EMERALD_CHAINMAIL_CHESTPLATE = resourceKey("armorer/3/emerald_chainmail_chestplate");
   public static final ResourceKey<VillagerTrade> ARMORER_3_EMERALD_SHIELD = resourceKey("armorer/3/emerald_shield");
   public static final ResourceKey<VillagerTrade> ARMORER_3_DIAMOND_EMERALD = resourceKey("armorer/3/diamond_emerald");
   public static final ResourceKey<VillagerTrade> ARMORER_4_EMERALD_ENCHANTED_DIAMOND_LEGGINGS = resourceKey("armorer/4/emerald_enchanted_diamond_leggings");
   public static final ResourceKey<VillagerTrade> ARMORER_4_EMERALD_ENCHANTED_DIAMOND_BOOTS = resourceKey("armorer/4/emerald_enchanted_diamond_boots");
   public static final ResourceKey<VillagerTrade> ARMORER_5_EMERALD_ENCHANTED_DIAMOND_HELMET = resourceKey("armorer/5/emerald_enchanted_diamond_helmet");
   public static final ResourceKey<VillagerTrade> ARMORER_5_EMERALD_ENCHANTED_DIAMOND_CHESTPLATE = resourceKey("armorer/5/emerald_enchanted_diamond_chestplate");
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_1_EMERALD_IRON_AXE = resourceKey("weaponsmith/1/emerald_iron_axe");
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_1_EMERALD_ENCHANTED_IRON_SWORD = resourceKey("weaponsmith/1/emerald_enchanted_iron_sword");
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_3_FLINT_EMERALD = resourceKey("weaponsmith/3/flint_emerald");
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_4_DIAMOND_EMERALD = resourceKey("weaponsmith/4/diamond_emerald");
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_4_EMERALD_ENCHANTED_DIAMOND_AXE = resourceKey("weaponsmith/4/emerald_enchanted_diamond_axe");
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_5_EMERALD_ENCHANTED_DIAMOND_SWORD = resourceKey("weaponsmith/5/emerald_enchanted_diamond_sword");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_AXE = resourceKey("toolsmith/1/emerald_stone_axe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_SHOVEL = resourceKey("toolsmith/1/emerald_stone_shovel");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_PICKAXE = resourceKey("toolsmith/1/emerald_stone_pickaxe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_HOE = resourceKey("toolsmith/1/emerald_stone_hoe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_FLINT_EMERALD = resourceKey("toolsmith/3/flint_emerald");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_IRON_AXE = resourceKey("toolsmith/3/emerald_enchanted_iron_axe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_IRON_SHOVEL = resourceKey("toolsmith/3/emerald_enchanted_iron_shovel");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_IRON_PICKAXE = resourceKey("toolsmith/3/emerald_enchanted_iron_pickaxe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_DIAMOND_HOE = resourceKey("toolsmith/3/emerald_diamond_hoe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_4_DIAMOND_EMERALD = resourceKey("toolsmith/4/diamond_emerald");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_4_EMERALD_DIAMOND_AXE = resourceKey("toolsmith/4/emerald_enchanted_diamond_axe");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_4_EMERALD_DIAMOND_SHOVEL = resourceKey("toolsmith/4/emerald_enchanted_diamond_shovel");
   public static final ResourceKey<VillagerTrade> TOOLSMITH_5_EMERALD_DIAMOND_PICKAXE = resourceKey("toolsmith/5/emerald_enchanted_diamond_pickaxe");
   public static final ResourceKey<VillagerTrade> BUTCHER_1_CHICKEN_EMERALD = resourceKey("butcher/1/chicken_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_1_PORKCHOP_EMERALD = resourceKey("butcher/1/porkchop_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_1_RABBIT_EMERALD = resourceKey("butcher/1/rabbit_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_1_EMERALD_RABBIT_STEW = resourceKey("butcher/1/emerald_rabbit_stew");
   public static final ResourceKey<VillagerTrade> BUTCHER_2_COAL_EMERALD = resourceKey("butcher/2/coal_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_2_EMERALD_COOKED_PORKCHOP = resourceKey("butcher/2/emerald_cooked_porkchop");
   public static final ResourceKey<VillagerTrade> BUTCHER_2_EMERALD_COOKED_CHICKEN = resourceKey("butcher/2/emerald_cooked_chicken");
   public static final ResourceKey<VillagerTrade> BUTCHER_3_MUTTON_EMERALD = resourceKey("butcher/3/mutton_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_3_BEEF_EMERALD = resourceKey("butcher/3/beef_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_4_DRIED_KELP_BLOCK_EMERALD = resourceKey("butcher/4/dried_kelp_block_emerald");
   public static final ResourceKey<VillagerTrade> BUTCHER_5_SWEET_BERRIES_EMERALD = resourceKey("butcher/5/sweet_berries_emerald");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_1_LEATHER_EMERALD = resourceKey("leatherworker/1/leather_emerald");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_1_EMERALD_DYED_LEATHER_LEGGINGS = resourceKey("leatherworker/1/emerald_dyed_leather_leggings");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_1_EMERALD_DYED_LEATHER_CHESTPLATE = resourceKey("leatherworker/1/emerald_dyed_leather_chestplate");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_2_FLINT_EMERALD = resourceKey("leatherworker/2/flint_emerald");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_2_EMERALD_DYED_LEATHER_HELMET = resourceKey("leatherworker/2/emerald_dyed_leather_helmet");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_2_EMERALD_DYED_LEATHER_BOOTS = resourceKey("leatherworker/2/emerald_dyed_leather_boots");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_3_RABBIT_HIDE_EMERALD = resourceKey("leatherworker/3/rabbit_hide_emerald");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_3_EMERALD_DYED_LEATHER_CHESTPLATE = resourceKey("leatherworker/3/emerald_dyed_leather_chestplate");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_4_TURTLE_SCUTE_EMERALD = resourceKey("leatherworker/4/turtle_scute_emerald");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_4_EMERALD_DYED_LEATHER_HORSE_ARMOR = resourceKey("leatherworker/4/emerald_dyed_leather_horse_armor");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_5_EMERALD_SADDLE = resourceKey("leatherworker/5/emerald_saddle");
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_5_EMERALD_DYED_LEATHER_HELMET = resourceKey("leatherworker/5/emerald_dyed_leather_helmet");
   public static final ResourceKey<VillagerTrade> MASON_1_CLAY_BALL_EMERALD = resourceKey("mason/1/clay_ball_emerald");
   public static final ResourceKey<VillagerTrade> MASON_1_EMERALD_BRICK = resourceKey("mason/1/emerald_brick");
   public static final ResourceKey<VillagerTrade> MASON_2_STONE_EMERALD = resourceKey("mason/2/stone_emerald");
   public static final ResourceKey<VillagerTrade> MASON_2_EMERALD_CHISELED_STONE_BRICKS = resourceKey("mason/2/emerald_chiseled_stone_bricks");
   public static final ResourceKey<VillagerTrade> MASON_3_GRANITE_EMERALD = resourceKey("mason/3/granite_emerald");
   public static final ResourceKey<VillagerTrade> MASON_3_ANDESITE_EMERALD = resourceKey("mason/3/andesite_emerald");
   public static final ResourceKey<VillagerTrade> MASON_3_DIORITE_EMERALD = resourceKey("mason/3/diorite_emerald");
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_DRIPSTONE_BLOCK = resourceKey("mason/3/emerald_dripstone_block");
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_POLISHED_ANDESITE = resourceKey("mason/3/emerald_polished_andesite");
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_POLISHED_DIORITE = resourceKey("mason/3/emerald_polished_diorite");
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_POLISHED_GRANTITE = resourceKey("mason/3/emerald_polished_granite");
   public static final ResourceKey<VillagerTrade> MASON_4_QUARTZ_EMERALD = resourceKey("mason/4/quartz_emerald");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_ORANGE_TERRACOTTA = resourceKey("mason/4/emerald_orange_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_WHITE_TERRACOTTA = resourceKey("mason/4/emerald_white_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_BLUE_TERRACOTTA = resourceKey("mason/4/emerald_blue_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_LIGHT_BLUE_TERRACOTTA = resourceKey("mason/4/emerald_light_blue_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_GRAY_TERRACOTTA = resourceKey("mason/4/emerald_gray_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_LIGHT_GRAY_TERRACOTTA = resourceKey("mason/4/emerald_light_gray_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_BLACK_TERRACOTTA = resourceKey("mason/4/emerald_black_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_RED_TERRACOTTA = resourceKey("mason/4/emerald_red_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_PINK_TERRACOTTA = resourceKey("mason/4/emerald_pink_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_MAGENTA_TERRACOTTA = resourceKey("mason/4/emerald_magenta_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_LIME_TERRACOTTA = resourceKey("mason/4/emerald_lime_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_GREEN_TERRACOTTA = resourceKey("mason/4/emerald_green_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_CYAN_TERRACOTTA = resourceKey("mason/4/emerald_cyan_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_PURPLE_TERRACOTTA = resourceKey("mason/4/emerald_purple_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_YELLOW_TERRACOTTA = resourceKey("mason/4/emerald_yellow_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_BROWN_TERRACOTTA = resourceKey("mason/4/emerald_brown_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_ORANGE_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_orange_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_WHITE_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_white_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_BLUE_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_blue_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_LIGHT_BLUE_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_light_blue_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_GRAY_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_gray_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_LIGHT_GRAY_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_light_gray_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_BLACK_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_black_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_RED_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_red_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_PINK_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_pink_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_MAGENTA_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_magenta_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_LIME_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_lime_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_GREEN_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_green_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_CYAN_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_cyan_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_PURPLE_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_purple_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_YELLOW_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_yellow_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_4_EMERALD_BROWN_GLAZED_TERRACOTTA = resourceKey("mason/4/emerald_brown_glazed_terracotta");
   public static final ResourceKey<VillagerTrade> MASON_5_EMERALD_QUARTZ_PILLAR = resourceKey("mason/5/emerald_quartz_pillar");
   public static final ResourceKey<VillagerTrade> MASON_5_EMERALD_QUARTZ_BLOCK = resourceKey("mason/5/emerald_quartz_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_WATER_BOTTLE_EMERALD = resourceKey("wandering_trader/water_bottle_emerald");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_WATER_BUCKET_EMERALD = resourceKey("wandering_trader/water_bucket_emerald");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_MILK_BUCKET_EMERALD = resourceKey("wandering_trader/milk_bucket_emerald");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_FERMENTED_SPIDER_EYE_EMERALD = resourceKey("wandering_trader/fermented_spider_eye_emerald");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_BAKED_POTATO_EMERALD = resourceKey("wandering_trader/baked_potato_emerald");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_HAY_BLOCK_EMERALD = resourceKey("wandering_trader/hay_block_emerald");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PACKED_ICE = resourceKey("wandering_trader/emerald_packed_ice");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BLUE_ICE = resourceKey("wandering_trader/emerald_blue_ice");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GUNPOWDER = resourceKey("wandering_trader/emerald_gunpowder");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PODZOL = resourceKey("wandering_trader/emerald_podzol");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ACACIA_LOG = resourceKey("wandering_trader/emerald_acacia_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BIRCH_LOG = resourceKey("wandering_trader/emerald_birch_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DARK_OAK_LOG = resourceKey("wandering_trader/emerald_dark_oak_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_JUNGLE_LOG = resourceKey("wandering_trader/emerald_jungle_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OAK_LOG = resourceKey("wandering_trader/emerald_oak_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SPRUCE_LOG = resourceKey("wandering_trader/emerald_spruce_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CHERRY_LOG = resourceKey("wandering_trader/emerald_cherry_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MANGROVE_LOG = resourceKey("wandering_trader/emerald_mangrove_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_OAK_LOG = resourceKey("wandering_trader/emerald_pale_oak_log");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ENCHANTED_IRON_PICKAXE = resourceKey("wandering_trader/emerald_enchanted_iron_pickaxe");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LONG_INVISIBILITY_POTION = resourceKey("wandering_trader/emerald_long_invisibility_potion");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_TROPICAL_FISH_BUCKET = resourceKey("wandering_trader/emerald_fish_bucket");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PUFFERFISH_BUCKET = resourceKey("wandering_trader/emerald_pufferfish_bucket");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SEA_PICKLE = resourceKey("wandering_trader/emerald_sea_pickle");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SLIME_BALL = resourceKey("wandering_trader/emerald_slime_ball");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GLOWSTONE = resourceKey("wandering_trader/emerald_glowstone");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_NAUTILUS_SHELL = resourceKey("wandering_trader/emerald_nautilus_shell");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_FERN = resourceKey("wandering_trader/emerald_fern");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SUGAR_CANE = resourceKey("wandering_trader/emerald_sugar_cane");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PUMPKIN = resourceKey("wandering_trader/emerald_pumpkin");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_KELP = resourceKey("wandering_trader/emerald_kelp");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CACTUS = resourceKey("wandering_trader/emerald_cactus");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DANDELION = resourceKey("wandering_trader/emerald_dandelion");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GOLDEN_DANDELION = resourceKey("wandering_trader/emerald_golden_dandelion");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_POPPY = resourceKey("wandering_trader/emerald_poppy");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BLUE_ORCHID = resourceKey("wandering_trader/emerald_blue_orchid");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ALLIUM = resourceKey("wandering_trader/emerald_allium");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_AZURE_BLUET = resourceKey("wandering_trader/emerald_azure_bluet");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_TULIP = resourceKey("wandering_trader/emerald_red_tulip");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ORANGE_TULIP = resourceKey("wandering_trader/emerald_orange_tulip");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WHITE_TULIP = resourceKey("wandering_trader/emerald_white_tulip");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PINK_TULIP = resourceKey("wandering_trader/emerald_pink_tulip");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OXEYE_DAISY = resourceKey("wandering_trader/emerald_oxeye_daisy");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CORNFLOWER = resourceKey("wandering_trader/emerald_cornflower");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LILY_OF_THE_VALLEY = resourceKey("wandering_trader/emerald_lily_of_the_valley");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OPEN_EYEBLOSSOM = resourceKey("wandering_trader/emerald_open_eyeblossom");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WHEAT_SEEDS = resourceKey("wandering_trader/emerald_wheat_seeds");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BEETROOT_SEEDS = resourceKey("wandering_trader/emerald_beetroot_seeds");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PUMPKIN_SEEDS = resourceKey("wandering_trader/emerald_pumpkin_seeds");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MELON_SEEDS = resourceKey("wandering_trader/emerald_melon_seeds");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ACACIA_SAPLING = resourceKey("wandering_trader/emerald_acacia_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BIRCH_SAPLING = resourceKey("wandering_trader/emerald_birch_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DARK_OAK_SAPLING = resourceKey("wandering_trader/emerald_dark_oak_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_JUNGLE_SAPLING = resourceKey("wandering_trader/emerald_jungle_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OAK_SAPLING = resourceKey("wandering_trader/emerald_oak_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SPRUCE_SAPLING = resourceKey("wandering_trader/emerald_spruce_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CHERRY_SAPLING = resourceKey("wandering_trader/emerald_cherry_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_OAK_SAPLING = resourceKey("wandering_trader/emerald_pale_oak_sapling");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MANGROVE_PROPAGULE = resourceKey("wandering_trader/emerald_mangrove_propagule");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_DYE = resourceKey("wandering_trader/emerald_red_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WHITE_DYE = resourceKey("wandering_trader/emerald_white_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BLUE_DYE = resourceKey("wandering_trader/emerald_blue_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PINK_DYE = resourceKey("wandering_trader/emerald_pink_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BLACK_DYE = resourceKey("wandering_trader/emerald_black_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GREEN_DYE = resourceKey("wandering_trader/emerald_green_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LIGHT_GRAY_DYE = resourceKey("wandering_trader/emerald_light_gray_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MAGENTA_DYE = resourceKey("wandering_trader/emerald_magenta_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_YELLOW_DYE = resourceKey("wandering_trader/emerald_yellow_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GRAY_DYE = resourceKey("wandering_trader/emerald_gray_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PURPLE_DYE = resourceKey("wandering_trader/emerald_purple_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LIGHT_BLUE_DYE = resourceKey("wandering_trader/emerald_light_blue_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LIME_DYE = resourceKey("wandering_trader/emerald_lime_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ORANGE_DYE = resourceKey("wandering_trader/emerald_orange_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BROWN_DYE = resourceKey("wandering_trader/emerald_brown_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CYAN_DYE = resourceKey("wandering_trader/emerald_cyan_dye");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BRAIN_CORAL_BLOCK = resourceKey("wandering_trader/emerald_brain_coral_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BUBBLE_CORAL_BLOCK = resourceKey("wandering_trader/emerald_bubble_coral_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_FIRE_CORAL_BLOCK = resourceKey("wandering_trader/emerald_fire_coral_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_HORN_CORAL_BLOCK = resourceKey("wandering_trader/emerald_horn_coral_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_TUBE_CORAL_BLOCK = resourceKey("wandering_trader/emerald_tube_coral_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_VINE = resourceKey("wandering_trader/emerald_vine");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_HANGING_MOSS = resourceKey("wandering_trader/emerald_pale_hanging_moss");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BROWN_MUSHROOM = resourceKey("wandering_trader/emerald_brown_mushroom");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_MUSHROOM = resourceKey("wandering_trader/emerald_red_mushroom");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LILY_PAD = resourceKey("wandering_trader/emerald_lily_pad");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SMALL_DRIPLEAF = resourceKey("wandering_trader/emerald_small_dripleaf");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SAND = resourceKey("wandering_trader/emerald_sand");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_SAND = resourceKey("wandering_trader/emerald_red_sand");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_POINTED_DRIPSTONE = resourceKey("wandering_trader/emerald_pointed_dripstone");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ROOTED_DIRT = resourceKey("wandering_trader/emerald_rooted_dirt");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MOSS_BLOCK = resourceKey("wandering_trader/emerald_moss_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_MOSS_BLOCK = resourceKey("wandering_trader/emerald_pale_moss_block");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WILDFLOWERS = resourceKey("wandering_trader/emerald_wildflowers");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DRY_TALL_GRASS = resourceKey("wandering_trader/emerald_dry_tall_grass");
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_FIREFLY_BUSH = resourceKey("wandering_trader/emerald_firefly_bush");

   public VillagerTrades() {
      super();
   }

   public static Holder<VillagerTrade> bootstrap(final BootstrapContext<VillagerTrade> context) {
      HolderGetter<Item> items = context.<Item>lookup(Registries.ITEM);
      Optional<HolderSet<Enchantment>> enchantmentsForTradedEquipment = context.lookup(Registries.ENCHANTMENT).get(EnchantmentTags.ON_TRADED_EQUIPMENT).map((named) -> named);
      Optional<HolderSet<Enchantment>> enchantmentsForBooks = context.lookup(Registries.ENCHANTMENT).get(EnchantmentTags.TRADEABLE).map((named) -> named);
      Optional<HolderSet<Enchantment>> doubleTradePrice = context.lookup(Registries.ENCHANTMENT).get(EnchantmentTags.DOUBLE_TRADE_PRICE).map((named) -> named);
      Optional<HolderSet<Potion>> potionsForTippedArrows = context.lookup(Registries.POTION).get(PotionTags.TRADEABLE).map((named) -> named);
      HolderGetter<VillagerType> villagerVariants = context.<VillagerType>lookup(Registries.VILLAGER_TYPE);
      register(context, FARMER_1_WHEAT_EMERALD, new VillagerTrade(new TradeCost(Items.WHEAT, 20), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_1_POTATO_EMERALD, new VillagerTrade(new TradeCost(Items.POTATO, 26), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_1_CARROT_EMERALD, new VillagerTrade(new TradeCost(Items.CARROT, 22), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_1_BEETROOT_EMERALD, new VillagerTrade(new TradeCost(Items.BEETROOT, 15), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_1_EMERALD_BREAD, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BREAD, 6), 16, 1, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_2_PUMPKIN_EMERALD, new VillagerTrade(new TradeCost(Items.PUMPKIN, 6), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_2_EMERALD_PUMPKIN_PIE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PUMPKIN_PIE, 4), 12, 5, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_2_EMERALD_APPLE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.APPLE, 4), 16, 5, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_3_EMERALD_COOKIE, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.COOKIE, 4), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_3_MELON_EMERALD, new VillagerTrade(new TradeCost(Items.MELON, 4), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_4_EMERALD_CAKE, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CAKE), 12, 15, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_4_EMERALD_SUSPICIOUS_STEW, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SUSPICIOUS_STEW), 12, 15, 0.05F, Optional.empty(), List.of((new SetStewEffectFunction.Builder()).withEffect(MobEffects.NIGHT_VISION, new ConstantValue(5.0F)).withEffect(MobEffects.JUMP_BOOST, new ConstantValue(8.0F)).withEffect(MobEffects.WEAKNESS, new ConstantValue(7.0F)).withEffect(MobEffects.BLINDNESS, new ConstantValue(6.0F)).withEffect(MobEffects.POISON, new ConstantValue(14.0F)).withEffect(MobEffects.SATURATION, new ConstantValue(7.0F)).build())));
      register(context, FARMER_5_EMERALD_GOLDEN_CARROT, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.GOLDEN_CARROT, 3), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, FARMER_5_EMERALD_GLISTENING_MELON_SLICE, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.GLISTERING_MELON_SLICE, 3), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_1_STRING_EMERALD, new VillagerTrade(new TradeCost(Items.STRING, 20), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_1_COAL_EMERALD, new VillagerTrade(new TradeCost(Items.COAL, 10), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_1_RAW_COD_AND_EMERALD_COOKED_COD, new VillagerTrade(new TradeCost(Items.COD, 6), Optional.of(new TradeCost(Items.EMERALD, 1)), new ItemStackTemplate(Items.COOKED_COD, 6), 16, 1, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_1_EMERALD_COD_BUCKET, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.COD_BUCKET), 16, 1, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_2_COD_EMERALD, new VillagerTrade(new TradeCost(Items.COD, 15), new ItemStackTemplate(Items.EMERALD), 16, 10, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_2_SALMON_AND_EMERALD_COOKED_SALMON, new VillagerTrade(new TradeCost(Items.SALMON, 6), Optional.of(new TradeCost(Items.EMERALD, 1)), new ItemStackTemplate(Items.COOKED_SALMON, 6), 16, 5, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_2_EMERALD_CAMPFIRE, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.CAMPFIRE), 16, 5, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_3_SALMON_EMERALD, new VillagerTrade(new TradeCost(Items.SALMON, 13), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_3_EMERALD_ENCHANTED_FISHING_ROD, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.FISHING_ROD), 3, 10, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.FISHING_ROD)));
      register(context, FISHERMAN_4_TROPICAL_FISH_EMERALD, new VillagerTrade(new TradeCost(Items.TROPICAL_FISH, 6), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, FISHERMAN_5_PUFFERFISH_EMERALD, new VillagerTrade(new TradeCost(Items.PUFFERFISH, 4), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      registerBoatTrades(context, villagerVariants);
      registerWoolSales(context);
      register(context, SHEPHERD_1_EMERALD_SHEARS, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.SHEARS), 12, 1, 0.05F, Optional.empty(), List.of()));
      registerLevelTwoDyeTrades(context);
      registerWoolPurchases(context);
      registerCarpetPurchases(context);
      registerLevelThreeDyeTrades(context);
      registerBedTrades(context);
      registerLevelFourDyeTrades(context);
      registerShepherdBannerTrades(context);
      register(context, SHEPHERD_5_EMERALD_PAINTING, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.PAINTING, 3), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_1_STICK_EMERALD, new VillagerTrade(new TradeCost(Items.STICK, 32), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_1_EMERALD_ARROW, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.ARROW, 16), 16, 1, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_1_GRAVEL_AND_EMERALD_ARROW, new VillagerTrade(new TradeCost(Items.GRAVEL, 10), Optional.of(new TradeCost(Items.EMERALD, 1)), new ItemStackTemplate(Items.FLINT, 10), 12, 1, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_2_FLINT_EMERALD, new VillagerTrade(new TradeCost(Items.FLINT, 26), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_2_EMERALD_BOW, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.BOW), 12, 5, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_3_STRING_EMERALD, new VillagerTrade(new TradeCost(Items.STRING, 14), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_3_EMERALD_CROSSBOW, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CROSSBOW), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_4_FEATHER_EMERALD, new VillagerTrade(new TradeCost(Items.FEATHER, 24), new ItemStackTemplate(Items.EMERALD), 16, 30, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_4_EMERALD_ENCHANTED_BOW, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.BOW), 3, 15, 0.05F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.BOW)));
      register(context, FLETCHER_5_TRIPWIRE_HOOK_EMERALD, new VillagerTrade(new TradeCost(Items.TRIPWIRE_HOOK, 8), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, FLETCHER_5_EMERALD_ENCHANTED_CROSSBOW, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CROSSBOW), 3, 15, 0.05F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.CROSSBOW)));
      register(context, FLETCHER_5_ARROW_AND_EMERALD_TIPPED_ARROW, new VillagerTrade(new TradeCost(Items.ARROW, 5), Optional.of(new TradeCost(Items.EMERALD, 2)), new ItemStackTemplate(Items.TIPPED_ARROW, 5), 12, 30, 0.05F, Optional.empty(), List.of(SetRandomPotionFunction.fromTagKey(potionsForTippedArrows).build())));
      register(context, LIBRARIAN_1_PAPER_EMERALD, new VillagerTrade(new TradeCost(Items.PAPER, 24), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_1_EMERALD_AND_BOOK_ENCHANTED_BOOK, new VillagerTrade(new TradeCost(Items.EMERALD, 0), Optional.of(new TradeCost(Items.BOOK, 1)), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 1, 0.2F, Optional.empty(), enchantedBook(items, enchantmentsForBooks), doubleTradePrice));
      register(context, LIBRARIAN_1_EMERALD_BOOKSHELF, new VillagerTrade(new TradeCost(Items.EMERALD, 9), new ItemStackTemplate(Items.BOOKSHELF), 12, 1, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_2_BOOK_EMERALD, new VillagerTrade(new TradeCost(Items.BOOK, 4), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_2_EMERALD_AND_BOOK_ENCHANTED_BOOK, new VillagerTrade(new TradeCost(Items.EMERALD, 0), Optional.of(new TradeCost(Items.BOOK, 1)), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 5, 0.2F, Optional.empty(), enchantedBook(items, enchantmentsForBooks), doubleTradePrice));
      register(context, LIBRARIAN_2_EMERALD_LANTERN, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LANTERN), 12, 5, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_3_INK_SAC_EMERALD, new VillagerTrade(new TradeCost(Items.INK_SAC, 5), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_3_EMERALD_AND_BOOK_ENCHANTED_BOOK, new VillagerTrade(new TradeCost(Items.EMERALD, 0), Optional.of(new TradeCost(Items.BOOK, 1)), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 10, 0.2F, Optional.empty(), enchantedBook(items, enchantmentsForBooks), doubleTradePrice));
      register(context, LIBRARIAN_3_EMERALD_GLASS, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.GLASS, 4), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_4_WRITABLE_BOOK_EMERALD, new VillagerTrade(new TradeCost(Items.WRITABLE_BOOK, 2), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_4_EMERALD_AND_BOOK_ENCHANTED_BOOK, new VillagerTrade(new TradeCost(Items.EMERALD, 0), Optional.of(new TradeCost(Items.BOOK, 1)), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 15, 0.2F, Optional.empty(), enchantedBook(items, enchantmentsForBooks), doubleTradePrice));
      register(context, LIBRARIAN_4_EMERALD_CLOCK, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.CLOCK), 12, 15, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_4_EMERALD_COMPASS, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.COMPASS), 12, 15, 0.05F, Optional.empty(), List.of()));
      register(context, LIBRARIAN_5_EMERALD_NAME_TAG, new VillagerTrade(new TradeCost(Items.EMERALD, 20), new ItemStackTemplate(Items.NAME_TAG), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, CARTOGRAPHER_1_PAPER_EMERALD, new VillagerTrade(new TradeCost(Items.PAPER, 24), new ItemStackTemplate(Items.EMERALD), 12, 2, 0.05F, Optional.empty(), List.of()));
      register(context, CARTOGRAPHER_1_EMERALD_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.MAP), 12, 1, 0.05F, Optional.empty(), List.of()));
      register(context, CARTOGRAPHER_2_GLASS_PANE_EMERALD, new VillagerTrade(new TradeCost(Items.GLASS_PANE, 11), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      registerBasicExplorerMapTrades(context, items, villagerVariants);
      register(context, CARTOGRAPHER_3_COMPASS_EMERALD, new VillagerTrade(new TradeCost(Items.COMPASS, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, CARTOGRAPHER_3_EMERALD_AND_COMPASS_OCEAN_EXPLORER_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 13), Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12, 10, 0.05F, Optional.empty(), List.of(ExplorationMapFunction.makeExplorationMap().setDestination(StructureTags.ON_OCEAN_EXPLORER_MAPS).setMapDecoration(MapDecorationTypes.OCEAN_MONUMENT).setSearchRadius(100).setSkipKnownStructures(true).build(), SetNameFunction.setName(Component.translatable("filled_map.monument"), SetNameFunction.Target.ITEM_NAME).build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, Items.FILLED_MAP).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));
      register(context, CARTOGRAPHER_3_EMERALD_AND_COMPASS_TRIAL_CHAMBER_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 12), Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12, 10, 0.05F, Optional.empty(), List.of(ExplorationMapFunction.makeExplorationMap().setDestination(StructureTags.ON_TRIAL_CHAMBERS_MAPS).setMapDecoration(MapDecorationTypes.TRIAL_CHAMBERS).setSearchRadius(100).setSkipKnownStructures(true).build(), SetNameFunction.setName(Component.translatable("filled_map.trial_chambers"), SetNameFunction.Target.ITEM_NAME).build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, Items.FILLED_MAP).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));
      register(context, CARTOGRAPHER_4_EMERALD_ITEM_FRAME, new VillagerTrade(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.ITEM_FRAME), 12, 25, 0.05F, Optional.empty(), List.of()));
      registerCartographerBannerTrades(context, villagerVariants);
      register(context, CARTOGRAPHER_5_EMERALD_GLOBE_BANNER_PATTERN, new VillagerTrade(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.GLOBE_BANNER_PATTERN), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, CARTOGRAPHER_5_EMERALD_AND_COMPASS_WOODLAND_MANSION_MAP, new VillagerTrade(new TradeCost(Items.EMERALD, 14), Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12, 30, 0.05F, Optional.empty(), List.of(ExplorationMapFunction.makeExplorationMap().setDestination(StructureTags.ON_WOODLAND_EXPLORER_MAPS).setMapDecoration(MapDecorationTypes.WOODLAND_MANSION).setSearchRadius(100).setSkipKnownStructures(true).build(), SetNameFunction.setName(Component.translatable("filled_map.mansion"), SetNameFunction.Target.ITEM_NAME).build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, Items.FILLED_MAP).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));
      register(context, CLERIC_1_ROTTEN_FLESH_EMERALD, new VillagerTrade(new TradeCost(Items.ROTTEN_FLESH, 32), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_1_EMERALD_REDSTONE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.REDSTONE, 2), 12, 1, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_2_GOLD_INGOT_EMERALD, new VillagerTrade(new TradeCost(Items.GOLD_INGOT, 3), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_2_EMERALD_LAPIS_LAZULI, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LAPIS_LAZULI), 12, 5, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_3_RABBIT_FOOT_EMERALD, new VillagerTrade(new TradeCost(Items.RABBIT_FOOT, 2), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_3_EMERALD_GLOWSTONE, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.GLOWSTONE), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_4_TURTLE_SCUTE_EMERALD, new VillagerTrade(new TradeCost(Items.TURTLE_SCUTE, 4), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_4_GLASS_BOTTLE_EMERALD, new VillagerTrade(new TradeCost(Items.GLASS_BOTTLE, 9), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_4_EMERALD_ENDER_PEARL, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.ENDER_PEARL), 12, 15, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_5_NETHER_WART_EMERALD, new VillagerTrade(new TradeCost(Items.NETHER_WART, 22), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, CLERIC_5_EMERALD_EXPERIENCE_BOTTLE, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.EXPERIENCE_BOTTLE), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, COMMON_SMITH_1_COAL_EMERALD, new VillagerTrade(new TradeCost(Items.COAL, 15), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, COMMON_SMITH_2_IRON_INGOT_EMERALD, new VillagerTrade(new TradeCost(Items.IRON_INGOT, 4), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, COMMON_SMITH_2_EMERALD_BELL, new VillagerTrade(new TradeCost(Items.EMERALD, 36), new ItemStackTemplate(Items.BELL), 12, 5, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_1_EMERALD_IRON_LEGGINGS, new VillagerTrade(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.IRON_LEGGINGS), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_1_EMERALD_IRON_BOOTS, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.IRON_BOOTS), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_1_EMERALD_IRON_HELMET, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.IRON_HELMET), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_1_EMERALD_IRON_CHESTPLATE, new VillagerTrade(new TradeCost(Items.EMERALD, 9), new ItemStackTemplate(Items.IRON_CHESTPLATE), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_2_EMERALD_CHAINMAIL_BOOTS, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.CHAINMAIL_BOOTS), 12, 5, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_2_EMERALD_CHAINMAIL_LEGGINGS, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CHAINMAIL_LEGGINGS), 12, 5, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_3_LAVA_BUCKET_EMERALD, new VillagerTrade(new TradeCost(Items.LAVA_BUCKET, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, ARMORER_3_EMERALD_CHAINMAIL_HELMET, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.CHAINMAIL_HELMET), 12, 10, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_3_EMERALD_CHAINMAIL_CHESTPLATE, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.CHAINMAIL_CHESTPLATE), 12, 10, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_3_EMERALD_SHIELD, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.SHIELD), 12, 10, 0.2F, Optional.empty(), List.of()));
      register(context, ARMORER_3_DIAMOND_EMERALD, new VillagerTrade(new TradeCost(Items.DIAMOND, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, ARMORER_4_EMERALD_ENCHANTED_DIAMOND_LEGGINGS, new VillagerTrade(new TradeCost(Items.EMERALD, 14), new ItemStackTemplate(Items.DIAMOND_LEGGINGS), 3, 15, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_LEGGINGS)));
      register(context, ARMORER_4_EMERALD_ENCHANTED_DIAMOND_BOOTS, new VillagerTrade(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.DIAMOND_BOOTS), 3, 15, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_BOOTS)));
      register(context, ARMORER_5_EMERALD_ENCHANTED_DIAMOND_HELMET, new VillagerTrade(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.DIAMOND_HELMET), 3, 30, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_HELMET)));
      register(context, ARMORER_5_EMERALD_ENCHANTED_DIAMOND_CHESTPLATE, new VillagerTrade(new TradeCost(Items.EMERALD, 16), new ItemStackTemplate(Items.DIAMOND_CHESTPLATE), 3, 30, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_CHESTPLATE)));
      register(context, WEAPONSMITH_1_EMERALD_IRON_AXE, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.IRON_AXE), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, WEAPONSMITH_1_EMERALD_ENCHANTED_IRON_SWORD, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.IRON_SWORD), 12, 1, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_SWORD)));
      register(context, WEAPONSMITH_3_FLINT_EMERALD, new VillagerTrade(new TradeCost(Items.FLINT, 24), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, WEAPONSMITH_4_EMERALD_ENCHANTED_DIAMOND_AXE, new VillagerTrade(new TradeCost(Items.EMERALD, 12), new ItemStackTemplate(Items.DIAMOND_AXE), 3, 15, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_AXE)));
      register(context, WEAPONSMITH_4_DIAMOND_EMERALD, new VillagerTrade(new TradeCost(Items.DIAMOND, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, WEAPONSMITH_5_EMERALD_ENCHANTED_DIAMOND_SWORD, new VillagerTrade(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.DIAMOND_SWORD), 3, 30, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_SWORD)));
      register(context, TOOLSMITH_1_EMERALD_STONE_AXE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_AXE), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_1_EMERALD_STONE_SHOVEL, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_SHOVEL), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_1_EMERALD_STONE_PICKAXE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_PICKAXE), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_1_EMERALD_STONE_HOE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_HOE), 12, 1, 0.2F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_3_FLINT_EMERALD, new VillagerTrade(new TradeCost(Items.FLINT, 30), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_3_EMERALD_IRON_AXE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.IRON_AXE), 3, 10, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_AXE)));
      register(context, TOOLSMITH_3_EMERALD_IRON_SHOVEL, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.IRON_SHOVEL), 3, 10, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_SHOVEL)));
      register(context, TOOLSMITH_3_EMERALD_IRON_PICKAXE, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.IRON_PICKAXE), 3, 10, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_PICKAXE)));
      register(context, TOOLSMITH_3_EMERALD_DIAMOND_HOE, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.DIAMOND_HOE), 3, 10, 0.2F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_4_EMERALD_DIAMOND_AXE, new VillagerTrade(new TradeCost(Items.EMERALD, 12), new ItemStackTemplate(Items.DIAMOND_AXE), 3, 15, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_AXE)));
      register(context, TOOLSMITH_4_EMERALD_DIAMOND_SHOVEL, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.DIAMOND_SHOVEL), 3, 15, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_SHOVEL)));
      register(context, TOOLSMITH_4_DIAMOND_EMERALD, new VillagerTrade(new TradeCost(Items.DIAMOND, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, TOOLSMITH_5_EMERALD_DIAMOND_PICKAXE, new VillagerTrade(new TradeCost(Items.EMERALD, 13), new ItemStackTemplate(Items.DIAMOND_PICKAXE), 3, 30, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_PICKAXE)));
      register(context, BUTCHER_1_CHICKEN_EMERALD, new VillagerTrade(new TradeCost(Items.CHICKEN, 14), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_1_PORKCHOP_EMERALD, new VillagerTrade(new TradeCost(Items.PORKCHOP, 7), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_1_RABBIT_EMERALD, new VillagerTrade(new TradeCost(Items.RABBIT, 4), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_1_EMERALD_RABBIT_STEW, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.RABBIT_STEW), 16, 1, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_2_COAL_EMERALD, new VillagerTrade(new TradeCost(Items.COAL, 15), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_2_EMERALD_COOKED_PORKCHOP, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.COOKED_PORKCHOP, 5), 16, 5, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_2_EMERALD_COOKED_CHICKEN, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.COOKED_CHICKEN, 8), 16, 5, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_3_MUTTON_EMERALD, new VillagerTrade(new TradeCost(Items.MUTTON, 7), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_3_BEEF_EMERALD, new VillagerTrade(new TradeCost(Items.BEEF, 10), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_4_DRIED_KELP_BLOCK_EMERALD, new VillagerTrade(new TradeCost(Items.DRIED_KELP_BLOCK, 10), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, BUTCHER_5_SWEET_BERRIES_EMERALD, new VillagerTrade(new TradeCost(Items.SWEET_BERRIES, 10), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, LEATHERWORKER_1_LEATHER_EMERALD, new VillagerTrade(new TradeCost(Items.LEATHER, 6), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, LEATHERWORKER_1_EMERALD_DYED_LEATHER_LEGGINGS, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.LEATHER_LEGGINGS), 12, 1, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_LEGGINGS)));
      register(context, LEATHERWORKER_1_EMERALD_DYED_LEATHER_CHESTPLATE, new VillagerTrade(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.LEATHER_CHESTPLATE), 12, 1, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_CHESTPLATE)));
      register(context, LEATHERWORKER_2_FLINT_EMERALD, new VillagerTrade(new TradeCost(Items.FLINT, 26), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F, Optional.empty(), List.of()));
      register(context, LEATHERWORKER_2_EMERALD_DYED_LEATHER_HELMET, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.LEATHER_HELMET), 12, 5, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_HELMET)));
      register(context, LEATHERWORKER_2_EMERALD_DYED_LEATHER_BOOTS, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.LEATHER_BOOTS), 12, 5, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_BOOTS)));
      register(context, LEATHERWORKER_3_RABBIT_HIDE_EMERALD, new VillagerTrade(new TradeCost(Items.RABBIT_HIDE, 9), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F, Optional.empty(), List.of()));
      register(context, LEATHERWORKER_3_EMERALD_DYED_LEATHER_CHESTPLATE, new VillagerTrade(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.LEATHER_CHESTPLATE), 12, 1, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_CHESTPLATE)));
      register(context, LEATHERWORKER_4_TURTLE_SCUTE_EMERALD, new VillagerTrade(new TradeCost(Items.TURTLE_SCUTE, 4), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, LEATHERWORKER_4_EMERALD_DYED_LEATHER_HORSE_ARMOR, new VillagerTrade(new TradeCost(Items.EMERALD, 6), new ItemStackTemplate(Items.LEATHER_HORSE_ARMOR), 12, 15, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_HORSE_ARMOR)));
      register(context, LEATHERWORKER_5_EMERALD_SADDLE, new VillagerTrade(new TradeCost(Items.EMERALD, 6), new ItemStackTemplate(Items.SADDLE), 12, 30, 0.2F, Optional.empty(), List.of()));
      register(context, LEATHERWORKER_5_EMERALD_DYED_LEATHER_HELMET, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.LEATHER_HELMET), 12, 5, 0.05F, Optional.empty(), dyedItem(items, Items.LEATHER_HELMET)));
      register(context, MASON_1_CLAY_BALL_EMERALD, new VillagerTrade(new TradeCost(Items.CLAY_BALL, 10), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      register(context, MASON_1_EMERALD_BRICK, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BRICK, 10), 16, 1, 0.05F, Optional.empty(), List.of()));
      register(context, MASON_2_STONE_EMERALD, new VillagerTrade(new TradeCost(Items.STONE, 20), new ItemStackTemplate(Items.EMERALD), 16, 10, 0.05F, Optional.empty(), List.of()));
      register(context, MASON_2_EMERALD_CHISELED_STONE_BRICKS, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.CHISELED_STONE_BRICKS, 4), 16, 5, 0.05F, Optional.empty(), List.of()));
      registerMasonLevelThreeStones(context);
      registerMasonLevelThreeBlocks(context);
      register(context, MASON_4_QUARTZ_EMERALD, new VillagerTrade(new TradeCost(Items.QUARTZ, 12), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      registerMasonLevelFourTerracotta(context);
      register(context, MASON_5_EMERALD_QUARTZ_PILLAR, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.QUARTZ_PILLAR), 12, 30, 0.05F, Optional.empty(), List.of()));
      register(context, MASON_5_EMERALD_QUARTZ_BLOCK, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.QUARTZ_BLOCK), 12, 30, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_WATER_BOTTLE_EMERALD, new VillagerTrade(new TradeCost(Items.POTION.builtInRegistryHolder(), ConstantValue.exactly(1.0F), DataComponentExactPredicate.expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER))), new ItemStackTemplate(Items.EMERALD), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_WATER_BUCKET_EMERALD, new VillagerTrade(new TradeCost(Items.WATER_BUCKET, 1), new ItemStackTemplate(Items.EMERALD, 2), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_MILK_BUCKET_EMERALD, new VillagerTrade(new TradeCost(Items.MILK_BUCKET, 1), new ItemStackTemplate(Items.EMERALD, 2), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_FERMENTED_SPIDER_EYE_EMERALD, new VillagerTrade(new TradeCost(Items.FERMENTED_SPIDER_EYE, 1), new ItemStackTemplate(Items.EMERALD, 3), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_BAKED_POTATO_EMERALD, new VillagerTrade(new TradeCost(Items.BAKED_POTATO, 4), new ItemStackTemplate(Items.EMERALD, 3), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_HAY_BLOCK_EMERALD, new VillagerTrade(new TradeCost(Items.HAY_BLOCK, 1), new ItemStackTemplate(Items.EMERALD), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_PACKED_ICE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PACKED_ICE), 6, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_BLUE_ICE, new VillagerTrade(new TradeCost(Items.EMERALD, 6), new ItemStackTemplate(Items.BLUE_ICE), 6, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_GUNPOWDER, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.GUNPOWDER, 4), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_PODZOL, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.PODZOL, 3), 6, 1, 0.05F, Optional.empty(), List.of()));

      for(Pair<ResourceKey<VillagerTrade>, Item> pair : List.of(Pair.of(WANDERING_TRADER_EMERALD_ACACIA_LOG, Items.ACACIA_LOG), Pair.of(WANDERING_TRADER_EMERALD_BIRCH_LOG, Items.BIRCH_LOG), Pair.of(WANDERING_TRADER_EMERALD_DARK_OAK_LOG, Items.DARK_OAK_LOG), Pair.of(WANDERING_TRADER_EMERALD_JUNGLE_LOG, Items.JUNGLE_LOG), Pair.of(WANDERING_TRADER_EMERALD_OAK_LOG, Items.OAK_LOG), Pair.of(WANDERING_TRADER_EMERALD_SPRUCE_LOG, Items.SPRUCE_LOG), Pair.of(WANDERING_TRADER_EMERALD_CHERRY_LOG, Items.CHERRY_LOG), Pair.of(WANDERING_TRADER_EMERALD_MANGROVE_LOG, Items.MANGROVE_LOG), Pair.of(WANDERING_TRADER_EMERALD_PALE_OAK_LOG, Items.PALE_OAK_LOG))) {
         context.register((ResourceKey)pair.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)pair.getRight(), 8), 4, 1, 0.05F, Optional.empty(), List.of()));
      }

      context.register(WANDERING_TRADER_EMERALD_ENCHANTED_IRON_PICKAXE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.IRON_PICKAXE), 1, 1, 0.2F, Optional.empty(), enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_PICKAXE)));
      context.register(WANDERING_TRADER_EMERALD_LONG_INVISIBILITY_POTION, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.POTION), 1, 1, 0.05F, Optional.empty(), List.of(SetPotionFunction.setPotion(Potions.LONG_INVISIBILITY).build())));
      context.register(WANDERING_TRADER_EMERALD_TROPICAL_FISH_BUCKET, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.TROPICAL_FISH_BUCKET), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_PUFFERFISH_BUCKET, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.PUFFERFISH_BUCKET), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_SEA_PICKLE, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.SEA_PICKLE), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_SLIME_BALL, new VillagerTrade(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.SLIME_BALL), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_GLOWSTONE, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.GLOWSTONE), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_NAUTILUS_SHELL, new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.NAUTILUS_SHELL), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_FERN, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.FERN), 12, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_SUGAR_CANE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SUGAR_CANE), 8, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_PUMPKIN, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PUMPKIN), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_KELP, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.KELP), 12, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_CACTUS, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CACTUS), 8, 1, 0.05F, Optional.empty(), List.of()));
      registerWanderingTraderFlowers(context);
      registerWanderingTraderSeeds(context);
      registerWanderingTraderSaplings(context);
      registerWanderingTraderDyes(context);

      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(WANDERING_TRADER_EMERALD_BRAIN_CORAL_BLOCK, Items.BRAIN_CORAL_BLOCK), Pair.of(WANDERING_TRADER_EMERALD_BUBBLE_CORAL_BLOCK, Items.BUBBLE_CORAL_BLOCK), Pair.of(WANDERING_TRADER_EMERALD_FIRE_CORAL_BLOCK, Items.FIRE_CORAL_BLOCK), Pair.of(WANDERING_TRADER_EMERALD_HORN_CORAL_BLOCK, Items.HORN_CORAL_BLOCK), Pair.of(WANDERING_TRADER_EMERALD_TUBE_CORAL_BLOCK, Items.TUBE_CORAL_BLOCK))) {
         context.register((ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate((Item)entry.getRight()), 8, 1, 0.05F, Optional.empty(), List.of()));
      }

      context.register(WANDERING_TRADER_EMERALD_VINE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.VINE, 3), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_PALE_HANGING_MOSS, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PALE_HANGING_MOSS, 3), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_BROWN_MUSHROOM, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BROWN_MUSHROOM, 3), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_RED_MUSHROOM, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.RED_MUSHROOM, 3), 4, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_LILY_PAD, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LILY_PAD, 5), 2, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_SMALL_DRIPLEAF, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SMALL_DRIPLEAF, 2), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_SAND, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SAND, 8), 8, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_RED_SAND, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.RED_SAND, 4), 6, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_POINTED_DRIPSTONE, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.POINTED_DRIPSTONE, 2), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_ROOTED_DIRT, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.ROOTED_DIRT, 2), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_MOSS_BLOCK, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.MOSS_BLOCK, 2), 5, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_PALE_MOSS_BLOCK, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PALE_MOSS_BLOCK, 2), 5, 1, 0.05F, Optional.empty(), List.of()));
      return context.register(WANDERING_TRADER_EMERALD_FIREFLY_BUSH, new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.FIREFLY_BUSH), 12, 1, 0.05F, Optional.empty(), List.of()));
   }

   private static void registerWanderingTraderFlowers(final BootstrapContext<VillagerTrade> context) {
      context.register(WANDERING_TRADER_EMERALD_BLUE_ORCHID, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BLUE_ORCHID), 8, 1, 0.05F, Optional.empty(), List.of()));

      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(WANDERING_TRADER_EMERALD_DANDELION, Items.DANDELION), Pair.of(WANDERING_TRADER_EMERALD_POPPY, Items.POPPY), Pair.of(WANDERING_TRADER_EMERALD_ALLIUM, Items.ALLIUM), Pair.of(WANDERING_TRADER_EMERALD_AZURE_BLUET, Items.AZURE_BLUET), Pair.of(WANDERING_TRADER_EMERALD_RED_TULIP, Items.RED_TULIP), Pair.of(WANDERING_TRADER_EMERALD_ORANGE_TULIP, Items.ORANGE_TULIP), Pair.of(WANDERING_TRADER_EMERALD_WHITE_TULIP, Items.WHITE_TULIP), Pair.of(WANDERING_TRADER_EMERALD_PINK_TULIP, Items.PINK_TULIP), Pair.of(WANDERING_TRADER_EMERALD_OXEYE_DAISY, Items.OXEYE_DAISY), Pair.of(WANDERING_TRADER_EMERALD_CORNFLOWER, Items.CORNFLOWER), Pair.of(WANDERING_TRADER_EMERALD_WILDFLOWERS, Items.WILDFLOWERS), Pair.of(WANDERING_TRADER_EMERALD_DRY_TALL_GRASS, Items.DRY_TALL_GRASS))) {
         context.register((ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)entry.getRight()), 12, 1, 0.05F, Optional.empty(), List.of()));
      }

      context.register(WANDERING_TRADER_EMERALD_GOLDEN_DANDELION, new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.GOLDEN_DANDELION), 12, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_LILY_OF_THE_VALLEY, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LILY_OF_THE_VALLEY), 7, 1, 0.05F, Optional.empty(), List.of()));
      context.register(WANDERING_TRADER_EMERALD_OPEN_EYEBLOSSOM, new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.OPEN_EYEBLOSSOM), 7, 1, 0.05F, Optional.empty(), List.of()));
   }

   private static void registerWanderingTraderSeeds(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(WANDERING_TRADER_EMERALD_WHEAT_SEEDS, Items.WHEAT_SEEDS), Pair.of(WANDERING_TRADER_EMERALD_BEETROOT_SEEDS, Items.BEETROOT_SEEDS), Pair.of(WANDERING_TRADER_EMERALD_PUMPKIN_SEEDS, Items.PUMPKIN_SEEDS), Pair.of(WANDERING_TRADER_EMERALD_MELON_SEEDS, Items.MELON_SEEDS))) {
         context.register((ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)entry.getRight()), 12, 1, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerWanderingTraderSaplings(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(WANDERING_TRADER_EMERALD_ACACIA_SAPLING, Items.ACACIA_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_BIRCH_SAPLING, Items.BIRCH_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_DARK_OAK_SAPLING, Items.DARK_OAK_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_JUNGLE_SAPLING, Items.JUNGLE_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_OAK_SAPLING, Items.OAK_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_SPRUCE_SAPLING, Items.SPRUCE_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_CHERRY_SAPLING, Items.CHERRY_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_PALE_OAK_SAPLING, Items.PALE_OAK_SAPLING), Pair.of(WANDERING_TRADER_EMERALD_MANGROVE_PROPAGULE, Items.MANGROVE_PROPAGULE))) {
         context.register((ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate((Item)entry.getRight()), 8, 1, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerWanderingTraderDyes(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(WANDERING_TRADER_EMERALD_RED_DYE, Items.RED_DYE), Pair.of(WANDERING_TRADER_EMERALD_WHITE_DYE, Items.WHITE_DYE), Pair.of(WANDERING_TRADER_EMERALD_BLUE_DYE, Items.BLUE_DYE), Pair.of(WANDERING_TRADER_EMERALD_PINK_DYE, Items.PINK_DYE), Pair.of(WANDERING_TRADER_EMERALD_BLACK_DYE, Items.BLACK_DYE), Pair.of(WANDERING_TRADER_EMERALD_GREEN_DYE, Items.GREEN_DYE), Pair.of(WANDERING_TRADER_EMERALD_LIGHT_GRAY_DYE, Items.LIGHT_GRAY_DYE), Pair.of(WANDERING_TRADER_EMERALD_MAGENTA_DYE, Items.MAGENTA_DYE), Pair.of(WANDERING_TRADER_EMERALD_YELLOW_DYE, Items.YELLOW_DYE), Pair.of(WANDERING_TRADER_EMERALD_GRAY_DYE, Items.GRAY_DYE), Pair.of(WANDERING_TRADER_EMERALD_PURPLE_DYE, Items.PURPLE_DYE), Pair.of(WANDERING_TRADER_EMERALD_LIGHT_BLUE_DYE, Items.LIGHT_BLUE_DYE), Pair.of(WANDERING_TRADER_EMERALD_LIME_DYE, Items.LIME_DYE), Pair.of(WANDERING_TRADER_EMERALD_ORANGE_DYE, Items.ORANGE_DYE), Pair.of(WANDERING_TRADER_EMERALD_BROWN_DYE, Items.BROWN_DYE), Pair.of(WANDERING_TRADER_EMERALD_CYAN_DYE, Items.CYAN_DYE))) {
         context.register((ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)entry.getRight(), 3), 12, 1, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerMasonLevelFourTerracotta(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> block : List.of(Pair.of(MASON_4_EMERALD_ORANGE_TERRACOTTA, Items.ORANGE_TERRACOTTA), Pair.of(MASON_4_EMERALD_WHITE_TERRACOTTA, Items.WHITE_TERRACOTTA), Pair.of(MASON_4_EMERALD_BLUE_TERRACOTTA, Items.BLUE_TERRACOTTA), Pair.of(MASON_4_EMERALD_LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_TERRACOTTA), Pair.of(MASON_4_EMERALD_GRAY_TERRACOTTA, Items.GRAY_TERRACOTTA), Pair.of(MASON_4_EMERALD_LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_TERRACOTTA), Pair.of(MASON_4_EMERALD_BLACK_TERRACOTTA, Items.BLACK_TERRACOTTA), Pair.of(MASON_4_EMERALD_RED_TERRACOTTA, Items.RED_TERRACOTTA), Pair.of(MASON_4_EMERALD_PINK_TERRACOTTA, Items.PINK_TERRACOTTA), Pair.of(MASON_4_EMERALD_MAGENTA_TERRACOTTA, Items.MAGENTA_TERRACOTTA), Pair.of(MASON_4_EMERALD_LIME_TERRACOTTA, Items.LIME_TERRACOTTA), Pair.of(MASON_4_EMERALD_GREEN_TERRACOTTA, Items.GREEN_TERRACOTTA), Pair.of(MASON_4_EMERALD_CYAN_TERRACOTTA, Items.CYAN_TERRACOTTA), Pair.of(MASON_4_EMERALD_PURPLE_TERRACOTTA, Items.PURPLE_TERRACOTTA), Pair.of(MASON_4_EMERALD_YELLOW_TERRACOTTA, Items.YELLOW_TERRACOTTA), Pair.of(MASON_4_EMERALD_BROWN_TERRACOTTA, Items.BROWN_TERRACOTTA), Pair.of(MASON_4_EMERALD_ORANGE_GLAZED_TERRACOTTA, Items.ORANGE_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_WHITE_GLAZED_TERRACOTTA, Items.WHITE_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_BLUE_GLAZED_TERRACOTTA, Items.BLUE_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_LIGHT_BLUE_GLAZED_TERRACOTTA, Items.LIGHT_BLUE_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_GRAY_GLAZED_TERRACOTTA, Items.GRAY_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_LIGHT_GRAY_GLAZED_TERRACOTTA, Items.LIGHT_GRAY_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_BLACK_GLAZED_TERRACOTTA, Items.BLACK_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_RED_GLAZED_TERRACOTTA, Items.RED_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_PINK_GLAZED_TERRACOTTA, Items.PINK_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_MAGENTA_GLAZED_TERRACOTTA, Items.MAGENTA_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_LIME_GLAZED_TERRACOTTA, Items.LIME_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_GREEN_GLAZED_TERRACOTTA, Items.GREEN_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_CYAN_GLAZED_TERRACOTTA, Items.CYAN_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_PURPLE_GLAZED_TERRACOTTA, Items.PURPLE_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_YELLOW_GLAZED_TERRACOTTA, Items.YELLOW_GLAZED_TERRACOTTA), Pair.of(MASON_4_EMERALD_BROWN_GLAZED_TERRACOTTA, Items.BROWN_GLAZED_TERRACOTTA))) {
         register(context, (ResourceKey)block.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)block.getRight()), 12, 15, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerMasonLevelThreeBlocks(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> block : List.of(Pair.of(MASON_3_EMERALD_DRIPSTONE_BLOCK, Items.DRIPSTONE_BLOCK), Pair.of(MASON_3_EMERALD_POLISHED_ANDESITE, Items.POLISHED_ANDESITE), Pair.of(MASON_3_EMERALD_POLISHED_DIORITE, Items.POLISHED_DIORITE), Pair.of(MASON_3_EMERALD_POLISHED_GRANTITE, Items.POLISHED_GRANITE))) {
         register(context, (ResourceKey)block.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)block.getRight(), 4), 16, 10, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerMasonLevelThreeStones(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> stone : List.of(Pair.of(MASON_3_GRANITE_EMERALD, Items.GRANITE), Pair.of(MASON_3_ANDESITE_EMERALD, Items.ANDESITE), Pair.of(MASON_3_DIORITE_EMERALD, Items.DIORITE))) {
         register(context, (ResourceKey)stone.getLeft(), new VillagerTrade(new TradeCost((ItemLike)stone.getRight(), 16), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerBoatTrades(final BootstrapContext<VillagerTrade> context, final HolderGetter<VillagerType> villagerVariants) {
      for(ImmutableTriple<ResourceKey<VillagerTrade>, Item, List<ResourceKey<VillagerType>>> entry : List.of(ImmutableTriple.of(FISHERMAN_5_OAK_BOAT_EMERALD, Items.OAK_BOAT, List.of(VillagerType.PLAINS)), ImmutableTriple.of(FISHERMAN_5_SPRUCE_BOAT_EMERALD, Items.SPRUCE_BOAT, List.of(VillagerType.TAIGA, VillagerType.SNOW)), ImmutableTriple.of(FISHERMAN_5_JUNGLE_BOAT_EMERALD, Items.JUNGLE_BOAT, List.of(VillagerType.DESERT, VillagerType.JUNGLE)), ImmutableTriple.of(FISHERMAN_5_ACACIA_BOAT_EMERALD, Items.ACACIA_BOAT, List.of(VillagerType.SAVANNA)), ImmutableTriple.of(FISHERMAN_5_DARK_OAK_BOAT_EMERALD, Items.DARK_OAK_BOAT, List.of(VillagerType.SWAMP)))) {
         ResourceKey<VillagerTrade> resouceKey = (ResourceKey)entry.left;
         Item item = (Item)entry.middle;
         register(context, resouceKey, new VillagerTrade(new TradeCost(item, 1), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F, villagerTypeRestriction(villagerTypeHolderSet(villagerVariants, (List)entry.right)), List.of()));
      }

   }

   private static void registerWoolSales(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_1_WHITE_WOOL_EMERALD, Items.WHITE_WOOL), Pair.of(SHEPHERD_1_BROWN_WOOL_EMERALD, Items.BROWN_WOOL), Pair.of(SHEPHERD_1_GRAY_WOOL_EMERALD, Items.GRAY_WOOL), Pair.of(SHEPHERD_1_BLACK_WOOL_EMERALD, Items.BLACK_WOOL))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost((ItemLike)entry.getRight(), 18), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerLevelTwoDyeTrades(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_2_WHITE_DYE_EMERALD, Items.WHITE_DYE), Pair.of(SHEPHERD_2_GRAY_DYE_EMERALD, Items.GRAY_DYE), Pair.of(SHEPHERD_2_BLACK_DYE_EMERALD, Items.BLACK_DYE), Pair.of(SHEPHERD_2_LIGHT_BLUE_DYE_EMERALD, Items.LIGHT_BLUE_DYE), Pair.of(SHEPHERD_2_LIME_DYE_EMERALD, Items.LIME_DYE))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost((ItemLike)entry.getRight(), 12), new ItemStackTemplate(Items.EMERALD), 16, 10, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerLevelThreeDyeTrades(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_3_YELLOW_DYE_EMERALD, Items.YELLOW_DYE), Pair.of(SHEPHERD_3_LIGHT_GRAY_DYE_EMERALD, Items.LIGHT_GRAY_DYE), Pair.of(SHEPHERD_3_ORANGE_DYE_EMERALD, Items.ORANGE_DYE), Pair.of(SHEPHERD_3_RED_DYE_EMERALD, Items.RED_DYE), Pair.of(SHEPHERD_3_PINK_DYE_EMERALD, Items.PINK_DYE))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost((ItemLike)entry.getRight(), 12), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerLevelFourDyeTrades(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_4_BROWN_DYE_EMERALD, Items.BROWN_DYE), Pair.of(SHEPHERD_4_PURPLE_DYE_EMERALD, Items.PURPLE_DYE), Pair.of(SHEPHERD_4_BLUE_DYE_EMERALD, Items.BLUE_DYE), Pair.of(SHEPHERD_4_GREEN_DYE_EMERALD, Items.GREEN_DYE), Pair.of(SHEPHERD_4_MAGENTA_DYE_EMERALD, Items.MAGENTA_DYE), Pair.of(SHEPHERD_4_CYAN_DYE_EMERALD, Items.CYAN_DYE))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost((ItemLike)entry.getRight(), 12), new ItemStackTemplate(Items.EMERALD), 16, 30, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerWoolPurchases(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_2_EMERALD_WHITE_WOOL, Items.WHITE_WOOL), Pair.of(SHEPHERD_2_EMERALD_ORANGE_WOOL, Items.ORANGE_WOOL), Pair.of(SHEPHERD_2_EMERALD_MAGENTA_WOOL, Items.MAGENTA_WOOL), Pair.of(SHEPHERD_2_EMERALD_BLUE_WOOL, Items.BLUE_WOOL), Pair.of(SHEPHERD_2_EMERALD_LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_WOOL), Pair.of(SHEPHERD_2_EMERALD_YELLOW_WOOL, Items.YELLOW_WOOL), Pair.of(SHEPHERD_2_EMERALD_LIME_WOOL, Items.LIME_WOOL), Pair.of(SHEPHERD_2_EMERALD_PINK_WOOL, Items.PINK_WOOL), Pair.of(SHEPHERD_2_EMERALD_GRAY_WOOL, Items.GRAY_WOOL), Pair.of(SHEPHERD_2_EMERALD_LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_WOOL), Pair.of(SHEPHERD_2_EMERALD_CYAN_WOOL, Items.CYAN_WOOL), Pair.of(SHEPHERD_2_EMERALD_PURPLE_WOOL, Items.PURPLE_WOOL), Pair.of(SHEPHERD_2_EMERALD_BROWN_WOOL, Items.BROWN_WOOL), Pair.of(SHEPHERD_2_EMERALD_GREEN_WOOL, Items.GREEN_WOOL), Pair.of(SHEPHERD_2_EMERALD_RED_WOOL, Items.RED_WOOL), Pair.of(SHEPHERD_2_EMERALD_BLACK_WOOL, Items.BLACK_WOOL))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)entry.getRight()), 16, 5, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerCarpetPurchases(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_2_EMERALD_WHITE_CARPET, Items.WHITE_CARPET), Pair.of(SHEPHERD_2_EMERALD_ORANGE_CARPET, Items.ORANGE_CARPET), Pair.of(SHEPHERD_2_EMERALD_MAGENTA_CARPET, Items.MAGENTA_CARPET), Pair.of(SHEPHERD_2_EMERALD_BLUE_CARPET, Items.BLUE_CARPET), Pair.of(SHEPHERD_2_EMERALD_LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_CARPET), Pair.of(SHEPHERD_2_EMERALD_YELLOW_CARPET, Items.YELLOW_CARPET), Pair.of(SHEPHERD_2_EMERALD_LIME_CARPET, Items.LIME_CARPET), Pair.of(SHEPHERD_2_EMERALD_PINK_CARPET, Items.PINK_CARPET), Pair.of(SHEPHERD_2_EMERALD_GRAY_CARPET, Items.GRAY_CARPET), Pair.of(SHEPHERD_2_EMERALD_LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_CARPET), Pair.of(SHEPHERD_2_EMERALD_CYAN_CARPET, Items.CYAN_CARPET), Pair.of(SHEPHERD_2_EMERALD_PURPLE_CARPET, Items.PURPLE_CARPET), Pair.of(SHEPHERD_2_EMERALD_BROWN_CARPET, Items.BROWN_CARPET), Pair.of(SHEPHERD_2_EMERALD_GREEN_CARPET, Items.GREEN_CARPET), Pair.of(SHEPHERD_2_EMERALD_RED_CARPET, Items.RED_CARPET), Pair.of(SHEPHERD_2_EMERALD_BLACK_CARPET, Items.BLACK_CARPET))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate((Item)entry.getRight(), 4), 16, 5, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerBedTrades(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_3_EMERALD_WHITE_BED, Items.WHITE_BED), Pair.of(SHEPHERD_3_EMERALD_ORANGE_BED, Items.ORANGE_BED), Pair.of(SHEPHERD_3_EMERALD_MAGENTA_BED, Items.MAGENTA_BED), Pair.of(SHEPHERD_3_EMERALD_BLUE_BED, Items.BLUE_BED), Pair.of(SHEPHERD_3_EMERALD_LIGHT_BLUE_BED, Items.LIGHT_BLUE_BED), Pair.of(SHEPHERD_3_EMERALD_YELLOW_BED, Items.YELLOW_BED), Pair.of(SHEPHERD_3_EMERALD_LIME_BED, Items.LIME_BED), Pair.of(SHEPHERD_3_EMERALD_PINK_BED, Items.PINK_BED), Pair.of(SHEPHERD_3_EMERALD_GRAY_BED, Items.GRAY_BED), Pair.of(SHEPHERD_3_EMERALD_LIGHT_GRAY_BED, Items.LIGHT_GRAY_BED), Pair.of(SHEPHERD_3_EMERALD_CYAN_BED, Items.CYAN_BED), Pair.of(SHEPHERD_3_EMERALD_PURPLE_BED, Items.PURPLE_BED), Pair.of(SHEPHERD_3_EMERALD_BROWN_BED, Items.BROWN_BED), Pair.of(SHEPHERD_3_EMERALD_GREEN_BED, Items.GREEN_BED), Pair.of(SHEPHERD_3_EMERALD_RED_BED, Items.RED_BED), Pair.of(SHEPHERD_3_EMERALD_BLACK_BED, Items.BLACK_BED))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate((Item)entry.getRight()), 12, 10, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerShepherdBannerTrades(final BootstrapContext<VillagerTrade> context) {
      for(Pair<ResourceKey<VillagerTrade>, Item> entry : List.of(Pair.of(SHEPHERD_4_EMERALD_WHITE_BANNER, Items.WHITE_BANNER), Pair.of(SHEPHERD_4_EMERALD_ORANGE_BANNER, Items.ORANGE_BANNER), Pair.of(SHEPHERD_4_EMERALD_MAGENTA_BANNER, Items.MAGENTA_BANNER), Pair.of(SHEPHERD_4_EMERALD_BLUE_BANNER, Items.BLUE_BANNER), Pair.of(SHEPHERD_4_EMERALD_LIGHT_BLUE_BANNER, Items.LIGHT_BLUE_BANNER), Pair.of(SHEPHERD_4_EMERALD_YELLOW_BANNER, Items.YELLOW_BANNER), Pair.of(SHEPHERD_4_EMERALD_LIME_BANNER, Items.LIME_BANNER), Pair.of(SHEPHERD_4_EMERALD_PINK_BANNER, Items.PINK_BANNER), Pair.of(SHEPHERD_4_EMERALD_GRAY_BANNER, Items.GRAY_BANNER), Pair.of(SHEPHERD_4_EMERALD_LIGHT_GRAY_BANNER, Items.LIGHT_GRAY_BANNER), Pair.of(SHEPHERD_4_EMERALD_CYAN_BANNER, Items.CYAN_BANNER), Pair.of(SHEPHERD_4_EMERALD_PURPLE_BANNER, Items.PURPLE_BANNER), Pair.of(SHEPHERD_4_EMERALD_BROWN_BANNER, Items.BROWN_BANNER), Pair.of(SHEPHERD_4_EMERALD_GREEN_BANNER, Items.GREEN_BANNER), Pair.of(SHEPHERD_4_EMERALD_RED_BANNER, Items.RED_BANNER), Pair.of(SHEPHERD_4_EMERALD_BLACK_BANNER, Items.BLACK_BANNER))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate((Item)entry.getRight()), 12, 15, 0.05F, Optional.empty(), List.of()));
      }

   }

   private static void registerCartographerBannerTrades(final BootstrapContext<VillagerTrade> context, final HolderGetter<VillagerType> villagerVariants) {
      for(Triple<ResourceKey<VillagerTrade>, Item, List<ResourceKey<VillagerType>>> entry : List.of(Triple.of(CARTOGRAPHER_4_EMERALD_WHITE_BANNER, Items.WHITE_BANNER, List.of(VillagerType.SNOW, VillagerType.PLAINS)), Triple.of(CARTOGRAPHER_4_EMERALD_ORANGE_BANNER, Items.ORANGE_BANNER, List.of(VillagerType.SAVANNA, VillagerType.DESERT)), Triple.of(CARTOGRAPHER_4_EMERALD_MAGENTA_BANNER, Items.MAGENTA_BANNER, List.of(VillagerType.SAVANNA)), Triple.of(CARTOGRAPHER_4_EMERALD_BLUE_BANNER, Items.BLUE_BANNER, List.of(VillagerType.SNOW, VillagerType.TAIGA)), Triple.of(CARTOGRAPHER_4_EMERALD_LIGHT_BLUE_BANNER, Items.LIGHT_BLUE_BANNER, List.of(VillagerType.SNOW, VillagerType.SWAMP)), Triple.of(CARTOGRAPHER_4_EMERALD_YELLOW_BANNER, Items.YELLOW_BANNER, List.of(VillagerType.PLAINS, VillagerType.JUNGLE)), Triple.of(CARTOGRAPHER_4_EMERALD_LIME_BANNER, Items.LIME_BANNER, List.of(VillagerType.DESERT, VillagerType.TAIGA)), Triple.of(CARTOGRAPHER_4_EMERALD_PINK_BANNER, Items.PINK_BANNER, List.of(VillagerType.TAIGA, VillagerType.PLAINS)), Triple.of(CARTOGRAPHER_4_EMERALD_GRAY_BANNER, Items.GRAY_BANNER, List.of(VillagerType.DESERT)), Triple.of(CARTOGRAPHER_4_EMERALD_CYAN_BANNER, Items.CYAN_BANNER, List.of(VillagerType.DESERT, VillagerType.SNOW)), Triple.of(CARTOGRAPHER_4_EMERALD_PURPLE_BANNER, Items.PURPLE_BANNER, List.of(VillagerType.TAIGA, VillagerType.SWAMP)), Triple.of(CARTOGRAPHER_4_EMERALD_BROWN_BANNER, Items.BROWN_BANNER, List.of(VillagerType.PLAINS, VillagerType.JUNGLE)), Triple.of(CARTOGRAPHER_4_EMERALD_GREEN_BANNER, Items.GREEN_BANNER, List.of(VillagerType.DESERT, VillagerType.SAVANNA, VillagerType.JUNGLE)), Triple.of(CARTOGRAPHER_4_EMERALD_RED_BANNER, Items.RED_BANNER, List.of(VillagerType.SNOW, VillagerType.SAVANNA)), Triple.of(CARTOGRAPHER_4_EMERALD_BLACK_BANNER, Items.BLACK_BANNER, List.of(VillagerType.SWAMP)))) {
         register(context, (ResourceKey)entry.getLeft(), new VillagerTrade(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate((Item)entry.getMiddle()), 12, 15, 0.05F, villagerTypeRestriction(villagerTypeHolderSet(villagerVariants, (List)entry.getRight())), List.of()));
      }

   }

   private static void registerBasicExplorerMapTrades(final BootstrapContext<VillagerTrade> context, final HolderGetter<Item> items, final HolderGetter<VillagerType> villagerVariants) {
      for(VillagerExplorerMapEntry entry : List.of(new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_TAIGA_MAP, StructureTags.ON_TAIGA_VILLAGE_MAPS, MapDecorationTypes.TAIGA_VILLAGE, "village_taiga", List.of(VillagerType.SWAMP, VillagerType.SNOW, VillagerType.PLAINS)), new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_SWAMP_MAP, StructureTags.ON_SWAMP_EXPLORER_MAPS, MapDecorationTypes.SWAMP_HUT, "explorer_swamp", List.of(VillagerType.TAIGA, VillagerType.SNOW, VillagerType.JUNGLE)), new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SNOWY_MAP, StructureTags.ON_SNOWY_VILLAGE_MAPS, MapDecorationTypes.SNOWY_VILLAGE, "village_snowy", List.of(VillagerType.TAIGA, VillagerType.SWAMP)), new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SAVANNA_MAP, StructureTags.ON_SAVANNA_VILLAGE_MAPS, MapDecorationTypes.SAVANNA_VILLAGE, "village_savanna", List.of(VillagerType.PLAINS, VillagerType.JUNGLE, VillagerType.DESERT)), new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_PLAINS_MAP, StructureTags.ON_PLAINS_VILLAGE_MAPS, MapDecorationTypes.PLAINS_VILLAGE, "village_plains", List.of(VillagerType.TAIGA, VillagerType.SNOW, VillagerType.SAVANNA, VillagerType.DESERT)), new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_JUNGLE_MAP, StructureTags.ON_JUNGLE_EXPLORER_MAPS, MapDecorationTypes.JUNGLE_TEMPLE, "explorer_jungle", List.of(VillagerType.SWAMP, VillagerType.SAVANNA, VillagerType.DESERT)), new VillagerExplorerMapEntry(CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_DESERT_MAP, StructureTags.ON_DESERT_VILLAGE_MAPS, MapDecorationTypes.DESERT_VILLAGE, "village_desert", List.of(VillagerType.SAVANNA, VillagerType.JUNGLE)))) {
         register(context, entry.resourceKey, new VillagerTrade(new TradeCost(Items.EMERALD, 8), Optional.of(new TradeCost(Items.COMPASS, 1)), new ItemStackTemplate(Items.MAP), 12, 5, 0.05F, villagerTypeRestriction(villagerTypeHolderSet(villagerVariants, entry.villagerTypes)), List.of(ExplorationMapFunction.makeExplorationMap().setDestination(entry.structureTagKey).setMapDecoration(entry.mapDecorationType).setSearchRadius(100).setSkipKnownStructures(true).build(), SetNameFunction.setName(Component.translatable("filled_map." + entry.translationKey), SetNameFunction.Target.ITEM_NAME).build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, Items.FILLED_MAP).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build())));
      }

   }

   public static Holder.Reference<VillagerTrade> register(final BootstrapContext<VillagerTrade> context, final ResourceKey<VillagerTrade> resourceKey, final VillagerTrade villagerTrade) {
      return context.register(resourceKey, villagerTrade);
   }

   public static ResourceKey<VillagerTrade> resourceKey(final String path) {
      return ResourceKey.create(Registries.VILLAGER_TRADE, Identifier.withDefaultNamespace(path));
   }

   public static HolderSet<VillagerType> villagerTypeHolderSet(final HolderGetter<VillagerType> villagerVariants, final ResourceKey<VillagerType> resourceKey) {
      Optional<Holder.Reference<VillagerType>> holder = villagerVariants.get(resourceKey);
      return (HolderSet<VillagerType>)(holder.isPresent() ? HolderSet.direct((Holder)holder.get()) : HolderSet.empty());
   }

   public static HolderSet<VillagerType> villagerTypeHolderSet(final HolderGetter<VillagerType> villagerVariants, final List<ResourceKey<VillagerType>> resourceKeys) {
      List<Holder<VillagerType>> villagerTypes = new ArrayList();

      for(ResourceKey<VillagerType> resourceKey : resourceKeys) {
         villagerTypes.add(villagerVariants.getOrThrow(resourceKey));
      }

      return HolderSet.direct(villagerTypes);
   }

   public static Optional<LootItemCondition> villagerTypeRestriction(final HolderSet<VillagerType> villagerTypes) {
      return Optional.of(new LootItemEntityPropertyCondition(Optional.of(EntityPredicate.Builder.entity().components(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.VILLAGER_VARIANT, VillagerTypePredicate.villagerTypes(villagerTypes)).build()).build()), LootContext.EntityTarget.THIS));
   }

   private static List<LootItemFunction> dyedItem(final HolderGetter<Item> items, final Item expectedItem) {
      return List.of(addRandomDye(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, expectedItem).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.DYED_COLOR).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build());
   }

   private static LootItemFunction addRandomDye() {
      return SetRandomDyesFunction.withCount(Sum.sum(ConstantValue.exactly(1.0F), new BinomialDistributionGenerator(ConstantValue.exactly(2.0F), ConstantValue.exactly(0.75F)))).build();
   }

   public static List<LootItemFunction> enchantedBook(final HolderGetter<Item> items, final Optional<HolderSet<Enchantment>> options) {
      return List.of((new EnchantRandomlyFunction.Builder()).withOptions(options).allowingIncompatibleEnchantments().includeAdditionalCostComponent().build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, Items.ENCHANTED_BOOK).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.STORED_ENCHANTMENTS, EnchantmentsPredicate.storedEnchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY)))).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build());
   }

   public static List<LootItemFunction> enchantedBook(final HolderGetter<Item> items, final Holder<Enchantment> enchantment, final int level) {
      return List.of((new SetEnchantmentsFunction.Builder()).withEnchantment(enchantment, ConstantValue.exactly((float)level)).build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, Items.ENCHANTED_BOOK).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.STORED_ENCHANTMENTS, EnchantmentsPredicate.storedEnchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.exactly(level))))).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build());
   }

   public static List<LootItemFunction> enchantedItem(final HolderGetter<Item> items, final Optional<HolderSet<Enchantment>> options, final Item expectedItem) {
      return List.of((new EnchantWithLevelsFunction.Builder(UniformGenerator.between(5.0F, 20.0F))).withOptions(options).includeAdditionalCostComponent().build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, expectedItem).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY)))).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build());
   }

   public static List<LootItemFunction> enchantedItem(final HolderGetter<Item> items, final Holder<Enchantment> enchantment, final int level, final Item expectedItem) {
      return List.of((new SetEnchantmentsFunction.Builder()).withEnchantment(enchantment, ConstantValue.exactly((float)level)).build(), FilteredFunction.filtered((new ItemPredicate.Builder()).of(items, expectedItem).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.exactly(level))))).build()).build()).onFail(Optional.of(DiscardItem.discardItem().build())).build());
   }

   private static record VillagerExplorerMapEntry(ResourceKey<VillagerTrade> resourceKey, TagKey<Structure> structureTagKey, Holder<MapDecorationType> mapDecorationType, String translationKey, List<ResourceKey<VillagerType>> villagerTypes) {
      private VillagerExplorerMapEntry {
         super();
      }
   }
}
