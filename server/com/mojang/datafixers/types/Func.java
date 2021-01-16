package com.mojang.datafixers.types;

import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import java.util.Objects;
import java.util.function.Function;

public final class Func<A, B> extends Type<Function<A, B>> {
   protected final Type<A> first;
   protected final Type<B> second;

   public Func(Type<A> var1, Type<B> var2) {
      super();
      this.first = var1;
      this.second = var2;
   }

   public TypeTemplate buildTemplate() {
      throw new UnsupportedOperationException("No template for function types.");
   }

   protected Codec<Function<A, B>> buildCodec() {
      return Codec.of(Encoder.error("Cannot save a function"), Decoder.error("Cannot read a function"));
   }

   public String toString() {
      return "(" + this.first + " -> " + this.second + ")";
   }

   public boolean equals(Object var1, boolean var2, boolean var3) {
      if (!(var1 instanceof Func)) {
         return false;
      } else {
         Func var4 = (Func)var1;
         return this.first.equals(var4.first, var2, var3) && this.second.equals(var4.second, var2, var3);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.first, this.second});
   }

   public Type<A> first() {
      return this.first;
   }

   public Type<B> second() {
      return this.second;
   }
}
