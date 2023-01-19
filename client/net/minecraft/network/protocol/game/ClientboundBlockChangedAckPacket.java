package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundBlockChangedAckPacket(int a) implements Packet<ClientGamePacketListener> {
   private final int sequence;

   public ClientboundBlockChangedAckPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundBlockChangedAckPacket(int var1) {
      super();
      this.sequence = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.sequence);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockChangedAck(this);
   }
}
