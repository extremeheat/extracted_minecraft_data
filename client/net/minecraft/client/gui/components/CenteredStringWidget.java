package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CenteredStringWidget extends AbstractWidget {
   private int color = 16777215;
   private final Font font;

   public CenteredStringWidget(Component var1, Font var2) {
      this(0, 0, var2.width(var1.getVisualOrderText()), 9, var1, var2);
   }

   public CenteredStringWidget(int var1, int var2, Component var3, Font var4) {
      this(0, 0, var1, var2, var3, var4);
   }

   public CenteredStringWidget(int var1, int var2, int var3, int var4, Component var5, Font var6) {
      super(var1, var2, var3, var4, var5);
      this.font = var6;
      this.active = false;
   }

   public CenteredStringWidget color(int var1) {
      this.color = var1;
      return this;
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
   }

   @Override
   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      drawCenteredString(var1, this.font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 9) / 2, this.color);
   }
}
