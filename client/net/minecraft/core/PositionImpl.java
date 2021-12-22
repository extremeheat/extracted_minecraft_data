package net.minecraft.core;

public class PositionImpl implements Position {
   // $FF: renamed from: x double
   protected final double field_454;
   // $FF: renamed from: y double
   protected final double field_455;
   // $FF: renamed from: z double
   protected final double field_456;

   public PositionImpl(double var1, double var3, double var5) {
      super();
      this.field_454 = var1;
      this.field_455 = var3;
      this.field_456 = var5;
   }

   // $FF: renamed from: x () double
   public double method_2() {
      return this.field_454;
   }

   // $FF: renamed from: y () double
   public double method_3() {
      return this.field_455;
   }

   // $FF: renamed from: z () double
   public double method_4() {
      return this.field_456;
   }
}
