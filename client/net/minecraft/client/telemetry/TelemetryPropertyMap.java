package net.minecraft.client.telemetry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class TelemetryPropertyMap {
   final Map<TelemetryProperty<?>, Object> entries;

   TelemetryPropertyMap(Map<TelemetryProperty<?>, Object> var1) {
      super();
      this.entries = var1;
   }

   public static TelemetryPropertyMap.Builder builder() {
      return new TelemetryPropertyMap.Builder();
   }

   public static Codec<TelemetryPropertyMap> createCodec(final List<TelemetryProperty<?>> var0) {
      return (new MapCodec<TelemetryPropertyMap>() {
            public <T> RecordBuilder<T> encode(TelemetryPropertyMap var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
               RecordBuilder var4 = var3;
   
               for(TelemetryProperty var6 : var0) {
                  var4 = this.encodeProperty(var1, var4, var6);
               }
   
               return var4;
            }
   
            private <T, V> RecordBuilder<T> encodeProperty(TelemetryPropertyMap var1, RecordBuilder<T> var2, TelemetryProperty<V> var3) {
               Object var4 = var1.get(var3);
               return var4 != null ? var2.add(var3.id(), var4, var3.codec()) : var2;
            }
   
            public <T> DataResult<TelemetryPropertyMap> decode(DynamicOps<T> var1, MapLike<T> var2) {
               DataResult var3 = DataResult.success(new TelemetryPropertyMap.Builder());
   
               for(TelemetryProperty var5 : var0) {
                  var3 = this.decodeProperty(var3, var1, var2, var5);
               }
   
               return var3.map(TelemetryPropertyMap.Builder::build);
            }
   
            private <T, V> DataResult<TelemetryPropertyMap.Builder> decodeProperty(
               DataResult<TelemetryPropertyMap.Builder> var1, DynamicOps<T> var2, MapLike<T> var3, TelemetryProperty<V> var4
            ) {
               Object var5 = var3.get(var4.id());
               if (var5 != null) {
                  DataResult var6 = var4.codec().parse(var2, var5);
                  return var1.apply2stable((var1x, var2x) -> var1x.put(var4, (T)var2x), var6);
               } else {
                  return var1;
               }
            }
   
            public <T> Stream<T> keys(DynamicOps<T> var1) {
               return var0.stream().map(TelemetryProperty::id).map(var1::createString);
            }
         })
         .codec();
   }

   @Nullable
   public <T> T get(TelemetryProperty<T> var1) {
      return (T)this.entries.get(var1);
   }

   @Override
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

      public <T> TelemetryPropertyMap.Builder put(TelemetryProperty<T> var1, T var2) {
         this.entries.put(var1, var2);
         return this;
      }

      public TelemetryPropertyMap.Builder putAll(TelemetryPropertyMap var1) {
         this.entries.putAll(var1.entries);
         return this;
      }

      public TelemetryPropertyMap build() {
         return new TelemetryPropertyMap(this.entries);
      }
   }
}
