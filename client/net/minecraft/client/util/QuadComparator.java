package net.minecraft.client.util;

import java.util.Comparator;

public class QuadComparator implements Comparator {
   private float field_147630_a;
   private float field_147628_b;
   private float field_147629_c;
   private int[] field_147627_d;

   public QuadComparator(int[] var1, float var2, float var3, float var4) {
      super();
      this.field_147627_d = var1;
      this.field_147630_a = var2;
      this.field_147628_b = var3;
      this.field_147629_c = var4;
   }

   public int compare(Integer var1, Integer var2) {
      float var3 = Float.intBitsToFloat(this.field_147627_d[var1]) - this.field_147630_a;
      float var4 = Float.intBitsToFloat(this.field_147627_d[var1 + 1]) - this.field_147628_b;
      float var5 = Float.intBitsToFloat(this.field_147627_d[var1 + 2]) - this.field_147629_c;
      float var6 = Float.intBitsToFloat(this.field_147627_d[var1 + 8]) - this.field_147630_a;
      float var7 = Float.intBitsToFloat(this.field_147627_d[var1 + 9]) - this.field_147628_b;
      float var8 = Float.intBitsToFloat(this.field_147627_d[var1 + 10]) - this.field_147629_c;
      float var9 = Float.intBitsToFloat(this.field_147627_d[var1 + 16]) - this.field_147630_a;
      float var10 = Float.intBitsToFloat(this.field_147627_d[var1 + 17]) - this.field_147628_b;
      float var11 = Float.intBitsToFloat(this.field_147627_d[var1 + 18]) - this.field_147629_c;
      float var12 = Float.intBitsToFloat(this.field_147627_d[var1 + 24]) - this.field_147630_a;
      float var13 = Float.intBitsToFloat(this.field_147627_d[var1 + 25]) - this.field_147628_b;
      float var14 = Float.intBitsToFloat(this.field_147627_d[var1 + 26]) - this.field_147629_c;
      float var15 = Float.intBitsToFloat(this.field_147627_d[var2]) - this.field_147630_a;
      float var16 = Float.intBitsToFloat(this.field_147627_d[var2 + 1]) - this.field_147628_b;
      float var17 = Float.intBitsToFloat(this.field_147627_d[var2 + 2]) - this.field_147629_c;
      float var18 = Float.intBitsToFloat(this.field_147627_d[var2 + 8]) - this.field_147630_a;
      float var19 = Float.intBitsToFloat(this.field_147627_d[var2 + 9]) - this.field_147628_b;
      float var20 = Float.intBitsToFloat(this.field_147627_d[var2 + 10]) - this.field_147629_c;
      float var21 = Float.intBitsToFloat(this.field_147627_d[var2 + 16]) - this.field_147630_a;
      float var22 = Float.intBitsToFloat(this.field_147627_d[var2 + 17]) - this.field_147628_b;
      float var23 = Float.intBitsToFloat(this.field_147627_d[var2 + 18]) - this.field_147629_c;
      float var24 = Float.intBitsToFloat(this.field_147627_d[var2 + 24]) - this.field_147630_a;
      float var25 = Float.intBitsToFloat(this.field_147627_d[var2 + 25]) - this.field_147628_b;
      float var26 = Float.intBitsToFloat(this.field_147627_d[var2 + 26]) - this.field_147629_c;
      float var27 = (var3 + var6 + var9 + var12) * 0.25F;
      float var28 = (var4 + var7 + var10 + var13) * 0.25F;
      float var29 = (var5 + var8 + var11 + var14) * 0.25F;
      float var30 = (var15 + var18 + var21 + var24) * 0.25F;
      float var31 = (var16 + var19 + var22 + var25) * 0.25F;
      float var32 = (var17 + var20 + var23 + var26) * 0.25F;
      float var33 = var27 * var27 + var28 * var28 + var29 * var29;
      float var34 = var30 * var30 + var31 * var31 + var32 * var32;
      return Float.compare(var34, var33);
   }
}
