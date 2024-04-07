package net.minecraft.util;

import net.minecraft.core.Direction;

public class SegmentedAnglePrecision {
   private final int mask;
   private final int precision;
   private final float degreeToAngle;
   private final float angleToDegree;

   public SegmentedAnglePrecision(int var1) {
      super();
      if (var1 < 2) {
         throw new IllegalArgumentException("Precision cannot be less than 2 bits");
      } else if (var1 > 30) {
         throw new IllegalArgumentException("Precision cannot be greater than 30 bits");
      } else {
         int var2 = 1 << var1;
         this.mask = var2 - 1;
         this.precision = var1;
         this.degreeToAngle = (float)var2 / 360.0F;
         this.angleToDegree = 360.0F / (float)var2;
      }
   }

   public boolean isSameAxis(int var1, int var2) {
      int var3 = this.getMask() >> 1;
      return (var1 & var3) == (var2 & var3);
   }

   public int fromDirection(Direction var1) {
      if (var1.getAxis().isVertical()) {
         return 0;
      } else {
         int var2 = var1.get2DDataValue();
         return var2 << this.precision - 2;
      }
   }

   public int fromDegreesWithTurns(float var1) {
      return Math.round(var1 * this.degreeToAngle);
   }

   public int fromDegrees(float var1) {
      return this.normalize(this.fromDegreesWithTurns(var1));
   }

   public float toDegreesWithTurns(int var1) {
      return (float)var1 * this.angleToDegree;
   }

   public float toDegrees(int var1) {
      float var2 = this.toDegreesWithTurns(this.normalize(var1));
      return var2 >= 180.0F ? var2 - 360.0F : var2;
   }

   public int normalize(int var1) {
      return var1 & this.mask;
   }

   public int getMask() {
      return this.mask;
   }
}
