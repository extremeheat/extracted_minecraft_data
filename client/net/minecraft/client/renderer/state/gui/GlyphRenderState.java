package net.minecraft.client.renderer.state.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public record GlyphRenderState(Matrix3x2fc pose, TextRenderable renderable, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public GlyphRenderState {
      super();
   }

   public void buildVertices(final VertexConsumer vertexConsumer) {
      this.renderable.render((new Matrix4f()).mul(this.pose), vertexConsumer, 15728880, true);
   }

   public RenderPipeline pipeline() {
      return this.renderable.guiPipeline();
   }

   public TextureSetup textureSetup() {
      return TextureSetup.singleTextureWithLightmap(this.renderable.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
   }

   public @Nullable ScreenRectangle bounds() {
      return null;
   }
}
