package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class BelowOrAboveWidgetTooltipPositioner implements ClientTooltipPositioner {
   private final ScreenRectangle screenRectangle;

   public BelowOrAboveWidgetTooltipPositioner(ScreenRectangle var1) {
      super();
      this.screenRectangle = var1;
   }

   public Vector2ic positionTooltip(int var1, int var2, int var3, int var4, int var5, int var6) {
      Vector2i var7 = new Vector2i();
      var7.x = this.screenRectangle.left() + 3;
      var7.y = this.screenRectangle.bottom() + 3 + 1;
      if (var7.y + var6 + 3 > var2) {
         var7.y = this.screenRectangle.top() - var6 - 3 - 1;
      }

      if (var7.x + var5 > var1) {
         var7.x = Math.max(this.screenRectangle.right() - var5 - 3, 4);
      }

      return var7;
   }
}
