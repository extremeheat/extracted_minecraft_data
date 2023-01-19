package net.minecraft.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;

public class BundleDelimiterPacket<T extends PacketListener> implements Packet<T> {
   public BundleDelimiterPacket() {
      super();
   }

   @Override
   public final void write(FriendlyByteBuf var1) {
   }

   @Override
   public final void handle(T var1) {
      throw new AssertionError("This packet should be handled by pipeline");
   }
}
