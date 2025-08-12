package net.minecraft.client.gui.components;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.SingleKeyCache;

public class MultiLineTextWidget extends AbstractStringWidget {
   private OptionalInt maxWidth;
   private OptionalInt maxRows;
   private final SingleKeyCache<CacheKey, MultiLineLabel> cache;
   private boolean centered;
   private boolean allowHoverComponents;
   @Nullable
   private Consumer<Style> componentClickHandler;

   public MultiLineTextWidget(Component var1, Font var2) {
      this(0, 0, var1, var2);
   }

   public MultiLineTextWidget(int var1, int var2, Component var3, Font var4) {
      super(var1, var2, 0, 0, var3, var4);
      this.maxWidth = OptionalInt.empty();
      this.maxRows = OptionalInt.empty();
      this.centered = false;
      this.allowHoverComponents = false;
      this.componentClickHandler = null;
      this.cache = Util.<CacheKey, MultiLineLabel>singleKeyCache((var1x) -> var1x.maxRows.isPresent() ? MultiLineLabel.create(var4, var1x.maxWidth, var1x.maxRows.getAsInt(), var1x.message) : MultiLineLabel.create(var4, var1x.message, var1x.maxWidth));
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

   public MultiLineTextWidget configureStyleHandling(boolean var1, @Nullable Consumer<Style> var2) {
      this.allowHoverComponents = var1;
      this.componentClickHandler = var2;
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
      MultiLineLabel var5 = this.cache.getValue(this.getFreshCacheKey());
      int var6 = this.getX();
      int var7 = this.getY();
      Objects.requireNonNull(this.getFont());
      byte var8 = 9;
      int var9 = this.getColor();
      if (this.centered) {
         int var10 = var6 + this.getWidth() / 2;
         var5.render(var1, MultiLineLabel.Align.CENTER, var10, var7, var8, true, var9);
      } else {
         var5.render(var1, MultiLineLabel.Align.LEFT, var6, var7, var8, true, var9);
      }

      if (this.allowHoverComponents) {
         Style var11 = this.getComponentStyleAt((double)var2, (double)var3);
         if (this.isHovered()) {
            var1.renderComponentHoverEffect(this.getFont(), var11, var2, var3);
         }
      }

   }

   @Nullable
   private Style getComponentStyleAt(double var1, double var3) {
      MultiLineLabel var5 = this.cache.getValue(this.getFreshCacheKey());
      int var6 = this.getX();
      int var7 = this.getY();
      Objects.requireNonNull(this.getFont());
      byte var8 = 9;
      if (this.centered) {
         int var9 = var6 + this.getWidth() / 2;
         return var5.getStyle(MultiLineLabel.Align.CENTER, var9, var7, var8, var1, var3);
      } else {
         return var5.getStyle(MultiLineLabel.Align.LEFT, var6, var7, var8, var1, var3);
      }
   }

   public void onClick(double var1, double var3, boolean var5) {
      if (this.componentClickHandler != null) {
         Style var6 = this.getComponentStyleAt(var1, var3);
         if (var6 != null) {
            this.componentClickHandler.accept(var6);
            return;
         }
      }

      super.onClick(var1, var3, var5);
   }

   private CacheKey getFreshCacheKey() {
      return new CacheKey(this.getMessage(), this.maxWidth.orElse(2147483647), this.maxRows);
   }

   // $FF: synthetic method
   public AbstractStringWidget setColor(final int var1) {
      return this.setColor(var1);
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
