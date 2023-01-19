package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringDecomposer;

public class Font {
   private static final float EFFECT_DEPTH = 0.01F;
   private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, 0.03F);
   public static final int ALPHA_CUTOFF = 8;
   public final int lineHeight = 9;
   public final RandomSource random = RandomSource.create();
   private final Function<ResourceLocation, FontSet> fonts;
   final boolean filterFishyGlyphs;
   private final StringSplitter splitter;

   public Font(Function<ResourceLocation, FontSet> var1, boolean var2) {
      super();
      this.fonts = var1;
      this.filterFishyGlyphs = var2;
      this.splitter = new StringSplitter(
         (var1x, var2x) -> this.getFontSet(var2x.getFont()).getGlyphInfo(var1x, this.filterFishyGlyphs).getAdvance(var2x.isBold())
      );
   }

   FontSet getFontSet(ResourceLocation var1) {
      return this.fonts.apply(var1);
   }

   public int drawShadow(PoseStack var1, String var2, float var3, float var4, int var5) {
      return this.drawInternal(var2, var3, var4, var5, var1.last().pose(), true, this.isBidirectional());
   }

   public int drawShadow(PoseStack var1, String var2, float var3, float var4, int var5, boolean var6) {
      return this.drawInternal(var2, var3, var4, var5, var1.last().pose(), true, var6);
   }

   public int draw(PoseStack var1, String var2, float var3, float var4, int var5) {
      return this.drawInternal(var2, var3, var4, var5, var1.last().pose(), false, this.isBidirectional());
   }

   public int drawShadow(PoseStack var1, FormattedCharSequence var2, float var3, float var4, int var5) {
      return this.drawInternal(var2, var3, var4, var5, var1.last().pose(), true);
   }

   public int drawShadow(PoseStack var1, Component var2, float var3, float var4, int var5) {
      return this.drawInternal(var2.getVisualOrderText(), var3, var4, var5, var1.last().pose(), true);
   }

   public int draw(PoseStack var1, FormattedCharSequence var2, float var3, float var4, int var5) {
      return this.drawInternal(var2, var3, var4, var5, var1.last().pose(), false);
   }

   public int draw(PoseStack var1, Component var2, float var3, float var4, int var5) {
      return this.drawInternal(var2.getVisualOrderText(), var3, var4, var5, var1.last().pose(), false);
   }

   public String bidirectionalShaping(String var1) {
      try {
         Bidi var2 = new Bidi(new ArabicShaping(8).shape(var1), 127);
         var2.setReorderingMode(0);
         return var2.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return var1;
      }
   }

   private int drawInternal(String var1, float var2, float var3, int var4, Matrix4f var5, boolean var6, boolean var7) {
      if (var1 == null) {
         return 0;
      } else {
         MultiBufferSource.BufferSource var8 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         int var9 = this.drawInBatch(var1, var2, var3, var4, var6, var5, var8, false, 0, 15728880, var7);
         var8.endBatch();
         return var9;
      }
   }

   private int drawInternal(FormattedCharSequence var1, float var2, float var3, int var4, Matrix4f var5, boolean var6) {
      MultiBufferSource.BufferSource var7 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      int var8 = this.drawInBatch(var1, var2, var3, var4, var6, var5, var7, false, 0, 15728880);
      var7.endBatch();
      return var8;
   }

   public int drawInBatch(
      String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10
   ) {
      return this.drawInBatch(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, this.isBidirectional());
   }

   public int drawInBatch(
      String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10, boolean var11
   ) {
      return this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public int drawInBatch(
      Component var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10
   ) {
      return this.drawInBatch(var1.getVisualOrderText(), var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public int drawInBatch(
      FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10
   ) {
      return this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void drawInBatch8xOutline(FormattedCharSequence var1, float var2, float var3, int var4, int var5, Matrix4f var6, MultiBufferSource var7, int var8) {
      int var9 = adjustColor(var5);
      Font.StringRenderOutput var10 = new Font.StringRenderOutput(var7, 0.0F, 0.0F, var9, false, var6, Font.DisplayMode.NORMAL, var8);

      for(int var11 = -1; var11 <= 1; ++var11) {
         for(int var12 = -1; var12 <= 1; ++var12) {
            if (var11 != 0 || var12 != 0) {
               float[] var13 = new float[]{var2};
               int var14 = var11;
               int var15 = var12;
               var1.accept((var7x, var8x, var9x) -> {
                  boolean var10x = var8x.isBold();
                  FontSet var11x = this.getFontSet(var8x.getFont());
                  GlyphInfo var12x = var11x.getGlyphInfo(var9x, this.filterFishyGlyphs);
                  var10.x = var13[0] + (float)var14 * var12x.getShadowOffset();
                  var10.y = var3 + (float)var15 * var12x.getShadowOffset();
                  var13[0] += var12x.getAdvance(var10x);
                  return var10.accept(var7x, var8x.withColor(var9), var9x);
               });
            }
         }
      }

      Font.StringRenderOutput var16 = new Font.StringRenderOutput(var7, var2, var3, adjustColor(var4), false, var6, Font.DisplayMode.POLYGON_OFFSET, var8);
      var1.accept(var16);
      var16.finish(0, var2);
   }

   private static int adjustColor(int var0) {
      return (var0 & -67108864) == 0 ? var0 | 0xFF000000 : var0;
   }

   private int drawInternal(
      String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10, boolean var11
   ) {
      if (var11) {
         var1 = this.bidirectionalShaping(var1);
      }

      var4 = adjustColor(var4);
      Matrix4f var12 = var6.copy();
      if (var5) {
         this.renderText(var1, var2, var3, var4, true, var6, var7, var8, var9, var10);
         var12.translate(SHADOW_OFFSET);
      }

      var2 = this.renderText(var1, var2, var3, var4, false, var12, var7, var8, var9, var10);
      return (int)var2 + (var5 ? 1 : 0);
   }

   private int drawInternal(
      FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10
   ) {
      var4 = adjustColor(var4);
      Matrix4f var11 = var6.copy();
      if (var5) {
         this.renderText(var1, var2, var3, var4, true, var6, var7, var8, var9, var10);
         var11.translate(SHADOW_OFFSET);
      }

      var2 = this.renderText(var1, var2, var3, var4, false, var11, var7, var8, var9, var10);
      return (int)var2 + (var5 ? 1 : 0);
   }

   private float renderText(
      String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10
   ) {
      Font.StringRenderOutput var11 = new Font.StringRenderOutput(var7, var2, var3, var4, var5, var6, var8, var10);
      StringDecomposer.iterateFormatted(var1, Style.EMPTY, var11);
      return var11.finish(var9, var2);
   }

   private float renderText(
      FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10
   ) {
      Font.StringRenderOutput var11 = new Font.StringRenderOutput(var7, var2, var3, var4, var5, var6, var8, var10);
      var1.accept(var11);
      return var11.finish(var9, var2);
   }

   void renderChar(
      BakedGlyph var1,
      boolean var2,
      boolean var3,
      float var4,
      float var5,
      float var6,
      Matrix4f var7,
      VertexConsumer var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13
   ) {
      var1.render(var3, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      if (var2) {
         var1.render(var3, var5 + var4, var6, var7, var8, var9, var10, var11, var12, var13);
      }
   }

   public int width(String var1) {
      return Mth.ceil(this.splitter.stringWidth(var1));
   }

   public int width(FormattedText var1) {
      return Mth.ceil(this.splitter.stringWidth(var1));
   }

   public int width(FormattedCharSequence var1) {
      return Mth.ceil(this.splitter.stringWidth(var1));
   }

   public String plainSubstrByWidth(String var1, int var2, boolean var3) {
      return var3 ? this.splitter.plainTailByWidth(var1, var2, Style.EMPTY) : this.splitter.plainHeadByWidth(var1, var2, Style.EMPTY);
   }

   public String plainSubstrByWidth(String var1, int var2) {
      return this.splitter.plainHeadByWidth(var1, var2, Style.EMPTY);
   }

   public FormattedText substrByWidth(FormattedText var1, int var2) {
      return this.splitter.headByWidth(var1, var2, Style.EMPTY);
   }

   public void drawWordWrap(FormattedText var1, int var2, int var3, int var4, int var5) {
      Matrix4f var6 = Transformation.identity().getMatrix();

      for(FormattedCharSequence var8 : this.split(var1, var4)) {
         this.drawInternal(var8, (float)var2, (float)var3, var5, var6, false);
         var3 += 9;
      }
   }

   public int wordWrapHeight(String var1, int var2) {
      return 9 * this.splitter.splitLines(var1, var2, Style.EMPTY).size();
   }

   public int wordWrapHeight(FormattedText var1, int var2) {
      return 9 * this.splitter.splitLines(var1, var2, Style.EMPTY).size();
   }

   public List<FormattedCharSequence> split(FormattedText var1, int var2) {
      return Language.getInstance().getVisualOrder(this.splitter.splitLines(var1, var2, Style.EMPTY));
   }

   public boolean isBidirectional() {
      return Language.getInstance().isDefaultRightToLeft();
   }

   public StringSplitter getSplitter() {
      return this.splitter;
   }

   public static enum DisplayMode {
      NORMAL,
      SEE_THROUGH,
      POLYGON_OFFSET;

      private DisplayMode() {
      }
   }

   class StringRenderOutput implements FormattedCharSink {
      final MultiBufferSource bufferSource;
      private final boolean dropShadow;
      private final float dimFactor;
      private final float r;
      private final float g;
      private final float b;
      private final float a;
      private final Matrix4f pose;
      private final Font.DisplayMode mode;
      private final int packedLightCoords;
      float x;
      float y;
      @Nullable
      private List<BakedGlyph.Effect> effects;

      private void addEffect(BakedGlyph.Effect var1) {
         if (this.effects == null) {
            this.effects = Lists.newArrayList();
         }

         this.effects.add(var1);
      }

      public StringRenderOutput(MultiBufferSource var2, float var3, float var4, int var5, boolean var6, Matrix4f var7, boolean var8, int var9) {
         this(var2, var3, var4, var5, var6, var7, var8 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, var9);
      }

      public StringRenderOutput(MultiBufferSource var2, float var3, float var4, int var5, boolean var6, Matrix4f var7, Font.DisplayMode var8, int var9) {
         super();
         this.bufferSource = var2;
         this.x = var3;
         this.y = var4;
         this.dropShadow = var6;
         this.dimFactor = var6 ? 0.25F : 1.0F;
         this.r = (float)(var5 >> 16 & 0xFF) / 255.0F * this.dimFactor;
         this.g = (float)(var5 >> 8 & 0xFF) / 255.0F * this.dimFactor;
         this.b = (float)(var5 & 0xFF) / 255.0F * this.dimFactor;
         this.a = (float)(var5 >> 24 & 0xFF) / 255.0F;
         this.pose = var7;
         this.mode = var8;
         this.packedLightCoords = var9;
      }

      @Override
      public boolean accept(int var1, Style var2, int var3) {
         FontSet var4 = Font.this.getFontSet(var2.getFont());
         GlyphInfo var5 = var4.getGlyphInfo(var3, Font.this.filterFishyGlyphs);
         BakedGlyph var6 = var2.isObfuscated() && var3 != 32 ? var4.getRandomGlyph(var5) : var4.getGlyph(var3);
         boolean var7 = var2.isBold();
         float var11 = this.a;
         TextColor var12 = var2.getColor();
         float var8;
         float var9;
         float var10;
         if (var12 != null) {
            int var13 = var12.getValue();
            var8 = (float)(var13 >> 16 & 0xFF) / 255.0F * this.dimFactor;
            var9 = (float)(var13 >> 8 & 0xFF) / 255.0F * this.dimFactor;
            var10 = (float)(var13 & 0xFF) / 255.0F * this.dimFactor;
         } else {
            var8 = this.r;
            var9 = this.g;
            var10 = this.b;
         }

         if (!(var6 instanceof EmptyGlyph)) {
            float var16 = var7 ? var5.getBoldOffset() : 0.0F;
            float var14 = this.dropShadow ? var5.getShadowOffset() : 0.0F;
            VertexConsumer var15 = this.bufferSource.getBuffer(var6.renderType(this.mode));
            Font.this.renderChar(
               var6, var7, var2.isItalic(), var16, this.x + var14, this.y + var14, this.pose, var15, var8, var9, var10, var11, this.packedLightCoords
            );
         }

         float var17 = var5.getAdvance(var7);
         float var18 = this.dropShadow ? 1.0F : 0.0F;
         if (var2.isStrikethrough()) {
            this.addEffect(
               new BakedGlyph.Effect(
                  this.x + var18 - 1.0F, this.y + var18 + 4.5F, this.x + var18 + var17, this.y + var18 + 4.5F - 1.0F, 0.01F, var8, var9, var10, var11
               )
            );
         }

         if (var2.isUnderlined()) {
            this.addEffect(
               new BakedGlyph.Effect(
                  this.x + var18 - 1.0F, this.y + var18 + 9.0F, this.x + var18 + var17, this.y + var18 + 9.0F - 1.0F, 0.01F, var8, var9, var10, var11
               )
            );
         }

         this.x += var17;
         return true;
      }

      public float finish(int var1, float var2) {
         if (var1 != 0) {
            float var3 = (float)(var1 >> 24 & 0xFF) / 255.0F;
            float var4 = (float)(var1 >> 16 & 0xFF) / 255.0F;
            float var5 = (float)(var1 >> 8 & 0xFF) / 255.0F;
            float var6 = (float)(var1 & 0xFF) / 255.0F;
            this.addEffect(new BakedGlyph.Effect(var2 - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, var4, var5, var6, var3));
         }

         if (this.effects != null) {
            BakedGlyph var7 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            VertexConsumer var8 = this.bufferSource.getBuffer(var7.renderType(this.mode));

            for(BakedGlyph.Effect var10 : this.effects) {
               var7.renderEffect(var10, this.pose, var8, this.packedLightCoords);
            }
         }

         return this.x;
      }
   }
}
