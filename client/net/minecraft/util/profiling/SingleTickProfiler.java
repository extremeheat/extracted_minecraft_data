package net.minecraft.util.profiling;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class SingleTickProfiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LongSupplier realTime;
   private final long saveThreshold;
   private int tick;
   private final File location;
   private ProfileCollector profiler;

   public SingleTickProfiler(LongSupplier var1, String var2, long var3) {
      super();
      this.profiler = InactiveProfiler.INSTANCE;
      this.realTime = var1;
      this.location = new File("debug", var2);
      this.saveThreshold = var3;
   }

   public ProfilerFiller startTick() {
      this.profiler = new ActiveProfiler(this.realTime, () -> {
         return this.tick;
      }, false);
      ++this.tick;
      return this.profiler;
   }

   public void endTick() {
      if (this.profiler != InactiveProfiler.INSTANCE) {
         ProfileResults var1 = this.profiler.getResults();
         this.profiler = InactiveProfiler.INSTANCE;
         if (var1.getNanoDuration() >= this.saveThreshold) {
            File var10002 = this.location;
            SimpleDateFormat var10003 = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            Date var10004 = new Date();
            File var2 = new File(var10002, "tick-results-" + var10003.format(var10004) + ".txt");
            var1.saveResults(var2.toPath());
            LOGGER.info("Recorded long tick -- wrote info to: {}", var2.getAbsolutePath());
         }

      }
   }

   @Nullable
   public static SingleTickProfiler createTickProfiler(String var0) {
      return null;
   }

   public static ProfilerFiller decorateFiller(ProfilerFiller var0, @Nullable SingleTickProfiler var1) {
      return var1 != null ? ProfilerFiller.tee(var1.startTick(), var0) : var0;
   }
}
