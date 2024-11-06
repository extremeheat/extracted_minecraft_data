package net.minecraft.client.resources.model;

import net.minecraft.resources.ResourceLocation;

public interface ResolvableModel {
   void resolveDependencies(Resolver var1);

   public interface Resolver {
      UnbakedModel resolve(ResourceLocation var1);
   }
}
