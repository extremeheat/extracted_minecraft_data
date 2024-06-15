package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import org.joml.Matrix3f;

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
   private static final SymmetricGroup3[][] cayleyTable = Util.make(new SymmetricGroup3[values().length][values().length], var0 -> {
      for (SymmetricGroup3 var4 : values()) {
         for (SymmetricGroup3 var8 : values()) {
            int[] var9 = new int[3];

            for (int var10 = 0; var10 < 3; var10++) {
               var9[var10] = var4.permutation[var8.permutation[var10]];
            }

            SymmetricGroup3 var11 = Arrays.stream(values()).filter(var1 -> Arrays.equals(var1.permutation, var9)).findFirst().get();
            var0[var4.ordinal()][var8.ordinal()] = var11;
         }
      }
   });

   private SymmetricGroup3(final int nullxx, final int nullxxx, final int nullxxxx) {
      this.permutation = new int[]{nullxx, nullxxx, nullxxxx};
      this.transformation = new Matrix3f();
      this.transformation.set(this.permutation(0), 0, 1.0F);
      this.transformation.set(this.permutation(1), 1, 1.0F);
      this.transformation.set(this.permutation(2), 2, 1.0F);
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
}
