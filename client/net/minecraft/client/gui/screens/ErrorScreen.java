package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ErrorScreen extends Screen {
   private final Component messageText;
   private MultiLineLabel message;

   public ErrorScreen(final Component title, final Component messageText) {
      super(title);
      this.messageText = messageText;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.messageText, this.width - 50);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.minecraft.gui.setScreen((Screen)null)).bounds(this.width / 2 - 100, 140, 200, 20).build());
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      graphics.centeredText(this.font, (Component)this.title, this.width / 2, 90, -1);
      MultiLineLabel var10000 = this.message;
      TextAlignment var10001 = TextAlignment.CENTER;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.font);
      var10000.visitLines(var10001, var10002, 110, 9, graphics.textRenderer());
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      graphics.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
