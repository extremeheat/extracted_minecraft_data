package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundLoginDisconnectPacket implements Packet<ClientLoginPacketListener> {
   private Component reason;

   public ClientboundLoginDisconnectPacket() {
      super();
   }

   public ClientboundLoginDisconnectPacket(Component var1) {
      super();
      this.reason = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.reason = Component.Serializer.fromJsonLenient(var1.readUtf(262144));
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeComponent(this.reason);
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
