package net.minecraft.client.gui.components;

import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
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

   public void refreshTooltipForNextRenderPass(GuiGraphics var1, int var2, int var3, boolean var4, boolean var5, ScreenRectangle var6) {
      if (this.tooltip == null) {
         this.wasDisplayed = false;
      } else {
         Minecraft var7 = Minecraft.getInstance();
         boolean var8 = var4 || var5 && var7.getLastInputType().isKeyboard();
         if (var8 != this.wasDisplayed) {
            if (var8) {
               this.displayStartTime = Util.getMillis();
            }

            this.wasDisplayed = var8;
         }

         if (var8 && Util.getMillis() - this.displayStartTime > this.delay.toMillis()) {
            var1.setTooltipForNextFrame(var7.font, this.tooltip.toCharSequence(var7), this.createTooltipPositioner(var6, var4, var5), var2, var3, var5);
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
