package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public class Donkey extends AbstractChestedHorse {
   public Donkey(EntityType<? extends Donkey> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.DONKEY_AMBIENT;
   }

   @Override
   protected SoundEvent getAngrySound() {
      return SoundEvents.DONKEY_ANGRY;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.DONKEY_DEATH;
   }

   @Nullable
   @Override
   protected SoundEvent getEatingSound() {
      return SoundEvents.DONKEY_EAT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.DONKEY_HURT;
   }

   @Override
   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else {
         return !(var1 instanceof Donkey) && !(var1 instanceof Horse) ? false : this.canParent() && ((AbstractHorse)var1).canParent();
      }
   }

   @Override
   protected void playJumpSound() {
      this.playSound(SoundEvents.DONKEY_JUMP, 0.4F, 1.0F);
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      EntityType var3 = var2 instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorse var4 = (AbstractHorse)var3.create(var1);
      if (var4 != null) {
         this.setOffspringAttributes(var2, var4);
      }

      return var4;
   }
}
