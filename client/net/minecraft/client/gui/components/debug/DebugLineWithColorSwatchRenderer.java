package net.minecraft.client.gui.components.debug;

import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jspecify.annotations.Nullable;

public record DebugLineWithColorSwatchRenderer(Font font, Component text, @Nullable Integer color) implements DebugCustomRenderer {
   private static final int SWATCH_GAP = 3;

   public DebugLineWithColorSwatchRenderer {
      super();
   }

   public void extract(final GuiGraphicsExtractor graphics, final int left, final int top, final DebugColumn.Side side) {
      Objects.requireNonNull(this.font);
      int swatchSize = 9 - 1;
      graphics.text(this.font, this.text, left, top, -2039584, false);
      if (this.color != null) {
         int x = left + this.font.width((FormattedText)this.text) + 3;
         graphics.fill(x, top, x + swatchSize, top + swatchSize, -16777216);
         graphics.fill(x + 1, top + 1, x + swatchSize - 1, top + swatchSize - 1, this.color);
      }

   }

   public int height() {
      Objects.requireNonNull(this.font);
      return 9;
   }

   public int width(final int groupWidth) {
      int var10001 = this.font.width((FormattedText)this.text);
      int var10002;
      if (this.color == null) {
         var10002 = 0;
      } else {
         Objects.requireNonNull(this.font);
         var10002 = 3 + 9 - 1;
      }

      return Math.max(groupWidth, var10001 + var10002);
   }
}
