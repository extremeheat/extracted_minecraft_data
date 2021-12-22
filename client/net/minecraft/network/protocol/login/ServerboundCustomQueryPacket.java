package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCustomQueryPacket implements Packet<ServerLoginPacketListener> {
   private static final int MAX_PAYLOAD_SIZE = 1048576;
   private final int transactionId;
   @Nullable
   private final FriendlyByteBuf data;

   public ServerboundCustomQueryPacket(int var1, @Nullable FriendlyByteBuf var2) {
      super();
      this.transactionId = var1;
      this.data = var2;
   }

   public ServerboundCustomQueryPacket(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      if (var1.readBoolean()) {
         int var2 = var1.readableBytes();
         if (var2 < 0 || var2 > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
         }

         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         this.data = null;
      }

   }

   public void write(FriendlyByteBuf var1) {
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

   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   public FriendlyByteBuf getData() {
      return this.data;
   }
}
