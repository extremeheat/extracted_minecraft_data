package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
   int DEFAULT = -1;

   int getId(T var1);

   @Nullable
   T byId(int var1);

   default T byIdOrThrow(int var1) {
      Object var2 = this.byId(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No value with id " + var1);
      } else {
         return (T)var2;
      }
   }

   int size();
}
