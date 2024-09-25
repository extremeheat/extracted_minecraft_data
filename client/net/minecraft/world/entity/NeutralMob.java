package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface NeutralMob {
   String TAG_ANGER_TIME = "AngerTime";
   String TAG_ANGRY_AT = "AngryAt";

   int getRemainingPersistentAngerTime();

   void setRemainingPersistentAngerTime(int var1);

   @Nullable
   UUID getPersistentAngerTarget();

   void setPersistentAngerTarget(@Nullable UUID var1);

   void startPersistentAngerTimer();

   default void addPersistentAngerSaveData(CompoundTag var1) {
      var1.putInt("AngerTime", this.getRemainingPersistentAngerTime());
      if (this.getPersistentAngerTarget() != null) {
         var1.putUUID("AngryAt", this.getPersistentAngerTarget());
      }
   }

   default void readPersistentAngerSaveData(Level var1, CompoundTag var2) {
      this.setRemainingPersistentAngerTime(var2.getInt("AngerTime"));
      if (var1 instanceof ServerLevel) {
         if (!var2.hasUUID("AngryAt")) {
            this.setPersistentAngerTarget(null);
         } else {
            UUID var3 = var2.getUUID("AngryAt");
            this.setPersistentAngerTarget(var3);
            Entity var4 = ((ServerLevel)var1).getEntity(var3);
            if (var4 != null) {
               if (var4 instanceof Mob var5) {
                  this.setTarget(var5);
                  this.setLastHurtByMob(var5);
               }

               if (var4 instanceof Player var6) {
                  this.setTarget(var6);
                  this.setLastHurtByPlayer(var6);
               }
            }
         }
      }
   }

   default void updatePersistentAnger(ServerLevel var1, boolean var2) {
      LivingEntity var3 = this.getTarget();
      UUID var4 = this.getPersistentAngerTarget();
      if ((var3 == null || var3.isDeadOrDying()) && var4 != null && var1.getEntity(var4) instanceof Mob) {
         this.stopBeingAngry();
      } else {
         if (var3 != null && !Objects.equals(var4, var3.getUUID())) {
            this.setPersistentAngerTarget(var3.getUUID());
            this.startPersistentAngerTimer();
         }

         if (this.getRemainingPersistentAngerTime() > 0 && (var3 == null || var3.getType() != EntityType.PLAYER || !var2)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
               this.stopBeingAngry();
            }
         }
      }
   }

   default boolean isAngryAt(LivingEntity var1, ServerLevel var2) {
      if (!this.canAttack(var1)) {
         return false;
      } else {
         return var1.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers(var2) ? true : var1.getUUID().equals(this.getPersistentAngerTarget());
      }
   }

   default boolean isAngryAtAllPlayers(ServerLevel var1) {
      return var1.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
   }

   default boolean isAngry() {
      return this.getRemainingPersistentAngerTime() > 0;
   }

   default void playerDied(ServerLevel var1, Player var2) {
      if (var1.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         if (var2.getUUID().equals(this.getPersistentAngerTarget())) {
            this.stopBeingAngry();
         }
      }
   }

   default void forgetCurrentTargetAndRefreshUniversalAnger() {
      this.stopBeingAngry();
      this.startPersistentAngerTimer();
   }

   default void stopBeingAngry() {
      this.setLastHurtByMob(null);
      this.setPersistentAngerTarget(null);
      this.setTarget(null);
      this.setRemainingPersistentAngerTime(0);
   }

   @Nullable
   LivingEntity getLastHurtByMob();

   void setLastHurtByMob(@Nullable LivingEntity var1);

   void setLastHurtByPlayer(@Nullable Player var1);

   void setTarget(@Nullable LivingEntity var1);

   boolean canAttack(LivingEntity var1);

   @Nullable
   LivingEntity getTarget();
}
