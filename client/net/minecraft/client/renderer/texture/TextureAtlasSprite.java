package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

public class TextureAtlasSprite {
   private final String field_110984_i;
   protected List<int[][]> field_110976_a = Lists.newArrayList();
   protected int[][] field_176605_b;
   private AnimationMetadataSection field_110982_k;
   protected boolean field_130222_e;
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
   private static String field_176607_p = "builtin/clock";
   private static String field_176606_q = "builtin/compass";

   protected TextureAtlasSprite(String var1) {
      super();
      this.field_110984_i = var1;
   }

   protected static TextureAtlasSprite func_176604_a(ResourceLocation var0) {
      String var1 = var0.toString();
      if (field_176607_p.equals(var1)) {
         return new TextureClock(var1);
      } else {
         return (TextureAtlasSprite)(field_176606_q.equals(var1) ? new TextureCompass(var1) : new TextureAtlasSprite(var1));
      }
   }

   public static void func_176602_a(String var0) {
      field_176607_p = var0;
   }

   public static void func_176603_b(String var0) {
      field_176606_q = var0;
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

   public float func_94206_g() {
      return this.field_110977_n;
   }

   public float func_94210_h() {
      return this.field_110978_o;
   }

   public float func_94207_b(double var1) {
      float var3 = this.field_110978_o - this.field_110977_n;
      return this.field_110977_n + var3 * ((float)var1 / 16.0F);
   }

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
            TextureUtil.func_147955_a((int[][])this.field_110976_a.get(var3), this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false);
         }
      } else if (this.field_110982_k.func_177219_e()) {
         this.func_180599_n();
      }

   }

   private void func_180599_n() {
      double var1 = 1.0D - (double)this.field_110983_h / (double)this.field_110982_k.func_110472_a(this.field_110973_g);
      int var3 = this.field_110982_k.func_110468_c(this.field_110973_g);
      int var4 = this.field_110982_k.func_110473_c() == 0 ? this.field_110976_a.size() : this.field_110982_k.func_110473_c();
      int var5 = this.field_110982_k.func_110468_c((this.field_110973_g + 1) % var4);
      if (var3 != var5 && var5 >= 0 && var5 < this.field_110976_a.size()) {
         int[][] var6 = (int[][])this.field_110976_a.get(var3);
         int[][] var7 = (int[][])this.field_110976_a.get(var5);
         if (this.field_176605_b == null || this.field_176605_b.length != var6.length) {
            this.field_176605_b = new int[var6.length][];
         }

         for(int var8 = 0; var8 < var6.length; ++var8) {
            if (this.field_176605_b[var8] == null) {
               this.field_176605_b[var8] = new int[var6[var8].length];
            }

            if (var8 < var7.length && var7[var8].length == var6[var8].length) {
               for(int var9 = 0; var9 < var6[var8].length; ++var9) {
                  int var10 = var6[var8][var9];
                  int var11 = var7[var8][var9];
                  int var12 = (int)((double)((var10 & 16711680) >> 16) * var1 + (double)((var11 & 16711680) >> 16) * (1.0D - var1));
                  int var13 = (int)((double)((var10 & '\uff00') >> 8) * var1 + (double)((var11 & '\uff00') >> 8) * (1.0D - var1));
                  int var14 = (int)((double)(var10 & 255) * var1 + (double)(var11 & 255) * (1.0D - var1));
                  this.field_176605_b[var8][var9] = var10 & -16777216 | var12 << 16 | var13 << 8 | var14;
               }
            }
         }

         TextureUtil.func_147955_a(this.field_176605_b, this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false);
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

   public void func_180598_a(BufferedImage[] var1, AnimationMetadataSection var2) throws IOException {
      this.func_130102_n();
      int var3 = var1[0].getWidth();
      int var4 = var1[0].getHeight();
      this.field_130223_c = var3;
      this.field_130224_d = var4;
      int[][] var5 = new int[var1.length][];

      int var6;
      for(var6 = 0; var6 < var1.length; ++var6) {
         BufferedImage var7 = var1[var6];
         if (var7 != null) {
            if (var6 > 0 && (var7.getWidth() != var3 >> var6 || var7.getHeight() != var4 >> var6)) {
               throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", var6, var7.getWidth(), var7.getHeight(), var3 >> var6, var4 >> var6));
            }

            var5[var6] = new int[var7.getWidth() * var7.getHeight()];
            var7.getRGB(0, 0, var7.getWidth(), var7.getHeight(), var5[var6], 0, var7.getWidth());
         }
      }

      if (var2 == null) {
         if (var4 != var3) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.field_110976_a.add(var5);
      } else {
         var6 = var4 / var3;
         int var11 = var3;
         int var8 = var3;
         this.field_130224_d = this.field_130223_c;
         int var10;
         if (var2.func_110473_c() > 0) {
            Iterator var9 = var2.func_130073_e().iterator();

            while(var9.hasNext()) {
               var10 = (Integer)var9.next();
               if (var10 >= var6) {
                  throw new RuntimeException("invalid frameindex " + var10);
               }

               this.func_130099_d(var10);
               this.field_110976_a.set(var10, func_147962_a(var5, var11, var8, var10));
            }

            this.field_110982_k = var2;
         } else {
            ArrayList var12 = Lists.newArrayList();

            for(var10 = 0; var10 < var6; ++var10) {
               this.field_110976_a.add(func_147962_a(var5, var11, var8, var10));
               var12.add(new AnimationFrame(var10, -1));
            }

            this.field_110982_k = new AnimationMetadataSection(var12, this.field_130223_c, this.field_130224_d, var2.func_110469_d(), var2.func_177219_e());
         }
      }

   }

   public void func_147963_d(int var1) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < this.field_110976_a.size(); ++var3) {
         final int[][] var4 = (int[][])this.field_110976_a.get(var3);
         if (var4 != null) {
            try {
               var2.add(TextureUtil.func_147949_a(var1, this.field_130223_c, var4));
            } catch (Throwable var8) {
               CrashReport var6 = CrashReport.func_85055_a(var8, "Generating mipmaps for frame");
               CrashReportCategory var7 = var6.func_85058_a("Frame being iterated");
               var7.func_71507_a("Frame index", var3);
               var7.func_71500_a("Frame sizes", new Callable<String>() {
                  public String call() throws Exception {
                     StringBuilder var1 = new StringBuilder();
                     int[][] var2 = var4;
                     int var3 = var2.length;

                     for(int var4x = 0; var4x < var3; ++var4x) {
                        int[] var5 = var2[var4x];
                        if (var1.length() > 0) {
                           var1.append(", ");
                        }

                        var1.append(var5 == null ? "null" : var5.length);
                     }

                     return var1.toString();
                  }

                  // $FF: synthetic method
                  public Object call() throws Exception {
                     return this.call();
                  }
               });
               throw new ReportedException(var6);
            }
         }
      }

      this.func_110968_a(var2);
   }

   private void func_130099_d(int var1) {
      if (this.field_110976_a.size() <= var1) {
         for(int var2 = this.field_110976_a.size(); var2 <= var1; ++var2) {
            this.field_110976_a.add((Object)null);
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

   public void func_110968_a(List<int[][]> var1) {
      this.field_110976_a = var1;
   }

   private void func_130102_n() {
      this.field_110982_k = null;
      this.func_110968_a(Lists.newArrayList());
      this.field_110973_g = 0;
      this.field_110983_h = 0;
   }

   public String toString() {
      return "TextureAtlasSprite{name='" + this.field_110984_i + '\'' + ", frameCount=" + this.field_110976_a.size() + ", rotated=" + this.field_130222_e + ", x=" + this.field_110975_c + ", y=" + this.field_110974_d + ", height=" + this.field_130224_d + ", width=" + this.field_130223_c + ", u0=" + this.field_110979_l + ", u1=" + this.field_110980_m + ", v0=" + this.field_110977_n + ", v1=" + this.field_110978_o + '}';
   }
}
