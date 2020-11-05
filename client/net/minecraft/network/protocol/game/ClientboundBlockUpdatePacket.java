package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket implements Packet<ClientGamePacketListener> {
   private BlockPos pos;
   private BlockState blockState;

   public ClientboundBlockUpdatePacket() {
      super();
   }

   public ClientboundBlockUpdatePacket(BlockPos var1, BlockState var2) {
      super();
      this.pos = var1;
      this.blockState = var2;
   }

   public ClientboundBlockUpdatePacket(BlockGetter var1, BlockPos var2) {
      this(var2, var1.getBlockState(var2));
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.blockState = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(var1.readVarInt());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
      var1.writeVarInt(Block.getId(this.blockState));
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
}
