package net.minecraft.network.protocol.game;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundConfigurationAcknowledgedPacket() implements Packet<ServerGamePacketListener> {
   public ServerboundConfigurationAcknowledgedPacket(FriendlyByteBuf var1) {
      this();
   }

   public ServerboundConfigurationAcknowledgedPacket() {
      super();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleConfigurationAcknowledged(this);
   }

   @Override
   public ConnectionProtocol nextProtocol() {
      return ConnectionProtocol.CONFIGURATION;
   }
}
