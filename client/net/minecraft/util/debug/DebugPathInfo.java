package net.minecraft.util.debug;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;

public record DebugPathInfo(Path path, float maxNodeDistance, int stuckCountdown) {
   public static final StreamCodec<FriendlyByteBuf, DebugPathInfo> STREAM_CODEC;

   public DebugPathInfo {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Path.DEBUG_STREAM_CODEC, DebugPathInfo::path, ByteBufCodecs.FLOAT, DebugPathInfo::maxNodeDistance, ByteBufCodecs.VAR_INT, DebugPathInfo::stuckCountdown, DebugPathInfo::new);
   }
}
