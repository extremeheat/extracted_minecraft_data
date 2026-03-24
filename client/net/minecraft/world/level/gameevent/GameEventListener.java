package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public interface GameEventListener {
   PositionSource getListenerSource();

   int getListenerRadius();

   boolean handleGameEvent(ServerLevel level, Holder<GameEvent> event, GameEvent.Context context, Vec3 sourcePosition);

   default DeliveryMode getDeliveryMode() {
      return GameEventListener.DeliveryMode.UNSPECIFIED;
   }

   public static enum DeliveryMode {
      UNSPECIFIED,
      BY_DISTANCE;

      private DeliveryMode() {
      }

      // $FF: synthetic method
      private static DeliveryMode[] $values() {
         return new DeliveryMode[]{UNSPECIFIED, BY_DISTANCE};
      }
   }

   public interface Provider<T extends GameEventListener> {
      T getListener();
   }
}
