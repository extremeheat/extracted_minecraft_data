package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2f;

public record ColoredRectangleRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, float z, int col1, int col2, GuiLayer layer, @Nullable ScreenRectangle scissorArea) implements GuiElementRenderState {
   public ColoredRectangleRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2f var3, int var4, int var5, int var6, int var7, float var8, int var9, int var10, GuiLayer var11, @Nullable ScreenRectangle var12) {
      super();
      this.pipeline = var1;
      this.textureSetup = var2;
      this.pose = var3;
      this.x0 = var4;
      this.y0 = var5;
      this.x1 = var6;
      this.y1 = var7;
      this.z = var8;
      this.col1 = var9;
      this.col2 = var10;
      this.layer = var11;
      this.scissorArea = var12;
   }

   public void buildVertices(VertexConsumer var1) {
      var1.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y0(), this.z()).setColor(this.col1());
      var1.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y1(), this.z()).setColor(this.col2());
      var1.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1(), this.z()).setColor(this.col2());
      var1.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y0(), this.z()).setColor(this.col1());
   }
}
