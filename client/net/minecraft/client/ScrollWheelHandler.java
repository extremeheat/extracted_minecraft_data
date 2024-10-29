package net.minecraft.client;

import org.joml.Vector2i;

public class ScrollWheelHandler {
   private double accumulatedScrollX;
   private double accumulatedScrollY;

   public ScrollWheelHandler() {
      super();
   }

   public Vector2i onMouseScroll(double var1, double var3) {
      if (this.accumulatedScrollX != 0.0 && Math.signum(var1) != Math.signum(this.accumulatedScrollX)) {
         this.accumulatedScrollX = 0.0;
      }

      if (this.accumulatedScrollY != 0.0 && Math.signum(var3) != Math.signum(this.accumulatedScrollY)) {
         this.accumulatedScrollY = 0.0;
      }

      this.accumulatedScrollX += var1;
      this.accumulatedScrollY += var3;
      int var5 = (int)this.accumulatedScrollX;
      int var6 = (int)this.accumulatedScrollY;
      if (var5 == 0 && var6 == 0) {
         return new Vector2i(0, 0);
      } else {
         this.accumulatedScrollX -= (double)var5;
         this.accumulatedScrollY -= (double)var6;
         return new Vector2i(var5, var6);
      }
   }

   public static int getNextScrollWheelSelection(double var0, int var2, int var3) {
      int var4 = (int)Math.signum(var0);
      var2 -= var4;

      for(var2 = Math.max(-1, var2); var2 < 0; var2 += var3) {
      }

      while(var2 >= var3) {
         var2 -= var3;
      }

      return var2;
   }
}
