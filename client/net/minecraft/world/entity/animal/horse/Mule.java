package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Mule extends AbstractChestedHorse {
   public Mule(EntityType<? extends Mule> var1, Level var2) {
      super(var1, var2);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.MULE_AMBIENT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.MULE_ANGRY;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.MULE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.MULE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.MULE_HURT;
   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.MULE_JUMP, 0.4F, 1.0F);
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (AgeableMob)EntityType.MULE.create(var1);
   }
}
