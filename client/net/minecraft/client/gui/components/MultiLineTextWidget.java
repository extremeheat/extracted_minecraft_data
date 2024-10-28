package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.OptionalInt;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
      this.cache = Util.singleKeyCache((var1x) -> {
         return var1x.maxRows.isPresent() ? MultiLineLabel.create(var4, var1x.message, var1x.maxWidth, var1x.maxRows.getAsInt()) : MultiLineLabel.create(var4, var1x.message, var1x.maxWidth);
      });
      this.active = false;
   }

   public MultiLineTextWidget setColor(int var1) {
      super.setColor(var1);
      return this;
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

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      MultiLineLabel var5 = (MultiLineLabel)this.cache.getValue(this.getFreshCacheKey());
      int var6 = this.getX();
      int var7 = this.getY();
      Objects.requireNonNull(this.getFont());
      byte var8 = 9;
      int var9 = this.getColor();
      if (this.centered) {
         var5.renderCentered(var1, var6 + this.getWidth() / 2, var7, var8, var9);
      } else {
         var5.renderLeftAligned(var1, var6, var7, var8, var9);
      }

   }

   private CacheKey getFreshCacheKey() {
      return new CacheKey(this.getMessage(), this.maxWidth.orElse(2147483647), this.maxRows);
   }

   // $FF: synthetic method
   public AbstractStringWidget setColor(int var1) {
      return this.setColor(var1);
   }

   private static record CacheKey(Component message, int maxWidth, OptionalInt maxRows) {
      final Component message;
      final int maxWidth;
      final OptionalInt maxRows;

      CacheKey(Component var1, int var2, OptionalInt var3) {
         super();
         this.message = var1;
         this.maxWidth = var2;
         this.maxRows = var3;
      }

      public Component message() {
         return this.message;
      }

      public int maxWidth() {
         return this.maxWidth;
      }

      public OptionalInt maxRows() {
         return this.maxRows;
      }
   }
}
