package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jspecify.annotations.Nullable;

public class GenericMessageScreen extends Screen {
   private @Nullable FocusableTextWidget textWidget;

   public GenericMessageScreen(final Component title) {
      super(title);
   }

   protected void init() {
      this.textWidget = (FocusableTextWidget)this.addRenderableWidget(FocusableTextWidget.builder(this.title, this.font, 12).textWidth(this.font.width((FormattedText)this.title)).build());
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.textWidget != null) {
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

   public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      this.renderPanorama(graphics, a);
      this.renderBlurredBackground(graphics);
      this.renderMenuBackground(graphics);
   }
}
