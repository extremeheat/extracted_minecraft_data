package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;

public enum ScreenDirection {
   UP,
   DOWN,
   LEFT,
   RIGHT;

   private final IntComparator coordinateValueComparator = (var1x, var2x) -> var1x == var2x ? 0 : (this.isBefore(var1x, var2x) ? -1 : 1);

   private ScreenDirection() {
   }

   public ScreenAxis getAxis() {
      return switch(this) {
         case UP, DOWN -> ScreenAxis.VERTICAL;
         case LEFT, RIGHT -> ScreenAxis.HORIZONTAL;
      };
   }

   public ScreenDirection getOpposite() {
      return switch(this) {
         case UP -> DOWN;
         case DOWN -> UP;
         case LEFT -> RIGHT;
         case RIGHT -> LEFT;
      };
   }

   public boolean isPositive() {
      return switch(this) {
         case UP, LEFT -> false;
         case DOWN, RIGHT -> true;
      };
   }

   public boolean isAfter(int var1, int var2) {
      if (this.isPositive()) {
         return var1 > var2;
      } else {
         return var2 > var1;
      }
   }

   public boolean isBefore(int var1, int var2) {
      if (this.isPositive()) {
         return var1 < var2;
      } else {
         return var2 < var1;
      }
   }

   public IntComparator coordinateValueComparator() {
      return this.coordinateValueComparator;
   }
}
