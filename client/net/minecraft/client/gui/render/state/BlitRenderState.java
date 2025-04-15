package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2f;

public record BlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public BlitRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2f var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10, float var11, int var12, @Nullable ScreenRectangle var13) {
      super();
      this.pipeline = var1;
      this.textureSetup = var2;
      this.pose = var3;
      this.x0 = var4;
      this.y0 = var5;
      this.x1 = var6;
      this.y1 = var7;
      this.u0 = var8;
      this.u1 = var9;
      this.v0 = var10;
      this.v1 = var11;
      this.color = var12;
      this.scissorArea = var13;
   }

   public void buildVertices(VertexConsumer var1, float var2) {
      var1.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y0(), var2).setUv(this.u0(), this.v0()).setColor(this.color());
      var1.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y1(), var2).setUv(this.u0(), this.v1()).setColor(this.color());
      var1.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1(), var2).setUv(this.u1(), this.v1()).setColor(this.color());
      var1.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y0(), var2).setUv(this.u1(), this.v0()).setColor(this.color());
   }
}
