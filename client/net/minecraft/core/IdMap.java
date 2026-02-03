package net.minecraft.core;

import org.jspecify.annotations.Nullable;

public interface IdMap<T> extends Iterable<T> {
   int DEFAULT = -1;

   int getId(T thing);

   @Nullable T byId(int id);

   default T byIdOrThrow(final int id) {
      T result = (T)this.byId(id);
      if (result == null) {
         throw new IllegalArgumentException("No value with id " + id);
      } else {
         return result;
      }
   }

   default int getIdOrThrow(final T value) {
      int id = this.getId(value);
      if (id == -1) {
         String var10002 = String.valueOf(value);
         throw new IllegalArgumentException("Can't find id for '" + var10002 + "' in map " + String.valueOf(this));
      } else {
         return id;
      }
   }

   int size();
}
