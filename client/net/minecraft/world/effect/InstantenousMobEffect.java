package net.minecraft.world.effect;

public class InstantenousMobEffect extends MobEffect {
   public InstantenousMobEffect(final MobEffectCategory category, final int color) {
      super(category, color);
   }

   public boolean isInstantenous() {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(final int remainingDuration, final int amplification) {
      return remainingDuration >= 1;
   }
}
