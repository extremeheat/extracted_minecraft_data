package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class BakedGlyph {
   public static final float Z_FIGHTER = 0.001F;
   private final GlyphRenderTypes renderTypes;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(GlyphRenderTypes var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      super();
      this.renderTypes = var1;
      this.u0 = var2;
      this.u1 = var3;
      this.v0 = var4;
      this.v1 = var5;
      this.left = var6;
      this.right = var7;
      this.up = var8;
      this.down = var9;
   }

   public void renderChar(GlyphInstance var1, Matrix4f var2, VertexConsumer var3, int var4) {
      Style var5 = var1.style();
      boolean var6 = var5.isItalic();
      float var7 = var1.x();
      float var8 = var1.y();
      int var9 = var1.color();
      int var10 = var1.shadowColor();
      boolean var11 = var5.isBold();
      if (var1.hasShadow()) {
         this.render(var6, var7 + var1.shadowOffset(), var8 + var1.shadowOffset(), var2, var3, var10, var11, var4);
         this.render(var6, var7, var8, 0.03F, var2, var3, var9, var11, var4);
      } else {
         this.render(var6, var7, var8, var2, var3, var9, var11, var4);
      }

      if (var11) {
         if (var1.hasShadow()) {
            this.render(var6, var7 + var1.boldOffset() + var1.shadowOffset(), var8 + var1.shadowOffset(), 0.001F, var2, var3, var10, true, var4);
            this.render(var6, var7 + var1.boldOffset(), var8, 0.03F, var2, var3, var9, true, var4);
         } else {
            this.render(var6, var7 + var1.boldOffset(), var8, var2, var3, var9, true, var4);
         }
      }

   }

   private void render(boolean var1, float var2, float var3, Matrix4f var4, VertexConsumer var5, int var6, boolean var7, int var8) {
      this.render(var1, var2, var3, 0.0F, var4, var5, var6, var7, var8);
   }

   private void render(boolean var1, float var2, float var3, float var4, Matrix4f var5, VertexConsumer var6, int var7, boolean var8, int var9) {
      float var10 = var2 + this.left;
      float var11 = var2 + this.right;
      float var12 = var3 + this.up;
      float var13 = var3 + this.down;
      float var14 = var1 ? 1.0F - 0.25F * this.up : 0.0F;
      float var15 = var1 ? 1.0F - 0.25F * this.down : 0.0F;
      float var16 = var8 ? 0.1F : 0.0F;
      var6.addVertex(var5, var10 + var14 - var16, var12 - var16, var4).setColor(var7).setUv(this.u0, this.v0).setLight(var9);
      var6.addVertex(var5, var10 + var15 - var16, var13 + var16, var4).setColor(var7).setUv(this.u0, this.v1).setLight(var9);
      var6.addVertex(var5, var11 + var15 + var16, var13 + var16, var4).setColor(var7).setUv(this.u1, this.v1).setLight(var9);
      var6.addVertex(var5, var11 + var14 + var16, var12 - var16, var4).setColor(var7).setUv(this.u1, this.v0).setLight(var9);
   }

   public void renderEffect(Effect var1, Matrix4f var2, VertexConsumer var3, int var4) {
      if (var1.hasShadow()) {
         this.buildEffect(var1, var1.shadowOffset(), 0.0F, var1.shadowColor(), var3, var4, var2);
         this.buildEffect(var1, 0.0F, 0.03F, var1.color, var3, var4, var2);
      } else {
         this.buildEffect(var1, 0.0F, 0.0F, var1.color, var3, var4, var2);
      }

   }

   private void buildEffect(Effect var1, float var2, float var3, int var4, VertexConsumer var5, int var6, Matrix4f var7) {
      var5.addVertex(var7, var1.x0 + var2, var1.y0 + var2, var1.depth + var3).setColor(var4).setUv(this.u0, this.v0).setLight(var6);
      var5.addVertex(var7, var1.x1 + var2, var1.y0 + var2, var1.depth + var3).setColor(var4).setUv(this.u0, this.v1).setLight(var6);
      var5.addVertex(var7, var1.x1 + var2, var1.y1 + var2, var1.depth + var3).setColor(var4).setUv(this.u1, this.v1).setLight(var6);
      var5.addVertex(var7, var1.x0 + var2, var1.y1 + var2, var1.depth + var3).setColor(var4).setUv(this.u1, this.v0).setLight(var6);
   }

   public RenderType renderType(Font.DisplayMode var1) {
      return this.renderTypes.select(var1);
   }

   public static record GlyphInstance(float x, float y, int color, int shadowColor, BakedGlyph glyph, Style style, float boldOffset, float shadowOffset) {
      public GlyphInstance(float var1, float var2, int var3, int var4, BakedGlyph var5, Style var6, float var7, float var8) {
         super();
         this.x = var1;
         this.y = var2;
         this.color = var3;
         this.shadowColor = var4;
         this.glyph = var5;
         this.style = var6;
         this.boldOffset = var7;
         this.shadowOffset = var8;
      }

      boolean hasShadow() {
         return this.shadowColor() != 0;
      }
   }

   public static record Effect(float x0, float y0, float x1, float y1, float depth, int color, int shadowColor, float shadowOffset) {
      final float x0;
      final float y0;
      final float x1;
      final float y1;
      final float depth;
      final int color;

      public Effect(float var1, float var2, float var3, float var4, float var5, int var6) {
         this(var1, var2, var3, var4, var5, var6, 0, 0.0F);
      }

      public Effect(float var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8) {
         super();
         this.x0 = var1;
         this.y0 = var2;
         this.x1 = var3;
         this.y1 = var4;
         this.depth = var5;
         this.color = var6;
         this.shadowColor = var7;
         this.shadowOffset = var8;
      }

      boolean hasShadow() {
         return this.shadowColor() != 0;
      }
   }
}
