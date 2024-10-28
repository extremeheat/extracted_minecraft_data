package net.minecraft.world.effect;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

class HealOrHarmMobEffect extends InstantenousMobEffect {
   private final boolean isHarm;

   public HealOrHarmMobEffect(MobEffectCategory var1, int var2, boolean var3) {
      super(var1, var2);
      this.isHarm = var3;
   }

   public boolean applyEffectTick(LivingEntity var1, int var2) {
      if (this.isHarm == var1.isInvertedHealAndHarm()) {
         var1.heal((float)Math.max(4 << var2, 0));
      } else {
         var1.hurt(var1.damageSources().magic(), (float)(6 << var2));
      }

      return true;
   }

   public void applyInstantenousEffect(@Nullable Entity var1, @Nullable Entity var2, LivingEntity var3, int var4, double var5) {
      int var7;
      if (this.isHarm == var3.isInvertedHealAndHarm()) {
         var7 = (int)(var5 * (double)(4 << var4) + 0.5);
         var3.heal((float)var7);
      } else {
         var7 = (int)(var5 * (double)(6 << var4) + 0.5);
         if (var1 == null) {
            var3.hurt(var3.damageSources().magic(), (float)var7);
         } else {
            var3.hurt(var3.damageSources().indirectMagic(var1, var2), (float)var7);
         }
      }

   }
}
