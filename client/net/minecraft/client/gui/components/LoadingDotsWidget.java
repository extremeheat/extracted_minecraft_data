package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class LoadingDotsWidget extends AbstractWidget {
   private final Font font;

   public LoadingDotsWidget(final Font font, final Component message) {
      int var10003 = font.width((FormattedText)message);
      Objects.requireNonNull(font);
      super(0, 0, var10003, 9 * 3, message);
      this.font = font;
   }

   protected void renderWidget(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      int centerX = this.getX() + this.getWidth() / 2;
      int centerY = this.getY() + this.getHeight() / 2;
      Component message = this.getMessage();
      Font var10001 = this.font;
      int var10003 = centerX - this.font.width((FormattedText)message) / 2;
      Objects.requireNonNull(this.font);
      graphics.drawString(var10001, (Component)message, var10003, centerY - 9, -1);
      String dots = LoadingDotsText.get(Util.getMillis());
      var10001 = this.font;
      var10003 = centerX - this.font.width(dots) / 2;
      Objects.requireNonNull(this.font);
      graphics.drawString(var10001, dots, var10003, centerY + 9, -8355712);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   public boolean isActive() {
      return false;
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      return null;
   }
}
