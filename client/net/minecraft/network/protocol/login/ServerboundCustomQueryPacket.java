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
      this.data = (FriendlyByteBuf)var1.readNullable((var0) -> {
         int var1 = var0.readableBytes();
         if (var1 >= 0 && var1 <= 1048576) {
            return new FriendlyByteBuf(var0.readBytes(var1));
         } else {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
         }
      });
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeNullable(this.data, (var0, var1x) -> {
         var0.writeBytes(var1x.slice());
      });
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
