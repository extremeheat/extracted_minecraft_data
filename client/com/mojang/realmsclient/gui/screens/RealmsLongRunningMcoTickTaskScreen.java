package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.gui.screens.Screen;

public class RealmsLongRunningMcoTickTaskScreen extends RealmsLongRunningMcoTaskScreen {
   private final LongRunningTask task;

   public RealmsLongRunningMcoTickTaskScreen(Screen var1, LongRunningTask var2) {
      super(var1, var2);
      this.task = var2;
   }

   public void tick() {
      super.tick();
      this.task.tick();
   }

   protected void cancel() {
      this.task.abortTask();
      super.cancel();
   }
}
