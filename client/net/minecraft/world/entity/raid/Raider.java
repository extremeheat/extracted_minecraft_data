package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public abstract class Raider extends PatrollingMonster {
   protected static final EntityDataAccessor<Boolean> IS_CELEBRATING = SynchedEntityData.defineId(Raider.class, EntityDataSerializers.BOOLEAN);
   static final Predicate<ItemEntity> ALLOWED_ITEMS = var0 -> !var0.hasPickUpDelay()
         && var0.isAlive()
         && ItemStack.matches(var0.getItem(), Raid.getOminousBannerInstance(var0.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN)));
   @Nullable
   protected Raid raid;
   private int wave;
   private boolean canJoinRaid;
   private int ticksOutsideRaid;

   protected Raider(EntityType<? extends Raider> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new Raider.ObtainRaidLeaderBannerGoal<>(this));
      this.goalSelector.addGoal(3, new PathfindToRaidGoal<>(this));
      this.goalSelector.addGoal(4, new Raider.RaiderMoveThroughVillageGoal(this, 1.0499999523162842, 1));
      this.goalSelector.addGoal(5, new Raider.RaiderCelebration(this));
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(IS_CELEBRATING, false);
   }

   public abstract void applyRaidBuffs(ServerLevel var1, int var2, boolean var3);

   public boolean canJoinRaid() {
      return this.canJoinRaid;
   }

   public void setCanJoinRaid(boolean var1) {
      this.canJoinRaid = var1;
   }

   @Override
   public void aiStep() {
      if (this.level() instanceof ServerLevel && this.isAlive()) {
         Raid var1 = this.getCurrentRaid();
         if (this.canJoinRaid()) {
            if (var1 == null) {
               if (this.level().getGameTime() % 20L == 0L) {
                  Raid var2 = ((ServerLevel)this.level()).getRaidAt(this.blockPosition());
                  if (var2 != null && Raids.canJoinRaid(this, var2)) {
                     var2.joinRaid(var2.getGroupsSpawned(), this, null, true);
                  }
               }
            } else {
               LivingEntity var3 = this.getTarget();
               if (var3 != null && (var3.getType() == EntityType.PLAYER || var3.getType() == EntityType.IRON_GOLEM)) {
                  this.noActionTime = 0;
               }
            }
         }
      }

      super.aiStep();
   }

   @Override
   protected void updateNoActionTime() {
      this.noActionTime += 2;
   }

   @Override
   public void die(DamageSource var1) {
      if (this.level() instanceof ServerLevel) {
         Entity var2 = var1.getEntity();
         Raid var3 = this.getCurrentRaid();
         if (var3 != null) {
            if (this.isPatrolLeader()) {
               var3.removeLeader(this.getWave());
            }

            if (var2 != null && var2.getType() == EntityType.PLAYER) {
               var3.addHeroOfTheVillage(var2);
            }

            var3.removeFromRaid(this, false);
         }
      }

      super.die(var1);
   }

   @Override
   public boolean canJoinPatrol() {
      return !this.hasActiveRaid();
   }

   public void setCurrentRaid(@Nullable Raid var1) {
      this.raid = var1;
   }

   @Nullable
   public Raid getCurrentRaid() {
      return this.raid;
   }

   public boolean isCaptain() {
      ItemStack var1 = this.getItemBySlot(EquipmentSlot.HEAD);
      boolean var2 = !var1.isEmpty() && ItemStack.matches(var1, Raid.getOminousBannerInstance(this.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN)));
      boolean var3 = this.isPatrolLeader();
      return var2 && var3;
   }

   public boolean hasRaid() {
      return !(this.level() instanceof ServerLevel var1) ? false : this.getCurrentRaid() != null || var1.getRaidAt(this.blockPosition()) != null;
   }

   public boolean hasActiveRaid() {
      return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
   }

   public void setWave(int var1) {
      this.wave = var1;
   }

   public int getWave() {
      return this.wave;
   }

   public boolean isCelebrating() {
      return this.entityData.get(IS_CELEBRATING);
   }

   public void setCelebrating(boolean var1) {
      this.entityData.set(IS_CELEBRATING, var1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Wave", this.wave);
      var1.putBoolean("CanJoinRaid", this.canJoinRaid);
      if (this.raid != null) {
         var1.putInt("RaidId", this.raid.getId());
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.wave = var1.getInt("Wave");
      this.canJoinRaid = var1.getBoolean("CanJoinRaid");
      if (var1.contains("RaidId", 3)) {
         if (this.level() instanceof ServerLevel) {
            this.raid = ((ServerLevel)this.level()).getRaids().get(var1.getInt("RaidId"));
         }

         if (this.raid != null) {
            this.raid.addWaveMob(this.wave, this, false);
            if (this.isPatrolLeader()) {
               this.raid.setLeader(this.wave, this);
            }
         }
      }
   }

   @Override
   protected void pickUpItem(ServerLevel var1, ItemEntity var2) {
      ItemStack var3 = var2.getItem();
      boolean var4 = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
      if (this.hasActiveRaid()
         && !var4
         && ItemStack.matches(var3, Raid.getOminousBannerInstance(this.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN)))) {
         EquipmentSlot var5 = EquipmentSlot.HEAD;
         ItemStack var6 = this.getItemBySlot(var5);
         double var7 = (double)this.getEquipmentDropChance(var5);
         if (!var6.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < var7) {
            this.spawnAtLocation(var1, var6);
         }

         this.onItemPickup(var2);
         this.setItemSlot(var5, var3);
         this.take(var2, var3.getCount());
         var2.discard();
         this.getCurrentRaid().setLeader(this.getWave(), this);
         this.setPatrolLeader(true);
      } else {
         super.pickUpItem(var1, var2);
      }
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return this.getCurrentRaid() == null ? super.removeWhenFarAway(var1) : false;
   }

   @Override
   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
   }

   public int getTicksOutsideRaid() {
      return this.ticksOutsideRaid;
   }

   public void setTicksOutsideRaid(int var1) {
      this.ticksOutsideRaid = var1;
   }

   @Override
   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.hasActiveRaid()) {
         this.getCurrentRaid().updateBossbar();
      }

      return super.hurtServer(var1, var2, var3);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      this.setCanJoinRaid(this.getType() != EntityType.WITCH || var3 != EntitySpawnReason.NATURAL);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public abstract SoundEvent getCelebrateSound();

   protected class HoldGroundAttackGoal extends Goal {
      private final Raider mob;
      private final float hostileRadiusSqr;
      public final TargetingConditions shoutTargeting = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight().ignoreInvisibilityTesting();

      public HoldGroundAttackGoal(final AbstractIllager nullx, final float nullxx) {
         super();
         this.mob = nullx;
         this.hostileRadiusSqr = nullxx * nullxx;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         LivingEntity var1 = this.mob.getLastHurtByMob();
         return this.mob.getCurrentRaid() == null
            && this.mob.isPatrolling()
            && this.mob.getTarget() != null
            && !this.mob.isAggressive()
            && (var1 == null || var1.getType() != EntityType.PLAYER);
      }

      @Override
      public void start() {
         super.start();
         this.mob.getNavigation().stop();

         for (Raider var3 : getServerLevel(this.mob)
            .getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0))) {
            var3.setTarget(this.mob.getTarget());
         }
      }

      @Override
      public void stop() {
         super.stop();
         LivingEntity var1 = this.mob.getTarget();
         if (var1 != null) {
            for (Raider var4 : getServerLevel(this.mob)
               .getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0))) {
               var4.setTarget(var1);
               var4.setAggressive(true);
            }

            this.mob.setAggressive(true);
         }
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void tick() {
         LivingEntity var1 = this.mob.getTarget();
         if (var1 != null) {
            if (this.mob.distanceToSqr(var1) > (double)this.hostileRadiusSqr) {
               this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
               if (this.mob.random.nextInt(50) == 0) {
                  this.mob.playAmbientSound();
               }
            } else {
               this.mob.setAggressive(true);
            }

            super.tick();
         }
      }
   }

   public class ObtainRaidLeaderBannerGoal<T extends Raider> extends Goal {
      private final T mob;
      private Int2LongOpenHashMap unreachableBannerCache = new Int2LongOpenHashMap();
      @Nullable
      private Path pathToBanner;
      @Nullable
      private ItemEntity pursuedBannerItemEntity;

      public ObtainRaidLeaderBannerGoal(final T nullx) {
         super();
         this.mob = (T)nullx;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         if (this.cannotPickUpBanner()) {
            return false;
         } else {
            Int2LongOpenHashMap var1 = new Int2LongOpenHashMap();
            double var2 = Raider.this.getAttributeValue(Attributes.FOLLOW_RANGE);

            for (ItemEntity var6 : this.mob
               .level()
               .getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(var2, 8.0, var2), Raider.ALLOWED_ITEMS)) {
               long var7 = this.unreachableBannerCache.getOrDefault(var6.getId(), -9223372036854775808L);
               if (Raider.this.level().getGameTime() < var7) {
                  var1.put(var6.getId(), var7);
               } else {
                  Path var9 = this.mob.getNavigation().createPath(var6, 1);
                  if (var9 != null && var9.canReach()) {
                     this.pathToBanner = var9;
                     this.pursuedBannerItemEntity = var6;
                     return true;
                  }

                  var1.put(var6.getId(), Raider.this.level().getGameTime() + 600L);
               }
            }

            this.unreachableBannerCache = var1;
            return false;
         }
      }

      @Override
      public boolean canContinueToUse() {
         if (this.pursuedBannerItemEntity == null || this.pathToBanner == null) {
            return false;
         } else if (this.pursuedBannerItemEntity.isRemoved()) {
            return false;
         } else {
            return this.pathToBanner.isDone() ? false : !this.cannotPickUpBanner();
         }
      }

      private boolean cannotPickUpBanner() {
         if (!this.mob.hasActiveRaid()) {
            return true;
         } else if (this.mob.getCurrentRaid().isOver()) {
            return true;
         } else if (!this.mob.canBeLeader()) {
            return true;
         } else if (ItemStack.matches(
            this.mob.getItemBySlot(EquipmentSlot.HEAD), Raid.getOminousBannerInstance(this.mob.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN))
         )) {
            return true;
         } else {
            Raider var1 = Raider.this.raid.getLeader(this.mob.getWave());
            return var1 != null && var1.isAlive();
         }
      }

      @Override
      public void start() {
         this.mob.getNavigation().moveTo(this.pathToBanner, 1.149999976158142);
      }

      @Override
      public void stop() {
         this.pathToBanner = null;
         this.pursuedBannerItemEntity = null;
      }

      @Override
      public void tick() {
         if (this.pursuedBannerItemEntity != null && this.pursuedBannerItemEntity.closerThan(this.mob, 1.414)) {
            this.mob.pickUpItem(getServerLevel(Raider.this.level()), this.pursuedBannerItemEntity);
         }
      }
   }

   public class RaiderCelebration extends Goal {
      private final Raider mob;

      RaiderCelebration(final Raider nullx) {
         super();
         this.mob = nullx;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         Raid var1 = this.mob.getCurrentRaid();
         return this.mob.isAlive() && this.mob.getTarget() == null && var1 != null && var1.isLoss();
      }

      @Override
      public void start() {
         this.mob.setCelebrating(true);
         super.start();
      }

      @Override
      public void stop() {
         this.mob.setCelebrating(false);
         super.stop();
      }

      @Override
      public void tick() {
         if (!this.mob.isSilent() && this.mob.random.nextInt(this.adjustedTickDelay(100)) == 0) {
            Raider.this.makeSound(Raider.this.getCelebrateSound());
         }

         if (!this.mob.isPassenger() && this.mob.random.nextInt(this.adjustedTickDelay(50)) == 0) {
            this.mob.getJumpControl().jump();
         }

         super.tick();
      }
   }

   static class RaiderMoveThroughVillageGoal extends Goal {
      private final Raider raider;
      private final double speedModifier;
      private BlockPos poiPos;
      private final List<BlockPos> visited = Lists.newArrayList();
      private final int distanceToPoi;
      private boolean stuck;

      public RaiderMoveThroughVillageGoal(Raider var1, double var2, int var4) {
         super();
         this.raider = var1;
         this.speedModifier = var2;
         this.distanceToPoi = var4;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         this.updateVisited();
         return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
      }

      private boolean isValidRaid() {
         return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
      }

      private boolean hasSuitablePoi() {
         ServerLevel var1 = (ServerLevel)this.raider.level();
         BlockPos var2 = this.raider.blockPosition();
         Optional var3 = var1.getPoiManager()
            .getRandom(var0 -> var0.is(PoiTypes.HOME), this::hasNotVisited, PoiManager.Occupancy.ANY, var2, 48, this.raider.random);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.poiPos = ((BlockPos)var3.get()).immutable();
            return true;
         }
      }

      @Override
      public boolean canContinueToUse() {
         return this.raider.getNavigation().isDone()
            ? false
            : this.raider.getTarget() == null
               && !this.poiPos.closerToCenterThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi))
               && !this.stuck;
      }

      @Override
      public void stop() {
         if (this.poiPos.closerToCenterThan(this.raider.position(), (double)this.distanceToPoi)) {
            this.visited.add(this.poiPos);
         }
      }

      @Override
      public void start() {
         super.start();
         this.raider.setNoActionTime(0);
         this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
         this.stuck = false;
      }

      @Override
      public void tick() {
         if (this.raider.getNavigation().isDone()) {
            Vec3 var1 = Vec3.atBottomCenterOf(this.poiPos);
            Vec3 var2 = DefaultRandomPos.getPosTowards(this.raider, 16, 7, var1, 0.3141592741012573);
            if (var2 == null) {
               var2 = DefaultRandomPos.getPosTowards(this.raider, 8, 7, var1, 1.5707963705062866);
            }

            if (var2 == null) {
               this.stuck = true;
               return;
            }

            this.raider.getNavigation().moveTo(var2.x, var2.y, var2.z, this.speedModifier);
         }
      }

      private boolean hasNotVisited(BlockPos var1) {
         for (BlockPos var3 : this.visited) {
            if (Objects.equals(var1, var3)) {
               return false;
            }
         }

         return true;
      }

      private void updateVisited() {
         if (this.visited.size() > 2) {
            this.visited.remove(0);
         }
      }
   }
}
