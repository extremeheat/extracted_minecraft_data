package net.minecraft.network.protocol.configuration;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundFinishConfigurationPacket() implements Packet<ServerConfigurationPacketListener> {
   public ServerboundFinishConfigurationPacket(FriendlyByteBuf var1) {
      this();
   }

   public ServerboundFinishConfigurationPacket() {
      super();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }

   public void handle(ServerConfigurationPacketListener var1) {
      var1.handleConfigurationFinished(this);
   }

   @Override
   public ConnectionProtocol nextProtocol() {
      return ConnectionProtocol.PLAY;
   }
}
