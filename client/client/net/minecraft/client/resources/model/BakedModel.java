package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface BakedModel {
   List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3);

   boolean useAmbientOcclusion();

   boolean isGui3d();

   boolean usesBlockLight();

   boolean isCustomRenderer();

   TextureAtlasSprite getParticleIcon();

   ItemTransforms getTransforms();

   ItemOverrides getOverrides();
}
