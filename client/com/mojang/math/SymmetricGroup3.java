package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;

public enum SymmetricGroup3 {
   P123(0, 1, 2),
   P213(1, 0, 2),
   P132(0, 2, 1),
   P231(1, 2, 0),
   P312(2, 0, 1),
   P321(2, 1, 0);

   private final int[] permutation;
   private final Matrix3f transformation;
   private static final int ORDER = 3;
   private static final SymmetricGroup3[][] cayleyTable = (SymmetricGroup3[][])Util.make(new SymmetricGroup3[values().length][values().length], (var0) -> {
      SymmetricGroup3[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SymmetricGroup3 var4 = var1[var3];
         SymmetricGroup3[] var5 = values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            SymmetricGroup3 var8 = var5[var7];
            int[] var9 = new int[3];

            for(int var10 = 0; var10 < 3; ++var10) {
               var9[var10] = var4.permutation[var8.permutation[var10]];
            }

            SymmetricGroup3 var11 = (SymmetricGroup3)Arrays.stream(values()).filter((var1x) -> {
               return Arrays.equals(var1x.permutation, var9);
            }).findFirst().get();
            var0[var4.ordinal()][var8.ordinal()] = var11;
         }
      }

   });

   private SymmetricGroup3(int var3, int var4, int var5) {
      this.permutation = new int[]{var3, var4, var5};
      this.transformation = new Matrix3f();
      this.transformation.set(0, this.permutation(0), 1.0F);
      this.transformation.set(1, this.permutation(1), 1.0F);
      this.transformation.set(2, this.permutation(2), 1.0F);
   }

   public SymmetricGroup3 compose(SymmetricGroup3 var1) {
      return cayleyTable[this.ordinal()][var1.ordinal()];
   }

   public int permutation(int var1) {
      return this.permutation[var1];
   }

   public Matrix3f transformation() {
      return this.transformation;
   }

   // $FF: synthetic method
   private static SymmetricGroup3[] $values() {
      return new SymmetricGroup3[]{P123, P213, P132, P231, P312, P321};
   }
}
