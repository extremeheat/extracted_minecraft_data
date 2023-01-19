package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VibrationListener implements GameEventListener {
   protected final PositionSource listenerSource;
   protected final int listenerRange;
   protected final VibrationListener.VibrationListenerConfig config;
   @Nullable
   protected VibrationListener.ReceivingEvent receivingEvent;
   protected float receivingDistance;
   protected int travelTimeInTicks;

   public static Codec<VibrationListener> codec(VibrationListener.VibrationListenerConfig var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(
                  PositionSource.CODEC.fieldOf("source").forGetter(var0xx -> var0xx.listenerSource),
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter(var0xx -> var0xx.listenerRange),
                  VibrationListener.ReceivingEvent.CODEC.optionalFieldOf("event").forGetter(var0xx -> Optional.ofNullable(var0xx.receivingEvent)),
                  Codec.floatRange(0.0F, 3.4028235E38F).fieldOf("event_distance").orElse(0.0F).forGetter(var0xx -> var0xx.receivingDistance),
                  ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(var0xx -> var0xx.travelTimeInTicks)
               )
               .apply(
                  var1,
                  (var1x, var2, var3, var4, var5) -> new VibrationListener(var1x, var2, var0, (VibrationListener.ReceivingEvent)var3.orElse(null), var4, var5)
               )
      );
   }

   public VibrationListener(
      PositionSource var1, int var2, VibrationListener.VibrationListenerConfig var3, @Nullable VibrationListener.ReceivingEvent var4, float var5, int var6
   ) {
      super();
      this.listenerSource = var1;
      this.listenerRange = var2;
      this.config = var3;
      this.receivingEvent = var4;
      this.receivingDistance = var5;
      this.travelTimeInTicks = var6;
   }

   public void tick(Level var1) {
      if (var1 instanceof ServerLevel var2 && this.receivingEvent != null) {
         --this.travelTimeInTicks;
         if (this.travelTimeInTicks <= 0) {
            this.travelTimeInTicks = 0;
            this.config
               .onSignalReceive(
                  (ServerLevel)var2,
                  this,
                  new BlockPos(this.receivingEvent.pos),
                  this.receivingEvent.gameEvent,
                  this.receivingEvent.getEntity((ServerLevel)var2).orElse(null),
                  this.receivingEvent.getProjectileOwner((ServerLevel)var2).orElse(null),
                  this.receivingDistance
               );
            this.receivingEvent = null;
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
   public boolean handleGameEvent(ServerLevel var1, GameEvent.Message var2) {
      if (this.receivingEvent != null) {
         return false;
      } else {
         GameEvent var3 = var2.gameEvent();
         GameEvent.Context var4 = var2.context();
         if (!this.config.isValidVibration(var3, var4)) {
            return false;
         } else {
            Optional var5 = this.listenerSource.getPosition(var1);
            if (var5.isEmpty()) {
               return false;
            } else {
               Vec3 var6 = var2.source();
               Vec3 var7 = (Vec3)var5.get();
               if (!this.config.shouldListen(var1, this, new BlockPos(var6), var3, var4)) {
                  return false;
               } else if (isOccluded(var1, var6, var7)) {
                  return false;
               } else {
                  this.scheduleSignal(var1, var3, var4, var6, var7);
                  return true;
               }
            }
         }
      }
   }

   private void scheduleSignal(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4, Vec3 var5) {
      this.receivingDistance = (float)var4.distanceTo(var5);
      this.receivingEvent = new VibrationListener.ReceivingEvent(var2, this.receivingDistance, var4, var3.sourceEntity());
      this.travelTimeInTicks = Mth.floor(this.receivingDistance);
      var1.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), var4.x, var4.y, var4.z, 1, 0.0, 0.0, 0.0, 0.0);
      this.config.onSignalSchedule();
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

   public static record ReceivingEvent(GameEvent b, float c, Vec3 d, @Nullable UUID e, @Nullable UUID f, @Nullable Entity g) {
      final GameEvent gameEvent;
      private final float distance;
      final Vec3 pos;
      @Nullable
      private final UUID uuid;
      @Nullable
      private final UUID projectileOwnerUuid;
      @Nullable
      private final Entity entity;
      public static final Codec<VibrationListener.ReceivingEvent> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Registry.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(VibrationListener.ReceivingEvent::gameEvent),
                  Codec.floatRange(0.0F, 3.4028235E38F).fieldOf("distance").forGetter(VibrationListener.ReceivingEvent::distance),
                  Vec3.CODEC.fieldOf("pos").forGetter(VibrationListener.ReceivingEvent::pos),
                  ExtraCodecs.UUID.optionalFieldOf("source").forGetter(var0x -> Optional.ofNullable(var0x.uuid())),
                  ExtraCodecs.UUID.optionalFieldOf("projectile_owner").forGetter(var0x -> Optional.ofNullable(var0x.projectileOwnerUuid()))
               )
               .apply(
                  var0,
                  (var0x, var1, var2, var3, var4) -> new VibrationListener.ReceivingEvent(var0x, var1, var2, (UUID)var3.orElse(null), (UUID)var4.orElse(null))
               )
      );

      public ReceivingEvent(GameEvent var1, float var2, Vec3 var3, @Nullable UUID var4, @Nullable UUID var5) {
         this(var1, var2, var3, var4, var5, null);
      }

      public ReceivingEvent(GameEvent var1, float var2, Vec3 var3, @Nullable Entity var4) {
         this(var1, var2, var3, var4 == null ? null : var4.getUUID(), getProjectileOwner(var4), var4);
      }

      public ReceivingEvent(GameEvent var1, float var2, Vec3 var3, @Nullable UUID var4, @Nullable UUID var5, @Nullable Entity var6) {
         super();
         this.gameEvent = var1;
         this.distance = var2;
         this.pos = var3;
         this.uuid = var4;
         this.projectileOwnerUuid = var5;
         this.entity = var6;
      }

      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Nullable
      private static UUID getProjectileOwner(@Nullable Entity var0) {
         if (var0 instanceof Projectile var1 && var1.getOwner() != null) {
            return var1.getOwner().getUUID();
         }

         return null;
      }

      public Optional<Entity> getEntity(ServerLevel var1) {
         return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(var1::getEntity));
      }

      public Optional<Entity> getProjectileOwner(ServerLevel var1) {
         return this.getEntity(var1)
            .filter(var0 -> var0 instanceof Projectile)
            .map(var0 -> (Projectile)var0)
            .map(Projectile::getOwner)
            .or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(var1::getEntity));
      }
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
