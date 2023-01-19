package net.minecraft.world.entity.boss.wither;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PowerableMob;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WitherBoss extends Monster implements PowerableMob, RangedAttackMob {
   private static final EntityDataAccessor<Integer> DATA_TARGET_A = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_TARGET_B = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_TARGET_C = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final List<EntityDataAccessor<Integer>> DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
   private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final int INVULNERABLE_TICKS = 220;
   private final float[] xRotHeads = new float[2];
   private final float[] yRotHeads = new float[2];
   private final float[] xRotOHeads = new float[2];
   private final float[] yRotOHeads = new float[2];
   private final int[] nextHeadUpdate = new int[2];
   private final int[] idleHeadUpdates = new int[2];
   private int destroyBlocksTick;
   private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(
         this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS
      )
      .setDarkenScreen(true);
   private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = var0 -> var0.getMobType() != MobType.UNDEAD && var0.attackable();
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0).selector(LIVING_ENTITY_SELECTOR);

   public WitherBoss(EntityType<? extends WitherBoss> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new FlyingMoveControl(this, 10, false);
      this.setHealth(this.getMaxHealth());
      this.xpReward = 50;
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1);
      var2.setCanOpenDoors(false);
      var2.setCanFloat(true);
      var2.setCanPassDoors(true);
      return var2;
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new WitherBoss.WitherDoNothingGoal());
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0, 40, 20.0F));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TARGET_A, 0);
      this.entityData.define(DATA_TARGET_B, 0);
      this.entityData.define(DATA_TARGET_C, 0);
      this.entityData.define(DATA_ID_INV, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Invul", this.getInvulnerableTicks());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setInvulnerableTicks(var1.getInt("Invul"));
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }
   }

   @Override
   public void setCustomName(@Nullable Component var1) {
      super.setCustomName(var1);
      this.bossEvent.setName(this.getDisplayName());
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.WITHER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_DEATH;
   }

   @Override
   public void aiStep() {
      Vec3 var1 = this.getDeltaMovement().multiply(1.0, 0.6, 1.0);
      if (!this.level.isClientSide && this.getAlternativeTarget(0) > 0) {
         Entity var2 = this.level.getEntity(this.getAlternativeTarget(0));
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

      for(int var21 = 0; var21 < 2; ++var21) {
         this.yRotOHeads[var21] = this.yRotHeads[var21];
         this.xRotOHeads[var21] = this.xRotHeads[var21];
      }

      for(int var22 = 0; var22 < 2; ++var22) {
         int var25 = this.getAlternativeTarget(var22 + 1);
         Entity var4 = null;
         if (var25 > 0) {
            var4 = this.level.getEntity(var25);
         }

         if (var4 != null) {
            double var29 = this.getHeadX(var22 + 1);
            double var7 = this.getHeadY(var22 + 1);
            double var9 = this.getHeadZ(var22 + 1);
            double var11 = var4.getX() - var29;
            double var13 = var4.getEyeY() - var7;
            double var15 = var4.getZ() - var9;
            double var17 = Math.sqrt(var11 * var11 + var15 * var15);
            float var19 = (float)(Mth.atan2(var15, var11) * 57.2957763671875) - 90.0F;
            float var20 = (float)(-(Mth.atan2(var13, var17) * 57.2957763671875));
            this.xRotHeads[var22] = this.rotlerp(this.xRotHeads[var22], var20, 40.0F);
            this.yRotHeads[var22] = this.rotlerp(this.yRotHeads[var22], var19, 10.0F);
         } else {
            this.yRotHeads[var22] = this.rotlerp(this.yRotHeads[var22], this.yBodyRot, 10.0F);
         }
      }

      boolean var23 = this.isPowered();

      for(int var26 = 0; var26 < 3; ++var26) {
         double var28 = this.getHeadX(var26);
         double var30 = this.getHeadY(var26);
         double var8 = this.getHeadZ(var26);
         this.level
            .addParticle(
               ParticleTypes.SMOKE,
               var28 + this.random.nextGaussian() * 0.30000001192092896,
               var30 + this.random.nextGaussian() * 0.30000001192092896,
               var8 + this.random.nextGaussian() * 0.30000001192092896,
               0.0,
               0.0,
               0.0
            );
         if (var23 && this.level.random.nextInt(4) == 0) {
            this.level
               .addParticle(
                  ParticleTypes.ENTITY_EFFECT,
                  var28 + this.random.nextGaussian() * 0.30000001192092896,
                  var30 + this.random.nextGaussian() * 0.30000001192092896,
                  var8 + this.random.nextGaussian() * 0.30000001192092896,
                  0.699999988079071,
                  0.699999988079071,
                  0.5
               );
         }
      }

      if (this.getInvulnerableTicks() > 0) {
         for(int var27 = 0; var27 < 3; ++var27) {
            this.level
               .addParticle(
                  ParticleTypes.ENTITY_EFFECT,
                  this.getX() + this.random.nextGaussian(),
                  this.getY() + (double)(this.random.nextFloat() * 3.3F),
                  this.getZ() + this.random.nextGaussian(),
                  0.699999988079071,
                  0.699999988079071,
                  0.8999999761581421
               );
         }
      }
   }

   @Override
   protected void customServerAiStep() {
      if (this.getInvulnerableTicks() > 0) {
         int var14 = this.getInvulnerableTicks() - 1;
         this.bossEvent.setProgress(1.0F - (float)var14 / 220.0F);
         if (var14 <= 0) {
            Explosion.BlockInteraction var17 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
               ? Explosion.BlockInteraction.DESTROY
               : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, var17);
            if (!this.isSilent()) {
               this.level.globalLevelEvent(1023, this.blockPosition(), 0);
            }
         }

         this.setInvulnerableTicks(var14);
         if (this.tickCount % 10 == 0) {
            this.heal(10.0F);
         }
      } else {
         super.customServerAiStep();

         for(int var1 = 1; var1 < 3; ++var1) {
            if (this.tickCount >= this.nextHeadUpdate[var1 - 1]) {
               this.nextHeadUpdate[var1 - 1] = this.tickCount + 10 + this.random.nextInt(10);
               if ((this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) && this.idleHeadUpdates[var1 - 1]++ > 15
                  )
                {
                  float var2 = 10.0F;
                  float var3 = 5.0F;
                  double var4 = Mth.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
                  double var6 = Mth.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
                  double var8 = Mth.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
                  this.performRangedAttack(var1 + 1, var4, var6, var8, true);
                  this.idleHeadUpdates[var1 - 1] = 0;
               }

               int var15 = this.getAlternativeTarget(var1);
               if (var15 > 0) {
                  LivingEntity var18 = (LivingEntity)this.level.getEntity(var15);
                  if (var18 != null && this.canAttack(var18) && !(this.distanceToSqr(var18) > 900.0) && this.hasLineOfSight(var18)) {
                     this.performRangedAttack(var1 + 1, var18);
                     this.nextHeadUpdate[var1 - 1] = this.tickCount + 40 + this.random.nextInt(20);
                     this.idleHeadUpdates[var1 - 1] = 0;
                  } else {
                     this.setAlternativeTarget(var1, 0);
                  }
               } else {
                  List var19 = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0, 8.0, 20.0));
                  if (!var19.isEmpty()) {
                     LivingEntity var21 = (LivingEntity)var19.get(this.random.nextInt(var19.size()));
                     this.setAlternativeTarget(var1, var21.getId());
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
            if (this.destroyBlocksTick == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               int var13 = Mth.floor(this.getY());
               int var16 = Mth.floor(this.getX());
               int var20 = Mth.floor(this.getZ());
               boolean var22 = false;

               for(int var5 = -1; var5 <= 1; ++var5) {
                  for(int var23 = -1; var23 <= 1; ++var23) {
                     for(int var7 = 0; var7 <= 3; ++var7) {
                        int var24 = var16 + var5;
                        int var9 = var13 + var7;
                        int var10 = var20 + var23;
                        BlockPos var11 = new BlockPos(var24, var9, var10);
                        BlockState var12 = this.level.getBlockState(var11);
                        if (canDestroy(var12)) {
                           var22 = this.level.destroyBlock(var11, true, this) || var22;
                        }
                     }
                  }
               }

               if (var22) {
                  this.level.levelEvent(null, 1022, this.blockPosition(), 0);
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

   @Override
   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
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

   private double getHeadX(int var1) {
      if (var1 <= 0) {
         return this.getX();
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (var1 - 1))) * 0.017453292F;
         float var3 = Mth.cos(var2);
         return this.getX() + (double)var3 * 1.3;
      }
   }

   private double getHeadY(int var1) {
      return var1 <= 0 ? this.getY() + 3.0 : this.getY() + 2.2;
   }

   private double getHeadZ(int var1) {
      if (var1 <= 0) {
         return this.getZ();
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (var1 - 1))) * 0.017453292F;
         float var3 = Mth.sin(var2);
         return this.getZ() + (double)var3 * 1.3;
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
         this.level.levelEvent(null, 1024, this.blockPosition(), 0);
      }

      double var9 = this.getHeadX(var1);
      double var11 = this.getHeadY(var1);
      double var13 = this.getHeadZ(var1);
      double var15 = var2 - var9;
      double var17 = var4 - var11;
      double var19 = var6 - var13;
      WitherSkull var21 = new WitherSkull(this.level, this, var15, var17, var19);
      var21.setOwner(this);
      if (var8) {
         var21.setDangerous(true);
      }

      var21.setPosRaw(var9, var11, var13);
      this.level.addFreshEntity(var21);
   }

   @Override
   public void performRangedAttack(LivingEntity var1, float var2) {
      this.performRangedAttack(0, var1);
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (var1 == DamageSource.DROWN || var1.getEntity() instanceof WitherBoss) {
         return false;
      } else if (this.getInvulnerableTicks() > 0 && var1 != DamageSource.OUT_OF_WORLD) {
         return false;
      } else {
         if (this.isPowered()) {
            Entity var3 = var1.getDirectEntity();
            if (var3 instanceof AbstractArrow) {
               return false;
            }
         }

         Entity var5 = var1.getEntity();
         if (var5 != null && !(var5 instanceof Player) && var5 instanceof LivingEntity && ((LivingEntity)var5).getMobType() == this.getMobType()) {
            return false;
         } else {
            if (this.destroyBlocksTick <= 0) {
               this.destroyBlocksTick = 20;
            }

            for(int var4 = 0; var4 < this.idleHeadUpdates.length; ++var4) {
               this.idleHeadUpdates[var4] += 3;
            }

            return super.hurt(var1, var2);
         }
      }
   }

   @Override
   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      ItemEntity var4 = this.spawnAtLocation(Items.NETHER_STAR);
      if (var4 != null) {
         var4.setExtendedLifetime();
      }
   }

   @Override
   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.discard();
      } else {
         this.noActionTime = 0;
      }
   }

   @Override
   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   @Override
   public boolean addEffect(MobEffectInstance var1, @Nullable Entity var2) {
      return false;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 300.0)
         .add(Attributes.MOVEMENT_SPEED, 0.6000000238418579)
         .add(Attributes.FLYING_SPEED, 0.6000000238418579)
         .add(Attributes.FOLLOW_RANGE, 40.0)
         .add(Attributes.ARMOR, 4.0);
   }

   public float getHeadYRot(int var1) {
      return this.yRotHeads[var1];
   }

   public float getHeadXRot(int var1) {
      return this.xRotHeads[var1];
   }

   public int getInvulnerableTicks() {
      return this.entityData.get(DATA_ID_INV);
   }

   public void setInvulnerableTicks(int var1) {
      this.entityData.set(DATA_ID_INV, var1);
   }

   public int getAlternativeTarget(int var1) {
      return this.entityData.get(DATA_TARGETS.get(var1));
   }

   public void setAlternativeTarget(int var1, int var2) {
      this.entityData.set(DATA_TARGETS.get(var1), var2);
   }

   @Override
   public boolean isPowered() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   @Override
   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   @Override
   protected boolean canRide(Entity var1) {
      return false;
   }

   @Override
   public boolean canChangeDimensions() {
      return false;
   }

   @Override
   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(var1);
   }

   class WitherDoNothingGoal extends Goal {
      public WitherDoNothingGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         return WitherBoss.this.getInvulnerableTicks() > 0;
      }
   }
}
