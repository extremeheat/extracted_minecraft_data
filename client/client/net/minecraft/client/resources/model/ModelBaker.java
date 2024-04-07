package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface ModelBaker {
   UnbakedModel getModel(ResourceLocation var1);

   @Nullable
   BakedModel bake(ResourceLocation var1, ModelState var2);
}
