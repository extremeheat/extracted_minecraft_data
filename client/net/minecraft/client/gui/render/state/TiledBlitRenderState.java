package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;

public record TiledBlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int tileWidth, int tileHeight, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
   public TiledBlitRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2f var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13, int var14, @Nullable ScreenRectangle var15) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, getBounds(var6, var7, var8, var9, var3, var15));
   }

   public TiledBlitRenderState(RenderPipeline var1, TextureSetup var2, Matrix3x2f var3, int var4, int var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13, int var14, @Nullable ScreenRectangle var15, @Nullable ScreenRectangle var16) {
      super();
      this.pipeline = var1;
      this.textureSetup = var2;
      this.pose = var3;
      this.tileWidth = var4;
      this.tileHeight = var5;
      this.x0 = var6;
      this.y0 = var7;
      this.x1 = var8;
      this.y1 = var9;
      this.u0 = var10;
      this.u1 = var11;
      this.v0 = var12;
      this.v1 = var13;
      this.color = var14;
      this.scissorArea = var15;
      this.bounds = var16;
   }

   public void buildVertices(VertexConsumer var1) {
      int var2 = this.x1() - this.x0();
      int var3 = this.y1() - this.y0();

      for(int var4 = 0; var4 < var2; var4 += this.tileWidth()) {
         int var6 = var2 - var4;
         int var5;
         float var7;
         if (this.tileWidth() <= var6) {
            var5 = this.tileWidth();
            var7 = this.u1();
         } else {
            var5 = var6;
            var7 = Mth.lerp((float)var6 / (float)this.tileWidth(), this.u0(), this.u1());
         }

         for(int var8 = 0; var8 < var3; var8 += this.tileHeight()) {
            int var10 = var3 - var8;
            int var9;
            float var11;
            if (this.tileHeight() <= var10) {
               var9 = this.tileHeight();
               var11 = this.v1();
            } else {
               var9 = var10;
               var11 = Mth.lerp((float)var10 / (float)this.tileHeight(), this.v0(), this.v1());
            }

            int var12 = this.x0() + var4;
            int var13 = this.x0() + var4 + var5;
            int var14 = this.y0() + var8;
            int var15 = this.y0() + var8 + var9;
            var1.addVertexWith2DPose(this.pose(), (float)var12, (float)var14).setUv(this.u0(), this.v0()).setColor(this.color());
            var1.addVertexWith2DPose(this.pose(), (float)var12, (float)var15).setUv(this.u0(), var11).setColor(this.color());
            var1.addVertexWith2DPose(this.pose(), (float)var13, (float)var15).setUv(var7, var11).setColor(this.color());
            var1.addVertexWith2DPose(this.pose(), (float)var13, (float)var14).setUv(var7, this.v0()).setColor(this.color());
         }
      }

   }

   @Nullable
   private static ScreenRectangle getBounds(int var0, int var1, int var2, int var3, Matrix3x2f var4, @Nullable ScreenRectangle var5) {
      ScreenRectangle var6 = (new ScreenRectangle(var0, var1, var2 - var0, var3 - var1)).transformMaxBounds(var4);
      return var5 != null ? var5.intersection(var6) : var6;
   }
}
