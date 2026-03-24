package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
   private final boolean showLoadingDots;
   private final Component messageText;
   private final Component buttonLabel;
   private final Runnable buttonCallback;
   private final boolean showButton;
   private final boolean closeOnEscape;
   private final MultiLineLabel message;
   private @Nullable Button button;
   private int disableButtonTicks;

   public static GenericWaitingScreen createWaitingWithoutButton(final Component title, final Component messageText) {
      return new GenericWaitingScreen(title, true, messageText, Component.empty(), () -> {
      }, 0, false, false);
   }

   public static GenericWaitingScreen createWaiting(final Component title, final Component buttonLabel, final Runnable buttonCallback) {
      return new GenericWaitingScreen(title, true, Component.empty(), buttonLabel, buttonCallback, 0, true, false);
   }

   public static GenericWaitingScreen createCompleted(final Component title, final Component messageText, final Component buttonLabel, final Runnable buttonCallback) {
      return new GenericWaitingScreen(title, false, messageText, buttonLabel, buttonCallback, 20, true, true);
   }

   protected GenericWaitingScreen(final Component title, final boolean showLoadingDots, final Component messageText, final Component buttonLabel, final Runnable buttonCallback, final int disableButtonTicks, final boolean showButton, final boolean closeOnEscape) {
      super(title);
      this.showLoadingDots = showLoadingDots;
      this.messageText = messageText;
      this.buttonLabel = buttonLabel;
      this.buttonCallback = buttonCallback;
      this.disableButtonTicks = disableButtonTicks;
      this.showButton = showButton;
      this.closeOnEscape = closeOnEscape;
      this.message = MultiLineLabel.create(this.font, messageText, 360);
   }

   protected void init() {
      super.init();
      int buttonWidth = 150;
      int buttonHeight = 20;
      int lineCount = this.message.getLineCount() + 1;
      int var10000 = Math.max(lineCount, 5);
      Objects.requireNonNull(this.font);
      int messageButtonSpacing = var10000 * 9;
      int buttonY = Math.min(120 + messageButtonSpacing, this.height - 40);
      if (this.showButton) {
         this.button = (Button)this.addRenderableWidget(Button.builder(this.buttonLabel, (b) -> this.onClose()).bounds((this.width - 150) / 2, buttonY, 150, 20).build());
      }

   }

   public void tick() {
      if (this.disableButtonTicks > 0) {
         --this.disableButtonTicks;
      }

      if (this.button != null) {
         this.button.active = this.disableButtonTicks == 0;
      }

   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      graphics.centeredText(this.font, (Component)this.title, this.width / 2, 80, -1);
      int messageY = 120;
      if (this.showLoadingDots) {
         String loadingDots = LoadingDotsText.get(Util.getMillis());
         graphics.centeredText(this.font, loadingDots, this.width / 2, messageY, -6250336);
         Objects.requireNonNull(this.font);
         messageY += 9 + 3;
      }

      MultiLineLabel var10000 = this.message;
      TextAlignment var10001 = TextAlignment.CENTER;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.font);
      var10000.visitLines(var10001, var10002, messageY, 9, textRenderer);
   }

   public boolean shouldCloseOnEsc() {
      return this.closeOnEscape && this.button != null && this.button.active;
   }

   public void onClose() {
      this.buttonCallback.run();
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.title, this.messageText);
   }
}
