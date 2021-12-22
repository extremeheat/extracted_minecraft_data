package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class WitherSkeleton extends AbstractSkeleton {
   public WitherSkeleton(EntityType<? extends WitherSkeleton> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
   }

   protected void registerGoals() {
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractPiglin.class, true));
      super.registerGoals();
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.WITHER_SKELETON_STEP;
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      Entity var4 = var1.getEntity();
      if (var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if (var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
         }
      }

   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
   }

   protected void populateDefaultEquipmentEnchantments(DifficultyInstance var1) {
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      SpawnGroupData var6 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.reassessWeaponGoal();
      return var6;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 2.1F;
   }

   public boolean doHurtTarget(Entity var1) {
      if (!super.doHurtTarget(var1)) {
         return false;
      } else {
         if (var1 instanceof LivingEntity) {
            ((LivingEntity)var1).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
         }

         return true;
      }
   }

   protected AbstractArrow getArrow(ItemStack var1, float var2) {
      AbstractArrow var3 = super.getArrow(var1, var2);
      var3.setSecondsOnFire(100);
      return var3;
   }

   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(var1);
   }
}
