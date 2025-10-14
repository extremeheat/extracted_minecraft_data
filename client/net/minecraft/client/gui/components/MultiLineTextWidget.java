package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.OptionalInt;
import net.minecraft.Util;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.SingleKeyCache;

public class MultiLineTextWidget extends AbstractStringWidget {
   private OptionalInt maxWidth;
   private OptionalInt maxRows;
   private final SingleKeyCache<CacheKey, MultiLineLabel> cache;
   private boolean centered;

   public MultiLineTextWidget(Component var1, Font var2) {
      this(0, 0, var1, var2);
   }

   public MultiLineTextWidget(int var1, int var2, Component var3, Font var4) {
      super(var1, var2, 0, 0, var3, var4);
      this.maxWidth = OptionalInt.empty();
      this.maxRows = OptionalInt.empty();
      this.centered = false;
      this.cache = Util.<CacheKey, MultiLineLabel>singleKeyCache((var1x) -> var1x.maxRows.isPresent() ? MultiLineLabel.create(var4, var1x.maxWidth, var1x.maxRows.getAsInt(), var1x.message) : MultiLineLabel.create(var4, var1x.message, var1x.maxWidth));
      this.active = false;
   }

   public MultiLineTextWidget setMaxWidth(int var1) {
      this.maxWidth = OptionalInt.of(var1);
      return this;
   }

   public MultiLineTextWidget setMaxRows(int var1) {
      this.maxRows = OptionalInt.of(var1);
      return this;
   }

   public MultiLineTextWidget setCentered(boolean var1) {
      this.centered = var1;
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

   public void visitLines(ActiveTextCollector var1) {
      MultiLineLabel var2 = this.cache.getValue(this.getFreshCacheKey());
      int var3 = this.getTextX();
      int var4 = this.getTextY();
      Objects.requireNonNull(this.getFont());
      byte var5 = 9;
      if (this.centered) {
         int var6 = this.getX() + this.getWidth() / 2;
         var2.visitLines(TextAlignment.CENTER, var6, var4, var5, var1);
      } else {
         var2.visitLines(TextAlignment.LEFT, var3, var4, var5, var1);
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

   static record CacheKey(Component message, int maxWidth, OptionalInt maxRows) {
      final Component message;
      final int maxWidth;
      final OptionalInt maxRows;

      CacheKey(Component var1, int var2, OptionalInt var3) {
         super();
         this.message = var1;
         this.maxWidth = var2;
         this.maxRows = var3;
      }
   }
}
