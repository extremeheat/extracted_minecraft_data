package net.minecraft.world;

import javax.annotation.Nullable;

public interface Clearable {
   void clearContent();

   static void tryClear(@Nullable Object var0) {
      if (var0 instanceof Clearable) {
         ((Clearable)var0).clearContent();
      }

   }
}
