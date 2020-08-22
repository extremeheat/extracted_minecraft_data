package net.minecraft.world.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Skeleton extends AbstractSkeleton {
   public Skeleton(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.SKELETON_STEP;
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      Entity var4 = var1.getEntity();
      if (var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if (var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            this.spawnAtLocation(Items.SKELETON_SKULL);
         }
      }

   }
}
