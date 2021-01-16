package net.minecraft.util.profiling;

import java.io.File;

public interface ProfileResults {
   boolean saveResults(File var1);

   long getStartTimeNano();

   int getStartTimeTicks();

   long getEndTimeNano();

   int getEndTimeTicks();

   default long getNanoDuration() {
      return this.getEndTimeNano() - this.getStartTimeNano();
   }

   default int getTickDuration() {
      return this.getEndTimeTicks() - this.getStartTimeTicks();
   }

   static String demanglePath(String var0) {
      return var0.replace('\u001e', '.');
   }
}
