package net.minecraft.world.entity.animal.golem;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class AbstractGolem extends PathfinderMob {
   protected AbstractGolem(final EntityType<? extends AbstractGolem> type, final Level level) {
      super(type, level);
   }

   protected @Nullable SoundEvent getAmbientSound() {
      return null;
   }

   protected @Nullable SoundEvent getHurtSound(final DamageSource source) {
      return null;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return null;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(final double distSqr) {
      return false;
   }
}
