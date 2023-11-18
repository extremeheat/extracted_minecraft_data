package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record GoalDebugPayload(int b, BlockPos c, List<GoalDebugPayload.DebugGoal> d) implements CustomPacketPayload {
   private final int entityId;
   private final BlockPos pos;
   private final List<GoalDebugPayload.DebugGoal> goals;
   public static final ResourceLocation ID = new ResourceLocation("debug/goal_selector");

   public GoalDebugPayload(FriendlyByteBuf var1) {
      this(var1.readInt(), var1.readBlockPos(), var1.readList(GoalDebugPayload.DebugGoal::new));
   }

   public GoalDebugPayload(int var1, BlockPos var2, List<GoalDebugPayload.DebugGoal> var3) {
      super();
      this.entityId = var1;
      this.pos = var2;
      this.goals = var3;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.entityId);
      var1.writeBlockPos(this.pos);
      var1.writeCollection(this.goals, (var0, var1x) -> var1x.write(var0));
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static record DebugGoal(int a, boolean b, String c) {
      private final int priority;
      private final boolean isRunning;
      private final String name;

      public DebugGoal(FriendlyByteBuf var1) {
         this(var1.readInt(), var1.readBoolean(), var1.readUtf(255));
      }

      public DebugGoal(int var1, boolean var2, String var3) {
         super();
         this.priority = var1;
         this.isRunning = var2;
         this.name = var3;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeInt(this.priority);
         var1.writeBoolean(this.isRunning);
         var1.writeUtf(this.name);
      }
   }
}
