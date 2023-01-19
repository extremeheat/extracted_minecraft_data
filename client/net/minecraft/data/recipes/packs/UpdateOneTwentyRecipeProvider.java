package net.minecraft.data.recipes.packs;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyRecipeProvider extends RecipeProvider {
   public UpdateOneTwentyRecipeProvider(PackOutput var1) {
      super(var1);
   }

   @Override
   protected void buildRecipes(Consumer<FinishedRecipe> var1) {
      generateForEnabledBlockFamilies(var1, FeatureFlagSet.of(FeatureFlags.UPDATE_1_20));
      threeByThreePacker(var1, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
      planksFromLogs(var1, Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
      mosaicBuilder(var1, RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
      woodenBoat(var1, Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
      chestBoat(var1, Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
      hangingSign(var1, Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
      hangingSign(var1, Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
      hangingSign(var1, Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
      hangingSign(var1, Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
      hangingSign(var1, Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
      hangingSign(var1, Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
      hangingSign(var1, Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
      hangingSign(var1, Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
      hangingSign(var1, Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
      hangingSign(var1, Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF)
         .define('#', ItemTags.PLANKS)
         .define('X', ItemTags.WOODEN_SLABS)
         .pattern("###")
         .pattern("XXX")
         .pattern("###")
         .unlockedBy("has_book", has(Items.BOOK))
         .save(var1);
   }
}
