package net.minecraft.util.profiling;

import java.nio.file.Path;
import java.util.List;

public interface ProfileResults {
   char PATH_SEPARATOR = '\u001e';

   List<ResultField> getTimes(String var1);

   boolean saveResults(Path var1);

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

   String getProfilerResults();

   static String demanglePath(String var0) {
      return var0.replace('\u001e', '.');
   }
}
