package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class BakedSheetGlyph implements BakedGlyph, EffectGlyph {
   public static final float Z_FIGHTER = 0.001F;
   private final GlyphInfo info;
   final GlyphRenderTypes renderTypes;
   @Nullable
   final GpuTextureView textureView;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedSheetGlyph(GlyphInfo var1, GlyphRenderTypes var2, @Nullable GpuTextureView var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      super();
      this.info = var1;
      this.renderTypes = var2;
      this.textureView = var3;
      this.u0 = var4;
      this.u1 = var5;
      this.v0 = var6;
      this.v1 = var7;
      this.left = var8;
      this.right = var9;
      this.up = var10;
      this.down = var11;
   }

   float left(GlyphInstance var1) {
      return var1.x + this.left + (var1.style.isItalic() ? Math.min(this.shearTop(), this.shearBottom()) : 0.0F) - extraThickness(var1.style.isBold());
   }

   float top(GlyphInstance var1) {
      return var1.y + this.up - extraThickness(var1.style.isBold());
   }

   float right(GlyphInstance var1) {
      return var1.x + this.right + (var1.hasShadow() ? var1.shadowOffset : 0.0F) + (var1.style.isItalic() ? Math.max(this.shearTop(), this.shearBottom()) : 0.0F) + extraThickness(var1.style.isBold());
   }

   float bottom(GlyphInstance var1) {
      return var1.y + this.down + (var1.hasShadow() ? var1.shadowOffset : 0.0F) + extraThickness(var1.style.isBold());
   }

   void renderChar(GlyphInstance var1, Matrix4f var2, VertexConsumer var3, int var4, boolean var5) {
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

         var12 = var5 ? 0.0F : 0.03F;
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

   void renderEffect(EffectInstance var1, Matrix4f var2, VertexConsumer var3, int var4, boolean var5) {
      float var6 = var5 ? 0.0F : var1.depth;
      if (var1.hasShadow()) {
         this.buildEffect(var1, var1.shadowOffset(), var6, var1.shadowColor(), var3, var4, var2);
         var6 += var5 ? 0.0F : 0.03F;
      }

      this.buildEffect(var1, 0.0F, var6, var1.color, var3, var4, var2);
   }

   private void buildEffect(EffectInstance var1, float var2, float var3, int var4, VertexConsumer var5, int var6, Matrix4f var7) {
      var5.addVertex(var7, var1.x0 + var2, var1.y1 + var2, var3).setColor(var4).setUv(this.u0, this.v0).setLight(var6);
      var5.addVertex(var7, var1.x1 + var2, var1.y1 + var2, var3).setColor(var4).setUv(this.u0, this.v1).setLight(var6);
      var5.addVertex(var7, var1.x1 + var2, var1.y0 + var2, var3).setColor(var4).setUv(this.u1, this.v1).setLight(var6);
      var5.addVertex(var7, var1.x0 + var2, var1.y0 + var2, var3).setColor(var4).setUv(this.u1, this.v0).setLight(var6);
   }

   public GlyphInfo info() {
      return this.info;
   }

   public TextRenderable createGlyph(float var1, float var2, int var3, int var4, Style var5, float var6, float var7) {
      return new GlyphInstance(var1, var2, var3, var4, this, var5, var6, var7);
   }

   public TextRenderable createEffect(float var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8) {
      return new EffectInstance(this, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   static record GlyphInstance(float x, float y, int color, int shadowColor, BakedSheetGlyph glyph, Style style, float boldOffset, float shadowOffset) implements TextRenderable {
      final float x;
      final float y;
      final Style style;
      final float shadowOffset;

      GlyphInstance(float var1, float var2, int var3, int var4, BakedSheetGlyph var5, Style var6, float var7, float var8) {
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

      public void render(Matrix4f var1, VertexConsumer var2, int var3, boolean var4) {
         this.glyph.renderChar(this, var1, var2, var3, var4);
      }

      public RenderType renderType(Font.DisplayMode var1) {
         return this.glyph.renderTypes.select(var1);
      }

      @Nullable
      public GpuTextureView textureView() {
         return this.glyph.textureView;
      }

      public RenderPipeline guiPipeline() {
         return this.glyph.renderTypes.guiPipeline();
      }
   }

   static record EffectInstance(BakedSheetGlyph glyph, float x0, float y0, float x1, float y1, float depth, int color, int shadowColor, float shadowOffset) implements TextRenderable {
      final float x0;
      final float y0;
      final float x1;
      final float y1;
      final float depth;
      final int color;

      EffectInstance(BakedSheetGlyph var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9) {
         super();
         this.glyph = var1;
         this.x0 = var2;
         this.y0 = var3;
         this.x1 = var4;
         this.y1 = var5;
         this.depth = var6;
         this.color = var7;
         this.shadowColor = var8;
         this.shadowOffset = var9;
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

      public void render(Matrix4f var1, VertexConsumer var2, int var3, boolean var4) {
         this.glyph.renderEffect(this, var1, var2, var3, false);
      }

      public RenderType renderType(Font.DisplayMode var1) {
         return this.glyph.renderTypes.select(var1);
      }

      @Nullable
      public GpuTextureView textureView() {
         return this.glyph.textureView;
      }

      public RenderPipeline guiPipeline() {
         return this.glyph.renderTypes.guiPipeline();
      }
   }
}
