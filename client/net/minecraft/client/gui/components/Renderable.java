package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;

public interface Renderable {
   void render(final GuiGraphics graphics, int mouseX, int mouseY, final float a);
}
