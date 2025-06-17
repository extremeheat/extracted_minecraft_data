package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.GameType;

public record ServerboundChangeGameModePacket(GameType mode) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundChangeGameModePacket> STREAM_CODEC;

   public ServerboundChangeGameModePacket(GameType var1) {
      super();
      this.mode = var1;
   }

   public PacketType<ServerboundChangeGameModePacket> type() {
      return GamePacketTypes.SERVERBOUND_CHANGE_GAME_MODE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChangeGameMode(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(GameType.STREAM_CODEC, ServerboundChangeGameModePacket::mode, ServerboundChangeGameModePacket::new);
   }
}
