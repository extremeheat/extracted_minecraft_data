package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.IntRange;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PolarBear extends Animal implements NeutralMob {
   private static final EntityDataAccessor<Boolean> DATA_STANDING_ID;
   private float clientSideStandAnimationO;
   private float clientSideStandAnimation;
   private int warningSoundTicks;
   private static final IntRange PERSISTENT_ANGER_TIME;
   private int remainingPersistentAngerTime;
   private UUID persistentAngerTarget;

   public PolarBear(EntityType<? extends PolarBear> var1, Level var2) {
      super(var1, var2);
   }

   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (AgeableMob)EntityType.POLAR_BEAR.create(var1);
   }

   public boolean isFood(ItemStack var1) {
      return false;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PolarBear.PolarBearMeleeAttackGoal());
      this.goalSelector.addGoal(1, new PolarBear.PolarBearPanicGoal());
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new PolarBear.PolarBearHurtByTargetGoal());
      this.targetSelector.addGoal(2, new PolarBear.PolarBearAttackPlayersGoal());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Fox.class, 10, true, true, (Predicate)null));
      this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 6.0D);
   }

   public static boolean checkPolarBearSpawnRules(EntityType<PolarBear> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      Optional var5 = var1.getBiomeName(var3);
      if (!Objects.equals(var5, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(var5, Optional.of(Biomes.DEEP_FROZEN_OCEAN))) {
         return checkAnimalSpawnRules(var0, var1, var2, var3, var4);
      } else {
         return var1.getRawBrightness(var3, 0) > 8 && var1.getBlockState(var3.below()).is(Blocks.ICE);
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readPersistentAngerSaveData(this.level, var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addPersistentAngerSaveData(var1);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   public void setRemainingPersistentAngerTime(int var1) {
      this.remainingPersistentAngerTime = var1;
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.POLAR_BEAR_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.POLAR_BEAR_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
   }

   protected void playWarningSound() {
      if (this.warningSoundTicks <= 0) {
         this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
         this.warningSoundTicks = 40;
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_STANDING_ID, false);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
            this.refreshDimensions();
         }

         this.clientSideStandAnimationO = this.clientSideStandAnimation;
         if (this.isStanding()) {
            this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
         } else {
            this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
         }
      }

      if (this.warningSoundTicks > 0) {
         --this.warningSoundTicks;
      }

      if (!this.level.isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level, true);
      }

   }

   public EntityDimensions getDimensions(Pose var1) {
      if (this.clientSideStandAnimation > 0.0F) {
         float var2 = this.clientSideStandAnimation / 6.0F;
         float var3 = 1.0F + var2;
         return super.getDimensions(var1).scale(1.0F, var3);
      } else {
         return super.getDimensions(var1);
      }
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = var1.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (var2) {
         this.doEnchantDamageEffects(this, var1);
      }

      return var2;
   }

   public boolean isStanding() {
      return (Boolean)this.entityData.get(DATA_STANDING_ID);
   }

   public void setStanding(boolean var1) {
      this.entityData.set(DATA_STANDING_ID, var1);
   }

   public float getStandingAnimationScale(float var1) {
      return Mth.lerp(var1, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (var4 == null) {
         var4 = new AgeableMob.AgeableMobGroupData(1.0F);
      }

      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   static {
      DATA_STANDING_ID = SynchedEntityData.defineId(PolarBear.class, EntityDataSerializers.BOOLEAN);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }

   class PolarBearPanicGoal extends PanicGoal {
      public PolarBearPanicGoal() {
         super(PolarBear.this, 2.0D);
      }

      public boolean canUse() {
         return !PolarBear.this.isBaby() && !PolarBear.this.isOnFire() ? false : super.canUse();
      }
   }

   class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
      public PolarBearMeleeAttackGoal() {
         super(PolarBear.this, 1.25D, true);
      }

      protected void checkAndPerformAttack(LivingEntity var1, double var2) {
         double var4 = this.getAttackReachSqr(var1);
         if (var2 <= var4 && this.isTimeToAttack()) {
            this.resetAttackCooldown();
            this.mob.doHurtTarget(var1);
            PolarBear.this.setStanding(false);
         } else if (var2 <= var4 * 2.0D) {
            if (this.isTimeToAttack()) {
               PolarBear.this.setStanding(false);
               this.resetAttackCooldown();
            }

            if (this.getTicksUntilNextAttack() <= 10) {
               PolarBear.this.setStanding(true);
               PolarBear.this.playWarningSound();
            }
         } else {
            this.resetAttackCooldown();
            PolarBear.this.setStanding(false);
         }

      }

      public void stop() {
         PolarBear.this.setStanding(false);
         super.stop();
      }

      protected double getAttackReachSqr(LivingEntity var1) {
         return (double)(4.0F + var1.getBbWidth());
      }
   }

   class PolarBearAttackPlayersGoal extends NearestAttackableTargetGoal<Player> {
      public PolarBearAttackPlayersGoal() {
         super(PolarBear.this, Player.class, 20, true, true, (Predicate)null);
      }

      public boolean canUse() {
         if (PolarBear.this.isBaby()) {
            return false;
         } else {
            if (super.canUse()) {
               List var1 = PolarBear.this.level.getEntitiesOfClass(PolarBear.class, PolarBear.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
               Iterator var2 = var1.iterator();

               while(var2.hasNext()) {
                  PolarBear var3 = (PolarBear)var2.next();
                  if (var3.isBaby()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      protected double getFollowDistance() {
         return super.getFollowDistance() * 0.5D;
      }
   }

   class PolarBearHurtByTargetGoal extends HurtByTargetGoal {
      public PolarBearHurtByTargetGoal() {
         super(PolarBear.this);
      }

      public void start() {
         super.start();
         if (PolarBear.this.isBaby()) {
            this.alertOthers();
            this.stop();
         }

      }

      protected void alertOther(Mob var1, LivingEntity var2) {
         if (var1 instanceof PolarBear && !var1.isBaby()) {
            super.alertOther(var1, var2);
         }

      }
   }
}
