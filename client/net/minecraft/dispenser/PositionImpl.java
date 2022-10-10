package net.minecraft.dispenser;

public class PositionImpl implements IPosition {
   protected final double field_82630_a;
   protected final double field_82628_b;
   protected final double field_82629_c;

   public PositionImpl(double var1, double var3, double var5) {
      super();
      this.field_82630_a = var1;
      this.field_82628_b = var3;
      this.field_82629_c = var5;
   }

   public double func_82615_a() {
      return this.field_82630_a;
   }

   public double func_82617_b() {
      return this.field_82628_b;
   }

   public double func_82616_c() {
      return this.field_82629_c;
   }
}
