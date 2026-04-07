package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import org.jspecify.annotations.Nullable;

public class BlockFamilies {
   private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();
   private static final String RECIPE_GROUP_PREFIX_WOODEN = "wooden";
   private static final String RECIPE_UNLOCKED_BY_HAS_PLANKS = "has_planks";
   public static final BlockFamily ACACIA_PLANKS;
   public static final BlockFamily CHERRY_PLANKS;
   public static final BlockFamily BIRCH_PLANKS;
   public static final BlockFamily CRIMSON_PLANKS;
   public static final BlockFamily JUNGLE_PLANKS;
   public static final BlockFamily OAK_PLANKS;
   public static final BlockFamily DARK_OAK_PLANKS;
   public static final BlockFamily PALE_OAK_PLANKS;
   public static final BlockFamily SPRUCE_PLANKS;
   public static final BlockFamily WARPED_PLANKS;
   public static final BlockFamily MANGROVE_PLANKS;
   public static final BlockFamily BAMBOO_PLANKS;
   public static final BlockFamily BAMBOO_MOSAIC;
   public static final BlockFamily MUD_BRICKS;
   public static final BlockFamily ANDESITE;
   public static final BlockFamily POLISHED_ANDESITE;
   public static final BlockFamily BLACKSTONE;
   public static final BlockFamily POLISHED_BLACKSTONE;
   public static final BlockFamily POLISHED_BLACKSTONE_BRICKS;
   public static final BlockFamily BRICKS;
   public static final BlockFamily END_STONE;
   public static final BlockFamily END_STONE_BRICKS;
   public static final BlockFamily MOSSY_STONE_BRICKS;
   public static final WeatheringCopperCollection<BlockFamily> CUT_COPPER;
   public static final WeatheringCopperCollection<BlockFamily> COPPER_BLOCK;
   public static final BlockFamily COBBLESTONE;
   public static final BlockFamily MOSSY_COBBLESTONE;
   public static final BlockFamily DIORITE;
   public static final BlockFamily POLISHED_DIORITE;
   public static final BlockFamily GRANITE;
   public static final BlockFamily SULFUR;
   public static final BlockFamily POLISHED_SULFUR;
   public static final BlockFamily SULFUR_BRICKS;
   public static final BlockFamily CINNABAR;
   public static final BlockFamily POLISHED_CINNABAR;
   public static final BlockFamily CINNABAR_BRICKS;
   public static final BlockFamily POLISHED_GRANITE;
   public static final BlockFamily TUFF;
   public static final BlockFamily POLISHED_TUFF;
   public static final BlockFamily TUFF_BRICKS;
   public static final BlockFamily RESIN_BRICKS;
   public static final BlockFamily NETHER_BRICKS;
   public static final BlockFamily RED_NETHER_BRICKS;
   public static final BlockFamily PRISMARINE;
   public static final BlockFamily PRISMARINE_BRICKS;
   public static final BlockFamily DARK_PRISMARINE;
   public static final BlockFamily PURPUR;
   public static final BlockFamily QUARTZ;
   public static final BlockFamily SMOOTH_QUARTZ;
   public static final BlockFamily SANDSTONE;
   public static final BlockFamily CUT_SANDSTONE;
   public static final BlockFamily SMOOTH_SANDSTONE;
   public static final BlockFamily RED_SANDSTONE;
   public static final BlockFamily CUT_RED_SANDSTONE;
   public static final BlockFamily SMOOTH_RED_SANDSTONE;
   public static final BlockFamily STONE;
   public static final BlockFamily STONE_BRICK;
   public static final BlockFamily DEEPSLATE;
   public static final BlockFamily COBBLED_DEEPSLATE;
   public static final BlockFamily POLISHED_DEEPSLATE;
   public static final BlockFamily DEEPSLATE_BRICKS;
   public static final BlockFamily DEEPSLATE_TILES;

   public BlockFamilies() {
      super();
   }

   private static BlockFamily.Builder familyBuilder(final Block base) {
      BlockFamily.Builder builder = new BlockFamily.Builder(base);
      BlockFamily blockFamily = (BlockFamily)MAP.put(base, builder.getFamily());
      if (blockFamily != null) {
         throw new IllegalStateException("Duplicate family definition for " + String.valueOf(BuiltInRegistries.BLOCK.getKey(base)));
      } else {
         return builder;
      }
   }

   public static Stream<BlockFamily> getAllFamilies() {
      return MAP.values().stream();
   }

   public static @Nullable BlockFamily getFamily(final Block base) {
      return (BlockFamily)MAP.get(base);
   }

   static {
      ACACIA_PLANKS = familyBuilder(Blocks.ACACIA_PLANKS).button(Blocks.ACACIA_BUTTON).fence(Blocks.ACACIA_FENCE).fenceGate(Blocks.ACACIA_FENCE_GATE).pressurePlate(Blocks.ACACIA_PRESSURE_PLATE).sign(Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN).slab(Blocks.ACACIA_SLAB).stairs(Blocks.ACACIA_STAIRS).door(Blocks.ACACIA_DOOR).trapdoor(Blocks.ACACIA_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      CHERRY_PLANKS = familyBuilder(Blocks.CHERRY_PLANKS).button(Blocks.CHERRY_BUTTON).fence(Blocks.CHERRY_FENCE).fenceGate(Blocks.CHERRY_FENCE_GATE).pressurePlate(Blocks.CHERRY_PRESSURE_PLATE).sign(Blocks.CHERRY_SIGN, Blocks.CHERRY_WALL_SIGN).slab(Blocks.CHERRY_SLAB).stairs(Blocks.CHERRY_STAIRS).door(Blocks.CHERRY_DOOR).trapdoor(Blocks.CHERRY_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      BIRCH_PLANKS = familyBuilder(Blocks.BIRCH_PLANKS).button(Blocks.BIRCH_BUTTON).fence(Blocks.BIRCH_FENCE).fenceGate(Blocks.BIRCH_FENCE_GATE).pressurePlate(Blocks.BIRCH_PRESSURE_PLATE).sign(Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN).slab(Blocks.BIRCH_SLAB).stairs(Blocks.BIRCH_STAIRS).door(Blocks.BIRCH_DOOR).trapdoor(Blocks.BIRCH_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      CRIMSON_PLANKS = familyBuilder(Blocks.CRIMSON_PLANKS).button(Blocks.CRIMSON_BUTTON).fence(Blocks.CRIMSON_FENCE).fenceGate(Blocks.CRIMSON_FENCE_GATE).pressurePlate(Blocks.CRIMSON_PRESSURE_PLATE).sign(Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN).slab(Blocks.CRIMSON_SLAB).stairs(Blocks.CRIMSON_STAIRS).door(Blocks.CRIMSON_DOOR).trapdoor(Blocks.CRIMSON_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      JUNGLE_PLANKS = familyBuilder(Blocks.JUNGLE_PLANKS).button(Blocks.JUNGLE_BUTTON).fence(Blocks.JUNGLE_FENCE).fenceGate(Blocks.JUNGLE_FENCE_GATE).pressurePlate(Blocks.JUNGLE_PRESSURE_PLATE).sign(Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN).slab(Blocks.JUNGLE_SLAB).stairs(Blocks.JUNGLE_STAIRS).door(Blocks.JUNGLE_DOOR).trapdoor(Blocks.JUNGLE_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      OAK_PLANKS = familyBuilder(Blocks.OAK_PLANKS).button(Blocks.OAK_BUTTON).fence(Blocks.OAK_FENCE).fenceGate(Blocks.OAK_FENCE_GATE).pressurePlate(Blocks.OAK_PRESSURE_PLATE).sign(Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN).slab(Blocks.OAK_SLAB).stairs(Blocks.OAK_STAIRS).door(Blocks.OAK_DOOR).trapdoor(Blocks.OAK_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      DARK_OAK_PLANKS = familyBuilder(Blocks.DARK_OAK_PLANKS).button(Blocks.DARK_OAK_BUTTON).fence(Blocks.DARK_OAK_FENCE).fenceGate(Blocks.DARK_OAK_FENCE_GATE).pressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE).sign(Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN).slab(Blocks.DARK_OAK_SLAB).stairs(Blocks.DARK_OAK_STAIRS).door(Blocks.DARK_OAK_DOOR).trapdoor(Blocks.DARK_OAK_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      PALE_OAK_PLANKS = familyBuilder(Blocks.PALE_OAK_PLANKS).button(Blocks.PALE_OAK_BUTTON).fence(Blocks.PALE_OAK_FENCE).fenceGate(Blocks.PALE_OAK_FENCE_GATE).pressurePlate(Blocks.PALE_OAK_PRESSURE_PLATE).sign(Blocks.PALE_OAK_SIGN, Blocks.PALE_OAK_WALL_SIGN).slab(Blocks.PALE_OAK_SLAB).stairs(Blocks.PALE_OAK_STAIRS).door(Blocks.PALE_OAK_DOOR).trapdoor(Blocks.PALE_OAK_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      SPRUCE_PLANKS = familyBuilder(Blocks.SPRUCE_PLANKS).button(Blocks.SPRUCE_BUTTON).fence(Blocks.SPRUCE_FENCE).fenceGate(Blocks.SPRUCE_FENCE_GATE).pressurePlate(Blocks.SPRUCE_PRESSURE_PLATE).sign(Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN).slab(Blocks.SPRUCE_SLAB).stairs(Blocks.SPRUCE_STAIRS).door(Blocks.SPRUCE_DOOR).trapdoor(Blocks.SPRUCE_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      WARPED_PLANKS = familyBuilder(Blocks.WARPED_PLANKS).button(Blocks.WARPED_BUTTON).fence(Blocks.WARPED_FENCE).fenceGate(Blocks.WARPED_FENCE_GATE).pressurePlate(Blocks.WARPED_PRESSURE_PLATE).sign(Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN).slab(Blocks.WARPED_SLAB).stairs(Blocks.WARPED_STAIRS).door(Blocks.WARPED_DOOR).trapdoor(Blocks.WARPED_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      MANGROVE_PLANKS = familyBuilder(Blocks.MANGROVE_PLANKS).button(Blocks.MANGROVE_BUTTON).slab(Blocks.MANGROVE_SLAB).stairs(Blocks.MANGROVE_STAIRS).fence(Blocks.MANGROVE_FENCE).fenceGate(Blocks.MANGROVE_FENCE_GATE).pressurePlate(Blocks.MANGROVE_PRESSURE_PLATE).sign(Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN).door(Blocks.MANGROVE_DOOR).trapdoor(Blocks.MANGROVE_TRAPDOOR).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      BAMBOO_PLANKS = familyBuilder(Blocks.BAMBOO_PLANKS).button(Blocks.BAMBOO_BUTTON).slab(Blocks.BAMBOO_SLAB).stairs(Blocks.BAMBOO_STAIRS).customFence(Blocks.BAMBOO_FENCE).customFenceGate(Blocks.BAMBOO_FENCE_GATE).pressurePlate(Blocks.BAMBOO_PRESSURE_PLATE).sign(Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN).door(Blocks.BAMBOO_DOOR).trapdoor(Blocks.BAMBOO_TRAPDOOR).mosaic(Blocks.BAMBOO_MOSAIC).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
      BAMBOO_MOSAIC = familyBuilder(Blocks.BAMBOO_MOSAIC).slab(Blocks.BAMBOO_MOSAIC_SLAB).stairs(Blocks.BAMBOO_MOSAIC_STAIRS).getFamily();
      MUD_BRICKS = familyBuilder(Blocks.MUD_BRICKS).wall(Blocks.MUD_BRICK_WALL).stairs(Blocks.MUD_BRICK_STAIRS).slab(Blocks.MUD_BRICK_SLAB).generateStonecutterRecipe().getFamily();
      ANDESITE = familyBuilder(Blocks.ANDESITE).wall(Blocks.ANDESITE_WALL).stairs(Blocks.ANDESITE_STAIRS).slab(Blocks.ANDESITE_SLAB).polished(Blocks.POLISHED_ANDESITE).generateStonecutterRecipe().getFamily();
      POLISHED_ANDESITE = familyBuilder(Blocks.POLISHED_ANDESITE).stairs(Blocks.POLISHED_ANDESITE_STAIRS).slab(Blocks.POLISHED_ANDESITE_SLAB).generateStonecutterRecipe().getFamily();
      BLACKSTONE = familyBuilder(Blocks.BLACKSTONE).wall(Blocks.BLACKSTONE_WALL).stairs(Blocks.BLACKSTONE_STAIRS).slab(Blocks.BLACKSTONE_SLAB).polished(Blocks.POLISHED_BLACKSTONE).generateStonecutterRecipe().getFamily();
      POLISHED_BLACKSTONE = familyBuilder(Blocks.POLISHED_BLACKSTONE).wall(Blocks.POLISHED_BLACKSTONE_WALL).pressurePlate(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE).button(Blocks.POLISHED_BLACKSTONE_BUTTON).stairs(Blocks.POLISHED_BLACKSTONE_STAIRS).slab(Blocks.POLISHED_BLACKSTONE_SLAB).bricks(Blocks.POLISHED_BLACKSTONE_BRICKS).chiseled(Blocks.CHISELED_POLISHED_BLACKSTONE).generateStonecutterRecipe().getFamily();
      POLISHED_BLACKSTONE_BRICKS = familyBuilder(Blocks.POLISHED_BLACKSTONE_BRICKS).wall(Blocks.POLISHED_BLACKSTONE_BRICK_WALL).stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB).cracked(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS).generateStonecutterRecipe().getFamily();
      BRICKS = familyBuilder(Blocks.BRICKS).wall(Blocks.BRICK_WALL).stairs(Blocks.BRICK_STAIRS).slab(Blocks.BRICK_SLAB).generateStonecutterRecipe().getFamily();
      END_STONE = familyBuilder(Blocks.END_STONE).bricks(Blocks.END_STONE_BRICKS).generateStonecutterRecipe().getFamily();
      END_STONE_BRICKS = familyBuilder(Blocks.END_STONE_BRICKS).wall(Blocks.END_STONE_BRICK_WALL).stairs(Blocks.END_STONE_BRICK_STAIRS).slab(Blocks.END_STONE_BRICK_SLAB).generateStonecutterRecipe().getFamily();
      MOSSY_STONE_BRICKS = familyBuilder(Blocks.MOSSY_STONE_BRICKS).wall(Blocks.MOSSY_STONE_BRICK_WALL).stairs(Blocks.MOSSY_STONE_BRICK_STAIRS).slab(Blocks.MOSSY_STONE_BRICK_SLAB).generateStonecutterRecipe().getFamily();
      CUT_COPPER = WeatheringCopperCollection.createFamily((prefix, state) -> familyBuilder(Blocks.CUT_COPPER.pick(state, true)).slab(Blocks.CUT_COPPER_SLAB.pick(state, true)).stairs(Blocks.CUT_COPPER_STAIRS.pick(state, true)).recipeGroupPrefix(prefix + "cut_copper").dontGenerateModel().generateStonecutterRecipe().getFamily(), (var0, state) -> familyBuilder(Blocks.CUT_COPPER.pick(state, false)).slab(Blocks.CUT_COPPER_SLAB.pick(state, false)).stairs(Blocks.CUT_COPPER_STAIRS.pick(state, false)).chiseled(Blocks.CHISELED_COPPER.pick(state, false)).dontGenerateModel().generateStonecutterRecipe().getFamily());
      COPPER_BLOCK = WeatheringCopperCollection.createFamily((prefix, state) -> familyBuilder(Blocks.COPPER_BLOCK.pick(state, true)).cut(Blocks.CUT_COPPER.pick(state, true)).recipeGroupPrefix(prefix + "cut_copper").dontGenerateModel().getFamily(), (var0, state) -> familyBuilder(Blocks.COPPER_BLOCK.pick(state, false)).cut(Blocks.CUT_COPPER.pick(state, false)).dontGenerateModel().getFamily());
      COBBLESTONE = familyBuilder(Blocks.COBBLESTONE).wall(Blocks.COBBLESTONE_WALL).stairs(Blocks.COBBLESTONE_STAIRS).slab(Blocks.COBBLESTONE_SLAB).generateStonecutterRecipe().getFamily();
      MOSSY_COBBLESTONE = familyBuilder(Blocks.MOSSY_COBBLESTONE).wall(Blocks.MOSSY_COBBLESTONE_WALL).stairs(Blocks.MOSSY_COBBLESTONE_STAIRS).slab(Blocks.MOSSY_COBBLESTONE_SLAB).generateStonecutterRecipe().getFamily();
      DIORITE = familyBuilder(Blocks.DIORITE).wall(Blocks.DIORITE_WALL).stairs(Blocks.DIORITE_STAIRS).slab(Blocks.DIORITE_SLAB).polished(Blocks.POLISHED_DIORITE).generateStonecutterRecipe().getFamily();
      POLISHED_DIORITE = familyBuilder(Blocks.POLISHED_DIORITE).stairs(Blocks.POLISHED_DIORITE_STAIRS).slab(Blocks.POLISHED_DIORITE_SLAB).generateStonecutterRecipe().getFamily();
      GRANITE = familyBuilder(Blocks.GRANITE).wall(Blocks.GRANITE_WALL).stairs(Blocks.GRANITE_STAIRS).slab(Blocks.GRANITE_SLAB).polished(Blocks.POLISHED_GRANITE).generateStonecutterRecipe().getFamily();
      SULFUR = familyBuilder(Blocks.SULFUR).wall(Blocks.SULFUR_WALL).stairs(Blocks.SULFUR_STAIRS).slab(Blocks.SULFUR_SLAB).chiseled(Blocks.CHISELED_SULFUR).polished(Blocks.POLISHED_SULFUR).generateStonecutterRecipe().getFamily();
      POLISHED_SULFUR = familyBuilder(Blocks.POLISHED_SULFUR).wall(Blocks.POLISHED_SULFUR_WALL).stairs(Blocks.POLISHED_SULFUR_STAIRS).slab(Blocks.POLISHED_SULFUR_SLAB).bricks(Blocks.SULFUR_BRICKS).generateStonecutterRecipe().getFamily();
      SULFUR_BRICKS = familyBuilder(Blocks.SULFUR_BRICKS).wall(Blocks.SULFUR_BRICK_WALL).stairs(Blocks.SULFUR_BRICK_STAIRS).slab(Blocks.SULFUR_BRICK_SLAB).generateStonecutterRecipe().getFamily();
      CINNABAR = familyBuilder(Blocks.CINNABAR).wall(Blocks.CINNABAR_WALL).stairs(Blocks.CINNABAR_STAIRS).slab(Blocks.CINNABAR_SLAB).chiseled(Blocks.CHISELED_CINNABAR).polished(Blocks.POLISHED_CINNABAR).generateStonecutterRecipe().getFamily();
      POLISHED_CINNABAR = familyBuilder(Blocks.POLISHED_CINNABAR).wall(Blocks.POLISHED_CINNABAR_WALL).stairs(Blocks.POLISHED_CINNABAR_STAIRS).slab(Blocks.POLISHED_CINNABAR_SLAB).bricks(Blocks.CINNABAR_BRICKS).generateStonecutterRecipe().getFamily();
      CINNABAR_BRICKS = familyBuilder(Blocks.CINNABAR_BRICKS).wall(Blocks.CINNABAR_BRICK_WALL).stairs(Blocks.CINNABAR_BRICK_STAIRS).slab(Blocks.CINNABAR_BRICK_SLAB).generateStonecutterRecipe().getFamily();
      POLISHED_GRANITE = familyBuilder(Blocks.POLISHED_GRANITE).stairs(Blocks.POLISHED_GRANITE_STAIRS).slab(Blocks.POLISHED_GRANITE_SLAB).generateStonecutterRecipe().getFamily();
      TUFF = familyBuilder(Blocks.TUFF).wall(Blocks.TUFF_WALL).stairs(Blocks.TUFF_STAIRS).slab(Blocks.TUFF_SLAB).chiseled(Blocks.CHISELED_TUFF).polished(Blocks.POLISHED_TUFF).generateStonecutterRecipe().getFamily();
      POLISHED_TUFF = familyBuilder(Blocks.POLISHED_TUFF).wall(Blocks.POLISHED_TUFF_WALL).stairs(Blocks.POLISHED_TUFF_STAIRS).slab(Blocks.POLISHED_TUFF_SLAB).bricks(Blocks.TUFF_BRICKS).generateStonecutterRecipe().getFamily();
      TUFF_BRICKS = familyBuilder(Blocks.TUFF_BRICKS).wall(Blocks.TUFF_BRICK_WALL).stairs(Blocks.TUFF_BRICK_STAIRS).slab(Blocks.TUFF_BRICK_SLAB).chiseled(Blocks.CHISELED_TUFF_BRICKS).generateStonecutterRecipe().getFamily();
      RESIN_BRICKS = familyBuilder(Blocks.RESIN_BRICKS).wall(Blocks.RESIN_BRICK_WALL).stairs(Blocks.RESIN_BRICK_STAIRS).slab(Blocks.RESIN_BRICK_SLAB).chiseled(Blocks.CHISELED_RESIN_BRICKS).generateStonecutterRecipe().getFamily();
      NETHER_BRICKS = familyBuilder(Blocks.NETHER_BRICKS).fence(Blocks.NETHER_BRICK_FENCE).wall(Blocks.NETHER_BRICK_WALL).stairs(Blocks.NETHER_BRICK_STAIRS).slab(Blocks.NETHER_BRICK_SLAB).chiseled(Blocks.CHISELED_NETHER_BRICKS).cracked(Blocks.CRACKED_NETHER_BRICKS).generateStonecutterRecipe().getFamily();
      RED_NETHER_BRICKS = familyBuilder(Blocks.RED_NETHER_BRICKS).slab(Blocks.RED_NETHER_BRICK_SLAB).stairs(Blocks.RED_NETHER_BRICK_STAIRS).wall(Blocks.RED_NETHER_BRICK_WALL).generateStonecutterRecipe().getFamily();
      PRISMARINE = familyBuilder(Blocks.PRISMARINE).wall(Blocks.PRISMARINE_WALL).stairs(Blocks.PRISMARINE_STAIRS).slab(Blocks.PRISMARINE_SLAB).generateStonecutterRecipe().getFamily();
      PRISMARINE_BRICKS = familyBuilder(Blocks.PRISMARINE_BRICKS).stairs(Blocks.PRISMARINE_BRICK_STAIRS).slab(Blocks.PRISMARINE_BRICK_SLAB).generateStonecutterRecipe().getFamily();
      DARK_PRISMARINE = familyBuilder(Blocks.DARK_PRISMARINE).stairs(Blocks.DARK_PRISMARINE_STAIRS).slab(Blocks.DARK_PRISMARINE_SLAB).generateStonecutterRecipe().getFamily();
      PURPUR = familyBuilder(Blocks.PURPUR_BLOCK).stairs(Blocks.PURPUR_STAIRS).slab(Blocks.PURPUR_SLAB).dontGenerateCraftingRecipe().generateStonecutterRecipe().getFamily();
      QUARTZ = familyBuilder(Blocks.QUARTZ_BLOCK).stairs(Blocks.QUARTZ_STAIRS).slab(Blocks.QUARTZ_SLAB).chiseled(Blocks.CHISELED_QUARTZ_BLOCK).bricks(Blocks.QUARTZ_BRICKS).dontGenerateCraftingRecipe().generateStonecutterRecipe().getFamily();
      SMOOTH_QUARTZ = familyBuilder(Blocks.SMOOTH_QUARTZ).stairs(Blocks.SMOOTH_QUARTZ_STAIRS).slab(Blocks.SMOOTH_QUARTZ_SLAB).generateStonecutterRecipe().getFamily();
      SANDSTONE = familyBuilder(Blocks.SANDSTONE).wall(Blocks.SANDSTONE_WALL).stairs(Blocks.SANDSTONE_STAIRS).slab(Blocks.SANDSTONE_SLAB).chiseled(Blocks.CHISELED_SANDSTONE).cut(Blocks.CUT_SANDSTONE).dontGenerateCraftingRecipe().generateStonecutterRecipe().getFamily();
      CUT_SANDSTONE = familyBuilder(Blocks.CUT_SANDSTONE).slab(Blocks.CUT_SANDSTONE_SLAB).generateStonecutterRecipe().getFamily();
      SMOOTH_SANDSTONE = familyBuilder(Blocks.SMOOTH_SANDSTONE).slab(Blocks.SMOOTH_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_SANDSTONE_STAIRS).generateStonecutterRecipe().getFamily();
      RED_SANDSTONE = familyBuilder(Blocks.RED_SANDSTONE).wall(Blocks.RED_SANDSTONE_WALL).stairs(Blocks.RED_SANDSTONE_STAIRS).slab(Blocks.RED_SANDSTONE_SLAB).chiseled(Blocks.CHISELED_RED_SANDSTONE).cut(Blocks.CUT_RED_SANDSTONE).dontGenerateCraftingRecipe().generateStonecutterRecipe().getFamily();
      CUT_RED_SANDSTONE = familyBuilder(Blocks.CUT_RED_SANDSTONE).slab(Blocks.CUT_RED_SANDSTONE_SLAB).generateStonecutterRecipe().getFamily();
      SMOOTH_RED_SANDSTONE = familyBuilder(Blocks.SMOOTH_RED_SANDSTONE).slab(Blocks.SMOOTH_RED_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_RED_SANDSTONE_STAIRS).generateStonecutterRecipe().getFamily();
      STONE = familyBuilder(Blocks.STONE).slab(Blocks.STONE_SLAB).pressurePlate(Blocks.STONE_PRESSURE_PLATE).button(Blocks.STONE_BUTTON).stairs(Blocks.STONE_STAIRS).bricks(Blocks.STONE_BRICKS).cobbled(Blocks.COBBLESTONE).generateStonecutterRecipe().getFamily();
      STONE_BRICK = familyBuilder(Blocks.STONE_BRICKS).wall(Blocks.STONE_BRICK_WALL).stairs(Blocks.STONE_BRICK_STAIRS).slab(Blocks.STONE_BRICK_SLAB).chiseled(Blocks.CHISELED_STONE_BRICKS).cracked(Blocks.CRACKED_STONE_BRICKS).dontGenerateCraftingRecipe().generateStonecutterRecipe().getFamily();
      DEEPSLATE = familyBuilder(Blocks.DEEPSLATE).cobbled(Blocks.COBBLED_DEEPSLATE).generateStonecutterRecipe().getFamily();
      COBBLED_DEEPSLATE = familyBuilder(Blocks.COBBLED_DEEPSLATE).slab(Blocks.COBBLED_DEEPSLATE_SLAB).stairs(Blocks.COBBLED_DEEPSLATE_STAIRS).wall(Blocks.COBBLED_DEEPSLATE_WALL).chiseled(Blocks.CHISELED_DEEPSLATE).polished(Blocks.POLISHED_DEEPSLATE).generateStonecutterRecipe().getFamily();
      POLISHED_DEEPSLATE = familyBuilder(Blocks.POLISHED_DEEPSLATE).slab(Blocks.POLISHED_DEEPSLATE_SLAB).stairs(Blocks.POLISHED_DEEPSLATE_STAIRS).wall(Blocks.POLISHED_DEEPSLATE_WALL).bricks(Blocks.DEEPSLATE_BRICKS).generateStonecutterRecipe().getFamily();
      DEEPSLATE_BRICKS = familyBuilder(Blocks.DEEPSLATE_BRICKS).slab(Blocks.DEEPSLATE_BRICK_SLAB).stairs(Blocks.DEEPSLATE_BRICK_STAIRS).wall(Blocks.DEEPSLATE_BRICK_WALL).cracked(Blocks.CRACKED_DEEPSLATE_BRICKS).tiles(Blocks.DEEPSLATE_TILES).generateStonecutterRecipe().getFamily();
      DEEPSLATE_TILES = familyBuilder(Blocks.DEEPSLATE_TILES).slab(Blocks.DEEPSLATE_TILE_SLAB).stairs(Blocks.DEEPSLATE_TILE_STAIRS).wall(Blocks.DEEPSLATE_TILE_WALL).cracked(Blocks.CRACKED_DEEPSLATE_TILES).generateStonecutterRecipe().getFamily();
   }
}
