package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LongRunningTask implements ErrorCallback, Runnable {
   public static final Logger LOGGER = LogManager.getLogger();
   protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

   public LongRunningTask() {
      super();
   }

   protected static void pause(int var0) {
      try {
         Thread.sleep((long)(var0 * 1000));
      } catch (InterruptedException var2) {
         LOGGER.error("", var2);
      }

   }

   public static void setScreen(Screen var0) {
      Minecraft var1 = Minecraft.getInstance();
      var1.execute(() -> {
         var1.setScreen(var0);
      });
   }

   public void setScreen(RealmsLongRunningMcoTaskScreen var1) {
      this.longRunningMcoTaskScreen = var1;
   }

   public void error(Component var1) {
      this.longRunningMcoTaskScreen.error(var1);
   }

   public void setTitle(Component var1) {
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
