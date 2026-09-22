package net.minecraft.world.item.component;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum MapPostProcessing {
   LOCK(0),
   SCALE(1);

   public static final StreamCodec<ByteBuf, MapPostProcessing> STREAM_CODEC = ByteBufCodecs.enumCodec(MapPostProcessing.class, MapPostProcessing::id);
   private final int id;

   private MapPostProcessing(final int id) {
      this.id = id;
   }

   public int id() {
      return this.id;
   }

   // $FF: synthetic method
   private static MapPostProcessing[] $values() {
      return new MapPostProcessing[]{LOCK, SCALE};
   }
}
