package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DatapackLoadFailureScreen extends Screen {
   private MultiLineLabel message;
   private final Runnable cancelCallback;
   private final Runnable safeModeCallback;

   public DatapackLoadFailureScreen(final Runnable cancelCallback, final Runnable safeModeCallback) {
      super(Component.translatable("datapackFailure.title"));
      this.message = MultiLineLabel.EMPTY;
      this.cancelCallback = cancelCallback;
      this.safeModeCallback = safeModeCallback;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.getTitle(), this.width - 50);
      this.addRenderableWidget(Button.builder(Component.translatable("datapackFailure.safeMode"), (button) -> this.safeModeCallback.run()).bounds(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.cancelCallback.run()).bounds(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      MultiLineLabel var10000 = this.message;
      TextAlignment var10001 = TextAlignment.CENTER;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.font);
      var10000.visitLines(var10001, var10002, 70, 9, textRenderer);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
