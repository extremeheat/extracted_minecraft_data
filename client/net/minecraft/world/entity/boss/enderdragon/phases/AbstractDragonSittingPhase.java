package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public abstract class AbstractDragonSittingPhase extends AbstractDragonPhaseInstance {
   public AbstractDragonSittingPhase(final EnderDragon dragon) {
      super(dragon);
   }

   public boolean isSitting() {
      return true;
   }

   public float onHurt(final DamageSource source, final float damage) {
      if (source.is(DamageTypeTags.CANNOT_HURT_PERCHING_ENDER_DRAGON)) {
         if (source.getDirectEntity() != null) {
            source.getDirectEntity().igniteForSeconds(1.0F);
         }

         return 0.0F;
      } else {
         return super.onHurt(source, damage);
      }
   }
}
