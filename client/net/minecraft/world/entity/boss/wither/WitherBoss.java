package net.minecraft.world.entity.boss.wither;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WitherBoss extends Monster implements RangedAttackMob {
   private static final EntityDataAccessor<Integer> DATA_TARGET_A;
   private static final EntityDataAccessor<Integer> DATA_TARGET_B;
   private static final EntityDataAccessor<Integer> DATA_TARGET_C;
   private static final List<EntityDataAccessor<Integer>> DATA_TARGETS;
   private static final EntityDataAccessor<Integer> DATA_ID_INV;
   private static final int INVULNERABLE_TICKS = 220;
   private final float[] xRotHeads = new float[2];
   private final float[] yRotHeads = new float[2];
   private final float[] xRotOHeads = new float[2];
   private final float[] yRotOHeads = new float[2];
   private final int[] nextHeadUpdate = new int[2];
   private final int[] idleHeadUpdates = new int[2];
   private int destroyBlocksTick;
   private final ServerBossEvent bossEvent;
   private static final TargetingConditions.Selector LIVING_ENTITY_SELECTOR;
   private static final TargetingConditions TARGETING_CONDITIONS;

   public WitherBoss(EntityType<? extends WitherBoss> var1, Level var2) {
      super(var1, var2);
      this.bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
      this.moveControl = new FlyingMoveControl(this, 10, false);
      this.setHealth(this.getMaxHealth());
      this.xpReward = 50;
   }

   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1);
      var2.setCanOpenDoors(false);
      var2.setCanFloat(true);
      return var2;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new WitherDoNothingGoal());
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 20.0F));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TARGET_A, 0);
      var1.define(DATA_TARGET_B, 0);
      var1.define(DATA_TARGET_C, 0);
      var1.define(DATA_ID_INV, 0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Invul", this.getInvulnerableTicks());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setInvulnerableTicks(var1.getInt("Invul"));
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }

   }

   public void setCustomName(@Nullable Component var1) {
      super.setCustomName(var1);
      this.bossEvent.setName(this.getDisplayName());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_DEATH;
   }

   public void aiStep() {
      Vec3 var1 = this.getDeltaMovement().multiply(1.0, 0.6, 1.0);
      if (!this.level().isClientSide && this.getAlternativeTarget(0) > 0) {
         Entity var2 = this.level().getEntity(this.getAlternativeTarget(0));
         if (var2 != null) {
            double var3 = var1.y;
            if (this.getY() < var2.getY() || !this.isPowered() && this.getY() < var2.getY() + 5.0) {
               var3 = Math.max(0.0, var3);
               var3 += 0.3 - var3 * 0.6000000238418579;
            }

            var1 = new Vec3(var1.x, var3, var1.z);
            Vec3 var5 = new Vec3(var2.getX() - this.getX(), 0.0, var2.getZ() - this.getZ());
            if (var5.horizontalDistanceSqr() > 9.0) {
               Vec3 var6 = var5.normalize();
               var1 = var1.add(var6.x * 0.3 - var1.x * 0.6, 0.0, var6.z * 0.3 - var1.z * 0.6);
            }
         }
      }

      this.setDeltaMovement(var1);
      if (var1.horizontalDistanceSqr() > 0.05) {
         this.setYRot((float)Mth.atan2(var1.z, var1.x) * 57.295776F - 90.0F);
      }

      super.aiStep();

      int var21;
      for(var21 = 0; var21 < 2; ++var21) {
         this.yRotOHeads[var21] = this.yRotHeads[var21];
         this.xRotOHeads[var21] = this.xRotHeads[var21];
      }

      int var23;
      for(var21 = 0; var21 < 2; ++var21) {
         var23 = this.getAlternativeTarget(var21 + 1);
         Entity var4 = null;
         if (var23 > 0) {
            var4 = this.level().getEntity(var23);
         }

         if (var4 != null) {
            double var25 = this.getHeadX(var21 + 1);
            double var7 = this.getHeadY(var21 + 1);
            double var9 = this.getHeadZ(var21 + 1);
            double var11 = var4.getX() - var25;
            double var13 = var4.getEyeY() - var7;
            double var15 = var4.getZ() - var9;
            double var17 = Math.sqrt(var11 * var11 + var15 * var15);
            float var19 = (float)(Mth.atan2(var15, var11) * 57.2957763671875) - 90.0F;
            float var20 = (float)(-(Mth.atan2(var13, var17) * 57.2957763671875));
            this.xRotHeads[var21] = this.rotlerp(this.xRotHeads[var21], var20, 40.0F);
            this.yRotHeads[var21] = this.rotlerp(this.yRotHeads[var21], var19, 10.0F);
         } else {
            this.yRotHeads[var21] = this.rotlerp(this.yRotHeads[var21], this.yBodyRot, 10.0F);
         }
      }

      boolean var22 = this.isPowered();

      for(var23 = 0; var23 < 3; ++var23) {
         double var24 = this.getHeadX(var23);
         double var28 = this.getHeadY(var23);
         double var8 = this.getHeadZ(var23);
         float var10 = 0.3F * this.getScale();
         this.level().addParticle(ParticleTypes.SMOKE, var24 + this.random.nextGaussian() * (double)var10, var28 + this.random.nextGaussian() * (double)var10, var8 + this.random.nextGaussian() * (double)var10, 0.0, 0.0, 0.0);
         if (var22 && this.level().random.nextInt(4) == 0) {
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.7F, 0.7F, 0.5F), var24 + this.random.nextGaussian() * (double)var10, var28 + this.random.nextGaussian() * (double)var10, var8 + this.random.nextGaussian() * (double)var10, 0.0, 0.0, 0.0);
         }
      }

      if (this.getInvulnerableTicks() > 0) {
         float var26 = 3.3F * this.getScale();

         for(int var27 = 0; var27 < 3; ++var27) {
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.7F, 0.7F, 0.9F), this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * var26), this.getZ() + this.random.nextGaussian(), 0.0, 0.0, 0.0);
         }
      }

   }

   protected void customServerAiStep(ServerLevel var1) {
      int var2;
      if (this.getInvulnerableTicks() > 0) {
         var2 = this.getInvulnerableTicks() - 1;
         this.bossEvent.setProgress(1.0F - (float)var2 / 220.0F);
         if (var2 <= 0) {
            var1.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, Level.ExplosionInteraction.MOB);
            if (!this.isSilent()) {
               var1.globalLevelEvent(1023, this.blockPosition(), 0);
            }
         }

         this.setInvulnerableTicks(var2);
         if (this.tickCount % 10 == 0) {
            this.heal(10.0F);
         }

      } else {
         super.customServerAiStep(var1);

         int var12;
         for(var2 = 1; var2 < 3; ++var2) {
            if (this.tickCount >= this.nextHeadUpdate[var2 - 1]) {
               this.nextHeadUpdate[var2 - 1] = this.tickCount + 10 + this.random.nextInt(10);
               if (var1.getDifficulty() == Difficulty.NORMAL || var1.getDifficulty() == Difficulty.HARD) {
                  int[] var10000 = this.idleHeadUpdates;
                  int var10001 = var2 - 1;
                  int var10003 = var10000[var2 - 1];
                  var10000[var10001] = var10000[var2 - 1] + 1;
                  if (var10003 > 15) {
                     float var3 = 10.0F;
                     float var4 = 5.0F;
                     double var5 = Mth.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
                     double var7 = Mth.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
                     double var9 = Mth.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
                     this.performRangedAttack(var2 + 1, var5, var7, var9, true);
                     this.idleHeadUpdates[var2 - 1] = 0;
                  }
               }

               var12 = this.getAlternativeTarget(var2);
               if (var12 > 0) {
                  LivingEntity var14 = (LivingEntity)var1.getEntity(var12);
                  if (var14 != null && this.canAttack(var14) && !(this.distanceToSqr(var14) > 900.0) && this.hasLineOfSight(var14)) {
                     this.performRangedAttack(var2 + 1, var14);
                     this.nextHeadUpdate[var2 - 1] = this.tickCount + 40 + this.random.nextInt(20);
                     this.idleHeadUpdates[var2 - 1] = 0;
                  } else {
                     this.setAlternativeTarget(var2, 0);
                  }
               } else {
                  List var13 = var1.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0, 8.0, 20.0));
                  if (!var13.isEmpty()) {
                     LivingEntity var16 = (LivingEntity)var13.get(this.random.nextInt(var13.size()));
                     this.setAlternativeTarget(var2, var16.getId());
                  }
               }
            }
         }

         if (this.getTarget() != null) {
            this.setAlternativeTarget(0, this.getTarget().getId());
         } else {
            this.setAlternativeTarget(0, 0);
         }

         if (this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0 && var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               boolean var11 = false;
               var12 = Mth.floor(this.getBbWidth() / 2.0F + 1.0F);
               int var15 = Mth.floor(this.getBbHeight());
               Iterator var17 = BlockPos.betweenClosed(this.getBlockX() - var12, this.getBlockY(), this.getBlockZ() - var12, this.getBlockX() + var12, this.getBlockY() + var15, this.getBlockZ() + var12).iterator();

               label74:
               while(true) {
                  BlockPos var6;
                  BlockState var18;
                  do {
                     if (!var17.hasNext()) {
                        if (var11) {
                           var1.levelEvent((Player)null, 1022, this.blockPosition(), 0);
                        }
                        break label74;
                     }

                     var6 = (BlockPos)var17.next();
                     var18 = var1.getBlockState(var6);
                  } while(!canDestroy(var18));

                  var11 = var1.destroyBlock(var6, true, this) || var11;
               }
            }
         }

         if (this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }
   }

   public static boolean canDestroy(BlockState var0) {
      return !var0.isAir() && !var0.is(BlockTags.WITHER_IMMUNE);
   }

   public void makeInvulnerable() {
      this.setInvulnerableTicks(220);
      this.bossEvent.setProgress(0.0F);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
   }

   public void startSeenByPlayer(ServerPlayer var1) {
      super.startSeenByPlayer(var1);
      this.bossEvent.addPlayer(var1);
   }

   public void stopSeenByPlayer(ServerPlayer var1) {
      super.stopSeenByPlayer(var1);
      this.bossEvent.removePlayer(var1);
   }

   private double getHeadX(int var1) {
      if (var1 <= 0) {
         return this.getX();
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (var1 - 1))) * 0.017453292F;
         float var3 = Mth.cos(var2);
         return this.getX() + (double)var3 * 1.3 * (double)this.getScale();
      }
   }

   private double getHeadY(int var1) {
      float var2 = var1 <= 0 ? 3.0F : 2.2F;
      return this.getY() + (double)(var2 * this.getScale());
   }

   private double getHeadZ(int var1) {
      if (var1 <= 0) {
         return this.getZ();
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (var1 - 1))) * 0.017453292F;
         float var3 = Mth.sin(var2);
         return this.getZ() + (double)var3 * 1.3 * (double)this.getScale();
      }
   }

   private float rotlerp(float var1, float var2, float var3) {
      float var4 = Mth.wrapDegrees(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }

   private void performRangedAttack(int var1, LivingEntity var2) {
      this.performRangedAttack(var1, var2.getX(), var2.getY() + (double)var2.getEyeHeight() * 0.5, var2.getZ(), var1 == 0 && this.random.nextFloat() < 0.001F);
   }

   private void performRangedAttack(int var1, double var2, double var4, double var6, boolean var8) {
      if (!this.isSilent()) {
         this.level().levelEvent((Player)null, 1024, this.blockPosition(), 0);
      }

      double var9 = this.getHeadX(var1);
      double var11 = this.getHeadY(var1);
      double var13 = this.getHeadZ(var1);
      double var15 = var2 - var9;
      double var17 = var4 - var11;
      double var19 = var6 - var13;
      Vec3 var21 = new Vec3(var15, var17, var19);
      WitherSkull var22 = new WitherSkull(this.level(), this, var21.normalize());
      var22.setOwner(this);
      if (var8) {
         var22.setDangerous(true);
      }

      var22.setPos(var9, var11, var13);
      this.level().addFreshEntity(var22);
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      this.performRangedAttack(0, var1);
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableTo(var1, var2)) {
         return false;
      } else if (!var2.is(DamageTypeTags.WITHER_IMMUNE_TO) && !(var2.getEntity() instanceof WitherBoss)) {
         if (this.getInvulnerableTicks() > 0 && !var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
         } else {
            Entity var4;
            if (this.isPowered()) {
               var4 = var2.getDirectEntity();
               if (var4 instanceof AbstractArrow || var4 instanceof WindCharge) {
                  return false;
               }
            }

            var4 = var2.getEntity();
            if (var4 != null && var4.getType().is(EntityTypeTags.WITHER_FRIENDS)) {
               return false;
            } else {
               if (this.destroyBlocksTick <= 0) {
                  this.destroyBlocksTick = 20;
               }

               for(int var5 = 0; var5 < this.idleHeadUpdates.length; ++var5) {
                  int[] var10000 = this.idleHeadUpdates;
                  var10000[var5] += 3;
               }

               return super.hurtServer(var1, var2, var3);
            }
         }
      } else {
         return false;
      }
   }

   protected void dropCustomDeathLoot(ServerLevel var1, DamageSource var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      ItemEntity var4 = this.spawnAtLocation(var1, Items.NETHER_STAR);
      if (var4 != null) {
         var4.setExtendedLifetime();
      }

   }

   public void checkDespawn() {
      if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.discard();
      } else {
         this.noActionTime = 0;
      }
   }

   public boolean addEffect(MobEffectInstance var1, @Nullable Entity var2) {
      return false;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 300.0).add(Attributes.MOVEMENT_SPEED, 0.6000000238418579).add(Attributes.FLYING_SPEED, 0.6000000238418579).add(Attributes.FOLLOW_RANGE, 40.0).add(Attributes.ARMOR, 4.0);
   }

   public float[] getHeadYRots() {
      return this.yRotHeads;
   }

   public float[] getHeadXRots() {
      return this.xRotHeads;
   }

   public int getInvulnerableTicks() {
      return (Integer)this.entityData.get(DATA_ID_INV);
   }

   public void setInvulnerableTicks(int var1) {
      this.entityData.set(DATA_ID_INV, var1);
   }

   public int getAlternativeTarget(int var1) {
      return (Integer)this.entityData.get((EntityDataAccessor)DATA_TARGETS.get(var1));
   }

   public void setAlternativeTarget(int var1, int var2) {
      this.entityData.set((EntityDataAccessor)DATA_TARGETS.get(var1), var2);
   }

   public boolean isPowered() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   protected boolean canRide(Entity var1) {
      return false;
   }

   public boolean canUsePortal(boolean var1) {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.is(MobEffects.WITHER) ? false : super.canBeAffected(var1);
   }

   static {
      DATA_TARGET_A = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      DATA_TARGET_B = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      DATA_TARGET_C = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
      DATA_ID_INV = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      LIVING_ENTITY_SELECTOR = (var0, var1) -> {
         return !var0.getType().is(EntityTypeTags.WITHER_FRIENDS) && var0.attackable();
      };
      TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0).selector(LIVING_ENTITY_SELECTOR);
   }

   private class WitherDoNothingGoal extends Goal {
      public WitherDoNothingGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return WitherBoss.this.getInvulnerableTicks() > 0;
      }
   }
}
