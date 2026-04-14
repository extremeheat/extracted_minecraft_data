package com.mojang.blaze3d.platform;

import java.time.Duration;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.dedicated.ServerWatchdog;
import org.jspecify.annotations.Nullable;

public class ClientShutdownWatchdog {
   private static final Duration CRASH_REPORT_PRELOAD_LOAD = Duration.ofSeconds(15L);

   public ClientShutdownWatchdog() {
      super();
   }

   public static void startShutdownWatchdog(final String callsite, final @Nullable Minecraft minecraft, final GameConfig gameConfig, final long mainThreadId) {
      Thread thread = new Thread(() -> {
         try {
            Thread.sleep(CRASH_REPORT_PRELOAD_LOAD);
         } catch (InterruptedException var6) {
            return;
         }

         CrashReport report = ServerWatchdog.createWatchdogCrashReport("Client shutdown from " + callsite, mainThreadId);
         if (minecraft != null) {
            minecraft.fillReport(report);
         } else {
            Minecraft.fillReport((Minecraft)null, (LanguageManager)null, gameConfig.game.launchVersion, (Options)null, report);
         }

         Minecraft.saveReport(gameConfig.location.gameDirectory, report);
      }, "Client shutdown watchdog");
      thread.setDaemon(true);
      thread.start();
   }
}
