package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class RecipeMap {
   public static final RecipeMap EMPTY = new RecipeMap(ImmutableMultimap.of(), Map.of());
   private final Multimap<RecipeType<?>, RecipeHolder<?>> byType;
   private final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey;

   private RecipeMap(final Multimap<RecipeType<?>, RecipeHolder<?>> byType, final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey) {
      super();
      this.byType = byType;
      this.byKey = byKey;
   }

   public static RecipeMap create(final HolderLookup<Recipe<?>> recipes) {
      ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.builder();
      ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey = ImmutableMap.builder();
      recipes.listElements().forEach((recipe) -> {
         RecipeHolder<? extends Recipe<?>> legacyRecipeHolder = new RecipeHolder<Recipe<?>>(recipe.key(), (Recipe)recipe.value());
         byType.put(((Recipe)recipe.value()).getType(), legacyRecipeHolder);
         byKey.put(recipe.key(), legacyRecipeHolder);
      });
      return new RecipeMap(byType.build(), byKey.build());
   }

   public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(final RecipeType<T> type) {
      return this.byType.get(type);
   }

   public Collection<RecipeHolder<?>> values() {
      return this.byKey.values();
   }

   public @Nullable RecipeHolder<?> byKey(final ResourceKey<Recipe<?>> recipeId) {
      return (RecipeHolder)this.byKey.get(recipeId);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getRecipesFor(final RecipeType<T> type, final I container, final Level level) {
      return container.isEmpty() ? Stream.empty() : this.byType(type).stream().filter((r) -> r.value().matches(container, level));
   }
}
