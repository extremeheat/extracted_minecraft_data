package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundChatPacket implements Packet<ServerGamePacketListener> {
   private String message;

   public ServerboundChatPacket() {
      super();
   }

   public ServerboundChatPacket(String var1) {
      super();
      if (var1.length() > 256) {
         var1 = var1.substring(0, 256);
      }

      this.message = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.message = var1.readUtf(256);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.message);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChat(this);
   }

   public String getMessage() {
      return this.message;
   }
}
