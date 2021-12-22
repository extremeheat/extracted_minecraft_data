package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.TranslatableComponent;

public class DatapackLoadFailureScreen extends Screen {
   private MultiLineLabel message;
   private final Runnable callback;

   public DatapackLoadFailureScreen(Runnable var1) {
      super(new TranslatableComponent("datapackFailure.title"));
      this.message = MultiLineLabel.EMPTY;
      this.callback = var1;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.getTitle(), this.width - 50);
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, new TranslatableComponent("datapackFailure.safeMode"), (var1) -> {
         this.callback.run();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, new TranslatableComponent("gui.toTitle"), (var1) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.message.renderCentered(var1, this.width / 2, 70);
      super.render(var1, var2, var3, var4);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
