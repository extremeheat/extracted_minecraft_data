package net.minecraft.util.profiling;

import java.io.File;

public class EmptyProfileResults implements ProfileResults {
   public static final EmptyProfileResults EMPTY = new EmptyProfileResults();

   private EmptyProfileResults() {
      super();
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
