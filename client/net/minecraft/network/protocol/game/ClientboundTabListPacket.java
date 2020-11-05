package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundTabListPacket implements Packet<ClientGamePacketListener> {
   private Component header;
   private Component footer;

   public ClientboundTabListPacket() {
      super();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.header = var1.readComponent();
      this.footer = var1.readComponent();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeComponent(this.header);
      var1.writeComponent(this.footer);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTabListCustomisation(this);
   }

   public Component getHeader() {
      return this.header;
   }

   public Component getFooter() {
      return this.footer;
   }
}
