package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.AbstractArrow;

public abstract class AbstractDragonSittingPhase extends AbstractDragonPhaseInstance {
   public AbstractDragonSittingPhase(EnderDragon var1) {
      super(var1);
   }

   public boolean isSitting() {
      return true;
   }

   public float onHurt(DamageSource var1, float var2) {
      if (var1.getDirectEntity() instanceof AbstractArrow) {
         var1.getDirectEntity().setSecondsOnFire(1);
         return 0.0F;
      } else {
         return super.onHurt(var1, var2);
      }
   }
}
