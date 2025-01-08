package com.mojang.blaze3d.resource;

public interface ResourceDescriptor<T> {
   T allocate();

   default void prepare(T var1) {
   }

   void free(T var1);

   default boolean canUsePhysicalResource(ResourceDescriptor<?> var1) {
      return this.equals(var1);
   }
}
