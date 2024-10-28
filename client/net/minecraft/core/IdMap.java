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
         return var2;
      }
   }

   default int getIdOrThrow(T var1) {
      int var2 = this.getId(var1);
      if (var2 == -1) {
         String var10002 = String.valueOf(var1);
         throw new IllegalArgumentException("Can't find id for '" + var10002 + "' in map " + String.valueOf(this));
      } else {
         return var2;
      }
   }

   int size();
}
