package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;

public abstract class LongRunningTask implements Runnable {
   protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

   public void setScreen(RealmsLongRunningMcoTaskScreen var1) {
      this.longRunningMcoTaskScreen = var1;
   }

   public void error(String var1) {
      this.longRunningMcoTaskScreen.error(var1);
   }

   public void setTitle(String var1) {
      this.longRunningMcoTaskScreen.setTitle(var1);
   }

   public boolean aborted() {
      return this.longRunningMcoTaskScreen.aborted();
   }

   public void tick() {
   }

   public void init() {
   }

   public void abortTask() {
   }
}
