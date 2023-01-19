package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginCompressionPacket implements Packet<ClientLoginPacketListener> {
   private final int compressionThreshold;

   public ClientboundLoginCompressionPacket(int var1) {
      super();
      this.compressionThreshold = var1;
   }

   public ClientboundLoginCompressionPacket(FriendlyByteBuf var1) {
      super();
      this.compressionThreshold = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.compressionThreshold);
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleCompression(this);
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
