package net.minecraft.server;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.TickRateManager;

public class ServerTickRateManager extends TickRateManager {
   private long remainingSprintTicks = 0L;
   private long sprintTickStartTime = 0L;
   private long sprintTimeSpend = 0L;
   private long scheduledCurrentSprintTicks = 0L;
   private boolean previousIsFrozen = false;
   private final MinecraftServer server;

   public ServerTickRateManager(MinecraftServer var1) {
      super();
      this.server = var1;
   }

   public boolean isSprinting() {
      return this.scheduledCurrentSprintTicks > 0L;
   }

   @Override
   public void setFrozen(boolean var1) {
      super.setFrozen(var1);
      this.updateStateToClients();
   }

   private void updateStateToClients() {
      this.server.getPlayerList().broadcastAll(ClientboundTickingStatePacket.from(this));
   }

   private void updateStepTicks() {
      this.server.getPlayerList().broadcastAll(ClientboundTickingStepPacket.from(this));
   }

   public boolean stepGameIfPaused(int var1) {
      if (!this.isFrozen()) {
         return false;
      } else {
         this.frozenTicksToRun = var1;
         this.updateStepTicks();
         return true;
      }
   }

   public boolean stopStepping() {
      if (this.frozenTicksToRun > 0) {
         this.frozenTicksToRun = 0;
         this.updateStepTicks();
         return true;
      } else {
         return false;
      }
   }

   public boolean stopSprinting() {
      if (this.remainingSprintTicks > 0L) {
         this.finishTickSprint();
         return true;
      } else {
         return false;
      }
   }

   public boolean requestGameToSprint(int var1) {
      boolean var2 = this.remainingSprintTicks > 0L;
      this.sprintTimeSpend = 0L;
      this.scheduledCurrentSprintTicks = (long)var1;
      this.remainingSprintTicks = (long)var1;
      this.previousIsFrozen = this.isFrozen();
      this.setFrozen(false);
      return var2;
   }

   private void finishTickSprint() {
      long var1 = this.scheduledCurrentSprintTicks - this.remainingSprintTicks;
      double var3 = Math.max(1.0, (double)this.sprintTimeSpend) / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND;
      int var5 = (int)((double)(TimeUtil.MILLISECONDS_PER_SECOND * var1) / var3);
      String var6 = String.format("%.2f", var1 == 0L ? (double)this.millisecondsPerTick() : var3 / (double)var1);
      this.scheduledCurrentSprintTicks = 0L;
      this.sprintTimeSpend = 0L;
      this.server.createCommandSourceStack().sendSuccess(() -> Component.translatable("commands.tick.sprint.report", var5, var6), true);
      this.remainingSprintTicks = 0L;
      this.setFrozen(this.previousIsFrozen);
      this.server.onTickRateChanged();
   }

   public boolean checkShouldSprintThisTick() {
      if (!this.runGameElements) {
         return false;
      } else if (this.remainingSprintTicks > 0L) {
         this.sprintTickStartTime = System.nanoTime();
         --this.remainingSprintTicks;
         return true;
      } else {
         this.finishTickSprint();
         return false;
      }
   }

   public void endTickWork() {
      this.sprintTimeSpend += System.nanoTime() - this.sprintTickStartTime;
   }

   @Override
   public void setTickRate(float var1) {
      super.setTickRate(var1);
      this.server.onTickRateChanged();
      this.updateStateToClients();
   }

   public void updateJoiningPlayer(ServerPlayer var1) {
      var1.connection.send(ClientboundTickingStatePacket.from(this));
      var1.connection.send(ClientboundTickingStepPacket.from(this));
   }
}
