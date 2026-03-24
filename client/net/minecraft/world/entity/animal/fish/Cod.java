package net.minecraft.world.entity.animal.fish;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Cod extends AbstractSchoolingFish {
   public Cod(final EntityType<? extends Cod> type, final Level level) {
      super(type, level);
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.COD_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COD_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.COD_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.COD_FLOP;
   }
}
