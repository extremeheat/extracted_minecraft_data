package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.OptionalInt;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.SingleKeyCache;
import net.minecraft.util.Util;

public class MultiLineTextWidget extends AbstractStringWidget {
   private OptionalInt maxWidth;
   private OptionalInt maxRows;
   private final SingleKeyCache<CacheKey, MultiLineLabel> cache;
   private boolean centered;

   public MultiLineTextWidget(final Component message, final Font font) {
      this(0, 0, message, font);
   }

   public MultiLineTextWidget(final int x, final int y, final Component message, final Font font) {
      super(x, y, 0, 0, message, font);
      this.maxWidth = OptionalInt.empty();
      this.maxRows = OptionalInt.empty();
      this.centered = false;
      this.cache = Util.<CacheKey, MultiLineLabel>singleKeyCache((key) -> key.maxRows.isPresent() ? MultiLineLabel.create(font, key.maxWidth, key.maxRows.getAsInt(), key.message) : MultiLineLabel.create(font, key.message, key.maxWidth));
      this.active = false;
   }

   public MultiLineTextWidget setMaxWidth(final int maxWidth) {
      this.maxWidth = OptionalInt.of(maxWidth);
      return this;
   }

   public MultiLineTextWidget setMaxRows(final int maxRows) {
      this.maxRows = OptionalInt.of(maxRows);
      return this;
   }

   public MultiLineTextWidget setCentered(final boolean centered) {
      this.centered = centered;
      return this;
   }

   public int getWidth() {
      return ((MultiLineLabel)this.cache.getValue(this.getFreshCacheKey())).getWidth();
   }

   public int getHeight() {
      int var10000 = ((MultiLineLabel)this.cache.getValue(this.getFreshCacheKey())).getLineCount();
      Objects.requireNonNull(this.getFont());
      return var10000 * 9;
   }

   public void visitLines(final ActiveTextCollector output) {
      MultiLineLabel multilineLabel = this.cache.getValue(this.getFreshCacheKey());
      int x = this.getTextX();
      int y = this.getTextY();
      Objects.requireNonNull(this.getFont());
      int lineHeight = 9;
      if (this.centered) {
         int midX = this.getX() + this.getWidth() / 2;
         multilineLabel.visitLines(TextAlignment.CENTER, midX, y, lineHeight, output);
      } else {
         multilineLabel.visitLines(TextAlignment.LEFT, x, y, lineHeight, output);
      }

   }

   protected int getTextX() {
      return this.getX();
   }

   protected int getTextY() {
      return this.getY();
   }

   private CacheKey getFreshCacheKey() {
      return new CacheKey(this.getMessage(), this.maxWidth.orElse(2147483647), this.maxRows);
   }

   private static record CacheKey(Component message, int maxWidth, OptionalInt maxRows) {
      private CacheKey {
         super();
      }
   }
}
