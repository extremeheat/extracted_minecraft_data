package net.minecraft.world.entity.animal.camel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CamelHusk extends Camel {
   public CamelHusk(EntityType<? extends Camel> var1, Level var2) {
      super(var1, var2);
   }

   public boolean removeWhenFarAway(double var1) {
      return true;
   }

   public boolean isMobControlled() {
      return this.getFirstPassenger() instanceof Mob;
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      this.setPersistenceRequired();
      return super.interact(var1, var2);
   }

   public boolean canBeLeashed() {
      return !this.isMobControlled();
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.CAMEL_HUSK_FOOD);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.CAMEL_HUSK_AMBIENT;
   }

   public boolean canMate(Animal var1) {
      return false;
   }

   public @Nullable Camel getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   public boolean canFallInLove() {
      return false;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CAMEL_HUSK_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CAMEL_HUSK_HURT;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      if (var2.is(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS)) {
         this.playSound(SoundEvents.CAMEL_HUSK_STEP_SAND, 0.4F, 1.0F);
      } else {
         this.playSound(SoundEvents.CAMEL_HUSK_STEP, 0.4F, 1.0F);
      }

   }

   protected SoundEvent getDashingSound() {
      return SoundEvents.CAMEL_HUSK_DASH;
   }

   protected SoundEvent getDashReadySound() {
      return SoundEvents.CAMEL_HUSK_DASH_READY;
   }

   protected SoundEvent getEatingSound() {
      return SoundEvents.CAMEL_HUSK_EAT;
   }

   protected SoundEvent getStandUpSound() {
      return SoundEvents.CAMEL_HUSK_STAND;
   }

   protected SoundEvent getSitDownSound() {
      return SoundEvents.CAMEL_HUSK_SIT;
   }

   protected Holder.Reference<SoundEvent> getSaddleSound() {
      return SoundEvents.CAMEL_HUSK_SADDLE;
   }

   public float chargeSpeedModifier() {
      return 4.0F;
   }

   // $FF: synthetic method
   public @Nullable AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }
}
