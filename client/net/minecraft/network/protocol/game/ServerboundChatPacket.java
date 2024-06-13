package net.minecraft.network.protocol.game;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages)
   implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChatPacket> STREAM_CODEC = Packet.codec(ServerboundChatPacket::write, ServerboundChatPacket::new);

   private ServerboundChatPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(256), var1.readInstant(), var1.readLong(), var1.readNullable(MessageSignature::read), new LastSeenMessages.Update(var1));
   }

   public ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages) {
      super();
      this.message = message;
      this.timeStamp = timeStamp;
      this.salt = salt;
      this.signature = signature;
      this.lastSeenMessages = lastSeenMessages;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.message, 256);
      var1.writeInstant(this.timeStamp);
      var1.writeLong(this.salt);
      var1.writeNullable(this.signature, MessageSignature::write);
      this.lastSeenMessages.write(var1);
   }

   @Override
   public PacketType<ServerboundChatPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChat(this);
   }
}
