package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.StringUtil;

public record ServerboundChatPreviewPacket(int a, String b) implements Packet<ServerGamePacketListener> {
   private final int queryId;
   private final String query;

   public ServerboundChatPreviewPacket(int var1, String var2) {
      super();
      var2 = StringUtil.trimChatMessage(var2);
      this.queryId = var1;
      this.query = var2;
   }

   public ServerboundChatPreviewPacket(FriendlyByteBuf var1) {
      this(var1.readInt(), var1.readUtf(256));
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.queryId);
      var1.writeUtf(this.query, 256);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatPreview(this);
   }

   public int queryId() {
      return this.queryId;
   }

   public String query() {
      return this.query;
   }
}
