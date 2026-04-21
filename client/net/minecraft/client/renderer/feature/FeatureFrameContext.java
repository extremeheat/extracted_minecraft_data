package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;

public record FeatureFrameContext(OptionsRenderState options, Font font, BlockStateModelSet blockStateModelSet, BlockColors blockColors, TextureManager textureManager, AtlasManager atlasManager, GpuTextureView lightmap, MultiBufferSource bufferSource, OutlineBufferSource outlineBufferSource) {
   public FeatureFrameContext {
      super();
   }
}
