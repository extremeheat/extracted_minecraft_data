package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager extends SimplePreparableReloadListener<RecipeMap> implements RecipeAccess {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceKey<RecipePropertySet>, RecipeManager.IngredientExtractor> RECIPE_PROPERTY_SETS = Map.of(
      RecipePropertySet.SMITHING_ADDITION,
      var0 -> var0 instanceof SmithingRecipe var1 ? var1.additionIngredient() : Optional.empty(),
      RecipePropertySet.SMITHING_BASE,
      var0 -> var0 instanceof SmithingRecipe var1 ? var1.baseIngredient() : Optional.empty(),
      RecipePropertySet.SMITHING_TEMPLATE,
      var0 -> var0 instanceof SmithingRecipe var1 ? var1.templateIngredient() : Optional.empty(),
      RecipePropertySet.FURNACE_INPUT,
      forSingleInput(RecipeType.SMELTING),
      RecipePropertySet.BLAST_FURNACE_INPUT,
      forSingleInput(RecipeType.BLASTING),
      RecipePropertySet.SMOKER_INPUT,
      forSingleInput(RecipeType.SMOKING),
      RecipePropertySet.CAMPFIRE_INPUT,
      forSingleInput(RecipeType.CAMPFIRE_COOKING)
   );
   private final HolderLookup.Provider registries;
   private RecipeMap recipes = RecipeMap.EMPTY;
   private Map<ResourceKey<RecipePropertySet>, RecipePropertySet> propertySets = Map.of();
   private SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes = SelectableRecipe.SingleInputSet.empty();
   private List<RecipeManager.ServerDisplayInfo> allDisplays = List.of();
   private Map<ResourceKey<Recipe<?>>, List<RecipeManager.ServerDisplayInfo>> recipeToDisplay = Map.of();

   public RecipeManager(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
   }

   protected RecipeMap prepare(ResourceManager var1, ProfilerFiller var2) {
      TreeMap var3 = new TreeMap();
      SimpleJsonResourceReloadListener.scanDirectory(
         var1, Registries.elementsDirPath(Registries.RECIPE), this.registries.createSerializationContext(JsonOps.INSTANCE), Recipe.CODEC, var3
      );
      ArrayList var4 = new ArrayList(var3.size());
      var3.forEach((var1x, var2x) -> {
         ResourceKey var3x = ResourceKey.create(Registries.RECIPE, var1x);
         RecipeHolder var4x = new RecipeHolder(var3x, var2x);
         var4.add(var4x);
      });
      return RecipeMap.create(var4);
   }

   protected void apply(RecipeMap var1, ResourceManager var2, ProfilerFiller var3) {
      this.recipes = var1;
      LOGGER.info("Loaded {} recipes", var1.values().size());
   }

   public void finalizeRecipeLoading(FeatureFlagSet var1) {
      ArrayList var2 = new ArrayList();
      List var3 = RECIPE_PROPERTY_SETS.entrySet().stream().map(var0 -> new RecipeManager.IngredientCollector(var0.getKey(), var0.getValue())).toList();
      this.recipes.values().forEach(var3x -> {
         Recipe var4 = var3x.value();
         if (!var4.isSpecial() && var4.placementInfo().isImpossibleToPlace()) {
            LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", var3x.id().location());
         } else {
            var3.forEach(var1xx -> var1xx.accept(var4));
            if (var4 instanceof StonecutterRecipe var5 && isIngredientEnabled(var1, var5.input()) && var5.resultDisplay().isEnabled(var1)) {
               var2.add(new SelectableRecipe.SingleInputEntry(var5.input(), new SelectableRecipe(var5.resultDisplay(), Optional.of(var3x))));
            }
         }
      });
      this.propertySets = var3.stream().collect(Collectors.toUnmodifiableMap(var0 -> var0.key, var1x -> var1x.asPropertySet(var1)));
      this.stonecutterRecipes = new SelectableRecipe.SingleInputSet<>(var2);
      this.allDisplays = unpackRecipeInfo(this.recipes.values(), var1);
      this.recipeToDisplay = this.allDisplays.stream().collect(Collectors.groupingBy(var0 -> var0.parent.id(), IdentityHashMap::new, Collectors.toList()));
   }

   static List<Ingredient> filterDisabled(FeatureFlagSet var0, List<Ingredient> var1) {
      var1.removeIf(var1x -> !isIngredientEnabled(var0, var1x));
      return var1;
   }

   private static boolean isIngredientEnabled(FeatureFlagSet var0, Ingredient var1) {
      return var1.items().stream().allMatch(var1x -> var1x.value().isEnabled(var0));
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(
      RecipeType<T> var1, I var2, Level var3, @Nullable ResourceKey<Recipe<?>> var4
   ) {
      RecipeHolder var5 = var4 != null ? this.byKeyTyped(var1, var4) : null;
      return this.getRecipeFor(var1, var2, var3, var5);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(
      RecipeType<T> var1, I var2, Level var3, @Nullable RecipeHolder<T> var4
   ) {
      return var4 != null && var4.value().matches(var2, var3) ? Optional.of(var4) : this.getRecipeFor(var1, var2, var3);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> var1, I var2, Level var3) {
      return this.recipes.<RecipeInput, T>getRecipesFor(var1, var2, var3).findFirst();
   }

   public Optional<RecipeHolder<?>> byKey(ResourceKey<Recipe<?>> var1) {
      return Optional.ofNullable(this.recipes.byKey(var1));
   }

   @Nullable
   private <T extends Recipe<?>> RecipeHolder<T> byKeyTyped(RecipeType<T> var1, ResourceKey<Recipe<?>> var2) {
      RecipeHolder var3 = this.recipes.byKey(var2);
      return var3 != null && var3.value().getType().equals(var1) ? var3 : null;
   }

   public Map<ResourceKey<RecipePropertySet>, RecipePropertySet> getSynchronizedItemProperties() {
      return this.propertySets;
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> getSynchronizedStonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   @Override
   public RecipePropertySet propertySet(ResourceKey<RecipePropertySet> var1) {
      return this.propertySets.getOrDefault(var1, RecipePropertySet.EMPTY);
   }

   @Override
   public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public Collection<RecipeHolder<?>> getRecipes() {
      return this.recipes.values();
   }

   @Nullable
   public RecipeManager.ServerDisplayInfo getRecipeFromDisplay(RecipeDisplayId var1) {
      return this.allDisplays.get(var1.index());
   }

   public void listDisplaysForRecipe(ResourceKey<Recipe<?>> var1, Consumer<RecipeDisplayEntry> var2) {
      List var3 = this.recipeToDisplay.get(var1);
      if (var3 != null) {
         var3.forEach(var1x -> var2.accept(var1x.display));
      }
   }

   @VisibleForTesting
   protected static RecipeHolder<?> fromJson(ResourceKey<Recipe<?>> var0, JsonObject var1, HolderLookup.Provider var2) {
      Recipe var3 = (Recipe)Recipe.CODEC.parse(var2.createSerializationContext(JsonOps.INSTANCE), var1).getOrThrow(JsonParseException::new);
      return new RecipeHolder(var0, var3);
   }

   public static <I extends RecipeInput, T extends Recipe<I>> RecipeManager.CachedCheck<I, T> createCheck(final RecipeType<T> var0) {
      return new RecipeManager.CachedCheck<I, T>() {
         @Nullable
         private ResourceKey<Recipe<?>> lastRecipe;

         @Override
         public Optional<RecipeHolder<T>> getRecipeFor(I var1, ServerLevel var2) {
            RecipeManager var3 = var2.recipeAccess();
            Optional var4 = var3.getRecipeFor(var0, var1, var2, this.lastRecipe);
            if (var4.isPresent()) {
               RecipeHolder var5 = (RecipeHolder)var4.get();
               this.lastRecipe = var5.id();
               return Optional.of(var5);
            } else {
               return Optional.empty();
            }
         }
      };
   }

   private static List<RecipeManager.ServerDisplayInfo> unpackRecipeInfo(Iterable<RecipeHolder<?>> var0, FeatureFlagSet var1) {
      ArrayList var2 = new ArrayList();
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();

      for (RecipeHolder var5 : var0) {
         Recipe var6 = var5.value();
         OptionalInt var7;
         if (var6.group().isEmpty()) {
            var7 = OptionalInt.empty();
         } else {
            var7 = OptionalInt.of(var3.computeIfAbsent(var6.group(), var1x -> var3.size()));
         }

         Optional var8;
         if (var6.isSpecial()) {
            var8 = Optional.empty();
         } else {
            var8 = Optional.of(var6.placementInfo().ingredients());
         }

         for (RecipeDisplay var10 : var6.display()) {
            if (var10.isEnabled(var1)) {
               int var11 = var2.size();
               RecipeDisplayId var12 = new RecipeDisplayId(var11);
               RecipeDisplayEntry var13 = new RecipeDisplayEntry(var12, var10, var7, var6.recipeBookCategory(), var8);
               var2.add(new RecipeManager.ServerDisplayInfo(var13, var5));
            }
         }
      }

      return var2;
   }

   private static RecipeManager.IngredientExtractor forSingleInput(RecipeType<? extends SingleItemRecipe> var0) {
      return var1 -> var1.getType() == var0 && var1 instanceof SingleItemRecipe var2 ? Optional.of(var2.input()) : Optional.empty();
   }

   public interface CachedCheck<I extends RecipeInput, T extends Recipe<I>> {
      Optional<RecipeHolder<T>> getRecipeFor(I var1, ServerLevel var2);
   }

   public static class IngredientCollector implements Consumer<Recipe<?>> {
      final ResourceKey<RecipePropertySet> key;
      private final RecipeManager.IngredientExtractor extractor;
      private final List<Ingredient> ingredients = new ArrayList<>();

      protected IngredientCollector(ResourceKey<RecipePropertySet> var1, RecipeManager.IngredientExtractor var2) {
         super();
         this.key = var1;
         this.extractor = var2;
      }

      public void accept(Recipe<?> var1) {
         this.extractor.apply(var1).ifPresent(this.ingredients::add);
      }

      public RecipePropertySet asPropertySet(FeatureFlagSet var1) {
         return RecipePropertySet.create(RecipeManager.filterDisabled(var1, this.ingredients));
      }
   }

   @FunctionalInterface
   public interface IngredientExtractor {
      Optional<Ingredient> apply(Recipe<?> var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
