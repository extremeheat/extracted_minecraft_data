package net.minecraft.data.recipes;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
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
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public RecipeProvider(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) throws IOException {
      Path var2 = this.generator.getOutputFolder();
      HashSet var3 = Sets.newHashSet();
      buildShapelessRecipes((var3x) -> {
         if (!var3.add(var3x.getId())) {
            throw new IllegalStateException("Duplicate recipe " + var3x.getId());
         } else {
            saveRecipe(var1, var3x.serializeRecipe(), var2.resolve("data/" + var3x.getId().getNamespace() + "/recipes/" + var3x.getId().getPath() + ".json"));
            JsonObject var4 = var3x.serializeAdvancement();
            if (var4 != null) {
               saveAdvancement(var1, var4, var2.resolve("data/" + var3x.getId().getNamespace() + "/advancements/" + var3x.getAdvancementId().getPath() + ".json"));
            }

         }
      });
      saveAdvancement(var1, Advancement.Builder.advancement().addCriterion("impossible", (CriterionTriggerInstance)(new ImpossibleTrigger.TriggerInstance())).serializeToJson(), var2.resolve("data/minecraft/advancements/recipes/root.json"));
   }

   private static void saveRecipe(HashCache var0, JsonObject var1, Path var2) {
      try {
         String var3 = GSON.toJson(var1);
         String var4 = SHA1.hashUnencodedChars(var3).toString();
         if (!Objects.equals(var0.getHash(var2), var4) || !Files.exists(var2, new LinkOption[0])) {
            Files.createDirectories(var2.getParent());
            BufferedWriter var5 = Files.newBufferedWriter(var2);
            Throwable var6 = null;

            try {
               var5.write(var3);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         }

         var0.putNew(var2, var4);
      } catch (IOException var18) {
         LOGGER.error("Couldn't save recipe {}", var2, var18);
      }

   }

   private static void saveAdvancement(HashCache var0, JsonObject var1, Path var2) {
      try {
         String var3 = GSON.toJson(var1);
         String var4 = SHA1.hashUnencodedChars(var3).toString();
         if (!Objects.equals(var0.getHash(var2), var4) || !Files.exists(var2, new LinkOption[0])) {
            Files.createDirectories(var2.getParent());
            BufferedWriter var5 = Files.newBufferedWriter(var2);
            Throwable var6 = null;

            try {
               var5.write(var3);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         }

         var0.putNew(var2, var4);
      } catch (IOException var18) {
         LOGGER.error("Couldn't save recipe advancement {}", var2, var18);
      }

   }

   private static void buildShapelessRecipes(Consumer<FinishedRecipe> var0) {
      planksFromLog(var0, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
      planksFromLogs(var0, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
      planksFromLogs(var0, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
      planksFromLog(var0, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
      planksFromLogs(var0, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
      planksFromLogs(var0, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
      planksFromLogs(var0, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
      planksFromLogs(var0, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
      woodFromLogs(var0, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
      woodFromLogs(var0, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
      woodFromLogs(var0, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
      woodFromLogs(var0, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
      woodFromLogs(var0, Blocks.OAK_WOOD, Blocks.OAK_LOG);
      woodFromLogs(var0, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
      woodFromLogs(var0, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
      woodFromLogs(var0, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
      woodFromLogs(var0, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
      woodFromLogs(var0, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
      woodFromLogs(var0, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
      woodenBoat(var0, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
      woodenBoat(var0, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
      woodenBoat(var0, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
      woodenBoat(var0, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
      woodenBoat(var0, Items.OAK_BOAT, Blocks.OAK_PLANKS);
      woodenBoat(var0, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
      woodenButton(var0, Blocks.ACACIA_BUTTON, Blocks.ACACIA_PLANKS);
      woodenDoor(var0, Blocks.ACACIA_DOOR, Blocks.ACACIA_PLANKS);
      woodenFence(var0, Blocks.ACACIA_FENCE, Blocks.ACACIA_PLANKS);
      woodenFenceGate(var0, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_PLANKS);
      woodenPressurePlate(var0, Blocks.ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PLANKS);
      woodenSlab(var0, Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS);
      woodenStairs(var0, Blocks.ACACIA_STAIRS, Blocks.ACACIA_PLANKS);
      woodenTrapdoor(var0, Blocks.ACACIA_TRAPDOOR, Blocks.ACACIA_PLANKS);
      woodenSign(var0, Blocks.ACACIA_SIGN, Blocks.ACACIA_PLANKS);
      woodenButton(var0, Blocks.BIRCH_BUTTON, Blocks.BIRCH_PLANKS);
      woodenDoor(var0, Blocks.BIRCH_DOOR, Blocks.BIRCH_PLANKS);
      woodenFence(var0, Blocks.BIRCH_FENCE, Blocks.BIRCH_PLANKS);
      woodenFenceGate(var0, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_PLANKS);
      woodenPressurePlate(var0, Blocks.BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PLANKS);
      woodenSlab(var0, Blocks.BIRCH_SLAB, Blocks.BIRCH_PLANKS);
      woodenStairs(var0, Blocks.BIRCH_STAIRS, Blocks.BIRCH_PLANKS);
      woodenTrapdoor(var0, Blocks.BIRCH_TRAPDOOR, Blocks.BIRCH_PLANKS);
      woodenSign(var0, Blocks.BIRCH_SIGN, Blocks.BIRCH_PLANKS);
      woodenButton(var0, Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_PLANKS);
      woodenDoor(var0, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_PLANKS);
      woodenFence(var0, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_PLANKS);
      woodenFenceGate(var0, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_PLANKS);
      woodenPressurePlate(var0, Blocks.CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PLANKS);
      woodenSlab(var0, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
      woodenStairs(var0, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_PLANKS);
      woodenTrapdoor(var0, Blocks.CRIMSON_TRAPDOOR, Blocks.CRIMSON_PLANKS);
      woodenSign(var0, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_PLANKS);
      woodenButton(var0, Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PLANKS);
      woodenDoor(var0, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_PLANKS);
      woodenFence(var0, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_PLANKS);
      woodenFenceGate(var0, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_PLANKS);
      woodenPressurePlate(var0, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PLANKS);
      woodenSlab(var0, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
      woodenStairs(var0, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS);
      woodenTrapdoor(var0, Blocks.DARK_OAK_TRAPDOOR, Blocks.DARK_OAK_PLANKS);
      woodenSign(var0, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_PLANKS);
      woodenButton(var0, Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PLANKS);
      woodenDoor(var0, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_PLANKS);
      woodenFence(var0, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_PLANKS);
      woodenFenceGate(var0, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_PLANKS);
      woodenPressurePlate(var0, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PLANKS);
      woodenSlab(var0, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_PLANKS);
      woodenStairs(var0, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_PLANKS);
      woodenTrapdoor(var0, Blocks.JUNGLE_TRAPDOOR, Blocks.JUNGLE_PLANKS);
      woodenSign(var0, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_PLANKS);
      woodenButton(var0, Blocks.OAK_BUTTON, Blocks.OAK_PLANKS);
      woodenDoor(var0, Blocks.OAK_DOOR, Blocks.OAK_PLANKS);
      woodenFence(var0, Blocks.OAK_FENCE, Blocks.OAK_PLANKS);
      woodenFenceGate(var0, Blocks.OAK_FENCE_GATE, Blocks.OAK_PLANKS);
      woodenPressurePlate(var0, Blocks.OAK_PRESSURE_PLATE, Blocks.OAK_PLANKS);
      woodenSlab(var0, Blocks.OAK_SLAB, Blocks.OAK_PLANKS);
      woodenStairs(var0, Blocks.OAK_STAIRS, Blocks.OAK_PLANKS);
      woodenTrapdoor(var0, Blocks.OAK_TRAPDOOR, Blocks.OAK_PLANKS);
      woodenSign(var0, Blocks.OAK_SIGN, Blocks.OAK_PLANKS);
      woodenButton(var0, Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PLANKS);
      woodenDoor(var0, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS);
      woodenFence(var0, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PLANKS);
      woodenFenceGate(var0, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_PLANKS);
      woodenPressurePlate(var0, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PLANKS);
      woodenSlab(var0, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
      woodenStairs(var0, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_PLANKS);
      woodenTrapdoor(var0, Blocks.SPRUCE_TRAPDOOR, Blocks.SPRUCE_PLANKS);
      woodenSign(var0, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_PLANKS);
      woodenButton(var0, Blocks.WARPED_BUTTON, Blocks.WARPED_PLANKS);
      woodenDoor(var0, Blocks.WARPED_DOOR, Blocks.WARPED_PLANKS);
      woodenFence(var0, Blocks.WARPED_FENCE, Blocks.WARPED_PLANKS);
      woodenFenceGate(var0, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_PLANKS);
      woodenPressurePlate(var0, Blocks.WARPED_PRESSURE_PLATE, Blocks.WARPED_PLANKS);
      woodenSlab(var0, Blocks.WARPED_SLAB, Blocks.WARPED_PLANKS);
      woodenStairs(var0, Blocks.WARPED_STAIRS, Blocks.WARPED_PLANKS);
      woodenTrapdoor(var0, Blocks.WARPED_TRAPDOOR, Blocks.WARPED_PLANKS);
      woodenSign(var0, Blocks.WARPED_SIGN, Blocks.WARPED_PLANKS);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.BLACK_WOOL, Items.BLACK_DYE);
      carpetFromWool(var0, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.BLACK_CARPET, Items.BLACK_DYE);
      bedFromPlanksAndWool(var0, Items.BLACK_BED, Blocks.BLACK_WOOL);
      bedFromWhiteBedAndDye(var0, Items.BLACK_BED, Items.BLACK_DYE);
      banner(var0, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.BLUE_WOOL, Items.BLUE_DYE);
      carpetFromWool(var0, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.BLUE_CARPET, Items.BLUE_DYE);
      bedFromPlanksAndWool(var0, Items.BLUE_BED, Blocks.BLUE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.BLUE_BED, Items.BLUE_DYE);
      banner(var0, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.BROWN_WOOL, Items.BROWN_DYE);
      carpetFromWool(var0, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.BROWN_CARPET, Items.BROWN_DYE);
      bedFromPlanksAndWool(var0, Items.BROWN_BED, Blocks.BROWN_WOOL);
      bedFromWhiteBedAndDye(var0, Items.BROWN_BED, Items.BROWN_DYE);
      banner(var0, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.CYAN_WOOL, Items.CYAN_DYE);
      carpetFromWool(var0, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.CYAN_CARPET, Items.CYAN_DYE);
      bedFromPlanksAndWool(var0, Items.CYAN_BED, Blocks.CYAN_WOOL);
      bedFromWhiteBedAndDye(var0, Items.CYAN_BED, Items.CYAN_DYE);
      banner(var0, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.GRAY_WOOL, Items.GRAY_DYE);
      carpetFromWool(var0, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.GRAY_CARPET, Items.GRAY_DYE);
      bedFromPlanksAndWool(var0, Items.GRAY_BED, Blocks.GRAY_WOOL);
      bedFromWhiteBedAndDye(var0, Items.GRAY_BED, Items.GRAY_DYE);
      banner(var0, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.GREEN_WOOL, Items.GREEN_DYE);
      carpetFromWool(var0, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.GREEN_CARPET, Items.GREEN_DYE);
      bedFromPlanksAndWool(var0, Items.GREEN_BED, Blocks.GREEN_WOOL);
      bedFromWhiteBedAndDye(var0, Items.GREEN_BED, Items.GREEN_DYE);
      banner(var0, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
      carpetFromWool(var0, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
      bedFromPlanksAndWool(var0, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
      banner(var0, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
      carpetFromWool(var0, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
      bedFromPlanksAndWool(var0, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
      bedFromWhiteBedAndDye(var0, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
      banner(var0, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.LIME_WOOL, Items.LIME_DYE);
      carpetFromWool(var0, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.LIME_CARPET, Items.LIME_DYE);
      bedFromPlanksAndWool(var0, Items.LIME_BED, Blocks.LIME_WOOL);
      bedFromWhiteBedAndDye(var0, Items.LIME_BED, Items.LIME_DYE);
      banner(var0, Items.LIME_BANNER, Blocks.LIME_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
      carpetFromWool(var0, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
      bedFromPlanksAndWool(var0, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
      bedFromWhiteBedAndDye(var0, Items.MAGENTA_BED, Items.MAGENTA_DYE);
      banner(var0, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
      carpetFromWool(var0, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
      bedFromPlanksAndWool(var0, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.ORANGE_BED, Items.ORANGE_DYE);
      banner(var0, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.PINK_WOOL, Items.PINK_DYE);
      carpetFromWool(var0, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.PINK_CARPET, Items.PINK_DYE);
      bedFromPlanksAndWool(var0, Items.PINK_BED, Blocks.PINK_WOOL);
      bedFromWhiteBedAndDye(var0, Items.PINK_BED, Items.PINK_DYE);
      banner(var0, Items.PINK_BANNER, Blocks.PINK_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
      carpetFromWool(var0, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
      bedFromPlanksAndWool(var0, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
      bedFromWhiteBedAndDye(var0, Items.PURPLE_BED, Items.PURPLE_DYE);
      banner(var0, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.RED_WOOL, Items.RED_DYE);
      carpetFromWool(var0, Blocks.RED_CARPET, Blocks.RED_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.RED_CARPET, Items.RED_DYE);
      bedFromPlanksAndWool(var0, Items.RED_BED, Blocks.RED_WOOL);
      bedFromWhiteBedAndDye(var0, Items.RED_BED, Items.RED_DYE);
      banner(var0, Items.RED_BANNER, Blocks.RED_WOOL);
      carpetFromWool(var0, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
      bedFromPlanksAndWool(var0, Items.WHITE_BED, Blocks.WHITE_WOOL);
      banner(var0, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
      coloredWoolFromWhiteWoolAndDye(var0, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
      carpetFromWool(var0, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
      coloredCarpetFromWhiteCarpetAndDye(var0, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
      bedFromPlanksAndWool(var0, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
      bedFromWhiteBedAndDye(var0, Items.YELLOW_BED, Items.YELLOW_DYE);
      banner(var0, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
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
      ShapedRecipeBuilder.shaped(Blocks.ACTIVATOR_RAIL, 6).define('#', (ItemLike)Blocks.REDSTONE_TORCH).define('S', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XSX").pattern("X#X").pattern("XSX").unlockedBy("has_rail", has((ItemLike)Blocks.RAIL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.ANDESITE, 2).requires((ItemLike)Blocks.DIORITE).requires((ItemLike)Blocks.COBBLESTONE).unlockedBy("has_stone", has((ItemLike)Blocks.DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ANVIL).define('I', (ItemLike)Blocks.IRON_BLOCK).define('i', (ItemLike)Items.IRON_INGOT).pattern("III").pattern(" i ").pattern("iii").unlockedBy("has_iron_block", has((ItemLike)Blocks.IRON_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.ARMOR_STAND).define('/', (ItemLike)Items.STICK).define('_', (ItemLike)Blocks.SMOOTH_STONE_SLAB).pattern("///").pattern(" / ").pattern("/_/").unlockedBy("has_stone_slab", has((ItemLike)Blocks.SMOOTH_STONE_SLAB)).save(var0);
      ShapedRecipeBuilder.shaped(Items.ARROW, 4).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.FLINT).define('Y', (ItemLike)Items.FEATHER).pattern("X").pattern("#").pattern("Y").unlockedBy("has_feather", has((ItemLike)Items.FEATHER)).unlockedBy("has_flint", has((ItemLike)Items.FLINT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BARREL, 1).define('P', (Tag)ItemTags.PLANKS).define('S', (Tag)ItemTags.WOODEN_SLABS).pattern("PSP").pattern("P P").pattern("PSP").unlockedBy("has_planks", has((Tag)ItemTags.PLANKS)).unlockedBy("has_wood_slab", has((Tag)ItemTags.WOODEN_SLABS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BEACON).define('S', (ItemLike)Items.NETHER_STAR).define('G', (ItemLike)Blocks.GLASS).define('O', (ItemLike)Blocks.OBSIDIAN).pattern("GGG").pattern("GSG").pattern("OOO").unlockedBy("has_nether_star", has((ItemLike)Items.NETHER_STAR)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BEEHIVE).define('P', (Tag)ItemTags.PLANKS).define('H', (ItemLike)Items.HONEYCOMB).pattern("PPP").pattern("HHH").pattern("PPP").unlockedBy("has_honeycomb", has((ItemLike)Items.HONEYCOMB)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BEETROOT_SOUP).requires((ItemLike)Items.BOWL).requires((ItemLike)Items.BEETROOT, 6).unlockedBy("has_beetroot", has((ItemLike)Items.BEETROOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BLACK_DYE).requires((ItemLike)Items.INK_SAC).group("black_dye").unlockedBy("has_ink_sac", has((ItemLike)Items.INK_SAC)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BLACK_DYE).requires((ItemLike)Blocks.WITHER_ROSE).group("black_dye").unlockedBy("has_black_flower", has((ItemLike)Blocks.WITHER_ROSE)).save(var0, "black_dye_from_wither_rose");
      ShapelessRecipeBuilder.shapeless(Items.BLAZE_POWDER, 2).requires((ItemLike)Items.BLAZE_ROD).unlockedBy("has_blaze_rod", has((ItemLike)Items.BLAZE_ROD)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BLUE_DYE).requires((ItemLike)Items.LAPIS_LAZULI).group("blue_dye").unlockedBy("has_lapis_lazuli", has((ItemLike)Items.LAPIS_LAZULI)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BLUE_DYE).requires((ItemLike)Blocks.CORNFLOWER).group("blue_dye").unlockedBy("has_blue_flower", has((ItemLike)Blocks.CORNFLOWER)).save(var0, "blue_dye_from_cornflower");
      ShapedRecipeBuilder.shaped(Blocks.BLUE_ICE).define('#', (ItemLike)Blocks.PACKED_ICE).pattern("###").pattern("###").pattern("###").unlockedBy("has_packed_ice", has((ItemLike)Blocks.PACKED_ICE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BONE_BLOCK).define('X', (ItemLike)Items.BONE_MEAL).pattern("XXX").pattern("XXX").pattern("XXX").unlockedBy("has_bonemeal", has((ItemLike)Items.BONE_MEAL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL, 3).requires((ItemLike)Items.BONE).group("bonemeal").unlockedBy("has_bone", has((ItemLike)Items.BONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BONE_MEAL, 9).requires((ItemLike)Blocks.BONE_BLOCK).group("bonemeal").unlockedBy("has_bone_block", has((ItemLike)Blocks.BONE_BLOCK)).save(var0, "bone_meal_from_bone_block");
      ShapelessRecipeBuilder.shapeless(Items.BOOK).requires((ItemLike)Items.PAPER, 3).requires((ItemLike)Items.LEATHER).unlockedBy("has_paper", has((ItemLike)Items.PAPER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BOOKSHELF).define('#', (Tag)ItemTags.PLANKS).define('X', (ItemLike)Items.BOOK).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", has((ItemLike)Items.BOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BOW).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.STRING).pattern(" #X").pattern("# X").pattern(" #X").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BOWL, 4).define('#', (Tag)ItemTags.PLANKS).pattern("# #").pattern(" # ").unlockedBy("has_brown_mushroom", has((ItemLike)Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", has((ItemLike)Blocks.RED_MUSHROOM)).unlockedBy("has_mushroom_stew", has((ItemLike)Items.MUSHROOM_STEW)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BREAD).define('#', (ItemLike)Items.WHEAT).pattern("###").unlockedBy("has_wheat", has((ItemLike)Items.WHEAT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BREWING_STAND).define('B', (ItemLike)Items.BLAZE_ROD).define('#', (Tag)ItemTags.STONE_CRAFTING_MATERIALS).pattern(" B ").pattern("###").unlockedBy("has_blaze_rod", has((ItemLike)Items.BLAZE_ROD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BRICKS).define('#', (ItemLike)Items.BRICK).pattern("##").pattern("##").unlockedBy("has_brick", has((ItemLike)Items.BRICK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BRICK_SLAB, 6).define('#', (ItemLike)Blocks.BRICKS).pattern("###").unlockedBy("has_brick_block", has((ItemLike)Blocks.BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_brick_block", has((ItemLike)Blocks.BRICKS)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.BROWN_DYE).requires((ItemLike)Items.COCOA_BEANS).group("brown_dye").unlockedBy("has_cocoa_beans", has((ItemLike)Items.COCOA_BEANS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.BUCKET).define('#', (ItemLike)Items.IRON_INGOT).pattern("# #").pattern(" # ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CAKE).define('A', (ItemLike)Items.MILK_BUCKET).define('B', (ItemLike)Items.SUGAR).define('C', (ItemLike)Items.WHEAT).define('E', (ItemLike)Items.EGG).pattern("AAA").pattern("BEB").pattern("CCC").unlockedBy("has_egg", has((ItemLike)Items.EGG)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CAMPFIRE).define('L', (Tag)ItemTags.LOGS).define('S', (ItemLike)Items.STICK).define('C', (Tag)ItemTags.COALS).pattern(" S ").pattern("SCS").pattern("LLL").unlockedBy("has_stick", has((ItemLike)Items.STICK)).unlockedBy("has_coal", has((Tag)ItemTags.COALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.CARROT_ON_A_STICK).define('#', (ItemLike)Items.FISHING_ROD).define('X', (ItemLike)Items.CARROT).pattern("# ").pattern(" X").unlockedBy("has_carrot", has((ItemLike)Items.CARROT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WARPED_FUNGUS_ON_A_STICK).define('#', (ItemLike)Items.FISHING_ROD).define('X', (ItemLike)Items.WARPED_FUNGUS).pattern("# ").pattern(" X").unlockedBy("has_warped_fungus", has((ItemLike)Items.WARPED_FUNGUS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CAULDRON).define('#', (ItemLike)Items.IRON_INGOT).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_water_bucket", has((ItemLike)Items.WATER_BUCKET)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COMPOSTER).define('#', (Tag)ItemTags.WOODEN_SLABS).pattern("# #").pattern("# #").pattern("###").unlockedBy("has_wood_slab", has((Tag)ItemTags.WOODEN_SLABS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHEST).define('#', (Tag)ItemTags.PLANKS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_lots_of_items", new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.atLeast(10), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new ItemPredicate[0])).save(var0);
      ShapedRecipeBuilder.shaped(Items.CHEST_MINECART).define('A', (ItemLike)Blocks.CHEST).define('B', (ItemLike)Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHISELED_NETHER_BRICKS).define('#', (ItemLike)Blocks.NETHER_BRICK_SLAB).pattern("#").pattern("#").unlockedBy("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHISELED_QUARTZ_BLOCK).define('#', (ItemLike)Blocks.QUARTZ_SLAB).pattern("#").pattern("#").unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHISELED_STONE_BRICKS).define('#', (ItemLike)Blocks.STONE_BRICK_SLAB).pattern("#").pattern("#").unlockedBy("has_stone_bricks", has((Tag)ItemTags.STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CLAY).define('#', (ItemLike)Items.CLAY_BALL).pattern("##").pattern("##").unlockedBy("has_clay_ball", has((ItemLike)Items.CLAY_BALL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.CLOCK).define('#', (ItemLike)Items.GOLD_INGOT).define('X', (ItemLike)Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.COAL, 9).requires((ItemLike)Blocks.COAL_BLOCK).unlockedBy("has_coal_block", has((ItemLike)Blocks.COAL_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COAL_BLOCK).define('#', (ItemLike)Items.COAL).pattern("###").pattern("###").pattern("###").unlockedBy("has_coal", has((ItemLike)Items.COAL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COARSE_DIRT, 4).define('D', (ItemLike)Blocks.DIRT).define('G', (ItemLike)Blocks.GRAVEL).pattern("DG").pattern("GD").unlockedBy("has_gravel", has((ItemLike)Blocks.GRAVEL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE_SLAB, 6).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("###").unlockedBy("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE_WALL, 6).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("###").pattern("###").unlockedBy("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COMPARATOR).define('#', (ItemLike)Blocks.REDSTONE_TORCH).define('X', (ItemLike)Items.QUARTZ).define('I', (ItemLike)Blocks.STONE).pattern(" # ").pattern("#X#").pattern("III").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Items.COMPASS).define('#', (ItemLike)Items.IRON_INGOT).define('X', (ItemLike)Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.COOKIE, 8).define('#', (ItemLike)Items.WHEAT).define('X', (ItemLike)Items.COCOA_BEANS).pattern("#X#").unlockedBy("has_cocoa", has((ItemLike)Items.COCOA_BEANS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CRAFTING_TABLE).define('#', (Tag)ItemTags.PLANKS).pattern("##").pattern("##").unlockedBy("has_planks", has((Tag)ItemTags.PLANKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.CROSSBOW).define('~', (ItemLike)Items.STRING).define('#', (ItemLike)Items.STICK).define('&', (ItemLike)Items.IRON_INGOT).define('$', (ItemLike)Blocks.TRIPWIRE_HOOK).pattern("#&#").pattern("~$~").pattern(" # ").unlockedBy("has_string", has((ItemLike)Items.STRING)).unlockedBy("has_stick", has((ItemLike)Items.STICK)).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).unlockedBy("has_tripwire_hook", has((ItemLike)Blocks.TRIPWIRE_HOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LOOM).define('#', (Tag)ItemTags.PLANKS).define('@', (ItemLike)Items.STRING).pattern("@@").pattern("##").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHISELED_RED_SANDSTONE).define('#', (ItemLike)Blocks.RED_SANDSTONE_SLAB).pattern("#").pattern("#").unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has((ItemLike)Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", has((ItemLike)Blocks.CUT_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHISELED_SANDSTONE).define('#', (ItemLike)Blocks.SANDSTONE_SLAB).pattern("#").pattern("#").unlockedBy("has_stone_slab", has((ItemLike)Blocks.SANDSTONE_SLAB)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.CYAN_DYE, 2).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.GREEN_DYE).unlockedBy("has_green_dye", has((ItemLike)Items.GREEN_DYE)).unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE).define('S', (ItemLike)Items.PRISMARINE_SHARD).define('I', (ItemLike)Items.BLACK_DYE).pattern("SSS").pattern("SIS").pattern("SSS").unlockedBy("has_prismarine_shard", has((ItemLike)Items.PRISMARINE_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_STAIRS, 4).define('#', (ItemLike)Blocks.PRISMARINE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_prismarine", has((ItemLike)Blocks.PRISMARINE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.PRISMARINE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_prismarine_bricks", has((ItemLike)Blocks.PRISMARINE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE_STAIRS, 4).define('#', (ItemLike)Blocks.DARK_PRISMARINE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_dark_prismarine", has((ItemLike)Blocks.DARK_PRISMARINE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DAYLIGHT_DETECTOR).define('Q', (ItemLike)Items.QUARTZ).define('G', (ItemLike)Blocks.GLASS).define('W', Ingredient.of((Tag)ItemTags.WOODEN_SLABS)).pattern("GGG").pattern("QQQ").pattern("WWW").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DETECTOR_RAIL, 6).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.STONE_PRESSURE_PLATE).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("XRX").unlockedBy("has_rail", has((ItemLike)Blocks.RAIL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.DIAMOND, 9).requires((ItemLike)Blocks.DIAMOND_BLOCK).unlockedBy("has_diamond_block", has((ItemLike)Blocks.DIAMOND_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.DIAMOND_AXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.DIAMOND).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DIAMOND_BLOCK).define('#', (ItemLike)Items.DIAMOND).pattern("###").pattern("###").pattern("###").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
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
      ShapedRecipeBuilder.shaped(Blocks.DROPPER).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("###").pattern("# #").pattern("#R#").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.EMERALD, 9).requires((ItemLike)Blocks.EMERALD_BLOCK).unlockedBy("has_emerald_block", has((ItemLike)Blocks.EMERALD_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.EMERALD_BLOCK).define('#', (ItemLike)Items.EMERALD).pattern("###").pattern("###").pattern("###").unlockedBy("has_emerald", has((ItemLike)Items.EMERALD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ENCHANTING_TABLE).define('B', (ItemLike)Items.BOOK).define('#', (ItemLike)Blocks.OBSIDIAN).define('D', (ItemLike)Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has((ItemLike)Blocks.OBSIDIAN)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ENDER_CHEST).define('#', (ItemLike)Blocks.OBSIDIAN).define('E', (ItemLike)Items.ENDER_EYE).pattern("###").pattern("#E#").pattern("###").unlockedBy("has_ender_eye", has((ItemLike)Items.ENDER_EYE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.ENDER_EYE).requires((ItemLike)Items.ENDER_PEARL).requires((ItemLike)Items.BLAZE_POWDER).unlockedBy("has_blaze_powder", has((ItemLike)Items.BLAZE_POWDER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICKS, 4).define('#', (ItemLike)Blocks.END_STONE).pattern("##").pattern("##").unlockedBy("has_end_stone", has((ItemLike)Blocks.END_STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.END_CRYSTAL).define('T', (ItemLike)Items.GHAST_TEAR).define('E', (ItemLike)Items.ENDER_EYE).define('G', (ItemLike)Blocks.GLASS).pattern("GGG").pattern("GEG").pattern("GTG").unlockedBy("has_ender_eye", has((ItemLike)Items.ENDER_EYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_ROD, 4).define('#', (ItemLike)Items.POPPED_CHORUS_FRUIT).define('/', (ItemLike)Items.BLAZE_ROD).pattern("/").pattern("#").unlockedBy("has_chorus_fruit_popped", has((ItemLike)Items.POPPED_CHORUS_FRUIT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FERMENTED_SPIDER_EYE).requires((ItemLike)Items.SPIDER_EYE).requires((ItemLike)Blocks.BROWN_MUSHROOM).requires((ItemLike)Items.SUGAR).unlockedBy("has_spider_eye", has((ItemLike)Items.SPIDER_EYE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FIRE_CHARGE, 3).requires((ItemLike)Items.GUNPOWDER).requires((ItemLike)Items.BLAZE_POWDER).requires(Ingredient.of(Items.COAL, Items.CHARCOAL)).unlockedBy("has_blaze_powder", has((ItemLike)Items.BLAZE_POWDER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.FISHING_ROD).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.STRING).pattern("  #").pattern(" #X").pattern("# X").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FLINT_AND_STEEL).requires((ItemLike)Items.IRON_INGOT).requires((ItemLike)Items.FLINT).unlockedBy("has_flint", has((ItemLike)Items.FLINT)).unlockedBy("has_obsidian", has((ItemLike)Blocks.OBSIDIAN)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.FLOWER_POT).define('#', (ItemLike)Items.BRICK).pattern("# #").pattern(" # ").unlockedBy("has_brick", has((ItemLike)Items.BRICK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.FURNACE).define('#', (Tag)ItemTags.STONE_CRAFTING_MATERIALS).pattern("###").pattern("# #").pattern("###").unlockedBy("has_cobblestone", has((Tag)ItemTags.STONE_CRAFTING_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.FURNACE_MINECART).define('A', (ItemLike)Blocks.FURNACE).define('B', (ItemLike)Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GLASS_BOTTLE, 3).define('#', (ItemLike)Blocks.GLASS).pattern("# #").pattern(" # ").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GLASS_PANE, 16).define('#', (ItemLike)Blocks.GLASS).pattern("###").pattern("###").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GLOWSTONE).define('#', (ItemLike)Items.GLOWSTONE_DUST).pattern("##").pattern("##").unlockedBy("has_glowstone_dust", has((ItemLike)Items.GLOWSTONE_DUST)).save(var0);
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
      ShapedRecipeBuilder.shaped(Blocks.GOLD_BLOCK).define('#', (ItemLike)Items.GOLD_INGOT).pattern("###").pattern("###").pattern("###").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.GOLD_INGOT, 9).requires((ItemLike)Blocks.GOLD_BLOCK).group("gold_ingot").unlockedBy("has_gold_block", has((ItemLike)Blocks.GOLD_BLOCK)).save(var0, "gold_ingot_from_gold_block");
      ShapedRecipeBuilder.shaped(Items.GOLD_INGOT).define('#', (ItemLike)Items.GOLD_NUGGET).pattern("###").pattern("###").pattern("###").group("gold_ingot").unlockedBy("has_gold_nugget", has((ItemLike)Items.GOLD_NUGGET)).save(var0, "gold_ingot_from_nuggets");
      ShapelessRecipeBuilder.shapeless(Items.GOLD_NUGGET, 9).requires((ItemLike)Items.GOLD_INGOT).unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.GRANITE).requires((ItemLike)Blocks.DIORITE).requires((ItemLike)Items.QUARTZ).unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.GRAY_DYE, 2).requires((ItemLike)Items.BLACK_DYE).requires((ItemLike)Items.WHITE_DYE).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).unlockedBy("has_black_dye", has((ItemLike)Items.BLACK_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HAY_BLOCK).define('#', (ItemLike)Items.WHEAT).pattern("###").pattern("###").pattern("###").unlockedBy("has_wheat", has((ItemLike)Items.WHEAT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE).define('#', (ItemLike)Items.IRON_INGOT).pattern("##").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.HONEY_BOTTLE, 4).requires((ItemLike)Items.HONEY_BLOCK).requires((ItemLike)Items.GLASS_BOTTLE, 4).unlockedBy("has_honey_block", has((ItemLike)Blocks.HONEY_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HONEY_BLOCK, 1).define('S', (ItemLike)Items.HONEY_BOTTLE).pattern("SS").pattern("SS").unlockedBy("has_honey_bottle", has((ItemLike)Items.HONEY_BOTTLE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HONEYCOMB_BLOCK).define('H', (ItemLike)Items.HONEYCOMB).pattern("HH").pattern("HH").unlockedBy("has_honeycomb", has((ItemLike)Items.HONEYCOMB)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.HOPPER).define('C', (ItemLike)Blocks.CHEST).define('I', (ItemLike)Items.IRON_INGOT).pattern("I I").pattern("ICI").pattern(" I ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.HOPPER_MINECART).define('A', (ItemLike)Blocks.HOPPER).define('B', (ItemLike)Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_AXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.IRON_BARS, 16).define('#', (ItemLike)Items.IRON_INGOT).pattern("###").pattern("###").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.IRON_BLOCK).define('#', (ItemLike)Items.IRON_INGOT).pattern("###").pattern("###").pattern("###").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_BOOTS).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_CHESTPLATE).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.IRON_DOOR, 3).define('#', (ItemLike)Items.IRON_INGOT).pattern("##").pattern("##").pattern("##").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_HELMET).define('X', (ItemLike)Items.IRON_INGOT).pattern("XXX").pattern("X X").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_HOE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.IRON_INGOT, 9).requires((ItemLike)Blocks.IRON_BLOCK).group("iron_ingot").unlockedBy("has_iron_block", has((ItemLike)Blocks.IRON_BLOCK)).save(var0, "iron_ingot_from_iron_block");
      ShapedRecipeBuilder.shaped(Items.IRON_INGOT).define('#', (ItemLike)Items.IRON_NUGGET).pattern("###").pattern("###").pattern("###").group("iron_ingot").unlockedBy("has_iron_nugget", has((ItemLike)Items.IRON_NUGGET)).save(var0, "iron_ingot_from_nuggets");
      ShapedRecipeBuilder.shaped(Items.IRON_LEGGINGS).define('X', (ItemLike)Items.IRON_INGOT).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.IRON_NUGGET, 9).requires((ItemLike)Items.IRON_INGOT).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("X").pattern("#").pattern("#").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.IRON_SWORD).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("X").pattern("X").pattern("#").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.IRON_TRAPDOOR).define('#', (ItemLike)Items.IRON_INGOT).pattern("##").pattern("##").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.ITEM_FRAME).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.LEATHER).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.JUKEBOX).define('#', (Tag)ItemTags.PLANKS).define('X', (ItemLike)Items.DIAMOND).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_diamond", has((ItemLike)Items.DIAMOND)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LADDER, 3).define('#', (ItemLike)Items.STICK).pattern("# #").pattern("###").pattern("# #").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LAPIS_BLOCK).define('#', (ItemLike)Items.LAPIS_LAZULI).pattern("###").pattern("###").pattern("###").unlockedBy("has_lapis", has((ItemLike)Items.LAPIS_LAZULI)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.LAPIS_LAZULI, 9).requires((ItemLike)Blocks.LAPIS_BLOCK).unlockedBy("has_lapis_block", has((ItemLike)Blocks.LAPIS_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEAD, 2).define('~', (ItemLike)Items.STRING).define('O', (ItemLike)Items.SLIME_BALL).pattern("~~ ").pattern("~O ").pattern("  ~").unlockedBy("has_slime_ball", has((ItemLike)Items.SLIME_BALL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER).define('#', (ItemLike)Items.RABBIT_HIDE).pattern("##").pattern("##").unlockedBy("has_rabbit_hide", has((ItemLike)Items.RABBIT_HIDE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_BOOTS).define('X', (ItemLike)Items.LEATHER).pattern("X X").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_CHESTPLATE).define('X', (ItemLike)Items.LEATHER).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_HELMET).define('X', (ItemLike)Items.LEATHER).pattern("XXX").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_LEGGINGS).define('X', (ItemLike)Items.LEATHER).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.LEATHER_HORSE_ARMOR).define('X', (ItemLike)Items.LEATHER).pattern("X X").pattern("XXX").pattern("X X").unlockedBy("has_leather", has((ItemLike)Items.LEATHER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LECTERN).define('S', (Tag)ItemTags.WOODEN_SLABS).define('B', (ItemLike)Blocks.BOOKSHELF).pattern("SSS").pattern(" B ").pattern(" S ").unlockedBy("has_book", has((ItemLike)Items.BOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LEVER).define('#', (ItemLike)Blocks.COBBLESTONE).define('X', (ItemLike)Items.STICK).pattern("X").pattern("#").unlockedBy("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_BLUE_DYE).requires((ItemLike)Blocks.BLUE_ORCHID).group("light_blue_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.BLUE_ORCHID)).save(var0, "light_blue_dye_from_blue_orchid");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_BLUE_DYE, 2).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.WHITE_DYE).group("light_blue_dye").unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0, "light_blue_dye_from_blue_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE).requires((ItemLike)Blocks.AZURE_BLUET).group("light_gray_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.AZURE_BLUET)).save(var0, "light_gray_dye_from_azure_bluet");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 2).requires((ItemLike)Items.GRAY_DYE).requires((ItemLike)Items.WHITE_DYE).group("light_gray_dye").unlockedBy("has_gray_dye", has((ItemLike)Items.GRAY_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0, "light_gray_dye_from_gray_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE, 3).requires((ItemLike)Items.BLACK_DYE).requires((ItemLike)Items.WHITE_DYE, 2).group("light_gray_dye").unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).unlockedBy("has_black_dye", has((ItemLike)Items.BLACK_DYE)).save(var0, "light_gray_dye_from_black_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE).requires((ItemLike)Blocks.OXEYE_DAISY).group("light_gray_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.OXEYE_DAISY)).save(var0, "light_gray_dye_from_oxeye_daisy");
      ShapelessRecipeBuilder.shapeless(Items.LIGHT_GRAY_DYE).requires((ItemLike)Blocks.WHITE_TULIP).group("light_gray_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.WHITE_TULIP)).save(var0, "light_gray_dye_from_white_tulip");
      ShapedRecipeBuilder.shaped(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).define('#', (ItemLike)Items.GOLD_INGOT).pattern("##").unlockedBy("has_gold_ingot", has((ItemLike)Items.GOLD_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.LIME_DYE, 2).requires((ItemLike)Items.GREEN_DYE).requires((ItemLike)Items.WHITE_DYE).unlockedBy("has_green_dye", has((ItemLike)Items.GREEN_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.JACK_O_LANTERN).define('A', (ItemLike)Blocks.CARVED_PUMPKIN).define('B', (ItemLike)Blocks.TORCH).pattern("A").pattern("B").unlockedBy("has_carved_pumpkin", has((ItemLike)Blocks.CARVED_PUMPKIN)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE).requires((ItemLike)Blocks.ALLIUM).group("magenta_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.ALLIUM)).save(var0, "magenta_dye_from_allium");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 4).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.RED_DYE, 2).requires((ItemLike)Items.WHITE_DYE).group("magenta_dye").unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_rose_red", has((ItemLike)Items.RED_DYE)).unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).save(var0, "magenta_dye_from_blue_red_white_dye");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 3).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.RED_DYE).requires((ItemLike)Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", has((ItemLike)Items.PINK_DYE)).unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).save(var0, "magenta_dye_from_blue_red_pink");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 2).requires((ItemLike)Blocks.LILAC).group("magenta_dye").unlockedBy("has_double_plant", has((ItemLike)Blocks.LILAC)).save(var0, "magenta_dye_from_lilac");
      ShapelessRecipeBuilder.shapeless(Items.MAGENTA_DYE, 2).requires((ItemLike)Items.PURPLE_DYE).requires((ItemLike)Items.PINK_DYE).group("magenta_dye").unlockedBy("has_pink_dye", has((ItemLike)Items.PINK_DYE)).unlockedBy("has_purple_dye", has((ItemLike)Items.PURPLE_DYE)).save(var0, "magenta_dye_from_purple_and_pink");
      ShapedRecipeBuilder.shaped(Blocks.MAGMA_BLOCK).define('#', (ItemLike)Items.MAGMA_CREAM).pattern("##").pattern("##").unlockedBy("has_magma_cream", has((ItemLike)Items.MAGMA_CREAM)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MAGMA_CREAM).requires((ItemLike)Items.BLAZE_POWDER).requires((ItemLike)Items.SLIME_BALL).unlockedBy("has_blaze_powder", has((ItemLike)Items.BLAZE_POWDER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.MAP).define('#', (ItemLike)Items.PAPER).define('X', (ItemLike)Items.COMPASS).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_compass", has((ItemLike)Items.COMPASS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MELON).define('M', (ItemLike)Items.MELON_SLICE).pattern("MMM").pattern("MMM").pattern("MMM").unlockedBy("has_melon", has((ItemLike)Items.MELON_SLICE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MELON_SEEDS).requires((ItemLike)Items.MELON_SLICE).unlockedBy("has_melon", has((ItemLike)Items.MELON_SLICE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.MINECART).define('#', (ItemLike)Items.IRON_INGOT).pattern("# #").pattern("###").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_COBBLESTONE).requires((ItemLike)Blocks.COBBLESTONE).requires((ItemLike)Blocks.VINE).unlockedBy("has_vine", has((ItemLike)Blocks.VINE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MOSSY_COBBLESTONE_WALL, 6).define('#', (ItemLike)Blocks.MOSSY_COBBLESTONE).pattern("###").pattern("###").unlockedBy("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.MOSSY_STONE_BRICKS).requires((ItemLike)Blocks.STONE_BRICKS).requires((ItemLike)Blocks.VINE).unlockedBy("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MUSHROOM_STEW).requires((ItemLike)Blocks.BROWN_MUSHROOM).requires((ItemLike)Blocks.RED_MUSHROOM).requires((ItemLike)Items.BOWL).unlockedBy("has_mushroom_stew", has((ItemLike)Items.MUSHROOM_STEW)).unlockedBy("has_bowl", has((ItemLike)Items.BOWL)).unlockedBy("has_brown_mushroom", has((ItemLike)Blocks.BROWN_MUSHROOM)).unlockedBy("has_red_mushroom", has((ItemLike)Blocks.RED_MUSHROOM)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICKS).define('N', (ItemLike)Items.NETHER_BRICK).pattern("NN").pattern("NN").unlockedBy("has_netherbrick", has((ItemLike)Items.NETHER_BRICK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_FENCE, 6).define('#', (ItemLike)Blocks.NETHER_BRICKS).define('-', (ItemLike)Items.NETHER_BRICK).pattern("#-#").pattern("#-#").unlockedBy("has_nether_brick", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.NETHER_BRICKS).pattern("###").unlockedBy("has_nether_brick", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.NETHER_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_nether_brick", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_WART_BLOCK).define('#', (ItemLike)Items.NETHER_WART).pattern("###").pattern("###").pattern("###").unlockedBy("has_nether_wart", has((ItemLike)Items.NETHER_WART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NOTE_BLOCK).define('#', (Tag)ItemTags.PLANKS).define('X', (ItemLike)Items.REDSTONE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.OBSERVER).define('Q', (ItemLike)Items.QUARTZ).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("###").pattern("RRQ").pattern("###").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.ORANGE_DYE).requires((ItemLike)Blocks.ORANGE_TULIP).group("orange_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.ORANGE_TULIP)).save(var0, "orange_dye_from_orange_tulip");
      ShapelessRecipeBuilder.shapeless(Items.ORANGE_DYE, 2).requires((ItemLike)Items.RED_DYE).requires((ItemLike)Items.YELLOW_DYE).group("orange_dye").unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).unlockedBy("has_yellow_dye", has((ItemLike)Items.YELLOW_DYE)).save(var0, "orange_dye_from_red_yellow");
      ShapedRecipeBuilder.shaped(Items.PAINTING).define('#', (ItemLike)Items.STICK).define('X', Ingredient.of((Tag)ItemTags.WOOL)).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_wool", has((Tag)ItemTags.WOOL)).save(var0);
      ShapedRecipeBuilder.shaped(Items.PAPER, 3).define('#', (ItemLike)Blocks.SUGAR_CANE).pattern("###").unlockedBy("has_reeds", has((ItemLike)Blocks.SUGAR_CANE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_PILLAR, 2).define('#', (ItemLike)Blocks.QUARTZ_BLOCK).pattern("#").pattern("#").unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.PACKED_ICE).requires((ItemLike)Blocks.ICE, 9).unlockedBy("has_ice", has((ItemLike)Blocks.ICE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PINK_DYE, 2).requires((ItemLike)Blocks.PEONY).group("pink_dye").unlockedBy("has_double_plant", has((ItemLike)Blocks.PEONY)).save(var0, "pink_dye_from_peony");
      ShapelessRecipeBuilder.shapeless(Items.PINK_DYE).requires((ItemLike)Blocks.PINK_TULIP).group("pink_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.PINK_TULIP)).save(var0, "pink_dye_from_pink_tulip");
      ShapelessRecipeBuilder.shapeless(Items.PINK_DYE, 2).requires((ItemLike)Items.RED_DYE).requires((ItemLike)Items.WHITE_DYE).group("pink_dye").unlockedBy("has_white_dye", has((ItemLike)Items.WHITE_DYE)).unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).save(var0, "pink_dye_from_red_white_dye");
      ShapedRecipeBuilder.shaped(Blocks.PISTON).define('R', (ItemLike)Items.REDSTONE).define('#', (ItemLike)Blocks.COBBLESTONE).define('T', (Tag)ItemTags.PLANKS).define('X', (ItemLike)Items.IRON_INGOT).pattern("TTT").pattern("#X#").pattern("#R#").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BASALT, 4).define('S', (ItemLike)Blocks.BASALT).pattern("SS").pattern("SS").unlockedBy("has_basalt", has((ItemLike)Blocks.BASALT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_GRANITE, 4).define('S', (ItemLike)Blocks.GRANITE).pattern("SS").pattern("SS").unlockedBy("has_stone", has((ItemLike)Blocks.GRANITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_DIORITE, 4).define('S', (ItemLike)Blocks.DIORITE).pattern("SS").pattern("SS").unlockedBy("has_stone", has((ItemLike)Blocks.DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_ANDESITE, 4).define('S', (ItemLike)Blocks.ANDESITE).pattern("SS").pattern("SS").unlockedBy("has_stone", has((ItemLike)Blocks.ANDESITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE).define('S', (ItemLike)Items.PRISMARINE_SHARD).pattern("SS").pattern("SS").unlockedBy("has_prismarine_shard", has((ItemLike)Items.PRISMARINE_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICKS).define('S', (ItemLike)Items.PRISMARINE_SHARD).pattern("SSS").pattern("SSS").pattern("SSS").unlockedBy("has_prismarine_shard", has((ItemLike)Items.PRISMARINE_SHARD)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_SLAB, 6).define('#', (ItemLike)Blocks.PRISMARINE).pattern("###").unlockedBy("has_prismarine", has((ItemLike)Blocks.PRISMARINE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.PRISMARINE_BRICKS).pattern("###").unlockedBy("has_prismarine_bricks", has((ItemLike)Blocks.PRISMARINE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DARK_PRISMARINE_SLAB, 6).define('#', (ItemLike)Blocks.DARK_PRISMARINE).pattern("###").unlockedBy("has_dark_prismarine", has((ItemLike)Blocks.DARK_PRISMARINE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_PIE).requires((ItemLike)Blocks.PUMPKIN).requires((ItemLike)Items.SUGAR).requires((ItemLike)Items.EGG).unlockedBy("has_carved_pumpkin", has((ItemLike)Blocks.CARVED_PUMPKIN)).unlockedBy("has_pumpkin", has((ItemLike)Blocks.PUMPKIN)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PUMPKIN_SEEDS, 4).requires((ItemLike)Blocks.PUMPKIN).unlockedBy("has_pumpkin", has((ItemLike)Blocks.PUMPKIN)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.PURPLE_DYE, 2).requires((ItemLike)Items.BLUE_DYE).requires((ItemLike)Items.RED_DYE).unlockedBy("has_blue_dye", has((ItemLike)Items.BLUE_DYE)).unlockedBy("has_red_dye", has((ItemLike)Items.RED_DYE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SHULKER_BOX).define('#', (ItemLike)Blocks.CHEST).define('-', (ItemLike)Items.SHULKER_SHELL).pattern("-").pattern("#").pattern("-").unlockedBy("has_shulker_shell", has((ItemLike)Items.SHULKER_SHELL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_BLOCK, 4).define('F', (ItemLike)Items.POPPED_CHORUS_FRUIT).pattern("FF").pattern("FF").unlockedBy("has_chorus_fruit_popped", has((ItemLike)Items.POPPED_CHORUS_FRUIT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_PILLAR).define('#', (ItemLike)Blocks.PURPUR_SLAB).pattern("#").pattern("#").unlockedBy("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_SLAB, 6).define('#', Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).pattern("###").unlockedBy("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PURPUR_STAIRS, 4).define('#', Ingredient.of(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BLOCK).define('#', (ItemLike)Items.QUARTZ).pattern("##").pattern("##").unlockedBy("has_quartz", has((ItemLike)Items.QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_BRICKS, 4).define('#', (ItemLike)Blocks.QUARTZ_BLOCK).pattern("##").pattern("##").unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_SLAB, 6).define('#', Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).pattern("###").unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.QUARTZ_STAIRS, 4).define('#', Ingredient.of(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_chiseled_quartz_block", has((ItemLike)Blocks.CHISELED_QUARTZ_BLOCK)).unlockedBy("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).unlockedBy("has_quartz_pillar", has((ItemLike)Blocks.QUARTZ_PILLAR)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires((ItemLike)Items.BAKED_POTATO).requires((ItemLike)Items.COOKED_RABBIT).requires((ItemLike)Items.BOWL).requires((ItemLike)Items.CARROT).requires((ItemLike)Blocks.BROWN_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", has((ItemLike)Items.COOKED_RABBIT)).save(var0, "rabbit_stew_from_brown_mushroom");
      ShapelessRecipeBuilder.shapeless(Items.RABBIT_STEW).requires((ItemLike)Items.BAKED_POTATO).requires((ItemLike)Items.COOKED_RABBIT).requires((ItemLike)Items.BOWL).requires((ItemLike)Items.CARROT).requires((ItemLike)Blocks.RED_MUSHROOM).group("rabbit_stew").unlockedBy("has_cooked_rabbit", has((ItemLike)Items.COOKED_RABBIT)).save(var0, "rabbit_stew_from_red_mushroom");
      ShapedRecipeBuilder.shaped(Blocks.RAIL, 16).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("X X").unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.REDSTONE, 9).requires((ItemLike)Blocks.REDSTONE_BLOCK).unlockedBy("has_redstone_block", has((ItemLike)Blocks.REDSTONE_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_BLOCK).define('#', (ItemLike)Items.REDSTONE).pattern("###").pattern("###").pattern("###").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_LAMP).define('R', (ItemLike)Items.REDSTONE).define('G', (ItemLike)Blocks.GLOWSTONE).pattern(" R ").pattern("RGR").pattern(" R ").unlockedBy("has_glowstone", has((ItemLike)Blocks.GLOWSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.REDSTONE_TORCH).define('#', (ItemLike)Items.STICK).define('X', (ItemLike)Items.REDSTONE).pattern("X").pattern("#").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires((ItemLike)Items.BEETROOT).group("red_dye").unlockedBy("has_beetroot", has((ItemLike)Items.BEETROOT)).save(var0, "red_dye_from_beetroot");
      ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires((ItemLike)Blocks.POPPY).group("red_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.POPPY)).save(var0, "red_dye_from_poppy");
      ShapelessRecipeBuilder.shapeless(Items.RED_DYE, 2).requires((ItemLike)Blocks.ROSE_BUSH).group("red_dye").unlockedBy("has_double_plant", has((ItemLike)Blocks.ROSE_BUSH)).save(var0, "red_dye_from_rose_bush");
      ShapelessRecipeBuilder.shapeless(Items.RED_DYE).requires((ItemLike)Blocks.RED_TULIP).group("red_dye").unlockedBy("has_red_flower", has((ItemLike)Blocks.RED_TULIP)).save(var0, "red_dye_from_tulip");
      ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICKS).define('W', (ItemLike)Items.NETHER_WART).define('N', (ItemLike)Items.NETHER_BRICK).pattern("NW").pattern("WN").unlockedBy("has_nether_wart", has((ItemLike)Items.NETHER_WART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE).define('#', (ItemLike)Blocks.RED_SAND).pattern("##").pattern("##").unlockedBy("has_sand", has((ItemLike)Blocks.RED_SAND)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE_SLAB, 6).define('#', Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).pattern("###").unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has((ItemLike)Blocks.CHISELED_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CUT_RED_SANDSTONE_SLAB, 6).define('#', (ItemLike)Blocks.CUT_RED_SANDSTONE).pattern("###").unlockedBy("has_cut_red_sandstone", has((ItemLike)Blocks.CUT_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE_STAIRS, 4).define('#', Ingredient.of(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).unlockedBy("has_chiseled_red_sandstone", has((ItemLike)Blocks.CHISELED_RED_SANDSTONE)).unlockedBy("has_cut_red_sandstone", has((ItemLike)Blocks.CUT_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.REPEATER).define('#', (ItemLike)Blocks.REDSTONE_TORCH).define('X', (ItemLike)Items.REDSTONE).define('I', (ItemLike)Blocks.STONE).pattern("#X#").pattern("III").unlockedBy("has_redstone_torch", has((ItemLike)Blocks.REDSTONE_TORCH)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SANDSTONE).define('#', (ItemLike)Blocks.SAND).pattern("##").pattern("##").unlockedBy("has_sand", has((ItemLike)Blocks.SAND)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SANDSTONE_SLAB, 6).define('#', Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).pattern("###").unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", has((ItemLike)Blocks.CHISELED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CUT_SANDSTONE_SLAB, 6).define('#', (ItemLike)Blocks.CUT_SANDSTONE).pattern("###").unlockedBy("has_cut_sandstone", has((ItemLike)Blocks.CUT_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SANDSTONE_STAIRS, 4).define('#', Ingredient.of(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).unlockedBy("has_chiseled_sandstone", has((ItemLike)Blocks.CHISELED_SANDSTONE)).unlockedBy("has_cut_sandstone", has((ItemLike)Blocks.CUT_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SEA_LANTERN).define('S', (ItemLike)Items.PRISMARINE_SHARD).define('C', (ItemLike)Items.PRISMARINE_CRYSTALS).pattern("SCS").pattern("CCC").pattern("SCS").unlockedBy("has_prismarine_crystals", has((ItemLike)Items.PRISMARINE_CRYSTALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SHEARS).define('#', (ItemLike)Items.IRON_INGOT).pattern(" #").pattern("# ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SHIELD).define('W', (Tag)ItemTags.PLANKS).define('o', (ItemLike)Items.IRON_INGOT).pattern("WoW").pattern("WWW").pattern(" W ").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SLIME_BLOCK).define('#', (ItemLike)Items.SLIME_BALL).pattern("###").pattern("###").pattern("###").unlockedBy("has_slime_ball", has((ItemLike)Items.SLIME_BALL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.SLIME_BALL, 9).requires((ItemLike)Blocks.SLIME_BLOCK).unlockedBy("has_slime", has((ItemLike)Blocks.SLIME_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CUT_RED_SANDSTONE, 4).define('#', (ItemLike)Blocks.RED_SANDSTONE).pattern("##").pattern("##").unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CUT_SANDSTONE, 4).define('#', (ItemLike)Blocks.SANDSTONE).pattern("##").pattern("##").unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SNOW_BLOCK).define('#', (ItemLike)Items.SNOWBALL).pattern("##").pattern("##").unlockedBy("has_snowball", has((ItemLike)Items.SNOWBALL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SNOW, 6).define('#', (ItemLike)Blocks.SNOW_BLOCK).pattern("###").unlockedBy("has_snowball", has((ItemLike)Items.SNOWBALL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_CAMPFIRE).define('L', (Tag)ItemTags.LOGS).define('S', (ItemLike)Items.STICK).define('#', (Tag)ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern(" S ").pattern("S#S").pattern("LLL").unlockedBy("has_stick", has((ItemLike)Items.STICK)).unlockedBy("has_soul_sand", has((Tag)ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.GLISTERING_MELON_SLICE).define('#', (ItemLike)Items.GOLD_NUGGET).define('X', (ItemLike)Items.MELON_SLICE).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_melon", has((ItemLike)Items.MELON_SLICE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.SPECTRAL_ARROW, 2).define('#', (ItemLike)Items.GLOWSTONE_DUST).define('X', (ItemLike)Items.ARROW).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_glowstone_dust", has((ItemLike)Items.GLOWSTONE_DUST)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STICK, 4).define('#', (Tag)ItemTags.PLANKS).pattern("#").pattern("#").group("sticks").unlockedBy("has_planks", has((Tag)ItemTags.PLANKS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STICK, 1).define('#', (ItemLike)Blocks.BAMBOO).pattern("#").pattern("#").group("sticks").unlockedBy("has_bamboo", has((ItemLike)Blocks.BAMBOO)).save(var0, "stick_from_bamboo_item");
      ShapedRecipeBuilder.shaped(Blocks.STICKY_PISTON).define('P', (ItemLike)Blocks.PISTON).define('S', (ItemLike)Items.SLIME_BALL).pattern("S").pattern("P").unlockedBy("has_slime_ball", has((ItemLike)Items.SLIME_BALL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_BRICKS, 4).define('#', (ItemLike)Blocks.STONE).pattern("##").pattern("##").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_AXE).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_cobblestone", has((Tag)ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.STONE_BRICKS).pattern("###").unlockedBy("has_stone_bricks", has((Tag)ItemTags.STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.STONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_stone_bricks", has((Tag)ItemTags.STONE_BRICKS)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.STONE_BUTTON).requires((ItemLike)Blocks.STONE).unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_HOE).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_cobblestone", has((Tag)ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.STONE_TOOL_MATERIALS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_cobblestone", has((Tag)ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_PRESSURE_PLATE).define('#', (ItemLike)Blocks.STONE).pattern("##").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("#").pattern("#").unlockedBy("has_cobblestone", has((Tag)ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_SLAB, 6).define('#', (ItemLike)Blocks.STONE).pattern("###").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_STONE_SLAB, 6).define('#', (ItemLike)Blocks.SMOOTH_STONE).pattern("###").unlockedBy("has_smooth_stone", has((ItemLike)Blocks.SMOOTH_STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.COBBLESTONE_STAIRS, 4).define('#', (ItemLike)Blocks.COBBLESTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Items.STONE_SWORD).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("X").pattern("#").unlockedBy("has_cobblestone", has((Tag)ItemTags.STONE_TOOL_MATERIALS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.WHITE_WOOL).define('#', (ItemLike)Items.STRING).pattern("##").pattern("##").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0, "white_wool_from_string");
      ShapelessRecipeBuilder.shapeless(Items.SUGAR).requires((ItemLike)Blocks.SUGAR_CANE).group("sugar").unlockedBy("has_reeds", has((ItemLike)Blocks.SUGAR_CANE)).save(var0, "sugar_from_sugar_cane");
      ShapelessRecipeBuilder.shapeless(Items.SUGAR, 3).requires((ItemLike)Items.HONEY_BOTTLE).group("sugar").unlockedBy("has_honey_bottle", has((ItemLike)Items.HONEY_BOTTLE)).save(var0, "sugar_from_honey_bottle");
      ShapedRecipeBuilder.shaped(Blocks.TARGET).define('H', (ItemLike)Items.HAY_BLOCK).define('R', (ItemLike)Items.REDSTONE).pattern(" R ").pattern("RHR").pattern(" R ").unlockedBy("has_redstone", has((ItemLike)Items.REDSTONE)).unlockedBy("has_hay_block", has((ItemLike)Blocks.HAY_BLOCK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TNT).define('#', Ingredient.of(Blocks.SAND, Blocks.RED_SAND)).define('X', (ItemLike)Items.GUNPOWDER).pattern("X#X").pattern("#X#").pattern("X#X").unlockedBy("has_gunpowder", has((ItemLike)Items.GUNPOWDER)).save(var0);
      ShapedRecipeBuilder.shaped(Items.TNT_MINECART).define('A', (ItemLike)Blocks.TNT).define('B', (ItemLike)Items.MINECART).pattern("A").pattern("B").unlockedBy("has_minecart", has((ItemLike)Items.MINECART)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TORCH, 4).define('#', (ItemLike)Items.STICK).define('X', Ingredient.of(Items.COAL, Items.CHARCOAL)).pattern("X").pattern("#").unlockedBy("has_stone_pickaxe", has((ItemLike)Items.STONE_PICKAXE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_TORCH, 4).define('X', Ingredient.of(Items.COAL, Items.CHARCOAL)).define('#', (ItemLike)Items.STICK).define('S', (Tag)ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern("X").pattern("#").pattern("S").unlockedBy("has_soul_sand", has((Tag)ItemTags.SOUL_FIRE_BASE_BLOCKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LANTERN).define('#', (ItemLike)Items.TORCH).define('X', (ItemLike)Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_iron_nugget", has((ItemLike)Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SOUL_LANTERN).define('#', (ItemLike)Items.SOUL_TORCH).define('X', (ItemLike)Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").unlockedBy("has_soul_torch", has((ItemLike)Items.SOUL_TORCH)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.TRAPPED_CHEST).requires((ItemLike)Blocks.CHEST).requires((ItemLike)Blocks.TRIPWIRE_HOOK).unlockedBy("has_tripwire_hook", has((ItemLike)Blocks.TRIPWIRE_HOOK)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.TRIPWIRE_HOOK, 2).define('#', (Tag)ItemTags.PLANKS).define('S', (ItemLike)Items.STICK).define('I', (ItemLike)Items.IRON_INGOT).pattern("I").pattern("S").pattern("#").unlockedBy("has_string", has((ItemLike)Items.STRING)).save(var0);
      ShapedRecipeBuilder.shaped(Items.TURTLE_HELMET).define('X', (ItemLike)Items.SCUTE).pattern("XXX").pattern("X X").unlockedBy("has_scute", has((ItemLike)Items.SCUTE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WHEAT, 9).requires((ItemLike)Blocks.HAY_BLOCK).unlockedBy("has_hay_block", has((ItemLike)Blocks.HAY_BLOCK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WHITE_DYE).requires((ItemLike)Items.BONE_MEAL).group("white_dye").unlockedBy("has_bone_meal", has((ItemLike)Items.BONE_MEAL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WHITE_DYE).requires((ItemLike)Blocks.LILY_OF_THE_VALLEY).group("white_dye").unlockedBy("has_white_flower", has((ItemLike)Blocks.LILY_OF_THE_VALLEY)).save(var0, "white_dye_from_lily_of_the_valley");
      ShapedRecipeBuilder.shaped(Items.WOODEN_AXE).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.PLANKS).pattern("XX").pattern("X#").pattern(" #").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_HOE).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.PLANKS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_PICKAXE).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.PLANKS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_SHOVEL).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.PLANKS).pattern("X").pattern("#").pattern("#").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapedRecipeBuilder.shaped(Items.WOODEN_SWORD).define('#', (ItemLike)Items.STICK).define('X', (Tag)ItemTags.PLANKS).pattern("X").pattern("X").pattern("#").unlockedBy("has_stick", has((ItemLike)Items.STICK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.WRITABLE_BOOK).requires((ItemLike)Items.BOOK).requires((ItemLike)Items.INK_SAC).requires((ItemLike)Items.FEATHER).unlockedBy("has_book", has((ItemLike)Items.BOOK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.YELLOW_DYE).requires((ItemLike)Blocks.DANDELION).group("yellow_dye").unlockedBy("has_yellow_flower", has((ItemLike)Blocks.DANDELION)).save(var0, "yellow_dye_from_dandelion");
      ShapelessRecipeBuilder.shapeless(Items.YELLOW_DYE, 2).requires((ItemLike)Blocks.SUNFLOWER).group("yellow_dye").unlockedBy("has_double_plant", has((ItemLike)Blocks.SUNFLOWER)).save(var0, "yellow_dye_from_sunflower");
      ShapelessRecipeBuilder.shapeless(Items.DRIED_KELP, 9).requires((ItemLike)Blocks.DRIED_KELP_BLOCK).unlockedBy("has_dried_kelp_block", has((ItemLike)Blocks.DRIED_KELP_BLOCK)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.DRIED_KELP_BLOCK).requires((ItemLike)Items.DRIED_KELP, 9).unlockedBy("has_dried_kelp", has((ItemLike)Items.DRIED_KELP)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CONDUIT).define('#', (ItemLike)Items.NAUTILUS_SHELL).define('X', (ItemLike)Items.HEART_OF_THE_SEA).pattern("###").pattern("#X#").pattern("###").unlockedBy("has_nautilus_core", has((ItemLike)Items.HEART_OF_THE_SEA)).unlockedBy("has_nautilus_shell", has((ItemLike)Items.NAUTILUS_SHELL)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_GRANITE_STAIRS, 4).define('#', (ItemLike)Blocks.POLISHED_GRANITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_granite", has((ItemLike)Blocks.POLISHED_GRANITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, 4).define('#', (ItemLike)Blocks.SMOOTH_RED_SANDSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_smooth_red_sandstone", has((ItemLike)Blocks.SMOOTH_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MOSSY_STONE_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.MOSSY_STONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_DIORITE_STAIRS, 4).define('#', (ItemLike)Blocks.POLISHED_DIORITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_diorite", has((ItemLike)Blocks.POLISHED_DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MOSSY_COBBLESTONE_STAIRS, 4).define('#', (ItemLike)Blocks.MOSSY_COBBLESTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.END_STONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_end_stone_bricks", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_STAIRS, 4).define('#', (ItemLike)Blocks.STONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_SANDSTONE_STAIRS, 4).define('#', (ItemLike)Blocks.SMOOTH_SANDSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_smooth_sandstone", has((ItemLike)Blocks.SMOOTH_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_QUARTZ_STAIRS, 4).define('#', (ItemLike)Blocks.SMOOTH_QUARTZ).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_smooth_quartz", has((ItemLike)Blocks.SMOOTH_QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GRANITE_STAIRS, 4).define('#', (ItemLike)Blocks.GRANITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ANDESITE_STAIRS, 4).define('#', (ItemLike)Blocks.ANDESITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.RED_NETHER_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_red_nether_bricks", has((ItemLike)Blocks.RED_NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_ANDESITE_STAIRS, 4).define('#', (ItemLike)Blocks.POLISHED_ANDESITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_andesite", has((ItemLike)Blocks.POLISHED_ANDESITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DIORITE_STAIRS, 4).define('#', (ItemLike)Blocks.DIORITE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_GRANITE_SLAB, 6).define('#', (ItemLike)Blocks.POLISHED_GRANITE).pattern("###").unlockedBy("has_polished_granite", has((ItemLike)Blocks.POLISHED_GRANITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_RED_SANDSTONE_SLAB, 6).define('#', (ItemLike)Blocks.SMOOTH_RED_SANDSTONE).pattern("###").unlockedBy("has_smooth_red_sandstone", has((ItemLike)Blocks.SMOOTH_RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MOSSY_STONE_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.MOSSY_STONE_BRICKS).pattern("###").unlockedBy("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_DIORITE_SLAB, 6).define('#', (ItemLike)Blocks.POLISHED_DIORITE).pattern("###").unlockedBy("has_polished_diorite", has((ItemLike)Blocks.POLISHED_DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MOSSY_COBBLESTONE_SLAB, 6).define('#', (ItemLike)Blocks.MOSSY_COBBLESTONE).pattern("###").unlockedBy("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.END_STONE_BRICKS).pattern("###").unlockedBy("has_end_stone_bricks", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_SANDSTONE_SLAB, 6).define('#', (ItemLike)Blocks.SMOOTH_SANDSTONE).pattern("###").unlockedBy("has_smooth_sandstone", has((ItemLike)Blocks.SMOOTH_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOOTH_QUARTZ_SLAB, 6).define('#', (ItemLike)Blocks.SMOOTH_QUARTZ).pattern("###").unlockedBy("has_smooth_quartz", has((ItemLike)Blocks.SMOOTH_QUARTZ)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GRANITE_SLAB, 6).define('#', (ItemLike)Blocks.GRANITE).pattern("###").unlockedBy("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ANDESITE_SLAB, 6).define('#', (ItemLike)Blocks.ANDESITE).pattern("###").unlockedBy("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.RED_NETHER_BRICKS).pattern("###").unlockedBy("has_red_nether_bricks", has((ItemLike)Blocks.RED_NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_ANDESITE_SLAB, 6).define('#', (ItemLike)Blocks.POLISHED_ANDESITE).pattern("###").unlockedBy("has_polished_andesite", has((ItemLike)Blocks.POLISHED_ANDESITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DIORITE_SLAB, 6).define('#', (ItemLike)Blocks.DIORITE).pattern("###").unlockedBy("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BRICK_WALL, 6).define('#', (ItemLike)Blocks.BRICKS).pattern("###").pattern("###").unlockedBy("has_bricks", has((ItemLike)Blocks.BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.PRISMARINE_WALL, 6).define('#', (ItemLike)Blocks.PRISMARINE).pattern("###").pattern("###").unlockedBy("has_prismarine", has((ItemLike)Blocks.PRISMARINE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_SANDSTONE_WALL, 6).define('#', (ItemLike)Blocks.RED_SANDSTONE).pattern("###").pattern("###").unlockedBy("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.MOSSY_STONE_BRICK_WALL, 6).define('#', (ItemLike)Blocks.MOSSY_STONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GRANITE_WALL, 6).define('#', (ItemLike)Blocks.GRANITE).pattern("###").pattern("###").unlockedBy("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONE_BRICK_WALL, 6).define('#', (ItemLike)Blocks.STONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHER_BRICK_WALL, 6).define('#', (ItemLike)Blocks.NETHER_BRICKS).pattern("###").pattern("###").unlockedBy("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.ANDESITE_WALL, 6).define('#', (ItemLike)Blocks.ANDESITE).pattern("###").pattern("###").unlockedBy("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RED_NETHER_BRICK_WALL, 6).define('#', (ItemLike)Blocks.RED_NETHER_BRICKS).pattern("###").pattern("###").unlockedBy("has_red_nether_bricks", has((ItemLike)Blocks.RED_NETHER_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SANDSTONE_WALL, 6).define('#', (ItemLike)Blocks.SANDSTONE).pattern("###").pattern("###").unlockedBy("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.END_STONE_BRICK_WALL, 6).define('#', (ItemLike)Blocks.END_STONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_end_stone_bricks", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.DIORITE_WALL, 6).define('#', (ItemLike)Blocks.DIORITE).pattern("###").pattern("###").unlockedBy("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.CREEPER_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.CREEPER_HEAD).unlockedBy("has_creeper_head", has((ItemLike)Items.CREEPER_HEAD)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.SKULL_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.WITHER_SKELETON_SKULL).unlockedBy("has_wither_skeleton_skull", has((ItemLike)Items.WITHER_SKELETON_SKULL)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.FLOWER_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Blocks.OXEYE_DAISY).unlockedBy("has_oxeye_daisy", has((ItemLike)Blocks.OXEYE_DAISY)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.MOJANG_BANNER_PATTERN).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.ENCHANTED_GOLDEN_APPLE).unlockedBy("has_enchanted_golden_apple", has((ItemLike)Items.ENCHANTED_GOLDEN_APPLE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SCAFFOLDING, 6).define('~', (ItemLike)Items.STRING).define('I', (ItemLike)Blocks.BAMBOO).pattern("I~I").pattern("I I").pattern("I I").unlockedBy("has_bamboo", has((ItemLike)Blocks.BAMBOO)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.GRINDSTONE).define('I', (ItemLike)Items.STICK).define('-', (ItemLike)Blocks.STONE_SLAB).define('#', (Tag)ItemTags.PLANKS).pattern("I-I").pattern("# #").unlockedBy("has_stone_slab", has((ItemLike)Blocks.STONE_SLAB)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BLAST_FURNACE).define('#', (ItemLike)Blocks.SMOOTH_STONE).define('X', (ItemLike)Blocks.FURNACE).define('I', (ItemLike)Items.IRON_INGOT).pattern("III").pattern("IXI").pattern("###").unlockedBy("has_smooth_stone", has((ItemLike)Blocks.SMOOTH_STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMOKER).define('#', (Tag)ItemTags.LOGS).define('X', (ItemLike)Blocks.FURNACE).pattern(" # ").pattern("#X#").pattern(" # ").unlockedBy("has_furnace", has((ItemLike)Blocks.FURNACE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CARTOGRAPHY_TABLE).define('#', (Tag)ItemTags.PLANKS).define('@', (ItemLike)Items.PAPER).pattern("@@").pattern("##").pattern("##").unlockedBy("has_paper", has((ItemLike)Items.PAPER)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.SMITHING_TABLE).define('#', (Tag)ItemTags.PLANKS).define('@', (ItemLike)Items.IRON_INGOT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.FLETCHING_TABLE).define('#', (Tag)ItemTags.PLANKS).define('@', (ItemLike)Items.FLINT).pattern("@@").pattern("##").pattern("##").unlockedBy("has_flint", has((ItemLike)Items.FLINT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.STONECUTTER).define('I', (ItemLike)Items.IRON_INGOT).define('#', (ItemLike)Blocks.STONE).pattern(" I ").pattern("###").unlockedBy("has_stone", has((ItemLike)Blocks.STONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.LODESTONE).define('S', (ItemLike)Items.CHISELED_STONE_BRICKS).define('#', (ItemLike)Items.NETHERITE_INGOT).pattern("SSS").pattern("S#S").pattern("SSS").unlockedBy("has_netherite_ingot", has((ItemLike)Items.NETHERITE_INGOT)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.NETHERITE_BLOCK).define('#', (ItemLike)Items.NETHERITE_INGOT).pattern("###").pattern("###").pattern("###").unlockedBy("has_netherite_ingot", has((ItemLike)Items.NETHERITE_INGOT)).save(var0);
      ShapelessRecipeBuilder.shapeless(Items.NETHERITE_INGOT, 9).requires((ItemLike)Blocks.NETHERITE_BLOCK).group("netherite_ingot").unlockedBy("has_netherite_block", has((ItemLike)Blocks.NETHERITE_BLOCK)).save(var0, "netherite_ingot_from_netherite_block");
      ShapelessRecipeBuilder.shapeless(Items.NETHERITE_INGOT).requires((ItemLike)Items.NETHERITE_SCRAP, 4).requires((ItemLike)Items.GOLD_INGOT, 4).group("netherite_ingot").unlockedBy("has_netherite_scrap", has((ItemLike)Items.NETHERITE_SCRAP)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.RESPAWN_ANCHOR).define('O', (ItemLike)Blocks.CRYING_OBSIDIAN).define('G', (ItemLike)Blocks.GLOWSTONE).pattern("OOO").pattern("GGG").pattern("OOO").unlockedBy("has_obsidian", has((ItemLike)Blocks.CRYING_OBSIDIAN)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BLACKSTONE_STAIRS, 4).define('#', (ItemLike)Blocks.BLACKSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_STAIRS, 4).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, 4).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_polished_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BLACKSTONE_SLAB, 6).define('#', (ItemLike)Blocks.BLACKSTONE).pattern("###").unlockedBy("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_SLAB, 6).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE).pattern("###").unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 6).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS).pattern("###").unlockedBy("has_polished_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE, 4).define('S', (ItemLike)Blocks.BLACKSTONE).pattern("SS").pattern("SS").unlockedBy("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICKS, 4).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE).pattern("##").pattern("##").unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHISELED_POLISHED_BLACKSTONE).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE_SLAB).pattern("#").pattern("#").unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.BLACKSTONE_WALL, 6).define('#', (ItemLike)Blocks.BLACKSTONE).pattern("###").pattern("###").unlockedBy("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_WALL, 6).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE).pattern("###").pattern("###").unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, 6).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS).pattern("###").pattern("###").unlockedBy("has_polished_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0);
      ShapelessRecipeBuilder.shapeless(Blocks.POLISHED_BLACKSTONE_BUTTON).requires((ItemLike)Blocks.POLISHED_BLACKSTONE).unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE).define('#', (ItemLike)Blocks.POLISHED_BLACKSTONE).pattern("##").unlockedBy("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0);
      ShapedRecipeBuilder.shaped(Blocks.CHAIN).define('I', (ItemLike)Items.IRON_INGOT).define('N', (ItemLike)Items.IRON_NUGGET).pattern("N").pattern("I").pattern("N").unlockedBy("has_iron_nugget", has((ItemLike)Items.IRON_NUGGET)).unlockedBy("has_iron_ingot", has((ItemLike)Items.IRON_INGOT)).save(var0);
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
      SimpleCookingRecipeBuilder.smelting(Ingredient.of((Tag)ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15F, 200).unlockedBy("has_log", has((Tag)ItemTags.LOGS_THAT_BURN)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1F, 200).unlockedBy("has_chorus_fruit", has((ItemLike)Items.CHORUS_FRUIT)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 200).unlockedBy("has_coal_ore", has((ItemLike)Blocks.COAL_ORE)).save(var0, "coal_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35F, 200).unlockedBy("has_beef", has((ItemLike)Items.BEEF)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, 200).unlockedBy("has_chicken", has((ItemLike)Items.CHICKEN)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35F, 200).unlockedBy("has_cod", has((ItemLike)Items.COD)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1F, 200).unlockedBy("has_kelp", has((ItemLike)Blocks.KELP)).save(var0, "dried_kelp_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35F, 200).unlockedBy("has_salmon", has((ItemLike)Items.SALMON)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, 200).unlockedBy("has_mutton", has((ItemLike)Items.MUTTON)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, 200).unlockedBy("has_porkchop", has((ItemLike)Items.PORKCHOP)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, 200).unlockedBy("has_rabbit", has((ItemLike)Items.RABBIT)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 200).unlockedBy("has_diamond_ore", has((ItemLike)Blocks.DIAMOND_ORE)).save(var0, "diamond_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 200).unlockedBy("has_lapis_ore", has((ItemLike)Blocks.LAPIS_ORE)).save(var0, "lapis_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 200).unlockedBy("has_emerald_ore", has((ItemLike)Blocks.EMERALD_ORE)).save(var0, "emerald_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of((Tag)ItemTags.SAND), Blocks.GLASS.asItem(), 0.1F, 200).unlockedBy("has_sand", has((Tag)ItemTags.SAND)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of((Tag)ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0F, 200).unlockedBy("has_gold_ore", has((Tag)ItemTags.GOLD_ORES)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.SEA_PICKLE.asItem()), Items.LIME_DYE, 0.1F, 200).unlockedBy("has_sea_pickle", has((ItemLike)Blocks.SEA_PICKLE)).save(var0, "lime_dye_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0F, 200).unlockedBy("has_cactus", has((ItemLike)Blocks.CACTUS)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 200).unlockedBy("has_golden_pickaxe", has((ItemLike)Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", has((ItemLike)Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", has((ItemLike)Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", has((ItemLike)Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", has((ItemLike)Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", has((ItemLike)Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", has((ItemLike)Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", has((ItemLike)Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", has((ItemLike)Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", has((ItemLike)Items.GOLDEN_HORSE_ARMOR)).save(var0, "gold_nugget_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 200).unlockedBy("has_iron_pickaxe", has((ItemLike)Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", has((ItemLike)Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", has((ItemLike)Items.IRON_AXE)).unlockedBy("has_iron_hoe", has((ItemLike)Items.IRON_HOE)).unlockedBy("has_iron_sword", has((ItemLike)Items.IRON_SWORD)).unlockedBy("has_iron_helmet", has((ItemLike)Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", has((ItemLike)Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", has((ItemLike)Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", has((ItemLike)Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", has((ItemLike)Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", has((ItemLike)Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", has((ItemLike)Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", has((ItemLike)Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", has((ItemLike)Items.CHAINMAIL_BOOTS)).save(var0, "iron_nugget_from_smelting");
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 200).unlockedBy("has_iron_ore", has((ItemLike)Blocks.IRON_ORE.asItem())).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35F, 200).unlockedBy("has_clay_block", has((ItemLike)Blocks.CLAY)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1F, 200).unlockedBy("has_netherrack", has((ItemLike)Blocks.NETHERRACK)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 200).unlockedBy("has_nether_quartz_ore", has((ItemLike)Blocks.NETHER_QUARTZ_ORE)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 200).unlockedBy("has_redstone_ore", has((ItemLike)Blocks.REDSTONE_ORE)).save(var0, "redstone_from_smelting");
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
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem(), 0.1F, 200).unlockedBy("has_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0);
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS.asItem(), 0.1F, 200).unlockedBy("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0);
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 100).unlockedBy("has_iron_ore", has((ItemLike)Blocks.IRON_ORE.asItem())).save(var0, "iron_ingot_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of((Tag)ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0F, 100).unlockedBy("has_gold_ore", has((Tag)ItemTags.GOLD_ORES)).save(var0, "gold_ingot_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 100).unlockedBy("has_diamond_ore", has((ItemLike)Blocks.DIAMOND_ORE)).save(var0, "diamond_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 100).unlockedBy("has_lapis_ore", has((ItemLike)Blocks.LAPIS_ORE)).save(var0, "lapis_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 100).unlockedBy("has_redstone_ore", has((ItemLike)Blocks.REDSTONE_ORE)).save(var0, "redstone_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 100).unlockedBy("has_coal_ore", has((ItemLike)Blocks.COAL_ORE)).save(var0, "coal_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 100).unlockedBy("has_emerald_ore", has((ItemLike)Blocks.EMERALD_ORE)).save(var0, "emerald_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 100).unlockedBy("has_nether_quartz_ore", has((ItemLike)Blocks.NETHER_QUARTZ_ORE)).save(var0, "quartz_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1F, 100).unlockedBy("has_golden_pickaxe", has((ItemLike)Items.GOLDEN_PICKAXE)).unlockedBy("has_golden_shovel", has((ItemLike)Items.GOLDEN_SHOVEL)).unlockedBy("has_golden_axe", has((ItemLike)Items.GOLDEN_AXE)).unlockedBy("has_golden_hoe", has((ItemLike)Items.GOLDEN_HOE)).unlockedBy("has_golden_sword", has((ItemLike)Items.GOLDEN_SWORD)).unlockedBy("has_golden_helmet", has((ItemLike)Items.GOLDEN_HELMET)).unlockedBy("has_golden_chestplate", has((ItemLike)Items.GOLDEN_CHESTPLATE)).unlockedBy("has_golden_leggings", has((ItemLike)Items.GOLDEN_LEGGINGS)).unlockedBy("has_golden_boots", has((ItemLike)Items.GOLDEN_BOOTS)).unlockedBy("has_golden_horse_armor", has((ItemLike)Items.GOLDEN_HORSE_ARMOR)).save(var0, "gold_nugget_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1F, 100).unlockedBy("has_iron_pickaxe", has((ItemLike)Items.IRON_PICKAXE)).unlockedBy("has_iron_shovel", has((ItemLike)Items.IRON_SHOVEL)).unlockedBy("has_iron_axe", has((ItemLike)Items.IRON_AXE)).unlockedBy("has_iron_hoe", has((ItemLike)Items.IRON_HOE)).unlockedBy("has_iron_sword", has((ItemLike)Items.IRON_SWORD)).unlockedBy("has_iron_helmet", has((ItemLike)Items.IRON_HELMET)).unlockedBy("has_iron_chestplate", has((ItemLike)Items.IRON_CHESTPLATE)).unlockedBy("has_iron_leggings", has((ItemLike)Items.IRON_LEGGINGS)).unlockedBy("has_iron_boots", has((ItemLike)Items.IRON_BOOTS)).unlockedBy("has_iron_horse_armor", has((ItemLike)Items.IRON_HORSE_ARMOR)).unlockedBy("has_chainmail_helmet", has((ItemLike)Items.CHAINMAIL_HELMET)).unlockedBy("has_chainmail_chestplate", has((ItemLike)Items.CHAINMAIL_CHESTPLATE)).unlockedBy("has_chainmail_leggings", has((ItemLike)Items.CHAINMAIL_LEGGINGS)).unlockedBy("has_chainmail_boots", has((ItemLike)Items.CHAINMAIL_BOOTS)).save(var0, "iron_nugget_from_blasting");
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 100).unlockedBy("has_ancient_debris", has((ItemLike)Blocks.ANCIENT_DEBRIS)).save(var0, "netherite_scrap_from_blasting");
      cookRecipes(var0, "smoking", RecipeSerializer.SMOKING_RECIPE, 100);
      cookRecipes(var0, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, 600);
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_SLAB, 2).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_slab_from_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_STAIRS).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_stairs_from_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICKS).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_bricks_from_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_SLAB, 2).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_brick_slab_from_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_STAIRS).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_brick_stairs_from_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "chiseled_stone_bricks_stone_from_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE), Blocks.STONE_BRICK_WALL).unlocks("has_stone", has((ItemLike)Blocks.STONE)).save(var0, "stone_brick_walls_from_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE).unlocks("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "cut_sandstone_from_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.SANDSTONE_SLAB, 2).unlocks("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "sandstone_slab_from_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).unlocks("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "cut_sandstone_slab_from_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.CUT_SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).unlocks("has_cut_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.SANDSTONE_STAIRS).unlocks("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "sandstone_stairs_from_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.SANDSTONE_WALL).unlocks("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "sandstone_wall_from_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SANDSTONE), Blocks.CHISELED_SANDSTONE).unlocks("has_sandstone", has((ItemLike)Blocks.SANDSTONE)).save(var0, "chiseled_sandstone_from_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE).unlocks("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "cut_red_sandstone_from_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_SLAB, 2).unlocks("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "red_sandstone_slab_from_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).unlocks("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.CUT_RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).unlocks("has_cut_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_STAIRS).unlocks("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "red_sandstone_stairs_from_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_WALL).unlocks("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "red_sandstone_wall_from_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_SANDSTONE), Blocks.CHISELED_RED_SANDSTONE).unlocks("has_red_sandstone", has((ItemLike)Blocks.RED_SANDSTONE)).save(var0, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).unlocks("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0, "quartz_slab_from_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_STAIRS).unlocks("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0, "quartz_stairs_from_quartz_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_PILLAR).unlocks("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0, "quartz_pillar_from_quartz_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.CHISELED_QUARTZ_BLOCK).unlocks("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0, "chiseled_quartz_block_from_quartz_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BRICKS).unlocks("has_quartz_block", has((ItemLike)Blocks.QUARTZ_BLOCK)).save(var0, "quartz_bricks_from_quartz_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), Blocks.COBBLESTONE_STAIRS).unlocks("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0, "cobblestone_stairs_from_cobblestone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), Blocks.COBBLESTONE_SLAB, 2).unlocks("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0, "cobblestone_slab_from_cobblestone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.COBBLESTONE), Blocks.COBBLESTONE_WALL).unlocks("has_cobblestone", has((ItemLike)Blocks.COBBLESTONE)).save(var0, "cobblestone_wall_from_cobblestone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_SLAB, 2).unlocks("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0, "stone_brick_slab_from_stone_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_STAIRS).unlocks("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0, "stone_brick_stairs_from_stone_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).unlocks("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0, "stone_brick_wall_from_stone_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.STONE_BRICKS), Blocks.CHISELED_STONE_BRICKS).unlocks("has_stone_bricks", has((ItemLike)Blocks.STONE_BRICKS)).save(var0, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), Blocks.BRICK_SLAB, 2).unlocks("has_bricks", has((ItemLike)Blocks.BRICKS)).save(var0, "brick_slab_from_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), Blocks.BRICK_STAIRS).unlocks("has_bricks", has((ItemLike)Blocks.BRICKS)).save(var0, "brick_stairs_from_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BRICKS), Blocks.BRICK_WALL).unlocks("has_bricks", has((ItemLike)Blocks.BRICKS)).save(var0, "brick_wall_from_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB, 2).unlocks("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0, "nether_brick_slab_from_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS).unlocks("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0, "nether_brick_stairs_from_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL).unlocks("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0, "nether_brick_wall_from_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS).unlocks("has_nether_bricks", has((ItemLike)Blocks.NETHER_BRICKS)).save(var0, "chiseled_nether_bricks_from_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB, 2).unlocks("has_nether_bricks", has((ItemLike)Blocks.RED_NETHER_BRICKS)).save(var0, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS).unlocks("has_nether_bricks", has((ItemLike)Blocks.RED_NETHER_BRICKS)).save(var0, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL).unlocks("has_nether_bricks", has((ItemLike)Blocks.RED_NETHER_BRICKS)).save(var0, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PURPUR_BLOCK), Blocks.PURPUR_SLAB, 2).unlocks("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0, "purpur_slab_from_purpur_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PURPUR_BLOCK), Blocks.PURPUR_STAIRS).unlocks("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0, "purpur_stairs_from_purpur_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PURPUR_BLOCK), Blocks.PURPUR_PILLAR).unlocks("has_purpur_block", has((ItemLike)Blocks.PURPUR_BLOCK)).save(var0, "purpur_pillar_from_purpur_block_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE), Blocks.PRISMARINE_SLAB, 2).unlocks("has_prismarine", has((ItemLike)Blocks.PRISMARINE)).save(var0, "prismarine_slab_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE), Blocks.PRISMARINE_STAIRS).unlocks("has_prismarine", has((ItemLike)Blocks.PRISMARINE)).save(var0, "prismarine_stairs_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE), Blocks.PRISMARINE_WALL).unlocks("has_prismarine", has((ItemLike)Blocks.PRISMARINE)).save(var0, "prismarine_wall_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).unlocks("has_prismarine_brick", has((ItemLike)Blocks.PRISMARINE_BRICKS)).save(var0, "prismarine_brick_slab_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).unlocks("has_prismarine_brick", has((ItemLike)Blocks.PRISMARINE_BRICKS)).save(var0, "prismarine_brick_stairs_from_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_SLAB, 2).unlocks("has_dark_prismarine", has((ItemLike)Blocks.DARK_PRISMARINE)).save(var0, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_STAIRS).unlocks("has_dark_prismarine", has((ItemLike)Blocks.DARK_PRISMARINE)).save(var0, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.ANDESITE_SLAB, 2).unlocks("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0, "andesite_slab_from_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.ANDESITE_STAIRS).unlocks("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0, "andesite_stairs_from_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.ANDESITE_WALL).unlocks("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0, "andesite_wall_from_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE).unlocks("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0, "polished_andesite_from_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).unlocks("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0, "polished_andesite_slab_from_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).unlocks("has_andesite", has((ItemLike)Blocks.ANDESITE)).save(var0, "polished_andesite_stairs_from_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).unlocks("has_polished_andesite", has((ItemLike)Blocks.POLISHED_ANDESITE)).save(var0, "polished_andesite_slab_from_polished_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).unlocks("has_polished_andesite", has((ItemLike)Blocks.POLISHED_ANDESITE)).save(var0, "polished_andesite_stairs_from_polished_andesite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BASALT), Blocks.POLISHED_BASALT).unlocks("has_basalt", has((ItemLike)Blocks.BASALT)).save(var0, "polished_basalt_from_basalt_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.GRANITE_SLAB, 2).unlocks("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0, "granite_slab_from_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.GRANITE_STAIRS).unlocks("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0, "granite_stairs_from_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.GRANITE_WALL).unlocks("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0, "granite_wall_from_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.POLISHED_GRANITE).unlocks("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0, "polished_granite_from_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).unlocks("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0, "polished_granite_slab_from_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.GRANITE), Blocks.POLISHED_GRANITE_STAIRS).unlocks("has_granite", has((ItemLike)Blocks.GRANITE)).save(var0, "polished_granite_stairs_from_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).unlocks("has_polished_granite", has((ItemLike)Blocks.POLISHED_GRANITE)).save(var0, "polished_granite_slab_from_polished_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_STAIRS).unlocks("has_polished_granite", has((ItemLike)Blocks.POLISHED_GRANITE)).save(var0, "polished_granite_stairs_from_polished_granite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.DIORITE_SLAB, 2).unlocks("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0, "diorite_slab_from_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.DIORITE_STAIRS).unlocks("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0, "diorite_stairs_from_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.DIORITE_WALL).unlocks("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0, "diorite_wall_from_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.POLISHED_DIORITE).unlocks("has_diorite", has((ItemLike)Blocks.DIORITE)).save(var0, "polished_diorite_from_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).unlocks("has_diorite", has((ItemLike)Blocks.POLISHED_DIORITE)).save(var0, "polished_diorite_slab_from_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.DIORITE), Blocks.POLISHED_DIORITE_STAIRS).unlocks("has_diorite", has((ItemLike)Blocks.POLISHED_DIORITE)).save(var0, "polished_diorite_stairs_from_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).unlocks("has_polished_diorite", has((ItemLike)Blocks.POLISHED_DIORITE)).save(var0, "polished_diorite_slab_from_polished_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_STAIRS).unlocks("has_polished_diorite", has((ItemLike)Blocks.POLISHED_DIORITE)).save(var0, "polished_diorite_stairs_from_polished_diorite_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).unlocks("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).unlocks("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).unlocks("has_mossy_stone_bricks", has((ItemLike)Blocks.MOSSY_STONE_BRICKS)).save(var0, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_SLAB, 2).unlocks("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_STAIRS).unlocks("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_WALL).unlocks("has_mossy_cobblestone", has((ItemLike)Blocks.MOSSY_COBBLESTONE)).save(var0, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_SLAB, 2).unlocks("has_smooth_sandstone", has((ItemLike)Blocks.SMOOTH_SANDSTONE)).save(var0, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_STAIRS).unlocks("has_mossy_cobblestone", has((ItemLike)Blocks.SMOOTH_SANDSTONE)).save(var0, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_SLAB, 2).unlocks("has_smooth_red_sandstone", has((ItemLike)Blocks.SMOOTH_RED_SANDSTONE)).save(var0, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS).unlocks("has_smooth_red_sandstone", has((ItemLike)Blocks.SMOOTH_RED_SANDSTONE)).save(var0, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB, 2).unlocks("has_smooth_quartz", has((ItemLike)Blocks.SMOOTH_QUARTZ)).save(var0, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS).unlocks("has_smooth_quartz", has((ItemLike)Blocks.SMOOTH_QUARTZ)).save(var0, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).unlocks("has_end_stone_brick", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).unlocks("has_end_stone_brick", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).unlocks("has_end_stone_brick", has((ItemLike)Blocks.END_STONE_BRICKS)).save(var0, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICKS).unlocks("has_end_stone", has((ItemLike)Blocks.END_STONE)).save(var0, "end_stone_bricks_from_end_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICK_SLAB, 2).unlocks("has_end_stone", has((ItemLike)Blocks.END_STONE)).save(var0, "end_stone_brick_slab_from_end_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICK_STAIRS).unlocks("has_end_stone", has((ItemLike)Blocks.END_STONE)).save(var0, "end_stone_brick_stairs_from_end_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.END_STONE), Blocks.END_STONE_BRICK_WALL).unlocks("has_end_stone", has((ItemLike)Blocks.END_STONE)).save(var0, "end_stone_brick_wall_from_end_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_STONE), Blocks.SMOOTH_STONE_SLAB, 2).unlocks("has_smooth_stone", has((ItemLike)Blocks.SMOOTH_STONE)).save(var0, "smooth_stone_slab_from_smooth_stone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.BLACKSTONE_SLAB, 2).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "blackstone_slab_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.BLACKSTONE_STAIRS).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "blackstone_stairs_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.BLACKSTONE_WALL).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "blackstone_wall_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_wall_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_slab_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_stairs_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "chiseled_polished_blackstone_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_bricks_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_brick_slab_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_brick_stairs_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).unlocks("has_blackstone", has((ItemLike)Blocks.BLACKSTONE)).save(var0, "polished_blackstone_brick_wall_from_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_slab_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_stairs_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_bricks_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_wall_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_brick_slab_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_brick_stairs_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "polished_blackstone_brick_wall_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).unlocks("has_polished_blackstone", has((ItemLike)Blocks.POLISHED_BLACKSTONE)).save(var0, "chiseled_polished_blackstone_from_polished_blackstone_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).unlocks("has_polished_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0, "polished_blackstone_brick_slab_from_polished_blackstone_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).unlocks("has_polished_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0, "polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting");
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).unlocks("has_polished_blackstone_bricks", has((ItemLike)Blocks.POLISHED_BLACKSTONE_BRICKS)).save(var0, "polished_blackstone_brick_wall_from_polished_blackstone_bricks_stonecutting");
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

   private static void netheriteSmithing(Consumer<FinishedRecipe> var0, Item var1, Item var2) {
      UpgradeRecipeBuilder.smithing(Ingredient.of(var1), Ingredient.of(Items.NETHERITE_INGOT), var2).unlocks("has_netherite_ingot", has((ItemLike)Items.NETHERITE_INGOT)).save(var0, Registry.ITEM.getKey(var2.asItem()).getPath() + "_smithing");
   }

   private static void planksFromLog(Consumer<FinishedRecipe> var0, ItemLike var1, Tag<Item> var2) {
      ShapelessRecipeBuilder.shapeless(var1, 4).requires(var2).group("planks").unlockedBy("has_log", has(var2)).save(var0);
   }

   private static void planksFromLogs(Consumer<FinishedRecipe> var0, ItemLike var1, Tag<Item> var2) {
      ShapelessRecipeBuilder.shapeless(var1, 4).requires(var2).group("planks").unlockedBy("has_logs", has(var2)).save(var0);
   }

   private static void woodFromLogs(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 3).define('#', var2).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(var2)).save(var0);
   }

   private static void woodenBoat(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1).define('#', var2).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(var0);
   }

   private static void woodenButton(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1).requires(var2).group("wooden_button").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenDoor(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 3).define('#', var2).pattern("##").pattern("##").pattern("##").group("wooden_door").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenFence(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 3).define('#', (ItemLike)Items.STICK).define('W', var2).pattern("W#W").pattern("W#W").group("wooden_fence").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenFenceGate(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1).define('#', (ItemLike)Items.STICK).define('W', var2).pattern("#W#").pattern("#W#").group("wooden_fence_gate").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenPressurePlate(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1).define('#', var2).pattern("##").group("wooden_pressure_plate").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenSlab(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 6).define('#', var2).pattern("###").group("wooden_slab").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenStairs(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 4).define('#', var2).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenTrapdoor(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 2).define('#', var2).pattern("###").pattern("###").group("wooden_trapdoor").unlockedBy("has_planks", has(var2)).save(var0);
   }

   private static void woodenSign(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var2.asItem()).getPath();
      ShapedRecipeBuilder.shaped(var1, 3).group("sign").define('#', var2).define('X', (ItemLike)Items.STICK).pattern("###").pattern("###").pattern(" X ").unlockedBy("has_" + var3, has(var2)).save(var0);
   }

   private static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1).requires(var2).requires((ItemLike)Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", has((ItemLike)Blocks.WHITE_WOOL)).save(var0);
   }

   private static void carpetFromWool(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var2.asItem()).getPath();
      ShapedRecipeBuilder.shaped(var1, 3).define('#', var2).pattern("##").group("carpet").unlockedBy("has_" + var3, has(var2)).save(var0);
   }

   private static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var1.asItem()).getPath();
      String var4 = Registry.ITEM.getKey(var2.asItem()).getPath();
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.WHITE_CARPET).define('$', var2).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", has((ItemLike)Blocks.WHITE_CARPET)).unlockedBy("has_" + var4, has(var2)).save(var0, var3 + "_from_white_carpet");
   }

   private static void bedFromPlanksAndWool(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var2.asItem()).getPath();
      ShapedRecipeBuilder.shaped(var1).define('#', var2).define('X', (Tag)ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy("has_" + var3, has(var2)).save(var0);
   }

   private static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var1.asItem()).getPath();
      ShapelessRecipeBuilder.shapeless(var1).requires((ItemLike)Items.WHITE_BED).requires(var2).group("dyed_bed").unlockedBy("has_bed", has((ItemLike)Items.WHITE_BED)).save(var0, var3 + "_from_white_bed");
   }

   private static void banner(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var2.asItem()).getPath();
      ShapedRecipeBuilder.shaped(var1).define('#', var2).define('|', (ItemLike)Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy("has_" + var3, has(var2)).save(var0);
   }

   private static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.GLASS).define('X', var2).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
   }

   private static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 16).define('#', var2).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(var2)).save(var0);
   }

   private static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      String var3 = Registry.ITEM.getKey(var1.asItem()).getPath();
      String var4 = Registry.ITEM.getKey(var2.asItem()).getPath();
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.GLASS_PANE).define('$', var2).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has((ItemLike)Blocks.GLASS_PANE)).unlockedBy("has_" + var4, has(var2)).save(var0, var3 + "_from_glass_pane");
   }

   private static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(var1, 8).define('#', (ItemLike)Blocks.TERRACOTTA).define('X', var2).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has((ItemLike)Blocks.TERRACOTTA)).save(var0);
   }

   private static void concretePowder(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(var1, 8).requires(var2).requires((ItemLike)Blocks.SAND, 4).requires((ItemLike)Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has((ItemLike)Blocks.SAND)).unlockedBy("has_gravel", has((ItemLike)Blocks.GRAVEL)).save(var0);
   }

   private static void cookRecipes(Consumer<FinishedRecipe> var0, String var1, SimpleCookingSerializer<?> var2, int var3) {
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35F, var3, var2).unlockedBy("has_beef", has((ItemLike)Items.BEEF)).save(var0, "cooked_beef_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, var3, var2).unlockedBy("has_chicken", has((ItemLike)Items.CHICKEN)).save(var0, "cooked_chicken_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35F, var3, var2).unlockedBy("has_cod", has((ItemLike)Items.COD)).save(var0, "cooked_cod_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1F, var3, var2).unlockedBy("has_kelp", has((ItemLike)Blocks.KELP)).save(var0, "dried_kelp_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35F, var3, var2).unlockedBy("has_salmon", has((ItemLike)Items.SALMON)).save(var0, "cooked_salmon_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, var3, var2).unlockedBy("has_mutton", has((ItemLike)Items.MUTTON)).save(var0, "cooked_mutton_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, var3, var2).unlockedBy("has_porkchop", has((ItemLike)Items.PORKCHOP)).save(var0, "cooked_porkchop_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.POTATO), Items.BAKED_POTATO, 0.35F, var3, var2).unlockedBy("has_potato", has((ItemLike)Items.POTATO)).save(var0, "baked_potato_from_" + var1);
      SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, var3, var2).unlockedBy("has_rabbit", has((ItemLike)Items.RABBIT)).save(var0, "cooked_rabbit_from_" + var1);
   }

   private static EnterBlockTrigger.TriggerInstance insideOf(Block var0) {
      return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, StatePropertiesPredicate.ANY);
   }

   private static InventoryChangeTrigger.TriggerInstance has(ItemLike var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0).build());
   }

   private static InventoryChangeTrigger.TriggerInstance has(Tag<Item> var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0).build());
   }

   private static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... var0) {
      return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, var0);
   }

   public String getName() {
      return "Recipes";
   }
}
