package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundChatSessionUpdatePacket> STREAM_CODEC;

   public ServerboundChatSessionUpdatePacket {
      super();
   }

   public PacketType<ServerboundChatSessionUpdatePacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT_SESSION_UPDATE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChatSessionUpdate(this);
   }

   static {
      STREAM_CODEC = RemoteChatSession.Data.STREAM_CODEC.map(ServerboundChatSessionUpdatePacket::new, ServerboundChatSessionUpdatePacket::chatSession);
   }
}
