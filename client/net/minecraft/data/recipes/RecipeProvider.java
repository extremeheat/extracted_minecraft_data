package net.minecraft.data.recipes;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class RecipeProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ImmutableList<ItemLike> COAL_SMELTABLES;
   private static final ImmutableList<ItemLike> IRON_SMELTABLES;
   private static final ImmutableList<ItemLike> COPPER_SMELTABLES;
   private static final ImmutableList<ItemLike> GOLD_SMELTABLES;
   private static final ImmutableList<ItemLike> DIAMOND_SMELTABLES;
   private static final ImmutableList<ItemLike> LAPIS_SMELTABLES;
   private static final ImmutableList<ItemLike> REDSTONE_SMELTABLES;
   private static final ImmutableList<ItemLike> EMERALD_SMELTABLES;
   private final DataGenerator.PathProvider recipePathProvider;
   private final DataGenerator.PathProvider advancementPathProvider;
   private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> shapeBuilders;

   public RecipeProvider(DataGenerator var1) {
      super();
      this.recipePathProvider = var1.createPathProvider(DataGenerator.Target.DATA_PACK, "recipes");
      this.advancementPathProvider = var1.createPathProvider(DataGenerator.Target.DATA_PACK, "advancements");
   }

   public void run(CachedOutput var1) {
      HashSet var2 = Sets.newHashSet();
      buildCraftingRecipes((var3) -> {
         if (!var2.add(var3.getId())) {
            throw new IllegalStateException("Duplicate recipe " + var3.getId());
         } else {
            saveRecipe(var1, var3.serializeRecipe(), this.recipePathProvider.json(var3.getId()));
            JsonObject var4 = var3.serializeAdvancement();
            if (var4 != null) {
               saveAdvancement(var1, var4, this.advancementPathProvider.json(var3.getAdvancementId()));
            }

         }
      });
      saveAdvancement(var1, Advancement.Builder.advancement().addCriterion("impossible", (CriterionTriggerInstance)(new ImpossibleTrigger.TriggerInstance())).serializeToJson(), this.advancementPathProvider.json(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT));
   }

   private static void saveRecipe(CachedOutput var0, JsonObject var1, Path var2) {
      try {
         DataProvider.saveStable(var0, var1, var2);
      } catch (IOException var4) {
         LOGGER.error("Couldn't save recipe {}", var2, var4);
      }

   }

   private static void saveAdvancement(CachedOutput var0, JsonObject var1, Path var2) {
      try {
         DataProvider.saveStable(var0, var1, var2);
      } catch (IOException var4) {
         LOGGER.error("Couldn't save recipe advancement {}", var2, var4);
      }

   }

   private static void buildCraftingRecipes(Consumer<FinishedRecipe> var0) {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach((var1) -> {
         generateRecipes(var0, var1);
      });
      planksFromLog(var0, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
      planksFromLogs(var0, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
      planksFromLogs(var0, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
      planksFromLog(var0, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
      planksFromLogs(var0, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
      planksFromLogs(var0, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
      planksFromLogs(var0, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
      planksFromLogs(var0, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
      planksFromLogs(var0, Blocks.MANGROVE_PLANKS, ItemTags.MANGROVE_LOGS);
      woodFromLogs(var0, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
      woodFromLogs(var0, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
      woodFromLogs(var0, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
      woodFromLogs(var0, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
      woodFromLogs(var0, Blocks.OAK_WOOD, Blocks.OAK_LOG);
      woodFromLogs(var0, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
      woodFromLogs(var0, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
      woodFromLogs(var0, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
      woodFromLogs(var0, Blocks.MANGROVE_WOOD, Blocks.MANGROVE_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
      woodFromLogs(var0, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
      woodFromLogs(var0, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG);
      woodenBoat(var0, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
      woodenBoat(var0, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
      woodenBoat(var0, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
      woodenBoat(var0, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
      woodenBoat(var0, Items.OAK_BOAT, Blocks.OAK_PLANKS);
      woodenBoat(var0, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
      woodenBoat(var0, Items.MANGROVE_BOAT, Blocks.MANGROVE_PLANKS);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.BLACK_WOOL, Items.BLACK_DYE);
      carpet(var0, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.BLACK_CARPET, Items.BLACK_DYE);
      bedFromPlanksAndWool(var0, Items.BLACK_BED, Blocks.BLACK_WOOL);
      bedFromWhiteBedAndDye(var0, Items.BLACK_BED, Items.BLACK_DYE);
      banner(var0, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.BLUE_WOOL, Items.BLUE_DYE);
      carpet(var0, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.BLUE_CARPET, Items.BLUE_DYE);
      bedFromPlanksAndWool(var0, Items.BLUE_BED, Blocks.BLUE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.BLUE_BED, Items.BLUE_DYE);
      banner(var0, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.BROWN_WOOL, Items.BROWN_DYE);
      carpet(var0, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.BROWN_CARPET, Items.BROWN_DYE);
      bedFromPlanksAndWool(var0, Items.BROWN_BED, Blocks.BROWN_WOOL);
      bedFromWhiteBedAndDye(var0, Items.BROWN_BED, Items.BROWN_DYE);
      banner(var0, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.CYAN_WOOL, Items.CYAN_DYE);
      carpet(var0, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.CYAN_CARPET, Items.CYAN_DYE);
      bedFromPlanksAndWool(var0, Items.CYAN_BED, Blocks.CYAN_WOOL);
      bedFromWhiteBedAndDye(var0, Items.CYAN_BED, Items.CYAN_DYE);
      banner(var0, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.GRAY_WOOL, Items.GRAY_DYE);
      carpet(var0, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.GRAY_CARPET, Items.GRAY_DYE);
      bedFromPlanksAndWool(var0, Items.GRAY_BED, Blocks.GRAY_WOOL);
      bedFromWhiteBedAndDye(var0, Items.GRAY_BED, Items.GRAY_DYE);
      banner(var0, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.GREEN_WOOL, Items.GREEN_DYE);
      carpet(var0, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.GREEN_CARPET, Items.GREEN_DYE);
      bedFromPlanksAndWool(var0, Items.GREEN_BED, Blocks.GREEN_WOOL);
      bedFromWhiteBedAndDye(var0, Items.GREEN_BED, Items.GREEN_DYE);
      banner(var0, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
      carpet(var0, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
      bedFromPlanksAndWool(var0, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
      banner(var0, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
      carpet(var0, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
      bedFromPlanksAndWool(var0, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
      bedFromWhiteBedAndDye(var0, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
      banner(var0, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.LIME_WOOL, Items.LIME_DYE);
      carpet(var0, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.LIME_CARPET, Items.LIME_DYE);
      bedFromPlanksAndWool(var0, Items.LIME_BED, Blocks.LIME_WOOL);
      bedFromWhiteBedAndDye(var0, Items.LIME_BED, Items.LIME_DYE);
      banner(var0, Items.LIME_BANNER, Blocks.LIME_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
      carpet(var0, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
      bedFromPlanksAndWool(var0, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
      bedFromWhiteBedAndDye(var0, Items.MAGENTA_BED, Items.MAGENTA_DYE);
      banner(var0, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
      carpet(var0, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
      bedFromPlanksAndWool(var0, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.ORANGE_BED, Items.ORANGE_DYE);
      banner(var0, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.PINK_WOOL, Items.PINK_DYE);
      carpet(var0, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.PINK_CARPET, Items.PINK_DYE);
      bedFromPlanksAndWool(var0, Items.PINK_BED, Blocks.PINK_WOOL);
      bedFromWhiteBedAndDye(var0, Items.PINK_BED, Items.PINK_DYE);
      banner(var0, Items.PINK_BANNER, Blocks.PINK_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
      carpet(var0, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
      bedFromPlanksAndWool(var0, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.PURPLE_BED, Items.PURPLE_DYE);
      banner(var0, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.RED_WOOL, Items.RED_DYE);
      carpet(var0, Blocks.RED_CARPET, Blocks.RED_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.RED_CARPET, Items.RED_DYE);
      bedFromPlanksAndWool(var0, Items.RED_BED, Blocks.RED_WOOL);
      bedFromWhiteBedAndDye(var0, Items.RED_BED, Items.RED_DYE);
      banner(var0, Items.RED_BANNER, Blocks.RED_WOOL);
      carpet(var0, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
      bedFromPlanksAndWool(var0, Items.WHITE_BED, Blocks.WHITE_WOOL);
      banner(var0, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
      carpet(var0, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
      bedFromPlanksAndWool(var0, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
      bedFromWhiteBedAndDye(var0, Items.YELLOW_BED, Items.YELLOW_DYE);
      banner(var0, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
      carpet(var0, Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK);
      stainedGlassFromGlassAndDye(var0, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
      stainedGlassFromGlassAndDye(var0, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
      stainedGlassPaneFromStainedGlass(var0, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
      stainedGlassPaneFromGlassPaneAndDye(var0, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.RED_TERRACOTTA, Items.RED_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
      coloredTerracottaFromTerracottaAndDye(var0, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
      concretePowder(var0, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
      concretePowder(var0, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
      concretePowder(var0, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
      concretePowder(var0, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
      concretePowder(var0, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
      concretePowder(var0, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
      concretePowder(var0, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
      concretePowder(var0, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
      concretePowder(var0, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
      concretePowder(var0, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
      concretePowder(var0, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
      concretePowder(var0, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
      concretePowder(var0, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
      concretePowder(var0, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
      concretePowder(var0, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
      concretePowder(var0, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
      ShapedRecipeBuilder.shaped(Items.CANDLE).define('S', (ItemLike)Items.STRING).define('H', (ItemLike)Items.HONEYCOMB).pattern("S").pattern("H").unlockedBy("has_string", has((ItemLike)Items.STRING)).unlockedBy("has_honeycomb", has((ItemLike)Items.HONEYCOMB)).save(var0);
      candle(var0, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
      candle(var0, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
      candle(var0, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
      candle(var0, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
      candle(var0, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
      candle(var0, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
      candle(var0, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
      candle(var0, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
      candle(var0, Blocks.LIME_CANDLE, Items.LIME_DYE);
      candle(var0, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
      candle(var0, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
      candle(var0, Blocks.PINK_CANDLE, Items.PINK_DYE);
      candle(var0, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
      candle(var0, Blocks.RED_CANDLE, Items.RED_DYE);
      candle(var0, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
      candle(var0, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
      ShapelessRecipeBuilder.shapeless(Blocks.PACKED_MUD, 1).requires((ItemLike)Blocks.MUD).requires((ItemLike)Items.WHEAT).unlockedBy("has_mud", has((ItemLike)Blocks.MUD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MUD_BRICKS, 4).define('#', (ItemLike)Blocks.PACKED_MUD).pattern("##").pattern("##").unlockedBy("has_packed_mud", has((ItemLike)Blocks.PACKED_MUD)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.MUDDY_MANGROVE_ROOTS, 1).requires((ItemLike)Blocks.MUD).requires((ItemLike)Items.MANGROVE_ROOTS).unlockedBy("has_mangrove_roots", has((ItemLike)Blocks.MANGROVE_ROOTS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ACTIVATOR_RAIL, 6).define('#', (ItemLike)Blocks.REDSTONE_TORCH).define('S', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XSX").pattern("X#X").pattern("XSX").unlockedBy("has_rail", has((ItemLike)Blocks.RAIL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.ANDESITE, 2).requires((ItemLike)Blocks.DIORITE).requires((ItemLike)Blocks.COBBLESTONE).unlockedBy("has_stone", has((ItemLike)Blocks.DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ANVIL).define('I', (ItemLike)Blocks.IRON_BLOCK).define('i', (ItemLike)Items.IRON_INGOT).pattern("III").pattern(" i ").pattern("iii").unlockedBy("has_iron_block", has((ItemLike)Blocks.IRON_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.ARMOR_STAND).define('/', (ItemLike)Items.STICK).define('_', (ItemLike)Blocks.SMOOTH_STONE_SLAB).pattern("///").pattern(" / ").pattern("/_/").unlockedBy("has_stone_slab", has((ItemLike)Blocks.SMOOTH_STONE_SLAB)).save(var0);
      ShapedRecipeBuilder.shaped(Items.ARROW, 4).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.FLINT).define('Y', (ItemLike)Items.FEATHER).pattern("X").pattern("#").pattern("Y").unlockedBy("has_feather", has((ItemLike)Items.FEATHER)).unlockedBy("has_flint", has((ItemLike)Items.FLINT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BARREL, 1).define('P', ItemTags.PLANKS).define('S', ItemTags.WOODEN_SLABS).pattern("PSP").pattern("P P").pattern("PSP").unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BEACON).define('S', (ItemLike)Items.NETHER_STAR).define('G', (ItemLike)Blocks.GLASS).define('O', (ItemLike)Blocks.OBSIDIAN).pattern("GGG").pattern("GSG").pattern("OOO").unlockedBy("has_nether_star", has((ItemLike)Items.NETHER_STAR)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BEEHIVE).define('P', ItemTags.PLANKS).define('H', (ItemLike)Items.HONEYCOMB).pattern("PPP").pattern("HHH").pattern("PPP").unlockedBy("has_honeycomb", has((ItemLike)Items.HONEYCOMB)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BEETROOT_SOUP).requires((ItemLike)Items.BOWL).requires((ItemLike)Items.BEETROOT, 6).unlockedBy("has_beetroot", has((ItemLike)Items.BEETROOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BLACK_DYE).requires((ItemLike)Items.INK_SAC).group("black_dye").unlockedBy("has_ink_sac", has((ItemLike)Items.INK_SAC)).save(var0);
      oneToOneConversionRecipe(var0, Items.BLACK_DYE, Blocks.WITHER_ROSE, "black_dye");
      ShapelessRecipeBuilder.shapeless(Items.BLAZE_POWDER, 2).requires((ItemLike)Items.BLAZE_ROD).unlockedBy("has_blaze_rod", has((ItemLike)Items.BLAZE_ROD)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BLUE_DYE).requires((ItemLike)Items.LAPIS_LAZULI).group("blue_dye").unlockedBy("has_lapis_lazuli", has((ItemLike)Items.LAPIS_LAZULI)).save(var0);
      oneToOneConversionRecipe(var0, Items.BLUE_DYE, Blocks.CORNFLOWER, "blue_dye");
      ShapedRecipeBuilder.shaped(Blocks.BLUE_ICE).define('#', (ItemLike)Blocks.PACKED_ICE).pattern("###").pattern("###").pattern("###").unlockedBy("has_packed_ice", has((ItemLike)Blocks.PACKED_ICE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL, 3).requires((ItemLike)Items.BONE).group("bonemeal").unlockedBy("has_bone", has((ItemLike)Items.BONE)).save(var0);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(var0, Items.BONE_MEAL, Items.BONE_BLOCK, "bone_meal_from_bone_block", "bonemeal");
      ShapelessRecipeBuilder.shapeless(Items.BOOK).requires((ItemLike)Items.PAPER, 3).requires((ItemLike)Items.LEATHER).unlockedBy("has_paper", has((ItemLike)Items.PAPER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BOOKSHELF).define('#', ItemTags.PLANKS).define('X', (ItemLike)Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", has((ItemLike)Items.BOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BOW).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.STRING).pattern(" #X").pattern("# X").pattern(" #X").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BOWL, 4).define('#', ItemTags.PLANKS).pattern("# #").pattern(" # ").unlockedBy("has_brown_mushroom", has((ItemLike)Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", has((ItemLike)Blocks.RED_MUSHROOM)).unlockedBy("has_mushroom_stew", has((ItemLike)Items.MUSHROOM_STEW)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BREAD).define('#', (ItemLike)Items.WHEAT).pattern("###").unlockedBy("has_wheat", has((ItemLike)Items.WHEAT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BREWING_STAND).define('B', (ItemLike)Items.BLAZE_ROD).define('#', ItemTags.STONE_CRAFTING_MATERIALS).pattern(" B ").pattern("###").unlockedBy("has_blaze_rod", has((ItemLike)Items.BLAZE_ROD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BRICKS).define('#', (ItemLike)Items.BRICK).pattern("##").pattern("##").unlockedBy("has_brick", has((ItemLike)Items.BRICK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BROWN_DYE).requires((ItemLike)Items.COCOA_BEANS).group("brown_dye").unlockedBy("has_cocoa_beans", has((ItemLike)Items.COCOA_BEANS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BUCKET).define('#', (ItemLike)Items.IRON_INGOT).pattern("# #").pattern(" # ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CAKE).define('A', (ItemLike)Items.MILK_BUCKET).define('B', (ItemLike)Items.SUGAR).define('C', (ItemLike)Items.WHEAT).define('E', (ItemLike)Items.EGG).pattern("AAA").pattern("BEB").pattern("CCC").unlockedBy("has_egg", has((ItemLike)Items.EGG)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CAMPFIRE).define('L', ItemTags.LOGS).define('S', (ItemLike)Items.STICK).define('C', ItemTags.COALS).pattern(" S ").pattern("SCS").pattern("LLL").unlockedBy("has_stick", has((ItemLike)Items.STICK)).unlockedBy("has_coal", has(ItemTags.COALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.CARROT_ON_A_STICK).define('#', (ItemLike)Items.FISHING_ROD).define('X', (ItemLike)Items.CARROT).pattern("# ").pattern(" X").unlockedBy("has_carrot", has((ItemLike)Items.CARROT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WARPED_FUNGUS_ON_A_STICK).define('#', (ItemLike)Items.FISHING_ROD).define('X', (ItemLike)Items.WARPED_FUNGUS).pattern("# ").pattern(" X").unlockedBy("has_warped_fungus", has((ItemLike)Items.WARPED_FUNGUS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CAULDRON).define('#', (ItemLike)Items.IRON_INGOT).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_water_bucket", has((ItemLike)Items.WATER_BUCKET)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COMPOSTER).define('#', ItemTags.WOODEN_SLABS).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHEST).define('#', ItemTags.PLANKS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_lots_of_items", new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.atLeast(10), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[0])).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.CHEST_MINECART).requires((ItemLike)Blocks.CHEST).requires((ItemLike)Items.MINECART).unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      chestBoat(var0, Items.ACACIA_CHEST_BOAT, Items.ACACIA_BOAT);
      chestBoat(var0, Items.BIRCH_CHEST_BOAT, Items.BIRCH_BOAT);
      chestBoat(var0, Items.DARK_OAK_CHEST_BOAT, Items.DARK_OAK_BOAT);
      chestBoat(var0, Items.JUNGLE_CHEST_BOAT, Items.JUNGLE_BOAT);
      chestBoat(var0, Items.OAK_CHEST_BOAT, Items.OAK_BOAT);
      chestBoat(var0, Items.SPRUCE_CHEST_BOAT, Items.SPRUCE_BOAT);
      chestBoat(var0, Items.MANGROVE_CHEST_BOAT, Items.MANGROVE_BOAT);
      chiseledBuilder(Blocks.CHISELED_QUARTZ_BLOCK, Ingredient.of(Blocks.QUARTZ_SLAB)).unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      chiseledBuilder(Blocks.CHISELED_STONE_BRICKS, Ingredient.of(Blocks.STONE_BRICK_SLAB)).unlockedBy("has_tag", has(ItemTags.STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CLAY).define('#', (ItemLike)Items.CLAY_BALL).pattern("##").pattern("##").unlockedBy("has_clay_ball", has((ItemLike)Items.CLAY_BALL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.CLOCK).define('#', (ItemLike)Items.GOLD_INGOT).define('X', (ItemLike)Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      nineBlockStorageRecipes(var0, Items.COAL, Items.COAL_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.COARSE_DIRT, 4).define('D', (ItemLike)Blocks.DIRT).define('G', (ItemLike)Blocks.GRAVEL).pattern("DG").pattern("GD").unlockedBy("has_gravel", has((ItemLike)Blocks.GRAVEL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COMPARATOR).define('#', (ItemLike)Blocks.REDSTONE_TORCH).define('X', (ItemLike)Items.QUARTZ).define('I', (ItemLike)Blocks.STONE).pattern(" # ").pattern("#X#").pattern("III").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Items.COMPASS).define('#', (ItemLike)Items.IRON_INGOT).define('X', (ItemLike)Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.COOKIE, 8).define('#', (ItemLike)Items.WHEAT).define('X', (ItemLike)Items.COCOA_BEANS).pattern("#X#").unlockedBy("has_cocoa", has((ItemLike)Items.COCOA_BEANS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CRAFTING_TABLE).define('#', ItemTags.PLANKS).pattern("##").pattern("##").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.CROSSBOW).define('~', (ItemLike)Items.STRING).define('#', (ItemLike)Items.STICK).define('&', (ItemLike)Items.IRON_INGOT).define('$', (ItemLike)Blocks.TRIPWIRE_HOOK).pattern("#&#").pattern("~$~").pattern(" # ").unlockedBy("has_string", has((ItemLike)Items.STRING)).unlockedBy("has_stick", has((ItemLike)Items.STICK)).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).unlockedBy("has_tripwire_hook", has((ItemLike)Blocks.TRIPWIRE_HOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LOOM).define('#', ItemTags.PLANKS).define('@', (ItemLike)Items.STRING).pattern("@@").pattern("##").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      chiseledBuilder(Blocks.CHISELED_RED_SANDSTONE, Ingredient.of(Blocks.RED_SANDSTONE_SLAB)).unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has((ItemLike)Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", has((ItemLike)Blocks.CUT_RED_SANDSTONE)).save(var0);
      chiseled(var0, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(var0, Items.COPPER_INGOT, Items.COPPER_BLOCK, getSimpleRecipeName(Items.COPPER_INGOT), getItemName(Items.COPPER_INGOT));
      ShapelessRecipeBuilder.shapeless(Items.COPPER_INGOT, 9).requires((ItemLike)Blocks.WAXED_COPPER_BLOCK).group(getItemName(Items.COPPER_INGOT)).unlockedBy(getHasName(Blocks.WAXED_COPPER_BLOCK), has((ItemLike)Blocks.WAXED_COPPER_BLOCK)).save(var0, getConversionRecipeName(Items.COPPER_INGOT, Blocks.WAXED_COPPER_BLOCK));
      waxRecipes(var0);
      ShapelessRecipeBuilder.shapeless(Items.CYAN_DYE, 2).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.GREEN_DYE).unlockedBy("has_green_dye", has((ItemLike)Items.GREEN_DYE)).unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE).define('S', (ItemLike)Items.PRISMARINE_SHARD).define('I', (ItemLike)Items.BLACK_DYE).pattern("SSS").pattern("SIS").pattern("SSS").unlockedBy("has_prismarine_shard", has((ItemLike)Items.PRISMARINE_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DAYLIGHT_DETECTOR).define('Q', (ItemLike)Items.QUARTZ).define('G', (ItemLike)Blocks.GLASS).define('W', Ingredient.of(ItemTags.WOODEN_SLABS)).pattern("GGG").pattern("QQQ").pattern("WWW").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DEEPSLATE_BRICKS, 4).define('S', (ItemLike)Blocks.POLISHED_DEEPSLATE).pattern("SS").pattern("SS").unlockedBy("has_polished_deepslate", has((ItemLike)Blocks.POLISHED_DEEPSLATE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DEEPSLATE_TILES, 4).define('S', (ItemLike)Blocks.DEEPSLATE_BRICKS).pattern("SS").pattern("SS").unlockedBy("has_deepslate_bricks", has((ItemLike)Blocks.DEEPSLATE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DETECTOR_RAIL, 6).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.STONE_PRESSURE_PLATE).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", has((ItemLike)Blocks.RAIL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_AXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.DIAMOND).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      nineBlockStorageRecipes(var0, Items.DIAMOND, Items.DIAMOND_BLOCK);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_BOOTS).define('X', (ItemLike)Items.DIAMOND).pattern("X X").pattern("X X").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_CHESTPLATE).define('X', (ItemLike)Items.DIAMOND).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_HELMET).define('X', (ItemLike)Items.DIAMOND).pattern("XXX").pattern("X X").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_HOE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.DIAMOND).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_LEGGINGS).define('X', (ItemLike)Items.DIAMOND).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.DIAMOND).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.DIAMOND).pattern("X").pattern("#").pattern("#").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_SWORD).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.DIAMOND).pattern("X").pattern("X").pattern("#").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DIORITE, 2).define('Q', (ItemLike)Items.QUARTZ).define('C', (ItemLike)Blocks.COBBLESTONE).pattern("CQ").pattern("QC").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DISPENSER).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).define('X', (ItemLike)Items.BOW).pattern("###").pattern("#X#").pattern("#R#").unlockedBy("has_bow", has((ItemLike)Items.BOW)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DRIPSTONE_BLOCK).define('#', (ItemLike)Items.POINTED_DRIPSTONE).pattern("##").pattern("##").group("pointed_dripstone").unlockedBy("has_pointed_dripstone", has((ItemLike)Items.POINTED_DRIPSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DROPPER).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("###").pattern("# #").pattern("#R#").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      nineBlockStorageRecipes(var0, Items.EMERALD, Items.EMERALD_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.ENCHANTING_TABLE).define('B', (ItemLike)Items.BOOK).define('#', (ItemLike)Blocks.OBSIDIAN).define('D', (ItemLike)Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has((ItemLike)Blocks.OBSIDIAN)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ENDER_CHEST).define('#', (ItemLike)Blocks.OBSIDIAN).define('E', (ItemLike)Items.ENDER_EYE).pattern("###").pattern("#E#").pattern("###").unlockedBy("has_ender_eye", has((ItemLike)Items.ENDER_EYE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.ENDER_EYE).requires((ItemLike)Items.ENDER_PEARL).requires((ItemLike)Items.BLAZE_POWDER).unlockedBy("has_blaze_powder", has((ItemLike)Items.BLAZE_POWDER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICKS, 4).define('#', (ItemLike)Blocks.END_STONE).pattern("##").pattern("##").unlockedBy("has_end_stone", has((ItemLike)Blocks.END_STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.END_CRYSTAL).define('T', (ItemLike)Items.GHAST_TEAR).define('E', (ItemLike)Items.ENDER_EYE).define('G', (ItemLike)Blocks.GLASS).pattern("GGG").pattern("GEG").pattern("GTG").unlockedBy("has_ender_eye", has((ItemLike)Items.ENDER_EYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_ROD, 4).define('#', (ItemLike)Items.POPPED_CHORUS_FRUIT).define('/', (ItemLike)Items.BLAZE_ROD).pattern("/").pattern("#").unlockedBy("has_chorus_fruit_popped", has((ItemLike)Items.POPPED_CHORUS_FRUIT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FERMENTED_SPIDER_EYE).requires((ItemLike)Items.SPIDER_EYE).requires((ItemLike)Blocks.BROWN_MUSHROOM).requires((ItemLike)Items.SUGAR).unlockedBy("has_spider_eye", has((ItemLike)Items.SPIDER_EYE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FIRE_CHARGE, 3).requires((ItemLike)Items.GUNPOWDER).requires((ItemLike)Items.BLAZE_POWDER).requires(Ingredient.of(Items.COAL, Items.CHARCOAL)).unlockedBy("has_blaze_powder", has((ItemLike)Items.BLAZE_POWDER)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FIREWORK_ROCKET, 3).requires((ItemLike)Items.GUNPOWDER).requires((ItemLike)Items.PAPER).unlockedBy("has_gunpowder", has((ItemLike)Items.GUNPOWDER)).save(var0, "firework_rocket_simple");
      ShapedRecipeBuilder.shaped(Items.FISHING_ROD).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.STRING).pattern("  #").pattern(" #X").pattern("# X").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FLINT_AND_STEEL).requires((ItemLike)Items.IRON_INGOT).requires((ItemLike)Items.FLINT).unlockedBy("has_flint", has((ItemLike)Items.FLINT)).unlockedBy("has_obsidian", has((ItemLike)Blocks.OBSIDIAN)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.FLOWER_POT).define('#', (ItemLike)Items.BRICK).pattern("# #").pattern(" # ").unlockedBy("has_brick", has((ItemLike)Items.BRICK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.FURNACE).define('#', ItemTags.STONE_CRAFTING_MATERIALS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_cobblestone", has(ItemTags.STONE_CRAFTING_MATERIALS)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FURNACE_MINECART).requires((ItemLike)Blocks.FURNACE).requires((ItemLike)Items.MINECART).unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GLASS_BOTTLE, 3).define('#', (ItemLike)Blocks.GLASS).pattern("# #").pattern(" # ").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GLASS_PANE, 16).define('#', (ItemLike)Blocks.GLASS).pattern("###").pattern("###").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GLOWSTONE).define('#', (ItemLike)Items.GLOWSTONE_DUST).pattern("##").pattern("##").unlockedBy("has_glowstone_dust", has((ItemLike)Items.GLOWSTONE_DUST)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.GLOW_ITEM_FRAME).requires((ItemLike)Items.ITEM_FRAME).requires((ItemLike)Items.GLOW_INK_SAC).unlockedBy("has_item_frame", has((ItemLike)Items.ITEM_FRAME)).unlockedBy("has_glow_ink_sac", has((ItemLike)Items.GLOW_INK_SAC)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_APPLE).define('#', (ItemLike)Items.GOLD_INGOT).define('X', (ItemLike)Items.APPLE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_AXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.GOLD_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_BOOTS).define('X', (ItemLike)Items.GOLD_INGOT).pattern("X X").pattern("X X").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_CARROT).define('#', (ItemLike)Items.GOLD_NUGGET).define('X', (ItemLike)Items.CARROT).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_gold_nugget", has((ItemLike)Items.GOLD_NUGGET)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_CHESTPLATE).define('X', (ItemLike)Items.GOLD_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_HELMET).define('X', (ItemLike)Items.GOLD_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_HOE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.GOLD_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_LEGGINGS).define('X', (ItemLike)Items.GOLD_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.GOLD_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POWERED_RAIL, 6).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.GOLD_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", has((ItemLike)Blocks.RAIL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.GOLD_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GOLDEN_SWORD).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.GOLD_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(var0, Items.GOLD_INGOT, Items.GOLD_BLOCK, "gold_ingot_from_gold_block", "gold_ingot");
      nineBlockStorageRecipesWithCustomPacking(var0, Items.GOLD_NUGGET, Items.GOLD_INGOT, "gold_ingot_from_nuggets", "gold_ingot");
      ShapelessRecipeBuilder.shapeless(Blocks.GRANITE).requires((ItemLike)Blocks.DIORITE).requires((ItemLike)Items.QUARTZ).unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.GRAY_DYE, 2).requires((ItemLike)Items.BLACK_DYE).requires((ItemLike)Items.WHITE_DYE).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).unlockedBy("has_black_dye", has((ItemLike)Items.BLACK_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HAY_BLOCK).define('#', (ItemLike)Items.WHEAT).pattern("###").pattern("###").pattern("###").unlockedBy("has_wheat", has((ItemLike)Items.WHEAT)).save(var0);
      pressurePlate(var0, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
      ShapelessRecipeBuilder.shapeless(Items.HONEY_BOTTLE, 4).requires((ItemLike)Items.HONEY_BLOCK).requires((ItemLike)Items.GLASS_BOTTLE, 4).unlockedBy("has_honey_block", has((ItemLike)Blocks.HONEY_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HONEY_BLOCK, 1).define('S', (ItemLike)Items.HONEY_BOTTLE).pattern("SS").pattern("SS").unlockedBy("has_honey_bottle", has((ItemLike)Items.HONEY_BOTTLE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HONEYCOMB_BLOCK).define('H', (ItemLike)Items.HONEYCOMB).pattern("HH").pattern("HH").unlockedBy("has_honeycomb", has((ItemLike)Items.HONEYCOMB)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HOPPER).define('C', (ItemLike)Blocks.CHEST).define('I', (ItemLike)Items.IRON_INGOT).pattern("I I").pattern("ICI").pattern(" I ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.HOPPER_MINECART).requires((ItemLike)Blocks.HOPPER).requires((ItemLike)Items.MINECART).unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_AXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.IRON_BARS, 16).define('#', (ItemLike)Items.IRON_INGOT).pattern("###").pattern("###").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_BOOTS).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_CHESTPLATE).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      doorBuilder(Blocks.IRON_DOOR, Ingredient.of(Items.IRON_INGOT)).unlockedBy(getHasName(Items.IRON_INGOT), has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_HELMET).define('X', (ItemLike)Items.IRON_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_HOE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(var0, Items.IRON_INGOT, Items.IRON_BLOCK, "iron_ingot_from_iron_block", "iron_ingot");
      nineBlockStorageRecipesWithCustomPacking(var0, Items.IRON_NUGGET, Items.IRON_INGOT, "iron_ingot_from_nuggets", "iron_ingot");
      ShapedRecipeBuilder.shaped(Items.IRON_LEGGINGS).define('X', (ItemLike)Items.IRON_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_SWORD).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.IRON_TRAPDOOR).define('#', (ItemLike)Items.IRON_INGOT).pattern("##").pattern("##").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.ITEM_FRAME).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.LEATHER).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.JUKEBOX).define('#', ItemTags.PLANKS).define('X', (ItemLike)Items.DIAMOND).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LADDER, 3).define('#', (ItemLike)Items.STICK).pattern("# #").pattern("###").pattern("# #").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      nineBlockStorageRecipes(var0, Items.LAPIS_LAZULI, Items.LAPIS_BLOCK);
      ShapedRecipeBuilder.shaped(Items.LEAD, 2).define('~', (ItemLike)Items.STRING).define('O', (ItemLike)Items.SLIME_BALL).pattern("~~ ").pattern("~O ").pattern("  ~").unlockedBy("has_slime_ball", has((ItemLike)Items.SLIME_BALL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER).define('#', (ItemLike)Items.RABBIT_HIDE).pattern("##").pattern("##").unlockedBy("has_rabbit_hide", has((ItemLike)Items.RABBIT_HIDE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_BOOTS).define('X', (ItemLike)Items.LEATHER).pattern("X X").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_CHESTPLATE).define('X', (ItemLike)Items.LEATHER).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_HELMET).define('X', (ItemLike)Items.LEATHER).pattern("XXX").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_LEGGINGS).define('X', (ItemLike)Items.LEATHER).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_HORSE_ARMOR).define('X', (ItemLike)Items.LEATHER).pattern("X X").pattern("XXX").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LECTERN).define('S', ItemTags.WOODEN_SLABS).define('B', (ItemLike)Blocks.BOOKSHELF).pattern("SSS").pattern(" B ").pattern(" S ").unlockedBy("has_book", has((ItemLike)Items.BOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LEVER).define('#', (ItemLike)Blocks.COBBLESTONE).define('X', (ItemLike)Items.STICK).pattern("X").pattern("#").unlockedBy("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0);
      oneToOneConversionRecipe(var0, Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID, "light_blue_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_BLUE_DYE, 2).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.WHITE_DYE).group("light_blue_dye").unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0, "light_blue_dye_from_blue_white_dye");
      oneToOneConversionRecipe(var0, Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET, "light_gray_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 2).requires((ItemLike)Items.GRAY_DYE).requires((ItemLike)Items.WHITE_DYE).group("light_gray_dye").unlockedBy("has_gray_dye", has((ItemLike)Items.GRAY_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0, "light_gray_dye_from_gray_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 3).requires((ItemLike)Items.BLACK_DYE).requires((ItemLike)Items.WHITE_DYE, 2).group("light_gray_dye").unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).unlockedBy("has_black_dye", has((ItemLike)Items.BLACK_DYE)).save(var0, "light_gray_dye_from_black_white_dye");
      oneToOneConversionRecipe(var0, Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY, "light_gray_dye");
      oneToOneConversionRecipe(var0, Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP, "light_gray_dye");
      pressurePlate(var0, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
      ShapedRecipeBuilder.shaped(Blocks.LIGHTNING_ROD).define('#', (ItemLike)Items.COPPER_INGOT).pattern("#").pattern("#").pattern("#").unlockedBy("has_copper_ingot", has((ItemLike)Items.COPPER_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.LIME_DYE, 2).requires((ItemLike)Items.GREEN_DYE).requires((ItemLike)Items.WHITE_DYE).unlockedBy("has_green_dye", has((ItemLike)Items.GREEN_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.JACK_O_LANTERN).define('A', (ItemLike)Blocks.CARVED_PUMPKIN).define('B', (ItemLike)Blocks.TORCH).pattern("A").pattern("B").unlockedBy("has_carved_pumpkin", has((ItemLike)Blocks.CARVED_PUMPKIN)).save(var0);
      oneToOneConversionRecipe(var0, Items.MAGENTA_DYE, Blocks.ALLIUM, "magenta_dye");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 4).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.RED_DYE, 2).requires((ItemLike)Items.WHITE_DYE).group("magenta_dye").unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_rose_red", has((ItemLike)Items.RED_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0, "magenta_dye_from_blue_red_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 3).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.RED_DYE).requires((ItemLike)Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", has((ItemLike)Items.PINK_DYE)).unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).save(var0, "magenta_dye_from_blue_red_pink");
      oneToOneConversionRecipe(var0, Items.MAGENTA_DYE, Blocks.LILAC, "magenta_dye", 2);
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 2).requires((ItemLike)Items.PURPLE_DYE).requires((ItemLike)Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", has((ItemLike)Items.PINK_DYE)).unlockedBy("has_purple_dye", has((ItemLike)Items.PURPLE_DYE)).save(var0, "magenta_dye_from_purple_and_pink");
      ShapedRecipeBuilder.shaped(Blocks.MAGMA_BLOCK).define('#', (ItemLike)Items.MAGMA_CREAM).pattern("##").pattern("##").unlockedBy("has_magma_cream", has((ItemLike)Items.MAGMA_CREAM)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MAGMA_CREAM).requires((ItemLike)Items.BLAZE_POWDER).requires((ItemLike)Items.SLIME_BALL).unlockedBy("has_blaze_powder", has((ItemLike)Items.BLAZE_POWDER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.MAP).define('#', (ItemLike)Items.PAPER).define('X', (ItemLike)Items.COMPASS).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_compass", has((ItemLike)Items.COMPASS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MELON).define('M', (ItemLike)Items.MELON_SLICE).pattern("MMM").pattern("MMM").pattern("MMM").unlockedBy("has_melon", has((ItemLike)Items.MELON_SLICE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MELON_SEEDS).requires((ItemLike)Items.MELON_SLICE).unlockedBy("has_melon", has((ItemLike)Items.MELON_SLICE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.MINECART).define('#', (ItemLike)Items.IRON_INGOT).pattern("# #").pattern("###").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_COBBLESTONE).requires((ItemLike)Blocks.COBBLESTONE).requires((ItemLike)Blocks.VINE).group("mossy_cobblestone").unlockedBy("has_vine", has((ItemLike)Blocks.VINE)).save(var0, getConversionRecipeName(Blocks.MOSSY_COBBLESTONE, Blocks.VINE));
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_STONE_BRICKS).requires((ItemLike)Blocks.STONE_BRICKS).requires((ItemLike)Blocks.VINE).group("mossy_stone_bricks").unlockedBy("has_vine", has((ItemLike)Blocks.VINE)).save(var0, getConversionRecipeName(Blocks.MOSSY_STONE_BRICKS, Blocks.VINE));
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_COBBLESTONE).requires((ItemLike)Blocks.COBBLESTONE).requires((ItemLike)Blocks.MOSS_BLOCK).group("mossy_cobblestone").unlockedBy("has_moss_block", has((ItemLike)Blocks.MOSS_BLOCK)).save(var0, getConversionRecipeName(Blocks.MOSSY_COBBLESTONE, Blocks.MOSS_BLOCK));
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_STONE_BRICKS).requires((ItemLike)Blocks.STONE_BRICKS).requires((ItemLike)Blocks.MOSS_BLOCK).group("mossy_stone_bricks").unlockedBy("has_moss_block", has((ItemLike)Blocks.MOSS_BLOCK)).save(var0, getConversionRecipeName(Blocks.MOSSY_STONE_BRICKS, Blocks.MOSS_BLOCK));
      ShapelessRecipeBuilder.shapeless(Items.MUSHROOM_STEW).requires((ItemLike)Blocks.BROWN_MUSHROOM).requires((ItemLike)Blocks.RED_MUSHROOM).requires((ItemLike)Items.BOWL).unlockedBy("has_mushroom_stew", has((ItemLike)Items.MUSHROOM_STEW)).unlockedBy("has_bowl", has((ItemLike)Items.BOWL)).unlockedBy("has_brown_mushroom", has((ItemLike)Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", has((ItemLike)Blocks.RED_MUSHROOM)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICKS).define('N', (ItemLike)Items.NETHER_BRICK).pattern("NN").pattern("NN").unlockedBy("has_netherbrick", has((ItemLike)Items.NETHER_BRICK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_WART_BLOCK).define('#', (ItemLike)Items.NETHER_WART).pattern("###").pattern("###").pattern("###").unlockedBy("has_nether_wart", has((ItemLike)Items.NETHER_WART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NOTE_BLOCK).define('#', ItemTags.PLANKS).define('X', (ItemLike)Items.REDSTONE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.OBSERVER).define('Q', (ItemLike)Items.QUARTZ).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("###").pattern("RRQ").pattern("###").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      oneToOneConversionRecipe(var0, Items.ORANGE_DYE, Blocks.ORANGE_TULIP, "orange_dye");
      ShapelessRecipeBuilder.shapeless(Items.ORANGE_DYE, 2).requires((ItemLike)Items.RED_DYE).requires((ItemLike)Items.YELLOW_DYE).group("orange_dye").unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).unlockedBy("has_yellow_dye", has((ItemLike)Items.YELLOW_DYE)).save(var0, "orange_dye_from_red_yellow");
      ShapedRecipeBuilder.shaped(Items.PAINTING).define('#', (ItemLike)Items.STICK).define('X', Ingredient.of(ItemTags.WOOL)).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_wool", has(ItemTags.WOOL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.PAPER, 3).define('#', (ItemLike)Blocks.SUGAR_CANE).pattern("###").unlockedBy("has_reeds", has((ItemLike)Blocks.SUGAR_CANE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_PILLAR, 2).define('#', (ItemLike)Blocks.QUARTZ_BLOCK).pattern("#").pattern("#").unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.PACKED_ICE).requires((ItemLike)Blocks.ICE, 9).unlockedBy("has_ice", has((ItemLike)Blocks.ICE)).save(var0);
      oneToOneConversionRecipe(var0, Items.PINK_DYE, Blocks.PEONY, "pink_dye", 2);
      oneToOneConversionRecipe(var0, Items.PINK_DYE, Blocks.PINK_TULIP, "pink_dye");
      ShapelessRecipeBuilder.shapeless(Items.PINK_DYE, 2).requires((ItemLike)Items.RED_DYE).requires((ItemLike)Items.WHITE_DYE).group("pink_dye").unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).save(var0, "pink_dye_from_red_white_dye");
      ShapedRecipeBuilder.shaped(Blocks.PISTON).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).define('T', ItemTags.PLANKS).define('X', (ItemLike)Items.IRON_INGOT).pattern("TTT").pattern("#X#").pattern("#R#").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      polished(var0, Blocks.POLISHED_BASALT, Blocks.BASALT);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE).define('S', (ItemLike)Items.PRISMARINE_SHARD).pattern("SS").pattern("SS").unlockedBy("has_prismarine_shard", has((ItemLike)Items.PRISMARINE_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICKS).define('S', (ItemLike)Items.PRISMARINE_SHARD).pattern("SSS").pattern("SSS").pattern("SSS").unlockedBy("has_prismarine_shard", has((ItemLike)Items.PRISMARINE_SHARD)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_PIE).requires((ItemLike)Blocks.PUMPKIN).requires((ItemLike)Items.SUGAR).requires((ItemLike)Items.EGG).unlockedBy("has_carved_pumpkin", has((ItemLike)Blocks.CARVED_PUMPKIN)).unlockedBy("has_pumpkin", has((ItemLike)Blocks.PUMPKIN)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_SEEDS, 4).requires((ItemLike)Blocks.PUMPKIN).unlockedBy("has_pumpkin", has((ItemLike)Blocks.PUMPKIN)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PURPLE_DYE, 2).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.RED_DYE).unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SHULKER_BOX).define('#', (ItemLike)Blocks.CHEST).define('-', (ItemLike)Items.SHULKER_SHELL).pattern("-").pattern("#").pattern("-").unlockedBy("has_shulker_shell", has((ItemLike)Items.SHULKER_SHELL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_BLOCK, 4).define('F', (ItemLike)Items.POPPED_CHORUS_FRUIT).pattern("FF").pattern("FF").unlockedBy("has_chorus_fruit_popped", has((ItemLike)Items.POPPED_CHORUS_FRUIT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_PILLAR).define('#', (ItemLike)Blocks.PURPUR_SLAB).pattern("#").pattern("#").unlockedBy("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0);
      slabBuilder(Blocks.PURPUR_SLAB, Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).unlockedBy("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0);
      stairBuilder(Blocks.PURPUR_STAIRS, Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).unlockedBy("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BLOCK).define('#', (ItemLike)Items.QUARTZ).pattern("##").pattern("##").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BRICKS, 4).define('#', (ItemLike)Blocks.QUARTZ_BLOCK).pattern("##").pattern("##").unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0);
      slabBuilder(Blocks.QUARTZ_SLAB, Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      stairBuilder(Blocks.QUARTZ_STAIRS, Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires((ItemLike)Items.BAKED_POTATO).requires((ItemLike)Items.COOKED_RABBIT).requires((ItemLike)Items.BOWL).requires((ItemLike)Items.CARROT).requires((ItemLike)Blocks.BROWN_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", has((ItemLike)Items.COOKED_RABBIT)).save(var0, getConversionRecipeName(Items.RABBIT_STEW, Items.BROWN_MUSHROOM));
      ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires((ItemLike)Items.BAKED_POTATO).requires((ItemLike)Items.COOKED_RABBIT).requires((ItemLike)Items.BOWL).requires((ItemLike)Items.CARROT).requires((ItemLike)Blocks.RED_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", has((ItemLike)Items.COOKED_RABBIT)).save(var0, getConversionRecipeName(Items.RABBIT_STEW, Items.RED_MUSHROOM));
      ShapedRecipeBuilder.shaped(Blocks.RAIL, 16).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("X X").unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      nineBlockStorageRecipes(var0, Items.REDSTONE, Items.REDSTONE_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_LAMP).define('R', (ItemLike)Items.REDSTONE).define('G', (ItemLike)Blocks.GLOWSTONE).pattern(" R ").pattern("RGR").pattern(" R ").unlockedBy("has_glowstone", has((ItemLike)Blocks.GLOWSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_TORCH).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.REDSTONE).pattern("X").pattern("#").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      oneToOneConversionRecipe(var0, Items.RED_DYE, Items.BEETROOT, "red_dye");
      oneToOneConversionRecipe(var0, Items.RED_DYE, Blocks.POPPY, "red_dye");
      oneToOneConversionRecipe(var0, Items.RED_DYE, Blocks.ROSE_BUSH, "red_dye", 2);
      ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires((ItemLike)Blocks.RED_TULIP).group("red_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.RED_TULIP)).save(var0, "red_dye_from_tulip");
      ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICKS).define('W', (ItemLike)Items.NETHER_WART).define('N', (ItemLike)Items.NETHER_BRICK).pattern("NW").pattern("WN").unlockedBy("has_nether_wart", has((ItemLike)Items.NETHER_WART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE).define('#', (ItemLike)Blocks.RED_SAND).pattern("##").pattern("##").unlockedBy("has_sand", has((ItemLike)Blocks.RED_SAND)).save(var0);
      slabBuilder(Blocks.RED_SANDSTONE_SLAB, Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has((ItemLike)Blocks.CHISELED_RED_SANDSTONE)).save(var0);
      stairBuilder(Blocks.RED_SANDSTONE_STAIRS, Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has((ItemLike)Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", has((ItemLike)Blocks.CUT_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.REPEATER).define('#', (ItemLike)Blocks.REDSTONE_TORCH).define('X', (ItemLike)Items.REDSTONE).define('I', (ItemLike)Blocks.STONE).pattern("#X#").pattern("III").unlockedBy("has_redstone_torch", has((ItemLike)Blocks.REDSTONE_TORCH)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SANDSTONE).define('#', (ItemLike)Blocks.SAND).pattern("##").pattern("##").unlockedBy("has_sand", has((ItemLike)Blocks.SAND)).save(var0);
      slabBuilder(Blocks.SANDSTONE_SLAB, Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", has((ItemLike)Blocks.CHISELED_SANDSTONE)).save(var0);
      stairBuilder(Blocks.SANDSTONE_STAIRS, Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", has((ItemLike)Blocks.CHISELED_SANDSTONE)).unlockedBy("has_cut_sandstone", has((ItemLike)Blocks.CUT_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SEA_LANTERN).define('S', (ItemLike)Items.PRISMARINE_SHARD).define('C', (ItemLike)Items.PRISMARINE_CRYSTALS).pattern("SCS").pattern("CCC").pattern("SCS").unlockedBy("has_prismarine_crystals", has((ItemLike)Items.PRISMARINE_CRYSTALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SHEARS).define('#', (ItemLike)Items.IRON_INGOT).pattern(" #").pattern("# ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SHIELD).define('W', ItemTags.PLANKS).define('o', (ItemLike)Items.IRON_INGOT).pattern("WoW").pattern("WWW").pattern(" W ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      nineBlockStorageRecipes(var0, Items.SLIME_BALL, Items.SLIME_BLOCK);
      cut(var0, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      cut(var0, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
      ShapedRecipeBuilder.shaped(Blocks.SNOW_BLOCK).define('#', (ItemLike)Items.SNOWBALL).pattern("##").pattern("##").unlockedBy("has_snowball", has((ItemLike)Items.SNOWBALL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SNOW, 6).define('#', (ItemLike)Blocks.SNOW_BLOCK).pattern("###").unlockedBy("has_snowball", has((ItemLike)Items.SNOWBALL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_CAMPFIRE).define('L', ItemTags.LOGS).define('S', (ItemLike)Items.STICK).define('#', ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern(" S ").pattern("S#S").pattern("LLL").unlockedBy("has_stick", has((ItemLike)Items.STICK)).unlockedBy("has_soul_sand", has(ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GLISTERING_MELON_SLICE).define('#', (ItemLike)Items.GOLD_NUGGET).define('X', (ItemLike)Items.MELON_SLICE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_melon", has((ItemLike)Items.MELON_SLICE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SPECTRAL_ARROW, 2).define('#', (ItemLike)Items.GLOWSTONE_DUST).define('X', (ItemLike)Items.ARROW).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_glowstone_dust", has((ItemLike)Items.GLOWSTONE_DUST)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SPYGLASS).define('#', (ItemLike)Items.AMETHYST_SHARD).define('X', (ItemLike)Items.COPPER_INGOT).pattern(" # ").pattern(" X ").pattern(" X ").unlockedBy("has_amethyst_shard", has((ItemLike)Items.AMETHYST_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STICK, 4).define('#', ItemTags.PLANKS).pattern("#").pattern("#").group("sticks").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STICK, 1).define('#', (ItemLike)Blocks.BAMBOO).pattern("#").pattern("#").group("sticks").unlockedBy("has_bamboo", has((ItemLike)Blocks.BAMBOO)).save(var0, "stick_from_bamboo_item");
      ShapedRecipeBuilder.shaped(Blocks.STICKY_PISTON).define('P', (ItemLike)Blocks.PISTON).define('S', (ItemLike)Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has((ItemLike)Items.SLIME_BALL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_BRICKS, 4).define('#', (ItemLike)Blocks.STONE).pattern("##").pattern("##").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_AXE).define('#', (ItemLike)Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      slabBuilder(Blocks.STONE_BRICK_SLAB, Ingredient.of(Blocks.STONE_BRICKS)).unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS)).save(var0);
      stairBuilder(Blocks.STONE_BRICK_STAIRS, Ingredient.of(Blocks.STONE_BRICKS)).unlockedBy("has_stone_bricks", has(ItemTags.STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_HOE).define('#', (ItemLike)Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("#").pattern("#").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      slab(var0, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
      ShapedRecipeBuilder.shaped(Items.STONE_SWORD).define('#', (ItemLike)Items.STICK).define('X', ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("X").pattern("#").unlockedBy("has_cobblestone", has(ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.WHITE_WOOL).define('#', (ItemLike)Items.STRING).pattern("##").pattern("##").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0, getConversionRecipeName(Blocks.WHITE_WOOL, Items.STRING));
      oneToOneConversionRecipe(var0, Items.SUGAR, Blocks.SUGAR_CANE, "sugar");
      ShapelessRecipeBuilder.shapeless(Items.SUGAR, 3).requires((ItemLike)Items.HONEY_BOTTLE).group("sugar").unlockedBy("has_honey_bottle", has((ItemLike)Items.HONEY_BOTTLE)).save(var0, getConversionRecipeName(Items.SUGAR, Items.HONEY_BOTTLE));
      ShapedRecipeBuilder.shaped(Blocks.TARGET).define('H', (ItemLike)Items.HAY_BLOCK).define('R', (ItemLike)Items.REDSTONE).pattern(" R ").pattern("RHR").pattern(" R ").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).unlockedBy("has_hay_block", has((ItemLike)Blocks.HAY_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TNT).define('#', Ingredient.of(Blocks.SAND, Blocks.RED_SAND)).define('X', (ItemLike)Items.GUNPOWDER).pattern("X#X").pattern("#X#").pattern("X#X").unlockedBy("has_gunpowder", has((ItemLike)Items.GUNPOWDER)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.TNT_MINECART).requires((ItemLike)Blocks.TNT).requires((ItemLike)Items.MINECART).unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TORCH, 4).define('#', (ItemLike)Items.STICK).define('X', Ingredient.of(Items.COAL, Items.CHARCOAL)).pattern("X").pattern("#").unlockedBy("has_stone_pickaxe", has((ItemLike)Items.STONE_PICKAXE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_TORCH, 4).define('X', Ingredient.of(Items.COAL, Items.CHARCOAL)).define('#', (ItemLike)Items.STICK).define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern("X").pattern("#").pattern("S").unlockedBy("has_soul_sand", has(ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LANTERN).define('#', (ItemLike)Items.TORCH).define('X', (ItemLike)Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_iron_nugget", has((ItemLike)Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_LANTERN).define('#', (ItemLike)Items.SOUL_TORCH).define('X', (ItemLike)Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_soul_torch", has((ItemLike)Items.SOUL_TORCH)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.TRAPPED_CHEST).requires((ItemLike)Blocks.CHEST).requires((ItemLike)Blocks.TRIPWIRE_HOOK).unlockedBy("has_tripwire_hook", has((ItemLike)Blocks.TRIPWIRE_HOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TRIPWIRE_HOOK, 2).define('#', ItemTags.PLANKS).define('S', (ItemLike)Items.STICK).define('I', (ItemLike)Items.IRON_INGOT).pattern("I").pattern("S").pattern("#").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapedRecipeBuilder.shaped(Items.TURTLE_HELMET).define('X', (ItemLike)Items.SCUTE).pattern("XXX").pattern("X X").unlockedBy("has_scute", has((ItemLike)Items.SCUTE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WHEAT, 9).requires((ItemLike)Blocks.HAY_BLOCK).unlockedBy("has_hay_block", has((ItemLike)Blocks.HAY_BLOCK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WHITE_DYE).requires((ItemLike)Items.BONE_MEAL).group("white_dye").unlockedBy("has_bone_meal", has((ItemLike)Items.BONE_MEAL)).save(var0);
      oneToOneConversionRecipe(var0, Items.WHITE_DYE, Blocks.LILY_OF_THE_VALLEY, "white_dye");
      ShapedRecipeBuilder.shaped(Items.WOODEN_AXE).define('#', (ItemLike)Items.STICK).define('X', ItemTags.PLANKS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_HOE).define('#', (ItemLike)Items.STICK).define('X', ItemTags.PLANKS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', ItemTags.PLANKS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', ItemTags.PLANKS).pattern("X").pattern("#").pattern("#").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_SWORD).define('#', (ItemLike)Items.STICK).define('X', ItemTags.PLANKS).pattern("X").pattern("X").pattern("#").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WRITABLE_BOOK).requires((ItemLike)Items.BOOK).requires((ItemLike)Items.INK_SAC).requires((ItemLike)Items.FEATHER).unlockedBy("has_book", has((ItemLike)Items.BOOK)).save(var0);
      oneToOneConversionRecipe(var0, Items.YELLOW_DYE, Blocks.DANDELION, "yellow_dye");
      oneToOneConversionRecipe(var0, Items.YELLOW_DYE, Blocks.SUNFLOWER, "yellow_dye", 2);
      nineBlockStorageRecipes(var0, Items.DRIED_KELP, Items.DRIED_KELP_BLOCK);
      ShapedRecipeBuilder.shaped(Blocks.CONDUIT).define('#', (ItemLike)Items.NAUTILUS_SHELL).define('X', (ItemLike)Items.HEART_OF_THE_SEA).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_nautilus_core", has((ItemLike)Items.HEART_OF_THE_SEA)).unlockedBy("has_nautilus_shell", has((ItemLike)Items.NAUTILUS_SHELL)).save(var0);
      wall(var0, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
      wall(var0, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
      wall(var0, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
      ShapelessRecipeBuilder.shapeless(Items.CREEPER_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.CREEPER_HEAD).unlockedBy("has_creeper_head", has((ItemLike)Items.CREEPER_HEAD)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.SKULL_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.WITHER_SKELETON_SKULL).unlockedBy("has_wither_skeleton_skull", has((ItemLike)Items.WITHER_SKELETON_SKULL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FLOWER_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Blocks.OXEYE_DAISY).unlockedBy("has_oxeye_daisy", has((ItemLike)Blocks.OXEYE_DAISY)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MOJANG_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.ENCHANTED_GOLDEN_APPLE).unlockedBy("has_enchanted_golden_apple", has((ItemLike)Items.ENCHANTED_GOLDEN_APPLE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SCAFFOLDING, 6).define('~', (ItemLike)Items.STRING).define('I', (ItemLike)Blocks.BAMBOO).pattern("I~I").pattern("I I").pattern("I I").unlockedBy("has_bamboo", has((ItemLike)Blocks.BAMBOO)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GRINDSTONE).define('I', (ItemLike)Items.STICK).define('-', (ItemLike)Blocks.STONE_SLAB).define('#', ItemTags.PLANKS).pattern("I-I").pattern("# #").unlockedBy("has_stone_slab", has((ItemLike)Blocks.STONE_SLAB)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BLAST_FURNACE).define('#', (ItemLike)Blocks.SMOOTH_STONE).define('X', (ItemLike)Blocks.FURNACE).define('I', (ItemLike)Items.IRON_INGOT).pattern("III").pattern("IXI").pattern("###").unlockedBy("has_smooth_stone", has((ItemLike)Blocks.SMOOTH_STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOKER).define('#', ItemTags.LOGS).define('X', (ItemLike)Blocks.FURNACE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_furnace", has((ItemLike)Blocks.FURNACE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CARTOGRAPHY_TABLE).define('#', ItemTags.PLANKS).define('@', (ItemLike)Items.PAPER).pattern("@@").pattern("##").pattern("##").unlockedBy("has_paper", has((ItemLike)Items.PAPER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMITHING_TABLE).define('#', ItemTags.PLANKS).define('@', (ItemLike)Items.IRON_INGOT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.FLETCHING_TABLE).define('#', ItemTags.PLANKS).define('@', (ItemLike)Items.FLINT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_flint", has((ItemLike)Items.FLINT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONECUTTER).define('I', (ItemLike)Items.IRON_INGOT).define('#', (ItemLike)Blocks.STONE).pattern(" I ").pattern("###").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LODESTONE).define('S', (ItemLike)Items.CHISELED_STONE_BRICKS).define('#', (ItemLike)Items.NETHERITE_INGOT).pattern("SSS").pattern("S#S").pattern("SSS").unlockedBy("has_netherite_ingot", has((ItemLike)Items.NETHERITE_INGOT)).save(var0);
      nineBlockStorageRecipesRecipesWithCustomUnpacking(var0, Items.NETHERITE_INGOT, Items.NETHERITE_BLOCK, "netherite_ingot_from_netherite_block", "netherite_ingot");
      ShapelessRecipeBuilder.shapeless(Items.NETHERITE_INGOT).requires((ItemLike)Items.NETHERITE_SCRAP, 4).requires((ItemLike)Items.GOLD_INGOT, 4).group("netherite_ingot").unlockedBy("has_netherite_scrap", has((ItemLike)Items.NETHERITE_SCRAP)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RESPAWN_ANCHOR).define('O', (ItemLike)Blocks.CRYING_OBSIDIAN).define('G', (ItemLike)Blocks.GLOWSTONE).pattern("OOO").pattern("GGG").pattern("OOO").unlockedBy("has_obsidian", has((ItemLike)Blocks.CRYING_OBSIDIAN)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHAIN).define('I', (ItemLike)Items.IRON_INGOT).define('N', (ItemLike)Items.IRON_NUGGET).pattern("N").pattern("I").pattern("N").unlockedBy("has_iron_nugget", has((ItemLike)Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TINTED_GLASS, 2).define('G', (ItemLike)Blocks.GLASS).define('S', (ItemLike)Items.AMETHYST_SHARD).pattern(" S ").pattern("SGS").pattern(" S ").unlockedBy("has_amethyst_shard", has((ItemLike)Items.AMETHYST_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.AMETHYST_BLOCK).define('S', (ItemLike)Items.AMETHYST_SHARD).pattern("SS").pattern("SS").unlockedBy("has_amethyst_shard", has((ItemLike)Items.AMETHYST_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Items.RECOVERY_COMPASS).define('C', (ItemLike)Items.COMPASS).define('S', (ItemLike)Items.ECHO_SHARD).pattern("SSS").pattern("SCS").pattern("SSS").unlockedBy("has_echo_shard", has((ItemLike)Items.ECHO_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Items.MUSIC_DISC_5).define('S', (ItemLike)Items.DISC_FRAGMENT_5).pattern("SSS").pattern("SSS").pattern("SSS").unlockedBy("has_disc_fragment_5", has((ItemLike)Items.DISC_FRAGMENT_5)).save(var0);
      SpecialRecipeBuilder.special(RecipeSerializer.ARMOR_DYE).save(var0, "armor_dye");
      SpecialRecipeBuilder.special(RecipeSerializer.BANNER_DUPLICATE).save(var0, "banner_duplicate");
      SpecialRecipeBuilder.special(RecipeSerializer.BOOK_CLONING).save(var0, "book_cloning");
      SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_ROCKET).save(var0, "firework_rocket");
      SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_STAR).save(var0, "firework_star");
      SpecialRecipeBuilder.special(RecipeSerializer.FIREWORK_STAR_FADE).save(var0, "firework_star_fade");
      SpecialRecipeBuilder.special(RecipeSerializer.MAP_CLONING).save(var0, "map_cloning");
      SpecialRecipeBuilder.special(RecipeSerializer.MAP_EXTENDING).save(var0, "map_extending");
      SpecialRecipeBuilder.special(RecipeSerializer.REPAIR_ITEM).save(var0, "repair_item");
      SpecialRecipeBuilder.special(RecipeSerializer.SHIELD_DECORATION).save(var0, "shield_decoration");
      SpecialRecipeBuilder.special(RecipeSerializer.SHULKER_BOX_COLORING).save(var0, "shulker_box_coloring");
      SpecialRecipeBuilder.special(RecipeSerializer.TIPPED_ARROW).save(var0, "tipped_arrow");
      SpecialRecipeBuilder.special(RecipeSerializer.SUSPICIOUS_STEW).save(var0, "suspicious_stew");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.POTATO), Items.BAKED_POTATO, 0.35F, 200).unlockedBy("has_potato", has((ItemLike)Items.POTATO)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CLAY_BALL), Items.BRICK, 0.3F, 200).unlockedBy("has_clay_ball", has((ItemLike)Items.CLAY_BALL)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15F, 200).unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1F, 200).unlockedBy("has_chorus_fruit", has((ItemLike)Items.CHORUS_FRUIT)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35F, 200).unlockedBy("has_beef", has((ItemLike)Items.BEEF)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, 200).unlockedBy("has_chicken", has((ItemLike)Items.CHICKEN)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35F, 200).unlockedBy("has_cod", has((ItemLike)Items.COD)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1F, 200).unlockedBy("has_kelp", has((ItemLike)Blocks.KELP)).save(var0, getSmeltingRecipeName(Items.DRIED_KELP));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35F, 200).unlockedBy("has_salmon", has((ItemLike)Items.SALMON)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, 200).unlockedBy("has_mutton", has((ItemLike)Items.MUTTON)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, 200).unlockedBy("has_porkchop", has((ItemLike)Items.PORKCHOP)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, 200).unlockedBy("has_rabbit", has((ItemLike)Items.RABBIT)).save(var0);
      oreSmelting(var0, COAL_SMELTABLES, Items.COAL, 0.1F, 200, "coal");
      oreSmelting(var0, IRON_SMELTABLES, Items.IRON_INGOT, 0.7F, 200, "iron_ingot");
      oreSmelting(var0, COPPER_SMELTABLES, Items.COPPER_INGOT, 0.7F, 200, "copper_ingot");
      oreSmelting(var0, GOLD_SMELTABLES, Items.GOLD_INGOT, 1.0F, 200, "gold_ingot");
      oreSmelting(var0, DIAMOND_SMELTABLES, Items.DIAMOND, 1.0F, 200, "diamond");
      oreSmelting(var0, LAPIS_SMELTABLES, Items.LAPIS_LAZULI, 0.2F, 200, "lapis_lazuli");
      oreSmelting(var0, REDSTONE_SMELTABLES, Items.REDSTONE, 0.7F, 200, "redstone");
      oreSmelting(var0, EMERALD_SMELTABLES, Items.EMERALD, 1.0F, 200, "emerald");
      nineBlockStorageRecipes(var0, Items.RAW_IRON, Items.RAW_IRON_BLOCK);
      nineBlockStorageRecipes(var0, Items.RAW_COPPER, Items.RAW_COPPER_BLOCK);
      nineBlockStorageRecipes(var0, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1F, 200).unlockedBy("has_sand", has(ItemTags.SAND)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SEA_PICKLE), Items.LIME_DYE, 0.1F, 200).unlockedBy("has_sea_pickle", has((ItemLike)Blocks.SEA_PICKLE)).save(var0, getSmeltingRecipeName(Items.LIME_DYE));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0F, 200).unlockedBy("has_cactus", has((ItemLike)Blocks.CACTUS)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 200).unlockedBy("has_golden_pickaxe", has((ItemLike)Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", has((ItemLike)Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", has((ItemLike)Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", has((ItemLike)Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", has((ItemLike)Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", has((ItemLike)Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", has((ItemLike)Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", has((ItemLike)Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", has((ItemLike)Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", has((ItemLike)Items.GOLDEN_HORSE_ARMOR)).save(var0, getSmeltingRecipeName(Items.GOLD_NUGGET));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 200).unlockedBy("has_iron_pickaxe", has((ItemLike)Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", has((ItemLike)Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", has((ItemLike)Items.IRON_AXE)).unlockedBy("has_iron_hoe", has((ItemLike)Items.IRON_HOE)).unlockedBy("has_iron_sword", has((ItemLike)Items.IRON_SWORD)).unlockedBy("has_iron_helmet", has((ItemLike)Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", has((ItemLike)Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", has((ItemLike)Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", has((ItemLike)Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", has((ItemLike)Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", has((ItemLike)Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", has((ItemLike)Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", has((ItemLike)Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", has((ItemLike)Items.CHAINMAIL_BOOTS)).save(var0, getSmeltingRecipeName(Items.IRON_NUGGET));
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35F, 200).unlockedBy("has_clay_block", has((ItemLike)Blocks.CLAY)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1F, 200).unlockedBy("has_netherrack", has((ItemLike)Blocks.NETHERRACK)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 200).unlockedBy("has_nether_quartz_ore", has((ItemLike)Blocks.NETHER_QUARTZ_ORE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15F, 200).unlockedBy("has_wet_sponge", has((ItemLike)Blocks.WET_SPONGE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1F, 200).unlockedBy("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1F, 200).unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200).unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200).unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200).unlockedBy("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_black_terracotta", has((ItemLike)Blocks.BLACK_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_blue_terracotta", has((ItemLike)Blocks.BLUE_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_brown_terracotta", has((ItemLike)Blocks.BROWN_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_cyan_terracotta", has((ItemLike)Blocks.CYAN_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_gray_terracotta", has((ItemLike)Blocks.GRAY_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_green_terracotta", has((ItemLike)Blocks.GREEN_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_light_blue_terracotta", has((ItemLike)Blocks.LIGHT_BLUE_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_light_gray_terracotta", has((ItemLike)Blocks.LIGHT_GRAY_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_lime_terracotta", has((ItemLike)Blocks.LIME_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_magenta_terracotta", has((ItemLike)Blocks.MAGENTA_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_orange_terracotta", has((ItemLike)Blocks.ORANGE_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_pink_terracotta", has((ItemLike)Blocks.PINK_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_purple_terracotta", has((ItemLike)Blocks.PURPLE_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_red_terracotta", has((ItemLike)Blocks.RED_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_white_terracotta", has((ItemLike)Blocks.WHITE_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200).unlockedBy("has_yellow_terracotta", has((ItemLike)Blocks.YELLOW_TERRACOTTA)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 200).unlockedBy("has_ancient_debris", has((ItemLike)Blocks.ANCIENT_DEBRIS)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.BASALT), Blocks.SMOOTH_BASALT, 0.1F, 200).unlockedBy("has_basalt", has((ItemLike)Blocks.BASALT)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COBBLED_DEEPSLATE), Blocks.DEEPSLATE, 0.1F, 200).unlockedBy("has_cobbled_deepslate", has((ItemLike)Blocks.COBBLED_DEEPSLATE)).save(var0);
      oreBlasting(var0, COAL_SMELTABLES, Items.COAL, 0.1F, 100, "coal");
      oreBlasting(var0, IRON_SMELTABLES, Items.IRON_INGOT, 0.7F, 100, "iron_ingot");
      oreBlasting(var0, COPPER_SMELTABLES, Items.COPPER_INGOT, 0.7F, 100, "copper_ingot");
      oreBlasting(var0, GOLD_SMELTABLES, Items.GOLD_INGOT, 1.0F, 100, "gold_ingot");
      oreBlasting(var0, DIAMOND_SMELTABLES, Items.DIAMOND, 1.0F, 100, "diamond");
      oreBlasting(var0, LAPIS_SMELTABLES, Items.LAPIS_LAZULI, 0.2F, 100, "lapis_lazuli");
      oreBlasting(var0, REDSTONE_SMELTABLES, Items.REDSTONE, 0.7F, 100, "redstone");
      oreBlasting(var0, EMERALD_SMELTABLES, Items.EMERALD, 1.0F, 100, "emerald");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 100).unlockedBy("has_nether_quartz_ore", has((ItemLike)Blocks.NETHER_QUARTZ_ORE)).save(var0, getBlastingRecipeName(Items.QUARTZ));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 100).unlockedBy("has_golden_pickaxe", has((ItemLike)Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", has((ItemLike)Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", has((ItemLike)Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", has((ItemLike)Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", has((ItemLike)Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", has((ItemLike)Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", has((ItemLike)Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", has((ItemLike)Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", has((ItemLike)Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", has((ItemLike)Items.GOLDEN_HORSE_ARMOR)).save(var0, getBlastingRecipeName(Items.GOLD_NUGGET));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 100).unlockedBy("has_iron_pickaxe", has((ItemLike)Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", has((ItemLike)Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", has((ItemLike)Items.IRON_AXE)).unlockedBy("has_iron_hoe", has((ItemLike)Items.IRON_HOE)).unlockedBy("has_iron_sword", has((ItemLike)Items.IRON_SWORD)).unlockedBy("has_iron_helmet", has((ItemLike)Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", has((ItemLike)Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", has((ItemLike)Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", has((ItemLike)Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", has((ItemLike)Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", has((ItemLike)Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", has((ItemLike)Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", has((ItemLike)Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", has((ItemLike)Items.CHAINMAIL_BOOTS)).save(var0, getBlastingRecipeName(Items.IRON_NUGGET));
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 100).unlockedBy("has_ancient_debris", has((ItemLike)Blocks.ANCIENT_DEBRIS)).save(var0, getBlastingRecipeName(Items.NETHERITE_SCRAP));
      cookRecipes(var0, "smoking", RecipeSerializer.SMOKING_RECIPE, 100);
      cookRecipes(var0, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600);
      stonecutterResultFromBase(var0, Blocks.STONE_SLAB, Blocks.STONE, 2);
      stonecutterResultFromBase(var0, Blocks.STONE_STAIRS, Blocks.STONE);
      stonecutterResultFromBase(var0, Blocks.STONE_BRICKS, Blocks.STONE);
      stonecutterResultFromBase(var0, Blocks.STONE_BRICK_SLAB, Blocks.STONE, 2);
      stonecutterResultFromBase(var0, Blocks.STONE_BRICK_STAIRS, Blocks.STONE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "chiseled_stone_bricks_stone_from_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_WALL).unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_brick_walls_from_stone_stonecutting");
      stonecutterResultFromBase(var0, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.CUT_SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0, "quartz_slab_from_stonecutting");
      stonecutterResultFromBase(var0, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(var0, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(var0, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(var0, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK);
      stonecutterResultFromBase(var0, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
      stonecutterResultFromBase(var0, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE, 2);
      stonecutterResultFromBase(var0, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
      stonecutterResultFromBase(var0, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICKS);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).unlockedBy("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0, "stone_brick_wall_from_stone_bricks_stonecutting");
      stonecutterResultFromBase(var0, Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.BRICK_SLAB, Blocks.BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.BRICK_STAIRS, Blocks.BRICKS);
      stonecutterResultFromBase(var0, Blocks.BRICK_WALL, Blocks.BRICKS);
      stonecutterResultFromBase(var0, Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.MUD_BRICK_STAIRS, Blocks.MUD_BRICKS);
      stonecutterResultFromBase(var0, Blocks.MUD_BRICK_WALL, Blocks.MUD_BRICKS);
      stonecutterResultFromBase(var0, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
      stonecutterResultFromBase(var0, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
      stonecutterResultFromBase(var0, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
      stonecutterResultFromBase(var0, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
      stonecutterResultFromBase(var0, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
      stonecutterResultFromBase(var0, Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK, 2);
      stonecutterResultFromBase(var0, Blocks.PURPUR_STAIRS, Blocks.PURPUR_BLOCK);
      stonecutterResultFromBase(var0, Blocks.PURPUR_PILLAR, Blocks.PURPUR_BLOCK);
      stonecutterResultFromBase(var0, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE, 2);
      stonecutterResultFromBase(var0, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
      stonecutterResultFromBase(var0, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).unlockedBy("has_prismarine_brick", has((ItemLike)Blocks.PRISMARINE_BRICKS)).save(var0, "prismarine_brick_slab_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).unlockedBy("has_prismarine_brick", has((ItemLike)Blocks.PRISMARINE_BRICKS)).save(var0, "prismarine_brick_stairs_from_prismarine_stonecutting");
      stonecutterResultFromBase(var0, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE, 2);
      stonecutterResultFromBase(var0, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
      stonecutterResultFromBase(var0, Blocks.ANDESITE_SLAB, Blocks.ANDESITE, 2);
      stonecutterResultFromBase(var0, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
      stonecutterResultFromBase(var0, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BASALT, Blocks.BASALT);
      stonecutterResultFromBase(var0, Blocks.GRANITE_SLAB, Blocks.GRANITE, 2);
      stonecutterResultFromBase(var0, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
      stonecutterResultFromBase(var0, Blocks.GRANITE_WALL, Blocks.GRANITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_GRANITE_SLAB, Blocks.GRANITE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_GRANITE_STAIRS, Blocks.GRANITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
      stonecutterResultFromBase(var0, Blocks.DIORITE_SLAB, Blocks.DIORITE, 2);
      stonecutterResultFromBase(var0, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
      stonecutterResultFromBase(var0, Blocks.DIORITE_WALL, Blocks.DIORITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DIORITE_SLAB, Blocks.DIORITE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DIORITE_STAIRS, Blocks.DIORITE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).unlockedBy("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).unlockedBy("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).unlockedBy("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
      stonecutterResultFromBase(var0, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE, 2);
      stonecutterResultFromBase(var0, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
      stonecutterResultFromBase(var0, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ, 2);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).unlockedBy("has_end_stone_brick", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).unlockedBy("has_end_stone_brick", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).unlockedBy("has_end_stone_brick", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
      stonecutterResultFromBase(var0, Blocks.END_STONE_BRICKS, Blocks.END_STONE);
      stonecutterResultFromBase(var0, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE, 2);
      stonecutterResultFromBase(var0, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE);
      stonecutterResultFromBase(var0, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE);
      stonecutterResultFromBase(var0, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, 2);
      stonecutterResultFromBase(var0, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.BLACKSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER, 2);
      stonecutterResultFromBase(var0, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER);
      stonecutterResultFromBase(var0, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK, 4);
      stonecutterResultFromBase(var0, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK, 4);
      stonecutterResultFromBase(var0, Blocks.CUT_COPPER_SLAB, Blocks.COPPER_BLOCK, 8);
      stonecutterResultFromBase(var0, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_COPPER, 8);
      stonecutterResultFromBase(var0, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_COPPER, 8);
      stonecutterResultFromBase(var0, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_COPPER, 8);
      stonecutterResultFromBase(var0, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_COPPER_BLOCK, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_COPPER_BLOCK, 8);
      stonecutterResultFromBase(var0, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_COPPER, 8);
      stonecutterResultFromBase(var0, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_COPPER, 8);
      stonecutterResultFromBase(var0, Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_COPPER, 4);
      stonecutterResultFromBase(var0, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_COPPER, 8);
      stonecutterResultFromBase(var0, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_WALL, Blocks.COBBLED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_DEEPSLATE);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICKS);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, 2);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES);
      stonecutterResultFromBase(var0, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_TILES);
      netheriteSmithing(var0, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
      netheriteSmithing(var0, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
      netheriteSmithing(var0, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
      netheriteSmithing(var0, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
      netheriteSmithing(var0, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
      netheriteSmithing(var0, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
      netheriteSmithing(var0, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
      netheriteSmithing(var0, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
      netheriteSmithing(var0, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
   }

   private static void oneToOneConversionRecipe(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, @Nullable String var3) {
      oneToOneConversionRecipe(var0, var1, var2, var3, 1);
   }

   private static void oneToOneConversionRecipe(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, @Nullable String var3, int var4) {
      ShapelessRecipeBuilder.shapeless(var1, var4).requires(var2).group(var3).unlockedBy(getHasName(var2), has(var2)).save(var0, getConversionRecipeName(var1, var2));
   }

   private static void oreSmelting(Consumer<FinishedRecipe> var0, List<ItemLike> var1, ItemLike var2, float var3, int var4, String var5) {
      oreCooking(var0, RecipeSerializer.SMELTING_RECIPE, var1, var2, var3, var4, var5, "_from_smelting");
   }

   private static void oreBlasting(Consumer<FinishedRecipe> var0, List<ItemLike> var1, ItemLike var2, float var3, int var4, String var5) {
      oreCooking(var0, RecipeSerializer.BLASTING_RECIPE, var1, var2, var3, var4, var5, "_from_blasting");
   }

   private static void oreCooking(Consumer<FinishedRecipe> var0, SimpleCookingSerializer<?> var1, List<ItemLike> var2, ItemLike var3, float var4, int var5, String var6, String var7) {
      Iterator var8 = var2.iterator();

      while(var8.hasNext()) {
         ItemLike var9 = (ItemLike)var8.next();
         SimpleCookingRecipeBuilder.cooking(Ingredient.of(var9), var3, var4, var5, var1).group(var6).unlockedBy(getHasName(var9), has(var9)).save(var0, getItemName(var3) + var7 + "_" + getItemName(var9));
      }

   }

   private static void netheriteSmithing(Consumer<FinishedRecipe> var0, Item var1, Item var2) {
      UpgradeRecipeBuilder.smithing(Ingredient.of(var1), Ingredient.of(Items.NETHERITE_INGOT), var2).unlocks("has_netherite_ingot", has((ItemLike)Items.NETHERITE_INGOT)).save(var0, getItemName(var2) + "_smithing");
   }

   private static void planksFromLog(Consumer<FinishedRecipe> var0, ItemLike var1, TagKey<Item> var2) {
      ShapelessRecipeBuilder.shapeless(var1, 4).requires(var2).group("planks").unlockedBy("has_log", has(var2)).save(var0);
   }

   private static void planksFromLogs(Consumer<FinishedRecipe> var0, ItemLike var1, TagKey<Item> var2) {
      ShapelessRecipeBuilder.shapeless(var1, 4).requires(var2).group("planks").unlockedBy("has_logs", has(var2)).save(var0);
   }

   private static void woodFromLogs(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 3).define('#', var2).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(var2)).save(var0);
   }

   private static void woodenBoat(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1).define('#', var2).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(var0);
   }

   private static void chestBoat(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1).requires((ItemLike)Blocks.CHEST).requires(var2).group("chest_boat").unlockedBy("has_boat", has(ItemTags.BOATS)).save(var0);
   }

   private static RecipeBuilder buttonBuilder(ItemLike var0, Ingredient var1) {
      return ShapelessRecipeBuilder.shapeless(var0).requires(var1);
   }

   private static RecipeBuilder doorBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 3).define('#', var1).pattern("##").pattern("##").pattern("##");
   }

   private static RecipeBuilder fenceBuilder(ItemLike var0, Ingredient var1) {
      int var2 = var0 == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
      Item var3 = var0 == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
      return ShapedRecipeBuilder.shaped(var0, var2).define('W', var1).define('#', (ItemLike)var3).pattern("W#W").pattern("W#W");
   }

   private static RecipeBuilder fenceGateBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0).define('#', (ItemLike)Items.STICK).define('W', var1).pattern("#W#").pattern("#W#");
   }

   private static void pressurePlate(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      pressurePlateBuilder(var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static RecipeBuilder pressurePlateBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0).define('#', var1).pattern("##");
   }

   private static void slab(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      slabBuilder(var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static RecipeBuilder slabBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 6).define('#', var1).pattern("###");
   }

   private static RecipeBuilder stairBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 4).define('#', var1).pattern("#  ").pattern("## ").pattern("###");
   }

   private static RecipeBuilder trapdoorBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 2).define('#', var1).pattern("###").pattern("###");
   }

   private static RecipeBuilder signBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 3).group("sign").define('#', var1).define('X', (ItemLike)Items.STICK).pattern("###").pattern("###").pattern(" X ");
   }

   private static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1).requires(var2).requires((ItemLike)Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", has((ItemLike)Blocks.WHITE_WOOL)).save(var0);
   }

   private static void carpet(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 3).define('#', var2).pattern("##").group("carpet").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.WHITE_CARPET).define('$', var2).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", has((ItemLike)Blocks.WHITE_CARPET)).unlockedBy(getHasName(var2), has(var2)).save(var0, getConversionRecipeName(var1, Blocks.WHITE_CARPET));
   }

   private static void bedFromPlanksAndWool(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1).define('#', var2).define('X', ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1).requires((ItemLike)Items.WHITE_BED).requires(var2).group("dyed_bed").unlockedBy("has_bed", has((ItemLike)Items.WHITE_BED)).save(var0, getConversionRecipeName(var1, Items.WHITE_BED));
   }

   private static void banner(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1).define('#', var2).define('|', (ItemLike)Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.GLASS).define('X', var2).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
   }

   private static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 16).define('#', var2).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(var2)).save(var0);
   }

   private static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.GLASS_PANE).define('$', var2).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has((ItemLike)Blocks.GLASS_PANE)).unlockedBy(getHasName(var2), has(var2)).save(var0, getConversionRecipeName(var1, Blocks.GLASS_PANE));
   }

   private static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.TERRACOTTA).define('X', var2).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has((ItemLike)Blocks.TERRACOTTA)).save(var0);
   }

   private static void concretePowder(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1, 8).requires(var2).requires((ItemLike)Blocks.SAND, 4).requires((ItemLike)Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has((ItemLike)Blocks.SAND)).unlockedBy("has_gravel", has((ItemLike)Blocks.GRAVEL)).save(var0);
   }

   public static void candle(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1).requires((ItemLike)Blocks.CANDLE).requires(var2).group("dyed_candle").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   public static void wall(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      wallBuilder(var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   public static RecipeBuilder wallBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 6).define('#', var1).pattern("###").pattern("###");
   }

   public static void polished(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      polishedBuilder(var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   public static RecipeBuilder polishedBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 4).define('S', var1).pattern("SS").pattern("SS");
   }

   public static void cut(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      cutBuilder(var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   public static ShapedRecipeBuilder cutBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0, 4).define('#', var1).pattern("##").pattern("##");
   }

   public static void chiseled(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      chiseledBuilder(var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   public static ShapedRecipeBuilder chiseledBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(var0).define('#', var1).pattern("#").pattern("#");
   }

   private static void stonecutterResultFromBase(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      stonecutterResultFromBase(var0, var1, var2, 1);
   }

   private static void stonecutterResultFromBase(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, int var3) {
      SingleItemRecipeBuilder var10000 = SingleItemRecipeBuilder.stonecutting(Ingredient.of(var2), var1, var3).unlockedBy(getHasName(var2), has(var2));
      String var10002 = getConversionRecipeName(var1, var2);
      var10000.save(var0, var10002 + "_stonecutting");
   }

   private static void smeltingResultFromBase(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(var2), var1, 0.1F, 200).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static void nineBlockStorageRecipes(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      nineBlockStorageRecipes(var0, var1, var2, getSimpleRecipeName(var2), (String)null, getSimpleRecipeName(var1), (String)null);
   }

   private static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, String var3, String var4) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, getSimpleRecipeName(var1), (String)null);
   }

   private static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, String var3, String var4) {
      nineBlockStorageRecipes(var0, var1, var2, getSimpleRecipeName(var2), (String)null, var3, var4);
   }

   private static void nineBlockStorageRecipes(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, String var3, @Nullable String var4, String var5, @Nullable String var6) {
      ShapelessRecipeBuilder.shapeless(var1, 9).requires(var2).group(var6).unlockedBy(getHasName(var2), has(var2)).save(var0, new ResourceLocation(var5));
      ShapedRecipeBuilder.shaped(var2).define('#', var1).pattern("###").pattern("###").pattern("###").group(var4).unlockedBy(getHasName(var1), has(var1)).save(var0, new ResourceLocation(var3));
   }

   private static void cookRecipes(Consumer<FinishedRecipe> var0, String var1, SimpleCookingSerializer<?> var2, int var3) {
      simpleCookingRecipe(var0, var1, var2, var3, Items.BEEF, Items.COOKED_BEEF, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.COD, Items.COOKED_COD, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.KELP, Items.DRIED_KELP, 0.1F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.SALMON, Items.COOKED_SALMON, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.POTATO, Items.BAKED_POTATO, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
   }

   private static void simpleCookingRecipe(Consumer<FinishedRecipe> var0, String var1, SimpleCookingSerializer<?> var2, int var3, ItemLike var4, ItemLike var5, float var6) {
      SimpleCookingRecipeBuilder var10000 = SimpleCookingRecipeBuilder.cooking(Ingredient.of(var4), var5, var6, var3, var2).unlockedBy(getHasName(var4), has(var4));
      String var10002 = getItemName(var5);
      var10000.save(var0, var10002 + "_from_" + var1);
   }

   private static void waxRecipes(Consumer<FinishedRecipe> var0) {
      ((BiMap)HoneycombItem.WAXABLES.get()).forEach((var1, var2) -> {
         ShapelessRecipeBuilder.shapeless(var2).requires((ItemLike)var1).requires((ItemLike)Items.HONEYCOMB).group(getItemName(var2)).unlockedBy(getHasName(var1), has((ItemLike)var1)).save(var0, getConversionRecipeName(var2, Items.HONEYCOMB));
      });
   }

   private static void generateRecipes(Consumer<FinishedRecipe> var0, BlockFamily var1) {
      var1.getVariants().forEach((var2, var3) -> {
         BiFunction var4 = (BiFunction)shapeBuilders.get(var2);
         Block var5 = getBaseBlock(var1, var2);
         if (var4 != null) {
            RecipeBuilder var6 = (RecipeBuilder)var4.apply(var3, var5);
            var1.getRecipeGroupPrefix().ifPresent((var2x) -> {
               var6.group(var2x + (var2 == BlockFamily.Variant.CUT ? "" : "_" + var2.getName()));
            });
            var6.unlockedBy((String)var1.getRecipeUnlockedBy().orElseGet(() -> {
               return getHasName(var5);
            }), has((ItemLike)var5));
            var6.save(var0);
         }

         if (var2 == BlockFamily.Variant.CRACKED) {
            smeltingResultFromBase(var0, var3, var5);
         }

      });
   }

   private static Block getBaseBlock(BlockFamily var0, BlockFamily.Variant var1) {
      if (var1 == BlockFamily.Variant.CHISELED) {
         if (!var0.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
            throw new IllegalStateException("Slab is not defined for the family.");
         } else {
            return var0.get(BlockFamily.Variant.SLAB);
         }
      } else {
         return var0.getBaseBlock();
      }
   }

   private static EnterBlockTrigger.TriggerInstance insideOf(Block var0) {
      return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, StatePropertiesPredicate.ANY);
   }

   private static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints var0, ItemLike var1) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var1).withCount(var0).build());
   }

   private static InventoryChangeTrigger.TriggerInstance has(ItemLike var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0).build());
   }

   private static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0).build());
   }

   private static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... var0) {
      return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, var0);
   }

   private static String getHasName(ItemLike var0) {
      return "has_" + getItemName(var0);
   }

   private static String getItemName(ItemLike var0) {
      return Registry.ITEM.getKey(var0.asItem()).getPath();
   }

   private static String getSimpleRecipeName(ItemLike var0) {
      return getItemName(var0);
   }

   private static String getConversionRecipeName(ItemLike var0, ItemLike var1) {
      String var10000 = getItemName(var0);
      return var10000 + "_from_" + getItemName(var1);
   }

   private static String getSmeltingRecipeName(ItemLike var0) {
      return getItemName(var0) + "_from_smelting";
   }

   private static String getBlastingRecipeName(ItemLike var0) {
      return getItemName(var0) + "_from_blasting";
   }

   public String getName() {
      return "Recipes";
   }

   static {
      COAL_SMELTABLES = ImmutableList.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE);
      IRON_SMELTABLES = ImmutableList.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
      COPPER_SMELTABLES = ImmutableList.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);
      GOLD_SMELTABLES = ImmutableList.of(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE, Items.RAW_GOLD);
      DIAMOND_SMELTABLES = ImmutableList.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE);
      LAPIS_SMELTABLES = ImmutableList.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE);
      REDSTONE_SMELTABLES = ImmutableList.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE);
      EMERALD_SMELTABLES = ImmutableList.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE);
      shapeBuilders = ImmutableMap.builder().put(BlockFamily.Variant.BUTTON, (var0, var1) -> {
         return buttonBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.CHISELED, (var0, var1) -> {
         return chiseledBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.CUT, (var0, var1) -> {
         return cutBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.DOOR, (var0, var1) -> {
         return doorBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.FENCE, (var0, var1) -> {
         return fenceBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.FENCE_GATE, (var0, var1) -> {
         return fenceGateBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.SIGN, (var0, var1) -> {
         return signBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.SLAB, (var0, var1) -> {
         return slabBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.STAIRS, (var0, var1) -> {
         return stairBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.PRESSURE_PLATE, (var0, var1) -> {
         return pressurePlateBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.POLISHED, (var0, var1) -> {
         return polishedBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.TRAPDOOR, (var0, var1) -> {
         return trapdoorBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.WALL, (var0, var1) -> {
         return wallBuilder(var0, Ingredient.of(var1));
      }).build();
   }
}
