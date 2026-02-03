package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class GenericWaitingScreen extends Screen {
   private static final int TITLE_Y = 80;
   private static final int MESSAGE_Y = 120;
   private static final int MESSAGE_MAX_WIDTH = 360;
   private final @Nullable Component messageText;
   private final Component buttonLabel;
   private final Runnable buttonCallback;
   private @Nullable MultiLineLabel message;
   private Button button;
   private int disableButtonTicks;

   public static GenericWaitingScreen createWaiting(final Component title, final Component buttonLabel, final Runnable buttonCallback) {
      return new GenericWaitingScreen(title, (Component)null, buttonLabel, buttonCallback, 0);
   }

   public static GenericWaitingScreen createCompleted(final Component title, final Component messageText, final Component buttonLabel, final Runnable buttonCallback) {
      return new GenericWaitingScreen(title, messageText, buttonLabel, buttonCallback, 20);
   }

   protected GenericWaitingScreen(final Component title, final @Nullable Component messageText, final Component buttonLabel, final Runnable buttonCallback, final int disableButtonTicks) {
      super(title);
      this.messageText = messageText;
      this.buttonLabel = buttonLabel;
      this.buttonCallback = buttonCallback;
      this.disableButtonTicks = disableButtonTicks;
   }

   protected void init() {
      super.init();
      if (this.messageText != null) {
         this.message = MultiLineLabel.create(this.font, this.messageText, 360);
      }

      int buttonWidth = 150;
      int buttonHeight = 20;
      int lineCount = this.message != null ? this.message.getLineCount() : 1;
      int var10000 = Math.max(lineCount, 5);
      Objects.requireNonNull(this.font);
      int messageButtonSpacing = var10000 * 9;
      int buttonY = Math.min(120 + messageButtonSpacing, this.height - 40);
      this.button = (Button)this.addRenderableWidget(Button.builder(this.buttonLabel, (b) -> this.onClose()).bounds((this.width - 150) / 2, buttonY, 150, 20).build());
   }

   public void tick() {
      if (this.disableButtonTicks > 0) {
         --this.disableButtonTicks;
      }

      this.button.active = this.disableButtonTicks == 0;
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      graphics.drawCenteredString(this.font, (Component)this.title, this.width / 2, 80, -1);
      if (this.message == null) {
         String loadingDots = LoadingDotsText.get(Util.getMillis());
         graphics.drawCenteredString(this.font, (String)loadingDots, this.width / 2, 120, -6250336);
      } else {
         MultiLineLabel var10000 = this.message;
         TextAlignment var10001 = TextAlignment.CENTER;
         int var10002 = this.width / 2;
         Objects.requireNonNull(this.font);
         var10000.visitLines(var10001, var10002, 120, 9, textRenderer);
      }

   }

   public boolean shouldCloseOnEsc() {
      return this.message != null && this.button.active;
   }

   public void onClose() {
      this.buttonCallback.run();
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.messageText != null ? this.messageText : CommonComponents.EMPTY);
   }
}
