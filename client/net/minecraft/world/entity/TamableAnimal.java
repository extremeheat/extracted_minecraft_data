package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.scores.PlayerTeam;

public abstract class TamableAnimal extends Animal implements OwnableEntity {
   public static final int TELEPORT_WHEN_DISTANCE_IS_SQ = 144;
   private static final int MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 2;
   private static final int MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 3;
   private static final int MAX_VERTICAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 1;
   protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;
   private boolean orderedToSit;

   protected TamableAnimal(EntityType<? extends TamableAnimal> var1, Level var2) {
      super(var1, var2);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_FLAGS_ID, (byte)0);
      var1.define(DATA_OWNERUUID_ID, Optional.empty());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.getOwnerUUID() != null) {
         var1.putUUID("Owner", this.getOwnerUUID());
      }

      var1.putBoolean("Sitting", this.orderedToSit);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      UUID var2;
      if (var1.hasUUID("Owner")) {
         var2 = var1.getUUID("Owner");
      } else {
         String var3 = var1.getString("Owner");
         var2 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), var3);
      }

      if (var2 != null) {
         try {
            this.setOwnerUUID(var2);
            this.setTame(true, false);
         } catch (Throwable var4) {
            this.setTame(false, true);
         }
      }

      this.orderedToSit = var1.getBoolean("Sitting");
      this.setInSittingPose(this.orderedToSit);
   }

   public boolean canBeLeashed() {
      return true;
   }

   public boolean handleLeashAtDistance(Entity var1, float var2) {
      if (this.isInSittingPose()) {
         if (var2 > 10.0F) {
            this.dropLeash(true, true);
         }

         return false;
      } else {
         return super.handleLeashAtDistance(var1, var2);
      }
   }

   protected void spawnTamingParticles(boolean var1) {
      SimpleParticleType var2 = ParticleTypes.HEART;
      if (!var1) {
         var2 = ParticleTypes.SMOKE;
      }

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.random.nextGaussian() * 0.02;
         double var6 = this.random.nextGaussian() * 0.02;
         double var8 = this.random.nextGaussian() * 0.02;
         this.level().addParticle(var2, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var4, var6, var8);
      }

   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 7) {
         this.spawnTamingParticles(true);
      } else if (var1 == 6) {
         this.spawnTamingParticles(false);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public boolean isTame() {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
   }

   public void setTame(boolean var1, boolean var2) {
      byte var3 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var3 | 4));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var3 & -5));
      }

      if (var2) {
         this.applyTamingSideEffects();
      }

   }

   protected void applyTamingSideEffects() {
   }

   public boolean isInSittingPose() {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setInSittingPose(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 & -2));
      }

   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse((Object)null);
   }

   public void setOwnerUUID(@Nullable UUID var1) {
      this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(var1));
   }

   public void tame(Player var1) {
      this.setTame(true, true);
      this.setOwnerUUID(var1.getUUID());
      if (var1 instanceof ServerPlayer var2) {
         CriteriaTriggers.TAME_ANIMAL.trigger(var2, this);
      }

   }

   public boolean canAttack(LivingEntity var1) {
      return this.isOwnedBy(var1) ? false : super.canAttack(var1);
   }

   public boolean isOwnedBy(LivingEntity var1) {
      return var1 == this.getOwner();
   }

   public boolean wantsToAttack(LivingEntity var1, LivingEntity var2) {
      return true;
   }

   public PlayerTeam getTeam() {
      if (this.isTame()) {
         LivingEntity var1 = this.getOwner();
         if (var1 != null) {
            return var1.getTeam();
         }
      }

      return super.getTeam();
   }

   protected boolean considersEntityAsAlly(Entity var1) {
      if (this.isTame()) {
         LivingEntity var2 = this.getOwner();
         if (var1 == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.considersEntityAsAlly(var1);
         }
      }

      return super.considersEntityAsAlly(var1);
   }

   public void die(DamageSource var1) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         if (var2.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
            LivingEntity var4 = this.getOwner();
            if (var4 instanceof ServerPlayer) {
               ServerPlayer var5 = (ServerPlayer)var4;
               var5.sendSystemMessage(this.getCombatTracker().getDeathMessage());
            }
         }
      }

      super.die(var1);
   }

   public boolean isOrderedToSit() {
      return this.orderedToSit;
   }

   public void setOrderedToSit(boolean var1) {
      this.orderedToSit = var1;
   }

   public void tryToTeleportToOwner() {
      LivingEntity var1 = this.getOwner();
      if (var1 != null) {
         this.teleportToAroundBlockPos(var1.blockPosition());
      }

   }

   public boolean shouldTryTeleportToOwner() {
      LivingEntity var1 = this.getOwner();
      return var1 != null && this.distanceToSqr(this.getOwner()) >= 144.0;
   }

   private void teleportToAroundBlockPos(BlockPos var1) {
      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.random.nextIntBetweenInclusive(-3, 3);
         int var4 = this.random.nextIntBetweenInclusive(-3, 3);
         if (Math.abs(var3) >= 2 || Math.abs(var4) >= 2) {
            int var5 = this.random.nextIntBetweenInclusive(-1, 1);
            if (this.maybeTeleportTo(var1.getX() + var3, var1.getY() + var5, var1.getZ() + var4)) {
               return;
            }
         }
      }

   }

   private boolean maybeTeleportTo(int var1, int var2, int var3) {
      if (!this.canTeleportTo(new BlockPos(var1, var2, var3))) {
         return false;
      } else {
         this.moveTo((double)var1 + 0.5, (double)var2, (double)var3 + 0.5, this.getYRot(), this.getXRot());
         this.navigation.stop();
         return true;
      }
   }

   private boolean canTeleportTo(BlockPos var1) {
      PathType var2 = WalkNodeEvaluator.getPathTypeStatic((Mob)this, (BlockPos)var1);
      if (var2 != PathType.WALKABLE) {
         return false;
      } else {
         BlockState var3 = this.level().getBlockState(var1.below());
         if (!this.canFlyToOwner() && var3.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos var4 = var1.subtract(this.blockPosition());
            return this.level().noCollision(this, this.getBoundingBox().move(var4));
         }
      }
   }

   public final boolean unableToMoveToOwner() {
      return this.isOrderedToSit() || this.isPassenger() || this.mayBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
   }

   protected boolean canFlyToOwner() {
      return false;
   }

   // $FF: synthetic method
   public EntityGetter level() {
      return super.level();
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.BYTE);
      DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);
   }

   public class TamableAnimalPanicGoal extends PanicGoal {
      public TamableAnimalPanicGoal(final double var2, final TagKey<DamageType> var4) {
         super(TamableAnimal.this, var2, (TagKey)var4);
      }

      public TamableAnimalPanicGoal(final double var2) {
         super(TamableAnimal.this, var2);
      }

      public void tick() {
         if (!TamableAnimal.this.unableToMoveToOwner() && TamableAnimal.this.shouldTryTeleportToOwner()) {
            TamableAnimal.this.tryToTeleportToOwner();
         }

         super.tick();
      }
   }
}
