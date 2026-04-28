package net.minecraft.world.entity.monster.skeleton;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import org.jspecify.annotations.Nullable;

public class WitherSkeleton extends AbstractSkeleton {
   public WitherSkeleton(final EntityType<? extends WitherSkeleton> type, final Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
   }

   protected void registerGoals() {
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractPiglin.class, true));
      super.registerGoals();
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.WITHER_SKELETON_STEP;
   }

   public TagKey<Item> getPreferredWeaponType() {
      return null;
   }

   public boolean canHoldItem(final ItemStack itemStack) {
      return !itemStack.is(ItemTags.WITHER_SKELETON_DISLIKED_WEAPONS) && super.canHoldItem(itemStack);
   }

   protected void populateDefaultEquipmentSlots(final RandomSource random, final DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
   }

   protected void populateDefaultEquipmentEnchantments(final ServerLevelAccessor level, final RandomSource random, final DifficultyInstance localDifficulty) {
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      SpawnGroupData spawnGroupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
      this.reassessWeaponGoal();
      return spawnGroupData;
   }

   public boolean doHurtTarget(final ServerLevel level, final Entity target) {
      if (!super.doHurtTarget(level, target)) {
         return false;
      } else {
         if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)target;
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
         }

         return true;
      }
   }

   protected AbstractArrow getArrow(final ItemStack projectile, final float power, final @Nullable ItemStack firingWeapon) {
      AbstractArrow arrow = super.getArrow(projectile, power, firingWeapon);
      arrow.igniteForSeconds(100.0F);
      return arrow;
   }

   public boolean canBeAffected(final MobEffectInstance newEffect) {
      return newEffect.is(MobEffects.WITHER) ? false : super.canBeAffected(newEffect);
   }
}
