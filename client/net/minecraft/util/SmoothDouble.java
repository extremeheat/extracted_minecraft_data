package net.minecraft.util;

public class SmoothDouble {
   private double targetValue;
   private double remainingValue;
   private double lastAmount;

   public SmoothDouble() {
      super();
   }

   public double getNewDeltaValue(double var1, double var3) {
      this.targetValue += var1;
      double var5 = this.targetValue - this.remainingValue;
      double var7 = Mth.lerp(0.5D, this.lastAmount, var5);
      double var9 = Math.signum(var5);
      if (var9 * var5 > var9 * this.lastAmount) {
         var5 = var7;
      }

      this.lastAmount = var7;
      this.remainingValue += var5 * var3;
      return var5 * var3;
   }

   public void reset() {
      this.targetValue = 0.0D;
      this.remainingValue = 0.0D;
      this.lastAmount = 0.0D;
   }
}
