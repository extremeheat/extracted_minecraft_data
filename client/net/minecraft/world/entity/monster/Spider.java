package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
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
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Spider extends Monster {
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   private static final float SPIDER_SPECIAL_EFFECT_CHANCE = 0.1F;

   public Spider(EntityType<? extends Spider> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new AvoidEntityGoal(this, Armadillo.class, 6.0F, 1.0, 1.2, (var0) -> {
         return !((Armadillo)var0).isScared();
      }));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new SpiderAttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new SpiderTargetGoal(this, Player.class));
      this.targetSelector.addGoal(3, new SpiderTargetGoal(this, IronGolem.class));
   }

   protected PathNavigation createNavigation(Level var1) {
      return new WallClimberNavigation(this, var1);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_FLAGS_ID, (byte)0);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.setClimbing(this.horizontalCollision);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
   }

   public boolean onClimbable() {
      return this.isClimbing();
   }

   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      if (!var1.is(Blocks.COBWEB)) {
         super.makeStuckInBlock(var1, var2);
      }

   }

   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.is(MobEffects.POISON) ? false : super.canBeAffected(var1);
   }

   public boolean isClimbing() {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setClimbing(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 &= -2;
      }

      this.entityData.set(DATA_FLAGS_ID, var2);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      Object var8 = super.finalizeSpawn(var1, var2, var3, var4);
      RandomSource var5 = var1.getRandom();
      if (var5.nextInt(100) == 0) {
         Skeleton var6 = (Skeleton)EntityType.SKELETON.create(this.level(), EntitySpawnReason.JOCKEY);
         if (var6 != null) {
            var6.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            var6.finalizeSpawn(var1, var2, var3, (SpawnGroupData)null);
            var6.startRiding(this);
         }
      }

      if (var8 == null) {
         var8 = new SpiderEffectsGroupData();
         if (var1.getDifficulty() == Difficulty.HARD && var5.nextFloat() < 0.1F * var2.getSpecialMultiplier()) {
            ((SpiderEffectsGroupData)var8).setRandomEffect(var5);
         }
      }

      if (var8 instanceof SpiderEffectsGroupData var9) {
         Holder var7 = var9.effect;
         if (var7 != null) {
            this.addEffect(new MobEffectInstance(var7, -1));
         }
      }

      return (SpawnGroupData)var8;
   }

   public Vec3 getVehicleAttachmentPoint(Entity var1) {
      return var1.getBbWidth() <= this.getBbWidth() ? new Vec3(0.0, 0.3125 * (double)this.getScale(), 0.0) : super.getVehicleAttachmentPoint(var1);
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BYTE);
   }

   static class SpiderAttackGoal extends MeleeAttackGoal {
      public SpiderAttackGoal(Spider var1) {
         super(var1, 1.0, true);
      }

      public boolean canUse() {
         return super.canUse() && !this.mob.isVehicle();
      }

      public boolean canContinueToUse() {
         float var1 = this.mob.getLightLevelDependentMagicValue();
         if (var1 >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget((LivingEntity)null);
            return false;
         } else {
            return super.canContinueToUse();
         }
      }
   }

   private static class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
      public SpiderTargetGoal(Spider var1, Class<T> var2) {
         super(var1, var2, true);
      }

      public boolean canUse() {
         float var1 = this.mob.getLightLevelDependentMagicValue();
         return var1 >= 0.5F ? false : super.canUse();
      }
   }

   public static class SpiderEffectsGroupData implements SpawnGroupData {
      @Nullable
      public Holder<MobEffect> effect;

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
}
