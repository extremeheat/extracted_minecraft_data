package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Font implements AutoCloseable {
   public final int lineHeight = 9;
   public final Random random = new Random();
   private final TextureManager textureManager;
   private final FontSet fonts;
   private boolean bidirectional;

   public Font(TextureManager var1, FontSet var2) {
      super();
      this.textureManager = var1;
      this.fonts = var2;
   }

   public void reload(List<GlyphProvider> var1) {
      this.fonts.reload(var1);
   }

   public void close() {
      this.fonts.close();
   }

   public int drawShadow(String var1, float var2, float var3, int var4) {
      GlStateManager.enableAlphaTest();
      return this.drawInternal(var1, var2, var3, var4, true);
   }

   public int draw(String var1, float var2, float var3, int var4) {
      GlStateManager.enableAlphaTest();
      return this.drawInternal(var1, var2, var3, var4, false);
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

   private int drawInternal(String var1, float var2, float var3, int var4, boolean var5) {
      if (var1 == null) {
         return 0;
      } else {
         if (this.bidirectional) {
            var1 = this.bidirectionalShaping(var1);
         }

         if ((var4 & -67108864) == 0) {
            var4 |= -16777216;
         }

         if (var5) {
            this.renderText(var1, var2, var3, var4, true);
         }

         var2 = this.renderText(var1, var2, var3, var4, false);
         return (int)var2 + (var5 ? 1 : 0);
      }
   }

   private float renderText(String var1, float var2, float var3, int var4, boolean var5) {
      float var6 = var5 ? 0.25F : 1.0F;
      float var7 = (float)(var4 >> 16 & 255) / 255.0F * var6;
      float var8 = (float)(var4 >> 8 & 255) / 255.0F * var6;
      float var9 = (float)(var4 & 255) / 255.0F * var6;
      float var10 = var7;
      float var11 = var8;
      float var12 = var9;
      float var13 = (float)(var4 >> 24 & 255) / 255.0F;
      Tesselator var14 = Tesselator.getInstance();
      BufferBuilder var15 = var14.getBuilder();
      ResourceLocation var16 = null;
      var15.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      boolean var17 = false;
      boolean var18 = false;
      boolean var19 = false;
      boolean var20 = false;
      boolean var21 = false;
      ArrayList var22 = Lists.newArrayList();

      for(int var23 = 0; var23 < var1.length(); ++var23) {
         char var24 = var1.charAt(var23);
         if (var24 == 167 && var23 + 1 < var1.length()) {
            ChatFormatting var32 = ChatFormatting.getByCode(var1.charAt(var23 + 1));
            if (var32 != null) {
               if (var32.shouldReset()) {
                  var17 = false;
                  var18 = false;
                  var21 = false;
                  var20 = false;
                  var19 = false;
                  var10 = var7;
                  var11 = var8;
                  var12 = var9;
               }

               if (var32.getColor() != null) {
                  int var33 = var32.getColor();
                  var10 = (float)(var33 >> 16 & 255) / 255.0F * var6;
                  var11 = (float)(var33 >> 8 & 255) / 255.0F * var6;
                  var12 = (float)(var33 & 255) / 255.0F * var6;
               } else if (var32 == ChatFormatting.OBFUSCATED) {
                  var17 = true;
               } else if (var32 == ChatFormatting.BOLD) {
                  var18 = true;
               } else if (var32 == ChatFormatting.STRIKETHROUGH) {
                  var21 = true;
               } else if (var32 == ChatFormatting.UNDERLINE) {
                  var20 = true;
               } else if (var32 == ChatFormatting.ITALIC) {
                  var19 = true;
               }
            }

            ++var23;
         } else {
            GlyphInfo var25 = this.fonts.getGlyphInfo(var24);
            BakedGlyph var26 = var17 && var24 != ' ' ? this.fonts.getRandomGlyph(var25) : this.fonts.getGlyph(var24);
            ResourceLocation var27 = var26.getTexture();
            float var28;
            float var29;
            if (var27 != null) {
               if (var16 != var27) {
                  var14.end();
                  this.textureManager.bind(var27);
                  var15.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                  var16 = var27;
               }

               var28 = var18 ? var25.getBoldOffset() : 0.0F;
               var29 = var5 ? var25.getShadowOffset() : 0.0F;
               this.renderChar(var26, var18, var19, var28, var2 + var29, var3 + var29, var15, var10, var11, var12, var13);
            }

            var28 = var25.getAdvance(var18);
            var29 = var5 ? 1.0F : 0.0F;
            if (var21) {
               var22.add(new Font.Effect(var2 + var29 - 1.0F, var3 + var29 + 4.5F, var2 + var29 + var28, var3 + var29 + 4.5F - 1.0F, var10, var11, var12, var13));
            }

            if (var20) {
               var22.add(new Font.Effect(var2 + var29 - 1.0F, var3 + var29 + 9.0F, var2 + var29 + var28, var3 + var29 + 9.0F - 1.0F, var10, var11, var12, var13));
            }

            var2 += var28;
         }
      }

      var14.end();
      if (!var22.isEmpty()) {
         GlStateManager.disableTexture();
         var15.begin(7, DefaultVertexFormat.POSITION_COLOR);
         Iterator var30 = var22.iterator();

         while(var30.hasNext()) {
            Font.Effect var31 = (Font.Effect)var30.next();
            var31.render(var15);
         }

         var14.end();
         GlStateManager.enableTexture();
      }

      return var2;
   }

   private void renderChar(BakedGlyph var1, boolean var2, boolean var3, float var4, float var5, float var6, BufferBuilder var7, float var8, float var9, float var10, float var11) {
      var1.render(this.textureManager, var3, var5, var6, var7, var8, var9, var10, var11);
      if (var2) {
         var1.render(this.textureManager, var3, var5 + var4, var6, var7, var8, var9, var10, var11);
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

      for(Iterator var7 = var6.iterator(); var7.hasNext(); var3 += 9) {
         String var8 = (String)var7.next();
         float var9 = (float)var2;
         if (this.bidirectional) {
            int var10 = this.width(this.bidirectionalShaping(var8));
            var9 += (float)(var4 - var10);
         }

         this.drawInternal(var8, var9, (float)var3, var5, false);
      }

   }

   public int wordWrapHeight(String var1, int var2) {
      return 9 * this.split(var1, var2).size();
   }

   public void setBidirectional(boolean var1) {
      this.bidirectional = var1;
   }

   public List<String> split(String var1, int var2) {
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
         case '\u00a7':
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

   static class Effect {
      protected final float x0;
      protected final float y0;
      protected final float x1;
      protected final float y1;
      protected final float r;
      protected final float g;
      protected final float b;
      protected final float a;

      private Effect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         super();
         this.x0 = var1;
         this.y0 = var2;
         this.x1 = var3;
         this.y1 = var4;
         this.r = var5;
         this.g = var6;
         this.b = var7;
         this.a = var8;
      }

      public void render(BufferBuilder var1) {
         var1.vertex((double)this.x0, (double)this.y0, 0.0D).color(this.r, this.g, this.b, this.a).endVertex();
         var1.vertex((double)this.x1, (double)this.y0, 0.0D).color(this.r, this.g, this.b, this.a).endVertex();
         var1.vertex((double)this.x1, (double)this.y1, 0.0D).color(this.r, this.g, this.b, this.a).endVertex();
         var1.vertex((double)this.x0, (double)this.y1, 0.0D).color(this.r, this.g, this.b, this.a).endVertex();
      }

      // $FF: synthetic method
      Effect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, Object var9) {
         this(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
