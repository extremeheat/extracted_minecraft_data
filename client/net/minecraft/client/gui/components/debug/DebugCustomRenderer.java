package net.minecraft.client.gui.components.debug;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface DebugCustomRenderer {
   void extract(final GuiGraphicsExtractor graphics, final int left, final int top, final DebugColumn.Side side);

   int height();

   int width(int groupWidth);
}
