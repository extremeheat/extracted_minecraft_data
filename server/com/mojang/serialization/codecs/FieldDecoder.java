package com.mojang.serialization.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import java.util.Objects;
import java.util.stream.Stream;

public final class FieldDecoder<A> extends MapDecoder.Implementation<A> {
   protected final String name;
   private final Decoder<A> elementCodec;

   public FieldDecoder(String var1, Decoder<A> var2) {
      super();
      this.name = var1;
      this.elementCodec = var2;
   }

   public <T> DataResult<A> decode(DynamicOps<T> var1, MapLike<T> var2) {
      Object var3 = var2.get(this.name);
      return var3 == null ? DataResult.error("No key " + this.name + " in " + var2) : this.elementCodec.parse(var1, var3);
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      return Stream.of(var1.createString(this.name));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FieldDecoder var2 = (FieldDecoder)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.elementCodec, var2.elementCodec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.elementCodec});
   }

   public String toString() {
      return "FieldDecoder[" + this.name + ": " + this.elementCodec + ']';
   }
}
