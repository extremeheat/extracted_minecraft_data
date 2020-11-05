package net.minecraft.network.protocol.login;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCustomQueryPacket implements Packet<ServerLoginPacketListener> {
   private int transactionId;
   private FriendlyByteBuf data;

   public ServerboundCustomQueryPacket() {
      super();
   }

   public ServerboundCustomQueryPacket(int var1, @Nullable FriendlyByteBuf var2) {
      super();
      this.transactionId = var1;
      this.data = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.transactionId = var1.readVarInt();
      if (var1.readBoolean()) {
         int var2 = var1.readableBytes();
         if (var2 < 0 || var2 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
         }

         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         this.data = null;
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.transactionId);
      if (this.data != null) {
         var1.writeBoolean(true);
         var1.writeBytes(this.data.copy());
      } else {
         var1.writeBoolean(false);
      }

   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleCustomQueryPacket(this);
   }
}
