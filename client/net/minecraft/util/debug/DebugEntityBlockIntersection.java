package net.minecraft.util.debug;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum DebugEntityBlockIntersection {
   IN_BLOCK(0, 1610678016),
   IN_FLUID(1, 1610612991),
   IN_AIR(2, 1613968179);

   private static final IntFunction<DebugEntityBlockIntersection> BY_ID = ByIdMap.<DebugEntityBlockIntersection>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, DebugEntityBlockIntersection> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
   private final int id;
   private final int color;

   private DebugEntityBlockIntersection(final int var3, final int var4) {
      this.id = var3;
      this.color = var4;
   }

   public int color() {
      return this.color;
   }

   // $FF: synthetic method
   private static DebugEntityBlockIntersection[] $values() {
      return new DebugEntityBlockIntersection[]{IN_BLOCK, IN_FLUID, IN_AIR};
   }
}
