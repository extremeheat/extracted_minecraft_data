package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class RecipeMap {
   public static final RecipeMap EMPTY = new RecipeMap(ImmutableMultimap.of(), Map.of());
   private final Multimap<RecipeType<?>, RecipeHolder<?>> byType;
   private final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey;

   private RecipeMap(Multimap<RecipeType<?>, RecipeHolder<?>> var1, Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> var2) {
      super();
      this.byType = var1;
      this.byKey = var2;
   }

   public static RecipeMap create(Iterable<RecipeHolder<?>> var0) {
      Builder var1 = ImmutableMultimap.builder();
      com.google.common.collect.ImmutableMap.Builder var2 = ImmutableMap.builder();

      for (RecipeHolder var4 : var0) {
         var1.put(var4.value().getType(), var4);
         var2.put(var4.id(), var4);
      }

      return new RecipeMap(var1.build(), var2.build());
   }

   public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> var1) {
      return this.byType.get(var1);
   }

   public Collection<RecipeHolder<?>> values() {
      return this.byKey.values();
   }

   @Nullable
   public RecipeHolder<?> byKey(ResourceKey<Recipe<?>> var1) {
      return this.byKey.get(var1);
   }

   public <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getRecipesFor(RecipeType<T> var1, I var2, Level var3) {
      return var2.isEmpty() ? Stream.empty() : this.byType(var1).stream().filter(var2x -> var2x.value().matches((I)var2, var3));
   }
}
