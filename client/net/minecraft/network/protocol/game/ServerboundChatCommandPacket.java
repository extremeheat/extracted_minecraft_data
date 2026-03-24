package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandPacket(String command) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChatCommandPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundChatCommandPacket>codec(ServerboundChatCommandPacket::write, ServerboundChatCommandPacket::new);

   private ServerboundChatCommandPacket(final FriendlyByteBuf input) {
      this(input.readUtf());
   }

   public ServerboundChatCommandPacket {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeUtf(this.command);
   }

   public PacketType<ServerboundChatCommandPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT_COMMAND;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChatCommand(this);
   }
}
