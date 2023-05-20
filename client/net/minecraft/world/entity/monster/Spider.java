package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Spider extends Monster {
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BYTE);
   private static final float SPIDER_SPECIAL_EFFECT_CHANCE = 0.1F;

   public Spider(EntityType<? extends Spider> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new Spider.SpiderAttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new Spider.SpiderTargetGoal<>(this, Player.class));
      this.targetSelector.addGoal(3, new Spider.SpiderTargetGoal<>(this, IronGolem.class));
   }

   @Override
   public double getPassengersRidingOffset() {
      return (double)(this.getBbHeight() * 0.5F);
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new WallClimberNavigation(this, var1);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         this.setClimbing(this.horizontalCollision);
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SPIDER_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SPIDER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.SPIDER_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
   }

   @Override
   public boolean onClimbable() {
      return this.isClimbing();
   }

   @Override
   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      if (!var1.is(Blocks.COBWEB)) {
         super.makeStuckInBlock(var1, var2);
      }
   }

   @Override
   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }

   @Override
   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.getEffect() == MobEffects.POISON ? false : super.canBeAffected(var1);
   }

   public boolean isClimbing() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setClimbing(boolean var1) {
      byte var2 = this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, var2);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      RandomSource var6 = var1.getRandom();
      if (var6.nextInt(100) == 0) {
         Skeleton var7 = EntityType.SKELETON.create(this.level);
         if (var7 != null) {
            var7.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            var7.finalizeSpawn(var1, var2, var3, null, null);
            var7.startRiding(this);
         }
      }

      if (var4 == null) {
         var4 = new Spider.SpiderEffectsGroupData();
         if (var1.getDifficulty() == Difficulty.HARD && var6.nextFloat() < 0.1F * var2.getSpecialMultiplier()) {
            ((Spider.SpiderEffectsGroupData)var4).setRandomEffect(var6);
         }
      }

      if (var4 instanceof Spider.SpiderEffectsGroupData var10) {
         MobEffect var8 = var10.effect;
         if (var8 != null) {
            this.addEffect(new MobEffectInstance(var8, -1));
         }
      }

      return var4;
   }

   @Override
   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.65F;
   }

   static class SpiderAttackGoal extends MeleeAttackGoal {
      public SpiderAttackGoal(Spider var1) {
         super(var1, 1.0, true);
      }

      @Override
      public boolean canUse() {
         return super.canUse() && !this.mob.isVehicle();
      }

      @Override
      public boolean canContinueToUse() {
         float var1 = this.mob.getLightLevelDependentMagicValue();
         if (var1 >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget(null);
            return false;
         } else {
            return super.canContinueToUse();
         }
      }

      @Override
      protected double getAttackReachSqr(LivingEntity var1) {
         return (double)(4.0F + var1.getBbWidth());
      }
   }

   public static class SpiderEffectsGroupData implements SpawnGroupData {
      @Nullable
      public MobEffect effect;

      public SpiderEffectsGroupData() {
         super();
      }

      public void setRandomEffect(RandomSource var1) {
         int var2 = var1.nextInt(5);
         if (var2 <= 1) {
            this.effect = MobEffects.MOVEMENT_SPEED;
         } else if (var2 <= 2) {
            this.effect = MobEffects.DAMAGE_BOOST;
         } else if (var2 <= 3) {
            this.effect = MobEffects.REGENERATION;
         } else if (var2 <= 4) {
            this.effect = MobEffects.INVISIBILITY;
         }
      }
   }

   static class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
      public SpiderTargetGoal(Spider var1, Class<T> var2) {
         super(var1, var2, true);
      }

      @Override
      public boolean canUse() {
         float var1 = this.mob.getLightLevelDependentMagicValue();
         return var1 >= 0.5F ? false : super.canUse();
      }
   }
}
