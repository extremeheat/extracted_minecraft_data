package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class TextCursorUtils {
   public static final int CURSOR_INSERT_WIDTH = 1;
   private static final String CURSOR_APPEND_CHARACTER = "_";
   private static final int CURSOR_BLINK_INTERVAL_MS = 300;

   public TextCursorUtils() {
      super();
   }

   public static void drawInsertCursor(final GuiGraphics graphics, final int x, final int y, final int color, final int lineHeight) {
      graphics.fill(x, y - 1, x + 1, y + lineHeight, color);
   }

   public static void drawAppendCursor(final GuiGraphics graphics, final Font font, final int x, final int y, final int color, final boolean shadow) {
      graphics.drawString(font, "_", x, y, color, shadow);
   }

   public static boolean isCursorVisible(final long timeInMs) {
      return timeInMs / 300L % 2L == 0L;
   }
}
