package net.minecraft.server.dedicated;

import com.mojang.logging.LogUtils;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TheGame;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class ServerWatchdog implements Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long MAX_SHUTDOWN_TIME = 10000L;
   private static final int SHUTDOWN_STATUS = 1;
   private final MinecraftServer server;
   private final TheGame theGame;
   private final long maxTickTimeNanos;

   public ServerWatchdog(TheGame var1, long var2) {
      super();
      this.theGame = var1;
      this.server = var1.server();
      this.maxTickTimeNanos = var2;
   }

   public void run() {
      while(this.server.isRunning() && this.server.theGame() == this.theGame) {
         long var1 = this.server.getNextTickTime();
         long var3 = Util.getNanos();
         long var5 = var3 - var1;
         if (var5 > this.maxTickTimeNanos) {
            float var7 = this.theGame.tickRateManager().millisecondsPerTick() / (float)TimeUtil.MILLISECONDS_PER_SECOND;
            LOGGER.error(LogUtils.FATAL_MARKER, "A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", (float)var5 / (float)TimeUtil.NANOSECONDS_PER_SECOND), String.format(Locale.ROOT, "%.2f", var7));
            LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
            CrashReport var8 = createWatchdogCrashReport("Watching Server", this.server.getRunningThread().threadId());
            this.server.fillSystemReport(var8.getSystemReport());
            CrashReportCategory var9 = var8.addCategory("Performance stats");
            var9.setDetail("Random tick rate", (CrashReportDetail)(() -> ((GameRules.IntegerValue)this.theGame.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING)).toString()));
            var9.setDetail("Level stats", (CrashReportDetail)(() -> (String)this.theGame.getAllLevels().stream().map((var0) -> {
                  String var10000 = String.valueOf(var0.dimension().location());
                  return var10000 + ": " + var0.getWatchdogStats();
               }).collect(Collectors.joining(",\n"))));
            Bootstrap.realStdoutPrintln("Crash report:\n" + var8.getFriendlyReport(ReportType.CRASH));
            Path var10 = this.server.getServerDirectory().resolve("crash-reports").resolve("crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
            if (var8.saveToFile(var10, ReportType.CRASH)) {
               LOGGER.error("This crash report has been saved to: {}", var10.toAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.exit();
         }

         try {
            Thread.sleep((var1 + this.maxTickTimeNanos - var3) / TimeUtil.NANOSECONDS_PER_MILLISECOND);
         } catch (InterruptedException var11) {
         }
      }

      LOGGER.info("Watchdog shutting down");
   }

   public static CrashReport createWatchdogCrashReport(String var0, long var1) {
      ThreadMXBean var3 = ManagementFactory.getThreadMXBean();
      ThreadInfo[] var4 = var3.dumpAllThreads(true, true);
      StringBuilder var5 = new StringBuilder();
      Error var6 = new Error("Watchdog");

      for(ThreadInfo var10 : var4) {
         if (var10.getThreadId() == var1) {
            var6.setStackTrace(var10.getStackTrace());
         }

         var5.append(var10);
         var5.append("\n");
      }

      CrashReport var11 = new CrashReport(var0, var6);
      CrashReportCategory var12 = var11.addCategory("Thread Dump");
      var12.setDetail("Threads", var5);
      return var11;
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
