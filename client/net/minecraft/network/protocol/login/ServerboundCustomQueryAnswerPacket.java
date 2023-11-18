package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import net.minecraft.network.protocol.login.custom.DiscardedQueryAnswerPayload;

public record ServerboundCustomQueryAnswerPacket(int a, @Nullable CustomQueryAnswerPayload b) implements Packet<ServerLoginPacketListener> {
   private final int transactionId;
   @Nullable
   private final CustomQueryAnswerPayload payload;
   private static final int MAX_PAYLOAD_SIZE = 1048576;

   public ServerboundCustomQueryAnswerPacket(int var1, @Nullable CustomQueryAnswerPayload var2) {
      super();
      this.transactionId = var1;
      this.payload = var2;
   }

   public static ServerboundCustomQueryAnswerPacket read(FriendlyByteBuf var0) {
      int var1 = var0.readVarInt();
      return new ServerboundCustomQueryAnswerPacket(var1, readPayload(var1, var0));
   }

   private static CustomQueryAnswerPayload readPayload(int var0, FriendlyByteBuf var1) {
      return readUnknownPayload(var1);
   }

   private static CustomQueryAnswerPayload readUnknownPayload(FriendlyByteBuf var0) {
      int var1 = var0.readableBytes();
      if (var1 >= 0 && var1 <= 1048576) {
         var0.skipBytes(var1);
         return DiscardedQueryAnswerPayload.INSTANCE;
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeNullable(this.payload, (var0, var1x) -> var1x.write(var0));
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleCustomQueryPacket(this);
   }
}
