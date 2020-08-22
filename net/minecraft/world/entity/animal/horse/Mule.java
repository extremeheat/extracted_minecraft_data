package net.minecraft.world.entity.animal.horse;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Mule extends AbstractChestedHorse {
   public Mule(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.MULE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.MULE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      super.getHurtSound(var1);
      return SoundEvents.MULE_HURT;
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public AgableMob getBreedOffspring(AgableMob var1) {
      return (AgableMob)EntityType.MULE.create(this.level);
   }
}
