package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class BakedGlyph {
   public static final float Z_FIGHTER = 0.001F;
   private final GlyphRenderTypes renderTypes;
   @Nullable
   private final GpuTextureView textureView;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(GlyphRenderTypes var1, @Nullable GpuTextureView var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      super();
      this.renderTypes = var1;
      this.textureView = var2;
      this.u0 = var3;
      this.u1 = var4;
      this.v0 = var5;
      this.v1 = var6;
      this.left = var7;
      this.right = var8;
      this.up = var9;
      this.down = var10;
   }

   public float left(GlyphInstance var1) {
      return var1.x + this.left + (var1.style.isItalic() ? Math.min(this.shearTop(), this.shearBottom()) : 0.0F) - extraThickness(var1.style.isBold());
   }

   public float top(GlyphInstance var1) {
      return var1.y + this.up - extraThickness(var1.style.isBold());
   }

   public float right(GlyphInstance var1) {
      return var1.x + this.right + (var1.hasShadow() ? var1.shadowOffset : 0.0F) + (var1.style.isItalic() ? Math.max(this.shearTop(), this.shearBottom()) : 0.0F) + extraThickness(var1.style.isBold());
   }

   public float bottom(GlyphInstance var1) {
      return var1.y + this.down + (var1.hasShadow() ? var1.shadowOffset : 0.0F) + extraThickness(var1.style.isBold());
   }

   public void renderChar(GlyphInstance var1, Matrix4f var2, VertexConsumer var3, int var4, boolean var5) {
      Style var6 = var1.style();
      boolean var7 = var6.isItalic();
      float var8 = var1.x();
      float var9 = var1.y();
      int var10 = var1.color();
      boolean var11 = var6.isBold();
      float var13 = var5 ? 0.0F : 0.001F;
      float var12;
      if (var1.hasShadow()) {
         int var14 = var1.shadowColor();
         this.render(var7, var8 + var1.shadowOffset(), var9 + var1.shadowOffset(), 0.0F, var2, var3, var14, var11, var4);
         if (var11) {
            this.render(var7, var8 + var1.boldOffset() + var1.shadowOffset(), var9 + var1.shadowOffset(), var13, var2, var3, var14, true, var4);
         }

         var12 = var5 ? 0.0F : 0.003F;
      } else {
         var12 = 0.0F;
      }

      this.render(var7, var8, var9, var12, var2, var3, var10, var11, var4);
      if (var11) {
         this.render(var7, var8 + var1.boldOffset(), var9, var12 + var13, var2, var3, var10, true, var4);
      }

   }

   private void render(boolean var1, float var2, float var3, float var4, Matrix4f var5, VertexConsumer var6, int var7, boolean var8, int var9) {
      float var10 = var2 + this.left;
      float var11 = var2 + this.right;
      float var12 = var3 + this.up;
      float var13 = var3 + this.down;
      float var14 = var1 ? this.shearTop() : 0.0F;
      float var15 = var1 ? this.shearBottom() : 0.0F;
      float var16 = extraThickness(var8);
      var6.addVertex(var5, var10 + var14 - var16, var12 - var16, var4).setColor(var7).setUv(this.u0, this.v0).setLight(var9);
      var6.addVertex(var5, var10 + var15 - var16, var13 + var16, var4).setColor(var7).setUv(this.u0, this.v1).setLight(var9);
      var6.addVertex(var5, var11 + var15 + var16, var13 + var16, var4).setColor(var7).setUv(this.u1, this.v1).setLight(var9);
      var6.addVertex(var5, var11 + var14 + var16, var12 - var16, var4).setColor(var7).setUv(this.u1, this.v0).setLight(var9);
   }

   private static float extraThickness(boolean var0) {
      return var0 ? 0.1F : 0.0F;
   }

   private float shearBottom() {
      return 1.0F - 0.25F * this.down;
   }

   private float shearTop() {
      return 1.0F - 0.25F * this.up;
   }

   public void renderEffect(Effect var1, Matrix4f var2, VertexConsumer var3, int var4, boolean var5) {
      float var6 = var5 ? 0.0F : var1.depth;
      if (var1.hasShadow()) {
         this.buildEffect(var1, var1.shadowOffset(), var6, var1.shadowColor(), var3, var4, var2);
         var6 += var5 ? 0.0F : 0.003F;
      }

      this.buildEffect(var1, 0.0F, var6, var1.color, var3, var4, var2);
   }

   private void buildEffect(Effect var1, float var2, float var3, int var4, VertexConsumer var5, int var6, Matrix4f var7) {
      var5.addVertex(var7, var1.x0 + var2, var1.y1 + var2, var3).setColor(var4).setUv(this.u0, this.v0).setLight(var6);
      var5.addVertex(var7, var1.x1 + var2, var1.y1 + var2, var3).setColor(var4).setUv(this.u0, this.v1).setLight(var6);
      var5.addVertex(var7, var1.x1 + var2, var1.y0 + var2, var3).setColor(var4).setUv(this.u1, this.v1).setLight(var6);
      var5.addVertex(var7, var1.x0 + var2, var1.y0 + var2, var3).setColor(var4).setUv(this.u1, this.v0).setLight(var6);
   }

   @Nullable
   public GpuTextureView textureView() {
      return this.textureView;
   }

   public RenderPipeline guiPipeline() {
      return this.renderTypes.guiPipeline();
   }

   public RenderType renderType(Font.DisplayMode var1) {
      return this.renderTypes.select(var1);
   }

   public static record GlyphInstance(float x, float y, int color, int shadowColor, BakedGlyph glyph, Style style, float boldOffset, float shadowOffset) {
      final float x;
      final float y;
      final Style style;
      final float shadowOffset;

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

      public float left() {
         return this.glyph.left(this);
      }

      public float top() {
         return this.glyph.top(this);
      }

      public float right() {
         return this.glyph.right(this);
      }

      public float bottom() {
         return this.glyph.bottom(this);
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

      public float left() {
         return this.x0;
      }

      public float top() {
         return this.y0;
      }

      public float right() {
         return this.x1 + (this.hasShadow() ? this.shadowOffset : 0.0F);
      }

      public float bottom() {
         return this.y1 + (this.hasShadow() ? this.shadowOffset : 0.0F);
      }

      boolean hasShadow() {
         return this.shadowColor() != 0;
      }
   }
}
