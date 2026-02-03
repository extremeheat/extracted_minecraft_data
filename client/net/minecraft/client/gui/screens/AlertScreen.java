package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class AlertScreen extends Screen {
   private static final int LABEL_Y = 90;
   private final Component messageText;
   private MultiLineLabel message;
   private final Runnable callback;
   private final Component okButton;
   private final boolean shouldCloseOnEsc;

   public AlertScreen(final Runnable callback, final Component title, final Component messageText) {
      this(callback, title, messageText, CommonComponents.GUI_BACK, true);
   }

   public AlertScreen(final Runnable callback, final Component title, final Component messageText, final Component okButton, final boolean shouldCloseOnEsc) {
      super(title);
      this.message = MultiLineLabel.EMPTY;
      this.callback = callback;
      this.messageText = messageText;
      this.okButton = okButton;
      this.shouldCloseOnEsc = shouldCloseOnEsc;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.messageText);
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.messageText, this.width - 50);
      int var10000 = this.message.getLineCount();
      Objects.requireNonNull(this.font);
      int textHeight = var10000 * 9;
      int buttonY = Mth.clamp(90 + textHeight + 12, this.height / 6 + 96, this.height - 24);
      int buttonWidth = 150;
      this.addRenderableWidget(Button.builder(this.okButton, (button) -> this.callback.run()).bounds((this.width - 150) / 2, buttonY, 150, 20).build());
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      graphics.drawCenteredString(this.font, (Component)this.title, this.width / 2, 70, -1);
      MultiLineLabel var10000 = this.message;
      TextAlignment var10001 = TextAlignment.CENTER;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.font);
      var10000.visitLines(var10001, var10002, 90, 9, textRenderer);
   }

   public boolean shouldCloseOnEsc() {
      return this.shouldCloseOnEsc;
   }
}
