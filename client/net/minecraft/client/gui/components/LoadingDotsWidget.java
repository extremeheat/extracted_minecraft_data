package net.minecraft.client.gui.components;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class LoadingDotsWidget extends AbstractWidget {
   private final Font font;

   public LoadingDotsWidget(Font var1, Component var2) {
      int var10003 = var1.width((FormattedText)var2);
      Objects.requireNonNull(var1);
      super(0, 0, var10003, 9 * 3, var2);
      this.font = var1;
   }

   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.getX() + this.getWidth() / 2;
      int var6 = this.getY() + this.getHeight() / 2;
      Component var7 = this.getMessage();
      Font var10001 = this.font;
      int var10003 = var5 - this.font.width((FormattedText)var7) / 2;
      Objects.requireNonNull(this.font);
      var1.drawString(var10001, (Component)var7, var10003, var6 - 9, -1, false);
      String var8 = LoadingDotsText.get(Util.getMillis());
      var10001 = this.font;
      var10003 = var5 - this.font.width(var8) / 2;
      Objects.requireNonNull(this.font);
      var1.drawString(var10001, var8, var10003, var6 + 9, -8355712, false);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   public void playDownSound(SoundManager var1) {
   }

   public boolean isActive() {
      return false;
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return null;
   }
}
