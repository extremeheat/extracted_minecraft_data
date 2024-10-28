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

   public static void startShutdownWatchdog(File var0, long var1) {
      Thread var3 = new Thread(() -> {
         try {
            Thread.sleep(CRASH_REPORT_PRELOAD_LOAD);
         } catch (InterruptedException var4) {
            return;
         }

         CrashReport var3 = ServerWatchdog.createWatchdogCrashReport("Client shutdown", var1);
         Minecraft.saveReport(var0, var3);
      });
      var3.setDaemon(true);
      var3.setName("Client shutdown watchdog");
      var3.start();
   }
}
