package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

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
               var1x.drawCenteredString(var0, var8.text, var2x, var6, var5);
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

         private List<TextAndWidth> getSplitMessage() {
            Language var1x = Language.getInstance();
            if (this.cachedTextAndWidth != null && var1x == this.splitWithLanguage) {
               return this.cachedTextAndWidth;
            } else {
               this.splitWithLanguage = var1x;
               ArrayList var2x = new ArrayList();

               for(Component var6 : var3) {
                  var2x.addAll(var0.split(var6, var1));
               }

               this.cachedTextAndWidth = new ArrayList();

               for(FormattedCharSequence var8 : var2x.subList(0, Math.min(var2x.size(), var2))) {
                  this.cachedTextAndWidth.add(new TextAndWidth(var8, var0.width(var8)));
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

   int getLineCount();

   int getWidth();

   public static record TextAndWidth(FormattedCharSequence text, int width) {
      final FormattedCharSequence text;

      public TextAndWidth(FormattedCharSequence var1, int var2) {
         super();
         this.text = var1;
         this.width = var2;
      }
   }
}
