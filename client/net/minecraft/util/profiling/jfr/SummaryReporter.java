package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SummaryReporter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Runnable onDeregistration;

   protected SummaryReporter(final Runnable onDeregistration) {
      super();
      this.onDeregistration = onDeregistration;
   }

   public void recordingStopped(final @Nullable Path result) {
      if (result != null) {
         this.onDeregistration.run();
         infoWithFallback(() -> "Dumped flight recorder profiling to " + String.valueOf(result));

         JfrStatsResult statsResult;
         try {
            statsResult = JfrStatsParser.parse(result);
         } catch (Throwable t) {
            warnWithFallback(() -> "Failed to parse JFR recording", t);
            return;
         }

         try {
            Objects.requireNonNull(statsResult);
            infoWithFallback(statsResult::asJson);
            String var10001 = result.getFileName().toString();
            Path jsonReport = result.resolveSibling("jfr-report-" + StringUtils.substringBefore(var10001, ".jfr") + ".json");
            Files.writeString(jsonReport, statsResult.asJson(), StandardOpenOption.CREATE);
            infoWithFallback(() -> "Dumped recording summary to " + String.valueOf(jsonReport));
         } catch (Throwable t) {
            warnWithFallback(() -> "Failed to output JFR report", t);
         }

      }
   }

   private static void infoWithFallback(final Supplier<String> message) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.info("{}", message.get());
      } else {
         Bootstrap.realStdoutPrintln((String)message.get());
      }

   }

   private static void warnWithFallback(final Supplier<String> message, final Throwable t) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.warn("{}", message.get(), t);
      } else {
         Bootstrap.realStdoutPrintln((String)message.get());
         t.printStackTrace(Bootstrap.STDOUT);
      }

   }
}
