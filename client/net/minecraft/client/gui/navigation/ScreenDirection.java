package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;

public enum ScreenDirection {
   UP,
   DOWN,
   LEFT,
   RIGHT;

   private final IntComparator coordinateValueComparator = (k1, k2) -> k1 == k2 ? 0 : (this.isBefore(k1, k2) ? -1 : 1);

   private ScreenDirection() {
   }

   public ScreenAxis getAxis() {
      ScreenAxis var10000;
      switch (this.ordinal()) {
         case 0:
         case 1:
            var10000 = ScreenAxis.VERTICAL;
            break;
         case 2:
         case 3:
            var10000 = ScreenAxis.HORIZONTAL;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getOpposite() {
      ScreenDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = DOWN;
         case 1 -> var10000 = UP;
         case 2 -> var10000 = RIGHT;
         case 3 -> var10000 = LEFT;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isPositive() {
      boolean var10000;
      switch (this.ordinal()) {
         case 0:
         case 2:
            var10000 = false;
            break;
         case 1:
         case 3:
            var10000 = true;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isAfter(final int a, final int b) {
      if (this.isPositive()) {
         return a > b;
      } else {
         return b > a;
      }
   }

   public boolean isBefore(final int a, final int b) {
      if (this.isPositive()) {
         return a < b;
      } else {
         return b < a;
      }
   }

   public IntComparator coordinateValueComparator() {
      return this.coordinateValueComparator;
   }

   // $FF: synthetic method
   private static ScreenDirection[] $values() {
      return new ScreenDirection[]{UP, DOWN, LEFT, RIGHT};
   }
}
