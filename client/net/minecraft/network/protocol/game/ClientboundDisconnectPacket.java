package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundDisconnectPacket implements Packet<ClientGamePacketListener> {
   private Component reason;

   public ClientboundDisconnectPacket() {
      super();
   }

   public ClientboundDisconnectPacket(Component var1) {
      super();
      this.reason = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.reason = var1.readComponent();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeComponent(this.reason);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
