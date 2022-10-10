package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class TextureAtlasSprite {
   private final ResourceLocation field_110984_i;
   protected final int field_130223_c;
   protected final int field_130224_d;
   protected NativeImage[] field_195670_c;
   @Nullable
   protected int[] field_195671_d;
   @Nullable
   protected int[] field_195672_e;
   protected NativeImage[] field_176605_b;
   private AnimationMetadataSection field_110982_k;
   protected boolean field_130222_e;
   protected int field_110975_c;
   protected int field_110974_d;
   private float field_110979_l;
   private float field_110980_m;
   private float field_110977_n;
   private float field_110978_o;
   protected int field_110973_g;
   protected int field_110983_h;
   private static final int[] field_195673_r = new int[4];
   private static final float[] field_195674_s = (float[])Util.func_200696_a(new float[256], (var0) -> {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = (float)Math.pow((double)((float)var1 / 255.0F), 2.2D);
      }

   });

   protected TextureAtlasSprite(ResourceLocation var1, int var2, int var3) {
      super();
      this.field_110984_i = var1;
      this.field_130223_c = var2;
      this.field_130224_d = var3;
   }

   protected TextureAtlasSprite(ResourceLocation var1, PngSizeInfo var2, @Nullable AnimationMetadataSection var3) {
      super();
      this.field_110984_i = var1;
      if (var3 != null) {
         int var4 = Math.min(var2.field_188533_a, var2.field_188534_b);
         this.field_130224_d = this.field_130223_c = var4;
      } else {
         if (var2.field_188534_b != var2.field_188533_a) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.field_130223_c = var2.field_188533_a;
         this.field_130224_d = var2.field_188534_b;
      }

      this.field_110982_k = var3;
   }

   private void func_195666_b(int var1) {
      NativeImage[] var2 = new NativeImage[var1 + 1];
      var2[0] = this.field_195670_c[0];
      if (var1 > 0) {
         boolean var3 = false;

         int var4;
         label71:
         for(var4 = 0; var4 < this.field_195670_c[0].func_195702_a(); ++var4) {
            for(int var5 = 0; var5 < this.field_195670_c[0].func_195714_b(); ++var5) {
               if (this.field_195670_c[0].func_195709_a(var4, var5) >> 24 == 0) {
                  var3 = true;
                  break label71;
               }
            }
         }

         for(var4 = 1; var4 <= var1; ++var4) {
            if (this.field_195670_c.length > var4 && this.field_195670_c[var4] != null) {
               var2[var4] = this.field_195670_c[var4];
            } else {
               NativeImage var11 = var2[var4 - 1];
               NativeImage var6 = new NativeImage(var11.func_195702_a() >> 1, var11.func_195714_b() >> 1, false);
               int var7 = var6.func_195702_a();
               int var8 = var6.func_195714_b();

               for(int var9 = 0; var9 < var7; ++var9) {
                  for(int var10 = 0; var10 < var8; ++var10) {
                     var6.func_195700_a(var9, var10, func_195661_b(var11.func_195709_a(var9 * 2 + 0, var10 * 2 + 0), var11.func_195709_a(var9 * 2 + 1, var10 * 2 + 0), var11.func_195709_a(var9 * 2 + 0, var10 * 2 + 1), var11.func_195709_a(var9 * 2 + 1, var10 * 2 + 1), var3));
                  }
               }

               var2[var4] = var6;
            }
         }

         for(var4 = var1 + 1; var4 < this.field_195670_c.length; ++var4) {
            if (this.field_195670_c[var4] != null) {
               this.field_195670_c[var4].close();
            }
         }
      }

      this.field_195670_c = var2;
   }

   private static int func_195661_b(int var0, int var1, int var2, int var3, boolean var4) {
      if (var4) {
         field_195673_r[0] = var0;
         field_195673_r[1] = var1;
         field_195673_r[2] = var2;
         field_195673_r[3] = var3;
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;

         int var9;
         for(var9 = 0; var9 < 4; ++var9) {
            if (field_195673_r[var9] >> 24 != 0) {
               var13 += func_195660_c(field_195673_r[var9] >> 24);
               var14 += func_195660_c(field_195673_r[var9] >> 16);
               var15 += func_195660_c(field_195673_r[var9] >> 8);
               var16 += func_195660_c(field_195673_r[var9] >> 0);
            }
         }

         var13 /= 4.0F;
         var14 /= 4.0F;
         var15 /= 4.0F;
         var16 /= 4.0F;
         var9 = (int)(Math.pow((double)var13, 0.45454545454545453D) * 255.0D);
         int var10 = (int)(Math.pow((double)var14, 0.45454545454545453D) * 255.0D);
         int var11 = (int)(Math.pow((double)var15, 0.45454545454545453D) * 255.0D);
         int var12 = (int)(Math.pow((double)var16, 0.45454545454545453D) * 255.0D);
         if (var9 < 96) {
            var9 = 0;
         }

         return var9 << 24 | var10 << 16 | var11 << 8 | var12;
      } else {
         int var5 = func_195669_a(var0, var1, var2, var3, 24);
         int var6 = func_195669_a(var0, var1, var2, var3, 16);
         int var7 = func_195669_a(var0, var1, var2, var3, 8);
         int var8 = func_195669_a(var0, var1, var2, var3, 0);
         return var5 << 24 | var6 << 16 | var7 << 8 | var8;
      }
   }

   private static int func_195669_a(int var0, int var1, int var2, int var3, int var4) {
      float var5 = func_195660_c(var0 >> var4);
      float var6 = func_195660_c(var1 >> var4);
      float var7 = func_195660_c(var2 >> var4);
      float var8 = func_195660_c(var3 >> var4);
      float var9 = (float)((double)((float)Math.pow((double)(var5 + var6 + var7 + var8) * 0.25D, 0.45454545454545453D)));
      return (int)((double)var9 * 255.0D);
   }

   private static float func_195660_c(int var0) {
      return field_195674_s[var0 & 255];
   }

   private void func_195659_d(int var1) {
      int var2 = 0;
      int var3 = 0;
      if (this.field_195671_d != null) {
         var2 = this.field_195671_d[var1] * this.field_130223_c;
         var3 = this.field_195672_e[var1] * this.field_130224_d;
      }

      this.func_195667_a(var2, var3, this.field_195670_c);
   }

   private void func_195667_a(int var1, int var2, NativeImage[] var3) {
      for(int var4 = 0; var4 < this.field_195670_c.length; ++var4) {
         var3[var4].func_195706_a(var4, this.field_110975_c >> var4, this.field_110974_d >> var4, var1 >> var4, var2 >> var4, this.field_130223_c >> var4, this.field_130224_d >> var4, this.field_195670_c.length > 1);
      }

   }

   public void func_110971_a(int var1, int var2, int var3, int var4, boolean var5) {
      this.field_110975_c = var3;
      this.field_110974_d = var4;
      this.field_130222_e = var5;
      float var6 = (float)(0.009999999776482582D / (double)var1);
      float var7 = (float)(0.009999999776482582D / (double)var2);
      this.field_110979_l = (float)var3 / (float)((double)var1) + var6;
      this.field_110980_m = (float)(var3 + this.field_130223_c) / (float)((double)var1) - var6;
      this.field_110977_n = (float)var4 / (float)var2 + var7;
      this.field_110978_o = (float)(var4 + this.field_130224_d) / (float)var2 - var7;
   }

   public int func_94211_a() {
      return this.field_130223_c;
   }

   public int func_94216_b() {
      return this.field_130224_d;
   }

   public float func_94209_e() {
      return this.field_110979_l;
   }

   public float func_94212_f() {
      return this.field_110980_m;
   }

   public float func_94214_a(double var1) {
      float var3 = this.field_110980_m - this.field_110979_l;
      return this.field_110979_l + var3 * (float)var1 / 16.0F;
   }

   public float func_188537_a(float var1) {
      float var2 = this.field_110980_m - this.field_110979_l;
      return (var1 - this.field_110979_l) / var2 * 16.0F;
   }

   public float func_94206_g() {
      return this.field_110977_n;
   }

   public float func_94210_h() {
      return this.field_110978_o;
   }

   public float func_94207_b(double var1) {
      float var3 = this.field_110978_o - this.field_110977_n;
      return this.field_110977_n + var3 * (float)var1 / 16.0F;
   }

   public float func_188536_b(float var1) {
      float var2 = this.field_110978_o - this.field_110977_n;
      return (var1 - this.field_110977_n) / var2 * 16.0F;
   }

   public ResourceLocation func_195668_m() {
      return this.field_110984_i;
   }

   public void func_94219_l() {
      ++this.field_110983_h;
      if (this.field_110983_h >= this.field_110982_k.func_110472_a(this.field_110973_g)) {
         int var1 = this.field_110982_k.func_110468_c(this.field_110973_g);
         int var2 = this.field_110982_k.func_110473_c() == 0 ? this.func_110970_k() : this.field_110982_k.func_110473_c();
         this.field_110973_g = (this.field_110973_g + 1) % var2;
         this.field_110983_h = 0;
         int var3 = this.field_110982_k.func_110468_c(this.field_110973_g);
         if (var1 != var3 && var3 >= 0 && var3 < this.func_110970_k()) {
            this.func_195659_d(var3);
         }
      } else if (this.field_110982_k.func_177219_e()) {
         this.func_180599_n();
      }

   }

   private void func_180599_n() {
      double var1 = 1.0D - (double)this.field_110983_h / (double)this.field_110982_k.func_110472_a(this.field_110973_g);
      int var3 = this.field_110982_k.func_110468_c(this.field_110973_g);
      int var4 = this.field_110982_k.func_110473_c() == 0 ? this.func_110970_k() : this.field_110982_k.func_110473_c();
      int var5 = this.field_110982_k.func_110468_c((this.field_110973_g + 1) % var4);
      if (var3 != var5 && var5 >= 0 && var5 < this.func_110970_k()) {
         int var7;
         int var8;
         if (this.field_176605_b == null || this.field_176605_b.length != this.field_195670_c.length) {
            if (this.field_176605_b != null) {
               NativeImage[] var6 = this.field_176605_b;
               var7 = var6.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  NativeImage var9 = var6[var8];
                  if (var9 != null) {
                     var9.close();
                  }
               }
            }

            this.field_176605_b = new NativeImage[this.field_195670_c.length];
         }

         for(int var16 = 0; var16 < this.field_195670_c.length; ++var16) {
            var7 = this.field_130223_c >> var16;
            var8 = this.field_130224_d >> var16;
            if (this.field_176605_b[var16] == null) {
               this.field_176605_b[var16] = new NativeImage(var7, var8, false);
            }

            for(int var17 = 0; var17 < var8; ++var17) {
               for(int var10 = 0; var10 < var7; ++var10) {
                  int var11 = this.func_195665_a(var3, var16, var10, var17);
                  int var12 = this.func_195665_a(var5, var16, var10, var17);
                  int var13 = this.func_188535_a(var1, var11 >> 16 & 255, var12 >> 16 & 255);
                  int var14 = this.func_188535_a(var1, var11 >> 8 & 255, var12 >> 8 & 255);
                  int var15 = this.func_188535_a(var1, var11 & 255, var12 & 255);
                  this.field_176605_b[var16].func_195700_a(var10, var17, var11 & -16777216 | var13 << 16 | var14 << 8 | var15);
               }
            }
         }

         this.func_195667_a(0, 0, this.field_176605_b);
      }

   }

   private int func_188535_a(double var1, int var3, int var4) {
      return (int)(var1 * (double)var3 + (1.0D - var1) * (double)var4);
   }

   public int func_110970_k() {
      return this.field_195671_d == null ? 0 : this.field_195671_d.length;
   }

   public void func_195664_a(IResource var1, int var2) throws IOException {
      NativeImage var3 = NativeImage.func_195713_a(var1.func_199027_b());
      this.field_195670_c = new NativeImage[var2];
      this.field_195670_c[0] = var3;
      int var4;
      if (this.field_110982_k != null && this.field_110982_k.func_110474_b() != -1) {
         var4 = var3.func_195702_a() / this.field_110982_k.func_110474_b();
      } else {
         var4 = var3.func_195702_a() / this.field_130223_c;
      }

      int var5;
      if (this.field_110982_k != null && this.field_110982_k.func_110471_a() != -1) {
         var5 = var3.func_195714_b() / this.field_110982_k.func_110471_a();
      } else {
         var5 = var3.func_195714_b() / this.field_130224_d;
      }

      int var8;
      int var9;
      int var10;
      if (this.field_110982_k != null && this.field_110982_k.func_110473_c() > 0) {
         int var11 = (Integer)this.field_110982_k.func_130073_e().stream().max(Integer::compareTo).get() + 1;
         this.field_195671_d = new int[var11];
         this.field_195672_e = new int[var11];
         Arrays.fill(this.field_195671_d, -1);
         Arrays.fill(this.field_195672_e, -1);

         for(Iterator var12 = this.field_110982_k.func_130073_e().iterator(); var12.hasNext(); this.field_195672_e[var8] = var9) {
            var8 = (Integer)var12.next();
            if (var8 >= var4 * var5) {
               throw new RuntimeException("invalid frameindex " + var8);
            }

            var9 = var8 / var4;
            var10 = var8 % var4;
            this.field_195671_d[var8] = var10;
         }
      } else {
         ArrayList var6 = Lists.newArrayList();
         int var7 = var4 * var5;
         this.field_195671_d = new int[var7];
         this.field_195672_e = new int[var7];

         for(var8 = 0; var8 < var5; ++var8) {
            for(var9 = 0; var9 < var4; ++var9) {
               var10 = var8 * var4 + var9;
               this.field_195671_d[var10] = var9;
               this.field_195672_e[var10] = var8;
               var6.add(new AnimationFrame(var10, -1));
            }
         }

         var8 = 1;
         boolean var13 = false;
         if (this.field_110982_k != null) {
            var8 = this.field_110982_k.func_110469_d();
            var13 = this.field_110982_k.func_177219_e();
         }

         this.field_110982_k = new AnimationMetadataSection(var6, this.field_130223_c, this.field_130224_d, var8, var13);
      }

   }

   public void func_147963_d(int var1) {
      try {
         this.func_195666_b(var1);
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.func_85055_a(var5, "Generating mipmaps for frame");
         CrashReportCategory var4 = var3.func_85058_a("Frame being iterated");
         var4.func_189529_a("Frame sizes", () -> {
            StringBuilder var1 = new StringBuilder();
            NativeImage[] var2 = this.field_195670_c;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               NativeImage var5 = var2[var4];
               if (var1.length() > 0) {
                  var1.append(", ");
               }

               var1.append(var5 == null ? "null" : var5.func_195702_a() + "x" + var5.func_195714_b());
            }

            return var1.toString();
         });
         throw new ReportedException(var3);
      }
   }

   public void func_130103_l() {
      NativeImage[] var1;
      int var2;
      int var3;
      NativeImage var4;
      if (this.field_195670_c != null) {
         var1 = this.field_195670_c;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            if (var4 != null) {
               var4.close();
            }
         }
      }

      this.field_195670_c = null;
      if (this.field_176605_b != null) {
         var1 = this.field_176605_b;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            if (var4 != null) {
               var4.close();
            }
         }
      }

      this.field_176605_b = null;
   }

   public boolean func_130098_m() {
      return this.field_110982_k != null && this.field_110982_k.func_110473_c() > 1;
   }

   public String toString() {
      int var1 = this.field_195671_d == null ? 0 : this.field_195671_d.length;
      return "TextureAtlasSprite{name='" + this.field_110984_i + '\'' + ", frameCount=" + var1 + ", rotated=" + this.field_130222_e + ", x=" + this.field_110975_c + ", y=" + this.field_110974_d + ", height=" + this.field_130224_d + ", width=" + this.field_130223_c + ", u0=" + this.field_110979_l + ", u1=" + this.field_110980_m + ", v0=" + this.field_110977_n + ", v1=" + this.field_110978_o + '}';
   }

   private int func_195665_a(int var1, int var2, int var3, int var4) {
      return this.field_195670_c[var2].func_195709_a(var3 + (this.field_195671_d[var1] * this.field_130223_c >> var2), var4 + (this.field_195672_e[var1] * this.field_130224_d >> var2));
   }

   public boolean func_195662_a(int var1, int var2, int var3) {
      return (this.field_195670_c[0].func_195709_a(var2 + this.field_195671_d[var1] * this.field_130223_c, var3 + this.field_195672_e[var1] * this.field_130224_d) >> 24 & 255) == 0;
   }

   public void func_195663_q() {
      this.func_195659_d(0);
   }
}
