package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public record ColoredRectangleRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
   public ColoredRectangleRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2fc var3, int var4, int var5, int var6, int var7, int var8, int var9, @Nullable ScreenRectangle var10) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, getBounds(var4, var5, var6, var7, var3, var10));
   }

   public ColoredRectangleRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2fc var3, int var4, int var5, int var6, int var7, int var8, int var9, @Nullable ScreenRectangle var10, @Nullable ScreenRectangle var11) {
      super();
      this.pipeline = var1;
      this.textureSetup = var2;
      this.pose = var3;
      this.x0 = var4;
      this.y0 = var5;
      this.x1 = var6;
      this.y1 = var7;
      this.col1 = var8;
      this.col2 = var9;
      this.scissorArea = var10;
      this.bounds = var11;
   }

   public void buildVertices(VertexConsumer var1) {
      var1.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y0()).setColor(this.col1());
      var1.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y1()).setColor(this.col2());
      var1.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1()).setColor(this.col2());
      var1.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y0()).setColor(this.col1());
   }

   private static @Nullable ScreenRectangle getBounds(int var0, int var1, int var2, int var3, Matrix3x2fc var4, @Nullable ScreenRectangle var5) {
      ScreenRectangle var6 = (new ScreenRectangle(var0, var1, var2 - var0, var3 - var1)).transformMaxBounds(var4);
      return var5 != null ? var5.intersection(var6) : var6;
   }
}
