package net.minecraft.data.recipes;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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

public abstract class RecipeProvider implements DataProvider {
   final PackOutput.PathProvider recipePathProvider;
   final PackOutput.PathProvider advancementPathProvider;
   private final CompletableFuture<HolderLookup.Provider> registries;
   private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS;

   public RecipeProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.recipePathProvider = var1.createRegistryElementsPathProvider(Registries.RECIPE);
      this.advancementPathProvider = var1.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
      this.registries = var2;
   }

   public final CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose((var2) -> {
         return this.run(var1, var2);
      });
   }

   protected CompletableFuture<?> run(final CachedOutput var1, final HolderLookup.Provider var2) {
      final HashSet var3 = Sets.newHashSet();
      final ArrayList var4 = new ArrayList();
      this.buildRecipes(new RecipeOutput() {
         public void accept(ResourceLocation var1x, Recipe<?> var2x, @Nullable AdvancementHolder var3x) {
            if (!var3.add(var1x)) {
               throw new IllegalStateException("Duplicate recipe " + String.valueOf(var1x));
            } else {
               var4.add(DataProvider.saveStable(var1, var2, Recipe.CODEC, var2x, RecipeProvider.this.recipePathProvider.json(var1x)));
               if (var3x != null) {
                  var4.add(DataProvider.saveStable(var1, var2, Advancement.CODEC, var3x.value(), RecipeProvider.this.advancementPathProvider.json(var3x.id())));
               }

            }
         }

         public Advancement.Builder advancement() {
            return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
         }
      });
      return CompletableFuture.allOf((CompletableFuture[])var4.toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
   }

   protected CompletableFuture<?> buildAdvancement(CachedOutput var1, HolderLookup.Provider var2, AdvancementHolder var3) {
      return DataProvider.saveStable(var1, var2, Advancement.CODEC, var3.value(), this.advancementPathProvider.json(var3.id()));
   }

   protected abstract void buildRecipes(RecipeOutput var1);

   protected static void generateForEnabledBlockFamilies(RecipeOutput var0, FeatureFlagSet var1) {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach((var2) -> {
         generateRecipes(var0, var2, var1);
      });
   }

   protected static void oneToOneConversionRecipe(RecipeOutput var0, ItemLike var1, ItemLike var2, @Nullable String var3) {
      oneToOneConversionRecipe(var0, var1, var2, var3, 1);
   }

   protected static void oneToOneConversionRecipe(RecipeOutput var0, ItemLike var1, ItemLike var2, @Nullable String var3, int var4) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, var1, var4).requires(var2).group(var3).unlockedBy(getHasName(var2), has(var2)).save(var0, getConversionRecipeName(var1, var2));
   }

   protected static void oreSmelting(RecipeOutput var0, List<ItemLike> var1, RecipeCategory var2, ItemLike var3, float var4, int var5, String var6) {
      oreCooking(var0, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, var1, var2, var3, var4, var5, var6, "_from_smelting");
   }

   protected static void oreBlasting(RecipeOutput var0, List<ItemLike> var1, RecipeCategory var2, ItemLike var3, float var4, int var5, String var6) {
      oreCooking(var0, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, var1, var2, var3, var4, var5, var6, "_from_blasting");
   }

   private static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput var0, RecipeSerializer<T> var1, AbstractCookingRecipe.Factory<T> var2, List<ItemLike> var3, RecipeCategory var4, ItemLike var5, float var6, int var7, String var8, String var9) {
      Iterator var10 = var3.iterator();

      while(var10.hasNext()) {
         ItemLike var11 = (ItemLike)var10.next();
         SimpleCookingRecipeBuilder.generic(Ingredient.of(var11), var4, var5, var6, var7, var1, var2).group(var8).unlockedBy(getHasName(var11), has(var11)).save(var0, getItemName(var5) + var9 + "_" + getItemName(var11));
      }

   }

   protected static void netheriteSmithing(RecipeOutput var0, Item var1, RecipeCategory var2, Item var3) {
      SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(var1), Ingredient.of(Items.NETHERITE_INGOT), var2, var3).unlocks("has_netherite_ingot", has((ItemLike)Items.NETHERITE_INGOT)).save(var0, getItemName(var3) + "_smithing");
   }

   protected static void trimSmithing(RecipeOutput var0, Item var1, ResourceLocation var2) {
      SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(var1), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC).unlocks("has_smithing_trim_template", has((ItemLike)var1)).save(var0, var2);
   }

   protected static void twoByTwoPacker(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      ShapedRecipeBuilder.shaped(var1, var2, 1).define('#', var3).pattern("##").pattern("##").unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static void threeByThreePacker(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3, String var4) {
      ShapelessRecipeBuilder.shapeless(var1, var2).requires((ItemLike)var3, 9).unlockedBy(var4, has(var3)).save(var0);
   }

   protected static void threeByThreePacker(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      threeByThreePacker(var0, var1, var2, var3, getHasName(var3));
   }

   protected static void planksFromLog(RecipeOutput var0, ItemLike var1, TagKey<Item> var2, int var3) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, var3).requires(var2).group("planks").unlockedBy("has_log", has(var2)).save(var0);
   }

   protected static void planksFromLogs(RecipeOutput var0, ItemLike var1, TagKey<Item> var2, int var3) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, var3).requires(var2).group("planks").unlockedBy("has_logs", has(var2)).save(var0);
   }

   protected static void woodFromLogs(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 3).define('#', var2).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(var2)).save(var0);
   }

   protected static void woodenBoat(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, var1).define('#', var2).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(var0);
   }

   protected static void chestBoat(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, var1).requires((ItemLike)Blocks.CHEST).requires(var2).group("chest_boat").unlockedBy("has_boat", has(ItemTags.BOATS)).save(var0);
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
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var0, var2).define('W', var1).define('#', (ItemLike)var3).pattern("W#W").pattern("W#W");
   }

   private static RecipeBuilder fenceGateBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, var0).define('#', (ItemLike)Items.STICK).define('W', var1).pattern("#W#").pattern("#W#");
   }

   protected static void pressurePlate(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      pressurePlateBuilder(RecipeCategory.REDSTONE, var1, Ingredient.of(var2)).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   private static RecipeBuilder pressurePlateBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1).define('#', var2).pattern("##");
   }

   protected static void slab(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      slabBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static RecipeBuilder slabBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 6).define('#', var2).pattern("###");
   }

   protected static RecipeBuilder stairBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var0, 4).define('#', var1).pattern("#  ").pattern("## ").pattern("###");
   }

   protected static RecipeBuilder trapdoorBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, var0, 2).define('#', var1).pattern("###").pattern("###");
   }

   private static RecipeBuilder signBuilder(ItemLike var0, Ingredient var1) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var0, 3).group("sign").define('#', var1).define('X', (ItemLike)Items.STICK).pattern("###").pattern("###").pattern(" X ");
   }

   protected static void hangingSign(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 6).group("hanging_sign").define('#', var2).define('X', (ItemLike)Items.CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", has(var2)).save(var0);
   }

   protected static void colorBlockWithDye(RecipeOutput var0, List<Item> var1, List<Item> var2, String var3) {
      for(int var4 = 0; var4 < var1.size(); ++var4) {
         Item var5 = (Item)var1.get(var4);
         Item var6 = (Item)var2.get(var4);
         ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var6).requires((ItemLike)var5).requires(Ingredient.of(var2.stream().filter((var1x) -> {
            return !var1x.equals(var6);
         }).map(ItemStack::new))).group(var3).unlockedBy("has_needed_dye", has((ItemLike)var5)).save(var0, "dye_" + getItemName(var6));
      }

   }

   protected static void carpet(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 3).define('#', var2).pattern("##").group("carpet").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   protected static void bedFromPlanksAndWool(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1).define('#', var2).define('X', ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   protected static void banner(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1).define('#', var2).define('|', (ItemLike)Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   protected static void stainedGlassFromGlassAndDye(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 8).define('#', (ItemLike)Blocks.GLASS).define('X', var2).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has((ItemLike)Blocks.GLASS)).save(var0);
   }

   protected static void stainedGlassPaneFromStainedGlass(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 16).define('#', var2).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(var2)).save(var0);
   }

   protected static void stainedGlassPaneFromGlassPaneAndDye(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, var1, 8).define('#', (ItemLike)Blocks.GLASS_PANE).define('$', var2).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has((ItemLike)Blocks.GLASS_PANE)).unlockedBy(getHasName(var2), has(var2)).save(var0, getConversionRecipeName(var1, Blocks.GLASS_PANE));
   }

   protected static void coloredTerracottaFromTerracottaAndDye(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 8).define('#', (ItemLike)Blocks.TERRACOTTA).define('X', var2).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has((ItemLike)Blocks.TERRACOTTA)).save(var0);
   }

   protected static void concretePowder(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var1, 8).requires(var2).requires((ItemLike)Blocks.SAND, 4).requires((ItemLike)Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has((ItemLike)Blocks.SAND)).unlockedBy("has_gravel", has((ItemLike)Blocks.GRAVEL)).save(var0);
   }

   protected static void candle(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, var1).requires((ItemLike)Blocks.CANDLE).requires(var2).group("dyed_candle").unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   protected static void wall(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      wallBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   private static RecipeBuilder wallBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 6).define('#', var2).pattern("###").pattern("###");
   }

   protected static void polished(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      polishedBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   private static RecipeBuilder polishedBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 4).define('S', var2).pattern("SS").pattern("SS");
   }

   protected static void cut(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      cutBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   private static ShapedRecipeBuilder cutBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1, 4).define('#', var2).pattern("##").pattern("##");
   }

   protected static void chiseled(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      chiseledBuilder(var1, var2, Ingredient.of(var3)).unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static void mosaicBuilder(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      ShapedRecipeBuilder.shaped(var1, var2).define('#', var3).pattern("#").pattern("#").unlockedBy(getHasName(var3), has(var3)).save(var0);
   }

   protected static ShapedRecipeBuilder chiseledBuilder(RecipeCategory var0, ItemLike var1, Ingredient var2) {
      return ShapedRecipeBuilder.shaped(var0, var1).define('#', var2).pattern("#").pattern("#");
   }

   protected static void stonecutterResultFromBase(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3) {
      stonecutterResultFromBase(var0, var1, var2, var3, 1);
   }

   protected static void stonecutterResultFromBase(RecipeOutput var0, RecipeCategory var1, ItemLike var2, ItemLike var3, int var4) {
      SingleItemRecipeBuilder var10000 = SingleItemRecipeBuilder.stonecutting(Ingredient.of(var3), var1, var2, var4).unlockedBy(getHasName(var3), has(var3));
      String var10002 = getConversionRecipeName(var2, var3);
      var10000.save(var0, var10002 + "_stonecutting");
   }

   private static void smeltingResultFromBase(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      SimpleCookingRecipeBuilder.smelting(Ingredient.of(var2), RecipeCategory.BUILDING_BLOCKS, var1, 0.1F, 200).unlockedBy(getHasName(var2), has(var2)).save(var0);
   }

   protected static void nineBlockStorageRecipes(RecipeOutput var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, getSimpleRecipeName(var4), (String)null, getSimpleRecipeName(var2), (String)null);
   }

   protected static void nineBlockStorageRecipesWithCustomPacking(RecipeOutput var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, String var6) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, var5, var6, getSimpleRecipeName(var2), (String)null);
   }

   protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeOutput var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, String var6) {
      nineBlockStorageRecipes(var0, var1, var2, var3, var4, getSimpleRecipeName(var4), (String)null, var5, var6);
   }

   private static void nineBlockStorageRecipes(RecipeOutput var0, RecipeCategory var1, ItemLike var2, RecipeCategory var3, ItemLike var4, String var5, @Nullable String var6, String var7, @Nullable String var8) {
      ShapelessRecipeBuilder.shapeless(var1, var2, 9).requires(var4).group(var8).unlockedBy(getHasName(var4), has(var4)).save(var0, ResourceLocation.parse(var7));
      ShapedRecipeBuilder.shaped(var3, var4).define('#', var2).pattern("###").pattern("###").pattern("###").group(var6).unlockedBy(getHasName(var2), has(var2)).save(var0, ResourceLocation.parse(var5));
   }

   protected static void copySmithingTemplate(RecipeOutput var0, ItemLike var1, TagKey<Item> var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, var1, 2).define('#', (ItemLike)Items.DIAMOND).define('C', var2).define('S', var1).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(var1), has(var1)).save(var0);
   }

   protected static void copySmithingTemplate(RecipeOutput var0, ItemLike var1, ItemLike var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, var1, 2).define('#', (ItemLike)Items.DIAMOND).define('C', var2).define('S', var1).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(var1), has(var1)).save(var0);
   }

   protected static void copySmithingTemplate(RecipeOutput var0, ItemLike var1, Ingredient var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, var1, 2).define('#', (ItemLike)Items.DIAMOND).define('C', var2).define('S', var1).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(getHasName(var1), has(var1)).save(var0);
   }

   protected static <T extends AbstractCookingRecipe> void cookRecipes(RecipeOutput var0, String var1, RecipeSerializer<T> var2, AbstractCookingRecipe.Factory<T> var3, int var4) {
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.BEEF, Items.COOKED_BEEF, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.COD, Items.COOKED_COD, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.KELP, Items.DRIED_KELP, 0.1F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.SALMON, Items.COOKED_SALMON, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.POTATO, Items.BAKED_POTATO, 0.35F);
      simpleCookingRecipe(var0, var1, var2, var3, var4, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
   }

   private static <T extends AbstractCookingRecipe> void simpleCookingRecipe(RecipeOutput var0, String var1, RecipeSerializer<T> var2, AbstractCookingRecipe.Factory<T> var3, int var4, ItemLike var5, ItemLike var6, float var7) {
      SimpleCookingRecipeBuilder var10000 = SimpleCookingRecipeBuilder.generic(Ingredient.of(var5), RecipeCategory.FOOD, var6, var7, var4, var2, var3).unlockedBy(getHasName(var5), has(var5));
      String var10002 = getItemName(var6);
      var10000.save(var0, var10002 + "_from_" + var1);
   }

   protected static void waxRecipes(RecipeOutput var0, FeatureFlagSet var1) {
      ((BiMap)HoneycombItem.WAXABLES.get()).forEach((var2, var3) -> {
         if (var3.requiredFeatures().isSubsetOf(var1)) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, var3).requires((ItemLike)var2).requires((ItemLike)Items.HONEYCOMB).group(getItemName(var3)).unlockedBy(getHasName(var2), has((ItemLike)var2)).save(var0, getConversionRecipeName(var3, Items.HONEYCOMB));
         }
      });
   }

   protected static void grate(RecipeOutput var0, Block var1, Block var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, var1, 4).define('M', (ItemLike)var2).pattern(" M ").pattern("M M").pattern(" M ").unlockedBy(getHasName(var2), has((ItemLike)var2)).save(var0);
   }

   protected static void copperBulb(RecipeOutput var0, Block var1, Block var2) {
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, var1, 4).define('C', (ItemLike)var2).define('R', (ItemLike)Items.REDSTONE).define('B', (ItemLike)Items.BLAZE_ROD).pattern(" C ").pattern("CBC").pattern(" R ").unlockedBy(getHasName(var2), has((ItemLike)var2)).save(var0);
   }

   protected static void generateRecipes(RecipeOutput var0, BlockFamily var1, FeatureFlagSet var2) {
      var1.getVariants().forEach((var3, var4) -> {
         if (var4.requiredFeatures().isSubsetOf(var2)) {
            BiFunction var5 = (BiFunction)SHAPE_BUILDERS.get(var3);
            Block var6 = getBaseBlock(var1, var3);
            if (var5 != null) {
               RecipeBuilder var7 = (RecipeBuilder)var5.apply(var4, var6);
               var1.getRecipeGroupPrefix().ifPresent((var2x) -> {
                  var7.group(var2x + (var3 == BlockFamily.Variant.CUT ? "" : "_" + var3.getRecipeGroup()));
               });
               var7.unlockedBy((String)var1.getRecipeUnlockedBy().orElseGet(() -> {
                  return getHasName(var6);
               }), has((ItemLike)var6));
               var7.save(var0);
            }

            if (var3 == BlockFamily.Variant.CRACKED) {
               smeltingResultFromBase(var0, var4, var6);
            }

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

   private static Criterion<EnterBlockTrigger.TriggerInstance> insideOf(Block var0) {
      return CriteriaTriggers.ENTER_BLOCK.createCriterion(new EnterBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.builtInRegistryHolder()), Optional.empty()));
   }

   private static Criterion<InventoryChangeTrigger.TriggerInstance> has(MinMaxBounds.Ints var0, ItemLike var1) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var1).withCount(var0));
   }

   protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0));
   }

   protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> var0) {
      return inventoryTrigger(ItemPredicate.Builder.item().of(var0));
   }

   private static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... var0) {
      return inventoryTrigger((ItemPredicate[])Arrays.stream(var0).map(ItemPredicate.Builder::build).toArray((var0x) -> {
         return new ItemPredicate[var0x];
      }));
   }

   private static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... var0) {
      return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(var0)));
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
      String var10000 = getItemName(var0);
      return var10000 + "_from_" + getItemName(var1);
   }

   protected static String getSmeltingRecipeName(ItemLike var0) {
      return getItemName(var0) + "_from_smelting";
   }

   protected static String getBlastingRecipeName(ItemLike var0) {
      return getItemName(var0) + "_from_blasting";
   }

   public final String getName() {
      return "Recipes";
   }

   static {
      SHAPE_BUILDERS = ImmutableMap.builder().put(BlockFamily.Variant.BUTTON, (var0, var1) -> {
         return buttonBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.CHISELED, (var0, var1) -> {
         return chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.CUT, (var0, var1) -> {
         return cutBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.DOOR, (var0, var1) -> {
         return doorBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.CUSTOM_FENCE, (var0, var1) -> {
         return fenceBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.FENCE, (var0, var1) -> {
         return fenceBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (var0, var1) -> {
         return fenceGateBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.FENCE_GATE, (var0, var1) -> {
         return fenceGateBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.SIGN, (var0, var1) -> {
         return signBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.SLAB, (var0, var1) -> {
         return slabBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.STAIRS, (var0, var1) -> {
         return stairBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.PRESSURE_PLATE, (var0, var1) -> {
         return pressurePlateBuilder(RecipeCategory.REDSTONE, var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.POLISHED, (var0, var1) -> {
         return polishedBuilder(RecipeCategory.BUILDING_BLOCKS, var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.TRAPDOOR, (var0, var1) -> {
         return trapdoorBuilder(var0, Ingredient.of(var1));
      }).put(BlockFamily.Variant.WALL, (var0, var1) -> {
         return wallBuilder(RecipeCategory.DECORATIONS, var0, Ingredient.of(var1));
      }).build();
   }
}
