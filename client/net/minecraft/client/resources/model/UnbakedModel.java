package net.minecraft.client.resources.model;

import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   Collection<ResourceLocation> getDependencies();

   void resolveParents(Function<ResourceLocation, UnbakedModel> var1);

   @Nullable
   BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3);
}
