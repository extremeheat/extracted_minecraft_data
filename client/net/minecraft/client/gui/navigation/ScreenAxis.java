package net.minecraft.client.gui.navigation;

public enum ScreenAxis {
   HORIZONTAL,
   VERTICAL;

   private ScreenAxis() {
   }

   public ScreenAxis orthogonal() {
      ScreenAxis var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = VERTICAL;
         case 1 -> var10000 = HORIZONTAL;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getPositive() {
      ScreenDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = ScreenDirection.RIGHT;
         case 1 -> var10000 = ScreenDirection.DOWN;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getNegative() {
      ScreenDirection var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = ScreenDirection.LEFT;
         case 1 -> var10000 = ScreenDirection.UP;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenDirection getDirection(boolean var1) {
      return var1 ? this.getPositive() : this.getNegative();
   }

   // $FF: synthetic method
   private static ScreenAxis[] $values() {
      return new ScreenAxis[]{HORIZONTAL, VERTICAL};
   }
}
