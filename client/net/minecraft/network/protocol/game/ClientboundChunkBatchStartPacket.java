package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundChunkBatchStartPacket() implements Packet<ClientGamePacketListener> {
   public ClientboundChunkBatchStartPacket(FriendlyByteBuf var1) {
      this();
   }

   public ClientboundChunkBatchStartPacket() {
      super();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunkBatchStart(this);
   }
}
