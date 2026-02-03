package net.minecraft.client.gui.contextualbar;

import com.mojang.blaze3d.platform.Window;
import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public interface ContextualBarRenderer {
   int WIDTH = 182;
   int HEIGHT = 5;
   int MARGIN_BOTTOM = 24;
   ContextualBarRenderer EMPTY = new ContextualBarRenderer() {
      public void renderBackground(final GuiGraphics graphics, final DeltaTracker deltaTracker) {
      }

      public void render(final GuiGraphics graphics, final DeltaTracker deltaTracker) {
      }
   };

   default int left(final Window window) {
      return (window.getGuiScaledWidth() - 182) / 2;
   }

   default int top(final Window window) {
      return window.getGuiScaledHeight() - 24 - 5;
   }

   void renderBackground(GuiGraphics graphics, DeltaTracker deltaTracker);

   void render(final GuiGraphics graphics, final DeltaTracker deltaTracker);

   static void renderExperienceLevel(final GuiGraphics graphics, final Font font, final int experienceLevel) {
      Component str = Component.translatable("gui.experience.level", experienceLevel);
      int x = (graphics.guiWidth() - font.width((FormattedText)str)) / 2;
      int var10000 = graphics.guiHeight() - 24;
      Objects.requireNonNull(font);
      int y = var10000 - 9 - 2;
      graphics.drawString(font, str, x + 1, y, -16777216, false);
      graphics.drawString(font, str, x - 1, y, -16777216, false);
      graphics.drawString(font, str, x, y + 1, -16777216, false);
      graphics.drawString(font, str, x, y - 1, -16777216, false);
      graphics.drawString(font, str, x, y, -8323296, false);
   }
}
