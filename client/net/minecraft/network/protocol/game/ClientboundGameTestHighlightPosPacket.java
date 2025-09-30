package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundGameTestHighlightPosPacket(BlockPos absolutePos, BlockPos relativePos) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundGameTestHighlightPosPacket> STREAM_CODEC;

   public ClientboundGameTestHighlightPosPacket(BlockPos var1, BlockPos var2) {
      super();
      this.absolutePos = var1;
      this.relativePos = var2;
   }

   public PacketType<ClientboundGameTestHighlightPosPacket> type() {
      return GamePacketTypes.CLIENTBOUND_GAME_TEST_HIGHLIGHT_POS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleGameTestHighlightPos(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundGameTestHighlightPosPacket::absolutePos, BlockPos.STREAM_CODEC, ClientboundGameTestHighlightPosPacket::relativePos, ClientboundGameTestHighlightPosPacket::new);
   }
}
