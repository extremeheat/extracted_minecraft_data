package net.minecraft.network;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;

public interface PacketSendListener {
   static PacketSendListener thenRun(final Runnable var0) {
      return new PacketSendListener() {
         public void onSuccess() {
            var0.run();
         }

         @Nullable
         public Packet<?> onFailure() {
            var0.run();
            return null;
         }
      };
   }

   static PacketSendListener exceptionallySend(final Supplier<Packet<?>> var0) {
      return new PacketSendListener() {
         @Nullable
         public Packet<?> onFailure() {
            return (Packet)var0.get();
         }
      };
   }

   default void onSuccess() {
   }

   @Nullable
   default Packet<?> onFailure() {
      return null;
   }
}
