package net.minecraft.client.gui.contextualbar;

import com.mojang.blaze3d.platform.Window;
import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public interface ContextualBarRenderer {
   int WIDTH = 182;
   int HEIGHT = 5;
   int MARGIN_BOTTOM = 24;
   ContextualBarRenderer EMPTY = new ContextualBarRenderer() {
      public void renderBackground(GuiGraphics var1, DeltaTracker var2) {
      }

      public void render(GuiGraphics var1, DeltaTracker var2) {
      }
   };

   default int left(Window var1) {
      return (var1.getGuiScaledWidth() - 182) / 2;
   }

   default int top(Window var1) {
      return var1.getGuiScaledHeight() - 24 - 5;
   }

   void renderBackground(GuiGraphics var1, DeltaTracker var2);

   void render(GuiGraphics var1, DeltaTracker var2);

   static void renderExperienceLevel(GuiGraphics var0, Font var1, int var2) {
      MutableComponent var3 = Component.translatable("gui.experience.level", var2);
      int var4 = (var0.guiWidth() - var1.width((FormattedText)var3)) / 2;
      int var10000 = var0.guiHeight() - 24;
      Objects.requireNonNull(var1);
      int var5 = var10000 - 9 - 2;
      var0.drawString(var1, (Component)var3, var4 + 1, var5, -16777216, false);
      var0.drawString(var1, (Component)var3, var4 - 1, var5, -16777216, false);
      var0.drawString(var1, (Component)var3, var4, var5 + 1, -16777216, false);
      var0.drawString(var1, (Component)var3, var4, var5 - 1, -16777216, false);
      var0.drawString(var1, (Component)var3, var4, var5, -8323296, false);
   }
}
