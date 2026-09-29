package net.minecraft.client.gui.components.debug;

import com.google.common.base.Strings;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;

public record DebugGroupContents(DebugGroup group, List<String> lines, List<Pair<String, Component>> facts, List<DebugCustomRenderer> customRenderers) {
   private static final int MARGIN_RIGHT = 3;
   private static final int MARGIN_LEFT = 3;
   private static final int TITLE_LEFT_PADDING = 3;
   private static final int FACT_NAME_VALUE_PADDING = 5;

   public DebugGroupContents(final DebugGroup group) {
      this(group, new ArrayList(), new ArrayList(), new ArrayList());
   }

   public DebugGroupContents {
      super();
   }

   public void addFact(final String name, final Component value) {
      this.facts.add(Pair.of(name, value));
   }

   public void addCustomRenderer(final DebugCustomRenderer renderer) {
      this.customRenderers.add(renderer);
   }

   public Rect2i extract(final GuiGraphicsExtractor graphics, final int top, final Font font, final DebugColumn.Side side, final int scaledScreenWidth) {
      Stream var10000 = this.lines.stream();
      Objects.requireNonNull(font);
      int fullWidth = var10000.mapToInt(font::width).max().orElse(0);
      int titleWidth = font.width((FormattedText)this.group.title());
      if (titleWidth + 3 > fullWidth) {
         fullWidth = titleWidth + 3;
      }

      int var25 = this.lines.size() + this.facts.size();
      Objects.requireNonNull(font);
      int fullHeight = var25 * 9;
      if (titleWidth > 0) {
         Objects.requireNonNull(font);
         fullHeight += 9;
      }

      int factNameWidth = this.facts.stream().mapToInt((f) -> font.width((String)f.getFirst())).max().orElse(0);

      for(Pair<String, Component> fact : this.facts) {
         int width = factNameWidth + 5 + font.width((FormattedText)fact.getSecond());
         if (width > fullWidth) {
            fullWidth = width;
         }
      }

      for(DebugCustomRenderer customRenderer : this.customRenderers) {
         fullWidth = Math.max(fullWidth, customRenderer.width(fullWidth));
         fullHeight += customRenderer.height();
      }

      int left = side == DebugColumn.Side.LEFT ? 3 : scaledScreenWidth - 3 - fullWidth;
      int y = top;
      if (titleWidth > 0) {
         int var10001 = left - 1;
         int var10002 = top - 1;
         int var10003 = left + fullWidth + 1;
         Objects.requireNonNull(font);
         graphics.fill(var10001, var10002, var10003, top + 9 - 1, -1875890128);
         graphics.text(font, (Component)this.group.title(), left + 3, top, -1, false);
         Objects.requireNonNull(font);
         y = top + 9;
      }

      graphics.fill(left - 1, y - 1, left + fullWidth + 1, top + fullHeight + 1, -1873784752);

      for(Pair<String, Component> fact : this.facts) {
         String name = (String)fact.getFirst() + ":";
         graphics.text(font, name, left + (factNameWidth - font.width((String)fact.getFirst())), y, -2039584, false);
         graphics.text(font, (Component)fact.getSecond(), left + factNameWidth + 5, y, -3092272, false);
         Objects.requireNonNull(font);
         y += 9;
      }

      if (!this.facts.isEmpty() && !this.lines.isEmpty()) {
         y += 2;
      }

      for(String line : this.lines) {
         if (!Strings.isNullOrEmpty(line)) {
            graphics.text(font, line, left, y, -2039584, false);
         }

         Objects.requireNonNull(font);
         y += 9;
      }

      for(DebugCustomRenderer customRenderer : this.customRenderers) {
         customRenderer.extract(graphics, left, y, side);
         y += customRenderer.height();
      }

      if (this.group.accentColor().isPresent()) {
         int accentColor = ARGB.opaque(this.group.accentColor().getAsInt());
         if (side == DebugColumn.Side.LEFT) {
            graphics.fill(0, top - 1, 1, top + fullHeight + 1, accentColor);
         } else {
            graphics.fill(scaledScreenWidth - 1, top - 1, scaledScreenWidth, top + fullHeight + 1, accentColor);
         }
      }

      return new Rect2i(left, top, fullWidth, fullHeight);
   }
}
