package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiRemovedDebugPayload(BlockPos pos) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, PoiRemovedDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(PoiRemovedDebugPayload::write, PoiRemovedDebugPayload::new);
   public static final CustomPacketPayload.Type<PoiRemovedDebugPayload> TYPE = CustomPacketPayload.<PoiRemovedDebugPayload>createType("debug/poi_removed");

   private PoiRemovedDebugPayload(FriendlyByteBuf var1) {
      this(var1.readBlockPos());
   }

   public PoiRemovedDebugPayload(BlockPos var1) {
      super();
      this.pos = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
   }

   public CustomPacketPayload.Type<PoiRemovedDebugPayload> type() {
      return TYPE;
   }
}
