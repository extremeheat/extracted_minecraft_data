package net.minecraft.stats;

final class StatBase$2 implements IStatType {
   StatBase$2() {
      super();
   }

   @Override
   public String func_75843_a(int var1) {
      double var2 = (double)var1 / 20.0;
      double var4 = var2 / 60.0;
      double var6 = var4 / 60.0;
      double var8 = var6 / 24.0;
      double var10 = var8 / 365.0;
      if (var10 > 0.5) {
         return StatBase.access$100().format(var10) + " y";
      } else if (var8 > 0.5) {
         return StatBase.access$100().format(var8) + " d";
      } else if (var6 > 0.5) {
         return StatBase.access$100().format(var6) + " h";
      } else {
         return var4 > 0.5 ? StatBase.access$100().format(var4) + " m" : var2 + " s";
      }
   }
}
