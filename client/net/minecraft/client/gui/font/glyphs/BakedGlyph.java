package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.RenderType;

public class BakedGlyph {
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(RenderType var1, RenderType var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      super();
      this.normalType = var1;
      this.seeThroughType = var2;
      this.u0 = var3;
      this.u1 = var4;
      this.v0 = var5;
      this.v1 = var6;
      this.left = var7;
      this.right = var8;
      this.up = var9;
      this.down = var10;
   }

   public void render(boolean var1, float var2, float var3, Matrix4f var4, VertexConsumer var5, float var6, float var7, float var8, float var9, int var10) {
      boolean var11 = true;
      float var12 = var2 + this.left;
      float var13 = var2 + this.right;
      float var14 = this.up - 3.0F;
      float var15 = this.down - 3.0F;
      float var16 = var3 + var14;
      float var17 = var3 + var15;
      float var18 = var1 ? 1.0F - 0.25F * var14 : 0.0F;
      float var19 = var1 ? 1.0F - 0.25F * var15 : 0.0F;
      var5.vertex(var4, var12 + var18, var16, 0.0F).color(var6, var7, var8, var9).uv(this.u0, this.v0).uv2(var10).endVertex();
      var5.vertex(var4, var12 + var19, var17, 0.0F).color(var6, var7, var8, var9).uv(this.u0, this.v1).uv2(var10).endVertex();
      var5.vertex(var4, var13 + var19, var17, 0.0F).color(var6, var7, var8, var9).uv(this.u1, this.v1).uv2(var10).endVertex();
      var5.vertex(var4, var13 + var18, var16, 0.0F).color(var6, var7, var8, var9).uv(this.u1, this.v0).uv2(var10).endVertex();
   }

   public void renderEffect(BakedGlyph.Effect var1, Matrix4f var2, VertexConsumer var3, int var4) {
      var3.vertex(var2, var1.x0, var1.y0, var1.depth).color(var1.r, var1.g, var1.b, var1.a).uv(this.u0, this.v0).uv2(var4).endVertex();
      var3.vertex(var2, var1.x1, var1.y0, var1.depth).color(var1.r, var1.g, var1.b, var1.a).uv(this.u0, this.v1).uv2(var4).endVertex();
      var3.vertex(var2, var1.x1, var1.y1, var1.depth).color(var1.r, var1.g, var1.b, var1.a).uv(this.u1, this.v1).uv2(var4).endVertex();
      var3.vertex(var2, var1.x0, var1.y1, var1.depth).color(var1.r, var1.g, var1.b, var1.a).uv(this.u1, this.v0).uv2(var4).endVertex();
   }

   public RenderType renderType(boolean var1) {
      return var1 ? this.seeThroughType : this.normalType;
   }

   public static class Effect {
      protected final float x0;
      protected final float y0;
      protected final float x1;
      protected final float y1;
      protected final float depth;
      protected final float r;
      protected final float g;
      protected final float b;
      protected final float a;

      public Effect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
         super();
         this.x0 = var1;
         this.y0 = var2;
         this.x1 = var3;
         this.y1 = var4;
         this.depth = var5;
         this.r = var6;
         this.g = var7;
         this.b = var8;
         this.a = var9;
      }
   }
}
