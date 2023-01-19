package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundSystemChatPacket(Component a, boolean b) implements Packet<ClientGamePacketListener> {
   private final Component content;
   private final boolean overlay;

   public ClientboundSystemChatPacket(FriendlyByteBuf var1) {
      this(var1.readComponent(), var1.readBoolean());
   }

   public ClientboundSystemChatPacket(Component var1, boolean var2) {
      super();
      this.content = var1;
      this.overlay = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeComponent(this.content);
      var1.writeBoolean(this.overlay);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSystemChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }
}
