package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record PoiTicketCountDebugPayload(BlockPos b, int c) implements CustomPacketPayload {
   private final BlockPos pos;
   private final int freeTicketCount;
   public static final ResourceLocation ID = new ResourceLocation("debug/poi_ticket_count");

   public PoiTicketCountDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readInt());
   }

   public PoiTicketCountDebugPayload(BlockPos var1, int var2) {
      super();
      this.pos = var1;
      this.freeTicketCount = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.freeTicketCount);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
