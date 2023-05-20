package net.minecraft.world.level.gameevent.vibrations;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
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
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VibrationListener implements GameEventListener {
   @VisibleForTesting
   public static final Object2IntMap<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap(), var0 -> {
      var0.put(GameEvent.STEP, 1);
      var0.put(GameEvent.ITEM_INTERACT_FINISH, 2);
      var0.put(GameEvent.FLAP, 2);
      var0.put(GameEvent.SWIM, 3);
      var0.put(GameEvent.ELYTRA_GLIDE, 4);
      var0.put(GameEvent.HIT_GROUND, 5);
      var0.put(GameEvent.TELEPORT, 5);
      var0.put(GameEvent.SPLASH, 6);
      var0.put(GameEvent.ENTITY_SHAKE, 6);
      var0.put(GameEvent.BLOCK_CHANGE, 6);
      var0.put(GameEvent.NOTE_BLOCK_PLAY, 6);
      var0.put(GameEvent.ENTITY_DISMOUNT, 6);
      var0.put(GameEvent.PROJECTILE_SHOOT, 7);
      var0.put(GameEvent.DRINK, 7);
      var0.put(GameEvent.PRIME_FUSE, 7);
      var0.put(GameEvent.ENTITY_MOUNT, 7);
      var0.put(GameEvent.PROJECTILE_LAND, 8);
      var0.put(GameEvent.EAT, 8);
      var0.put(GameEvent.ENTITY_INTERACT, 8);
      var0.put(GameEvent.ENTITY_DAMAGE, 8);
      var0.put(GameEvent.EQUIP, 9);
      var0.put(GameEvent.SHEAR, 9);
      var0.put(GameEvent.ENTITY_ROAR, 9);
      var0.put(GameEvent.BLOCK_CLOSE, 10);
      var0.put(GameEvent.BLOCK_DEACTIVATE, 10);
      var0.put(GameEvent.BLOCK_DETACH, 10);
      var0.put(GameEvent.DISPENSE_FAIL, 10);
      var0.put(GameEvent.BLOCK_OPEN, 11);
      var0.put(GameEvent.BLOCK_ACTIVATE, 11);
      var0.put(GameEvent.BLOCK_ATTACH, 11);
      var0.put(GameEvent.ENTITY_PLACE, 12);
      var0.put(GameEvent.BLOCK_PLACE, 12);
      var0.put(GameEvent.FLUID_PLACE, 12);
      var0.put(GameEvent.ENTITY_DIE, 13);
      var0.put(GameEvent.BLOCK_DESTROY, 13);
      var0.put(GameEvent.FLUID_PICKUP, 13);
      var0.put(GameEvent.CONTAINER_CLOSE, 14);
      var0.put(GameEvent.PISTON_CONTRACT, 14);
      var0.put(GameEvent.PISTON_EXTEND, 15);
      var0.put(GameEvent.CONTAINER_OPEN, 15);
      var0.put(GameEvent.EXPLODE, 15);
      var0.put(GameEvent.LIGHTNING_STRIKE, 15);
      var0.put(GameEvent.INSTRUMENT_PLAY, 15);
   }));
   protected final PositionSource listenerSource;
   protected final int listenerRange;
   protected final VibrationListener.VibrationListenerConfig config;
   @Nullable
   protected VibrationInfo currentVibration;
   protected int travelTimeInTicks;
   private final VibrationSelector selectionStrategy;

   public static Codec<VibrationListener> codec(VibrationListener.VibrationListenerConfig var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(
                  PositionSource.CODEC.fieldOf("source").forGetter(var0xx -> var0xx.listenerSource),
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter(var0xx -> var0xx.listenerRange),
                  VibrationInfo.CODEC.optionalFieldOf("event").forGetter(var0xx -> Optional.ofNullable(var0xx.currentVibration)),
                  VibrationSelector.CODEC.fieldOf("selector").forGetter(var0xx -> var0xx.selectionStrategy),
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(var0xx -> var0xx.travelTimeInTicks)
               )
               .apply(var1, (var1x, var2, var3, var4, var5) -> new VibrationListener(var1x, var2, var0, (VibrationInfo)var3.orElse(null), var4, var5))
      );
   }

   private VibrationListener(
      PositionSource var1, int var2, VibrationListener.VibrationListenerConfig var3, @Nullable VibrationInfo var4, VibrationSelector var5, int var6
   ) {
      super();
      this.listenerSource = var1;
      this.listenerRange = var2;
      this.config = var3;
      this.currentVibration = var4;
      this.travelTimeInTicks = var6;
      this.selectionStrategy = var5;
   }

   public VibrationListener(PositionSource var1, int var2, VibrationListener.VibrationListenerConfig var3) {
      this(var1, var2, var3, null, new VibrationSelector(), 0);
   }

   public static int getGameEventFrequency(GameEvent var0) {
      return VIBRATION_FREQUENCY_FOR_EVENT.getOrDefault(var0, 0);
   }

   public void tick(Level var1) {
      if (var1 instanceof ServerLevel var2) {
         if (this.currentVibration == null) {
            this.selectionStrategy.chosenCandidate(((ServerLevel)var2).getGameTime()).ifPresent(var2x -> {
               this.currentVibration = var2x;
               Vec3 var3 = this.currentVibration.pos();
               this.travelTimeInTicks = Mth.floor(this.currentVibration.distance());
               var2.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), var3.x, var3.y, var3.z, 1, 0.0, 0.0, 0.0, 0.0);
               this.config.onSignalSchedule();
               this.selectionStrategy.startOver();
            });
         }

         if (this.currentVibration != null) {
            --this.travelTimeInTicks;
            if (this.travelTimeInTicks <= 0) {
               this.travelTimeInTicks = 0;
               this.config
                  .onSignalReceive(
                     (ServerLevel)var2,
                     this,
                     BlockPos.containing(this.currentVibration.pos()),
                     this.currentVibration.gameEvent(),
                     this.currentVibration.getEntity((ServerLevel)var2).orElse(null),
                     this.currentVibration.getProjectileOwner((ServerLevel)var2).orElse(null),
                     this.currentVibration.distance()
                  );
               this.currentVibration = null;
            }
         }
      }
   }

   @Override
   public PositionSource getListenerSource() {
      return this.listenerSource;
   }

   @Override
   public int getListenerRadius() {
      return this.listenerRange;
   }

   @Override
   public boolean handleGameEvent(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4) {
      if (this.currentVibration != null) {
         return false;
      } else if (!this.config.isValidVibration(var2, var3)) {
         return false;
      } else {
         Optional var5 = this.listenerSource.getPosition(var1);
         if (var5.isEmpty()) {
            return false;
         } else {
            Vec3 var6 = (Vec3)var5.get();
            if (!this.config.shouldListen(var1, this, BlockPos.containing(var4), var2, var3)) {
               return false;
            } else if (isOccluded(var1, var4, var6)) {
               return false;
            } else {
               this.scheduleVibration(var1, var2, var3, var4, var6);
               return true;
            }
         }
      }
   }

   public void forceGameEvent(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4) {
      this.listenerSource.getPosition(var1).ifPresent(var5 -> this.scheduleVibration(var1, var2, var3, var4, var5));
   }

   public void scheduleVibration(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4, Vec3 var5) {
      this.selectionStrategy.addCandidate(new VibrationInfo(var2, (float)var4.distanceTo(var5), var4, var3.sourceEntity()), var1.getGameTime());
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

   public interface VibrationListenerConfig {
      default TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.VIBRATIONS;
      }

      default boolean canTriggerAvoidVibration() {
         return false;
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

      boolean shouldListen(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Context var5);

      void onSignalReceive(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7);

      default void onSignalSchedule() {
      }
   }
}
