package net.minecraft.client.renderer.block.model;

import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public interface BlockModelPart {
   List<BakedQuad> getQuads(@Nullable Direction var1);

   boolean useAmbientOcclusion();

   TextureAtlasSprite particleIcon();

   public interface Unbaked extends ResolvableModel {
      BlockModelPart bake(ModelBaker var1);
   }
}
