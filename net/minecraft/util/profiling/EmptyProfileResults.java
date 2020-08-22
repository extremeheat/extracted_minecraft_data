package net.minecraft.util.profiling;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class EmptyProfileResults implements ProfileResults {
   public static final EmptyProfileResults EMPTY = new EmptyProfileResults();

   private EmptyProfileResults() {
   }

   public List getTimes(String var1) {
      return Collections.emptyList();
   }

   public boolean saveResults(File var1) {
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
}
