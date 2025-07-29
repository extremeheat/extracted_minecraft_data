package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.glyphs.BakeableGlyph;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
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
   private static final float OVER_EFFECT_DEPTH = 0.01F;
   private static final float UNDER_EFFECT_DEPTH = -0.01F;
   public static final float SHADOW_DEPTH = 0.03F;
   public static final int NO_SHADOW = 0;
   public final int lineHeight = 9;
   private final RandomSource random = RandomSource.create();
   final Provider provider;
   private final StringSplitter splitter;

   public Font(Provider var1) {
      super();
      this.provider = var1;
      this.splitter = new StringSplitter((var1x, var2) -> this.getGlyphSource(var2.getFont()).getGlyph(var1x).info().getAdvance(var2.isBold()));
   }

   private GlyphSource getGlyphSource(ResourceLocation var1) {
      return this.provider.glyphs(var1);
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
      PreparedText var11 = this.prepareText(var1, var2, var3, var4, var5, var9);
      var11.visit(Font.GlyphVisitor.forMultiBufferSource(var7, var6, var8, var10));
   }

   public void drawInBatch(Component var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      PreparedText var11 = this.prepareText(var1.getVisualOrderText(), var2, var3, var4, var5, var9);
      var11.visit(Font.GlyphVisitor.forMultiBufferSource(var7, var6, var8, var10));
   }

   public void drawInBatch(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, DisplayMode var8, int var9, int var10) {
      PreparedText var11 = this.prepareText(var1, var2, var3, var4, var5, var9);
      var11.visit(Font.GlyphVisitor.forMultiBufferSource(var7, var6, var8, var10));
   }

   public void drawInBatch8xOutline(FormattedCharSequence var1, float var2, float var3, int var4, int var5, Matrix4f var6, MultiBufferSource var7, int var8) {
      PreparedTextBuilder var9 = new PreparedTextBuilder(0.0F, 0.0F, var5, false);

      for(int var10 = -1; var10 <= 1; ++var10) {
         for(int var11 = -1; var11 <= 1; ++var11) {
            if (var10 != 0 || var11 != 0) {
               float[] var12 = new float[]{var2};
               var1.accept((var7x, var8x, var9x) -> {
                  boolean var10x = var8x.isBold();
                  BakeableGlyph var11x = this.getGlyph(var9x, var8x);
                  var9.x = var12[0] + (float)var10 * var11x.info().getShadowOffset();
                  var9.y = var3 + (float)var11 * var11x.info().getShadowOffset();
                  var12[0] += var11x.info().getAdvance(var10x);
                  return var9.accept(var7x, var8x.withColor(var5), var11x);
               });
            }
         }
      }

      GlyphVisitor var15 = Font.GlyphVisitor.forMultiBufferSource(var7, var6, Font.DisplayMode.NORMAL, var8);

      for(BakedGlyph.GlyphInstance var18 : var9.glyphs) {
         var15.acceptGlyph(var18);
      }

      PreparedTextBuilder var17 = new PreparedTextBuilder(var2, var3, var4, false);
      var1.accept(var17);
      var17.visit(Font.GlyphVisitor.forMultiBufferSource(var7, var6, Font.DisplayMode.POLYGON_OFFSET, var8));
   }

   BakeableGlyph getGlyph(int var1, Style var2) {
      GlyphSource var3 = this.getGlyphSource(var2.getFont());
      BakeableGlyph var4 = var3.getGlyph(var1);
      if (var2.isObfuscated() && var1 != 32) {
         int var5 = Mth.ceil(var4.info().getAdvance(false));
         var4 = var3.getRandomGlyph(this.random, var5);
      }

      return var4;
   }

   public PreparedText prepareText(String var1, float var2, float var3, int var4, boolean var5, int var6) {
      if (this.isBidirectional()) {
         var1 = this.bidirectionalShaping(var1);
      }

      PreparedTextBuilder var7 = new PreparedTextBuilder(var2, var3, var4, var6, var5);
      StringDecomposer.iterateFormatted((String)var1, Style.EMPTY, var7);
      return var7;
   }

   public PreparedText prepareText(FormattedCharSequence var1, float var2, float var3, int var4, boolean var5, int var6) {
      PreparedTextBuilder var7 = new PreparedTextBuilder(var2, var3, var4, var6, var5);
      var1.accept(var7);
      return var7;
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

   public List<FormattedText> splitIgnoringLanguage(FormattedText var1, int var2) {
      return this.splitter.splitLines(var1, var2, Style.EMPTY);
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

   class PreparedTextBuilder implements FormattedCharSink, PreparedText {
      private final boolean drawShadow;
      private final int color;
      private final int backgroundColor;
      float x;
      float y;
      private float left;
      private float top;
      private float right;
      private float bottom;
      private float backgroundLeft;
      private float backgroundTop;
      private float backgroundRight;
      private float backgroundBottom;
      final List<BakedGlyph.GlyphInstance> glyphs;
      @Nullable
      private List<BakedGlyph.Effect> effects;

      public PreparedTextBuilder(final float var2, final float var3, final int var4, final boolean var5) {
         this(var2, var3, var4, 0, var5);
      }

      public PreparedTextBuilder(final float var2, final float var3, final int var4, final int var5, final boolean var6) {
         super();
         this.left = 3.4028235E38F;
         this.top = 3.4028235E38F;
         this.right = -3.4028235E38F;
         this.bottom = -3.4028235E38F;
         this.backgroundLeft = 3.4028235E38F;
         this.backgroundTop = 3.4028235E38F;
         this.backgroundRight = -3.4028235E38F;
         this.backgroundBottom = -3.4028235E38F;
         this.glyphs = new ArrayList();
         this.x = var2;
         this.y = var3;
         this.drawShadow = var6;
         this.color = var4;
         this.backgroundColor = var5;
         this.markBackground(var2, var3, 0.0F);
      }

      private void markSize(float var1, float var2, float var3, float var4) {
         this.left = Math.min(this.left, var1);
         this.top = Math.min(this.top, var2);
         this.right = Math.max(this.right, var3);
         this.bottom = Math.max(this.bottom, var4);
      }

      private void markBackground(float var1, float var2, float var3) {
         if (ARGB.alpha(this.backgroundColor) != 0) {
            this.backgroundLeft = Math.min(this.backgroundLeft, var1 - 1.0F);
            this.backgroundTop = Math.min(this.backgroundTop, var2 - 1.0F);
            this.backgroundRight = Math.max(this.backgroundRight, var1 + var3);
            this.backgroundBottom = Math.max(this.backgroundBottom, var2 + 9.0F);
            this.markSize(this.backgroundLeft, this.backgroundTop, this.backgroundRight, this.backgroundBottom);
         }
      }

      private void addGlyph(BakedGlyph.GlyphInstance var1) {
         this.glyphs.add(var1);
         this.markSize(var1.left(), var1.top(), var1.right(), var1.bottom());
      }

      private void addEffect(BakedGlyph.Effect var1) {
         if (this.effects == null) {
            this.effects = new ArrayList();
         }

         this.effects.add(var1);
         this.markSize(var1.left(), var1.top(), var1.right(), var1.bottom());
      }

      public boolean accept(int var1, Style var2, int var3) {
         BakeableGlyph var4 = Font.this.getGlyph(var3, var2);
         return this.accept(var1, var2, var4);
      }

      public boolean accept(int var1, Style var2, BakeableGlyph var3) {
         GlyphInfo var4 = var3.info();
         BakedGlyph var5 = var3.baked();
         boolean var6 = var2.isBold();
         TextColor var7 = var2.getColor();
         int var8 = this.getTextColor(var7);
         int var9 = this.getShadowColor(var2, var8);
         float var10 = var4.getAdvance(var6);
         float var11 = var1 == 0 ? this.x - 1.0F : this.x;
         float var12 = var4.getShadowOffset();
         if (!(var5 instanceof EmptyGlyph)) {
            float var13 = var6 ? var4.getBoldOffset() : 0.0F;
            this.addGlyph(new BakedGlyph.GlyphInstance(this.x, this.y, var8, var9, var5, var2, var13, var12));
         }

         this.markBackground(this.x, this.y, var10);
         if (var2.isStrikethrough()) {
            this.addEffect(new BakedGlyph.Effect(var11, this.y + 4.5F - 1.0F, this.x + var10, this.y + 4.5F, 0.01F, var8, var9, var12));
         }

         if (var2.isUnderlined()) {
            this.addEffect(new BakedGlyph.Effect(var11, this.y + 9.0F - 1.0F, this.x + var10, this.y + 9.0F, 0.01F, var8, var9, var12));
         }

         this.x += var10;
         return true;
      }

      public void visit(GlyphVisitor var1) {
         BakedGlyph var2 = Font.this.provider.whiteGlyph().baked();
         if (ARGB.alpha(this.backgroundColor) != 0) {
            BakedGlyph.Effect var3 = new BakedGlyph.Effect(this.backgroundLeft, this.backgroundTop, this.backgroundRight, this.backgroundBottom, -0.01F, this.backgroundColor);
            var1.acceptEffect(var2, var3);
         }

         for(BakedGlyph.GlyphInstance var4 : this.glyphs) {
            var1.acceptGlyph(var4);
         }

         if (this.effects != null) {
            for(BakedGlyph.Effect var7 : this.effects) {
               var1.acceptEffect(var2, var7);
            }
         }

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

      @Nullable
      public ScreenRectangle bounds() {
         if (!(this.left >= this.right) && !(this.top >= this.bottom)) {
            int var1 = Mth.floor(this.left);
            int var2 = Mth.floor(this.top);
            int var3 = Mth.ceil(this.right);
            int var4 = Mth.ceil(this.bottom);
            return new ScreenRectangle(var1, var2, var3 - var1, var4 - var2);
         } else {
            return null;
         }
      }
   }

   public interface GlyphVisitor {
      static GlyphVisitor forMultiBufferSource(final MultiBufferSource var0, final Matrix4f var1, final DisplayMode var2, final int var3) {
         return new GlyphVisitor() {
            public void acceptGlyph(BakedGlyph.GlyphInstance var1x) {
               BakedGlyph var2x = var1x.glyph();
               VertexConsumer var3x = var0.getBuffer(var2x.renderType(var2));
               var2x.renderChar(var1x, var1, var3x, var3, false);
            }

            public void acceptEffect(BakedGlyph var1x, BakedGlyph.Effect var2x) {
               VertexConsumer var3x = var0.getBuffer(var1x.renderType(var2));
               var1x.renderEffect(var2x, var1, var3x, var3, false);
            }
         };
      }

      void acceptGlyph(BakedGlyph.GlyphInstance var1);

      void acceptEffect(BakedGlyph var1, BakedGlyph.Effect var2);
   }

   public interface PreparedText {
      void visit(GlyphVisitor var1);

      @Nullable
      ScreenRectangle bounds();
   }

   public interface Provider {
      GlyphSource glyphs(ResourceLocation var1);

      BakeableGlyph whiteGlyph();
   }
}
