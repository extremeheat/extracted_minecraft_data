package net.minecraft.client.gui.navigation;

public record ScreenPosition(int x, int y) {
   public ScreenPosition(int x, int y) {
      super();
      this.x = x;
      this.y = y;
   }

   public static ScreenPosition of(ScreenAxis var0, int var1, int var2) {
      return switch (var0) {
         case HORIZONTAL -> new ScreenPosition(var1, var2);
         case VERTICAL -> new ScreenPosition(var2, var1);
         default -> throw new MatchException(null, null);
      };
   }

   public ScreenPosition step(ScreenDirection var1) {
      return switch (var1) {
         case DOWN -> new ScreenPosition(this.x, this.y + 1);
         case UP -> new ScreenPosition(this.x, this.y - 1);
         case LEFT -> new ScreenPosition(this.x - 1, this.y);
         case RIGHT -> new ScreenPosition(this.x + 1, this.y);
         default -> throw new MatchException(null, null);
      };
   }

   public int getCoordinate(ScreenAxis var1) {
      return switch (var1) {
         case HORIZONTAL -> this.x;
         case VERTICAL -> this.y;
         default -> throw new MatchException(null, null);
      };
   }
}
