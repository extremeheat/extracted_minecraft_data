package net.minecraft.util.profiling;

import java.util.function.Supplier;
import javax.annotation.Nullable;

public class Zone implements AutoCloseable {
   public static final Zone INACTIVE = new Zone((ProfilerFiller)null);
   @Nullable
   private final ProfilerFiller profiler;

   Zone(@Nullable ProfilerFiller var1) {
      super();
      this.profiler = var1;
   }

   public Zone addText(String var1) {
      if (this.profiler != null) {
         this.profiler.addZoneText(var1);
      }

      return this;
   }

   public Zone addText(Supplier<String> var1) {
      if (this.profiler != null) {
         this.profiler.addZoneText((String)var1.get());
      }

      return this;
   }

   public Zone addValue(long var1) {
      if (this.profiler != null) {
         this.profiler.addZoneValue(var1);
      }

      return this;
   }

   public Zone setColor(int var1) {
      if (this.profiler != null) {
         this.profiler.setZoneColor(var1);
      }

      return this;
   }

   public void close() {
      if (this.profiler != null) {
         this.profiler.pop();
      }

   }
}
