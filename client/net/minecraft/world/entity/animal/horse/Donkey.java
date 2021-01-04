package net.minecraft.world.entity.animal.horse;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public class Donkey extends AbstractChestedHorse {
   public Donkey(EntityType<? extends Donkey> var1, Level var2) {
      super(var1, var2);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.DONKEY_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.DONKEY_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      super.getHurtSound(var1);
      return SoundEvents.DONKEY_HURT;
   }

   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (!(var1 instanceof Donkey) && !(var1 instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)var1).canParent();
      }
   }

   public AgableMob getBreedOffspring(AgableMob var1) {
      EntityType var2 = var1 instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorse var3 = (AbstractHorse)var2.create(this.level);
      this.setOffspringAttributes(var1, var3);
      return var3;
   }
}
