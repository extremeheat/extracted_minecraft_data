package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
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

   public AlertScreen(Runnable var1, Component var2, Component var3) {
      this(var1, var2, var3, CommonComponents.GUI_BACK, true);
   }

   public AlertScreen(Runnable var1, Component var2, Component var3, Component var4, boolean var5) {
      super(var2);
      this.message = MultiLineLabel.EMPTY;
      this.callback = var1;
      this.messageText = var3;
      this.okButton = var4;
      this.shouldCloseOnEsc = var5;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.messageText);
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.messageText, this.width - 50);
      int var10000 = this.message.getLineCount();
      Objects.requireNonNull(this.font);
      int var1 = var10000 * 9;
      int var2 = Mth.clamp(90 + var1 + 12, this.height / 6 + 96, this.height - 24);
      boolean var3 = true;
      this.addRenderableWidget(Button.builder(this.okButton, (var1x) -> {
         this.callback.run();
      }).bounds((this.width - 150) / 2, var2, 150, 20).build());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(var1, this.width / 2, 90);
   }

   public boolean shouldCloseOnEsc() {
      return this.shouldCloseOnEsc;
   }
}
