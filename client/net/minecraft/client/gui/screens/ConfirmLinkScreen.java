package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ConfirmLinkScreen extends ConfirmScreen {
   private static final Component COPY_BUTTON_TEXT = new TranslatableComponent("chat.copy");
   private static final Component WARNING_TEXT = new TranslatableComponent("chat.link.warning");
   private final String url;
   private final boolean showWarning;

   public ConfirmLinkScreen(BooleanConsumer var1, String var2, boolean var3) {
      super(var1, new TranslatableComponent(var3 ? "chat.link.confirmTrusted" : "chat.link.confirm"), new TextComponent(var2));
      this.yesButton = (Component)(var3 ? new TranslatableComponent("chat.link.open") : CommonComponents.GUI_YES);
      this.noButton = var3 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO;
      this.showWarning = !var3;
      this.url = var2;
   }

   protected void addButtons(int var1) {
      this.addRenderableWidget(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesButton, (var1x) -> {
         this.callback.accept(true);
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, COPY_BUTTON_TEXT, (var1x) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noButton, (var1x) -> {
         this.callback.accept(false);
      }));
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.showWarning) {
         drawCenteredString(var1, this.font, WARNING_TEXT, this.width / 2, 110, 16764108);
      }

   }
}
