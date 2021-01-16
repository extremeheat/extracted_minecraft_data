package com.mojang.serialization.codecs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.mutable.MutableObject;

public final class CompoundListCodec<K, V> implements Codec<List<Pair<K, V>>> {
   private final Codec<K> keyCodec;
   private final Codec<V> elementCodec;

   public CompoundListCodec(Codec<K> var1, Codec<V> var2) {
      super();
      this.keyCodec = var1;
      this.elementCodec = var2;
   }

   public <T> DataResult<Pair<List<Pair<K, V>>, T>> decode(DynamicOps<T> var1, T var2) {
      return var1.getMapEntries(var2).flatMap((var2x) -> {
         ImmutableList.Builder var3 = ImmutableList.builder();
         ImmutableMap.Builder var4 = ImmutableMap.builder();
         MutableObject var5 = new MutableObject(DataResult.success(Unit.INSTANCE, Lifecycle.experimental()));
         var2x.accept((var5x, var6x) -> {
            DataResult var7 = this.keyCodec.parse(var1, var5x);
            DataResult var8 = this.elementCodec.parse(var1, var6x);
            DataResult var9 = var7.apply2stable(Pair::new, var8);
            var9.error().ifPresent((var3x) -> {
               var4.put(var5x, var6x);
            });
            var5.setValue(((DataResult)var5.getValue()).apply2stable((var1x, var2) -> {
               var3.add((Object)var2);
               return var1x;
            }, var9));
         });
         ImmutableList var6 = var3.build();
         Object var7 = var1.createMap((Map)var4.build());
         Pair var8 = Pair.of(var6, var7);
         return ((DataResult)var5.getValue()).map((var1x) -> {
            return var8;
         }).setPartial((Object)var8);
      });
   }

   public <T> DataResult<T> encode(List<Pair<K, V>> var1, DynamicOps<T> var2, T var3) {
      RecordBuilder var4 = var2.mapBuilder();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Pair var6 = (Pair)var5.next();
         var4.add(this.keyCodec.encodeStart(var2, var6.getFirst()), this.elementCodec.encodeStart(var2, var6.getSecond()));
      }

      return var4.build(var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         CompoundListCodec var2 = (CompoundListCodec)var1;
         return Objects.equals(this.keyCodec, var2.keyCodec) && Objects.equals(this.elementCodec, var2.elementCodec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.keyCodec, this.elementCodec});
   }

   public String toString() {
      return "CompoundListCodec[" + this.keyCodec + " -> " + this.elementCodec + ']';
   }
}
