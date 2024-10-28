package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
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
         infoWithFallback(() -> {
            return "Dumped flight recorder profiling to " + String.valueOf(var1);
         });

         JfrStatsResult var2;
         try {
            var2 = JfrStatsParser.parse(var1);
         } catch (Throwable var5) {
            warnWithFallback(() -> {
               return "Failed to parse JFR recording";
            }, var5);
            return;
         }

         try {
            Objects.requireNonNull(var2);
            infoWithFallback(var2::asJson);
            String var10001 = var1.getFileName().toString();
            Path var3 = var1.resolveSibling("jfr-report-" + StringUtils.substringBefore(var10001, ".jfr") + ".json");
            Files.writeString(var3, var2.asJson(), StandardOpenOption.CREATE);
            infoWithFallback(() -> {
               return "Dumped recording summary to " + String.valueOf(var3);
            });
         } catch (Throwable var4) {
            warnWithFallback(() -> {
               return "Failed to output JFR report";
            }, var4);
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
