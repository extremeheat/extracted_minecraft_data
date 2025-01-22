package net.minecraft.world.level.entity;

import javax.annotation.Nullable;

public interface EntityTypeTest<B, T extends B> {
   static <B, T extends B> EntityTypeTest<B, T> forClass(final Class<T> var0) {
      return new EntityTypeTest<B, T>() {
         @Nullable
         public T tryCast(B var1) {
            return var0.isInstance(var1) ? var1 : null;
         }

         public Class<? extends B> getBaseClass() {
            return var0;
         }
      };
   }

   static <B, T extends B> EntityTypeTest<B, T> forExactClass(final Class<T> var0) {
      return new EntityTypeTest<B, T>() {
         @Nullable
         public T tryCast(B var1) {
            return var0.equals(var1.getClass()) ? var1 : null;
         }

         public Class<? extends B> getBaseClass() {
            return var0;
         }
      };
   }

   @Nullable
   T tryCast(B var1);

   Class<? extends B> getBaseClass();
}
