package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RaidsDebugPayload(List<BlockPos> raidCenters) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, RaidsDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      RaidsDebugPayload::write, RaidsDebugPayload::new
   );
   public static final CustomPacketPayload.Type<RaidsDebugPayload> TYPE = CustomPacketPayload.createType("debug/raids");

   private RaidsDebugPayload(FriendlyByteBuf var1) {
      this(var1.readList(BlockPos.STREAM_CODEC));
   }

   public RaidsDebugPayload(List<BlockPos> raidCenters) {
      super();
      this.raidCenters = raidCenters;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.raidCenters, BlockPos.STREAM_CODEC);
   }

   @Override
   public CustomPacketPayload.Type<RaidsDebugPayload> type() {
      return TYPE;
   }
}
