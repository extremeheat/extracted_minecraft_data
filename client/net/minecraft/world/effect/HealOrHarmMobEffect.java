package net.minecraft.world.effect;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

class HealOrHarmMobEffect extends InstantenousMobEffect {
   private final boolean isHarm;

   public HealOrHarmMobEffect(MobEffectCategory var1, int var2, boolean var3) {
      super(var1, var2);
      this.isHarm = var3;
   }

   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (this.isHarm == var2.isInvertedHealAndHarm()) {
         var2.heal((float)Math.max(4 << var3, 0));
      } else {
         var2.hurtServer(var1, var2.damageSources().magic(), (float)(6 << var3));
      }

      return true;
   }

   public void applyInstantenousEffect(ServerLevel var1, @Nullable Entity var2, @Nullable Entity var3, LivingEntity var4, int var5, double var6) {
      if (this.isHarm == var4.isInvertedHealAndHarm()) {
         int var8 = (int)(var6 * (double)(4 << var5) + 0.5);
         var4.heal((float)var8);
      } else {
         int var9 = (int)(var6 * (double)(6 << var5) + 0.5);
         if (var2 == null) {
            var4.hurtServer(var1, var4.damageSources().magic(), (float)var9);
         } else {
            var4.hurtServer(var1, var4.damageSources().indirectMagic(var2, var3), (float)var9);
         }
      }

   }
}
