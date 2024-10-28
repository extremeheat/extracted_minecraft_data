package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import net.minecraft.network.protocol.login.custom.DiscardedQueryAnswerPayload;

public record ServerboundCustomQueryAnswerPacket(int transactionId, @Nullable CustomQueryAnswerPayload payload) implements Packet<ServerLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundCustomQueryAnswerPacket> STREAM_CODEC = Packet.codec(ServerboundCustomQueryAnswerPacket::write, ServerboundCustomQueryAnswerPacket::read);
   private static final int MAX_PAYLOAD_SIZE = 1048576;

   public ServerboundCustomQueryAnswerPacket(int transactionId, @Nullable CustomQueryAnswerPayload payload) {
      super();
      this.transactionId = transactionId;
      this.payload = payload;
   }

   private static ServerboundCustomQueryAnswerPacket read(FriendlyByteBuf var0) {
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

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeNullable(this.payload, (var0, var1x) -> {
         var1x.write(var0);
      });
   }

   public PacketType<ServerboundCustomQueryAnswerPacket> type() {
      return LoginPacketTypes.SERVERBOUND_CUSTOM_QUERY_ANSWER;
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleCustomQueryPacket(this);
   }

   public int transactionId() {
      return this.transactionId;
   }

   @Nullable
   public CustomQueryAnswerPayload payload() {
      return this.payload;
   }
}
