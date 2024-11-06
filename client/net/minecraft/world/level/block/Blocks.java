package net.minecraft.world.level.block;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.references.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class Blocks {
   private static final BlockBehaviour.StatePredicate NOT_CLOSED_SHULKER = (var0x, var1x, var2x) -> {
      BlockEntity var4 = var1x.getBlockEntity(var2x);
      if (var4 instanceof ShulkerBoxBlockEntity var3) {
         return var3.isClosed();
      } else {
         return true;
      }
   };
   private static final BlockBehaviour.StatePredicate NOT_EXTENDED_PISTON = (var0x, var1x, var2x) -> {
      return !(Boolean)var0x.getValue(PistonBaseBlock.EXTENDED);
   };
   public static final Block AIR = register("air", AirBlock::new, BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air());
   public static final Block STONE;
   public static final Block GRANITE;
   public static final Block POLISHED_GRANITE;
   public static final Block DIORITE;
   public static final Block POLISHED_DIORITE;
   public static final Block ANDESITE;
   public static final Block POLISHED_ANDESITE;
   public static final Block GRASS_BLOCK;
   public static final Block DIRT;
   public static final Block COARSE_DIRT;
   public static final Block PODZOL;
   public static final Block COBBLESTONE;
   public static final Block OAK_PLANKS;
   public static final Block SPRUCE_PLANKS;
   public static final Block BIRCH_PLANKS;
   public static final Block JUNGLE_PLANKS;
   public static final Block ACACIA_PLANKS;
   public static final Block CHERRY_PLANKS;
   public static final Block DARK_OAK_PLANKS;
   public static final Block PALE_OAK_WOOD;
   public static final Block PALE_OAK_PLANKS;
   public static final Block MANGROVE_PLANKS;
   public static final Block BAMBOO_PLANKS;
   public static final Block BAMBOO_MOSAIC;
   public static final Block OAK_SAPLING;
   public static final Block SPRUCE_SAPLING;
   public static final Block BIRCH_SAPLING;
   public static final Block JUNGLE_SAPLING;
   public static final Block ACACIA_SAPLING;
   public static final Block CHERRY_SAPLING;
   public static final Block DARK_OAK_SAPLING;
   public static final Block PALE_OAK_SAPLING;
   public static final Block MANGROVE_PROPAGULE;
   public static final Block BEDROCK;
   public static final Block WATER;
   public static final Block LAVA;
   public static final Block SAND;
   public static final Block SUSPICIOUS_SAND;
   public static final Block RED_SAND;
   public static final Block GRAVEL;
   public static final Block SUSPICIOUS_GRAVEL;
   public static final Block GOLD_ORE;
   public static final Block DEEPSLATE_GOLD_ORE;
   public static final Block IRON_ORE;
   public static final Block DEEPSLATE_IRON_ORE;
   public static final Block COAL_ORE;
   public static final Block DEEPSLATE_COAL_ORE;
   public static final Block NETHER_GOLD_ORE;
   public static final Block OAK_LOG;
   public static final Block SPRUCE_LOG;
   public static final Block BIRCH_LOG;
   public static final Block JUNGLE_LOG;
   public static final Block ACACIA_LOG;
   public static final Block CHERRY_LOG;
   public static final Block DARK_OAK_LOG;
   public static final Block PALE_OAK_LOG;
   public static final Block MANGROVE_LOG;
   public static final Block MANGROVE_ROOTS;
   public static final Block MUDDY_MANGROVE_ROOTS;
   public static final Block BAMBOO_BLOCK;
   public static final Block STRIPPED_SPRUCE_LOG;
   public static final Block STRIPPED_BIRCH_LOG;
   public static final Block STRIPPED_JUNGLE_LOG;
   public static final Block STRIPPED_ACACIA_LOG;
   public static final Block STRIPPED_CHERRY_LOG;
   public static final Block STRIPPED_DARK_OAK_LOG;
   public static final Block STRIPPED_PALE_OAK_LOG;
   public static final Block STRIPPED_OAK_LOG;
   public static final Block STRIPPED_MANGROVE_LOG;
   public static final Block STRIPPED_BAMBOO_BLOCK;
   public static final Block OAK_WOOD;
   public static final Block SPRUCE_WOOD;
   public static final Block BIRCH_WOOD;
   public static final Block JUNGLE_WOOD;
   public static final Block ACACIA_WOOD;
   public static final Block CHERRY_WOOD;
   public static final Block DARK_OAK_WOOD;
   public static final Block MANGROVE_WOOD;
   public static final Block STRIPPED_OAK_WOOD;
   public static final Block STRIPPED_SPRUCE_WOOD;
   public static final Block STRIPPED_BIRCH_WOOD;
   public static final Block STRIPPED_JUNGLE_WOOD;
   public static final Block STRIPPED_ACACIA_WOOD;
   public static final Block STRIPPED_CHERRY_WOOD;
   public static final Block STRIPPED_DARK_OAK_WOOD;
   public static final Block STRIPPED_PALE_OAK_WOOD;
   public static final Block STRIPPED_MANGROVE_WOOD;
   public static final Block OAK_LEAVES;
   public static final Block SPRUCE_LEAVES;
   public static final Block BIRCH_LEAVES;
   public static final Block JUNGLE_LEAVES;
   public static final Block ACACIA_LEAVES;
   public static final Block CHERRY_LEAVES;
   public static final Block DARK_OAK_LEAVES;
   public static final Block PALE_OAK_LEAVES;
   public static final Block MANGROVE_LEAVES;
   public static final Block AZALEA_LEAVES;
   public static final Block FLOWERING_AZALEA_LEAVES;
   public static final Block SPONGE;
   public static final Block WET_SPONGE;
   public static final Block GLASS;
   public static final Block LAPIS_ORE;
   public static final Block DEEPSLATE_LAPIS_ORE;
   public static final Block LAPIS_BLOCK;
   public static final Block DISPENSER;
   public static final Block SANDSTONE;
   public static final Block CHISELED_SANDSTONE;
   public static final Block CUT_SANDSTONE;
   public static final Block NOTE_BLOCK;
   public static final Block WHITE_BED;
   public static final Block ORANGE_BED;
   public static final Block MAGENTA_BED;
   public static final Block LIGHT_BLUE_BED;
   public static final Block YELLOW_BED;
   public static final Block LIME_BED;
   public static final Block PINK_BED;
   public static final Block GRAY_BED;
   public static final Block LIGHT_GRAY_BED;
   public static final Block CYAN_BED;
   public static final Block PURPLE_BED;
   public static final Block BLUE_BED;
   public static final Block BROWN_BED;
   public static final Block GREEN_BED;
   public static final Block RED_BED;
   public static final Block BLACK_BED;
   public static final Block POWERED_RAIL;
   public static final Block DETECTOR_RAIL;
   public static final Block STICKY_PISTON;
   public static final Block COBWEB;
   public static final Block SHORT_GRASS;
   public static final Block FERN;
   public static final Block DEAD_BUSH;
   public static final Block SEAGRASS;
   public static final Block TALL_SEAGRASS;
   public static final Block PISTON;
   public static final Block PISTON_HEAD;
   public static final Block WHITE_WOOL;
   public static final Block ORANGE_WOOL;
   public static final Block MAGENTA_WOOL;
   public static final Block LIGHT_BLUE_WOOL;
   public static final Block YELLOW_WOOL;
   public static final Block LIME_WOOL;
   public static final Block PINK_WOOL;
   public static final Block GRAY_WOOL;
   public static final Block LIGHT_GRAY_WOOL;
   public static final Block CYAN_WOOL;
   public static final Block PURPLE_WOOL;
   public static final Block BLUE_WOOL;
   public static final Block BROWN_WOOL;
   public static final Block GREEN_WOOL;
   public static final Block RED_WOOL;
   public static final Block BLACK_WOOL;
   public static final Block MOVING_PISTON;
   public static final Block DANDELION;
   public static final Block TORCHFLOWER;
   public static final Block POPPY;
   public static final Block BLUE_ORCHID;
   public static final Block ALLIUM;
   public static final Block AZURE_BLUET;
   public static final Block RED_TULIP;
   public static final Block ORANGE_TULIP;
   public static final Block WHITE_TULIP;
   public static final Block PINK_TULIP;
   public static final Block OXEYE_DAISY;
   public static final Block CORNFLOWER;
   public static final Block WITHER_ROSE;
   public static final Block LILY_OF_THE_VALLEY;
   public static final Block BROWN_MUSHROOM;
   public static final Block RED_MUSHROOM;
   public static final Block GOLD_BLOCK;
   public static final Block IRON_BLOCK;
   public static final Block BRICKS;
   public static final Block TNT;
   public static final Block BOOKSHELF;
   public static final Block CHISELED_BOOKSHELF;
   public static final Block MOSSY_COBBLESTONE;
   public static final Block OBSIDIAN;
   public static final Block TORCH;
   public static final Block WALL_TORCH;
   public static final Block FIRE;
   public static final Block SOUL_FIRE;
   public static final Block SPAWNER;
   public static final Block CREAKING_HEART;
   public static final Block OAK_STAIRS;
   public static final Block CHEST;
   public static final Block REDSTONE_WIRE;
   public static final Block DIAMOND_ORE;
   public static final Block DEEPSLATE_DIAMOND_ORE;
   public static final Block DIAMOND_BLOCK;
   public static final Block CRAFTING_TABLE;
   public static final Block WHEAT;
   public static final Block FARMLAND;
   public static final Block FURNACE;
   public static final Block OAK_SIGN;
   public static final Block SPRUCE_SIGN;
   public static final Block BIRCH_SIGN;
   public static final Block ACACIA_SIGN;
   public static final Block CHERRY_SIGN;
   public static final Block JUNGLE_SIGN;
   public static final Block DARK_OAK_SIGN;
   public static final Block PALE_OAK_SIGN;
   public static final Block MANGROVE_SIGN;
   public static final Block BAMBOO_SIGN;
   public static final Block OAK_DOOR;
   public static final Block LADDER;
   public static final Block RAIL;
   public static final Block COBBLESTONE_STAIRS;
   public static final Block OAK_WALL_SIGN;
   public static final Block SPRUCE_WALL_SIGN;
   public static final Block BIRCH_WALL_SIGN;
   public static final Block ACACIA_WALL_SIGN;
   public static final Block CHERRY_WALL_SIGN;
   public static final Block JUNGLE_WALL_SIGN;
   public static final Block DARK_OAK_WALL_SIGN;
   public static final Block PALE_OAK_WALL_SIGN;
   public static final Block MANGROVE_WALL_SIGN;
   public static final Block BAMBOO_WALL_SIGN;
   public static final Block OAK_HANGING_SIGN;
   public static final Block SPRUCE_HANGING_SIGN;
   public static final Block BIRCH_HANGING_SIGN;
   public static final Block ACACIA_HANGING_SIGN;
   public static final Block CHERRY_HANGING_SIGN;
   public static final Block JUNGLE_HANGING_SIGN;
   public static final Block DARK_OAK_HANGING_SIGN;
   public static final Block PALE_OAK_HANGING_SIGN;
   public static final Block CRIMSON_HANGING_SIGN;
   public static final Block WARPED_HANGING_SIGN;
   public static final Block MANGROVE_HANGING_SIGN;
   public static final Block BAMBOO_HANGING_SIGN;
   public static final Block OAK_WALL_HANGING_SIGN;
   public static final Block SPRUCE_WALL_HANGING_SIGN;
   public static final Block BIRCH_WALL_HANGING_SIGN;
   public static final Block ACACIA_WALL_HANGING_SIGN;
   public static final Block CHERRY_WALL_HANGING_SIGN;
   public static final Block JUNGLE_WALL_HANGING_SIGN;
   public static final Block DARK_OAK_WALL_HANGING_SIGN;
   public static final Block PALE_OAK_WALL_HANGING_SIGN;
   public static final Block MANGROVE_WALL_HANGING_SIGN;
   public static final Block CRIMSON_WALL_HANGING_SIGN;
   public static final Block WARPED_WALL_HANGING_SIGN;
   public static final Block BAMBOO_WALL_HANGING_SIGN;
   public static final Block LEVER;
   public static final Block STONE_PRESSURE_PLATE;
   public static final Block IRON_DOOR;
   public static final Block OAK_PRESSURE_PLATE;
   public static final Block SPRUCE_PRESSURE_PLATE;
   public static final Block BIRCH_PRESSURE_PLATE;
   public static final Block JUNGLE_PRESSURE_PLATE;
   public static final Block ACACIA_PRESSURE_PLATE;
   public static final Block CHERRY_PRESSURE_PLATE;
   public static final Block DARK_OAK_PRESSURE_PLATE;
   public static final Block PALE_OAK_PRESSURE_PLATE;
   public static final Block MANGROVE_PRESSURE_PLATE;
   public static final Block BAMBOO_PRESSURE_PLATE;
   public static final Block REDSTONE_ORE;
   public static final Block DEEPSLATE_REDSTONE_ORE;
   public static final Block REDSTONE_TORCH;
   public static final Block REDSTONE_WALL_TORCH;
   public static final Block STONE_BUTTON;
   public static final Block SNOW;
   public static final Block ICE;
   public static final Block SNOW_BLOCK;
   public static final Block CACTUS;
   public static final Block CLAY;
   public static final Block SUGAR_CANE;
   public static final Block JUKEBOX;
   public static final Block OAK_FENCE;
   public static final Block NETHERRACK;
   public static final Block SOUL_SAND;
   public static final Block SOUL_SOIL;
   public static final Block BASALT;
   public static final Block POLISHED_BASALT;
   public static final Block SOUL_TORCH;
   public static final Block SOUL_WALL_TORCH;
   public static final Block GLOWSTONE;
   public static final Block NETHER_PORTAL;
   public static final Block CARVED_PUMPKIN;
   public static final Block JACK_O_LANTERN;
   public static final Block CAKE;
   public static final Block REPEATER;
   public static final Block WHITE_STAINED_GLASS;
   public static final Block ORANGE_STAINED_GLASS;
   public static final Block MAGENTA_STAINED_GLASS;
   public static final Block LIGHT_BLUE_STAINED_GLASS;
   public static final Block YELLOW_STAINED_GLASS;
   public static final Block LIME_STAINED_GLASS;
   public static final Block PINK_STAINED_GLASS;
   public static final Block GRAY_STAINED_GLASS;
   public static final Block LIGHT_GRAY_STAINED_GLASS;
   public static final Block CYAN_STAINED_GLASS;
   public static final Block PURPLE_STAINED_GLASS;
   public static final Block BLUE_STAINED_GLASS;
   public static final Block BROWN_STAINED_GLASS;
   public static final Block GREEN_STAINED_GLASS;
   public static final Block RED_STAINED_GLASS;
   public static final Block BLACK_STAINED_GLASS;
   public static final Block OAK_TRAPDOOR;
   public static final Block SPRUCE_TRAPDOOR;
   public static final Block BIRCH_TRAPDOOR;
   public static final Block JUNGLE_TRAPDOOR;
   public static final Block ACACIA_TRAPDOOR;
   public static final Block CHERRY_TRAPDOOR;
   public static final Block DARK_OAK_TRAPDOOR;
   public static final Block PALE_OAK_TRAPDOOR;
   public static final Block MANGROVE_TRAPDOOR;
   public static final Block BAMBOO_TRAPDOOR;
   public static final Block STONE_BRICKS;
   public static final Block MOSSY_STONE_BRICKS;
   public static final Block CRACKED_STONE_BRICKS;
   public static final Block CHISELED_STONE_BRICKS;
   public static final Block PACKED_MUD;
   public static final Block MUD_BRICKS;
   public static final Block INFESTED_STONE;
   public static final Block INFESTED_COBBLESTONE;
   public static final Block INFESTED_STONE_BRICKS;
   public static final Block INFESTED_MOSSY_STONE_BRICKS;
   public static final Block INFESTED_CRACKED_STONE_BRICKS;
   public static final Block INFESTED_CHISELED_STONE_BRICKS;
   public static final Block BROWN_MUSHROOM_BLOCK;
   public static final Block RED_MUSHROOM_BLOCK;
   public static final Block MUSHROOM_STEM;
   public static final Block IRON_BARS;
   public static final Block CHAIN;
   public static final Block GLASS_PANE;
   public static final Block PUMPKIN;
   public static final Block MELON;
   public static final Block ATTACHED_PUMPKIN_STEM;
   public static final Block ATTACHED_MELON_STEM;
   public static final Block PUMPKIN_STEM;
   public static final Block MELON_STEM;
   public static final Block VINE;
   public static final Block GLOW_LICHEN;
   public static final Block RESIN_CLUMP;
   public static final Block OAK_FENCE_GATE;
   public static final Block BRICK_STAIRS;
   public static final Block STONE_BRICK_STAIRS;
   public static final Block MUD_BRICK_STAIRS;
   public static final Block MYCELIUM;
   public static final Block LILY_PAD;
   public static final Block RESIN_BLOCK;
   public static final Block RESIN_BRICKS;
   public static final Block RESIN_BRICK_STAIRS;
   public static final Block RESIN_BRICK_SLAB;
   public static final Block RESIN_BRICK_WALL;
   public static final Block CHISELED_RESIN_BRICKS;
   public static final Block NETHER_BRICKS;
   public static final Block NETHER_BRICK_FENCE;
   public static final Block NETHER_BRICK_STAIRS;
   public static final Block NETHER_WART;
   public static final Block ENCHANTING_TABLE;
   public static final Block BREWING_STAND;
   public static final Block CAULDRON;
   public static final Block WATER_CAULDRON;
   public static final Block LAVA_CAULDRON;
   public static final Block POWDER_SNOW_CAULDRON;
   public static final Block END_PORTAL;
   public static final Block END_PORTAL_FRAME;
   public static final Block END_STONE;
   public static final Block DRAGON_EGG;
   public static final Block REDSTONE_LAMP;
   public static final Block COCOA;
   public static final Block SANDSTONE_STAIRS;
   public static final Block EMERALD_ORE;
   public static final Block DEEPSLATE_EMERALD_ORE;
   public static final Block ENDER_CHEST;
   public static final Block TRIPWIRE_HOOK;
   public static final Block TRIPWIRE;
   public static final Block EMERALD_BLOCK;
   public static final Block SPRUCE_STAIRS;
   public static final Block BIRCH_STAIRS;
   public static final Block JUNGLE_STAIRS;
   public static final Block COMMAND_BLOCK;
   public static final Block BEACON;
   public static final Block COBBLESTONE_WALL;
   public static final Block MOSSY_COBBLESTONE_WALL;
   public static final Block FLOWER_POT;
   public static final Block POTTED_TORCHFLOWER;
   public static final Block POTTED_OAK_SAPLING;
   public static final Block POTTED_SPRUCE_SAPLING;
   public static final Block POTTED_BIRCH_SAPLING;
   public static final Block POTTED_JUNGLE_SAPLING;
   public static final Block POTTED_ACACIA_SAPLING;
   public static final Block POTTED_CHERRY_SAPLING;
   public static final Block POTTED_DARK_OAK_SAPLING;
   public static final Block POTTED_PALE_OAK_SAPLING;
   public static final Block POTTED_MANGROVE_PROPAGULE;
   public static final Block POTTED_FERN;
   public static final Block POTTED_DANDELION;
   public static final Block POTTED_POPPY;
   public static final Block POTTED_BLUE_ORCHID;
   public static final Block POTTED_ALLIUM;
   public static final Block POTTED_AZURE_BLUET;
   public static final Block POTTED_RED_TULIP;
   public static final Block POTTED_ORANGE_TULIP;
   public static final Block POTTED_WHITE_TULIP;
   public static final Block POTTED_PINK_TULIP;
   public static final Block POTTED_OXEYE_DAISY;
   public static final Block POTTED_CORNFLOWER;
   public static final Block POTTED_LILY_OF_THE_VALLEY;
   public static final Block POTTED_WITHER_ROSE;
   public static final Block POTTED_RED_MUSHROOM;
   public static final Block POTTED_BROWN_MUSHROOM;
   public static final Block POTTED_DEAD_BUSH;
   public static final Block POTTED_CACTUS;
   public static final Block CARROTS;
   public static final Block POTATOES;
   public static final Block OAK_BUTTON;
   public static final Block SPRUCE_BUTTON;
   public static final Block BIRCH_BUTTON;
   public static final Block JUNGLE_BUTTON;
   public static final Block ACACIA_BUTTON;
   public static final Block CHERRY_BUTTON;
   public static final Block DARK_OAK_BUTTON;
   public static final Block PALE_OAK_BUTTON;
   public static final Block MANGROVE_BUTTON;
   public static final Block BAMBOO_BUTTON;
   public static final Block SKELETON_SKULL;
   public static final Block SKELETON_WALL_SKULL;
   public static final Block WITHER_SKELETON_SKULL;
   public static final Block WITHER_SKELETON_WALL_SKULL;
   public static final Block ZOMBIE_HEAD;
   public static final Block ZOMBIE_WALL_HEAD;
   public static final Block PLAYER_HEAD;
   public static final Block PLAYER_WALL_HEAD;
   public static final Block CREEPER_HEAD;
   public static final Block CREEPER_WALL_HEAD;
   public static final Block DRAGON_HEAD;
   public static final Block DRAGON_WALL_HEAD;
   public static final Block PIGLIN_HEAD;
   public static final Block PIGLIN_WALL_HEAD;
   public static final Block ANVIL;
   public static final Block CHIPPED_ANVIL;
   public static final Block DAMAGED_ANVIL;
   public static final Block TRAPPED_CHEST;
   public static final Block LIGHT_WEIGHTED_PRESSURE_PLATE;
   public static final Block HEAVY_WEIGHTED_PRESSURE_PLATE;
   public static final Block COMPARATOR;
   public static final Block DAYLIGHT_DETECTOR;
   public static final Block REDSTONE_BLOCK;
   public static final Block NETHER_QUARTZ_ORE;
   public static final Block HOPPER;
   public static final Block QUARTZ_BLOCK;
   public static final Block CHISELED_QUARTZ_BLOCK;
   public static final Block QUARTZ_PILLAR;
   public static final Block QUARTZ_STAIRS;
   public static final Block ACTIVATOR_RAIL;
   public static final Block DROPPER;
   public static final Block WHITE_TERRACOTTA;
   public static final Block ORANGE_TERRACOTTA;
   public static final Block MAGENTA_TERRACOTTA;
   public static final Block LIGHT_BLUE_TERRACOTTA;
   public static final Block YELLOW_TERRACOTTA;
   public static final Block LIME_TERRACOTTA;
   public static final Block PINK_TERRACOTTA;
   public static final Block GRAY_TERRACOTTA;
   public static final Block LIGHT_GRAY_TERRACOTTA;
   public static final Block CYAN_TERRACOTTA;
   public static final Block PURPLE_TERRACOTTA;
   public static final Block BLUE_TERRACOTTA;
   public static final Block BROWN_TERRACOTTA;
   public static final Block GREEN_TERRACOTTA;
   public static final Block RED_TERRACOTTA;
   public static final Block BLACK_TERRACOTTA;
   public static final Block WHITE_STAINED_GLASS_PANE;
   public static final Block ORANGE_STAINED_GLASS_PANE;
   public static final Block MAGENTA_STAINED_GLASS_PANE;
   public static final Block LIGHT_BLUE_STAINED_GLASS_PANE;
   public static final Block YELLOW_STAINED_GLASS_PANE;
   public static final Block LIME_STAINED_GLASS_PANE;
   public static final Block PINK_STAINED_GLASS_PANE;
   public static final Block GRAY_STAINED_GLASS_PANE;
   public static final Block LIGHT_GRAY_STAINED_GLASS_PANE;
   public static final Block CYAN_STAINED_GLASS_PANE;
   public static final Block PURPLE_STAINED_GLASS_PANE;
   public static final Block BLUE_STAINED_GLASS_PANE;
   public static final Block BROWN_STAINED_GLASS_PANE;
   public static final Block GREEN_STAINED_GLASS_PANE;
   public static final Block RED_STAINED_GLASS_PANE;
   public static final Block BLACK_STAINED_GLASS_PANE;
   public static final Block ACACIA_STAIRS;
   public static final Block CHERRY_STAIRS;
   public static final Block DARK_OAK_STAIRS;
   public static final Block PALE_OAK_STAIRS;
   public static final Block MANGROVE_STAIRS;
   public static final Block BAMBOO_STAIRS;
   public static final Block BAMBOO_MOSAIC_STAIRS;
   public static final Block SLIME_BLOCK;
   public static final Block BARRIER;
   public static final Block LIGHT;
   public static final Block IRON_TRAPDOOR;
   public static final Block PRISMARINE;
   public static final Block PRISMARINE_BRICKS;
   public static final Block DARK_PRISMARINE;
   public static final Block PRISMARINE_STAIRS;
   public static final Block PRISMARINE_BRICK_STAIRS;
   public static final Block DARK_PRISMARINE_STAIRS;
   public static final Block PRISMARINE_SLAB;
   public static final Block PRISMARINE_BRICK_SLAB;
   public static final Block DARK_PRISMARINE_SLAB;
   public static final Block SEA_LANTERN;
   public static final Block HAY_BLOCK;
   public static final Block WHITE_CARPET;
   public static final Block ORANGE_CARPET;
   public static final Block MAGENTA_CARPET;
   public static final Block LIGHT_BLUE_CARPET;
   public static final Block YELLOW_CARPET;
   public static final Block LIME_CARPET;
   public static final Block PINK_CARPET;
   public static final Block GRAY_CARPET;
   public static final Block LIGHT_GRAY_CARPET;
   public static final Block CYAN_CARPET;
   public static final Block PURPLE_CARPET;
   public static final Block BLUE_CARPET;
   public static final Block BROWN_CARPET;
   public static final Block GREEN_CARPET;
   public static final Block RED_CARPET;
   public static final Block BLACK_CARPET;
   public static final Block TERRACOTTA;
   public static final Block COAL_BLOCK;
   public static final Block PACKED_ICE;
   public static final Block SUNFLOWER;
   public static final Block LILAC;
   public static final Block ROSE_BUSH;
   public static final Block PEONY;
   public static final Block TALL_GRASS;
   public static final Block LARGE_FERN;
   public static final Block WHITE_BANNER;
   public static final Block ORANGE_BANNER;
   public static final Block MAGENTA_BANNER;
   public static final Block LIGHT_BLUE_BANNER;
   public static final Block YELLOW_BANNER;
   public static final Block LIME_BANNER;
   public static final Block PINK_BANNER;
   public static final Block GRAY_BANNER;
   public static final Block LIGHT_GRAY_BANNER;
   public static final Block CYAN_BANNER;
   public static final Block PURPLE_BANNER;
   public static final Block BLUE_BANNER;
   public static final Block BROWN_BANNER;
   public static final Block GREEN_BANNER;
   public static final Block RED_BANNER;
   public static final Block BLACK_BANNER;
   public static final Block WHITE_WALL_BANNER;
   public static final Block ORANGE_WALL_BANNER;
   public static final Block MAGENTA_WALL_BANNER;
   public static final Block LIGHT_BLUE_WALL_BANNER;
   public static final Block YELLOW_WALL_BANNER;
   public static final Block LIME_WALL_BANNER;
   public static final Block PINK_WALL_BANNER;
   public static final Block GRAY_WALL_BANNER;
   public static final Block LIGHT_GRAY_WALL_BANNER;
   public static final Block CYAN_WALL_BANNER;
   public static final Block PURPLE_WALL_BANNER;
   public static final Block BLUE_WALL_BANNER;
   public static final Block BROWN_WALL_BANNER;
   public static final Block GREEN_WALL_BANNER;
   public static final Block RED_WALL_BANNER;
   public static final Block BLACK_WALL_BANNER;
   public static final Block RED_SANDSTONE;
   public static final Block CHISELED_RED_SANDSTONE;
   public static final Block CUT_RED_SANDSTONE;
   public static final Block RED_SANDSTONE_STAIRS;
   public static final Block OAK_SLAB;
   public static final Block SPRUCE_SLAB;
   public static final Block BIRCH_SLAB;
   public static final Block JUNGLE_SLAB;
   public static final Block ACACIA_SLAB;
   public static final Block CHERRY_SLAB;
   public static final Block DARK_OAK_SLAB;
   public static final Block PALE_OAK_SLAB;
   public static final Block MANGROVE_SLAB;
   public static final Block BAMBOO_SLAB;
   public static final Block BAMBOO_MOSAIC_SLAB;
   public static final Block STONE_SLAB;
   public static final Block SMOOTH_STONE_SLAB;
   public static final Block SANDSTONE_SLAB;
   public static final Block CUT_SANDSTONE_SLAB;
   public static final Block PETRIFIED_OAK_SLAB;
   public static final Block COBBLESTONE_SLAB;
   public static final Block BRICK_SLAB;
   public static final Block STONE_BRICK_SLAB;
   public static final Block MUD_BRICK_SLAB;
   public static final Block NETHER_BRICK_SLAB;
   public static final Block QUARTZ_SLAB;
   public static final Block RED_SANDSTONE_SLAB;
   public static final Block CUT_RED_SANDSTONE_SLAB;
   public static final Block PURPUR_SLAB;
   public static final Block SMOOTH_STONE;
   public static final Block SMOOTH_SANDSTONE;
   public static final Block SMOOTH_QUARTZ;
   public static final Block SMOOTH_RED_SANDSTONE;
   public static final Block SPRUCE_FENCE_GATE;
   public static final Block BIRCH_FENCE_GATE;
   public static final Block JUNGLE_FENCE_GATE;
   public static final Block ACACIA_FENCE_GATE;
   public static final Block CHERRY_FENCE_GATE;
   public static final Block DARK_OAK_FENCE_GATE;
   public static final Block PALE_OAK_FENCE_GATE;
   public static final Block MANGROVE_FENCE_GATE;
   public static final Block BAMBOO_FENCE_GATE;
   public static final Block SPRUCE_FENCE;
   public static final Block BIRCH_FENCE;
   public static final Block JUNGLE_FENCE;
   public static final Block ACACIA_FENCE;
   public static final Block CHERRY_FENCE;
   public static final Block DARK_OAK_FENCE;
   public static final Block PALE_OAK_FENCE;
   public static final Block MANGROVE_FENCE;
   public static final Block BAMBOO_FENCE;
   public static final Block SPRUCE_DOOR;
   public static final Block BIRCH_DOOR;
   public static final Block JUNGLE_DOOR;
   public static final Block ACACIA_DOOR;
   public static final Block CHERRY_DOOR;
   public static final Block DARK_OAK_DOOR;
   public static final Block PALE_OAK_DOOR;
   public static final Block MANGROVE_DOOR;
   public static final Block BAMBOO_DOOR;
   public static final Block END_ROD;
   public static final Block CHORUS_PLANT;
   public static final Block CHORUS_FLOWER;
   public static final Block PURPUR_BLOCK;
   public static final Block PURPUR_PILLAR;
   public static final Block PURPUR_STAIRS;
   public static final Block END_STONE_BRICKS;
   public static final Block TORCHFLOWER_CROP;
   public static final Block PITCHER_CROP;
   public static final Block PITCHER_PLANT;
   public static final Block BEETROOTS;
   public static final Block DIRT_PATH;
   public static final Block END_GATEWAY;
   public static final Block REPEATING_COMMAND_BLOCK;
   public static final Block CHAIN_COMMAND_BLOCK;
   public static final Block FROSTED_ICE;
   public static final Block MAGMA_BLOCK;
   public static final Block NETHER_WART_BLOCK;
   public static final Block RED_NETHER_BRICKS;
   public static final Block BONE_BLOCK;
   public static final Block STRUCTURE_VOID;
   public static final Block OBSERVER;
   public static final Block SHULKER_BOX;
   public static final Block WHITE_SHULKER_BOX;
   public static final Block ORANGE_SHULKER_BOX;
   public static final Block MAGENTA_SHULKER_BOX;
   public static final Block LIGHT_BLUE_SHULKER_BOX;
   public static final Block YELLOW_SHULKER_BOX;
   public static final Block LIME_SHULKER_BOX;
   public static final Block PINK_SHULKER_BOX;
   public static final Block GRAY_SHULKER_BOX;
   public static final Block LIGHT_GRAY_SHULKER_BOX;
   public static final Block CYAN_SHULKER_BOX;
   public static final Block PURPLE_SHULKER_BOX;
   public static final Block BLUE_SHULKER_BOX;
   public static final Block BROWN_SHULKER_BOX;
   public static final Block GREEN_SHULKER_BOX;
   public static final Block RED_SHULKER_BOX;
   public static final Block BLACK_SHULKER_BOX;
   public static final Block WHITE_GLAZED_TERRACOTTA;
   public static final Block ORANGE_GLAZED_TERRACOTTA;
   public static final Block MAGENTA_GLAZED_TERRACOTTA;
   public static final Block LIGHT_BLUE_GLAZED_TERRACOTTA;
   public static final Block YELLOW_GLAZED_TERRACOTTA;
   public static final Block LIME_GLAZED_TERRACOTTA;
   public static final Block PINK_GLAZED_TERRACOTTA;
   public static final Block GRAY_GLAZED_TERRACOTTA;
   public static final Block LIGHT_GRAY_GLAZED_TERRACOTTA;
   public static final Block CYAN_GLAZED_TERRACOTTA;
   public static final Block PURPLE_GLAZED_TERRACOTTA;
   public static final Block BLUE_GLAZED_TERRACOTTA;
   public static final Block BROWN_GLAZED_TERRACOTTA;
   public static final Block GREEN_GLAZED_TERRACOTTA;
   public static final Block RED_GLAZED_TERRACOTTA;
   public static final Block BLACK_GLAZED_TERRACOTTA;
   public static final Block WHITE_CONCRETE;
   public static final Block ORANGE_CONCRETE;
   public static final Block MAGENTA_CONCRETE;
   public static final Block LIGHT_BLUE_CONCRETE;
   public static final Block YELLOW_CONCRETE;
   public static final Block LIME_CONCRETE;
   public static final Block PINK_CONCRETE;
   public static final Block GRAY_CONCRETE;
   public static final Block LIGHT_GRAY_CONCRETE;
   public static final Block CYAN_CONCRETE;
   public static final Block PURPLE_CONCRETE;
   public static final Block BLUE_CONCRETE;
   public static final Block BROWN_CONCRETE;
   public static final Block GREEN_CONCRETE;
   public static final Block RED_CONCRETE;
   public static final Block BLACK_CONCRETE;
   public static final Block WHITE_CONCRETE_POWDER;
   public static final Block ORANGE_CONCRETE_POWDER;
   public static final Block MAGENTA_CONCRETE_POWDER;
   public static final Block LIGHT_BLUE_CONCRETE_POWDER;
   public static final Block YELLOW_CONCRETE_POWDER;
   public static final Block LIME_CONCRETE_POWDER;
   public static final Block PINK_CONCRETE_POWDER;
   public static final Block GRAY_CONCRETE_POWDER;
   public static final Block LIGHT_GRAY_CONCRETE_POWDER;
   public static final Block CYAN_CONCRETE_POWDER;
   public static final Block PURPLE_CONCRETE_POWDER;
   public static final Block BLUE_CONCRETE_POWDER;
   public static final Block BROWN_CONCRETE_POWDER;
   public static final Block GREEN_CONCRETE_POWDER;
   public static final Block RED_CONCRETE_POWDER;
   public static final Block BLACK_CONCRETE_POWDER;
   public static final Block KELP;
   public static final Block KELP_PLANT;
   public static final Block DRIED_KELP_BLOCK;
   public static final Block TURTLE_EGG;
   public static final Block SNIFFER_EGG;
   public static final Block DEAD_TUBE_CORAL_BLOCK;
   public static final Block DEAD_BRAIN_CORAL_BLOCK;
   public static final Block DEAD_BUBBLE_CORAL_BLOCK;
   public static final Block DEAD_FIRE_CORAL_BLOCK;
   public static final Block DEAD_HORN_CORAL_BLOCK;
   public static final Block TUBE_CORAL_BLOCK;
   public static final Block BRAIN_CORAL_BLOCK;
   public static final Block BUBBLE_CORAL_BLOCK;
   public static final Block FIRE_CORAL_BLOCK;
   public static final Block HORN_CORAL_BLOCK;
   public static final Block DEAD_TUBE_CORAL;
   public static final Block DEAD_BRAIN_CORAL;
   public static final Block DEAD_BUBBLE_CORAL;
   public static final Block DEAD_FIRE_CORAL;
   public static final Block DEAD_HORN_CORAL;
   public static final Block TUBE_CORAL;
   public static final Block BRAIN_CORAL;
   public static final Block BUBBLE_CORAL;
   public static final Block FIRE_CORAL;
   public static final Block HORN_CORAL;
   public static final Block DEAD_TUBE_CORAL_FAN;
   public static final Block DEAD_BRAIN_CORAL_FAN;
   public static final Block DEAD_BUBBLE_CORAL_FAN;
   public static final Block DEAD_FIRE_CORAL_FAN;
   public static final Block DEAD_HORN_CORAL_FAN;
   public static final Block TUBE_CORAL_FAN;
   public static final Block BRAIN_CORAL_FAN;
   public static final Block BUBBLE_CORAL_FAN;
   public static final Block FIRE_CORAL_FAN;
   public static final Block HORN_CORAL_FAN;
   public static final Block DEAD_TUBE_CORAL_WALL_FAN;
   public static final Block DEAD_BRAIN_CORAL_WALL_FAN;
   public static final Block DEAD_BUBBLE_CORAL_WALL_FAN;
   public static final Block DEAD_FIRE_CORAL_WALL_FAN;
   public static final Block DEAD_HORN_CORAL_WALL_FAN;
   public static final Block TUBE_CORAL_WALL_FAN;
   public static final Block BRAIN_CORAL_WALL_FAN;
   public static final Block BUBBLE_CORAL_WALL_FAN;
   public static final Block FIRE_CORAL_WALL_FAN;
   public static final Block HORN_CORAL_WALL_FAN;
   public static final Block SEA_PICKLE;
   public static final Block BLUE_ICE;
   public static final Block CONDUIT;
   public static final Block BAMBOO_SAPLING;
   public static final Block BAMBOO;
   public static final Block POTTED_BAMBOO;
   public static final Block VOID_AIR;
   public static final Block CAVE_AIR;
   public static final Block BUBBLE_COLUMN;
   public static final Block POLISHED_GRANITE_STAIRS;
   public static final Block SMOOTH_RED_SANDSTONE_STAIRS;
   public static final Block MOSSY_STONE_BRICK_STAIRS;
   public static final Block POLISHED_DIORITE_STAIRS;
   public static final Block MOSSY_COBBLESTONE_STAIRS;
   public static final Block END_STONE_BRICK_STAIRS;
   public static final Block STONE_STAIRS;
   public static final Block SMOOTH_SANDSTONE_STAIRS;
   public static final Block SMOOTH_QUARTZ_STAIRS;
   public static final Block GRANITE_STAIRS;
   public static final Block ANDESITE_STAIRS;
   public static final Block RED_NETHER_BRICK_STAIRS;
   public static final Block POLISHED_ANDESITE_STAIRS;
   public static final Block DIORITE_STAIRS;
   public static final Block POLISHED_GRANITE_SLAB;
   public static final Block SMOOTH_RED_SANDSTONE_SLAB;
   public static final Block MOSSY_STONE_BRICK_SLAB;
   public static final Block POLISHED_DIORITE_SLAB;
   public static final Block MOSSY_COBBLESTONE_SLAB;
   public static final Block END_STONE_BRICK_SLAB;
   public static final Block SMOOTH_SANDSTONE_SLAB;
   public static final Block SMOOTH_QUARTZ_SLAB;
   public static final Block GRANITE_SLAB;
   public static final Block ANDESITE_SLAB;
   public static final Block RED_NETHER_BRICK_SLAB;
   public static final Block POLISHED_ANDESITE_SLAB;
   public static final Block DIORITE_SLAB;
   public static final Block BRICK_WALL;
   public static final Block PRISMARINE_WALL;
   public static final Block RED_SANDSTONE_WALL;
   public static final Block MOSSY_STONE_BRICK_WALL;
   public static final Block GRANITE_WALL;
   public static final Block STONE_BRICK_WALL;
   public static final Block MUD_BRICK_WALL;
   public static final Block NETHER_BRICK_WALL;
   public static final Block ANDESITE_WALL;
   public static final Block RED_NETHER_BRICK_WALL;
   public static final Block SANDSTONE_WALL;
   public static final Block END_STONE_BRICK_WALL;
   public static final Block DIORITE_WALL;
   public static final Block SCAFFOLDING;
   public static final Block LOOM;
   public static final Block BARREL;
   public static final Block SMOKER;
   public static final Block BLAST_FURNACE;
   public static final Block CARTOGRAPHY_TABLE;
   public static final Block FLETCHING_TABLE;
   public static final Block GRINDSTONE;
   public static final Block LECTERN;
   public static final Block SMITHING_TABLE;
   public static final Block STONECUTTER;
   public static final Block BELL;
   public static final Block LANTERN;
   public static final Block SOUL_LANTERN;
   public static final Block CAMPFIRE;
   public static final Block SOUL_CAMPFIRE;
   public static final Block SWEET_BERRY_BUSH;
   public static final Block WARPED_STEM;
   public static final Block STRIPPED_WARPED_STEM;
   public static final Block WARPED_HYPHAE;
   public static final Block STRIPPED_WARPED_HYPHAE;
   public static final Block WARPED_NYLIUM;
   public static final Block WARPED_FUNGUS;
   public static final Block WARPED_WART_BLOCK;
   public static final Block WARPED_ROOTS;
   public static final Block NETHER_SPROUTS;
   public static final Block CRIMSON_STEM;
   public static final Block STRIPPED_CRIMSON_STEM;
   public static final Block CRIMSON_HYPHAE;
   public static final Block STRIPPED_CRIMSON_HYPHAE;
   public static final Block CRIMSON_NYLIUM;
   public static final Block CRIMSON_FUNGUS;
   public static final Block SHROOMLIGHT;
   public static final Block WEEPING_VINES;
   public static final Block WEEPING_VINES_PLANT;
   public static final Block TWISTING_VINES;
   public static final Block TWISTING_VINES_PLANT;
   public static final Block CRIMSON_ROOTS;
   public static final Block CRIMSON_PLANKS;
   public static final Block WARPED_PLANKS;
   public static final Block CRIMSON_SLAB;
   public static final Block WARPED_SLAB;
   public static final Block CRIMSON_PRESSURE_PLATE;
   public static final Block WARPED_PRESSURE_PLATE;
   public static final Block CRIMSON_FENCE;
   public static final Block WARPED_FENCE;
   public static final Block CRIMSON_TRAPDOOR;
   public static final Block WARPED_TRAPDOOR;
   public static final Block CRIMSON_FENCE_GATE;
   public static final Block WARPED_FENCE_GATE;
   public static final Block CRIMSON_STAIRS;
   public static final Block WARPED_STAIRS;
   public static final Block CRIMSON_BUTTON;
   public static final Block WARPED_BUTTON;
   public static final Block CRIMSON_DOOR;
   public static final Block WARPED_DOOR;
   public static final Block CRIMSON_SIGN;
   public static final Block WARPED_SIGN;
   public static final Block CRIMSON_WALL_SIGN;
   public static final Block WARPED_WALL_SIGN;
   public static final Block STRUCTURE_BLOCK;
   public static final Block JIGSAW;
   public static final Block COMPOSTER;
   public static final Block TARGET;
   public static final Block BEE_NEST;
   public static final Block BEEHIVE;
   public static final Block HONEY_BLOCK;
   public static final Block HONEYCOMB_BLOCK;
   public static final Block NETHERITE_BLOCK;
   public static final Block ANCIENT_DEBRIS;
   public static final Block CRYING_OBSIDIAN;
   public static final Block RESPAWN_ANCHOR;
   public static final Block POTTED_CRIMSON_FUNGUS;
   public static final Block POTTED_WARPED_FUNGUS;
   public static final Block POTTED_CRIMSON_ROOTS;
   public static final Block POTTED_WARPED_ROOTS;
   public static final Block LODESTONE;
   public static final Block BLACKSTONE;
   public static final Block BLACKSTONE_STAIRS;
   public static final Block BLACKSTONE_WALL;
   public static final Block BLACKSTONE_SLAB;
   public static final Block POLISHED_BLACKSTONE;
   public static final Block POLISHED_BLACKSTONE_BRICKS;
   public static final Block CRACKED_POLISHED_BLACKSTONE_BRICKS;
   public static final Block CHISELED_POLISHED_BLACKSTONE;
   public static final Block POLISHED_BLACKSTONE_BRICK_SLAB;
   public static final Block POLISHED_BLACKSTONE_BRICK_STAIRS;
   public static final Block POLISHED_BLACKSTONE_BRICK_WALL;
   public static final Block GILDED_BLACKSTONE;
   public static final Block POLISHED_BLACKSTONE_STAIRS;
   public static final Block POLISHED_BLACKSTONE_SLAB;
   public static final Block POLISHED_BLACKSTONE_PRESSURE_PLATE;
   public static final Block POLISHED_BLACKSTONE_BUTTON;
   public static final Block POLISHED_BLACKSTONE_WALL;
   public static final Block CHISELED_NETHER_BRICKS;
   public static final Block CRACKED_NETHER_BRICKS;
   public static final Block QUARTZ_BRICKS;
   public static final Block CANDLE;
   public static final Block WHITE_CANDLE;
   public static final Block ORANGE_CANDLE;
   public static final Block MAGENTA_CANDLE;
   public static final Block LIGHT_BLUE_CANDLE;
   public static final Block YELLOW_CANDLE;
   public static final Block LIME_CANDLE;
   public static final Block PINK_CANDLE;
   public static final Block GRAY_CANDLE;
   public static final Block LIGHT_GRAY_CANDLE;
   public static final Block CYAN_CANDLE;
   public static final Block PURPLE_CANDLE;
   public static final Block BLUE_CANDLE;
   public static final Block BROWN_CANDLE;
   public static final Block GREEN_CANDLE;
   public static final Block RED_CANDLE;
   public static final Block BLACK_CANDLE;
   public static final Block CANDLE_CAKE;
   public static final Block WHITE_CANDLE_CAKE;
   public static final Block ORANGE_CANDLE_CAKE;
   public static final Block MAGENTA_CANDLE_CAKE;
   public static final Block LIGHT_BLUE_CANDLE_CAKE;
   public static final Block YELLOW_CANDLE_CAKE;
   public static final Block LIME_CANDLE_CAKE;
   public static final Block PINK_CANDLE_CAKE;
   public static final Block GRAY_CANDLE_CAKE;
   public static final Block LIGHT_GRAY_CANDLE_CAKE;
   public static final Block CYAN_CANDLE_CAKE;
   public static final Block PURPLE_CANDLE_CAKE;
   public static final Block BLUE_CANDLE_CAKE;
   public static final Block BROWN_CANDLE_CAKE;
   public static final Block GREEN_CANDLE_CAKE;
   public static final Block RED_CANDLE_CAKE;
   public static final Block BLACK_CANDLE_CAKE;
   public static final Block AMETHYST_BLOCK;
   public static final Block BUDDING_AMETHYST;
   public static final Block AMETHYST_CLUSTER;
   public static final Block LARGE_AMETHYST_BUD;
   public static final Block MEDIUM_AMETHYST_BUD;
   public static final Block SMALL_AMETHYST_BUD;
   public static final Block TUFF;
   public static final Block TUFF_SLAB;
   public static final Block TUFF_STAIRS;
   public static final Block TUFF_WALL;
   public static final Block POLISHED_TUFF;
   public static final Block POLISHED_TUFF_SLAB;
   public static final Block POLISHED_TUFF_STAIRS;
   public static final Block POLISHED_TUFF_WALL;
   public static final Block CHISELED_TUFF;
   public static final Block TUFF_BRICKS;
   public static final Block TUFF_BRICK_SLAB;
   public static final Block TUFF_BRICK_STAIRS;
   public static final Block TUFF_BRICK_WALL;
   public static final Block CHISELED_TUFF_BRICKS;
   public static final Block CALCITE;
   public static final Block TINTED_GLASS;
   public static final Block POWDER_SNOW;
   public static final Block SCULK_SENSOR;
   public static final Block CALIBRATED_SCULK_SENSOR;
   public static final Block SCULK;
   public static final Block SCULK_VEIN;
   public static final Block SCULK_CATALYST;
   public static final Block SCULK_SHRIEKER;
   public static final Block COPPER_BLOCK;
   public static final Block EXPOSED_COPPER;
   public static final Block WEATHERED_COPPER;
   public static final Block OXIDIZED_COPPER;
   public static final Block COPPER_ORE;
   public static final Block DEEPSLATE_COPPER_ORE;
   public static final Block OXIDIZED_CUT_COPPER;
   public static final Block WEATHERED_CUT_COPPER;
   public static final Block EXPOSED_CUT_COPPER;
   public static final Block CUT_COPPER;
   public static final Block OXIDIZED_CHISELED_COPPER;
   public static final Block WEATHERED_CHISELED_COPPER;
   public static final Block EXPOSED_CHISELED_COPPER;
   public static final Block CHISELED_COPPER;
   public static final Block WAXED_OXIDIZED_CHISELED_COPPER;
   public static final Block WAXED_WEATHERED_CHISELED_COPPER;
   public static final Block WAXED_EXPOSED_CHISELED_COPPER;
   public static final Block WAXED_CHISELED_COPPER;
   public static final Block OXIDIZED_CUT_COPPER_STAIRS;
   public static final Block WEATHERED_CUT_COPPER_STAIRS;
   public static final Block EXPOSED_CUT_COPPER_STAIRS;
   public static final Block CUT_COPPER_STAIRS;
   public static final Block OXIDIZED_CUT_COPPER_SLAB;
   public static final Block WEATHERED_CUT_COPPER_SLAB;
   public static final Block EXPOSED_CUT_COPPER_SLAB;
   public static final Block CUT_COPPER_SLAB;
   public static final Block WAXED_COPPER_BLOCK;
   public static final Block WAXED_WEATHERED_COPPER;
   public static final Block WAXED_EXPOSED_COPPER;
   public static final Block WAXED_OXIDIZED_COPPER;
   public static final Block WAXED_OXIDIZED_CUT_COPPER;
   public static final Block WAXED_WEATHERED_CUT_COPPER;
   public static final Block WAXED_EXPOSED_CUT_COPPER;
   public static final Block WAXED_CUT_COPPER;
   public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS;
   public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS;
   public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS;
   public static final Block WAXED_CUT_COPPER_STAIRS;
   public static final Block WAXED_OXIDIZED_CUT_COPPER_SLAB;
   public static final Block WAXED_WEATHERED_CUT_COPPER_SLAB;
   public static final Block WAXED_EXPOSED_CUT_COPPER_SLAB;
   public static final Block WAXED_CUT_COPPER_SLAB;
   public static final Block COPPER_DOOR;
   public static final Block EXPOSED_COPPER_DOOR;
   public static final Block OXIDIZED_COPPER_DOOR;
   public static final Block WEATHERED_COPPER_DOOR;
   public static final Block WAXED_COPPER_DOOR;
   public static final Block WAXED_EXPOSED_COPPER_DOOR;
   public static final Block WAXED_OXIDIZED_COPPER_DOOR;
   public static final Block WAXED_WEATHERED_COPPER_DOOR;
   public static final Block COPPER_TRAPDOOR;
   public static final Block EXPOSED_COPPER_TRAPDOOR;
   public static final Block OXIDIZED_COPPER_TRAPDOOR;
   public static final Block WEATHERED_COPPER_TRAPDOOR;
   public static final Block WAXED_COPPER_TRAPDOOR;
   public static final Block WAXED_EXPOSED_COPPER_TRAPDOOR;
   public static final Block WAXED_OXIDIZED_COPPER_TRAPDOOR;
   public static final Block WAXED_WEATHERED_COPPER_TRAPDOOR;
   public static final Block COPPER_GRATE;
   public static final Block EXPOSED_COPPER_GRATE;
   public static final Block WEATHERED_COPPER_GRATE;
   public static final Block OXIDIZED_COPPER_GRATE;
   public static final Block WAXED_COPPER_GRATE;
   public static final Block WAXED_EXPOSED_COPPER_GRATE;
   public static final Block WAXED_WEATHERED_COPPER_GRATE;
   public static final Block WAXED_OXIDIZED_COPPER_GRATE;
   public static final Block COPPER_BULB;
   public static final Block EXPOSED_COPPER_BULB;
   public static final Block WEATHERED_COPPER_BULB;
   public static final Block OXIDIZED_COPPER_BULB;
   public static final Block WAXED_COPPER_BULB;
   public static final Block WAXED_EXPOSED_COPPER_BULB;
   public static final Block WAXED_WEATHERED_COPPER_BULB;
   public static final Block WAXED_OXIDIZED_COPPER_BULB;
   public static final Block LIGHTNING_ROD;
   public static final Block POINTED_DRIPSTONE;
   public static final Block DRIPSTONE_BLOCK;
   public static final Block CAVE_VINES;
   public static final Block CAVE_VINES_PLANT;
   public static final Block SPORE_BLOSSOM;
   public static final Block AZALEA;
   public static final Block FLOWERING_AZALEA;
   public static final Block MOSS_CARPET;
   public static final Block PINK_PETALS;
   public static final Block MOSS_BLOCK;
   public static final Block BIG_DRIPLEAF;
   public static final Block BIG_DRIPLEAF_STEM;
   public static final Block SMALL_DRIPLEAF;
   public static final Block HANGING_ROOTS;
   public static final Block ROOTED_DIRT;
   public static final Block MUD;
   public static final Block DEEPSLATE;
   public static final Block COBBLED_DEEPSLATE;
   public static final Block COBBLED_DEEPSLATE_STAIRS;
   public static final Block COBBLED_DEEPSLATE_SLAB;
   public static final Block COBBLED_DEEPSLATE_WALL;
   public static final Block POLISHED_DEEPSLATE;
   public static final Block POLISHED_DEEPSLATE_STAIRS;
   public static final Block POLISHED_DEEPSLATE_SLAB;
   public static final Block POLISHED_DEEPSLATE_WALL;
   public static final Block DEEPSLATE_TILES;
   public static final Block DEEPSLATE_TILE_STAIRS;
   public static final Block DEEPSLATE_TILE_SLAB;
   public static final Block DEEPSLATE_TILE_WALL;
   public static final Block DEEPSLATE_BRICKS;
   public static final Block DEEPSLATE_BRICK_STAIRS;
   public static final Block DEEPSLATE_BRICK_SLAB;
   public static final Block DEEPSLATE_BRICK_WALL;
   public static final Block CHISELED_DEEPSLATE;
   public static final Block CRACKED_DEEPSLATE_BRICKS;
   public static final Block CRACKED_DEEPSLATE_TILES;
   public static final Block INFESTED_DEEPSLATE;
   public static final Block SMOOTH_BASALT;
   public static final Block RAW_IRON_BLOCK;
   public static final Block RAW_COPPER_BLOCK;
   public static final Block RAW_GOLD_BLOCK;
   public static final Block POTTED_AZALEA;
   public static final Block POTTED_FLOWERING_AZALEA;
   public static final Block OCHRE_FROGLIGHT;
   public static final Block VERDANT_FROGLIGHT;
   public static final Block PEARLESCENT_FROGLIGHT;
   public static final Block FROGSPAWN;
   public static final Block REINFORCED_DEEPSLATE;
   public static final Block DECORATED_POT;
   public static final Block CRAFTER;
   public static final Block TRIAL_SPAWNER;
   public static final Block VAULT;
   public static final Block HEAVY_CORE;
   public static final Block PALE_MOSS_BLOCK;
   public static final Block PALE_MOSS_CARPET;
   public static final Block PALE_HANGING_MOSS;
   public static final Block OPEN_EYEBLOSSOM;
   public static final Block CLOSED_EYEBLOSSOM;
   public static final Block POTTED_OPEN_EYEBLOSSOM;
   public static final Block POTTED_CLOSED_EYEBLOSSOM;

   public Blocks() {
      super();
   }

   private static ToIntFunction<BlockState> litBlockEmission(int var0) {
      return (var1) -> {
         return (Boolean)var1.getValue(BlockStateProperties.LIT) ? var0 : 0;
      };
   }

   private static Function<BlockState, MapColor> waterloggedMapColor(MapColor var0) {
      return (var1) -> {
         return (Boolean)var1.getValue(BlockStateProperties.WATERLOGGED) ? MapColor.WATER : var0;
      };
   }

   private static Boolean never(BlockState var0, BlockGetter var1, BlockPos var2, EntityType<?> var3) {
      return false;
   }

   private static Boolean always(BlockState var0, BlockGetter var1, BlockPos var2, EntityType<?> var3) {
      return true;
   }

   private static Boolean ocelotOrParrot(BlockState var0, BlockGetter var1, BlockPos var2, EntityType<?> var3) {
      return var3 == EntityType.OCELOT || var3 == EntityType.PARROT;
   }

   private static Block registerBed(String var0, DyeColor var1) {
      return register(var0, (var1x) -> {
         return new BedBlock(var1, var1x);
      }, BlockBehaviour.Properties.of().mapColor((var1x) -> {
         return var1x.getValue(BedBlock.PART) == BedPart.FOOT ? var1.getMapColor() : MapColor.WOOL;
      }).sound(SoundType.WOOD).strength(0.2F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
   }

   private static BlockBehaviour.Properties logProperties(MapColor var0, MapColor var1, SoundType var2) {
      return BlockBehaviour.Properties.of().mapColor((var2x) -> {
         return var2x.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? var0 : var1;
      }).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(var2).ignitedByLava();
   }

   private static BlockBehaviour.Properties netherStemProperties(MapColor var0) {
      return BlockBehaviour.Properties.of().mapColor((var1) -> {
         return var0;
      }).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM);
   }

   private static boolean always(BlockState var0, BlockGetter var1, BlockPos var2) {
      return true;
   }

   private static boolean never(BlockState var0, BlockGetter var1, BlockPos var2) {
      return false;
   }

   private static Block registerStainedGlass(String var0, DyeColor var1) {
      return register(var0, (var1x) -> {
         return new StainedGlassBlock(var1, var1x);
      }, BlockBehaviour.Properties.of().mapColor(var1).instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never));
   }

   private static BlockBehaviour.Properties leavesProperties(SoundType var0) {
      return BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(var0).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never);
   }

   private static BlockBehaviour.Properties shulkerBoxProperties(MapColor var0) {
      return BlockBehaviour.Properties.of().mapColor(var0).forceSolidOn().strength(2.0F).dynamicShape().noOcclusion().isSuffocating(NOT_CLOSED_SHULKER).isViewBlocking(NOT_CLOSED_SHULKER).pushReaction(PushReaction.DESTROY);
   }

   private static BlockBehaviour.Properties pistonProperties() {
      return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F).isRedstoneConductor(Blocks::never).isSuffocating(NOT_EXTENDED_PISTON).isViewBlocking(NOT_EXTENDED_PISTON).pushReaction(PushReaction.BLOCK);
   }

   private static BlockBehaviour.Properties buttonProperties() {
      return BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY);
   }

   private static BlockBehaviour.Properties flowerPotProperties() {
      return BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
   }

   private static BlockBehaviour.Properties candleProperties(MapColor var0) {
      return BlockBehaviour.Properties.of().mapColor(var0).noOcclusion().strength(0.1F).sound(SoundType.CANDLE).lightLevel(CandleBlock.LIGHT_EMISSION).pushReaction(PushReaction.DESTROY);
   }

   /** @deprecated */
   @Deprecated
   private static Block registerLegacyStair(String var0, Block var1) {
      return register(var0, (var1x) -> {
         return new StairBlock(var1.defaultBlockState(), var1x);
      }, BlockBehaviour.Properties.ofLegacyCopy(var1));
   }

   private static Block registerStair(String var0, Block var1) {
      return register(var0, (var1x) -> {
         return new StairBlock(var1.defaultBlockState(), var1x);
      }, BlockBehaviour.Properties.ofFullCopy(var1));
   }

   private static BlockBehaviour.Properties wallVariant(Block var0, boolean var1) {
      BlockBehaviour.Properties var2 = var0.properties();
      BlockBehaviour.Properties var3 = BlockBehaviour.Properties.of().overrideLootTable(var0.getLootTable());
      if (var1) {
         var3 = var3.overrideDescription(var0.getDescriptionId());
      }

      return var3;
   }

   private static Block register(ResourceKey<Block> var0, Function<BlockBehaviour.Properties, Block> var1, BlockBehaviour.Properties var2) {
      Block var3 = (Block)var1.apply(var2.setId(var0));
      return (Block)Registry.register(BuiltInRegistries.BLOCK, (ResourceKey)var0, var3);
   }

   private static Block register(ResourceKey<Block> var0, BlockBehaviour.Properties var1) {
      return register(var0, Block::new, var1);
   }

   private static ResourceKey<Block> vanillaBlockId(String var0) {
      return ResourceKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace(var0));
   }

   private static Block register(String var0, Function<BlockBehaviour.Properties, Block> var1, BlockBehaviour.Properties var2) {
      return register(vanillaBlockId(var0), var1, var2);
   }

   private static Block register(String var0, BlockBehaviour.Properties var1) {
      return register(var0, Block::new, var1);
   }

   static {
      STONE = register("stone", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      GRANITE = register("granite", BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      POLISHED_GRANITE = register("polished_granite", BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DIORITE = register("diorite", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      POLISHED_DIORITE = register("polished_diorite", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      ANDESITE = register("andesite", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      POLISHED_ANDESITE = register("polished_andesite", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      GRASS_BLOCK = register("grass_block", GrassBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).randomTicks().strength(0.6F).sound(SoundType.GRASS));
      DIRT = register("dirt", BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL));
      COARSE_DIRT = register("coarse_dirt", BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL));
      PODZOL = register("podzol", SnowyDirtBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).strength(0.5F).sound(SoundType.GRAVEL));
      COBBLESTONE = register("cobblestone", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      OAK_PLANKS = register("oak_planks", BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      SPRUCE_PLANKS = register("spruce_planks", BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      BIRCH_PLANKS = register("birch_planks", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      JUNGLE_PLANKS = register("jungle_planks", BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      ACACIA_PLANKS = register("acacia_planks", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      CHERRY_PLANKS = register("cherry_planks", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava());
      DARK_OAK_PLANKS = register("dark_oak_planks", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      PALE_OAK_WOOD = register("pale_oak_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      PALE_OAK_PLANKS = register("pale_oak_planks", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      MANGROVE_PLANKS = register("mangrove_planks", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      BAMBOO_PLANKS = register("bamboo_planks", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD).ignitedByLava());
      BAMBOO_MOSAIC = register("bamboo_mosaic", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD).ignitedByLava());
      OAK_SAPLING = register("oak_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      SPRUCE_SAPLING = register("spruce_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      BIRCH_SAPLING = register("birch_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      JUNGLE_SAPLING = register("jungle_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      ACACIA_SAPLING = register("acacia_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      CHERRY_SAPLING = register("cherry_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).noCollission().randomTicks().instabreak().sound(SoundType.CHERRY_SAPLING).pushReaction(PushReaction.DESTROY));
      DARK_OAK_SAPLING = register("dark_oak_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      PALE_OAK_SAPLING = register("pale_oak_sapling", (var0x) -> {
         return new SaplingBlock(TreeGrower.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      MANGROVE_PROPAGULE = register("mangrove_propagule", (var0x) -> {
         return new MangrovePropaguleBlock(TreeGrower.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      BEDROCK = register("bedrock", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).noLootTable().isValidSpawn(Blocks::never));
      WATER = register("water", (var0x) -> {
         return new LiquidBlock(Fluids.WATER, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
      LAVA = register("lava", (var0x) -> {
         return new LiquidBlock(Fluids.LAVA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).replaceable().noCollission().randomTicks().strength(100.0F).lightLevel((var0x) -> {
         return 15;
      }).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
      SAND = register("sand", (var0x) -> {
         return new ColoredFallingBlock(new ColorRGBA(14406560), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      SUSPICIOUS_SAND = register("suspicious_sand", (var0x) -> {
         return new BrushableBlock(SAND, SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.25F).sound(SoundType.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY));
      RED_SAND = register("red_sand", (var0x) -> {
         return new ColoredFallingBlock(new ColorRGBA(11098145), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      GRAVEL = register("gravel", (var0x) -> {
         return new ColoredFallingBlock(new ColorRGBA(-8356741), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.SNARE).strength(0.6F).sound(SoundType.GRAVEL));
      SUSPICIOUS_GRAVEL = register("suspicious_gravel", (var0x) -> {
         return new BrushableBlock(GRAVEL, SoundEvents.BRUSH_GRAVEL, SoundEvents.BRUSH_GRAVEL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.SNARE).strength(0.25F).sound(SoundType.SUSPICIOUS_GRAVEL).pushReaction(PushReaction.DESTROY));
      GOLD_ORE = register("gold_ore", (var0x) -> {
         return new DropExperienceBlock(ConstantInt.of(0), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DEEPSLATE_GOLD_ORE = register("deepslate_gold_ore", (var0x) -> {
         return new DropExperienceBlock(ConstantInt.of(0), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(GOLD_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      IRON_ORE = register("iron_ore", (var0x) -> {
         return new DropExperienceBlock(ConstantInt.of(0), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DEEPSLATE_IRON_ORE = register("deepslate_iron_ore", (var0x) -> {
         return new DropExperienceBlock(ConstantInt.of(0), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(IRON_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      COAL_ORE = register("coal_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(0, 2), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DEEPSLATE_COAL_ORE = register("deepslate_coal_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(0, 2), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(COAL_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      NETHER_GOLD_ORE = register("nether_gold_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(0, 1), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.NETHER_GOLD_ORE));
      OAK_LOG = register("oak_log", RotatedPillarBlock::new, logProperties(MapColor.WOOD, MapColor.PODZOL, SoundType.WOOD));
      SPRUCE_LOG = register("spruce_log", RotatedPillarBlock::new, logProperties(MapColor.PODZOL, MapColor.COLOR_BROWN, SoundType.WOOD));
      BIRCH_LOG = register("birch_log", RotatedPillarBlock::new, logProperties(MapColor.SAND, MapColor.QUARTZ, SoundType.WOOD));
      JUNGLE_LOG = register("jungle_log", RotatedPillarBlock::new, logProperties(MapColor.DIRT, MapColor.PODZOL, SoundType.WOOD));
      ACACIA_LOG = register("acacia_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_ORANGE, MapColor.STONE, SoundType.WOOD));
      CHERRY_LOG = register("cherry_log", RotatedPillarBlock::new, logProperties(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_GRAY, SoundType.CHERRY_WOOD));
      DARK_OAK_LOG = register("dark_oak_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BROWN, MapColor.COLOR_BROWN, SoundType.WOOD));
      PALE_OAK_LOG = register("pale_oak_log", RotatedPillarBlock::new, logProperties(PALE_OAK_PLANKS.defaultMapColor(), PALE_OAK_WOOD.defaultMapColor(), SoundType.WOOD));
      MANGROVE_LOG = register("mangrove_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_RED, MapColor.PODZOL, SoundType.WOOD));
      MANGROVE_ROOTS = register("mangrove_roots", MangroveRootsBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(0.7F).sound(SoundType.MANGROVE_ROOTS).noOcclusion().isSuffocating(Blocks::never).isViewBlocking(Blocks::never).noOcclusion().ignitedByLava());
      MUDDY_MANGROVE_ROOTS = register("muddy_mangrove_roots", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).strength(0.7F).sound(SoundType.MUDDY_MANGROVE_ROOTS));
      BAMBOO_BLOCK = register("bamboo_block", RotatedPillarBlock::new, logProperties(MapColor.COLOR_YELLOW, MapColor.PLANT, SoundType.BAMBOO_WOOD));
      STRIPPED_SPRUCE_LOG = register("stripped_spruce_log", RotatedPillarBlock::new, logProperties(MapColor.PODZOL, MapColor.PODZOL, SoundType.WOOD));
      STRIPPED_BIRCH_LOG = register("stripped_birch_log", RotatedPillarBlock::new, logProperties(MapColor.SAND, MapColor.SAND, SoundType.WOOD));
      STRIPPED_JUNGLE_LOG = register("stripped_jungle_log", RotatedPillarBlock::new, logProperties(MapColor.DIRT, MapColor.DIRT, SoundType.WOOD));
      STRIPPED_ACACIA_LOG = register("stripped_acacia_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_ORANGE, MapColor.COLOR_ORANGE, SoundType.WOOD));
      STRIPPED_CHERRY_LOG = register("stripped_cherry_log", RotatedPillarBlock::new, logProperties(MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_PINK, SoundType.CHERRY_WOOD));
      STRIPPED_DARK_OAK_LOG = register("stripped_dark_oak_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_BROWN, MapColor.COLOR_BROWN, SoundType.WOOD));
      STRIPPED_PALE_OAK_LOG = register("stripped_pale_oak_log", RotatedPillarBlock::new, logProperties(PALE_OAK_PLANKS.defaultMapColor(), PALE_OAK_PLANKS.defaultMapColor(), SoundType.WOOD));
      STRIPPED_OAK_LOG = register("stripped_oak_log", RotatedPillarBlock::new, logProperties(MapColor.WOOD, MapColor.WOOD, SoundType.WOOD));
      STRIPPED_MANGROVE_LOG = register("stripped_mangrove_log", RotatedPillarBlock::new, logProperties(MapColor.COLOR_RED, MapColor.COLOR_RED, SoundType.WOOD));
      STRIPPED_BAMBOO_BLOCK = register("stripped_bamboo_block", RotatedPillarBlock::new, logProperties(MapColor.COLOR_YELLOW, MapColor.COLOR_YELLOW, SoundType.BAMBOO_WOOD));
      OAK_WOOD = register("oak_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      SPRUCE_WOOD = register("spruce_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      BIRCH_WOOD = register("birch_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      JUNGLE_WOOD = register("jungle_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      ACACIA_WOOD = register("acacia_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      CHERRY_WOOD = register("cherry_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava());
      DARK_OAK_WOOD = register("dark_oak_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      MANGROVE_WOOD = register("mangrove_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_OAK_WOOD = register("stripped_oak_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_SPRUCE_WOOD = register("stripped_spruce_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_BIRCH_WOOD = register("stripped_birch_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_JUNGLE_WOOD = register("stripped_jungle_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_ACACIA_WOOD = register("stripped_acacia_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_CHERRY_WOOD = register("stripped_cherry_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava());
      STRIPPED_DARK_OAK_WOOD = register("stripped_dark_oak_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_PALE_OAK_WOOD = register("stripped_pale_oak_wood", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
      STRIPPED_MANGROVE_WOOD = register("stripped_mangrove_wood", RotatedPillarBlock::new, logProperties(MapColor.COLOR_RED, MapColor.COLOR_RED, SoundType.WOOD));
      OAK_LEAVES = register("oak_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
      SPRUCE_LEAVES = register("spruce_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
      BIRCH_LEAVES = register("birch_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
      JUNGLE_LEAVES = register("jungle_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
      ACACIA_LEAVES = register("acacia_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
      CHERRY_LEAVES = register("cherry_leaves", (var0x) -> {
         return new ParticleLeavesBlock(10, ParticleTypes.CHERRY_LEAVES, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));
      DARK_OAK_LEAVES = register("dark_oak_leaves", LeavesBlock::new, leavesProperties(SoundType.GRASS));
      PALE_OAK_LEAVES = register("pale_oak_leaves", (var0x) -> {
         return new ParticleLeavesBlock(50, ParticleTypes.PALE_OAK_LEAVES, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));
      MANGROVE_LEAVES = register("mangrove_leaves", MangroveLeavesBlock::new, leavesProperties(SoundType.GRASS));
      AZALEA_LEAVES = register("azalea_leaves", LeavesBlock::new, leavesProperties(SoundType.AZALEA_LEAVES));
      FLOWERING_AZALEA_LEAVES = register("flowering_azalea_leaves", LeavesBlock::new, leavesProperties(SoundType.AZALEA_LEAVES));
      SPONGE = register("sponge", SpongeBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.6F).sound(SoundType.SPONGE));
      WET_SPONGE = register("wet_sponge", WetSpongeBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.6F).sound(SoundType.WET_SPONGE));
      GLASS = register("glass", TransparentBlock::new, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never));
      LAPIS_ORE = register("lapis_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(2, 5), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DEEPSLATE_LAPIS_ORE = register("deepslate_lapis_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(2, 5), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(LAPIS_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      LAPIS_BLOCK = register("lapis_block", BlockBehaviour.Properties.of().mapColor(MapColor.LAPIS).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DISPENSER = register("dispenser", DispenserBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F));
      SANDSTONE = register("sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      CHISELED_SANDSTONE = register("chiseled_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      CUT_SANDSTONE = register("cut_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      NOTE_BLOCK = register("note_block", NoteBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(0.8F).ignitedByLava());
      WHITE_BED = registerBed("white_bed", DyeColor.WHITE);
      ORANGE_BED = registerBed("orange_bed", DyeColor.ORANGE);
      MAGENTA_BED = registerBed("magenta_bed", DyeColor.MAGENTA);
      LIGHT_BLUE_BED = registerBed("light_blue_bed", DyeColor.LIGHT_BLUE);
      YELLOW_BED = registerBed("yellow_bed", DyeColor.YELLOW);
      LIME_BED = registerBed("lime_bed", DyeColor.LIME);
      PINK_BED = registerBed("pink_bed", DyeColor.PINK);
      GRAY_BED = registerBed("gray_bed", DyeColor.GRAY);
      LIGHT_GRAY_BED = registerBed("light_gray_bed", DyeColor.LIGHT_GRAY);
      CYAN_BED = registerBed("cyan_bed", DyeColor.CYAN);
      PURPLE_BED = registerBed("purple_bed", DyeColor.PURPLE);
      BLUE_BED = registerBed("blue_bed", DyeColor.BLUE);
      BROWN_BED = registerBed("brown_bed", DyeColor.BROWN);
      GREEN_BED = registerBed("green_bed", DyeColor.GREEN);
      RED_BED = registerBed("red_bed", DyeColor.RED);
      BLACK_BED = registerBed("black_bed", DyeColor.BLACK);
      POWERED_RAIL = register("powered_rail", PoweredRailBlock::new, BlockBehaviour.Properties.of().noCollission().strength(0.7F).sound(SoundType.METAL));
      DETECTOR_RAIL = register("detector_rail", DetectorRailBlock::new, BlockBehaviour.Properties.of().noCollission().strength(0.7F).sound(SoundType.METAL));
      STICKY_PISTON = register("sticky_piston", (var0x) -> {
         return new PistonBaseBlock(true, var0x);
      }, pistonProperties());
      COBWEB = register("cobweb", WebBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).sound(SoundType.COBWEB).forceSolidOn().noCollission().requiresCorrectToolForDrops().strength(4.0F).pushReaction(PushReaction.DESTROY));
      SHORT_GRASS = register("short_grass", TallGrassBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XYZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      FERN = register("fern", TallGrassBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XYZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      DEAD_BUSH = register("dead_bush", DeadBushBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).replaceable().noCollission().instabreak().sound(SoundType.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY));
      SEAGRASS = register("seagrass", SeagrassBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      TALL_SEAGRASS = register("tall_seagrass", TallSeagrassBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().instabreak().sound(SoundType.WET_GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      PISTON = register("piston", (var0x) -> {
         return new PistonBaseBlock(false, var0x);
      }, pistonProperties());
      PISTON_HEAD = register("piston_head", PistonHeadBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F).noLootTable().pushReaction(PushReaction.BLOCK));
      WHITE_WOOL = register("white_wool", BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      ORANGE_WOOL = register("orange_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      MAGENTA_WOOL = register("magenta_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      LIGHT_BLUE_WOOL = register("light_blue_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      YELLOW_WOOL = register("yellow_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      LIME_WOOL = register("lime_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      PINK_WOOL = register("pink_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      GRAY_WOOL = register("gray_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      LIGHT_GRAY_WOOL = register("light_gray_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      CYAN_WOOL = register("cyan_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      PURPLE_WOOL = register("purple_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      BLUE_WOOL = register("blue_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      BROWN_WOOL = register("brown_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      GREEN_WOOL = register("green_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      RED_WOOL = register("red_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      BLACK_WOOL = register("black_wool", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).ignitedByLava());
      MOVING_PISTON = register("moving_piston", MovingPistonBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).forceSolidOn().strength(-1.0F).dynamicShape().noLootTable().noOcclusion().isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).pushReaction(PushReaction.BLOCK));
      DANDELION = register("dandelion", (var0x) -> {
         return new FlowerBlock(MobEffects.SATURATION, 0.35F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      TORCHFLOWER = register("torchflower", (var0x) -> {
         return new FlowerBlock(MobEffects.NIGHT_VISION, 5.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      POPPY = register("poppy", (var0x) -> {
         return new FlowerBlock(MobEffects.NIGHT_VISION, 5.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      BLUE_ORCHID = register("blue_orchid", (var0x) -> {
         return new FlowerBlock(MobEffects.SATURATION, 0.35F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      ALLIUM = register("allium", (var0x) -> {
         return new FlowerBlock(MobEffects.FIRE_RESISTANCE, 3.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      AZURE_BLUET = register("azure_bluet", (var0x) -> {
         return new FlowerBlock(MobEffects.BLINDNESS, 11.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      RED_TULIP = register("red_tulip", (var0x) -> {
         return new FlowerBlock(MobEffects.WEAKNESS, 7.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      ORANGE_TULIP = register("orange_tulip", (var0x) -> {
         return new FlowerBlock(MobEffects.WEAKNESS, 7.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      WHITE_TULIP = register("white_tulip", (var0x) -> {
         return new FlowerBlock(MobEffects.WEAKNESS, 7.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      PINK_TULIP = register("pink_tulip", (var0x) -> {
         return new FlowerBlock(MobEffects.WEAKNESS, 7.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      OXEYE_DAISY = register("oxeye_daisy", (var0x) -> {
         return new FlowerBlock(MobEffects.REGENERATION, 7.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      CORNFLOWER = register("cornflower", (var0x) -> {
         return new FlowerBlock(MobEffects.JUMP, 5.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      WITHER_ROSE = register("wither_rose", (var0x) -> {
         return new WitherRoseBlock(MobEffects.WITHER, 7.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      LILY_OF_THE_VALLEY = register("lily_of_the_valley", (var0x) -> {
         return new FlowerBlock(MobEffects.POISON, 11.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      BROWN_MUSHROOM = register("brown_mushroom", (var0x) -> {
         return new MushroomBlock(TreeFeatures.HUGE_BROWN_MUSHROOM, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).lightLevel((var0x) -> {
         return 1;
      }).hasPostProcess(Blocks::always).pushReaction(PushReaction.DESTROY));
      RED_MUSHROOM = register("red_mushroom", (var0x) -> {
         return new MushroomBlock(TreeFeatures.HUGE_RED_MUSHROOM, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).hasPostProcess(Blocks::always).pushReaction(PushReaction.DESTROY));
      GOLD_BLOCK = register("gold_block", BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL));
      IRON_BLOCK = register("iron_block", BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
      BRICKS = register("bricks", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      TNT = register("tnt", TntBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).instabreak().sound(SoundType.GRASS).ignitedByLava().isRedstoneConductor(Blocks::never));
      BOOKSHELF = register("bookshelf", BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.5F).sound(SoundType.WOOD).ignitedByLava());
      CHISELED_BOOKSHELF = register("chiseled_bookshelf", ChiseledBookShelfBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(1.5F).sound(SoundType.CHISELED_BOOKSHELF).ignitedByLava());
      MOSSY_COBBLESTONE = register("mossy_cobblestone", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      OBSIDIAN = register("obsidian", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
      TORCH = register("torch", (var0x) -> {
         return new TorchBlock(ParticleTypes.FLAME, var0x);
      }, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((var0x) -> {
         return 14;
      }).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      WALL_TORCH = register("wall_torch", (var0x) -> {
         return new WallTorchBlock(ParticleTypes.FLAME, var0x);
      }, wallVariant(TORCH, true).noCollission().instabreak().lightLevel((var0x) -> {
         return 14;
      }).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      FIRE = register("fire", FireBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).replaceable().noCollission().instabreak().lightLevel((var0x) -> {
         return 15;
      }).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
      SOUL_FIRE = register("soul_fire", SoulFireBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).replaceable().noCollission().instabreak().lightLevel((var0x) -> {
         return 10;
      }).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
      SPAWNER = register("spawner", SpawnerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.SPAWNER).noOcclusion());
      CREAKING_HEART = register("creaking_heart", CreakingHeartBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).strength(5.0F).sound(SoundType.CREAKING_HEART));
      OAK_STAIRS = registerLegacyStair("oak_stairs", OAK_PLANKS);
      CHEST = register("chest", (var0x) -> {
         return new ChestBlock(() -> {
            return BlockEntityType.CHEST;
         }, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      REDSTONE_WIRE = register("redstone_wire", RedStoneWireBlock::new, BlockBehaviour.Properties.of().noCollission().instabreak().pushReaction(PushReaction.DESTROY));
      DIAMOND_ORE = register("diamond_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(3, 7), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DEEPSLATE_DIAMOND_ORE = register("deepslate_diamond_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(3, 7), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(DIAMOND_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      DIAMOND_BLOCK = register("diamond_block", BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
      CRAFTING_TABLE = register("crafting_table", CraftingTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      WHEAT = register("wheat", CropBlock::new, BlockBehaviour.Properties.of().mapColor((var0x) -> {
         return (Integer)var0x.getValue(CropBlock.AGE) >= 6 ? MapColor.COLOR_YELLOW : MapColor.PLANT;
      }).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
      FARMLAND = register("farmland", FarmBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).randomTicks().strength(0.6F).sound(SoundType.GRAVEL).isViewBlocking(Blocks::always).isSuffocating(Blocks::always));
      FURNACE = register("furnace", FurnaceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13)));
      OAK_SIGN = register("oak_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      SPRUCE_SIGN = register("spruce_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(SPRUCE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BIRCH_SIGN = register("birch_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      ACACIA_SIGN = register("acacia_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      CHERRY_SIGN = register("cherry_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CHERRY_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      JUNGLE_SIGN = register("jungle_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(JUNGLE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      DARK_OAK_SIGN = register("dark_oak_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DARK_OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      PALE_OAK_SIGN = register("pale_oak_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      MANGROVE_SIGN = register("mangrove_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MANGROVE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BAMBOO_SIGN = register("bamboo_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.BAMBOO, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      OAK_DOOR = register("oak_door", (var0x) -> {
         return new DoorBlock(BlockSetType.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      LADDER = register("ladder", LadderBlock::new, BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY));
      RAIL = register("rail", RailBlock::new, BlockBehaviour.Properties.of().noCollission().strength(0.7F).sound(SoundType.METAL));
      COBBLESTONE_STAIRS = registerLegacyStair("cobblestone_stairs", COBBLESTONE);
      OAK_WALL_SIGN = register("oak_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.OAK, var0x);
      }, wallVariant(OAK_SIGN, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      SPRUCE_WALL_SIGN = register("spruce_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.SPRUCE, var0x);
      }, wallVariant(SPRUCE_SIGN, true).mapColor(SPRUCE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BIRCH_WALL_SIGN = register("birch_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.BIRCH, var0x);
      }, wallVariant(BIRCH_SIGN, true).mapColor(MapColor.SAND).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      ACACIA_WALL_SIGN = register("acacia_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.ACACIA, var0x);
      }, wallVariant(ACACIA_SIGN, true).mapColor(MapColor.COLOR_ORANGE).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      CHERRY_WALL_SIGN = register("cherry_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.CHERRY, var0x);
      }, wallVariant(CHERRY_SIGN, true).mapColor(CHERRY_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      JUNGLE_WALL_SIGN = register("jungle_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.JUNGLE, var0x);
      }, wallVariant(JUNGLE_SIGN, true).mapColor(JUNGLE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      DARK_OAK_WALL_SIGN = register("dark_oak_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.DARK_OAK, var0x);
      }, wallVariant(DARK_OAK_SIGN, true).mapColor(DARK_OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      PALE_OAK_WALL_SIGN = register("pale_oak_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.PALE_OAK, var0x);
      }, wallVariant(PALE_OAK_SIGN, true).mapColor(PALE_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      MANGROVE_WALL_SIGN = register("mangrove_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.MANGROVE, var0x);
      }, wallVariant(MANGROVE_SIGN, true).mapColor(MANGROVE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BAMBOO_WALL_SIGN = register("bamboo_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.BAMBOO, var0x);
      }, wallVariant(BAMBOO_SIGN, true).mapColor(BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      OAK_HANGING_SIGN = register("oak_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      SPRUCE_HANGING_SIGN = register("spruce_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(SPRUCE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BIRCH_HANGING_SIGN = register("birch_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      ACACIA_HANGING_SIGN = register("acacia_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      CHERRY_HANGING_SIGN = register("cherry_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      JUNGLE_HANGING_SIGN = register("jungle_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(JUNGLE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      DARK_OAK_HANGING_SIGN = register("dark_oak_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DARK_OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      PALE_OAK_HANGING_SIGN = register("pale_oak_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      CRIMSON_HANGING_SIGN = register("crimson_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.CRIMSON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_STEM).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F));
      WARPED_HANGING_SIGN = register("warped_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.WARPED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_STEM).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F));
      MANGROVE_HANGING_SIGN = register("mangrove_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MANGROVE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BAMBOO_HANGING_SIGN = register("bamboo_hanging_sign", (var0x) -> {
         return new CeilingHangingSignBlock(WoodType.BAMBOO, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      OAK_WALL_HANGING_SIGN = register("oak_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.OAK, var0x);
      }, wallVariant(OAK_HANGING_SIGN, true).mapColor(OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      SPRUCE_WALL_HANGING_SIGN = register("spruce_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.SPRUCE, var0x);
      }, wallVariant(SPRUCE_HANGING_SIGN, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      BIRCH_WALL_HANGING_SIGN = register("birch_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.BIRCH, var0x);
      }, wallVariant(BIRCH_HANGING_SIGN, true).mapColor(MapColor.SAND).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      ACACIA_WALL_HANGING_SIGN = register("acacia_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.ACACIA, var0x);
      }, wallVariant(ACACIA_HANGING_SIGN, true).mapColor(MapColor.COLOR_ORANGE).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      CHERRY_WALL_HANGING_SIGN = register("cherry_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.CHERRY, var0x);
      }, wallVariant(CHERRY_HANGING_SIGN, true).mapColor(MapColor.TERRACOTTA_PINK).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      JUNGLE_WALL_HANGING_SIGN = register("jungle_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.JUNGLE, var0x);
      }, wallVariant(JUNGLE_HANGING_SIGN, true).mapColor(JUNGLE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      DARK_OAK_WALL_HANGING_SIGN = register("dark_oak_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.DARK_OAK, var0x);
      }, wallVariant(DARK_OAK_HANGING_SIGN, true).mapColor(DARK_OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      PALE_OAK_WALL_HANGING_SIGN = register("pale_oak_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.PALE_OAK, var0x);
      }, wallVariant(PALE_OAK_HANGING_SIGN, true).mapColor(PALE_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      MANGROVE_WALL_HANGING_SIGN = register("mangrove_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.MANGROVE, var0x);
      }, wallVariant(MANGROVE_HANGING_SIGN, true).mapColor(MANGROVE_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      CRIMSON_WALL_HANGING_SIGN = register("crimson_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.CRIMSON, var0x);
      }, wallVariant(CRIMSON_HANGING_SIGN, true).mapColor(MapColor.CRIMSON_STEM).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F));
      WARPED_WALL_HANGING_SIGN = register("warped_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.WARPED, var0x);
      }, wallVariant(WARPED_HANGING_SIGN, true).mapColor(MapColor.WARPED_STEM).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F));
      BAMBOO_WALL_HANGING_SIGN = register("bamboo_wall_hanging_sign", (var0x) -> {
         return new WallHangingSignBlock(WoodType.BAMBOO, var0x);
      }, wallVariant(BAMBOO_HANGING_SIGN, true).mapColor(MapColor.COLOR_YELLOW).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
      LEVER = register("lever", LeverBlock::new, BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.STONE).pushReaction(PushReaction.DESTROY));
      STONE_PRESSURE_PLATE = register("stone_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.STONE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
      IRON_DOOR = register("iron_door", (var0x) -> {
         return new DoorBlock(BlockSetType.IRON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
      OAK_PRESSURE_PLATE = register("oak_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      SPRUCE_PRESSURE_PLATE = register("spruce_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(SPRUCE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      BIRCH_PRESSURE_PLATE = register("birch_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BIRCH_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      JUNGLE_PRESSURE_PLATE = register("jungle_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(JUNGLE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      ACACIA_PRESSURE_PLATE = register("acacia_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(ACACIA_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      CHERRY_PRESSURE_PLATE = register("cherry_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CHERRY_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      DARK_OAK_PRESSURE_PLATE = register("dark_oak_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DARK_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      PALE_OAK_PRESSURE_PLATE = register("pale_oak_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      MANGROVE_PRESSURE_PLATE = register("mangrove_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MANGROVE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      BAMBOO_PRESSURE_PLATE = register("bamboo_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.BAMBOO, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).ignitedByLava().pushReaction(PushReaction.DESTROY));
      REDSTONE_ORE = register("redstone_ore", RedStoneOreBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().randomTicks().lightLevel(litBlockEmission(9)).strength(3.0F, 3.0F));
      DEEPSLATE_REDSTONE_ORE = register("deepslate_redstone_ore", RedStoneOreBlock::new, BlockBehaviour.Properties.ofLegacyCopy(REDSTONE_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      REDSTONE_TORCH = register("redstone_torch", RedstoneTorchBlock::new, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(litBlockEmission(7)).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      REDSTONE_WALL_TORCH = register("redstone_wall_torch", RedstoneWallTorchBlock::new, wallVariant(REDSTONE_TORCH, true).noCollission().instabreak().lightLevel(litBlockEmission(7)).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      STONE_BUTTON = register("stone_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.STONE, 20, var0x);
      }, buttonProperties());
      SNOW = register("snow", SnowLayerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).replaceable().forceSolidOff().randomTicks().strength(0.1F).requiresCorrectToolForDrops().sound(SoundType.SNOW).isViewBlocking((var0x, var1x, var2x) -> {
         return (Integer)var0x.getValue(SnowLayerBlock.LAYERS) >= 8;
      }).pushReaction(PushReaction.DESTROY));
      ICE = register("ice", IceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.ICE).friction(0.98F).randomTicks().strength(0.5F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((var0x, var1x, var2x, var3x) -> {
         return var3x == EntityType.POLAR_BEAR;
      }).isRedstoneConductor(Blocks::never));
      SNOW_BLOCK = register("snow_block", BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).requiresCorrectToolForDrops().strength(0.2F).sound(SoundType.SNOW));
      CACTUS = register("cactus", CactusBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().strength(0.4F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
      CLAY = register("clay", BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).instrument(NoteBlockInstrument.FLUTE).strength(0.6F).sound(SoundType.GRAVEL));
      SUGAR_CANE = register("sugar_cane", SugarCaneBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
      JUKEBOX = register("jukebox", JukeboxBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(2.0F, 6.0F).sound(SoundType.WOOD).ignitedByLava());
      OAK_FENCE = register("oak_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      NETHERRACK = register("netherrack", NetherrackBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.4F).sound(SoundType.NETHERRACK));
      SOUL_SAND = register("soul_sand", SoulSandBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.COW_BELL).strength(0.5F).speedFactor(0.4F).sound(SoundType.SOUL_SAND).isValidSpawn(Blocks::always).isRedstoneConductor(Blocks::always).isViewBlocking(Blocks::always).isSuffocating(Blocks::always));
      SOUL_SOIL = register("soul_soil", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.SOUL_SOIL));
      BASALT = register("basalt", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F).sound(SoundType.BASALT));
      POLISHED_BASALT = register("polished_basalt", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F).sound(SoundType.BASALT));
      SOUL_TORCH = register("soul_torch", (var0x) -> {
         return new TorchBlock(ParticleTypes.SOUL_FIRE_FLAME, var0x);
      }, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((var0x) -> {
         return 10;
      }).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      SOUL_WALL_TORCH = register("soul_wall_torch", (var0x) -> {
         return new WallTorchBlock(ParticleTypes.SOUL_FIRE_FLAME, var0x);
      }, wallVariant(SOUL_TORCH, true).noCollission().instabreak().lightLevel((var0x) -> {
         return 10;
      }).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      GLOWSTONE = register("glowstone", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.PLING).strength(0.3F).sound(SoundType.GLASS).lightLevel((var0x) -> {
         return 15;
      }).isRedstoneConductor(Blocks::never));
      NETHER_PORTAL = register("nether_portal", NetherPortalBlock::new, BlockBehaviour.Properties.of().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel((var0x) -> {
         return 11;
      }).pushReaction(PushReaction.BLOCK));
      CARVED_PUMPKIN = register("carved_pumpkin", CarvedPumpkinBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).isValidSpawn(Blocks::always).pushReaction(PushReaction.DESTROY));
      JACK_O_LANTERN = register("jack_o_lantern", CarvedPumpkinBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).lightLevel((var0x) -> {
         return 15;
      }).isValidSpawn(Blocks::always).pushReaction(PushReaction.DESTROY));
      CAKE = register("cake", CakeBlock::new, BlockBehaviour.Properties.of().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
      REPEATER = register("repeater", RepeaterBlock::new, BlockBehaviour.Properties.of().instabreak().sound(SoundType.STONE).pushReaction(PushReaction.DESTROY));
      WHITE_STAINED_GLASS = registerStainedGlass("white_stained_glass", DyeColor.WHITE);
      ORANGE_STAINED_GLASS = registerStainedGlass("orange_stained_glass", DyeColor.ORANGE);
      MAGENTA_STAINED_GLASS = registerStainedGlass("magenta_stained_glass", DyeColor.MAGENTA);
      LIGHT_BLUE_STAINED_GLASS = registerStainedGlass("light_blue_stained_glass", DyeColor.LIGHT_BLUE);
      YELLOW_STAINED_GLASS = registerStainedGlass("yellow_stained_glass", DyeColor.YELLOW);
      LIME_STAINED_GLASS = registerStainedGlass("lime_stained_glass", DyeColor.LIME);
      PINK_STAINED_GLASS = registerStainedGlass("pink_stained_glass", DyeColor.PINK);
      GRAY_STAINED_GLASS = registerStainedGlass("gray_stained_glass", DyeColor.GRAY);
      LIGHT_GRAY_STAINED_GLASS = registerStainedGlass("light_gray_stained_glass", DyeColor.LIGHT_GRAY);
      CYAN_STAINED_GLASS = registerStainedGlass("cyan_stained_glass", DyeColor.CYAN);
      PURPLE_STAINED_GLASS = registerStainedGlass("purple_stained_glass", DyeColor.PURPLE);
      BLUE_STAINED_GLASS = registerStainedGlass("blue_stained_glass", DyeColor.BLUE);
      BROWN_STAINED_GLASS = registerStainedGlass("brown_stained_glass", DyeColor.BROWN);
      GREEN_STAINED_GLASS = registerStainedGlass("green_stained_glass", DyeColor.GREEN);
      RED_STAINED_GLASS = registerStainedGlass("red_stained_glass", DyeColor.RED);
      BLACK_STAINED_GLASS = registerStainedGlass("black_stained_glass", DyeColor.BLACK);
      OAK_TRAPDOOR = register("oak_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      SPRUCE_TRAPDOOR = register("spruce_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      BIRCH_TRAPDOOR = register("birch_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      JUNGLE_TRAPDOOR = register("jungle_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      ACACIA_TRAPDOOR = register("acacia_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      CHERRY_TRAPDOOR = register("cherry_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      DARK_OAK_TRAPDOOR = register("dark_oak_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      PALE_OAK_TRAPDOOR = register("pale_oak_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      MANGROVE_TRAPDOOR = register("mangrove_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      BAMBOO_TRAPDOOR = register("bamboo_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.BAMBOO, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never).ignitedByLava());
      STONE_BRICKS = register("stone_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      MOSSY_STONE_BRICKS = register("mossy_stone_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      CRACKED_STONE_BRICKS = register("cracked_stone_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      CHISELED_STONE_BRICKS = register("chiseled_stone_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      PACKED_MUD = register("packed_mud", BlockBehaviour.Properties.ofLegacyCopy(DIRT).strength(1.0F, 3.0F).sound(SoundType.PACKED_MUD));
      MUD_BRICKS = register("mud_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 3.0F).sound(SoundType.MUD_BRICKS));
      INFESTED_STONE = register("infested_stone", (var0x) -> {
         return new InfestedBlock(STONE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY));
      INFESTED_COBBLESTONE = register("infested_cobblestone", (var0x) -> {
         return new InfestedBlock(COBBLESTONE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY));
      INFESTED_STONE_BRICKS = register("infested_stone_bricks", (var0x) -> {
         return new InfestedBlock(STONE_BRICKS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY));
      INFESTED_MOSSY_STONE_BRICKS = register("infested_mossy_stone_bricks", (var0x) -> {
         return new InfestedBlock(MOSSY_STONE_BRICKS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY));
      INFESTED_CRACKED_STONE_BRICKS = register("infested_cracked_stone_bricks", (var0x) -> {
         return new InfestedBlock(CRACKED_STONE_BRICKS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY));
      INFESTED_CHISELED_STONE_BRICKS = register("infested_chiseled_stone_bricks", (var0x) -> {
         return new InfestedBlock(CHISELED_STONE_BRICKS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY));
      BROWN_MUSHROOM_BLOCK = register("brown_mushroom_block", HugeMushroomBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(0.2F).sound(SoundType.WOOD).ignitedByLava());
      RED_MUSHROOM_BLOCK = register("red_mushroom_block", HugeMushroomBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(0.2F).sound(SoundType.WOOD).ignitedByLava());
      MUSHROOM_STEM = register("mushroom_stem", HugeMushroomBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASS).strength(0.2F).sound(SoundType.WOOD).ignitedByLava());
      IRON_BARS = register("iron_bars", IronBarsBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion());
      CHAIN = register("chain", ChainBlock::new, BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion());
      GLASS_PANE = register("glass_pane", IronBarsBlock::new, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      PUMPKIN = register(net.minecraft.references.Blocks.PUMPKIN, PumpkinBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.DIDGERIDOO).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      MELON = register(net.minecraft.references.Blocks.MELON, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      ATTACHED_PUMPKIN_STEM = register(net.minecraft.references.Blocks.ATTACHED_PUMPKIN_STEM, (var0x) -> {
         return new AttachedStemBlock(net.minecraft.references.Blocks.PUMPKIN_STEM, net.minecraft.references.Blocks.PUMPKIN, Items.PUMPKIN_SEEDS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      ATTACHED_MELON_STEM = register(net.minecraft.references.Blocks.ATTACHED_MELON_STEM, (var0x) -> {
         return new AttachedStemBlock(net.minecraft.references.Blocks.MELON_STEM, net.minecraft.references.Blocks.MELON, Items.MELON_SEEDS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      PUMPKIN_STEM = register(net.minecraft.references.Blocks.PUMPKIN_STEM, (var0x) -> {
         return new StemBlock(net.minecraft.references.Blocks.PUMPKIN, net.minecraft.references.Blocks.ATTACHED_PUMPKIN_STEM, Items.PUMPKIN_SEEDS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.HARD_CROP).pushReaction(PushReaction.DESTROY));
      MELON_STEM = register(net.minecraft.references.Blocks.MELON_STEM, (var0x) -> {
         return new StemBlock(net.minecraft.references.Blocks.MELON, net.minecraft.references.Blocks.ATTACHED_MELON_STEM, Items.MELON_SEEDS, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.HARD_CROP).pushReaction(PushReaction.DESTROY));
      VINE = register("vine", VineBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission().randomTicks().strength(0.2F).sound(SoundType.VINE).ignitedByLava().pushReaction(PushReaction.DESTROY));
      GLOW_LICHEN = register("glow_lichen", GlowLichenBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.GLOW_LICHEN).replaceable().noCollission().strength(0.2F).sound(SoundType.GLOW_LICHEN).lightLevel(GlowLichenBlock.emission(7)).ignitedByLava().pushReaction(PushReaction.DESTROY));
      RESIN_CLUMP = register("resin_clump", MultifaceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).replaceable().noCollission().sound(SoundType.RESIN).ignitedByLava().pushReaction(PushReaction.DESTROY));
      OAK_FENCE_GATE = register("oak_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      BRICK_STAIRS = registerLegacyStair("brick_stairs", BRICKS);
      STONE_BRICK_STAIRS = registerLegacyStair("stone_brick_stairs", STONE_BRICKS);
      MUD_BRICK_STAIRS = registerLegacyStair("mud_brick_stairs", MUD_BRICKS);
      MYCELIUM = register("mycelium", MyceliumBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).randomTicks().strength(0.6F).sound(SoundType.GRASS));
      LILY_PAD = register("lily_pad", WaterlilyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).instabreak().sound(SoundType.LILY_PAD).noOcclusion().pushReaction(PushReaction.DESTROY));
      RESIN_BLOCK = register("resin_block", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.RESIN));
      RESIN_BRICKS = register("resin_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().sound(SoundType.RESIN_BRICKS).strength(1.5F, 6.0F));
      RESIN_BRICK_STAIRS = registerLegacyStair("resin_brick_stairs", RESIN_BRICKS);
      RESIN_BRICK_SLAB = register("resin_brick_slab", (var0x) -> {
         return new SlabBlock(var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().sound(SoundType.RESIN_BRICKS).strength(1.5F, 6.0F));
      RESIN_BRICK_WALL = register("resin_brick_wall", (var0x) -> {
         return new WallBlock(var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().sound(SoundType.RESIN_BRICKS).strength(1.5F, 6.0F));
      CHISELED_RESIN_BRICKS = register("chiseled_resin_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().sound(SoundType.RESIN_BRICKS).strength(1.5F, 6.0F));
      NETHER_BRICKS = register("nether_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
      NETHER_BRICK_FENCE = register("nether_brick_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
      NETHER_BRICK_STAIRS = registerLegacyStair("nether_brick_stairs", NETHER_BRICKS);
      NETHER_WART = register("nether_wart", NetherWartBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).noCollission().randomTicks().sound(SoundType.NETHER_WART).pushReaction(PushReaction.DESTROY));
      ENCHANTING_TABLE = register("enchanting_table", EnchantingTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().lightLevel((var0x) -> {
         return 7;
      }).strength(5.0F, 1200.0F));
      BREWING_STAND = register("brewing_stand", BrewingStandBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.5F).lightLevel((var0x) -> {
         return 1;
      }).noOcclusion());
      CAULDRON = register("cauldron", CauldronBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
      WATER_CAULDRON = register("water_cauldron", (var0x) -> {
         return new LayeredCauldronBlock(Biome.Precipitation.RAIN, CauldronInteraction.WATER, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CAULDRON));
      LAVA_CAULDRON = register("lava_cauldron", LavaCauldronBlock::new, BlockBehaviour.Properties.ofLegacyCopy(CAULDRON).lightLevel((var0x) -> {
         return 15;
      }));
      POWDER_SNOW_CAULDRON = register("powder_snow_cauldron", (var0x) -> {
         return new LayeredCauldronBlock(Biome.Precipitation.SNOW, CauldronInteraction.POWDER_SNOW, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CAULDRON));
      END_PORTAL = register("end_portal", EndPortalBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission().lightLevel((var0x) -> {
         return 15;
      }).strength(-1.0F, 3600000.0F).noLootTable().pushReaction(PushReaction.BLOCK));
      END_PORTAL_FRAME = register("end_portal_frame", EndPortalFrameBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GLASS).lightLevel((var0x) -> {
         return 1;
      }).strength(-1.0F, 3600000.0F).noLootTable());
      END_STONE = register("end_stone", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 9.0F));
      DRAGON_EGG = register("dragon_egg", DragonEggBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0F, 9.0F).lightLevel((var0x) -> {
         return 1;
      }).noOcclusion().pushReaction(PushReaction.DESTROY));
      REDSTONE_LAMP = register("redstone_lamp", RedstoneLampBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).lightLevel(litBlockEmission(15)).strength(0.3F).sound(SoundType.GLASS).isValidSpawn(Blocks::always));
      COCOA = register("cocoa", CocoaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().strength(0.2F, 3.0F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY));
      SANDSTONE_STAIRS = registerLegacyStair("sandstone_stairs", SANDSTONE);
      EMERALD_ORE = register("emerald_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(3, 7), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
      DEEPSLATE_EMERALD_ORE = register("deepslate_emerald_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(3, 7), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(EMERALD_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      ENDER_CHEST = register("ender_chest", EnderChestBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(22.5F, 600.0F).lightLevel((var0x) -> {
         return 7;
      }));
      TRIPWIRE_HOOK = register("tripwire_hook", TripWireHookBlock::new, BlockBehaviour.Properties.of().noCollission().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY));
      TRIPWIRE = register("tripwire", (var0x) -> {
         return new TripWireBlock(TRIPWIRE_HOOK, var0x);
      }, BlockBehaviour.Properties.of().noCollission().pushReaction(PushReaction.DESTROY));
      EMERALD_BLOCK = register("emerald_block", BlockBehaviour.Properties.of().mapColor(MapColor.EMERALD).instrument(NoteBlockInstrument.BIT).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
      SPRUCE_STAIRS = registerLegacyStair("spruce_stairs", SPRUCE_PLANKS);
      BIRCH_STAIRS = registerLegacyStair("birch_stairs", BIRCH_PLANKS);
      JUNGLE_STAIRS = registerLegacyStair("jungle_stairs", JUNGLE_PLANKS);
      COMMAND_BLOCK = register("command_block", (var0x) -> {
         return new CommandBlock(false, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());
      BEACON = register("beacon", BeaconBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.HAT).strength(3.0F).lightLevel((var0x) -> {
         return 15;
      }).noOcclusion().isRedstoneConductor(Blocks::never));
      COBBLESTONE_WALL = register("cobblestone_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(COBBLESTONE).forceSolidOn());
      MOSSY_COBBLESTONE_WALL = register("mossy_cobblestone_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(COBBLESTONE).forceSolidOn());
      FLOWER_POT = register("flower_pot", (var0x) -> {
         return new FlowerPotBlock(AIR, var0x);
      }, flowerPotProperties());
      POTTED_TORCHFLOWER = register("potted_torchflower", (var0x) -> {
         return new FlowerPotBlock(TORCHFLOWER, var0x);
      }, flowerPotProperties());
      POTTED_OAK_SAPLING = register("potted_oak_sapling", (var0x) -> {
         return new FlowerPotBlock(OAK_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_SPRUCE_SAPLING = register("potted_spruce_sapling", (var0x) -> {
         return new FlowerPotBlock(SPRUCE_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_BIRCH_SAPLING = register("potted_birch_sapling", (var0x) -> {
         return new FlowerPotBlock(BIRCH_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_JUNGLE_SAPLING = register("potted_jungle_sapling", (var0x) -> {
         return new FlowerPotBlock(JUNGLE_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_ACACIA_SAPLING = register("potted_acacia_sapling", (var0x) -> {
         return new FlowerPotBlock(ACACIA_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_CHERRY_SAPLING = register("potted_cherry_sapling", (var0x) -> {
         return new FlowerPotBlock(CHERRY_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_DARK_OAK_SAPLING = register("potted_dark_oak_sapling", (var0x) -> {
         return new FlowerPotBlock(DARK_OAK_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_PALE_OAK_SAPLING = register("potted_pale_oak_sapling", (var0x) -> {
         return new FlowerPotBlock(PALE_OAK_SAPLING, var0x);
      }, flowerPotProperties());
      POTTED_MANGROVE_PROPAGULE = register("potted_mangrove_propagule", (var0x) -> {
         return new FlowerPotBlock(MANGROVE_PROPAGULE, var0x);
      }, flowerPotProperties());
      POTTED_FERN = register("potted_fern", (var0x) -> {
         return new FlowerPotBlock(FERN, var0x);
      }, flowerPotProperties());
      POTTED_DANDELION = register("potted_dandelion", (var0x) -> {
         return new FlowerPotBlock(DANDELION, var0x);
      }, flowerPotProperties());
      POTTED_POPPY = register("potted_poppy", (var0x) -> {
         return new FlowerPotBlock(POPPY, var0x);
      }, flowerPotProperties());
      POTTED_BLUE_ORCHID = register("potted_blue_orchid", (var0x) -> {
         return new FlowerPotBlock(BLUE_ORCHID, var0x);
      }, flowerPotProperties());
      POTTED_ALLIUM = register("potted_allium", (var0x) -> {
         return new FlowerPotBlock(ALLIUM, var0x);
      }, flowerPotProperties());
      POTTED_AZURE_BLUET = register("potted_azure_bluet", (var0x) -> {
         return new FlowerPotBlock(AZURE_BLUET, var0x);
      }, flowerPotProperties());
      POTTED_RED_TULIP = register("potted_red_tulip", (var0x) -> {
         return new FlowerPotBlock(RED_TULIP, var0x);
      }, flowerPotProperties());
      POTTED_ORANGE_TULIP = register("potted_orange_tulip", (var0x) -> {
         return new FlowerPotBlock(ORANGE_TULIP, var0x);
      }, flowerPotProperties());
      POTTED_WHITE_TULIP = register("potted_white_tulip", (var0x) -> {
         return new FlowerPotBlock(WHITE_TULIP, var0x);
      }, flowerPotProperties());
      POTTED_PINK_TULIP = register("potted_pink_tulip", (var0x) -> {
         return new FlowerPotBlock(PINK_TULIP, var0x);
      }, flowerPotProperties());
      POTTED_OXEYE_DAISY = register("potted_oxeye_daisy", (var0x) -> {
         return new FlowerPotBlock(OXEYE_DAISY, var0x);
      }, flowerPotProperties());
      POTTED_CORNFLOWER = register("potted_cornflower", (var0x) -> {
         return new FlowerPotBlock(CORNFLOWER, var0x);
      }, flowerPotProperties());
      POTTED_LILY_OF_THE_VALLEY = register("potted_lily_of_the_valley", (var0x) -> {
         return new FlowerPotBlock(LILY_OF_THE_VALLEY, var0x);
      }, flowerPotProperties());
      POTTED_WITHER_ROSE = register("potted_wither_rose", (var0x) -> {
         return new FlowerPotBlock(WITHER_ROSE, var0x);
      }, flowerPotProperties());
      POTTED_RED_MUSHROOM = register("potted_red_mushroom", (var0x) -> {
         return new FlowerPotBlock(RED_MUSHROOM, var0x);
      }, flowerPotProperties());
      POTTED_BROWN_MUSHROOM = register("potted_brown_mushroom", (var0x) -> {
         return new FlowerPotBlock(BROWN_MUSHROOM, var0x);
      }, flowerPotProperties());
      POTTED_DEAD_BUSH = register("potted_dead_bush", (var0x) -> {
         return new FlowerPotBlock(DEAD_BUSH, var0x);
      }, flowerPotProperties());
      POTTED_CACTUS = register("potted_cactus", (var0x) -> {
         return new FlowerPotBlock(CACTUS, var0x);
      }, flowerPotProperties());
      CARROTS = register("carrots", CarrotBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
      POTATOES = register("potatoes", PotatoBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
      OAK_BUTTON = register("oak_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.OAK, 30, var0x);
      }, buttonProperties());
      SPRUCE_BUTTON = register("spruce_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.SPRUCE, 30, var0x);
      }, buttonProperties());
      BIRCH_BUTTON = register("birch_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.BIRCH, 30, var0x);
      }, buttonProperties());
      JUNGLE_BUTTON = register("jungle_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.JUNGLE, 30, var0x);
      }, buttonProperties());
      ACACIA_BUTTON = register("acacia_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.ACACIA, 30, var0x);
      }, buttonProperties());
      CHERRY_BUTTON = register("cherry_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.CHERRY, 30, var0x);
      }, buttonProperties());
      DARK_OAK_BUTTON = register("dark_oak_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.DARK_OAK, 30, var0x);
      }, buttonProperties());
      PALE_OAK_BUTTON = register("pale_oak_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.PALE_OAK, 30, var0x);
      }, buttonProperties());
      MANGROVE_BUTTON = register("mangrove_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.MANGROVE, 30, var0x);
      }, buttonProperties());
      BAMBOO_BUTTON = register("bamboo_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.BAMBOO, 30, var0x);
      }, buttonProperties());
      SKELETON_SKULL = register("skeleton_skull", (var0x) -> {
         return new SkullBlock(SkullBlock.Types.SKELETON, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY));
      SKELETON_WALL_SKULL = register("skeleton_wall_skull", (var0x) -> {
         return new WallSkullBlock(SkullBlock.Types.SKELETON, var0x);
      }, wallVariant(SKELETON_SKULL, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      WITHER_SKELETON_SKULL = register("wither_skeleton_skull", WitherSkullBlock::new, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.WITHER_SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY));
      WITHER_SKELETON_WALL_SKULL = register("wither_skeleton_wall_skull", WitherWallSkullBlock::new, wallVariant(WITHER_SKELETON_SKULL, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      ZOMBIE_HEAD = register("zombie_head", (var0x) -> {
         return new SkullBlock(SkullBlock.Types.ZOMBIE, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.ZOMBIE).strength(1.0F).pushReaction(PushReaction.DESTROY));
      ZOMBIE_WALL_HEAD = register("zombie_wall_head", (var0x) -> {
         return new WallSkullBlock(SkullBlock.Types.ZOMBIE, var0x);
      }, wallVariant(ZOMBIE_HEAD, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      PLAYER_HEAD = register("player_head", PlayerHeadBlock::new, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CUSTOM_HEAD).strength(1.0F).pushReaction(PushReaction.DESTROY));
      PLAYER_WALL_HEAD = register("player_wall_head", PlayerWallHeadBlock::new, wallVariant(PLAYER_HEAD, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      CREEPER_HEAD = register("creeper_head", (var0x) -> {
         return new SkullBlock(SkullBlock.Types.CREEPER, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY));
      CREEPER_WALL_HEAD = register("creeper_wall_head", (var0x) -> {
         return new WallSkullBlock(SkullBlock.Types.CREEPER, var0x);
      }, wallVariant(CREEPER_HEAD, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      DRAGON_HEAD = register("dragon_head", (var0x) -> {
         return new SkullBlock(SkullBlock.Types.DRAGON, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.DRAGON).strength(1.0F).pushReaction(PushReaction.DESTROY));
      DRAGON_WALL_HEAD = register("dragon_wall_head", (var0x) -> {
         return new WallSkullBlock(SkullBlock.Types.DRAGON, var0x);
      }, wallVariant(DRAGON_HEAD, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      PIGLIN_HEAD = register("piglin_head", (var0x) -> {
         return new SkullBlock(SkullBlock.Types.PIGLIN, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.PIGLIN).strength(1.0F).pushReaction(PushReaction.DESTROY));
      PIGLIN_WALL_HEAD = register("piglin_wall_head", PiglinWallSkullBlock::new, wallVariant(PIGLIN_HEAD, true).strength(1.0F).pushReaction(PushReaction.DESTROY));
      ANVIL = register("anvil", AnvilBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK));
      CHIPPED_ANVIL = register("chipped_anvil", AnvilBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK));
      DAMAGED_ANVIL = register("damaged_anvil", AnvilBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL).pushReaction(PushReaction.BLOCK));
      TRAPPED_CHEST = register("trapped_chest", TrappedChestBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      LIGHT_WEIGHTED_PRESSURE_PLATE = register("light_weighted_pressure_plate", (var0x) -> {
         return new WeightedPressurePlateBlock(15, BlockSetType.GOLD, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).forceSolidOn().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
      HEAVY_WEIGHTED_PRESSURE_PLATE = register("heavy_weighted_pressure_plate", (var0x) -> {
         return new WeightedPressurePlateBlock(150, BlockSetType.IRON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
      COMPARATOR = register("comparator", ComparatorBlock::new, BlockBehaviour.Properties.of().instabreak().sound(SoundType.STONE).pushReaction(PushReaction.DESTROY));
      DAYLIGHT_DETECTOR = register("daylight_detector", DaylightDetectorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(0.2F).sound(SoundType.WOOD).ignitedByLava());
      REDSTONE_BLOCK = register("redstone_block", PoweredBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).isRedstoneConductor(Blocks::never));
      NETHER_QUARTZ_ORE = register("nether_quartz_ore", (var0x) -> {
         return new DropExperienceBlock(UniformInt.of(2, 5), var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.NETHER_ORE));
      HOPPER = register("hopper", HopperBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 4.8F).sound(SoundType.METAL).noOcclusion());
      QUARTZ_BLOCK = register("quartz_block", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      CHISELED_QUARTZ_BLOCK = register("chiseled_quartz_block", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      QUARTZ_PILLAR = register("quartz_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      QUARTZ_STAIRS = registerLegacyStair("quartz_stairs", QUARTZ_BLOCK);
      ACTIVATOR_RAIL = register("activator_rail", PoweredRailBlock::new, BlockBehaviour.Properties.of().noCollission().strength(0.7F).sound(SoundType.METAL));
      DROPPER = register("dropper", DropperBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F));
      WHITE_TERRACOTTA = register("white_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      ORANGE_TERRACOTTA = register("orange_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      MAGENTA_TERRACOTTA = register("magenta_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      LIGHT_BLUE_TERRACOTTA = register("light_blue_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      YELLOW_TERRACOTTA = register("yellow_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      LIME_TERRACOTTA = register("lime_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      PINK_TERRACOTTA = register("pink_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      GRAY_TERRACOTTA = register("gray_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      LIGHT_GRAY_TERRACOTTA = register("light_gray_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      CYAN_TERRACOTTA = register("cyan_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      PURPLE_TERRACOTTA = register("purple_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      BLUE_TERRACOTTA = register("blue_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      BROWN_TERRACOTTA = register("brown_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      GREEN_TERRACOTTA = register("green_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      RED_TERRACOTTA = register("red_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      BLACK_TERRACOTTA = register("black_terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      WHITE_STAINED_GLASS_PANE = register("white_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.WHITE, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      ORANGE_STAINED_GLASS_PANE = register("orange_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.ORANGE, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      MAGENTA_STAINED_GLASS_PANE = register("magenta_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.MAGENTA, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      LIGHT_BLUE_STAINED_GLASS_PANE = register("light_blue_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      YELLOW_STAINED_GLASS_PANE = register("yellow_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.YELLOW, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      LIME_STAINED_GLASS_PANE = register("lime_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.LIME, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      PINK_STAINED_GLASS_PANE = register("pink_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.PINK, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      GRAY_STAINED_GLASS_PANE = register("gray_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.GRAY, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      LIGHT_GRAY_STAINED_GLASS_PANE = register("light_gray_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.LIGHT_GRAY, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      CYAN_STAINED_GLASS_PANE = register("cyan_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.CYAN, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      PURPLE_STAINED_GLASS_PANE = register("purple_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.PURPLE, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      BLUE_STAINED_GLASS_PANE = register("blue_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.BLUE, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      BROWN_STAINED_GLASS_PANE = register("brown_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.BROWN, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      GREEN_STAINED_GLASS_PANE = register("green_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.GREEN, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      RED_STAINED_GLASS_PANE = register("red_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.RED, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      BLACK_STAINED_GLASS_PANE = register("black_stained_glass_pane", (var0x) -> {
         return new StainedGlassPaneBlock(DyeColor.BLACK, var0x);
      }, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
      ACACIA_STAIRS = registerLegacyStair("acacia_stairs", ACACIA_PLANKS);
      CHERRY_STAIRS = registerLegacyStair("cherry_stairs", CHERRY_PLANKS);
      DARK_OAK_STAIRS = registerLegacyStair("dark_oak_stairs", DARK_OAK_PLANKS);
      PALE_OAK_STAIRS = registerLegacyStair("pale_oak_stairs", PALE_OAK_PLANKS);
      MANGROVE_STAIRS = registerLegacyStair("mangrove_stairs", MANGROVE_PLANKS);
      BAMBOO_STAIRS = registerLegacyStair("bamboo_stairs", BAMBOO_PLANKS);
      BAMBOO_MOSAIC_STAIRS = registerLegacyStair("bamboo_mosaic_stairs", BAMBOO_MOSAIC);
      SLIME_BLOCK = register("slime_block", SlimeBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion());
      BARRIER = register("barrier", BarrierBlock::new, BlockBehaviour.Properties.of().strength(-1.0F, 3600000.8F).mapColor(waterloggedMapColor(MapColor.NONE)).noLootTable().noOcclusion().isValidSpawn(Blocks::never).noTerrainParticles().pushReaction(PushReaction.BLOCK));
      LIGHT = register("light", LightBlock::new, BlockBehaviour.Properties.of().replaceable().strength(-1.0F, 3600000.8F).mapColor(waterloggedMapColor(MapColor.NONE)).noLootTable().noOcclusion().lightLevel(LightBlock.LIGHT_EMISSION));
      IRON_TRAPDOOR = register("iron_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.IRON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F).noOcclusion().isValidSpawn(Blocks::never));
      PRISMARINE = register("prismarine", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      PRISMARINE_BRICKS = register("prismarine_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DARK_PRISMARINE = register("dark_prismarine", BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      PRISMARINE_STAIRS = registerLegacyStair("prismarine_stairs", PRISMARINE);
      PRISMARINE_BRICK_STAIRS = registerLegacyStair("prismarine_brick_stairs", PRISMARINE_BRICKS);
      DARK_PRISMARINE_STAIRS = registerLegacyStair("dark_prismarine_stairs", DARK_PRISMARINE);
      PRISMARINE_SLAB = register("prismarine_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      PRISMARINE_BRICK_SLAB = register("prismarine_brick_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DARK_PRISMARINE_SLAB = register("dark_prismarine_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      SEA_LANTERN = register("sea_lantern", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).lightLevel((var0x) -> {
         return 15;
      }).isRedstoneConductor(Blocks::never));
      HAY_BLOCK = register("hay_block", HayBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BANJO).strength(0.5F).sound(SoundType.GRASS));
      WHITE_CARPET = register("white_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.WHITE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      ORANGE_CARPET = register("orange_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.ORANGE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      MAGENTA_CARPET = register("magenta_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.MAGENTA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      LIGHT_BLUE_CARPET = register("light_blue_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.LIGHT_BLUE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      YELLOW_CARPET = register("yellow_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.YELLOW, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      LIME_CARPET = register("lime_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.LIME, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      PINK_CARPET = register("pink_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.PINK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      GRAY_CARPET = register("gray_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.GRAY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      LIGHT_GRAY_CARPET = register("light_gray_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.LIGHT_GRAY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      CYAN_CARPET = register("cyan_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.CYAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      PURPLE_CARPET = register("purple_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.PURPLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      BLUE_CARPET = register("blue_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.BLUE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      BROWN_CARPET = register("brown_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.BROWN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      GREEN_CARPET = register("green_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.GREEN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      RED_CARPET = register("red_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.RED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      BLACK_CARPET = register("black_carpet", (var0x) -> {
         return new WoolCarpetBlock(DyeColor.BLACK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.1F).sound(SoundType.WOOL).ignitedByLava());
      TERRACOTTA = register("terracotta", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.25F, 4.2F));
      COAL_BLOCK = register("coal_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
      PACKED_ICE = register("packed_ice", BlockBehaviour.Properties.of().mapColor(MapColor.ICE).instrument(NoteBlockInstrument.CHIME).friction(0.98F).strength(0.5F).sound(SoundType.GLASS));
      SUNFLOWER = register("sunflower", TallFlowerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      LILAC = register("lilac", TallFlowerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      ROSE_BUSH = register("rose_bush", TallFlowerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      PEONY = register("peony", TallFlowerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      TALL_GRASS = register("tall_grass", DoublePlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      LARGE_FERN = register("large_fern", DoublePlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      WHITE_BANNER = register("white_banner", (var0x) -> {
         return new BannerBlock(DyeColor.WHITE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      ORANGE_BANNER = register("orange_banner", (var0x) -> {
         return new BannerBlock(DyeColor.ORANGE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      MAGENTA_BANNER = register("magenta_banner", (var0x) -> {
         return new BannerBlock(DyeColor.MAGENTA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      LIGHT_BLUE_BANNER = register("light_blue_banner", (var0x) -> {
         return new BannerBlock(DyeColor.LIGHT_BLUE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      YELLOW_BANNER = register("yellow_banner", (var0x) -> {
         return new BannerBlock(DyeColor.YELLOW, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      LIME_BANNER = register("lime_banner", (var0x) -> {
         return new BannerBlock(DyeColor.LIME, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      PINK_BANNER = register("pink_banner", (var0x) -> {
         return new BannerBlock(DyeColor.PINK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      GRAY_BANNER = register("gray_banner", (var0x) -> {
         return new BannerBlock(DyeColor.GRAY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      LIGHT_GRAY_BANNER = register("light_gray_banner", (var0x) -> {
         return new BannerBlock(DyeColor.LIGHT_GRAY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      CYAN_BANNER = register("cyan_banner", (var0x) -> {
         return new BannerBlock(DyeColor.CYAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      PURPLE_BANNER = register("purple_banner", (var0x) -> {
         return new BannerBlock(DyeColor.PURPLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      BLUE_BANNER = register("blue_banner", (var0x) -> {
         return new BannerBlock(DyeColor.BLUE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      BROWN_BANNER = register("brown_banner", (var0x) -> {
         return new BannerBlock(DyeColor.BROWN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      GREEN_BANNER = register("green_banner", (var0x) -> {
         return new BannerBlock(DyeColor.GREEN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      RED_BANNER = register("red_banner", (var0x) -> {
         return new BannerBlock(DyeColor.RED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      BLACK_BANNER = register("black_banner", (var0x) -> {
         return new BannerBlock(DyeColor.BLACK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      WHITE_WALL_BANNER = register("white_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.WHITE, var0x);
      }, wallVariant(WHITE_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      ORANGE_WALL_BANNER = register("orange_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.ORANGE, var0x);
      }, wallVariant(ORANGE_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      MAGENTA_WALL_BANNER = register("magenta_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.MAGENTA, var0x);
      }, wallVariant(MAGENTA_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      LIGHT_BLUE_WALL_BANNER = register("light_blue_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.LIGHT_BLUE, var0x);
      }, wallVariant(LIGHT_BLUE_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      YELLOW_WALL_BANNER = register("yellow_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.YELLOW, var0x);
      }, wallVariant(YELLOW_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      LIME_WALL_BANNER = register("lime_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.LIME, var0x);
      }, wallVariant(LIME_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      PINK_WALL_BANNER = register("pink_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.PINK, var0x);
      }, wallVariant(PINK_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      GRAY_WALL_BANNER = register("gray_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.GRAY, var0x);
      }, wallVariant(GRAY_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      LIGHT_GRAY_WALL_BANNER = register("light_gray_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.LIGHT_GRAY, var0x);
      }, wallVariant(LIGHT_GRAY_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      CYAN_WALL_BANNER = register("cyan_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.CYAN, var0x);
      }, wallVariant(CYAN_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      PURPLE_WALL_BANNER = register("purple_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.PURPLE, var0x);
      }, wallVariant(PURPLE_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      BLUE_WALL_BANNER = register("blue_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.BLUE, var0x);
      }, wallVariant(BLUE_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      BROWN_WALL_BANNER = register("brown_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.BROWN, var0x);
      }, wallVariant(BROWN_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      GREEN_WALL_BANNER = register("green_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.GREEN, var0x);
      }, wallVariant(GREEN_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      RED_WALL_BANNER = register("red_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.RED, var0x);
      }, wallVariant(RED_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      BLACK_WALL_BANNER = register("black_wall_banner", (var0x) -> {
         return new WallBannerBlock(DyeColor.BLACK, var0x);
      }, wallVariant(BLACK_BANNER, true).mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
      RED_SANDSTONE = register("red_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      CHISELED_RED_SANDSTONE = register("chiseled_red_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      CUT_RED_SANDSTONE = register("cut_red_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F));
      RED_SANDSTONE_STAIRS = registerLegacyStair("red_sandstone_stairs", RED_SANDSTONE);
      OAK_SLAB = register("oak_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      SPRUCE_SLAB = register("spruce_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      BIRCH_SLAB = register("birch_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      JUNGLE_SLAB = register("jungle_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      ACACIA_SLAB = register("acacia_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      CHERRY_SLAB = register("cherry_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.CHERRY_WOOD).ignitedByLava());
      DARK_OAK_SLAB = register("dark_oak_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      PALE_OAK_SLAB = register("pale_oak_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      MANGROVE_SLAB = register("mangrove_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
      BAMBOO_SLAB = register("bamboo_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD).ignitedByLava());
      BAMBOO_MOSAIC_SLAB = register("bamboo_mosaic_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD).ignitedByLava());
      STONE_SLAB = register("stone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SMOOTH_STONE_SLAB = register("smooth_stone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SANDSTONE_SLAB = register("sandstone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      CUT_SANDSTONE_SLAB = register("cut_sandstone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      PETRIFIED_OAK_SLAB = register("petrified_oak_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      COBBLESTONE_SLAB = register("cobblestone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      BRICK_SLAB = register("brick_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      STONE_BRICK_SLAB = register("stone_brick_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      MUD_BRICK_SLAB = register("mud_brick_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 3.0F).sound(SoundType.MUD_BRICKS));
      NETHER_BRICK_SLAB = register("nether_brick_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
      QUARTZ_SLAB = register("quartz_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      RED_SANDSTONE_SLAB = register("red_sandstone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      CUT_RED_SANDSTONE_SLAB = register("cut_red_sandstone_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      PURPUR_SLAB = register("purpur_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SMOOTH_STONE = register("smooth_stone", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SMOOTH_SANDSTONE = register("smooth_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SMOOTH_QUARTZ = register("smooth_quartz", BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SMOOTH_RED_SANDSTONE = register("smooth_red_sandstone", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
      SPRUCE_FENCE_GATE = register("spruce_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(SPRUCE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      BIRCH_FENCE_GATE = register("birch_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BIRCH_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      JUNGLE_FENCE_GATE = register("jungle_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(JUNGLE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      ACACIA_FENCE_GATE = register("acacia_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(ACACIA_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      CHERRY_FENCE_GATE = register("cherry_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CHERRY_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      DARK_OAK_FENCE_GATE = register("dark_oak_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DARK_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      PALE_OAK_FENCE_GATE = register("pale_oak_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      MANGROVE_FENCE_GATE = register("mangrove_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MANGROVE_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      BAMBOO_FENCE_GATE = register("bamboo_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.BAMBOO, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BAMBOO_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava());
      SPRUCE_FENCE = register("spruce_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(SPRUCE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      BIRCH_FENCE = register("birch_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(BIRCH_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      JUNGLE_FENCE = register("jungle_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(JUNGLE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      ACACIA_FENCE = register("acacia_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(ACACIA_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      CHERRY_FENCE = register("cherry_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.CHERRY_WOOD));
      DARK_OAK_FENCE = register("dark_oak_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(DARK_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      PALE_OAK_FENCE = register("pale_oak_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      MANGROVE_FENCE = register("mangrove_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(MANGROVE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).ignitedByLava().sound(SoundType.WOOD));
      BAMBOO_FENCE = register("bamboo_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(BAMBOO_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.BAMBOO_WOOD).ignitedByLava());
      SPRUCE_DOOR = register("spruce_door", (var0x) -> {
         return new DoorBlock(BlockSetType.SPRUCE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(SPRUCE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      BIRCH_DOOR = register("birch_door", (var0x) -> {
         return new DoorBlock(BlockSetType.BIRCH, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BIRCH_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      JUNGLE_DOOR = register("jungle_door", (var0x) -> {
         return new DoorBlock(BlockSetType.JUNGLE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(JUNGLE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      ACACIA_DOOR = register("acacia_door", (var0x) -> {
         return new DoorBlock(BlockSetType.ACACIA, var0x);
      }, BlockBehaviour.Properties.of().mapColor(ACACIA_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      CHERRY_DOOR = register("cherry_door", (var0x) -> {
         return new DoorBlock(BlockSetType.CHERRY, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CHERRY_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      DARK_OAK_DOOR = register("dark_oak_door", (var0x) -> {
         return new DoorBlock(BlockSetType.DARK_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DARK_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      PALE_OAK_DOOR = register("pale_oak_door", (var0x) -> {
         return new DoorBlock(BlockSetType.PALE_OAK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      MANGROVE_DOOR = register("mangrove_door", (var0x) -> {
         return new DoorBlock(BlockSetType.MANGROVE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MANGROVE_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      BAMBOO_DOOR = register("bamboo_door", (var0x) -> {
         return new DoorBlock(BlockSetType.BAMBOO, var0x);
      }, BlockBehaviour.Properties.of().mapColor(BAMBOO_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
      END_ROD = register("end_rod", EndRodBlock::new, BlockBehaviour.Properties.of().forceSolidOff().instabreak().lightLevel((var0x) -> {
         return 14;
      }).sound(SoundType.WOOD).noOcclusion());
      CHORUS_PLANT = register("chorus_plant", ChorusPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).forceSolidOff().strength(0.4F).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY));
      CHORUS_FLOWER = register("chorus_flower", (var0x) -> {
         return new ChorusFlowerBlock(CHORUS_PLANT, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).forceSolidOff().randomTicks().strength(0.4F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(Blocks::never).pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));
      PURPUR_BLOCK = register("purpur_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      PURPUR_PILLAR = register("purpur_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      PURPUR_STAIRS = registerLegacyStair("purpur_stairs", PURPUR_BLOCK);
      END_STONE_BRICKS = register("end_stone_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 9.0F));
      TORCHFLOWER_CROP = register("torchflower_crop", TorchflowerCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
      PITCHER_CROP = register("pitcher_crop", PitcherCropBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
      PITCHER_PLANT = register("pitcher_plant", DoublePlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      BEETROOTS = register("beetroots", BeetrootBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY));
      DIRT_PATH = register("dirt_path", DirtPathBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.65F).sound(SoundType.GRASS).isViewBlocking(Blocks::always).isSuffocating(Blocks::always));
      END_GATEWAY = register("end_gateway", EndGatewayBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission().lightLevel((var0x) -> {
         return 15;
      }).strength(-1.0F, 3600000.0F).noLootTable().pushReaction(PushReaction.BLOCK));
      REPEATING_COMMAND_BLOCK = register("repeating_command_block", (var0x) -> {
         return new CommandBlock(false, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());
      CHAIN_COMMAND_BLOCK = register("chain_command_block", (var0x) -> {
         return new CommandBlock(true, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());
      FROSTED_ICE = register("frosted_ice", FrostedIceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.ICE).friction(0.98F).strength(0.5F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((var0x, var1x, var2x, var3x) -> {
         return var3x == EntityType.POLAR_BEAR;
      }).isRedstoneConductor(Blocks::never));
      MAGMA_BLOCK = register("magma_block", MagmaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().lightLevel((var0x) -> {
         return 3;
      }).strength(0.5F).isValidSpawn((var0x, var1x, var2x, var3x) -> {
         return var3x.fireImmune();
      }).hasPostProcess(Blocks::always).emissiveRendering(Blocks::always));
      NETHER_WART_BLOCK = register("nether_wart_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.0F).sound(SoundType.WART_BLOCK));
      RED_NETHER_BRICKS = register("red_nether_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
      BONE_BLOCK = register("bone_block", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.XYLOPHONE).requiresCorrectToolForDrops().strength(2.0F).sound(SoundType.BONE_BLOCK));
      STRUCTURE_VOID = register("structure_void", StructureVoidBlock::new, BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().noTerrainParticles().pushReaction(PushReaction.DESTROY));
      OBSERVER = register("observer", ObserverBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).requiresCorrectToolForDrops().isRedstoneConductor(Blocks::never));
      SHULKER_BOX = register("shulker_box", (var0x) -> {
         return new ShulkerBoxBlock((DyeColor)null, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_PURPLE));
      WHITE_SHULKER_BOX = register("white_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.WHITE, var0x);
      }, shulkerBoxProperties(MapColor.SNOW));
      ORANGE_SHULKER_BOX = register("orange_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.ORANGE, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_ORANGE));
      MAGENTA_SHULKER_BOX = register("magenta_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.MAGENTA, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_MAGENTA));
      LIGHT_BLUE_SHULKER_BOX = register("light_blue_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.LIGHT_BLUE, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_LIGHT_BLUE));
      YELLOW_SHULKER_BOX = register("yellow_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.YELLOW, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_YELLOW));
      LIME_SHULKER_BOX = register("lime_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.LIME, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_LIGHT_GREEN));
      PINK_SHULKER_BOX = register("pink_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.PINK, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_PINK));
      GRAY_SHULKER_BOX = register("gray_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.GRAY, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_GRAY));
      LIGHT_GRAY_SHULKER_BOX = register("light_gray_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.LIGHT_GRAY, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_LIGHT_GRAY));
      CYAN_SHULKER_BOX = register("cyan_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.CYAN, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_CYAN));
      PURPLE_SHULKER_BOX = register("purple_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.PURPLE, var0x);
      }, shulkerBoxProperties(MapColor.TERRACOTTA_PURPLE));
      BLUE_SHULKER_BOX = register("blue_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.BLUE, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_BLUE));
      BROWN_SHULKER_BOX = register("brown_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.BROWN, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_BROWN));
      GREEN_SHULKER_BOX = register("green_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.GREEN, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_GREEN));
      RED_SHULKER_BOX = register("red_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.RED, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_RED));
      BLACK_SHULKER_BOX = register("black_shulker_box", (var0x) -> {
         return new ShulkerBoxBlock(DyeColor.BLACK, var0x);
      }, shulkerBoxProperties(MapColor.COLOR_BLACK));
      WHITE_GLAZED_TERRACOTTA = register("white_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      ORANGE_GLAZED_TERRACOTTA = register("orange_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      MAGENTA_GLAZED_TERRACOTTA = register("magenta_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      LIGHT_BLUE_GLAZED_TERRACOTTA = register("light_blue_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      YELLOW_GLAZED_TERRACOTTA = register("yellow_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      LIME_GLAZED_TERRACOTTA = register("lime_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.LIME).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      PINK_GLAZED_TERRACOTTA = register("pink_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      GRAY_GLAZED_TERRACOTTA = register("gray_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      LIGHT_GRAY_GLAZED_TERRACOTTA = register("light_gray_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      CYAN_GLAZED_TERRACOTTA = register("cyan_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      PURPLE_GLAZED_TERRACOTTA = register("purple_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      BLUE_GLAZED_TERRACOTTA = register("blue_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      BROWN_GLAZED_TERRACOTTA = register("brown_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      GREEN_GLAZED_TERRACOTTA = register("green_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      RED_GLAZED_TERRACOTTA = register("red_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      BLACK_GLAZED_TERRACOTTA = register("black_glazed_terracotta", GlazedTerracottaBlock::new, BlockBehaviour.Properties.of().mapColor(DyeColor.BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.4F).pushReaction(PushReaction.PUSH_ONLY));
      WHITE_CONCRETE = register("white_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      ORANGE_CONCRETE = register("orange_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      MAGENTA_CONCRETE = register("magenta_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.MAGENTA).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      LIGHT_BLUE_CONCRETE = register("light_blue_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      YELLOW_CONCRETE = register("yellow_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      LIME_CONCRETE = register("lime_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.LIME).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      PINK_CONCRETE = register("pink_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      GRAY_CONCRETE = register("gray_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      LIGHT_GRAY_CONCRETE = register("light_gray_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      CYAN_CONCRETE = register("cyan_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.CYAN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      PURPLE_CONCRETE = register("purple_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      BLUE_CONCRETE = register("blue_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      BROWN_CONCRETE = register("brown_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      GREEN_CONCRETE = register("green_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      RED_CONCRETE = register("red_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      BLACK_CONCRETE = register("black_concrete", BlockBehaviour.Properties.of().mapColor(DyeColor.BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.8F));
      WHITE_CONCRETE_POWDER = register("white_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(WHITE_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      ORANGE_CONCRETE_POWDER = register("orange_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(ORANGE_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      MAGENTA_CONCRETE_POWDER = register("magenta_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(MAGENTA_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.MAGENTA).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      LIGHT_BLUE_CONCRETE_POWDER = register("light_blue_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(LIGHT_BLUE_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_BLUE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      YELLOW_CONCRETE_POWDER = register("yellow_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(YELLOW_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.YELLOW).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      LIME_CONCRETE_POWDER = register("lime_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(LIME_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.LIME).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      PINK_CONCRETE_POWDER = register("pink_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(PINK_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.PINK).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      GRAY_CONCRETE_POWDER = register("gray_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(GRAY_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.GRAY).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      LIGHT_GRAY_CONCRETE_POWDER = register("light_gray_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(LIGHT_GRAY_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_GRAY).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      CYAN_CONCRETE_POWDER = register("cyan_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(CYAN_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.CYAN).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      PURPLE_CONCRETE_POWDER = register("purple_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(PURPLE_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.PURPLE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      BLUE_CONCRETE_POWDER = register("blue_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(BLUE_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      BROWN_CONCRETE_POWDER = register("brown_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(BROWN_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      GREEN_CONCRETE_POWDER = register("green_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(GREEN_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      RED_CONCRETE_POWDER = register("red_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(RED_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.RED).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      BLACK_CONCRETE_POWDER = register("black_concrete_powder", (var0x) -> {
         return new ConcretePowderBlock(BLACK_CONCRETE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(DyeColor.BLACK).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
      KELP = register("kelp", KelpBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().randomTicks().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      KELP_PLANT = register("kelp_plant", KelpPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      DRIED_KELP_BLOCK = register("dried_kelp_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F, 2.5F).sound(SoundType.GRASS));
      TURTLE_EGG = register("turtle_egg", TurtleEggBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).forceSolidOn().strength(0.5F).sound(SoundType.METAL).randomTicks().noOcclusion().pushReaction(PushReaction.DESTROY));
      SNIFFER_EGG = register("sniffer_egg", SnifferEggBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F).sound(SoundType.METAL).noOcclusion());
      DEAD_TUBE_CORAL_BLOCK = register("dead_tube_coral_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DEAD_BRAIN_CORAL_BLOCK = register("dead_brain_coral_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DEAD_BUBBLE_CORAL_BLOCK = register("dead_bubble_coral_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DEAD_FIRE_CORAL_BLOCK = register("dead_fire_coral_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      DEAD_HORN_CORAL_BLOCK = register("dead_horn_coral_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      TUBE_CORAL_BLOCK = register("tube_coral_block", (var0x) -> {
         return new CoralBlock(DEAD_TUBE_CORAL_BLOCK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CORAL_BLOCK));
      BRAIN_CORAL_BLOCK = register("brain_coral_block", (var0x) -> {
         return new CoralBlock(DEAD_BRAIN_CORAL_BLOCK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CORAL_BLOCK));
      BUBBLE_CORAL_BLOCK = register("bubble_coral_block", (var0x) -> {
         return new CoralBlock(DEAD_BUBBLE_CORAL_BLOCK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CORAL_BLOCK));
      FIRE_CORAL_BLOCK = register("fire_coral_block", (var0x) -> {
         return new CoralBlock(DEAD_FIRE_CORAL_BLOCK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CORAL_BLOCK));
      HORN_CORAL_BLOCK = register("horn_coral_block", (var0x) -> {
         return new CoralBlock(DEAD_HORN_CORAL_BLOCK, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CORAL_BLOCK));
      DEAD_TUBE_CORAL = register("dead_tube_coral", BaseCoralPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_BRAIN_CORAL = register("dead_brain_coral", BaseCoralPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_BUBBLE_CORAL = register("dead_bubble_coral", BaseCoralPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_FIRE_CORAL = register("dead_fire_coral", BaseCoralPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_HORN_CORAL = register("dead_horn_coral", BaseCoralPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      TUBE_CORAL = register("tube_coral", (var0x) -> {
         return new CoralPlantBlock(DEAD_TUBE_CORAL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      BRAIN_CORAL = register("brain_coral", (var0x) -> {
         return new CoralPlantBlock(DEAD_BRAIN_CORAL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      BUBBLE_CORAL = register("bubble_coral", (var0x) -> {
         return new CoralPlantBlock(DEAD_BUBBLE_CORAL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      FIRE_CORAL = register("fire_coral", (var0x) -> {
         return new CoralPlantBlock(DEAD_FIRE_CORAL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      HORN_CORAL = register("horn_coral", (var0x) -> {
         return new CoralPlantBlock(DEAD_HORN_CORAL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      DEAD_TUBE_CORAL_FAN = register("dead_tube_coral_fan", BaseCoralFanBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_BRAIN_CORAL_FAN = register("dead_brain_coral_fan", BaseCoralFanBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_BUBBLE_CORAL_FAN = register("dead_bubble_coral_fan", BaseCoralFanBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_FIRE_CORAL_FAN = register("dead_fire_coral_fan", BaseCoralFanBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_HORN_CORAL_FAN = register("dead_horn_coral_fan", BaseCoralFanBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      TUBE_CORAL_FAN = register("tube_coral_fan", (var0x) -> {
         return new CoralFanBlock(DEAD_TUBE_CORAL_FAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      BRAIN_CORAL_FAN = register("brain_coral_fan", (var0x) -> {
         return new CoralFanBlock(DEAD_BRAIN_CORAL_FAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      BUBBLE_CORAL_FAN = register("bubble_coral_fan", (var0x) -> {
         return new CoralFanBlock(DEAD_BUBBLE_CORAL_FAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      FIRE_CORAL_FAN = register("fire_coral_fan", (var0x) -> {
         return new CoralFanBlock(DEAD_FIRE_CORAL_FAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      HORN_CORAL_FAN = register("horn_coral_fan", (var0x) -> {
         return new CoralFanBlock(DEAD_HORN_CORAL_FAN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      DEAD_TUBE_CORAL_WALL_FAN = register("dead_tube_coral_wall_fan", BaseCoralWallFanBlock::new, wallVariant(DEAD_TUBE_CORAL_FAN, false).mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_BRAIN_CORAL_WALL_FAN = register("dead_brain_coral_wall_fan", BaseCoralWallFanBlock::new, wallVariant(DEAD_BRAIN_CORAL_FAN, false).mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_BUBBLE_CORAL_WALL_FAN = register("dead_bubble_coral_wall_fan", BaseCoralWallFanBlock::new, wallVariant(DEAD_BUBBLE_CORAL_FAN, false).mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_FIRE_CORAL_WALL_FAN = register("dead_fire_coral_wall_fan", BaseCoralWallFanBlock::new, wallVariant(DEAD_FIRE_CORAL_FAN, false).mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      DEAD_HORN_CORAL_WALL_FAN = register("dead_horn_coral_wall_fan", BaseCoralWallFanBlock::new, wallVariant(DEAD_HORN_CORAL_FAN, false).mapColor(MapColor.COLOR_GRAY).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollission().instabreak());
      TUBE_CORAL_WALL_FAN = register("tube_coral_wall_fan", (var0x) -> {
         return new CoralWallFanBlock(DEAD_TUBE_CORAL_WALL_FAN, var0x);
      }, wallVariant(TUBE_CORAL_FAN, false).mapColor(MapColor.COLOR_BLUE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      BRAIN_CORAL_WALL_FAN = register("brain_coral_wall_fan", (var0x) -> {
         return new CoralWallFanBlock(DEAD_BRAIN_CORAL_WALL_FAN, var0x);
      }, wallVariant(BRAIN_CORAL_FAN, false).mapColor(MapColor.COLOR_PINK).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      BUBBLE_CORAL_WALL_FAN = register("bubble_coral_wall_fan", (var0x) -> {
         return new CoralWallFanBlock(DEAD_BUBBLE_CORAL_WALL_FAN, var0x);
      }, wallVariant(BUBBLE_CORAL_FAN, false).mapColor(MapColor.COLOR_PURPLE).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      FIRE_CORAL_WALL_FAN = register("fire_coral_wall_fan", (var0x) -> {
         return new CoralWallFanBlock(DEAD_FIRE_CORAL_WALL_FAN, var0x);
      }, wallVariant(FIRE_CORAL_FAN, false).mapColor(MapColor.COLOR_RED).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      HORN_CORAL_WALL_FAN = register("horn_coral_wall_fan", (var0x) -> {
         return new CoralWallFanBlock(DEAD_HORN_CORAL_WALL_FAN, var0x);
      }, wallVariant(HORN_CORAL_FAN, false).mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.DESTROY));
      SEA_PICKLE = register("sea_pickle", SeaPickleBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).lightLevel((var0x) -> {
         return SeaPickleBlock.isDead(var0x) ? 0 : 3 + 3 * (Integer)var0x.getValue(SeaPickleBlock.PICKLES);
      }).sound(SoundType.SLIME_BLOCK).noOcclusion().pushReaction(PushReaction.DESTROY));
      BLUE_ICE = register("blue_ice", HalfTransparentBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(2.8F).friction(0.989F).sound(SoundType.GLASS));
      CONDUIT = register("conduit", ConduitBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).forceSolidOn().instrument(NoteBlockInstrument.HAT).strength(3.0F).lightLevel((var0x) -> {
         return 15;
      }).noOcclusion());
      BAMBOO_SAPLING = register("bamboo_sapling", BambooSaplingBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().randomTicks().instabreak().noCollission().strength(1.0F).sound(SoundType.BAMBOO_SAPLING).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      BAMBOO = register("bamboo", BambooStalkBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).forceSolidOn().randomTicks().instabreak().strength(1.0F).sound(SoundType.BAMBOO).noOcclusion().dynamicShape().offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));
      POTTED_BAMBOO = register("potted_bamboo", (var0x) -> {
         return new FlowerPotBlock(BAMBOO, var0x);
      }, flowerPotProperties());
      VOID_AIR = register("void_air", AirBlock::new, BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air());
      CAVE_AIR = register("cave_air", AirBlock::new, BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air());
      BUBBLE_COLUMN = register("bubble_column", BubbleColumnBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).replaceable().noCollission().noLootTable().pushReaction(PushReaction.DESTROY).liquid().sound(SoundType.EMPTY));
      POLISHED_GRANITE_STAIRS = registerLegacyStair("polished_granite_stairs", POLISHED_GRANITE);
      SMOOTH_RED_SANDSTONE_STAIRS = registerLegacyStair("smooth_red_sandstone_stairs", SMOOTH_RED_SANDSTONE);
      MOSSY_STONE_BRICK_STAIRS = registerLegacyStair("mossy_stone_brick_stairs", MOSSY_STONE_BRICKS);
      POLISHED_DIORITE_STAIRS = registerLegacyStair("polished_diorite_stairs", POLISHED_DIORITE);
      MOSSY_COBBLESTONE_STAIRS = registerLegacyStair("mossy_cobblestone_stairs", MOSSY_COBBLESTONE);
      END_STONE_BRICK_STAIRS = registerLegacyStair("end_stone_brick_stairs", END_STONE_BRICKS);
      STONE_STAIRS = registerLegacyStair("stone_stairs", STONE);
      SMOOTH_SANDSTONE_STAIRS = registerLegacyStair("smooth_sandstone_stairs", SMOOTH_SANDSTONE);
      SMOOTH_QUARTZ_STAIRS = registerLegacyStair("smooth_quartz_stairs", SMOOTH_QUARTZ);
      GRANITE_STAIRS = registerLegacyStair("granite_stairs", GRANITE);
      ANDESITE_STAIRS = registerLegacyStair("andesite_stairs", ANDESITE);
      RED_NETHER_BRICK_STAIRS = registerLegacyStair("red_nether_brick_stairs", RED_NETHER_BRICKS);
      POLISHED_ANDESITE_STAIRS = registerLegacyStair("polished_andesite_stairs", POLISHED_ANDESITE);
      DIORITE_STAIRS = registerLegacyStair("diorite_stairs", DIORITE);
      POLISHED_GRANITE_SLAB = register("polished_granite_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_GRANITE));
      SMOOTH_RED_SANDSTONE_SLAB = register("smooth_red_sandstone_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(SMOOTH_RED_SANDSTONE));
      MOSSY_STONE_BRICK_SLAB = register("mossy_stone_brick_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(MOSSY_STONE_BRICKS));
      POLISHED_DIORITE_SLAB = register("polished_diorite_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_DIORITE));
      MOSSY_COBBLESTONE_SLAB = register("mossy_cobblestone_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(MOSSY_COBBLESTONE));
      END_STONE_BRICK_SLAB = register("end_stone_brick_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(END_STONE_BRICKS));
      SMOOTH_SANDSTONE_SLAB = register("smooth_sandstone_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(SMOOTH_SANDSTONE));
      SMOOTH_QUARTZ_SLAB = register("smooth_quartz_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(SMOOTH_QUARTZ));
      GRANITE_SLAB = register("granite_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(GRANITE));
      ANDESITE_SLAB = register("andesite_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(ANDESITE));
      RED_NETHER_BRICK_SLAB = register("red_nether_brick_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(RED_NETHER_BRICKS));
      POLISHED_ANDESITE_SLAB = register("polished_andesite_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_ANDESITE));
      DIORITE_SLAB = register("diorite_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DIORITE));
      BRICK_WALL = register("brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(BRICKS).forceSolidOn());
      PRISMARINE_WALL = register("prismarine_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(PRISMARINE).forceSolidOn());
      RED_SANDSTONE_WALL = register("red_sandstone_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(RED_SANDSTONE).forceSolidOn());
      MOSSY_STONE_BRICK_WALL = register("mossy_stone_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(MOSSY_STONE_BRICKS).forceSolidOn());
      GRANITE_WALL = register("granite_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(GRANITE).forceSolidOn());
      STONE_BRICK_WALL = register("stone_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(STONE_BRICKS).forceSolidOn());
      MUD_BRICK_WALL = register("mud_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(MUD_BRICKS).forceSolidOn());
      NETHER_BRICK_WALL = register("nether_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(NETHER_BRICKS).forceSolidOn());
      ANDESITE_WALL = register("andesite_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(ANDESITE).forceSolidOn());
      RED_NETHER_BRICK_WALL = register("red_nether_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(RED_NETHER_BRICKS).forceSolidOn());
      SANDSTONE_WALL = register("sandstone_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(SANDSTONE).forceSolidOn());
      END_STONE_BRICK_WALL = register("end_stone_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(END_STONE_BRICKS).forceSolidOn());
      DIORITE_WALL = register("diorite_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DIORITE).forceSolidOn());
      SCAFFOLDING = register("scaffolding", ScaffoldingBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).noCollission().sound(SoundType.SCAFFOLDING).dynamicShape().isValidSpawn(Blocks::never).pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));
      LOOM = register("loom", LoomBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      BARREL = register("barrel", BarrelBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      SMOKER = register("smoker", SmokerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13)));
      BLAST_FURNACE = register("blast_furnace", BlastFurnaceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13)));
      CARTOGRAPHY_TABLE = register("cartography_table", CartographyTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      FLETCHING_TABLE = register("fletching_table", FletchingTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      GRINDSTONE = register("grindstone", GrindstoneBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.STONE).pushReaction(PushReaction.BLOCK));
      LECTERN = register("lectern", LecternBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      SMITHING_TABLE = register("smithing_table", SmithingTableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava());
      STONECUTTER = register("stonecutter", StonecutterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F));
      BELL = register("bell", BellBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).forceSolidOn().strength(5.0F).sound(SoundType.ANVIL).pushReaction(PushReaction.DESTROY));
      LANTERN = register("lantern", LanternBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel((var0x) -> {
         return 15;
      }).noOcclusion().pushReaction(PushReaction.DESTROY));
      SOUL_LANTERN = register("soul_lantern", LanternBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel((var0x) -> {
         return 10;
      }).noOcclusion().pushReaction(PushReaction.DESTROY));
      CAMPFIRE = register("campfire", (var0x) -> {
         return new CampfireBlock(true, 1, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(litBlockEmission(15)).noOcclusion().ignitedByLava());
      SOUL_CAMPFIRE = register("soul_campfire", (var0x) -> {
         return new CampfireBlock(false, 2, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(litBlockEmission(10)).noOcclusion().ignitedByLava());
      SWEET_BERRY_BUSH = register("sweet_berry_bush", SweetBerryBushBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollission().sound(SoundType.SWEET_BERRY_BUSH).pushReaction(PushReaction.DESTROY));
      WARPED_STEM = register("warped_stem", RotatedPillarBlock::new, netherStemProperties(MapColor.WARPED_STEM));
      STRIPPED_WARPED_STEM = register("stripped_warped_stem", RotatedPillarBlock::new, netherStemProperties(MapColor.WARPED_STEM));
      WARPED_HYPHAE = register("warped_hyphae", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_HYPHAE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM));
      STRIPPED_WARPED_HYPHAE = register("stripped_warped_hyphae", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_HYPHAE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM));
      WARPED_NYLIUM = register("warped_nylium", NyliumBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_NYLIUM).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.4F).sound(SoundType.NYLIUM).randomTicks());
      WARPED_FUNGUS = register("warped_fungus", (var0x) -> {
         return new FungusBlock(TreeFeatures.WARPED_FUNGUS_PLANTED, WARPED_NYLIUM, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).instabreak().noCollission().sound(SoundType.FUNGUS).pushReaction(PushReaction.DESTROY));
      WARPED_WART_BLOCK = register("warped_wart_block", BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).strength(1.0F).sound(SoundType.WART_BLOCK));
      WARPED_ROOTS = register("warped_roots", RootsBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).replaceable().noCollission().instabreak().sound(SoundType.ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      NETHER_SPROUTS = register("nether_sprouts", NetherSproutsBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).replaceable().noCollission().instabreak().sound(SoundType.NETHER_SPROUTS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      CRIMSON_STEM = register("crimson_stem", RotatedPillarBlock::new, netherStemProperties(MapColor.CRIMSON_STEM));
      STRIPPED_CRIMSON_STEM = register("stripped_crimson_stem", RotatedPillarBlock::new, netherStemProperties(MapColor.CRIMSON_STEM));
      CRIMSON_HYPHAE = register("crimson_hyphae", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM));
      STRIPPED_CRIMSON_HYPHAE = register("stripped_crimson_hyphae", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_HYPHAE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM));
      CRIMSON_NYLIUM = register("crimson_nylium", NyliumBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_NYLIUM).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.4F).sound(SoundType.NYLIUM).randomTicks());
      CRIMSON_FUNGUS = register("crimson_fungus", (var0x) -> {
         return new FungusBlock(TreeFeatures.CRIMSON_FUNGUS_PLANTED, CRIMSON_NYLIUM, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instabreak().noCollission().sound(SoundType.FUNGUS).pushReaction(PushReaction.DESTROY));
      SHROOMLIGHT = register("shroomlight", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.0F).sound(SoundType.SHROOMLIGHT).lightLevel((var0x) -> {
         return 15;
      }));
      WEEPING_VINES = register("weeping_vines", WeepingVinesBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).randomTicks().noCollission().instabreak().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY));
      WEEPING_VINES_PLANT = register("weeping_vines_plant", WeepingVinesPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).noCollission().instabreak().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY));
      TWISTING_VINES = register("twisting_vines", TwistingVinesBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).randomTicks().noCollission().instabreak().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY));
      TWISTING_VINES_PLANT = register("twisting_vines_plant", TwistingVinesPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).noCollission().instabreak().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY));
      CRIMSON_ROOTS = register("crimson_roots", RootsBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).replaceable().noCollission().instabreak().sound(SoundType.ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
      CRIMSON_PLANKS = register("crimson_planks", BlockBehaviour.Properties.of().mapColor(MapColor.CRIMSON_STEM).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD));
      WARPED_PLANKS = register("warped_planks", BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_STEM).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD));
      CRIMSON_SLAB = register("crimson_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD));
      WARPED_SLAB = register("warped_slab", SlabBlock::new, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD));
      CRIMSON_PRESSURE_PLATE = register("crimson_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.CRIMSON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
      WARPED_PRESSURE_PLATE = register("warped_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.WARPED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
      CRIMSON_FENCE = register("crimson_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD));
      WARPED_FENCE = register("warped_fence", FenceBlock::new, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD));
      CRIMSON_TRAPDOOR = register("crimson_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.CRIMSON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never));
      WARPED_TRAPDOOR = register("warped_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.WARPED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(Blocks::never));
      CRIMSON_FENCE_GATE = register("crimson_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.CRIMSON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F));
      WARPED_FENCE_GATE = register("warped_fence_gate", (var0x) -> {
         return new FenceGateBlock(WoodType.WARPED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F));
      CRIMSON_STAIRS = registerLegacyStair("crimson_stairs", CRIMSON_PLANKS);
      WARPED_STAIRS = registerLegacyStair("warped_stairs", WARPED_PLANKS);
      CRIMSON_BUTTON = register("crimson_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.CRIMSON, 30, var0x);
      }, buttonProperties());
      WARPED_BUTTON = register("warped_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.WARPED, 30, var0x);
      }, buttonProperties());
      CRIMSON_DOOR = register("crimson_door", (var0x) -> {
         return new DoorBlock(BlockSetType.CRIMSON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
      WARPED_DOOR = register("warped_door", (var0x) -> {
         return new DoorBlock(BlockSetType.WARPED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
      CRIMSON_SIGN = register("crimson_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.CRIMSON, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F));
      WARPED_SIGN = register("warped_sign", (var0x) -> {
         return new StandingSignBlock(WoodType.WARPED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F));
      CRIMSON_WALL_SIGN = register("crimson_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.CRIMSON, var0x);
      }, wallVariant(CRIMSON_SIGN, true).mapColor(CRIMSON_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F));
      WARPED_WALL_SIGN = register("warped_wall_sign", (var0x) -> {
         return new WallSignBlock(WoodType.WARPED, var0x);
      }, wallVariant(WARPED_SIGN, true).mapColor(WARPED_PLANKS.defaultMapColor()).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollission().strength(1.0F));
      STRUCTURE_BLOCK = register("structure_block", StructureBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());
      JIGSAW = register("jigsaw", JigsawBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noLootTable());
      COMPOSTER = register("composter", ComposterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(0.6F).sound(SoundType.WOOD).ignitedByLava());
      TARGET = register("target", TargetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(0.5F).sound(SoundType.GRASS));
      BEE_NEST = register("bee_nest", BeehiveBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).strength(0.3F).sound(SoundType.WOOD).ignitedByLava());
      BEEHIVE = register("beehive", BeehiveBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(0.6F).sound(SoundType.WOOD).ignitedByLava());
      HONEY_BLOCK = register("honey_block", HoneyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).speedFactor(0.4F).jumpFactor(0.5F).noOcclusion().sound(SoundType.HONEY_BLOCK));
      HONEYCOMB_BLOCK = register("honeycomb_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.6F).sound(SoundType.CORAL_BLOCK));
      NETHERITE_BLOCK = register("netherite_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).sound(SoundType.NETHERITE_BLOCK));
      ANCIENT_DEBRIS = register("ancient_debris", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS));
      CRYING_OBSIDIAN = register("crying_obsidian", CryingObsidianBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel((var0x) -> {
         return 10;
      }));
      RESPAWN_ANCHOR = register("respawn_anchor", RespawnAnchorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).lightLevel((var0x) -> {
         return RespawnAnchorBlock.getScaledChargeLevel(var0x, 15);
      }));
      POTTED_CRIMSON_FUNGUS = register("potted_crimson_fungus", (var0x) -> {
         return new FlowerPotBlock(CRIMSON_FUNGUS, var0x);
      }, flowerPotProperties());
      POTTED_WARPED_FUNGUS = register("potted_warped_fungus", (var0x) -> {
         return new FlowerPotBlock(WARPED_FUNGUS, var0x);
      }, flowerPotProperties());
      POTTED_CRIMSON_ROOTS = register("potted_crimson_roots", (var0x) -> {
         return new FlowerPotBlock(CRIMSON_ROOTS, var0x);
      }, flowerPotProperties());
      POTTED_WARPED_ROOTS = register("potted_warped_roots", (var0x) -> {
         return new FlowerPotBlock(WARPED_ROOTS, var0x);
      }, flowerPotProperties());
      LODESTONE = register("lodestone", BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LODESTONE).pushReaction(PushReaction.BLOCK));
      BLACKSTONE = register("blackstone", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      BLACKSTONE_STAIRS = registerLegacyStair("blackstone_stairs", BLACKSTONE);
      BLACKSTONE_WALL = register("blackstone_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(BLACKSTONE).forceSolidOn());
      BLACKSTONE_SLAB = register("blackstone_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(BLACKSTONE).strength(2.0F, 6.0F));
      POLISHED_BLACKSTONE = register("polished_blackstone", BlockBehaviour.Properties.ofLegacyCopy(BLACKSTONE).strength(2.0F, 6.0F));
      POLISHED_BLACKSTONE_BRICKS = register("polished_blackstone_bricks", BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE).strength(1.5F, 6.0F));
      CRACKED_POLISHED_BLACKSTONE_BRICKS = register("cracked_polished_blackstone_bricks", BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE_BRICKS));
      CHISELED_POLISHED_BLACKSTONE = register("chiseled_polished_blackstone", BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE).strength(1.5F, 6.0F));
      POLISHED_BLACKSTONE_BRICK_SLAB = register("polished_blackstone_brick_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE_BRICKS).strength(2.0F, 6.0F));
      POLISHED_BLACKSTONE_BRICK_STAIRS = registerLegacyStair("polished_blackstone_brick_stairs", POLISHED_BLACKSTONE_BRICKS);
      POLISHED_BLACKSTONE_BRICK_WALL = register("polished_blackstone_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE_BRICKS).forceSolidOn());
      GILDED_BLACKSTONE = register("gilded_blackstone", BlockBehaviour.Properties.ofLegacyCopy(BLACKSTONE).sound(SoundType.GILDED_BLACKSTONE));
      POLISHED_BLACKSTONE_STAIRS = registerLegacyStair("polished_blackstone_stairs", POLISHED_BLACKSTONE);
      POLISHED_BLACKSTONE_SLAB = register("polished_blackstone_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE));
      POLISHED_BLACKSTONE_PRESSURE_PLATE = register("polished_blackstone_pressure_plate", (var0x) -> {
         return new PressurePlateBlock(BlockSetType.POLISHED_BLACKSTONE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
      POLISHED_BLACKSTONE_BUTTON = register("polished_blackstone_button", (var0x) -> {
         return new ButtonBlock(BlockSetType.STONE, 20, var0x);
      }, buttonProperties());
      POLISHED_BLACKSTONE_WALL = register("polished_blackstone_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_BLACKSTONE).forceSolidOn());
      CHISELED_NETHER_BRICKS = register("chiseled_nether_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
      CRACKED_NETHER_BRICKS = register("cracked_nether_bricks", BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
      QUARTZ_BRICKS = register("quartz_bricks", BlockBehaviour.Properties.ofLegacyCopy(QUARTZ_BLOCK));
      CANDLE = register("candle", CandleBlock::new, candleProperties(MapColor.SAND));
      WHITE_CANDLE = register("white_candle", CandleBlock::new, candleProperties(MapColor.WOOL));
      ORANGE_CANDLE = register("orange_candle", CandleBlock::new, candleProperties(MapColor.COLOR_ORANGE));
      MAGENTA_CANDLE = register("magenta_candle", CandleBlock::new, candleProperties(MapColor.COLOR_MAGENTA));
      LIGHT_BLUE_CANDLE = register("light_blue_candle", CandleBlock::new, candleProperties(MapColor.COLOR_LIGHT_BLUE));
      YELLOW_CANDLE = register("yellow_candle", CandleBlock::new, candleProperties(MapColor.COLOR_YELLOW));
      LIME_CANDLE = register("lime_candle", CandleBlock::new, candleProperties(MapColor.COLOR_LIGHT_GREEN));
      PINK_CANDLE = register("pink_candle", CandleBlock::new, candleProperties(MapColor.COLOR_PINK));
      GRAY_CANDLE = register("gray_candle", CandleBlock::new, candleProperties(MapColor.COLOR_GRAY));
      LIGHT_GRAY_CANDLE = register("light_gray_candle", CandleBlock::new, candleProperties(MapColor.COLOR_LIGHT_GRAY));
      CYAN_CANDLE = register("cyan_candle", CandleBlock::new, candleProperties(MapColor.COLOR_CYAN));
      PURPLE_CANDLE = register("purple_candle", CandleBlock::new, candleProperties(MapColor.COLOR_PURPLE));
      BLUE_CANDLE = register("blue_candle", CandleBlock::new, candleProperties(MapColor.COLOR_BLUE));
      BROWN_CANDLE = register("brown_candle", CandleBlock::new, candleProperties(MapColor.COLOR_BROWN));
      GREEN_CANDLE = register("green_candle", CandleBlock::new, candleProperties(MapColor.COLOR_GREEN));
      RED_CANDLE = register("red_candle", CandleBlock::new, candleProperties(MapColor.COLOR_RED));
      BLACK_CANDLE = register("black_candle", CandleBlock::new, candleProperties(MapColor.COLOR_BLACK));
      CANDLE_CAKE = register("candle_cake", (var0x) -> {
         return new CandleCakeBlock(CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CAKE).lightLevel(litBlockEmission(3)));
      WHITE_CANDLE_CAKE = register("white_candle_cake", (var0x) -> {
         return new CandleCakeBlock(WHITE_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      ORANGE_CANDLE_CAKE = register("orange_candle_cake", (var0x) -> {
         return new CandleCakeBlock(ORANGE_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      MAGENTA_CANDLE_CAKE = register("magenta_candle_cake", (var0x) -> {
         return new CandleCakeBlock(MAGENTA_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      LIGHT_BLUE_CANDLE_CAKE = register("light_blue_candle_cake", (var0x) -> {
         return new CandleCakeBlock(LIGHT_BLUE_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      YELLOW_CANDLE_CAKE = register("yellow_candle_cake", (var0x) -> {
         return new CandleCakeBlock(YELLOW_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      LIME_CANDLE_CAKE = register("lime_candle_cake", (var0x) -> {
         return new CandleCakeBlock(LIME_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      PINK_CANDLE_CAKE = register("pink_candle_cake", (var0x) -> {
         return new CandleCakeBlock(PINK_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      GRAY_CANDLE_CAKE = register("gray_candle_cake", (var0x) -> {
         return new CandleCakeBlock(GRAY_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      LIGHT_GRAY_CANDLE_CAKE = register("light_gray_candle_cake", (var0x) -> {
         return new CandleCakeBlock(LIGHT_GRAY_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      CYAN_CANDLE_CAKE = register("cyan_candle_cake", (var0x) -> {
         return new CandleCakeBlock(CYAN_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      PURPLE_CANDLE_CAKE = register("purple_candle_cake", (var0x) -> {
         return new CandleCakeBlock(PURPLE_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      BLUE_CANDLE_CAKE = register("blue_candle_cake", (var0x) -> {
         return new CandleCakeBlock(BLUE_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      BROWN_CANDLE_CAKE = register("brown_candle_cake", (var0x) -> {
         return new CandleCakeBlock(BROWN_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      GREEN_CANDLE_CAKE = register("green_candle_cake", (var0x) -> {
         return new CandleCakeBlock(GREEN_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      RED_CANDLE_CAKE = register("red_candle_cake", (var0x) -> {
         return new CandleCakeBlock(RED_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      BLACK_CANDLE_CAKE = register("black_candle_cake", (var0x) -> {
         return new CandleCakeBlock(BLACK_CANDLE, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(CANDLE_CAKE));
      AMETHYST_BLOCK = register("amethyst_block", AmethystBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops());
      BUDDING_AMETHYST = register("budding_amethyst", BuddingAmethystBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).randomTicks().strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().pushReaction(PushReaction.DESTROY));
      AMETHYST_CLUSTER = register("amethyst_cluster", (var0x) -> {
         return new AmethystClusterBlock(7.0F, 3.0F, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).forceSolidOn().noOcclusion().sound(SoundType.AMETHYST_CLUSTER).strength(1.5F).lightLevel((var0x) -> {
         return 5;
      }).pushReaction(PushReaction.DESTROY));
      LARGE_AMETHYST_BUD = register("large_amethyst_bud", (var0x) -> {
         return new AmethystClusterBlock(5.0F, 3.0F, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(AMETHYST_CLUSTER).sound(SoundType.MEDIUM_AMETHYST_BUD).lightLevel((var0x) -> {
         return 4;
      }));
      MEDIUM_AMETHYST_BUD = register("medium_amethyst_bud", (var0x) -> {
         return new AmethystClusterBlock(4.0F, 3.0F, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(AMETHYST_CLUSTER).sound(SoundType.LARGE_AMETHYST_BUD).lightLevel((var0x) -> {
         return 2;
      }));
      SMALL_AMETHYST_BUD = register("small_amethyst_bud", (var0x) -> {
         return new AmethystClusterBlock(3.0F, 4.0F, var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(AMETHYST_CLUSTER).sound(SoundType.SMALL_AMETHYST_BUD).lightLevel((var0x) -> {
         return 1;
      }));
      TUFF = register("tuff", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.TUFF).requiresCorrectToolForDrops().strength(1.5F, 6.0F));
      TUFF_SLAB = register("tuff_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(TUFF));
      TUFF_STAIRS = register("tuff_stairs", (var0x) -> {
         return new StairBlock(TUFF.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(TUFF));
      TUFF_WALL = register("tuff_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(TUFF).forceSolidOn());
      POLISHED_TUFF = register("polished_tuff", BlockBehaviour.Properties.ofLegacyCopy(TUFF).sound(SoundType.POLISHED_TUFF));
      POLISHED_TUFF_SLAB = register("polished_tuff_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_TUFF));
      POLISHED_TUFF_STAIRS = register("polished_tuff_stairs", (var0x) -> {
         return new StairBlock(POLISHED_TUFF.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_TUFF));
      POLISHED_TUFF_WALL = register("polished_tuff_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_TUFF).forceSolidOn());
      CHISELED_TUFF = register("chiseled_tuff", BlockBehaviour.Properties.ofLegacyCopy(TUFF));
      TUFF_BRICKS = register("tuff_bricks", BlockBehaviour.Properties.ofLegacyCopy(TUFF).sound(SoundType.TUFF_BRICKS));
      TUFF_BRICK_SLAB = register("tuff_brick_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(TUFF_BRICKS));
      TUFF_BRICK_STAIRS = register("tuff_brick_stairs", (var0x) -> {
         return new StairBlock(TUFF_BRICKS.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(TUFF_BRICKS));
      TUFF_BRICK_WALL = register("tuff_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(TUFF_BRICKS).forceSolidOn());
      CHISELED_TUFF_BRICKS = register("chiseled_tuff_bricks", BlockBehaviour.Properties.ofLegacyCopy(TUFF_BRICKS));
      CALCITE = register("calcite", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.CALCITE).requiresCorrectToolForDrops().strength(0.75F));
      TINTED_GLASS = register("tinted_glass", TintedGlassBlock::new, BlockBehaviour.Properties.ofLegacyCopy(GLASS).mapColor(MapColor.COLOR_GRAY).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never));
      POWDER_SNOW = register("powder_snow", PowderSnowBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.25F).sound(SoundType.POWDER_SNOW).dynamicShape().noOcclusion().isRedstoneConductor(Blocks::never));
      SCULK_SENSOR = register("sculk_sensor", SculkSensorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.5F).sound(SoundType.SCULK_SENSOR).lightLevel((var0x) -> {
         return 1;
      }).emissiveRendering((var0x, var1x, var2x) -> {
         return SculkSensorBlock.getPhase(var0x) == SculkSensorPhase.ACTIVE;
      }));
      CALIBRATED_SCULK_SENSOR = register("calibrated_sculk_sensor", CalibratedSculkSensorBlock::new, BlockBehaviour.Properties.ofLegacyCopy(SCULK_SENSOR));
      SCULK = register("sculk", SculkBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.2F).sound(SoundType.SCULK));
      SCULK_VEIN = register("sculk_vein", SculkVeinBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).forceSolidOn().noCollission().strength(0.2F).sound(SoundType.SCULK_VEIN).pushReaction(PushReaction.DESTROY));
      SCULK_CATALYST = register("sculk_catalyst", SculkCatalystBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0F, 3.0F).sound(SoundType.SCULK_CATALYST).lightLevel((var0x) -> {
         return 6;
      }));
      SCULK_SHRIEKER = register("sculk_shrieker", SculkShriekerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.0F, 3.0F).sound(SoundType.SCULK_SHRIEKER));
      COPPER_BLOCK = register("copper_block", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER));
      EXPOSED_COPPER = register("exposed_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY));
      WEATHERED_COPPER = register("weathered_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK).mapColor(MapColor.WARPED_STEM));
      OXIDIZED_COPPER = register("oxidized_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK).mapColor(MapColor.WARPED_NYLIUM));
      COPPER_ORE = register("copper_ore", (var0x) -> {
         return new DropExperienceBlock(ConstantInt.of(0), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(IRON_ORE));
      DEEPSLATE_COPPER_ORE = register("deepslate_copper_ore", (var0x) -> {
         return new DropExperienceBlock(ConstantInt.of(0), var0x);
      }, BlockBehaviour.Properties.ofLegacyCopy(COPPER_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE));
      OXIDIZED_CUT_COPPER = register("oxidized_cut_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER));
      WEATHERED_CUT_COPPER = register("weathered_cut_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER));
      EXPOSED_CUT_COPPER = register("exposed_cut_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER));
      CUT_COPPER = register("cut_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK));
      OXIDIZED_CHISELED_COPPER = register("oxidized_chiseled_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER));
      WEATHERED_CHISELED_COPPER = register("weathered_chiseled_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER));
      EXPOSED_CHISELED_COPPER = register("exposed_chiseled_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER));
      CHISELED_COPPER = register("chiseled_copper", (var0x) -> {
         return new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK));
      WAXED_OXIDIZED_CHISELED_COPPER = register("waxed_oxidized_chiseled_copper", BlockBehaviour.Properties.ofFullCopy(OXIDIZED_CHISELED_COPPER));
      WAXED_WEATHERED_CHISELED_COPPER = register("waxed_weathered_chiseled_copper", BlockBehaviour.Properties.ofFullCopy(WEATHERED_CHISELED_COPPER));
      WAXED_EXPOSED_CHISELED_COPPER = register("waxed_exposed_chiseled_copper", BlockBehaviour.Properties.ofFullCopy(EXPOSED_CHISELED_COPPER));
      WAXED_CHISELED_COPPER = register("waxed_chiseled_copper", BlockBehaviour.Properties.ofFullCopy(CHISELED_COPPER));
      OXIDIZED_CUT_COPPER_STAIRS = register("oxidized_cut_copper_stairs", (var0x) -> {
         return new WeatheringCopperStairBlock(WeatheringCopper.WeatherState.OXIDIZED, OXIDIZED_CUT_COPPER.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_CUT_COPPER));
      WEATHERED_CUT_COPPER_STAIRS = register("weathered_cut_copper_stairs", (var0x) -> {
         return new WeatheringCopperStairBlock(WeatheringCopper.WeatherState.WEATHERED, WEATHERED_CUT_COPPER.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER));
      EXPOSED_CUT_COPPER_STAIRS = register("exposed_cut_copper_stairs", (var0x) -> {
         return new WeatheringCopperStairBlock(WeatheringCopper.WeatherState.EXPOSED, EXPOSED_CUT_COPPER.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER));
      CUT_COPPER_STAIRS = register("cut_copper_stairs", (var0x) -> {
         return new WeatheringCopperStairBlock(WeatheringCopper.WeatherState.UNAFFECTED, CUT_COPPER.defaultBlockState(), var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK));
      OXIDIZED_CUT_COPPER_SLAB = register("oxidized_cut_copper_slab", (var0x) -> {
         return new WeatheringCopperSlabBlock(WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_CUT_COPPER));
      WEATHERED_CUT_COPPER_SLAB = register("weathered_cut_copper_slab", (var0x) -> {
         return new WeatheringCopperSlabBlock(WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(WEATHERED_CUT_COPPER));
      EXPOSED_CUT_COPPER_SLAB = register("exposed_cut_copper_slab", (var0x) -> {
         return new WeatheringCopperSlabBlock(WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(EXPOSED_CUT_COPPER));
      CUT_COPPER_SLAB = register("cut_copper_slab", (var0x) -> {
         return new WeatheringCopperSlabBlock(WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(CUT_COPPER));
      WAXED_COPPER_BLOCK = register("waxed_copper_block", BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK));
      WAXED_WEATHERED_COPPER = register("waxed_weathered_copper", BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER));
      WAXED_EXPOSED_COPPER = register("waxed_exposed_copper", BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER));
      WAXED_OXIDIZED_COPPER = register("waxed_oxidized_copper", BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER));
      WAXED_OXIDIZED_CUT_COPPER = register("waxed_oxidized_cut_copper", BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER));
      WAXED_WEATHERED_CUT_COPPER = register("waxed_weathered_cut_copper", BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER));
      WAXED_EXPOSED_CUT_COPPER = register("waxed_exposed_cut_copper", BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER));
      WAXED_CUT_COPPER = register("waxed_cut_copper", BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK));
      WAXED_OXIDIZED_CUT_COPPER_STAIRS = registerStair("waxed_oxidized_cut_copper_stairs", WAXED_OXIDIZED_CUT_COPPER);
      WAXED_WEATHERED_CUT_COPPER_STAIRS = registerStair("waxed_weathered_cut_copper_stairs", WAXED_WEATHERED_CUT_COPPER);
      WAXED_EXPOSED_CUT_COPPER_STAIRS = registerStair("waxed_exposed_cut_copper_stairs", WAXED_EXPOSED_CUT_COPPER);
      WAXED_CUT_COPPER_STAIRS = registerStair("waxed_cut_copper_stairs", WAXED_CUT_COPPER);
      WAXED_OXIDIZED_CUT_COPPER_SLAB = register("waxed_oxidized_cut_copper_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(WAXED_OXIDIZED_CUT_COPPER).requiresCorrectToolForDrops());
      WAXED_WEATHERED_CUT_COPPER_SLAB = register("waxed_weathered_cut_copper_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(WAXED_WEATHERED_CUT_COPPER).requiresCorrectToolForDrops());
      WAXED_EXPOSED_CUT_COPPER_SLAB = register("waxed_exposed_cut_copper_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(WAXED_EXPOSED_CUT_COPPER).requiresCorrectToolForDrops());
      WAXED_CUT_COPPER_SLAB = register("waxed_cut_copper_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(WAXED_CUT_COPPER).requiresCorrectToolForDrops());
      COPPER_DOOR = register("copper_door", (var0x) -> {
         return new WeatheringCopperDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(COPPER_BLOCK.defaultMapColor()).strength(3.0F, 6.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
      EXPOSED_COPPER_DOOR = register("exposed_copper_door", (var0x) -> {
         return new WeatheringCopperDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_DOOR).mapColor(EXPOSED_COPPER.defaultMapColor()));
      OXIDIZED_COPPER_DOOR = register("oxidized_copper_door", (var0x) -> {
         return new WeatheringCopperDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_DOOR).mapColor(OXIDIZED_COPPER.defaultMapColor()));
      WEATHERED_COPPER_DOOR = register("weathered_copper_door", (var0x) -> {
         return new WeatheringCopperDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_DOOR).mapColor(WEATHERED_COPPER.defaultMapColor()));
      WAXED_COPPER_DOOR = register("waxed_copper_door", (var0x) -> {
         return new DoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_DOOR));
      WAXED_EXPOSED_COPPER_DOOR = register("waxed_exposed_copper_door", (var0x) -> {
         return new DoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER_DOOR));
      WAXED_OXIDIZED_COPPER_DOOR = register("waxed_oxidized_copper_door", (var0x) -> {
         return new DoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER_DOOR));
      WAXED_WEATHERED_COPPER_DOOR = register("waxed_weathered_copper_door", (var0x) -> {
         return new DoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER_DOOR));
      COPPER_TRAPDOOR = register("copper_trapdoor", (var0x) -> {
         return new WeatheringCopperTrapDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(COPPER_BLOCK.defaultMapColor()).strength(3.0F, 6.0F).requiresCorrectToolForDrops().noOcclusion().isValidSpawn(Blocks::never));
      EXPOSED_COPPER_TRAPDOOR = register("exposed_copper_trapdoor", (var0x) -> {
         return new WeatheringCopperTrapDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_TRAPDOOR).mapColor(EXPOSED_COPPER.defaultMapColor()));
      OXIDIZED_COPPER_TRAPDOOR = register("oxidized_copper_trapdoor", (var0x) -> {
         return new WeatheringCopperTrapDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_TRAPDOOR).mapColor(OXIDIZED_COPPER.defaultMapColor()));
      WEATHERED_COPPER_TRAPDOOR = register("weathered_copper_trapdoor", (var0x) -> {
         return new WeatheringCopperTrapDoorBlock(BlockSetType.COPPER, WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_TRAPDOOR).mapColor(WEATHERED_COPPER.defaultMapColor()));
      WAXED_COPPER_TRAPDOOR = register("waxed_copper_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_TRAPDOOR));
      WAXED_EXPOSED_COPPER_TRAPDOOR = register("waxed_exposed_copper_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER_TRAPDOOR));
      WAXED_OXIDIZED_COPPER_TRAPDOOR = register("waxed_oxidized_copper_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER_TRAPDOOR));
      WAXED_WEATHERED_COPPER_TRAPDOOR = register("waxed_weathered_copper_trapdoor", (var0x) -> {
         return new TrapDoorBlock(BlockSetType.COPPER, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER_TRAPDOOR));
      COPPER_GRATE = register("copper_grate", (var0x) -> {
         return new WeatheringCopperGrateBlock(WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.of().strength(3.0F, 6.0F).sound(SoundType.COPPER_GRATE).mapColor(MapColor.COLOR_ORANGE).noOcclusion().requiresCorrectToolForDrops().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never));
      EXPOSED_COPPER_GRATE = register("exposed_copper_grate", (var0x) -> {
         return new WeatheringCopperGrateBlock(WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_GRATE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY));
      WEATHERED_COPPER_GRATE = register("weathered_copper_grate", (var0x) -> {
         return new WeatheringCopperGrateBlock(WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_GRATE).mapColor(MapColor.WARPED_STEM));
      OXIDIZED_COPPER_GRATE = register("oxidized_copper_grate", (var0x) -> {
         return new WeatheringCopperGrateBlock(WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_GRATE).mapColor(MapColor.WARPED_NYLIUM));
      WAXED_COPPER_GRATE = register("waxed_copper_grate", WaterloggedTransparentBlock::new, BlockBehaviour.Properties.ofFullCopy(COPPER_GRATE));
      WAXED_EXPOSED_COPPER_GRATE = register("waxed_exposed_copper_grate", WaterloggedTransparentBlock::new, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER_GRATE));
      WAXED_WEATHERED_COPPER_GRATE = register("waxed_weathered_copper_grate", WaterloggedTransparentBlock::new, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER_GRATE));
      WAXED_OXIDIZED_COPPER_GRATE = register("waxed_oxidized_copper_grate", WaterloggedTransparentBlock::new, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER_GRATE));
      COPPER_BULB = register("copper_bulb", (var0x) -> {
         return new WeatheringCopperBulbBlock(WeatheringCopper.WeatherState.UNAFFECTED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(COPPER_BLOCK.defaultMapColor()).strength(3.0F, 6.0F).sound(SoundType.COPPER_BULB).requiresCorrectToolForDrops().isRedstoneConductor(Blocks::never).lightLevel(litBlockEmission(15)));
      EXPOSED_COPPER_BULB = register("exposed_copper_bulb", (var0x) -> {
         return new WeatheringCopperBulbBlock(WeatheringCopper.WeatherState.EXPOSED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BULB).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).lightLevel(litBlockEmission(12)));
      WEATHERED_COPPER_BULB = register("weathered_copper_bulb", (var0x) -> {
         return new WeatheringCopperBulbBlock(WeatheringCopper.WeatherState.WEATHERED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BULB).mapColor(MapColor.WARPED_STEM).lightLevel(litBlockEmission(8)));
      OXIDIZED_COPPER_BULB = register("oxidized_copper_bulb", (var0x) -> {
         return new WeatheringCopperBulbBlock(WeatheringCopper.WeatherState.OXIDIZED, var0x);
      }, BlockBehaviour.Properties.ofFullCopy(COPPER_BULB).mapColor(MapColor.WARPED_NYLIUM).lightLevel(litBlockEmission(4)));
      WAXED_COPPER_BULB = register("waxed_copper_bulb", CopperBulbBlock::new, BlockBehaviour.Properties.ofFullCopy(COPPER_BULB));
      WAXED_EXPOSED_COPPER_BULB = register("waxed_exposed_copper_bulb", CopperBulbBlock::new, BlockBehaviour.Properties.ofFullCopy(EXPOSED_COPPER_BULB));
      WAXED_WEATHERED_COPPER_BULB = register("waxed_weathered_copper_bulb", CopperBulbBlock::new, BlockBehaviour.Properties.ofFullCopy(WEATHERED_COPPER_BULB));
      WAXED_OXIDIZED_COPPER_BULB = register("waxed_oxidized_copper_bulb", CopperBulbBlock::new, BlockBehaviour.Properties.ofFullCopy(OXIDIZED_COPPER_BULB));
      LIGHTNING_ROD = register("lightning_rod", LightningRodBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).forceSolidOn().requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion());
      POINTED_DRIPSTONE = register("pointed_dripstone", PointedDripstoneBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).noOcclusion().sound(SoundType.POINTED_DRIPSTONE).randomTicks().strength(1.5F, 3.0F).dynamicShape().offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));
      DRIPSTONE_BLOCK = register("dripstone_block", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DRIPSTONE_BLOCK).requiresCorrectToolForDrops().strength(1.5F, 1.0F));
      CAVE_VINES = register("cave_vines", CaveVinesBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollission().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY));
      CAVE_VINES_PLANT = register("cave_vines_plant", CaveVinesPlantBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY));
      SPORE_BLOSSOM = register("spore_blossom", SporeBlossomBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).instabreak().noCollission().sound(SoundType.SPORE_BLOSSOM).pushReaction(PushReaction.DESTROY));
      AZALEA = register("azalea", AzaleaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).forceSolidOff().instabreak().sound(SoundType.AZALEA).noOcclusion().pushReaction(PushReaction.DESTROY));
      FLOWERING_AZALEA = register("flowering_azalea", AzaleaBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).forceSolidOff().instabreak().sound(SoundType.FLOWERING_AZALEA).noOcclusion().pushReaction(PushReaction.DESTROY));
      MOSS_CARPET = register("moss_carpet", CarpetBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.1F).sound(SoundType.MOSS_CARPET).pushReaction(PushReaction.DESTROY));
      PINK_PETALS = register("pink_petals", PinkPetalsBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY));
      MOSS_BLOCK = register("moss_block", (var0x) -> {
         return new BonemealableFeaturePlacerBlock(CaveFeatures.MOSS_PATCH_BONEMEAL, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.1F).sound(SoundType.MOSS).pushReaction(PushReaction.DESTROY));
      BIG_DRIPLEAF = register("big_dripleaf", BigDripleafBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).forceSolidOff().strength(0.1F).sound(SoundType.BIG_DRIPLEAF).pushReaction(PushReaction.DESTROY));
      BIG_DRIPLEAF_STEM = register("big_dripleaf_stem", BigDripleafStemBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().strength(0.1F).sound(SoundType.BIG_DRIPLEAF).pushReaction(PushReaction.DESTROY));
      SMALL_DRIPLEAF = register("small_dripleaf", SmallDripleafBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.SMALL_DRIPLEAF).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.DESTROY));
      HANGING_ROOTS = register("hanging_roots", HangingRootsBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).replaceable().noCollission().instabreak().sound(SoundType.HANGING_ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY));
      ROOTED_DIRT = register("rooted_dirt", RootedDirtBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.ROOTED_DIRT));
      MUD = register("mud", MudBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DIRT).mapColor(MapColor.TERRACOTTA_CYAN).isValidSpawn(Blocks::always).isRedstoneConductor(Blocks::always).isViewBlocking(Blocks::always).isSuffocating(Blocks::always).sound(SoundType.MUD));
      DEEPSLATE = register("deepslate", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE));
      COBBLED_DEEPSLATE = register("cobbled_deepslate", BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE).strength(3.5F, 6.0F));
      COBBLED_DEEPSLATE_STAIRS = registerLegacyStair("cobbled_deepslate_stairs", COBBLED_DEEPSLATE);
      COBBLED_DEEPSLATE_SLAB = register("cobbled_deepslate_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(COBBLED_DEEPSLATE));
      COBBLED_DEEPSLATE_WALL = register("cobbled_deepslate_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(COBBLED_DEEPSLATE).forceSolidOn());
      POLISHED_DEEPSLATE = register("polished_deepslate", BlockBehaviour.Properties.ofLegacyCopy(COBBLED_DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE));
      POLISHED_DEEPSLATE_STAIRS = registerLegacyStair("polished_deepslate_stairs", POLISHED_DEEPSLATE);
      POLISHED_DEEPSLATE_SLAB = register("polished_deepslate_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_DEEPSLATE));
      POLISHED_DEEPSLATE_WALL = register("polished_deepslate_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(POLISHED_DEEPSLATE).forceSolidOn());
      DEEPSLATE_TILES = register("deepslate_tiles", BlockBehaviour.Properties.ofLegacyCopy(COBBLED_DEEPSLATE).sound(SoundType.DEEPSLATE_TILES));
      DEEPSLATE_TILE_STAIRS = registerLegacyStair("deepslate_tile_stairs", DEEPSLATE_TILES);
      DEEPSLATE_TILE_SLAB = register("deepslate_tile_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE_TILES));
      DEEPSLATE_TILE_WALL = register("deepslate_tile_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE_TILES).forceSolidOn());
      DEEPSLATE_BRICKS = register("deepslate_bricks", BlockBehaviour.Properties.ofLegacyCopy(COBBLED_DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
      DEEPSLATE_BRICK_STAIRS = registerLegacyStair("deepslate_brick_stairs", DEEPSLATE_BRICKS);
      DEEPSLATE_BRICK_SLAB = register("deepslate_brick_slab", SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE_BRICKS));
      DEEPSLATE_BRICK_WALL = register("deepslate_brick_wall", WallBlock::new, BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE_BRICKS).forceSolidOn());
      CHISELED_DEEPSLATE = register("chiseled_deepslate", BlockBehaviour.Properties.ofLegacyCopy(COBBLED_DEEPSLATE).sound(SoundType.DEEPSLATE_BRICKS));
      CRACKED_DEEPSLATE_BRICKS = register("cracked_deepslate_bricks", BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE_BRICKS));
      CRACKED_DEEPSLATE_TILES = register("cracked_deepslate_tiles", BlockBehaviour.Properties.ofLegacyCopy(DEEPSLATE_TILES));
      INFESTED_DEEPSLATE = register("infested_deepslate", (var0x) -> {
         return new InfestedRotatedPillarBlock(DEEPSLATE, var0x);
      }, BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE));
      SMOOTH_BASALT = register("smooth_basalt", BlockBehaviour.Properties.ofLegacyCopy(BASALT));
      RAW_IRON_BLOCK = register("raw_iron_block", BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
      RAW_COPPER_BLOCK = register("raw_copper_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
      RAW_GOLD_BLOCK = register("raw_gold_block", BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
      POTTED_AZALEA = register("potted_azalea_bush", (var0x) -> {
         return new FlowerPotBlock(AZALEA, var0x);
      }, flowerPotProperties());
      POTTED_FLOWERING_AZALEA = register("potted_flowering_azalea_bush", (var0x) -> {
         return new FlowerPotBlock(FLOWERING_AZALEA, var0x);
      }, flowerPotProperties());
      OCHRE_FROGLIGHT = register("ochre_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.3F).lightLevel((var0x) -> {
         return 15;
      }).sound(SoundType.FROGLIGHT));
      VERDANT_FROGLIGHT = register("verdant_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.GLOW_LICHEN).strength(0.3F).lightLevel((var0x) -> {
         return 15;
      }).sound(SoundType.FROGLIGHT));
      PEARLESCENT_FROGLIGHT = register("pearlescent_froglight", RotatedPillarBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.3F).lightLevel((var0x) -> {
         return 15;
      }).sound(SoundType.FROGLIGHT));
      FROGSPAWN = register("frogspawn", FrogspawnBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).instabreak().noOcclusion().noCollission().sound(SoundType.FROGSPAWN).pushReaction(PushReaction.DESTROY));
      REINFORCED_DEEPSLATE = register("reinforced_deepslate", BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DEEPSLATE).strength(55.0F, 1200.0F));
      DECORATED_POT = register("decorated_pot", DecoratedPotBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(0.0F, 0.0F).pushReaction(PushReaction.DESTROY).noOcclusion());
      CRAFTER = register("crafter", CrafterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.5F));
      TRIAL_SPAWNER = register("trial_spawner", TrialSpawnerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).lightLevel((var0x) -> {
         return ((TrialSpawnerState)var0x.getValue(TrialSpawnerBlock.STATE)).lightLevel();
      }).strength(50.0F).sound(SoundType.TRIAL_SPAWNER).isViewBlocking(Blocks::never).noOcclusion());
      VAULT = register("vault", VaultBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).noOcclusion().sound(SoundType.VAULT).lightLevel((var0x) -> {
         return ((VaultState)var0x.getValue(VaultBlock.STATE)).lightLevel();
      }).strength(50.0F).isViewBlocking(Blocks::never));
      HEAVY_CORE = register("heavy_core", HeavyCoreBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.SNARE).sound(SoundType.HEAVY_CORE).strength(10.0F).pushReaction(PushReaction.NORMAL).explosionResistance(1200.0F));
      PALE_MOSS_BLOCK = register("pale_moss_block", (var0x) -> {
         return new BonemealableFeaturePlacerBlock(VegetationFeatures.PALE_MOSS_PATCH_BONEMEAL, var0x);
      }, BlockBehaviour.Properties.of().ignitedByLava().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.1F).sound(SoundType.MOSS).pushReaction(PushReaction.DESTROY));
      PALE_MOSS_CARPET = register("pale_moss_carpet", MossyCarpetBlock::new, BlockBehaviour.Properties.of().ignitedByLava().mapColor(PALE_MOSS_BLOCK.defaultMapColor()).strength(0.1F).sound(SoundType.MOSS_CARPET).pushReaction(PushReaction.DESTROY));
      PALE_HANGING_MOSS = register("pale_hanging_moss", HangingMossBlock::new, BlockBehaviour.Properties.of().ignitedByLava().mapColor(PALE_MOSS_BLOCK.defaultMapColor()).noCollission().sound(SoundType.MOSS_CARPET).pushReaction(PushReaction.DESTROY));
      OPEN_EYEBLOSSOM = register("open_eyeblossom", (var0x) -> {
         return new EyeblossomBlock(EyeblossomBlock.Type.OPEN, var0x);
      }, BlockBehaviour.Properties.of().mapColor(CREAKING_HEART.defaultMapColor()).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY).randomTicks());
      CLOSED_EYEBLOSSOM = register("closed_eyeblossom", (var0x) -> {
         return new EyeblossomBlock(EyeblossomBlock.Type.CLOSED, var0x);
      }, BlockBehaviour.Properties.of().mapColor(PALE_OAK_LEAVES.defaultMapColor()).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY).randomTicks());
      POTTED_OPEN_EYEBLOSSOM = register("potted_open_eyeblossom", (var0x) -> {
         return new FlowerPotBlock(OPEN_EYEBLOSSOM, var0x);
      }, flowerPotProperties().randomTicks());
      POTTED_CLOSED_EYEBLOSSOM = register("potted_closed_eyeblossom", (var0x) -> {
         return new FlowerPotBlock(CLOSED_EYEBLOSSOM, var0x);
      }, flowerPotProperties().randomTicks());
      Iterator var0 = BuiltInRegistries.BLOCK.iterator();

      while(var0.hasNext()) {
         Block var1 = (Block)var0.next();
         UnmodifiableIterator var2 = var1.getStateDefinition().getPossibleStates().iterator();

         while(var2.hasNext()) {
            BlockState var3 = (BlockState)var2.next();
            Block.BLOCK_STATE_REGISTRY.add(var3);
            var3.initCache();
         }
      }

   }
}
