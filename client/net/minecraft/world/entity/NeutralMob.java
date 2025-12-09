package net.minecraft.world.entity;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public interface NeutralMob {
   String TAG_ANGER_END_TIME = "anger_end_time";
   String TAG_ANGRY_AT = "angry_at";
   long NO_ANGER_END_TIME = -1L;

   long getPersistentAngerEndTime();

   default void setTimeToRemainAngry(long var1) {
      this.setPersistentAngerEndTime(this.level().getGameTime() + var1);
   }

   void setPersistentAngerEndTime(long var1);

   @Nullable EntityReference<LivingEntity> getPersistentAngerTarget();

   void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> var1);

   void startPersistentAngerTimer();

   Level level();

   default void addPersistentAngerSaveData(ValueOutput var1) {
      var1.putLong("anger_end_time", this.getPersistentAngerEndTime());
      var1.storeNullable("angry_at", EntityReference.codec(), this.getPersistentAngerTarget());
   }

   default void readPersistentAngerSaveData(Level var1, ValueInput var2) {
      Optional var3 = var2.getLong("anger_end_time");
      if (var3.isPresent()) {
         this.setPersistentAngerEndTime((Long)var3.get());
      } else {
         Optional var4 = var2.getInt("AngerTime");
         if (var4.isPresent()) {
            this.setTimeToRemainAngry((long)(Integer)var4.get());
         } else {
            this.setPersistentAngerEndTime(-1L);
         }
      }

      if (var1 instanceof ServerLevel) {
         this.setPersistentAngerTarget(EntityReference.read(var2, "angry_at"));
         this.setTarget(EntityReference.getLivingEntity(this.getPersistentAngerTarget(), var1));
      }
   }

   default void updatePersistentAnger(ServerLevel var1, boolean var2) {
      LivingEntity var3 = this.getTarget();
      EntityReference var4 = this.getPersistentAngerTarget();
      if (var3 != null && var3.isDeadOrDying() && var4 != null && var4.matches(var3) && var3 instanceof Mob) {
         this.stopBeingAngry();
      } else {
         if (var3 != null) {
            if (var4 == null || !var4.matches(var3)) {
               this.setPersistentAngerTarget(EntityReference.of(var3));
            }

            this.startPersistentAngerTimer();
         }

         if (var4 != null && !this.isAngry() && (var3 == null || !isValidPlayerTarget(var3) || !var2)) {
            this.stopBeingAngry();
         }

      }
   }

   private static boolean isValidPlayerTarget(LivingEntity var0) {
      boolean var10000;
      if (var0 instanceof Player var1) {
         if (!var1.isCreative() && !var1.isSpectator()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   default boolean isAngryAt(LivingEntity var1, ServerLevel var2) {
      if (!this.canAttack(var1)) {
         return false;
      } else if (isValidPlayerTarget(var1) && this.isAngryAtAllPlayers(var2)) {
         return true;
      } else {
         EntityReference var3 = this.getPersistentAngerTarget();
         return var3 != null && var3.matches(var1);
      }
   }

   default boolean isAngryAtAllPlayers(ServerLevel var1) {
      return (Boolean)var1.getGameRules().get(GameRules.UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
   }

   default boolean isAngry() {
      long var1 = this.getPersistentAngerEndTime();
      if (var1 > 0L) {
         long var3 = var1 - this.level().getGameTime();
         return var3 > 0L;
      } else {
         return false;
      }
   }

   default void playerDied(ServerLevel var1, Player var2) {
      if ((Boolean)var1.getGameRules().get(GameRules.FORGIVE_DEAD_PLAYERS)) {
         EntityReference var3 = this.getPersistentAngerTarget();
         if (var3 != null && var3.matches(var2)) {
            this.stopBeingAngry();
         }
      }
   }

   default void forgetCurrentTargetAndRefreshUniversalAnger() {
      this.stopBeingAngry();
      this.startPersistentAngerTimer();
   }

   default void stopBeingAngry() {
      this.setLastHurtByMob((LivingEntity)null);
      this.setPersistentAngerTarget((EntityReference)null);
      this.setTarget((LivingEntity)null);
      this.setPersistentAngerEndTime(-1L);
   }

   @Nullable LivingEntity getLastHurtByMob();

   void setLastHurtByMob(@Nullable LivingEntity var1);

   void setTarget(@Nullable LivingEntity var1);

   boolean canAttack(LivingEntity var1);

   @Nullable LivingEntity getTarget();
}
