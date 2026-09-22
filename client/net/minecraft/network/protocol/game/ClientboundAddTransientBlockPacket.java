package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundAddTransientBlockPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddTransientBlockPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundAddTransientBlockPacket>codec(ClientboundAddTransientBlockPacket::write, ClientboundAddTransientBlockPacket::new);
   private final BlockPos pos;
   private final BlockState blockState;

   public ClientboundAddTransientBlockPacket(final BlockPos pos, final BlockState blockState) {
      super();
      this.pos = pos;
      this.blockState = blockState;
   }

   public ClientboundAddTransientBlockPacket(final RegistryFriendlyByteBuf input) {
      super();
      this.pos = input.readBlockPos();
      this.blockState = (BlockState)Block.BLOCK_STATE_REGISTRY_STREAM_CODEC.decode(input);
   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeBlockPos(this.pos);
      Block.BLOCK_STATE_REGISTRY_STREAM_CODEC.encode(output, this.blockState);
   }

   public PacketType<ClientboundAddTransientBlockPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ADD_TRANSIENT_BLOCK;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleAddTransientBlockPacket(this);
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
