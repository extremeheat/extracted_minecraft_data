package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandPacket(String command) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChatCommandPacket> STREAM_CODEC = Packet.codec(
      ServerboundChatCommandPacket::write, ServerboundChatCommandPacket::new
   );

   private ServerboundChatCommandPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(256));
   }

   public ServerboundChatCommandPacket(String command) {
      super();
      this.command = command;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.command, 256);
   }

   @Override
   public PacketType<ServerboundChatCommandPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT_COMMAND;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatCommand(this);
   }
}
