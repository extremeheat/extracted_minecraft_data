package net.minecraft.util.debug;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DebugBeeInfo(Optional<BlockPos> hivePos, Optional<BlockPos> flowerPos, int travelTicks, List<BlockPos> blacklistedHives) {
   public static final StreamCodec<ByteBuf, DebugBeeInfo> STREAM_CODEC;

   public DebugBeeInfo(Optional<BlockPos> var1, Optional<BlockPos> var2, int var3, List<BlockPos> var4) {
      super();
      this.hivePos = var1;
      this.flowerPos = var2;
      this.travelTicks = var3;
      this.blacklistedHives = var4;
   }

   public boolean hasHive(BlockPos var1) {
      return this.hivePos.isPresent() && var1.equals(this.hivePos.get());
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional), DebugBeeInfo::hivePos, BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional), DebugBeeInfo::flowerPos, ByteBufCodecs.VAR_INT, DebugBeeInfo::travelTicks, BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), DebugBeeInfo::blacklistedHives, DebugBeeInfo::new);
   }
}
