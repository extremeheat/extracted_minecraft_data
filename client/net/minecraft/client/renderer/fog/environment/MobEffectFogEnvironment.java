package net.minecraft.client.renderer.fog.environment;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public abstract class MobEffectFogEnvironment extends FogEnvironment {
   public MobEffectFogEnvironment() {
      super();
   }

   public abstract Holder<MobEffect> getMobEffect();

   public boolean providesColor() {
      return false;
   }

   public boolean modifiesDarkness() {
      return true;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      boolean var10000;
      if (var2 instanceof LivingEntity var3) {
         if (var3.hasEffect(this.getMobEffect())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }
}
