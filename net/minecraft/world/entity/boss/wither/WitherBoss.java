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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
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
   private static final EntityDataAccessor DATA_TARGET_A;
   private static final EntityDataAccessor DATA_TARGET_B;
   private static final EntityDataAccessor DATA_TARGET_C;
   private static final List DATA_TARGETS;
   private static final EntityDataAccessor DATA_ID_INV;
   private final float[] xRotHeads = new float[2];
   private final float[] yRotHeads = new float[2];
   private final float[] xRotOHeads = new float[2];
   private final float[] yRotOHeads = new float[2];
   private final int[] nextHeadUpdate = new int[2];
   private final int[] idleHeadUpdates = new int[2];
   private int destroyBlocksTick;
   private final ServerBossEvent bossEvent;
   private static final Predicate LIVING_ENTITY_SELECTOR;
   private static final TargetingConditions TARGETING_CONDITIONS;

   public WitherBoss(EntityType var1, Level var2) {
      super(var1, var2);
      this.bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
      this.setHealth(this.getMaxHealth());
      this.getNavigation().setCanFloat(true);
      this.xpReward = 50;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new WitherBoss.WitherDoNothingGoal());
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Mob.class, 0, false, false, LIVING_ENTITY_SELECTOR));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TARGET_A, 0);
      this.entityData.define(DATA_TARGET_B, 0);
      this.entityData.define(DATA_TARGET_C, 0);
      this.entityData.define(DATA_ID_INV, 0);
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
      Vec3 var1 = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
      if (!this.level.isClientSide && this.getAlternativeTarget(0) > 0) {
         Entity var2 = this.level.getEntity(this.getAlternativeTarget(0));
         if (var2 != null) {
            double var3 = var1.y;
            if (this.getY() < var2.getY() || !this.isPowered() && this.getY() < var2.getY() + 5.0D) {
               var3 = Math.max(0.0D, var3);
               var3 += 0.3D - var3 * 0.6000000238418579D;
            }

            var1 = new Vec3(var1.x, var3, var1.z);
            Vec3 var5 = new Vec3(var2.getX() - this.getX(), 0.0D, var2.getZ() - this.getZ());
            if (getHorizontalDistanceSqr(var5) > 9.0D) {
               Vec3 var6 = var5.normalize();
               var1 = var1.add(var6.x * 0.3D - var1.x * 0.6D, 0.0D, var6.z * 0.3D - var1.z * 0.6D);
            }
         }
      }

      this.setDeltaMovement(var1);
      if (getHorizontalDistanceSqr(var1) > 0.05D) {
         this.yRot = (float)Mth.atan2(var1.z, var1.x) * 57.295776F - 90.0F;
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
            var4 = this.level.getEntity(var23);
         }

         if (var4 != null) {
            double var25 = this.getHeadX(var21 + 1);
            double var7 = this.getHeadY(var21 + 1);
            double var9 = this.getHeadZ(var21 + 1);
            double var11 = var4.getX() - var25;
            double var13 = var4.getEyeY() - var7;
            double var15 = var4.getZ() - var9;
            double var17 = (double)Mth.sqrt(var11 * var11 + var15 * var15);
            float var19 = (float)(Mth.atan2(var15, var11) * 57.2957763671875D) - 90.0F;
            float var20 = (float)(-(Mth.atan2(var13, var17) * 57.2957763671875D));
            this.xRotHeads[var21] = this.rotlerp(this.xRotHeads[var21], var20, 40.0F);
            this.yRotHeads[var21] = this.rotlerp(this.yRotHeads[var21], var19, 10.0F);
         } else {
            this.yRotHeads[var21] = this.rotlerp(this.yRotHeads[var21], this.yBodyRot, 10.0F);
         }
      }

      boolean var22 = this.isPowered();

      for(var23 = 0; var23 < 3; ++var23) {
         double var24 = this.getHeadX(var23);
         double var26 = this.getHeadY(var23);
         double var8 = this.getHeadZ(var23);
         this.level.addParticle(ParticleTypes.SMOKE, var24 + this.random.nextGaussian() * 0.30000001192092896D, var26 + this.random.nextGaussian() * 0.30000001192092896D, var8 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
         if (var22 && this.level.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, var24 + this.random.nextGaussian() * 0.30000001192092896D, var26 + this.random.nextGaussian() * 0.30000001192092896D, var8 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
         }
      }

      if (this.getInvulnerableTicks() > 0) {
         for(var23 = 0; var23 < 3; ++var23) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
         }
      }

   }

   protected void customServerAiStep() {
      int var1;
      if (this.getInvulnerableTicks() > 0) {
         var1 = this.getInvulnerableTicks() - 1;
         if (var1 <= 0) {
            Explosion.BlockInteraction var14 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, var14);
            this.level.globalLevelEvent(1023, new BlockPos(this), 0);
         }

         this.setInvulnerableTicks(var1);
         if (this.tickCount % 10 == 0) {
            this.heal(10.0F);
         }

      } else {
         super.customServerAiStep();

         int var13;
         for(var1 = 1; var1 < 3; ++var1) {
            if (this.tickCount >= this.nextHeadUpdate[var1 - 1]) {
               this.nextHeadUpdate[var1 - 1] = this.tickCount + 10 + this.random.nextInt(10);
               if (this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) {
                  int[] var10000 = this.idleHeadUpdates;
                  int var10001 = var1 - 1;
                  int var10003 = var10000[var1 - 1];
                  var10000[var10001] = var10000[var1 - 1] + 1;
                  if (var10003 > 15) {
                     float var2 = 10.0F;
                     float var3 = 5.0F;
                     double var4 = Mth.nextDouble(this.random, this.getX() - 10.0D, this.getX() + 10.0D);
                     double var6 = Mth.nextDouble(this.random, this.getY() - 5.0D, this.getY() + 5.0D);
                     double var8 = Mth.nextDouble(this.random, this.getZ() - 10.0D, this.getZ() + 10.0D);
                     this.performRangedAttack(var1 + 1, var4, var6, var8, true);
                     this.idleHeadUpdates[var1 - 1] = 0;
                  }
               }

               var13 = this.getAlternativeTarget(var1);
               if (var13 > 0) {
                  Entity var16 = this.level.getEntity(var13);
                  if (var16 != null && var16.isAlive() && this.distanceToSqr(var16) <= 900.0D && this.canSee(var16)) {
                     if (var16 instanceof Player && ((Player)var16).abilities.invulnerable) {
                        this.setAlternativeTarget(var1, 0);
                     } else {
                        this.performRangedAttack(var1 + 1, (LivingEntity)var16);
                        this.nextHeadUpdate[var1 - 1] = this.tickCount + 40 + this.random.nextInt(20);
                        this.idleHeadUpdates[var1 - 1] = 0;
                     }
                  } else {
                     this.setAlternativeTarget(var1, 0);
                  }
               } else {
                  List var15 = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));

                  for(int var18 = 0; var18 < 10 && !var15.isEmpty(); ++var18) {
                     LivingEntity var5 = (LivingEntity)var15.get(this.random.nextInt(var15.size()));
                     if (var5 != this && var5.isAlive() && this.canSee(var5)) {
                        if (var5 instanceof Player) {
                           if (!((Player)var5).abilities.invulnerable) {
                              this.setAlternativeTarget(var1, var5.getId());
                           }
                        } else {
                           this.setAlternativeTarget(var1, var5.getId());
                        }
                        break;
                     }

                     var15.remove(var5);
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
               var1 = Mth.floor(this.getY());
               var13 = Mth.floor(this.getX());
               int var17 = Mth.floor(this.getZ());
               boolean var19 = false;

               for(int var20 = -1; var20 <= 1; ++var20) {
                  for(int var21 = -1; var21 <= 1; ++var21) {
                     for(int var7 = 0; var7 <= 3; ++var7) {
                        int var22 = var13 + var20;
                        int var9 = var1 + var7;
                        int var10 = var17 + var21;
                        BlockPos var11 = new BlockPos(var22, var9, var10);
                        BlockState var12 = this.level.getBlockState(var11);
                        if (canDestroy(var12)) {
                           var19 = this.level.destroyBlock(var11, true, this) || var19;
                        }
                     }
                  }
               }

               if (var19) {
                  this.level.levelEvent((Player)null, 1022, new BlockPos(this), 0);
               }
            }
         }

         if (this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
      }
   }

   public static boolean canDestroy(BlockState var0) {
      return !var0.isAir() && !BlockTags.WITHER_IMMUNE.contains(var0.getBlock());
   }

   public void makeInvulnerable() {
      this.setInvulnerableTicks(220);
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
         return this.getX() + (double)var3 * 1.3D;
      }
   }

   private double getHeadY(int var1) {
      return var1 <= 0 ? this.getY() + 3.0D : this.getY() + 2.2D;
   }

   private double getHeadZ(int var1) {
      if (var1 <= 0) {
         return this.getZ();
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (var1 - 1))) * 0.017453292F;
         float var3 = Mth.sin(var2);
         return this.getZ() + (double)var3 * 1.3D;
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
      this.performRangedAttack(var1, var2.getX(), var2.getY() + (double)var2.getEyeHeight() * 0.5D, var2.getZ(), var1 == 0 && this.random.nextFloat() < 0.001F);
   }

   private void performRangedAttack(int var1, double var2, double var4, double var6, boolean var8) {
      this.level.levelEvent((Player)null, 1024, new BlockPos(this), 0);
      double var9 = this.getHeadX(var1);
      double var11 = this.getHeadY(var1);
      double var13 = this.getHeadZ(var1);
      double var15 = var2 - var9;
      double var17 = var4 - var11;
      double var19 = var6 - var13;
      WitherSkull var21 = new WitherSkull(this.level, this, var15, var17, var19);
      if (var8) {
         var21.setDangerous(true);
      }

      var21.setPosRaw(var9, var11, var13);
      this.level.addFreshEntity(var21);
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      this.performRangedAttack(0, var1);
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (var1 != DamageSource.DROWN && !(var1.getEntity() instanceof WitherBoss)) {
         if (this.getInvulnerableTicks() > 0 && var1 != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            Entity var3;
            if (this.isPowered()) {
               var3 = var1.getDirectEntity();
               if (var3 instanceof AbstractArrow) {
                  return false;
               }
            }

            var3 = var1.getEntity();
            if (var3 != null && !(var3 instanceof Player) && var3 instanceof LivingEntity && ((LivingEntity)var3).getMobType() == this.getMobType()) {
               return false;
            } else {
               if (this.destroyBlocksTick <= 0) {
                  this.destroyBlocksTick = 20;
               }

               for(int var4 = 0; var4 < this.idleHeadUpdates.length; ++var4) {
                  int[] var10000 = this.idleHeadUpdates;
                  var10000[var4] += 3;
               }

               return super.hurt(var1, var2);
            }
         }
      } else {
         return false;
      }
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      ItemEntity var4 = this.spawnAtLocation(Items.NETHER_STAR);
      if (var4 != null) {
         var4.setExtendedLifetime();
      }

   }

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.remove();
      } else {
         this.noActionTime = 0;
      }
   }

   public boolean causeFallDamage(float var1, float var2) {
      return false;
   }

   public boolean addEffect(MobEffectInstance var1) {
      return false;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6000000238418579D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
   }

   public float getHeadYRot(int var1) {
      return this.yRotHeads[var1];
   }

   public float getHeadXRot(int var1) {
      return this.xRotHeads[var1];
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

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected boolean canRide(Entity var1) {
      return false;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance var1) {
      return var1.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(var1);
   }

   static {
      DATA_TARGET_A = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      DATA_TARGET_B = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      DATA_TARGET_C = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
      DATA_ID_INV = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
      LIVING_ENTITY_SELECTOR = (var0) -> {
         return var0.getMobType() != MobType.UNDEAD && var0.attackable();
      };
      TARGETING_CONDITIONS = (new TargetingConditions()).range(20.0D).selector(LIVING_ENTITY_SELECTOR);
   }

   class WitherDoNothingGoal extends Goal {
      public WitherDoNothingGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return WitherBoss.this.getInvulnerableTicks() > 0;
      }
   }
}
