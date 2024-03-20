package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;

public class WitherSkeleton extends AbstractSkeleton {
   public WitherSkeleton(EntityType<? extends WitherSkeleton> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
   }

   @Override
   protected void registerGoals() {
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
      super.registerGoals();
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_SKELETON_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WITHER_SKELETON_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_SKELETON_DEATH;
   }

   @Override
   SoundEvent getStepSound() {
      return SoundEvents.WITHER_SKELETON_STEP;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      Entity var4 = var1.getEntity();
      if (var4 instanceof Creeper var5 && var5.canDropMobsSkull()) {
         var5.increaseDroppedSkulls();
         this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
      }
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
   }

   @Override
   protected void populateDefaultEquipmentEnchantments(RandomSource var1, DifficultyInstance var2) {
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      SpawnGroupData var5 = super.finalizeSpawn(var1, var2, var3, var4);
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
      this.reassessWeaponGoal();
      return var5;
   }

   @Override
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

   @Override
   protected AbstractArrow getArrow(ItemStack var1, float var2) {
      AbstractArrow var3 = super.getArrow(var1, var2);
      var3.igniteForSeconds(100);
      return var3;
   }

   @Override
   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.is(MobEffects.WITHER) ? false : super.canBeAffected(var1);
   }
}