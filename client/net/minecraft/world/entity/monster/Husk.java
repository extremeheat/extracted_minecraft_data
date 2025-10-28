package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.camel.CamelHusk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

public class Husk extends Zombie {
   public Husk(EntityType<? extends Husk> var1, Level var2) {
      super(var1, var2);
   }

   protected boolean isSunSensitive() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.HUSK_STEP;
   }

   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      boolean var3 = super.doHurtTarget(var1, var2);
      if (var3 && this.getMainHandItem().isEmpty() && var2 instanceof LivingEntity) {
         float var4 = var1.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)var4), this);
      }

      return var3;
   }

   protected boolean convertsInWater() {
      return true;
   }

   protected void doUnderWaterConversion(ServerLevel var1) {
      this.convertToZombieType(var1, EntityType.ZOMBIE);
      if (!this.isSilent()) {
         var1.levelEvent((Entity)null, 1041, this.blockPosition(), 0);
      }

   }

   public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      float var6 = var2.getSpecialMultiplier();
      if (var3 != EntitySpawnReason.CONVERSION) {
         this.setCanPickUpLoot(var5.nextFloat() < 0.55F * var6);
      }

      if (var4 != null) {
         var4 = new HuskGroupData((Zombie.ZombieGroupData)var4);
         ((HuskGroupData)var4).triedToSpawnCamelHusk = var3 != EntitySpawnReason.NATURAL;
      }

      if (var4 instanceof HuskGroupData var7) {
         if (!var7.triedToSpawnCamelHusk) {
            BlockPos var8 = this.blockPosition();
            if (var1.noCollision(EntityType.CAMEL_HUSK.getSpawnAABB((double)var8.getX() + 0.5, (double)var8.getY(), (double)var8.getZ() + 0.5))) {
               var7.triedToSpawnCamelHusk = true;
               if (var5.nextFloat() < 0.1F) {
                  this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SPEAR));
                  CamelHusk var9 = EntityType.CAMEL_HUSK.create(this.level(), EntitySpawnReason.NATURAL);
                  if (var9 != null) {
                     var9.setPos(this.getX(), this.getY(), this.getZ());
                     var9.finalizeSpawn(var1, var2, var3, (SpawnGroupData)null);
                     this.startRiding(var9, true, true);
                     var1.addFreshEntity(var9);
                     Parched var10 = EntityType.PARCHED.create(this.level(), EntitySpawnReason.NATURAL);
                     if (var10 != null) {
                        var10.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        var10.finalizeSpawn(var1, var2, var3, (SpawnGroupData)null);
                        var10.startRiding(var9, false, false);
                        var1.addFreshEntityWithPassengers(var10);
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   public static class HuskGroupData extends Zombie.ZombieGroupData {
      public boolean triedToSpawnCamelHusk = false;

      public HuskGroupData(Zombie.ZombieGroupData var1) {
         super(var1.isBaby, var1.canSpawnJockey);
      }
   }
}
