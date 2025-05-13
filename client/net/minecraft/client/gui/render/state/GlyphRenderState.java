package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public record GlyphRenderState(Matrix3x2f pose, BakedGlyph.GlyphInstance instance, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public GlyphRenderState(Matrix3x2f var1, BakedGlyph.GlyphInstance var2, @Nullable ScreenRectangle var3) {
      super();
      this.pose = var1;
      this.instance = var2;
      this.scissorArea = var3;
   }

   public void buildVertices(VertexConsumer var1, float var2) {
      Matrix4f var3 = (new Matrix4f()).mul(this.pose).translate(0.0F, 0.0F, var2).scale(1.0F, 1.0F, 0.0F);
      this.instance.glyph().renderChar(this.instance, var3, var1, 15728880);
   }

   public RenderPipeline pipeline() {
      return this.instance.glyph().guiPipeline();
   }

   public TextureSetup textureSetup() {
      return TextureSetup.singleTextureWithLightmap((GpuTextureView)Objects.requireNonNull(this.instance.glyph().textureView()));
   }

   @Nullable
   public ScreenRectangle bounds() {
      return null;
   }
}
