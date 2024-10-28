package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public class RealmsLabel implements Renderable {
   private final Component text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(Component var1, int var2, int var3, int var4) {
      super();
      this.text = var1;
      this.x = var2;
      this.y = var3;
      this.color = var4;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      var1.drawCenteredString(Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
   }

   public Component getText() {
      return this.text;
   }
}
