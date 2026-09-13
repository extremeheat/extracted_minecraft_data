package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.IIcon;
import net.minecraft.util.ReportedException;

public class TextureAtlasSprite implements IIcon {
   private final String field_110984_i;
   protected List field_110976_a = Lists.newArrayList();
   private AnimationMetadataSection field_110982_k;
   protected boolean field_130222_e;
   private boolean field_147966_k;
   protected int field_110975_c;
   protected int field_110974_d;
   protected int field_130223_c;
   protected int field_130224_d;
   private float field_110979_l;
   private float field_110980_m;
   private float field_110977_n;
   private float field_110978_o;
   protected int field_110973_g;
   protected int field_110983_h;

   protected TextureAtlasSprite(String var1) {
      super();
      this.field_110984_i = var1;
   }

   public void func_110971_a(int var1, int var2, int var3, int var4, boolean var5) {
      this.field_110975_c = var3;
      this.field_110974_d = var4;
      this.field_130222_e = var5;
      float var6 = (float)(0.009999999776482582 / (double)var1);
      float var7 = (float)(0.009999999776482582 / (double)var2);
      this.field_110979_l = (float)var3 / (float)((double)var1) + var6;
      this.field_110980_m = (float)(var3 + this.field_130223_c) / (float)((double)var1) - var6;
      this.field_110977_n = (float)var4 / (float)var2 + var7;
      this.field_110978_o = (float)(var4 + this.field_130224_d) / (float)var2 - var7;
      if (this.field_147966_k) {
         float var8 = 8.0F / (float)var1;
         float var9 = 8.0F / (float)var2;
         this.field_110979_l += var8;
         this.field_110980_m -= var8;
         this.field_110977_n += var9;
         this.field_110978_o -= var9;
      }
   }

   public void func_94217_a(TextureAtlasSprite var1) {
      this.field_110975_c = var1.field_110975_c;
      this.field_110974_d = var1.field_110974_d;
      this.field_130223_c = var1.field_130223_c;
      this.field_130224_d = var1.field_130224_d;
      this.field_130222_e = var1.field_130222_e;
      this.field_110979_l = var1.field_110979_l;
      this.field_110980_m = var1.field_110980_m;
      this.field_110977_n = var1.field_110977_n;
      this.field_110978_o = var1.field_110978_o;
   }

   public int func_130010_a() {
      return this.field_110975_c;
   }

   public int func_110967_i() {
      return this.field_110974_d;
   }

   @Override
   public int func_94211_a() {
      return this.field_130223_c;
   }

   @Override
   public int func_94216_b() {
      return this.field_130224_d;
   }

   @Override
   public float func_94209_e() {
      return this.field_110979_l;
   }

   @Override
   public float func_94212_f() {
      return this.field_110980_m;
   }

   @Override
   public float func_94214_a(double var1) {
      float var3 = this.field_110980_m - this.field_110979_l;
      return this.field_110979_l + var3 * (float)var1 / 16.0F;
   }

   @Override
   public float func_94206_g() {
      return this.field_110977_n;
   }

   @Override
   public float func_94210_h() {
      return this.field_110978_o;
   }

   @Override
   public float func_94207_b(double var1) {
      float var3 = this.field_110978_o - this.field_110977_n;
      return this.field_110977_n + var3 * ((float)var1 / 16.0F);
   }

   @Override
   public String func_94215_i() {
      return this.field_110984_i;
   }

   public void func_94219_l() {
      ++this.field_110983_h;
      if (this.field_110983_h >= this.field_110982_k.func_110472_a(this.field_110973_g)) {
         int var1 = this.field_110982_k.func_110468_c(this.field_110973_g);
         int var2 = this.field_110982_k.func_110473_c() == 0 ? this.field_110976_a.size() : this.field_110982_k.func_110473_c();
         this.field_110973_g = (this.field_110973_g + 1) % var2;
         this.field_110983_h = 0;
         int var3 = this.field_110982_k.func_110468_c(this.field_110973_g);
         if (var1 != var3 && var3 >= 0 && var3 < this.field_110976_a.size()) {
            TextureUtil.func_147955_a(
               (int[][])this.field_110976_a.get(var3), this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false
            );
         }
      }
   }

   public int[][] func_147965_a(int var1) {
      return (int[][])this.field_110976_a.get(var1);
   }

   public int func_110970_k() {
      return this.field_110976_a.size();
   }

   public void func_110966_b(int var1) {
      this.field_130223_c = var1;
   }

   public void func_110969_c(int var1) {
      this.field_130224_d = var1;
   }

   public void func_147964_a(BufferedImage[] var1, AnimationMetadataSection var2, boolean var3) {
      this.func_130102_n();
      this.field_147966_k = var3;
      int var4 = var1[0].getWidth();
      int var5 = var1[0].getHeight();
      this.field_130223_c = var4;
      this.field_130224_d = var5;
      if (var3) {
         this.field_130223_c += 16;
         this.field_130224_d += 16;
      }

      int[][] var6 = new int[var1.length][];

      for(int var7 = 0; var7 < var1.length; ++var7) {
         BufferedImage var8 = var1[var7];
         if (var8 != null) {
            if (var7 > 0 && (var8.getWidth() != var4 >> var7 || var8.getHeight() != var5 >> var7)) {
               throw new RuntimeException(
                  String.format(
                     "Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", var7, var8.getWidth(), var8.getHeight(), var4 >> var7, var5 >> var7
                  )
               );
            }

            var6[var7] = new int[var8.getWidth() * var8.getHeight()];
            var8.getRGB(0, 0, var8.getWidth(), var8.getHeight(), var6[var7], 0, var8.getWidth());
         }
      }

      if (var2 == null) {
         if (var5 != var4) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.func_147961_a(var6);
         this.field_110976_a.add(this.func_147960_a(var6, var4, var5));
      } else {
         int var12 = var5 / var4;
         int var13 = var4;
         int var9 = var4;
         this.field_130224_d = this.field_130223_c;
         if (var2.func_110473_c() > 0) {
            for(int var11 : var2.func_130073_e()) {
               if (var11 >= var12) {
                  throw new RuntimeException("invalid frameindex " + var11);
               }

               this.func_130099_d(var11);
               this.field_110976_a.set(var11, this.func_147960_a(func_147962_a(var6, var13, var9, var11), var13, var9));
            }

            this.field_110982_k = var2;
         } else {
            ArrayList var14 = Lists.newArrayList();

            for(int var15 = 0; var15 < var12; ++var15) {
               this.field_110976_a.add(this.func_147960_a(func_147962_a(var6, var13, var9, var15), var13, var9));
               var14.add(new AnimationFrame(var15, -1));
            }

            this.field_110982_k = new AnimationMetadataSection(var14, this.field_130223_c, this.field_130224_d, var2.func_110469_d());
         }
      }
   }

   public void func_147963_d(int var1) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < this.field_110976_a.size(); ++var3) {
         int[][] var4 = (int[][])this.field_110976_a.get(var3);
         if (var4 != null) {
            try {
               var2.add(TextureUtil.func_147949_a(var1, this.field_130223_c, var4));
            } catch (Throwable var8) {
               CrashReport var6 = CrashReport.func_85055_a(var8, "Generating mipmaps for frame");
               CrashReportCategory var7 = var6.func_85058_a("Frame being iterated");
               var7.func_71507_a("Frame index", var3);
               var7.func_71500_a("Frame sizes", new TextureAtlasSprite$1(this, var4));
               throw new ReportedException(var6);
            }
         }
      }

      this.func_110968_a(var2);
   }

   private void func_147961_a(int[][] var1) {
      int[] var2 = var1[0];
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      for(int var7 = 0; var7 < var2.length; ++var7) {
         if ((var2[var7] & 0xFF000000) != 0) {
            var4 += var2[var7] >> 16 & 0xFF;
            var5 += var2[var7] >> 8 & 0xFF;
            var6 += var2[var7] >> 0 & 0xFF;
            ++var3;
         }
      }

      if (var3 != 0) {
         var4 /= var3;
         var5 /= var3;
         var6 /= var3;

         for(int var11 = 0; var11 < var2.length; ++var11) {
            if ((var2[var11] & 0xFF000000) == 0) {
               var2[var11] = var4 << 16 | var5 << 8 | var6;
            }
         }
      }
   }

   private int[][] func_147960_a(int[][] var1, int var2, int var3) {
      if (!this.field_147966_k) {
         return var1;
      } else {
         int[][] var4 = new int[var1.length][];

         for(int var5 = 0; var5 < var1.length; ++var5) {
            int[] var6 = var1[var5];
            if (var6 != null) {
               int[] var7 = new int[(var2 + 16 >> var5) * (var3 + 16 >> var5)];
               System.arraycopy(var6, 0, var7, 0, var6.length);
               var4[var5] = TextureUtil.func_147948_a(var7, var2 >> var5, var3 >> var5, 8 >> var5);
            }
         }

         return var4;
      }
   }

   private void func_130099_d(int var1) {
      if (this.field_110976_a.size() <= var1) {
         for(int var2 = this.field_110976_a.size(); var2 <= var1; ++var2) {
            this.field_110976_a.add(null);
         }
      }
   }

   private static int[][] func_147962_a(int[][] var0, int var1, int var2, int var3) {
      int[][] var4 = new int[var0.length][];

      for(int var5 = 0; var5 < var0.length; ++var5) {
         int[] var6 = var0[var5];
         if (var6 != null) {
            var4[var5] = new int[(var1 >> var5) * (var2 >> var5)];
            System.arraycopy(var6, var3 * var4[var5].length, var4[var5], 0, var4[var5].length);
         }
      }

      return var4;
   }

   public void func_130103_l() {
      this.field_110976_a.clear();
   }

   public boolean func_130098_m() {
      return this.field_110982_k != null;
   }

   public void func_110968_a(List var1) {
      this.field_110976_a = var1;
   }

   private void func_130102_n() {
      this.field_110982_k = null;
      this.func_110968_a(Lists.newArrayList());
      this.field_110973_g = 0;
      this.field_110983_h = 0;
   }

   @Override
   public String toString() {
      return "TextureAtlasSprite{name='"
         + this.field_110984_i
         + '\''
         + ", frameCount="
         + this.field_110976_a.size()
         + ", rotated="
         + this.field_130222_e
         + ", x="
         + this.field_110975_c
         + ", y="
         + this.field_110974_d
         + ", height="
         + this.field_130224_d
         + ", width="
         + this.field_130223_c
         + ", u0="
         + this.field_110979_l
         + ", u1="
         + this.field_110980_m
         + ", v0="
         + this.field_110977_n
         + ", v1="
         + this.field_110978_o
         + '}';
   }
}
