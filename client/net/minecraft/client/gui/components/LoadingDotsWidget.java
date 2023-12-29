package net.minecraft.client.gui.components;

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

public class LoadingDotsWidget extends AbstractWidget {
   private final Font font;

   public LoadingDotsWidget(Font var1, Component var2) {
      super(0, 0, var1.width(var2), 9 * 3, var2);
      this.font = var1;
   }

   @Override
   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.getX() + this.getWidth() / 2;
      int var6 = this.getY() + this.getHeight() / 2;
      Component var7 = this.getMessage();
      var1.drawString(this.font, var7, var5 - this.font.width(var7) / 2, var6 - 9, -1, false);
      String var8 = LoadingDotsText.get(Util.getMillis());
      var1.drawString(this.font, var8, var5 - this.font.width(var8) / 2, var6 + 9, -8355712, false);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   @Override
   public void playDownSound(SoundManager var1) {
   }

   @Override
   public boolean isActive() {
      return false;
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return null;
   }
}
