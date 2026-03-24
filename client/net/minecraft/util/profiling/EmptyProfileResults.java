package net.minecraft.util.profiling;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class EmptyProfileResults implements ProfileResults {
   public static final EmptyProfileResults EMPTY = new EmptyProfileResults();

   private EmptyProfileResults() {
      super();
   }

   public List<ResultField> getTimes(final String path) {
      return Collections.emptyList();
   }

   public boolean saveResults(final Path file) {
      return false;
   }

   public long getStartTimeNano() {
      return 0L;
   }

   public int getStartTimeTicks() {
      return 0;
   }

   public long getEndTimeNano() {
      return 0L;
   }

   public int getEndTimeTicks() {
      return 0;
   }

   public String getProfilerResults() {
      return "";
   }
}
