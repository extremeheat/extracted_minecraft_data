package net.minecraft.util.debug;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DebugGoalInfo(List<DebugGoal> goals) {
   public static final StreamCodec<ByteBuf, DebugGoalInfo> STREAM_CODEC;

   public DebugGoalInfo {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(DebugGoalInfo.DebugGoal.STREAM_CODEC.apply(ByteBufCodecs.list()), DebugGoalInfo::goals, DebugGoalInfo::new);
   }

   public static record DebugGoal(int priority, boolean isRunning, String name) {
      public static final StreamCodec<ByteBuf, DebugGoal> STREAM_CODEC;

      public DebugGoal {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, DebugGoal::priority, ByteBufCodecs.BOOL, DebugGoal::isRunning, ByteBufCodecs.stringUtf8(255), DebugGoal::name, DebugGoal::new);
      }
   }
}
