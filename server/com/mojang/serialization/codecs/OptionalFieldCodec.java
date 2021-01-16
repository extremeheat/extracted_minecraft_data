package com.mojang.serialization.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class OptionalFieldCodec<A> extends MapCodec<Optional<A>> {
   private final String name;
   private final Codec<A> elementCodec;

   public OptionalFieldCodec(String var1, Codec<A> var2) {
      super();
      this.name = var1;
      this.elementCodec = var2;
   }

   public <T> DataResult<Optional<A>> decode(DynamicOps<T> var1, MapLike<T> var2) {
      Object var3 = var2.get(this.name);
      if (var3 == null) {
         return DataResult.success(Optional.empty());
      } else {
         DataResult var4 = this.elementCodec.parse(var1, var3);
         return var4.result().isPresent() ? var4.map(Optional::of) : DataResult.success(Optional.empty());
      }
   }

   public <T> RecordBuilder<T> encode(Optional<A> var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      return var1.isPresent() ? var3.add(this.name, this.elementCodec.encodeStart(var2, var1.get())) : var3;
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      return Stream.of(var1.createString(this.name));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         OptionalFieldCodec var2 = (OptionalFieldCodec)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.elementCodec, var2.elementCodec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.elementCodec});
   }

   public String toString() {
      return "OptionalFieldCodec[" + this.name + ": " + this.elementCodec + ']';
   }
}
