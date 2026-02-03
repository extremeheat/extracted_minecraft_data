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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jspecify.annotations.Nullable;

public interface DataComponentMap extends Iterable<TypedDataComponent<?>>, DataComponentGetter {
   DataComponentMap EMPTY = new DataComponentMap() {
      public <T> @Nullable T get(final DataComponentType<? extends T> type) {
         return null;
      }

      public Set<DataComponentType<?>> keySet() {
         return Set.of();
      }

      public Iterator<TypedDataComponent<?>> iterator() {
         return Collections.emptyIterator();
      }
   };
   Codec<DataComponentMap> CODEC = makeCodecFromMap(DataComponentType.VALUE_MAP_CODEC);

   static Codec<DataComponentMap> makeCodec(final Codec<DataComponentType<?>> componentTypeCodec) {
      return makeCodecFromMap(Codec.dispatchedMap(componentTypeCodec, DataComponentType::codecOrThrow));
   }

   static Codec<DataComponentMap> makeCodecFromMap(final Codec<Map<DataComponentType<?>, Object>> mapCodec) {
      return mapCodec.flatComapMap(Builder::buildFromMapTrusted, (components) -> {
         int size = components.size();
         if (size == 0) {
            return DataResult.success(Reference2ObjectMaps.emptyMap());
         } else {
            Reference2ObjectMap<DataComponentType<?>, Object> map = new Reference2ObjectArrayMap(size);

            for(TypedDataComponent<?> entry : components) {
               if (!entry.type().isTransient()) {
                  map.put(entry.type(), entry.value());
               }
            }

            return DataResult.success(map);
         }
      });
   }

   static DataComponentMap composite(final DataComponentMap prototype, final DataComponentMap overrides) {
      return new DataComponentMap() {
         public <T> @Nullable T get(final DataComponentType<? extends T> type) {
            T value = (T)overrides.get(type);
            return (T)(value != null ? value : prototype.get(type));
         }

         public Set<DataComponentType<?>> keySet() {
            return Sets.union(prototype.keySet(), overrides.keySet());
         }
      };
   }

   static Builder builder() {
      return new Builder();
   }

   Set<DataComponentType<?>> keySet();

   default boolean has(final DataComponentType<?> type) {
      return this.get(type) != null;
   }

   default Iterator<TypedDataComponent<?>> iterator() {
      return Iterators.transform(this.keySet().iterator(), (type) -> (TypedDataComponent)Objects.requireNonNull(this.getTyped(type)));
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

   default DataComponentMap filter(final Predicate<DataComponentType<?>> predicate) {
      return new DataComponentMap() {
         {
            Objects.requireNonNull(DataComponentMap.this);
         }

         public <T> @Nullable T get(final DataComponentType<? extends T> type) {
            return (T)(predicate.test(type) ? DataComponentMap.this.get(type) : null);
         }

         public Set<DataComponentType<?>> keySet() {
            Set var10000 = DataComponentMap.this.keySet();
            Predicate var10001 = predicate;
            Objects.requireNonNull(var10001);
            return Sets.filter(var10000, var10001::test);
         }
      };
   }

   public static class Builder {
      private final Reference2ObjectMap<DataComponentType<?>, Object> map = new Reference2ObjectArrayMap();
      private Consumer<DataComponentMap> validator = (components) -> {
      };

      private Builder() {
         super();
      }

      public <T> Builder set(final DataComponentType<T> type, final @Nullable T value) {
         this.setUnchecked(type, value);
         return this;
      }

      <T> void setUnchecked(final DataComponentType<T> type, final @Nullable Object value) {
         if (value != null) {
            this.map.put(type, value);
         } else {
            this.map.remove(type);
         }

      }

      public Builder addAll(final DataComponentMap map) {
         for(TypedDataComponent<?> entry : map) {
            this.map.put(entry.type(), entry.value());
         }

         return this;
      }

      public Builder addValidator(final Consumer<DataComponentMap> newValidator) {
         this.validator = this.validator.andThen(newValidator);
         return this;
      }

      public DataComponentMap build() {
         DataComponentMap result = buildFromMapTrusted(this.map);
         this.validator.accept(result);
         return result;
      }

      private static DataComponentMap buildFromMapTrusted(final Map<DataComponentType<?>, Object> map) {
         if (map.isEmpty()) {
            return DataComponentMap.EMPTY;
         } else {
            return map.size() < 8 ? new SimpleMap(new Reference2ObjectArrayMap(map)) : new SimpleMap(new Reference2ObjectOpenHashMap(map));
         }
      }

      private static record SimpleMap(Reference2ObjectMap<DataComponentType<?>, Object> map) implements DataComponentMap {
         private SimpleMap {
            super();
         }

         public <T> @Nullable T get(final DataComponentType<? extends T> type) {
            return (T)this.map.get(type);
         }

         public boolean has(final DataComponentType<?> type) {
            return this.map.containsKey(type);
         }

         public Set<DataComponentType<?>> keySet() {
            return this.map.keySet();
         }

         public Iterator<TypedDataComponent<?>> iterator() {
            return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), TypedDataComponent::fromEntryUnchecked);
         }

         public int size() {
            return this.map.size();
         }

         public String toString() {
            return this.map.toString();
         }
      }
   }
}
