package net.minecraft.core.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public interface DataComponentMap extends Iterable<TypedDataComponent<?>> {
   DataComponentMap EMPTY = new DataComponentMap() {
      @Nullable
      @Override
      public <T> T get(DataComponentType<? extends T> var1) {
         return null;
      }

      @Override
      public Set<DataComponentType<?>> keySet() {
         return Set.of();
      }

      @Override
      public Iterator<TypedDataComponent<?>> iterator() {
         return Collections.emptyIterator();
      }
   };
   Codec<DataComponentMap> CODEC = DataComponentType.VALUE_MAP_CODEC.flatComapMap(DataComponentMap.Builder::buildFromMapTrusted, var0 -> {
      int var1 = var0.size();
      if (var1 == 0) {
         return DataResult.success(Reference2ObjectMaps.emptyMap());
      } else {
         Reference2ObjectArrayMap var2 = new Reference2ObjectArrayMap(var1);

         for(TypedDataComponent var4 : var0) {
            if (!var4.type().isTransient()) {
               var2.put(var4.type(), var4.value());
            }
         }

         return DataResult.success(var2);
      }
   });

   static DataComponentMap.Builder builder() {
      return new DataComponentMap.Builder();
   }

   @Nullable
   <T> T get(DataComponentType<? extends T> var1);

   Set<DataComponentType<?>> keySet();

   default boolean has(DataComponentType<?> var1) {
      return this.get(var1) != null;
   }

   default <T> T getOrDefault(DataComponentType<? extends T> var1, T var2) {
      Object var3 = this.get(var1);
      return (T)(var3 != null ? var3 : var2);
   }

   @Nullable
   default <T> TypedDataComponent<T> getTyped(DataComponentType<T> var1) {
      Object var2 = this.get(var1);
      return var2 != null ? new TypedDataComponent<>(var1, (T)var2) : null;
   }

   @Override
   default Iterator<TypedDataComponent<?>> iterator() {
      return Iterators.transform(this.keySet().iterator(), var1 -> Objects.requireNonNull(this.getTyped(var1)));
   }

   default Stream<TypedDataComponent<?>> stream() {
      return StreamSupport.stream(Spliterators.spliterator(this.iterator(), (long)this.size(), 1345), false);
   }

   default int size() {
      return this.keySet().size();
   }

   default boolean isEmpty() {
      return this.size() == 0;
   }

   default DataComponentMap filter(final Predicate<DataComponentType<?>> var1) {
      return new DataComponentMap() {
         @Nullable
         @Override
         public <T> T get(DataComponentType<? extends T> var1x) {
            return var1.test(var1x) ? DataComponentMap.this.get(var1x) : null;
         }

         @Override
         public Set<DataComponentType<?>> keySet() {
            return Sets.filter(DataComponentMap.this.keySet(), var1::test);
         }
      };
   }

   public static class Builder {
      private final Reference2ObjectMap<DataComponentType<?>, Object> map = new Reference2ObjectArrayMap();

      Builder() {
         super();
      }

      public <T> DataComponentMap.Builder set(DataComponentType<T> var1, @Nullable T var2) {
         this.setUnchecked(var1, var2);
         return this;
      }

      <T> void setUnchecked(DataComponentType<T> var1, @Nullable Object var2) {
         if (var2 != null) {
            this.map.put(var1, var2);
         } else {
            this.map.remove(var1);
         }
      }

      public DataComponentMap.Builder addAll(DataComponentMap var1) {
         for(TypedDataComponent var3 : var1) {
            this.map.put(var3.type(), var3.value());
         }

         return this;
      }

      public DataComponentMap build() {
         return buildFromMapTrusted(this.map);
      }

      private static DataComponentMap buildFromMapTrusted(Map<DataComponentType<?>, Object> var0) {
         if (var0.isEmpty()) {
            return DataComponentMap.EMPTY;
         } else {
            return var0.size() < 8
               ? new DataComponentMap.Builder.SimpleMap(new Reference2ObjectArrayMap(var0))
               : new DataComponentMap.Builder.SimpleMap(new Reference2ObjectOpenHashMap(var0));
         }
      }

      static record SimpleMap(Reference2ObjectMap<DataComponentType<?>, Object> c) implements DataComponentMap {
         private final Reference2ObjectMap<DataComponentType<?>, Object> map;

         SimpleMap(Reference2ObjectMap<DataComponentType<?>, Object> var1) {
            super();
            this.map = var1;
         }

         @Nullable
         @Override
         public <T> T get(DataComponentType<? extends T> var1) {
            return (T)this.map.get(var1);
         }

         @Override
         public boolean has(DataComponentType<?> var1) {
            return this.map.containsKey(var1);
         }

         @Override
         public Set<DataComponentType<?>> keySet() {
            return this.map.keySet();
         }

         @Override
         public Iterator<TypedDataComponent<?>> iterator() {
            return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), TypedDataComponent::fromEntryUnchecked);
         }

         @Override
         public int size() {
            return this.map.size();
         }

         @Override
         public String toString() {
            return this.map.toString();
         }
      }
   }
}
