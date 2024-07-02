package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;

public class Slime extends Mob implements Enemy {
   private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);
   public static final int MIN_SIZE = 1;
   public static final int MAX_SIZE = 127;
   public static final int MAX_NATURAL_SIZE = 4;
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround;

   public Slime(EntityType<? extends Slime> var1, Level var2) {
      super(var1, var2);
      this.fixupDimensions();
      this.moveControl = new Slime.SlimeMoveControl(this);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Slime.SlimeFloatGoal(this));
      this.goalSelector.addGoal(2, new Slime.SlimeAttackGoal(this));
      this.goalSelector.addGoal(3, new Slime.SlimeRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new Slime.SlimeKeepOnJumpingGoal(this));
      this.targetSelector
         .addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, var1 -> Math.abs(var1.getY() - this.getY()) <= 4.0));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_SIZE, 1);
   }

   @VisibleForTesting
   public void setSize(int var1, boolean var2) {
      int var3 = Mth.clamp(var1, 1, 127);
      this.entityData.set(ID_SIZE, var3);
      this.reapplyPosition();
      this.refreshDimensions();
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(var3 * var3));
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)var3));
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)var3);
      if (var2) {
         this.setHealth(this.getMaxHealth());
      }

      this.xpReward = var3;
   }

   public int getSize() {
      return this.entityData.get(ID_SIZE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Size", this.getSize() - 1);
      var1.putBoolean("wasOnGround", this.wasOnGround);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      this.setSize(var1.getInt("Size") + 1, false);
      super.readAdditionalSaveData(var1);
      this.wasOnGround = var1.getBoolean("wasOnGround");
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.ITEM_SLIME;
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return this.getSize() > 0;
   }

   @Override
   public void tick() {
      this.squish = this.squish + (this.targetSquish - this.squish) * 0.5F;
      this.oSquish = this.squish;
      super.tick();
      if (this.onGround() && !this.wasOnGround) {
         float var1 = this.getDimensions(this.getPose()).width() * 2.0F;
         float var2 = var1 / 2.0F;

         for (int var3 = 0; (float)var3 < var1 * 16.0F; var3++) {
            float var4 = this.random.nextFloat() * 6.2831855F;
            float var5 = this.random.nextFloat() * 0.5F + 0.5F;
            float var6 = Mth.sin(var4) * var2 * var5;
            float var7 = Mth.cos(var4) * var2 * var5;
            this.level().addParticle(this.getParticleType(), this.getX() + (double)var6, this.getY(), this.getZ() + (double)var7, 0.0, 0.0, 0.0);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.targetSquish = -0.5F;
      } else if (!this.onGround() && this.wasOnGround) {
         this.targetSquish = 1.0F;
      }

      this.wasOnGround = this.onGround();
      this.decreaseSquish();
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.random.nextInt(20) + 10;
   }

   @Override
   public void refreshDimensions() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (ID_SIZE.equals(var1)) {
         this.refreshDimensions();
         this.setYRot(this.yHeadRot);
         this.yBodyRot = this.yHeadRot;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public EntityType<? extends Slime> getType() {
      return (EntityType<? extends Slime>)super.getType();
   }

   @Override
   public void remove(Entity.RemovalReason var1) {
      int var2 = this.getSize();
      if (!this.level().isClientSide && var2 > 1 && this.isDeadOrDying()) {
         Component var3 = this.getCustomName();
         boolean var4 = this.isNoAi();
         float var5 = this.getDimensions(this.getPose()).width();
         float var6 = var5 / 2.0F;
         int var7 = var2 / 2;
         int var8 = 2 + this.random.nextInt(3);

         for (int var9 = 0; var9 < var8; var9++) {
            float var10 = ((float)(var9 % 2) - 0.5F) * var6;
            float var11 = ((float)(var9 / 2) - 0.5F) * var6;
            Slime var12 = this.getType().create(this.level());
            if (var12 != null) {
               if (this.isPersistenceRequired()) {
                  var12.setPersistenceRequired();
               }

               var12.setCustomName(var3);
               var12.setNoAi(var4);
               var12.setInvulnerable(this.isInvulnerable());
               var12.setSize(var7, true);
               var12.moveTo(this.getX() + (double)var10, this.getY() + 0.5, this.getZ() + (double)var11, this.random.nextFloat() * 360.0F, 0.0F);
               this.level().addFreshEntity(var12);
            }
         }
      }

      super.remove(var1);
   }

   @Override
   public void push(Entity var1) {
      super.push(var1);
      if (var1 instanceof IronGolem && this.isDealsDamage()) {
         this.dealDamage((LivingEntity)var1);
      }
   }

   @Override
   public void playerTouch(Player var1) {
      if (this.isDealsDamage()) {
         this.dealDamage(var1);
      }
   }

   protected void dealDamage(LivingEntity var1) {
      if (this.isAlive() && this.isWithinMeleeAttackRange(var1) && this.hasLineOfSight(var1)) {
         DamageSource var2 = this.damageSources().mobAttack(this);
         if (var1.hurt(var2, this.getAttackDamage())) {
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.level() instanceof ServerLevel var3) {
               EnchantmentHelper.doPostAttackEffects(var3, var1, var2);
            }
         }
      }
   }

   @Override
   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      return new Vec3(0.0, (double)var2.height() - 0.015625 * (double)this.getSize() * (double)var3, 0.0);
   }

   protected boolean isDealsDamage() {
      return !this.isTiny() && this.isEffectiveAi();
   }

   protected float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
   }

   public static boolean checkSlimeSpawnRules(EntityType<Slime> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      if (MobSpawnType.isSpawner(var2)) {
         return checkMobSpawnRules(var0, var1, var2, var3, var4);
      } else {
         if (var1.getDifficulty() != Difficulty.PEACEFUL) {
            if (var2 == MobSpawnType.SPAWNER) {
               return checkMobSpawnRules(var0, var1, var2, var3, var4);
            }

            if (var1.getBiome(var3).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS)
               && var3.getY() > 50
               && var3.getY() < 70
               && var4.nextFloat() < 0.5F
               && var4.nextFloat() < var1.getMoonBrightness()
               && var1.getMaxLocalRawBrightness(var3) <= var4.nextInt(8)) {
               return checkMobSpawnRules(var0, var1, var2, var3, var4);
            }

            if (!(var1 instanceof WorldGenLevel)) {
               return false;
            }

            ChunkPos var5 = new ChunkPos(var3);
            boolean var6 = WorldgenRandom.seedSlimeChunk(var5.x, var5.z, ((WorldGenLevel)var1).getSeed(), 987234911L).nextInt(10) == 0;
            if (var4.nextInt(10) == 0 && var6 && var3.getY() < 40) {
               return checkMobSpawnRules(var0, var1, var2, var3, var4);
            }
         }

         return false;
      }
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F * (float)this.getSize();
   }

   @Override
   public int getMaxHeadXRot() {
      return 0;
   }

   protected boolean doPlayJumpSound() {
      return this.getSize() > 0;
   }

   @Override
   public void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x, (double)this.getJumpPower(), var1.z);
      this.hasImpulse = true;
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      int var6 = var5.nextInt(3);
      if (var6 < 2 && var5.nextFloat() < 0.5F * var2.getSpecialMultiplier()) {
         var6++;
      }

      int var7 = 1 << var6;
      this.setSize(var7, true);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   float getSoundPitch() {
      float var1 = this.isTiny() ? 1.4F : 0.8F;
      return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * var1;
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1).scale((float)this.getSize());
   }

   static class SlimeAttackGoal extends Goal {
      private final Slime slime;
      private int growTiredTimer;

      public SlimeAttackGoal(Slime var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         LivingEntity var1 = this.slime.getTarget();
         if (var1 == null) {
            return false;
         } else {
            return !this.slime.canAttack(var1) ? false : this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
         }
      }

      @Override
      public void start() {
         this.growTiredTimer = reducedTickDelay(300);
         super.start();
      }

      @Override
      public boolean canContinueToUse() {
         LivingEntity var1 = this.slime.getTarget();
         if (var1 == null) {
            return false;
         } else {
            return !this.slime.canAttack(var1) ? false : --this.growTiredTimer > 0;
         }
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void tick() {
         LivingEntity var1 = this.slime.getTarget();
         if (var1 != null) {
            this.slime.lookAt(var1, 10.0F, 10.0F);
         }

         if (this.slime.getMoveControl() instanceof Slime.SlimeMoveControl var2) {
            var2.setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
         }
      }
   }

   static class SlimeFloatGoal extends Goal {
      private final Slime slime;

      public SlimeFloatGoal(Slime var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         var1.getNavigation().setCanFloat(true);
      }

      @Override
      public boolean canUse() {
         return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void tick() {
         if (this.slime.getRandom().nextFloat() < 0.8F) {
            this.slime.getJumpControl().jump();
         }

         if (this.slime.getMoveControl() instanceof Slime.SlimeMoveControl var1) {
            var1.setWantedMovement(1.2);
         }
      }
   }

   static class SlimeKeepOnJumpingGoal extends Goal {
      private final Slime slime;

      public SlimeKeepOnJumpingGoal(Slime var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         return !this.slime.isPassenger();
      }

      @Override
      public void tick() {
         if (this.slime.getMoveControl() instanceof Slime.SlimeMoveControl var1) {
            var1.setWantedMovement(1.0);
         }
      }
   }

   static class SlimeMoveControl extends MoveControl {
      private float yRot;
      private int jumpDelay;
      private final Slime slime;
      private boolean isAggressive;

      public SlimeMoveControl(Slime var1) {
         super(var1);
         this.slime = var1;
         this.yRot = 180.0F * var1.getYRot() / 3.1415927F;
      }

      public void setDirection(float var1, boolean var2) {
         this.yRot = var1;
         this.isAggressive = var2;
      }

      public void setWantedMovement(double var1) {
         this.speedModifier = var1;
         this.operation = MoveControl.Operation.MOVE_TO;
      }

      @Override
      public void tick() {
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
         this.mob.yHeadRot = this.mob.getYRot();
         this.mob.yBodyRot = this.mob.getYRot();
         if (this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
         } else {
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround()) {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpControl().jump();
                  if (this.slime.doPlayJumpSound()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                  }
               } else {
                  this.slime.xxa = 0.0F;
                  this.slime.zza = 0.0F;
                  this.mob.setSpeed(0.0F);
               }
            } else {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
         }
      }
   }

   static class SlimeRandomDirectionGoal extends Goal {
      private final Slime slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public SlimeRandomDirectionGoal(Slime var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         return this.slime.getTarget() == null
            && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION))
            && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      @Override
      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
            this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
         }

         if (this.slime.getMoveControl() instanceof Slime.SlimeMoveControl var1) {
            var1.setDirection(this.chosenDegrees, false);
         }
      }
   }
}
