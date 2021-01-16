package com.mojang.serialization.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import java.util.Map;
import java.util.Objects;

public final class UnboundedMapCodec<K, V> implements BaseMapCodec<K, V>, Codec<Map<K, V>> {
   private final Codec<K> keyCodec;
   private final Codec<V> elementCodec;

   public UnboundedMapCodec(Codec<K> var1, Codec<V> var2) {
      super();
      this.keyCodec = var1;
      this.elementCodec = var2;
   }

   public Codec<K> keyCodec() {
      return this.keyCodec;
   }

   public Codec<V> elementCodec() {
      return this.elementCodec;
   }

   public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> var1, T var2) {
      return var1.getMap(var2).setLifecycle(Lifecycle.stable()).flatMap((var2x) -> {
         return this.decode(var1, var2x);
      }).map((var1x) -> {
         return Pair.of(var1x, var2);
      });
   }

   public <T> DataResult<T> encode(Map<K, V> var1, DynamicOps<T> var2, T var3) {
      return this.encode((Map)var1, var2, (RecordBuilder)var2.mapBuilder()).build(var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         UnboundedMapCodec var2 = (UnboundedMapCodec)var1;
         return Objects.equals(this.keyCodec, var2.keyCodec) && Objects.equals(this.elementCodec, var2.elementCodec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.keyCodec, this.elementCodec});
   }

   public String toString() {
      return "UnboundedMapCodec[" + this.keyCodec + " -> " + this.elementCodec + ']';
   }
}
