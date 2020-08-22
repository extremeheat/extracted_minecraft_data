package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class Slime extends Mob implements Enemy {
   private static final EntityDataAccessor ID_SIZE;
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround;

   public Slime(EntityType var1, Level var2) {
      super(var1, var2);
      this.moveControl = new Slime.SlimeMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Slime.SlimeFloatGoal(this));
      this.goalSelector.addGoal(2, new Slime.SlimeAttackGoal(this));
      this.goalSelector.addGoal(3, new Slime.SlimeRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new Slime.SlimeKeepOnJumpingGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (var1) -> {
         return Math.abs(var1.getY() - this.getY()) <= 4.0D;
      }));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_SIZE, 1);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected void setSize(int var1, boolean var2) {
      this.entityData.set(ID_SIZE, var1);
      this.reapplyPosition();
      this.refreshDimensions();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(var1 * var1));
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)var1));
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)var1);
      if (var2) {
         this.setHealth(this.getMaxHealth());
      }

      this.xpReward = var1;
   }

   public int getSize() {
      return (Integer)this.entityData.get(ID_SIZE);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Size", this.getSize() - 1);
      var1.putBoolean("wasOnGround", this.wasOnGround);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      int var2 = var1.getInt("Size");
      if (var2 < 0) {
         var2 = 0;
      }

      this.setSize(var2 + 1, false);
      super.readAdditionalSaveData(var1);
      this.wasOnGround = var1.getBoolean("wasOnGround");
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.ITEM_SLIME;
   }

   protected boolean shouldDespawnInPeaceful() {
      return this.getSize() > 0;
   }

   public void tick() {
      this.squish += (this.targetSquish - this.squish) * 0.5F;
      this.oSquish = this.squish;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int var1 = this.getSize();

         for(int var2 = 0; var2 < var1 * 8; ++var2) {
            float var3 = this.random.nextFloat() * 6.2831855F;
            float var4 = this.random.nextFloat() * 0.5F + 0.5F;
            float var5 = Mth.sin(var3) * (float)var1 * 0.5F * var4;
            float var6 = Mth.cos(var3) * (float)var1 * 0.5F * var4;
            this.level.addParticle(this.getParticleType(), this.getX() + (double)var5, this.getY(), this.getZ() + (double)var6, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.targetSquish = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.targetSquish = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.decreaseSquish();
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.random.nextInt(20) + 10;
   }

   public void refreshDimensions() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   public void onSyncedDataUpdated(EntityDataAccessor var1) {
      if (ID_SIZE.equals(var1)) {
         this.refreshDimensions();
         this.yRot = this.yHeadRot;
         this.yBodyRot = this.yHeadRot;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   public EntityType getType() {
      return super.getType();
   }

   public void remove() {
      int var1 = this.getSize();
      if (!this.level.isClientSide && var1 > 1 && this.getHealth() <= 0.0F) {
         int var2 = 2 + this.random.nextInt(3);

         for(int var3 = 0; var3 < var2; ++var3) {
            float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 4.0F;
            float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 4.0F;
            Slime var6 = (Slime)this.getType().create(this.level);
            if (this.hasCustomName()) {
               var6.setCustomName(this.getCustomName());
            }

            if (this.isPersistenceRequired()) {
               var6.setPersistenceRequired();
            }

            var6.setInvulnerable(this.isInvulnerable());
            var6.setSize(var1 / 2, true);
            var6.moveTo(this.getX() + (double)var4, this.getY() + 0.5D, this.getZ() + (double)var5, this.random.nextFloat() * 360.0F, 0.0F);
            this.level.addFreshEntity(var6);
         }
      }

      super.remove();
   }

   public void push(Entity var1) {
      super.push(var1);
      if (var1 instanceof IronGolem && this.isDealsDamage()) {
         this.dealDamage((LivingEntity)var1);
      }

   }

   public void playerTouch(Player var1) {
      if (this.isDealsDamage()) {
         this.dealDamage(var1);
      }

   }

   protected void dealDamage(LivingEntity var1) {
      if (this.isAlive()) {
         int var2 = this.getSize();
         if (this.distanceToSqr(var1) < 0.6D * (double)var2 * 0.6D * (double)var2 && this.canSee(var1) && var1.hurt(DamageSource.mobAttack(this), this.getAttackDamage())) {
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.doEnchantDamageEffects(this, var1);
         }
      }

   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.625F * var2.height;
   }

   protected boolean isDealsDamage() {
      return !this.isTiny() && this.isEffectiveAi();
   }

   protected float getAttackDamage() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
   }

   protected ResourceLocation getDefaultLootTable() {
      return this.getSize() == 1 ? this.getType().getDefaultLootTable() : BuiltInLootTables.EMPTY;
   }

   public static boolean checkSlimeSpawnRules(EntityType var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      if (var1.getLevelData().getGeneratorType() == LevelType.FLAT && var4.nextInt(4) != 1) {
         return false;
      } else {
         if (var1.getDifficulty() != Difficulty.PEACEFUL) {
            Biome var5 = var1.getBiome(var3);
            if (var5 == Biomes.SWAMP && var3.getY() > 50 && var3.getY() < 70 && var4.nextFloat() < 0.5F && var4.nextFloat() < var1.getMoonBrightness() && var1.getMaxLocalRawBrightness(var3) <= var4.nextInt(8)) {
               return checkMobSpawnRules(var0, var1, var2, var3, var4);
            }

            ChunkPos var6 = new ChunkPos(var3);
            boolean var7 = WorldgenRandom.seedSlimeChunk(var6.x, var6.z, var1.getSeed(), 987234911L).nextInt(10) == 0;
            if (var4.nextInt(10) == 0 && var7 && var3.getY() < 40) {
               return checkMobSpawnRules(var0, var1, var2, var3, var4);
            }
         }

         return false;
      }
   }

   protected float getSoundVolume() {
      return 0.4F * (float)this.getSize();
   }

   public int getMaxHeadXRot() {
      return 0;
   }

   protected boolean doPlayJumpSound() {
      return this.getSize() > 0;
   }

   protected void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x, (double)this.getJumpPower(), var1.z);
      this.hasImpulse = true;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      int var6 = this.random.nextInt(3);
      if (var6 < 2 && this.random.nextFloat() < 0.5F * var2.getSpecialMultiplier()) {
         ++var6;
      }

      int var7 = 1 << var6;
      this.setSize(var7, true);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
   }

   public EntityDimensions getDimensions(Pose var1) {
      return super.getDimensions(var1).scale(0.255F * (float)this.getSize());
   }

   static {
      ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);
   }

   static class SlimeKeepOnJumpingGoal extends Goal {
      private final Slime slime;

      public SlimeKeepOnJumpingGoal(Slime var1) {
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !this.slime.isPassenger();
      }

      public void tick() {
         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.0D);
      }
   }

   static class SlimeFloatGoal extends Goal {
      private final Slime slime;

      public SlimeFloatGoal(Slime var1) {
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         var1.getNavigation().setCanFloat(true);
      }

      public boolean canUse() {
         return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      public void tick() {
         if (this.slime.getRandom().nextFloat() < 0.8F) {
            this.slime.getJumpControl().jump();
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setWantedMovement(1.2D);
      }
   }

   static class SlimeRandomDirectionGoal extends Goal {
      private final Slime slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public SlimeRandomDirectionGoal(Slime var1) {
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
      }

      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.getRandom().nextInt(60);
            this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
         }

         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.chosenDegrees, false);
      }
   }

   static class SlimeAttackGoal extends Goal {
      private final Slime slime;
      private int growTiredTimer;

      public SlimeAttackGoal(Slime var1) {
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = this.slime.getTarget();
         if (var1 == null) {
            return false;
         } else if (!var1.isAlive()) {
            return false;
         } else {
            return var1 instanceof Player && ((Player)var1).abilities.invulnerable ? false : this.slime.getMoveControl() instanceof Slime.SlimeMoveControl;
         }
      }

      public void start() {
         this.growTiredTimer = 300;
         super.start();
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = this.slime.getTarget();
         if (var1 == null) {
            return false;
         } else if (!var1.isAlive()) {
            return false;
         } else if (var1 instanceof Player && ((Player)var1).abilities.invulnerable) {
            return false;
         } else {
            return --this.growTiredTimer > 0;
         }
      }

      public void tick() {
         this.slime.lookAt(this.slime.getTarget(), 10.0F, 10.0F);
         ((Slime.SlimeMoveControl)this.slime.getMoveControl()).setDirection(this.slime.yRot, this.slime.isDealsDamage());
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
         this.yRot = 180.0F * var1.yRot / 3.1415927F;
      }

      public void setDirection(float var1, boolean var2) {
         this.yRot = var1;
         this.isAggressive = var2;
      }

      public void setWantedMovement(double var1) {
         this.speedModifier = var1;
         this.operation = MoveControl.Operation.MOVE_TO;
      }

      public void tick() {
         this.mob.yRot = this.rotlerp(this.mob.yRot, this.yRot, 90.0F);
         this.mob.yHeadRot = this.mob.yRot;
         this.mob.yBodyRot = this.mob.yRot;
         if (this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
         } else {
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround) {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpControl().jump();
                  if (this.slime.doPlayJumpSound()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRandom().nextFloat() - this.slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.slime.xxa = 0.0F;
                  this.slime.zza = 0.0F;
                  this.mob.setSpeed(0.0F);
               }
            } else {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
            }

         }
      }
   }
}
