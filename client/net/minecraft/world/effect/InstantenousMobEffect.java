package net.minecraft.world.effect;

public class InstantenousMobEffect extends MobEffect {
   public InstantenousMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public boolean isInstantenous() {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return var1 >= 1;
   }
}
