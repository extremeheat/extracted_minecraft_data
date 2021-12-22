package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

public abstract class TamableAnimal extends Animal implements OwnableEntity {
   protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;
   private boolean orderedToSit;

   protected TamableAnimal(EntityType<? extends TamableAnimal> var1, Level var2) {
      super(var1, var2);
      this.reassessTameGoals();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
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
            this.setTame(true);
         } catch (Throwable var4) {
            this.setTame(false);
         }
      }

      this.orderedToSit = var1.getBoolean("Sitting");
      this.setInSittingPose(this.orderedToSit);
   }

   public boolean canBeLeashed(Player var1) {
      return !this.isLeashed();
   }

   protected void spawnTamingParticles(boolean var1) {
      SimpleParticleType var2 = ParticleTypes.HEART;
      if (!var1) {
         var2 = ParticleTypes.SMOKE;
      }

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.random.nextGaussian() * 0.02D;
         double var6 = this.random.nextGaussian() * 0.02D;
         double var8 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(var2, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), var4, var6, var8);
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

   public void setTame(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 | 4));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 & -5));
      }

      this.reassessTameGoals();
   }

   protected void reassessTameGoals() {
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
      this.setTame(true);
      this.setOwnerUUID(var1.getUUID());
      if (var1 instanceof ServerPlayer) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)var1, this);
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      try {
         UUID var1 = this.getOwnerUUID();
         return var1 == null ? null : this.level.getPlayerByUUID(var1);
      } catch (IllegalArgumentException var2) {
         return null;
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

   public Team getTeam() {
      if (this.isTame()) {
         LivingEntity var1 = this.getOwner();
         if (var1 != null) {
            return var1.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isAlliedTo(Entity var1) {
      if (this.isTame()) {
         LivingEntity var2 = this.getOwner();
         if (var1 == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.isAlliedTo(var1);
         }
      }

      return super.isAlliedTo(var1);
   }

   public void die(DamageSource var1) {
      if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
         this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), Util.NIL_UUID);
      }

      super.die(var1);
   }

   public boolean isOrderedToSit() {
      return this.orderedToSit;
   }

   public void setOrderedToSit(boolean var1) {
      this.orderedToSit = var1;
   }

   // $FF: synthetic method
   @Nullable
   public Entity getOwner() {
      return this.getOwner();
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.BYTE);
      DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);
   }
}
