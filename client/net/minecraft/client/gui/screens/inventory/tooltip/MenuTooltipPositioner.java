package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class MenuTooltipPositioner implements ClientTooltipPositioner {
   private static final int MARGIN = 5;
   private static final int MOUSE_OFFSET_X = 12;
   public static final int MAX_OVERLAP_WITH_WIDGET = 3;
   public static final int MAX_DISTANCE_TO_WIDGET = 5;
   private final AbstractWidget widget;

   public MenuTooltipPositioner(AbstractWidget var1) {
      super();
      this.widget = var1;
   }

   @Override
   public Vector2ic positionTooltip(Screen var1, int var2, int var3, int var4, int var5) {
      Vector2i var6 = new Vector2i(var2 + 12, var3);
      if (var6.x + var4 > var1.width - 5) {
         var6.x = Math.max(var2 - 12 - var4, 9);
      }

      var6.y += 3;
      int var7 = var5 + 3 + 3;
      int var8 = this.widget.getY() + this.widget.getHeight() + 3 + getOffset(0, 0, this.widget.getHeight());
      int var9 = var1.height - 5;
      if (var8 + var7 <= var9) {
         var6.y += getOffset(var6.y, this.widget.getY(), this.widget.getHeight());
      } else {
         var6.y -= var7 + getOffset(var6.y, this.widget.getY() + this.widget.getHeight(), this.widget.getHeight());
      }

      return var6;
   }

   private static int getOffset(int var0, int var1, int var2) {
      int var3 = Math.min(Math.abs(var0 - var1), var2);
      return Math.round(Mth.lerp((float)var3 / (float)var2, (float)(var2 - 3), 5.0F));
   }
}
