package net.minecraft.client.telemetry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class TelemetryPropertyMap {
   final Map<TelemetryProperty<?>, Object> entries;

   TelemetryPropertyMap(Map<TelemetryProperty<?>, Object> var1) {
      super();
      this.entries = var1;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static MapCodec<TelemetryPropertyMap> createCodec(final List<TelemetryProperty<?>> var0) {
      return new MapCodec<TelemetryPropertyMap>() {
         public <T> RecordBuilder<T> encode(TelemetryPropertyMap var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
            RecordBuilder var4 = var3;

            TelemetryProperty var6;
            for(Iterator var5 = var0.iterator(); var5.hasNext(); var4 = this.encodeProperty(var1, var4, var6)) {
               var6 = (TelemetryProperty)var5.next();
            }

            return var4;
         }

         private <T, V> RecordBuilder<T> encodeProperty(TelemetryPropertyMap var1, RecordBuilder<T> var2, TelemetryProperty<V> var3) {
            Object var4 = var1.get(var3);
            return var4 != null ? var2.add(var3.id(), var4, var3.codec()) : var2;
         }

         public <T> DataResult<TelemetryPropertyMap> decode(DynamicOps<T> var1, MapLike<T> var2) {
            DataResult var3 = DataResult.success(new Builder());

            TelemetryProperty var5;
            for(Iterator var4 = var0.iterator(); var4.hasNext(); var3 = this.decodeProperty(var3, var1, var2, var5)) {
               var5 = (TelemetryProperty)var4.next();
            }

            return var3.map(Builder::build);
         }

         private <T, V> DataResult<Builder> decodeProperty(DataResult<Builder> var1, DynamicOps<T> var2, MapLike<T> var3, TelemetryProperty<V> var4) {
            Object var5 = var3.get(var4.id());
            if (var5 != null) {
               DataResult var6 = var4.codec().parse(var2, var5);
               return var1.apply2stable((var1x, var2x) -> {
                  return var1x.put(var4, var2x);
               }, var6);
            } else {
               return var1;
            }
         }

         public <T> Stream<T> keys(DynamicOps<T> var1) {
            Stream var10000 = var0.stream().map(TelemetryProperty::id);
            Objects.requireNonNull(var1);
            return var10000.map(var1::createString);
         }

         // $FF: synthetic method
         public RecordBuilder encode(Object var1, DynamicOps var2, RecordBuilder var3) {
            return this.encode((TelemetryPropertyMap)var1, var2, var3);
         }
      };
   }

   @Nullable
   public <T> T get(TelemetryProperty<T> var1) {
      return this.entries.get(var1);
   }

   public String toString() {
      return this.entries.toString();
   }

   public Set<TelemetryProperty<?>> propertySet() {
      return this.entries.keySet();
   }

   public static class Builder {
      private final Map<TelemetryProperty<?>, Object> entries = new Reference2ObjectOpenHashMap();

      Builder() {
         super();
      }

      public <T> Builder put(TelemetryProperty<T> var1, T var2) {
         this.entries.put(var1, var2);
         return this;
      }

      public <T> Builder putIfNotNull(TelemetryProperty<T> var1, @Nullable T var2) {
         if (var2 != null) {
            this.entries.put(var1, var2);
         }

         return this;
      }

      public Builder putAll(TelemetryPropertyMap var1) {
         this.entries.putAll(var1.entries);
         return this;
      }

      public TelemetryPropertyMap build() {
         return new TelemetryPropertyMap(this.entries);
      }
   }
}
