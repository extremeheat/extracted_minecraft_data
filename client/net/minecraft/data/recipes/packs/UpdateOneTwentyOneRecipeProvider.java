package net.minecraft.data.recipes.packs;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyOneRecipeProvider extends RecipeProvider {
   public UpdateOneTwentyOneRecipeProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, var2);
   }

   @Override
   protected void buildRecipes(RecipeOutput var1) {
      generateForEnabledBlockFamilies(var1, FeatureFlagSet.of(FeatureFlags.UPDATE_1_21));
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.CRAFTER)
         .define('#', Items.IRON_INGOT)
         .define('C', Items.CRAFTING_TABLE)
         .define('R', Items.REDSTONE)
         .define('D', Items.DROPPER)
         .pattern("###")
         .pattern("#C#")
         .pattern("RDR")
         .unlockedBy("has_dropper", has(Items.DROPPER))
         .save(var1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_SLAB, Blocks.TUFF, 2);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_STAIRS, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.DECORATIONS, Blocks.TUFF_WALL, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_SLAB, Blocks.TUFF, 2);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_STAIRS, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.DECORATIONS, Blocks.POLISHED_TUFF_WALL, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICKS, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF, 2);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_SLAB, Blocks.POLISHED_TUFF, 2);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.DECORATIONS, Blocks.POLISHED_TUFF_WALL, Blocks.POLISHED_TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICKS, Blocks.POLISHED_TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.POLISHED_TUFF, 2);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.POLISHED_TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.POLISHED_TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.POLISHED_TUFF);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF_BRICKS, 2);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF_BRICKS);
      stonecutterResultFromBase(var1, RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.TUFF_BRICKS);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF_BRICKS);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_COPPER, Blocks.COPPER_BLOCK, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_COPPER, Blocks.CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, 1);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER, 1);
      grate(var1, Blocks.COPPER_GRATE, Blocks.COPPER_BLOCK);
      grate(var1, Blocks.EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER);
      grate(var1, Blocks.WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER);
      grate(var1, Blocks.OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER);
      grate(var1, Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_COPPER_BLOCK);
      grate(var1, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER);
      grate(var1, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER);
      grate(var1, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER);
      copperBulb(var1, Blocks.COPPER_BULB, Blocks.COPPER_BLOCK);
      copperBulb(var1, Blocks.EXPOSED_COPPER_BULB, Blocks.EXPOSED_COPPER);
      copperBulb(var1, Blocks.WEATHERED_COPPER_BULB, Blocks.WEATHERED_COPPER);
      copperBulb(var1, Blocks.OXIDIZED_COPPER_BULB, Blocks.OXIDIZED_COPPER);
      copperBulb(var1, Blocks.WAXED_COPPER_BULB, Blocks.WAXED_COPPER_BLOCK);
      copperBulb(var1, Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER);
      copperBulb(var1, Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER);
      copperBulb(var1, Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.COPPER_GRATE, Blocks.COPPER_BLOCK, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_COPPER_BLOCK, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER, 4);
      smithingTrims().forEach(var1x -> trimSmithing(var1, var1x.template(), var1x.id()));
      copySmithingTemplate(var1, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BREEZE_ROD);
      copySmithingTemplate(var1, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COPPER_BLOCK);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WIND_CHARGE, 4)
         .requires(Items.BREEZE_ROD)
         .unlockedBy("has_breeze_rod", has(Items.BREEZE_ROD))
         .save(var1);
      ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.MACE, 1)
         .define('A', Items.BREEZE_ROD)
         .define('B', Blocks.HEAVY_CORE)
         .pattern(" B ")
         .pattern(" A ")
         .unlockedBy("has_breeze_rod", has(Items.BREEZE_ROD))
         .unlockedBy("has_heavy_core", has(Blocks.HEAVY_CORE))
         .save(var1);
      waxRecipes(var1, FeatureFlagSet.of(FeatureFlags.UPDATE_1_21));
   }

   public static Stream<VanillaRecipeProvider.TrimTemplate> smithingTrims() {
      return Stream.of(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE)
         .map(var0 -> new VanillaRecipeProvider.TrimTemplate(var0, new ResourceLocation(getItemName(var0) + "_smithing_trim")));
   }
}
