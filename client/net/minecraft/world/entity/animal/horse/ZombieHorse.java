package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ZombieHorse extends AbstractHorse {
   public ZombieHorse(EntityType<? extends ZombieHorse> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 15.0D).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224D);
   }

   protected void randomizeAttributes() {
      this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      super.getHurtSound(var1);
      return SoundEvents.ZOMBIE_HORSE_HURT;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (AgeableMob)EntityType.ZOMBIE_HORSE.create(var1);
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!this.isTamed()) {
         return InteractionResult.PASS;
      } else if (this.isBaby()) {
         return super.mobInteract(var1, var2);
      } else if (var1.isSecondaryUseActive()) {
         this.openInventory(var1);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (this.isVehicle()) {
         return super.mobInteract(var1, var2);
      } else {
         if (!var3.isEmpty()) {
            if (var3.is(Items.SADDLE) && !this.isSaddled()) {
               this.openInventory(var1);
               return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
            if (var4.consumesAction()) {
               return var4;
            }
         }

         this.doPlayerRide(var1);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      }
   }

   protected void addBehaviourGoals() {
   }
}
