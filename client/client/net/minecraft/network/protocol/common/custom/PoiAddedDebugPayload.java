package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiAddedDebugPayload(BlockPos pos, String poiType, int freeTicketCount) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, PoiAddedDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      PoiAddedDebugPayload::write, PoiAddedDebugPayload::new
   );
   public static final CustomPacketPayload.Type<PoiAddedDebugPayload> TYPE = CustomPacketPayload.createType("debug/poi_added");

   private PoiAddedDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos(), var1.readUtf(), var1.readInt());
   }

   public PoiAddedDebugPayload(BlockPos pos, String poiType, int freeTicketCount) {
      super();
      this.pos = pos;
      this.poiType = poiType;
      this.freeTicketCount = freeTicketCount;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeUtf(this.poiType);
      var1.writeInt(this.freeTicketCount);
   }

   @Override
   public CustomPacketPayload.Type<PoiAddedDebugPayload> type() {
      return TYPE;
   }
}