package net.minecraft.world.level.gameevent.vibrations;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
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
   protected final PositionSource listenerSource;
   protected final int listenerRange;
   protected final VibrationListener.VibrationListenerConfig config;
   protected Optional<GameEvent> receivingEvent = Optional.empty();
   protected int receivingDistance;
   protected int travelTimeInTicks = 0;

   public VibrationListener(PositionSource var1, int var2, VibrationListener.VibrationListenerConfig var3) {
      super();
      this.listenerSource = var1;
      this.listenerRange = var2;
      this.config = var3;
   }

   public void tick(Level var1) {
      if (this.receivingEvent.isPresent()) {
         --this.travelTimeInTicks;
         if (this.travelTimeInTicks <= 0) {
            this.travelTimeInTicks = 0;
            this.config.onSignalReceive(var1, this, (GameEvent)this.receivingEvent.get(), this.receivingDistance);
            this.receivingEvent = Optional.empty();
         }
      }

   }

   public PositionSource getListenerSource() {
      return this.listenerSource;
   }

   public int getListenerRadius() {
      return this.listenerRange;
   }

   public boolean handleGameEvent(Level var1, GameEvent var2, @Nullable Entity var3, BlockPos var4) {
      if (!this.isValidVibration(var2, var3)) {
         return false;
      } else {
         Optional var5 = this.listenerSource.getPosition(var1);
         if (!var5.isPresent()) {
            return false;
         } else {
            BlockPos var6 = (BlockPos)var5.get();
            if (!this.config.shouldListen(var1, this, var4, var2, var3)) {
               return false;
            } else if (this.isOccluded(var1, var4, var6)) {
               return false;
            } else {
               this.sendSignal(var1, var2, var4, var6);
               return true;
            }
         }
      }
   }

   private boolean isValidVibration(GameEvent var1, @Nullable Entity var2) {
      if (this.receivingEvent.isPresent()) {
         return false;
      } else if (!GameEventTags.VIBRATIONS.contains(var1)) {
         return false;
      } else {
         if (var2 != null) {
            if (GameEventTags.IGNORE_VIBRATIONS_SNEAKING.contains(var1) && var2.isSteppingCarefully()) {
               return false;
            }

            if (var2.occludesVibrations()) {
               return false;
            }
         }

         return var2 == null || !var2.isSpectator();
      }
   }

   private void sendSignal(Level var1, GameEvent var2, BlockPos var3, BlockPos var4) {
      this.receivingEvent = Optional.of(var2);
      if (var1 instanceof ServerLevel) {
         this.receivingDistance = Mth.floor(Math.sqrt(var3.distSqr(var4, false)));
         this.travelTimeInTicks = this.receivingDistance;
         ((ServerLevel)var1).sendVibrationParticle(new VibrationPath(var3, this.listenerSource, this.travelTimeInTicks));
      }

   }

   private boolean isOccluded(Level var1, BlockPos var2, BlockPos var3) {
      return var1.isBlockInLine(new ClipBlockStateContext(Vec3.atCenterOf(var2), Vec3.atCenterOf(var3), (var0) -> {
         return var0.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
      })).getType() == HitResult.Type.BLOCK;
   }

   public interface VibrationListenerConfig {
      boolean shouldListen(Level var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5);

      void onSignalReceive(Level var1, GameEventListener var2, GameEvent var3, int var4);
   }
}
