package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.entity.SignTextSlot;

public record ServerboundSignUpdatePacket(BlockPos pos, List<String> lines, SignTextSlot slot) implements Packet<ServerGamePacketListener> {
   private static final int MAX_STRING_LENGTH = 384;
   public static final StreamCodec<ByteBuf, ServerboundSignUpdatePacket> STREAM_CODEC;

   public ServerboundSignUpdatePacket {
      super();
   }

   public PacketType<ServerboundSignUpdatePacket> type() {
      return GamePacketTypes.SERVERBOUND_SIGN_UPDATE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSignUpdate(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ServerboundSignUpdatePacket::pos, ByteBufCodecs.stringUtf8(384).apply(ByteBufCodecs.fixedSizeList(4)), ServerboundSignUpdatePacket::lines, SignTextSlot.STREAM_CODEC, ServerboundSignUpdatePacket::slot, ServerboundSignUpdatePacket::new);
   }
}
