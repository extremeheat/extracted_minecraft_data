package net.minecraft.client.resources.model;

import net.minecraft.resources.ResourceLocation;

public interface ModelBaker {
   ResolvedModel getModel(ResourceLocation var1);

   SpriteGetter sprites();

   <T> T compute(SharedOperationKey<T> var1);

   @FunctionalInterface
   public interface SharedOperationKey<T> {
      T compute(ModelBaker var1);
   }
}
