package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EnderMan extends Monster {
   private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING;
   private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE;
   private static final EntityDataAccessor<Boolean> DATA_CREEPY;
   private static final Predicate<LivingEntity> ENDERMITE_SELECTOR;
   private int lastCreepySound;
   private int targetChangeTime;

   public EnderMan(EntityType<? extends EnderMan> var1, Level var2) {
      super(var1, var2);
      this.maxUpStep = 1.0F;
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EnderMan.EndermanFreezeWhenLookedAt(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(10, new EnderMan.EndermanLeaveBlockGoal(this));
      this.goalSelector.addGoal(11, new EnderMan.EndermanTakeBlockGoal(this));
      this.targetSelector.addGoal(1, new EnderMan.EndermanLookForPlayerGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Endermite.class, 10, true, false, ENDERMITE_SELECTOR));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
   }

   public void setTarget(@Nullable LivingEntity var1) {
      super.setTarget(var1);
      AttributeInstance var2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (var1 == null) {
         this.targetChangeTime = 0;
         this.entityData.set(DATA_CREEPY, false);
         var2.removeModifier(SPEED_MODIFIER_ATTACKING);
      } else {
         this.targetChangeTime = this.tickCount;
         this.entityData.set(DATA_CREEPY, true);
         if (!var2.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            var2.addModifier(SPEED_MODIFIER_ATTACKING);
         }
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CARRY_STATE, Optional.empty());
      this.entityData.define(DATA_CREEPY, false);
   }

   public void playCreepySound() {
      if (this.tickCount >= this.lastCreepySound + 400) {
         this.lastCreepySound = this.tickCount;
         if (!this.isSilent()) {
            this.level.playLocalSound(this.x, this.y + (double)this.getEyeHeight(), this.z, SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5F, 1.0F, false);
         }
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_CREEPY.equals(var1) && this.isCreepy() && this.level.isClientSide) {
         this.playCreepySound();
      }

      super.onSyncedDataUpdated(var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      BlockState var2 = this.getCarriedBlock();
      if (var2 != null) {
         var1.put("carriedBlockState", NbtUtils.writeBlockState(var2));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      BlockState var2 = null;
      if (var1.contains("carriedBlockState", 10)) {
         var2 = NbtUtils.readBlockState(var1.getCompound("carriedBlockState"));
         if (var2.isAir()) {
            var2 = null;
         }
      }

      this.setCarriedBlock(var2);
   }

   private boolean isLookingAtMe(Player var1) {
      ItemStack var2 = (ItemStack)var1.inventory.armor.get(3);
      if (var2.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
         return false;
      } else {
         Vec3 var3 = var1.getViewVector(1.0F).normalize();
         Vec3 var4 = new Vec3(this.x - var1.x, this.getBoundingBox().minY + (double)this.getEyeHeight() - (var1.y + (double)var1.getEyeHeight()), this.z - var1.z);
         double var5 = var4.length();
         var4 = var4.normalize();
         double var7 = var3.dot(var4);
         return var7 > 1.0D - 0.025D / var5 ? var1.canSee(this) : false;
      }
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 2.55F;
   }

   public void aiStep() {
      if (this.level.isClientSide) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.level.addParticle(ParticleTypes.PORTAL, this.x + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), this.y + this.random.nextDouble() * (double)this.getBbHeight() - 0.25D, this.z + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
         }
      }

      this.jumping = false;
      super.aiStep();
   }

   protected void customServerAiStep() {
      if (this.isInWaterRainOrBubble()) {
         this.hurt(DamageSource.DROWN, 1.0F);
      }

      if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600) {
         float var1 = this.getBrightness();
         if (var1 > 0.5F && this.level.canSeeSky(new BlockPos(this)) && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.setTarget((LivingEntity)null);
            this.teleport();
         }
      }

      super.customServerAiStep();
   }

   protected boolean teleport() {
      double var1 = this.x + (this.random.nextDouble() - 0.5D) * 64.0D;
      double var3 = this.y + (double)(this.random.nextInt(64) - 32);
      double var5 = this.z + (this.random.nextDouble() - 0.5D) * 64.0D;
      return this.teleport(var1, var3, var5);
   }

   private boolean teleportTowards(Entity var1) {
      Vec3 var2 = new Vec3(this.x - var1.x, this.getBoundingBox().minY + (double)(this.getBbHeight() / 2.0F) - var1.y + (double)var1.getEyeHeight(), this.z - var1.z);
      var2 = var2.normalize();
      double var3 = 16.0D;
      double var5 = this.x + (this.random.nextDouble() - 0.5D) * 8.0D - var2.x * 16.0D;
      double var7 = this.y + (double)(this.random.nextInt(16) - 8) - var2.y * 16.0D;
      double var9 = this.z + (this.random.nextDouble() - 0.5D) * 8.0D - var2.z * 16.0D;
      return this.teleport(var5, var7, var9);
   }

   private boolean teleport(double var1, double var3, double var5) {
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos(var1, var3, var5);

      while(var7.getY() > 0 && !this.level.getBlockState(var7).getMaterial().blocksMotion()) {
         var7.move(Direction.DOWN);
      }

      if (!this.level.getBlockState(var7).getMaterial().blocksMotion()) {
         return false;
      } else {
         boolean var8 = this.randomTeleport(var1, var3, var5, true);
         if (var8) {
            this.level.playSound((Player)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
         }

         return var8;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMAN_DEATH;
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      BlockState var4 = this.getCarriedBlock();
      if (var4 != null) {
         this.spawnAtLocation(var4.getBlock());
      }

   }

   public void setCarriedBlock(@Nullable BlockState var1) {
      this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable(var1));
   }

   @Nullable
   public BlockState getCarriedBlock() {
      return (BlockState)((Optional)this.entityData.get(DATA_CARRY_STATE)).orElse((Object)null);
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (!(var1 instanceof IndirectEntityDamageSource) && var1 != DamageSource.FIREWORKS) {
         boolean var4 = super.hurt(var1, var2);
         if (var1.isBypassArmor() && this.random.nextInt(10) != 0) {
            this.teleport();
         }

         return var4;
      } else {
         for(int var3 = 0; var3 < 64; ++var3) {
            if (this.teleport()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isCreepy() {
      return (Boolean)this.entityData.get(DATA_CREEPY);
   }

   static {
      SPEED_MODIFIER_ATTACKING = (new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION)).setSerialize(false);
      DATA_CARRY_STATE = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BLOCK_STATE);
      DATA_CREEPY = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BOOLEAN);
      ENDERMITE_SELECTOR = (var0) -> {
         return var0 instanceof Endermite && ((Endermite)var0).isPlayerSpawned();
      };
   }

   static class EndermanTakeBlockGoal extends Goal {
      private final EnderMan enderman;

      public EndermanTakeBlockGoal(EnderMan var1) {
         super();
         this.enderman = var1;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() != null) {
            return false;
         } else if (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(20) == 0;
         }
      }

      public void tick() {
         Random var1 = this.enderman.getRandom();
         Level var2 = this.enderman.level;
         int var3 = Mth.floor(this.enderman.x - 2.0D + var1.nextDouble() * 4.0D);
         int var4 = Mth.floor(this.enderman.y + var1.nextDouble() * 3.0D);
         int var5 = Mth.floor(this.enderman.z - 2.0D + var1.nextDouble() * 4.0D);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         BlockState var7 = var2.getBlockState(var6);
         Block var8 = var7.getBlock();
         Vec3 var9 = new Vec3((double)Mth.floor(this.enderman.x) + 0.5D, (double)var4 + 0.5D, (double)Mth.floor(this.enderman.z) + 0.5D);
         Vec3 var10 = new Vec3((double)var3 + 0.5D, (double)var4 + 0.5D, (double)var5 + 0.5D);
         BlockHitResult var11 = var2.clip(new ClipContext(var9, var10, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.enderman));
         boolean var12 = var11.getType() != HitResult.Type.MISS && var11.getBlockPos().equals(var6);
         if (var8.is(BlockTags.ENDERMAN_HOLDABLE) && var12) {
            this.enderman.setCarriedBlock(var7);
            var2.removeBlock(var6, false);
         }

      }
   }

   static class EndermanLeaveBlockGoal extends Goal {
      private final EnderMan enderman;

      public EndermanLeaveBlockGoal(EnderMan var1) {
         super();
         this.enderman = var1;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() == null) {
            return false;
         } else if (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(2000) == 0;
         }
      }

      public void tick() {
         Random var1 = this.enderman.getRandom();
         Level var2 = this.enderman.level;
         int var3 = Mth.floor(this.enderman.x - 1.0D + var1.nextDouble() * 2.0D);
         int var4 = Mth.floor(this.enderman.y + var1.nextDouble() * 2.0D);
         int var5 = Mth.floor(this.enderman.z - 1.0D + var1.nextDouble() * 2.0D);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         BlockState var7 = var2.getBlockState(var6);
         BlockPos var8 = var6.below();
         BlockState var9 = var2.getBlockState(var8);
         BlockState var10 = this.enderman.getCarriedBlock();
         if (var10 != null && this.canPlaceBlock(var2, var6, var10, var7, var9, var8)) {
            var2.setBlock(var6, var10, 3);
            this.enderman.setCarriedBlock((BlockState)null);
         }

      }

      private boolean canPlaceBlock(LevelReader var1, BlockPos var2, BlockState var3, BlockState var4, BlockState var5, BlockPos var6) {
         return var4.isAir() && !var5.isAir() && var5.isCollisionShapeFullBlock(var1, var6) && var3.canSurvive(var1, var2);
      }
   }

   static class EndermanFreezeWhenLookedAt extends Goal {
      private final EnderMan enderman;

      public EndermanFreezeWhenLookedAt(EnderMan var1) {
         super();
         this.enderman = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         LivingEntity var1 = this.enderman.getTarget();
         if (!(var1 instanceof Player)) {
            return false;
         } else {
            double var2 = var1.distanceToSqr(this.enderman);
            return var2 > 256.0D ? false : this.enderman.isLookingAtMe((Player)var1);
         }
      }

      public void start() {
         this.enderman.getNavigation().stop();
      }
   }

   static class EndermanLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
      private final EnderMan enderman;
      private Player pendingTarget;
      private int aggroTime;
      private int teleportTime;
      private final TargetingConditions startAggroTargetConditions;
      private final TargetingConditions continueAggroTargetConditions = (new TargetingConditions()).allowUnseeable();

      public EndermanLookForPlayerGoal(EnderMan var1) {
         super(var1, Player.class, false);
         this.enderman = var1;
         this.startAggroTargetConditions = (new TargetingConditions()).range(this.getFollowDistance()).selector((var1x) -> {
            return var1.isLookingAtMe((Player)var1x);
         });
      }

      public boolean canUse() {
         this.pendingTarget = this.enderman.level.getNearestPlayer(this.startAggroTargetConditions, this.enderman);
         return this.pendingTarget != null;
      }

      public void start() {
         this.aggroTime = 5;
         this.teleportTime = 0;
      }

      public void stop() {
         this.pendingTarget = null;
         super.stop();
      }

      public boolean canContinueToUse() {
         if (this.pendingTarget != null) {
            if (!this.enderman.isLookingAtMe(this.pendingTarget)) {
               return false;
            } else {
               this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
               return true;
            }
         } else {
            return this.target != null && this.continueAggroTargetConditions.test(this.enderman, this.target) ? true : super.canContinueToUse();
         }
      }

      public void tick() {
         if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
               this.target = this.pendingTarget;
               this.pendingTarget = null;
               super.start();
            }
         } else {
            if (this.target != null && !this.enderman.isPassenger()) {
               if (this.enderman.isLookingAtMe((Player)this.target)) {
                  if (this.target.distanceToSqr(this.enderman) < 16.0D) {
                     this.enderman.teleport();
                  }

                  this.teleportTime = 0;
               } else if (this.target.distanceToSqr(this.enderman) > 256.0D && this.teleportTime++ >= 30 && this.enderman.teleportTowards(this.target)) {
                  this.teleportTime = 0;
               }
            }

            super.tick();
         }

      }
   }
}
