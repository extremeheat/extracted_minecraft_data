package net.minecraft.util.debug;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum DebugEntityBlockIntersection {
   IN_BLOCK(0, 1610678016),
   IN_FLUID(1, 1610612991),
   IN_AIR(2, 1613968179);

   public static final StreamCodec<ByteBuf, DebugEntityBlockIntersection> STREAM_CODEC = ByteBufCodecs.enumCodec(DebugEntityBlockIntersection.class, (i) -> i.id);
   private final int id;
   private final int color;

   private DebugEntityBlockIntersection(final int id, final int color) {
      this.id = id;
      this.color = color;
   }

   public int color() {
      return this.color;
   }

   // $FF: synthetic method
   private static DebugEntityBlockIntersection[] $values() {
      return new DebugEntityBlockIntersection[]{IN_BLOCK, IN_FLUID, IN_AIR};
   }
}
