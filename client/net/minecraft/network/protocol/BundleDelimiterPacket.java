package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;

public abstract class BundleDelimiterPacket<T extends PacketListener> implements Packet<T> {
   public BundleDelimiterPacket() {
      super();
   }

   public final void handle(T var1) {
      throw new AssertionError("This packet should be handled by pipeline");
   }

   public abstract PacketType<? extends BundleDelimiterPacket<T>> type();
}
