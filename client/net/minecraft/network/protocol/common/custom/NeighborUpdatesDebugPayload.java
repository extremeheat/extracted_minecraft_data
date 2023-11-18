package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record NeighborUpdatesDebugPayload(long b, BlockPos c) implements CustomPacketPayload {
   private final long time;
   private final BlockPos pos;
   public static final ResourceLocation ID = new ResourceLocation("debug/neighbors_update");

   public NeighborUpdatesDebugPayload(FriendlyByteBuf var1) {
      this(var1.readVarLong(), var1.readBlockPos());
   }

   public NeighborUpdatesDebugPayload(long var1, BlockPos var3) {
      super();
      this.time = var1;
      this.pos = var3;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarLong(this.time);
      var1.writeBlockPos(this.pos);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
