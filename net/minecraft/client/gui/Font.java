package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;

public class Font implements AutoCloseable {
   public final int lineHeight = 9;
   public final Random random = new Random();
   private final TextureManager textureManager;
   private final FontSet fonts;
   private boolean bidirectional;

   public Font(TextureManager var1, FontSet var2) {
      this.textureManager = var1;
      this.fonts = var2;
   }

   public void reload(List var1) {
      this.fonts.reload(var1);
   }

   public void close() {
      this.fonts.close();
   }

   public int drawShadow(String var1, float var2, float var3, int var4) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(var1, var2, var3, var4, Transformation.identity().getMatrix(), true);
   }

   public int draw(String var1, float var2, float var3, int var4) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(var1, var2, var3, var4, Transformation.identity().getMatrix(), false);
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

   private int drawInternal(String var1, float var2, float var3, int var4, Matrix4f var5, boolean var6) {
      if (var1 == null) {
         return 0;
      } else {
         MultiBufferSource.BufferSource var7 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         int var8 = this.drawInBatch(var1, var2, var3, var4, var6, var5, var7, false, 0, 15728880);
         var7.endBatch();
         return var8;
      }
   }

   public int drawInBatch(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10) {
      return this.drawInternal(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private int drawInternal(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10) {
      if (this.bidirectional) {
         var1 = this.bidirectionalShaping(var1);
      }

      if ((var4 & -67108864) == 0) {
         var4 |= -16777216;
      }

      if (var5) {
         this.renderText(var1, var2, var3, var4, true, var6, var7, var8, var9, var10);
      }

      Matrix4f var11 = var6.copy();
      var11.translate(new Vector3f(0.0F, 0.0F, 0.001F));
      var2 = this.renderText(var1, var2, var3, var4, false, var11, var7, var8, var9, var10);
      return (int)var2 + (var5 ? 1 : 0);
   }

   private float renderText(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, MultiBufferSource var7, boolean var8, int var9, int var10) {
      float var11 = var5 ? 0.25F : 1.0F;
      float var12 = (float)(var4 >> 16 & 255) / 255.0F * var11;
      float var13 = (float)(var4 >> 8 & 255) / 255.0F * var11;
      float var14 = (float)(var4 & 255) / 255.0F * var11;
      float var15 = var2;
      float var16 = var12;
      float var17 = var13;
      float var18 = var14;
      float var19 = (float)(var4 >> 24 & 255) / 255.0F;
      boolean var20 = false;
      boolean var21 = false;
      boolean var22 = false;
      boolean var23 = false;
      boolean var24 = false;
      ArrayList var25 = Lists.newArrayList();

      for(int var26 = 0; var26 < var1.length(); ++var26) {
         char var27 = var1.charAt(var26);
         if (var27 == 167 && var26 + 1 < var1.length()) {
            ChatFormatting var37 = ChatFormatting.getByCode(var1.charAt(var26 + 1));
            if (var37 != null) {
               if (var37.shouldReset()) {
                  var20 = false;
                  var21 = false;
                  var24 = false;
                  var23 = false;
                  var22 = false;
                  var16 = var12;
                  var17 = var13;
                  var18 = var14;
               }

               if (var37.getColor() != null) {
                  int var40 = var37.getColor();
                  var16 = (float)(var40 >> 16 & 255) / 255.0F * var11;
                  var17 = (float)(var40 >> 8 & 255) / 255.0F * var11;
                  var18 = (float)(var40 & 255) / 255.0F * var11;
               } else if (var37 == ChatFormatting.OBFUSCATED) {
                  var20 = true;
               } else if (var37 == ChatFormatting.BOLD) {
                  var21 = true;
               } else if (var37 == ChatFormatting.STRIKETHROUGH) {
                  var24 = true;
               } else if (var37 == ChatFormatting.UNDERLINE) {
                  var23 = true;
               } else if (var37 == ChatFormatting.ITALIC) {
                  var22 = true;
               }
            }

            ++var26;
         } else {
            GlyphInfo var28 = this.fonts.getGlyphInfo(var27);
            BakedGlyph var29 = var20 && var27 != ' ' ? this.fonts.getRandomGlyph(var28) : this.fonts.getGlyph(var27);
            float var30;
            float var31;
            if (!(var29 instanceof EmptyGlyph)) {
               var30 = var21 ? var28.getBoldOffset() : 0.0F;
               var31 = var5 ? var28.getShadowOffset() : 0.0F;
               VertexConsumer var32 = var7.getBuffer(var29.renderType(var8));
               this.renderChar(var29, var21, var22, var30, var15 + var31, var3 + var31, var6, var32, var16, var17, var18, var19, var10);
            }

            var30 = var28.getAdvance(var21);
            var31 = var5 ? 1.0F : 0.0F;
            if (var24) {
               var25.add(new BakedGlyph.Effect(var15 + var31 - 1.0F, var3 + var31 + 4.5F, var15 + var31 + var30, var3 + var31 + 4.5F - 1.0F, -0.01F, var16, var17, var18, var19));
            }

            if (var23) {
               var25.add(new BakedGlyph.Effect(var15 + var31 - 1.0F, var3 + var31 + 9.0F, var15 + var31 + var30, var3 + var31 + 9.0F - 1.0F, -0.01F, var16, var17, var18, var19));
            }

            var15 += var30;
         }
      }

      if (var9 != 0) {
         float var33 = (float)(var9 >> 24 & 255) / 255.0F;
         float var34 = (float)(var9 >> 16 & 255) / 255.0F;
         float var38 = (float)(var9 >> 8 & 255) / 255.0F;
         float var41 = (float)(var9 & 255) / 255.0F;
         var25.add(new BakedGlyph.Effect(var2 - 1.0F, var3 + 9.0F, var15 + 1.0F, var3 - 1.0F, 0.01F, var34, var38, var41, var33));
      }

      if (!var25.isEmpty()) {
         BakedGlyph var35 = this.fonts.whiteGlyph();
         VertexConsumer var36 = var7.getBuffer(var35.renderType(var8));
         Iterator var39 = var25.iterator();

         while(var39.hasNext()) {
            BakedGlyph.Effect var42 = (BakedGlyph.Effect)var39.next();
            var35.renderEffect(var42, var6, var36, var10);
         }
      }

      return var15;
   }

   private void renderChar(BakedGlyph var1, boolean var2, boolean var3, float var4, float var5, float var6, Matrix4f var7, VertexConsumer var8, float var9, float var10, float var11, float var12, int var13) {
      var1.render(var3, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      if (var2) {
         var1.render(var3, var5 + var4, var6, var7, var8, var9, var10, var11, var12, var13);
      }

   }

   public int width(String var1) {
      if (var1 == null) {
         return 0;
      } else {
         float var2 = 0.0F;
         boolean var3 = false;

         for(int var4 = 0; var4 < var1.length(); ++var4) {
            char var5 = var1.charAt(var4);
            if (var5 == 167 && var4 < var1.length() - 1) {
               ++var4;
               ChatFormatting var6 = ChatFormatting.getByCode(var1.charAt(var4));
               if (var6 == ChatFormatting.BOLD) {
                  var3 = true;
               } else if (var6 != null && var6.shouldReset()) {
                  var3 = false;
               }
            } else {
               var2 += this.fonts.getGlyphInfo(var5).getAdvance(var3);
            }
         }

         return Mth.ceil(var2);
      }
   }

   public float charWidth(char var1) {
      return var1 == 167 ? 0.0F : this.fonts.getGlyphInfo(var1).getAdvance(false);
   }

   public String substrByWidth(String var1, int var2) {
      return this.substrByWidth(var1, var2, false);
   }

   public String substrByWidth(String var1, int var2, boolean var3) {
      StringBuilder var4 = new StringBuilder();
      float var5 = 0.0F;
      int var6 = var3 ? var1.length() - 1 : 0;
      int var7 = var3 ? -1 : 1;
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var6; var10 >= 0 && var10 < var1.length() && var5 < (float)var2; var10 += var7) {
         char var11 = var1.charAt(var10);
         if (var8) {
            var8 = false;
            ChatFormatting var12 = ChatFormatting.getByCode(var11);
            if (var12 == ChatFormatting.BOLD) {
               var9 = true;
            } else if (var12 != null && var12.shouldReset()) {
               var9 = false;
            }
         } else if (var11 == 167) {
            var8 = true;
         } else {
            var5 += this.charWidth(var11);
            if (var9) {
               ++var5;
            }
         }

         if (var5 > (float)var2) {
            break;
         }

         if (var3) {
            var4.insert(0, var11);
         } else {
            var4.append(var11);
         }
      }

      return var4.toString();
   }

   private String eraseTrailingNewLines(String var1) {
      while(var1 != null && var1.endsWith("\n")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1;
   }

   public void drawWordWrap(String var1, int var2, int var3, int var4, int var5) {
      var1 = this.eraseTrailingNewLines(var1);
      this.drawWordWrapInternal(var1, var2, var3, var4, var5);
   }

   private void drawWordWrapInternal(String var1, int var2, int var3, int var4, int var5) {
      List var6 = this.split(var1, var4);
      Matrix4f var7 = Transformation.identity().getMatrix();

      for(Iterator var8 = var6.iterator(); var8.hasNext(); var3 += 9) {
         String var9 = (String)var8.next();
         float var10 = (float)var2;
         if (this.bidirectional) {
            int var11 = this.width(this.bidirectionalShaping(var9));
            var10 += (float)(var4 - var11);
         }

         this.drawInternal(var9, var10, (float)var3, var5, var7, false);
      }

   }

   public int wordWrapHeight(String var1, int var2) {
      return 9 * this.split(var1, var2).size();
   }

   public void setBidirectional(boolean var1) {
      this.bidirectional = var1;
   }

   public List split(String var1, int var2) {
      return Arrays.asList(this.insertLineBreaks(var1, var2).split("\n"));
   }

   public String insertLineBreaks(String var1, int var2) {
      String var3;
      String var5;
      for(var3 = ""; !var1.isEmpty(); var3 = var3 + var5 + "\n") {
         int var4 = this.indexAtWidth(var1, var2);
         if (var1.length() <= var4) {
            return var3 + var1;
         }

         var5 = var1.substring(0, var4);
         char var6 = var1.charAt(var4);
         boolean var7 = var6 == ' ' || var6 == '\n';
         var1 = ChatFormatting.getLastColors(var5) + var1.substring(var4 + (var7 ? 1 : 0));
      }

      return var3;
   }

   public int indexAtWidth(String var1, int var2) {
      int var3 = Math.max(1, var2);
      int var4 = var1.length();
      float var5 = 0.0F;
      int var6 = 0;
      int var7 = -1;
      boolean var8 = false;

      for(boolean var9 = true; var6 < var4; ++var6) {
         char var10 = var1.charAt(var6);
         switch(var10) {
         case '\n':
            --var6;
            break;
         case ' ':
            var7 = var6;
         default:
            if (var5 != 0.0F) {
               var9 = false;
            }

            var5 += this.charWidth(var10);
            if (var8) {
               ++var5;
            }
            break;
         case '§':
            if (var6 < var4 - 1) {
               ++var6;
               ChatFormatting var11 = ChatFormatting.getByCode(var1.charAt(var6));
               if (var11 == ChatFormatting.BOLD) {
                  var8 = true;
               } else if (var11 != null && var11.shouldReset()) {
                  var8 = false;
               }
            }
         }

         if (var10 == '\n') {
            ++var6;
            var7 = var6;
            break;
         }

         if (var5 > (float)var3) {
            if (var9) {
               ++var6;
            }
            break;
         }
      }

      return var6 != var4 && var7 != -1 && var7 < var6 ? var7 : var6;
   }

   public int getWordPosition(String var1, int var2, int var3, boolean var4) {
      int var5 = var3;
      boolean var6 = var2 < 0;
      int var7 = Math.abs(var2);

      for(int var8 = 0; var8 < var7; ++var8) {
         if (var6) {
            while(var4 && var5 > 0 && (var1.charAt(var5 - 1) == ' ' || var1.charAt(var5 - 1) == '\n')) {
               --var5;
            }

            while(var5 > 0 && var1.charAt(var5 - 1) != ' ' && var1.charAt(var5 - 1) != '\n') {
               --var5;
            }
         } else {
            int var9 = var1.length();
            int var10 = var1.indexOf(32, var5);
            int var11 = var1.indexOf(10, var5);
            if (var10 == -1 && var11 == -1) {
               var5 = -1;
            } else if (var10 != -1 && var11 != -1) {
               var5 = Math.min(var10, var11);
            } else if (var10 != -1) {
               var5 = var10;
            } else {
               var5 = var11;
            }

            if (var5 == -1) {
               var5 = var9;
            } else {
               while(var4 && var5 < var9 && (var1.charAt(var5) == ' ' || var1.charAt(var5) == '\n')) {
                  ++var5;
               }
            }
         }
      }

      return var5;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }
}
