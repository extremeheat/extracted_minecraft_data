package net.minecraft.client.util;

import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import net.minecraft.client.renderer.RenderList;

public class RenderDistanceSorter implements Comparator {
   int field_152632_a;
   int field_152633_b;

   public RenderDistanceSorter(int var1, int var2) {
      super();
      this.field_152632_a = var1;
      this.field_152633_b = var2;
   }

   public int compare(RenderList var1, RenderList var2) {
      int var3 = var1.field_78429_a - this.field_152632_a;
      int var4 = var1.field_78428_c - this.field_152633_b;
      int var5 = var2.field_78429_a - this.field_152632_a;
      int var6 = var2.field_78428_c - this.field_152633_b;
      int var7 = var3 * var3 + var4 * var4;
      int var8 = var5 * var5 + var6 * var6;
      return ComparisonChain.start().compare(var8, var7).result();
   }
}
