package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public abstract class Raider extends PatrollingMonster {
   protected static final EntityDataAccessor IS_CELEBRATING;
   private static final Predicate ALLOWED_ITEMS;
   @Nullable
   protected Raid raid;
   private int wave;
   private boolean canJoinRaid;
   private int ticksOutsideRaid;

   protected Raider(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new Raider.ObtainRaidLeaderBannerGoal(this));
      this.goalSelector.addGoal(3, new PathfindToRaidGoal(this));
      this.goalSelector.addGoal(4, new Raider.RaiderMoveThroughVillageGoal(this, 1.0499999523162842D, 1));
      this.goalSelector.addGoal(5, new Raider.RaiderCelebration(this));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CELEBRATING, false);
   }

   public abstract void applyRaidBuffs(int var1, boolean var2);

   public boolean canJoinRaid() {
      return this.canJoinRaid;
   }

   public void setCanJoinRaid(boolean var1) {
      this.canJoinRaid = var1;
   }

   public void aiStep() {
      if (this.level instanceof ServerLevel && this.isAlive()) {
         Raid var1 = this.getCurrentRaid();
         if (this.canJoinRaid()) {
            if (var1 == null) {
               if (this.level.getGameTime() % 20L == 0L) {
                  Raid var2 = ((ServerLevel)this.level).getRaidAt(new BlockPos(this));
                  if (var2 != null && Raids.canJoinRaid(this, var2)) {
                     var2.joinRaid(var2.getGroupsSpawned(), this, (BlockPos)null, true);
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

   protected void updateNoActionTime() {
      this.noActionTime += 2;
   }

   public void die(DamageSource var1) {
      if (this.level instanceof ServerLevel) {
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

         if (this.isPatrolLeader() && var3 == null && ((ServerLevel)this.level).getRaidAt(new BlockPos(this)) == null) {
            ItemStack var4 = this.getItemBySlot(EquipmentSlot.HEAD);
            Player var5 = null;
            if (var2 instanceof Player) {
               var5 = (Player)var2;
            } else if (var2 instanceof Wolf) {
               Wolf var7 = (Wolf)var2;
               LivingEntity var8 = var7.getOwner();
               if (var7.isTame() && var8 instanceof Player) {
                  var5 = (Player)var8;
               }
            }

            if (!var4.isEmpty() && ItemStack.matches(var4, Raid.getLeaderBannerInstance()) && var5 != null) {
               MobEffectInstance var10 = var5.getEffect(MobEffects.BAD_OMEN);
               byte var11 = 1;
               int var12;
               if (var10 != null) {
                  var12 = var11 + var10.getAmplifier();
                  var5.removeEffectNoUpdate(MobEffects.BAD_OMEN);
               } else {
                  var12 = var11 - 1;
               }

               var12 = Mth.clamp(var12, 0, 5);
               MobEffectInstance var9 = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, var12, false, false, true);
               if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                  var5.addEffect(var9);
               }
            }
         }
      }

      super.die(var1);
   }

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
      return (Boolean)this.entityData.get(IS_CELEBRATING);
   }

   public void setCelebrating(boolean var1) {
      this.entityData.set(IS_CELEBRATING, var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Wave", this.wave);
      var1.putBoolean("CanJoinRaid", this.canJoinRaid);
      if (this.raid != null) {
         var1.putInt("RaidId", this.raid.getId());
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.wave = var1.getInt("Wave");
      this.canJoinRaid = var1.getBoolean("CanJoinRaid");
      if (var1.contains("RaidId", 3)) {
         if (this.level instanceof ServerLevel) {
            this.raid = ((ServerLevel)this.level).getRaids().get(var1.getInt("RaidId"));
         }

         if (this.raid != null) {
            this.raid.addWaveMob(this.wave, this, false);
            if (this.isPatrolLeader()) {
               this.raid.setLeader(this.wave, this);
            }
         }
      }

   }

   protected void pickUpItem(ItemEntity var1) {
      ItemStack var2 = var1.getItem();
      boolean var3 = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
      if (this.hasActiveRaid() && !var3 && ItemStack.matches(var2, Raid.getLeaderBannerInstance())) {
         EquipmentSlot var4 = EquipmentSlot.HEAD;
         ItemStack var5 = this.getItemBySlot(var4);
         double var6 = (double)this.getEquipmentDropChance(var4);
         if (!var5.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < var6) {
            this.spawnAtLocation(var5);
         }

         this.setItemSlot(var4, var2);
         this.take(var1, var2.getCount());
         var1.remove();
         this.getCurrentRaid().setLeader(this.getWave(), this);
         this.setPatrolLeader(true);
      } else {
         super.pickUpItem(var1);
      }

   }

   public boolean removeWhenFarAway(double var1) {
      return this.getCurrentRaid() == null ? super.removeWhenFarAway(var1) : false;
   }

   public boolean requiresCustomPersistence() {
      return this.getCurrentRaid() != null;
   }

   public int getTicksOutsideRaid() {
      return this.ticksOutsideRaid;
   }

   public void setTicksOutsideRaid(int var1) {
      this.ticksOutsideRaid = var1;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.hasActiveRaid()) {
         this.getCurrentRaid().updateBossbar();
      }

      return super.hurt(var1, var2);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setCanJoinRaid(this.getType() != EntityType.WITCH || var3 != MobSpawnType.NATURAL);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public abstract SoundEvent getCelebrateSound();

   static {
      IS_CELEBRATING = SynchedEntityData.defineId(Raider.class, EntityDataSerializers.BOOLEAN);
      ALLOWED_ITEMS = (var0) -> {
         return !var0.hasPickUpDelay() && var0.isAlive() && ItemStack.matches(var0.getItem(), Raid.getLeaderBannerInstance());
      };
   }

   static class RaiderMoveThroughVillageGoal extends Goal {
      private final Raider raider;
      private final double speedModifier;
      private BlockPos poiPos;
      private final List visited = Lists.newArrayList();
      private final int distanceToPoi;
      private boolean stuck;

      public RaiderMoveThroughVillageGoal(Raider var1, double var2, int var4) {
         this.raider = var1;
         this.speedModifier = var2;
         this.distanceToPoi = var4;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         this.updateVisited();
         return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
      }

      private boolean isValidRaid() {
         return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
      }

      private boolean hasSuitablePoi() {
         ServerLevel var1 = (ServerLevel)this.raider.level;
         BlockPos var2 = new BlockPos(this.raider);
         Optional var3 = var1.getPoiManager().getRandom((var0) -> {
            return var0 == PoiType.HOME;
         }, this::hasNotVisited, PoiManager.Occupancy.ANY, var2, 48, this.raider.random);
         if (!var3.isPresent()) {
            return false;
         } else {
            this.poiPos = ((BlockPos)var3.get()).immutable();
            return true;
         }
      }

      public boolean canContinueToUse() {
         if (this.raider.getNavigation().isDone()) {
            return false;
         } else {
            return this.raider.getTarget() == null && !this.poiPos.closerThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
         }
      }

      public void stop() {
         if (this.poiPos.closerThan(this.raider.position(), (double)this.distanceToPoi)) {
            this.visited.add(this.poiPos);
         }

      }

      public void start() {
         super.start();
         this.raider.setNoActionTime(0);
         this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
         this.stuck = false;
      }

      public void tick() {
         if (this.raider.getNavigation().isDone()) {
            Vec3 var1 = new Vec3(this.poiPos);
            Vec3 var2 = RandomPos.getPosTowards(this.raider, 16, 7, var1, 0.3141592741012573D);
            if (var2 == null) {
               var2 = RandomPos.getPosTowards(this.raider, 8, 7, var1);
            }

            if (var2 == null) {
               this.stuck = true;
               return;
            }

            this.raider.getNavigation().moveTo(var2.x, var2.y, var2.z, this.speedModifier);
         }

      }

      private boolean hasNotVisited(BlockPos var1) {
         Iterator var2 = this.visited.iterator();

         BlockPos var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (BlockPos)var2.next();
         } while(!Objects.equals(var1, var3));

         return false;
      }

      private void updateVisited() {
         if (this.visited.size() > 2) {
            this.visited.remove(0);
         }

      }
   }

   public class HoldGroundAttackGoal extends Goal {
      private final Raider mob;
      private final float hostileRadiusSqr;
      public final TargetingConditions shoutTargeting = (new TargetingConditions()).range(8.0D).allowNonAttackable().allowInvulnerable().allowSameTeam().allowUnseeable().ignoreInvisibilityTesting();

      public HoldGroundAttackGoal(AbstractIllager var2, float var3) {
         this.mob = var2;
         this.hostileRadiusSqr = var3 * var3;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = this.mob.getLastHurtByMob();
         return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && (var1 == null || var1.getType() != EntityType.PLAYER);
      }

      public void start() {
         super.start();
         this.mob.getNavigation().stop();
         List var1 = this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Raider var3 = (Raider)var2.next();
            var3.setTarget(this.mob.getTarget());
         }

      }

      public void stop() {
         super.stop();
         LivingEntity var1 = this.mob.getTarget();
         if (var1 != null) {
            List var2 = this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               Raider var4 = (Raider)var3.next();
               var4.setTarget(var1);
               var4.setAggressive(true);
            }

            this.mob.setAggressive(true);
         }

      }

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

   public class RaiderCelebration extends Goal {
      private final Raider mob;

      RaiderCelebration(Raider var2) {
         this.mob = var2;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         Raid var1 = this.mob.getCurrentRaid();
         return this.mob.isAlive() && this.mob.getTarget() == null && var1 != null && var1.isLoss();
      }

      public void start() {
         this.mob.setCelebrating(true);
         super.start();
      }

      public void stop() {
         this.mob.setCelebrating(false);
         super.stop();
      }

      public void tick() {
         if (!this.mob.isSilent() && this.mob.random.nextInt(100) == 0) {
            Raider.this.playSound(Raider.this.getCelebrateSound(), Raider.this.getSoundVolume(), Raider.this.getVoicePitch());
         }

         if (!this.mob.isPassenger() && this.mob.random.nextInt(50) == 0) {
            this.mob.getJumpControl().jump();
         }

         super.tick();
      }
   }

   public class ObtainRaidLeaderBannerGoal extends Goal {
      private final Raider mob;

      public ObtainRaidLeaderBannerGoal(Raider var2) {
         this.mob = var2;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         Raid var1 = this.mob.getCurrentRaid();
         if (this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.canBeLeader() && !ItemStack.matches(this.mob.getItemBySlot(EquipmentSlot.HEAD), Raid.getLeaderBannerInstance())) {
            Raider var2 = var1.getLeader(this.mob.getWave());
            if (var2 == null || !var2.isAlive()) {
               List var3 = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), Raider.ALLOWED_ITEMS);
               if (!var3.isEmpty()) {
                  return this.mob.getNavigation().moveTo((Entity)var3.get(0), 1.149999976158142D);
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.mob.getNavigation().getTargetPos().closerThan(this.mob.position(), 1.414D)) {
            List var1 = this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(4.0D, 4.0D, 4.0D), Raider.ALLOWED_ITEMS);
            if (!var1.isEmpty()) {
               this.mob.pickUpItem((ItemEntity)var1.get(0));
            }
         }

      }
   }
}
