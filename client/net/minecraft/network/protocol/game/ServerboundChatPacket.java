package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundChatPacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_MESSAGE_LENGTH = 256;
   private final String message;

   public ServerboundChatPacket(String var1) {
      super();
      if (var1.length() > 256) {
         var1 = var1.substring(0, 256);
      }

      this.message = var1;
   }

   public ServerboundChatPacket(FriendlyByteBuf var1) {
      super();
      this.message = var1.readUtf(256);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.message);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChat(this);
   }

   public String getMessage() {
      return this.message;
   }
}
