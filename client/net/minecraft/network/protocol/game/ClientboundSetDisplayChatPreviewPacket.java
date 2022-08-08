package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundSetDisplayChatPreviewPacket(boolean a) implements Packet<ClientGamePacketListener> {
   private final boolean enabled;

   public ClientboundSetDisplayChatPreviewPacket(FriendlyByteBuf var1) {
      this(var1.readBoolean());
   }

   public ClientboundSetDisplayChatPreviewPacket(boolean var1) {
      super();
      this.enabled = var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.enabled);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetDisplayChatPreview(this);
   }

   public boolean enabled() {
      return this.enabled;
   }
}
