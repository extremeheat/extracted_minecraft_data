package net.minecraft.util.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public record DebugPoiInfo(BlockPos pos, Holder<PoiType> poiType, int freeTicketCount) {
   public static final StreamCodec<RegistryFriendlyByteBuf, DebugPoiInfo> STREAM_CODEC;

   public DebugPoiInfo(PoiRecord var1) {
      this(var1.getPos(), var1.getPoiType(), var1.getFreeTickets());
   }

   public DebugPoiInfo(BlockPos var1, Holder<PoiType> var2, int var3) {
      super();
      this.pos = var1;
      this.poiType = var2;
      this.freeTicketCount = var3;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, DebugPoiInfo::pos, ByteBufCodecs.holderRegistry(Registries.POINT_OF_INTEREST_TYPE), DebugPoiInfo::poiType, ByteBufCodecs.VAR_INT, DebugPoiInfo::freeTicketCount, DebugPoiInfo::new);
   }
}
