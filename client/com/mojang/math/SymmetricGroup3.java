package com.mojang.math;

import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
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
      SymmetricGroup3[] values = values();
      SymmetricGroup3[][] table = new SymmetricGroup3[values.length][values.length];

      for(SymmetricGroup3 first : values) {
         for(SymmetricGroup3 second : values) {
            int p0 = first.permute(second.p0);
            int p1 = first.permute(second.p1);
            int p2 = first.permute(second.p2);
            SymmetricGroup3 result = (SymmetricGroup3)Arrays.stream(values).filter((p) -> p.p0 == p0 && p.p1 == p1 && p.p2 == p2).findFirst().get();
            table[first.ordinal()][second.ordinal()] = result;
         }
      }

      return table;
   });
   private static final SymmetricGroup3[] INVERSE_TABLE = (SymmetricGroup3[])Util.make(() -> {
      SymmetricGroup3[] values = values();
      return (SymmetricGroup3[])Arrays.stream(values).map((f) -> (SymmetricGroup3)Arrays.stream(values()).filter((s) -> f.compose(s) == P123).findAny().get()).toArray((x$0) -> new SymmetricGroup3[x$0]);
   });

   private SymmetricGroup3(final int p0, final int p1, final int p2) {
      this.p0 = p0;
      this.p1 = p1;
      this.p2 = p2;
      this.transformation = (new Matrix3f()).zero().set(this.permute(0), 0, 1.0F).set(this.permute(1), 1, 1.0F).set(this.permute(2), 2, 1.0F);
   }

   public SymmetricGroup3 compose(final SymmetricGroup3 that) {
      return CAYLEY_TABLE[this.ordinal()][that.ordinal()];
   }

   public SymmetricGroup3 inverse() {
      return INVERSE_TABLE[this.ordinal()];
   }

   public int permute(final int i) {
      int var10000;
      switch (i) {
         case 0 -> var10000 = this.p0;
         case 1 -> var10000 = this.p1;
         case 2 -> var10000 = this.p2;
         default -> throw new IllegalArgumentException("Must be 0, 1 or 2, but got " + i);
      }

      return var10000;
   }

   public Direction.Axis permuteAxis(final Direction.Axis axis) {
      return Direction.Axis.VALUES[this.permute(axis.ordinal())];
   }

   public Vector3f permuteVector(final Vector3f v) {
      float v0 = v.get(this.p0);
      float v1 = v.get(this.p1);
      float v2 = v.get(this.p2);
      return v.set(v0, v1, v2);
   }

   public Vector3i permuteVector(final Vector3i v) {
      int v0 = v.get(this.p0);
      int v1 = v.get(this.p1);
      int v2 = v.get(this.p2);
      return v.set(v0, v1, v2);
   }

   public Matrix3fc transformation() {
      return this.transformation;
   }

   // $FF: synthetic method
   private static SymmetricGroup3[] $values() {
      return new SymmetricGroup3[]{P123, P213, P132, P312, P231, P321};
   }
}
