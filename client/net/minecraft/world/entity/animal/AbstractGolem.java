package net.minecraft.world.entity.animal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class AbstractGolem extends PathfinderMob {
   protected AbstractGolem(EntityType<? extends AbstractGolem> var1, Level var2) {
      super(var1, var2);
   }

   protected @Nullable SoundEvent getAmbientSound() {
      return null;
   }

   protected @Nullable SoundEvent getHurtSound(DamageSource var1) {
      return null;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return null;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double var1) {
      return false;
   }
}
