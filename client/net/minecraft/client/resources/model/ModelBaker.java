package net.minecraft.client.resources.model;

import net.minecraft.resources.ResourceLocation;

public interface ModelBaker {
   ResolvedModel getModel(ResourceLocation var1);

   SpriteGetter sprites();
}
