package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DatapackLoadFailureScreen extends Screen {
   private MultiLineLabel message;
   private final Runnable cancelCallback;
   private final Runnable safeModeCallback;

   public DatapackLoadFailureScreen(Runnable var1, Runnable var2) {
      super(Component.translatable("datapackFailure.title"));
      this.message = MultiLineLabel.EMPTY;
      this.cancelCallback = var1;
      this.safeModeCallback = var2;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.getTitle(), this.width - 50);
      this.addRenderableWidget(Button.builder(Component.translatable("datapackFailure.safeMode"), (var1) -> {
         this.safeModeCallback.run();
      }).bounds(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (var1) -> {
         this.cancelCallback.run();
      }).bounds(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.message.renderCentered(var1, this.width / 2, 70);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
