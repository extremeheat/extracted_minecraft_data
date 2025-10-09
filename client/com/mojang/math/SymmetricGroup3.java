package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import org.joml.Vector3i;

public enum SymmetricGroup3 {
   P123(0, 1, 2),
   P213(1, 0, 2),
   P132(0, 2, 1),
   P312(2, 0, 1),
   P231(1, 2, 0),
   P321(2, 1, 0);

   private final int p0;
   private final int p1;
   private final int p2;
   private final Matrix3fc transformation;
   private static final SymmetricGroup3[][] CAYLEY_TABLE = (SymmetricGroup3[][])Util.make(() -> {
      SymmetricGroup3[] var0 = values();
      SymmetricGroup3[][] var1 = new SymmetricGroup3[var0.length][var0.length];

      for(SymmetricGroup3 var5 : var0) {
         for(SymmetricGroup3 var9 : var0) {
            int var10 = var5.permute(var9.p0);
            int var11 = var5.permute(var9.p1);
            int var12 = var5.permute(var9.p2);
            SymmetricGroup3 var13 = (SymmetricGroup3)Arrays.stream(var0).filter((var3) -> var3.p0 == var10 && var3.p1 == var11 && var3.p2 == var12).findFirst().get();
            var1[var5.ordinal()][var9.ordinal()] = var13;
         }
      }

      return var1;
   });
   private static final SymmetricGroup3[] INVERSE_TABLE = (SymmetricGroup3[])Util.make(() -> {
      SymmetricGroup3[] var0 = values();
      return (SymmetricGroup3[])Arrays.stream(var0).map((var0x) -> (SymmetricGroup3)Arrays.stream(values()).filter((var1) -> var0x.compose(var1) == P123).findAny().get()).toArray((var0x) -> new SymmetricGroup3[var0x]);
   });

   private SymmetricGroup3(final int var3, final int var4, final int var5) {
      this.p0 = var3;
      this.p1 = var4;
      this.p2 = var5;
      this.transformation = (new Matrix3f()).zero().set(this.permute(0), 0, 1.0F).set(this.permute(1), 1, 1.0F).set(this.permute(2), 2, 1.0F);
   }

   public SymmetricGroup3 compose(SymmetricGroup3 var1) {
      return CAYLEY_TABLE[this.ordinal()][var1.ordinal()];
   }

   public SymmetricGroup3 inverse() {
      return INVERSE_TABLE[this.ordinal()];
   }

   public int permute(int var1) {
      int var10000;
      switch (var1) {
         case 0 -> var10000 = this.p0;
         case 1 -> var10000 = this.p1;
         case 2 -> var10000 = this.p2;
         default -> throw new IllegalArgumentException("Must be 0, 1 or 2, but got " + var1);
      }

      return var10000;
   }

   public Direction.Axis permuteAxis(Direction.Axis var1) {
      return Direction.Axis.VALUES[this.permute(var1.ordinal())];
   }

   public Vector3f permuteVector(Vector3f var1) {
      float var2 = var1.get(this.p0);
      float var3 = var1.get(this.p1);
      float var4 = var1.get(this.p2);
      return var1.set(var2, var3, var4);
   }

   public Vector3i permuteVector(Vector3i var1) {
      int var2 = var1.get(this.p0);
      int var3 = var1.get(this.p1);
      int var4 = var1.get(this.p2);
      return var1.set(var2, var3, var4);
   }

   public Matrix3fc transformation() {
      return this.transformation;
   }

   // $FF: synthetic method
   private static SymmetricGroup3[] $values() {
      return new SymmetricGroup3[]{P123, P213, P132, P312, P231, P321};
   }
}
