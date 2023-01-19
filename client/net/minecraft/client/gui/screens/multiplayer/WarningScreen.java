package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class WarningScreen extends Screen {
   private final Component titleComponent;
   private final Component content;
   private final Component check;
   private final Component narration;
   @Nullable
   protected Checkbox stopShowing;
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   protected WarningScreen(Component var1, Component var2, Component var3, Component var4) {
      super(NarratorChatListener.NO_TITLE);
      this.titleComponent = var1;
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
      int var2 = this.font.width(this.check);
      this.stopShowing = new Checkbox(this.width / 2 - var2 / 2 - 8, 76 + var1, var2 + 24, 20, this.check, false);
      this.addRenderableWidget(this.stopShowing);
      this.initButtons(var1);
   }

   @Override
   public Component getNarrationMessage() {
      return this.narration;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawString(var1, this.font, this.titleComponent, 25, 30, 16777215);
      int var5 = this.width / 2 - this.message.getWidth() / 2;
      this.message.renderLeftAligned(var1, var5, 70, this.getLineHeight(), 16777215);
      super.render(var1, var2, var3, var4);
   }

   protected int getLineHeight() {
      return 9 * 2;
   }
}
