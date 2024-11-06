package net.minecraft.client.resources.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.VisibleForDebug;

public interface ModelBaker {
   BakedModel bake(ResourceLocation var1, ModelState var2);

   SpriteGetter sprites();

   @VisibleForDebug
   ModelDebugName rootName();
}
