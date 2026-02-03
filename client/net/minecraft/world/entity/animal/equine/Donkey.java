package net.minecraft.world.entity.animal.equine;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class Donkey extends AbstractChestedHorse {
   public Donkey(final EntityType<? extends Donkey> type, final Level level) {
      super(type, level);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.DONKEY_AMBIENT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.DONKEY_ANGRY;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.DONKEY_DEATH;
   }

   protected SoundEvent getEatingSound() {
      return SoundEvents.DONKEY_EAT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.DONKEY_HURT;
   }

   public boolean canMate(final Animal partner) {
      if (partner == this) {
         return false;
      } else if (!(partner instanceof Donkey) && !(partner instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)partner).canParent();
      }
   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.DONKEY_JUMP, 0.4F, 1.0F);
   }

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      EntityType<? extends AbstractHorse> babyType = partner instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorse baby = babyType.create(level, EntitySpawnReason.BREEDING);
      if (baby != null) {
         this.setOffspringAttributes(partner, baby);
      }

      return baby;
   }
}
