package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;

public abstract class AbstractDragonSittingPhase extends AbstractDragonPhaseInstance {
   public AbstractDragonSittingPhase(EnderDragon var1) {
      super(var1);
   }

   public boolean isSitting() {
      return true;
   }

   public float onHurt(DamageSource var1, float var2) {
      if (!(var1.getDirectEntity() instanceof AbstractArrow) && !(var1.getDirectEntity() instanceof WindCharge)) {
         return super.onHurt(var1, var2);
      } else {
         var1.getDirectEntity().igniteForSeconds(1.0F);
         return 0.0F;
      }
   }
}
