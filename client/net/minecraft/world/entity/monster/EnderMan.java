package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EnderMan extends Monster implements NeutralMob {
   private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(
      SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.15000000596046448, AttributeModifier.Operation.ADD_VALUE
   );
   private static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
   private static final int MIN_DEAGGRESSION_TIME = 600;
   private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE = SynchedEntityData.defineId(
      EnderMan.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
   );
   private static final EntityDataAccessor<Boolean> DATA_CREEPY = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_STARED_AT = SynchedEntityData.defineId(EnderMan.class, EntityDataSerializers.BOOLEAN);
   private int lastStareSound = -2147483648;
   private int targetChangeTime;
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   private int remainingPersistentAngerTime;
   @Nullable
   private UUID persistentAngerTarget;

   public EnderMan(EntityType<? extends EnderMan> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EnderMan.EndermanFreezeWhenLookedAt(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(10, new EnderMan.EndermanLeaveBlockGoal(this));
      this.goalSelector.addGoal(11, new EnderMan.EndermanTakeBlockGoal(this));
      this.targetSelector.addGoal(1, new EnderMan.EndermanLookForPlayerGoal(this, this::isAngryAt));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true, false));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.ATTACK_DAMAGE, 7.0)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Override
   public void setTarget(@Nullable LivingEntity var1) {
      super.setTarget(var1);
      AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (var1 == null) {
         this.targetChangeTime = 0;
         this.entityData.set(DATA_CREEPY, false);
         this.entityData.set(DATA_STARED_AT, false);
         var2.removeModifier(SPEED_MODIFIER_ATTACKING.id());
      } else {
         this.targetChangeTime = this.tickCount;
         this.entityData.set(DATA_CREEPY, true);
         if (!var2.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            var2.addTransientModifier(SPEED_MODIFIER_ATTACKING);
         }
      }
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_CARRY_STATE, Optional.empty());
      var1.define(DATA_CREEPY, false);
      var1.define(DATA_STARED_AT, false);
   }

   @Override
   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   @Override
   public void setRemainingPersistentAngerTime(int var1) {
      this.remainingPersistentAngerTime = var1;
   }

   @Override
   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   @Override
   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   @Nullable
   @Override
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void playStareSound() {
      if (this.tickCount >= this.lastStareSound + 400) {
         this.lastStareSound = this.tickCount;
         if (!this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5F, 1.0F, false);
         }
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_CREEPY.equals(var1) && this.hasBeenStaredAt() && this.level().isClientSide) {
         this.playStareSound();
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      BlockState var2 = this.getCarriedBlock();
      if (var2 != null) {
         var1.put("carriedBlockState", NbtUtils.writeBlockState(var2));
      }

      this.addPersistentAngerSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      BlockState var2 = null;
      if (var1.contains("carriedBlockState", 10)) {
         var2 = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("carriedBlockState"));
         if (var2.isAir()) {
            var2 = null;
         }
      }

      this.setCarriedBlock(var2);
      this.readPersistentAngerSaveData(this.level(), var1);
   }

   boolean isLookingAtMe(Player var1) {
      ItemStack var2 = var1.getInventory().armor.get(3);
      if (var2.is(Blocks.CARVED_PUMPKIN.asItem())) {
         return false;
      } else {
         Vec3 var3 = var1.getViewVector(1.0F).normalize();
         Vec3 var4 = new Vec3(this.getX() - var1.getX(), this.getEyeY() - var1.getEyeY(), this.getZ() - var1.getZ());
         double var5 = var4.length();
         var4 = var4.normalize();
         double var7 = var3.dot(var4);
         return var7 > 1.0 - 0.025 / var5 ? var1.hasLineOfSight(this) : false;
      }
   }

   @Override
   public void aiStep() {
      if (this.level().isClientSide) {
         for (int var1 = 0; var1 < 2; var1++) {
            this.level()
               .addParticle(
                  ParticleTypes.PORTAL,
                  this.getRandomX(0.5),
                  this.getRandomY() - 0.25,
                  this.getRandomZ(0.5),
                  (this.random.nextDouble() - 0.5) * 2.0,
                  -this.random.nextDouble(),
                  (this.random.nextDouble() - 0.5) * 2.0
               );
         }
      }

      this.jumping = false;
      if (!this.level().isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level(), true);
      }

      super.aiStep();
   }

   @Override
   public boolean isSensitiveToWater() {
      return true;
   }

   @Override
   protected void customServerAiStep() {
      if (this.level().isDay() && this.tickCount >= this.targetChangeTime + 600) {
         float var1 = this.getLightLevelDependentMagicValue();
         if (var1 > 0.5F && this.level().canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.setTarget(null);
            this.teleport();
         }
      }

      super.customServerAiStep();
   }

   protected boolean teleport() {
      if (!this.level().isClientSide() && this.isAlive()) {
         double var1 = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
         double var3 = this.getY() + (double)(this.random.nextInt(64) - 32);
         double var5 = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
         return this.teleport(var1, var3, var5);
      } else {
         return false;
      }
   }

   boolean teleportTowards(Entity var1) {
      Vec3 var2 = new Vec3(this.getX() - var1.getX(), this.getY(0.5) - var1.getEyeY(), this.getZ() - var1.getZ());
      var2 = var2.normalize();
      double var3 = 16.0;
      double var5 = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - var2.x * 16.0;
      double var7 = this.getY() + (double)(this.random.nextInt(16) - 8) - var2.y * 16.0;
      double var9 = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - var2.z * 16.0;
      return this.teleport(var5, var7, var9);
   }

   private boolean teleport(double var1, double var3, double var5) {
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos(var1, var3, var5);

      while (var7.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(var7).blocksMotion()) {
         var7.move(Direction.DOWN);
      }

      BlockState var8 = this.level().getBlockState(var7);
      boolean var9 = var8.blocksMotion();
      boolean var10 = var8.getFluidState().is(FluidTags.WATER);
      if (var9 && !var10) {
         Vec3 var11 = this.position();
         boolean var12 = this.randomTeleport(var1, var3, var5, true);
         if (var12) {
            this.level().gameEvent(GameEvent.TELEPORT, var11, GameEvent.Context.of(this));
            if (!this.isSilent()) {
               this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
               this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
         }

         return var12;
      } else {
         return false;
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ENDERMAN_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMAN_DEATH;
   }

   @Override
   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      BlockState var4 = this.getCarriedBlock();
      if (var4 != null) {
         ItemStack var5 = new ItemStack(Items.DIAMOND_AXE);
         var5.enchant(Enchantments.SILK_TOUCH, 1);
         LootParams.Builder var6 = new LootParams.Builder((ServerLevel)this.level())
            .withParameter(LootContextParams.ORIGIN, this.position())
            .withParameter(LootContextParams.TOOL, var5)
            .withOptionalParameter(LootContextParams.THIS_ENTITY, this);

         for (ItemStack var9 : var4.getDrops(var6)) {
            this.spawnAtLocation(var9);
         }
      }
   }

   public void setCarriedBlock(@Nullable BlockState var1) {
      this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable(var1));
   }

   @Nullable
   public BlockState getCarriedBlock() {
      return this.entityData.get(DATA_CARRY_STATE).orElse(null);
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         boolean var3 = var1.getDirectEntity() instanceof ThrownPotion;
         if (!var1.is(DamageTypeTags.IS_PROJECTILE) && !var3) {
            boolean var6 = super.hurt(var1, var2);
            if (!this.level().isClientSide() && !(var1.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
               this.teleport();
            }

            return var6;
         } else {
            boolean var4 = var3 && this.hurtWithCleanWater(var1, (ThrownPotion)var1.getDirectEntity(), var2);

            for (int var5 = 0; var5 < 64; var5++) {
               if (this.teleport()) {
                  return true;
               }
            }

            return var4;
         }
      }
   }

   private boolean hurtWithCleanWater(DamageSource var1, ThrownPotion var2, float var3) {
      ItemStack var4 = var2.getItem();
      PotionContents var5 = var4.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      return var5.is(Potions.WATER) ? super.hurt(var1, var3) : false;
   }

   public boolean isCreepy() {
      return this.entityData.get(DATA_CREEPY);
   }

   public boolean hasBeenStaredAt() {
      return this.entityData.get(DATA_STARED_AT);
   }

   public void setBeingStaredAt() {
      this.entityData.set(DATA_STARED_AT, true);
   }

   @Override
   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
   }

   static class EndermanFreezeWhenLookedAt extends Goal {
      private final EnderMan enderman;
      @Nullable
      private LivingEntity target;

      public EndermanFreezeWhenLookedAt(EnderMan var1) {
         super();
         this.enderman = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         this.target = this.enderman.getTarget();
         if (!(this.target instanceof Player)) {
            return false;
         } else {
            double var1 = this.target.distanceToSqr(this.enderman);
            return var1 > 256.0 ? false : this.enderman.isLookingAtMe((Player)this.target);
         }
      }

      @Override
      public void start() {
         this.enderman.getNavigation().stop();
      }

      @Override
      public void tick() {
         this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
      }
   }

   static class EndermanLeaveBlockGoal extends Goal {
      private final EnderMan enderman;

      public EndermanLeaveBlockGoal(EnderMan var1) {
         super();
         this.enderman = var1;
      }

      @Override
      public boolean canUse() {
         if (this.enderman.getCarriedBlock() == null) {
            return false;
         } else {
            return !this.enderman.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
               ? false
               : this.enderman.getRandom().nextInt(reducedTickDelay(2000)) == 0;
         }
      }

      @Override
      public void tick() {
         RandomSource var1 = this.enderman.getRandom();
         Level var2 = this.enderman.level();
         int var3 = Mth.floor(this.enderman.getX() - 1.0 + var1.nextDouble() * 2.0);
         int var4 = Mth.floor(this.enderman.getY() + var1.nextDouble() * 2.0);
         int var5 = Mth.floor(this.enderman.getZ() - 1.0 + var1.nextDouble() * 2.0);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         BlockState var7 = var2.getBlockState(var6);
         BlockPos var8 = var6.below();
         BlockState var9 = var2.getBlockState(var8);
         BlockState var10 = this.enderman.getCarriedBlock();
         if (var10 != null) {
            var10 = Block.updateFromNeighbourShapes(var10, this.enderman.level(), var6);
            if (this.canPlaceBlock(var2, var6, var10, var7, var9, var8)) {
               var2.setBlock(var6, var10, 3);
               var2.gameEvent(GameEvent.BLOCK_PLACE, var6, GameEvent.Context.of(this.enderman, var10));
               this.enderman.setCarriedBlock(null);
            }
         }
      }

      private boolean canPlaceBlock(Level var1, BlockPos var2, BlockState var3, BlockState var4, BlockState var5, BlockPos var6) {
         return var4.isAir()
            && !var5.isAir()
            && !var5.is(Blocks.BEDROCK)
            && var5.isCollisionShapeFullBlock(var1, var6)
            && var3.canSurvive(var1, var2)
            && var1.getEntities(this.enderman, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(var2))).isEmpty();
      }
   }

   static class EndermanLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
      private final EnderMan enderman;
      @Nullable
      private Player pendingTarget;
      private int aggroTime;
      private int teleportTime;
      private final TargetingConditions startAggroTargetConditions;
      private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
      private final Predicate<LivingEntity> isAngerInducing;

      public EndermanLookForPlayerGoal(EnderMan var1, @Nullable Predicate<LivingEntity> var2) {
         super(var1, Player.class, 10, false, false, var2);
         this.enderman = var1;
         this.isAngerInducing = var1x -> (var1.isLookingAtMe((Player)var1x) || var1.isAngryAt(var1x)) && !var1.hasIndirectPassenger(var1x);
         this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
      }

      @Override
      public boolean canUse() {
         this.pendingTarget = this.enderman.level().getNearestPlayer(this.startAggroTargetConditions, this.enderman);
         return this.pendingTarget != null;
      }

      @Override
      public void start() {
         this.aggroTime = this.adjustedTickDelay(5);
         this.teleportTime = 0;
         this.enderman.setBeingStaredAt();
      }

      @Override
      public void stop() {
         this.pendingTarget = null;
         super.stop();
      }

      @Override
      public boolean canContinueToUse() {
         if (this.pendingTarget != null) {
            if (!this.isAngerInducing.test(this.pendingTarget)) {
               return false;
            } else {
               this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
               return true;
            }
         } else {
            if (this.target != null) {
               if (this.enderman.hasIndirectPassenger(this.target)) {
                  return false;
               }

               if (this.continueAggroTargetConditions.test(this.enderman, this.target)) {
                  return true;
               }
            }

            return super.canContinueToUse();
         }
      }

      @Override
      public void tick() {
         if (this.enderman.getTarget() == null) {
            super.setTarget(null);
         }

         if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
               this.target = this.pendingTarget;
               this.pendingTarget = null;
               super.start();
            }
         } else {
            if (this.target != null && !this.enderman.isPassenger()) {
               if (this.enderman.isLookingAtMe((Player)this.target)) {
                  if (this.target.distanceToSqr(this.enderman) < 16.0) {
                     this.enderman.teleport();
                  }

                  this.teleportTime = 0;
               } else if (this.target.distanceToSqr(this.enderman) > 256.0
                  && this.teleportTime++ >= this.adjustedTickDelay(30)
                  && this.enderman.teleportTowards(this.target)) {
                  this.teleportTime = 0;
               }
            }

            super.tick();
         }
      }
   }

   static class EndermanTakeBlockGoal extends Goal {
      private final EnderMan enderman;

      public EndermanTakeBlockGoal(EnderMan var1) {
         super();
         this.enderman = var1;
      }

      @Override
      public boolean canUse() {
         if (this.enderman.getCarriedBlock() != null) {
            return false;
         } else {
            return !this.enderman.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
               ? false
               : this.enderman.getRandom().nextInt(reducedTickDelay(20)) == 0;
         }
      }

      @Override
      public void tick() {
         RandomSource var1 = this.enderman.getRandom();
         Level var2 = this.enderman.level();
         int var3 = Mth.floor(this.enderman.getX() - 2.0 + var1.nextDouble() * 4.0);
         int var4 = Mth.floor(this.enderman.getY() + var1.nextDouble() * 3.0);
         int var5 = Mth.floor(this.enderman.getZ() - 2.0 + var1.nextDouble() * 4.0);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         BlockState var7 = var2.getBlockState(var6);
         Vec3 var8 = new Vec3((double)this.enderman.getBlockX() + 0.5, (double)var4 + 0.5, (double)this.enderman.getBlockZ() + 0.5);
         Vec3 var9 = new Vec3((double)var3 + 0.5, (double)var4 + 0.5, (double)var5 + 0.5);
         BlockHitResult var10 = var2.clip(new ClipContext(var8, var9, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.enderman));
         boolean var11 = var10.getBlockPos().equals(var6);
         if (var7.is(BlockTags.ENDERMAN_HOLDABLE) && var11) {
            var2.removeBlock(var6, false);
            var2.gameEvent(GameEvent.BLOCK_DESTROY, var6, GameEvent.Context.of(this.enderman, var7));
            this.enderman.setCarriedBlock(var7.getBlock().defaultBlockState());
         }
      }
   }
}
