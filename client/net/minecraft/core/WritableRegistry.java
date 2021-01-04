package net.minecraft.core;

import net.minecraft.resources.ResourceLocation;

public abstract class WritableRegistry<T> extends Registry<T> {
   public WritableRegistry() {
      super();
   }

   public abstract <V extends T> V registerMapping(int var1, ResourceLocation var2, V var3);

   public abstract <V extends T> V register(ResourceLocation var1, V var2);

   public abstract boolean isEmpty();
}
