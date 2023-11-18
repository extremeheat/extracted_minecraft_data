package net.minecraft.network.protocol.login;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundLoginAcknowledgedPacket() implements Packet<ServerLoginPacketListener> {
   public ServerboundLoginAcknowledgedPacket(FriendlyByteBuf var1) {
      this();
   }

   public ServerboundLoginAcknowledgedPacket() {
      super();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleLoginAcknowledgement(this);
   }

   @Override
   public ConnectionProtocol nextProtocol() {
      return ConnectionProtocol.CONFIGURATION;
   }
}
