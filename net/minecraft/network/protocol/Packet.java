package net.minecraft.network.protocol;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;

public interface Packet {
   void read(FriendlyByteBuf var1) throws IOException;

   void write(FriendlyByteBuf var1) throws IOException;

   void handle(PacketListener var1);

   default boolean isSkippable() {
      return false;
   }
}
