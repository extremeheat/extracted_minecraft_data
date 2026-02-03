package net.minecraft.client.gui.navigation;

public record ScreenPosition(int x, int y) {
   public ScreenPosition {
      super();
   }

   public static ScreenPosition of(final ScreenAxis axis, final int primaryValue, final int secondaryValue) {
      ScreenPosition var10000;
      switch (axis) {
         case HORIZONTAL -> var10000 = new ScreenPosition(primaryValue, secondaryValue);
         case VERTICAL -> var10000 = new ScreenPosition(secondaryValue, primaryValue);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenPosition step(final ScreenDirection direction) {
      ScreenPosition var10000;
      switch (direction) {
         case DOWN -> var10000 = new ScreenPosition(this.x, this.y + 1);
         case UP -> var10000 = new ScreenPosition(this.x, this.y - 1);
         case LEFT -> var10000 = new ScreenPosition(this.x - 1, this.y);
         case RIGHT -> var10000 = new ScreenPosition(this.x + 1, this.y);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getCoordinate(final ScreenAxis axis) {
      int var10000;
      switch (axis) {
         case HORIZONTAL -> var10000 = this.x;
         case VERTICAL -> var10000 = this.y;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
