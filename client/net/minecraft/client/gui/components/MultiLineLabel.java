package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public interface MultiLineLabel {
   MultiLineLabel EMPTY = new MultiLineLabel() {
      public void renderCentered(GuiGraphics var1, int var2, int var3) {
      }

      public void renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      }

      public void renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      }

      public int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      @Nullable
      public Style getStyleAtCentered(int var1, int var2, int var3, double var4, double var6) {
         return null;
      }

      @Nullable
      public Style getStyleAtLeftAligned(int var1, int var2, int var3, double var4, double var6) {
         return null;
      }

      public int getLineCount() {
         return 0;
      }

      public int getWidth() {
         return 0;
      }
   };

   static MultiLineLabel create(Font var0, Component... var1) {
      return create(var0, 2147483647, 2147483647, var1);
   }

   static MultiLineLabel create(Font var0, int var1, Component... var2) {
      return create(var0, var1, 2147483647, var2);
   }

   static MultiLineLabel create(Font var0, Component var1, int var2) {
      return create(var0, var2, 2147483647, var1);
   }

   static MultiLineLabel create(final Font var0, final int var1, final int var2, final Component... var3) {
      return var3.length == 0 ? EMPTY : new MultiLineLabel() {
         @Nullable
         private List<TextAndWidth> cachedTextAndWidth;
         @Nullable
         private Language splitWithLanguage;

         public void renderCentered(GuiGraphics var1x, int var2x, int var3x) {
            Objects.requireNonNull(var0);
            this.renderCentered(var1x, var2x, var3x, 9, -1);
         }

         public void renderCentered(GuiGraphics var1x, int var2x, int var3x, int var4, int var5) {
            int var6 = var3x;

            for(TextAndWidth var8 : this.getSplitMessage()) {
               var1x.drawString(var0, var8.text, var2x - var8.width / 2, var6, var5);
               var6 += var4;
            }

         }

         public void renderLeftAligned(GuiGraphics var1x, int var2x, int var3x, int var4, int var5) {
            int var6 = var3x;

            for(TextAndWidth var8 : this.getSplitMessage()) {
               var1x.drawString(var0, var8.text, var2x, var6, var5);
               var6 += var4;
            }

         }

         public int renderLeftAlignedNoShadow(GuiGraphics var1x, int var2x, int var3x, int var4, int var5) {
            int var6 = var3x;

            for(TextAndWidth var8 : this.getSplitMessage()) {
               var1x.drawString(var0, var8.text, var2x, var6, var5, false);
               var6 += var4;
            }

            return var6;
         }

         @Nullable
         public Style getStyleAtCentered(int var1x, int var2x, int var3x, double var4, double var6) {
            List var8 = this.getSplitMessage();
            int var9 = Mth.floor((var6 - (double)var2x) / (double)var3x);
            if (var9 >= 0 && var9 < var8.size()) {
               TextAndWidth var10 = (TextAndWidth)var8.get(var9);
               int var11 = var1x - var10.width / 2;
               if (var4 < (double)var11) {
                  return null;
               } else {
                  int var12 = Mth.floor(var4 - (double)var11);
                  return var0.getSplitter().componentStyleAtWidth(var10.text, var12);
               }
            } else {
               return null;
            }
         }

         @Nullable
         public Style getStyleAtLeftAligned(int var1x, int var2x, int var3x, double var4, double var6) {
            if (var4 < (double)var1x) {
               return null;
            } else {
               List var8 = this.getSplitMessage();
               int var9 = Mth.floor((var6 - (double)var2x) / (double)var3x);
               if (var9 >= 0 && var9 < var8.size()) {
                  TextAndWidth var10 = (TextAndWidth)var8.get(var9);
                  int var11 = Mth.floor(var4 - (double)var1x);
                  return var0.getSplitter().componentStyleAtWidth(var10.text, var11);
               } else {
                  return null;
               }
            }
         }

         private List<TextAndWidth> getSplitMessage() {
            Language var1x = Language.getInstance();
            if (this.cachedTextAndWidth != null && var1x == this.splitWithLanguage) {
               return this.cachedTextAndWidth;
            } else {
               this.splitWithLanguage = var1x;
               ArrayList var2x = new ArrayList();

               for(Component var6 : var3) {
                  var2x.addAll(var0.splitIgnoringLanguage(var6, var1));
               }

               this.cachedTextAndWidth = new ArrayList();
               int var10 = Math.min(var2x.size(), var2);
               List var11 = var2x.subList(0, var10);

               for(int var12 = 0; var12 < var11.size(); ++var12) {
                  FormattedText var13 = (FormattedText)var11.get(var12);
                  FormattedCharSequence var7 = Language.getInstance().getVisualOrder(var13);
                  if (var12 == var11.size() - 1 && var10 == var2 && var10 != var2x.size()) {
                     FormattedText var8 = var0.substrByWidth(var13, var0.width(var13) - var0.width((FormattedText)CommonComponents.ELLIPSIS));
                     FormattedText var9 = FormattedText.composite(var8, CommonComponents.ELLIPSIS);
                     this.cachedTextAndWidth.add(new TextAndWidth(Language.getInstance().getVisualOrder(var9), var0.width(var9)));
                  } else {
                     this.cachedTextAndWidth.add(new TextAndWidth(var7, var0.width(var7)));
                  }
               }

               return this.cachedTextAndWidth;
            }
         }

         public int getLineCount() {
            return this.getSplitMessage().size();
         }

         public int getWidth() {
            return Math.min(var1, this.getSplitMessage().stream().mapToInt(TextAndWidth::width).max().orElse(0));
         }
      };
   }

   void renderCentered(GuiGraphics var1, int var2, int var3);

   void renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5);

   void renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5);

   int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5);

   @Nullable
   Style getStyleAtCentered(int var1, int var2, int var3, double var4, double var6);

   @Nullable
   Style getStyleAtLeftAligned(int var1, int var2, int var3, double var4, double var6);

   int getLineCount();

   int getWidth();

   public static record TextAndWidth(FormattedCharSequence text, int width) {
      final FormattedCharSequence text;
      final int width;

      public TextAndWidth(FormattedCharSequence var1, int var2) {
         super();
         this.text = var1;
         this.width = var2;
      }
   }
}
