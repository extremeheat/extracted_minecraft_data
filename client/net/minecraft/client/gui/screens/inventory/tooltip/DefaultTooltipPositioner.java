package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class DefaultTooltipPositioner implements ClientTooltipPositioner {
   public static final ClientTooltipPositioner INSTANCE = new DefaultTooltipPositioner();

   private DefaultTooltipPositioner() {
      super();
   }

   @Override
   public Vector2ic positionTooltip(Screen var1, int var2, int var3, int var4, int var5) {
      Vector2i var6 = new Vector2i(var2, var3);
      this.positionTooltip(var1, var6, var4, var5);
      return var6;
   }

   private void positionTooltip(Screen var1, Vector2i var2, int var3, int var4) {
      if (var2.x + var3 > var1.width) {
         var2.x = Math.max(var2.x - 24 - var3, 4);
      }

      int var5 = var4 + 3;
      if (var2.y + var5 > var1.height) {
         var2.y = var1.height - var5;
      }
   }
}
