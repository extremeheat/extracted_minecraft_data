package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockUpdatePacket> STREAM_CODEC;
   private final BlockPos pos;
   private final BlockState blockState;

   public ClientboundBlockUpdatePacket(BlockPos var1, BlockState var2) {
      super();
      this.pos = var1;
      this.blockState = var2;
   }

   public ClientboundBlockUpdatePacket(BlockGetter var1, BlockPos var2) {
      this(var2, var1.getBlockState(var2));
   }

   public PacketType<ClientboundBlockUpdatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_BLOCK_UPDATE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockUpdate(this);
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundBlockUpdatePacket::getPos, ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), ClientboundBlockUpdatePacket::getBlockState, ClientboundBlockUpdatePacket::new);
   }
}
