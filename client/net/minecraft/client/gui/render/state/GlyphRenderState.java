package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2f;

public record GlyphRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, float shearY0, float shearY1, float u0, float u1, float v0, float v1, int color, int packedLightCoords, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public GlyphRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2f var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, int var14, int var15, @Nullable ScreenRectangle var16) {
      super();
      this.pipeline = var1;
      this.textureSetup = var2;
      this.pose = var3;
      this.x0 = var4;
      this.y0 = var5;
      this.x1 = var6;
      this.y1 = var7;
      this.shearY0 = var8;
      this.shearY1 = var9;
      this.u0 = var10;
      this.u1 = var11;
      this.v0 = var12;
      this.v1 = var13;
      this.color = var14;
      this.packedLightCoords = var15;
      this.scissorArea = var16;
   }

   public void buildVertices(VertexConsumer var1, float var2) {
      var1.addVertexWith2DPose(this.pose(), this.x0() + this.shearY0(), this.y0(), var2).setUv(this.u0(), this.v0()).setLight(this.packedLightCoords()).setColor(this.color());
      var1.addVertexWith2DPose(this.pose(), this.x0() + this.shearY1(), this.y1(), var2).setUv(this.u0(), this.v1()).setLight(this.packedLightCoords()).setColor(this.color());
      var1.addVertexWith2DPose(this.pose(), this.x1() + this.shearY1(), this.y1(), var2).setUv(this.u1(), this.v1()).setLight(this.packedLightCoords()).setColor(this.color());
      var1.addVertexWith2DPose(this.pose(), this.x1() + this.shearY0(), this.y0(), var2).setUv(this.u1(), this.v0()).setLight(this.packedLightCoords()).setColor(this.color());
   }
}
