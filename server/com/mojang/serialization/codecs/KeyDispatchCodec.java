package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Function;
import java.util.stream.Stream;

public class KeyDispatchCodec<K, V> extends MapCodec<V> {
   private final String typeKey;
   private final Codec<K> keyCodec;
   private final String valueKey;
   private final Function<? super V, ? extends DataResult<? extends K>> type;
   private final Function<? super K, ? extends DataResult<? extends Decoder<? extends V>>> decoder;
   private final Function<? super V, ? extends DataResult<? extends Encoder<V>>> encoder;
   private final boolean assumeMap;

   public static <K, V> KeyDispatchCodec<K, V> unsafe(String var0, Codec<K> var1, Function<? super V, ? extends DataResult<? extends K>> var2, Function<? super K, ? extends DataResult<? extends Decoder<? extends V>>> var3, Function<? super V, ? extends DataResult<? extends Encoder<V>>> var4) {
      return new KeyDispatchCodec(var0, var1, var2, var3, var4, true);
   }

   protected KeyDispatchCodec(String var1, Codec<K> var2, Function<? super V, ? extends DataResult<? extends K>> var3, Function<? super K, ? extends DataResult<? extends Decoder<? extends V>>> var4, Function<? super V, ? extends DataResult<? extends Encoder<V>>> var5, boolean var6) {
      super();
      this.valueKey = "value";
      this.typeKey = var1;
      this.keyCodec = var2;
      this.type = var3;
      this.decoder = var4;
      this.encoder = var5;
      this.assumeMap = var6;
   }

   public KeyDispatchCodec(String var1, Codec<K> var2, Function<? super V, ? extends DataResult<? extends K>> var3, Function<? super K, ? extends DataResult<? extends Codec<? extends V>>> var4) {
      this(var1, var2, var3, var4, (var2x) -> {
         return getCodec(var3, var4, var2x);
      }, false);
   }

   public <T> DataResult<V> decode(DynamicOps<T> var1, MapLike<T> var2) {
      Object var3 = var2.get(this.typeKey);
      return var3 == null ? DataResult.error("Input does not contain a key [" + this.typeKey + "]: " + var2) : this.keyCodec.decode(var1, var3).flatMap((var3x) -> {
         DataResult var4 = (DataResult)this.decoder.apply(var3x.getFirst());
         return var4.flatMap((var3) -> {
            if (var1.compressMaps()) {
               Object var4 = var2.get(var1.createString("value"));
               return var4 == null ? DataResult.error("Input does not have a \"value\" entry: " + var2) : var3.parse(var1, var4).map(Function.identity());
            } else if (var3 instanceof MapCodec.MapCodecCodec) {
               return ((MapCodec.MapCodecCodec)var3).codec().decode(var1, var2).map(Function.identity());
            } else {
               return this.assumeMap ? var3.decode(var1, var1.createMap(var2.entries())).map(Pair::getFirst) : var3.decode(var1, var2.get("value")).map(Pair::getFirst);
            }
         });
      });
   }

   public <T> RecordBuilder<T> encode(V var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      DataResult var4 = (DataResult)this.encoder.apply(var1);
      RecordBuilder var5 = var3.withErrorsFrom(var4);
      if (!var4.result().isPresent()) {
         return var5;
      } else {
         Encoder var6 = (Encoder)var4.result().get();
         if (var2.compressMaps()) {
            return var3.add(this.typeKey, ((DataResult)this.type.apply(var1)).flatMap((var2x) -> {
               return this.keyCodec.encodeStart(var2, var2x);
            })).add("value", var6.encodeStart(var2, var1));
         } else if (var6 instanceof MapCodec.MapCodecCodec) {
            return ((MapCodec.MapCodecCodec)var6).codec().encode(var1, var2, var3).add(this.typeKey, ((DataResult)this.type.apply(var1)).flatMap((var2x) -> {
               return this.keyCodec.encodeStart(var2, var2x);
            }));
         } else {
            Object var7 = var2.createString(this.typeKey);
            DataResult var8 = var6.encodeStart(var2, var1);
            if (this.assumeMap) {
               var2.getClass();
               DataResult var9 = var8.flatMap(var2::getMap);
               return (RecordBuilder)var9.map((var5x) -> {
                  var3.add(var7, ((DataResult)this.type.apply(var1)).flatMap((var2x) -> {
                     return this.keyCodec.encodeStart(var2, var2x);
                  }));
                  var5x.entries().forEach((var2x) -> {
                     if (!var2x.getFirst().equals(var7)) {
                        var3.add(var2x.getFirst(), var2x.getSecond());
                     }

                  });
                  return var3;
               }).result().orElseGet(() -> {
                  return var3.withErrorsFrom(var9);
               });
            } else {
               var3.add(var7, ((DataResult)this.type.apply(var1)).flatMap((var2x) -> {
                  return this.keyCodec.encodeStart(var2, var2x);
               }));
               var3.add("value", var8);
               return var3;
            }
         }
      }
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      Stream var10000 = Stream.of(this.typeKey, "value");
      var1.getClass();
      return var10000.map(var1::createString);
   }

   private static <K, V> DataResult<? extends Encoder<V>> getCodec(Function<? super V, ? extends DataResult<? extends K>> var0, Function<? super K, ? extends DataResult<? extends Encoder<? extends V>>> var1, V var2) {
      return ((DataResult)var0.apply(var2)).flatMap((var1x) -> {
         return ((DataResult)var1.apply(var1x)).map(Function.identity());
      }).map((var0x) -> {
         return var0x;
      });
   }

   public String toString() {
      return "KeyDispatchCodec[" + this.keyCodec.toString() + " " + this.type + " " + this.decoder + "]";
   }
}
