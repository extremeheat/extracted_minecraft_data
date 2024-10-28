package net.minecraft.client.gui.screens;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class GenericWaitingScreen extends Screen {
   private static final int TITLE_Y = 80;
   private static final int MESSAGE_Y = 120;
   private static final int MESSAGE_MAX_WIDTH = 360;
   @Nullable
   private final Component messageText;
   private final Component buttonLabel;
   private final Runnable buttonCallback;
   @Nullable
   private MultiLineLabel message;
   private Button button;
   private int disableButtonTicks;

   public static GenericWaitingScreen createWaiting(Component var0, Component var1, Runnable var2) {
      return new GenericWaitingScreen(var0, (Component)null, var1, var2, 0);
   }

   public static GenericWaitingScreen createCompleted(Component var0, Component var1, Component var2, Runnable var3) {
      return new GenericWaitingScreen(var0, var1, var2, var3, 20);
   }

   protected GenericWaitingScreen(Component var1, @Nullable Component var2, Component var3, Runnable var4, int var5) {
      super(var1);
      this.messageText = var2;
      this.buttonLabel = var3;
      this.buttonCallback = var4;
      this.disableButtonTicks = var5;
   }

   protected void init() {
      super.init();
      if (this.messageText != null) {
         this.message = MultiLineLabel.create(this.font, this.messageText, 360);
      }

      boolean var1 = true;
      boolean var2 = true;
      int var3 = this.message != null ? this.message.getLineCount() : 1;
      int var10000 = Math.max(var3, 5);
      Objects.requireNonNull(this.font);
      int var4 = var10000 * 9;
      int var5 = Math.min(120 + var4, this.height - 40);
      this.button = (Button)this.addRenderableWidget(Button.builder(this.buttonLabel, (var1x) -> {
         this.onClose();
      }).bounds((this.width - 150) / 2, var5, 150, 20).build());
   }

   public void tick() {
      if (this.disableButtonTicks > 0) {
         --this.disableButtonTicks;
      }

      this.button.active = this.disableButtonTicks == 0;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 80, 16777215);
      if (this.message == null) {
         String var5 = LoadingDotsText.get(Util.getMillis());
         var1.drawCenteredString(this.font, (String)var5, this.width / 2, 120, 10526880);
      } else {
         this.message.renderCentered(var1, this.width / 2, 120);
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
