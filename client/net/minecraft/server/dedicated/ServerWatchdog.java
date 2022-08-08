package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class ServerWatchdog implements Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long MAX_SHUTDOWN_TIME = 10000L;
   private static final int SHUTDOWN_STATUS = 1;
   private final DedicatedServer server;
   private final long maxTickTime;

   public ServerWatchdog(DedicatedServer var1) {
      super();
      this.server = var1;
      this.maxTickTime = var1.getMaxTickLength();
   }

   public void run() {
      while(this.server.isRunning()) {
         long var1 = this.server.getNextTickTime();
         long var3 = Util.getMillis();
         long var5 = var3 - var1;
         if (var5 > this.maxTickTime) {
            LOGGER.error(LogUtils.FATAL_MARKER, "A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float)var5 / 1000.0F), String.format(Locale.ROOT, "%.2f", 0.05F));
            LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
            ThreadMXBean var7 = ManagementFactory.getThreadMXBean();
            ThreadInfo[] var8 = var7.dumpAllThreads(true, true);
            StringBuilder var9 = new StringBuilder();
            Error var10 = new Error("Watchdog");
            ThreadInfo[] var11 = var8;
            int var12 = var8.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               ThreadInfo var14 = var11[var13];
               if (var14.getThreadId() == this.server.getRunningThread().getId()) {
                  var10.setStackTrace(var14.getStackTrace());
               }

               var9.append(var14);
               var9.append("\n");
            }

            CrashReport var16 = new CrashReport("Watching Server", var10);
            this.server.fillSystemReport(var16.getSystemReport());
            CrashReportCategory var17 = var16.addCategory("Thread Dump");
            var17.setDetail("Threads", (Object)var9);
            CrashReportCategory var18 = var16.addCategory("Performance stats");
            var18.setDetail("Random tick rate", () -> {
               return ((GameRules.IntegerValue)this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING)).toString();
            });
            var18.setDetail("Level stats", () -> {
               return (String)Streams.stream(this.server.getAllLevels()).map((var0) -> {
                  ResourceKey var10000 = var0.dimension();
                  return "" + var10000 + ": " + var0.getWatchdogStats();
               }).collect(Collectors.joining(",\n"));
            });
            Bootstrap.realStdoutPrintln("Crash report:\n" + var16.getFriendlyReport());
            File var10002 = new File(this.server.getServerDirectory(), "crash-reports");
            SimpleDateFormat var10003 = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            Date var10004 = new Date();
            File var19 = new File(var10002, "crash-" + var10003.format(var10004) + "-server.txt");
            if (var16.saveToFile(var19)) {
               LOGGER.error("This crash report has been saved to: {}", var19.getAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.exit();
         }

         try {
            Thread.sleep(var1 + this.maxTickTime - var3);
         } catch (InterruptedException var15) {
         }
      }

   }

   private void exit() {
      try {
         Timer var1 = new Timer();
         var1.schedule(new TimerTask() {
            public void run() {
               Runtime.getRuntime().halt(1);
            }
         }, 10000L);
         System.exit(1);
      } catch (Throwable var2) {
         Runtime.getRuntime().halt(1);
      }

   }
}
