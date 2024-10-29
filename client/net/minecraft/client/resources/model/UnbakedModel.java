package net.minecraft.client.resources.model;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   void resolveDependencies(Resolver var1);

   BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3);

   public interface Resolver {
      UnbakedModel resolve(ResourceLocation var1);
   }
}
