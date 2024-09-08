package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager extends SimpleJsonResourceReloadListener<Recipe<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private Multimap<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.of();
   private Map<ResourceLocation, RecipeHolder<?>> byName = ImmutableMap.of();
   @Nullable
   private List<RecipeHolder<?>> synchronizedRecipes;
   private boolean hasErrors;

   public RecipeManager(HolderLookup.Provider var1) {
      super(var1, Recipe.CODEC, Registries.elementsDirPath(Registries.RECIPE));
   }

   protected void apply(Map<ResourceLocation, Recipe<?>> var1, ResourceManager var2, ProfilerFiller var3) {
      this.hasErrors = false;
      Builder var4 = ImmutableMultimap.builder();
      com.google.common.collect.ImmutableMap.Builder var5 = ImmutableMap.builder();

      for (Entry var7 : var1.entrySet()) {
         ResourceLocation var8 = (ResourceLocation)var7.getKey();
         Recipe var9 = (Recipe)var7.getValue();

         try {
            RecipeHolder var10 = new RecipeHolder(var8, var9);
            var4.put(var9.getType(), var10);
            var5.put(var8, var10);
         } catch (IllegalArgumentException | JsonParseException var11) {
            LOGGER.error("Parsing error loading recipe {}", var8, var11);
         }
      }

      this.byType = var4.build();
      this.byName = var5.build();
      this.synchronizedRecipes = null;
      LOGGER.info("Loaded {} recipes", this.byType.size());
   }

   public void logImpossibleRecipes() {
      this.byName.values().forEach(var0 -> {
         Recipe var1 = var0.value();
         if (!var1.isSpecial() && var1.placementInfo().isImpossibleToPlace()) {
            LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", var0.id());
         }
      });
   }

   public boolean hadErrorsLoading() {
      return this.hasErrors;
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> var1, I var2, Level var3) {
      return this.getRecipeFor(var1, var2, var3, (RecipeHolder<T>)null);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(
      RecipeType<T> var1, I var2, Level var3, @Nullable ResourceLocation var4
   ) {
      RecipeHolder var5 = var4 != null ? this.byKeyTyped(var1, var4) : null;
      return this.getRecipeFor(var1, var2, var3, var5);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(
      RecipeType<T> var1, I var2, Level var3, @Nullable RecipeHolder<T> var4
   ) {
      if (var2.isEmpty()) {
         return Optional.empty();
      } else {
         return var4 != null && var4.value().matches(var2, var3)
            ? Optional.of(var4)
            : this.byType(var1).stream().filter(var2x -> var2x.value().matches((I)var2, var3)).findFirst();
      }
   }

   public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(RecipeType<T> var1) {
      return List.copyOf(this.byType(var1));
   }

   public <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> var1, I var2, Level var3) {
      return this.byType(var1)
         .stream()
         .filter(var2x -> var2x.value().matches(var2, var3))
         .sorted(Comparator.comparing(var1x -> var1x.value().getResultItem(var3.registryAccess()).getItem().getDescriptionId()))
         .collect(Collectors.toList());
   }

   private <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> var1) {
      return this.byType.get(var1);
   }

   public <I extends RecipeInput, T extends Recipe<I>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> var1, I var2, Level var3) {
      Optional var4 = this.getRecipeFor(var1, var2, var3);
      if (var4.isPresent()) {
         return ((RecipeHolder)var4.get()).value().getRemainingItems(var2);
      } else {
         NonNullList var5 = NonNullList.withSize(var2.size(), ItemStack.EMPTY);

         for (int var6 = 0; var6 < var5.size(); var6++) {
            var5.set(var6, var2.getItem(var6));
         }

         return var5;
      }
   }

   public Optional<RecipeHolder<?>> byKey(ResourceLocation var1) {
      return Optional.ofNullable(this.byName.get(var1));
   }

   @Nullable
   private <T extends Recipe<?>> RecipeHolder<T> byKeyTyped(RecipeType<T> var1, ResourceLocation var2) {
      RecipeHolder var3 = this.byName.get(var2);
      return var3 != null && var3.value().getType().equals(var1) ? var3 : null;
   }

   public Collection<RecipeHolder<?>> getOrderedRecipes() {
      return this.byType.values();
   }

   public Collection<RecipeHolder<?>> getSynchronizedRecipes() {
      if (this.synchronizedRecipes == null) {
         this.synchronizedRecipes = this.getOrderedRecipes().stream().filter(var0 -> !var0.value().placementInfo().isImpossibleToPlace()).toList();
      }

      return this.synchronizedRecipes;
   }

   public Collection<RecipeHolder<?>> getRecipes() {
      return this.byName.values();
   }

   public Stream<ResourceLocation> getRecipeIds() {
      return this.byName.keySet().stream();
   }

   @VisibleForTesting
   protected static RecipeHolder<?> fromJson(ResourceLocation var0, JsonObject var1, HolderLookup.Provider var2) {
      Recipe var3 = (Recipe)Recipe.CODEC.parse(var2.createSerializationContext(JsonOps.INSTANCE), var1).getOrThrow(JsonParseException::new);
      return new RecipeHolder(var0, var3);
   }

   public void replaceRecipes(Iterable<RecipeHolder<?>> var1) {
      this.hasErrors = false;
      Builder var2 = ImmutableMultimap.builder();
      com.google.common.collect.ImmutableMap.Builder var3 = ImmutableMap.builder();

      for (RecipeHolder var5 : var1) {
         RecipeType var6 = var5.value().getType();
         var2.put(var6, var5);
         var3.put(var5.id(), var5);
      }

      this.byType = var2.build();
      this.byName = var3.build();
      this.synchronizedRecipes = null;
   }

   public static <I extends RecipeInput, T extends Recipe<I>> RecipeManager.CachedCheck<I, T> createCheck(final RecipeType<T> var0) {
      return new RecipeManager.CachedCheck<I, T>() {
         @Nullable
         private ResourceLocation lastRecipe;

         @Override
         public Optional<RecipeHolder<T>> getRecipeFor(I var1, Level var2) {
            RecipeManager var3 = var2.getRecipeManager();
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

   public interface CachedCheck<I extends RecipeInput, T extends Recipe<I>> {
      Optional<RecipeHolder<T>> getRecipeFor(I var1, Level var2);
   }
}
