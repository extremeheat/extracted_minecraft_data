package net.minecraft.util.profiling.jfr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.Supplier;

public class SummaryReporter {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Runnable onDeregistration;

   protected SummaryReporter(Runnable var1) {
      super();
      this.onDeregistration = var1;
   }

   public void recordingStopped(@Nullable Path var1) {
      if (var1 != null) {
         this.onDeregistration.run();
         infoWithFallback(() -> {
            return "Dumped flight recorder profiling to " + var1;
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
               return "Dumped recording summary to " + var3;
            });
         } catch (Throwable var4) {
            warnWithFallback(() -> {
               return "Failed to output JFR report";
            }, var4);
         }

      }
   }

   private static void infoWithFallback(Supplier<String> var0) {
      if (log4jIsActive()) {
         LOGGER.info(var0);
      } else {
         Bootstrap.realStdoutPrintln((String)var0.get());
      }

   }

   private static void warnWithFallback(Supplier<String> var0, Throwable var1) {
      if (log4jIsActive()) {
         LOGGER.warn(var0, var1);
      } else {
         Bootstrap.realStdoutPrintln((String)var0.get());
         var1.printStackTrace(Bootstrap.STDOUT);
      }

   }

   private static boolean log4jIsActive() {
      LoggerContext var0 = LogManager.getContext();
      if (var0 instanceof LifeCycle) {
         LifeCycle var1 = (LifeCycle)var0;
         return !var1.isStopped();
      } else {
         return true;
      }
   }
}
