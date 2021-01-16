package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface MapLike<T> {
   @Nullable
   T get(T var1);

   @Nullable
   T get(String var1);

   Stream<Pair<T, T>> entries();

   static <T> MapLike<T> forMap(final Map<T, T> var0, final DynamicOps<T> var1) {
      return new MapLike<T>() {
         @Nullable
         public T get(T var1x) {
            return var0.get(var1x);
         }

         @Nullable
         public T get(String var1x) {
            return this.get(var1.createString(var1x));
         }

         public Stream<Pair<T, T>> entries() {
            return var0.entrySet().stream().map((var0x) -> {
               return Pair.of(var0x.getKey(), var0x.getValue());
            });
         }

         public String toString() {
            return "MapLike[" + var0 + "]";
         }
      };
   }
}
