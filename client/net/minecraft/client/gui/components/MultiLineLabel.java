package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public interface MultiLineLabel {
   MultiLineLabel EMPTY = new MultiLineLabel() {
      public int renderCentered(GuiGraphics var1, int var2, int var3) {
         return var3;
      }

      public int renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      public int renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      public int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      public void renderBackgroundCentered(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6) {
      }

      public int getLineCount() {
         return 0;
      }

      public int getWidth() {
         return 0;
      }
   };

   static MultiLineLabel create(Font var0, FormattedText var1, int var2) {
      return createFixed(var0, (List)var0.split(var1, var2).stream().map((var1x) -> {
         return new TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel create(Font var0, FormattedText var1, int var2, int var3) {
      return createFixed(var0, (List)var0.split(var1, var2).stream().limit((long)var3).map((var1x) -> {
         return new TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel create(Font var0, Component... var1) {
      return createFixed(var0, (List)Arrays.stream(var1).map(Component::getVisualOrderText).map((var1x) -> {
         return new TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel create(Font var0, List<Component> var1) {
      return createFixed(var0, (List)var1.stream().map(Component::getVisualOrderText).map((var1x) -> {
         return new TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel createFixed(final Font var0, final List<TextWithWidth> var1) {
      return var1.isEmpty() ? EMPTY : new MultiLineLabel() {
         private final int width = var1.stream().mapToInt((var0x) -> {
            return var0x.width;
         }).max().orElse(0);

         public int renderCentered(GuiGraphics var1x, int var2, int var3) {
            Objects.requireNonNull(var0);
            return this.renderCentered(var1x, var2, var3, 9, 16777215);
         }

         public int renderCentered(GuiGraphics var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(Iterator var7 = var1.iterator(); var7.hasNext(); var6 += var4) {
               TextWithWidth var8 = (TextWithWidth)var7.next();
               var1x.drawString(var0, var8.text, var2 - var8.width / 2, var6, var5);
            }

            return var6;
         }

         public int renderLeftAligned(GuiGraphics var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(Iterator var7 = var1.iterator(); var7.hasNext(); var6 += var4) {
               TextWithWidth var8 = (TextWithWidth)var7.next();
               var1x.drawString(var0, var8.text, var2, var6, var5);
            }

            return var6;
         }

         public int renderLeftAlignedNoShadow(GuiGraphics var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(Iterator var7 = var1.iterator(); var7.hasNext(); var6 += var4) {
               TextWithWidth var8 = (TextWithWidth)var7.next();
               var1x.drawString(var0, var8.text, var2, var6, var5, false);
            }

            return var6;
         }

         public void renderBackgroundCentered(GuiGraphics var1x, int var2, int var3, int var4, int var5, int var6) {
            int var7 = var1.stream().mapToInt((var0x) -> {
               return var0x.width;
            }).max().orElse(0);
            if (var7 > 0) {
               var1x.fill(var2 - var7 / 2 - var5, var3 - var5, var2 + var7 / 2 + var5, var3 + var1.size() * var4 + var5, var6);
            }

         }

         public int getLineCount() {
            return var1.size();
         }

         public int getWidth() {
            return this.width;
         }
      };
   }

   int renderCentered(GuiGraphics var1, int var2, int var3);

   int renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5);

   int renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5);

   int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5);

   void renderBackgroundCentered(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6);

   int getLineCount();

   int getWidth();

   public static class TextWithWidth {
      final FormattedCharSequence text;
      final int width;

      TextWithWidth(FormattedCharSequence var1, int var2) {
         super();
         this.text = var1;
         this.width = var2;
      }
   }
}
