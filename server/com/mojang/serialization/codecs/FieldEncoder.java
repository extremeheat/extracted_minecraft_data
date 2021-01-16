package com.mojang.serialization.codecs;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.stream.Stream;

public class FieldEncoder<A> extends MapEncoder.Implementation<A> {
   private final String name;
   private final Encoder<A> elementCodec;

   public FieldEncoder(String var1, Encoder<A> var2) {
      super();
      this.name = var1;
      this.elementCodec = var2;
   }

   public <T> RecordBuilder<T> encode(A var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
      return var3.add(this.name, this.elementCodec.encodeStart(var2, var1));
   }

   public <T> Stream<T> keys(DynamicOps<T> var1) {
      return Stream.of(var1.createString(this.name));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FieldEncoder var2 = (FieldEncoder)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.elementCodec, var2.elementCodec);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.elementCodec});
   }

   public String toString() {
      return "FieldEncoder[" + this.name + ": " + this.elementCodec + ']';
   }
}
