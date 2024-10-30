package net.minecraft.world.item;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentModels;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.material.Fluids;

public class Items {
   public static final Item AIR;
   public static final Item STONE;
   public static final Item GRANITE;
   public static final Item POLISHED_GRANITE;
   public static final Item DIORITE;
   public static final Item POLISHED_DIORITE;
   public static final Item ANDESITE;
   public static final Item POLISHED_ANDESITE;
   public static final Item DEEPSLATE;
   public static final Item COBBLED_DEEPSLATE;
   public static final Item POLISHED_DEEPSLATE;
   public static final Item CALCITE;
   public static final Item TUFF;
   public static final Item TUFF_SLAB;
   public static final Item TUFF_STAIRS;
   public static final Item TUFF_WALL;
   public static final Item CHISELED_TUFF;
   public static final Item POLISHED_TUFF;
   public static final Item POLISHED_TUFF_SLAB;
   public static final Item POLISHED_TUFF_STAIRS;
   public static final Item POLISHED_TUFF_WALL;
   public static final Item TUFF_BRICKS;
   public static final Item TUFF_BRICK_SLAB;
   public static final Item TUFF_BRICK_STAIRS;
   public static final Item TUFF_BRICK_WALL;
   public static final Item CHISELED_TUFF_BRICKS;
   public static final Item DRIPSTONE_BLOCK;
   public static final Item GRASS_BLOCK;
   public static final Item DIRT;
   public static final Item COARSE_DIRT;
   public static final Item PODZOL;
   public static final Item ROOTED_DIRT;
   public static final Item MUD;
   public static final Item CRIMSON_NYLIUM;
   public static final Item WARPED_NYLIUM;
   public static final Item COBBLESTONE;
   public static final Item OAK_PLANKS;
   public static final Item SPRUCE_PLANKS;
   public static final Item BIRCH_PLANKS;
   public static final Item JUNGLE_PLANKS;
   public static final Item ACACIA_PLANKS;
   public static final Item CHERRY_PLANKS;
   public static final Item DARK_OAK_PLANKS;
   public static final Item PALE_OAK_PLANKS;
   public static final Item MANGROVE_PLANKS;
   public static final Item BAMBOO_PLANKS;
   public static final Item CRIMSON_PLANKS;
   public static final Item WARPED_PLANKS;
   public static final Item BAMBOO_MOSAIC;
   public static final Item OAK_SAPLING;
   public static final Item SPRUCE_SAPLING;
   public static final Item BIRCH_SAPLING;
   public static final Item JUNGLE_SAPLING;
   public static final Item ACACIA_SAPLING;
   public static final Item CHERRY_SAPLING;
   public static final Item DARK_OAK_SAPLING;
   public static final Item PALE_OAK_SAPLING;
   public static final Item MANGROVE_PROPAGULE;
   public static final Item BEDROCK;
   public static final Item SAND;
   public static final Item SUSPICIOUS_SAND;
   public static final Item SUSPICIOUS_GRAVEL;
   public static final Item RED_SAND;
   public static final Item GRAVEL;
   public static final Item COAL_ORE;
   public static final Item DEEPSLATE_COAL_ORE;
   public static final Item IRON_ORE;
   public static final Item DEEPSLATE_IRON_ORE;
   public static final Item COPPER_ORE;
   public static final Item DEEPSLATE_COPPER_ORE;
   public static final Item GOLD_ORE;
   public static final Item DEEPSLATE_GOLD_ORE;
   public static final Item REDSTONE_ORE;
   public static final Item DEEPSLATE_REDSTONE_ORE;
   public static final Item EMERALD_ORE;
   public static final Item DEEPSLATE_EMERALD_ORE;
   public static final Item LAPIS_ORE;
   public static final Item DEEPSLATE_LAPIS_ORE;
   public static final Item DIAMOND_ORE;
   public static final Item DEEPSLATE_DIAMOND_ORE;
   public static final Item NETHER_GOLD_ORE;
   public static final Item NETHER_QUARTZ_ORE;
   public static final Item ANCIENT_DEBRIS;
   public static final Item COAL_BLOCK;
   public static final Item RAW_IRON_BLOCK;
   public static final Item RAW_COPPER_BLOCK;
   public static final Item RAW_GOLD_BLOCK;
   public static final Item HEAVY_CORE;
   public static final Item AMETHYST_BLOCK;
   public static final Item BUDDING_AMETHYST;
   public static final Item IRON_BLOCK;
   public static final Item COPPER_BLOCK;
   public static final Item GOLD_BLOCK;
   public static final Item DIAMOND_BLOCK;
   public static final Item NETHERITE_BLOCK;
   public static final Item EXPOSED_COPPER;
   public static final Item WEATHERED_COPPER;
   public static final Item OXIDIZED_COPPER;
   public static final Item CHISELED_COPPER;
   public static final Item EXPOSED_CHISELED_COPPER;
   public static final Item WEATHERED_CHISELED_COPPER;
   public static final Item OXIDIZED_CHISELED_COPPER;
   public static final Item CUT_COPPER;
   public static final Item EXPOSED_CUT_COPPER;
   public static final Item WEATHERED_CUT_COPPER;
   public static final Item OXIDIZED_CUT_COPPER;
   public static final Item CUT_COPPER_STAIRS;
   public static final Item EXPOSED_CUT_COPPER_STAIRS;
   public static final Item WEATHERED_CUT_COPPER_STAIRS;
   public static final Item OXIDIZED_CUT_COPPER_STAIRS;
   public static final Item CUT_COPPER_SLAB;
   public static final Item EXPOSED_CUT_COPPER_SLAB;
   public static final Item WEATHERED_CUT_COPPER_SLAB;
   public static final Item OXIDIZED_CUT_COPPER_SLAB;
   public static final Item WAXED_COPPER_BLOCK;
   public static final Item WAXED_EXPOSED_COPPER;
   public static final Item WAXED_WEATHERED_COPPER;
   public static final Item WAXED_OXIDIZED_COPPER;
   public static final Item WAXED_CHISELED_COPPER;
   public static final Item WAXED_EXPOSED_CHISELED_COPPER;
   public static final Item WAXED_WEATHERED_CHISELED_COPPER;
   public static final Item WAXED_OXIDIZED_CHISELED_COPPER;
   public static final Item WAXED_CUT_COPPER;
   public static final Item WAXED_EXPOSED_CUT_COPPER;
   public static final Item WAXED_WEATHERED_CUT_COPPER;
   public static final Item WAXED_OXIDIZED_CUT_COPPER;
   public static final Item WAXED_CUT_COPPER_STAIRS;
   public static final Item WAXED_EXPOSED_CUT_COPPER_STAIRS;
   public static final Item WAXED_WEATHERED_CUT_COPPER_STAIRS;
   public static final Item WAXED_OXIDIZED_CUT_COPPER_STAIRS;
   public static final Item WAXED_CUT_COPPER_SLAB;
   public static final Item WAXED_EXPOSED_CUT_COPPER_SLAB;
   public static final Item WAXED_WEATHERED_CUT_COPPER_SLAB;
   public static final Item WAXED_OXIDIZED_CUT_COPPER_SLAB;
   public static final Item OAK_LOG;
   public static final Item SPRUCE_LOG;
   public static final Item BIRCH_LOG;
   public static final Item JUNGLE_LOG;
   public static final Item ACACIA_LOG;
   public static final Item CHERRY_LOG;
   public static final Item PALE_OAK_LOG;
   public static final Item DARK_OAK_LOG;
   public static final Item MANGROVE_LOG;
   public static final Item MANGROVE_ROOTS;
   public static final Item MUDDY_MANGROVE_ROOTS;
   public static final Item CRIMSON_STEM;
   public static final Item WARPED_STEM;
   public static final Item BAMBOO_BLOCK;
   public static final Item STRIPPED_OAK_LOG;
   public static final Item STRIPPED_SPRUCE_LOG;
   public static final Item STRIPPED_BIRCH_LOG;
   public static final Item STRIPPED_JUNGLE_LOG;
   public static final Item STRIPPED_ACACIA_LOG;
   public static final Item STRIPPED_CHERRY_LOG;
   public static final Item STRIPPED_DARK_OAK_LOG;
   public static final Item STRIPPED_PALE_OAK_LOG;
   public static final Item STRIPPED_MANGROVE_LOG;
   public static final Item STRIPPED_CRIMSON_STEM;
   public static final Item STRIPPED_WARPED_STEM;
   public static final Item STRIPPED_OAK_WOOD;
   public static final Item STRIPPED_SPRUCE_WOOD;
   public static final Item STRIPPED_BIRCH_WOOD;
   public static final Item STRIPPED_JUNGLE_WOOD;
   public static final Item STRIPPED_ACACIA_WOOD;
   public static final Item STRIPPED_CHERRY_WOOD;
   public static final Item STRIPPED_DARK_OAK_WOOD;
   public static final Item STRIPPED_PALE_OAK_WOOD;
   public static final Item STRIPPED_MANGROVE_WOOD;
   public static final Item STRIPPED_CRIMSON_HYPHAE;
   public static final Item STRIPPED_WARPED_HYPHAE;
   public static final Item STRIPPED_BAMBOO_BLOCK;
   public static final Item OAK_WOOD;
   public static final Item SPRUCE_WOOD;
   public static final Item BIRCH_WOOD;
   public static final Item JUNGLE_WOOD;
   public static final Item ACACIA_WOOD;
   public static final Item CHERRY_WOOD;
   public static final Item PALE_OAK_WOOD;
   public static final Item DARK_OAK_WOOD;
   public static final Item MANGROVE_WOOD;
   public static final Item CRIMSON_HYPHAE;
   public static final Item WARPED_HYPHAE;
   public static final Item OAK_LEAVES;
   public static final Item SPRUCE_LEAVES;
   public static final Item BIRCH_LEAVES;
   public static final Item JUNGLE_LEAVES;
   public static final Item ACACIA_LEAVES;
   public static final Item CHERRY_LEAVES;
   public static final Item DARK_OAK_LEAVES;
   public static final Item PALE_OAK_LEAVES;
   public static final Item MANGROVE_LEAVES;
   public static final Item AZALEA_LEAVES;
   public static final Item FLOWERING_AZALEA_LEAVES;
   public static final Item SPONGE;
   public static final Item WET_SPONGE;
   public static final Item GLASS;
   public static final Item TINTED_GLASS;
   public static final Item LAPIS_BLOCK;
   public static final Item SANDSTONE;
   public static final Item CHISELED_SANDSTONE;
   public static final Item CUT_SANDSTONE;
   public static final Item COBWEB;
   public static final Item SHORT_GRASS;
   public static final Item FERN;
   public static final Item AZALEA;
   public static final Item FLOWERING_AZALEA;
   public static final Item DEAD_BUSH;
   public static final Item SEAGRASS;
   public static final Item SEA_PICKLE;
   public static final Item WHITE_WOOL;
   public static final Item ORANGE_WOOL;
   public static final Item MAGENTA_WOOL;
   public static final Item LIGHT_BLUE_WOOL;
   public static final Item YELLOW_WOOL;
   public static final Item LIME_WOOL;
   public static final Item PINK_WOOL;
   public static final Item GRAY_WOOL;
   public static final Item LIGHT_GRAY_WOOL;
   public static final Item CYAN_WOOL;
   public static final Item PURPLE_WOOL;
   public static final Item BLUE_WOOL;
   public static final Item BROWN_WOOL;
   public static final Item GREEN_WOOL;
   public static final Item RED_WOOL;
   public static final Item BLACK_WOOL;
   public static final Item DANDELION;
   public static final Item OPEN_EYEBLOSSOM;
   public static final Item CLOSED_EYEBLOSSOM;
   public static final Item POPPY;
   public static final Item BLUE_ORCHID;
   public static final Item ALLIUM;
   public static final Item AZURE_BLUET;
   public static final Item RED_TULIP;
   public static final Item ORANGE_TULIP;
   public static final Item WHITE_TULIP;
   public static final Item PINK_TULIP;
   public static final Item OXEYE_DAISY;
   public static final Item CORNFLOWER;
   public static final Item LILY_OF_THE_VALLEY;
   public static final Item WITHER_ROSE;
   public static final Item TORCHFLOWER;
   public static final Item PITCHER_PLANT;
   public static final Item SPORE_BLOSSOM;
   public static final Item BROWN_MUSHROOM;
   public static final Item RED_MUSHROOM;
   public static final Item CRIMSON_FUNGUS;
   public static final Item WARPED_FUNGUS;
   public static final Item CRIMSON_ROOTS;
   public static final Item WARPED_ROOTS;
   public static final Item NETHER_SPROUTS;
   public static final Item WEEPING_VINES;
   public static final Item TWISTING_VINES;
   public static final Item SUGAR_CANE;
   public static final Item KELP;
   public static final Item PINK_PETALS;
   public static final Item MOSS_CARPET;
   public static final Item MOSS_BLOCK;
   public static final Item PALE_MOSS_CARPET;
   public static final Item PALE_HANGING_MOSS;
   public static final Item PALE_MOSS_BLOCK;
   public static final Item HANGING_ROOTS;
   public static final Item BIG_DRIPLEAF;
   public static final Item SMALL_DRIPLEAF;
   public static final Item BAMBOO;
   public static final Item OAK_SLAB;
   public static final Item SPRUCE_SLAB;
   public static final Item BIRCH_SLAB;
   public static final Item JUNGLE_SLAB;
   public static final Item ACACIA_SLAB;
   public static final Item CHERRY_SLAB;
   public static final Item DARK_OAK_SLAB;
   public static final Item PALE_OAK_SLAB;
   public static final Item MANGROVE_SLAB;
   public static final Item BAMBOO_SLAB;
   public static final Item BAMBOO_MOSAIC_SLAB;
   public static final Item CRIMSON_SLAB;
   public static final Item WARPED_SLAB;
   public static final Item STONE_SLAB;
   public static final Item SMOOTH_STONE_SLAB;
   public static final Item SANDSTONE_SLAB;
   public static final Item CUT_STANDSTONE_SLAB;
   public static final Item PETRIFIED_OAK_SLAB;
   public static final Item COBBLESTONE_SLAB;
   public static final Item BRICK_SLAB;
   public static final Item STONE_BRICK_SLAB;
   public static final Item MUD_BRICK_SLAB;
   public static final Item NETHER_BRICK_SLAB;
   public static final Item QUARTZ_SLAB;
   public static final Item RED_SANDSTONE_SLAB;
   public static final Item CUT_RED_SANDSTONE_SLAB;
   public static final Item PURPUR_SLAB;
   public static final Item PRISMARINE_SLAB;
   public static final Item PRISMARINE_BRICK_SLAB;
   public static final Item DARK_PRISMARINE_SLAB;
   public static final Item SMOOTH_QUARTZ;
   public static final Item SMOOTH_RED_SANDSTONE;
   public static final Item SMOOTH_SANDSTONE;
   public static final Item SMOOTH_STONE;
   public static final Item BRICKS;
   public static final Item BOOKSHELF;
   public static final Item CHISELED_BOOKSHELF;
   public static final Item DECORATED_POT;
   public static final Item MOSSY_COBBLESTONE;
   public static final Item OBSIDIAN;
   public static final Item TORCH;
   public static final Item END_ROD;
   public static final Item CHORUS_PLANT;
   public static final Item CHORUS_FLOWER;
   public static final Item PURPUR_BLOCK;
   public static final Item PURPUR_PILLAR;
   public static final Item PURPUR_STAIRS;
   public static final Item SPAWNER;
   public static final Item CREAKING_HEART;
   public static final Item CHEST;
   public static final Item CRAFTING_TABLE;
   public static final Item FARMLAND;
   public static final Item FURNACE;
   public static final Item LADDER;
   public static final Item COBBLESTONE_STAIRS;
   public static final Item SNOW;
   public static final Item ICE;
   public static final Item SNOW_BLOCK;
   public static final Item CACTUS;
   public static final Item CLAY;
   public static final Item JUKEBOX;
   public static final Item OAK_FENCE;
   public static final Item SPRUCE_FENCE;
   public static final Item BIRCH_FENCE;
   public static final Item JUNGLE_FENCE;
   public static final Item ACACIA_FENCE;
   public static final Item CHERRY_FENCE;
   public static final Item DARK_OAK_FENCE;
   public static final Item PALE_OAK_FENCE;
   public static final Item MANGROVE_FENCE;
   public static final Item BAMBOO_FENCE;
   public static final Item CRIMSON_FENCE;
   public static final Item WARPED_FENCE;
   public static final Item PUMPKIN;
   public static final Item CARVED_PUMPKIN;
   public static final Item JACK_O_LANTERN;
   public static final Item NETHERRACK;
   public static final Item SOUL_SAND;
   public static final Item SOUL_SOIL;
   public static final Item BASALT;
   public static final Item POLISHED_BASALT;
   public static final Item SMOOTH_BASALT;
   public static final Item SOUL_TORCH;
   public static final Item GLOWSTONE;
   public static final Item INFESTED_STONE;
   public static final Item INFESTED_COBBLESTONE;
   public static final Item INFESTED_STONE_BRICKS;
   public static final Item INFESTED_MOSSY_STONE_BRICKS;
   public static final Item INFESTED_CRACKED_STONE_BRICKS;
   public static final Item INFESTED_CHISELED_STONE_BRICKS;
   public static final Item INFESTED_DEEPSLATE;
   public static final Item STONE_BRICKS;
   public static final Item MOSSY_STONE_BRICKS;
   public static final Item CRACKED_STONE_BRICKS;
   public static final Item CHISELED_STONE_BRICKS;
   public static final Item PACKED_MUD;
   public static final Item MUD_BRICKS;
   public static final Item DEEPSLATE_BRICKS;
   public static final Item CRACKED_DEEPSLATE_BRICKS;
   public static final Item DEEPSLATE_TILES;
   public static final Item CRACKED_DEEPSLATE_TILES;
   public static final Item CHISELED_DEEPSLATE;
   public static final Item REINFORCED_DEEPSLATE;
   public static final Item BROWN_MUSHROOM_BLOCK;
   public static final Item RED_MUSHROOM_BLOCK;
   public static final Item MUSHROOM_STEM;
   public static final Item IRON_BARS;
   public static final Item CHAIN;
   public static final Item GLASS_PANE;
   public static final Item MELON;
   public static final Item VINE;
   public static final Item GLOW_LICHEN;
   public static final Item RESIN_CLUMP;
   public static final Item RESIN_BLOCK;
   public static final Item RESIN_BRICKS;
   public static final Item RESIN_BRICK_STAIRS;
   public static final Item RESIN_BRICK_SLAB;
   public static final Item RESIN_BRICK_WALL;
   public static final Item CHISELED_RESIN_BRICKS;
   public static final Item BRICK_STAIRS;
   public static final Item STONE_BRICK_STAIRS;
   public static final Item MUD_BRICK_STAIRS;
   public static final Item MYCELIUM;
   public static final Item LILY_PAD;
   public static final Item NETHER_BRICKS;
   public static final Item CRACKED_NETHER_BRICKS;
   public static final Item CHISELED_NETHER_BRICKS;
   public static final Item NETHER_BRICK_FENCE;
   public static final Item NETHER_BRICK_STAIRS;
   public static final Item SCULK;
   public static final Item SCULK_VEIN;
   public static final Item SCULK_CATALYST;
   public static final Item SCULK_SHRIEKER;
   public static final Item ENCHANTING_TABLE;
   public static final Item END_PORTAL_FRAME;
   public static final Item END_STONE;
   public static final Item END_STONE_BRICKS;
   public static final Item DRAGON_EGG;
   public static final Item SANDSTONE_STAIRS;
   public static final Item ENDER_CHEST;
   public static final Item EMERALD_BLOCK;
   public static final Item OAK_STAIRS;
   public static final Item SPRUCE_STAIRS;
   public static final Item BIRCH_STAIRS;
   public static final Item JUNGLE_STAIRS;
   public static final Item ACACIA_STAIRS;
   public static final Item CHERRY_STAIRS;
   public static final Item DARK_OAK_STAIRS;
   public static final Item PALE_OAK_STAIRS;
   public static final Item MANGROVE_STAIRS;
   public static final Item BAMBOO_STAIRS;
   public static final Item BAMBOO_MOSAIC_STAIRS;
   public static final Item CRIMSON_STAIRS;
   public static final Item WARPED_STAIRS;
   public static final Item COMMAND_BLOCK;
   public static final Item BEACON;
   public static final Item COBBLESTONE_WALL;
   public static final Item MOSSY_COBBLESTONE_WALL;
   public static final Item BRICK_WALL;
   public static final Item PRISMARINE_WALL;
   public static final Item RED_SANDSTONE_WALL;
   public static final Item MOSSY_STONE_BRICK_WALL;
   public static final Item GRANITE_WALL;
   public static final Item STONE_BRICK_WALL;
   public static final Item MUD_BRICK_WALL;
   public static final Item NETHER_BRICK_WALL;
   public static final Item ANDESITE_WALL;
   public static final Item RED_NETHER_BRICK_WALL;
   public static final Item SANDSTONE_WALL;
   public static final Item END_STONE_BRICK_WALL;
   public static final Item DIORITE_WALL;
   public static final Item BLACKSTONE_WALL;
   public static final Item POLISHED_BLACKSTONE_WALL;
   public static final Item POLISHED_BLACKSTONE_BRICK_WALL;
   public static final Item COBBLED_DEEPSLATE_WALL;
   public static final Item POLISHED_DEEPSLATE_WALL;
   public static final Item DEEPSLATE_BRICK_WALL;
   public static final Item DEEPSLATE_TILE_WALL;
   public static final Item ANVIL;
   public static final Item CHIPPED_ANVIL;
   public static final Item DAMAGED_ANVIL;
   public static final Item CHISELED_QUARTZ_BLOCK;
   public static final Item QUARTZ_BLOCK;
   public static final Item QUARTZ_BRICKS;
   public static final Item QUARTZ_PILLAR;
   public static final Item QUARTZ_STAIRS;
   public static final Item WHITE_TERRACOTTA;
   public static final Item ORANGE_TERRACOTTA;
   public static final Item MAGENTA_TERRACOTTA;
   public static final Item LIGHT_BLUE_TERRACOTTA;
   public static final Item YELLOW_TERRACOTTA;
   public static final Item LIME_TERRACOTTA;
   public static final Item PINK_TERRACOTTA;
   public static final Item GRAY_TERRACOTTA;
   public static final Item LIGHT_GRAY_TERRACOTTA;
   public static final Item CYAN_TERRACOTTA;
   public static final Item PURPLE_TERRACOTTA;
   public static final Item BLUE_TERRACOTTA;
   public static final Item BROWN_TERRACOTTA;
   public static final Item GREEN_TERRACOTTA;
   public static final Item RED_TERRACOTTA;
   public static final Item BLACK_TERRACOTTA;
   public static final Item BARRIER;
   public static final Item LIGHT;
   public static final Item HAY_BLOCK;
   public static final Item WHITE_CARPET;
   public static final Item ORANGE_CARPET;
   public static final Item MAGENTA_CARPET;
   public static final Item LIGHT_BLUE_CARPET;
   public static final Item YELLOW_CARPET;
   public static final Item LIME_CARPET;
   public static final Item PINK_CARPET;
   public static final Item GRAY_CARPET;
   public static final Item LIGHT_GRAY_CARPET;
   public static final Item CYAN_CARPET;
   public static final Item PURPLE_CARPET;
   public static final Item BLUE_CARPET;
   public static final Item BROWN_CARPET;
   public static final Item GREEN_CARPET;
   public static final Item RED_CARPET;
   public static final Item BLACK_CARPET;
   public static final Item TERRACOTTA;
   public static final Item PACKED_ICE;
   public static final Item DIRT_PATH;
   public static final Item SUNFLOWER;
   public static final Item LILAC;
   public static final Item ROSE_BUSH;
   public static final Item PEONY;
   public static final Item TALL_GRASS;
   public static final Item LARGE_FERN;
   public static final Item WHITE_STAINED_GLASS;
   public static final Item ORANGE_STAINED_GLASS;
   public static final Item MAGENTA_STAINED_GLASS;
   public static final Item LIGHT_BLUE_STAINED_GLASS;
   public static final Item YELLOW_STAINED_GLASS;
   public static final Item LIME_STAINED_GLASS;
   public static final Item PINK_STAINED_GLASS;
   public static final Item GRAY_STAINED_GLASS;
   public static final Item LIGHT_GRAY_STAINED_GLASS;
   public static final Item CYAN_STAINED_GLASS;
   public static final Item PURPLE_STAINED_GLASS;
   public static final Item BLUE_STAINED_GLASS;
   public static final Item BROWN_STAINED_GLASS;
   public static final Item GREEN_STAINED_GLASS;
   public static final Item RED_STAINED_GLASS;
   public static final Item BLACK_STAINED_GLASS;
   public static final Item WHITE_STAINED_GLASS_PANE;
   public static final Item ORANGE_STAINED_GLASS_PANE;
   public static final Item MAGENTA_STAINED_GLASS_PANE;
   public static final Item LIGHT_BLUE_STAINED_GLASS_PANE;
   public static final Item YELLOW_STAINED_GLASS_PANE;
   public static final Item LIME_STAINED_GLASS_PANE;
   public static final Item PINK_STAINED_GLASS_PANE;
   public static final Item GRAY_STAINED_GLASS_PANE;
   public static final Item LIGHT_GRAY_STAINED_GLASS_PANE;
   public static final Item CYAN_STAINED_GLASS_PANE;
   public static final Item PURPLE_STAINED_GLASS_PANE;
   public static final Item BLUE_STAINED_GLASS_PANE;
   public static final Item BROWN_STAINED_GLASS_PANE;
   public static final Item GREEN_STAINED_GLASS_PANE;
   public static final Item RED_STAINED_GLASS_PANE;
   public static final Item BLACK_STAINED_GLASS_PANE;
   public static final Item PRISMARINE;
   public static final Item PRISMARINE_BRICKS;
   public static final Item DARK_PRISMARINE;
   public static final Item PRISMARINE_STAIRS;
   public static final Item PRISMARINE_BRICK_STAIRS;
   public static final Item DARK_PRISMARINE_STAIRS;
   public static final Item SEA_LANTERN;
   public static final Item RED_SANDSTONE;
   public static final Item CHISELED_RED_SANDSTONE;
   public static final Item CUT_RED_SANDSTONE;
   public static final Item RED_SANDSTONE_STAIRS;
   public static final Item REPEATING_COMMAND_BLOCK;
   public static final Item CHAIN_COMMAND_BLOCK;
   public static final Item MAGMA_BLOCK;
   public static final Item NETHER_WART_BLOCK;
   public static final Item WARPED_WART_BLOCK;
   public static final Item RED_NETHER_BRICKS;
   public static final Item BONE_BLOCK;
   public static final Item STRUCTURE_VOID;
   public static final Item SHULKER_BOX;
   public static final Item WHITE_SHULKER_BOX;
   public static final Item ORANGE_SHULKER_BOX;
   public static final Item MAGENTA_SHULKER_BOX;
   public static final Item LIGHT_BLUE_SHULKER_BOX;
   public static final Item YELLOW_SHULKER_BOX;
   public static final Item LIME_SHULKER_BOX;
   public static final Item PINK_SHULKER_BOX;
   public static final Item GRAY_SHULKER_BOX;
   public static final Item LIGHT_GRAY_SHULKER_BOX;
   public static final Item CYAN_SHULKER_BOX;
   public static final Item PURPLE_SHULKER_BOX;
   public static final Item BLUE_SHULKER_BOX;
   public static final Item BROWN_SHULKER_BOX;
   public static final Item GREEN_SHULKER_BOX;
   public static final Item RED_SHULKER_BOX;
   public static final Item BLACK_SHULKER_BOX;
   public static final Item WHITE_GLAZED_TERRACOTTA;
   public static final Item ORANGE_GLAZED_TERRACOTTA;
   public static final Item MAGENTA_GLAZED_TERRACOTTA;
   public static final Item LIGHT_BLUE_GLAZED_TERRACOTTA;
   public static final Item YELLOW_GLAZED_TERRACOTTA;
   public static final Item LIME_GLAZED_TERRACOTTA;
   public static final Item PINK_GLAZED_TERRACOTTA;
   public static final Item GRAY_GLAZED_TERRACOTTA;
   public static final Item LIGHT_GRAY_GLAZED_TERRACOTTA;
   public static final Item CYAN_GLAZED_TERRACOTTA;
   public static final Item PURPLE_GLAZED_TERRACOTTA;
   public static final Item BLUE_GLAZED_TERRACOTTA;
   public static final Item BROWN_GLAZED_TERRACOTTA;
   public static final Item GREEN_GLAZED_TERRACOTTA;
   public static final Item RED_GLAZED_TERRACOTTA;
   public static final Item BLACK_GLAZED_TERRACOTTA;
   public static final Item WHITE_CONCRETE;
   public static final Item ORANGE_CONCRETE;
   public static final Item MAGENTA_CONCRETE;
   public static final Item LIGHT_BLUE_CONCRETE;
   public static final Item YELLOW_CONCRETE;
   public static final Item LIME_CONCRETE;
   public static final Item PINK_CONCRETE;
   public static final Item GRAY_CONCRETE;
   public static final Item LIGHT_GRAY_CONCRETE;
   public static final Item CYAN_CONCRETE;
   public static final Item PURPLE_CONCRETE;
   public static final Item BLUE_CONCRETE;
   public static final Item BROWN_CONCRETE;
   public static final Item GREEN_CONCRETE;
   public static final Item RED_CONCRETE;
   public static final Item BLACK_CONCRETE;
   public static final Item WHITE_CONCRETE_POWDER;
   public static final Item ORANGE_CONCRETE_POWDER;
   public static final Item MAGENTA_CONCRETE_POWDER;
   public static final Item LIGHT_BLUE_CONCRETE_POWDER;
   public static final Item YELLOW_CONCRETE_POWDER;
   public static final Item LIME_CONCRETE_POWDER;
   public static final Item PINK_CONCRETE_POWDER;
   public static final Item GRAY_CONCRETE_POWDER;
   public static final Item LIGHT_GRAY_CONCRETE_POWDER;
   public static final Item CYAN_CONCRETE_POWDER;
   public static final Item PURPLE_CONCRETE_POWDER;
   public static final Item BLUE_CONCRETE_POWDER;
   public static final Item BROWN_CONCRETE_POWDER;
   public static final Item GREEN_CONCRETE_POWDER;
   public static final Item RED_CONCRETE_POWDER;
   public static final Item BLACK_CONCRETE_POWDER;
   public static final Item TURTLE_EGG;
   public static final Item SNIFFER_EGG;
   public static final Item DEAD_TUBE_CORAL_BLOCK;
   public static final Item DEAD_BRAIN_CORAL_BLOCK;
   public static final Item DEAD_BUBBLE_CORAL_BLOCK;
   public static final Item DEAD_FIRE_CORAL_BLOCK;
   public static final Item DEAD_HORN_CORAL_BLOCK;
   public static final Item TUBE_CORAL_BLOCK;
   public static final Item BRAIN_CORAL_BLOCK;
   public static final Item BUBBLE_CORAL_BLOCK;
   public static final Item FIRE_CORAL_BLOCK;
   public static final Item HORN_CORAL_BLOCK;
   public static final Item TUBE_CORAL;
   public static final Item BRAIN_CORAL;
   public static final Item BUBBLE_CORAL;
   public static final Item FIRE_CORAL;
   public static final Item HORN_CORAL;
   public static final Item DEAD_BRAIN_CORAL;
   public static final Item DEAD_BUBBLE_CORAL;
   public static final Item DEAD_FIRE_CORAL;
   public static final Item DEAD_HORN_CORAL;
   public static final Item DEAD_TUBE_CORAL;
   public static final Item TUBE_CORAL_FAN;
   public static final Item BRAIN_CORAL_FAN;
   public static final Item BUBBLE_CORAL_FAN;
   public static final Item FIRE_CORAL_FAN;
   public static final Item HORN_CORAL_FAN;
   public static final Item DEAD_TUBE_CORAL_FAN;
   public static final Item DEAD_BRAIN_CORAL_FAN;
   public static final Item DEAD_BUBBLE_CORAL_FAN;
   public static final Item DEAD_FIRE_CORAL_FAN;
   public static final Item DEAD_HORN_CORAL_FAN;
   public static final Item BLUE_ICE;
   public static final Item CONDUIT;
   public static final Item POLISHED_GRANITE_STAIRS;
   public static final Item SMOOTH_RED_SANDSTONE_STAIRS;
   public static final Item MOSSY_STONE_BRICK_STAIRS;
   public static final Item POLISHED_DIORITE_STAIRS;
   public static final Item MOSSY_COBBLESTONE_STAIRS;
   public static final Item END_STONE_BRICK_STAIRS;
   public static final Item STONE_STAIRS;
   public static final Item SMOOTH_SANDSTONE_STAIRS;
   public static final Item SMOOTH_QUARTZ_STAIRS;
   public static final Item GRANITE_STAIRS;
   public static final Item ANDESITE_STAIRS;
   public static final Item RED_NETHER_BRICK_STAIRS;
   public static final Item POLISHED_ANDESITE_STAIRS;
   public static final Item DIORITE_STAIRS;
   public static final Item COBBLED_DEEPSLATE_STAIRS;
   public static final Item POLISHED_DEEPSLATE_STAIRS;
   public static final Item DEEPSLATE_BRICK_STAIRS;
   public static final Item DEEPSLATE_TILE_STAIRS;
   public static final Item POLISHED_GRANITE_SLAB;
   public static final Item SMOOTH_RED_SANDSTONE_SLAB;
   public static final Item MOSSY_STONE_BRICK_SLAB;
   public static final Item POLISHED_DIORITE_SLAB;
   public static final Item MOSSY_COBBLESTONE_SLAB;
   public static final Item END_STONE_BRICK_SLAB;
   public static final Item SMOOTH_SANDSTONE_SLAB;
   public static final Item SMOOTH_QUARTZ_SLAB;
   public static final Item GRANITE_SLAB;
   public static final Item ANDESITE_SLAB;
   public static final Item RED_NETHER_BRICK_SLAB;
   public static final Item POLISHED_ANDESITE_SLAB;
   public static final Item DIORITE_SLAB;
   public static final Item COBBLED_DEEPSLATE_SLAB;
   public static final Item POLISHED_DEEPSLATE_SLAB;
   public static final Item DEEPSLATE_BRICK_SLAB;
   public static final Item DEEPSLATE_TILE_SLAB;
   public static final Item SCAFFOLDING;
   public static final Item REDSTONE;
   public static final Item REDSTONE_TORCH;
   public static final Item REDSTONE_BLOCK;
   public static final Item REPEATER;
   public static final Item COMPARATOR;
   public static final Item PISTON;
   public static final Item STICKY_PISTON;
   public static final Item SLIME_BLOCK;
   public static final Item HONEY_BLOCK;
   public static final Item OBSERVER;
   public static final Item HOPPER;
   public static final Item DISPENSER;
   public static final Item DROPPER;
   public static final Item LECTERN;
   public static final Item TARGET;
   public static final Item LEVER;
   public static final Item LIGHTNING_ROD;
   public static final Item DAYLIGHT_DETECTOR;
   public static final Item SCULK_SENSOR;
   public static final Item CALIBRATED_SCULK_SENSOR;
   public static final Item TRIPWIRE_HOOK;
   public static final Item TRAPPED_CHEST;
   public static final Item TNT;
   public static final Item REDSTONE_LAMP;
   public static final Item NOTE_BLOCK;
   public static final Item STONE_BUTTON;
   public static final Item POLISHED_BLACKSTONE_BUTTON;
   public static final Item OAK_BUTTON;
   public static final Item SPRUCE_BUTTON;
   public static final Item BIRCH_BUTTON;
   public static final Item JUNGLE_BUTTON;
   public static final Item ACACIA_BUTTON;
   public static final Item CHERRY_BUTTON;
   public static final Item DARK_OAK_BUTTON;
   public static final Item PALE_OAK_BUTTON;
   public static final Item MANGROVE_BUTTON;
   public static final Item BAMBOO_BUTTON;
   public static final Item CRIMSON_BUTTON;
   public static final Item WARPED_BUTTON;
   public static final Item STONE_PRESSURE_PLATE;
   public static final Item POLISHED_BLACKSTONE_PRESSURE_PLATE;
   public static final Item LIGHT_WEIGHTED_PRESSURE_PLATE;
   public static final Item HEAVY_WEIGHTED_PRESSURE_PLATE;
   public static final Item OAK_PRESSURE_PLATE;
   public static final Item SPRUCE_PRESSURE_PLATE;
   public static final Item BIRCH_PRESSURE_PLATE;
   public static final Item JUNGLE_PRESSURE_PLATE;
   public static final Item ACACIA_PRESSURE_PLATE;
   public static final Item CHERRY_PRESSURE_PLATE;
   public static final Item DARK_OAK_PRESSURE_PLATE;
   public static final Item PALE_OAK_PRESSURE_PLATE;
   public static final Item MANGROVE_PRESSURE_PLATE;
   public static final Item BAMBOO_PRESSURE_PLATE;
   public static final Item CRIMSON_PRESSURE_PLATE;
   public static final Item WARPED_PRESSURE_PLATE;
   public static final Item IRON_DOOR;
   public static final Item OAK_DOOR;
   public static final Item SPRUCE_DOOR;
   public static final Item BIRCH_DOOR;
   public static final Item JUNGLE_DOOR;
   public static final Item ACACIA_DOOR;
   public static final Item CHERRY_DOOR;
   public static final Item DARK_OAK_DOOR;
   public static final Item PALE_OAK_DOOR;
   public static final Item MANGROVE_DOOR;
   public static final Item BAMBOO_DOOR;
   public static final Item CRIMSON_DOOR;
   public static final Item WARPED_DOOR;
   public static final Item COPPER_DOOR;
   public static final Item EXPOSED_COPPER_DOOR;
   public static final Item WEATHERED_COPPER_DOOR;
   public static final Item OXIDIZED_COPPER_DOOR;
   public static final Item WAXED_COPPER_DOOR;
   public static final Item WAXED_EXPOSED_COPPER_DOOR;
   public static final Item WAXED_WEATHERED_COPPER_DOOR;
   public static final Item WAXED_OXIDIZED_COPPER_DOOR;
   public static final Item IRON_TRAPDOOR;
   public static final Item OAK_TRAPDOOR;
   public static final Item SPRUCE_TRAPDOOR;
   public static final Item BIRCH_TRAPDOOR;
   public static final Item JUNGLE_TRAPDOOR;
   public static final Item ACACIA_TRAPDOOR;
   public static final Item CHERRY_TRAPDOOR;
   public static final Item DARK_OAK_TRAPDOOR;
   public static final Item PALE_OAK_TRAPDOOR;
   public static final Item MANGROVE_TRAPDOOR;
   public static final Item BAMBOO_TRAPDOOR;
   public static final Item CRIMSON_TRAPDOOR;
   public static final Item WARPED_TRAPDOOR;
   public static final Item COPPER_TRAPDOOR;
   public static final Item EXPOSED_COPPER_TRAPDOOR;
   public static final Item WEATHERED_COPPER_TRAPDOOR;
   public static final Item OXIDIZED_COPPER_TRAPDOOR;
   public static final Item WAXED_COPPER_TRAPDOOR;
   public static final Item WAXED_EXPOSED_COPPER_TRAPDOOR;
   public static final Item WAXED_WEATHERED_COPPER_TRAPDOOR;
   public static final Item WAXED_OXIDIZED_COPPER_TRAPDOOR;
   public static final Item OAK_FENCE_GATE;
   public static final Item SPRUCE_FENCE_GATE;
   public static final Item BIRCH_FENCE_GATE;
   public static final Item JUNGLE_FENCE_GATE;
   public static final Item ACACIA_FENCE_GATE;
   public static final Item CHERRY_FENCE_GATE;
   public static final Item DARK_OAK_FENCE_GATE;
   public static final Item PALE_OAK_FENCE_GATE;
   public static final Item MANGROVE_FENCE_GATE;
   public static final Item BAMBOO_FENCE_GATE;
   public static final Item CRIMSON_FENCE_GATE;
   public static final Item WARPED_FENCE_GATE;
   public static final Item POWERED_RAIL;
   public static final Item DETECTOR_RAIL;
   public static final Item RAIL;
   public static final Item ACTIVATOR_RAIL;
   public static final Item SADDLE;
   public static final Item MINECART;
   public static final Item CHEST_MINECART;
   public static final Item FURNACE_MINECART;
   public static final Item TNT_MINECART;
   public static final Item HOPPER_MINECART;
   public static final Item CARROT_ON_A_STICK;
   public static final Item WARPED_FUNGUS_ON_A_STICK;
   public static final Item PHANTOM_MEMBRANE;
   public static final Item ELYTRA;
   public static final Item OAK_BOAT;
   public static final Item OAK_CHEST_BOAT;
   public static final Item SPRUCE_BOAT;
   public static final Item SPRUCE_CHEST_BOAT;
   public static final Item BIRCH_BOAT;
   public static final Item BIRCH_CHEST_BOAT;
   public static final Item JUNGLE_BOAT;
   public static final Item JUNGLE_CHEST_BOAT;
   public static final Item ACACIA_BOAT;
   public static final Item ACACIA_CHEST_BOAT;
   public static final Item CHERRY_BOAT;
   public static final Item CHERRY_CHEST_BOAT;
   public static final Item DARK_OAK_BOAT;
   public static final Item DARK_OAK_CHEST_BOAT;
   public static final Item PALE_OAK_BOAT;
   public static final Item PALE_OAK_CHEST_BOAT;
   public static final Item MANGROVE_BOAT;
   public static final Item MANGROVE_CHEST_BOAT;
   public static final Item BAMBOO_RAFT;
   public static final Item BAMBOO_CHEST_RAFT;
   public static final Item STRUCTURE_BLOCK;
   public static final Item JIGSAW;
   public static final Item TURTLE_HELMET;
   public static final Item TURTLE_SCUTE;
   public static final Item ARMADILLO_SCUTE;
   public static final Item WOLF_ARMOR;
   public static final Item FLINT_AND_STEEL;
   public static final Item BOWL;
   public static final Item APPLE;
   public static final Item BOW;
   public static final Item ARROW;
   public static final Item COAL;
   public static final Item CHARCOAL;
   public static final Item DIAMOND;
   public static final Item EMERALD;
   public static final Item LAPIS_LAZULI;
   public static final Item QUARTZ;
   public static final Item AMETHYST_SHARD;
   public static final Item RAW_IRON;
   public static final Item IRON_INGOT;
   public static final Item RAW_COPPER;
   public static final Item COPPER_INGOT;
   public static final Item RAW_GOLD;
   public static final Item GOLD_INGOT;
   public static final Item NETHERITE_INGOT;
   public static final Item NETHERITE_SCRAP;
   public static final Item WOODEN_SWORD;
   public static final Item WOODEN_SHOVEL;
   public static final Item WOODEN_PICKAXE;
   public static final Item WOODEN_AXE;
   public static final Item WOODEN_HOE;
   public static final Item STONE_SWORD;
   public static final Item STONE_SHOVEL;
   public static final Item STONE_PICKAXE;
   public static final Item STONE_AXE;
   public static final Item STONE_HOE;
   public static final Item GOLDEN_SWORD;
   public static final Item GOLDEN_SHOVEL;
   public static final Item GOLDEN_PICKAXE;
   public static final Item GOLDEN_AXE;
   public static final Item GOLDEN_HOE;
   public static final Item IRON_SWORD;
   public static final Item IRON_SHOVEL;
   public static final Item IRON_PICKAXE;
   public static final Item IRON_AXE;
   public static final Item IRON_HOE;
   public static final Item DIAMOND_SWORD;
   public static final Item DIAMOND_SHOVEL;
   public static final Item DIAMOND_PICKAXE;
   public static final Item DIAMOND_AXE;
   public static final Item DIAMOND_HOE;
   public static final Item NETHERITE_SWORD;
   public static final Item NETHERITE_SHOVEL;
   public static final Item NETHERITE_PICKAXE;
   public static final Item NETHERITE_AXE;
   public static final Item NETHERITE_HOE;
   public static final Item STICK;
   public static final Item MUSHROOM_STEW;
   public static final Item STRING;
   public static final Item FEATHER;
   public static final Item GUNPOWDER;
   public static final Item WHEAT_SEEDS;
   public static final Item WHEAT;
   public static final Item BREAD;
   public static final Item LEATHER_HELMET;
   public static final Item LEATHER_CHESTPLATE;
   public static final Item LEATHER_LEGGINGS;
   public static final Item LEATHER_BOOTS;
   public static final Item CHAINMAIL_HELMET;
   public static final Item CHAINMAIL_CHESTPLATE;
   public static final Item CHAINMAIL_LEGGINGS;
   public static final Item CHAINMAIL_BOOTS;
   public static final Item IRON_HELMET;
   public static final Item IRON_CHESTPLATE;
   public static final Item IRON_LEGGINGS;
   public static final Item IRON_BOOTS;
   public static final Item DIAMOND_HELMET;
   public static final Item DIAMOND_CHESTPLATE;
   public static final Item DIAMOND_LEGGINGS;
   public static final Item DIAMOND_BOOTS;
   public static final Item GOLDEN_HELMET;
   public static final Item GOLDEN_CHESTPLATE;
   public static final Item GOLDEN_LEGGINGS;
   public static final Item GOLDEN_BOOTS;
   public static final Item NETHERITE_HELMET;
   public static final Item NETHERITE_CHESTPLATE;
   public static final Item NETHERITE_LEGGINGS;
   public static final Item NETHERITE_BOOTS;
   public static final Item FLINT;
   public static final Item PORKCHOP;
   public static final Item COOKED_PORKCHOP;
   public static final Item PAINTING;
   public static final Item GOLDEN_APPLE;
   public static final Item ENCHANTED_GOLDEN_APPLE;
   public static final Item OAK_SIGN;
   public static final Item SPRUCE_SIGN;
   public static final Item BIRCH_SIGN;
   public static final Item JUNGLE_SIGN;
   public static final Item ACACIA_SIGN;
   public static final Item CHERRY_SIGN;
   public static final Item DARK_OAK_SIGN;
   public static final Item PALE_OAK_SIGN;
   public static final Item MANGROVE_SIGN;
   public static final Item BAMBOO_SIGN;
   public static final Item CRIMSON_SIGN;
   public static final Item WARPED_SIGN;
   public static final Item OAK_HANGING_SIGN;
   public static final Item SPRUCE_HANGING_SIGN;
   public static final Item BIRCH_HANGING_SIGN;
   public static final Item JUNGLE_HANGING_SIGN;
   public static final Item ACACIA_HANGING_SIGN;
   public static final Item CHERRY_HANGING_SIGN;
   public static final Item DARK_OAK_HANGING_SIGN;
   public static final Item PALE_OAK_HANGING_SIGN;
   public static final Item MANGROVE_HANGING_SIGN;
   public static final Item BAMBOO_HANGING_SIGN;
   public static final Item CRIMSON_HANGING_SIGN;
   public static final Item WARPED_HANGING_SIGN;
   public static final Item BUCKET;
   public static final Item WATER_BUCKET;
   public static final Item LAVA_BUCKET;
   public static final Item POWDER_SNOW_BUCKET;
   public static final Item SNOWBALL;
   public static final Item LEATHER;
   public static final Item MILK_BUCKET;
   public static final Item PUFFERFISH_BUCKET;
   public static final Item SALMON_BUCKET;
   public static final Item COD_BUCKET;
   public static final Item TROPICAL_FISH_BUCKET;
   public static final Item AXOLOTL_BUCKET;
   public static final Item TADPOLE_BUCKET;
   public static final Item BRICK;
   public static final Item CLAY_BALL;
   public static final Item DRIED_KELP_BLOCK;
   public static final Item PAPER;
   public static final Item BOOK;
   public static final Item SLIME_BALL;
   public static final Item EGG;
   public static final Item COMPASS;
   public static final Item RECOVERY_COMPASS;
   public static final Item BUNDLE;
   public static final Item WHITE_BUNDLE;
   public static final Item ORANGE_BUNDLE;
   public static final Item MAGENTA_BUNDLE;
   public static final Item LIGHT_BLUE_BUNDLE;
   public static final Item YELLOW_BUNDLE;
   public static final Item LIME_BUNDLE;
   public static final Item PINK_BUNDLE;
   public static final Item GRAY_BUNDLE;
   public static final Item LIGHT_GRAY_BUNDLE;
   public static final Item CYAN_BUNDLE;
   public static final Item PURPLE_BUNDLE;
   public static final Item BLUE_BUNDLE;
   public static final Item BROWN_BUNDLE;
   public static final Item GREEN_BUNDLE;
   public static final Item RED_BUNDLE;
   public static final Item BLACK_BUNDLE;
   public static final Item FISHING_ROD;
   public static final Item CLOCK;
   public static final Item SPYGLASS;
   public static final Item GLOWSTONE_DUST;
   public static final Item COD;
   public static final Item SALMON;
   public static final Item TROPICAL_FISH;
   public static final Item PUFFERFISH;
   public static final Item COOKED_COD;
   public static final Item COOKED_SALMON;
   public static final Item INK_SAC;
   public static final Item GLOW_INK_SAC;
   public static final Item COCOA_BEANS;
   public static final Item WHITE_DYE;
   public static final Item ORANGE_DYE;
   public static final Item MAGENTA_DYE;
   public static final Item LIGHT_BLUE_DYE;
   public static final Item YELLOW_DYE;
   public static final Item LIME_DYE;
   public static final Item PINK_DYE;
   public static final Item GRAY_DYE;
   public static final Item LIGHT_GRAY_DYE;
   public static final Item CYAN_DYE;
   public static final Item PURPLE_DYE;
   public static final Item BLUE_DYE;
   public static final Item BROWN_DYE;
   public static final Item GREEN_DYE;
   public static final Item RED_DYE;
   public static final Item BLACK_DYE;
   public static final Item BONE_MEAL;
   public static final Item BONE;
   public static final Item SUGAR;
   public static final Item CAKE;
   public static final Item WHITE_BED;
   public static final Item ORANGE_BED;
   public static final Item MAGENTA_BED;
   public static final Item LIGHT_BLUE_BED;
   public static final Item YELLOW_BED;
   public static final Item LIME_BED;
   public static final Item PINK_BED;
   public static final Item GRAY_BED;
   public static final Item LIGHT_GRAY_BED;
   public static final Item CYAN_BED;
   public static final Item PURPLE_BED;
   public static final Item BLUE_BED;
   public static final Item BROWN_BED;
   public static final Item GREEN_BED;
   public static final Item RED_BED;
   public static final Item BLACK_BED;
   public static final Item COOKIE;
   public static final Item CRAFTER;
   public static final Item FILLED_MAP;
   public static final Item SHEARS;
   public static final Item MELON_SLICE;
   public static final Item DRIED_KELP;
   public static final Item PUMPKIN_SEEDS;
   public static final Item MELON_SEEDS;
   public static final Item BEEF;
   public static final Item COOKED_BEEF;
   public static final Item CHICKEN;
   public static final Item COOKED_CHICKEN;
   public static final Item ROTTEN_FLESH;
   public static final Item ENDER_PEARL;
   public static final Item BLAZE_ROD;
   public static final Item GHAST_TEAR;
   public static final Item GOLD_NUGGET;
   public static final Item NETHER_WART;
   public static final Item GLASS_BOTTLE;
   public static final Item POTION;
   public static final Item SPIDER_EYE;
   public static final Item FERMENTED_SPIDER_EYE;
   public static final Item BLAZE_POWDER;
   public static final Item MAGMA_CREAM;
   public static final Item BREWING_STAND;
   public static final Item CAULDRON;
   public static final Item ENDER_EYE;
   public static final Item GLISTERING_MELON_SLICE;
   public static final Item ARMADILLO_SPAWN_EGG;
   public static final Item ALLAY_SPAWN_EGG;
   public static final Item AXOLOTL_SPAWN_EGG;
   public static final Item BAT_SPAWN_EGG;
   public static final Item BEE_SPAWN_EGG;
   public static final Item BLAZE_SPAWN_EGG;
   public static final Item BOGGED_SPAWN_EGG;
   public static final Item BREEZE_SPAWN_EGG;
   public static final Item CAT_SPAWN_EGG;
   public static final Item CAMEL_SPAWN_EGG;
   public static final Item CAVE_SPIDER_SPAWN_EGG;
   public static final Item CHICKEN_SPAWN_EGG;
   public static final Item COD_SPAWN_EGG;
   public static final Item COW_SPAWN_EGG;
   public static final Item CREEPER_SPAWN_EGG;
   public static final Item DOLPHIN_SPAWN_EGG;
   public static final Item DONKEY_SPAWN_EGG;
   public static final Item DROWNED_SPAWN_EGG;
   public static final Item ELDER_GUARDIAN_SPAWN_EGG;
   public static final Item ENDER_DRAGON_SPAWN_EGG;
   public static final Item ENDERMAN_SPAWN_EGG;
   public static final Item ENDERMITE_SPAWN_EGG;
   public static final Item EVOKER_SPAWN_EGG;
   public static final Item FOX_SPAWN_EGG;
   public static final Item FROG_SPAWN_EGG;
   public static final Item GHAST_SPAWN_EGG;
   public static final Item GLOW_SQUID_SPAWN_EGG;
   public static final Item GOAT_SPAWN_EGG;
   public static final Item GUARDIAN_SPAWN_EGG;
   public static final Item HOGLIN_SPAWN_EGG;
   public static final Item HORSE_SPAWN_EGG;
   public static final Item HUSK_SPAWN_EGG;
   public static final Item IRON_GOLEM_SPAWN_EGG;
   public static final Item LLAMA_SPAWN_EGG;
   public static final Item MAGMA_CUBE_SPAWN_EGG;
   public static final Item MOOSHROOM_SPAWN_EGG;
   public static final Item MULE_SPAWN_EGG;
   public static final Item OCELOT_SPAWN_EGG;
   public static final Item PANDA_SPAWN_EGG;
   public static final Item PARROT_SPAWN_EGG;
   public static final Item PHANTOM_SPAWN_EGG;
   public static final Item PIG_SPAWN_EGG;
   public static final Item PIGLIN_SPAWN_EGG;
   public static final Item PIGLIN_BRUTE_SPAWN_EGG;
   public static final Item PILLAGER_SPAWN_EGG;
   public static final Item POLAR_BEAR_SPAWN_EGG;
   public static final Item PUFFERFISH_SPAWN_EGG;
   public static final Item RABBIT_SPAWN_EGG;
   public static final Item RAVAGER_SPAWN_EGG;
   public static final Item SALMON_SPAWN_EGG;
   public static final Item SHEEP_SPAWN_EGG;
   public static final Item SHULKER_SPAWN_EGG;
   public static final Item SILVERFISH_SPAWN_EGG;
   public static final Item SKELETON_SPAWN_EGG;
   public static final Item SKELETON_HORSE_SPAWN_EGG;
   public static final Item SLIME_SPAWN_EGG;
   public static final Item SNIFFER_SPAWN_EGG;
   public static final Item SNOW_GOLEM_SPAWN_EGG;
   public static final Item SPIDER_SPAWN_EGG;
   public static final Item SQUID_SPAWN_EGG;
   public static final Item STRAY_SPAWN_EGG;
   public static final Item STRIDER_SPAWN_EGG;
   public static final Item TADPOLE_SPAWN_EGG;
   public static final Item TRADER_LLAMA_SPAWN_EGG;
   public static final Item TROPICAL_FISH_SPAWN_EGG;
   public static final Item TURTLE_SPAWN_EGG;
   public static final Item VEX_SPAWN_EGG;
   public static final Item VILLAGER_SPAWN_EGG;
   public static final Item VINDICATOR_SPAWN_EGG;
   public static final Item WANDERING_TRADER_SPAWN_EGG;
   public static final Item WARDEN_SPAWN_EGG;
   public static final Item WITCH_SPAWN_EGG;
   public static final Item WITHER_SPAWN_EGG;
   public static final Item WITHER_SKELETON_SPAWN_EGG;
   public static final Item WOLF_SPAWN_EGG;
   public static final Item ZOGLIN_SPAWN_EGG;
   public static final Item CREAKING_SPAWN_EGG;
   public static final Item ZOMBIE_SPAWN_EGG;
   public static final Item ZOMBIE_HORSE_SPAWN_EGG;
   public static final Item ZOMBIE_VILLAGER_SPAWN_EGG;
   public static final Item ZOMBIFIED_PIGLIN_SPAWN_EGG;
   public static final Item EXPERIENCE_BOTTLE;
   public static final Item FIRE_CHARGE;
   public static final Item WIND_CHARGE;
   public static final Item WRITABLE_BOOK;
   public static final Item WRITTEN_BOOK;
   public static final Item BREEZE_ROD;
   public static final Item MACE;
   public static final Item ITEM_FRAME;
   public static final Item GLOW_ITEM_FRAME;
   public static final Item FLOWER_POT;
   public static final Item CARROT;
   public static final Item POTATO;
   public static final Item BAKED_POTATO;
   public static final Item POISONOUS_POTATO;
   public static final Item MAP;
   public static final Item GOLDEN_CARROT;
   public static final Item SKELETON_SKULL;
   public static final Item WITHER_SKELETON_SKULL;
   public static final Item PLAYER_HEAD;
   public static final Item ZOMBIE_HEAD;
   public static final Item CREEPER_HEAD;
   public static final Item DRAGON_HEAD;
   public static final Item PIGLIN_HEAD;
   public static final Item NETHER_STAR;
   public static final Item PUMPKIN_PIE;
   public static final Item FIREWORK_ROCKET;
   public static final Item FIREWORK_STAR;
   public static final Item ENCHANTED_BOOK;
   public static final Item NETHER_BRICK;
   public static final Item RESIN_BRICK;
   public static final Item PRISMARINE_SHARD;
   public static final Item PRISMARINE_CRYSTALS;
   public static final Item RABBIT;
   public static final Item COOKED_RABBIT;
   public static final Item RABBIT_STEW;
   public static final Item RABBIT_FOOT;
   public static final Item RABBIT_HIDE;
   public static final Item ARMOR_STAND;
   public static final Item IRON_HORSE_ARMOR;
   public static final Item GOLDEN_HORSE_ARMOR;
   public static final Item DIAMOND_HORSE_ARMOR;
   public static final Item LEATHER_HORSE_ARMOR;
   public static final Item LEAD;
   public static final Item NAME_TAG;
   public static final Item COMMAND_BLOCK_MINECART;
   public static final Item MUTTON;
   public static final Item COOKED_MUTTON;
   public static final Item WHITE_BANNER;
   public static final Item ORANGE_BANNER;
   public static final Item MAGENTA_BANNER;
   public static final Item LIGHT_BLUE_BANNER;
   public static final Item YELLOW_BANNER;
   public static final Item LIME_BANNER;
   public static final Item PINK_BANNER;
   public static final Item GRAY_BANNER;
   public static final Item LIGHT_GRAY_BANNER;
   public static final Item CYAN_BANNER;
   public static final Item PURPLE_BANNER;
   public static final Item BLUE_BANNER;
   public static final Item BROWN_BANNER;
   public static final Item GREEN_BANNER;
   public static final Item RED_BANNER;
   public static final Item BLACK_BANNER;
   public static final Item END_CRYSTAL;
   public static final Item CHORUS_FRUIT;
   public static final Item POPPED_CHORUS_FRUIT;
   public static final Item TORCHFLOWER_SEEDS;
   public static final Item PITCHER_POD;
   public static final Item BEETROOT;
   public static final Item BEETROOT_SEEDS;
   public static final Item BEETROOT_SOUP;
   public static final Item DRAGON_BREATH;
   public static final Item SPLASH_POTION;
   public static final Item SPECTRAL_ARROW;
   public static final Item TIPPED_ARROW;
   public static final Item LINGERING_POTION;
   public static final Item SHIELD;
   public static final Item TOTEM_OF_UNDYING;
   public static final Item SHULKER_SHELL;
   public static final Item IRON_NUGGET;
   public static final Item KNOWLEDGE_BOOK;
   public static final Item DEBUG_STICK;
   public static final Item MUSIC_DISC_13;
   public static final Item MUSIC_DISC_CAT;
   public static final Item MUSIC_DISC_BLOCKS;
   public static final Item MUSIC_DISC_CHIRP;
   public static final Item MUSIC_DISC_CREATOR;
   public static final Item MUSIC_DISC_CREATOR_MUSIC_BOX;
   public static final Item MUSIC_DISC_FAR;
   public static final Item MUSIC_DISC_MALL;
   public static final Item MUSIC_DISC_MELLOHI;
   public static final Item MUSIC_DISC_STAL;
   public static final Item MUSIC_DISC_STRAD;
   public static final Item MUSIC_DISC_WARD;
   public static final Item MUSIC_DISC_11;
   public static final Item MUSIC_DISC_WAIT;
   public static final Item MUSIC_DISC_OTHERSIDE;
   public static final Item MUSIC_DISC_RELIC;
   public static final Item MUSIC_DISC_5;
   public static final Item MUSIC_DISC_PIGSTEP;
   public static final Item MUSIC_DISC_PRECIPICE;
   public static final Item DISC_FRAGMENT_5;
   public static final Item TRIDENT;
   public static final Item NAUTILUS_SHELL;
   public static final Item HEART_OF_THE_SEA;
   public static final Item CROSSBOW;
   public static final Item SUSPICIOUS_STEW;
   public static final Item LOOM;
   public static final Item FLOWER_BANNER_PATTERN;
   public static final Item CREEPER_BANNER_PATTERN;
   public static final Item SKULL_BANNER_PATTERN;
   public static final Item MOJANG_BANNER_PATTERN;
   public static final Item GLOBE_BANNER_PATTERN;
   public static final Item PIGLIN_BANNER_PATTERN;
   public static final Item FLOW_BANNER_PATTERN;
   public static final Item GUSTER_BANNER_PATTERN;
   public static final Item FIELD_MASONED_BANNER_PATTERN;
   public static final Item BORDURE_INDENTED_BANNER_PATTERN;
   public static final Item GOAT_HORN;
   public static final Item COMPOSTER;
   public static final Item BARREL;
   public static final Item SMOKER;
   public static final Item BLAST_FURNACE;
   public static final Item CARTOGRAPHY_TABLE;
   public static final Item FLETCHING_TABLE;
   public static final Item GRINDSTONE;
   public static final Item SMITHING_TABLE;
   public static final Item STONECUTTER;
   public static final Item BELL;
   public static final Item LANTERN;
   public static final Item SOUL_LANTERN;
   public static final Item SWEET_BERRIES;
   public static final Item GLOW_BERRIES;
   public static final Item CAMPFIRE;
   public static final Item SOUL_CAMPFIRE;
   public static final Item SHROOMLIGHT;
   public static final Item HONEYCOMB;
   public static final Item BEE_NEST;
   public static final Item BEEHIVE;
   public static final Item HONEY_BOTTLE;
   public static final Item HONEYCOMB_BLOCK;
   public static final Item LODESTONE;
   public static final Item CRYING_OBSIDIAN;
   public static final Item BLACKSTONE;
   public static final Item BLACKSTONE_SLAB;
   public static final Item BLACKSTONE_STAIRS;
   public static final Item GILDED_BLACKSTONE;
   public static final Item POLISHED_BLACKSTONE;
   public static final Item POLISHED_BLACKSTONE_SLAB;
   public static final Item POLISHED_BLACKSTONE_STAIRS;
   public static final Item CHISELED_POLISHED_BLACKSTONE;
   public static final Item POLISHED_BLACKSTONE_BRICKS;
   public static final Item POLISHED_BLACKSTONE_BRICK_SLAB;
   public static final Item POLISHED_BLACKSTONE_BRICK_STAIRS;
   public static final Item CRACKED_POLISHED_BLACKSTONE_BRICKS;
   public static final Item RESPAWN_ANCHOR;
   public static final Item CANDLE;
   public static final Item WHITE_CANDLE;
   public static final Item ORANGE_CANDLE;
   public static final Item MAGENTA_CANDLE;
   public static final Item LIGHT_BLUE_CANDLE;
   public static final Item YELLOW_CANDLE;
   public static final Item LIME_CANDLE;
   public static final Item PINK_CANDLE;
   public static final Item GRAY_CANDLE;
   public static final Item LIGHT_GRAY_CANDLE;
   public static final Item CYAN_CANDLE;
   public static final Item PURPLE_CANDLE;
   public static final Item BLUE_CANDLE;
   public static final Item BROWN_CANDLE;
   public static final Item GREEN_CANDLE;
   public static final Item RED_CANDLE;
   public static final Item BLACK_CANDLE;
   public static final Item SMALL_AMETHYST_BUD;
   public static final Item MEDIUM_AMETHYST_BUD;
   public static final Item LARGE_AMETHYST_BUD;
   public static final Item AMETHYST_CLUSTER;
   public static final Item POINTED_DRIPSTONE;
   public static final Item OCHRE_FROGLIGHT;
   public static final Item VERDANT_FROGLIGHT;
   public static final Item PEARLESCENT_FROGLIGHT;
   public static final Item FROGSPAWN;
   public static final Item ECHO_SHARD;
   public static final Item BRUSH;
   public static final Item NETHERITE_UPGRADE_SMITHING_TEMPLATE;
   public static final Item SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item DUNE_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item COAST_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item WILD_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item WARD_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item EYE_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item VEX_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item TIDE_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item RIB_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item RAISER_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item HOST_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item FLOW_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item BOLT_ARMOR_TRIM_SMITHING_TEMPLATE;
   public static final Item ANGLER_POTTERY_SHERD;
   public static final Item ARCHER_POTTERY_SHERD;
   public static final Item ARMS_UP_POTTERY_SHERD;
   public static final Item BLADE_POTTERY_SHERD;
   public static final Item BREWER_POTTERY_SHERD;
   public static final Item BURN_POTTERY_SHERD;
   public static final Item DANGER_POTTERY_SHERD;
   public static final Item EXPLORER_POTTERY_SHERD;
   public static final Item FLOW_POTTERY_SHERD;
   public static final Item FRIEND_POTTERY_SHERD;
   public static final Item GUSTER_POTTERY_SHERD;
   public static final Item HEART_POTTERY_SHERD;
   public static final Item HEARTBREAK_POTTERY_SHERD;
   public static final Item HOWL_POTTERY_SHERD;
   public static final Item MINER_POTTERY_SHERD;
   public static final Item MOURNER_POTTERY_SHERD;
   public static final Item PLENTY_POTTERY_SHERD;
   public static final Item PRIZE_POTTERY_SHERD;
   public static final Item SCRAPE_POTTERY_SHERD;
   public static final Item SHEAF_POTTERY_SHERD;
   public static final Item SHELTER_POTTERY_SHERD;
   public static final Item SKULL_POTTERY_SHERD;
   public static final Item SNORT_POTTERY_SHERD;
   public static final Item COPPER_GRATE;
   public static final Item EXPOSED_COPPER_GRATE;
   public static final Item WEATHERED_COPPER_GRATE;
   public static final Item OXIDIZED_COPPER_GRATE;
   public static final Item WAXED_COPPER_GRATE;
   public static final Item WAXED_EXPOSED_COPPER_GRATE;
   public static final Item WAXED_WEATHERED_COPPER_GRATE;
   public static final Item WAXED_OXIDIZED_COPPER_GRATE;
   public static final Item COPPER_BULB;
   public static final Item EXPOSED_COPPER_BULB;
   public static final Item WEATHERED_COPPER_BULB;
   public static final Item OXIDIZED_COPPER_BULB;
   public static final Item WAXED_COPPER_BULB;
   public static final Item WAXED_EXPOSED_COPPER_BULB;
   public static final Item WAXED_WEATHERED_COPPER_BULB;
   public static final Item WAXED_OXIDIZED_COPPER_BULB;
   public static final Item TRIAL_SPAWNER;
   public static final Item TRIAL_KEY;
   public static final Item OMINOUS_TRIAL_KEY;
   public static final Item VAULT;
   public static final Item OMINOUS_BOTTLE;

   public Items() {
      super();
   }

   private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block var0) {
      return (var1) -> {
         return new BlockItem(var0, var1.useItemDescriptionPrefix());
      };
   }

   private static ResourceKey<Item> vanillaItemId(String var0) {
      return ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(var0));
   }

   private static ResourceKey<Item> blockIdToItemId(ResourceKey<Block> var0) {
      return ResourceKey.create(Registries.ITEM, var0.location());
   }

   public static Item registerBlock(Block var0) {
      return registerBlock(var0, BlockItem::new);
   }

   public static Item registerBlock(Block var0, Item.Properties var1) {
      return registerBlock(var0, BlockItem::new, var1);
   }

   public static Item registerBlock(Block var0, UnaryOperator<Item.Properties> var1) {
      return registerBlock(var0, (var1x, var2) -> {
         return new BlockItem(var1x, (Item.Properties)var1.apply(var2));
      });
   }

   public static Item registerBlock(Block var0, Block... var1) {
      Item var2 = registerBlock(var0);
      Block[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Block var6 = var3[var5];
         Item.BY_BLOCK.put(var6, var2);
      }

      return var2;
   }

   public static Item registerBlock(Block var0, BiFunction<Block, Item.Properties, Item> var1) {
      return registerBlock(var0, var1, new Item.Properties());
   }

   public static Item registerBlock(Block var0, BiFunction<Block, Item.Properties, Item> var1, Item.Properties var2) {
      return registerItem(blockIdToItemId(var0.builtInRegistryHolder().key()), (var2x) -> {
         return (Item)var1.apply(var0, var2x);
      }, var2.useBlockDescriptionPrefix());
   }

   public static Item registerItem(String var0, Function<Item.Properties, Item> var1) {
      return registerItem(vanillaItemId(var0), var1, new Item.Properties());
   }

   public static Item registerItem(String var0, Function<Item.Properties, Item> var1, Item.Properties var2) {
      return registerItem(vanillaItemId(var0), var1, var2);
   }

   public static Item registerItem(String var0, Item.Properties var1) {
      return registerItem(vanillaItemId(var0), Item::new, var1);
   }

   public static Item registerItem(String var0) {
      return registerItem(vanillaItemId(var0), Item::new, new Item.Properties());
   }

   public static Item registerItem(ResourceKey<Item> var0, Function<Item.Properties, Item> var1) {
      return registerItem(var0, var1, new Item.Properties());
   }

   public static Item registerItem(ResourceKey<Item> var0, Function<Item.Properties, Item> var1, Item.Properties var2) {
      Item var3 = (Item)var1.apply(var2.setId(var0));
      if (var3 instanceof BlockItem var4) {
         var4.registerBlocks(Item.BY_BLOCK, var3);
      }

      return (Item)Registry.register(BuiltInRegistries.ITEM, (ResourceKey)var0, var3);
   }

   static {
      AIR = registerBlock(Blocks.AIR, AirItem::new);
      STONE = registerBlock(Blocks.STONE);
      GRANITE = registerBlock(Blocks.GRANITE);
      POLISHED_GRANITE = registerBlock(Blocks.POLISHED_GRANITE);
      DIORITE = registerBlock(Blocks.DIORITE);
      POLISHED_DIORITE = registerBlock(Blocks.POLISHED_DIORITE);
      ANDESITE = registerBlock(Blocks.ANDESITE);
      POLISHED_ANDESITE = registerBlock(Blocks.POLISHED_ANDESITE);
      DEEPSLATE = registerBlock(Blocks.DEEPSLATE);
      COBBLED_DEEPSLATE = registerBlock(Blocks.COBBLED_DEEPSLATE);
      POLISHED_DEEPSLATE = registerBlock(Blocks.POLISHED_DEEPSLATE);
      CALCITE = registerBlock(Blocks.CALCITE);
      TUFF = registerBlock(Blocks.TUFF);
      TUFF_SLAB = registerBlock(Blocks.TUFF_SLAB);
      TUFF_STAIRS = registerBlock(Blocks.TUFF_STAIRS);
      TUFF_WALL = registerBlock(Blocks.TUFF_WALL);
      CHISELED_TUFF = registerBlock(Blocks.CHISELED_TUFF);
      POLISHED_TUFF = registerBlock(Blocks.POLISHED_TUFF);
      POLISHED_TUFF_SLAB = registerBlock(Blocks.POLISHED_TUFF_SLAB);
      POLISHED_TUFF_STAIRS = registerBlock(Blocks.POLISHED_TUFF_STAIRS);
      POLISHED_TUFF_WALL = registerBlock(Blocks.POLISHED_TUFF_WALL);
      TUFF_BRICKS = registerBlock(Blocks.TUFF_BRICKS);
      TUFF_BRICK_SLAB = registerBlock(Blocks.TUFF_BRICK_SLAB);
      TUFF_BRICK_STAIRS = registerBlock(Blocks.TUFF_BRICK_STAIRS);
      TUFF_BRICK_WALL = registerBlock(Blocks.TUFF_BRICK_WALL);
      CHISELED_TUFF_BRICKS = registerBlock(Blocks.CHISELED_TUFF_BRICKS);
      DRIPSTONE_BLOCK = registerBlock(Blocks.DRIPSTONE_BLOCK);
      GRASS_BLOCK = registerBlock(Blocks.GRASS_BLOCK);
      DIRT = registerBlock(Blocks.DIRT);
      COARSE_DIRT = registerBlock(Blocks.COARSE_DIRT);
      PODZOL = registerBlock(Blocks.PODZOL);
      ROOTED_DIRT = registerBlock(Blocks.ROOTED_DIRT);
      MUD = registerBlock(Blocks.MUD);
      CRIMSON_NYLIUM = registerBlock(Blocks.CRIMSON_NYLIUM);
      WARPED_NYLIUM = registerBlock(Blocks.WARPED_NYLIUM);
      COBBLESTONE = registerBlock(Blocks.COBBLESTONE);
      OAK_PLANKS = registerBlock(Blocks.OAK_PLANKS);
      SPRUCE_PLANKS = registerBlock(Blocks.SPRUCE_PLANKS);
      BIRCH_PLANKS = registerBlock(Blocks.BIRCH_PLANKS);
      JUNGLE_PLANKS = registerBlock(Blocks.JUNGLE_PLANKS);
      ACACIA_PLANKS = registerBlock(Blocks.ACACIA_PLANKS);
      CHERRY_PLANKS = registerBlock(Blocks.CHERRY_PLANKS);
      DARK_OAK_PLANKS = registerBlock(Blocks.DARK_OAK_PLANKS);
      PALE_OAK_PLANKS = registerBlock(Blocks.PALE_OAK_PLANKS);
      MANGROVE_PLANKS = registerBlock(Blocks.MANGROVE_PLANKS);
      BAMBOO_PLANKS = registerBlock(Blocks.BAMBOO_PLANKS);
      CRIMSON_PLANKS = registerBlock(Blocks.CRIMSON_PLANKS);
      WARPED_PLANKS = registerBlock(Blocks.WARPED_PLANKS);
      BAMBOO_MOSAIC = registerBlock(Blocks.BAMBOO_MOSAIC);
      OAK_SAPLING = registerBlock(Blocks.OAK_SAPLING);
      SPRUCE_SAPLING = registerBlock(Blocks.SPRUCE_SAPLING);
      BIRCH_SAPLING = registerBlock(Blocks.BIRCH_SAPLING);
      JUNGLE_SAPLING = registerBlock(Blocks.JUNGLE_SAPLING);
      ACACIA_SAPLING = registerBlock(Blocks.ACACIA_SAPLING);
      CHERRY_SAPLING = registerBlock(Blocks.CHERRY_SAPLING);
      DARK_OAK_SAPLING = registerBlock(Blocks.DARK_OAK_SAPLING);
      PALE_OAK_SAPLING = registerBlock(Blocks.PALE_OAK_SAPLING);
      MANGROVE_PROPAGULE = registerBlock(Blocks.MANGROVE_PROPAGULE);
      BEDROCK = registerBlock(Blocks.BEDROCK);
      SAND = registerBlock(Blocks.SAND);
      SUSPICIOUS_SAND = registerBlock(Blocks.SUSPICIOUS_SAND);
      SUSPICIOUS_GRAVEL = registerBlock(Blocks.SUSPICIOUS_GRAVEL);
      RED_SAND = registerBlock(Blocks.RED_SAND);
      GRAVEL = registerBlock(Blocks.GRAVEL);
      COAL_ORE = registerBlock(Blocks.COAL_ORE);
      DEEPSLATE_COAL_ORE = registerBlock(Blocks.DEEPSLATE_COAL_ORE);
      IRON_ORE = registerBlock(Blocks.IRON_ORE);
      DEEPSLATE_IRON_ORE = registerBlock(Blocks.DEEPSLATE_IRON_ORE);
      COPPER_ORE = registerBlock(Blocks.COPPER_ORE);
      DEEPSLATE_COPPER_ORE = registerBlock(Blocks.DEEPSLATE_COPPER_ORE);
      GOLD_ORE = registerBlock(Blocks.GOLD_ORE);
      DEEPSLATE_GOLD_ORE = registerBlock(Blocks.DEEPSLATE_GOLD_ORE);
      REDSTONE_ORE = registerBlock(Blocks.REDSTONE_ORE);
      DEEPSLATE_REDSTONE_ORE = registerBlock(Blocks.DEEPSLATE_REDSTONE_ORE);
      EMERALD_ORE = registerBlock(Blocks.EMERALD_ORE);
      DEEPSLATE_EMERALD_ORE = registerBlock(Blocks.DEEPSLATE_EMERALD_ORE);
      LAPIS_ORE = registerBlock(Blocks.LAPIS_ORE);
      DEEPSLATE_LAPIS_ORE = registerBlock(Blocks.DEEPSLATE_LAPIS_ORE);
      DIAMOND_ORE = registerBlock(Blocks.DIAMOND_ORE);
      DEEPSLATE_DIAMOND_ORE = registerBlock(Blocks.DEEPSLATE_DIAMOND_ORE);
      NETHER_GOLD_ORE = registerBlock(Blocks.NETHER_GOLD_ORE);
      NETHER_QUARTZ_ORE = registerBlock(Blocks.NETHER_QUARTZ_ORE);
      ANCIENT_DEBRIS = registerBlock(Blocks.ANCIENT_DEBRIS, (new Item.Properties()).fireResistant());
      COAL_BLOCK = registerBlock(Blocks.COAL_BLOCK);
      RAW_IRON_BLOCK = registerBlock(Blocks.RAW_IRON_BLOCK);
      RAW_COPPER_BLOCK = registerBlock(Blocks.RAW_COPPER_BLOCK);
      RAW_GOLD_BLOCK = registerBlock(Blocks.RAW_GOLD_BLOCK);
      HEAVY_CORE = registerBlock(Blocks.HEAVY_CORE, (new Item.Properties()).rarity(Rarity.EPIC));
      AMETHYST_BLOCK = registerBlock(Blocks.AMETHYST_BLOCK);
      BUDDING_AMETHYST = registerBlock(Blocks.BUDDING_AMETHYST);
      IRON_BLOCK = registerBlock(Blocks.IRON_BLOCK);
      COPPER_BLOCK = registerBlock(Blocks.COPPER_BLOCK);
      GOLD_BLOCK = registerBlock(Blocks.GOLD_BLOCK);
      DIAMOND_BLOCK = registerBlock(Blocks.DIAMOND_BLOCK);
      NETHERITE_BLOCK = registerBlock(Blocks.NETHERITE_BLOCK, (new Item.Properties()).fireResistant());
      EXPOSED_COPPER = registerBlock(Blocks.EXPOSED_COPPER);
      WEATHERED_COPPER = registerBlock(Blocks.WEATHERED_COPPER);
      OXIDIZED_COPPER = registerBlock(Blocks.OXIDIZED_COPPER);
      CHISELED_COPPER = registerBlock(Blocks.CHISELED_COPPER);
      EXPOSED_CHISELED_COPPER = registerBlock(Blocks.EXPOSED_CHISELED_COPPER);
      WEATHERED_CHISELED_COPPER = registerBlock(Blocks.WEATHERED_CHISELED_COPPER);
      OXIDIZED_CHISELED_COPPER = registerBlock(Blocks.OXIDIZED_CHISELED_COPPER);
      CUT_COPPER = registerBlock(Blocks.CUT_COPPER);
      EXPOSED_CUT_COPPER = registerBlock(Blocks.EXPOSED_CUT_COPPER);
      WEATHERED_CUT_COPPER = registerBlock(Blocks.WEATHERED_CUT_COPPER);
      OXIDIZED_CUT_COPPER = registerBlock(Blocks.OXIDIZED_CUT_COPPER);
      CUT_COPPER_STAIRS = registerBlock(Blocks.CUT_COPPER_STAIRS);
      EXPOSED_CUT_COPPER_STAIRS = registerBlock(Blocks.EXPOSED_CUT_COPPER_STAIRS);
      WEATHERED_CUT_COPPER_STAIRS = registerBlock(Blocks.WEATHERED_CUT_COPPER_STAIRS);
      OXIDIZED_CUT_COPPER_STAIRS = registerBlock(Blocks.OXIDIZED_CUT_COPPER_STAIRS);
      CUT_COPPER_SLAB = registerBlock(Blocks.CUT_COPPER_SLAB);
      EXPOSED_CUT_COPPER_SLAB = registerBlock(Blocks.EXPOSED_CUT_COPPER_SLAB);
      WEATHERED_CUT_COPPER_SLAB = registerBlock(Blocks.WEATHERED_CUT_COPPER_SLAB);
      OXIDIZED_CUT_COPPER_SLAB = registerBlock(Blocks.OXIDIZED_CUT_COPPER_SLAB);
      WAXED_COPPER_BLOCK = registerBlock(Blocks.WAXED_COPPER_BLOCK);
      WAXED_EXPOSED_COPPER = registerBlock(Blocks.WAXED_EXPOSED_COPPER);
      WAXED_WEATHERED_COPPER = registerBlock(Blocks.WAXED_WEATHERED_COPPER);
      WAXED_OXIDIZED_COPPER = registerBlock(Blocks.WAXED_OXIDIZED_COPPER);
      WAXED_CHISELED_COPPER = registerBlock(Blocks.WAXED_CHISELED_COPPER);
      WAXED_EXPOSED_CHISELED_COPPER = registerBlock(Blocks.WAXED_EXPOSED_CHISELED_COPPER);
      WAXED_WEATHERED_CHISELED_COPPER = registerBlock(Blocks.WAXED_WEATHERED_CHISELED_COPPER);
      WAXED_OXIDIZED_CHISELED_COPPER = registerBlock(Blocks.WAXED_OXIDIZED_CHISELED_COPPER);
      WAXED_CUT_COPPER = registerBlock(Blocks.WAXED_CUT_COPPER);
      WAXED_EXPOSED_CUT_COPPER = registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER);
      WAXED_WEATHERED_CUT_COPPER = registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER);
      WAXED_OXIDIZED_CUT_COPPER = registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER);
      WAXED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_CUT_COPPER_STAIRS);
      WAXED_EXPOSED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS);
      WAXED_WEATHERED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS);
      WAXED_OXIDIZED_CUT_COPPER_STAIRS = registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
      WAXED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_CUT_COPPER_SLAB);
      WAXED_EXPOSED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB);
      WAXED_WEATHERED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB);
      WAXED_OXIDIZED_CUT_COPPER_SLAB = registerBlock(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
      OAK_LOG = registerBlock(Blocks.OAK_LOG);
      SPRUCE_LOG = registerBlock(Blocks.SPRUCE_LOG);
      BIRCH_LOG = registerBlock(Blocks.BIRCH_LOG);
      JUNGLE_LOG = registerBlock(Blocks.JUNGLE_LOG);
      ACACIA_LOG = registerBlock(Blocks.ACACIA_LOG);
      CHERRY_LOG = registerBlock(Blocks.CHERRY_LOG);
      PALE_OAK_LOG = registerBlock(Blocks.PALE_OAK_LOG);
      DARK_OAK_LOG = registerBlock(Blocks.DARK_OAK_LOG);
      MANGROVE_LOG = registerBlock(Blocks.MANGROVE_LOG);
      MANGROVE_ROOTS = registerBlock(Blocks.MANGROVE_ROOTS);
      MUDDY_MANGROVE_ROOTS = registerBlock(Blocks.MUDDY_MANGROVE_ROOTS);
      CRIMSON_STEM = registerBlock(Blocks.CRIMSON_STEM);
      WARPED_STEM = registerBlock(Blocks.WARPED_STEM);
      BAMBOO_BLOCK = registerBlock(Blocks.BAMBOO_BLOCK);
      STRIPPED_OAK_LOG = registerBlock(Blocks.STRIPPED_OAK_LOG);
      STRIPPED_SPRUCE_LOG = registerBlock(Blocks.STRIPPED_SPRUCE_LOG);
      STRIPPED_BIRCH_LOG = registerBlock(Blocks.STRIPPED_BIRCH_LOG);
      STRIPPED_JUNGLE_LOG = registerBlock(Blocks.STRIPPED_JUNGLE_LOG);
      STRIPPED_ACACIA_LOG = registerBlock(Blocks.STRIPPED_ACACIA_LOG);
      STRIPPED_CHERRY_LOG = registerBlock(Blocks.STRIPPED_CHERRY_LOG);
      STRIPPED_DARK_OAK_LOG = registerBlock(Blocks.STRIPPED_DARK_OAK_LOG);
      STRIPPED_PALE_OAK_LOG = registerBlock(Blocks.STRIPPED_PALE_OAK_LOG);
      STRIPPED_MANGROVE_LOG = registerBlock(Blocks.STRIPPED_MANGROVE_LOG);
      STRIPPED_CRIMSON_STEM = registerBlock(Blocks.STRIPPED_CRIMSON_STEM);
      STRIPPED_WARPED_STEM = registerBlock(Blocks.STRIPPED_WARPED_STEM);
      STRIPPED_OAK_WOOD = registerBlock(Blocks.STRIPPED_OAK_WOOD);
      STRIPPED_SPRUCE_WOOD = registerBlock(Blocks.STRIPPED_SPRUCE_WOOD);
      STRIPPED_BIRCH_WOOD = registerBlock(Blocks.STRIPPED_BIRCH_WOOD);
      STRIPPED_JUNGLE_WOOD = registerBlock(Blocks.STRIPPED_JUNGLE_WOOD);
      STRIPPED_ACACIA_WOOD = registerBlock(Blocks.STRIPPED_ACACIA_WOOD);
      STRIPPED_CHERRY_WOOD = registerBlock(Blocks.STRIPPED_CHERRY_WOOD);
      STRIPPED_DARK_OAK_WOOD = registerBlock(Blocks.STRIPPED_DARK_OAK_WOOD);
      STRIPPED_PALE_OAK_WOOD = registerBlock(Blocks.STRIPPED_PALE_OAK_WOOD);
      STRIPPED_MANGROVE_WOOD = registerBlock(Blocks.STRIPPED_MANGROVE_WOOD);
      STRIPPED_CRIMSON_HYPHAE = registerBlock(Blocks.STRIPPED_CRIMSON_HYPHAE);
      STRIPPED_WARPED_HYPHAE = registerBlock(Blocks.STRIPPED_WARPED_HYPHAE);
      STRIPPED_BAMBOO_BLOCK = registerBlock(Blocks.STRIPPED_BAMBOO_BLOCK);
      OAK_WOOD = registerBlock(Blocks.OAK_WOOD);
      SPRUCE_WOOD = registerBlock(Blocks.SPRUCE_WOOD);
      BIRCH_WOOD = registerBlock(Blocks.BIRCH_WOOD);
      JUNGLE_WOOD = registerBlock(Blocks.JUNGLE_WOOD);
      ACACIA_WOOD = registerBlock(Blocks.ACACIA_WOOD);
      CHERRY_WOOD = registerBlock(Blocks.CHERRY_WOOD);
      PALE_OAK_WOOD = registerBlock(Blocks.PALE_OAK_WOOD);
      DARK_OAK_WOOD = registerBlock(Blocks.DARK_OAK_WOOD);
      MANGROVE_WOOD = registerBlock(Blocks.MANGROVE_WOOD);
      CRIMSON_HYPHAE = registerBlock(Blocks.CRIMSON_HYPHAE);
      WARPED_HYPHAE = registerBlock(Blocks.WARPED_HYPHAE);
      OAK_LEAVES = registerBlock(Blocks.OAK_LEAVES);
      SPRUCE_LEAVES = registerBlock(Blocks.SPRUCE_LEAVES);
      BIRCH_LEAVES = registerBlock(Blocks.BIRCH_LEAVES);
      JUNGLE_LEAVES = registerBlock(Blocks.JUNGLE_LEAVES);
      ACACIA_LEAVES = registerBlock(Blocks.ACACIA_LEAVES);
      CHERRY_LEAVES = registerBlock(Blocks.CHERRY_LEAVES);
      DARK_OAK_LEAVES = registerBlock(Blocks.DARK_OAK_LEAVES);
      PALE_OAK_LEAVES = registerBlock(Blocks.PALE_OAK_LEAVES);
      MANGROVE_LEAVES = registerBlock(Blocks.MANGROVE_LEAVES);
      AZALEA_LEAVES = registerBlock(Blocks.AZALEA_LEAVES);
      FLOWERING_AZALEA_LEAVES = registerBlock(Blocks.FLOWERING_AZALEA_LEAVES);
      SPONGE = registerBlock(Blocks.SPONGE);
      WET_SPONGE = registerBlock(Blocks.WET_SPONGE);
      GLASS = registerBlock(Blocks.GLASS);
      TINTED_GLASS = registerBlock(Blocks.TINTED_GLASS);
      LAPIS_BLOCK = registerBlock(Blocks.LAPIS_BLOCK);
      SANDSTONE = registerBlock(Blocks.SANDSTONE);
      CHISELED_SANDSTONE = registerBlock(Blocks.CHISELED_SANDSTONE);
      CUT_SANDSTONE = registerBlock(Blocks.CUT_SANDSTONE);
      COBWEB = registerBlock(Blocks.COBWEB);
      SHORT_GRASS = registerBlock(Blocks.SHORT_GRASS);
      FERN = registerBlock(Blocks.FERN);
      AZALEA = registerBlock(Blocks.AZALEA);
      FLOWERING_AZALEA = registerBlock(Blocks.FLOWERING_AZALEA);
      DEAD_BUSH = registerBlock(Blocks.DEAD_BUSH);
      SEAGRASS = registerBlock(Blocks.SEAGRASS);
      SEA_PICKLE = registerBlock(Blocks.SEA_PICKLE);
      WHITE_WOOL = registerBlock(Blocks.WHITE_WOOL);
      ORANGE_WOOL = registerBlock(Blocks.ORANGE_WOOL);
      MAGENTA_WOOL = registerBlock(Blocks.MAGENTA_WOOL);
      LIGHT_BLUE_WOOL = registerBlock(Blocks.LIGHT_BLUE_WOOL);
      YELLOW_WOOL = registerBlock(Blocks.YELLOW_WOOL);
      LIME_WOOL = registerBlock(Blocks.LIME_WOOL);
      PINK_WOOL = registerBlock(Blocks.PINK_WOOL);
      GRAY_WOOL = registerBlock(Blocks.GRAY_WOOL);
      LIGHT_GRAY_WOOL = registerBlock(Blocks.LIGHT_GRAY_WOOL);
      CYAN_WOOL = registerBlock(Blocks.CYAN_WOOL);
      PURPLE_WOOL = registerBlock(Blocks.PURPLE_WOOL);
      BLUE_WOOL = registerBlock(Blocks.BLUE_WOOL);
      BROWN_WOOL = registerBlock(Blocks.BROWN_WOOL);
      GREEN_WOOL = registerBlock(Blocks.GREEN_WOOL);
      RED_WOOL = registerBlock(Blocks.RED_WOOL);
      BLACK_WOOL = registerBlock(Blocks.BLACK_WOOL);
      DANDELION = registerBlock(Blocks.DANDELION);
      OPEN_EYEBLOSSOM = registerBlock(Blocks.OPEN_EYEBLOSSOM);
      CLOSED_EYEBLOSSOM = registerBlock(Blocks.CLOSED_EYEBLOSSOM);
      POPPY = registerBlock(Blocks.POPPY);
      BLUE_ORCHID = registerBlock(Blocks.BLUE_ORCHID);
      ALLIUM = registerBlock(Blocks.ALLIUM);
      AZURE_BLUET = registerBlock(Blocks.AZURE_BLUET);
      RED_TULIP = registerBlock(Blocks.RED_TULIP);
      ORANGE_TULIP = registerBlock(Blocks.ORANGE_TULIP);
      WHITE_TULIP = registerBlock(Blocks.WHITE_TULIP);
      PINK_TULIP = registerBlock(Blocks.PINK_TULIP);
      OXEYE_DAISY = registerBlock(Blocks.OXEYE_DAISY);
      CORNFLOWER = registerBlock(Blocks.CORNFLOWER);
      LILY_OF_THE_VALLEY = registerBlock(Blocks.LILY_OF_THE_VALLEY);
      WITHER_ROSE = registerBlock(Blocks.WITHER_ROSE);
      TORCHFLOWER = registerBlock(Blocks.TORCHFLOWER);
      PITCHER_PLANT = registerBlock(Blocks.PITCHER_PLANT);
      SPORE_BLOSSOM = registerBlock(Blocks.SPORE_BLOSSOM);
      BROWN_MUSHROOM = registerBlock(Blocks.BROWN_MUSHROOM);
      RED_MUSHROOM = registerBlock(Blocks.RED_MUSHROOM);
      CRIMSON_FUNGUS = registerBlock(Blocks.CRIMSON_FUNGUS);
      WARPED_FUNGUS = registerBlock(Blocks.WARPED_FUNGUS);
      CRIMSON_ROOTS = registerBlock(Blocks.CRIMSON_ROOTS);
      WARPED_ROOTS = registerBlock(Blocks.WARPED_ROOTS);
      NETHER_SPROUTS = registerBlock(Blocks.NETHER_SPROUTS);
      WEEPING_VINES = registerBlock(Blocks.WEEPING_VINES);
      TWISTING_VINES = registerBlock(Blocks.TWISTING_VINES);
      SUGAR_CANE = registerBlock(Blocks.SUGAR_CANE);
      KELP = registerBlock(Blocks.KELP);
      PINK_PETALS = registerBlock(Blocks.PINK_PETALS);
      MOSS_CARPET = registerBlock(Blocks.MOSS_CARPET);
      MOSS_BLOCK = registerBlock(Blocks.MOSS_BLOCK);
      PALE_MOSS_CARPET = registerBlock(Blocks.PALE_MOSS_CARPET);
      PALE_HANGING_MOSS = registerBlock(Blocks.PALE_HANGING_MOSS);
      PALE_MOSS_BLOCK = registerBlock(Blocks.PALE_MOSS_BLOCK);
      HANGING_ROOTS = registerBlock(Blocks.HANGING_ROOTS);
      BIG_DRIPLEAF = registerBlock(Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM);
      SMALL_DRIPLEAF = registerBlock(Blocks.SMALL_DRIPLEAF, DoubleHighBlockItem::new);
      BAMBOO = registerBlock(Blocks.BAMBOO);
      OAK_SLAB = registerBlock(Blocks.OAK_SLAB);
      SPRUCE_SLAB = registerBlock(Blocks.SPRUCE_SLAB);
      BIRCH_SLAB = registerBlock(Blocks.BIRCH_SLAB);
      JUNGLE_SLAB = registerBlock(Blocks.JUNGLE_SLAB);
      ACACIA_SLAB = registerBlock(Blocks.ACACIA_SLAB);
      CHERRY_SLAB = registerBlock(Blocks.CHERRY_SLAB);
      DARK_OAK_SLAB = registerBlock(Blocks.DARK_OAK_SLAB);
      PALE_OAK_SLAB = registerBlock(Blocks.PALE_OAK_SLAB);
      MANGROVE_SLAB = registerBlock(Blocks.MANGROVE_SLAB);
      BAMBOO_SLAB = registerBlock(Blocks.BAMBOO_SLAB);
      BAMBOO_MOSAIC_SLAB = registerBlock(Blocks.BAMBOO_MOSAIC_SLAB);
      CRIMSON_SLAB = registerBlock(Blocks.CRIMSON_SLAB);
      WARPED_SLAB = registerBlock(Blocks.WARPED_SLAB);
      STONE_SLAB = registerBlock(Blocks.STONE_SLAB);
      SMOOTH_STONE_SLAB = registerBlock(Blocks.SMOOTH_STONE_SLAB);
      SANDSTONE_SLAB = registerBlock(Blocks.SANDSTONE_SLAB);
      CUT_STANDSTONE_SLAB = registerBlock(Blocks.CUT_SANDSTONE_SLAB);
      PETRIFIED_OAK_SLAB = registerBlock(Blocks.PETRIFIED_OAK_SLAB);
      COBBLESTONE_SLAB = registerBlock(Blocks.COBBLESTONE_SLAB);
      BRICK_SLAB = registerBlock(Blocks.BRICK_SLAB);
      STONE_BRICK_SLAB = registerBlock(Blocks.STONE_BRICK_SLAB);
      MUD_BRICK_SLAB = registerBlock(Blocks.MUD_BRICK_SLAB);
      NETHER_BRICK_SLAB = registerBlock(Blocks.NETHER_BRICK_SLAB);
      QUARTZ_SLAB = registerBlock(Blocks.QUARTZ_SLAB);
      RED_SANDSTONE_SLAB = registerBlock(Blocks.RED_SANDSTONE_SLAB);
      CUT_RED_SANDSTONE_SLAB = registerBlock(Blocks.CUT_RED_SANDSTONE_SLAB);
      PURPUR_SLAB = registerBlock(Blocks.PURPUR_SLAB);
      PRISMARINE_SLAB = registerBlock(Blocks.PRISMARINE_SLAB);
      PRISMARINE_BRICK_SLAB = registerBlock(Blocks.PRISMARINE_BRICK_SLAB);
      DARK_PRISMARINE_SLAB = registerBlock(Blocks.DARK_PRISMARINE_SLAB);
      SMOOTH_QUARTZ = registerBlock(Blocks.SMOOTH_QUARTZ);
      SMOOTH_RED_SANDSTONE = registerBlock(Blocks.SMOOTH_RED_SANDSTONE);
      SMOOTH_SANDSTONE = registerBlock(Blocks.SMOOTH_SANDSTONE);
      SMOOTH_STONE = registerBlock(Blocks.SMOOTH_STONE);
      BRICKS = registerBlock(Blocks.BRICKS);
      BOOKSHELF = registerBlock(Blocks.BOOKSHELF);
      CHISELED_BOOKSHELF = registerBlock(Blocks.CHISELED_BOOKSHELF, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      DECORATED_POT = registerBlock(Blocks.DECORATED_POT, (new Item.Properties()).component(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      MOSSY_COBBLESTONE = registerBlock(Blocks.MOSSY_COBBLESTONE);
      OBSIDIAN = registerBlock(Blocks.OBSIDIAN);
      TORCH = registerBlock(Blocks.TORCH, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.WALL_TORCH, Direction.DOWN, var1);
      });
      END_ROD = registerBlock(Blocks.END_ROD);
      CHORUS_PLANT = registerBlock(Blocks.CHORUS_PLANT);
      CHORUS_FLOWER = registerBlock(Blocks.CHORUS_FLOWER);
      PURPUR_BLOCK = registerBlock(Blocks.PURPUR_BLOCK);
      PURPUR_PILLAR = registerBlock(Blocks.PURPUR_PILLAR);
      PURPUR_STAIRS = registerBlock(Blocks.PURPUR_STAIRS);
      SPAWNER = registerBlock(Blocks.SPAWNER);
      CREAKING_HEART = registerBlock(Blocks.CREAKING_HEART);
      CHEST = registerBlock(Blocks.CHEST, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      CRAFTING_TABLE = registerBlock(Blocks.CRAFTING_TABLE);
      FARMLAND = registerBlock(Blocks.FARMLAND);
      FURNACE = registerBlock(Blocks.FURNACE, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      LADDER = registerBlock(Blocks.LADDER);
      COBBLESTONE_STAIRS = registerBlock(Blocks.COBBLESTONE_STAIRS);
      SNOW = registerBlock(Blocks.SNOW);
      ICE = registerBlock(Blocks.ICE);
      SNOW_BLOCK = registerBlock(Blocks.SNOW_BLOCK);
      CACTUS = registerBlock(Blocks.CACTUS);
      CLAY = registerBlock(Blocks.CLAY);
      JUKEBOX = registerBlock(Blocks.JUKEBOX);
      OAK_FENCE = registerBlock(Blocks.OAK_FENCE);
      SPRUCE_FENCE = registerBlock(Blocks.SPRUCE_FENCE);
      BIRCH_FENCE = registerBlock(Blocks.BIRCH_FENCE);
      JUNGLE_FENCE = registerBlock(Blocks.JUNGLE_FENCE);
      ACACIA_FENCE = registerBlock(Blocks.ACACIA_FENCE);
      CHERRY_FENCE = registerBlock(Blocks.CHERRY_FENCE);
      DARK_OAK_FENCE = registerBlock(Blocks.DARK_OAK_FENCE);
      PALE_OAK_FENCE = registerBlock(Blocks.PALE_OAK_FENCE);
      MANGROVE_FENCE = registerBlock(Blocks.MANGROVE_FENCE);
      BAMBOO_FENCE = registerBlock(Blocks.BAMBOO_FENCE);
      CRIMSON_FENCE = registerBlock(Blocks.CRIMSON_FENCE);
      WARPED_FENCE = registerBlock(Blocks.WARPED_FENCE);
      PUMPKIN = registerBlock(Blocks.PUMPKIN);
      CARVED_PUMPKIN = registerBlock(Blocks.CARVED_PUMPKIN, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).setSwappable(false).setCameraOverlay(ResourceLocation.withDefaultNamespace("misc/pumpkinblur")).build());
      });
      JACK_O_LANTERN = registerBlock(Blocks.JACK_O_LANTERN);
      NETHERRACK = registerBlock(Blocks.NETHERRACK);
      SOUL_SAND = registerBlock(Blocks.SOUL_SAND);
      SOUL_SOIL = registerBlock(Blocks.SOUL_SOIL);
      BASALT = registerBlock(Blocks.BASALT);
      POLISHED_BASALT = registerBlock(Blocks.POLISHED_BASALT);
      SMOOTH_BASALT = registerBlock(Blocks.SMOOTH_BASALT);
      SOUL_TORCH = registerBlock(Blocks.SOUL_TORCH, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.SOUL_WALL_TORCH, Direction.DOWN, var1);
      });
      GLOWSTONE = registerBlock(Blocks.GLOWSTONE);
      INFESTED_STONE = registerBlock(Blocks.INFESTED_STONE);
      INFESTED_COBBLESTONE = registerBlock(Blocks.INFESTED_COBBLESTONE);
      INFESTED_STONE_BRICKS = registerBlock(Blocks.INFESTED_STONE_BRICKS);
      INFESTED_MOSSY_STONE_BRICKS = registerBlock(Blocks.INFESTED_MOSSY_STONE_BRICKS);
      INFESTED_CRACKED_STONE_BRICKS = registerBlock(Blocks.INFESTED_CRACKED_STONE_BRICKS);
      INFESTED_CHISELED_STONE_BRICKS = registerBlock(Blocks.INFESTED_CHISELED_STONE_BRICKS);
      INFESTED_DEEPSLATE = registerBlock(Blocks.INFESTED_DEEPSLATE);
      STONE_BRICKS = registerBlock(Blocks.STONE_BRICKS);
      MOSSY_STONE_BRICKS = registerBlock(Blocks.MOSSY_STONE_BRICKS);
      CRACKED_STONE_BRICKS = registerBlock(Blocks.CRACKED_STONE_BRICKS);
      CHISELED_STONE_BRICKS = registerBlock(Blocks.CHISELED_STONE_BRICKS);
      PACKED_MUD = registerBlock(Blocks.PACKED_MUD);
      MUD_BRICKS = registerBlock(Blocks.MUD_BRICKS);
      DEEPSLATE_BRICKS = registerBlock(Blocks.DEEPSLATE_BRICKS);
      CRACKED_DEEPSLATE_BRICKS = registerBlock(Blocks.CRACKED_DEEPSLATE_BRICKS);
      DEEPSLATE_TILES = registerBlock(Blocks.DEEPSLATE_TILES);
      CRACKED_DEEPSLATE_TILES = registerBlock(Blocks.CRACKED_DEEPSLATE_TILES);
      CHISELED_DEEPSLATE = registerBlock(Blocks.CHISELED_DEEPSLATE);
      REINFORCED_DEEPSLATE = registerBlock(Blocks.REINFORCED_DEEPSLATE);
      BROWN_MUSHROOM_BLOCK = registerBlock(Blocks.BROWN_MUSHROOM_BLOCK);
      RED_MUSHROOM_BLOCK = registerBlock(Blocks.RED_MUSHROOM_BLOCK);
      MUSHROOM_STEM = registerBlock(Blocks.MUSHROOM_STEM);
      IRON_BARS = registerBlock(Blocks.IRON_BARS);
      CHAIN = registerBlock(Blocks.CHAIN);
      GLASS_PANE = registerBlock(Blocks.GLASS_PANE);
      MELON = registerBlock(Blocks.MELON);
      VINE = registerBlock(Blocks.VINE);
      GLOW_LICHEN = registerBlock(Blocks.GLOW_LICHEN);
      RESIN_CLUMP = registerItem("resin_clump", createBlockItemWithCustomItemName(Blocks.RESIN_CLUMP));
      RESIN_BLOCK = registerBlock(Blocks.RESIN_BLOCK);
      RESIN_BRICKS = registerBlock(Blocks.RESIN_BRICKS);
      RESIN_BRICK_STAIRS = registerBlock(Blocks.RESIN_BRICK_STAIRS);
      RESIN_BRICK_SLAB = registerBlock(Blocks.RESIN_BRICK_SLAB);
      RESIN_BRICK_WALL = registerBlock(Blocks.RESIN_BRICK_WALL);
      CHISELED_RESIN_BRICKS = registerBlock(Blocks.CHISELED_RESIN_BRICKS);
      BRICK_STAIRS = registerBlock(Blocks.BRICK_STAIRS);
      STONE_BRICK_STAIRS = registerBlock(Blocks.STONE_BRICK_STAIRS);
      MUD_BRICK_STAIRS = registerBlock(Blocks.MUD_BRICK_STAIRS);
      MYCELIUM = registerBlock(Blocks.MYCELIUM);
      LILY_PAD = registerBlock(Blocks.LILY_PAD, PlaceOnWaterBlockItem::new);
      NETHER_BRICKS = registerBlock(Blocks.NETHER_BRICKS);
      CRACKED_NETHER_BRICKS = registerBlock(Blocks.CRACKED_NETHER_BRICKS);
      CHISELED_NETHER_BRICKS = registerBlock(Blocks.CHISELED_NETHER_BRICKS);
      NETHER_BRICK_FENCE = registerBlock(Blocks.NETHER_BRICK_FENCE);
      NETHER_BRICK_STAIRS = registerBlock(Blocks.NETHER_BRICK_STAIRS);
      SCULK = registerBlock(Blocks.SCULK);
      SCULK_VEIN = registerBlock(Blocks.SCULK_VEIN);
      SCULK_CATALYST = registerBlock(Blocks.SCULK_CATALYST);
      SCULK_SHRIEKER = registerBlock(Blocks.SCULK_SHRIEKER);
      ENCHANTING_TABLE = registerBlock(Blocks.ENCHANTING_TABLE);
      END_PORTAL_FRAME = registerBlock(Blocks.END_PORTAL_FRAME);
      END_STONE = registerBlock(Blocks.END_STONE);
      END_STONE_BRICKS = registerBlock(Blocks.END_STONE_BRICKS);
      DRAGON_EGG = registerBlock(Blocks.DRAGON_EGG, (new Item.Properties()).rarity(Rarity.EPIC));
      SANDSTONE_STAIRS = registerBlock(Blocks.SANDSTONE_STAIRS);
      ENDER_CHEST = registerBlock(Blocks.ENDER_CHEST);
      EMERALD_BLOCK = registerBlock(Blocks.EMERALD_BLOCK);
      OAK_STAIRS = registerBlock(Blocks.OAK_STAIRS);
      SPRUCE_STAIRS = registerBlock(Blocks.SPRUCE_STAIRS);
      BIRCH_STAIRS = registerBlock(Blocks.BIRCH_STAIRS);
      JUNGLE_STAIRS = registerBlock(Blocks.JUNGLE_STAIRS);
      ACACIA_STAIRS = registerBlock(Blocks.ACACIA_STAIRS);
      CHERRY_STAIRS = registerBlock(Blocks.CHERRY_STAIRS);
      DARK_OAK_STAIRS = registerBlock(Blocks.DARK_OAK_STAIRS);
      PALE_OAK_STAIRS = registerBlock(Blocks.PALE_OAK_STAIRS);
      MANGROVE_STAIRS = registerBlock(Blocks.MANGROVE_STAIRS);
      BAMBOO_STAIRS = registerBlock(Blocks.BAMBOO_STAIRS);
      BAMBOO_MOSAIC_STAIRS = registerBlock(Blocks.BAMBOO_MOSAIC_STAIRS);
      CRIMSON_STAIRS = registerBlock(Blocks.CRIMSON_STAIRS);
      WARPED_STAIRS = registerBlock(Blocks.WARPED_STAIRS);
      COMMAND_BLOCK = registerBlock(Blocks.COMMAND_BLOCK, GameMasterBlockItem::new, (new Item.Properties()).rarity(Rarity.EPIC));
      BEACON = registerBlock(Blocks.BEACON, (new Item.Properties()).rarity(Rarity.RARE));
      COBBLESTONE_WALL = registerBlock(Blocks.COBBLESTONE_WALL);
      MOSSY_COBBLESTONE_WALL = registerBlock(Blocks.MOSSY_COBBLESTONE_WALL);
      BRICK_WALL = registerBlock(Blocks.BRICK_WALL);
      PRISMARINE_WALL = registerBlock(Blocks.PRISMARINE_WALL);
      RED_SANDSTONE_WALL = registerBlock(Blocks.RED_SANDSTONE_WALL);
      MOSSY_STONE_BRICK_WALL = registerBlock(Blocks.MOSSY_STONE_BRICK_WALL);
      GRANITE_WALL = registerBlock(Blocks.GRANITE_WALL);
      STONE_BRICK_WALL = registerBlock(Blocks.STONE_BRICK_WALL);
      MUD_BRICK_WALL = registerBlock(Blocks.MUD_BRICK_WALL);
      NETHER_BRICK_WALL = registerBlock(Blocks.NETHER_BRICK_WALL);
      ANDESITE_WALL = registerBlock(Blocks.ANDESITE_WALL);
      RED_NETHER_BRICK_WALL = registerBlock(Blocks.RED_NETHER_BRICK_WALL);
      SANDSTONE_WALL = registerBlock(Blocks.SANDSTONE_WALL);
      END_STONE_BRICK_WALL = registerBlock(Blocks.END_STONE_BRICK_WALL);
      DIORITE_WALL = registerBlock(Blocks.DIORITE_WALL);
      BLACKSTONE_WALL = registerBlock(Blocks.BLACKSTONE_WALL);
      POLISHED_BLACKSTONE_WALL = registerBlock(Blocks.POLISHED_BLACKSTONE_WALL);
      POLISHED_BLACKSTONE_BRICK_WALL = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      COBBLED_DEEPSLATE_WALL = registerBlock(Blocks.COBBLED_DEEPSLATE_WALL);
      POLISHED_DEEPSLATE_WALL = registerBlock(Blocks.POLISHED_DEEPSLATE_WALL);
      DEEPSLATE_BRICK_WALL = registerBlock(Blocks.DEEPSLATE_BRICK_WALL);
      DEEPSLATE_TILE_WALL = registerBlock(Blocks.DEEPSLATE_TILE_WALL);
      ANVIL = registerBlock(Blocks.ANVIL);
      CHIPPED_ANVIL = registerBlock(Blocks.CHIPPED_ANVIL);
      DAMAGED_ANVIL = registerBlock(Blocks.DAMAGED_ANVIL);
      CHISELED_QUARTZ_BLOCK = registerBlock(Blocks.CHISELED_QUARTZ_BLOCK);
      QUARTZ_BLOCK = registerBlock(Blocks.QUARTZ_BLOCK);
      QUARTZ_BRICKS = registerBlock(Blocks.QUARTZ_BRICKS);
      QUARTZ_PILLAR = registerBlock(Blocks.QUARTZ_PILLAR);
      QUARTZ_STAIRS = registerBlock(Blocks.QUARTZ_STAIRS);
      WHITE_TERRACOTTA = registerBlock(Blocks.WHITE_TERRACOTTA);
      ORANGE_TERRACOTTA = registerBlock(Blocks.ORANGE_TERRACOTTA);
      MAGENTA_TERRACOTTA = registerBlock(Blocks.MAGENTA_TERRACOTTA);
      LIGHT_BLUE_TERRACOTTA = registerBlock(Blocks.LIGHT_BLUE_TERRACOTTA);
      YELLOW_TERRACOTTA = registerBlock(Blocks.YELLOW_TERRACOTTA);
      LIME_TERRACOTTA = registerBlock(Blocks.LIME_TERRACOTTA);
      PINK_TERRACOTTA = registerBlock(Blocks.PINK_TERRACOTTA);
      GRAY_TERRACOTTA = registerBlock(Blocks.GRAY_TERRACOTTA);
      LIGHT_GRAY_TERRACOTTA = registerBlock(Blocks.LIGHT_GRAY_TERRACOTTA);
      CYAN_TERRACOTTA = registerBlock(Blocks.CYAN_TERRACOTTA);
      PURPLE_TERRACOTTA = registerBlock(Blocks.PURPLE_TERRACOTTA);
      BLUE_TERRACOTTA = registerBlock(Blocks.BLUE_TERRACOTTA);
      BROWN_TERRACOTTA = registerBlock(Blocks.BROWN_TERRACOTTA);
      GREEN_TERRACOTTA = registerBlock(Blocks.GREEN_TERRACOTTA);
      RED_TERRACOTTA = registerBlock(Blocks.RED_TERRACOTTA);
      BLACK_TERRACOTTA = registerBlock(Blocks.BLACK_TERRACOTTA);
      BARRIER = registerBlock(Blocks.BARRIER, (new Item.Properties()).rarity(Rarity.EPIC));
      LIGHT = registerBlock(Blocks.LIGHT, (new Item.Properties()).rarity(Rarity.EPIC));
      HAY_BLOCK = registerBlock(Blocks.HAY_BLOCK);
      WHITE_CARPET = registerBlock(Blocks.WHITE_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.WHITE));
      });
      ORANGE_CARPET = registerBlock(Blocks.ORANGE_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.ORANGE));
      });
      MAGENTA_CARPET = registerBlock(Blocks.MAGENTA_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.MAGENTA));
      });
      LIGHT_BLUE_CARPET = registerBlock(Blocks.LIGHT_BLUE_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.LIGHT_BLUE));
      });
      YELLOW_CARPET = registerBlock(Blocks.YELLOW_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.YELLOW));
      });
      LIME_CARPET = registerBlock(Blocks.LIME_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.LIME));
      });
      PINK_CARPET = registerBlock(Blocks.PINK_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.PINK));
      });
      GRAY_CARPET = registerBlock(Blocks.GRAY_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.GRAY));
      });
      LIGHT_GRAY_CARPET = registerBlock(Blocks.LIGHT_GRAY_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.LIGHT_GRAY));
      });
      CYAN_CARPET = registerBlock(Blocks.CYAN_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.CYAN));
      });
      PURPLE_CARPET = registerBlock(Blocks.PURPLE_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.PURPLE));
      });
      BLUE_CARPET = registerBlock(Blocks.BLUE_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.BLUE));
      });
      BROWN_CARPET = registerBlock(Blocks.BROWN_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.BROWN));
      });
      GREEN_CARPET = registerBlock(Blocks.GREEN_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.GREEN));
      });
      RED_CARPET = registerBlock(Blocks.RED_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.RED));
      });
      BLACK_CARPET = registerBlock(Blocks.BLACK_CARPET, (var0) -> {
         return var0.component(DataComponents.EQUIPPABLE, Equippable.llamaSwag(DyeColor.BLACK));
      });
      TERRACOTTA = registerBlock(Blocks.TERRACOTTA);
      PACKED_ICE = registerBlock(Blocks.PACKED_ICE);
      DIRT_PATH = registerBlock(Blocks.DIRT_PATH);
      SUNFLOWER = registerBlock(Blocks.SUNFLOWER, DoubleHighBlockItem::new);
      LILAC = registerBlock(Blocks.LILAC, DoubleHighBlockItem::new);
      ROSE_BUSH = registerBlock(Blocks.ROSE_BUSH, DoubleHighBlockItem::new);
      PEONY = registerBlock(Blocks.PEONY, DoubleHighBlockItem::new);
      TALL_GRASS = registerBlock(Blocks.TALL_GRASS, DoubleHighBlockItem::new);
      LARGE_FERN = registerBlock(Blocks.LARGE_FERN, DoubleHighBlockItem::new);
      WHITE_STAINED_GLASS = registerBlock(Blocks.WHITE_STAINED_GLASS);
      ORANGE_STAINED_GLASS = registerBlock(Blocks.ORANGE_STAINED_GLASS);
      MAGENTA_STAINED_GLASS = registerBlock(Blocks.MAGENTA_STAINED_GLASS);
      LIGHT_BLUE_STAINED_GLASS = registerBlock(Blocks.LIGHT_BLUE_STAINED_GLASS);
      YELLOW_STAINED_GLASS = registerBlock(Blocks.YELLOW_STAINED_GLASS);
      LIME_STAINED_GLASS = registerBlock(Blocks.LIME_STAINED_GLASS);
      PINK_STAINED_GLASS = registerBlock(Blocks.PINK_STAINED_GLASS);
      GRAY_STAINED_GLASS = registerBlock(Blocks.GRAY_STAINED_GLASS);
      LIGHT_GRAY_STAINED_GLASS = registerBlock(Blocks.LIGHT_GRAY_STAINED_GLASS);
      CYAN_STAINED_GLASS = registerBlock(Blocks.CYAN_STAINED_GLASS);
      PURPLE_STAINED_GLASS = registerBlock(Blocks.PURPLE_STAINED_GLASS);
      BLUE_STAINED_GLASS = registerBlock(Blocks.BLUE_STAINED_GLASS);
      BROWN_STAINED_GLASS = registerBlock(Blocks.BROWN_STAINED_GLASS);
      GREEN_STAINED_GLASS = registerBlock(Blocks.GREEN_STAINED_GLASS);
      RED_STAINED_GLASS = registerBlock(Blocks.RED_STAINED_GLASS);
      BLACK_STAINED_GLASS = registerBlock(Blocks.BLACK_STAINED_GLASS);
      WHITE_STAINED_GLASS_PANE = registerBlock(Blocks.WHITE_STAINED_GLASS_PANE);
      ORANGE_STAINED_GLASS_PANE = registerBlock(Blocks.ORANGE_STAINED_GLASS_PANE);
      MAGENTA_STAINED_GLASS_PANE = registerBlock(Blocks.MAGENTA_STAINED_GLASS_PANE);
      LIGHT_BLUE_STAINED_GLASS_PANE = registerBlock(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      YELLOW_STAINED_GLASS_PANE = registerBlock(Blocks.YELLOW_STAINED_GLASS_PANE);
      LIME_STAINED_GLASS_PANE = registerBlock(Blocks.LIME_STAINED_GLASS_PANE);
      PINK_STAINED_GLASS_PANE = registerBlock(Blocks.PINK_STAINED_GLASS_PANE);
      GRAY_STAINED_GLASS_PANE = registerBlock(Blocks.GRAY_STAINED_GLASS_PANE);
      LIGHT_GRAY_STAINED_GLASS_PANE = registerBlock(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      CYAN_STAINED_GLASS_PANE = registerBlock(Blocks.CYAN_STAINED_GLASS_PANE);
      PURPLE_STAINED_GLASS_PANE = registerBlock(Blocks.PURPLE_STAINED_GLASS_PANE);
      BLUE_STAINED_GLASS_PANE = registerBlock(Blocks.BLUE_STAINED_GLASS_PANE);
      BROWN_STAINED_GLASS_PANE = registerBlock(Blocks.BROWN_STAINED_GLASS_PANE);
      GREEN_STAINED_GLASS_PANE = registerBlock(Blocks.GREEN_STAINED_GLASS_PANE);
      RED_STAINED_GLASS_PANE = registerBlock(Blocks.RED_STAINED_GLASS_PANE);
      BLACK_STAINED_GLASS_PANE = registerBlock(Blocks.BLACK_STAINED_GLASS_PANE);
      PRISMARINE = registerBlock(Blocks.PRISMARINE);
      PRISMARINE_BRICKS = registerBlock(Blocks.PRISMARINE_BRICKS);
      DARK_PRISMARINE = registerBlock(Blocks.DARK_PRISMARINE);
      PRISMARINE_STAIRS = registerBlock(Blocks.PRISMARINE_STAIRS);
      PRISMARINE_BRICK_STAIRS = registerBlock(Blocks.PRISMARINE_BRICK_STAIRS);
      DARK_PRISMARINE_STAIRS = registerBlock(Blocks.DARK_PRISMARINE_STAIRS);
      SEA_LANTERN = registerBlock(Blocks.SEA_LANTERN);
      RED_SANDSTONE = registerBlock(Blocks.RED_SANDSTONE);
      CHISELED_RED_SANDSTONE = registerBlock(Blocks.CHISELED_RED_SANDSTONE);
      CUT_RED_SANDSTONE = registerBlock(Blocks.CUT_RED_SANDSTONE);
      RED_SANDSTONE_STAIRS = registerBlock(Blocks.RED_SANDSTONE_STAIRS);
      REPEATING_COMMAND_BLOCK = registerBlock(Blocks.REPEATING_COMMAND_BLOCK, GameMasterBlockItem::new, (new Item.Properties()).rarity(Rarity.EPIC));
      CHAIN_COMMAND_BLOCK = registerBlock(Blocks.CHAIN_COMMAND_BLOCK, GameMasterBlockItem::new, (new Item.Properties()).rarity(Rarity.EPIC));
      MAGMA_BLOCK = registerBlock(Blocks.MAGMA_BLOCK);
      NETHER_WART_BLOCK = registerBlock(Blocks.NETHER_WART_BLOCK);
      WARPED_WART_BLOCK = registerBlock(Blocks.WARPED_WART_BLOCK);
      RED_NETHER_BRICKS = registerBlock(Blocks.RED_NETHER_BRICKS);
      BONE_BLOCK = registerBlock(Blocks.BONE_BLOCK);
      STRUCTURE_VOID = registerBlock(Blocks.STRUCTURE_VOID, (new Item.Properties()).rarity(Rarity.EPIC));
      SHULKER_BOX = registerBlock(Blocks.SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      WHITE_SHULKER_BOX = registerBlock(Blocks.WHITE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      ORANGE_SHULKER_BOX = registerBlock(Blocks.ORANGE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      MAGENTA_SHULKER_BOX = registerBlock(Blocks.MAGENTA_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      LIGHT_BLUE_SHULKER_BOX = registerBlock(Blocks.LIGHT_BLUE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      YELLOW_SHULKER_BOX = registerBlock(Blocks.YELLOW_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      LIME_SHULKER_BOX = registerBlock(Blocks.LIME_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      PINK_SHULKER_BOX = registerBlock(Blocks.PINK_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      GRAY_SHULKER_BOX = registerBlock(Blocks.GRAY_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      LIGHT_GRAY_SHULKER_BOX = registerBlock(Blocks.LIGHT_GRAY_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      CYAN_SHULKER_BOX = registerBlock(Blocks.CYAN_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      PURPLE_SHULKER_BOX = registerBlock(Blocks.PURPLE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      BLUE_SHULKER_BOX = registerBlock(Blocks.BLUE_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      BROWN_SHULKER_BOX = registerBlock(Blocks.BROWN_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      GREEN_SHULKER_BOX = registerBlock(Blocks.GREEN_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      RED_SHULKER_BOX = registerBlock(Blocks.RED_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      BLACK_SHULKER_BOX = registerBlock(Blocks.BLACK_SHULKER_BOX, (new Item.Properties()).stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
      WHITE_GLAZED_TERRACOTTA = registerBlock(Blocks.WHITE_GLAZED_TERRACOTTA);
      ORANGE_GLAZED_TERRACOTTA = registerBlock(Blocks.ORANGE_GLAZED_TERRACOTTA);
      MAGENTA_GLAZED_TERRACOTTA = registerBlock(Blocks.MAGENTA_GLAZED_TERRACOTTA);
      LIGHT_BLUE_GLAZED_TERRACOTTA = registerBlock(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
      YELLOW_GLAZED_TERRACOTTA = registerBlock(Blocks.YELLOW_GLAZED_TERRACOTTA);
      LIME_GLAZED_TERRACOTTA = registerBlock(Blocks.LIME_GLAZED_TERRACOTTA);
      PINK_GLAZED_TERRACOTTA = registerBlock(Blocks.PINK_GLAZED_TERRACOTTA);
      GRAY_GLAZED_TERRACOTTA = registerBlock(Blocks.GRAY_GLAZED_TERRACOTTA);
      LIGHT_GRAY_GLAZED_TERRACOTTA = registerBlock(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
      CYAN_GLAZED_TERRACOTTA = registerBlock(Blocks.CYAN_GLAZED_TERRACOTTA);
      PURPLE_GLAZED_TERRACOTTA = registerBlock(Blocks.PURPLE_GLAZED_TERRACOTTA);
      BLUE_GLAZED_TERRACOTTA = registerBlock(Blocks.BLUE_GLAZED_TERRACOTTA);
      BROWN_GLAZED_TERRACOTTA = registerBlock(Blocks.BROWN_GLAZED_TERRACOTTA);
      GREEN_GLAZED_TERRACOTTA = registerBlock(Blocks.GREEN_GLAZED_TERRACOTTA);
      RED_GLAZED_TERRACOTTA = registerBlock(Blocks.RED_GLAZED_TERRACOTTA);
      BLACK_GLAZED_TERRACOTTA = registerBlock(Blocks.BLACK_GLAZED_TERRACOTTA);
      WHITE_CONCRETE = registerBlock(Blocks.WHITE_CONCRETE);
      ORANGE_CONCRETE = registerBlock(Blocks.ORANGE_CONCRETE);
      MAGENTA_CONCRETE = registerBlock(Blocks.MAGENTA_CONCRETE);
      LIGHT_BLUE_CONCRETE = registerBlock(Blocks.LIGHT_BLUE_CONCRETE);
      YELLOW_CONCRETE = registerBlock(Blocks.YELLOW_CONCRETE);
      LIME_CONCRETE = registerBlock(Blocks.LIME_CONCRETE);
      PINK_CONCRETE = registerBlock(Blocks.PINK_CONCRETE);
      GRAY_CONCRETE = registerBlock(Blocks.GRAY_CONCRETE);
      LIGHT_GRAY_CONCRETE = registerBlock(Blocks.LIGHT_GRAY_CONCRETE);
      CYAN_CONCRETE = registerBlock(Blocks.CYAN_CONCRETE);
      PURPLE_CONCRETE = registerBlock(Blocks.PURPLE_CONCRETE);
      BLUE_CONCRETE = registerBlock(Blocks.BLUE_CONCRETE);
      BROWN_CONCRETE = registerBlock(Blocks.BROWN_CONCRETE);
      GREEN_CONCRETE = registerBlock(Blocks.GREEN_CONCRETE);
      RED_CONCRETE = registerBlock(Blocks.RED_CONCRETE);
      BLACK_CONCRETE = registerBlock(Blocks.BLACK_CONCRETE);
      WHITE_CONCRETE_POWDER = registerBlock(Blocks.WHITE_CONCRETE_POWDER);
      ORANGE_CONCRETE_POWDER = registerBlock(Blocks.ORANGE_CONCRETE_POWDER);
      MAGENTA_CONCRETE_POWDER = registerBlock(Blocks.MAGENTA_CONCRETE_POWDER);
      LIGHT_BLUE_CONCRETE_POWDER = registerBlock(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
      YELLOW_CONCRETE_POWDER = registerBlock(Blocks.YELLOW_CONCRETE_POWDER);
      LIME_CONCRETE_POWDER = registerBlock(Blocks.LIME_CONCRETE_POWDER);
      PINK_CONCRETE_POWDER = registerBlock(Blocks.PINK_CONCRETE_POWDER);
      GRAY_CONCRETE_POWDER = registerBlock(Blocks.GRAY_CONCRETE_POWDER);
      LIGHT_GRAY_CONCRETE_POWDER = registerBlock(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
      CYAN_CONCRETE_POWDER = registerBlock(Blocks.CYAN_CONCRETE_POWDER);
      PURPLE_CONCRETE_POWDER = registerBlock(Blocks.PURPLE_CONCRETE_POWDER);
      BLUE_CONCRETE_POWDER = registerBlock(Blocks.BLUE_CONCRETE_POWDER);
      BROWN_CONCRETE_POWDER = registerBlock(Blocks.BROWN_CONCRETE_POWDER);
      GREEN_CONCRETE_POWDER = registerBlock(Blocks.GREEN_CONCRETE_POWDER);
      RED_CONCRETE_POWDER = registerBlock(Blocks.RED_CONCRETE_POWDER);
      BLACK_CONCRETE_POWDER = registerBlock(Blocks.BLACK_CONCRETE_POWDER);
      TURTLE_EGG = registerBlock(Blocks.TURTLE_EGG);
      SNIFFER_EGG = registerBlock(Blocks.SNIFFER_EGG, (var0) -> {
         return var0.rarity(Rarity.UNCOMMON);
      });
      DEAD_TUBE_CORAL_BLOCK = registerBlock(Blocks.DEAD_TUBE_CORAL_BLOCK);
      DEAD_BRAIN_CORAL_BLOCK = registerBlock(Blocks.DEAD_BRAIN_CORAL_BLOCK);
      DEAD_BUBBLE_CORAL_BLOCK = registerBlock(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      DEAD_FIRE_CORAL_BLOCK = registerBlock(Blocks.DEAD_FIRE_CORAL_BLOCK);
      DEAD_HORN_CORAL_BLOCK = registerBlock(Blocks.DEAD_HORN_CORAL_BLOCK);
      TUBE_CORAL_BLOCK = registerBlock(Blocks.TUBE_CORAL_BLOCK);
      BRAIN_CORAL_BLOCK = registerBlock(Blocks.BRAIN_CORAL_BLOCK);
      BUBBLE_CORAL_BLOCK = registerBlock(Blocks.BUBBLE_CORAL_BLOCK);
      FIRE_CORAL_BLOCK = registerBlock(Blocks.FIRE_CORAL_BLOCK);
      HORN_CORAL_BLOCK = registerBlock(Blocks.HORN_CORAL_BLOCK);
      TUBE_CORAL = registerBlock(Blocks.TUBE_CORAL);
      BRAIN_CORAL = registerBlock(Blocks.BRAIN_CORAL);
      BUBBLE_CORAL = registerBlock(Blocks.BUBBLE_CORAL);
      FIRE_CORAL = registerBlock(Blocks.FIRE_CORAL);
      HORN_CORAL = registerBlock(Blocks.HORN_CORAL);
      DEAD_BRAIN_CORAL = registerBlock(Blocks.DEAD_BRAIN_CORAL);
      DEAD_BUBBLE_CORAL = registerBlock(Blocks.DEAD_BUBBLE_CORAL);
      DEAD_FIRE_CORAL = registerBlock(Blocks.DEAD_FIRE_CORAL);
      DEAD_HORN_CORAL = registerBlock(Blocks.DEAD_HORN_CORAL);
      DEAD_TUBE_CORAL = registerBlock(Blocks.DEAD_TUBE_CORAL);
      TUBE_CORAL_FAN = registerBlock(Blocks.TUBE_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.TUBE_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      BRAIN_CORAL_FAN = registerBlock(Blocks.BRAIN_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.BRAIN_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      BUBBLE_CORAL_FAN = registerBlock(Blocks.BUBBLE_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.BUBBLE_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      FIRE_CORAL_FAN = registerBlock(Blocks.FIRE_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.FIRE_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      HORN_CORAL_FAN = registerBlock(Blocks.HORN_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.HORN_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      DEAD_TUBE_CORAL_FAN = registerBlock(Blocks.DEAD_TUBE_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.DEAD_TUBE_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      DEAD_BRAIN_CORAL_FAN = registerBlock(Blocks.DEAD_BRAIN_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.DEAD_BRAIN_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      DEAD_BUBBLE_CORAL_FAN = registerBlock(Blocks.DEAD_BUBBLE_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      DEAD_FIRE_CORAL_FAN = registerBlock(Blocks.DEAD_FIRE_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.DEAD_FIRE_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      DEAD_HORN_CORAL_FAN = registerBlock(Blocks.DEAD_HORN_CORAL_FAN, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.DEAD_HORN_CORAL_WALL_FAN, Direction.DOWN, var1);
      });
      BLUE_ICE = registerBlock(Blocks.BLUE_ICE);
      CONDUIT = registerBlock(Blocks.CONDUIT, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      POLISHED_GRANITE_STAIRS = registerBlock(Blocks.POLISHED_GRANITE_STAIRS);
      SMOOTH_RED_SANDSTONE_STAIRS = registerBlock(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
      MOSSY_STONE_BRICK_STAIRS = registerBlock(Blocks.MOSSY_STONE_BRICK_STAIRS);
      POLISHED_DIORITE_STAIRS = registerBlock(Blocks.POLISHED_DIORITE_STAIRS);
      MOSSY_COBBLESTONE_STAIRS = registerBlock(Blocks.MOSSY_COBBLESTONE_STAIRS);
      END_STONE_BRICK_STAIRS = registerBlock(Blocks.END_STONE_BRICK_STAIRS);
      STONE_STAIRS = registerBlock(Blocks.STONE_STAIRS);
      SMOOTH_SANDSTONE_STAIRS = registerBlock(Blocks.SMOOTH_SANDSTONE_STAIRS);
      SMOOTH_QUARTZ_STAIRS = registerBlock(Blocks.SMOOTH_QUARTZ_STAIRS);
      GRANITE_STAIRS = registerBlock(Blocks.GRANITE_STAIRS);
      ANDESITE_STAIRS = registerBlock(Blocks.ANDESITE_STAIRS);
      RED_NETHER_BRICK_STAIRS = registerBlock(Blocks.RED_NETHER_BRICK_STAIRS);
      POLISHED_ANDESITE_STAIRS = registerBlock(Blocks.POLISHED_ANDESITE_STAIRS);
      DIORITE_STAIRS = registerBlock(Blocks.DIORITE_STAIRS);
      COBBLED_DEEPSLATE_STAIRS = registerBlock(Blocks.COBBLED_DEEPSLATE_STAIRS);
      POLISHED_DEEPSLATE_STAIRS = registerBlock(Blocks.POLISHED_DEEPSLATE_STAIRS);
      DEEPSLATE_BRICK_STAIRS = registerBlock(Blocks.DEEPSLATE_BRICK_STAIRS);
      DEEPSLATE_TILE_STAIRS = registerBlock(Blocks.DEEPSLATE_TILE_STAIRS);
      POLISHED_GRANITE_SLAB = registerBlock(Blocks.POLISHED_GRANITE_SLAB);
      SMOOTH_RED_SANDSTONE_SLAB = registerBlock(Blocks.SMOOTH_RED_SANDSTONE_SLAB);
      MOSSY_STONE_BRICK_SLAB = registerBlock(Blocks.MOSSY_STONE_BRICK_SLAB);
      POLISHED_DIORITE_SLAB = registerBlock(Blocks.POLISHED_DIORITE_SLAB);
      MOSSY_COBBLESTONE_SLAB = registerBlock(Blocks.MOSSY_COBBLESTONE_SLAB);
      END_STONE_BRICK_SLAB = registerBlock(Blocks.END_STONE_BRICK_SLAB);
      SMOOTH_SANDSTONE_SLAB = registerBlock(Blocks.SMOOTH_SANDSTONE_SLAB);
      SMOOTH_QUARTZ_SLAB = registerBlock(Blocks.SMOOTH_QUARTZ_SLAB);
      GRANITE_SLAB = registerBlock(Blocks.GRANITE_SLAB);
      ANDESITE_SLAB = registerBlock(Blocks.ANDESITE_SLAB);
      RED_NETHER_BRICK_SLAB = registerBlock(Blocks.RED_NETHER_BRICK_SLAB);
      POLISHED_ANDESITE_SLAB = registerBlock(Blocks.POLISHED_ANDESITE_SLAB);
      DIORITE_SLAB = registerBlock(Blocks.DIORITE_SLAB);
      COBBLED_DEEPSLATE_SLAB = registerBlock(Blocks.COBBLED_DEEPSLATE_SLAB);
      POLISHED_DEEPSLATE_SLAB = registerBlock(Blocks.POLISHED_DEEPSLATE_SLAB);
      DEEPSLATE_BRICK_SLAB = registerBlock(Blocks.DEEPSLATE_BRICK_SLAB);
      DEEPSLATE_TILE_SLAB = registerBlock(Blocks.DEEPSLATE_TILE_SLAB);
      SCAFFOLDING = registerBlock(Blocks.SCAFFOLDING, ScaffoldingBlockItem::new);
      REDSTONE = registerItem("redstone", createBlockItemWithCustomItemName(Blocks.REDSTONE_WIRE));
      REDSTONE_TORCH = registerBlock(Blocks.REDSTONE_TORCH, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.REDSTONE_WALL_TORCH, Direction.DOWN, var1);
      });
      REDSTONE_BLOCK = registerBlock(Blocks.REDSTONE_BLOCK);
      REPEATER = registerBlock(Blocks.REPEATER);
      COMPARATOR = registerBlock(Blocks.COMPARATOR);
      PISTON = registerBlock(Blocks.PISTON);
      STICKY_PISTON = registerBlock(Blocks.STICKY_PISTON);
      SLIME_BLOCK = registerBlock(Blocks.SLIME_BLOCK);
      HONEY_BLOCK = registerBlock(Blocks.HONEY_BLOCK);
      OBSERVER = registerBlock(Blocks.OBSERVER);
      HOPPER = registerBlock(Blocks.HOPPER, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      DISPENSER = registerBlock(Blocks.DISPENSER, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      DROPPER = registerBlock(Blocks.DROPPER, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      LECTERN = registerBlock(Blocks.LECTERN);
      TARGET = registerBlock(Blocks.TARGET);
      LEVER = registerBlock(Blocks.LEVER);
      LIGHTNING_ROD = registerBlock(Blocks.LIGHTNING_ROD);
      DAYLIGHT_DETECTOR = registerBlock(Blocks.DAYLIGHT_DETECTOR);
      SCULK_SENSOR = registerBlock(Blocks.SCULK_SENSOR);
      CALIBRATED_SCULK_SENSOR = registerBlock(Blocks.CALIBRATED_SCULK_SENSOR);
      TRIPWIRE_HOOK = registerBlock(Blocks.TRIPWIRE_HOOK);
      TRAPPED_CHEST = registerBlock(Blocks.TRAPPED_CHEST, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      TNT = registerBlock(Blocks.TNT);
      REDSTONE_LAMP = registerBlock(Blocks.REDSTONE_LAMP);
      NOTE_BLOCK = registerBlock(Blocks.NOTE_BLOCK);
      STONE_BUTTON = registerBlock(Blocks.STONE_BUTTON);
      POLISHED_BLACKSTONE_BUTTON = registerBlock(Blocks.POLISHED_BLACKSTONE_BUTTON);
      OAK_BUTTON = registerBlock(Blocks.OAK_BUTTON);
      SPRUCE_BUTTON = registerBlock(Blocks.SPRUCE_BUTTON);
      BIRCH_BUTTON = registerBlock(Blocks.BIRCH_BUTTON);
      JUNGLE_BUTTON = registerBlock(Blocks.JUNGLE_BUTTON);
      ACACIA_BUTTON = registerBlock(Blocks.ACACIA_BUTTON);
      CHERRY_BUTTON = registerBlock(Blocks.CHERRY_BUTTON);
      DARK_OAK_BUTTON = registerBlock(Blocks.DARK_OAK_BUTTON);
      PALE_OAK_BUTTON = registerBlock(Blocks.PALE_OAK_BUTTON);
      MANGROVE_BUTTON = registerBlock(Blocks.MANGROVE_BUTTON);
      BAMBOO_BUTTON = registerBlock(Blocks.BAMBOO_BUTTON);
      CRIMSON_BUTTON = registerBlock(Blocks.CRIMSON_BUTTON);
      WARPED_BUTTON = registerBlock(Blocks.WARPED_BUTTON);
      STONE_PRESSURE_PLATE = registerBlock(Blocks.STONE_PRESSURE_PLATE);
      POLISHED_BLACKSTONE_PRESSURE_PLATE = registerBlock(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
      LIGHT_WEIGHTED_PRESSURE_PLATE = registerBlock(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
      HEAVY_WEIGHTED_PRESSURE_PLATE = registerBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
      OAK_PRESSURE_PLATE = registerBlock(Blocks.OAK_PRESSURE_PLATE);
      SPRUCE_PRESSURE_PLATE = registerBlock(Blocks.SPRUCE_PRESSURE_PLATE);
      BIRCH_PRESSURE_PLATE = registerBlock(Blocks.BIRCH_PRESSURE_PLATE);
      JUNGLE_PRESSURE_PLATE = registerBlock(Blocks.JUNGLE_PRESSURE_PLATE);
      ACACIA_PRESSURE_PLATE = registerBlock(Blocks.ACACIA_PRESSURE_PLATE);
      CHERRY_PRESSURE_PLATE = registerBlock(Blocks.CHERRY_PRESSURE_PLATE);
      DARK_OAK_PRESSURE_PLATE = registerBlock(Blocks.DARK_OAK_PRESSURE_PLATE);
      PALE_OAK_PRESSURE_PLATE = registerBlock(Blocks.PALE_OAK_PRESSURE_PLATE);
      MANGROVE_PRESSURE_PLATE = registerBlock(Blocks.MANGROVE_PRESSURE_PLATE);
      BAMBOO_PRESSURE_PLATE = registerBlock(Blocks.BAMBOO_PRESSURE_PLATE);
      CRIMSON_PRESSURE_PLATE = registerBlock(Blocks.CRIMSON_PRESSURE_PLATE);
      WARPED_PRESSURE_PLATE = registerBlock(Blocks.WARPED_PRESSURE_PLATE);
      IRON_DOOR = registerBlock(Blocks.IRON_DOOR, DoubleHighBlockItem::new);
      OAK_DOOR = registerBlock(Blocks.OAK_DOOR, DoubleHighBlockItem::new);
      SPRUCE_DOOR = registerBlock(Blocks.SPRUCE_DOOR, DoubleHighBlockItem::new);
      BIRCH_DOOR = registerBlock(Blocks.BIRCH_DOOR, DoubleHighBlockItem::new);
      JUNGLE_DOOR = registerBlock(Blocks.JUNGLE_DOOR, DoubleHighBlockItem::new);
      ACACIA_DOOR = registerBlock(Blocks.ACACIA_DOOR, DoubleHighBlockItem::new);
      CHERRY_DOOR = registerBlock(Blocks.CHERRY_DOOR, DoubleHighBlockItem::new);
      DARK_OAK_DOOR = registerBlock(Blocks.DARK_OAK_DOOR, DoubleHighBlockItem::new);
      PALE_OAK_DOOR = registerBlock(Blocks.PALE_OAK_DOOR, DoubleHighBlockItem::new);
      MANGROVE_DOOR = registerBlock(Blocks.MANGROVE_DOOR, DoubleHighBlockItem::new);
      BAMBOO_DOOR = registerBlock(Blocks.BAMBOO_DOOR, DoubleHighBlockItem::new);
      CRIMSON_DOOR = registerBlock(Blocks.CRIMSON_DOOR, DoubleHighBlockItem::new);
      WARPED_DOOR = registerBlock(Blocks.WARPED_DOOR, DoubleHighBlockItem::new);
      COPPER_DOOR = registerBlock(Blocks.COPPER_DOOR, DoubleHighBlockItem::new);
      EXPOSED_COPPER_DOOR = registerBlock(Blocks.EXPOSED_COPPER_DOOR, DoubleHighBlockItem::new);
      WEATHERED_COPPER_DOOR = registerBlock(Blocks.WEATHERED_COPPER_DOOR, DoubleHighBlockItem::new);
      OXIDIZED_COPPER_DOOR = registerBlock(Blocks.OXIDIZED_COPPER_DOOR, DoubleHighBlockItem::new);
      WAXED_COPPER_DOOR = registerBlock(Blocks.WAXED_COPPER_DOOR, DoubleHighBlockItem::new);
      WAXED_EXPOSED_COPPER_DOOR = registerBlock(Blocks.WAXED_EXPOSED_COPPER_DOOR, DoubleHighBlockItem::new);
      WAXED_WEATHERED_COPPER_DOOR = registerBlock(Blocks.WAXED_WEATHERED_COPPER_DOOR, DoubleHighBlockItem::new);
      WAXED_OXIDIZED_COPPER_DOOR = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_DOOR, DoubleHighBlockItem::new);
      IRON_TRAPDOOR = registerBlock(Blocks.IRON_TRAPDOOR);
      OAK_TRAPDOOR = registerBlock(Blocks.OAK_TRAPDOOR);
      SPRUCE_TRAPDOOR = registerBlock(Blocks.SPRUCE_TRAPDOOR);
      BIRCH_TRAPDOOR = registerBlock(Blocks.BIRCH_TRAPDOOR);
      JUNGLE_TRAPDOOR = registerBlock(Blocks.JUNGLE_TRAPDOOR);
      ACACIA_TRAPDOOR = registerBlock(Blocks.ACACIA_TRAPDOOR);
      CHERRY_TRAPDOOR = registerBlock(Blocks.CHERRY_TRAPDOOR);
      DARK_OAK_TRAPDOOR = registerBlock(Blocks.DARK_OAK_TRAPDOOR);
      PALE_OAK_TRAPDOOR = registerBlock(Blocks.PALE_OAK_TRAPDOOR);
      MANGROVE_TRAPDOOR = registerBlock(Blocks.MANGROVE_TRAPDOOR);
      BAMBOO_TRAPDOOR = registerBlock(Blocks.BAMBOO_TRAPDOOR);
      CRIMSON_TRAPDOOR = registerBlock(Blocks.CRIMSON_TRAPDOOR);
      WARPED_TRAPDOOR = registerBlock(Blocks.WARPED_TRAPDOOR);
      COPPER_TRAPDOOR = registerBlock(Blocks.COPPER_TRAPDOOR);
      EXPOSED_COPPER_TRAPDOOR = registerBlock(Blocks.EXPOSED_COPPER_TRAPDOOR);
      WEATHERED_COPPER_TRAPDOOR = registerBlock(Blocks.WEATHERED_COPPER_TRAPDOOR);
      OXIDIZED_COPPER_TRAPDOOR = registerBlock(Blocks.OXIDIZED_COPPER_TRAPDOOR);
      WAXED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_COPPER_TRAPDOOR);
      WAXED_EXPOSED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
      WAXED_WEATHERED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
      WAXED_OXIDIZED_COPPER_TRAPDOOR = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
      OAK_FENCE_GATE = registerBlock(Blocks.OAK_FENCE_GATE);
      SPRUCE_FENCE_GATE = registerBlock(Blocks.SPRUCE_FENCE_GATE);
      BIRCH_FENCE_GATE = registerBlock(Blocks.BIRCH_FENCE_GATE);
      JUNGLE_FENCE_GATE = registerBlock(Blocks.JUNGLE_FENCE_GATE);
      ACACIA_FENCE_GATE = registerBlock(Blocks.ACACIA_FENCE_GATE);
      CHERRY_FENCE_GATE = registerBlock(Blocks.CHERRY_FENCE_GATE);
      DARK_OAK_FENCE_GATE = registerBlock(Blocks.DARK_OAK_FENCE_GATE);
      PALE_OAK_FENCE_GATE = registerBlock(Blocks.PALE_OAK_FENCE_GATE);
      MANGROVE_FENCE_GATE = registerBlock(Blocks.MANGROVE_FENCE_GATE);
      BAMBOO_FENCE_GATE = registerBlock(Blocks.BAMBOO_FENCE_GATE);
      CRIMSON_FENCE_GATE = registerBlock(Blocks.CRIMSON_FENCE_GATE);
      WARPED_FENCE_GATE = registerBlock(Blocks.WARPED_FENCE_GATE);
      POWERED_RAIL = registerBlock(Blocks.POWERED_RAIL);
      DETECTOR_RAIL = registerBlock(Blocks.DETECTOR_RAIL);
      RAIL = registerBlock(Blocks.RAIL);
      ACTIVATOR_RAIL = registerBlock(Blocks.ACTIVATOR_RAIL);
      SADDLE = registerItem("saddle", SaddleItem::new, (new Item.Properties()).stacksTo(1));
      MINECART = registerItem("minecart", (var0) -> {
         return new MinecartItem(EntityType.MINECART, var0);
      }, (new Item.Properties()).stacksTo(1));
      CHEST_MINECART = registerItem("chest_minecart", (var0) -> {
         return new MinecartItem(EntityType.CHEST_MINECART, var0);
      }, (new Item.Properties()).stacksTo(1));
      FURNACE_MINECART = registerItem("furnace_minecart", (var0) -> {
         return new MinecartItem(EntityType.FURNACE_MINECART, var0);
      }, (new Item.Properties()).stacksTo(1));
      TNT_MINECART = registerItem("tnt_minecart", (var0) -> {
         return new MinecartItem(EntityType.TNT_MINECART, var0);
      }, (new Item.Properties()).stacksTo(1));
      HOPPER_MINECART = registerItem("hopper_minecart", (var0) -> {
         return new MinecartItem(EntityType.HOPPER_MINECART, var0);
      }, (new Item.Properties()).stacksTo(1));
      CARROT_ON_A_STICK = registerItem("carrot_on_a_stick", (var0) -> {
         return new FoodOnAStickItem(EntityType.PIG, 7, var0);
      }, (new Item.Properties()).durability(25));
      WARPED_FUNGUS_ON_A_STICK = registerItem("warped_fungus_on_a_stick", (var0) -> {
         return new FoodOnAStickItem(EntityType.STRIDER, 1, var0);
      }, (new Item.Properties()).durability(100));
      PHANTOM_MEMBRANE = registerItem("phantom_membrane");
      ELYTRA = registerItem("elytra", (new Item.Properties()).durability(432).rarity(Rarity.EPIC).component(DataComponents.GLIDER, Unit.INSTANCE).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA).setModel(EquipmentModels.ELYTRA).setDamageOnHurt(false).build()).repairable(PHANTOM_MEMBRANE));
      OAK_BOAT = registerItem("oak_boat", (var0) -> {
         return new BoatItem(EntityType.OAK_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      OAK_CHEST_BOAT = registerItem("oak_chest_boat", (var0) -> {
         return new BoatItem(EntityType.OAK_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      SPRUCE_BOAT = registerItem("spruce_boat", (var0) -> {
         return new BoatItem(EntityType.SPRUCE_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      SPRUCE_CHEST_BOAT = registerItem("spruce_chest_boat", (var0) -> {
         return new BoatItem(EntityType.SPRUCE_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      BIRCH_BOAT = registerItem("birch_boat", (var0) -> {
         return new BoatItem(EntityType.BIRCH_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      BIRCH_CHEST_BOAT = registerItem("birch_chest_boat", (var0) -> {
         return new BoatItem(EntityType.BIRCH_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      JUNGLE_BOAT = registerItem("jungle_boat", (var0) -> {
         return new BoatItem(EntityType.JUNGLE_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      JUNGLE_CHEST_BOAT = registerItem("jungle_chest_boat", (var0) -> {
         return new BoatItem(EntityType.JUNGLE_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      ACACIA_BOAT = registerItem("acacia_boat", (var0) -> {
         return new BoatItem(EntityType.ACACIA_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      ACACIA_CHEST_BOAT = registerItem("acacia_chest_boat", (var0) -> {
         return new BoatItem(EntityType.ACACIA_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      CHERRY_BOAT = registerItem("cherry_boat", (var0) -> {
         return new BoatItem(EntityType.CHERRY_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      CHERRY_CHEST_BOAT = registerItem("cherry_chest_boat", (var0) -> {
         return new BoatItem(EntityType.CHERRY_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      DARK_OAK_BOAT = registerItem("dark_oak_boat", (var0) -> {
         return new BoatItem(EntityType.DARK_OAK_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      DARK_OAK_CHEST_BOAT = registerItem("dark_oak_chest_boat", (var0) -> {
         return new BoatItem(EntityType.DARK_OAK_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      PALE_OAK_BOAT = registerItem("pale_oak_boat", (var0) -> {
         return new BoatItem(EntityType.PALE_OAK_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      PALE_OAK_CHEST_BOAT = registerItem("pale_oak_chest_boat", (var0) -> {
         return new BoatItem(EntityType.PALE_OAK_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      MANGROVE_BOAT = registerItem("mangrove_boat", (var0) -> {
         return new BoatItem(EntityType.MANGROVE_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      MANGROVE_CHEST_BOAT = registerItem("mangrove_chest_boat", (var0) -> {
         return new BoatItem(EntityType.MANGROVE_CHEST_BOAT, var0);
      }, (new Item.Properties()).stacksTo(1));
      BAMBOO_RAFT = registerItem("bamboo_raft", (var0) -> {
         return new BoatItem(EntityType.BAMBOO_RAFT, var0);
      }, (new Item.Properties()).stacksTo(1));
      BAMBOO_CHEST_RAFT = registerItem("bamboo_chest_raft", (var0) -> {
         return new BoatItem(EntityType.BAMBOO_CHEST_RAFT, var0);
      }, (new Item.Properties()).stacksTo(1));
      STRUCTURE_BLOCK = registerBlock(Blocks.STRUCTURE_BLOCK, GameMasterBlockItem::new, (new Item.Properties()).rarity(Rarity.EPIC));
      JIGSAW = registerBlock(Blocks.JIGSAW, GameMasterBlockItem::new, (new Item.Properties()).rarity(Rarity.EPIC));
      TURTLE_HELMET = registerItem("turtle_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.TURTLE_SCUTE, ArmorType.HELMET, var0);
      });
      TURTLE_SCUTE = registerItem("turtle_scute");
      ARMADILLO_SCUTE = registerItem("armadillo_scute");
      WOLF_ARMOR = registerItem("wolf_armor", (var0) -> {
         return new AnimalArmorItem(ArmorMaterials.ARMADILLO_SCUTE, AnimalArmorItem.BodyType.CANINE, var0);
      });
      FLINT_AND_STEEL = registerItem("flint_and_steel", FlintAndSteelItem::new, (new Item.Properties()).durability(64));
      BOWL = registerItem("bowl");
      APPLE = registerItem("apple", (new Item.Properties()).food(Foods.APPLE));
      BOW = registerItem("bow", BowItem::new, (new Item.Properties()).durability(384).enchantable(1));
      ARROW = registerItem("arrow", ArrowItem::new);
      COAL = registerItem("coal");
      CHARCOAL = registerItem("charcoal");
      DIAMOND = registerItem("diamond");
      EMERALD = registerItem("emerald");
      LAPIS_LAZULI = registerItem("lapis_lazuli");
      QUARTZ = registerItem("quartz");
      AMETHYST_SHARD = registerItem("amethyst_shard");
      RAW_IRON = registerItem("raw_iron");
      IRON_INGOT = registerItem("iron_ingot");
      RAW_COPPER = registerItem("raw_copper");
      COPPER_INGOT = registerItem("copper_ingot");
      RAW_GOLD = registerItem("raw_gold");
      GOLD_INGOT = registerItem("gold_ingot");
      NETHERITE_INGOT = registerItem("netherite_ingot", (new Item.Properties()).fireResistant());
      NETHERITE_SCRAP = registerItem("netherite_scrap", (new Item.Properties()).fireResistant());
      WOODEN_SWORD = registerItem("wooden_sword", (var0) -> {
         return new SwordItem(ToolMaterial.WOOD, 3.0F, -2.4F, var0);
      });
      WOODEN_SHOVEL = registerItem("wooden_shovel", (var0) -> {
         return new ShovelItem(ToolMaterial.WOOD, 1.5F, -3.0F, var0);
      });
      WOODEN_PICKAXE = registerItem("wooden_pickaxe", (var0) -> {
         return new PickaxeItem(ToolMaterial.WOOD, 1.0F, -2.8F, var0);
      });
      WOODEN_AXE = registerItem("wooden_axe", (var0) -> {
         return new AxeItem(ToolMaterial.WOOD, 6.0F, -3.2F, var0);
      });
      WOODEN_HOE = registerItem("wooden_hoe", (var0) -> {
         return new HoeItem(ToolMaterial.WOOD, 0.0F, -3.0F, var0);
      });
      STONE_SWORD = registerItem("stone_sword", (var0) -> {
         return new SwordItem(ToolMaterial.STONE, 3.0F, -2.4F, var0);
      });
      STONE_SHOVEL = registerItem("stone_shovel", (var0) -> {
         return new ShovelItem(ToolMaterial.STONE, 1.5F, -3.0F, var0);
      });
      STONE_PICKAXE = registerItem("stone_pickaxe", (var0) -> {
         return new PickaxeItem(ToolMaterial.STONE, 1.0F, -2.8F, var0);
      });
      STONE_AXE = registerItem("stone_axe", (var0) -> {
         return new AxeItem(ToolMaterial.STONE, 7.0F, -3.2F, var0);
      });
      STONE_HOE = registerItem("stone_hoe", (var0) -> {
         return new HoeItem(ToolMaterial.STONE, -1.0F, -2.0F, var0);
      });
      GOLDEN_SWORD = registerItem("golden_sword", (var0) -> {
         return new SwordItem(ToolMaterial.GOLD, 3.0F, -2.4F, var0);
      });
      GOLDEN_SHOVEL = registerItem("golden_shovel", (var0) -> {
         return new ShovelItem(ToolMaterial.GOLD, 1.5F, -3.0F, var0);
      });
      GOLDEN_PICKAXE = registerItem("golden_pickaxe", (var0) -> {
         return new PickaxeItem(ToolMaterial.GOLD, 1.0F, -2.8F, var0);
      });
      GOLDEN_AXE = registerItem("golden_axe", (var0) -> {
         return new AxeItem(ToolMaterial.GOLD, 6.0F, -3.0F, var0);
      });
      GOLDEN_HOE = registerItem("golden_hoe", (var0) -> {
         return new HoeItem(ToolMaterial.GOLD, 0.0F, -3.0F, var0);
      });
      IRON_SWORD = registerItem("iron_sword", (var0) -> {
         return new SwordItem(ToolMaterial.IRON, 3.0F, -2.4F, var0);
      });
      IRON_SHOVEL = registerItem("iron_shovel", (var0) -> {
         return new ShovelItem(ToolMaterial.IRON, 1.5F, -3.0F, var0);
      });
      IRON_PICKAXE = registerItem("iron_pickaxe", (var0) -> {
         return new PickaxeItem(ToolMaterial.IRON, 1.0F, -2.8F, var0);
      });
      IRON_AXE = registerItem("iron_axe", (var0) -> {
         return new AxeItem(ToolMaterial.IRON, 6.0F, -3.1F, var0);
      });
      IRON_HOE = registerItem("iron_hoe", (var0) -> {
         return new HoeItem(ToolMaterial.IRON, -2.0F, -1.0F, var0);
      });
      DIAMOND_SWORD = registerItem("diamond_sword", (var0) -> {
         return new SwordItem(ToolMaterial.DIAMOND, 3.0F, -2.4F, var0);
      });
      DIAMOND_SHOVEL = registerItem("diamond_shovel", (var0) -> {
         return new ShovelItem(ToolMaterial.DIAMOND, 1.5F, -3.0F, var0);
      });
      DIAMOND_PICKAXE = registerItem("diamond_pickaxe", (var0) -> {
         return new PickaxeItem(ToolMaterial.DIAMOND, 1.0F, -2.8F, var0);
      });
      DIAMOND_AXE = registerItem("diamond_axe", (var0) -> {
         return new AxeItem(ToolMaterial.DIAMOND, 5.0F, -3.0F, var0);
      });
      DIAMOND_HOE = registerItem("diamond_hoe", (var0) -> {
         return new HoeItem(ToolMaterial.DIAMOND, -3.0F, 0.0F, var0);
      });
      NETHERITE_SWORD = registerItem("netherite_sword", (var0) -> {
         return new SwordItem(ToolMaterial.NETHERITE, 3.0F, -2.4F, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_SHOVEL = registerItem("netherite_shovel", (var0) -> {
         return new ShovelItem(ToolMaterial.NETHERITE, 1.5F, -3.0F, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_PICKAXE = registerItem("netherite_pickaxe", (var0) -> {
         return new PickaxeItem(ToolMaterial.NETHERITE, 1.0F, -2.8F, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_AXE = registerItem("netherite_axe", (var0) -> {
         return new AxeItem(ToolMaterial.NETHERITE, 5.0F, -3.0F, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_HOE = registerItem("netherite_hoe", (var0) -> {
         return new HoeItem(ToolMaterial.NETHERITE, -4.0F, 0.0F, var0);
      }, (new Item.Properties()).fireResistant());
      STICK = registerItem("stick");
      MUSHROOM_STEW = registerItem("mushroom_stew", (new Item.Properties()).stacksTo(1).food(Foods.MUSHROOM_STEW).usingConvertsTo(BOWL));
      STRING = registerItem("string", createBlockItemWithCustomItemName(Blocks.TRIPWIRE));
      FEATHER = registerItem("feather");
      GUNPOWDER = registerItem("gunpowder");
      WHEAT_SEEDS = registerItem("wheat_seeds", createBlockItemWithCustomItemName(Blocks.WHEAT));
      WHEAT = registerItem("wheat");
      BREAD = registerItem("bread", (new Item.Properties()).food(Foods.BREAD));
      LEATHER_HELMET = registerItem("leather_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.LEATHER, ArmorType.HELMET, var0);
      });
      LEATHER_CHESTPLATE = registerItem("leather_chestplate", (var0) -> {
         return new ArmorItem(ArmorMaterials.LEATHER, ArmorType.CHESTPLATE, var0);
      });
      LEATHER_LEGGINGS = registerItem("leather_leggings", (var0) -> {
         return new ArmorItem(ArmorMaterials.LEATHER, ArmorType.LEGGINGS, var0);
      });
      LEATHER_BOOTS = registerItem("leather_boots", (var0) -> {
         return new ArmorItem(ArmorMaterials.LEATHER, ArmorType.BOOTS, var0);
      });
      CHAINMAIL_HELMET = registerItem("chainmail_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.HELMET, var0);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      CHAINMAIL_CHESTPLATE = registerItem("chainmail_chestplate", (var0) -> {
         return new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.CHESTPLATE, var0);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      CHAINMAIL_LEGGINGS = registerItem("chainmail_leggings", (var0) -> {
         return new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.LEGGINGS, var0);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      CHAINMAIL_BOOTS = registerItem("chainmail_boots", (var0) -> {
         return new ArmorItem(ArmorMaterials.CHAINMAIL, ArmorType.BOOTS, var0);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      IRON_HELMET = registerItem("iron_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.IRON, ArmorType.HELMET, var0);
      });
      IRON_CHESTPLATE = registerItem("iron_chestplate", (var0) -> {
         return new ArmorItem(ArmorMaterials.IRON, ArmorType.CHESTPLATE, var0);
      });
      IRON_LEGGINGS = registerItem("iron_leggings", (var0) -> {
         return new ArmorItem(ArmorMaterials.IRON, ArmorType.LEGGINGS, var0);
      });
      IRON_BOOTS = registerItem("iron_boots", (var0) -> {
         return new ArmorItem(ArmorMaterials.IRON, ArmorType.BOOTS, var0);
      });
      DIAMOND_HELMET = registerItem("diamond_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.HELMET, var0);
      });
      DIAMOND_CHESTPLATE = registerItem("diamond_chestplate", (var0) -> {
         return new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.CHESTPLATE, var0);
      });
      DIAMOND_LEGGINGS = registerItem("diamond_leggings", (var0) -> {
         return new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.LEGGINGS, var0);
      });
      DIAMOND_BOOTS = registerItem("diamond_boots", (var0) -> {
         return new ArmorItem(ArmorMaterials.DIAMOND, ArmorType.BOOTS, var0);
      });
      GOLDEN_HELMET = registerItem("golden_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.GOLD, ArmorType.HELMET, var0);
      });
      GOLDEN_CHESTPLATE = registerItem("golden_chestplate", (var0) -> {
         return new ArmorItem(ArmorMaterials.GOLD, ArmorType.CHESTPLATE, var0);
      });
      GOLDEN_LEGGINGS = registerItem("golden_leggings", (var0) -> {
         return new ArmorItem(ArmorMaterials.GOLD, ArmorType.LEGGINGS, var0);
      });
      GOLDEN_BOOTS = registerItem("golden_boots", (var0) -> {
         return new ArmorItem(ArmorMaterials.GOLD, ArmorType.BOOTS, var0);
      });
      NETHERITE_HELMET = registerItem("netherite_helmet", (var0) -> {
         return new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.HELMET, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_CHESTPLATE = registerItem("netherite_chestplate", (var0) -> {
         return new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.CHESTPLATE, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_LEGGINGS = registerItem("netherite_leggings", (var0) -> {
         return new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.LEGGINGS, var0);
      }, (new Item.Properties()).fireResistant());
      NETHERITE_BOOTS = registerItem("netherite_boots", (var0) -> {
         return new ArmorItem(ArmorMaterials.NETHERITE, ArmorType.BOOTS, var0);
      }, (new Item.Properties()).fireResistant());
      FLINT = registerItem("flint");
      PORKCHOP = registerItem("porkchop", (new Item.Properties()).food(Foods.PORKCHOP));
      COOKED_PORKCHOP = registerItem("cooked_porkchop", (new Item.Properties()).food(Foods.COOKED_PORKCHOP));
      PAINTING = registerItem("painting", (var0) -> {
         return new HangingEntityItem(EntityType.PAINTING, var0);
      });
      GOLDEN_APPLE = registerItem("golden_apple", (new Item.Properties()).food(Foods.GOLDEN_APPLE, Consumables.GOLDEN_APPLE));
      ENCHANTED_GOLDEN_APPLE = registerItem("enchanted_golden_apple", (new Item.Properties()).rarity(Rarity.RARE).food(Foods.ENCHANTED_GOLDEN_APPLE, Consumables.ENCHANTED_GOLDEN_APPLE).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
      OAK_SIGN = registerBlock(Blocks.OAK_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.OAK_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      SPRUCE_SIGN = registerBlock(Blocks.SPRUCE_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.SPRUCE_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      BIRCH_SIGN = registerBlock(Blocks.BIRCH_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.BIRCH_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      JUNGLE_SIGN = registerBlock(Blocks.JUNGLE_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.JUNGLE_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      ACACIA_SIGN = registerBlock(Blocks.ACACIA_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.ACACIA_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      CHERRY_SIGN = registerBlock(Blocks.CHERRY_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.CHERRY_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      DARK_OAK_SIGN = registerBlock(Blocks.DARK_OAK_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.DARK_OAK_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      PALE_OAK_SIGN = registerBlock(Blocks.PALE_OAK_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.PALE_OAK_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      MANGROVE_SIGN = registerBlock(Blocks.MANGROVE_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.MANGROVE_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      BAMBOO_SIGN = registerBlock(Blocks.BAMBOO_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.BAMBOO_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      CRIMSON_SIGN = registerBlock(Blocks.CRIMSON_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.CRIMSON_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      WARPED_SIGN = registerBlock(Blocks.WARPED_SIGN, (var0, var1) -> {
         return new SignItem(var0, Blocks.WARPED_WALL_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      OAK_HANGING_SIGN = registerBlock(Blocks.OAK_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.OAK_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      SPRUCE_HANGING_SIGN = registerBlock(Blocks.SPRUCE_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.SPRUCE_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      BIRCH_HANGING_SIGN = registerBlock(Blocks.BIRCH_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.BIRCH_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      JUNGLE_HANGING_SIGN = registerBlock(Blocks.JUNGLE_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.JUNGLE_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      ACACIA_HANGING_SIGN = registerBlock(Blocks.ACACIA_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.ACACIA_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      CHERRY_HANGING_SIGN = registerBlock(Blocks.CHERRY_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.CHERRY_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      DARK_OAK_HANGING_SIGN = registerBlock(Blocks.DARK_OAK_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.DARK_OAK_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      PALE_OAK_HANGING_SIGN = registerBlock(Blocks.PALE_OAK_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.PALE_OAK_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      MANGROVE_HANGING_SIGN = registerBlock(Blocks.MANGROVE_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.MANGROVE_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      BAMBOO_HANGING_SIGN = registerBlock(Blocks.BAMBOO_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.BAMBOO_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      CRIMSON_HANGING_SIGN = registerBlock(Blocks.CRIMSON_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.CRIMSON_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      WARPED_HANGING_SIGN = registerBlock(Blocks.WARPED_HANGING_SIGN, (var0, var1) -> {
         return new HangingSignItem(var0, Blocks.WARPED_WALL_HANGING_SIGN, var1);
      }, (new Item.Properties()).stacksTo(16));
      BUCKET = registerItem("bucket", (var0) -> {
         return new BucketItem(Fluids.EMPTY, var0);
      }, (new Item.Properties()).stacksTo(16));
      WATER_BUCKET = registerItem("water_bucket", (var0) -> {
         return new BucketItem(Fluids.WATER, var0);
      }, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1));
      LAVA_BUCKET = registerItem("lava_bucket", (var0) -> {
         return new BucketItem(Fluids.LAVA, var0);
      }, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1));
      POWDER_SNOW_BUCKET = registerItem("powder_snow_bucket", (var0) -> {
         return new SolidBucketItem(Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, var0);
      }, (new Item.Properties()).stacksTo(1).useItemDescriptionPrefix());
      SNOWBALL = registerItem("snowball", SnowballItem::new, (new Item.Properties()).stacksTo(16));
      LEATHER = registerItem("leather");
      MILK_BUCKET = registerItem("milk_bucket", (new Item.Properties()).craftRemainder(BUCKET).component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET).usingConvertsTo(BUCKET).stacksTo(1));
      PUFFERFISH_BUCKET = registerItem("pufferfish_bucket", (var0) -> {
         return new MobBucketItem(EntityType.PUFFERFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
      SALMON_BUCKET = registerItem("salmon_bucket", (var0) -> {
         return new MobBucketItem(EntityType.SALMON, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
      COD_BUCKET = registerItem("cod_bucket", (var0) -> {
         return new MobBucketItem(EntityType.COD, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
      TROPICAL_FISH_BUCKET = registerItem("tropical_fish_bucket", (var0) -> {
         return new MobBucketItem(EntityType.TROPICAL_FISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
      AXOLOTL_BUCKET = registerItem("axolotl_bucket", (var0) -> {
         return new MobBucketItem(EntityType.AXOLOTL, Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL, var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
      TADPOLE_BUCKET = registerItem("tadpole_bucket", (var0) -> {
         return new MobBucketItem(EntityType.TADPOLE, Fluids.WATER, SoundEvents.BUCKET_EMPTY_TADPOLE, var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
      BRICK = registerItem("brick");
      CLAY_BALL = registerItem("clay_ball");
      DRIED_KELP_BLOCK = registerBlock(Blocks.DRIED_KELP_BLOCK);
      PAPER = registerItem("paper");
      BOOK = registerItem("book", (new Item.Properties()).enchantable(1));
      SLIME_BALL = registerItem("slime_ball");
      EGG = registerItem("egg", EggItem::new, (new Item.Properties()).stacksTo(16));
      COMPASS = registerItem("compass", CompassItem::new);
      RECOVERY_COMPASS = registerItem("recovery_compass", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      BUNDLE = registerItem("bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("bundle_open_front"), ResourceLocation.withDefaultNamespace("bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      WHITE_BUNDLE = registerItem("white_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("white_bundle_open_front"), ResourceLocation.withDefaultNamespace("white_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      ORANGE_BUNDLE = registerItem("orange_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("orange_bundle_open_front"), ResourceLocation.withDefaultNamespace("orange_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      MAGENTA_BUNDLE = registerItem("magenta_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("magenta_bundle_open_front"), ResourceLocation.withDefaultNamespace("magenta_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      LIGHT_BLUE_BUNDLE = registerItem("light_blue_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("light_blue_bundle_open_front"), ResourceLocation.withDefaultNamespace("light_blue_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      YELLOW_BUNDLE = registerItem("yellow_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("yellow_bundle_open_front"), ResourceLocation.withDefaultNamespace("yellow_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      LIME_BUNDLE = registerItem("lime_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("lime_bundle_open_front"), ResourceLocation.withDefaultNamespace("lime_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      PINK_BUNDLE = registerItem("pink_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("pink_bundle_open_front"), ResourceLocation.withDefaultNamespace("pink_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      GRAY_BUNDLE = registerItem("gray_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("gray_bundle_open_front"), ResourceLocation.withDefaultNamespace("gray_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      LIGHT_GRAY_BUNDLE = registerItem("light_gray_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("light_gray_bundle_open_front"), ResourceLocation.withDefaultNamespace("light_gray_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      CYAN_BUNDLE = registerItem("cyan_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("cyan_bundle_open_front"), ResourceLocation.withDefaultNamespace("cyan_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      PURPLE_BUNDLE = registerItem("purple_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("purple_bundle_open_front"), ResourceLocation.withDefaultNamespace("purple_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      BLUE_BUNDLE = registerItem("blue_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("blue_bundle_open_front"), ResourceLocation.withDefaultNamespace("blue_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      BROWN_BUNDLE = registerItem("brown_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("brown_bundle_open_front"), ResourceLocation.withDefaultNamespace("brown_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      GREEN_BUNDLE = registerItem("green_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("green_bundle_open_front"), ResourceLocation.withDefaultNamespace("green_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      RED_BUNDLE = registerItem("red_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("red_bundle_open_front"), ResourceLocation.withDefaultNamespace("red_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      BLACK_BUNDLE = registerItem("black_bundle", (var0) -> {
         return new BundleItem(ResourceLocation.withDefaultNamespace("black_bundle_open_front"), ResourceLocation.withDefaultNamespace("black_bundle_open_back"), var0);
      }, (new Item.Properties()).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));
      FISHING_ROD = registerItem("fishing_rod", FishingRodItem::new, (new Item.Properties()).durability(64).enchantable(1));
      CLOCK = registerItem("clock");
      SPYGLASS = registerItem("spyglass", SpyglassItem::new, (new Item.Properties()).stacksTo(1).overrideModel(ResourceLocation.withDefaultNamespace("spyglass_in_hand")));
      GLOWSTONE_DUST = registerItem("glowstone_dust");
      COD = registerItem("cod", (new Item.Properties()).food(Foods.COD));
      SALMON = registerItem("salmon", (new Item.Properties()).food(Foods.SALMON));
      TROPICAL_FISH = registerItem("tropical_fish", (new Item.Properties()).food(Foods.TROPICAL_FISH));
      PUFFERFISH = registerItem("pufferfish", (new Item.Properties()).food(Foods.PUFFERFISH, Consumables.PUFFERFISH));
      COOKED_COD = registerItem("cooked_cod", (new Item.Properties()).food(Foods.COOKED_COD));
      COOKED_SALMON = registerItem("cooked_salmon", (new Item.Properties()).food(Foods.COOKED_SALMON));
      INK_SAC = registerItem("ink_sac", InkSacItem::new);
      GLOW_INK_SAC = registerItem("glow_ink_sac", GlowInkSacItem::new);
      COCOA_BEANS = registerItem("cocoa_beans", createBlockItemWithCustomItemName(Blocks.COCOA));
      WHITE_DYE = registerItem("white_dye", (var0) -> {
         return new DyeItem(DyeColor.WHITE, var0);
      });
      ORANGE_DYE = registerItem("orange_dye", (var0) -> {
         return new DyeItem(DyeColor.ORANGE, var0);
      });
      MAGENTA_DYE = registerItem("magenta_dye", (var0) -> {
         return new DyeItem(DyeColor.MAGENTA, var0);
      });
      LIGHT_BLUE_DYE = registerItem("light_blue_dye", (var0) -> {
         return new DyeItem(DyeColor.LIGHT_BLUE, var0);
      });
      YELLOW_DYE = registerItem("yellow_dye", (var0) -> {
         return new DyeItem(DyeColor.YELLOW, var0);
      });
      LIME_DYE = registerItem("lime_dye", (var0) -> {
         return new DyeItem(DyeColor.LIME, var0);
      });
      PINK_DYE = registerItem("pink_dye", (var0) -> {
         return new DyeItem(DyeColor.PINK, var0);
      });
      GRAY_DYE = registerItem("gray_dye", (var0) -> {
         return new DyeItem(DyeColor.GRAY, var0);
      });
      LIGHT_GRAY_DYE = registerItem("light_gray_dye", (var0) -> {
         return new DyeItem(DyeColor.LIGHT_GRAY, var0);
      });
      CYAN_DYE = registerItem("cyan_dye", (var0) -> {
         return new DyeItem(DyeColor.CYAN, var0);
      });
      PURPLE_DYE = registerItem("purple_dye", (var0) -> {
         return new DyeItem(DyeColor.PURPLE, var0);
      });
      BLUE_DYE = registerItem("blue_dye", (var0) -> {
         return new DyeItem(DyeColor.BLUE, var0);
      });
      BROWN_DYE = registerItem("brown_dye", (var0) -> {
         return new DyeItem(DyeColor.BROWN, var0);
      });
      GREEN_DYE = registerItem("green_dye", (var0) -> {
         return new DyeItem(DyeColor.GREEN, var0);
      });
      RED_DYE = registerItem("red_dye", (var0) -> {
         return new DyeItem(DyeColor.RED, var0);
      });
      BLACK_DYE = registerItem("black_dye", (var0) -> {
         return new DyeItem(DyeColor.BLACK, var0);
      });
      BONE_MEAL = registerItem("bone_meal", BoneMealItem::new);
      BONE = registerItem("bone");
      SUGAR = registerItem("sugar");
      CAKE = registerBlock(Blocks.CAKE, (new Item.Properties()).stacksTo(1));
      WHITE_BED = registerBlock(Blocks.WHITE_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      ORANGE_BED = registerBlock(Blocks.ORANGE_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      MAGENTA_BED = registerBlock(Blocks.MAGENTA_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      LIGHT_BLUE_BED = registerBlock(Blocks.LIGHT_BLUE_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      YELLOW_BED = registerBlock(Blocks.YELLOW_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      LIME_BED = registerBlock(Blocks.LIME_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      PINK_BED = registerBlock(Blocks.PINK_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      GRAY_BED = registerBlock(Blocks.GRAY_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      LIGHT_GRAY_BED = registerBlock(Blocks.LIGHT_GRAY_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      CYAN_BED = registerBlock(Blocks.CYAN_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      PURPLE_BED = registerBlock(Blocks.PURPLE_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      BLUE_BED = registerBlock(Blocks.BLUE_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      BROWN_BED = registerBlock(Blocks.BROWN_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      GREEN_BED = registerBlock(Blocks.GREEN_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      RED_BED = registerBlock(Blocks.RED_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      BLACK_BED = registerBlock(Blocks.BLACK_BED, BedItem::new, (new Item.Properties()).stacksTo(1));
      COOKIE = registerItem("cookie", (new Item.Properties()).food(Foods.COOKIE));
      CRAFTER = registerBlock(Blocks.CRAFTER, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      FILLED_MAP = registerItem("filled_map", MapItem::new, (new Item.Properties()).component(DataComponents.MAP_COLOR, MapItemColor.DEFAULT).component(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY));
      SHEARS = registerItem("shears", ShearsItem::new, (new Item.Properties()).durability(238).component(DataComponents.TOOL, ShearsItem.createToolProperties()));
      MELON_SLICE = registerItem("melon_slice", (new Item.Properties()).food(Foods.MELON_SLICE));
      DRIED_KELP = registerItem("dried_kelp", (new Item.Properties()).food(Foods.DRIED_KELP, Consumables.DRIED_KELP));
      PUMPKIN_SEEDS = registerItem(net.minecraft.references.Items.PUMPKIN_SEEDS, createBlockItemWithCustomItemName(Blocks.PUMPKIN_STEM));
      MELON_SEEDS = registerItem(net.minecraft.references.Items.MELON_SEEDS, createBlockItemWithCustomItemName(Blocks.MELON_STEM));
      BEEF = registerItem("beef", (new Item.Properties()).food(Foods.BEEF));
      COOKED_BEEF = registerItem("cooked_beef", (new Item.Properties()).food(Foods.COOKED_BEEF));
      CHICKEN = registerItem("chicken", (new Item.Properties()).food(Foods.CHICKEN, Consumables.CHICKEN));
      COOKED_CHICKEN = registerItem("cooked_chicken", (new Item.Properties()).food(Foods.COOKED_CHICKEN));
      ROTTEN_FLESH = registerItem("rotten_flesh", (new Item.Properties()).food(Foods.ROTTEN_FLESH, Consumables.ROTTEN_FLESH));
      ENDER_PEARL = registerItem("ender_pearl", EnderpearlItem::new, (new Item.Properties()).stacksTo(16).useCooldown(1.0F));
      BLAZE_ROD = registerItem("blaze_rod");
      GHAST_TEAR = registerItem("ghast_tear");
      GOLD_NUGGET = registerItem("gold_nugget");
      NETHER_WART = registerItem("nether_wart", createBlockItemWithCustomItemName(Blocks.NETHER_WART));
      GLASS_BOTTLE = registerItem("glass_bottle", BottleItem::new);
      POTION = registerItem("potion", PotionItem::new, (new Item.Properties()).stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(GLASS_BOTTLE));
      SPIDER_EYE = registerItem("spider_eye", (new Item.Properties()).food(Foods.SPIDER_EYE, Consumables.SPIDER_EYE));
      FERMENTED_SPIDER_EYE = registerItem("fermented_spider_eye");
      BLAZE_POWDER = registerItem("blaze_powder");
      MAGMA_CREAM = registerItem("magma_cream");
      BREWING_STAND = registerBlock(Blocks.BREWING_STAND, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      CAULDRON = registerBlock(Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
      ENDER_EYE = registerItem("ender_eye", EnderEyeItem::new);
      GLISTERING_MELON_SLICE = registerItem("glistering_melon_slice");
      ARMADILLO_SPAWN_EGG = registerItem("armadillo_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ARMADILLO, 11366765, 8538184, var0);
      });
      ALLAY_SPAWN_EGG = registerItem("allay_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ALLAY, 56063, 44543, var0);
      });
      AXOLOTL_SPAWN_EGG = registerItem("axolotl_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.AXOLOTL, 16499171, 10890612, var0);
      });
      BAT_SPAWN_EGG = registerItem("bat_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.BAT, 4996656, 986895, var0);
      });
      BEE_SPAWN_EGG = registerItem("bee_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.BEE, 15582019, 4400155, var0);
      });
      BLAZE_SPAWN_EGG = registerItem("blaze_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.BLAZE, 16167425, 16775294, var0);
      });
      BOGGED_SPAWN_EGG = registerItem("bogged_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.BOGGED, 9084018, 3231003, var0);
      });
      BREEZE_SPAWN_EGG = registerItem("breeze_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.BREEZE, 11506911, 9529055, var0);
      });
      CAT_SPAWN_EGG = registerItem("cat_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.CAT, 15714446, 9794134, var0);
      });
      CAMEL_SPAWN_EGG = registerItem("camel_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.CAMEL, 16565097, 13341495, var0);
      });
      CAVE_SPIDER_SPAWN_EGG = registerItem("cave_spider_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.CAVE_SPIDER, 803406, 11013646, var0);
      });
      CHICKEN_SPAWN_EGG = registerItem("chicken_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.CHICKEN, 10592673, 16711680, var0);
      });
      COD_SPAWN_EGG = registerItem("cod_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.COD, 12691306, 15058059, var0);
      });
      COW_SPAWN_EGG = registerItem("cow_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.COW, 4470310, 10592673, var0);
      });
      CREEPER_SPAWN_EGG = registerItem("creeper_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.CREEPER, 894731, 0, var0);
      });
      DOLPHIN_SPAWN_EGG = registerItem("dolphin_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.DOLPHIN, 2243405, 16382457, var0);
      });
      DONKEY_SPAWN_EGG = registerItem("donkey_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.DONKEY, 5457209, 8811878, var0);
      });
      DROWNED_SPAWN_EGG = registerItem("drowned_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.DROWNED, 9433559, 7969893, var0);
      });
      ELDER_GUARDIAN_SPAWN_EGG = registerItem("elder_guardian_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ELDER_GUARDIAN, 13552826, 7632531, var0);
      });
      ENDER_DRAGON_SPAWN_EGG = registerItem("ender_dragon_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ENDER_DRAGON, 1842204, 14711290, var0);
      });
      ENDERMAN_SPAWN_EGG = registerItem("enderman_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ENDERMAN, 1447446, 0, var0);
      });
      ENDERMITE_SPAWN_EGG = registerItem("endermite_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ENDERMITE, 1447446, 7237230, var0);
      });
      EVOKER_SPAWN_EGG = registerItem("evoker_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.EVOKER, 9804699, 1973274, var0);
      });
      FOX_SPAWN_EGG = registerItem("fox_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.FOX, 14005919, 13396256, var0);
      });
      FROG_SPAWN_EGG = registerItem("frog_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.FROG, 13661252, 16762748, var0);
      });
      GHAST_SPAWN_EGG = registerItem("ghast_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.GHAST, 16382457, 12369084, var0);
      });
      GLOW_SQUID_SPAWN_EGG = registerItem("glow_squid_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.GLOW_SQUID, 611926, 8778172, var0);
      });
      GOAT_SPAWN_EGG = registerItem("goat_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.GOAT, 10851452, 5589310, var0);
      });
      GUARDIAN_SPAWN_EGG = registerItem("guardian_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.GUARDIAN, 5931634, 15826224, var0);
      });
      HOGLIN_SPAWN_EGG = registerItem("hoglin_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.HOGLIN, 13004373, 6251620, var0);
      });
      HORSE_SPAWN_EGG = registerItem("horse_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.HORSE, 12623485, 15656192, var0);
      });
      HUSK_SPAWN_EGG = registerItem("husk_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.HUSK, 7958625, 15125652, var0);
      });
      IRON_GOLEM_SPAWN_EGG = registerItem("iron_golem_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.IRON_GOLEM, 14405058, 7643954, var0);
      });
      LLAMA_SPAWN_EGG = registerItem("llama_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.LLAMA, 12623485, 10051392, var0);
      });
      MAGMA_CUBE_SPAWN_EGG = registerItem("magma_cube_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.MAGMA_CUBE, 3407872, 16579584, var0);
      });
      MOOSHROOM_SPAWN_EGG = registerItem("mooshroom_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.MOOSHROOM, 10489616, 12040119, var0);
      });
      MULE_SPAWN_EGG = registerItem("mule_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.MULE, 1769984, 5321501, var0);
      });
      OCELOT_SPAWN_EGG = registerItem("ocelot_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.OCELOT, 15720061, 5653556, var0);
      });
      PANDA_SPAWN_EGG = registerItem("panda_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PANDA, 15198183, 1776418, var0);
      });
      PARROT_SPAWN_EGG = registerItem("parrot_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PARROT, 894731, 16711680, var0);
      });
      PHANTOM_SPAWN_EGG = registerItem("phantom_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PHANTOM, 4411786, 8978176, var0);
      });
      PIG_SPAWN_EGG = registerItem("pig_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PIG, 15771042, 14377823, var0);
      });
      PIGLIN_SPAWN_EGG = registerItem("piglin_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PIGLIN, 10051392, 16380836, var0);
      });
      PIGLIN_BRUTE_SPAWN_EGG = registerItem("piglin_brute_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PIGLIN_BRUTE, 5843472, 16380836, var0);
      });
      PILLAGER_SPAWN_EGG = registerItem("pillager_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PILLAGER, 5451574, 9804699, var0);
      });
      POLAR_BEAR_SPAWN_EGG = registerItem("polar_bear_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.POLAR_BEAR, 15658718, 14014157, var0);
      });
      PUFFERFISH_SPAWN_EGG = registerItem("pufferfish_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.PUFFERFISH, 16167425, 3654642, var0);
      });
      RABBIT_SPAWN_EGG = registerItem("rabbit_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.RABBIT, 10051392, 7555121, var0);
      });
      RAVAGER_SPAWN_EGG = registerItem("ravager_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.RAVAGER, 7697520, 5984329, var0);
      });
      SALMON_SPAWN_EGG = registerItem("salmon_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SALMON, 10489616, 951412, var0);
      });
      SHEEP_SPAWN_EGG = registerItem("sheep_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SHEEP, 15198183, 16758197, var0);
      });
      SHULKER_SPAWN_EGG = registerItem("shulker_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SHULKER, 9725844, 5060690, var0);
      });
      SILVERFISH_SPAWN_EGG = registerItem("silverfish_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SILVERFISH, 7237230, 3158064, var0);
      });
      SKELETON_SPAWN_EGG = registerItem("skeleton_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SKELETON, 12698049, 4802889, var0);
      });
      SKELETON_HORSE_SPAWN_EGG = registerItem("skeleton_horse_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SKELETON_HORSE, 6842447, 15066584, var0);
      });
      SLIME_SPAWN_EGG = registerItem("slime_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SLIME, 5349438, 8306542, var0);
      });
      SNIFFER_SPAWN_EGG = registerItem("sniffer_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SNIFFER, 8855049, 2468720, var0);
      });
      SNOW_GOLEM_SPAWN_EGG = registerItem("snow_golem_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SNOW_GOLEM, 14283506, 8496292, var0);
      });
      SPIDER_SPAWN_EGG = registerItem("spider_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SPIDER, 3419431, 11013646, var0);
      });
      SQUID_SPAWN_EGG = registerItem("squid_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.SQUID, 2243405, 7375001, var0);
      });
      STRAY_SPAWN_EGG = registerItem("stray_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.STRAY, 6387319, 14543594, var0);
      });
      STRIDER_SPAWN_EGG = registerItem("strider_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.STRIDER, 10236982, 5065037, var0);
      });
      TADPOLE_SPAWN_EGG = registerItem("tadpole_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.TADPOLE, 7164733, 1444352, var0);
      });
      TRADER_LLAMA_SPAWN_EGG = registerItem("trader_llama_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.TRADER_LLAMA, 15377456, 4547222, var0);
      });
      TROPICAL_FISH_SPAWN_EGG = registerItem("tropical_fish_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.TROPICAL_FISH, 15690005, 16775663, var0);
      });
      TURTLE_SPAWN_EGG = registerItem("turtle_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.TURTLE, 15198183, 44975, var0);
      });
      VEX_SPAWN_EGG = registerItem("vex_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.VEX, 8032420, 15265265, var0);
      });
      VILLAGER_SPAWN_EGG = registerItem("villager_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.VILLAGER, 5651507, 12422002, var0);
      });
      VINDICATOR_SPAWN_EGG = registerItem("vindicator_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.VINDICATOR, 9804699, 2580065, var0);
      });
      WANDERING_TRADER_SPAWN_EGG = registerItem("wandering_trader_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.WANDERING_TRADER, 4547222, 15377456, var0);
      });
      WARDEN_SPAWN_EGG = registerItem("warden_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.WARDEN, 1001033, 3790560, var0);
      });
      WITCH_SPAWN_EGG = registerItem("witch_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.WITCH, 3407872, 5349438, var0);
      });
      WITHER_SPAWN_EGG = registerItem("wither_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.WITHER, 1315860, 5075616, var0);
      });
      WITHER_SKELETON_SPAWN_EGG = registerItem("wither_skeleton_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.WITHER_SKELETON, 1315860, 4672845, var0);
      });
      WOLF_SPAWN_EGG = registerItem("wolf_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.WOLF, 14144467, 13545366, var0);
      });
      ZOGLIN_SPAWN_EGG = registerItem("zoglin_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ZOGLIN, 13004373, 15132390, var0);
      });
      CREAKING_SPAWN_EGG = registerItem("creaking_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.CREAKING, 6250335, 16545810, var0);
      });
      ZOMBIE_SPAWN_EGG = registerItem("zombie_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ZOMBIE, 44975, 7969893, var0);
      });
      ZOMBIE_HORSE_SPAWN_EGG = registerItem("zombie_horse_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ZOMBIE_HORSE, 3232308, 9945732, var0);
      });
      ZOMBIE_VILLAGER_SPAWN_EGG = registerItem("zombie_villager_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, var0);
      });
      ZOMBIFIED_PIGLIN_SPAWN_EGG = registerItem("zombified_piglin_spawn_egg", (var0) -> {
         return new SpawnEggItem(EntityType.ZOMBIFIED_PIGLIN, 15373203, 5009705, var0);
      });
      EXPERIENCE_BOTTLE = registerItem("experience_bottle", ExperienceBottleItem::new, (new Item.Properties()).rarity(Rarity.UNCOMMON).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
      FIRE_CHARGE = registerItem("fire_charge", FireChargeItem::new);
      WIND_CHARGE = registerItem("wind_charge", WindChargeItem::new, (new Item.Properties()).useCooldown(0.5F));
      WRITABLE_BOOK = registerItem("writable_book", WritableBookItem::new, (new Item.Properties()).stacksTo(1).component(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY));
      WRITTEN_BOOK = registerItem("written_book", WrittenBookItem::new, (new Item.Properties()).stacksTo(16).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
      BREEZE_ROD = registerItem("breeze_rod");
      MACE = registerItem("mace", MaceItem::new, (new Item.Properties()).rarity(Rarity.EPIC).durability(500).component(DataComponents.TOOL, MaceItem.createToolProperties()).repairable(BREEZE_ROD).attributes(MaceItem.createAttributes()).enchantable(15));
      ITEM_FRAME = registerItem("item_frame", (var0) -> {
         return new ItemFrameItem(EntityType.ITEM_FRAME, var0);
      });
      GLOW_ITEM_FRAME = registerItem("glow_item_frame", (var0) -> {
         return new ItemFrameItem(EntityType.GLOW_ITEM_FRAME, var0);
      });
      FLOWER_POT = registerBlock(Blocks.FLOWER_POT);
      CARROT = registerItem("carrot", createBlockItemWithCustomItemName(Blocks.CARROTS), (new Item.Properties()).food(Foods.CARROT));
      POTATO = registerItem("potato", createBlockItemWithCustomItemName(Blocks.POTATOES), (new Item.Properties()).food(Foods.POTATO));
      BAKED_POTATO = registerItem("baked_potato", (new Item.Properties()).food(Foods.BAKED_POTATO));
      POISONOUS_POTATO = registerItem("poisonous_potato", (new Item.Properties()).food(Foods.POISONOUS_POTATO, Consumables.POISONOUS_POTATO));
      MAP = registerItem("map", EmptyMapItem::new);
      GOLDEN_CARROT = registerItem("golden_carrot", (new Item.Properties()).food(Foods.GOLDEN_CARROT));
      SKELETON_SKULL = registerBlock(Blocks.SKELETON_SKULL, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.SKELETON_WALL_SKULL, Direction.DOWN, var1);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD));
      WITHER_SKELETON_SKULL = registerBlock(Blocks.WITHER_SKELETON_SKULL, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.WITHER_SKELETON_WALL_SKULL, Direction.DOWN, var1);
      }, (new Item.Properties()).rarity(Rarity.RARE).equippableUnswappable(EquipmentSlot.HEAD));
      PLAYER_HEAD = registerBlock(Blocks.PLAYER_HEAD, (var0, var1) -> {
         return new PlayerHeadItem(var0, Blocks.PLAYER_WALL_HEAD, var1);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD));
      ZOMBIE_HEAD = registerBlock(Blocks.ZOMBIE_HEAD, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.ZOMBIE_WALL_HEAD, Direction.DOWN, var1);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD));
      CREEPER_HEAD = registerBlock(Blocks.CREEPER_HEAD, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.CREEPER_WALL_HEAD, Direction.DOWN, var1);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD));
      DRAGON_HEAD = registerBlock(Blocks.DRAGON_HEAD, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.DRAGON_WALL_HEAD, Direction.DOWN, var1);
      }, (new Item.Properties()).rarity(Rarity.EPIC).equippableUnswappable(EquipmentSlot.HEAD));
      PIGLIN_HEAD = registerBlock(Blocks.PIGLIN_HEAD, (var0, var1) -> {
         return new StandingAndWallBlockItem(var0, Blocks.PIGLIN_WALL_HEAD, Direction.DOWN, var1);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON).equippableUnswappable(EquipmentSlot.HEAD));
      NETHER_STAR = registerItem("nether_star", (new Item.Properties()).rarity(Rarity.RARE).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_EXPLOSION)));
      PUMPKIN_PIE = registerItem("pumpkin_pie", (new Item.Properties()).food(Foods.PUMPKIN_PIE));
      FIREWORK_ROCKET = registerItem("firework_rocket", FireworkRocketItem::new, (new Item.Properties()).component(DataComponents.FIREWORKS, new Fireworks(1, List.of())));
      FIREWORK_STAR = registerItem("firework_star", FireworkStarItem::new);
      ENCHANTED_BOOK = registerItem("enchanted_book", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
      NETHER_BRICK = registerItem("nether_brick");
      RESIN_BRICK = registerItem("resin_brick");
      PRISMARINE_SHARD = registerItem("prismarine_shard");
      PRISMARINE_CRYSTALS = registerItem("prismarine_crystals");
      RABBIT = registerItem("rabbit", (new Item.Properties()).food(Foods.RABBIT));
      COOKED_RABBIT = registerItem("cooked_rabbit", (new Item.Properties()).food(Foods.COOKED_RABBIT));
      RABBIT_STEW = registerItem("rabbit_stew", (new Item.Properties()).stacksTo(1).food(Foods.RABBIT_STEW).usingConvertsTo(BOWL));
      RABBIT_FOOT = registerItem("rabbit_foot");
      RABBIT_HIDE = registerItem("rabbit_hide");
      ARMOR_STAND = registerItem("armor_stand", ArmorStandItem::new, (new Item.Properties()).stacksTo(16));
      IRON_HORSE_ARMOR = registerItem("iron_horse_armor", (var0) -> {
         return new AnimalArmorItem(ArmorMaterials.IRON, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, var0);
      }, (new Item.Properties()).stacksTo(1));
      GOLDEN_HORSE_ARMOR = registerItem("golden_horse_armor", (var0) -> {
         return new AnimalArmorItem(ArmorMaterials.GOLD, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, var0);
      }, (new Item.Properties()).stacksTo(1));
      DIAMOND_HORSE_ARMOR = registerItem("diamond_horse_armor", (var0) -> {
         return new AnimalArmorItem(ArmorMaterials.DIAMOND, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, var0);
      }, (new Item.Properties()).stacksTo(1));
      LEATHER_HORSE_ARMOR = registerItem("leather_horse_armor", (var0) -> {
         return new AnimalArmorItem(ArmorMaterials.LEATHER, AnimalArmorItem.BodyType.EQUESTRIAN, SoundEvents.HORSE_ARMOR, false, var0);
      }, (new Item.Properties()).stacksTo(1));
      LEAD = registerItem("lead", LeadItem::new);
      NAME_TAG = registerItem("name_tag", NameTagItem::new);
      COMMAND_BLOCK_MINECART = registerItem("command_block_minecart", (var0) -> {
         return new MinecartItem(EntityType.COMMAND_BLOCK_MINECART, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC));
      MUTTON = registerItem("mutton", (new Item.Properties()).food(Foods.MUTTON));
      COOKED_MUTTON = registerItem("cooked_mutton", (new Item.Properties()).food(Foods.COOKED_MUTTON));
      WHITE_BANNER = registerBlock(Blocks.WHITE_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.WHITE_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      ORANGE_BANNER = registerBlock(Blocks.ORANGE_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.ORANGE_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      MAGENTA_BANNER = registerBlock(Blocks.MAGENTA_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.MAGENTA_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      LIGHT_BLUE_BANNER = registerBlock(Blocks.LIGHT_BLUE_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.LIGHT_BLUE_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      YELLOW_BANNER = registerBlock(Blocks.YELLOW_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.YELLOW_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      LIME_BANNER = registerBlock(Blocks.LIME_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.LIME_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      PINK_BANNER = registerBlock(Blocks.PINK_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.PINK_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      GRAY_BANNER = registerBlock(Blocks.GRAY_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.GRAY_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      LIGHT_GRAY_BANNER = registerBlock(Blocks.LIGHT_GRAY_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.LIGHT_GRAY_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      CYAN_BANNER = registerBlock(Blocks.CYAN_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.CYAN_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      PURPLE_BANNER = registerBlock(Blocks.PURPLE_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.PURPLE_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      BLUE_BANNER = registerBlock(Blocks.BLUE_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.BLUE_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      BROWN_BANNER = registerBlock(Blocks.BROWN_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.BROWN_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      GREEN_BANNER = registerBlock(Blocks.GREEN_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.GREEN_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      RED_BANNER = registerBlock(Blocks.RED_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.RED_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      BLACK_BANNER = registerBlock(Blocks.BLACK_BANNER, (var0, var1) -> {
         return new BannerItem(var0, Blocks.BLACK_WALL_BANNER, var1);
      }, (new Item.Properties()).stacksTo(16).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
      END_CRYSTAL = registerItem("end_crystal", EndCrystalItem::new, (new Item.Properties()).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
      CHORUS_FRUIT = registerItem("chorus_fruit", (new Item.Properties()).food(Foods.CHORUS_FRUIT, Consumables.CHORUS_FRUIT).useCooldown(1.0F));
      POPPED_CHORUS_FRUIT = registerItem("popped_chorus_fruit");
      TORCHFLOWER_SEEDS = registerItem("torchflower_seeds", createBlockItemWithCustomItemName(Blocks.TORCHFLOWER_CROP));
      PITCHER_POD = registerItem("pitcher_pod", createBlockItemWithCustomItemName(Blocks.PITCHER_CROP));
      BEETROOT = registerItem("beetroot", (new Item.Properties()).food(Foods.BEETROOT));
      BEETROOT_SEEDS = registerItem("beetroot_seeds", createBlockItemWithCustomItemName(Blocks.BEETROOTS));
      BEETROOT_SOUP = registerItem("beetroot_soup", (new Item.Properties()).stacksTo(1).food(Foods.BEETROOT_SOUP).usingConvertsTo(BOWL));
      DRAGON_BREATH = registerItem("dragon_breath", (new Item.Properties()).craftRemainder(GLASS_BOTTLE).rarity(Rarity.UNCOMMON));
      SPLASH_POTION = registerItem("splash_potion", SplashPotionItem::new, (new Item.Properties()).stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
      SPECTRAL_ARROW = registerItem("spectral_arrow", SpectralArrowItem::new);
      TIPPED_ARROW = registerItem("tipped_arrow", TippedArrowItem::new, (new Item.Properties()).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
      LINGERING_POTION = registerItem("lingering_potion", LingeringPotionItem::new, (new Item.Properties()).stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
      SHIELD = registerItem("shield", ShieldItem::new, (new Item.Properties()).durability(336).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).repairable(ItemTags.WOODEN_TOOL_MATERIALS).equippableUnswappable(EquipmentSlot.OFFHAND));
      TOTEM_OF_UNDYING = registerItem("totem_of_undying", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING));
      SHULKER_SHELL = registerItem("shulker_shell");
      IRON_NUGGET = registerItem("iron_nugget");
      KNOWLEDGE_BOOK = registerItem("knowledge_book", KnowledgeBookItem::new, (new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC).component(DataComponents.RECIPES, List.of()));
      DEBUG_STICK = registerItem("debug_stick", DebugStickItem::new, (new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC).component(DataComponents.DEBUG_STICK_STATE, DebugStickState.EMPTY).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
      MUSIC_DISC_13 = registerItem("music_disc_13", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.THIRTEEN));
      MUSIC_DISC_CAT = registerItem("music_disc_cat", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CAT));
      MUSIC_DISC_BLOCKS = registerItem("music_disc_blocks", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.BLOCKS));
      MUSIC_DISC_CHIRP = registerItem("music_disc_chirp", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CHIRP));
      MUSIC_DISC_CREATOR = registerItem("music_disc_creator", (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.CREATOR));
      MUSIC_DISC_CREATOR_MUSIC_BOX = registerItem("music_disc_creator_music_box", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.CREATOR_MUSIC_BOX));
      MUSIC_DISC_FAR = registerItem("music_disc_far", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.FAR));
      MUSIC_DISC_MALL = registerItem("music_disc_mall", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.MALL));
      MUSIC_DISC_MELLOHI = registerItem("music_disc_mellohi", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.MELLOHI));
      MUSIC_DISC_STAL = registerItem("music_disc_stal", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.STAL));
      MUSIC_DISC_STRAD = registerItem("music_disc_strad", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.STRAD));
      MUSIC_DISC_WARD = registerItem("music_disc_ward", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.WARD));
      MUSIC_DISC_11 = registerItem("music_disc_11", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.ELEVEN));
      MUSIC_DISC_WAIT = registerItem("music_disc_wait", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.WAIT));
      MUSIC_DISC_OTHERSIDE = registerItem("music_disc_otherside", (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.OTHERSIDE));
      MUSIC_DISC_RELIC = registerItem("music_disc_relic", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.RELIC));
      MUSIC_DISC_5 = registerItem("music_disc_5", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.FIVE));
      MUSIC_DISC_PIGSTEP = registerItem("music_disc_pigstep", (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxSongs.PIGSTEP));
      MUSIC_DISC_PRECIPICE = registerItem("music_disc_precipice", (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(JukeboxSongs.PRECIPICE));
      DISC_FRAGMENT_5 = registerItem("disc_fragment_5", DiscFragmentItem::new, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      TRIDENT = registerItem("trident", TridentItem::new, (new Item.Properties()).rarity(Rarity.RARE).durability(250).attributes(TridentItem.createAttributes()).component(DataComponents.TOOL, TridentItem.createToolProperties()).enchantable(1).overrideModel(ResourceLocation.withDefaultNamespace("trident_in_hand")));
      NAUTILUS_SHELL = registerItem("nautilus_shell", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      HEART_OF_THE_SEA = registerItem("heart_of_the_sea", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      CROSSBOW = registerItem("crossbow", CrossbowItem::new, (new Item.Properties()).stacksTo(1).durability(465).component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).enchantable(1));
      SUSPICIOUS_STEW = registerItem("suspicious_stew", (new Item.Properties()).stacksTo(1).food(Foods.SUSPICIOUS_STEW).component(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY).usingConvertsTo(BOWL));
      LOOM = registerBlock(Blocks.LOOM);
      FLOWER_BANNER_PATTERN = registerItem("flower_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_FLOWER, var0);
      }, (new Item.Properties()).stacksTo(1));
      CREEPER_BANNER_PATTERN = registerItem("creeper_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_CREEPER, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON));
      SKULL_BANNER_PATTERN = registerItem("skull_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_SKULL, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE));
      MOJANG_BANNER_PATTERN = registerItem("mojang_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_MOJANG, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE));
      GLOBE_BANNER_PATTERN = registerItem("globe_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_GLOBE, var0);
      }, (new Item.Properties()).stacksTo(1));
      PIGLIN_BANNER_PATTERN = registerItem("piglin_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_PIGLIN, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.UNCOMMON));
      FLOW_BANNER_PATTERN = registerItem("flow_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_FLOW, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE));
      GUSTER_BANNER_PATTERN = registerItem("guster_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_GUSTER, var0);
      }, (new Item.Properties()).stacksTo(1).rarity(Rarity.RARE));
      FIELD_MASONED_BANNER_PATTERN = registerItem("field_masoned_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_FIELD_MASONED, var0);
      }, (new Item.Properties()).stacksTo(1));
      BORDURE_INDENTED_BANNER_PATTERN = registerItem("bordure_indented_banner_pattern", (var0) -> {
         return new BannerPatternItem(BannerPatternTags.PATTERN_ITEM_BORDURE_INDENTED, var0);
      }, (new Item.Properties()).stacksTo(1));
      GOAT_HORN = registerItem("goat_horn", (var0) -> {
         return new InstrumentItem(InstrumentTags.GOAT_HORNS, var0);
      }, (new Item.Properties()).rarity(Rarity.UNCOMMON).stacksTo(1));
      COMPOSTER = registerBlock(Blocks.COMPOSTER);
      BARREL = registerBlock(Blocks.BARREL, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      SMOKER = registerBlock(Blocks.SMOKER, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      BLAST_FURNACE = registerBlock(Blocks.BLAST_FURNACE, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      CARTOGRAPHY_TABLE = registerBlock(Blocks.CARTOGRAPHY_TABLE);
      FLETCHING_TABLE = registerBlock(Blocks.FLETCHING_TABLE);
      GRINDSTONE = registerBlock(Blocks.GRINDSTONE);
      SMITHING_TABLE = registerBlock(Blocks.SMITHING_TABLE);
      STONECUTTER = registerBlock(Blocks.STONECUTTER);
      BELL = registerBlock(Blocks.BELL);
      LANTERN = registerBlock(Blocks.LANTERN);
      SOUL_LANTERN = registerBlock(Blocks.SOUL_LANTERN);
      SWEET_BERRIES = registerItem("sweet_berries", createBlockItemWithCustomItemName(Blocks.SWEET_BERRY_BUSH), (new Item.Properties()).food(Foods.SWEET_BERRIES));
      GLOW_BERRIES = registerItem("glow_berries", createBlockItemWithCustomItemName(Blocks.CAVE_VINES), (new Item.Properties()).food(Foods.GLOW_BERRIES));
      CAMPFIRE = registerBlock(Blocks.CAMPFIRE, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      SOUL_CAMPFIRE = registerBlock(Blocks.SOUL_CAMPFIRE, (var0) -> {
         return var0.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      });
      SHROOMLIGHT = registerBlock(Blocks.SHROOMLIGHT);
      HONEYCOMB = registerItem("honeycomb", HoneycombItem::new);
      BEE_NEST = registerBlock(Blocks.BEE_NEST, (new Item.Properties()).component(DataComponents.BEES, List.of()));
      BEEHIVE = registerBlock(Blocks.BEEHIVE, (new Item.Properties()).component(DataComponents.BEES, List.of()));
      HONEY_BOTTLE = registerItem("honey_bottle", (new Item.Properties()).craftRemainder(GLASS_BOTTLE).food(Foods.HONEY_BOTTLE, Consumables.HONEY_BOTTLE).usingConvertsTo(GLASS_BOTTLE).stacksTo(16));
      HONEYCOMB_BLOCK = registerBlock(Blocks.HONEYCOMB_BLOCK);
      LODESTONE = registerBlock(Blocks.LODESTONE);
      CRYING_OBSIDIAN = registerBlock(Blocks.CRYING_OBSIDIAN);
      BLACKSTONE = registerBlock(Blocks.BLACKSTONE);
      BLACKSTONE_SLAB = registerBlock(Blocks.BLACKSTONE_SLAB);
      BLACKSTONE_STAIRS = registerBlock(Blocks.BLACKSTONE_STAIRS);
      GILDED_BLACKSTONE = registerBlock(Blocks.GILDED_BLACKSTONE);
      POLISHED_BLACKSTONE = registerBlock(Blocks.POLISHED_BLACKSTONE);
      POLISHED_BLACKSTONE_SLAB = registerBlock(Blocks.POLISHED_BLACKSTONE_SLAB);
      POLISHED_BLACKSTONE_STAIRS = registerBlock(Blocks.POLISHED_BLACKSTONE_STAIRS);
      CHISELED_POLISHED_BLACKSTONE = registerBlock(Blocks.CHISELED_POLISHED_BLACKSTONE);
      POLISHED_BLACKSTONE_BRICKS = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICKS);
      POLISHED_BLACKSTONE_BRICK_SLAB = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
      POLISHED_BLACKSTONE_BRICK_STAIRS = registerBlock(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      CRACKED_POLISHED_BLACKSTONE_BRICKS = registerBlock(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
      RESPAWN_ANCHOR = registerBlock(Blocks.RESPAWN_ANCHOR);
      CANDLE = registerBlock(Blocks.CANDLE);
      WHITE_CANDLE = registerBlock(Blocks.WHITE_CANDLE);
      ORANGE_CANDLE = registerBlock(Blocks.ORANGE_CANDLE);
      MAGENTA_CANDLE = registerBlock(Blocks.MAGENTA_CANDLE);
      LIGHT_BLUE_CANDLE = registerBlock(Blocks.LIGHT_BLUE_CANDLE);
      YELLOW_CANDLE = registerBlock(Blocks.YELLOW_CANDLE);
      LIME_CANDLE = registerBlock(Blocks.LIME_CANDLE);
      PINK_CANDLE = registerBlock(Blocks.PINK_CANDLE);
      GRAY_CANDLE = registerBlock(Blocks.GRAY_CANDLE);
      LIGHT_GRAY_CANDLE = registerBlock(Blocks.LIGHT_GRAY_CANDLE);
      CYAN_CANDLE = registerBlock(Blocks.CYAN_CANDLE);
      PURPLE_CANDLE = registerBlock(Blocks.PURPLE_CANDLE);
      BLUE_CANDLE = registerBlock(Blocks.BLUE_CANDLE);
      BROWN_CANDLE = registerBlock(Blocks.BROWN_CANDLE);
      GREEN_CANDLE = registerBlock(Blocks.GREEN_CANDLE);
      RED_CANDLE = registerBlock(Blocks.RED_CANDLE);
      BLACK_CANDLE = registerBlock(Blocks.BLACK_CANDLE);
      SMALL_AMETHYST_BUD = registerBlock(Blocks.SMALL_AMETHYST_BUD);
      MEDIUM_AMETHYST_BUD = registerBlock(Blocks.MEDIUM_AMETHYST_BUD);
      LARGE_AMETHYST_BUD = registerBlock(Blocks.LARGE_AMETHYST_BUD);
      AMETHYST_CLUSTER = registerBlock(Blocks.AMETHYST_CLUSTER);
      POINTED_DRIPSTONE = registerBlock(Blocks.POINTED_DRIPSTONE);
      OCHRE_FROGLIGHT = registerBlock(Blocks.OCHRE_FROGLIGHT);
      VERDANT_FROGLIGHT = registerBlock(Blocks.VERDANT_FROGLIGHT);
      PEARLESCENT_FROGLIGHT = registerBlock(Blocks.PEARLESCENT_FROGLIGHT);
      FROGSPAWN = registerBlock(Blocks.FROGSPAWN, PlaceOnWaterBlockItem::new);
      ECHO_SHARD = registerItem("echo_shard", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      BRUSH = registerItem("brush", BrushItem::new, (new Item.Properties()).durability(64));
      NETHERITE_UPGRADE_SMITHING_TEMPLATE = registerItem("netherite_upgrade_smithing_template", SmithingTemplateItem::createNetheriteUpgradeTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("sentry_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      DUNE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("dune_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      COAST_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("coast_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      WILD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("wild_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      WARD_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("ward_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.RARE));
      EYE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("eye_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.RARE));
      VEX_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("vex_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.RARE));
      TIDE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("tide_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("snout_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      RIB_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("rib_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("spire_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.RARE));
      WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("wayfinder_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("shaper_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("silence_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.EPIC));
      RAISER_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("raiser_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      HOST_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("host_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      FLOW_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("flow_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      BOLT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("bolt_armor_trim_smithing_template", SmithingTemplateItem::createArmorTrimTemplate, (new Item.Properties()).rarity(Rarity.UNCOMMON));
      ANGLER_POTTERY_SHERD = registerItem("angler_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      ARCHER_POTTERY_SHERD = registerItem("archer_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      ARMS_UP_POTTERY_SHERD = registerItem("arms_up_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      BLADE_POTTERY_SHERD = registerItem("blade_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      BREWER_POTTERY_SHERD = registerItem("brewer_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      BURN_POTTERY_SHERD = registerItem("burn_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      DANGER_POTTERY_SHERD = registerItem("danger_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      EXPLORER_POTTERY_SHERD = registerItem("explorer_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      FLOW_POTTERY_SHERD = registerItem("flow_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      FRIEND_POTTERY_SHERD = registerItem("friend_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      GUSTER_POTTERY_SHERD = registerItem("guster_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      HEART_POTTERY_SHERD = registerItem("heart_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      HEARTBREAK_POTTERY_SHERD = registerItem("heartbreak_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      HOWL_POTTERY_SHERD = registerItem("howl_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      MINER_POTTERY_SHERD = registerItem("miner_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      MOURNER_POTTERY_SHERD = registerItem("mourner_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      PLENTY_POTTERY_SHERD = registerItem("plenty_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      PRIZE_POTTERY_SHERD = registerItem("prize_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SCRAPE_POTTERY_SHERD = registerItem("scrape_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SHEAF_POTTERY_SHERD = registerItem("sheaf_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SHELTER_POTTERY_SHERD = registerItem("shelter_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SKULL_POTTERY_SHERD = registerItem("skull_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      SNORT_POTTERY_SHERD = registerItem("snort_pottery_sherd", (new Item.Properties()).rarity(Rarity.UNCOMMON));
      COPPER_GRATE = registerBlock(Blocks.COPPER_GRATE);
      EXPOSED_COPPER_GRATE = registerBlock(Blocks.EXPOSED_COPPER_GRATE);
      WEATHERED_COPPER_GRATE = registerBlock(Blocks.WEATHERED_COPPER_GRATE);
      OXIDIZED_COPPER_GRATE = registerBlock(Blocks.OXIDIZED_COPPER_GRATE);
      WAXED_COPPER_GRATE = registerBlock(Blocks.WAXED_COPPER_GRATE);
      WAXED_EXPOSED_COPPER_GRATE = registerBlock(Blocks.WAXED_EXPOSED_COPPER_GRATE);
      WAXED_WEATHERED_COPPER_GRATE = registerBlock(Blocks.WAXED_WEATHERED_COPPER_GRATE);
      WAXED_OXIDIZED_COPPER_GRATE = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_GRATE);
      COPPER_BULB = registerBlock(Blocks.COPPER_BULB);
      EXPOSED_COPPER_BULB = registerBlock(Blocks.EXPOSED_COPPER_BULB);
      WEATHERED_COPPER_BULB = registerBlock(Blocks.WEATHERED_COPPER_BULB);
      OXIDIZED_COPPER_BULB = registerBlock(Blocks.OXIDIZED_COPPER_BULB);
      WAXED_COPPER_BULB = registerBlock(Blocks.WAXED_COPPER_BULB);
      WAXED_EXPOSED_COPPER_BULB = registerBlock(Blocks.WAXED_EXPOSED_COPPER_BULB);
      WAXED_WEATHERED_COPPER_BULB = registerBlock(Blocks.WAXED_WEATHERED_COPPER_BULB);
      WAXED_OXIDIZED_COPPER_BULB = registerBlock(Blocks.WAXED_OXIDIZED_COPPER_BULB);
      TRIAL_SPAWNER = registerBlock(Blocks.TRIAL_SPAWNER);
      TRIAL_KEY = registerItem("trial_key");
      OMINOUS_TRIAL_KEY = registerItem("ominous_trial_key");
      VAULT = registerBlock(Blocks.VAULT);
      OMINOUS_BOTTLE = registerItem("ominous_bottle", (new Item.Properties()).rarity(Rarity.UNCOMMON).component(DataComponents.CONSUMABLE, Consumables.OMINOUS_BOTTLE).component(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(0)));
   }
}
