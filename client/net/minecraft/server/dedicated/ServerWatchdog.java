package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
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
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class ServerWatchdog implements Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long MAX_SHUTDOWN_TIME = 10000L;
   private static final int SHUTDOWN_STATUS = 1;
   private final DedicatedServer server;
   private final long maxTickTimeNanos;

   public ServerWatchdog(DedicatedServer var1) {
      super();
      this.server = var1;
      this.maxTickTimeNanos = var1.getMaxTickLength() * TimeUtil.NANOSECONDS_PER_MILLISECOND;
   }

   @Override
   public void run() {
      while (this.server.isRunning()) {
         long var1 = this.server.getNextTickTime();
         long var3 = Util.getNanos();
         long var5 = var3 - var1;
         if (var5 > this.maxTickTimeNanos) {
            LOGGER.error(
               LogUtils.FATAL_MARKER,
               "A single server tick took {} seconds (should be max {})",
               String.format(Locale.ROOT, "%.2f", (float)var5 / (float)TimeUtil.NANOSECONDS_PER_SECOND),
               String.format(Locale.ROOT, "%.2f", this.server.tickRateManager().millisecondsPerTick() / (float)TimeUtil.MILLISECONDS_PER_SECOND)
            );
            LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
            CrashReport var7 = createWatchdogCrashReport("Watching Server", this.server.getRunningThread().threadId());
            this.server.fillSystemReport(var7.getSystemReport());
            CrashReportCategory var8 = var7.addCategory("Performance stats");
            var8.setDetail("Random tick rate", () -> this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).toString());
            var8.setDetail(
               "Level stats",
               () -> Streams.stream(this.server.getAllLevels())
                     .map(var0 -> var0.dimension() + ": " + var0.getWatchdogStats())
                     .collect(Collectors.joining(",\n"))
            );
            Bootstrap.realStdoutPrintln("Crash report:\n" + var7.getFriendlyReport(ReportType.CRASH));
            Path var9 = this.server.getServerDirectory().resolve("crash-reports").resolve("crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
            if (var7.saveToFile(var9, ReportType.CRASH)) {
               LOGGER.error("This crash report has been saved to: {}", var9.toAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.exit();
         }

         try {
            Thread.sleep((var1 + this.maxTickTimeNanos - var3) / TimeUtil.NANOSECONDS_PER_MILLISECOND);
         } catch (InterruptedException var10) {
         }
      }
   }

   public static CrashReport createWatchdogCrashReport(String var0, long var1) {
      ThreadMXBean var3 = ManagementFactory.getThreadMXBean();
      ThreadInfo[] var4 = var3.dumpAllThreads(true, true);
      StringBuilder var5 = new StringBuilder();
      Error var6 = new Error("Watchdog");

      for (ThreadInfo var10 : var4) {
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
            @Override
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
