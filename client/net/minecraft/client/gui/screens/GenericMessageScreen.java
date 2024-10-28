package net.minecraft.client.gui.screens;

import java.util.Objects;
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

   protected void init() {
      this.textWidget = (FocusableTextWidget)this.addRenderableWidget(new FocusableTextWidget(this.width, this.title, this.font, 12));
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.textWidget != null) {
         this.textWidget.containWithin(this.width);
         FocusableTextWidget var10000 = this.textWidget;
         int var10001 = this.width / 2 - this.textWidget.getWidth() / 2;
         int var10002 = this.height / 2;
         Objects.requireNonNull(this.font);
         var10000.setPosition(var10001, var10002 - 9 / 2);
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderPanorama(var1, var4);
      this.renderBlurredBackground(var4);
      this.renderMenuBackground(var1);
   }
}
