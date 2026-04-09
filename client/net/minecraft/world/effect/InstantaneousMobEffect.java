package net.minecraft.world.effect;

public class InstantaneousMobEffect extends MobEffect {
   public InstantaneousMobEffect(final MobEffectCategory category, final int color) {
      super(category, color);
   }

   public boolean isInstantaneous() {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(final int remainingDuration, final int amplification) {
      return remainingDuration >= 1;
   }
}
