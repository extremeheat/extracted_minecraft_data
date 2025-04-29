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
import net.minecraft.client.gui.render.state.TextRenderState;
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
   private static final float EFFECT_DEPTH = 0.001F;
   public static final float SHADOW_DEPTH = 0.003F;
   public static final int NO_SHADOW = 0;
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

   public void drawInBatch(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      if (this.isBidirectional()) {
         var1 = this.bidirectionalShaping(var1);
      }

      this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   public TextRenderState extractTextRenderState(String var1, float var2, float var3, int var4, boolean var5, DisplayMode var6, int var7, int var8) {
      if (this.isBidirectional()) {
         var1 = this.bidirectionalShaping(var1);
      }

      return this.extractInternal(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public void drawInBatch(Component var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      this.drawInBatch(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   public void drawInBatch(Component var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      this.drawInternal(var1.getVisualOrderText(), var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void drawInBatch(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   public TextRenderState extractTextRenderState(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, DisplayMode var6, int var7, int var8) {
      return this.extractInternal(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public void drawInBatch8xOutline(FormattedCharSequence var1, float var2, float var3, int var4, int var5, Matrix4f var6, MultiBufferSource var7, int var8) {
      StringRenderOutput var9 = new StringRenderOutput(0.0F, 0.0F, var5, false, Font.DisplayMode.NORMAL, var8);

      for(int var10 = -1; var10 <= 1; ++var10) {
         for(int var11 = -1; var11 <= 1; ++var11) {
            if (var10 != 0 || var11 != 0) {
               float[] var12 = new float[]{var2};
               var1.accept((var7x, var8x, var9x) -> {
                  boolean var10x = var8x.isBold();
                  FontSet var11x = this.getFontSet(var8x.getFont());
                  GlyphInfo var12x = var11x.getGlyphInfo(var9x, this.filterFishyGlyphs);
                  var9.x = var12[0] + (float)var10 * var12x.getShadowOffset();
                  var9.y = var3 + (float)var11 * var12x.getShadowOffset();
                  var12[0] += var12x.getAdvance(var10x);
                  return var9.accept(var7x, var8x.withColor(var5), var9x);
               });
            }
         }
      }

      var9.renderCharacters(var7, var6);
      StringRenderOutput var15 = new StringRenderOutput(var2, var3, var4, false, Font.DisplayMode.POLYGON_OFFSET, var8);
      var1.accept(var15);
      var15.finish(var2, var7, var6);
   }

   private void drawInternal(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      this.renderText(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   private TextRenderState extractInternal(String var1, float var2, float var3, int var4, boolean var5, DisplayMode var6, int var7, int var8, boolean var9) {
      return this.extractText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private void drawInternal(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      this.renderText(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   private TextRenderState extractInternal(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, DisplayMode var6, int var7, int var8, boolean var9) {
      return this.extractText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private void renderText(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      StringRenderOutput var12 = new StringRenderOutput(var2, var3, var4, var9, var5, var8, var10, var11);
      StringDecomposer.iterateFormatted((String)var1, Style.EMPTY, var12);
      var12.finish(var2, var7, var6);
   }

   private TextRenderState extractText(String var1, float var2, float var3, int var4, boolean var5, DisplayMode var6, int var7, int var8, boolean var9) {
      StringRenderOutput var10 = new StringRenderOutput(var2, var3, var4, var7, var5, var6, var8, var9);
      StringDecomposer.iterateFormatted((String)var1, Style.EMPTY, var10);
      return var10.extract();
   }

   private void renderText(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10, boolean var11) {
      StringRenderOutput var12 = new StringRenderOutput(var2, var3, var4, var9, var5, var8, var10, var11);
      var1.accept(var12);
      var12.finish(var2, var7, var6);
   }

   private TextRenderState extractText(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, DisplayMode var6, int var7, int var8, boolean var9) {
      StringRenderOutput var10 = new StringRenderOutput(var2, var3, var4, var7, var5, var6, var8, var9);
      var1.accept(var10);
      return var10.extract();
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
      private final boolean drawShadow;
      private final int color;
      private final int backgroundColor;
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

      public StringRenderOutput(final float var2, final float var3, final int var4, final boolean var5, final DisplayMode var6, final int var7) {
         this(var2, var3, var4, 0, var5, var6, var7, true);
      }

      public StringRenderOutput(final float var2, final float var3, final int var4, final int var5, final boolean var6, final DisplayMode var7, final int var8, final boolean var9) {
         super();
         this.glyphInstances = new ArrayList();
         this.x = var2;
         this.y = var3;
         this.drawShadow = var6;
         this.color = var4;
         this.backgroundColor = var5;
         this.mode = var7;
         this.packedLightCoords = var8;
         this.inverseDepth = var9;
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
            this.addEffect(new BakedGlyph.Effect(var12, this.y + 4.5F - 1.0F, this.x + var11, this.y + 4.5F, this.getOverTextEffectDepth(), var9, var10, var13));
         }

         if (var2.isUnderlined()) {
            this.addEffect(new BakedGlyph.Effect(var12, this.y + 9.0F - 1.0F, this.x + var11, this.y + 9.0F, this.getOverTextEffectDepth(), var9, var10, var13));
         }

         this.x += var11;
         return true;
      }

      void finish(float var1, MultiBufferSource var2, Matrix4f var3) {
         BakedGlyph var4 = null;
         if (this.backgroundColor != 0) {
            BakedGlyph.Effect var5 = new BakedGlyph.Effect(var1 - 1.0F, this.y - 1.0F, this.x, this.y + 9.0F, this.getUnderTextEffectDepth(), this.backgroundColor);
            var4 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            VertexConsumer var6 = var2.getBuffer(var4.renderType(this.mode));
            var4.renderEffect(var5, var3, var6, this.packedLightCoords);
         }

         this.renderCharacters(var2, var3);
         if (this.effects != null) {
            if (var4 == null) {
               var4 = Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            }

            VertexConsumer var8 = var2.getBuffer(var4.renderType(this.mode));

            for(BakedGlyph.Effect var7 : this.effects) {
               var4.renderEffect(var7, var3, var8, this.packedLightCoords);
            }
         }

      }

      TextRenderState extract() {
         return new TextRenderState(this.drawShadow, this.color, this.backgroundColor, this.mode, this.packedLightCoords, this.glyphInstances, this.effects, 9, Font.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph(), this.x);
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

      void renderCharacters(MultiBufferSource var1, Matrix4f var2) {
         for(BakedGlyph.GlyphInstance var4 : this.glyphInstances) {
            BakedGlyph var5 = var4.glyph();
            VertexConsumer var6 = var1.getBuffer(var5.renderType(this.mode));
            var5.renderChar(var4, var2, var6, this.packedLightCoords);
         }

      }

      private float getOverTextEffectDepth() {
         return this.inverseDepth ? 0.001F : -0.001F;
      }

      private float getUnderTextEffectDepth() {
         return this.inverseDepth ? -0.001F : 0.001F;
      }
   }
}
