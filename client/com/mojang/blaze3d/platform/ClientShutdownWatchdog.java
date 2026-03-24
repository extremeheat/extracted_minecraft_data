package com.mojang.blaze3d.platform;

import java.io.File;
import java.time.Duration;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.ServerWatchdog;

public class ClientShutdownWatchdog {
   private static final Duration CRASH_REPORT_PRELOAD_LOAD = Duration.ofSeconds(15L);

   public ClientShutdownWatchdog() {
      super();
   }

   public static void startShutdownWatchdog(final Minecraft minecraft, final File gameDirectory, final long mainThreadId) {
      Thread thread = new Thread(() -> {
         try {
            Thread.sleep(CRASH_REPORT_PRELOAD_LOAD);
         } catch (InterruptedException var5) {
            return;
         }

         CrashReport report = ServerWatchdog.createWatchdogCrashReport("Client shutdown", mainThreadId);
         minecraft.fillReport(report);
         Minecraft.saveReport(gameDirectory, report);
      });
      thread.setDaemon(true);
      thread.setName("Client shutdown watchdog");
      thread.start();
   }
}
