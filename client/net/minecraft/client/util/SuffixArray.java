package net.minecraft.client.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuffixArray<T> {
   private static final boolean field_194062_b = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
   private static final boolean field_194063_c = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
   private static final Logger field_194064_d = LogManager.getLogger();
   protected final List<T> field_194061_a = Lists.newArrayList();
   private final IntList field_194065_e = new IntArrayList();
   private final IntList field_194066_f = new IntArrayList();
   private IntList field_194067_g = new IntArrayList();
   private IntList field_194068_h = new IntArrayList();
   private int field_194069_i;

   public SuffixArray() {
      super();
   }

   public void func_194057_a(T var1, String var2) {
      this.field_194069_i = Math.max(this.field_194069_i, var2.length());
      int var3 = this.field_194061_a.size();
      this.field_194061_a.add(var1);
      this.field_194066_f.add(this.field_194065_e.size());

      for(int var4 = 0; var4 < var2.length(); ++var4) {
         this.field_194067_g.add(var3);
         this.field_194068_h.add(var4);
         this.field_194065_e.add(var2.charAt(var4));
      }

      this.field_194067_g.add(var3);
      this.field_194068_h.add(var2.length());
      this.field_194065_e.add(-1);
   }

   public void func_194058_a() {
      int var1 = this.field_194065_e.size();
      int[] var2 = new int[var1];
      final int[] var3 = new int[var1];
      final int[] var4 = new int[var1];
      int[] var5 = new int[var1];
      IntComparator var6 = new IntComparator() {
         public int compare(int var1, int var2) {
            return var3[var1] == var3[var2] ? Integer.compare(var4[var1], var4[var2]) : Integer.compare(var3[var1], var3[var2]);
         }

         public int compare(Integer var1, Integer var2) {
            return this.compare(var1, var2);
         }
      };
      Swapper var7 = (var3x, var4x) -> {
         if (var3x != var4x) {
            int var5x = var3[var3x];
            var3[var3x] = var3[var4x];
            var3[var4x] = var5x;
            var5x = var4[var3x];
            var4[var3x] = var4[var4x];
            var4[var4x] = var5x;
            var5x = var5[var3x];
            var5[var3x] = var5[var4x];
            var5[var4x] = var5x;
         }

      };

      int var8;
      for(var8 = 0; var8 < var1; ++var8) {
         var2[var8] = this.field_194065_e.getInt(var8);
      }

      var8 = 1;

      for(int var9 = Math.min(var1, this.field_194069_i); var8 * 2 < var9; var8 *= 2) {
         int var10;
         for(var10 = 0; var10 < var1; var5[var10] = var10++) {
            var3[var10] = var2[var10];
            var4[var10] = var10 + var8 < var1 ? var2[var10 + var8] : -2;
         }

         Arrays.quickSort(0, var1, var6, var7);

         for(var10 = 0; var10 < var1; ++var10) {
            if (var10 > 0 && var3[var10] == var3[var10 - 1] && var4[var10] == var4[var10 - 1]) {
               var2[var5[var10]] = var2[var5[var10 - 1]];
            } else {
               var2[var5[var10]] = var10;
            }
         }
      }

      IntList var14 = this.field_194067_g;
      IntList var11 = this.field_194068_h;
      this.field_194067_g = new IntArrayList(var14.size());
      this.field_194068_h = new IntArrayList(var11.size());

      for(int var12 = 0; var12 < var1; ++var12) {
         int var13 = var5[var12];
         this.field_194067_g.add(var14.getInt(var13));
         this.field_194068_h.add(var11.getInt(var13));
      }

      if (field_194063_c) {
         this.func_194060_b();
      }

   }

   private void func_194060_b() {
      for(int var1 = 0; var1 < this.field_194067_g.size(); ++var1) {
         field_194064_d.debug("{} {}", var1, this.func_194059_a(var1));
      }

      field_194064_d.debug("");
   }

   private String func_194059_a(int var1) {
      int var2 = this.field_194068_h.getInt(var1);
      int var3 = this.field_194066_f.getInt(this.field_194067_g.getInt(var1));
      StringBuilder var4 = new StringBuilder();

      for(int var5 = 0; var3 + var5 < this.field_194065_e.size(); ++var5) {
         if (var5 == var2) {
            var4.append('^');
         }

         int var6 = this.field_194065_e.get(var3 + var5);
         if (var6 == -1) {
            break;
         }

         var4.append((char)var6);
      }

      return var4.toString();
   }

   private int func_194056_a(String var1, int var2) {
      int var3 = this.field_194066_f.getInt(this.field_194067_g.getInt(var2));
      int var4 = this.field_194068_h.getInt(var2);

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         int var6 = this.field_194065_e.getInt(var3 + var4 + var5);
         if (var6 == -1) {
            return 1;
         }

         char var7 = var1.charAt(var5);
         char var8 = (char)var6;
         if (var7 < var8) {
            return -1;
         }

         if (var7 > var8) {
            return 1;
         }
      }

      return 0;
   }

   public List<T> func_194055_a(String var1) {
      int var2 = this.field_194067_g.size();
      int var3 = 0;
      int var4 = var2;

      int var5;
      int var6;
      while(var3 < var4) {
         var5 = var3 + (var4 - var3) / 2;
         var6 = this.func_194056_a(var1, var5);
         if (field_194062_b) {
            field_194064_d.debug("comparing lower \"{}\" with {} \"{}\": {}", var1, var5, this.func_194059_a(var5), var6);
         }

         if (var6 > 0) {
            var3 = var5 + 1;
         } else {
            var4 = var5;
         }
      }

      if (var3 >= 0 && var3 < var2) {
         var5 = var3;
         var4 = var2;

         while(var3 < var4) {
            var6 = var3 + (var4 - var3) / 2;
            int var7 = this.func_194056_a(var1, var6);
            if (field_194062_b) {
               field_194064_d.debug("comparing upper \"{}\" with {} \"{}\": {}", var1, var6, this.func_194059_a(var6), var7);
            }

            if (var7 >= 0) {
               var3 = var6 + 1;
            } else {
               var4 = var6;
            }
         }

         var6 = var3;
         IntOpenHashSet var14 = new IntOpenHashSet();

         for(int var8 = var5; var8 < var6; ++var8) {
            var14.add(this.field_194067_g.getInt(var8));
         }

         int[] var15 = var14.toIntArray();
         java.util.Arrays.sort(var15);
         LinkedHashSet var9 = Sets.newLinkedHashSet();
         int[] var10 = var15;
         int var11 = var15.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            int var13 = var10[var12];
            var9.add(this.field_194061_a.get(var13));
         }

         return Lists.newArrayList(var9);
      } else {
         return Collections.emptyList();
      }
   }
}
