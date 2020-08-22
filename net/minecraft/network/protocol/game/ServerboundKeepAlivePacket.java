package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundKeepAlivePacket implements Packet {
   private long id;

   public ServerboundKeepAlivePacket() {
   }

   public ServerboundKeepAlivePacket(long var1) {
      this.id = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeLong(this.id);
   }

   public long getId() {
      return this.id;
   }
}
