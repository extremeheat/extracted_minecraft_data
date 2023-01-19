package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public interface MultiLineLabel {
   MultiLineLabel EMPTY = new MultiLineLabel() {
      @Override
      public int renderCentered(PoseStack var1, int var2, int var3) {
         return var3;
      }

      @Override
      public int renderCentered(PoseStack var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      @Override
      public int renderLeftAligned(PoseStack var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      @Override
      public int renderLeftAlignedNoShadow(PoseStack var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      @Override
      public void renderBackgroundCentered(PoseStack var1, int var2, int var3, int var4, int var5, int var6) {
      }

      @Override
      public int getLineCount() {
         return 0;
      }

      @Override
      public int getWidth() {
         return 0;
      }
   };

   static MultiLineLabel create(Font var0, FormattedText var1, int var2) {
      return createFixed(
         var0,
         var0.split(var1, var2).stream().map(var1x -> new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x))).collect(ImmutableList.toImmutableList())
      );
   }

   static MultiLineLabel create(Font var0, FormattedText var1, int var2, int var3) {
      return createFixed(
         var0,
         var0.split(var1, var2)
            .stream()
            .limit((long)var3)
            .map(var1x -> new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x)))
            .collect(ImmutableList.toImmutableList())
      );
   }

   static MultiLineLabel create(Font var0, Component... var1) {
      return createFixed(
         var0,
         Arrays.stream(var1)
            .map(Component::getVisualOrderText)
            .map(var1x -> new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x)))
            .collect(ImmutableList.toImmutableList())
      );
   }

   static MultiLineLabel create(Font var0, List<Component> var1) {
      return createFixed(
         var0,
         var1.stream()
            .map(Component::getVisualOrderText)
            .map(var1x -> new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x)))
            .collect(ImmutableList.toImmutableList())
      );
   }

   static MultiLineLabel createFixed(final Font var0, final List<MultiLineLabel.TextWithWidth> var1) {
      return var1.isEmpty() ? EMPTY : new MultiLineLabel() {
         private final int width = var1.stream().mapToInt(var0xx -> var0xx.width).max().orElse(0);

         @Override
         public int renderCentered(PoseStack var1x, int var2, int var3) {
            return this.renderCentered(var1x, var2, var3, 9, 16777215);
         }

         @Override
         public int renderCentered(PoseStack var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(MultiLineLabel.TextWithWidth var8 : var1) {
               var0.drawShadow(var1x, var8.text, (float)(var2 - var8.width / 2), (float)var6, var5);
               var6 += var4;
            }

            return var6;
         }

         @Override
         public int renderLeftAligned(PoseStack var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(MultiLineLabel.TextWithWidth var8 : var1) {
               var0.drawShadow(var1x, var8.text, (float)var2, (float)var6, var5);
               var6 += var4;
            }

            return var6;
         }

         @Override
         public int renderLeftAlignedNoShadow(PoseStack var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(MultiLineLabel.TextWithWidth var8 : var1) {
               var0.draw(var1x, var8.text, (float)var2, (float)var6, var5);
               var6 += var4;
            }

            return var6;
         }

         @Override
         public void renderBackgroundCentered(PoseStack var1x, int var2, int var3, int var4, int var5, int var6) {
            int var7 = var1.stream().mapToInt(var0xx -> var0xx.width).max().orElse(0);
            if (var7 > 0) {
               GuiComponent.fill(var1x, var2 - var7 / 2 - var5, var3 - var5, var2 + var7 / 2 + var5, var3 + var1.size() * var4 + var5, var6);
            }
         }

         @Override
         public int getLineCount() {
            return var1.size();
         }

         @Override
         public int getWidth() {
            return this.width;
         }
      };
   }

   int renderCentered(PoseStack var1, int var2, int var3);

   int renderCentered(PoseStack var1, int var2, int var3, int var4, int var5);

   int renderLeftAligned(PoseStack var1, int var2, int var3, int var4, int var5);

   int renderLeftAlignedNoShadow(PoseStack var1, int var2, int var3, int var4, int var5);

   void renderBackgroundCentered(PoseStack var1, int var2, int var3, int var4, int var5, int var6);

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
