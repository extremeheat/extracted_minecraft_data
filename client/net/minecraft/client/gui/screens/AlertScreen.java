package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AlertScreen extends Screen {
   private final Runnable callback;
   protected final Component text;
   private MultiLineLabel message;
   protected final Component okButton;

   public AlertScreen(Runnable var1, Component var2, Component var3) {
      this(var1, var2, var3, CommonComponents.GUI_BACK);
   }

   public AlertScreen(Runnable var1, Component var2, Component var3, Component var4) {
      super(var2);
      this.message = MultiLineLabel.EMPTY;
      this.callback = var1;
      this.text = var3;
      this.okButton = var4;
   }

   protected void init() {
      super.init();
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, (var1) -> {
         this.callback.run();
      }));
      this.message = MultiLineLabel.create(this.font, this.text, this.width - 50);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(var1, this.width / 2, 90);
      super.render(var1, var2, var3, var4);
   }
}
