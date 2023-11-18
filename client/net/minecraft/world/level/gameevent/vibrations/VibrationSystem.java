package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface VibrationSystem {
   GameEvent[] RESONANCE_EVENTS = new GameEvent[]{
      GameEvent.RESONATE_1,
      GameEvent.RESONATE_2,
      GameEvent.RESONATE_3,
      GameEvent.RESONATE_4,
      GameEvent.RESONATE_5,
      GameEvent.RESONATE_6,
      GameEvent.RESONATE_7,
      GameEvent.RESONATE_8,
      GameEvent.RESONATE_9,
      GameEvent.RESONATE_10,
      GameEvent.RESONATE_11,
      GameEvent.RESONATE_12,
      GameEvent.RESONATE_13,
      GameEvent.RESONATE_14,
      GameEvent.RESONATE_15
   };
   ToIntFunction<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Util.make(new Object2IntOpenHashMap(), var0 -> {
      var0.defaultReturnValue(0);
      var0.put(GameEvent.STEP, 1);
      var0.put(GameEvent.SWIM, 1);
      var0.put(GameEvent.FLAP, 1);
      var0.put(GameEvent.PROJECTILE_LAND, 2);
      var0.put(GameEvent.HIT_GROUND, 2);
      var0.put(GameEvent.SPLASH, 2);
      var0.put(GameEvent.ITEM_INTERACT_FINISH, 3);
      var0.put(GameEvent.PROJECTILE_SHOOT, 3);
      var0.put(GameEvent.INSTRUMENT_PLAY, 3);
      var0.put(GameEvent.ENTITY_ACTION, 4);
      var0.put(GameEvent.ELYTRA_GLIDE, 4);
      var0.put(GameEvent.UNEQUIP, 4);
      var0.put(GameEvent.ENTITY_DISMOUNT, 5);
      var0.put(GameEvent.EQUIP, 5);
      var0.put(GameEvent.ENTITY_INTERACT, 6);
      var0.put(GameEvent.SHEAR, 6);
      var0.put(GameEvent.ENTITY_MOUNT, 6);
      var0.put(GameEvent.ENTITY_DAMAGE, 7);
      var0.put(GameEvent.DRINK, 8);
      var0.put(GameEvent.EAT, 8);
      var0.put(GameEvent.CONTAINER_CLOSE, 9);
      var0.put(GameEvent.BLOCK_CLOSE, 9);
      var0.put(GameEvent.BLOCK_DEACTIVATE, 9);
      var0.put(GameEvent.BLOCK_DETACH, 9);
      var0.put(GameEvent.CONTAINER_OPEN, 10);
      var0.put(GameEvent.BLOCK_OPEN, 10);
      var0.put(GameEvent.BLOCK_ACTIVATE, 10);
      var0.put(GameEvent.BLOCK_ATTACH, 10);
      var0.put(GameEvent.PRIME_FUSE, 10);
      var0.put(GameEvent.NOTE_BLOCK_PLAY, 10);
      var0.put(GameEvent.BLOCK_CHANGE, 11);
      var0.put(GameEvent.BLOCK_DESTROY, 12);
      var0.put(GameEvent.FLUID_PICKUP, 12);
      var0.put(GameEvent.BLOCK_PLACE, 13);
      var0.put(GameEvent.FLUID_PLACE, 13);
      var0.put(GameEvent.ENTITY_PLACE, 14);
      var0.put(GameEvent.LIGHTNING_STRIKE, 14);
      var0.put(GameEvent.TELEPORT, 14);
      var0.put(GameEvent.ENTITY_DIE, 15);
      var0.put(GameEvent.EXPLODE, 15);

      for(int var1 = 1; var1 <= 15; ++var1) {
         var0.put(getResonanceEventByFrequency(var1), var1);
      }
   });

   VibrationSystem.Data getVibrationData();

   VibrationSystem.User getVibrationUser();

   static int getGameEventFrequency(GameEvent var0) {
      return VIBRATION_FREQUENCY_FOR_EVENT.applyAsInt(var0);
   }

   static GameEvent getResonanceEventByFrequency(int var0) {
      return RESONANCE_EVENTS[var0 - 1];
   }

   static int getRedstoneStrengthForDistance(float var0, int var1) {
      double var2 = 15.0 / (double)var1;
      return Math.max(1, 15 - Mth.floor(var2 * (double)var0));
   }

   public static final class Data {
      public static Codec<VibrationSystem.Data> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  VibrationInfo.CODEC.optionalFieldOf("event").forGetter(var0x -> Optional.ofNullable(var0x.currentVibration)),
                  VibrationSelector.CODEC.fieldOf("selector").forGetter(VibrationSystem.Data::getSelectionStrategy),
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(VibrationSystem.Data::getTravelTimeInTicks)
               )
               .apply(var0, (var0x, var1, var2) -> new VibrationSystem.Data((VibrationInfo)var0x.orElse(null), var1, var2, true))
      );
      public static final String NBT_TAG_KEY = "listener";
      @Nullable
      VibrationInfo currentVibration;
      private int travelTimeInTicks;
      final VibrationSelector selectionStrategy;
      private boolean reloadVibrationParticle;

      private Data(@Nullable VibrationInfo var1, VibrationSelector var2, int var3, boolean var4) {
         super();
         this.currentVibration = var1;
         this.travelTimeInTicks = var3;
         this.selectionStrategy = var2;
         this.reloadVibrationParticle = var4;
      }

      public Data() {
         this(null, new VibrationSelector(), 0, false);
      }

      public VibrationSelector getSelectionStrategy() {
         return this.selectionStrategy;
      }

      @Nullable
      public VibrationInfo getCurrentVibration() {
         return this.currentVibration;
      }

      public void setCurrentVibration(@Nullable VibrationInfo var1) {
         this.currentVibration = var1;
      }

      public int getTravelTimeInTicks() {
         return this.travelTimeInTicks;
      }

      public void setTravelTimeInTicks(int var1) {
         this.travelTimeInTicks = var1;
      }

      public void decrementTravelTime() {
         this.travelTimeInTicks = Math.max(0, this.travelTimeInTicks - 1);
      }

      public boolean shouldReloadVibrationParticle() {
         return this.reloadVibrationParticle;
      }

      public void setReloadVibrationParticle(boolean var1) {
         this.reloadVibrationParticle = var1;
      }
   }

   public static class Listener implements GameEventListener {
      private final VibrationSystem system;

      public Listener(VibrationSystem var1) {
         super();
         this.system = var1;
      }

      @Override
      public PositionSource getListenerSource() {
         return this.system.getVibrationUser().getPositionSource();
      }

      @Override
      public int getListenerRadius() {
         return this.system.getVibrationUser().getListenerRadius();
      }

      @Override
      public boolean handleGameEvent(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4) {
         VibrationSystem.Data var5 = this.system.getVibrationData();
         VibrationSystem.User var6 = this.system.getVibrationUser();
         if (var5.getCurrentVibration() != null) {
            return false;
         } else if (!var6.isValidVibration(var2, var3)) {
            return false;
         } else {
            Optional var7 = var6.getPositionSource().getPosition(var1);
            if (var7.isEmpty()) {
               return false;
            } else {
               Vec3 var8 = (Vec3)var7.get();
               if (!var6.canReceiveVibration(var1, BlockPos.containing(var4), var2, var3)) {
                  return false;
               } else if (isOccluded(var1, var4, var8)) {
                  return false;
               } else {
                  this.scheduleVibration(var1, var5, var2, var3, var4, var8);
                  return true;
               }
            }
         }
      }

      public void forceScheduleVibration(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4) {
         this.system
            .getVibrationUser()
            .getPositionSource()
            .getPosition(var1)
            .ifPresent(var5 -> this.scheduleVibration(var1, this.system.getVibrationData(), var2, var3, var4, var5));
      }

      private void scheduleVibration(ServerLevel var1, VibrationSystem.Data var2, GameEvent var3, GameEvent.Context var4, Vec3 var5, Vec3 var6) {
         var2.selectionStrategy.addCandidate(new VibrationInfo(var3, (float)var5.distanceTo(var6), var5, var4.sourceEntity()), var1.getGameTime());
      }

      public static float distanceBetweenInBlocks(BlockPos var0, BlockPos var1) {
         return (float)Math.sqrt(var0.distSqr(var1));
      }

      private static boolean isOccluded(Level var0, Vec3 var1, Vec3 var2) {
         Vec3 var3 = new Vec3((double)Mth.floor(var1.x) + 0.5, (double)Mth.floor(var1.y) + 0.5, (double)Mth.floor(var1.z) + 0.5);
         Vec3 var4 = new Vec3((double)Mth.floor(var2.x) + 0.5, (double)Mth.floor(var2.y) + 0.5, (double)Mth.floor(var2.z) + 0.5);

         for(Direction var8 : Direction.values()) {
            Vec3 var9 = var3.relative(var8, 9.999999747378752E-6);
            if (var0.isBlockInLine(new ClipBlockStateContext(var9, var4, var0x -> var0x.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType()
               != HitResult.Type.BLOCK) {
               return false;
            }
         }

         return true;
      }
   }

   public interface Ticker {
      static void tick(Level var0, VibrationSystem.Data var1, VibrationSystem.User var2) {
         if (var0 instanceof ServerLevel var3) {
            if (var1.currentVibration == null) {
               trySelectAndScheduleVibration((ServerLevel)var3, var1, var2);
            }

            if (var1.currentVibration != null) {
               boolean var4 = var1.getTravelTimeInTicks() > 0;
               tryReloadVibrationParticle((ServerLevel)var3, var1, var2);
               var1.decrementTravelTime();
               if (var1.getTravelTimeInTicks() <= 0) {
                  var4 = receiveVibration((ServerLevel)var3, var1, var2, var1.currentVibration);
               }

               if (var4) {
                  var2.onDataChanged();
               }
            }
         }
      }

      private static void trySelectAndScheduleVibration(ServerLevel var0, VibrationSystem.Data var1, VibrationSystem.User var2) {
         var1.getSelectionStrategy()
            .chosenCandidate(var0.getGameTime())
            .ifPresent(
               var3 -> {
                  var1.setCurrentVibration(var3);
                  Vec3 var4 = var3.pos();
                  var1.setTravelTimeInTicks(var2.calculateTravelTimeInTicks(var3.distance()));
                  var0.sendParticles(
                     new VibrationParticleOption(var2.getPositionSource(), var1.getTravelTimeInTicks()), var4.x, var4.y, var4.z, 1, 0.0, 0.0, 0.0, 0.0
                  );
                  var2.onDataChanged();
                  var1.getSelectionStrategy().startOver();
               }
            );
      }

      private static void tryReloadVibrationParticle(ServerLevel var0, VibrationSystem.Data var1, VibrationSystem.User var2) {
         if (var1.shouldReloadVibrationParticle()) {
            if (var1.currentVibration == null) {
               var1.setReloadVibrationParticle(false);
            } else {
               Vec3 var3 = var1.currentVibration.pos();
               PositionSource var4 = var2.getPositionSource();
               Vec3 var5 = var4.getPosition(var0).orElse(var3);
               int var6 = var1.getTravelTimeInTicks();
               int var7 = var2.calculateTravelTimeInTicks(var1.currentVibration.distance());
               double var8 = 1.0 - (double)var6 / (double)var7;
               double var10 = Mth.lerp(var8, var3.x, var5.x);
               double var12 = Mth.lerp(var8, var3.y, var5.y);
               double var14 = Mth.lerp(var8, var3.z, var5.z);
               boolean var16 = var0.sendParticles(new VibrationParticleOption(var4, var6), var10, var12, var14, 1, 0.0, 0.0, 0.0, 0.0) > 0;
               if (var16) {
                  var1.setReloadVibrationParticle(false);
               }
            }
         }
      }

      private static boolean receiveVibration(ServerLevel var0, VibrationSystem.Data var1, VibrationSystem.User var2, VibrationInfo var3) {
         BlockPos var4 = BlockPos.containing(var3.pos());
         BlockPos var5 = var2.getPositionSource().getPosition(var0).map(BlockPos::containing).orElse(var4);
         if (var2.requiresAdjacentChunksToBeTicking() && !areAdjacentChunksTicking(var0, var5)) {
            return false;
         } else {
            var2.onReceiveVibration(
               var0,
               var4,
               var3.gameEvent(),
               var3.getEntity(var0).orElse(null),
               var3.getProjectileOwner(var0).orElse(null),
               VibrationSystem.Listener.distanceBetweenInBlocks(var4, var5)
            );
            var1.setCurrentVibration(null);
            return true;
         }
      }

      private static boolean areAdjacentChunksTicking(Level var0, BlockPos var1) {
         ChunkPos var2 = new ChunkPos(var1);

         for(int var3 = var2.x - 1; var3 <= var2.x + 1; ++var3) {
            for(int var4 = var2.z - 1; var4 <= var2.z + 1; ++var4) {
               if (!var0.shouldTickBlocksAt(ChunkPos.asLong(var3, var4)) || var0.getChunkSource().getChunkNow(var3, var4) == null) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public interface User {
      int getListenerRadius();

      PositionSource getPositionSource();

      boolean canReceiveVibration(ServerLevel var1, BlockPos var2, GameEvent var3, GameEvent.Context var4);

      void onReceiveVibration(ServerLevel var1, BlockPos var2, GameEvent var3, @Nullable Entity var4, @Nullable Entity var5, float var6);

      default TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.VIBRATIONS;
      }

      default boolean canTriggerAvoidVibration() {
         return false;
      }

      default boolean requiresAdjacentChunksToBeTicking() {
         return false;
      }

      default int calculateTravelTimeInTicks(float var1) {
         return Mth.floor(var1);
      }

      default boolean isValidVibration(GameEvent var1, GameEvent.Context var2) {
         if (!var1.is(this.getListenableEvents())) {
            return false;
         } else {
            Entity var3 = var2.sourceEntity();
            if (var3 != null) {
               if (var3.isSpectator()) {
                  return false;
               }

               if (var3.isSteppingCarefully() && var1.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                  if (this.canTriggerAvoidVibration() && var3 instanceof ServerPlayer var4) {
                     CriteriaTriggers.AVOID_VIBRATION.trigger((ServerPlayer)var4);
                  }

                  return false;
               }

               if (var3.dampensVibrations()) {
                  return false;
               }
            }

            if (var2.affectedState() != null) {
               return !var2.affectedState().is(BlockTags.DAMPENS_VIBRATIONS);
            } else {
               return true;
            }
         }
      }

      default void onDataChanged() {
      }
   }
}
