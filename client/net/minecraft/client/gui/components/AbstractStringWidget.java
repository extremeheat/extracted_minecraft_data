package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class AbstractStringWidget extends AbstractWidget {
   private final Font font;
   private int color = 16777215;

   public AbstractStringWidget(int var1, int var2, int var3, int var4, Component var5, Font var6) {
      super(var1, var2, var3, var4, var5);
      this.font = var6;
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   public AbstractStringWidget setColor(int var1) {
      this.color = var1;
      return this;
   }

   protected final Font getFont() {
      return this.font;
   }

   protected final int getColor() {
      return this.color;
   }
}
