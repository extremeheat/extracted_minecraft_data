package net.minecraft.scoreboard;

import java.util.Comparator;

final class Score$1 implements Comparator {
   Score$1() {
      super();
   }

   public int compare(Score var1, Score var2) {
      if (var1.func_96652_c() > var2.func_96652_c()) {
         return 1;
      } else {
         return var1.func_96652_c() < var2.func_96652_c() ? -1 : 0;
      }
   }
}
