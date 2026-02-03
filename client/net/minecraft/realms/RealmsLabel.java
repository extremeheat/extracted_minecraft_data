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

   public RealmsLabel(final Component text, final int x, final int y, final int color) {
      super();
      this.text = text;
      this.x = x;
      this.y = y;
      this.color = color;
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      graphics.drawCenteredString(Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
   }

   public Component getText() {
      return this.text;
   }
}
