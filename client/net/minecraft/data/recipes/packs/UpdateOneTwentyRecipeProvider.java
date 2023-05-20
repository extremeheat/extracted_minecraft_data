package net.minecraft.data.recipes.packs;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
      hangingSign(var1, Items.CHERRY_HANGING_SIGN, Blocks.STRIPPED_CHERRY_LOG);
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
      trimSmithing(var1, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
      trimSmithing(var1, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
      netheriteSmithing(var1, Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, Items.NETHERITE_CHESTPLATE);
      netheriteSmithing(var1, Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, Items.NETHERITE_LEGGINGS);
      netheriteSmithing(var1, Items.DIAMOND_HELMET, RecipeCategory.COMBAT, Items.NETHERITE_HELMET);
      netheriteSmithing(var1, Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, Items.NETHERITE_BOOTS);
      netheriteSmithing(var1, Items.DIAMOND_SWORD, RecipeCategory.COMBAT, Items.NETHERITE_SWORD);
      netheriteSmithing(var1, Items.DIAMOND_AXE, RecipeCategory.TOOLS, Items.NETHERITE_AXE);
      netheriteSmithing(var1, Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE);
      netheriteSmithing(var1, Items.DIAMOND_HOE, RecipeCategory.TOOLS, Items.NETHERITE_HOE);
      netheriteSmithing(var1, Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL);
      copySmithingTemplate(var1, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
      copySmithingTemplate(var1, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
      copySmithingTemplate(var1, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
      copySmithingTemplate(var1, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
      copySmithingTemplate(var1, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
      copySmithingTemplate(var1, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
      copySmithingTemplate(var1, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
      copySmithingTemplate(var1, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
      copySmithingTemplate(var1, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
      copySmithingTemplate(var1, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
      copySmithingTemplate(var1, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
      copySmithingTemplate(var1, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
      oneToOneConversionRecipe(var1, Items.ORANGE_DYE, Blocks.TORCHFLOWER, "orange_dye");
      planksFromLog(var1, Blocks.CHERRY_PLANKS, ItemTags.CHERRY_LOGS, 4);
      woodFromLogs(var1, Blocks.CHERRY_WOOD, Blocks.CHERRY_LOG);
      woodFromLogs(var1, Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG);
      woodenBoat(var1, Items.CHERRY_BOAT, Blocks.CHERRY_PLANKS);
      chestBoat(var1, Items.CHERRY_CHEST_BOAT, Items.CHERRY_BOAT);
      oneToOneConversionRecipe(var1, Items.PINK_DYE, Items.PINK_PETALS, "pink_dye", 1);
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.BRUSH)
         .define('X', Items.FEATHER)
         .define('#', Items.COPPER_INGOT)
         .define('I', Items.STICK)
         .pattern("X")
         .pattern("#")
         .pattern("I")
         .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
         .save(var1);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.DECORATED_POT)
         .define('#', Items.BRICK)
         .pattern(" # ")
         .pattern("# #")
         .pattern(" # ")
         .unlockedBy("has_brick", has(ItemTags.DECORATED_POT_SHARDS))
         .save(var1, "decorated_pot_simple");
      SpecialRecipeBuilder.special(RecipeSerializer.DECORATED_POT_RECIPE).save(var1, "decorated_pot");
   }
}
