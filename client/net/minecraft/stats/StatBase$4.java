package net.minecraft.stats;

final class StatBase$4 implements IStatType {
   StatBase$4() {
      super();
   }

   @Override
   public String func_75843_a(int var1) {
      return StatBase.access$100().format((double)var1 * 0.1);
   }
}
