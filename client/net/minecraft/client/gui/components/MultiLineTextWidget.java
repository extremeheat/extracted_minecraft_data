package net.minecraft.client.gui.components;

import java.util.OptionalInt;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.SingleKeyCache;

public class MultiLineTextWidget extends AbstractStringWidget {
   private OptionalInt maxWidth = OptionalInt.empty();
   private OptionalInt maxRows = OptionalInt.empty();
   private final SingleKeyCache<MultiLineTextWidget.CacheKey, MultiLineLabel> cache;
   private boolean centered = false;

   public MultiLineTextWidget(Component var1, Font var2) {
      this(0, 0, var1, var2);
   }

   public MultiLineTextWidget(int var1, int var2, Component var3, Font var4) {
      super(var1, var2, 0, 0, var3, var4);
      this.cache = Util.singleKeyCache(
         var1x -> var1x.maxRows.isPresent()
               ? MultiLineLabel.create(var4, var1x.message, var1x.maxWidth, var1x.maxRows.getAsInt())
               : MultiLineLabel.create(var4, var1x.message, var1x.maxWidth)
      );
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

   @Override
   public int getWidth() {
      return this.cache.getValue(this.getFreshCacheKey()).getWidth();
   }

   @Override
   public int getHeight() {
      return this.cache.getValue(this.getFreshCacheKey()).getLineCount() * 9;
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      MultiLineLabel var5 = this.cache.getValue(this.getFreshCacheKey());
      int var6 = this.getX();
      int var7 = this.getY();
      byte var8 = 9;
      int var9 = this.getColor();
      if (this.centered) {
         var5.renderCentered(var1, var6 + this.getWidth() / 2, var7, var8, var9);
      } else {
         var5.renderLeftAligned(var1, var6, var7, var8, var9);
      }
   }

   private MultiLineTextWidget.CacheKey getFreshCacheKey() {
      return new MultiLineTextWidget.CacheKey(this.getMessage(), this.maxWidth.orElse(2147483647), this.maxRows);
   }

   static record CacheKey(Component a, int b, OptionalInt c) {
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
