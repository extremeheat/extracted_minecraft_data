package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FontRenderer implements AutoCloseable {
   private static final Logger field_195437_c = LogManager.getLogger();
   public int field_78288_b = 9;
   public Random field_78289_c = new Random();
   private final TextureManager field_78298_i;
   private final Font field_211127_e;
   private boolean field_78294_m;

   public FontRenderer(TextureManager var1, Font var2) {
      super();
      this.field_78298_i = var1;
      this.field_211127_e = var2;
   }

   public void func_211568_a(List<IGlyphProvider> var1) {
      this.field_211127_e.func_211570_a(var1);
   }

   public void close() {
      this.field_211127_e.close();
   }

   public int func_175063_a(String var1, float var2, float var3, int var4) {
      GlStateManager.func_179141_d();
      return this.func_180455_b(var1, var2, var3, var4, true);
   }

   public int func_211126_b(String var1, float var2, float var3, int var4) {
      GlStateManager.func_179141_d();
      return this.func_180455_b(var1, var2, var3, var4, false);
   }

   private String func_147647_b(String var1) {
      try {
         Bidi var2 = new Bidi((new ArabicShaping(8)).shape(var1), 127);
         var2.setReorderingMode(0);
         return var2.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return var1;
      }
   }

   private int func_180455_b(String var1, float var2, float var3, int var4, boolean var5) {
      if (var1 == null) {
         return 0;
      } else {
         if (this.field_78294_m) {
            var1 = this.func_147647_b(var1);
         }

         if ((var4 & -67108864) == 0) {
            var4 |= -16777216;
         }

         if (var5) {
            this.func_211843_b(var1, var2, var3, var4, true);
         }

         var2 = this.func_211843_b(var1, var2, var3, var4, false);
         return (int)var2 + (var5 ? 1 : 0);
      }
   }

   private float func_211843_b(String var1, float var2, float var3, int var4, boolean var5) {
      float var6 = var5 ? 0.25F : 1.0F;
      float var7 = (float)(var4 >> 16 & 255) / 255.0F * var6;
      float var8 = (float)(var4 >> 8 & 255) / 255.0F * var6;
      float var9 = (float)(var4 & 255) / 255.0F * var6;
      float var10 = var7;
      float var11 = var8;
      float var12 = var9;
      float var13 = (float)(var4 >> 24 & 255) / 255.0F;
      Tessellator var14 = Tessellator.func_178181_a();
      BufferBuilder var15 = var14.func_178180_c();
      ResourceLocation var16 = null;
      var15.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      boolean var17 = false;
      boolean var18 = false;
      boolean var19 = false;
      boolean var20 = false;
      boolean var21 = false;
      ArrayList var22 = Lists.newArrayList();

      for(int var23 = 0; var23 < var1.length(); ++var23) {
         char var24 = var1.charAt(var23);
         if (var24 == 167 && var23 + 1 < var1.length()) {
            TextFormatting var32 = TextFormatting.func_211165_a(var1.charAt(var23 + 1));
            if (var32 != null) {
               if (var32.func_211166_f()) {
                  var17 = false;
                  var18 = false;
                  var21 = false;
                  var20 = false;
                  var19 = false;
                  var10 = var7;
                  var11 = var8;
                  var12 = var9;
               }

               if (var32.func_211163_e() != null) {
                  int var33 = var32.func_211163_e();
                  var10 = (float)(var33 >> 16 & 255) / 255.0F * var6;
                  var11 = (float)(var33 >> 8 & 255) / 255.0F * var6;
                  var12 = (float)(var33 & 255) / 255.0F * var6;
               } else if (var32 == TextFormatting.OBFUSCATED) {
                  var17 = true;
               } else if (var32 == TextFormatting.BOLD) {
                  var18 = true;
               } else if (var32 == TextFormatting.STRIKETHROUGH) {
                  var21 = true;
               } else if (var32 == TextFormatting.UNDERLINE) {
                  var20 = true;
               } else if (var32 == TextFormatting.ITALIC) {
                  var19 = true;
               }
            }

            ++var23;
         } else {
            IGlyph var25 = this.field_211127_e.func_211184_b(var24);
            TexturedGlyph var26 = var17 && var24 != ' ' ? this.field_211127_e.func_211188_a(var25) : this.field_211127_e.func_211187_a(var24);
            ResourceLocation var27 = var26.func_211233_b();
            float var28;
            float var29;
            if (var27 != null) {
               if (var16 != var27) {
                  var14.func_78381_a();
                  this.field_78298_i.func_110577_a(var27);
                  var15.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                  var16 = var27;
               }

               var28 = var18 ? var25.getBoldOffset() : 0.0F;
               var29 = var5 ? var25.getShadowOffset() : 0.0F;
               this.func_212452_a(var26, var18, var19, var28, var2 + var29, var3 + var29, var15, var10, var11, var12, var13);
            }

            var28 = var25.getAdvance(var18);
            var29 = var5 ? 1.0F : 0.0F;
            if (var21) {
               var22.add(new FontRenderer.Entry(var2 + var29 - 1.0F, var3 + var29 + (float)this.field_78288_b / 2.0F, var2 + var29 + var28, var3 + var29 + (float)this.field_78288_b / 2.0F - 1.0F, var10, var11, var12, var13));
            }

            if (var20) {
               var22.add(new FontRenderer.Entry(var2 + var29 - 1.0F, var3 + var29 + (float)this.field_78288_b, var2 + var29 + var28, var3 + var29 + (float)this.field_78288_b - 1.0F, var10, var11, var12, var13));
            }

            var2 += var28;
         }
      }

      var14.func_78381_a();
      if (!var22.isEmpty()) {
         GlStateManager.func_179090_x();
         var15.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         Iterator var30 = var22.iterator();

         while(var30.hasNext()) {
            FontRenderer.Entry var31 = (FontRenderer.Entry)var30.next();
            var31.func_211168_a(var15);
         }

         var14.func_78381_a();
         GlStateManager.func_179098_w();
      }

      return var2;
   }

   private void func_212452_a(TexturedGlyph var1, boolean var2, boolean var3, float var4, float var5, float var6, BufferBuilder var7, float var8, float var9, float var10, float var11) {
      var1.func_211234_a(this.field_78298_i, var3, var5, var6, var7, var8, var9, var10, var11);
      if (var2) {
         var1.func_211234_a(this.field_78298_i, var3, var5 + var4, var6, var7, var8, var9, var10, var11);
      }

   }

   public int func_78256_a(String var1) {
      if (var1 == null) {
         return 0;
      } else {
         float var2 = 0.0F;
         boolean var3 = false;

         for(int var4 = 0; var4 < var1.length(); ++var4) {
            char var5 = var1.charAt(var4);
            if (var5 == 167 && var4 < var1.length() - 1) {
               ++var4;
               TextFormatting var6 = TextFormatting.func_211165_a(var1.charAt(var4));
               if (var6 == TextFormatting.BOLD) {
                  var3 = true;
               } else if (var6 != null && var6.func_211166_f()) {
                  var3 = false;
               }
            } else {
               var2 += this.field_211127_e.func_211184_b(var5).getAdvance(var3);
            }
         }

         return MathHelper.func_76123_f(var2);
      }
   }

   private float func_211125_a(char var1) {
      return var1 == 167 ? 0.0F : (float)MathHelper.func_76123_f(this.field_211127_e.func_211184_b(var1).getAdvance(false));
   }

   public String func_78269_a(String var1, int var2) {
      return this.func_78262_a(var1, var2, false);
   }

   public String func_78262_a(String var1, int var2, boolean var3) {
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
            TextFormatting var12 = TextFormatting.func_211165_a(var11);
            if (var12 == TextFormatting.BOLD) {
               var9 = true;
            } else if (var12 != null && var12.func_211166_f()) {
               var9 = false;
            }
         } else if (var11 == 167) {
            var8 = true;
         } else {
            var5 += this.func_211125_a(var11);
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

   private String func_78273_d(String var1) {
      while(var1 != null && var1.endsWith("\n")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      return var1;
   }

   public void func_78279_b(String var1, int var2, int var3, int var4, int var5) {
      var1 = this.func_78273_d(var1);
      this.func_211124_b(var1, var2, var3, var4, var5);
   }

   private void func_211124_b(String var1, int var2, int var3, int var4, int var5) {
      List var6 = this.func_78271_c(var1, var4);

      for(Iterator var7 = var6.iterator(); var7.hasNext(); var3 += this.field_78288_b) {
         String var8 = (String)var7.next();
         float var9 = (float)var2;
         if (this.field_78294_m) {
            int var10 = this.func_78256_a(this.func_147647_b(var8));
            var9 += (float)(var4 - var10);
         }

         this.func_180455_b(var8, var9, (float)var3, var5, false);
      }

   }

   public int func_78267_b(String var1, int var2) {
      return this.field_78288_b * this.func_78271_c(var1, var2).size();
   }

   public void func_78275_b(boolean var1) {
      this.field_78294_m = var1;
   }

   public List<String> func_78271_c(String var1, int var2) {
      return Arrays.asList(this.func_78280_d(var1, var2).split("\n"));
   }

   public String func_78280_d(String var1, int var2) {
      String var3;
      String var5;
      for(var3 = ""; !var1.isEmpty(); var3 = var3 + var5 + "\n") {
         int var4 = this.func_78259_e(var1, var2);
         if (var1.length() <= var4) {
            return var3 + var1;
         }

         var5 = var1.substring(0, var4);
         char var6 = var1.charAt(var4);
         boolean var7 = var6 == ' ' || var6 == '\n';
         var1 = TextFormatting.func_211164_a(var5) + var1.substring(var4 + (var7 ? 1 : 0));
      }

      return var3;
   }

   private int func_78259_e(String var1, int var2) {
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

            var5 += this.func_211125_a(var10);
            if (var8) {
               ++var5;
            }
            break;
         case '\u00a7':
            if (var6 < var4 - 1) {
               ++var6;
               TextFormatting var11 = TextFormatting.func_211165_a(var1.charAt(var6));
               if (var11 == TextFormatting.BOLD) {
                  var8 = true;
               } else if (var11 != null && var11.func_211166_f()) {
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

   public boolean func_78260_a() {
      return this.field_78294_m;
   }

   static class Entry {
      protected final float field_211169_a;
      protected final float field_211170_b;
      protected final float field_211171_c;
      protected final float field_211172_d;
      protected final float field_211173_e;
      protected final float field_211174_f;
      protected final float field_211175_g;
      protected final float field_211176_h;

      private Entry(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         super();
         this.field_211169_a = var1;
         this.field_211170_b = var2;
         this.field_211171_c = var3;
         this.field_211172_d = var4;
         this.field_211173_e = var5;
         this.field_211174_f = var6;
         this.field_211175_g = var7;
         this.field_211176_h = var8;
      }

      public void func_211168_a(BufferBuilder var1) {
         var1.func_181662_b((double)this.field_211169_a, (double)this.field_211170_b, 0.0D).func_181666_a(this.field_211173_e, this.field_211174_f, this.field_211175_g, this.field_211176_h).func_181675_d();
         var1.func_181662_b((double)this.field_211171_c, (double)this.field_211170_b, 0.0D).func_181666_a(this.field_211173_e, this.field_211174_f, this.field_211175_g, this.field_211176_h).func_181675_d();
         var1.func_181662_b((double)this.field_211171_c, (double)this.field_211172_d, 0.0D).func_181666_a(this.field_211173_e, this.field_211174_f, this.field_211175_g, this.field_211176_h).func_181675_d();
         var1.func_181662_b((double)this.field_211169_a, (double)this.field_211172_d, 0.0D).func_181666_a(this.field_211173_e, this.field_211174_f, this.field_211175_g, this.field_211176_h).func_181675_d();
      }

      // $FF: synthetic method
      Entry(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, Object var9) {
         this(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
