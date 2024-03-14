package net.minecraft.client.searchtree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.slf4j.Logger;

public class SuffixArray<T> {
   private static final boolean DEBUG_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
   private static final boolean DEBUG_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int END_OF_TEXT_MARKER = -1;
   private static final int END_OF_DATA = -2;
   protected final List<T> list = Lists.newArrayList();
   private final IntList chars = new IntArrayList();
   private final IntList wordStarts = new IntArrayList();
   private IntList suffixToT = new IntArrayList();
   private IntList offsets = new IntArrayList();
   private int maxStringLength;

   public SuffixArray() {
      super();
   }

   public void add(T var1, String var2) {
      this.maxStringLength = Math.max(this.maxStringLength, var2.length());
      int var3 = this.list.size();
      this.list.add((T)var1);
      this.wordStarts.add(this.chars.size());

      for(int var4 = 0; var4 < var2.length(); ++var4) {
         this.suffixToT.add(var3);
         this.offsets.add(var4);
         this.chars.add(var2.charAt(var4));
      }

      this.suffixToT.add(var3);
      this.offsets.add(var2.length());
      this.chars.add(-1);
   }

   public void generate() {
      int var1 = this.chars.size();
      int[] var2 = new int[var1];
      int[] var3 = new int[var1];
      int[] var4 = new int[var1];
      int[] var5 = new int[var1];
      IntComparator var6 = (var2x, var3x) -> var3[var2x] == var3[var3x] ? Integer.compare(var4[var2x], var4[var3x]) : Integer.compare(var3[var2x], var3[var3x]);
      Swapper var7 = (var3x, var4x) -> {
         if (var3x != var4x) {
            int var5xx = var3[var3x];
            var3[var3x] = var3[var4x];
            var3[var4x] = var5xx;
            var5xx = var4[var3x];
            var4[var3x] = var4[var4x];
            var4[var4x] = var5xx;
            var5xx = var5[var3x];
            var5[var3x] = var5[var4x];
            var5[var4x] = var5xx;
         }
      };

      for(int var8 = 0; var8 < var1; ++var8) {
         var2[var8] = this.chars.getInt(var8);
      }

      int var14 = 1;

      for(int var9 = Math.min(var1, this.maxStringLength); var14 * 2 < var9; var14 *= 2) {
         for(int var10 = 0; var10 < var1; var5[var10] = var10++) {
            var3[var10] = var2[var10];
            var4[var10] = var10 + var14 < var1 ? var2[var10 + var14] : -2;
         }

         Arrays.quickSort(0, var1, var6, var7);

         for(int var15 = 0; var15 < var1; ++var15) {
            if (var15 > 0 && var3[var15] == var3[var15 - 1] && var4[var15] == var4[var15 - 1]) {
               var2[var5[var15]] = var2[var5[var15 - 1]];
            } else {
               var2[var5[var15]] = var15;
            }
         }
      }

      IntList var16 = this.suffixToT;
      IntList var11 = this.offsets;
      this.suffixToT = new IntArrayList(var16.size());
      this.offsets = new IntArrayList(var11.size());

      for(int var12 = 0; var12 < var1; ++var12) {
         int var13 = var5[var12];
         this.suffixToT.add(var16.getInt(var13));
         this.offsets.add(var11.getInt(var13));
      }

      if (DEBUG_ARRAY) {
         this.print();
      }
   }

   private void print() {
      for(int var1 = 0; var1 < this.suffixToT.size(); ++var1) {
         LOGGER.debug("{} {}", var1, this.getString(var1));
      }

      LOGGER.debug("");
   }

   private String getString(int var1) {
      int var2 = this.offsets.getInt(var1);
      int var3 = this.wordStarts.getInt(this.suffixToT.getInt(var1));
      StringBuilder var4 = new StringBuilder();

      for(int var5 = 0; var3 + var5 < this.chars.size(); ++var5) {
         if (var5 == var2) {
            var4.append('^');
         }

         int var6 = this.chars.getInt(var3 + var5);
         if (var6 == -1) {
            break;
         }

         var4.append((char)var6);
      }

      return var4.toString();
   }

   private int compare(String var1, int var2) {
      int var3 = this.wordStarts.getInt(this.suffixToT.getInt(var2));
      int var4 = this.offsets.getInt(var2);

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         int var6 = this.chars.getInt(var3 + var4 + var5);
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

   public List<T> search(String var1) {
      int var2 = this.suffixToT.size();
      int var3 = 0;
      int var4 = var2;

      while(var3 < var4) {
         int var5 = var3 + (var4 - var3) / 2;
         int var6 = this.compare(var1, var5);
         if (DEBUG_COMPARISONS) {
            LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", new Object[]{var1, var5, this.getString(var5), var6});
         }

         if (var6 > 0) {
            var3 = var5 + 1;
         } else {
            var4 = var5;
         }
      }

      if (var3 >= 0 && var3 < var2) {
         int var15 = var3;
         var4 = var2;

         while(var3 < var4) {
            int var16 = var3 + (var4 - var3) / 2;
            int var7 = this.compare(var1, var16);
            if (DEBUG_COMPARISONS) {
               LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", new Object[]{var1, var16, this.getString(var16), var7});
            }

            if (var7 >= 0) {
               var3 = var16 + 1;
            } else {
               var4 = var16;
            }
         }

         int var17 = var3;
         IntOpenHashSet var18 = new IntOpenHashSet();

         for(int var8 = var15; var8 < var17; ++var8) {
            var18.add(this.suffixToT.getInt(var8));
         }

         int[] var19 = var18.toIntArray();
         java.util.Arrays.sort(var19);
         LinkedHashSet var9 = Sets.newLinkedHashSet();

         for(int var13 : var19) {
            var9.add(this.list.get(var13));
         }

         return Lists.newArrayList(var9);
      } else {
         return Collections.emptyList();
      }
   }
}
