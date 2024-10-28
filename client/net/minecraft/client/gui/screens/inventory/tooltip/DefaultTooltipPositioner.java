package net.minecraft.client.gui.screens.inventory.tooltip;

import org.joml.Vector2i;
import org.joml.Vector2ic;

public class DefaultTooltipPositioner implements ClientTooltipPositioner {
   public static final ClientTooltipPositioner INSTANCE = new DefaultTooltipPositioner();

   private DefaultTooltipPositioner() {
      super();
   }

   public Vector2ic positionTooltip(int var1, int var2, int var3, int var4, int var5, int var6) {
      Vector2i var7 = (new Vector2i(var3, var4)).add(12, -12);
      this.positionTooltip(var1, var2, var7, var5, var6);
      return var7;
   }

   private void positionTooltip(int var1, int var2, Vector2i var3, int var4, int var5) {
      if (var3.x + var4 > var1) {
         var3.x = Math.max(var3.x - 24 - var4, 4);
      }

      int var6 = var5 + 3;
      if (var3.y + var6 > var2) {
         var3.y = var2 - var6;
      }

   }
}
