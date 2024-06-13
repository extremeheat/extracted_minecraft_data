package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GoalDebugPayload(int entityId, BlockPos pos, List<GoalDebugPayload.DebugGoal> goals) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, GoalDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(GoalDebugPayload::write, GoalDebugPayload::new);
   public static final CustomPacketPayload.Type<GoalDebugPayload> TYPE = CustomPacketPayload.createType("debug/goal_selector");

   private GoalDebugPayload(FriendlyByteBuf var1) {
      this(var1.readInt(), var1.readBlockPos(), var1.readList(GoalDebugPayload.DebugGoal::new));
   }

   public GoalDebugPayload(int entityId, BlockPos pos, List<GoalDebugPayload.DebugGoal> goals) {
      super();
      this.entityId = entityId;
      this.pos = pos;
      this.goals = goals;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.entityId);
      var1.writeBlockPos(this.pos);
      var1.writeCollection(this.goals, (var0, var1x) -> var1x.write(var0));
   }

   @Override
   public CustomPacketPayload.Type<GoalDebugPayload> type() {
      return TYPE;
   }

   public static record DebugGoal(int priority, boolean isRunning, String name) {
      public DebugGoal(FriendlyByteBuf var1) {
         this(var1.readInt(), var1.readBoolean(), var1.readUtf(255));
      }

      public DebugGoal(int priority, boolean isRunning, String name) {
         super();
         this.priority = priority;
         this.isRunning = isRunning;
         this.name = name;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeInt(this.priority);
         var1.writeBoolean(this.isRunning);
         var1.writeUtf(this.name);
      }
   }
}
