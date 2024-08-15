package net.minecraft.client.resources.model;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   void resolveDependencies(UnbakedModel.Resolver var1, UnbakedModel.ResolutionContext var2);

   @Nullable
   BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3);

   public static enum ResolutionContext {
      TOP,
      OVERRIDE;

      private ResolutionContext() {
      }
   }

   public interface Resolver {
      UnbakedModel resolve(ResourceLocation var1);

      UnbakedModel resolveForOverride(ResourceLocation var1);
   }
}
