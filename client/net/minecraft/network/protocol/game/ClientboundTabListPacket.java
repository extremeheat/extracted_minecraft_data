package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundTabListPacket implements Packet<ClientGamePacketListener> {
   private final Component header;
   private final Component footer;

   public ClientboundTabListPacket(Component var1, Component var2) {
      super();
      this.header = var1;
      this.footer = var2;
   }

   public ClientboundTabListPacket(FriendlyByteBuf var1) {
      super();
      this.header = var1.readComponent();
      this.footer = var1.readComponent();
   }

   public void write(FriendlyByteBuf var1) {
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
