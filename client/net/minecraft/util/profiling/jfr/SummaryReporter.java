package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class SummaryReporter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Runnable onDeregistration;

   protected SummaryReporter(Runnable var1) {
      super();
      this.onDeregistration = var1;
   }

   public void recordingStopped(@Nullable Path var1) {
      if (var1 != null) {
         this.onDeregistration.run();
         infoWithFallback(() -> "Dumped flight recorder profiling to " + var1);

         JfrStatsResult var2;
         try {
            var2 = JfrStatsParser.parse(var1);
         } catch (Throwable var5) {
            warnWithFallback(() -> "Failed to parse JFR recording", var5);
            return;
         }

         try {
            infoWithFallback(var2::asJson);
            Path var3 = var1.resolveSibling("jfr-report-" + StringUtils.substringBefore(var1.getFileName().toString(), ".jfr") + ".json");
            Files.writeString(var3, var2.asJson(), StandardOpenOption.CREATE);
            infoWithFallback(() -> "Dumped recording summary to " + var3);
         } catch (Throwable var4) {
            warnWithFallback(() -> "Failed to output JFR report", var4);
         }
      }
   }

   private static void infoWithFallback(Supplier<String> var0) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.info((String)var0.get());
      } else {
         Bootstrap.realStdoutPrintln((String)var0.get());
      }
   }

   private static void warnWithFallback(Supplier<String> var0, Throwable var1) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.warn((String)var0.get(), var1);
      } else {
         Bootstrap.realStdoutPrintln((String)var0.get());
         var1.printStackTrace(Bootstrap.STDOUT);
      }
   }
}
