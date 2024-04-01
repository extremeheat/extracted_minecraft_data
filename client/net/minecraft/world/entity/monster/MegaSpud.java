package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Containers;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;

public class MegaSpud extends PathfinderMob implements PowerableMob, Enemy {
   private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(MegaSpud.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(MegaSpud.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Boolean> HAS_MINIONS = SynchedEntityData.defineId(MegaSpud.class, EntityDataSerializers.BOOLEAN);
   private static final int STARTING_HEALTH = 1024;
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround;
   private MegaSpud.Stage currentStage;
   private final List<Mob> adds = new ArrayList<>();
   private final ServerBossEvent bossEvent;
   private final List<Runnable> minionCalls = new ArrayList<>();
   private static final ParticleOptions BREAKING_PARICLE = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.POISONOUS_POTATO));

   public MegaSpud(EntityType<? extends MegaSpud> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new MegaSpud.SlimeMoveControl(this);
      this.currentStage = MegaSpud.Stage.CHICKEN;
      this.xpReward = 50;
      this.bossEvent = new ServerBossEvent(this, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
      this.bossEvent.setDarkenScreen(false);
      this.fixupDimensions();
   }

   @Override
   public void checkDespawn() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 1024.0)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.JUMP_STRENGTH, 0.6200000047683716)
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(7, new MegaSpud.GhastShootFireballGoal(this));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(0, new FloatGoal(this) {
         @Override
         public void tick() {
            if (MegaSpud.this.getRandom().nextFloat() < 0.8F) {
               MegaSpud.this.jumpFromGround();
            }
         }
      });
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(3, new MegaSpud.SlimeRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new MegaSpud.SlimeKeepOnJumpingGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      boolean var1 = false;
      if (!this.adds.isEmpty()) {
         var1 = true;
         this.adds.removeIf(var0 -> var0.isRemoved() || var0.isDeadOrDying());
      }

      if (!this.minionCalls.isEmpty()) {
         var1 = true;
         if (this.random.nextFloat() < 0.05F) {
            this.minionCalls.remove(0).run();
            if (this.minionCalls.isEmpty()) {
               this.playSound(SoundEvents.MEGASPUD_CHALLENGE, this.getSoundVolume(), 1.0F);
            }
         }
      }

      if (this.adds.isEmpty() && this.minionCalls.isEmpty()) {
         this.setHasMinions(false);
         if (var1) {
            this.playSound(SoundEvents.MEGASPUD_UPSET, this.getSoundVolume(), 1.0F);
         }
      }

      if ((!this.hasRestriction() || this.getRestrictCenter().distToCenterSqr(this.position()) > 16384.0) && !this.blockPosition().equals(BlockPos.ZERO)) {
         this.restrictTo(this.blockPosition(), 3);
      }

      MegaSpud.Stage var2 = this.currentStage.validStageBasedOnHealth(this.getHealth());
      if (this.currentStage != var2) {
         this.setHealth(this.currentStage.getHealth());
         AABB var3 = this.getBoundingBox();
         Vec3 var4 = this.position();
         Containers.dropItemStack(this.level(), var4.x, var4.y, var4.z, Items.CORRUPTED_POTATO_PEELS.getDefaultInstance());

         for(int var5 = 0; var5 < 100; ++var5) {
            Vec3 var6 = var4.add(
               (double)this.random.nextFloat(-5.0F, 5.0F), (double)this.random.nextFloat(0.0F, 10.0F), (double)this.random.nextFloat(-5.0F, 5.0F)
            );
            if (var3.contains(var6)) {
               Containers.dropItemStack(this.level(), var6.x, var6.y, var6.z, Items.CORRUPTED_POTATO_PEELS.getDefaultInstance());
            }
         }

         ServerLevel var9 = (ServerLevel)this.level();
         PlayerTeam var10 = this.getTeam();
         this.summonMinion(this.currentStage, var9, var10);
         MegaSpud.Stage var7 = this.currentStage;

         for(int var8 = 1; var8 <= this.currentStage.ordinal(); ++var8) {
            this.minionCalls.add(() -> this.summonMinion(var7, var9, var10));
         }

         this.currentStage = var2;
         this.setSize(this.currentStage.size);
         this.setHasMinions(true);
         this.bossEvent.setName(this.getDisplayName());
      }

      this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      this.bossEvent.setLocation(this.position(), 64);
   }

   private void summonMinion(MegaSpud.Stage var1, ServerLevel var2, PlayerTeam var3) {
      BlockPos var4 = this.getRestrictCenter()
         .offset(this.random.nextInt(5) - this.random.nextInt(5), this.random.nextInt(5), this.random.nextInt(5) - this.random.nextInt(5));
      Mob var5 = var1.getMinion().create(this.level());
      if (var5 != null) {
         var5.moveTo(var4, 0.0F, 0.0F);
         var5.finalizeSpawn(var2, this.level().getCurrentDifficultyAt(var4), MobSpawnType.MOB_SUMMONED, null);
         if (var3 != null) {
            var2.getScoreboard().addPlayerToTeam(var5.getScoreboardName(), var3);
         }

         var2.addFreshEntityWithPassengers(var5);
         var2.gameEvent(GameEvent.ENTITY_PLACE, var4, GameEvent.Context.of(this));
         var5.setPersistenceRequired();
         var5.restrictTo(this.getRestrictCenter(), 8);
         this.playSound(SoundEvents.MEGASPUD_SUMMON, this.getSoundVolume(), 1.0F);
         var2.levelEvent(3012, var4, 0);
         var2.sendParticles(ParticleTypes.TRIAL_SPAWNER_DETECTION, var5.getX(), var5.getY(0.5), var5.getZ(), 100, 0.5, 0.5, 0.5, 0.0);
         this.adds.add(var5);
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return super.hurt(var1, var2);
      } else {
         var2 = Math.min(var2, 100.0F);
         if (!this.isPowered()) {
            return super.hurt(var1, var2);
         } else {
            if (var1.getEntity() instanceof Player) {
               for(Mob var4 : this.adds) {
                  var4.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200), this);
               }
            }

            if (var1.getEntity() != null && !var1.is(DamageTypes.THORNS)) {
               var1.getEntity().hurt(this.level().damageSources().potatoMagic(), var2);
            }

            return false;
         }
      }
   }

   @Override
   public void startSeenByPlayer(ServerPlayer var1) {
      super.startSeenByPlayer(var1);
      this.bossEvent.addPlayer(var1);
   }

   @Override
   public void stopSeenByPlayer(ServerPlayer var1) {
      super.stopSeenByPlayer(var1);
      this.bossEvent.removePlayer(var1);
   }

   @Override
   public boolean isPotato() {
      return true;
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_SIZE, MegaSpud.Stage.CHICKEN.size);
      var1.define(DATA_SPELL_CASTING_ID, (byte)0);
      var1.define(HAS_MINIONS, false);
   }

   public void setHasMinions(boolean var1) {
      this.entityData.set(HAS_MINIONS, var1);
   }

   @Override
   public boolean isInvulnerable() {
      return super.isInvulnerable() || this.isPowered();
   }

   @VisibleForTesting
   public void setSize(int var1) {
      this.entityData.set(ID_SIZE, var1);
      this.reapplyPosition();
      this.refreshDimensions();
   }

   public int getSize() {
      return this.entityData.get(ID_SIZE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Size", this.getSize() - 1);
      var1.putBoolean("wasOnGround", this.wasOnGround);
      var1.putInt("homeX", this.getRestrictCenter().getX());
      var1.putInt("homeY", this.getRestrictCenter().getY());
      var1.putInt("homeZ", this.getRestrictCenter().getZ());
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }
   }

   @Override
   protected Component getTypeName() {
      return this.currentStage.getStageName();
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      this.setSize(var1.getInt("Size") + 1);
      super.readAdditionalSaveData(var1);
      this.wasOnGround = var1.getBoolean("wasOnGround");
      this.restrictTo(new BlockPos(var1.getInt("homeX"), var1.getInt("homeY"), var1.getInt("homeZ")), 3);

      while(this.currentStage != this.currentStage.validStageBasedOnHealth(this.getHealth())) {
         this.currentStage = this.currentStage.nextStage();
      }

      this.bossEvent.setName(this.getDisplayName());
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected ParticleOptions getParticleType() {
      return BREAKING_PARICLE;
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return this.getSize() > 0;
   }

   @Override
   public void tick() {
      this.squish += (this.targetSquish - this.squish) * 0.5F;
      this.oSquish = this.squish;
      super.tick();
      if (this.onGround() && !this.wasOnGround) {
         float var1 = this.getDimensions(this.getPose()).width() * 2.0F;
         float var2 = var1 / 2.0F;

         for(int var3 = 0; (float)var3 < var1 * 16.0F; ++var3) {
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
   public EntityType<? extends MegaSpud> getType() {
      return super.getType();
   }

   @Override
   public void remove(Entity.RemovalReason var1) {
      for(Mob var3 : this.adds) {
         var3.remove(var1);
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
      if (this.isAlive()) {
         int var2 = this.getSize();
         if (this.distanceToSqr(var1) < 0.6 * (double)var2 * 0.6 * (double)var2
            && this.hasLineOfSight(var1)
            && var1.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
            this.doEnchantDamageEffects(this, var1);
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
      return SoundEvents.MEGASPUD_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.MEGASPUD_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return SoundEvents.MEGASPUD_JUMP_HI;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.MEGASPUD_IDLE;
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F + 0.4F * (float)this.getSize();
   }

   @Override
   public int getMaxHeadXRot() {
      return 0;
   }

   protected boolean doPlayJumpSound() {
      return this.getSize() > 0;
   }

   @Override
   protected void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      Vec3 var2 = this.getLookAngle();
      float var3 = this.getJumpPower();
      float var4 = this.isWithinRestriction() ? 0.0F : var3;
      this.setDeltaMovement(var1.x + var2.x * (double)var4, (double)var3, var1.z + var2.z * (double)var4);
      this.hasImpulse = true;
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      this.setSize(MegaSpud.Stage.CHICKEN.size);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   public boolean isPowered() {
      return this.entityData.get(HAS_MINIONS);
   }

   float getSoundPitch() {
      float var1 = this.isTiny() ? 1.4F : 0.8F;
      return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * var1;
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny() ? SoundEvents.MEGASPUD_JUMP_HI : SoundEvents.MEGASPUD_JUMP;
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1).scale((float)this.getSize());
   }

   static class GhastShootFireballGoal extends Goal {
      private final MegaSpud spud;
      public int chargeTime;

      public GhastShootFireballGoal(MegaSpud var1) {
         super();
         this.spud = var1;
      }

      @Override
      public boolean canUse() {
         return this.spud.getTarget() != null;
      }

      @Override
      public void start() {
         this.chargeTime = 0;
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void tick() {
         LivingEntity var1 = this.spud.getTarget();
         if (var1 != null) {
            double var2 = 64.0;
            if (var1.distanceToSqr(this.spud) < 4096.0 && this.spud.hasLineOfSight(var1)) {
               Level var4 = this.spud.level();
               ++this.chargeTime;
               if (this.chargeTime == 10 && !this.spud.isSilent()) {
                  this.spud.playSound(SoundEvents.MEGASPUD_FIREBALL);
               }

               if (this.chargeTime == 20) {
                  AABB var5 = this.spud.getBoundingBox().inflate(0.5);
                  Vec3 var6 = this.spud.getEyePosition();
                  Vec3 var7 = var1.getEyePosition().subtract(var6).normalize().scale(0.1);
                  Vec3 var8 = var6;

                  while(var5.contains(var8)) {
                     var8 = var8.add(var7);
                  }

                  LargeFireball var9 = new LargeFireball(var4, this.spud, var7.x, var7.y, var7.z, 2, false);
                  var9.setPos(var8);
                  var4.addFreshEntity(var9);
                  this.chargeTime = -40;
               }
            } else if (this.chargeTime > 0) {
               --this.chargeTime;
            }
         }
      }
   }

   public static class SlimeKeepOnJumpingGoal extends Goal {
      private final Mob slime;

      public SlimeKeepOnJumpingGoal(Mob var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         return !this.slime.isPassenger();
      }

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Override
      public void tick() {
         MoveControl var2 = this.slime.getMoveControl();
         if (var2 instanceof MegaSpud.SlimeMoveControl var1) {
            var1.setWantedMovement(1.0);
         }
      }
   }

   static class SlimeMoveControl extends MoveControl {
      private float yRot;
      private int jumpDelay;
      private final MegaSpud slime;
      private boolean isAggressive;

      public SlimeMoveControl(MegaSpud var1) {
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
      private final MegaSpud slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public SlimeRandomDirectionGoal(MegaSpud var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         return (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION))
            && this.slime.getMoveControl() instanceof MegaSpud.SlimeMoveControl;
      }

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Override
      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
            if (this.slime.getTarget() != null && this.slime.random.nextFloat() < 0.4F) {
               this.chosenDegrees = this.getAngleToTarget(this.slime.getTarget().position()) + 90.0F;
            } else if (this.slime.hasRestriction() && !this.slime.isWithinRestriction()) {
               Vec3 var1 = Vec3.atBottomCenterOf(this.slime.getRestrictCenter());
               this.chosenDegrees = this.getAngleToTarget(var1) + 60.0F;
            } else {
               this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
            }
         }

         MoveControl var2 = this.slime.getMoveControl();
         if (var2 instanceof MegaSpud.SlimeMoveControl var3) {
            var3.setDirection(this.chosenDegrees + 20.0F - (float)this.slime.random.nextInt(40), false);
         }
      }

      private float getAngleToTarget(Vec3 var1) {
         return (float)Mth.atan2(this.slime.getZ() - var1.z, this.slime.getX() - var1.x) * 57.295776F;
      }
   }

   static enum Stage {
      CHICKEN(10, 1.0F, EntityType.CHICKEN),
      ARMADILLO(9, 0.9F, EntityType.ARMADILLO),
      ZOMBIE(8, 0.8F, EntityType.POISONOUS_POTATO_ZOMBIE),
      SPIDER(7, 0.7F, EntityType.SPIDER),
      STRAY(6, 0.6F, EntityType.STRAY),
      CREEPER(5, 0.5F, EntityType.CREEPER),
      BRUTE(4, 0.4F, EntityType.PIGLIN_BRUTE),
      GHAST(3, 0.3F, EntityType.GHAST),
      PLAGUEWHALE(2, 0.2F, EntityType.PLAGUEWHALE),
      GIANT(1, 0.1F, EntityType.GIANT),
      END(1, -1.0F, EntityType.FROG);

      final int size;
      private final float percentHealthTransition;
      private final EntityType<? extends Mob> minion;
      private Component name = EntityType.MEGA_SPUD.getDescription();

      private Stage(int var3, float var4, EntityType<? extends Mob> var5) {
         this.size = var3;
         this.percentHealthTransition = var4;
         this.minion = var5;
      }

      public MegaSpud.Stage nextStage() {
         int var1 = this.ordinal() + 1;
         return var1 >= values().length ? this : values()[var1];
      }

      @Nullable
      public MegaSpud.Stage previousStage() {
         int var1 = this.ordinal() - 1;
         return var1 < 0 ? null : values()[var1];
      }

      public EntityType<? extends Mob> getMinion() {
         return this.minion;
      }

      public float getHealth() {
         return this.percentHealthTransition * 1024.0F;
      }

      public MegaSpud.Stage validStageBasedOnHealth(float var1) {
         return var1 < this.getHealth() ? this.nextStage() : this;
      }

      public Component getStageName() {
         return this.name;
      }

      static {
         for(MegaSpud.Stage var3 : values()) {
            MegaSpud.Stage var4 = var3.previousStage();
            if (var4 == null) {
               var3.name = EntityType.MEGA_SPUD.getDescription();
            } else {
               var3.name = Component.translatable("entity.minecraft.mega_spud." + BuiltInRegistries.ENTITY_TYPE.getKey(var4.getMinion()).getPath());
            }
         }
      }
   }
}
