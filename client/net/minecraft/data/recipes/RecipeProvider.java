package net.minecraft.data.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public abstract class RecipeProvider {
   protected final HolderLookup.Provider registries;
   private final HolderGetter<Item> items;
   protected final RecipeOutput output;
   private static final Map<BlockFamily.Variant, RecipeProvider.FamilyRecipeProvider> SHAPE_BUILDERS = ImmutableMap.builder()
      .put(BlockFamily.Variant.BUTTON, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.buttonBuilder(var1, Ingredient.of(var2)))
      .put(
         BlockFamily.Variant.CHISELED,
         (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, var1, Ingredient.of(var2))
      )
      .put(
         BlockFamily.Variant.CUT,
         (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.cutBuilder(RecipeCategory.BUILDING_BLOCKS, var1, Ingredient.of(var2))
      )
      .put(BlockFamily.Variant.DOOR, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.doorBuilder(var1, Ingredient.of(var2)))
      .put(BlockFamily.Variant.CUSTOM_FENCE, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.fenceBuilder(var1, Ingredient.of(var2)))
      .put(BlockFamily.Variant.FENCE, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.fenceBuilder(var1, Ingredient.of(var2)))
      .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.fenceGateBuilder(var1, Ingredient.of(var2)))
      .put(BlockFamily.Variant.FENCE_GATE, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.fenceGateBuilder(var1, Ingredient.of(var2)))
      .put(BlockFamily.Variant.SIGN, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.signBuilder(var1, Ingredient.of(var2)))
      .put(
         BlockFamily.Variant.SLAB,
         (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.slabBuilder(RecipeCategory.BUILDING_BLOCKS, var1, Ingredient.of(var2))
      )
      .put(BlockFamily.Variant.STAIRS, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.stairBuilder(var1, Ingredient.of(var2)))
      .put(
         BlockFamily.Variant.PRESSURE_PLATE,
         (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.pressurePlateBuilder(RecipeCategory.REDSTONE, var1, Ingredient.of(var2))
      )
      .put(
         BlockFamily.Variant.POLISHED,
         (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.polishedBuilder(RecipeCategory.BUILDING_BLOCKS, var1, Ingredient.of(var2))
      )
      .put(BlockFamily.Variant.TRAPDOOR, (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.trapdoorBuilder(var1, Ingredient.of(var2)))
      .put(
         BlockFamily.Variant.WALL,
         (RecipeProvider.FamilyRecipeProvider)(var0, var1, var2) -> var0.wallBuilder(RecipeCategory.DECORATIONS, var1, Ingredient.of(var2))
      )
      .build();

   protected RecipeProvider(HolderLookup.Provider var1, RecipeOutput var2) {
      super();
      this.registries = var1;
      this.items = var1.lookupOrThrow(Registries.ITEM);
      this.output = var2;
   }

   protected abstract void buildRecipes();

   protected void generateForEnabledBlockFamilies(FeatureFlagSet var1) {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach(var2 -> this.generateRecipes(var2, var1));
   }

   protected void oneToOneConversionRecipe(ItemLike var1, ItemLike var2, @Nullable String var3) {
      this.oneToOneConversionRecipe(var1, var2, var3, 1);
   }

   protected void oneToOneConversionRecipe(ItemLike var1, ItemLike var2, @Nullable String var3, int var4) {
      this.shapeless(RecipeCategory.MISC, var1, var4)
         .requires(var2)
         .group(var3)
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output, getConversionRecipeName(var1, var2));
   }

   protected void oreSmelting(List<ItemLike> var1, RecipeCategory var2, ItemLike var3, float var4, int var5, String var6) {
      this.oreCooking(RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, var1, var2, var3, var4, var5, var6, "_from_smelting");
   }

   protected void oreBlasting(List<ItemLike> var1, RecipeCategory var2, ItemLike var3, float var4, int var5, String var6) {
      this.oreCooking(RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, var1, var2, var3, var4, var5, var6, "_from_blasting");
   }

   private <T extends AbstractCookingRecipe> void oreCooking(
      RecipeSerializer<T> var1,
      AbstractCookingRecipe.Factory<T> var2,
      List<ItemLike> var3,
      RecipeCategory var4,
      ItemLike var5,
      float var6,
      int var7,
      String var8,
      String var9
   ) {
      for (ItemLike var11 : var3) {
         SimpleCookingRecipeBuilder.generic(Ingredient.of(var11), var4, var5, var6, var7, var1, var2)
            .group(var8)
            .unlockedBy(getHasName(var11), this.has(var11))
            .save(this.output, getItemName(var5) + var9 + "_" + getItemName(var11));
      }
   }

   protected void netheriteSmithing(Item var1, RecipeCategory var2, Item var3) {
      SmithingTransformRecipeBuilder.smithing(
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(var1), this.tag(ItemTags.NETHERITE_TOOL_MATERIALS), var2, var3
         )
         .unlocks("has_netherite_ingot", this.has(ItemTags.NETHERITE_TOOL_MATERIALS))
         .save(this.output, getItemName(var3) + "_smithing");
   }

   protected void trimSmithing(Item var1, ResourceKey<Recipe<?>> var2) {
      SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(var1), this.tag(ItemTags.TRIMMABLE_ARMOR), this.tag(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC)
         .unlocks("has_smithing_trim_template", this.has(var1))
         .save(this.output, var2);
   }

   protected void twoByTwoPacker(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.shaped(var1, var2, 1).define('#', var3).pattern("##").pattern("##").unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   protected void threeByThreePacker(RecipeCategory var1, ItemLike var2, ItemLike var3, String var4) {
      this.shapeless(var1, var2).requires(var3, 9).unlockedBy(var4, this.has(var3)).save(this.output);
   }

   protected void threeByThreePacker(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.threeByThreePacker(var1, var2, var3, getHasName(var3));
   }

   protected void planksFromLog(ItemLike var1, TagKey<Item> var2, int var3) {
      this.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, var3).requires(var2).group("planks").unlockedBy("has_log", this.has(var2)).save(this.output);
   }

   protected void planksFromLogs(ItemLike var1, TagKey<Item> var2, int var3) {
      this.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, var3).requires(var2).group("planks").unlockedBy("has_logs", this.has(var2)).save(this.output);
   }

   protected void woodFromLogs(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 3)
         .define('#', var2)
         .pattern("##")
         .pattern("##")
         .group("bark")
         .unlockedBy("has_log", this.has(var2))
         .save(this.output);
   }

   protected void woodenBoat(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.TRANSPORTATION, var1)
         .define('#', var2)
         .pattern("# #")
         .pattern("###")
         .group("boat")
         .unlockedBy("in_water", insideOf(Blocks.WATER))
         .save(this.output);
   }

   protected void chestBoat(ItemLike var1, ItemLike var2) {
      this.shapeless(RecipeCategory.TRANSPORTATION, var1)
         .requires(Blocks.CHEST)
         .requires(var2)
         .group("chest_boat")
         .unlockedBy("has_boat", this.has(ItemTags.BOATS))
         .save(this.output);
   }

   private RecipeBuilder buttonBuilder(ItemLike var1, Ingredient var2) {
      return this.shapeless(RecipeCategory.REDSTONE, var1).requires(var2);
   }

   protected RecipeBuilder doorBuilder(ItemLike var1, Ingredient var2) {
      return this.shaped(RecipeCategory.REDSTONE, var1, 3).define('#', var2).pattern("##").pattern("##").pattern("##");
   }

   private RecipeBuilder fenceBuilder(ItemLike var1, Ingredient var2) {
      int var3 = var1 == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
      Item var4 = var1 == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
      return this.shaped(RecipeCategory.DECORATIONS, var1, var3).define('W', var2).define('#', var4).pattern("W#W").pattern("W#W");
   }

   private RecipeBuilder fenceGateBuilder(ItemLike var1, Ingredient var2) {
      return this.shaped(RecipeCategory.REDSTONE, var1).define('#', Items.STICK).define('W', var2).pattern("#W#").pattern("#W#");
   }

   protected void pressurePlate(ItemLike var1, ItemLike var2) {
      this.pressurePlateBuilder(RecipeCategory.REDSTONE, var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), this.has(var2)).save(this.output);
   }

   private RecipeBuilder pressurePlateBuilder(RecipeCategory var1, ItemLike var2, Ingredient var3) {
      return this.shaped(var1, var2).define('#', var3).pattern("##");
   }

   protected void slab(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.slabBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   protected RecipeBuilder slabBuilder(RecipeCategory var1, ItemLike var2, Ingredient var3) {
      return this.shaped(var1, var2, 6).define('#', var3).pattern("###");
   }

   protected RecipeBuilder stairBuilder(ItemLike var1, Ingredient var2) {
      return this.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 4).define('#', var2).pattern("#  ").pattern("## ").pattern("###");
   }

   protected RecipeBuilder trapdoorBuilder(ItemLike var1, Ingredient var2) {
      return this.shaped(RecipeCategory.REDSTONE, var1, 2).define('#', var2).pattern("###").pattern("###");
   }

   private RecipeBuilder signBuilder(ItemLike var1, Ingredient var2) {
      return this.shaped(RecipeCategory.DECORATIONS, var1, 3)
         .group("sign")
         .define('#', var2)
         .define('X', Items.STICK)
         .pattern("###")
         .pattern("###")
         .pattern(" X ");
   }

   protected void hangingSign(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.DECORATIONS, var1, 6)
         .group("hanging_sign")
         .define('#', var2)
         .define('X', Items.CHAIN)
         .pattern("X X")
         .pattern("###")
         .pattern("###")
         .unlockedBy("has_stripped_logs", this.has(var2))
         .save(this.output);
   }

   protected void colorBlockWithDye(List<Item> var1, List<Item> var2, String var3) {
      this.colorWithDye(var1, var2, null, var3, RecipeCategory.BUILDING_BLOCKS);
   }

   protected void colorWithDye(List<Item> var1, List<Item> var2, @Nullable Item var3, String var4, RecipeCategory var5) {
      for (int var6 = 0; var6 < var1.size(); var6++) {
         Item var7 = (Item)var1.get(var6);
         Item var8 = (Item)var2.get(var6);
         Stream var9 = var2.stream().filter(var1x -> !var1x.equals(var8));
         if (var3 != null) {
            var9 = Stream.concat(var9, Stream.of(var3));
         }

         this.shapeless(var5, var8)
            .requires(var7)
            .requires(Ingredient.of(var9))
            .group(var4)
            .unlockedBy("has_needed_dye", this.has(var7))
            .save(this.output, "dye_" + getItemName(var8));
      }
   }

   protected void carpet(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.DECORATIONS, var1, 3)
         .define('#', var2)
         .pattern("##")
         .group("carpet")
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void bedFromPlanksAndWool(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.DECORATIONS, var1)
         .define('#', var2)
         .define('X', ItemTags.PLANKS)
         .pattern("###")
         .pattern("XXX")
         .group("bed")
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void banner(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.DECORATIONS, var1)
         .define('#', var2)
         .define('|', Items.STICK)
         .pattern("###")
         .pattern("###")
         .pattern(" | ")
         .group("banner")
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void stainedGlassFromGlassAndDye(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 8)
         .define('#', Blocks.GLASS)
         .define('X', var2)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .group("stained_glass")
         .unlockedBy("has_glass", this.has(Blocks.GLASS))
         .save(this.output);
   }

   protected void stainedGlassPaneFromStainedGlass(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.DECORATIONS, var1, 16)
         .define('#', var2)
         .pattern("###")
         .pattern("###")
         .group("stained_glass_pane")
         .unlockedBy("has_glass", this.has(var2))
         .save(this.output);
   }

   protected void stainedGlassPaneFromGlassPaneAndDye(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.DECORATIONS, var1, 8)
         .define('#', Blocks.GLASS_PANE)
         .define('$', var2)
         .pattern("###")
         .pattern("#$#")
         .pattern("###")
         .group("stained_glass_pane")
         .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE))
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output, getConversionRecipeName(var1, Blocks.GLASS_PANE));
   }

   protected void coloredTerracottaFromTerracottaAndDye(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 8)
         .define('#', Blocks.TERRACOTTA)
         .define('X', var2)
         .pattern("###")
         .pattern("#X#")
         .pattern("###")
         .group("stained_terracotta")
         .unlockedBy("has_terracotta", this.has(Blocks.TERRACOTTA))
         .save(this.output);
   }

   protected void concretePowder(ItemLike var1, ItemLike var2) {
      this.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, 8)
         .requires(var2)
         .requires(Blocks.SAND, 4)
         .requires(Blocks.GRAVEL, 4)
         .group("concrete_powder")
         .unlockedBy("has_sand", this.has(Blocks.SAND))
         .unlockedBy("has_gravel", this.has(Blocks.GRAVEL))
         .save(this.output);
   }

   protected void candle(ItemLike var1, ItemLike var2) {
      this.shapeless(RecipeCategory.DECORATIONS, var1)
         .requires(Blocks.CANDLE)
         .requires(var2)
         .group("dyed_candle")
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void wall(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.wallBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   private RecipeBuilder wallBuilder(RecipeCategory var1, ItemLike var2, Ingredient var3) {
      return this.shaped(var1, var2, 6).define('#', var3).pattern("###").pattern("###");
   }

   protected void polished(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.polishedBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   private RecipeBuilder polishedBuilder(RecipeCategory var1, ItemLike var2, Ingredient var3) {
      return this.shaped(var1, var2, 4).define('S', var3).pattern("SS").pattern("SS");
   }

   protected void cut(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.cutBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   private ShapedRecipeBuilder cutBuilder(RecipeCategory var1, ItemLike var2, Ingredient var3) {
      return this.shaped(var1, var2, 4).define('#', var3).pattern("##").pattern("##");
   }

   protected void chiseled(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.chiseledBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   protected void mosaicBuilder(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.shaped(var1, var2).define('#', var3).pattern("#").pattern("#").unlockedBy(getHasName(var3), this.has(var3)).save(this.output);
   }

   protected ShapedRecipeBuilder chiseledBuilder(RecipeCategory var1, ItemLike var2, Ingredient var3) {
      return this.shaped(var1, var2).define('#', var3).pattern("#").pattern("#");
   }

   protected void stonecutterResultFromBase(RecipeCategory var1, ItemLike var2, ItemLike var3) {
      this.stonecutterResultFromBase(var1, var2, var3, 1);
   }

   protected void stonecutterResultFromBase(RecipeCategory var1, ItemLike var2, ItemLike var3, int var4) {
      SingleItemRecipeBuilder.stonecutting(Ingredient.of(var3), var1, var2, var4)
         .unlockedBy(getHasName(var3), this.has(var3))
         .save(this.output, getConversionRecipeName(var2, var3) + "_stonecutting");
   }

   private void smeltingResultFromBase(ItemLike var1, ItemLike var2) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(var2), RecipeCategory.BUILDING_BLOCKS, var1, 0.1F, 200)
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void nineBlockStorageRecipes(RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4) {
      this.nineBlockStorageRecipes(var1, var2, var3, var4, getSimpleRecipeName(var4), null, getSimpleRecipeName(var2), null);
   }

   protected void nineBlockStorageRecipesWithCustomPacking(RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, String var6) {
      this.nineBlockStorageRecipes(var1, var2, var3, var4, var5, var6, getSimpleRecipeName(var2), null);
   }

   protected void nineBlockStorageRecipesRecipesWithCustomUnpacking(
      RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, String var6
   ) {
      this.nineBlockStorageRecipes(var1, var2, var3, var4, getSimpleRecipeName(var4), null, var5, var6);
   }

   private void nineBlockStorageRecipes(
      RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, @Nullable String var6, String var7, @Nullable String var8
   ) {
      this.shapeless(var1, var2, 9)
         .requires(var4)
         .group(var8)
         .unlockedBy(getHasName(var4), this.has(var4))
         .save(this.output, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(var7)));
      this.shaped(var3, var4)
         .define('#', var2)
         .pattern("###")
         .pattern("###")
         .pattern("###")
         .group(var6)
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(var5)));
   }

   protected void copySmithingTemplate(ItemLike var1, ItemLike var2) {
      this.shaped(RecipeCategory.MISC, var1, 2)
         .define('#', Items.DIAMOND)
         .define('C', var2)
         .define('S', var1)
         .pattern("#S#")
         .pattern("#C#")
         .pattern("###")
         .unlockedBy(getHasName(var1), this.has(var1))
         .save(this.output);
   }

   protected void copySmithingTemplate(ItemLike var1, Ingredient var2) {
      this.shaped(RecipeCategory.MISC, var1, 2)
         .define('#', Items.DIAMOND)
         .define('C', var2)
         .define('S', var1)
         .pattern("#S#")
         .pattern("#C#")
         .pattern("###")
         .unlockedBy(getHasName(var1), this.has(var1))
         .save(this.output);
   }

   protected <T extends AbstractCookingRecipe> void cookRecipes(String var1, RecipeSerializer<T> var2, AbstractCookingRecipe.Factory<T> var3, int var4) {
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.BEEF, Items.COOKED_BEEF, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.COD, Items.COOKED_COD, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.KELP, Items.DRIED_KELP, 0.1F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.SALMON, Items.COOKED_SALMON, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.POTATO, Items.BAKED_POTATO, 0.35F);
      this.simpleCookingRecipe(var1, var2, var3, var4, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
   }

   private <T extends AbstractCookingRecipe> void simpleCookingRecipe(
      String var1, RecipeSerializer<T> var2, AbstractCookingRecipe.Factory<T> var3, int var4, ItemLike var5, ItemLike var6, float var7
   ) {
      SimpleCookingRecipeBuilder.generic(Ingredient.of(var5), RecipeCategory.FOOD, var6, var7, var4, var2, var3)
         .unlockedBy(getHasName(var5), this.has(var5))
         .save(this.output, getItemName(var6) + "_from_" + var1);
   }

   protected void waxRecipes(FeatureFlagSet var1) {
      HoneycombItem.WAXABLES
         .get()
         .forEach(
            (var2, var3) -> {
               if (var3.requiredFeatures().isSubsetOf(var1)) {
                  this.shapeless(RecipeCategory.BUILDING_BLOCKS, var3)
                     .requires(var2)
                     .requires(Items.HONEYCOMB)
                     .group(getItemName(var3))
                     .unlockedBy(getHasName(var2), this.has(var2))
                     .save(this.output, getConversionRecipeName(var3, Items.HONEYCOMB));
               }
            }
         );
   }

   protected void grate(Block var1, Block var2) {
      this.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 4)
         .define('M', var2)
         .pattern(" M ")
         .pattern("M M")
         .pattern(" M ")
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void copperBulb(Block var1, Block var2) {
      this.shaped(RecipeCategory.REDSTONE, var1, 4)
         .define('C', var2)
         .define('R', Items.REDSTONE)
         .define('B', Items.BLAZE_ROD)
         .pattern(" C ")
         .pattern("CBC")
         .pattern(" R ")
         .unlockedBy(getHasName(var2), this.has(var2))
         .save(this.output);
   }

   protected void suspiciousStew(Item var1, SuspiciousEffectHolder var2) {
      ItemStack var3 = new ItemStack(
         Items.SUSPICIOUS_STEW.builtInRegistryHolder(),
         1,
         DataComponentPatch.builder().set(DataComponents.SUSPICIOUS_STEW_EFFECTS, var2.getSuspiciousEffects()).build()
      );
      this.shapeless(RecipeCategory.FOOD, var3)
         .requires(Items.BOWL)
         .requires(Items.BROWN_MUSHROOM)
         .requires(Items.RED_MUSHROOM)
         .requires(var1)
         .group("suspicious_stew")
         .unlockedBy(getHasName(var1), this.has(var1))
         .save(this.output, getItemName(var3.getItem()) + "_from_" + getItemName(var1));
   }

   protected void generateRecipes(BlockFamily var1, FeatureFlagSet var2) {
      var1.getVariants().forEach((var3, var4) -> {
         if (var4.requiredFeatures().isSubsetOf(var2)) {
            RecipeProvider.FamilyRecipeProvider var5 = SHAPE_BUILDERS.get(var3);
            Block var6 = this.getBaseBlock(var1, var3);
            if (var5 != null) {
               RecipeBuilder var7 = var5.create(this, var4, var6);
               var1.getRecipeGroupPrefix().ifPresent(var2xx -> var7.group(var2xx + (var3 == BlockFamily.Variant.CUT ? "" : "_" + var3.getRecipeGroup())));
               var7.unlockedBy(var1.getRecipeUnlockedBy().orElseGet(() -> getHasName(var6)), this.has(var6));
               var7.save(this.output);
            }

            if (var3 == BlockFamily.Variant.CRACKED) {
               this.smeltingResultFromBase(var4, var6);
            }
         }
      });
   }

   private Block getBaseBlock(BlockFamily var1, BlockFamily.Variant var2) {
      if (var2 == BlockFamily.Variant.CHISELED) {
         if (!var1.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
            throw new IllegalStateException("Slab is not defined for the family.");
         } else {
            return var1.get(BlockFamily.Variant.SLAB);
         }
      } else {
         return var1.getBaseBlock();
      }
   }

   private static Criterion<EnterBlockTrigger.TriggerInstance> insideOf(Block var0) {
      return CriteriaTriggers.ENTER_BLOCK
         .createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.builtInRegistryHolder()), Optional.empty()));
   }

   private Criterion<InventoryChangeTrigger.TriggerInstance> has(MinMaxBounds.Ints var1, ItemLike var2) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, var2).withCount(var1));
   }

   protected Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike var1) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, var1));
   }

   protected Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> var1) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, var1));
   }

   private static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... var0) {
      return inventoryTrigger(Arrays.stream(var0).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
   }

   private static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... var0) {
      return CriteriaTriggers.INVENTORY_CHANGED
         .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(var0)));
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

   protected Ingredient tag(TagKey<Item> var1) {
      return Ingredient.of(this.items.getOrThrow(var1));
   }

   protected ShapedRecipeBuilder shaped(RecipeCategory var1, ItemLike var2) {
      return ShapedRecipeBuilder.shaped(this.items, var1, var2);
   }

   protected ShapedRecipeBuilder shaped(RecipeCategory var1, ItemLike var2, int var3) {
      return ShapedRecipeBuilder.shaped(this.items, var1, var2, var3);
   }

   protected ShapelessRecipeBuilder shapeless(RecipeCategory var1, ItemStack var2) {
      return ShapelessRecipeBuilder.shapeless(this.items, var1, var2);
   }

   protected ShapelessRecipeBuilder shapeless(RecipeCategory var1, ItemLike var2) {
      return ShapelessRecipeBuilder.shapeless(this.items, var1, var2);
   }

   protected ShapelessRecipeBuilder shapeless(RecipeCategory var1, ItemLike var2, int var3) {
      return ShapelessRecipeBuilder.shapeless(this.items, var1, var2, var3);
   }

   @FunctionalInterface
   interface FamilyRecipeProvider {
      RecipeBuilder create(RecipeProvider var1, ItemLike var2, ItemLike var3);
   }

   protected abstract static class Runner implements DataProvider {
      private final PackOutput packOutput;
      private final CompletableFuture<HolderLookup.Provider> registries;

      protected Runner(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
         super();
         this.packOutput = var1;
         this.registries = var2;
      }

      @Override
      public final CompletableFuture<?> run(CachedOutput var1) {
         return this.registries
            .thenCompose(
               var2 -> {
                  final PackOutput.PathProvider var3 = this.packOutput.createRegistryElementsPathProvider(Registries.RECIPE);
                  final PackOutput.PathProvider var4 = this.packOutput.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
                  final HashSet var5 = Sets.newHashSet();
                  final ArrayList var6 = new ArrayList();
                  RecipeOutput var7 = new RecipeOutput() {
                     @Override
                     public void accept(ResourceKey<Recipe<?>> var1x, Recipe<?> var2x, @Nullable AdvancementHolder var3x) {
                        if (!var5.add(var1x)) {
                           throw new IllegalStateException("Duplicate recipe " + var1x);
                        } else {
                           this.saveRecipe(var1x, var2x);
                           if (var3x != null) {
                              this.saveAdvancement(var3x);
                           }
                        }
                     }

                     @Override
                     public Advancement.Builder advancement() {
                        return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
                     }

                     @Override
                     public void includeRootAdvancement() {
                        AdvancementHolder var1x = Advancement.Builder.recipeAdvancement()
                           .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                           .build(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
                        this.saveAdvancement(var1x);
                     }

                     private void saveRecipe(ResourceKey<Recipe<?>> var1x, Recipe<?> var2x) {
                        var6.add(DataProvider.saveStable(var1, var2, Recipe.CODEC, var2x, var3.json(var1x.location())));
                     }

                     private void saveAdvancement(AdvancementHolder var1x) {
                        var6.add(DataProvider.saveStable(var1, var2, Advancement.CODEC, var1x.value(), var4.json(var1x.id())));
                     }
                  };
                  this.createRecipeProvider(var2, var7).buildRecipes();
                  return CompletableFuture.allOf(var6.toArray(CompletableFuture[]::new));
               }
            );
      }

      protected abstract RecipeProvider createRecipeProvider(HolderLookup.Provider var1, RecipeOutput var2);
   }
}
