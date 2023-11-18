package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundDisconnectPacket implements Packet<ClientCommonPacketListener> {
   private final Component reason;

   public ClientboundDisconnectPacket(Component var1) {
      super();
      this.reason = var1;
   }

   public ClientboundDisconnectPacket(FriendlyByteBuf var1) {
      super();
      this.reason = var1.readComponent();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeComponent(this.reason);
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
