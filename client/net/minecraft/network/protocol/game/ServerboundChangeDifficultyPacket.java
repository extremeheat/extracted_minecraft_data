package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.Difficulty;

public record ServerboundChangeDifficultyPacket(Difficulty difficulty) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundChangeDifficultyPacket> STREAM_CODEC;

   public ServerboundChangeDifficultyPacket(Difficulty var1) {
      super();
      this.difficulty = var1;
   }

   public PacketType<ServerboundChangeDifficultyPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHANGE_DIFFICULTY;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChangeDifficulty(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Difficulty.STREAM_CODEC, ServerboundChangeDifficultyPacket::difficulty, ServerboundChangeDifficultyPacket::new);
   }
}
