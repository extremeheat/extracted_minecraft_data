package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginCompressionPacket implements Packet<ClientLoginPacketListener> {
   private int compressionThreshold;

   public ClientboundLoginCompressionPacket() {
      super();
   }

   public ClientboundLoginCompressionPacket(int var1) {
      super();
      this.compressionThreshold = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.compressionThreshold = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.compressionThreshold);
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleCompression(this);
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
