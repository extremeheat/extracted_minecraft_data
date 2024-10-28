package net.minecraft.client.gui.components;

import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;

public class WidgetTooltipHolder {
   @Nullable
   private Tooltip tooltip;
   private Duration delay;
   private long displayStartTime;
   private boolean wasDisplayed;

   public WidgetTooltipHolder() {
      super();
      this.delay = Duration.ZERO;
   }

   public void setDelay(Duration var1) {
      this.delay = var1;
   }

   public void set(@Nullable Tooltip var1) {
      this.tooltip = var1;
   }

   @Nullable
   public Tooltip get() {
      return this.tooltip;
   }

   public void refreshTooltipForNextRenderPass(boolean var1, boolean var2, ScreenRectangle var3) {
      if (this.tooltip == null) {
         this.wasDisplayed = false;
      } else {
         boolean var4 = var1 || var2 && Minecraft.getInstance().getLastInputType().isKeyboard();
         if (var4 != this.wasDisplayed) {
            if (var4) {
               this.displayStartTime = Util.getMillis();
            }

            this.wasDisplayed = var4;
         }

         if (var4 && Util.getMillis() - this.displayStartTime > this.delay.toMillis()) {
            Screen var5 = Minecraft.getInstance().screen;
            if (var5 != null) {
               var5.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(var3, var1, var2), var2);
            }
         }

      }
   }

   private ClientTooltipPositioner createTooltipPositioner(ScreenRectangle var1, boolean var2, boolean var3) {
      return (ClientTooltipPositioner)(!var2 && var3 && Minecraft.getInstance().getLastInputType().isKeyboard() ? new BelowOrAboveWidgetTooltipPositioner(var1) : new MenuTooltipPositioner(var1));
   }

   public void updateNarration(NarrationElementOutput var1) {
      if (this.tooltip != null) {
         this.tooltip.updateNarration(var1);
      }

   }
}
