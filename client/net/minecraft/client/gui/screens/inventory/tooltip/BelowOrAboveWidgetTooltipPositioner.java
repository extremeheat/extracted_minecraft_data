package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class BelowOrAboveWidgetTooltipPositioner implements ClientTooltipPositioner {
   private final AbstractWidget widget;

   public BelowOrAboveWidgetTooltipPositioner(AbstractWidget var1) {
      super();
      this.widget = var1;
   }

   @Override
   public Vector2ic positionTooltip(Screen var1, int var2, int var3, int var4, int var5) {
      Vector2i var6 = new Vector2i();
      var6.x = this.widget.getX() + 3;
      var6.y = this.widget.getY() + this.widget.getHeight() + 3 + 1;
      if (var6.y + var5 + 3 > var1.height) {
         var6.y = this.widget.getY() - var5 - 3 - 1;
      }

      if (var6.x + var4 > var1.width) {
         var6.x = Math.max(this.widget.getX() + this.widget.getWidth() - var4 - 3, 4);
      }

      return var6;
   }
}
