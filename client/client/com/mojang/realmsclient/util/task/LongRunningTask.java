package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public abstract class LongRunningTask implements Runnable {
   protected static final int NUMBER_OF_RETRIES = 25;
   private static final Logger LOGGER = LogUtils.getLogger();
   private boolean aborted = false;

   public LongRunningTask() {
      super();
   }

   protected static void pause(long var0) {
      try {
         Thread.sleep(var0 * 1000L);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         LOGGER.error("", var3);
      }
   }

   public static void setScreen(Screen var0) {
      Minecraft var1 = Minecraft.getInstance();
      var1.execute(() -> var1.setScreen(var0));
   }

   protected void error(Component var1) {
      this.abortTask();
      Minecraft var2 = Minecraft.getInstance();
      var2.execute(() -> var2.setScreen(new RealmsGenericErrorScreen(var1, new RealmsMainScreen(new TitleScreen()))));
   }

   protected void error(Exception var1) {
      if (var1 instanceof RealmsServiceException var2) {
         this.error(var2.realmsError.errorMessage());
      } else {
         this.error(Component.literal(var1.getMessage()));
      }
   }

   protected void error(RealmsServiceException var1) {
      this.error(var1.realmsError.errorMessage());
   }

   public abstract Component getTitle();

   public boolean aborted() {
      return this.aborted;
   }

   public void tick() {
   }

   public void init() {
   }

   public void abortTask() {
      this.aborted = true;
   }
}
