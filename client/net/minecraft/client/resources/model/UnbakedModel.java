package net.minecraft.client.resources.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   Collection<ResourceLocation> getDependencies();

   Collection<ResourceLocation> getTextures(Function<ResourceLocation, UnbakedModel> var1, Set<String> var2);

   @Nullable
   BakedModel bake(ModelBakery var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelState var3);
}
