package com.mojang.serialization.codecs;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import org.apache.commons.lang3.mutable.MutableObject;

public final class ListCodec<A> implements Codec<List<A>> {
   private final Codec<A> elementCodec;

   public ListCodec(Codec<A> var1) {
      super();
      this.elementCodec = var1;
   }

   public <T> DataResult<T> encode(List<A> var1, DynamicOps<T> var2, T var3) {
      ListBuilder var4 = var2.listBuilder();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         var4.add(this.elementCodec.encodeStart(var2, var6));
      }

      return var4.build(var3);
   }

   public <T> DataResult<Pair<List<A>, T>> decode(DynamicOps<T> var1, T var2) {
      return var1.getList(var2).setLifecycle(Lifecycle.stable()).flatMap((var2x) -> {
         ImmutableList.Builder var3 = ImmutableList.builder();
         Builder var4 = Stream.builder();
         MutableObject var5 = new MutableObject(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));
         var2x.accept((var5x) -> {
            DataResult var6 = this.elementCodec.decode(var1, var5x);
            var6.error().ifPresent((var2) -> {
               var4.add(var5x);
            });
            var5.setValue(((DataResult)var5.getValue()).apply2stable((var1x, var2) -> {
               var3.add(var2.getFirst());
               return var1x;
            }, var6));
         });
         ImmutableList var6 = var3.build();
         Object var7 = var1.createList(var4.build());
         Pair var8 = Pair.of(var6, var7);
         return ((DataResult)var5.getValue()).map((var1x) -> {
            return var8;
         }).setPartial((Object)var8);
      });
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ListCodec var2 = (ListCodec)var1;
         return Objects.equals(this.elementCodec, var2.elementCodec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.elementCodec});
   }

   public String toString() {
      return "ListCodec[" + this.elementCodec + ']';
   }
}
