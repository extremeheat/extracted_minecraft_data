package net.minecraft.client.gui.screens.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class WarningScreen extends Screen {
   private final Component content;
   @Nullable
   private final Component check;
   private final Component narration;
   @Nullable
   protected Checkbox stopShowing;
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   protected WarningScreen(Component var1, Component var2, Component var3) {
      this(var1, var2, null, var3);
   }

   protected WarningScreen(Component var1, Component var2, @Nullable Component var3, Component var4) {
      super(var1);
      this.content = var2;
      this.check = var3;
      this.narration = var4;
   }

   protected abstract void initButtons(int var1);

   @Override
   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.content, this.width - 100);
      int var1 = (this.message.getLineCount() + 1) * this.getLineHeight();
      if (this.check != null) {
         int var2 = this.font.width(this.check);
         this.stopShowing = new Checkbox(this.width / 2 - var2 / 2 - 8, 76 + var1, var2 + 24, 20, this.check, false);
         this.addRenderableWidget(this.stopShowing);
      }

      this.initButtons(var1);
   }

   @Override
   public Component getNarrationMessage() {
      return this.narration;
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTitle(var1);
      int var5 = this.width / 2 - this.message.getWidth() / 2;
      this.message.renderLeftAligned(var1, var5, 70, this.getLineHeight(), 16777215);
   }

   protected void renderTitle(GuiGraphics var1) {
      var1.drawString(this.font, this.title, 25, 30, 16777215);
   }

   protected int getLineHeight() {
      return 9 * 2;
   }
}
