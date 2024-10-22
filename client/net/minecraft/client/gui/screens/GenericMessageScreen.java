package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.network.chat.Component;

public class GenericMessageScreen extends Screen {
   @Nullable
   private FocusableTextWidget textWidget;

   public GenericMessageScreen(Component var1) {
      super(var1);
   }

   @Override
   protected void init() {
      this.textWidget = this.addRenderableWidget(new FocusableTextWidget(this.width, this.title, this.font, 12));
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      if (this.textWidget != null) {
         this.textWidget.containWithin(this.width);
         this.textWidget.setPosition(this.width / 2 - this.textWidget.getWidth() / 2, this.height / 2 - 9 / 2);
      }
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   protected boolean shouldNarrateNavigation() {
      return false;
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderPanorama(var1, var4);
      this.renderBlurredBackground();
      this.renderMenuBackground(var1);
   }
}
