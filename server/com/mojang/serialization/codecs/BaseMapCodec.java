package com.mojang.serialization.codecs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public interface BaseMapCodec<K, V> {
   Codec<K> keyCodec();

   Codec<V> elementCodec();

   default <T> DataResult<Map<K, V>> decode(DynamicOps<T> var1, MapLike<T> var2) {
      ImmutableMap.Builder var3 = ImmutableMap.builder();
      ImmutableList.Builder var4 = ImmutableList.builder();
      DataResult var5 = (DataResult)var2.entries().reduce(DataResult.success(Unit.INSTANCE, Lifecycle.stable()), (var4x, var5x) -> {
         DataResult var6 = this.keyCodec().parse(var1, var5x.getFirst());
         DataResult var7 = this.elementCodec().parse(var1, var5x.getSecond());
         DataResult var8 = var6.apply2stable(Pair::of, var7);
         var8.error().ifPresent((var2) -> {
            var4.add((Object)var5x);
         });
         return var4x.apply2stable((var1x, var2) -> {
            var3.put(var2.getFirst(), var2.getSecond());
            return var1x;
         }, var8);
      }, (var0, var1x) -> {
         return var0.apply2stable((var0x, var1) -> {
            return var0x;
         }, var1x);
      });
      ImmutableMap var6 = var3.build();
      Object var7 = var1.createMap(var4.build().stream());
      return var5.map((var1x) -> {
         return var6;
      }).setPartial((Object)var6).mapError((var1x) -> {
         return var1x + " missed input: " + var7;
      });
   }

   default <T> RecordBuilder<T> encode(Map<K, V> var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      Iterator var4 = var1.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var3.add(this.keyCodec().encodeStart(var2, var5.getKey()), this.elementCodec().encodeStart(var2, var5.getValue()));
      }

      return var3;
   }
}
