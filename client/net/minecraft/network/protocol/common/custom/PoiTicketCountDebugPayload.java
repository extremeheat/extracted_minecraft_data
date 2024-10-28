package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiTicketCountDebugPayload(BlockPos pos, int freeTicketCount) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, PoiTicketCountDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(PoiTicketCountDebugPayload::write, PoiTicketCountDebugPayload::new);
   public static final CustomPacketPayload.Type<PoiTicketCountDebugPayload> TYPE = CustomPacketPayload.createType("debug/poi_ticket_count");

   private PoiTicketCountDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readInt());
   }

   public PoiTicketCountDebugPayload(BlockPos pos, int freeTicketCount) {
      super();
      this.pos = pos;
      this.freeTicketCount = freeTicketCount;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.freeTicketCount);
   }

   public CustomPacketPayload.Type<PoiTicketCountDebugPayload> type() {
      return TYPE;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int freeTicketCount() {
      return this.freeTicketCount;
   }
}
