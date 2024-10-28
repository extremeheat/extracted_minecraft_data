package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class MenuTooltipPositioner implements ClientTooltipPositioner {
   private static final int MARGIN = 5;
   private static final int MOUSE_OFFSET_X = 12;
   public static final int MAX_OVERLAP_WITH_WIDGET = 3;
   public static final int MAX_DISTANCE_TO_WIDGET = 5;
   private final ScreenRectangle screenRectangle;

   public MenuTooltipPositioner(ScreenRectangle var1) {
      super();
      this.screenRectangle = var1;
   }

   public Vector2ic positionTooltip(int var1, int var2, int var3, int var4, int var5, int var6) {
      Vector2i var7 = new Vector2i(var3 + 12, var4);
      if (var7.x + var5 > var1 - 5) {
         var7.x = Math.max(var3 - 12 - var5, 9);
      }

      var7.y += 3;
      int var8 = var6 + 3 + 3;
      int var9 = this.screenRectangle.bottom() + 3 + getOffset(0, 0, this.screenRectangle.height());
      int var10 = var2 - 5;
      if (var9 + var8 <= var10) {
         var7.y += getOffset(var7.y, this.screenRectangle.top(), this.screenRectangle.height());
      } else {
         var7.y -= var8 + getOffset(var7.y, this.screenRectangle.bottom(), this.screenRectangle.height());
      }

      return var7;
   }

   private static int getOffset(int var0, int var1, int var2) {
      int var3 = Math.min(Math.abs(var0 - var1), var2);
      return Math.round(Mth.lerp((float)var3 / (float)var2, (float)(var2 - 3), 5.0F));
   }
}
