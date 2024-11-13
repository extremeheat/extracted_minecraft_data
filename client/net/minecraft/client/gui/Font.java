package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
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
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringDecomposer;
import org.joml.Matrix4f;

public class Font {
   private static final float EFFECT_DEPTH = 0.01F;
   public static final float SHADOW_DEPTH = 0.03F;
   public static final int NO_SHADOW = 0;
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
      this.splitter = new StringSplitter((var1x, var2x) -> this.getFontSet(var2x.getFont()).getGlyphInfo(var1x, this.filterFishyGlyphs).getAdvance(var2x.isBold()));
   }

   FontSet getFontSet(ResourceLocation var1) {
      return (FontSet)this.fonts.apply(var1);
   }

   public String bidirectionalShaping(String var1) {
      try {
         Bidi var2 = new Bidi((new ArabicShaping(8)).shape(var1), 127);
         var2.setReorderingMode(0);
         return var2.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return var1;
      }
   }

   public int drawInBatch(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      if (this.isBidirectional()) {
         var1 = this.bidirectionalShaping(var1);
      }

      return this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   public int drawInBatch(Component var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      return this.drawInBatch(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   public int drawInBatch(Component var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      return this.drawInternal(var1.getVisualOrderText(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public int drawInBatch(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      return this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   public void drawInBatch8xOutline(FormattedCharSequence var1, float var2, float var3, int var4, int var5, Matrix4f var6, MultiBufferSource var7, int var8) {
      int var9 = adjustColor(var5);
      StringRenderOutput var10 = new StringRenderOutput(var7, 0.0F, 0.0F, var9, false, var6, Font.DisplayMode.NORMAL, var8);

      for(int var11 = -1; var11 <= 1; ++var11) {
         for(int var12 = -1; var12 <= 1; ++var12) {
            if (var11 != 0 || var12 != 0) {
               float[] var13 = new float[]{var2};
               var1.accept((var7x, var8x, var9x) -> {
                  boolean var10x = var8x.isBold();
                  FontSet var11x = this.getFontSet(var8x.getFont());
                  GlyphInfo var12x = var11x.getGlyphInfo(var9x, this.filterFishyGlyphs);
                  var10.x = var13[0] + (float)var11 * var12x.getShadowOffset();
                  var10.y = var3 + (float)var12 * var12x.getShadowOffset();
                  var13[0] += var12x.getAdvance(var10x);
                  return var10.accept(var7x, var8x.withColor(var9), var9x);
               });
            }
         }
      }

      var10.renderCharacters();
      StringRenderOutput var16 = new StringRenderOutput(var7, var2, var3, adjustColor(var4), false, var6, Font.DisplayMode.POLYGON_OFFSET, var8);
      var1.accept(var16);
      var16.finish(var2);
   }

   private static int adjustColor(int var0) {
      return (var0 & -67108864) == 0 ? ARGB.opaque(var0) : var0;
   }

   private int drawInternal(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      var4 = adjustColor(var4);
      var2 = this.renderText(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      return (int)var2 + (var5 ? 1 : 0);
   }

   private int drawInternal(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      var4 = adjustColor(var4);
      var2 = this.renderText(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      return (int)var2 + (var5 ? 1 : 0);
   }

   private float renderText(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      StringRenderOutput var12 = new StringRenderOutput(var7, var2, var3, var4, var9, var5, var6, var8, var10, var11);
      StringDecomposer.iterateFormatted((String)var1, Style.EMPTY, var12);
      return var12.finish(var2);
   }

   private float renderText(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      StringRenderOutput var12 = new StringRenderOutput(var7, var2, var3, var4, var9, var5, var6, var8, var10, var11);
      var1.accept(var12);
      return var12.finish(var2);
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

      // $FF: synthetic method
      private static DisplayMode[] $values() {
         return new DisplayMode[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
      }
   }

   class StringRenderOutput implements FormattedCharSink {
      final MultiBufferSource bufferSource;
      private final boolean drawShadow;
      private final int color;
      private final int backgroundColor;
      private final Matrix4f pose;
      private final DisplayMode mode;
      private final int packedLightCoords;
      private final boolean inverseDepth;
      float x;
      float y;
      private final List<BakedGlyph.GlyphInstance> glyphInstances;
      @Nullable
      private List<BakedGlyph.Effect> effects;

      private void addEffect(BakedGlyph.Effect var1) {
         if (this.effects == null) {
            this.effects = Lists.newArrayList();
         }

         this.effects.add(var1);
      }

      public StringRenderOutput(final MultiBufferSource var2, final float var3, final float var4, final int var5, final boolean var6, final Matrix4f var7, final DisplayMode var8, final int var9) {
         this(var2, var3, var4, var5, 0, var6, var7, var8, var9, true);
      }

      public StringRenderOutput(final MultiBufferSource var2, final float var3, final float var4, final int var5, final int var6, final boolean var7, final Matrix4f var8, final DisplayMode var9, final int var10, final boolean var11) {
         super();
         this.glyphInstances = new ArrayList();
         this.bufferSource = var2;
         this.x = var3;
         this.y = var4;
         this.drawShadow = var7;
         this.color = var5;
         this.backgroundColor = var6;
         this.pose = var8;
         this.mode = var9;
         this.packedLightCoords = var10;
         this.inverseDepth = var11;
      }

      public boolean accept(int var1, Style var2, int var3) {
         FontSet var4 = Font.this.getFontSet(var2.getFont());
         GlyphInfo var5 = var4.getGlyphInfo(var3, Font.this.filterFishyGlyphs);
         BakedGlyph var6 = var2.isObfuscated() && var3 != 32 ? var4.getRandomGlyph(var5) : var4.getGlyph(var3);
         boolean var7 = var2.isBold();
         TextColor var8 = var2.getColor();
         int var9 = this.getTextColor(var8);
         int var10 = this.getShadowColor(var2, var9);
         float var11 = var5.getAdvance(var7);
         float var12 = var1 == 0 ? this.x - 1.0F : this.x;
         float var13 = var5.getShadowOffset();
         if (!(var6 instanceof EmptyGlyph)) {
            float var14 = var7 ? var5.getBoldOffset() : 0.0F;
            this.glyphInstances.add(new BakedGlyph.GlyphInstance(this.x, this.y, var9, var10, var6, var2, var14, var13));
         }

         if (var2.isStrikethrough()) {
            this.addEffect(new BakedGlyph.Effect(var12, this.y + 4.5F, this.x + var11, this.y + 4.5F - 1.0F, this.getOverTextEffectDepth(), var9, var10, var13));
         }

         if (var2.isUnderlined()) {
            this.addEffect(new BakedGlyph.Effect(var12, this.y + 9.0F, this.x + var11, this.y + 9.0F - 1.0F, this.getOverTextEffectDepth(), var9, var10, var13));
         }

         this.x += var11;
         return true;
      }

      float finish(float var1) {
         BakedGlyph var2 = null;
         if (this.backgroundColor != 0) {
            BakedGlyph.Effect var3 = new BakedGlyph.Effect(var1 - 1.0F, this.y + 9.0F, this.x, this.y - 1.0F, this.getUnderTextEffectDepth(), this.backgroundColor);
            var2 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            VertexConsumer var4 = this.bufferSource.getBuffer(var2.renderType(this.mode));
            var2.renderEffect(var3, this.pose, var4, this.packedLightCoords);
         }

         this.renderCharacters();
         if (this.effects != null) {
            if (var2 == null) {
               var2 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            }

            VertexConsumer var6 = this.bufferSource.getBuffer(var2.renderType(this.mode));

            for(BakedGlyph.Effect var5 : this.effects) {
               var2.renderEffect(var5, this.pose, var6, this.packedLightCoords);
            }
         }

         return this.x;
      }

      private int getTextColor(@Nullable TextColor var1) {
         if (var1 != null) {
            int var2 = ARGB.alpha(this.color);
            int var3 = var1.getValue();
            return ARGB.color(var2, var3);
         } else {
            return this.color;
         }
      }

      private int getShadowColor(Style var1, int var2) {
         Integer var3 = var1.getShadowColor();
         if (var3 != null) {
            float var4 = ARGB.alphaFloat(var2);
            float var5 = ARGB.alphaFloat(var3);
            return var4 != 1.0F ? ARGB.color(ARGB.as8BitChannel(var4 * var5), var3) : var3;
         } else {
            return this.drawShadow ? ARGB.scaleRGB(var2, 0.25F) : 0;
         }
      }

      void renderCharacters() {
         for(BakedGlyph.GlyphInstance var2 : this.glyphInstances) {
            BakedGlyph var3 = var2.glyph();
            VertexConsumer var4 = this.bufferSource.getBuffer(var3.renderType(this.mode));
            var3.renderChar(var2, this.pose, var4, this.packedLightCoords);
         }

      }

      private float getOverTextEffectDepth() {
         return this.inverseDepth ? 0.01F : -0.01F;
      }

      private float getUnderTextEffectDepth() {
         return this.inverseDepth ? -0.01F : 0.01F;
      }
   }
}
