package net.minecraft.data.recipes;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public abstract class RecipeProvider implements DataProvider {
   private final PackOutput.PathProvider recipePathProvider;
   private final PackOutput.PathProvider advancementPathProvider;
   private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.builder()
      .put(BlockFamily.Variant.BUTTON, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> buttonBuilder(var0, Ingredient.of(var1)))
      .put(
         BlockFamily.Variant.CHISELED,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1))
      )
      .put(
         BlockFamily.Variant.CUT,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1))
      )
      .put(BlockFamily.Variant.DOOR, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> doorBuilder(var0, Ingredient.of(var1)))
      .put(BlockFamily.Variant.CUSTOM_FENCE, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> fenceBuilder(var0, Ingredient.of(var1)))
      .put(BlockFamily.Variant.FENCE, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> fenceBuilder(var0, Ingredient.of(var1)))
      .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> fenceGateBuilder(var0, Ingredient.of(var1)))
      .put(BlockFamily.Variant.FENCE_GATE, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> fenceGateBuilder(var0, Ingredient.of(var1)))
      .put(BlockFamily.Variant.SIGN, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> signBuilder(var0, Ingredient.of(var1)))
      .put(
         BlockFamily.Variant.SLAB,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1))
      )
      .put(BlockFamily.Variant.STAIRS, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> stairBuilder(var0, Ingredient.of(var1)))
      .put(
         BlockFamily.Variant.PRESSURE_PLATE,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> pressurePlateBuilder(RecipeCategory.REDSTONE, var0, Ingredient.of(var1))
      )
      .put(
         BlockFamily.Variant.POLISHED,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1))
      )
      .put(BlockFamily.Variant.TRAPDOOR, (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> trapdoorBuilder(var0, Ingredient.of(var1)))
      .put(
         BlockFamily.Variant.WALL,
         (BiFunction<ItemLike, ItemLike, RecipeBuilder>)(var0, var1) -> wallBuilder(RecipeCategory.DECORATIONS, var0, Ingredient.of(var1))
      )
      .build();

   public RecipeProvider(PackOutput var1) {
      super();
      this.recipePathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
      this.advancementPathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      HashSet var2 = Sets.newHashSet();
      ArrayList var3 = new ArrayList();
      this.buildRecipes(var4 -> {
         if (!var2.add(var4.getId())) {
            throw new IllegalStateException("Duplicate recipe " + var4.getId());
         } else {
            var3.add(DataProvider.saveStable(var1, var4.serializeRecipe(), this.recipePathProvider.json(var4.getId())));
            JsonObject var5 = var4.serializeAdvancement();
            if (var5 != null) {
               var3.add(DataProvider.saveStable(var1, var5, this.advancementPathProvider.json(var4.getAdvancementId())));
            }
         }
      });
      return CompletableFuture.allOf(var3.toArray(var0 -> new CompletableFuture[var0]));
   }

   protected CompletableFuture<?> buildAdvancement(CachedOutput var1, ResourceLocation var2, Advancement.Builder var3) {
      return DataProvider.saveStable(var1, var3.serializeToJson(), this.advancementPathProvider.json(var2));
   }

   protected abstract void buildRecipes(Consumer<FinishedRecipe> var1);

   protected static void generateForEnabledBlockFamilies(Consumer<FinishedRecipe> var0, FeatureFlagSet var1) {
      BlockFamilies.getAllFamilies().filter(var1x -> var1x.shouldGenerateRecipe(var1)).forEach(var1x -> generateRecipes(var0, var1x));
   }

   protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, @Nullable String var3) {
      oneToOneConversionRecipe(var0, var1, var2, var3, 1);
   }

   protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2, @Nullable String var3, int var4) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, var1, var4)
         .requires(var2)
         .group(var3)
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0, getConversionRecipeName(var1, var2));
   }

   protected static void oreSmelting(Consumer<FinishedRecipe> var0, List<ItemLike> var1, RecipeCategory var2, ItemLike var3, float var4, int var5, String var6) {
      oreCooking(var0, RecipeSerializer.SMELTING_RECIPE, var1, var2, var3, var4, var5, var6, "_from_smelting");
   }

   protected static void oreBlasting(Consumer<FinishedRecipe> var0, List<ItemLike> var1, RecipeCategory var2, ItemLike var3, float var4, int var5, String var6) {
      oreCooking(var0, RecipeSerializer.BLASTING_RECIPE, var1, var2, var3, var4, var5, var6, "_from_blasting");
   }

   private static void oreCooking(
      Consumer<FinishedRecipe> var0,
      RecipeSerializer<? extends AbstractCookingRecipe> var1,
      List<ItemLike> var2,
      RecipeCategory var3,
      ItemLike var4,
      float var5,
      int var6,
      String var7,
      String var8
   ) {
      for(ItemLike var10 : var2) {
         SimpleCookingRecipeBuilder.generic(Ingredient.of(var10), var3, var4, var5, var6, var1)
            .group(var7)
            .unlockedBy(getHasName(var10), has(var10))
            .save(var0, getItemName(var4) + var8 + "_" + getItemName(var10));
      }
   }

   @Deprecated
   protected static void legacyNetheriteSmithing(Consumer<FinishedRecipe> var0, Item var1, RecipeCategory var2, Item var3) {
      LegacyUpgradeRecipeBuilder.smithing(Ingredient.of(var1), Ingredient.of(Items.NETHERITE_INGOT), var2, var3)
         .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
         .save(var0, getItemName(var3) + "_smithing");
   }

   protected static void netheriteSmithing(Consumer<FinishedRecipe> var0, Item var1, RecipeCategory var2, Item var3) {
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(var1), Ingredient.of(Items.NETHERITE_INGOT), var2, var3
         )
         .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
         .save(var0, getItemName(var3) + "_smithing");
   }

   protected static void trimSmithing(Consumer<FinishedRecipe> var0, Item var1) {
      SmithingTrimRecipeBuilder.smithingTrim(
            Ingredient.of(var1), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC
         )
         .unlocks("has_smithing_trim_template", has(var1))
         .save(var0, getItemName(var1) + "_smithing_trim");
   }

   protected static void twoByTwoPacker(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      ShapedRecipeBuilder.shaped(var1, var2, 1).define('#', var3).pattern("##").pattern("##").unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static void threeByThreePacker(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3, String var4) {
      ShapelessRecipeBuilder.shapeless(var1, var2).requires(var3, 9).unlockedBy(var4, has(var3)).save(var0);
   }

   protected static void threeByThreePacker(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      threeByThreePacker(var0, var1, var2, var3, getHasName(var3));
   }

   protected static void planksFromLog(Consumer<FinishedRecipe> var0, ItemLike var1, TagKey<Item> var2, int var3) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, var3).requires(var2).group("planks").unlockedBy("has_log", has(var2)).save(var0);
   }

   protected static void planksFromLogs(Consumer<FinishedRecipe> var0, ItemLike var1, TagKey<Item> var2, int var3) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, var3).requires(var2).group("planks").unlockedBy("has_logs", has(var2)).save(var0);
   }

   protected static void woodFromLogs(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 3)
         .define('#', var2)
         .pattern("##")
         .pattern("##")
         .group("bark")
         .unlockedBy("has_log", has(var2))
         .save(var0);
   }

   protected static void woodenBoat(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, var1)
         .define('#', var2)
         .pattern("# #")
         .pattern("###")
         .group("boat")
         .unlockedBy("in_water", insideOf(Blocks.WATER))
         .save(var0);
   }

   protected static void chestBoat(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, var1)
         .requires(Blocks.CHEST)
         .requires(var2)
         .group("chest_boat")
         .unlockedBy("has_boat", has(ItemTags.BOATS))
         .save(var0);
   }

   private static RecipeBuilder buttonBuilder(ItemLike var0, Ingredient var1) {
      return ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, var0).requires(var1);
   }

   protected static RecipeBuilder doorBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, var0, 3).define('#', var1).pattern("##").pattern("##").pattern("##");
   }

   private static RecipeBuilder fenceBuilder(ItemLike var0, Ingredient var1) {
      int var2 = var0 == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
      Item var3 = var0 == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var0, var2).define('W', var1).define('#', var3).pattern("W#W").pattern("W#W");
   }

   private static RecipeBuilder fenceGateBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, var0).define('#', Items.STICK).define('W', var1).pattern("#W#").pattern("#W#");
   }

   protected static void pressurePlate(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      pressurePlateBuilder(RecipeCategory.REDSTONE, var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static RecipeBuilder pressurePlateBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1).define('#', var2).pattern("##");
   }

   protected static void slab(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      slabBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static RecipeBuilder slabBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 6).define('#', var2).pattern("###");
   }

   protected static RecipeBuilder stairBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var0, 4).define('#', var1).pattern("#  ").pattern("## ").pattern("###");
   }

   private static RecipeBuilder trapdoorBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, var0, 2).define('#', var1).pattern("###").pattern("###");
   }

   private static RecipeBuilder signBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var0, 3)
         .group("sign")
         .define('#', var1)
         .define('X', Items.STICK)
         .pattern("###")
         .pattern("###")
         .pattern(" X ");
   }

   protected static void hangingSign(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 6)
         .group("hanging_sign")
         .define('#', var2)
         .define('X', Items.CHAIN)
         .pattern("X X")
         .pattern("###")
         .pattern("###")
         .unlockedBy("has_stripped_logs", has(var2))
         .save(var0);
   }

   protected static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1)
         .requires(var2)
         .requires(Blocks.WHITE_WOOL)
         .group("wool")
         .unlockedBy("has_white_wool", has(Blocks.WHITE_WOOL))
         .save(var0);
   }

   protected static void carpet(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 3)
         .define('#', var2)
         .pattern("##")
         .group("carpet")
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0);
   }

   protected static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 8)
         .define('#', Blocks.WHITE_CARPET)
         .define('$', var2)
         .pattern("###")
         .pattern("#$#")
         .pattern("###")
         .group("carpet")
         .unlockedBy("has_white_carpet", has(Blocks.WHITE_CARPET))
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0, getConversionRecipeName(var1, Blocks.WHITE_CARPET));
   }

   protected static void bedFromPlanksAndWool(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1)
         .define('#', var2)
         .define('X', ItemTags.PLANKS)
         .pattern("###")
         .pattern("XXX")
         .group("bed")
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0);
   }

   protected static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, var1)
         .requires(Items.WHITE_BED)
         .requires(var2)
         .group("dyed_bed")
         .unlockedBy("has_bed", has(Items.WHITE_BED))
         .save(var0, getConversionRecipeName(var1, Items.WHITE_BED));
   }

   protected static void banner(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1)
         .define('#', var2)
         .define('|', Items.STICK)
         .pattern("###")
         .pattern("###")
         .pattern(" | ")
         .group("banner")
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0);
   }

   protected static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 8)
         .define('#', Blocks.GLASS)
         .define('X', var2)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .group("stained_glass")
         .unlockedBy("has_glass", has(Blocks.GLASS))
         .save(var0);
   }

   protected static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 16)
         .define('#', var2)
         .pattern("###")
         .pattern("###")
         .group("stained_glass_pane")
         .unlockedBy("has_glass", has(var2))
         .save(var0);
   }

   protected static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 8)
         .define('#', Blocks.GLASS_PANE)
         .define('$', var2)
         .pattern("###")
         .pattern("#$#")
         .pattern("###")
         .group("stained_glass_pane")
         .unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE))
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0, getConversionRecipeName(var1, Blocks.GLASS_PANE));
   }

   protected static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 8)
         .define('#', Blocks.TERRACOTTA)
         .define('X', var2)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .group("stained_terracotta")
         .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
         .save(var0);
   }

   protected static void concretePowder(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, 8)
         .requires(var2)
         .requires(Blocks.SAND, 4)
         .requires(Blocks.GRAVEL, 4)
         .group("concrete_powder")
         .unlockedBy("has_sand", has(Blocks.SAND))
         .unlockedBy("has_gravel", has(Blocks.GRAVEL))
         .save(var0);
   }

   protected static void candle(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, var1)
         .requires(Blocks.CANDLE)
         .requires(var2)
         .group("dyed_candle")
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0);
   }

   protected static void wall(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      wallBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   private static RecipeBuilder wallBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 6).define('#', var2).pattern("###").pattern("###");
   }

   protected static void polished(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      polishedBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   private static RecipeBuilder polishedBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 4).define('S', var2).pattern("SS").pattern("SS");
   }

   protected static void cut(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      cutBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   private static ShapedRecipeBuilder cutBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 4).define('#', var2).pattern("##").pattern("##");
   }

   protected static void chiseled(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      chiseledBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static void mosaicBuilder(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      ShapedRecipeBuilder.shaped(var1, var2).define('#', var3).pattern("#").pattern("#").unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static ShapedRecipeBuilder chiseledBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1).define('#', var2).pattern("#").pattern("#");
   }

   protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      stonecutterResultFromBase(var0, var1, var2, var3, 1);
   }

   protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, ItemLike var3, int var4) {
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(var3), var1, var2, var4)
         .unlockedBy(getHasName(var3), has(var3))
         .save(var0, getConversionRecipeName(var2, var3) + "_stonecutting");
   }

   private static void smeltingResultFromBase(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(var2), RecipeCategory.BUILDING_BLOCKS, var1, 0.1F, 200)
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0);
   }

   protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, getSimpleRecipeName(var4), null, getSimpleRecipeName(var2), null);
   }

   protected static void nineBlockStorageRecipesWithCustomPacking(
      Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, String var6
   ) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, var5, var6, getSimpleRecipeName(var2), null);
   }

   protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(
      Consumer<FinishedRecipe> var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, String var6
   ) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, getSimpleRecipeName(var4), null, var5, var6);
   }

   private static void nineBlockStorageRecipes(
      Consumer<FinishedRecipe> var0,
      RecipeCategory var1,
      ItemLike var2,
      RecipeCategory var3,
      ItemLike var4,
      String var5,
      @Nullable String var6,
      String var7,
      @Nullable String var8
   ) {
      ShapelessRecipeBuilder.shapeless(var1, var2, 9)
         .requires(var4)
         .group(var8)
         .unlockedBy(getHasName(var4), has(var4))
         .save(var0, new ResourceLocation(var7));
      ShapedRecipeBuilder.shaped(var3, var4)
         .define('#', var2)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .group(var6)
         .unlockedBy(getHasName(var2), has(var2))
         .save(var0, new ResourceLocation(var5));
   }

   protected static void copySmithingTemplate(Consumer<FinishedRecipe> var0, ItemLike var1, TagKey<Item> var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, var1, 2)
         .define('#', Items.DIAMOND)
         .define('C', var2)
         .define('S', var1)
         .pattern("#S#")
         .pattern("#C#")
         .pattern("###")
         .unlockedBy(getHasName(var1), has(var1))
         .save(var0);
   }

   protected static void copySmithingTemplate(Consumer<FinishedRecipe> var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, var1, 2)
         .define('#', Items.DIAMOND)
         .define('C', var2)
         .define('S', var1)
         .pattern("#S#")
         .pattern("#C#")
         .pattern("###")
         .unlockedBy(getHasName(var1), has(var1))
         .save(var0);
   }

   protected static void cookRecipes(Consumer<FinishedRecipe> var0, String var1, RecipeSerializer<? extends AbstractCookingRecipe> var2, int var3) {
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

   private static void simpleCookingRecipe(
      Consumer<FinishedRecipe> var0, String var1, RecipeSerializer<? extends AbstractCookingRecipe> var2, int var3, ItemLike var4, ItemLike var5, float var6
   ) {
      SimpleCookingRecipeBuilder.generic(Ingredient.of(var4), RecipeCategory.FOOD, var5, var6, var3, var2)
         .unlockedBy(getHasName(var4), has(var4))
         .save(var0, getItemName(var5) + "_from_" + var1);
   }

   protected static void waxRecipes(Consumer<FinishedRecipe> var0) {
      ((BiMap)HoneycombItem.WAXABLES.get())
         .forEach(
            (var1, var2) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var2)
                  .requires(var1)
                  .requires(Items.HONEYCOMB)
                  .group(getItemName(var2))
                  .unlockedBy(getHasName(var1), has(var1))
                  .save(var0, getConversionRecipeName(var2, Items.HONEYCOMB))
         );
   }

   protected static void generateRecipes(Consumer<FinishedRecipe> var0, BlockFamily var1) {
      var1.getVariants().forEach((var2, var3) -> {
         BiFunction var4 = SHAPE_BUILDERS.get(var2);
         Block var5 = getBaseBlock(var1, var2);
         if (var4 != null) {
            RecipeBuilder var6 = (RecipeBuilder)var4.apply(var3, var5);
            var1.getRecipeGroupPrefix().ifPresent(var2x -> var6.group(var2x + (var2 == BlockFamily.Variant.CUT ? "" : "_" + var2.getName())));
            var6.unlockedBy(var1.getRecipeUnlockedBy().orElseGet(() -> getHasName(var5)), has(var5));
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

   protected static InventoryChangeTrigger.TriggerInstance has(ItemLike var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0).build());
   }

   protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0).build());
   }

   private static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... var0) {
      return new InventoryChangeTrigger.TriggerInstance(
         EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, var0
      );
   }

   protected static String getHasName(ItemLike var0) {
      return "has_" + getItemName(var0);
   }

   protected static String getItemName(ItemLike var0) {
      return BuiltInRegistries.ITEM.getKey(var0.asItem()).getPath();
   }

   protected static String getSimpleRecipeName(ItemLike var0) {
      return getItemName(var0);
   }

   protected static String getConversionRecipeName(ItemLike var0, ItemLike var1) {
      return getItemName(var0) + "_from_" + getItemName(var1);
   }

   protected static String getSmeltingRecipeName(ItemLike var0) {
      return getItemName(var0) + "_from_smelting";
   }

   protected static String getBlastingRecipeName(ItemLike var0) {
      return getItemName(var0) + "_from_blasting";
   }

   @Override
   public final String getName() {
      return "Recipes";
   }
}
