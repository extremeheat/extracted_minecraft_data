package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public class ZombieHorse extends AbstractHorse {
   public ZombieHorse(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
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
   public AgableMob getBreedOffspring(AgableMob var1) {
      return (AgableMob)EntityType.ZOMBIE_HORSE.create(this.level);
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() instanceof SpawnEggItem) {
         return super.mobInteract(var1, var2);
      } else if (!this.isTamed()) {
         return false;
      } else if (this.isBaby()) {
         return super.mobInteract(var1, var2);
      } else if (var1.isSecondaryUseActive()) {
         this.openInventory(var1);
         return true;
      } else if (this.isVehicle()) {
         return super.mobInteract(var1, var2);
      } else {
         if (!var3.isEmpty()) {
            if (!this.isSaddled() && var3.getItem() == Items.SADDLE) {
               this.openInventory(var1);
               return true;
            }

            if (var3.interactEnemy(var1, this, var2)) {
               return true;
            }
         }

         this.doPlayerRide(var1);
         return true;
      }
   }

   protected void addBehaviourGoals() {
   }
}
