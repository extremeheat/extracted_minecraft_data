package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChatSessionUpdatePacket> STREAM_CODEC = Packet.codec(ServerboundChatSessionUpdatePacket::write, ServerboundChatSessionUpdatePacket::new);

   private ServerboundChatSessionUpdatePacket(FriendlyByteBuf var1) {
      this(RemoteChatSession.Data.read(var1));
   }

   public ServerboundChatSessionUpdatePacket(RemoteChatSession.Data var1) {
      super();
      this.chatSession = var1;
   }

   private void write(FriendlyByteBuf var1) {
      RemoteChatSession.Data.write(var1, this.chatSession);
   }

   public PacketType<ServerboundChatSessionUpdatePacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT_SESSION_UPDATE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatSessionUpdate(this);
   }

   public RemoteChatSession.Data chatSession() {
      return this.chatSession;
   }
}
