package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.eventlog.EventLogDirectory;
import org.slf4j.Logger;

public class TelemetryLogManager implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String RAW_EXTENSION = ".json";
   private static final int EXPIRY_DAYS = 7;
   private final EventLogDirectory directory;
   @Nullable
   private CompletableFuture<Optional<TelemetryEventLog>> sessionLog;

   private TelemetryLogManager(EventLogDirectory var1) {
      super();
      this.directory = var1;
   }

   public static CompletableFuture<Optional<TelemetryLogManager>> open(Path var0) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            EventLogDirectory var1 = EventLogDirectory.open(var0, ".json");
            var1.listFiles().prune(LocalDate.now(), 7).compressAll();
            return Optional.of(new TelemetryLogManager(var1));
         } catch (Exception var2) {
            LOGGER.error("Failed to create telemetry log manager", var2);
            return Optional.empty();
         }
      }, Util.backgroundExecutor());
   }

   public CompletableFuture<Optional<TelemetryEventLogger>> openLogger() {
      if (this.sessionLog == null) {
         this.sessionLog = CompletableFuture.supplyAsync(() -> {
            try {
               EventLogDirectory.RawFile var1 = this.directory.createNewFile(LocalDate.now());
               FileChannel var2 = var1.openChannel();
               return Optional.of(new TelemetryEventLog(var2, Util.backgroundExecutor()));
            } catch (IOException var3) {
               LOGGER.error("Failed to open channel for telemetry event log", var3);
               return Optional.empty();
            }
         }, Util.backgroundExecutor());
      }

      return this.sessionLog.thenApply((var0) -> {
         return var0.map(TelemetryEventLog::logger);
      });
   }

   public void close() {
      if (this.sessionLog != null) {
         this.sessionLog.thenAccept((var0) -> {
            var0.ifPresent(TelemetryEventLog::close);
         });
      }

   }
}
