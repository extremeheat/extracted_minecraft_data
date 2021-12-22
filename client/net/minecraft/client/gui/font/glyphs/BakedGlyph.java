package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;

public class BakedGlyph {
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final RenderType polygonOffsetType;
   // $FF: renamed from: u0 float
   private final float field_380;
   // $FF: renamed from: u1 float
   private final float field_381;
   // $FF: renamed from: v0 float
   private final float field_382;
   // $FF: renamed from: v1 float
   private final float field_383;
   private final float left;
   private final float right;
   // $FF: renamed from: up float
   private final float field_384;
   private final float down;

   public BakedGlyph(RenderType var1, RenderType var2, RenderType var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      super();
      this.normalType = var1;
      this.seeThroughType = var2;
      this.polygonOffsetType = var3;
      this.field_380 = var4;
      this.field_381 = var5;
      this.field_382 = var6;
      this.field_383 = var7;
      this.left = var8;
      this.right = var9;
      this.field_384 = var10;
      this.down = var11;
   }

   public void render(boolean var1, float var2, float var3, Matrix4f var4, VertexConsumer var5, float var6, float var7, float var8, float var9, int var10) {
      boolean var11 = true;
      float var12 = var2 + this.left;
      float var13 = var2 + this.right;
      float var14 = this.field_384 - 3.0F;
      float var15 = this.down - 3.0F;
      float var16 = var3 + var14;
      float var17 = var3 + var15;
      float var18 = var1 ? 1.0F - 0.25F * var14 : 0.0F;
      float var19 = var1 ? 1.0F - 0.25F * var15 : 0.0F;
      var5.vertex(var4, var12 + var18, var16, 0.0F).color(var6, var7, var8, var9).method_7(this.field_380, this.field_382).uv2(var10).endVertex();
      var5.vertex(var4, var12 + var19, var17, 0.0F).color(var6, var7, var8, var9).method_7(this.field_380, this.field_383).uv2(var10).endVertex();
      var5.vertex(var4, var13 + var19, var17, 0.0F).color(var6, var7, var8, var9).method_7(this.field_381, this.field_383).uv2(var10).endVertex();
      var5.vertex(var4, var13 + var18, var16, 0.0F).color(var6, var7, var8, var9).method_7(this.field_381, this.field_382).uv2(var10).endVertex();
   }

   public void renderEffect(BakedGlyph.Effect var1, Matrix4f var2, VertexConsumer var3, int var4) {
      var3.vertex(var2, var1.field_445, var1.field_446, var1.depth).color(var1.field_449, var1.field_450, var1.field_451, var1.field_452).method_7(this.field_380, this.field_382).uv2(var4).endVertex();
      var3.vertex(var2, var1.field_447, var1.field_446, var1.depth).color(var1.field_449, var1.field_450, var1.field_451, var1.field_452).method_7(this.field_380, this.field_383).uv2(var4).endVertex();
      var3.vertex(var2, var1.field_447, var1.field_448, var1.depth).color(var1.field_449, var1.field_450, var1.field_451, var1.field_452).method_7(this.field_381, this.field_383).uv2(var4).endVertex();
      var3.vertex(var2, var1.field_445, var1.field_448, var1.depth).color(var1.field_449, var1.field_450, var1.field_451, var1.field_452).method_7(this.field_381, this.field_382).uv2(var4).endVertex();
   }

   public RenderType renderType(Font.DisplayMode var1) {
      switch(var1) {
      case NORMAL:
      default:
         return this.normalType;
      case SEE_THROUGH:
         return this.seeThroughType;
      case POLYGON_OFFSET:
         return this.polygonOffsetType;
      }
   }

   public static class Effect {
      // $FF: renamed from: x0 float
      protected final float field_445;
      // $FF: renamed from: y0 float
      protected final float field_446;
      // $FF: renamed from: x1 float
      protected final float field_447;
      // $FF: renamed from: y1 float
      protected final float field_448;
      protected final float depth;
      // $FF: renamed from: r float
      protected final float field_449;
      // $FF: renamed from: g float
      protected final float field_450;
      // $FF: renamed from: b float
      protected final float field_451;
      // $FF: renamed from: a float
      protected final float field_452;

      public Effect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
         super();
         this.field_445 = var1;
         this.field_446 = var2;
         this.field_447 = var3;
         this.field_448 = var4;
         this.depth = var5;
         this.field_449 = var6;
         this.field_450 = var7;
         this.field_451 = var8;
         this.field_452 = var9;
      }
   }
}
