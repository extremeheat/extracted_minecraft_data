package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;

public record ClientboundChatPreviewPacket(int a, @Nullable Component b) implements Packet<ClientGamePacketListener> {
   private final int queryId;
   private final @Nullable Component preview;

   public ClientboundChatPreviewPacket(FriendlyByteBuf var1) {
      this(var1.readInt(), (Component)var1.readNullable(FriendlyByteBuf::readComponent));
   }

   public ClientboundChatPreviewPacket(int var1, @Nullable Component var2) {
      super();
      this.queryId = var1;
      this.preview = var2;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.queryId);
      var1.writeNullable(this.preview, FriendlyByteBuf::writeComponent);
   }

   public boolean isSkippable() {
      return true;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChatPreview(this);
   }

   public int queryId() {
      return this.queryId;
   }

   public @Nullable Component preview() {
      return this.preview;
   }
}
