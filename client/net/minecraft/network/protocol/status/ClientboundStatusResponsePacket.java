package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundStatusResponsePacket(ServerStatus a) implements Packet<ClientStatusPacketListener> {
   private final ServerStatus status;

   public ClientboundStatusResponsePacket(FriendlyByteBuf var1) {
      this(var1.readJsonWithCodec(ServerStatus.CODEC));
   }

   public ClientboundStatusResponsePacket(ServerStatus var1) {
      super();
      this.status = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeJsonWithCodec(ServerStatus.CODEC, this.status);
   }

   public void handle(ClientStatusPacketListener var1) {
      var1.handleStatusResponse(this);
   }
}
