package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class Tooltip implements NarrationSupplier {
   private static final int MAX_WIDTH = 170;
   private final Component message;
   @Nullable
   private List<FormattedCharSequence> cachedTooltip;
   @Nullable
   private final Component narration;
   private int msDelay;
   private long hoverOrFocusedStartTime;
   private boolean wasHoveredOrFocused;

   private Tooltip(Component var1, @Nullable Component var2) {
      super();
      this.message = var1;
      this.narration = var2;
   }

   public void setDelay(int var1) {
      this.msDelay = var1;
   }

   public static Tooltip create(Component var0, @Nullable Component var1) {
      return new Tooltip(var0, var1);
   }

   public static Tooltip create(Component var0) {
      return new Tooltip(var0, var0);
   }

   @Override
   public void updateNarration(NarrationElementOutput var1) {
      if (this.narration != null) {
         var1.add(NarratedElementType.HINT, this.narration);
      }
   }

   public List<FormattedCharSequence> toCharSequence(Minecraft var1) {
      if (this.cachedTooltip == null) {
         this.cachedTooltip = splitTooltip(var1, this.message);
      }

      return this.cachedTooltip;
   }

   public static List<FormattedCharSequence> splitTooltip(Minecraft var0, Component var1) {
      return var0.font.split(var1, 170);
   }

   public void refreshTooltipForNextRenderPass(boolean var1, boolean var2, ScreenRectangle var3) {
      boolean var4 = var1 || var2 && Minecraft.getInstance().getLastInputType().isKeyboard();
      if (var4 != this.wasHoveredOrFocused) {
         if (var4) {
            this.hoverOrFocusedStartTime = Util.getMillis();
         }

         this.wasHoveredOrFocused = var4;
      }

      if (var4 && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.msDelay) {
         Screen var5 = Minecraft.getInstance().screen;
         if (var5 != null) {
            var5.setTooltipForNextRenderPass(this, this.createTooltipPositioner(var1, var2, var3), var2);
         }
      }
   }

   protected ClientTooltipPositioner createTooltipPositioner(boolean var1, boolean var2, ScreenRectangle var3) {
      return (ClientTooltipPositioner)(!var1 && var2 && Minecraft.getInstance().getLastInputType().isKeyboard()
         ? new BelowOrAboveWidgetTooltipPositioner(var3)
         : new MenuTooltipPositioner(var3));
   }
}
