package net.minecraft.client.gui.components;

import java.util.Objects;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class LoadingDotsWidget extends AbstractWidget {
   private static final int Y_PADDING = 2;
   private final Font font;

   public LoadingDotsWidget(final Font font, final Component message) {
      int var10003 = font.width((FormattedText)message);
      Objects.requireNonNull(font);
      int var10004 = 2 + 9 + 6;
      Objects.requireNonNull(font);
      super(0, 0, var10003, var10004 + 9 + 2, message);
      this.font = font;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      int centerX = this.getX() + this.getWidth() / 2;
      Component message = this.getMessage();
      graphics.text(this.font, (Component)message, centerX - this.font.width((FormattedText)message) / 2, this.getY() + 2, -1);
      String dots = LoadingDotsText.get(Util.getMillis());
      Font var10001 = this.font;
      int var10003 = centerX - this.font.width(dots) / 2;
      int var10004 = this.getBottom();
      Objects.requireNonNull(this.font);
      graphics.text(var10001, dots, var10003, var10004 - 9 - 2, -8355712);
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
