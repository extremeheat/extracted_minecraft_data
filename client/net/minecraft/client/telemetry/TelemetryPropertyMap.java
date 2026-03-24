package net.minecraft.client.telemetry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public class TelemetryPropertyMap {
   private final Map<TelemetryProperty<?>, Object> entries;

   private TelemetryPropertyMap(final Map<TelemetryProperty<?>, Object> entries) {
      super();
      this.entries = entries;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static MapCodec<TelemetryPropertyMap> createCodec(final List<TelemetryProperty<?>> properties) {
      return new MapCodec<TelemetryPropertyMap>() {
         public <T> RecordBuilder<T> encode(final TelemetryPropertyMap input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
            RecordBuilder<T> result = prefix;

            for(TelemetryProperty<?> property : properties) {
               result = this.encodeProperty(input, result, property);
            }

            return result;
         }

         private <T, V> RecordBuilder<T> encodeProperty(final TelemetryPropertyMap input, final RecordBuilder<T> result, final TelemetryProperty<V> property) {
            V value = (V)input.get(property);
            return value != null ? result.add(property.id(), value, property.codec()) : result;
         }

         public <T> DataResult<TelemetryPropertyMap> decode(final DynamicOps<T> ops, final MapLike<T> input) {
            DataResult<Builder> result = DataResult.success(new Builder());

            for(TelemetryProperty<?> property : properties) {
               result = this.decodeProperty(result, ops, input, property);
            }

            return result.map(Builder::build);
         }

         private <T, V> DataResult<Builder> decodeProperty(final DataResult<Builder> result, final DynamicOps<T> ops, final MapLike<T> input, final TelemetryProperty<V> property) {
            T value = (T)input.get(property.id());
            if (value != null) {
               DataResult<V> parse = property.codec().parse(ops, value);
               return result.apply2stable((b, v) -> b.put(property, v), parse);
            } else {
               return result;
            }
         }

         public <T> Stream<T> keys(final DynamicOps<T> ops) {
            Stream var10000 = properties.stream().map(TelemetryProperty::id);
            Objects.requireNonNull(ops);
            return var10000.map(ops::createString);
         }
      };
   }

   public <T> @Nullable T get(final TelemetryProperty<T> property) {
      return (T)this.entries.get(property);
   }

   public String toString() {
      return this.entries.toString();
   }

   public Set<TelemetryProperty<?>> propertySet() {
      return this.entries.keySet();
   }

   public static class Builder {
      private final Map<TelemetryProperty<?>, Object> entries = new Reference2ObjectOpenHashMap();

      private Builder() {
         super();
      }

      public <T> Builder put(final TelemetryProperty<T> property, final T value) {
         this.entries.put(property, value);
         return this;
      }

      public <T> Builder putIfNotNull(final TelemetryProperty<T> property, final @Nullable T value) {
         if (value != null) {
            this.entries.put(property, value);
         }

         return this;
      }

      public Builder putAll(final TelemetryPropertyMap properties) {
         this.entries.putAll(properties.entries);
         return this;
      }

      public TelemetryPropertyMap build() {
         return new TelemetryPropertyMap(this.entries);
      }
   }
}
