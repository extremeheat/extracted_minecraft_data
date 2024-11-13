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
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager extends SimplePreparableReloadListener<RecipeMap> implements RecipeAccess {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceKey<RecipePropertySet>, IngredientExtractor> RECIPE_PROPERTY_SETS;
   private static final FileToIdConverter RECIPE_LISTER;
   private final HolderLookup.Provider registries;
   private RecipeMap recipes;
   private Map<ResourceKey<RecipePropertySet>, RecipePropertySet> propertySets;
   private SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes;
   private List<ServerDisplayInfo> allDisplays;
   private Map<ResourceKey<Recipe<?>>, List<ServerDisplayInfo>> recipeToDisplay;

   public RecipeManager(HolderLookup.Provider var1) {
      super();
      this.recipes = RecipeMap.EMPTY;
      this.propertySets = Map.of();
      this.stonecutterRecipes = SelectableRecipe.SingleInputSet.<StonecutterRecipe>empty();
      this.allDisplays = List.of();
      this.recipeToDisplay = Map.of();
      this.registries = var1;
   }

   protected RecipeMap prepare(ResourceManager var1, ProfilerFiller var2) {
      TreeMap var3 = new TreeMap();
      SimpleJsonResourceReloadListener.scanDirectory(var1, RECIPE_LISTER, this.registries.createSerializationContext(JsonOps.INSTANCE), Recipe.CODEC, var3);
      ArrayList var4 = new ArrayList(var3.size());
      var3.forEach((var1x, var2x) -> {
         ResourceKey var3 = ResourceKey.create(Registries.RECIPE, var1x);
         RecipeHolder var4x = new RecipeHolder(var3, var2x);
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
      List var3 = RECIPE_PROPERTY_SETS.entrySet().stream().map((var0) -> new IngredientCollector((ResourceKey)var0.getKey(), (IngredientExtractor)var0.getValue())).toList();
      this.recipes.values().forEach((var3x) -> {
         Recipe var4 = var3x.value();
         if (!var4.isSpecial() && var4.placementInfo().isImpossibleToPlace()) {
            LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", var3x.id().location());
         } else {
            var3.forEach((var1x) -> var1x.accept(var4));
            if (var4 instanceof StonecutterRecipe) {
               StonecutterRecipe var5 = (StonecutterRecipe)var4;
               if (isIngredientEnabled(var1, var5.input()) && var5.resultDisplay().isEnabled(var1)) {
                  var2.add(new SelectableRecipe.SingleInputEntry(var5.input(), new SelectableRecipe(var5.resultDisplay(), Optional.of(var3x))));
               }
            }

         }
      });
      this.propertySets = (Map)var3.stream().collect(Collectors.toUnmodifiableMap((var0) -> var0.key, (var1x) -> var1x.asPropertySet(var1)));
      this.stonecutterRecipes = new SelectableRecipe.SingleInputSet<StonecutterRecipe>(var2);
      this.allDisplays = unpackRecipeInfo(this.recipes.values(), var1);
      this.recipeToDisplay = (Map)this.allDisplays.stream().collect(Collectors.groupingBy((var0) -> var0.parent.id(), IdentityHashMap::new, Collectors.toList()));
   }

   static List<Ingredient> filterDisabled(FeatureFlagSet var0, List<Ingredient> var1) {
      var1.removeIf((var1x) -> !isIngredientEnabled(var0, var1x));
      return var1;
   }

   private static boolean isIngredientEnabled(FeatureFlagSet var0, Ingredient var1) {
      return var1.items().allMatch((var1x) -> ((Item)var1x.value()).isEnabled(var0));
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> var1, I var2, Level var3, @Nullable ResourceKey<Recipe<?>> var4) {
      RecipeHolder var5 = var4 != null ? this.byKeyTyped(var1, var4) : null;
      return this.getRecipeFor(var1, var2, var3, var5);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> var1, I var2, Level var3, @Nullable RecipeHolder<T> var4) {
      return var4 != null && var4.value().matches(var2, var3) ? Optional.of(var4) : this.getRecipeFor(var1, var2, var3);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> var1, I var2, Level var3) {
      return this.recipes.getRecipesFor(var1, var2, var3).findFirst();
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

   public RecipePropertySet propertySet(ResourceKey<RecipePropertySet> var1) {
      return (RecipePropertySet)this.propertySets.getOrDefault(var1, RecipePropertySet.EMPTY);
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public Collection<RecipeHolder<?>> getRecipes() {
      return this.recipes.values();
   }

   @Nullable
   public ServerDisplayInfo getRecipeFromDisplay(RecipeDisplayId var1) {
      return (ServerDisplayInfo)this.allDisplays.get(var1.index());
   }

   public void listDisplaysForRecipe(ResourceKey<Recipe<?>> var1, Consumer<RecipeDisplayEntry> var2) {
      List var3 = (List)this.recipeToDisplay.get(var1);
      if (var3 != null) {
         var3.forEach((var1x) -> var2.accept(var1x.display));
      }

   }

   @VisibleForTesting
   protected static RecipeHolder<?> fromJson(ResourceKey<Recipe<?>> var0, JsonObject var1, HolderLookup.Provider var2) {
      Recipe var3 = (Recipe)Recipe.CODEC.parse(var2.createSerializationContext(JsonOps.INSTANCE), var1).getOrThrow(JsonParseException::new);
      return new RecipeHolder(var0, var3);
   }

   public static <I extends RecipeInput, T extends Recipe<I>> CachedCheck<I, T> createCheck(final RecipeType<T> var0) {
      return new CachedCheck<I, T>() {
         @Nullable
         private ResourceKey<Recipe<?>> lastRecipe;

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

   private static List<ServerDisplayInfo> unpackRecipeInfo(Iterable<RecipeHolder<?>> var0, FeatureFlagSet var1) {
      ArrayList var2 = new ArrayList();
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();

      for(RecipeHolder var5 : var0) {
         Recipe var6 = var5.value();
         OptionalInt var7;
         if (var6.group().isEmpty()) {
            var7 = OptionalInt.empty();
         } else {
            var7 = OptionalInt.of(var3.computeIfAbsent(var6.group(), (var1x) -> var3.size()));
         }

         Optional var8;
         if (var6.isSpecial()) {
            var8 = Optional.empty();
         } else {
            var8 = Optional.of(var6.placementInfo().ingredients());
         }

         for(RecipeDisplay var10 : var6.display()) {
            if (var10.isEnabled(var1)) {
               int var11 = var2.size();
               RecipeDisplayId var12 = new RecipeDisplayId(var11);
               RecipeDisplayEntry var13 = new RecipeDisplayEntry(var12, var10, var7, var6.recipeBookCategory(), var8);
               var2.add(new ServerDisplayInfo(var13, var5));
            }
         }
      }

      return var2;
   }

   private static IngredientExtractor forSingleInput(RecipeType<? extends SingleItemRecipe> var0) {
      return (var1) -> {
         Optional var10000;
         if (var1.getType() == var0 && var1 instanceof SingleItemRecipe var2) {
            var10000 = Optional.of(var2.input());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      };
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   static {
      RECIPE_PROPERTY_SETS = Map.of(RecipePropertySet.SMITHING_ADDITION, (IngredientExtractor)(var0) -> {
         Optional var10000;
         if (var0 instanceof SmithingRecipe var1) {
            var10000 = var1.additionIngredient();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.SMITHING_BASE, (IngredientExtractor)(var0) -> {
         Optional var10000;
         if (var0 instanceof SmithingRecipe var1) {
            var10000 = var1.baseIngredient();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.SMITHING_TEMPLATE, (IngredientExtractor)(var0) -> {
         Optional var10000;
         if (var0 instanceof SmithingRecipe var1) {
            var10000 = var1.templateIngredient();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.FURNACE_INPUT, forSingleInput(RecipeType.SMELTING), RecipePropertySet.BLAST_FURNACE_INPUT, forSingleInput(RecipeType.BLASTING), RecipePropertySet.SMOKER_INPUT, forSingleInput(RecipeType.SMOKING), RecipePropertySet.CAMPFIRE_INPUT, forSingleInput(RecipeType.CAMPFIRE_COOKING));
      RECIPE_LISTER = FileToIdConverter.registry(Registries.RECIPE);
   }

   public static record ServerDisplayInfo(RecipeDisplayEntry display, RecipeHolder<?> parent) {
      final RecipeDisplayEntry display;
      final RecipeHolder<?> parent;

      public ServerDisplayInfo(RecipeDisplayEntry var1, RecipeHolder<?> var2) {
         super();
         this.display = var1;
         this.parent = var2;
      }
   }

   public static class IngredientCollector implements Consumer<Recipe<?>> {
      final ResourceKey<RecipePropertySet> key;
      private final IngredientExtractor extractor;
      private final List<Ingredient> ingredients = new ArrayList();

      protected IngredientCollector(ResourceKey<RecipePropertySet> var1, IngredientExtractor var2) {
         super();
         this.key = var1;
         this.extractor = var2;
      }

      public void accept(Recipe<?> var1) {
         Optional var10000 = this.extractor.apply(var1);
         List var10001 = this.ingredients;
         Objects.requireNonNull(var10001);
         var10000.ifPresent(var10001::add);
      }

      public RecipePropertySet asPropertySet(FeatureFlagSet var1) {
         return RecipePropertySet.create(RecipeManager.filterDisabled(var1, this.ingredients));
      }

      // $FF: synthetic method
      public void accept(final Object var1) {
         this.accept((Recipe)var1);
      }
   }

   public interface CachedCheck<I extends RecipeInput, T extends Recipe<I>> {
      Optional<RecipeHolder<T>> getRecipeFor(I var1, ServerLevel var2);
   }

   @FunctionalInterface
   public interface IngredientExtractor {
      Optional<Ingredient> apply(Recipe<?> var1);
   }
}
