package net.minecraft.world.item.crafting;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RecipeManager implements RecipeAccess {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceKey<RecipePropertySet>, IngredientExtractor> RECIPE_PROPERTY_SETS;
   private final RecipeMap recipes;
   private Map<ResourceKey<RecipePropertySet>, RecipePropertySet> propertySets = Map.of();
   private SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes = SelectableRecipe.SingleInputSet.<StonecutterRecipe>empty();
   private List<ServerDisplayInfo> allDisplays = List.of();
   private Map<ResourceKey<Recipe<?>>, List<ServerDisplayInfo>> recipeToDisplay = Map.of();
   private final Collection<RecipeHolder<?>> learnableRecipes;

   public RecipeManager(final HolderLookup.Provider registries) {
      super();
      HolderLookup.RegistryLookup<Recipe<?>> recipeRegistries = registries.lookupOrThrow(Registries.RECIPE);
      this.recipes = RecipeMap.create(recipeRegistries);
      this.learnableRecipes = (Collection)this.recipes.values().stream().filter((r) -> !r.value().isSpecial()).collect(Collectors.toUnmodifiableList());
   }

   public void finalizeRecipeLoading(final FeatureFlagSet enabledFlags) {
      List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> stonecutterRecipes = new ArrayList();
      List<IngredientCollector> propertySetCollectors = RECIPE_PROPERTY_SETS.entrySet().stream().map((e) -> new IngredientCollector((ResourceKey)e.getKey(), (IngredientExtractor)e.getValue())).toList();
      this.recipes.values().forEach((recipeHolder) -> {
         Recipe<?> recipe = recipeHolder.value();
         if (!recipe.isSpecial() && recipe.placementInfo().isImpossibleToPlace()) {
            LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", recipeHolder.id().identifier());
         } else {
            propertySetCollectors.forEach((c) -> c.accept(recipe));
            if (recipe instanceof StonecutterRecipe) {
               StonecutterRecipe stonecutterRecipe = (StonecutterRecipe)recipe;
               if (isIngredientEnabled(enabledFlags, stonecutterRecipe.input()) && stonecutterRecipe.resultDisplay().isEnabled(enabledFlags)) {
                  stonecutterRecipes.add(new SelectableRecipe.SingleInputEntry(stonecutterRecipe.input(), new SelectableRecipe(stonecutterRecipe.resultDisplay(), Optional.of(recipeHolder))));
               }
            }

         }
      });
      this.propertySets = (Map)propertySetCollectors.stream().collect(Collectors.toUnmodifiableMap((c) -> c.key, (c) -> c.asPropertySet(enabledFlags)));
      this.stonecutterRecipes = new SelectableRecipe.SingleInputSet<StonecutterRecipe>(stonecutterRecipes);
      this.allDisplays = unpackRecipeInfo(this.recipes.values(), enabledFlags);
      this.recipeToDisplay = (Map)this.allDisplays.stream().collect(Collectors.groupingBy((r) -> r.parent.id(), IdentityHashMap::new, Collectors.toList()));
   }

   private static List<Ingredient> filterDisabled(final FeatureFlagSet enabledFlags, final List<Ingredient> ingredients) {
      ingredients.removeIf((e) -> !isIngredientEnabled(enabledFlags, e));
      return ingredients;
   }

   private static boolean isIngredientEnabled(final FeatureFlagSet enabledFlags, final Ingredient ingredient) {
      return ingredient.items().allMatch((i) -> ((Item)i.value()).isEnabled(enabledFlags));
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(final RecipeType<T> type, final I input, final Level level, final @Nullable ResourceKey<Recipe<?>> recipeHint) {
      RecipeHolder<T> hintedRecipe = recipeHint != null ? this.byKeyTyped(type, recipeHint) : null;
      return this.getRecipeFor(type, input, level, hintedRecipe);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(final RecipeType<T> type, final I input, final Level level, final @Nullable RecipeHolder<T> recipeHint) {
      return recipeHint != null && recipeHint.value().matches(input, level) ? Optional.of(recipeHint) : this.getRecipeFor(type, input, level);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(final RecipeType<T> type, final I input, final Level level) {
      return this.recipes.getRecipesFor(type, input, level).findFirst();
   }

   public Optional<RecipeHolder<?>> byKey(final ResourceKey<Recipe<?>> recipeId) {
      return Optional.ofNullable(this.recipes.byKey(recipeId));
   }

   private <T extends Recipe<?>> @Nullable RecipeHolder<T> byKeyTyped(final RecipeType<T> type, final ResourceKey<Recipe<?>> recipeId) {
      RecipeHolder<?> recipe = this.recipes.byKey(recipeId);
      return recipe != null && recipe.value().getType().equals(type) ? recipe : null;
   }

   public Map<ResourceKey<RecipePropertySet>, RecipePropertySet> getSynchronizedItemProperties() {
      return this.propertySets;
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> getSynchronizedStonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public RecipePropertySet propertySet(final ResourceKey<RecipePropertySet> id) {
      return (RecipePropertySet)this.propertySets.getOrDefault(id, RecipePropertySet.EMPTY);
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public Collection<RecipeHolder<?>> getRecipes() {
      return this.recipes.values();
   }

   public Collection<RecipeHolder<?>> getLearnableRecipes() {
      return this.learnableRecipes;
   }

   public @Nullable ServerDisplayInfo getRecipeFromDisplay(final RecipeDisplayId id) {
      int index = id.index();
      return index >= 0 && index < this.allDisplays.size() ? (ServerDisplayInfo)this.allDisplays.get(index) : null;
   }

   public void listDisplaysForRecipe(final ResourceKey<Recipe<?>> id, final Consumer<RecipeDisplayEntry> output) {
      List<ServerDisplayInfo> recipes = (List)this.recipeToDisplay.get(id);
      if (recipes != null) {
         recipes.forEach((e) -> output.accept(e.display));
      }

   }

   public static <I extends RecipeInput, T extends Recipe<I>> CachedCheck<I, T> createCheck(final RecipeType<T> type) {
      return new CachedCheck<I, T>() {
         private @Nullable ResourceKey<Recipe<?>> lastRecipe;

         public Optional<RecipeHolder<T>> getRecipeFor(final I input, final ServerLevel level) {
            RecipeManager recipeManager = level.recipeAccess();
            Optional<RecipeHolder<T>> result = recipeManager.getRecipeFor(type, input, level, this.lastRecipe);
            if (result.isPresent()) {
               RecipeHolder<T> unpackedResult = (RecipeHolder)result.get();
               this.lastRecipe = unpackedResult.id();
               return Optional.of(unpackedResult);
            } else {
               return Optional.empty();
            }
         }
      };
   }

   private static List<ServerDisplayInfo> unpackRecipeInfo(final Iterable<RecipeHolder<?>> recipes, final FeatureFlagSet enabledFeatures) {
      List<ServerDisplayInfo> result = new ArrayList();
      Object2IntMap<String> recipeGroups = new Object2IntOpenHashMap();

      for(RecipeHolder<?> recipeHolder : recipes) {
         Recipe<?> recipe = recipeHolder.value();
         OptionalInt groupId;
         if (recipe.group().isEmpty()) {
            groupId = OptionalInt.empty();
         } else {
            groupId = OptionalInt.of(recipeGroups.computeIfAbsent(recipe.group(), (idx) -> recipeGroups.size()));
         }

         Optional<List<Ingredient>> placementCheck;
         if (recipe.isSpecial()) {
            placementCheck = Optional.empty();
         } else {
            placementCheck = Optional.of(recipe.placementInfo().ingredients());
         }

         for(RecipeDisplay recipeDisplay : recipe.display()) {
            if (recipeDisplay.isEnabled(enabledFeatures)) {
               int nextDisplayId = result.size();
               RecipeDisplayId id = new RecipeDisplayId(nextDisplayId);
               RecipeDisplayEntry entry = new RecipeDisplayEntry(id, recipeDisplay, groupId, recipe.recipeBookCategory(), placementCheck);
               result.add(new ServerDisplayInfo(entry, recipeHolder));
            }
         }
      }

      return result;
   }

   private static IngredientExtractor forSingleInput(final RecipeType<? extends SingleItemRecipe> type) {
      return (recipe) -> {
         Optional var10000;
         if (recipe.getType() == type && recipe instanceof SingleItemRecipe singleItemRecipe) {
            var10000 = Optional.of(singleItemRecipe.input());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      };
   }

   static {
      RECIPE_PROPERTY_SETS = Map.of(RecipePropertySet.SMITHING_ADDITION, (IngredientExtractor)(recipe) -> {
         Optional var10000;
         if (recipe instanceof SmithingRecipe smithingRecipe) {
            var10000 = smithingRecipe.additionIngredient();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.SMITHING_BASE, (IngredientExtractor)(recipe) -> {
         Optional var10000;
         if (recipe instanceof SmithingRecipe smithingRecipe) {
            var10000 = Optional.of(smithingRecipe.baseIngredient());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.SMITHING_TEMPLATE, (IngredientExtractor)(recipe) -> {
         Optional var10000;
         if (recipe instanceof SmithingRecipe smithingRecipe) {
            var10000 = smithingRecipe.templateIngredient();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.FURNACE_INPUT, forSingleInput(RecipeType.SMELTING), RecipePropertySet.BLAST_FURNACE_INPUT, forSingleInput(RecipeType.BLASTING), RecipePropertySet.SMOKER_INPUT, forSingleInput(RecipeType.SMOKING), RecipePropertySet.CAMPFIRE_INPUT, forSingleInput(RecipeType.CAMPFIRE_COOKING), RecipePropertySet.BREWING_INPUTS, (IngredientExtractor)(recipe) -> {
         Optional var10000;
         if (recipe instanceof BrewingRecipe brewingRecipe) {
            var10000 = Optional.of(brewingRecipe.getInput().ingredient());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.BREWING_REAGENTS, (IngredientExtractor)(recipe) -> {
         Optional var10000;
         if (recipe instanceof BrewingRecipe brewingRecipe) {
            var10000 = Optional.of(brewingRecipe.getReagent().ingredient());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      });
   }

   public static record ServerDisplayInfo(RecipeDisplayEntry display, RecipeHolder<?> parent) {
      public ServerDisplayInfo {
         super();
      }
   }

   public static class IngredientCollector implements Consumer<Recipe<?>> {
      private final ResourceKey<RecipePropertySet> key;
      private final IngredientExtractor extractor;
      private final List<Ingredient> ingredients = new ArrayList();

      protected IngredientCollector(final ResourceKey<RecipePropertySet> key, final IngredientExtractor extractor) {
         super();
         this.key = key;
         this.extractor = extractor;
      }

      public void accept(final Recipe<?> recipe) {
         Optional var10000 = this.extractor.apply(recipe);
         List var10001 = this.ingredients;
         Objects.requireNonNull(var10001);
         var10000.ifPresent(var10001::add);
      }

      public RecipePropertySet asPropertySet(final FeatureFlagSet enabledFeatures) {
         return RecipePropertySet.create(RecipeManager.filterDisabled(enabledFeatures, this.ingredients));
      }
   }

   public interface CachedCheck<I extends RecipeInput, T extends Recipe<I>> {
      Optional<RecipeHolder<T>> getRecipeFor(I input, ServerLevel level);
   }

   @FunctionalInterface
   public interface IngredientExtractor {
      Optional<Ingredient> apply(Recipe<?> recipe);
   }
}
