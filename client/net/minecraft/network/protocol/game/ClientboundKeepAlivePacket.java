package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientGamePacketListener> {
   private long id;

   public ClientboundKeepAlivePacket() {
      super();
   }

   public ClientboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   public void handle(ClientGamePacketListener var1) {
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
