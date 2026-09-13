package net.minecraft.stats;

final class StatBase$3 implements IStatType {
   StatBase$3() {
      super();
   }

   @Override
   public String func_75843_a(int var1) {
      double var2 = (double)var1 / 100.0;
      double var4 = var2 / 1000.0;
      if (var4 > 0.5) {
         return StatBase.access$100().format(var4) + " km";
      } else {
         return var2 > 0.5 ? StatBase.access$100().format(var2) + " m" : var1 + " cm";
      }
   }
}
