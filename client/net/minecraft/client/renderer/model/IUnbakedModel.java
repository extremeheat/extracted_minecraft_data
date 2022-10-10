package net.minecraft.client.renderer.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public interface IUnbakedModel {
   Collection<ResourceLocation> func_187965_e();

   Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> var1, Set<String> var2);

   @Nullable
   IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelRotation var3, boolean var4);
}
