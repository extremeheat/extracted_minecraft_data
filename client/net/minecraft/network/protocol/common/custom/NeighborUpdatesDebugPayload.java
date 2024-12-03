package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NeighborUpdatesDebugPayload(long time, BlockPos pos) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, NeighborUpdatesDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(NeighborUpdatesDebugPayload::write, NeighborUpdatesDebugPayload::new);
   public static final CustomPacketPayload.Type<NeighborUpdatesDebugPayload> TYPE = CustomPacketPayload.<NeighborUpdatesDebugPayload>createType("debug/neighbors_update");

   private NeighborUpdatesDebugPayload(FriendlyByteBuf var1) {
      this(var1.readVarLong(), var1.readBlockPos());
   }

   public NeighborUpdatesDebugPayload(long var1, BlockPos var3) {
      super();
      this.time = var1;
      this.pos = var3;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarLong(this.time);
      var1.writeBlockPos(this.pos);
   }

   public CustomPacketPayload.Type<NeighborUpdatesDebugPayload> type() {
      return TYPE;
   }
}
