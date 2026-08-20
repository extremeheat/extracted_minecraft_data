package net.minecraft.world.item.trading;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
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
import net.minecraft.world.level.block.ColorCollection;
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
   public static final ColorCollection<ResourceKey<VillagerTrade>> SHEPHERD_2_EMERALD_WOOL;
   public static final ColorCollection<ResourceKey<VillagerTrade>> SHEPHERD_2_EMERALD_CARPETS;
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_YELLOW_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_LIGHT_GRAY_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_ORANGE_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_RED_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_3_PINK_DYE_EMERALD;
   public static final ColorCollection<ResourceKey<VillagerTrade>> SHEPHERD_3_EMERALD_BED;
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_BROWN_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_PURPLE_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_BLUE_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_GREEN_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_MAGENTA_DYE_EMERALD;
   public static final ResourceKey<VillagerTrade> SHEPHERD_4_CYAN_DYE_EMERALD;
   public static final ColorCollection<ResourceKey<VillagerTrade>> SHEPHERD_4_EMERALD_BANNER;
   public static final ResourceKey<VillagerTrade> SHEPHERD_5_EMERALD_PAINTING;
   public static final ResourceKey<VillagerTrade> FLETCHER_1_STICK_EMERALD;
   public static final ResourceKey<VillagerTrade> FLETCHER_1_EMERALD_ARROW;
   public static final ResourceKey<VillagerTrade> FLETCHER_1_GRAVEL_AND_EMERALD_FLINT;
   public static final ResourceKey<VillagerTrade> FLETCHER_2_FLINT_EMERALD;
   public static final ResourceKey<VillagerTrade> FLETCHER_2_EMERALD_BOW;
   public static final ResourceKey<VillagerTrade> FLETCHER_3_STRING_EMERALD;
   public static final ResourceKey<VillagerTrade> FLETCHER_3_EMERALD_CROSSBOW;
   public static final ResourceKey<VillagerTrade> FLETCHER_4_FEATHER_EMERALD;
   public static final ResourceKey<VillagerTrade> FLETCHER_4_EMERALD_ENCHANTED_BOW;
   public static final ResourceKey<VillagerTrade> FLETCHER_5_TRIPWIRE_HOOK_EMERALD;
   public static final ResourceKey<VillagerTrade> FLETCHER_5_EMERALD_ENCHANTED_CROSSBOW;
   public static final ResourceKey<VillagerTrade> FLETCHER_5_ARROW_AND_EMERALD_TIPPED_ARROW;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_1_PAPER_EMERALD;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_1_EMERALD_AND_BOOK_ENCHANTED_BOOK;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_1_EMERALD_BOOKSHELF;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_2_BOOK_EMERALD;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_2_EMERALD_AND_BOOK_ENCHANTED_BOOK;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_2_EMERALD_LANTERN;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_3_INK_SAC_EMERALD;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_3_EMERALD_AND_BOOK_ENCHANTED_BOOK;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_3_EMERALD_GLASS;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_WRITABLE_BOOK_EMERALD;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_EMERALD_AND_BOOK_ENCHANTED_BOOK;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_EMERALD_CLOCK;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_4_EMERALD_COMPASS;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_5_EMERALD_YELLOW_CANDLE;
   public static final ResourceKey<VillagerTrade> LIBRARIAN_5_EMERALD_RED_CANDLE;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_1_PAPER_EMERALD;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_1_EMERALD_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_GLASS_PANE_EMERALD;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_TAIGA_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_SWAMP_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SNOWY_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SAVANNA_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_PLAINS_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_JUNGLE_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_DESERT_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_COMPASS_EMERALD;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_EMERALD_AND_COMPASS_OCEAN_EXPLORER_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_EMERALD_AND_COMPASS_TRIAL_CHAMBER_MAP;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_ITEM_FRAME;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_WHITE_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_ORANGE_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_MAGENTA_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_BLUE_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_LIGHT_BLUE_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_YELLOW_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_LIME_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_PINK_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_GRAY_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_CYAN_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_PURPLE_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_BROWN_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_GREEN_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_RED_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_4_EMERALD_BLACK_BANNER;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_5_EMERALD_GLOBE_BANNER_PATTERN;
   public static final ResourceKey<VillagerTrade> CARTOGRAPHER_5_EMERALD_AND_COMPASS_WOODLAND_MANSION_MAP;
   public static final ResourceKey<VillagerTrade> CLERIC_1_ROTTEN_FLESH_EMERALD;
   public static final ResourceKey<VillagerTrade> CLERIC_1_EMERALD_REDSTONE;
   public static final ResourceKey<VillagerTrade> CLERIC_2_GOLD_INGOT_EMERALD;
   public static final ResourceKey<VillagerTrade> CLERIC_2_EMERALD_LAPIS_LAZULI;
   public static final ResourceKey<VillagerTrade> CLERIC_3_RABBIT_FOOT_EMERALD;
   public static final ResourceKey<VillagerTrade> CLERIC_3_EMERALD_GLOWSTONE;
   public static final ResourceKey<VillagerTrade> CLERIC_4_TURTLE_SCUTE_EMERALD;
   public static final ResourceKey<VillagerTrade> CLERIC_4_GLASS_BOTTLE_EMERALD;
   public static final ResourceKey<VillagerTrade> CLERIC_4_EMERALD_ENDER_PEARL;
   public static final ResourceKey<VillagerTrade> CLERIC_5_NETHER_WART_EMERALD;
   public static final ResourceKey<VillagerTrade> CLERIC_5_EMERALD_EXPERIENCE_BOTTLE;
   public static final ResourceKey<VillagerTrade> COMMON_SMITH_1_COAL_EMERALD;
   public static final ResourceKey<VillagerTrade> COMMON_SMITH_2_IRON_INGOT_EMERALD;
   public static final ResourceKey<VillagerTrade> COMMON_SMITH_2_EMERALD_BELL;
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_LEGGINGS;
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_BOOTS;
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_HELMET;
   public static final ResourceKey<VillagerTrade> ARMORER_1_EMERALD_IRON_CHESTPLATE;
   public static final ResourceKey<VillagerTrade> ARMORER_2_EMERALD_CHAINMAIL_BOOTS;
   public static final ResourceKey<VillagerTrade> ARMORER_2_EMERALD_CHAINMAIL_LEGGINGS;
   public static final ResourceKey<VillagerTrade> ARMORER_3_LAVA_BUCKET_EMERALD;
   public static final ResourceKey<VillagerTrade> ARMORER_3_EMERALD_CHAINMAIL_HELMET;
   public static final ResourceKey<VillagerTrade> ARMORER_3_EMERALD_CHAINMAIL_CHESTPLATE;
   public static final ResourceKey<VillagerTrade> ARMORER_3_EMERALD_SHIELD;
   public static final ResourceKey<VillagerTrade> ARMORER_3_DIAMOND_EMERALD;
   public static final ResourceKey<VillagerTrade> ARMORER_4_EMERALD_ENCHANTED_DIAMOND_LEGGINGS;
   public static final ResourceKey<VillagerTrade> ARMORER_4_EMERALD_ENCHANTED_DIAMOND_BOOTS;
   public static final ResourceKey<VillagerTrade> ARMORER_5_EMERALD_ENCHANTED_DIAMOND_HELMET;
   public static final ResourceKey<VillagerTrade> ARMORER_5_EMERALD_ENCHANTED_DIAMOND_CHESTPLATE;
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_1_EMERALD_IRON_AXE;
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_1_EMERALD_ENCHANTED_IRON_SWORD;
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_3_FLINT_EMERALD;
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_4_DIAMOND_EMERALD;
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_4_EMERALD_ENCHANTED_DIAMOND_AXE;
   public static final ResourceKey<VillagerTrade> WEAPONSMITH_5_EMERALD_ENCHANTED_DIAMOND_SWORD;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_AXE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_SHOVEL;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_PICKAXE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_1_EMERALD_STONE_HOE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_FLINT_EMERALD;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_IRON_AXE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_IRON_SHOVEL;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_IRON_PICKAXE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_3_EMERALD_DIAMOND_HOE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_4_DIAMOND_EMERALD;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_4_EMERALD_DIAMOND_AXE;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_4_EMERALD_DIAMOND_SHOVEL;
   public static final ResourceKey<VillagerTrade> TOOLSMITH_5_EMERALD_DIAMOND_PICKAXE;
   public static final ResourceKey<VillagerTrade> BUTCHER_1_CHICKEN_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_1_PORKCHOP_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_1_RABBIT_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_1_EMERALD_RABBIT_STEW;
   public static final ResourceKey<VillagerTrade> BUTCHER_2_COAL_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_2_EMERALD_COOKED_PORKCHOP;
   public static final ResourceKey<VillagerTrade> BUTCHER_2_EMERALD_COOKED_CHICKEN;
   public static final ResourceKey<VillagerTrade> BUTCHER_3_MUTTON_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_3_BEEF_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_4_DRIED_KELP_BLOCK_EMERALD;
   public static final ResourceKey<VillagerTrade> BUTCHER_5_SWEET_BERRIES_EMERALD;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_1_LEATHER_EMERALD;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_1_EMERALD_DYED_LEATHER_LEGGINGS;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_1_EMERALD_DYED_LEATHER_CHESTPLATE;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_2_FLINT_EMERALD;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_2_EMERALD_DYED_LEATHER_HELMET;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_2_EMERALD_DYED_LEATHER_BOOTS;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_3_RABBIT_HIDE_EMERALD;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_3_EMERALD_DYED_LEATHER_CHESTPLATE;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_4_TURTLE_SCUTE_EMERALD;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_4_EMERALD_DYED_LEATHER_HORSE_ARMOR;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_5_EMERALD_SADDLE;
   public static final ResourceKey<VillagerTrade> LEATHERWORKER_5_EMERALD_DYED_LEATHER_HELMET;
   public static final ResourceKey<VillagerTrade> MASON_1_CLAY_BALL_EMERALD;
   public static final ResourceKey<VillagerTrade> MASON_1_EMERALD_BRICK;
   public static final ResourceKey<VillagerTrade> MASON_2_STONE_EMERALD;
   public static final ResourceKey<VillagerTrade> MASON_2_EMERALD_CHISELED_STONE_BRICKS;
   public static final ResourceKey<VillagerTrade> MASON_3_GRANITE_EMERALD;
   public static final ResourceKey<VillagerTrade> MASON_3_ANDESITE_EMERALD;
   public static final ResourceKey<VillagerTrade> MASON_3_DIORITE_EMERALD;
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_DRIPSTONE_BLOCK;
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_POLISHED_ANDESITE;
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_POLISHED_DIORITE;
   public static final ResourceKey<VillagerTrade> MASON_3_EMERALD_POLISHED_GRANTITE;
   public static final ResourceKey<VillagerTrade> MASON_4_QUARTZ_EMERALD;
   public static final ColorCollection<ResourceKey<VillagerTrade>> MASON_4_EMERALD_TERRACOTTA;
   public static final ColorCollection<ResourceKey<VillagerTrade>> MASON_4_EMERALD_GLAZED_TERRACOTTA;
   public static final ResourceKey<VillagerTrade> MASON_5_EMERALD_QUARTZ_PILLAR;
   public static final ResourceKey<VillagerTrade> MASON_5_EMERALD_QUARTZ_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_WATER_BOTTLE_EMERALD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_WATER_BUCKET_EMERALD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_MILK_BUCKET_EMERALD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_FERMENTED_SPIDER_EYE_EMERALD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_BAKED_POTATO_EMERALD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_HAY_BLOCK_EMERALD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PACKED_ICE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BLUE_ICE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GUNPOWDER;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PODZOL;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ACACIA_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BIRCH_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DARK_OAK_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_JUNGLE_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OAK_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SPRUCE_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CHERRY_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MANGROVE_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_OAK_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_POPLAR_LOG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ENCHANTED_IRON_PICKAXE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LONG_INVISIBILITY_POTION;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_TROPICAL_FISH_BUCKET;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PUFFERFISH_BUCKET;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SEA_PICKLE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SLIME_BALL;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GLOWSTONE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_NAUTILUS_SHELL;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_FERN;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SUGAR_CANE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PUMPKIN;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_KELP;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CACTUS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DANDELION;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_GOLDEN_DANDELION;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_POPPY;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BLUE_ORCHID;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ALLIUM;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_AZURE_BLUET;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_TULIP;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ORANGE_TULIP;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WHITE_TULIP;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PINK_TULIP;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OXEYE_DAISY;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CORNFLOWER;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LILY_OF_THE_VALLEY;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OPEN_EYEBLOSSOM;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WHEAT_SEEDS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BEETROOT_SEEDS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PUMPKIN_SEEDS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MELON_SEEDS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ACACIA_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BIRCH_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DARK_OAK_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_JUNGLE_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_OAK_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SPRUCE_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_CHERRY_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_OAK_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_POPLAR_SAPLING;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MANGROVE_PROPAGULE;
   public static final ColorCollection<ResourceKey<VillagerTrade>> WANDERING_TRADER_EMERALD_DYE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BRAIN_CORAL_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BUBBLE_CORAL_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_FIRE_CORAL_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_HORN_CORAL_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_TUBE_CORAL_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_VINE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_HANGING_MOSS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_BROWN_MUSHROOM;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_MUSHROOM;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_LILY_PAD;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SMALL_DRIPLEAF;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SAND;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_RED_SAND;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_POINTED_DRIPSTONE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SULFUR_SPIKE;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_ROOTED_DIRT;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_MOSS_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_PALE_MOSS_BLOCK;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_WILDFLOWERS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_DRY_TALL_GRASS;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_FIREFLY_BUSH;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_NAME_TAG;
   public static final ResourceKey<VillagerTrade> WANDERING_TRADER_EMERALD_SHELF_MUSHROOM;

   public VillagerTrades() {
      super();
   }

   public static Holder<VillagerTrade> bootstrap(final BootstrapContext<VillagerTrade> context) {
      HolderGetter<Item> items = context.lookup(Registries.ITEM);
      HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
      HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
      HolderSet<Enchantment> enchantmentsForTradedEquipment = enchantments.getOrThrow(EnchantmentTags.ON_TRADED_EQUIPMENT);
      HolderSet<Enchantment> enchantmentsForBooks = enchantments.getOrThrow(EnchantmentTags.TRADEABLE);
      HolderSet<Enchantment> doubleTradePrice = enchantments.getOrThrow(EnchantmentTags.DOUBLE_TRADE_PRICE);
      HolderGetter<Potion> potions = context.lookup(Registries.POTION);
      HolderSet<Potion> potionsForTippedArrows = potions.getOrThrow(PotionTags.TRADEABLE);
      HolderGetter<VillagerType> villagerVariants = context.lookup(Registries.VILLAGER_TYPE);
      register(context, FARMER_1_WHEAT_EMERALD, VillagerTrade.builder(new TradeCost(Items.WHEAT, 20), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FARMER_1_POTATO_EMERALD, VillagerTrade.builder(new TradeCost(Items.POTATO, 26), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FARMER_1_CARROT_EMERALD, VillagerTrade.builder(new TradeCost(Items.CARROT, 22), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FARMER_1_BEETROOT_EMERALD, VillagerTrade.builder(new TradeCost(Items.BEETROOT, 15), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FARMER_1_EMERALD_BREAD, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BREAD, 6), 16, 1, 0.05F).build());
      register(context, FARMER_2_PUMPKIN_EMERALD, VillagerTrade.builder(new TradeCost(Items.PUMPKIN, 6), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      register(context, FARMER_2_EMERALD_PUMPKIN_PIE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PUMPKIN_PIE, 4), 12, 5, 0.05F).build());
      register(context, FARMER_2_EMERALD_APPLE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.APPLE, 4), 16, 5, 0.05F).build());
      register(context, FARMER_3_EMERALD_COOKIE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.COOKIE, 18), 12, 10, 0.05F).build());
      register(context, FARMER_3_MELON_EMERALD, VillagerTrade.builder(new TradeCost(Items.MELON, 4), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, FARMER_4_EMERALD_CAKE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CAKE), 12, 15, 0.05F).build());
      register(context, FARMER_4_EMERALD_SUSPICIOUS_STEW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SUSPICIOUS_STEW), 12, 15, 0.05F).addModifier((new SetStewEffectFunction.Builder()).withEffect(MobEffects.NIGHT_VISION, ConstantValue.exactly(5.0F)).withEffect(MobEffects.JUMP_BOOST, ConstantValue.exactly(8.0F)).withEffect(MobEffects.WEAKNESS, ConstantValue.exactly(7.0F)).withEffect(MobEffects.BLINDNESS, ConstantValue.exactly(6.0F)).withEffect(MobEffects.POISON, ConstantValue.exactly(14.0F)).withEffect(MobEffects.SATURATION, ConstantValue.exactly(7.0F))).build());
      register(context, FARMER_5_EMERALD_GOLDEN_CARROT, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.GOLDEN_CARROT, 3), 12, 30, 0.05F).build());
      register(context, FARMER_5_EMERALD_GLISTENING_MELON_SLICE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.GLISTERING_MELON_SLICE, 3), 12, 30, 0.05F).build());
      register(context, FISHERMAN_1_STRING_EMERALD, VillagerTrade.builder(new TradeCost(Items.STRING, 20), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FISHERMAN_1_COAL_EMERALD, VillagerTrade.builder(new TradeCost(Items.COAL, 10), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FISHERMAN_1_RAW_COD_AND_EMERALD_COOKED_COD, VillagerTrade.builder(new TradeCost(Items.COD, 6), new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.COOKED_COD, 6), 16, 1, 0.05F).build());
      register(context, FISHERMAN_1_EMERALD_COD_BUCKET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.COD_BUCKET), 16, 1, 0.05F).build());
      register(context, FISHERMAN_2_COD_EMERALD, VillagerTrade.builder(new TradeCost(Items.COD, 15), new ItemStackTemplate(Items.EMERALD), 16, 10, 0.05F).build());
      register(context, FISHERMAN_2_SALMON_AND_EMERALD_COOKED_SALMON, VillagerTrade.builder(new TradeCost(Items.SALMON, 6), new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.COOKED_SALMON, 6), 16, 5, 0.05F).build());
      register(context, FISHERMAN_2_EMERALD_CAMPFIRE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.CAMPFIRE), 12, 5, 0.05F).build());
      register(context, FISHERMAN_3_SALMON_EMERALD, VillagerTrade.builder(new TradeCost(Items.SALMON, 13), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F).build());
      register(context, FISHERMAN_3_EMERALD_ENCHANTED_FISHING_ROD, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.FISHING_ROD), 3, 10, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.FISHING_ROD)).build());
      register(context, FISHERMAN_4_TROPICAL_FISH_EMERALD, VillagerTrade.builder(new TradeCost(Items.TROPICAL_FISH, 6), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, FISHERMAN_5_PUFFERFISH_EMERALD, VillagerTrade.builder(new TradeCost(Items.PUFFERFISH, 4), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      registerBoatTrades(context, villagerVariants);
      registerShepherdWoolSales(context);
      register(context, SHEPHERD_1_EMERALD_SHEARS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.SHEARS), 12, 1, 0.05F).build());
      registerShepherdLevelTwoDyeTrades(context);
      registerWoolPurchases(context);
      registerCarpetPurchases(context);
      registerShepherdLevelThreeDyeTrades(context);
      registerBedTrades(context);
      registerLevelFourDyeTrades(context);
      registerShepherdBannerTrades(context);
      register(context, SHEPHERD_5_EMERALD_PAINTING, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.PAINTING, 3), 12, 30, 0.05F).build());
      register(context, FLETCHER_1_STICK_EMERALD, VillagerTrade.builder(new TradeCost(Items.STICK, 32), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, FLETCHER_1_EMERALD_ARROW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.ARROW, 16), 12, 1, 0.05F).build());
      register(context, FLETCHER_1_GRAVEL_AND_EMERALD_FLINT, VillagerTrade.builder(new TradeCost(Items.GRAVEL, 10), new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.FLINT, 10), 12, 1, 0.05F).build());
      register(context, FLETCHER_2_FLINT_EMERALD, VillagerTrade.builder(new TradeCost(Items.FLINT, 26), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      register(context, FLETCHER_2_EMERALD_BOW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.BOW), 12, 5, 0.05F).build());
      register(context, FLETCHER_3_STRING_EMERALD, VillagerTrade.builder(new TradeCost(Items.STRING, 14), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F).build());
      register(context, FLETCHER_3_EMERALD_CROSSBOW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CROSSBOW), 12, 10, 0.05F).build());
      register(context, FLETCHER_4_FEATHER_EMERALD, VillagerTrade.builder(new TradeCost(Items.FEATHER, 24), new ItemStackTemplate(Items.EMERALD), 16, 30, 0.05F).build());
      register(context, FLETCHER_4_EMERALD_ENCHANTED_BOW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.BOW), 3, 15, 0.05F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.BOW)).build());
      register(context, FLETCHER_5_TRIPWIRE_HOOK_EMERALD, VillagerTrade.builder(new TradeCost(Items.TRIPWIRE_HOOK, 8), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, FLETCHER_5_EMERALD_ENCHANTED_CROSSBOW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CROSSBOW), 3, 15, 0.05F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.CROSSBOW)).build());
      register(context, FLETCHER_5_ARROW_AND_EMERALD_TIPPED_ARROW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new TradeCost(Items.ARROW, 5), new ItemStackTemplate(Items.TIPPED_ARROW, 5), 12, 30, 0.05F).addModifier(SetRandomPotionFunction.fromTagKey(potionsForTippedArrows)).build());
      register(context, LIBRARIAN_1_PAPER_EMERALD, VillagerTrade.builder(new TradeCost(Items.PAPER, 24), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, LIBRARIAN_1_EMERALD_AND_BOOK_ENCHANTED_BOOK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 0), new TradeCost(Items.BOOK, 1), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 1, 0.2F).addModifiers(enchantedBook(items, enchantmentsForBooks)).doubleTradePriceEnchantments(doubleTradePrice).build());
      register(context, LIBRARIAN_1_EMERALD_BOOKSHELF, VillagerTrade.builder(new TradeCost(Items.EMERALD, 9), new ItemStackTemplate(Items.BOOKSHELF), 12, 1, 0.05F).build());
      register(context, LIBRARIAN_2_BOOK_EMERALD, VillagerTrade.builder(new TradeCost(Items.BOOK, 4), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      register(context, LIBRARIAN_2_EMERALD_AND_BOOK_ENCHANTED_BOOK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 0), new TradeCost(Items.BOOK, 1), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 5, 0.2F).addModifiers(enchantedBook(items, enchantmentsForBooks)).doubleTradePriceEnchantments(doubleTradePrice).build());
      register(context, LIBRARIAN_2_EMERALD_LANTERN, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LANTERN), 12, 5, 0.05F).build());
      register(context, LIBRARIAN_3_INK_SAC_EMERALD, VillagerTrade.builder(new TradeCost(Items.INK_SAC, 5), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, LIBRARIAN_3_EMERALD_AND_BOOK_ENCHANTED_BOOK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 0), new TradeCost(Items.BOOK, 1), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 10, 0.2F).addModifiers(enchantedBook(items, enchantmentsForBooks)).doubleTradePriceEnchantments(doubleTradePrice).build());
      register(context, LIBRARIAN_3_EMERALD_GLASS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.GLASS, 4), 12, 10, 0.05F).build());
      register(context, LIBRARIAN_4_WRITABLE_BOOK_EMERALD, VillagerTrade.builder(new TradeCost(Items.WRITABLE_BOOK, 2), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, LIBRARIAN_4_EMERALD_AND_BOOK_ENCHANTED_BOOK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 0), new TradeCost(Items.BOOK, 1), new ItemStackTemplate(Items.ENCHANTED_BOOK), 12, 15, 0.2F).addModifiers(enchantedBook(items, enchantmentsForBooks)).doubleTradePriceEnchantments(doubleTradePrice).build());
      register(context, LIBRARIAN_4_EMERALD_CLOCK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.CLOCK), 12, 15, 0.05F).build());
      register(context, LIBRARIAN_4_EMERALD_COMPASS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.COMPASS), 12, 15, 0.05F).build());
      register(context, LIBRARIAN_5_EMERALD_YELLOW_CANDLE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.DYED_CANDLE.yellow()), 12, 30, 0.05F).build());
      register(context, LIBRARIAN_5_EMERALD_RED_CANDLE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.DYED_CANDLE.red()), 12, 30, 0.05F).build());
      register(context, CARTOGRAPHER_1_PAPER_EMERALD, VillagerTrade.builder(new TradeCost(Items.PAPER, 24), new ItemStackTemplate(Items.EMERALD), 12, 2, 0.05F).build());
      register(context, CARTOGRAPHER_1_EMERALD_MAP, VillagerTrade.builder(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.MAP), 12, 1, 0.05F).build());
      register(context, CARTOGRAPHER_2_GLASS_PANE_EMERALD, VillagerTrade.builder(new TradeCost(Items.GLASS_PANE, 11), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      registerBasicExplorerMapTrades(context, villagerVariants, structures);
      register(context, CARTOGRAPHER_3_COMPASS_EMERALD, VillagerTrade.builder(new TradeCost(Items.COMPASS, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, CARTOGRAPHER_3_EMERALD_AND_COMPASS_OCEAN_EXPLORER_MAP, VillagerTrade.builder(new TradeCost(Items.EMERALD, 13), new TradeCost(Items.COMPASS, 1), new ItemStackTemplate(Items.OCEAN_EXPLORER_MAP), 12, 10, 0.2F).addModifiers(Holder.direct(ExplorationMapFunction.makeExplorationMap(structures.getOrThrow(StructureTags.ON_OCEAN_EXPLORER_MAPS)).setMapDecoration(MapDecorationTypes.OCEAN_MONUMENT).setSearchRadius(100).setSkipKnownStructures(true).build()), discardItemIfItsNot(anyValidMap())).build());
      register(context, CARTOGRAPHER_3_EMERALD_AND_COMPASS_TRIAL_CHAMBER_MAP, VillagerTrade.builder(new TradeCost(Items.EMERALD, 12), new TradeCost(Items.COMPASS, 1), new ItemStackTemplate(Items.TRIAL_EXPLORER_MAP), 12, 10, 0.2F).addModifiers(Holder.direct(ExplorationMapFunction.makeExplorationMap(structures.getOrThrow(StructureTags.ON_TRIAL_CHAMBERS_MAPS)).setMapDecoration(MapDecorationTypes.TRIAL_CHAMBERS).setSearchRadius(100).setSkipKnownStructures(true).build()), discardItemIfItsNot(anyValidMap())).build());
      register(context, CARTOGRAPHER_4_EMERALD_ITEM_FRAME, VillagerTrade.builder(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.ITEM_FRAME), 12, 15, 0.05F).build());
      registerCartographerBannerTrades(context, villagerVariants);
      register(context, CARTOGRAPHER_5_EMERALD_GLOBE_BANNER_PATTERN, VillagerTrade.builder(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.GLOBE_BANNER_PATTERN), 12, 30, 0.05F).build());
      register(context, CARTOGRAPHER_5_EMERALD_AND_COMPASS_WOODLAND_MANSION_MAP, VillagerTrade.builder(new TradeCost(Items.EMERALD, 14), new TradeCost(Items.COMPASS, 1), new ItemStackTemplate(Items.WOODLAND_EXPLORER_MAP), 12, 30, 0.2F).addModifiers(Holder.direct(ExplorationMapFunction.makeExplorationMap(structures.getOrThrow(StructureTags.ON_WOODLAND_EXPLORER_MAPS)).setMapDecoration(MapDecorationTypes.WOODLAND_MANSION).setSearchRadius(100).setSkipKnownStructures(true).build()), discardItemIfItsNot(anyValidMap())).build());
      register(context, CLERIC_1_ROTTEN_FLESH_EMERALD, VillagerTrade.builder(new TradeCost(Items.ROTTEN_FLESH, 32), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, CLERIC_1_EMERALD_REDSTONE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.REDSTONE, 2), 12, 1, 0.05F).build());
      register(context, CLERIC_2_GOLD_INGOT_EMERALD, VillagerTrade.builder(new TradeCost(Items.GOLD_INGOT, 3), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      register(context, CLERIC_2_EMERALD_LAPIS_LAZULI, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LAPIS_LAZULI), 12, 5, 0.05F).build());
      register(context, CLERIC_3_RABBIT_FOOT_EMERALD, VillagerTrade.builder(new TradeCost(Items.RABBIT_FOOT, 2), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, CLERIC_3_EMERALD_GLOWSTONE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.GLOWSTONE), 12, 10, 0.05F).build());
      register(context, CLERIC_4_TURTLE_SCUTE_EMERALD, VillagerTrade.builder(new TradeCost(Items.TURTLE_SCUTE, 4), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, CLERIC_4_GLASS_BOTTLE_EMERALD, VillagerTrade.builder(new TradeCost(Items.GLASS_BOTTLE, 9), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, CLERIC_4_EMERALD_ENDER_PEARL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.ENDER_PEARL), 12, 15, 0.05F).build());
      register(context, CLERIC_5_NETHER_WART_EMERALD, VillagerTrade.builder(new TradeCost(Items.NETHER_WART, 22), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, CLERIC_5_EMERALD_EXPERIENCE_BOTTLE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.EXPERIENCE_BOTTLE), 12, 30, 0.05F).build());
      register(context, COMMON_SMITH_1_COAL_EMERALD, VillagerTrade.builder(new TradeCost(Items.COAL, 15), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, COMMON_SMITH_2_IRON_INGOT_EMERALD, VillagerTrade.builder(new TradeCost(Items.IRON_INGOT, 4), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      register(context, COMMON_SMITH_2_EMERALD_BELL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 36), new ItemStackTemplate(Items.BELL), 12, 5, 0.2F).build());
      register(context, ARMORER_1_EMERALD_IRON_LEGGINGS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.IRON_LEGGINGS), 12, 1, 0.2F).build());
      register(context, ARMORER_1_EMERALD_IRON_BOOTS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.IRON_BOOTS), 12, 1, 0.2F).build());
      register(context, ARMORER_1_EMERALD_IRON_HELMET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.IRON_HELMET), 12, 1, 0.2F).build());
      register(context, ARMORER_1_EMERALD_IRON_CHESTPLATE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 9), new ItemStackTemplate(Items.IRON_CHESTPLATE), 12, 1, 0.2F).build());
      register(context, ARMORER_2_EMERALD_CHAINMAIL_BOOTS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.CHAINMAIL_BOOTS), 12, 5, 0.2F).build());
      register(context, ARMORER_2_EMERALD_CHAINMAIL_LEGGINGS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CHAINMAIL_LEGGINGS), 12, 5, 0.2F).build());
      register(context, ARMORER_3_LAVA_BUCKET_EMERALD, VillagerTrade.builder(new TradeCost(Items.LAVA_BUCKET, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, ARMORER_3_EMERALD_CHAINMAIL_HELMET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.CHAINMAIL_HELMET), 12, 10, 0.2F).build());
      register(context, ARMORER_3_EMERALD_CHAINMAIL_CHESTPLATE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.CHAINMAIL_CHESTPLATE), 12, 10, 0.2F).build());
      register(context, ARMORER_3_EMERALD_SHIELD, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.SHIELD), 12, 10, 0.2F).build());
      register(context, ARMORER_3_DIAMOND_EMERALD, VillagerTrade.builder(new TradeCost(Items.DIAMOND, 1), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, ARMORER_4_EMERALD_ENCHANTED_DIAMOND_LEGGINGS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 14), new ItemStackTemplate(Items.DIAMOND_LEGGINGS), 3, 15, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_LEGGINGS)).build());
      register(context, ARMORER_4_EMERALD_ENCHANTED_DIAMOND_BOOTS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.DIAMOND_BOOTS), 3, 15, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_BOOTS)).build());
      register(context, ARMORER_5_EMERALD_ENCHANTED_DIAMOND_HELMET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.DIAMOND_HELMET), 3, 30, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_HELMET)).build());
      register(context, ARMORER_5_EMERALD_ENCHANTED_DIAMOND_CHESTPLATE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 16), new ItemStackTemplate(Items.DIAMOND_CHESTPLATE), 3, 30, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_CHESTPLATE)).build());
      register(context, WEAPONSMITH_1_EMERALD_IRON_AXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.IRON_AXE), 12, 1, 0.2F).build());
      register(context, WEAPONSMITH_1_EMERALD_ENCHANTED_IRON_SWORD, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.IRON_SWORD), 12, 1, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_SWORD)).build());
      register(context, WEAPONSMITH_3_FLINT_EMERALD, VillagerTrade.builder(new TradeCost(Items.FLINT, 24), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, WEAPONSMITH_4_EMERALD_ENCHANTED_DIAMOND_AXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 12), new ItemStackTemplate(Items.DIAMOND_AXE), 3, 15, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_AXE)).build());
      register(context, WEAPONSMITH_4_DIAMOND_EMERALD, VillagerTrade.builder(new TradeCost(Items.DIAMOND, 1), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, WEAPONSMITH_5_EMERALD_ENCHANTED_DIAMOND_SWORD, VillagerTrade.builder(new TradeCost(Items.EMERALD, 8), new ItemStackTemplate(Items.DIAMOND_SWORD), 3, 30, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_SWORD)).build());
      register(context, TOOLSMITH_1_EMERALD_STONE_AXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_AXE), 12, 1, 0.2F).build());
      register(context, TOOLSMITH_1_EMERALD_STONE_SHOVEL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_SHOVEL), 12, 1, 0.2F).build());
      register(context, TOOLSMITH_1_EMERALD_STONE_PICKAXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_PICKAXE), 12, 1, 0.2F).build());
      register(context, TOOLSMITH_1_EMERALD_STONE_HOE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.STONE_HOE), 12, 1, 0.2F).build());
      register(context, TOOLSMITH_3_FLINT_EMERALD, VillagerTrade.builder(new TradeCost(Items.FLINT, 30), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, TOOLSMITH_3_EMERALD_IRON_AXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.IRON_AXE), 3, 10, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_AXE)).build());
      register(context, TOOLSMITH_3_EMERALD_IRON_SHOVEL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.IRON_SHOVEL), 3, 10, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_SHOVEL)).build());
      register(context, TOOLSMITH_3_EMERALD_IRON_PICKAXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.IRON_PICKAXE), 3, 10, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_PICKAXE)).build());
      register(context, TOOLSMITH_3_EMERALD_DIAMOND_HOE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.DIAMOND_HOE), 3, 10, 0.2F).build());
      register(context, TOOLSMITH_4_EMERALD_DIAMOND_AXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 12), new ItemStackTemplate(Items.DIAMOND_AXE), 3, 15, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_AXE)).build());
      register(context, TOOLSMITH_4_EMERALD_DIAMOND_SHOVEL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.DIAMOND_SHOVEL), 3, 15, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_SHOVEL)).build());
      register(context, TOOLSMITH_4_DIAMOND_EMERALD, VillagerTrade.builder(new TradeCost(Items.DIAMOND, 1), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, TOOLSMITH_5_EMERALD_DIAMOND_PICKAXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 13), new ItemStackTemplate(Items.DIAMOND_PICKAXE), 3, 30, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.DIAMOND_PICKAXE)).build());
      register(context, BUTCHER_1_CHICKEN_EMERALD, VillagerTrade.builder(new TradeCost(Items.CHICKEN, 14), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, BUTCHER_1_PORKCHOP_EMERALD, VillagerTrade.builder(new TradeCost(Items.PORKCHOP, 7), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, BUTCHER_1_RABBIT_EMERALD, VillagerTrade.builder(new TradeCost(Items.RABBIT, 4), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, BUTCHER_1_EMERALD_RABBIT_STEW, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.RABBIT_STEW), 12, 1, 0.05F).build());
      register(context, BUTCHER_2_COAL_EMERALD, VillagerTrade.builder(new TradeCost(Items.COAL, 15), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, BUTCHER_2_EMERALD_COOKED_PORKCHOP, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.COOKED_PORKCHOP, 5), 16, 5, 0.05F).build());
      register(context, BUTCHER_2_EMERALD_COOKED_CHICKEN, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.COOKED_CHICKEN, 8), 16, 5, 0.05F).build());
      register(context, BUTCHER_3_MUTTON_EMERALD, VillagerTrade.builder(new TradeCost(Items.MUTTON, 7), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F).build());
      register(context, BUTCHER_3_BEEF_EMERALD, VillagerTrade.builder(new TradeCost(Items.BEEF, 10), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F).build());
      register(context, BUTCHER_4_DRIED_KELP_BLOCK_EMERALD, VillagerTrade.builder(new TradeCost(Items.DRIED_KELP_BLOCK, 10), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, BUTCHER_5_SWEET_BERRIES_EMERALD, VillagerTrade.builder(new TradeCost(Items.SWEET_BERRIES, 10), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, LEATHERWORKER_1_LEATHER_EMERALD, VillagerTrade.builder(new TradeCost(Items.LEATHER, 6), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, LEATHERWORKER_1_EMERALD_DYED_LEATHER_LEGGINGS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.LEATHER_LEGGINGS), 12, 1, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_LEGGINGS)).build());
      register(context, LEATHERWORKER_1_EMERALD_DYED_LEATHER_CHESTPLATE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.LEATHER_CHESTPLATE), 12, 1, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_CHESTPLATE)).build());
      register(context, LEATHERWORKER_2_FLINT_EMERALD, VillagerTrade.builder(new TradeCost(Items.FLINT, 26), new ItemStackTemplate(Items.EMERALD), 12, 10, 0.05F).build());
      register(context, LEATHERWORKER_2_EMERALD_DYED_LEATHER_HELMET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.LEATHER_HELMET), 12, 5, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_HELMET)).build());
      register(context, LEATHERWORKER_2_EMERALD_DYED_LEATHER_BOOTS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.LEATHER_BOOTS), 12, 5, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_BOOTS)).build());
      register(context, LEATHERWORKER_3_RABBIT_HIDE_EMERALD, VillagerTrade.builder(new TradeCost(Items.RABBIT_HIDE, 9), new ItemStackTemplate(Items.EMERALD), 12, 20, 0.05F).build());
      register(context, LEATHERWORKER_3_EMERALD_DYED_LEATHER_CHESTPLATE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 7), new ItemStackTemplate(Items.LEATHER_CHESTPLATE), 12, 1, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_CHESTPLATE)).build());
      register(context, LEATHERWORKER_4_TURTLE_SCUTE_EMERALD, VillagerTrade.builder(new TradeCost(Items.TURTLE_SCUTE, 4), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      register(context, LEATHERWORKER_4_EMERALD_DYED_LEATHER_HORSE_ARMOR, VillagerTrade.builder(new TradeCost(Items.EMERALD, 6), new ItemStackTemplate(Items.LEATHER_HORSE_ARMOR), 12, 15, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_HORSE_ARMOR)).build());
      register(context, LEATHERWORKER_5_EMERALD_SADDLE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 6), new ItemStackTemplate(Items.SADDLE), 12, 30, 0.2F).build());
      register(context, LEATHERWORKER_5_EMERALD_DYED_LEATHER_HELMET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.LEATHER_HELMET), 12, 5, 0.2F).addModifiers(dyedItem(items, Items.LEATHER_HELMET)).build());
      register(context, MASON_1_CLAY_BALL_EMERALD, VillagerTrade.builder(new TradeCost(Items.CLAY_BALL, 10), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build());
      register(context, MASON_1_EMERALD_BRICK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BRICK, 10), 16, 1, 0.05F).build());
      register(context, MASON_2_STONE_EMERALD, VillagerTrade.builder(new TradeCost(Items.STONE, 20), new ItemStackTemplate(Items.EMERALD), 16, 10, 0.05F).build());
      register(context, MASON_2_EMERALD_CHISELED_STONE_BRICKS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.CHISELED_STONE_BRICKS, 4), 16, 5, 0.05F).build());
      registerMasonLevelThreeStones(context);
      registerMasonLevelThreeBlocks(context);
      register(context, MASON_4_QUARTZ_EMERALD, VillagerTrade.builder(new TradeCost(Items.QUARTZ, 12), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).build());
      registerMasonLevelFourTerracotta(context);
      register(context, MASON_5_EMERALD_QUARTZ_PILLAR, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.QUARTZ_PILLAR), 12, 30, 0.05F).build());
      register(context, MASON_5_EMERALD_QUARTZ_BLOCK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.QUARTZ_BLOCK), 12, 30, 0.05F).build());
      context.register(WANDERING_TRADER_WATER_BOTTLE_EMERALD, VillagerTrade.builder(new TradeCost(Items.POTION.builtInRegistryHolder(), ConstantValue.exactly(1.0F), DataComponentExactPredicate.expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER))), new ItemStackTemplate(Items.EMERALD), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_WATER_BUCKET_EMERALD, VillagerTrade.builder(new TradeCost(Items.WATER_BUCKET, 1), new ItemStackTemplate(Items.EMERALD, 2), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_MILK_BUCKET_EMERALD, VillagerTrade.builder(new TradeCost(Items.MILK_BUCKET, 1), new ItemStackTemplate(Items.EMERALD, 2), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_FERMENTED_SPIDER_EYE_EMERALD, VillagerTrade.builder(new TradeCost(Items.FERMENTED_SPIDER_EYE, 1), new ItemStackTemplate(Items.EMERALD, 3), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_BAKED_POTATO_EMERALD, VillagerTrade.builder(new TradeCost(Items.BAKED_POTATO, 4), new ItemStackTemplate(Items.EMERALD, 1), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_HAY_BLOCK_EMERALD, VillagerTrade.builder(new TradeCost(Items.HAY_BLOCK, 1), new ItemStackTemplate(Items.EMERALD), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_PACKED_ICE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PACKED_ICE), 6, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_BLUE_ICE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 6), new ItemStackTemplate(Items.BLUE_ICE), 6, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_GUNPOWDER, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.GUNPOWDER, 4), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_PODZOL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.PODZOL, 3), 6, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_ACACIA_LOG, createWanderingTraderLogSell(Items.ACACIA_LOG));
      context.register(WANDERING_TRADER_EMERALD_BIRCH_LOG, createWanderingTraderLogSell(Items.BIRCH_LOG));
      context.register(WANDERING_TRADER_EMERALD_DARK_OAK_LOG, createWanderingTraderLogSell(Items.DARK_OAK_LOG));
      context.register(WANDERING_TRADER_EMERALD_JUNGLE_LOG, createWanderingTraderLogSell(Items.JUNGLE_LOG));
      context.register(WANDERING_TRADER_EMERALD_OAK_LOG, createWanderingTraderLogSell(Items.OAK_LOG));
      context.register(WANDERING_TRADER_EMERALD_SPRUCE_LOG, createWanderingTraderLogSell(Items.SPRUCE_LOG));
      context.register(WANDERING_TRADER_EMERALD_CHERRY_LOG, createWanderingTraderLogSell(Items.CHERRY_LOG));
      context.register(WANDERING_TRADER_EMERALD_MANGROVE_LOG, createWanderingTraderLogSell(Items.MANGROVE_LOG));
      context.register(WANDERING_TRADER_EMERALD_PALE_OAK_LOG, createWanderingTraderLogSell(Items.PALE_OAK_LOG));
      context.register(WANDERING_TRADER_EMERALD_POPLAR_LOG, createWanderingTraderLogSell(Items.POPLAR_LOG));
      context.register(WANDERING_TRADER_EMERALD_ENCHANTED_IRON_PICKAXE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.IRON_PICKAXE), 1, 1, 0.2F).addModifiers(enchantedItem(items, enchantmentsForTradedEquipment, Items.IRON_PICKAXE)).build());
      context.register(WANDERING_TRADER_EMERALD_LONG_INVISIBILITY_POTION, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.POTION), 1, 1, 0.05F).addModifier(SetPotionFunction.setPotion(Potions.LONG_INVISIBILITY)).build());
      context.register(WANDERING_TRADER_EMERALD_TROPICAL_FISH_BUCKET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.TROPICAL_FISH_BUCKET), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_PUFFERFISH_BUCKET, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.PUFFERFISH_BUCKET), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_SEA_PICKLE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.SEA_PICKLE), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_SLIME_BALL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 4), new ItemStackTemplate(Items.SLIME_BALL), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_GLOWSTONE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.GLOWSTONE), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_NAUTILUS_SHELL, VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(Items.NAUTILUS_SHELL), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_FERN, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.FERN), 12, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_SUGAR_CANE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SUGAR_CANE), 8, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_PUMPKIN, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PUMPKIN), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_KELP, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.KELP), 12, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_CACTUS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.CACTUS), 8, 1, 0.05F).build());
      registerWanderingTraderFlowers(context);
      registerWanderingTraderSeeds(context);
      registerWanderingTraderSaplings(context);
      registerWanderingTraderDyes(context);
      context.register(WANDERING_TRADER_EMERALD_BRAIN_CORAL_BLOCK, createWanderingTraderCoralBlockSell(Items.BRAIN_CORAL_BLOCK));
      context.register(WANDERING_TRADER_EMERALD_BUBBLE_CORAL_BLOCK, createWanderingTraderCoralBlockSell(Items.BUBBLE_CORAL_BLOCK));
      context.register(WANDERING_TRADER_EMERALD_FIRE_CORAL_BLOCK, createWanderingTraderCoralBlockSell(Items.FIRE_CORAL_BLOCK));
      context.register(WANDERING_TRADER_EMERALD_HORN_CORAL_BLOCK, createWanderingTraderCoralBlockSell(Items.HORN_CORAL_BLOCK));
      context.register(WANDERING_TRADER_EMERALD_TUBE_CORAL_BLOCK, createWanderingTraderCoralBlockSell(Items.TUBE_CORAL_BLOCK));
      context.register(WANDERING_TRADER_EMERALD_VINE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.VINE, 3), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_PALE_HANGING_MOSS, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PALE_HANGING_MOSS, 3), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_BROWN_MUSHROOM, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.BROWN_MUSHROOM, 3), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_RED_MUSHROOM, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.RED_MUSHROOM, 3), 4, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_LILY_PAD, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.LILY_PAD, 5), 2, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_SMALL_DRIPLEAF, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SMALL_DRIPLEAF, 2), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_SAND, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SAND, 8), 8, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_RED_SAND, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.RED_SAND, 4), 6, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_POINTED_DRIPSTONE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.POINTED_DRIPSTONE, 2), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_SULFUR_SPIKE, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SULFUR_SPIKE, 2), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_ROOTED_DIRT, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.ROOTED_DIRT, 2), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_MOSS_BLOCK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.MOSS_BLOCK, 2), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_PALE_MOSS_BLOCK, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.PALE_MOSS_BLOCK, 2), 5, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_FIREFLY_BUSH, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(Items.FIREFLY_BUSH), 12, 1, 0.05F).build());
      context.register(WANDERING_TRADER_EMERALD_NAME_TAG, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.NAME_TAG), 5, 1, 0.05F).build());
      return context.register(WANDERING_TRADER_EMERALD_SHELF_MUSHROOM, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(Items.SHELF_MUSHROOM, 3), 12, 1, 0.05F).build());
   }

   private static VillagerTrade createWanderingTraderCoralBlockSell(final Item item) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(item), 8, 1, 0.05F).build();
   }

   private static VillagerTrade createWanderingTraderLogSell(final Item item) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(item, 8), 4, 1, 0.05F).build();
   }

   private static void registerWanderingTraderFlowers(final BootstrapContext<VillagerTrade> context) {
      context.register(WANDERING_TRADER_EMERALD_BLUE_ORCHID, createWanderingTraderFlowerSell(Items.BLUE_ORCHID, 8));
      context.register(WANDERING_TRADER_EMERALD_DANDELION, createWanderingTraderFlowerSell(Items.DANDELION, 12));
      context.register(WANDERING_TRADER_EMERALD_POPPY, createWanderingTraderFlowerSell(Items.POPPY, 12));
      context.register(WANDERING_TRADER_EMERALD_ALLIUM, createWanderingTraderFlowerSell(Items.ALLIUM, 12));
      context.register(WANDERING_TRADER_EMERALD_AZURE_BLUET, createWanderingTraderFlowerSell(Items.AZURE_BLUET, 12));
      context.register(WANDERING_TRADER_EMERALD_RED_TULIP, createWanderingTraderFlowerSell(Items.RED_TULIP, 12));
      context.register(WANDERING_TRADER_EMERALD_ORANGE_TULIP, createWanderingTraderFlowerSell(Items.ORANGE_TULIP, 12));
      context.register(WANDERING_TRADER_EMERALD_WHITE_TULIP, createWanderingTraderFlowerSell(Items.WHITE_TULIP, 12));
      context.register(WANDERING_TRADER_EMERALD_PINK_TULIP, createWanderingTraderFlowerSell(Items.PINK_TULIP, 12));
      context.register(WANDERING_TRADER_EMERALD_OXEYE_DAISY, createWanderingTraderFlowerSell(Items.OXEYE_DAISY, 12));
      context.register(WANDERING_TRADER_EMERALD_CORNFLOWER, createWanderingTraderFlowerSell(Items.CORNFLOWER, 12));
      context.register(WANDERING_TRADER_EMERALD_WILDFLOWERS, createWanderingTraderFlowerSell(Items.WILDFLOWERS, 12));
      context.register(WANDERING_TRADER_EMERALD_DRY_TALL_GRASS, createWanderingTraderFlowerSell(Items.DRY_TALL_GRASS, 12));
      context.register(WANDERING_TRADER_EMERALD_LILY_OF_THE_VALLEY, createWanderingTraderFlowerSell(Items.LILY_OF_THE_VALLEY, 7));
      context.register(WANDERING_TRADER_EMERALD_OPEN_EYEBLOSSOM, createWanderingTraderFlowerSell(Items.OPEN_EYEBLOSSOM, 7));
      context.register(WANDERING_TRADER_EMERALD_GOLDEN_DANDELION, VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(Items.GOLDEN_DANDELION), 12, 1, 0.05F).build());
   }

   private static VillagerTrade createWanderingTraderFlowerSell(final Item item, final int maxUses) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(item), maxUses, 1, 0.05F).build();
   }

   private static void registerWanderingTraderSeeds(final BootstrapContext<VillagerTrade> context) {
      context.register(WANDERING_TRADER_EMERALD_WHEAT_SEEDS, createWanderingTraderSeedSell(Items.WHEAT_SEEDS));
      context.register(WANDERING_TRADER_EMERALD_BEETROOT_SEEDS, createWanderingTraderSeedSell(Items.BEETROOT_SEEDS));
      context.register(WANDERING_TRADER_EMERALD_PUMPKIN_SEEDS, createWanderingTraderSeedSell(Items.PUMPKIN_SEEDS));
      context.register(WANDERING_TRADER_EMERALD_MELON_SEEDS, createWanderingTraderSeedSell(Items.MELON_SEEDS));
   }

   private static VillagerTrade createWanderingTraderSeedSell(final Item item) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(item), 12, 1, 0.05F).build();
   }

   private static void registerWanderingTraderSaplings(final BootstrapContext<VillagerTrade> context) {
      context.register(WANDERING_TRADER_EMERALD_ACACIA_SAPLING, createWanderingTraderSaplingSell(Items.ACACIA_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_BIRCH_SAPLING, createWanderingTraderSaplingSell(Items.BIRCH_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_DARK_OAK_SAPLING, createWanderingTraderSaplingSell(Items.DARK_OAK_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_JUNGLE_SAPLING, createWanderingTraderSaplingSell(Items.JUNGLE_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_OAK_SAPLING, createWanderingTraderSaplingSell(Items.OAK_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_SPRUCE_SAPLING, createWanderingTraderSaplingSell(Items.SPRUCE_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_CHERRY_SAPLING, createWanderingTraderSaplingSell(Items.CHERRY_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_PALE_OAK_SAPLING, createWanderingTraderSaplingSell(Items.PALE_OAK_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_POPLAR_SAPLING, createWanderingTraderSaplingSell(Items.POPLAR_SAPLING));
      context.register(WANDERING_TRADER_EMERALD_MANGROVE_PROPAGULE, createWanderingTraderSaplingSell(Items.MANGROVE_PROPAGULE));
   }

   private static VillagerTrade createWanderingTraderSaplingSell(final Item item) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 5), new ItemStackTemplate(item), 8, 1, 0.05F).build();
   }

   private static void registerWanderingTraderDyes(final BootstrapContext<VillagerTrade> context) {
      ColorCollection.zipApply(WANDERING_TRADER_EMERALD_DYE, Items.DYE, (name, dye) -> context.register(name, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(dye, 3), 12, 1, 0.05F).build()));
   }

   private static void registerMasonLevelFourTerracotta(final BootstrapContext<VillagerTrade> context) {
      registerWanderingTraderTerracottaSellTrades(context, MASON_4_EMERALD_TERRACOTTA, Items.DYED_TERRACOTTA);
      registerWanderingTraderTerracottaSellTrades(context, MASON_4_EMERALD_GLAZED_TERRACOTTA, Items.GLAZED_TERRACOTTA);
   }

   private static void registerWanderingTraderTerracottaSellTrades(final BootstrapContext<VillagerTrade> context, final ColorCollection<ResourceKey<VillagerTrade>> trades, final ColorCollection<Item> items) {
      ColorCollection.zipApply(trades, items, (trade, item) -> register(context, trade, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(item), 12, 15, 0.05F).build()));
   }

   private static void registerMasonLevelThreeBlocks(final BootstrapContext<VillagerTrade> context) {
      register(context, MASON_3_EMERALD_DRIPSTONE_BLOCK, createMasonStoneSell(Items.DRIPSTONE_BLOCK));
      register(context, MASON_3_EMERALD_POLISHED_ANDESITE, createMasonStoneSell(Items.POLISHED_ANDESITE));
      register(context, MASON_3_EMERALD_POLISHED_DIORITE, createMasonStoneSell(Items.POLISHED_DIORITE));
      register(context, MASON_3_EMERALD_POLISHED_GRANTITE, createMasonStoneSell(Items.POLISHED_GRANITE));
   }

   private static VillagerTrade createMasonStoneSell(final Item item) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(item, 4), 16, 10, 0.05F).build();
   }

   private static void registerMasonLevelThreeStones(final BootstrapContext<VillagerTrade> context) {
      register(context, MASON_3_GRANITE_EMERALD, createMasonStoneBuy(Items.GRANITE));
      register(context, MASON_3_ANDESITE_EMERALD, createMasonStoneBuy(Items.ANDESITE));
      register(context, MASON_3_DIORITE_EMERALD, createMasonStoneBuy(Items.DIORITE));
   }

   private static VillagerTrade createMasonStoneBuy(final Item item) {
      return VillagerTrade.builder(new TradeCost(item, 16), new ItemStackTemplate(Items.EMERALD), 16, 20, 0.05F).build();
   }

   private static void registerBoatTrades(final BootstrapContext<VillagerTrade> context, final HolderGetter<VillagerType> villagerVariants) {
      register(context, FISHERMAN_5_OAK_BOAT_EMERALD, createMasonBoatBuyTrade(villagerVariants, Items.OAK_BOAT, List.of(VillagerType.PLAINS)));
      register(context, FISHERMAN_5_SPRUCE_BOAT_EMERALD, createMasonBoatBuyTrade(villagerVariants, Items.SPRUCE_BOAT, List.of(VillagerType.TAIGA, VillagerType.SNOW)));
      register(context, FISHERMAN_5_JUNGLE_BOAT_EMERALD, createMasonBoatBuyTrade(villagerVariants, Items.JUNGLE_BOAT, List.of(VillagerType.DESERT, VillagerType.JUNGLE)));
      register(context, FISHERMAN_5_ACACIA_BOAT_EMERALD, createMasonBoatBuyTrade(villagerVariants, Items.ACACIA_BOAT, List.of(VillagerType.SAVANNA)));
      register(context, FISHERMAN_5_DARK_OAK_BOAT_EMERALD, createMasonBoatBuyTrade(villagerVariants, Items.DARK_OAK_BOAT, List.of(VillagerType.SWAMP)));
   }

   private static VillagerTrade createMasonBoatBuyTrade(final HolderGetter<VillagerType> villagerVariants, final Item item, final List<ResourceKey<VillagerType>> villagerTypes) {
      return VillagerTrade.builder(new TradeCost(item, 1), new ItemStackTemplate(Items.EMERALD), 12, 30, 0.05F).merchantPredicate(villagerTypeRestriction(villagerTypeHolderSet(villagerVariants, villagerTypes))).build();
   }

   private static void registerShepherdWoolSales(final BootstrapContext<VillagerTrade> context) {
      register(context, SHEPHERD_1_WHITE_WOOL_EMERALD, createShepherdWoolBuy(Items.WOOL.white()));
      register(context, SHEPHERD_1_BROWN_WOOL_EMERALD, createShepherdWoolBuy(Items.WOOL.brown()));
      register(context, SHEPHERD_1_GRAY_WOOL_EMERALD, createShepherdWoolBuy(Items.WOOL.gray()));
      register(context, SHEPHERD_1_BLACK_WOOL_EMERALD, createShepherdWoolBuy(Items.WOOL.black()));
   }

   private static VillagerTrade createShepherdWoolBuy(final Item item) {
      return VillagerTrade.builder(new TradeCost(item, 18), new ItemStackTemplate(Items.EMERALD), 16, 2, 0.05F).build();
   }

   private static void registerShepherdLevelTwoDyeTrades(final BootstrapContext<VillagerTrade> context) {
      register(context, SHEPHERD_2_WHITE_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.white(), 10));
      register(context, SHEPHERD_2_GRAY_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.gray(), 10));
      register(context, SHEPHERD_2_BLACK_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.black(), 10));
      register(context, SHEPHERD_2_LIGHT_BLUE_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.lightBlue(), 10));
      register(context, SHEPHERD_2_LIME_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.lime(), 10));
   }

   private static void registerShepherdLevelThreeDyeTrades(final BootstrapContext<VillagerTrade> context) {
      register(context, SHEPHERD_3_YELLOW_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.yellow(), 20));
      register(context, SHEPHERD_3_LIGHT_GRAY_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.lightGray(), 20));
      register(context, SHEPHERD_3_ORANGE_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.orange(), 20));
      register(context, SHEPHERD_3_RED_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.red(), 20));
      register(context, SHEPHERD_3_PINK_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.pink(), 20));
   }

   private static void registerLevelFourDyeTrades(final BootstrapContext<VillagerTrade> context) {
      register(context, SHEPHERD_4_BROWN_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.brown(), 30));
      register(context, SHEPHERD_4_PURPLE_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.purple(), 30));
      register(context, SHEPHERD_4_BLUE_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.blue(), 30));
      register(context, SHEPHERD_4_GREEN_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.green(), 30));
      register(context, SHEPHERD_4_MAGENTA_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.magenta(), 30));
      register(context, SHEPHERD_4_CYAN_DYE_EMERALD, createShepherdDyeBuy(Items.DYE.cyan(), 30));
   }

   private static VillagerTrade createShepherdDyeBuy(final Item item, final int xp) {
      return VillagerTrade.builder(new TradeCost(item, 12), new ItemStackTemplate(Items.EMERALD), 16, xp, 0.05F).build();
   }

   private static void registerWoolPurchases(final BootstrapContext<VillagerTrade> context) {
      ColorCollection.zipApply(SHEPHERD_2_EMERALD_WOOL, Items.WOOL, (name, wool) -> register(context, name, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(wool), 16, 5, 0.05F).build()));
   }

   private static void registerCarpetPurchases(final BootstrapContext<VillagerTrade> context) {
      ColorCollection.zipApply(SHEPHERD_2_EMERALD_CARPETS, Items.CARPET, (name, carpet) -> register(context, name, VillagerTrade.builder(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(carpet, 4), 16, 5, 0.05F).build()));
   }

   private static void registerBedTrades(final BootstrapContext<VillagerTrade> context) {
      ColorCollection.zipApply(SHEPHERD_3_EMERALD_BED, Items.BED, (name, bed) -> register(context, name, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(bed), 12, 10, 0.05F).build()));
   }

   private static void registerShepherdBannerTrades(final BootstrapContext<VillagerTrade> context) {
      ColorCollection.zipApply(SHEPHERD_4_EMERALD_BANNER, Items.BANNER, (name, banner) -> register(context, name, VillagerTrade.builder(new TradeCost(Items.EMERALD, 3), new ItemStackTemplate(banner), 12, 15, 0.05F).build()));
   }

   private static void registerCartographerBannerTrades(final BootstrapContext<VillagerTrade> context, final HolderGetter<VillagerType> villagerVariants) {
      register(context, CARTOGRAPHER_4_EMERALD_WHITE_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.white(), List.of(VillagerType.SNOW, VillagerType.PLAINS)));
      register(context, CARTOGRAPHER_4_EMERALD_ORANGE_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.orange(), List.of(VillagerType.SAVANNA, VillagerType.DESERT)));
      register(context, CARTOGRAPHER_4_EMERALD_MAGENTA_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.magenta(), List.of(VillagerType.SAVANNA)));
      register(context, CARTOGRAPHER_4_EMERALD_BLUE_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.blue(), List.of(VillagerType.SNOW, VillagerType.TAIGA)));
      register(context, CARTOGRAPHER_4_EMERALD_LIGHT_BLUE_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.lightBlue(), List.of(VillagerType.SNOW, VillagerType.SWAMP)));
      register(context, CARTOGRAPHER_4_EMERALD_YELLOW_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.yellow(), List.of(VillagerType.PLAINS, VillagerType.JUNGLE)));
      register(context, CARTOGRAPHER_4_EMERALD_LIME_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.lime(), List.of(VillagerType.DESERT, VillagerType.TAIGA)));
      register(context, CARTOGRAPHER_4_EMERALD_PINK_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.pink(), List.of(VillagerType.TAIGA, VillagerType.PLAINS)));
      register(context, CARTOGRAPHER_4_EMERALD_GRAY_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.gray(), List.of(VillagerType.DESERT)));
      register(context, CARTOGRAPHER_4_EMERALD_CYAN_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.cyan(), List.of(VillagerType.DESERT, VillagerType.SNOW)));
      register(context, CARTOGRAPHER_4_EMERALD_PURPLE_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.purple(), List.of(VillagerType.TAIGA, VillagerType.SWAMP)));
      register(context, CARTOGRAPHER_4_EMERALD_BROWN_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.brown(), List.of(VillagerType.PLAINS, VillagerType.JUNGLE)));
      register(context, CARTOGRAPHER_4_EMERALD_GREEN_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.green(), List.of(VillagerType.DESERT, VillagerType.SAVANNA, VillagerType.JUNGLE)));
      register(context, CARTOGRAPHER_4_EMERALD_RED_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.red(), List.of(VillagerType.SNOW, VillagerType.SAVANNA)));
      register(context, CARTOGRAPHER_4_EMERALD_BLACK_BANNER, createCartographerBannerSell(villagerVariants, Items.BANNER.black(), List.of(VillagerType.SWAMP)));
   }

   private static VillagerTrade createCartographerBannerSell(final HolderGetter<VillagerType> villagerVariants, final Item item, final List<ResourceKey<VillagerType>> villagerTypes) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 2), new ItemStackTemplate(item), 12, 15, 0.05F).merchantPredicate(villagerTypeRestriction(villagerTypeHolderSet(villagerVariants, villagerTypes))).build();
   }

   private static void registerBasicExplorerMapTrades(final BootstrapContext<VillagerTrade> context, final HolderGetter<VillagerType> villagerVariants, final HolderGetter<Structure> structures) {
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_TAIGA_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_TAIGA_VILLAGE_MAPS, MapDecorationTypes.TAIGA_VILLAGE, Items.TAIGA_VILLAGE_MAP, List.of(VillagerType.SWAMP, VillagerType.SNOW, VillagerType.PLAINS)));
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_SWAMP_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_SWAMP_EXPLORER_MAPS, MapDecorationTypes.SWAMP_HUT, Items.SWAMP_EXPLORER_MAP, List.of(VillagerType.TAIGA, VillagerType.SNOW, VillagerType.JUNGLE)));
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SNOWY_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_SNOWY_VILLAGE_MAPS, MapDecorationTypes.SNOWY_VILLAGE, Items.SNOWY_VILLAGE_MAP, List.of(VillagerType.TAIGA, VillagerType.SWAMP)));
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SAVANNA_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_SAVANNA_VILLAGE_MAPS, MapDecorationTypes.SAVANNA_VILLAGE, Items.SAVANNA_VILLAGE_MAP, List.of(VillagerType.PLAINS, VillagerType.JUNGLE, VillagerType.DESERT)));
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_PLAINS_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_PLAINS_VILLAGE_MAPS, MapDecorationTypes.PLAINS_VILLAGE, Items.PLAINS_VILLAGE_MAP, List.of(VillagerType.TAIGA, VillagerType.SNOW, VillagerType.SAVANNA, VillagerType.DESERT)));
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_JUNGLE_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_JUNGLE_EXPLORER_MAPS, MapDecorationTypes.JUNGLE_TEMPLE, Items.JUNGLE_EXPLORER_MAP, List.of(VillagerType.SWAMP, VillagerType.SAVANNA, VillagerType.DESERT)));
      register(context, CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_DESERT_MAP, createBasicExplorerMapTrades(villagerVariants, structures, StructureTags.ON_DESERT_VILLAGE_MAPS, MapDecorationTypes.DESERT_VILLAGE, Items.DESERT_VILLAGE_MAP, List.of(VillagerType.SAVANNA, VillagerType.JUNGLE)));
   }

   private static VillagerTrade createBasicExplorerMapTrades(final HolderGetter<VillagerType> villagerVariants, final HolderGetter<Structure> structures, final TagKey<Structure> structureTagKey, final Holder<MapDecorationType> mapDecorationType, final Item mapItem, final List<ResourceKey<VillagerType>> villagerTypes) {
      return VillagerTrade.builder(new TradeCost(Items.EMERALD, 8), new TradeCost(Items.COMPASS, 1), new ItemStackTemplate(mapItem), 12, 5, 0.2F).merchantPredicate(villagerTypeRestriction(villagerTypeHolderSet(villagerVariants, villagerTypes))).addModifiers(Holder.direct(ExplorationMapFunction.makeExplorationMap(structures.getOrThrow(structureTagKey)).setMapDecoration(mapDecorationType).setSearchRadius(100).setSkipKnownStructures(true).build()), discardItemIfItsNot(anyValidMap())).build();
   }

   public static Holder.Reference<VillagerTrade> register(final BootstrapContext<VillagerTrade> context, final ResourceKey<VillagerTrade> resourceKey, final VillagerTrade villagerTrade) {
      return context.register(resourceKey, villagerTrade);
   }

   public static ResourceKey<VillagerTrade> resourceKey(final String path) {
      return ResourceKey.create(Registries.VILLAGER_TRADE, Identifier.withDefaultNamespace(path));
   }

   public static HolderSet<VillagerType> villagerTypeHolderSet(final HolderGetter<VillagerType> villagerVariants, final ResourceKey<VillagerType> resourceKey) {
      return HolderSet.direct(villagerVariants.getOrThrow(resourceKey));
   }

   public static HolderSet<VillagerType> villagerTypeHolderSet(final HolderGetter<VillagerType> villagerVariants, final List<ResourceKey<VillagerType>> resourceKeys) {
      List<Holder<VillagerType>> villagerTypes = new ArrayList();

      for(ResourceKey<VillagerType> resourceKey : resourceKeys) {
         villagerTypes.add(villagerVariants.getOrThrow(resourceKey));
      }

      return HolderSet.direct(villagerTypes);
   }

   public static Holder<LootItemCondition> villagerTypeRestriction(final HolderSet<VillagerType> villagerTypes) {
      return Holder.<LootItemCondition>direct(new LootItemEntityPropertyCondition(Optional.of(EntityPredicate.Builder.entity().components(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.VILLAGER_VARIANT, VillagerTypePredicate.villagerTypes(villagerTypes)).build()).build()), LootContext.EntityTarget.THIS));
   }

   private static List<Holder<LootItemFunction>> dyedItem(final HolderGetter<Item> items, final Item expectedItem) {
      ItemPredicate.Builder anyDyedItem = (new ItemPredicate.Builder()).of(items, expectedItem).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.DYED_COLOR).build());
      return discardItemIfItsNot(addRandomDye(), anyDyedItem);
   }

   private static LootItemFunction.Builder addRandomDye() {
      return SetRandomDyesFunction.withCount(Sum.sum(ConstantValue.exactly(1.0F), Holder.direct(new BinomialDistributionGenerator(ConstantValue.exactly(2.0F), ConstantValue.exactly(0.75F)))));
   }

   public static List<Holder<LootItemFunction>> enchantedBook(final HolderGetter<Item> items, final HolderSet<Enchantment> options) {
      ItemPredicate.Builder bookWithAnyEnchants = (new ItemPredicate.Builder()).of(items, Items.ENCHANTED_BOOK).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.STORED_ENCHANTMENTS, EnchantmentsPredicate.storedEnchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY)))).build());
      return discardItemIfItsNot((new EnchantRandomlyFunction.Builder()).withOptions(options).allowingIncompatibleEnchantments().includeAdditionalCostComponent(), bookWithAnyEnchants);
   }

   public static List<Holder<LootItemFunction>> enchantedBook(final HolderGetter<Item> items, final Holder<Enchantment> enchantment, final int level) {
      ItemPredicate.Builder bookWithExactLevelEnchants = (new ItemPredicate.Builder()).of(items, Items.ENCHANTED_BOOK).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.STORED_ENCHANTMENTS, EnchantmentsPredicate.storedEnchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.exactly(level))))).build());
      return discardItemIfItsNot((new SetEnchantmentsFunction.Builder()).withEnchantment(enchantment, ConstantValue.exactly((float)level)), bookWithExactLevelEnchants);
   }

   public static List<Holder<LootItemFunction>> enchantedItem(final HolderGetter<Item> items, final HolderSet<Enchantment> options, final Item expectedItem) {
      ItemPredicate.Builder itemWithAnyEnchants = (new ItemPredicate.Builder()).of(items, expectedItem).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.ANY)))).build());
      return discardItemIfItsNot((new EnchantWithLevelsFunction.Builder(UniformGenerator.between(5.0F, 19.0F))).withOptions(options).includeAdditionalCostComponent(), itemWithAnyEnchants);
   }

   public static List<Holder<LootItemFunction>> enchantedItem(final HolderGetter<Item> items, final Holder<Enchantment> enchantment, final int level, final Item expectedItem) {
      ItemPredicate.Builder itemWithExactLevelEnchants = (new ItemPredicate.Builder()).of(items, expectedItem).withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(Optional.empty(), MinMaxBounds.Ints.exactly(level))))).build());
      return discardItemIfItsNot((new SetEnchantmentsFunction.Builder()).withEnchantment(enchantment, ConstantValue.exactly((float)level)), itemWithExactLevelEnchants);
   }

   public static ItemPredicate.Builder anyValidMap() {
      return (new ItemPredicate.Builder()).withComponents(DataComponentMatchers.Builder.components().any(DataComponents.MAP_ID).build());
   }

   public static Holder<LootItemFunction> discardItemIfItsNot(final ItemPredicate.Builder test) {
      return Holder.<LootItemFunction>direct(FilteredFunction.filtered(test.build()).onFail(DiscardItem.discardItem().build()).build());
   }

   public static List<Holder<LootItemFunction>> discardItemIfItsNot(final LootItemFunction.Builder function, final ItemPredicate.Builder preserveCondition) {
      return List.of(Holder.direct(function.build()), discardItemIfItsNot(preserveCondition));
   }

   static {
      SHEPHERD_2_EMERALD_WOOL = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("shepherd/2/emerald_" + color + "_wool"));
      SHEPHERD_2_EMERALD_CARPETS = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("shepherd/2/emerald_" + color + "_carpet"));
      SHEPHERD_3_YELLOW_DYE_EMERALD = resourceKey("shepherd/3/yellow_dye_emerald");
      SHEPHERD_3_LIGHT_GRAY_DYE_EMERALD = resourceKey("shepherd/3/light_gray_dye_emerald");
      SHEPHERD_3_ORANGE_DYE_EMERALD = resourceKey("shepherd/3/orange_dye_emerald");
      SHEPHERD_3_RED_DYE_EMERALD = resourceKey("shepherd/3/red_dye_emerald");
      SHEPHERD_3_PINK_DYE_EMERALD = resourceKey("shepherd/3/pink_dye_emerald");
      SHEPHERD_3_EMERALD_BED = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("shepherd/3/emerald_" + color + "_bed"));
      SHEPHERD_4_BROWN_DYE_EMERALD = resourceKey("shepherd/4/brown_dye_emerald");
      SHEPHERD_4_PURPLE_DYE_EMERALD = resourceKey("shepherd/4/purple_dye_emerald");
      SHEPHERD_4_BLUE_DYE_EMERALD = resourceKey("shepherd/4/blue_dye_emerald");
      SHEPHERD_4_GREEN_DYE_EMERALD = resourceKey("shepherd/4/green_dye_emerald");
      SHEPHERD_4_MAGENTA_DYE_EMERALD = resourceKey("shepherd/4/magenta_dye_emerald");
      SHEPHERD_4_CYAN_DYE_EMERALD = resourceKey("shepherd/4/cyan_dye_emerald");
      SHEPHERD_4_EMERALD_BANNER = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("shepherd/4/emerald_" + color + "_banner"));
      SHEPHERD_5_EMERALD_PAINTING = resourceKey("shepherd/5/emerald_painting");
      FLETCHER_1_STICK_EMERALD = resourceKey("fletcher/1/stick_emerald");
      FLETCHER_1_EMERALD_ARROW = resourceKey("fletcher/1/emerald_arrow");
      FLETCHER_1_GRAVEL_AND_EMERALD_FLINT = resourceKey("fletcher/1/gravel_and_emerald_flint");
      FLETCHER_2_FLINT_EMERALD = resourceKey("fletcher/2/flint_emerald");
      FLETCHER_2_EMERALD_BOW = resourceKey("fletcher/2/emerald_bow");
      FLETCHER_3_STRING_EMERALD = resourceKey("fletcher/3/string_emerald");
      FLETCHER_3_EMERALD_CROSSBOW = resourceKey("fletcher/3/emerald_crossbow");
      FLETCHER_4_FEATHER_EMERALD = resourceKey("fletcher/4/feather_emerald");
      FLETCHER_4_EMERALD_ENCHANTED_BOW = resourceKey("fletcher/4/emerald_enchanted_bow");
      FLETCHER_5_TRIPWIRE_HOOK_EMERALD = resourceKey("fletcher/5/tripwire_hook_emerald");
      FLETCHER_5_EMERALD_ENCHANTED_CROSSBOW = resourceKey("fletcher/5/emerald_enchanted_crossbow");
      FLETCHER_5_ARROW_AND_EMERALD_TIPPED_ARROW = resourceKey("fletcher/5/arrow_and_emerald_tipped_arrow");
      LIBRARIAN_1_PAPER_EMERALD = resourceKey("librarian/1/paper_emerald");
      LIBRARIAN_1_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/1/emerald_and_book_enchanted_book");
      LIBRARIAN_1_EMERALD_BOOKSHELF = resourceKey("librarian/1/emerald_bookshelf");
      LIBRARIAN_2_BOOK_EMERALD = resourceKey("librarian/2/book_emerald");
      LIBRARIAN_2_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/2/emerald_and_book_enchanted_book");
      LIBRARIAN_2_EMERALD_LANTERN = resourceKey("librarian/2/emerald_lantern");
      LIBRARIAN_3_INK_SAC_EMERALD = resourceKey("librarian/3/ink_sac_emerald");
      LIBRARIAN_3_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/3/emerald_and_book_enchanted_book");
      LIBRARIAN_3_EMERALD_GLASS = resourceKey("librarian/3/emerald_glass");
      LIBRARIAN_4_WRITABLE_BOOK_EMERALD = resourceKey("librarian/4/writable_book_emerald");
      LIBRARIAN_4_EMERALD_AND_BOOK_ENCHANTED_BOOK = resourceKey("librarian/4/emerald_book_and_enchanted_book");
      LIBRARIAN_4_EMERALD_CLOCK = resourceKey("librarian/4/emerald_clock");
      LIBRARIAN_4_EMERALD_COMPASS = resourceKey("librarian/4/emerald_compass");
      LIBRARIAN_5_EMERALD_YELLOW_CANDLE = resourceKey("librarian/5/emerald_yellow_candle");
      LIBRARIAN_5_EMERALD_RED_CANDLE = resourceKey("librarian/5/emerald_red_candle");
      CARTOGRAPHER_1_PAPER_EMERALD = resourceKey("cartographer/1/paper_emerald");
      CARTOGRAPHER_1_EMERALD_MAP = resourceKey("cartographer/1/emerald_map");
      CARTOGRAPHER_2_GLASS_PANE_EMERALD = resourceKey("cartographer/2/glass_pane_emerald");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_TAIGA_MAP = resourceKey("cartographer/2/emerald_and_compass_village_taiga_map");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_SWAMP_MAP = resourceKey("cartographer/2/emerald_and_compass_explorer_swamp_map");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SNOWY_MAP = resourceKey("cartographer/2/emerald_and_compass_village_snowy_map");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_SAVANNA_MAP = resourceKey("cartographer/2/emerald_and_compass_village_savanna_map");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_PLAINS_MAP = resourceKey("cartographer/2/emerald_and_compass_village_plains_map");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_EXPLORER_JUNGLE_MAP = resourceKey("cartographer/2/emerald_and_compass_explorer_jungle_map");
      CARTOGRAPHER_2_EMERALD_AND_COMPASS_VILLAGE_DESERT_MAP = resourceKey("cartographer/2/emerald_and_compass_village_desert_map");
      CARTOGRAPHER_3_COMPASS_EMERALD = resourceKey("cartographer/3/compass_emerald");
      CARTOGRAPHER_3_EMERALD_AND_COMPASS_OCEAN_EXPLORER_MAP = resourceKey("cartographer/3/emerald_and_compass_ocean_explorer_map");
      CARTOGRAPHER_3_EMERALD_AND_COMPASS_TRIAL_CHAMBER_MAP = resourceKey("cartographer/3/emerald_and_compass_trial_chamber_map");
      CARTOGRAPHER_4_EMERALD_ITEM_FRAME = resourceKey("cartographer/4/emerald_item_frame");
      CARTOGRAPHER_4_EMERALD_WHITE_BANNER = resourceKey("cartographer/4/emerald_white_banner");
      CARTOGRAPHER_4_EMERALD_ORANGE_BANNER = resourceKey("cartographer/4/emerald_orange_banner");
      CARTOGRAPHER_4_EMERALD_MAGENTA_BANNER = resourceKey("cartographer/4/emerald_magenta_banner");
      CARTOGRAPHER_4_EMERALD_BLUE_BANNER = resourceKey("cartographer/4/emerald_blue_banner");
      CARTOGRAPHER_4_EMERALD_LIGHT_BLUE_BANNER = resourceKey("cartographer/4/emerald_light_blue_banner");
      CARTOGRAPHER_4_EMERALD_YELLOW_BANNER = resourceKey("cartographer/4/emerald_yellow_banner");
      CARTOGRAPHER_4_EMERALD_LIME_BANNER = resourceKey("cartographer/4/emerald_lime_banner");
      CARTOGRAPHER_4_EMERALD_PINK_BANNER = resourceKey("cartographer/4/emerald_pink_banner");
      CARTOGRAPHER_4_EMERALD_GRAY_BANNER = resourceKey("cartographer/4/emerald_gray_banner");
      CARTOGRAPHER_4_EMERALD_CYAN_BANNER = resourceKey("cartographer/4/emerald_cyan_banner");
      CARTOGRAPHER_4_EMERALD_PURPLE_BANNER = resourceKey("cartographer/4/emerald_purple_banner");
      CARTOGRAPHER_4_EMERALD_BROWN_BANNER = resourceKey("cartographer/4/emerald_brown_banner");
      CARTOGRAPHER_4_EMERALD_GREEN_BANNER = resourceKey("cartographer/4/emerald_green_banner");
      CARTOGRAPHER_4_EMERALD_RED_BANNER = resourceKey("cartographer/4/emerald_red_banner");
      CARTOGRAPHER_4_EMERALD_BLACK_BANNER = resourceKey("cartographer/4/emerald_black_banner");
      CARTOGRAPHER_5_EMERALD_GLOBE_BANNER_PATTERN = resourceKey("cartographer/5/emerald_globe_banner_pattern");
      CARTOGRAPHER_5_EMERALD_AND_COMPASS_WOODLAND_MANSION_MAP = resourceKey("cartographer/5/emerald_and_compass_woodland_mansion_map");
      CLERIC_1_ROTTEN_FLESH_EMERALD = resourceKey("cleric/1/rotten_flesh_emerald");
      CLERIC_1_EMERALD_REDSTONE = resourceKey("cleric/1/emerald_redstone");
      CLERIC_2_GOLD_INGOT_EMERALD = resourceKey("cleric/2/gold_ingot_emerald");
      CLERIC_2_EMERALD_LAPIS_LAZULI = resourceKey("cleric/2/emerald_lapis_lazuli");
      CLERIC_3_RABBIT_FOOT_EMERALD = resourceKey("cleric/3/rabbit_foot_emerald");
      CLERIC_3_EMERALD_GLOWSTONE = resourceKey("cleric/3/emerald_glowstone");
      CLERIC_4_TURTLE_SCUTE_EMERALD = resourceKey("cleric/4/turtle_scute_emerald");
      CLERIC_4_GLASS_BOTTLE_EMERALD = resourceKey("cleric/4/glass_bottle_emerald");
      CLERIC_4_EMERALD_ENDER_PEARL = resourceKey("cleric/4/emerald_ender_pearl");
      CLERIC_5_NETHER_WART_EMERALD = resourceKey("cleric/5/nether_wart_emerald");
      CLERIC_5_EMERALD_EXPERIENCE_BOTTLE = resourceKey("cleric/5/emerald_experience_bottle");
      COMMON_SMITH_1_COAL_EMERALD = resourceKey("smith/1/coal_emerald");
      COMMON_SMITH_2_IRON_INGOT_EMERALD = resourceKey("smith/2/iron_ingot_emerald");
      COMMON_SMITH_2_EMERALD_BELL = resourceKey("smith/2/emerald_bell");
      ARMORER_1_EMERALD_IRON_LEGGINGS = resourceKey("armorer/1/emerald_iron_leggings");
      ARMORER_1_EMERALD_IRON_BOOTS = resourceKey("armorer/1/emerald_iron_boots");
      ARMORER_1_EMERALD_IRON_HELMET = resourceKey("armorer/1/emerald_iron_helmet");
      ARMORER_1_EMERALD_IRON_CHESTPLATE = resourceKey("armorer/1/emerald_iron_chestplate");
      ARMORER_2_EMERALD_CHAINMAIL_BOOTS = resourceKey("armorer/2/emerald_chainmail_boots");
      ARMORER_2_EMERALD_CHAINMAIL_LEGGINGS = resourceKey("armorer/2/emerald_chainmail_leggings");
      ARMORER_3_LAVA_BUCKET_EMERALD = resourceKey("armorer/3/lava_bucket_emerald");
      ARMORER_3_EMERALD_CHAINMAIL_HELMET = resourceKey("armorer/3/emerald_chainmail_helmet");
      ARMORER_3_EMERALD_CHAINMAIL_CHESTPLATE = resourceKey("armorer/3/emerald_chainmail_chestplate");
      ARMORER_3_EMERALD_SHIELD = resourceKey("armorer/3/emerald_shield");
      ARMORER_3_DIAMOND_EMERALD = resourceKey("armorer/3/diamond_emerald");
      ARMORER_4_EMERALD_ENCHANTED_DIAMOND_LEGGINGS = resourceKey("armorer/4/emerald_enchanted_diamond_leggings");
      ARMORER_4_EMERALD_ENCHANTED_DIAMOND_BOOTS = resourceKey("armorer/4/emerald_enchanted_diamond_boots");
      ARMORER_5_EMERALD_ENCHANTED_DIAMOND_HELMET = resourceKey("armorer/5/emerald_enchanted_diamond_helmet");
      ARMORER_5_EMERALD_ENCHANTED_DIAMOND_CHESTPLATE = resourceKey("armorer/5/emerald_enchanted_diamond_chestplate");
      WEAPONSMITH_1_EMERALD_IRON_AXE = resourceKey("weaponsmith/1/emerald_iron_axe");
      WEAPONSMITH_1_EMERALD_ENCHANTED_IRON_SWORD = resourceKey("weaponsmith/1/emerald_enchanted_iron_sword");
      WEAPONSMITH_3_FLINT_EMERALD = resourceKey("weaponsmith/3/flint_emerald");
      WEAPONSMITH_4_DIAMOND_EMERALD = resourceKey("weaponsmith/4/diamond_emerald");
      WEAPONSMITH_4_EMERALD_ENCHANTED_DIAMOND_AXE = resourceKey("weaponsmith/4/emerald_enchanted_diamond_axe");
      WEAPONSMITH_5_EMERALD_ENCHANTED_DIAMOND_SWORD = resourceKey("weaponsmith/5/emerald_enchanted_diamond_sword");
      TOOLSMITH_1_EMERALD_STONE_AXE = resourceKey("toolsmith/1/emerald_stone_axe");
      TOOLSMITH_1_EMERALD_STONE_SHOVEL = resourceKey("toolsmith/1/emerald_stone_shovel");
      TOOLSMITH_1_EMERALD_STONE_PICKAXE = resourceKey("toolsmith/1/emerald_stone_pickaxe");
      TOOLSMITH_1_EMERALD_STONE_HOE = resourceKey("toolsmith/1/emerald_stone_hoe");
      TOOLSMITH_3_FLINT_EMERALD = resourceKey("toolsmith/3/flint_emerald");
      TOOLSMITH_3_EMERALD_IRON_AXE = resourceKey("toolsmith/3/emerald_enchanted_iron_axe");
      TOOLSMITH_3_EMERALD_IRON_SHOVEL = resourceKey("toolsmith/3/emerald_enchanted_iron_shovel");
      TOOLSMITH_3_EMERALD_IRON_PICKAXE = resourceKey("toolsmith/3/emerald_enchanted_iron_pickaxe");
      TOOLSMITH_3_EMERALD_DIAMOND_HOE = resourceKey("toolsmith/3/emerald_diamond_hoe");
      TOOLSMITH_4_DIAMOND_EMERALD = resourceKey("toolsmith/4/diamond_emerald");
      TOOLSMITH_4_EMERALD_DIAMOND_AXE = resourceKey("toolsmith/4/emerald_enchanted_diamond_axe");
      TOOLSMITH_4_EMERALD_DIAMOND_SHOVEL = resourceKey("toolsmith/4/emerald_enchanted_diamond_shovel");
      TOOLSMITH_5_EMERALD_DIAMOND_PICKAXE = resourceKey("toolsmith/5/emerald_enchanted_diamond_pickaxe");
      BUTCHER_1_CHICKEN_EMERALD = resourceKey("butcher/1/chicken_emerald");
      BUTCHER_1_PORKCHOP_EMERALD = resourceKey("butcher/1/porkchop_emerald");
      BUTCHER_1_RABBIT_EMERALD = resourceKey("butcher/1/rabbit_emerald");
      BUTCHER_1_EMERALD_RABBIT_STEW = resourceKey("butcher/1/emerald_rabbit_stew");
      BUTCHER_2_COAL_EMERALD = resourceKey("butcher/2/coal_emerald");
      BUTCHER_2_EMERALD_COOKED_PORKCHOP = resourceKey("butcher/2/emerald_cooked_porkchop");
      BUTCHER_2_EMERALD_COOKED_CHICKEN = resourceKey("butcher/2/emerald_cooked_chicken");
      BUTCHER_3_MUTTON_EMERALD = resourceKey("butcher/3/mutton_emerald");
      BUTCHER_3_BEEF_EMERALD = resourceKey("butcher/3/beef_emerald");
      BUTCHER_4_DRIED_KELP_BLOCK_EMERALD = resourceKey("butcher/4/dried_kelp_block_emerald");
      BUTCHER_5_SWEET_BERRIES_EMERALD = resourceKey("butcher/5/sweet_berries_emerald");
      LEATHERWORKER_1_LEATHER_EMERALD = resourceKey("leatherworker/1/leather_emerald");
      LEATHERWORKER_1_EMERALD_DYED_LEATHER_LEGGINGS = resourceKey("leatherworker/1/emerald_dyed_leather_leggings");
      LEATHERWORKER_1_EMERALD_DYED_LEATHER_CHESTPLATE = resourceKey("leatherworker/1/emerald_dyed_leather_chestplate");
      LEATHERWORKER_2_FLINT_EMERALD = resourceKey("leatherworker/2/flint_emerald");
      LEATHERWORKER_2_EMERALD_DYED_LEATHER_HELMET = resourceKey("leatherworker/2/emerald_dyed_leather_helmet");
      LEATHERWORKER_2_EMERALD_DYED_LEATHER_BOOTS = resourceKey("leatherworker/2/emerald_dyed_leather_boots");
      LEATHERWORKER_3_RABBIT_HIDE_EMERALD = resourceKey("leatherworker/3/rabbit_hide_emerald");
      LEATHERWORKER_3_EMERALD_DYED_LEATHER_CHESTPLATE = resourceKey("leatherworker/3/emerald_dyed_leather_chestplate");
      LEATHERWORKER_4_TURTLE_SCUTE_EMERALD = resourceKey("leatherworker/4/turtle_scute_emerald");
      LEATHERWORKER_4_EMERALD_DYED_LEATHER_HORSE_ARMOR = resourceKey("leatherworker/4/emerald_dyed_leather_horse_armor");
      LEATHERWORKER_5_EMERALD_SADDLE = resourceKey("leatherworker/5/emerald_saddle");
      LEATHERWORKER_5_EMERALD_DYED_LEATHER_HELMET = resourceKey("leatherworker/5/emerald_dyed_leather_helmet");
      MASON_1_CLAY_BALL_EMERALD = resourceKey("mason/1/clay_ball_emerald");
      MASON_1_EMERALD_BRICK = resourceKey("mason/1/emerald_brick");
      MASON_2_STONE_EMERALD = resourceKey("mason/2/stone_emerald");
      MASON_2_EMERALD_CHISELED_STONE_BRICKS = resourceKey("mason/2/emerald_chiseled_stone_bricks");
      MASON_3_GRANITE_EMERALD = resourceKey("mason/3/granite_emerald");
      MASON_3_ANDESITE_EMERALD = resourceKey("mason/3/andesite_emerald");
      MASON_3_DIORITE_EMERALD = resourceKey("mason/3/diorite_emerald");
      MASON_3_EMERALD_DRIPSTONE_BLOCK = resourceKey("mason/3/emerald_dripstone_block");
      MASON_3_EMERALD_POLISHED_ANDESITE = resourceKey("mason/3/emerald_polished_andesite");
      MASON_3_EMERALD_POLISHED_DIORITE = resourceKey("mason/3/emerald_polished_diorite");
      MASON_3_EMERALD_POLISHED_GRANTITE = resourceKey("mason/3/emerald_polished_granite");
      MASON_4_QUARTZ_EMERALD = resourceKey("mason/4/quartz_emerald");
      MASON_4_EMERALD_TERRACOTTA = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("mason/4/emerald_" + color + "_terracotta"));
      MASON_4_EMERALD_GLAZED_TERRACOTTA = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("mason/4/emerald_" + color + "_glazed_terracotta"));
      MASON_5_EMERALD_QUARTZ_PILLAR = resourceKey("mason/5/emerald_quartz_pillar");
      MASON_5_EMERALD_QUARTZ_BLOCK = resourceKey("mason/5/emerald_quartz_block");
      WANDERING_TRADER_WATER_BOTTLE_EMERALD = resourceKey("wandering_trader/water_bottle_emerald");
      WANDERING_TRADER_WATER_BUCKET_EMERALD = resourceKey("wandering_trader/water_bucket_emerald");
      WANDERING_TRADER_MILK_BUCKET_EMERALD = resourceKey("wandering_trader/milk_bucket_emerald");
      WANDERING_TRADER_FERMENTED_SPIDER_EYE_EMERALD = resourceKey("wandering_trader/fermented_spider_eye_emerald");
      WANDERING_TRADER_BAKED_POTATO_EMERALD = resourceKey("wandering_trader/baked_potato_emerald");
      WANDERING_TRADER_HAY_BLOCK_EMERALD = resourceKey("wandering_trader/hay_block_emerald");
      WANDERING_TRADER_EMERALD_PACKED_ICE = resourceKey("wandering_trader/emerald_packed_ice");
      WANDERING_TRADER_EMERALD_BLUE_ICE = resourceKey("wandering_trader/emerald_blue_ice");
      WANDERING_TRADER_EMERALD_GUNPOWDER = resourceKey("wandering_trader/emerald_gunpowder");
      WANDERING_TRADER_EMERALD_PODZOL = resourceKey("wandering_trader/emerald_podzol");
      WANDERING_TRADER_EMERALD_ACACIA_LOG = resourceKey("wandering_trader/emerald_acacia_log");
      WANDERING_TRADER_EMERALD_BIRCH_LOG = resourceKey("wandering_trader/emerald_birch_log");
      WANDERING_TRADER_EMERALD_DARK_OAK_LOG = resourceKey("wandering_trader/emerald_dark_oak_log");
      WANDERING_TRADER_EMERALD_JUNGLE_LOG = resourceKey("wandering_trader/emerald_jungle_log");
      WANDERING_TRADER_EMERALD_OAK_LOG = resourceKey("wandering_trader/emerald_oak_log");
      WANDERING_TRADER_EMERALD_SPRUCE_LOG = resourceKey("wandering_trader/emerald_spruce_log");
      WANDERING_TRADER_EMERALD_CHERRY_LOG = resourceKey("wandering_trader/emerald_cherry_log");
      WANDERING_TRADER_EMERALD_MANGROVE_LOG = resourceKey("wandering_trader/emerald_mangrove_log");
      WANDERING_TRADER_EMERALD_PALE_OAK_LOG = resourceKey("wandering_trader/emerald_pale_oak_log");
      WANDERING_TRADER_EMERALD_POPLAR_LOG = resourceKey("wandering_trader/emerald_poplar_log");
      WANDERING_TRADER_EMERALD_ENCHANTED_IRON_PICKAXE = resourceKey("wandering_trader/emerald_enchanted_iron_pickaxe");
      WANDERING_TRADER_EMERALD_LONG_INVISIBILITY_POTION = resourceKey("wandering_trader/emerald_long_invisibility_potion");
      WANDERING_TRADER_EMERALD_TROPICAL_FISH_BUCKET = resourceKey("wandering_trader/emerald_fish_bucket");
      WANDERING_TRADER_EMERALD_PUFFERFISH_BUCKET = resourceKey("wandering_trader/emerald_pufferfish_bucket");
      WANDERING_TRADER_EMERALD_SEA_PICKLE = resourceKey("wandering_trader/emerald_sea_pickle");
      WANDERING_TRADER_EMERALD_SLIME_BALL = resourceKey("wandering_trader/emerald_slime_ball");
      WANDERING_TRADER_EMERALD_GLOWSTONE = resourceKey("wandering_trader/emerald_glowstone");
      WANDERING_TRADER_EMERALD_NAUTILUS_SHELL = resourceKey("wandering_trader/emerald_nautilus_shell");
      WANDERING_TRADER_EMERALD_FERN = resourceKey("wandering_trader/emerald_fern");
      WANDERING_TRADER_EMERALD_SUGAR_CANE = resourceKey("wandering_trader/emerald_sugar_cane");
      WANDERING_TRADER_EMERALD_PUMPKIN = resourceKey("wandering_trader/emerald_pumpkin");
      WANDERING_TRADER_EMERALD_KELP = resourceKey("wandering_trader/emerald_kelp");
      WANDERING_TRADER_EMERALD_CACTUS = resourceKey("wandering_trader/emerald_cactus");
      WANDERING_TRADER_EMERALD_DANDELION = resourceKey("wandering_trader/emerald_dandelion");
      WANDERING_TRADER_EMERALD_GOLDEN_DANDELION = resourceKey("wandering_trader/emerald_golden_dandelion");
      WANDERING_TRADER_EMERALD_POPPY = resourceKey("wandering_trader/emerald_poppy");
      WANDERING_TRADER_EMERALD_BLUE_ORCHID = resourceKey("wandering_trader/emerald_blue_orchid");
      WANDERING_TRADER_EMERALD_ALLIUM = resourceKey("wandering_trader/emerald_allium");
      WANDERING_TRADER_EMERALD_AZURE_BLUET = resourceKey("wandering_trader/emerald_azure_bluet");
      WANDERING_TRADER_EMERALD_RED_TULIP = resourceKey("wandering_trader/emerald_red_tulip");
      WANDERING_TRADER_EMERALD_ORANGE_TULIP = resourceKey("wandering_trader/emerald_orange_tulip");
      WANDERING_TRADER_EMERALD_WHITE_TULIP = resourceKey("wandering_trader/emerald_white_tulip");
      WANDERING_TRADER_EMERALD_PINK_TULIP = resourceKey("wandering_trader/emerald_pink_tulip");
      WANDERING_TRADER_EMERALD_OXEYE_DAISY = resourceKey("wandering_trader/emerald_oxeye_daisy");
      WANDERING_TRADER_EMERALD_CORNFLOWER = resourceKey("wandering_trader/emerald_cornflower");
      WANDERING_TRADER_EMERALD_LILY_OF_THE_VALLEY = resourceKey("wandering_trader/emerald_lily_of_the_valley");
      WANDERING_TRADER_EMERALD_OPEN_EYEBLOSSOM = resourceKey("wandering_trader/emerald_open_eyeblossom");
      WANDERING_TRADER_EMERALD_WHEAT_SEEDS = resourceKey("wandering_trader/emerald_wheat_seeds");
      WANDERING_TRADER_EMERALD_BEETROOT_SEEDS = resourceKey("wandering_trader/emerald_beetroot_seeds");
      WANDERING_TRADER_EMERALD_PUMPKIN_SEEDS = resourceKey("wandering_trader/emerald_pumpkin_seeds");
      WANDERING_TRADER_EMERALD_MELON_SEEDS = resourceKey("wandering_trader/emerald_melon_seeds");
      WANDERING_TRADER_EMERALD_ACACIA_SAPLING = resourceKey("wandering_trader/emerald_acacia_sapling");
      WANDERING_TRADER_EMERALD_BIRCH_SAPLING = resourceKey("wandering_trader/emerald_birch_sapling");
      WANDERING_TRADER_EMERALD_DARK_OAK_SAPLING = resourceKey("wandering_trader/emerald_dark_oak_sapling");
      WANDERING_TRADER_EMERALD_JUNGLE_SAPLING = resourceKey("wandering_trader/emerald_jungle_sapling");
      WANDERING_TRADER_EMERALD_OAK_SAPLING = resourceKey("wandering_trader/emerald_oak_sapling");
      WANDERING_TRADER_EMERALD_SPRUCE_SAPLING = resourceKey("wandering_trader/emerald_spruce_sapling");
      WANDERING_TRADER_EMERALD_CHERRY_SAPLING = resourceKey("wandering_trader/emerald_cherry_sapling");
      WANDERING_TRADER_EMERALD_PALE_OAK_SAPLING = resourceKey("wandering_trader/emerald_pale_oak_sapling");
      WANDERING_TRADER_EMERALD_POPLAR_SAPLING = resourceKey("wandering_trader/emerald_poplar_sapling");
      WANDERING_TRADER_EMERALD_MANGROVE_PROPAGULE = resourceKey("wandering_trader/emerald_mangrove_propagule");
      WANDERING_TRADER_EMERALD_DYE = ColorCollection.NAMES.<ResourceKey<VillagerTrade>>map((color) -> resourceKey("wandering_trader/emerald_" + color + "_dye"));
      WANDERING_TRADER_EMERALD_BRAIN_CORAL_BLOCK = resourceKey("wandering_trader/emerald_brain_coral_block");
      WANDERING_TRADER_EMERALD_BUBBLE_CORAL_BLOCK = resourceKey("wandering_trader/emerald_bubble_coral_block");
      WANDERING_TRADER_EMERALD_FIRE_CORAL_BLOCK = resourceKey("wandering_trader/emerald_fire_coral_block");
      WANDERING_TRADER_EMERALD_HORN_CORAL_BLOCK = resourceKey("wandering_trader/emerald_horn_coral_block");
      WANDERING_TRADER_EMERALD_TUBE_CORAL_BLOCK = resourceKey("wandering_trader/emerald_tube_coral_block");
      WANDERING_TRADER_EMERALD_VINE = resourceKey("wandering_trader/emerald_vine");
      WANDERING_TRADER_EMERALD_PALE_HANGING_MOSS = resourceKey("wandering_trader/emerald_pale_hanging_moss");
      WANDERING_TRADER_EMERALD_BROWN_MUSHROOM = resourceKey("wandering_trader/emerald_brown_mushroom");
      WANDERING_TRADER_EMERALD_RED_MUSHROOM = resourceKey("wandering_trader/emerald_red_mushroom");
      WANDERING_TRADER_EMERALD_LILY_PAD = resourceKey("wandering_trader/emerald_lily_pad");
      WANDERING_TRADER_EMERALD_SMALL_DRIPLEAF = resourceKey("wandering_trader/emerald_small_dripleaf");
      WANDERING_TRADER_EMERALD_SAND = resourceKey("wandering_trader/emerald_sand");
      WANDERING_TRADER_EMERALD_RED_SAND = resourceKey("wandering_trader/emerald_red_sand");
      WANDERING_TRADER_EMERALD_POINTED_DRIPSTONE = resourceKey("wandering_trader/emerald_pointed_dripstone");
      WANDERING_TRADER_EMERALD_SULFUR_SPIKE = resourceKey("wandering_trader/emerald_sulfur_spike");
      WANDERING_TRADER_EMERALD_ROOTED_DIRT = resourceKey("wandering_trader/emerald_rooted_dirt");
      WANDERING_TRADER_EMERALD_MOSS_BLOCK = resourceKey("wandering_trader/emerald_moss_block");
      WANDERING_TRADER_EMERALD_PALE_MOSS_BLOCK = resourceKey("wandering_trader/emerald_pale_moss_block");
      WANDERING_TRADER_EMERALD_WILDFLOWERS = resourceKey("wandering_trader/emerald_wildflowers");
      WANDERING_TRADER_EMERALD_DRY_TALL_GRASS = resourceKey("wandering_trader/emerald_dry_tall_grass");
      WANDERING_TRADER_EMERALD_FIREFLY_BUSH = resourceKey("wandering_trader/emerald_firefly_bush");
      WANDERING_TRADER_EMERALD_NAME_TAG = resourceKey("wandering_trader/emerald_name_tag");
      WANDERING_TRADER_EMERALD_SHELF_MUSHROOM = resourceKey("wandering_trader/emerald_shelf_mushroom");
   }
}
