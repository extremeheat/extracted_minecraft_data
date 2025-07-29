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

public record GlyphEffectRenderState(Matrix3x2f pose, BakedGlyph whiteGlyph, BakedGlyph.Effect effect, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public GlyphEffectRenderState(Matrix3x2f var1, BakedGlyph var2, BakedGlyph.Effect var3, @Nullable ScreenRectangle var4) {
      super();
      this.pose = var1;
      this.whiteGlyph = var2;
      this.effect = var3;
      this.scissorArea = var4;
   }

   public void buildVertices(VertexConsumer var1) {
      this.whiteGlyph.renderEffect(this.effect, (new Matrix4f()).mul(this.pose), var1, 15728880, true);
   }

   public RenderPipeline pipeline() {
      return this.whiteGlyph.guiPipeline();
   }

   public TextureSetup textureSetup() {
      return TextureSetup.singleTextureWithLightmap((GpuTextureView)Objects.requireNonNull(this.whiteGlyph.textureView()));
   }

   @Nullable
   public ScreenRectangle bounds() {
      return null;
   }
}
