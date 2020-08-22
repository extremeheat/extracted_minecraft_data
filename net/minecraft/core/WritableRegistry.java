package net.minecraft.core;

import net.minecraft.resources.ResourceLocation;

public abstract class WritableRegistry extends Registry {
   public abstract Object registerMapping(int var1, ResourceLocation var2, Object var3);

   public abstract Object register(ResourceLocation var1, Object var2);

   public abstract boolean isEmpty();
}
