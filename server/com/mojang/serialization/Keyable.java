package com.mojang.serialization;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Keyable {
   <T> Stream<T> keys(DynamicOps<T> var1);

   static Keyable forStrings(final Supplier<Stream<String>> var0) {
      return new Keyable() {
         public <T> Stream<T> keys(DynamicOps<T> var1) {
            Stream var10000 = (Stream)var0.get();
            var1.getClass();
            return var10000.map(var1::createString);
         }
      };
   }
}
