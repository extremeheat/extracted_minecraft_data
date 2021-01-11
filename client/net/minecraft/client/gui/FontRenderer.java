package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private static final ResourceLocation[] field_111274_c = new ResourceLocation[256];
   private int[] field_78286_d = new int[256];
   public int field_78288_b = 9;
   public Random field_78289_c = new Random();
   private byte[] field_78287_e = new byte[65536];
   private int[] field_78285_g = new int[32];
   private final ResourceLocation field_111273_g;
   private final TextureManager field_78298_i;
   private float field_78295_j;
   private float field_78296_k;
   private boolean field_78293_l;
   private boolean field_78294_m;
   private float field_78291_n;
   private float field_78292_o;
   private float field_78306_p;
   private float field_78305_q;
   private int field_78304_r;
   private boolean field_78303_s;
   private boolean field_78302_t;
   private boolean field_78301_u;
   private boolean field_78300_v;
   private boolean field_78299_w;

   public FontRenderer(GameSettings var1, ResourceLocation var2, TextureManager var3, boolean var4) {
      super();
      this.field_111273_g = var2;
      this.field_78298_i = var3;
      this.field_78293_l = var4;
      var3.func_110577_a(this.field_111273_g);

      for(int var5 = 0; var5 < 32; ++var5) {
         int var6 = (var5 >> 3 & 1) * 85;
         int var7 = (var5 >> 2 & 1) * 170 + var6;
         int var8 = (var5 >> 1 & 1) * 170 + var6;
         int var9 = (var5 >> 0 & 1) * 170 + var6;
         if (var5 == 6) {
            var7 += 85;
         }

         if (var1.field_74337_g) {
            int var10 = (var7 * 30 + var8 * 59 + var9 * 11) / 100;
            int var11 = (var7 * 30 + var8 * 70) / 100;
            int var12 = (var7 * 30 + var9 * 70) / 100;
            var7 = var10;
            var8 = var11;
            var9 = var12;
         }

         if (var5 >= 16) {
            var7 /= 4;
            var8 /= 4;
            var9 /= 4;
         }

         this.field_78285_g[var5] = (var7 & 255) << 16 | (var8 & 255) << 8 | var9 & 255;
      }

      this.func_98306_d();
   }

   public void func_110549_a(IResourceManager var1) {
      this.func_111272_d();
   }

   private void func_111272_d() {
      BufferedImage var1;
      try {
         var1 = TextureUtil.func_177053_a(Minecraft.func_71410_x().func_110442_L().func_110536_a(this.field_111273_g).func_110527_b());
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      }

      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      int[] var4 = new int[var2 * var3];
      var1.getRGB(0, 0, var2, var3, var4, 0, var2);
      int var5 = var3 / 16;
      int var6 = var2 / 16;
      byte var7 = 1;
      float var8 = 8.0F / (float)var6;

      for(int var9 = 0; var9 < 256; ++var9) {
         int var10 = var9 % 16;
         int var11 = var9 / 16;
         if (var9 == 32) {
            this.field_78286_d[var9] = 3 + var7;
         }

         int var12;
         for(var12 = var6 - 1; var12 >= 0; --var12) {
            int var13 = var10 * var6 + var12;
            boolean var14 = true;

            for(int var15 = 0; var15 < var5 && var14; ++var15) {
               int var16 = (var11 * var6 + var15) * var2;
               if ((var4[var13 + var16] >> 24 & 255) != 0) {
                  var14 = false;
               }
            }

            if (!var14) {
               break;
            }
         }

         ++var12;
         this.field_78286_d[var9] = (int)(0.5D + (double)((float)var12 * var8)) + var7;
      }

   }

   private void func_98306_d() {
      InputStream var1 = null;

      try {
         var1 = Minecraft.func_71410_x().func_110442_L().func_110536_a(new ResourceLocation("font/glyph_sizes.bin")).func_110527_b();
         var1.read(this.field_78287_e);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      } finally {
         IOUtils.closeQuietly(var1);
      }

   }

   private float func_181559_a(char var1, boolean var2) {
      if (var1 == ' ') {
         return 4.0F;
      } else {
         int var3 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(var1);
         return var3 != -1 && !this.field_78293_l ? this.func_78266_a(var3, var2) : this.func_78277_a(var1, var2);
      }
   }

   private float func_78266_a(int var1, boolean var2) {
      int var3 = var1 % 16 * 8;
      int var4 = var1 / 16 * 8;
      int var5 = var2 ? 1 : 0;
      this.field_78298_i.func_110577_a(this.field_111273_g);
      int var6 = this.field_78286_d[var1];
      float var7 = (float)var6 - 0.01F;
      GL11.glBegin(5);
      GL11.glTexCoord2f((float)var3 / 128.0F, (float)var4 / 128.0F);
      GL11.glVertex3f(this.field_78295_j + (float)var5, this.field_78296_k, 0.0F);
      GL11.glTexCoord2f((float)var3 / 128.0F, ((float)var4 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.field_78295_j - (float)var5, this.field_78296_k + 7.99F, 0.0F);
      GL11.glTexCoord2f(((float)var3 + var7 - 1.0F) / 128.0F, (float)var4 / 128.0F);
      GL11.glVertex3f(this.field_78295_j + var7 - 1.0F + (float)var5, this.field_78296_k, 0.0F);
      GL11.glTexCoord2f(((float)var3 + var7 - 1.0F) / 128.0F, ((float)var4 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.field_78295_j + var7 - 1.0F - (float)var5, this.field_78296_k + 7.99F, 0.0F);
      GL11.glEnd();
      return (float)var6;
   }

   private ResourceLocation func_111271_a(int var1) {
      if (field_111274_c[var1] == null) {
         field_111274_c[var1] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", var1));
      }

      return field_111274_c[var1];
   }

   private void func_78257_a(int var1) {
      this.field_78298_i.func_110577_a(this.func_111271_a(var1));
   }

   private float func_78277_a(char var1, boolean var2) {
      if (this.field_78287_e[var1] == 0) {
         return 0.0F;
      } else {
         int var3 = var1 / 256;
         this.func_78257_a(var3);
         int var4 = this.field_78287_e[var1] >>> 4;
         int var5 = this.field_78287_e[var1] & 15;
         float var6 = (float)var4;
         float var7 = (float)(var5 + 1);
         float var8 = (float)(var1 % 16 * 16) + var6;
         float var9 = (float)((var1 & 255) / 16 * 16);
         float var10 = var7 - var6 - 0.02F;
         float var11 = var2 ? 1.0F : 0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(var8 / 256.0F, var9 / 256.0F);
         GL11.glVertex3f(this.field_78295_j + var11, this.field_78296_k, 0.0F);
         GL11.glTexCoord2f(var8 / 256.0F, (var9 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.field_78295_j - var11, this.field_78296_k + 7.99F, 0.0F);
         GL11.glTexCoord2f((var8 + var10) / 256.0F, var9 / 256.0F);
         GL11.glVertex3f(this.field_78295_j + var10 / 2.0F + var11, this.field_78296_k, 0.0F);
         GL11.glTexCoord2f((var8 + var10) / 256.0F, (var9 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.field_78295_j + var10 / 2.0F - var11, this.field_78296_k + 7.99F, 0.0F);
         GL11.glEnd();
         return (var7 - var6) / 2.0F + 1.0F;
      }
   }

   public int func_175063_a(String var1, float var2, float var3, int var4) {
      return this.func_175065_a(var1, var2, var3, var4, true);
   }

   public int func_78276_b(String var1, int var2, int var3, int var4) {
      return this.func_175065_a(var1, (float)var2, (float)var3, var4, false);
   }

   public int func_175065_a(String var1, float var2, float var3, int var4, boolean var5) {
      GlStateManager.func_179141_d();
      this.func_78265_b();
      int var6;
      if (var5) {
         var6 = this.func_180455_b(var1, var2 + 1.0F, var3 + 1.0F, var4, true);
         var6 = Math.max(var6, this.func_180455_b(var1, var2, var3, var4, false));
      } else {
         var6 = this.func_180455_b(var1, var2, var3, var4, false);
      }

      return var6;
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

   private void func_78265_b() {
      this.field_78303_s = false;
      this.field_78302_t = false;
      this.field_78301_u = false;
      this.field_78300_v = false;
      this.field_78299_w = false;
   }

   private void func_78255_a(String var1, boolean var2) {
      for(int var3 = 0; var3 < var1.length(); ++var3) {
         char var4 = var1.charAt(var3);
         int var5;
         int var6;
         if (var4 == 167 && var3 + 1 < var1.length()) {
            var5 = "0123456789abcdefklmnor".indexOf(var1.toLowerCase(Locale.ENGLISH).charAt(var3 + 1));
            if (var5 < 16) {
               this.field_78303_s = false;
               this.field_78302_t = false;
               this.field_78299_w = false;
               this.field_78300_v = false;
               this.field_78301_u = false;
               if (var5 < 0 || var5 > 15) {
                  var5 = 15;
               }

               if (var2) {
                  var5 += 16;
               }

               var6 = this.field_78285_g[var5];
               this.field_78304_r = var6;
               GlStateManager.func_179131_c((float)(var6 >> 16) / 255.0F, (float)(var6 >> 8 & 255) / 255.0F, (float)(var6 & 255) / 255.0F, this.field_78305_q);
            } else if (var5 == 16) {
               this.field_78303_s = true;
            } else if (var5 == 17) {
               this.field_78302_t = true;
            } else if (var5 == 18) {
               this.field_78299_w = true;
            } else if (var5 == 19) {
               this.field_78300_v = true;
            } else if (var5 == 20) {
               this.field_78301_u = true;
            } else if (var5 == 21) {
               this.field_78303_s = false;
               this.field_78302_t = false;
               this.field_78299_w = false;
               this.field_78300_v = false;
               this.field_78301_u = false;
               GlStateManager.func_179131_c(this.field_78291_n, this.field_78292_o, this.field_78306_p, this.field_78305_q);
            }

            ++var3;
         } else {
            var5 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(var4);
            if (this.field_78303_s && var5 != -1) {
               var6 = this.func_78263_a(var4);

               char var7;
               do {
                  var5 = this.field_78289_c.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".length());
                  var7 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".charAt(var5);
               } while(var6 != this.func_78263_a(var7));

               var4 = var7;
            }

            float var12 = this.field_78293_l ? 0.5F : 1.0F;
            boolean var13 = (var4 == 0 || var5 == -1 || this.field_78293_l) && var2;
            if (var13) {
               this.field_78295_j -= var12;
               this.field_78296_k -= var12;
            }

            float var8 = this.func_181559_a(var4, this.field_78301_u);
            if (var13) {
               this.field_78295_j += var12;
               this.field_78296_k += var12;
            }

            if (this.field_78302_t) {
               this.field_78295_j += var12;
               if (var13) {
                  this.field_78295_j -= var12;
                  this.field_78296_k -= var12;
               }

               this.func_181559_a(var4, this.field_78301_u);
               this.field_78295_j -= var12;
               if (var13) {
                  this.field_78295_j += var12;
                  this.field_78296_k += var12;
               }

               ++var8;
            }

            Tessellator var9;
            WorldRenderer var10;
            if (this.field_78299_w) {
               var9 = Tessellator.func_178181_a();
               var10 = var9.func_178180_c();
               GlStateManager.func_179090_x();
               var10.func_181668_a(7, DefaultVertexFormats.field_181705_e);
               var10.func_181662_b((double)this.field_78295_j, (double)(this.field_78296_k + (float)(this.field_78288_b / 2)), 0.0D).func_181675_d();
               var10.func_181662_b((double)(this.field_78295_j + var8), (double)(this.field_78296_k + (float)(this.field_78288_b / 2)), 0.0D).func_181675_d();
               var10.func_181662_b((double)(this.field_78295_j + var8), (double)(this.field_78296_k + (float)(this.field_78288_b / 2) - 1.0F), 0.0D).func_181675_d();
               var10.func_181662_b((double)this.field_78295_j, (double)(this.field_78296_k + (float)(this.field_78288_b / 2) - 1.0F), 0.0D).func_181675_d();
               var9.func_78381_a();
               GlStateManager.func_179098_w();
            }

            if (this.field_78300_v) {
               var9 = Tessellator.func_178181_a();
               var10 = var9.func_178180_c();
               GlStateManager.func_179090_x();
               var10.func_181668_a(7, DefaultVertexFormats.field_181705_e);
               int var11 = this.field_78300_v ? -1 : 0;
               var10.func_181662_b((double)(this.field_78295_j + (float)var11), (double)(this.field_78296_k + (float)this.field_78288_b), 0.0D).func_181675_d();
               var10.func_181662_b((double)(this.field_78295_j + var8), (double)(this.field_78296_k + (float)this.field_78288_b), 0.0D).func_181675_d();
               var10.func_181662_b((double)(this.field_78295_j + var8), (double)(this.field_78296_k + (float)this.field_78288_b - 1.0F), 0.0D).func_181675_d();
               var10.func_181662_b((double)(this.field_78295_j + (float)var11), (double)(this.field_78296_k + (float)this.field_78288_b - 1.0F), 0.0D).func_181675_d();
               var9.func_78381_a();
               GlStateManager.func_179098_w();
            }

            this.field_78295_j += (float)((int)var8);
         }
      }

   }

   private int func_78274_b(String var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (this.field_78294_m) {
         int var7 = this.func_78256_a(this.func_147647_b(var1));
         var2 = var2 + var4 - var7;
      }

      return this.func_180455_b(var1, (float)var2, (float)var3, var5, var6);
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
            var4 = (var4 & 16579836) >> 2 | var4 & -16777216;
         }

         this.field_78291_n = (float)(var4 >> 16 & 255) / 255.0F;
         this.field_78292_o = (float)(var4 >> 8 & 255) / 255.0F;
         this.field_78306_p = (float)(var4 & 255) / 255.0F;
         this.field_78305_q = (float)(var4 >> 24 & 255) / 255.0F;
         GlStateManager.func_179131_c(this.field_78291_n, this.field_78292_o, this.field_78306_p, this.field_78305_q);
         this.field_78295_j = var2;
         this.field_78296_k = var3;
         this.func_78255_a(var1, var5);
         return (int)this.field_78295_j;
      }
   }

   public int func_78256_a(String var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;
         boolean var3 = false;

         for(int var4 = 0; var4 < var1.length(); ++var4) {
            char var5 = var1.charAt(var4);
            int var6 = this.func_78263_a(var5);
            if (var6 < 0 && var4 < var1.length() - 1) {
               ++var4;
               var5 = var1.charAt(var4);
               if (var5 != 'l' && var5 != 'L') {
                  if (var5 == 'r' || var5 == 'R') {
                     var3 = false;
                  }
               } else {
                  var3 = true;
               }

               var6 = 0;
            }

            var2 += var6;
            if (var3 && var6 > 0) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public int func_78263_a(char var1) {
      if (var1 == 167) {
         return -1;
      } else if (var1 == ' ') {
         return 4;
      } else {
         int var2 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(var1);
         if (var1 > 0 && var2 != -1 && !this.field_78293_l) {
            return this.field_78286_d[var2];
         } else if (this.field_78287_e[var1] != 0) {
            int var3 = this.field_78287_e[var1] >>> 4;
            int var4 = this.field_78287_e[var1] & 15;
            if (var4 > 7) {
               var4 = 15;
               var3 = 0;
            }

            ++var4;
            return (var4 - var3) / 2 + 1;
         } else {
            return 0;
         }
      }
   }

   public String func_78269_a(String var1, int var2) {
      return this.func_78262_a(var1, var2, false);
   }

   public String func_78262_a(String var1, int var2, boolean var3) {
      StringBuilder var4 = new StringBuilder();
      int var5 = 0;
      int var6 = var3 ? var1.length() - 1 : 0;
      int var7 = var3 ? -1 : 1;
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var6; var10 >= 0 && var10 < var1.length() && var5 < var2; var10 += var7) {
         char var11 = var1.charAt(var10);
         int var12 = this.func_78263_a(var11);
         if (var8) {
            var8 = false;
            if (var11 != 'l' && var11 != 'L') {
               if (var11 == 'r' || var11 == 'R') {
                  var9 = false;
               }
            } else {
               var9 = true;
            }
         } else if (var12 < 0) {
            var8 = true;
         } else {
            var5 += var12;
            if (var9) {
               ++var5;
            }
         }

         if (var5 > var2) {
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
      this.func_78265_b();
      this.field_78304_r = var5;
      var1 = this.func_78273_d(var1);
      this.func_78268_b(var1, var2, var3, var4, false);
   }

   private void func_78268_b(String var1, int var2, int var3, int var4, boolean var5) {
      List var6 = this.func_78271_c(var1, var4);

      for(Iterator var7 = var6.iterator(); var7.hasNext(); var3 += this.field_78288_b) {
         String var8 = (String)var7.next();
         this.func_78274_b(var8, var2, var3, var4, this.field_78304_r, var5);
      }

   }

   public int func_78267_b(String var1, int var2) {
      return this.field_78288_b * this.func_78271_c(var1, var2).size();
   }

   public void func_78264_a(boolean var1) {
      this.field_78293_l = var1;
   }

   public boolean func_82883_a() {
      return this.field_78293_l;
   }

   public void func_78275_b(boolean var1) {
      this.field_78294_m = var1;
   }

   public List<String> func_78271_c(String var1, int var2) {
      return Arrays.asList(this.func_78280_d(var1, var2).split("\n"));
   }

   String func_78280_d(String var1, int var2) {
      int var3 = this.func_78259_e(var1, var2);
      if (var1.length() <= var3) {
         return var1;
      } else {
         String var4 = var1.substring(0, var3);
         char var5 = var1.charAt(var3);
         boolean var6 = var5 == ' ' || var5 == '\n';
         String var7 = func_78282_e(var4) + var1.substring(var3 + (var6 ? 1 : 0));
         return var4 + "\n" + this.func_78280_d(var7, var2);
      }
   }

   private int func_78259_e(String var1, int var2) {
      int var3 = var1.length();
      int var4 = 0;
      int var5 = 0;
      int var6 = -1;

      for(boolean var7 = false; var5 < var3; ++var5) {
         char var8 = var1.charAt(var5);
         switch(var8) {
         case '\n':
            --var5;
            break;
         case ' ':
            var6 = var5;
         default:
            var4 += this.func_78263_a(var8);
            if (var7) {
               ++var4;
            }
            break;
         case '\u00a7':
            if (var5 < var3 - 1) {
               ++var5;
               char var9 = var1.charAt(var5);
               if (var9 != 'l' && var9 != 'L') {
                  if (var9 == 'r' || var9 == 'R' || func_78272_b(var9)) {
                     var7 = false;
                  }
               } else {
                  var7 = true;
               }
            }
         }

         if (var8 == '\n') {
            ++var5;
            var6 = var5;
            break;
         }

         if (var4 > var2) {
            break;
         }
      }

      return var5 != var3 && var6 != -1 && var6 < var5 ? var6 : var5;
   }

   private static boolean func_78272_b(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'f' || var0 >= 'A' && var0 <= 'F';
   }

   private static boolean func_78270_c(char var0) {
      return var0 >= 'k' && var0 <= 'o' || var0 >= 'K' && var0 <= 'O' || var0 == 'r' || var0 == 'R';
   }

   public static String func_78282_e(String var0) {
      String var1 = "";
      int var2 = -1;
      int var3 = var0.length();

      while((var2 = var0.indexOf(167, var2 + 1)) != -1) {
         if (var2 < var3 - 1) {
            char var4 = var0.charAt(var2 + 1);
            if (func_78272_b(var4)) {
               var1 = "\u00a7" + var4;
            } else if (func_78270_c(var4)) {
               var1 = var1 + "\u00a7" + var4;
            }
         }
      }

      return var1;
   }

   public boolean func_78260_a() {
      return this.field_78294_m;
   }

   public int func_175064_b(char var1) {
      return this.field_78285_g["0123456789abcdef".indexOf(var1)];
   }
}
