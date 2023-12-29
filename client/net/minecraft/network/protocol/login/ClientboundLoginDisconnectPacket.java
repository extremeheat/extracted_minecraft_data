package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginDisconnectPacket implements Packet<ClientLoginPacketListener> {
   private final Component reason;

   public ClientboundLoginDisconnectPacket(Component var1) {
      super();
      this.reason = var1;
   }

   public ClientboundLoginDisconnectPacket(FriendlyByteBuf var1) {
      super();
      this.reason = Component.Serializer.fromJsonLenient(var1.readUtf(262144));
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(Component.Serializer.toJson(this.reason));
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
