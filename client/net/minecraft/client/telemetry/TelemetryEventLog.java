package net.minecraft.client.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.minecraft.util.eventlog.JsonEventLog;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TelemetryEventLog implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final JsonEventLog<TelemetryEventInstance> log;
   private final ConsecutiveExecutor consecutiveExecutor;

   public TelemetryEventLog(FileChannel var1, Executor var2) {
      super();
      this.log = new JsonEventLog<TelemetryEventInstance>(TelemetryEventInstance.CODEC, var1);
      this.consecutiveExecutor = new ConsecutiveExecutor(var2, "telemetry-event-log");
   }

   public TelemetryEventLogger logger() {
      return (var1) -> this.consecutiveExecutor.schedule(() -> {
            try {
               this.log.write(var1);
            } catch (IOException var3) {
               LOGGER.error("Failed to write telemetry event to log", var3);
            }

         });
   }

   public void close() {
      this.consecutiveExecutor.schedule(() -> IOUtils.closeQuietly(this.log));
      this.consecutiveExecutor.close();
   }
}
