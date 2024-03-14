package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiTicketCountDebugPayload(BlockPos c, int d) implements CustomPacketPayload {
   private final BlockPos pos;
   private final int freeTicketCount;
   public static final StreamCodec<FriendlyByteBuf, PoiTicketCountDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      PoiTicketCountDebugPayload::write, PoiTicketCountDebugPayload::new
   );
   public static final CustomPacketPayload.Type<PoiTicketCountDebugPayload> TYPE = CustomPacketPayload.createType("debug/poi_ticket_count");

   private PoiTicketCountDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readInt());
   }

   public PoiTicketCountDebugPayload(BlockPos var1, int var2) {
      super();
      this.pos = var1;
      this.freeTicketCount = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.freeTicketCount);
   }

   @Override
   public CustomPacketPayload.Type<PoiTicketCountDebugPayload> type() {
      return TYPE;
   }
}
