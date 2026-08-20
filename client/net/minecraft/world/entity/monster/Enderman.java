package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.PotionTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
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
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Enderman extends Monster implements NeutralMob {
   private static final Identifier SPEED_MODIFIER_ATTACKING_ID = Identifier.withDefaultNamespace("attacking");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING;
   private static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
   private static final int MIN_DEAGGRESSION_TIME = 600;
   private static final EntityDataAccessor<Optional<BlockState>> DATA_CARRY_STATE;
   private static final EntityDataAccessor<Boolean> DATA_CREEPY;
   private static final EntityDataAccessor<Boolean> DATA_STARED_AT;
   private int lastStareSound = -2147483648;
   private int targetChangeTime;
   private static final UniformInt PERSISTENT_ANGER_TIME;
   private long persistentAngerEndTime;
   private @Nullable EntityReference<LivingEntity> persistentAngerTarget;

   public Enderman(final EntityType<? extends Enderman> type, final Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EndermanFreezeWhenLookedAt(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(10, new EndermanLeaveBlockGoal(this));
      this.goalSelector.addGoal(11, new EndermanTakeBlockGoal(this));
      this.targetSelector.addGoal(1, new EndermanLookForPlayerGoal(this, this::isAngryAt));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Endermite.class, true, false));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal(this, false));
   }

   public float getWalkTargetValue(final BlockPos pos, final LevelReader level) {
      return 0.0F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 64.0).add(Attributes.STEP_HEIGHT, 1.0);
   }

   public void setTarget(final @Nullable LivingEntity target) {
      super.setTarget(target);
      AttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (target == null) {
         this.targetChangeTime = 0;
         this.entityData.set(DATA_CREEPY, false);
         this.entityData.set(DATA_STARED_AT, false);
         movementSpeed.removeModifier(SPEED_MODIFIER_ATTACKING_ID);
      } else {
         this.targetChangeTime = this.tickCount;
         this.entityData.set(DATA_CREEPY, true);
         if (!movementSpeed.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
            movementSpeed.addTransientModifier(SPEED_MODIFIER_ATTACKING);
         }
      }

   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_CARRY_STATE, Optional.empty());
      entityData.define(DATA_CREEPY, false);
      entityData.define(DATA_STARED_AT, false);
   }

   public void startPersistentAngerTimer() {
      this.setTimeToRemainAngry((long)PERSISTENT_ANGER_TIME.sample(this.random));
   }

   public void setPersistentAngerEndTime(final long endTime) {
      this.persistentAngerEndTime = endTime;
   }

   public long getPersistentAngerEndTime() {
      return this.persistentAngerEndTime;
   }

   public void setPersistentAngerTarget(final @Nullable EntityReference<LivingEntity> persistentAngerTarget) {
      this.persistentAngerTarget = persistentAngerTarget;
   }

   public @Nullable EntityReference<LivingEntity> getPersistentAngerTarget() {
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

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (DATA_CREEPY.equals(accessor) && this.hasBeenStaredAt() && this.level().isClientSide()) {
         this.playStareSound();
      }

      super.onSyncedDataUpdated(accessor);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      BlockState blockState = this.getCarriedBlock();
      if (blockState != null) {
         output.store("carriedBlockState", BlockState.CODEC, blockState);
      }

      this.addPersistentAngerSaveData(output);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setCarriedBlock((BlockState)input.read("carriedBlockState", BlockState.CODEC).filter((blockState) -> !blockState.isAir()).orElse((Object)null));
      this.readPersistentAngerSaveData(this.level(), input);
   }

   private boolean isBeingStaredBy(final Player player) {
      return !LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM.test(player) ? false : this.isLookingAtMe(player, 0.025, true, false, new double[]{this.getEyeY()});
   }

   public void aiStep() {
      if (this.level().isClientSide()) {
         for(int i = 0; i < 2; ++i) {
            this.level().addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5), this.getRandomY() - 0.25, this.getRandomZ(0.5), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
         }
      }

      this.jumping = false;
      if (!this.level().isClientSide()) {
         this.updatePersistentAnger((ServerLevel)this.level(), true);
      }

      super.aiStep();
   }

   public boolean isSensitiveToWater() {
      return true;
   }

   protected void customServerAiStep(final ServerLevel level) {
      if (level.isBrightOutside() && this.tickCount >= this.targetChangeTime + 600) {
         float br = this.getLightLevelDependentMagicValue();
         if (br > 0.5F && level.canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0F < (br - 0.4F) * 2.0F) {
            this.setTarget((LivingEntity)null);
            this.teleport();
         }
      }

      super.customServerAiStep(level);
   }

   protected boolean teleport() {
      if (!this.level().isClientSide() && this.isAlive()) {
         double xx = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
         double yy = this.getY() + (double)(this.random.nextInt(64) - 32);
         double zz = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
         return this.teleport(xx, yy, zz);
      } else {
         return false;
      }
   }

   private boolean teleportTowards(final Entity entity) {
      Vec3 dir = new Vec3(this.getX() - entity.getX(), this.getY(0.5) - entity.getEyeY(), this.getZ() - entity.getZ());
      dir = dir.normalize();
      double d = 16.0;
      double xx = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - dir.x * 16.0;
      double yy = this.getY() + (double)(this.random.nextInt(16) - 8) - dir.y * 16.0;
      double zz = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - dir.z * 16.0;
      return this.teleport(xx, yy, zz);
   }

   @VisibleForTesting
   public boolean teleport(final double x, final double y, final double z) {
      if (this.isPassenger()) {
         return false;
      } else {
         Vec3 oldPos = this.position();
         boolean result = this.randomTeleport(x, y, z, true, BlockTags.ENDERMAN_DOES_NOT_TELEPORT_TO);
         if (result) {
            this.level().gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of((Entity)this));
            if (!this.isSilent()) {
               this.level().playSound((Entity)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
               this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
         }

         return result;
      }
   }

   public boolean canRandomlyTeleportTo(final double x, final double y, final double z) {
      BlockPos pos = BlockPos.containing(x, y, z);
      BlockState blockState = this.level().getBlockState(pos);
      return !blockState.getFluidState().is(FluidTags.WATER);
   }

   private boolean repeatedlyTryToTeleport() {
      if (!this.isPassenger()) {
         for(int i = 0; i < 64; ++i) {
            if (this.teleport()) {
               return true;
            }
         }
      }

      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isCreepy() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMAN_DEATH;
   }

   protected void dropCustomDeathLoot(final ServerLevel level, final DamageSource source, final boolean killedByPlayer) {
      super.dropCustomDeathLoot(level, source, killedByPlayer);
      BlockState carryingBlock = this.getCarriedBlock();
      if (carryingBlock != null) {
         ItemStack fakeTool = new ItemStack(Items.DIAMOND_AXE);
         EnchantmentHelper.enchantItemFromProvider(fakeTool, level.registryAccess(), VanillaEnchantmentProviders.ENDERMAN_LOOT_DROP, level.getCurrentDifficultyAt(this.blockPosition()), this.getRandom());
         LootParams.Builder params = (new LootParams.Builder((ServerLevel)this.level())).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, fakeTool).withOptionalParameter(LootContextParams.THIS_ENTITY, this);

         for(ItemStack itemStack : carryingBlock.getDrops(params)) {
            this.spawnAtLocation(level, itemStack);
         }
      }

   }

   public void setCarriedBlock(final @Nullable BlockState carryingBlock) {
      this.entityData.set(DATA_CARRY_STATE, Optional.ofNullable(carryingBlock));
   }

   public @Nullable BlockState getCarriedBlock() {
      return (BlockState)((Optional)this.entityData.get(DATA_CARRY_STATE)).orElse((Object)null);
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (this.isInvulnerableTo(level, source)) {
         return false;
      } else {
         Entity var6 = source.getDirectEntity();
         AbstractThrownPotion var10000;
         if (var6 instanceof AbstractThrownPotion) {
            AbstractThrownPotion potion = (AbstractThrownPotion)var6;
            var10000 = potion;
         } else {
            var10000 = null;
         }

         AbstractThrownPotion thrownPotion = var10000;
         if (source.is(DamageTypeTags.IS_PROJECTILE) && !this.isPassenger()) {
            return this.repeatedlyTryToTeleport();
         } else if (thrownPotion == null) {
            boolean result = super.hurtServer(level, source, damage);
            if (!(source.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
               this.teleport();
            }

            return result;
         } else {
            boolean hurtWithCleanWater = this.hurtWithCleanWater(level, source, thrownPotion, damage);
            return this.repeatedlyTryToTeleport() || hurtWithCleanWater;
         }
      }
   }

   private boolean hurtWithCleanWater(final ServerLevel level, final DamageSource source, final AbstractThrownPotion thrownPotion, final float damage) {
      ItemStack potionItemStack = thrownPotion.getItem();
      PotionContents potionContents = (PotionContents)potionItemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      return potionContents.is(PotionTags.HURTS_WATER_SENSITIVE_ENTITIES) ? super.hurtServer(level, source, damage) : false;
   }

   public boolean isCreepy() {
      return (Boolean)this.entityData.get(DATA_CREEPY);
   }

   public boolean hasBeenStaredAt() {
      return (Boolean)this.entityData.get(DATA_STARED_AT);
   }

   public void setBeingStaredAt() {
      this.entityData.set(DATA_STARED_AT, true);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
   }

   static {
      SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.15000000596046448, AttributeModifier.Operation.ADD_VALUE);
      DATA_CARRY_STATE = SynchedEntityData.<Optional<BlockState>>defineId(Enderman.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
      DATA_CREEPY = SynchedEntityData.<Boolean>defineId(Enderman.class, EntityDataSerializers.BOOLEAN);
      DATA_STARED_AT = SynchedEntityData.<Boolean>defineId(Enderman.class, EntityDataSerializers.BOOLEAN);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }

   private static class EndermanLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
      private final Enderman enderman;
      private @Nullable Player pendingTarget;
      private int aggroTime;
      private int teleportTime;
      private final TargetingConditions startAggroTargetConditions;
      private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
      private final TargetingConditions.Selector isAngerInducing;

      public EndermanLookForPlayerGoal(final Enderman enderman, final TargetingConditions.@Nullable Selector isAngryAt) {
         super(enderman, Player.class, 10, false, false, isAngryAt);
         this.enderman = enderman;
         this.isAngerInducing = (target, level) -> (enderman.isBeingStaredBy((Player)target) || enderman.isAngryAt(target, level)) && !enderman.hasIndirectPassenger(target);
         this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
      }

      public boolean canUse() {
         this.pendingTarget = getServerLevel(this.enderman).getNearestPlayer(this.startAggroTargetConditions.range(this.getFollowDistance()), this.enderman);
         return this.pendingTarget != null;
      }

      public void start() {
         this.aggroTime = this.adjustedTickDelay(5);
         this.teleportTime = 0;
         this.enderman.setBeingStaredAt();
      }

      public void stop() {
         this.pendingTarget = null;
         super.stop();
      }

      public boolean canContinueToUse() {
         if (this.pendingTarget != null) {
            if (!this.isAngerInducing.test(this.pendingTarget, getServerLevel(this.enderman))) {
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

               if (this.continueAggroTargetConditions.test(getServerLevel(this.enderman), this.enderman, this.target)) {
                  return true;
               }
            }

            return super.canContinueToUse();
         }
      }

      public void tick() {
         if (this.enderman.getTarget() == null) {
            super.setTarget((LivingEntity)null);
         }

         if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
               this.target = this.pendingTarget;
               this.pendingTarget = null;
               super.start();
            }
         } else {
            if (this.target != null && !this.enderman.isPassenger()) {
               if (this.enderman.isBeingStaredBy((Player)this.target)) {
                  if (this.target.distanceToSqr(this.enderman) < 16.0) {
                     this.enderman.teleport();
                  }

                  this.teleportTime = 0;
               } else if (this.target.distanceToSqr(this.enderman) > 256.0 && this.teleportTime++ >= this.adjustedTickDelay(30) && this.enderman.teleportTowards(this.target)) {
                  this.teleportTime = 0;
               }
            }

            super.tick();
         }

      }
   }

   private static class EndermanFreezeWhenLookedAt extends Goal {
      private final Enderman enderman;
      private @Nullable LivingEntity target;

      public EndermanFreezeWhenLookedAt(final Enderman enderman) {
         super();
         this.enderman = enderman;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         this.target = this.enderman.getTarget();
         LivingEntity var2 = this.target;
         if (var2 instanceof Player playerTarget) {
            double dist = this.target.distanceToSqr(this.enderman);
            return dist > 256.0 ? false : this.enderman.isBeingStaredBy(playerTarget);
         } else {
            return false;
         }
      }

      public void start() {
         this.enderman.getNavigation().stop();
      }

      public void tick() {
         this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
      }
   }

   private static class EndermanLeaveBlockGoal extends Goal {
      private final Enderman enderman;

      public EndermanLeaveBlockGoal(final Enderman enderman) {
         super();
         this.enderman = enderman;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() == null) {
            return false;
         } else if (!(Boolean)getServerLevel(this.enderman).getGameRules().get(GameRules.MOB_GRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(reducedTickDelay(2000)) == 0;
         }
      }

      public void tick() {
         RandomSource random = this.enderman.getRandom();
         Level level = this.enderman.level();
         int xt = Mth.floor(this.enderman.getX() - 1.0 + random.nextDouble() * 2.0);
         int yt = Mth.floor(this.enderman.getY() + random.nextDouble() * 2.0);
         int zt = Mth.floor(this.enderman.getZ() - 1.0 + random.nextDouble() * 2.0);
         BlockPos pos = new BlockPos(xt, yt, zt);
         BlockState targetState = level.getBlockState(pos);
         BlockPos below = pos.below();
         BlockState belowState = level.getBlockState(below);
         BlockState carried = this.enderman.getCarriedBlock();
         if (carried != null) {
            carried = Block.updateFromNeighbourShapes(carried, this.enderman.level(), pos);
            if (this.canPlaceBlock(level, pos, carried, targetState, belowState, below)) {
               level.setBlockAndUpdate(pos, carried);
               level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(this.enderman, carried));
               this.enderman.setCarriedBlock((BlockState)null);
            }

         }
      }

      private boolean canPlaceBlock(final Level level, final BlockPos pos, final BlockState carried, final BlockState targetState, final BlockState belowState, final BlockPos below) {
         return targetState.isAir() && !belowState.isAir() && !belowState.is(Blocks.BEDROCK) && belowState.isCollisionShapeFullBlock(level, below) && carried.canSurvive(level, pos) && level.getEntities(this.enderman, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos))).isEmpty();
      }
   }

   private static class EndermanTakeBlockGoal extends Goal {
      private final Enderman enderman;

      public EndermanTakeBlockGoal(final Enderman enderman) {
         super();
         this.enderman = enderman;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() != null) {
            return false;
         } else if (!(Boolean)getServerLevel(this.enderman).getGameRules().get(GameRules.MOB_GRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(reducedTickDelay(20)) == 0;
         }
      }

      public void tick() {
         RandomSource random = this.enderman.getRandom();
         Level level = this.enderman.level();
         int xt = Mth.floor(this.enderman.getX() - 2.0 + random.nextDouble() * 4.0);
         int yt = Mth.floor(this.enderman.getY() + random.nextDouble() * 3.0);
         int zt = Mth.floor(this.enderman.getZ() - 2.0 + random.nextDouble() * 4.0);
         BlockPos pos = new BlockPos(xt, yt, zt);
         BlockState blockState = level.getBlockState(pos);
         Vec3 from = new Vec3((double)this.enderman.getBlockX() + 0.5, (double)yt + 0.5, (double)this.enderman.getBlockZ() + 0.5);
         Vec3 to = new Vec3((double)xt + 0.5, (double)yt + 0.5, (double)zt + 0.5);
         BlockHitResult result = level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.enderman));
         boolean reachable = result.getBlockPos().equals(pos);
         if (blockState.is(BlockTags.ENDERMAN_HOLDABLE) && reachable) {
            level.removeBlock(pos, false);
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(this.enderman, blockState));
            this.enderman.setCarriedBlock(blockState.getBlock().defaultBlockState());
         }

      }
   }
}
