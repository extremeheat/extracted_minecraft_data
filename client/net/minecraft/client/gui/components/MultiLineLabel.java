package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public interface MultiLineLabel {
   MultiLineLabel EMPTY = new MultiLineLabel() {
      public int renderCentered(PoseStack var1, int var2, int var3) {
         return var3;
      }

      public int renderCentered(PoseStack var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      public int renderLeftAligned(PoseStack var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      public int renderLeftAlignedNoShadow(PoseStack var1, int var2, int var3, int var4, int var5) {
         return var3;
      }

      public int getLineCount() {
         return 0;
      }
   };

   static MultiLineLabel create(Font var0, FormattedText var1, int var2) {
      return createFixed(var0, (List)var0.split(var1, var2).stream().map((var1x) -> {
         return new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel create(Font var0, FormattedText var1, int var2, int var3) {
      return createFixed(var0, (List)var0.split(var1, var2).stream().limit((long)var3).map((var1x) -> {
         return new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel create(Font var0, Component... var1) {
      return createFixed(var0, (List)Arrays.stream(var1).map(Component::getVisualOrderText).map((var1x) -> {
         return new MultiLineLabel.TextWithWidth(var1x, var0.width(var1x));
      }).collect(ImmutableList.toImmutableList()));
   }

   static MultiLineLabel createFixed(final Font var0, final List<MultiLineLabel.TextWithWidth> var1) {
      return var1.isEmpty() ? EMPTY : new MultiLineLabel() {
         public int renderCentered(PoseStack var1x, int var2, int var3) {
            var0.getClass();
            return this.renderCentered(var1x, var2, var3, 9, 16777215);
         }

         public int renderCentered(PoseStack var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(Iterator var7 = var1.iterator(); var7.hasNext(); var6 += var4) {
               MultiLineLabel.TextWithWidth var8 = (MultiLineLabel.TextWithWidth)var7.next();
               var0.drawShadow(var1x, var8.text, (float)(var2 - var8.width / 2), (float)var6, var5);
            }

            return var6;
         }

         public int renderLeftAligned(PoseStack var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(Iterator var7 = var1.iterator(); var7.hasNext(); var6 += var4) {
               MultiLineLabel.TextWithWidth var8 = (MultiLineLabel.TextWithWidth)var7.next();
               var0.drawShadow(var1x, var8.text, (float)var2, (float)var6, var5);
            }

            return var6;
         }

         public int renderLeftAlignedNoShadow(PoseStack var1x, int var2, int var3, int var4, int var5) {
            int var6 = var3;

            for(Iterator var7 = var1.iterator(); var7.hasNext(); var6 += var4) {
               MultiLineLabel.TextWithWidth var8 = (MultiLineLabel.TextWithWidth)var7.next();
               var0.draw(var1x, var8.text, (float)var2, (float)var6, var5);
            }

            return var6;
         }

         public int getLineCount() {
            return var1.size();
         }
      };
   }

   int renderCentered(PoseStack var1, int var2, int var3);

   int renderCentered(PoseStack var1, int var2, int var3, int var4, int var5);

   int renderLeftAligned(PoseStack var1, int var2, int var3, int var4, int var5);

   int renderLeftAlignedNoShadow(PoseStack var1, int var2, int var3, int var4, int var5);

   int getLineCount();

   public static class TextWithWidth {
      private final FormattedCharSequence text;
      private final int width;

      private TextWithWidth(FormattedCharSequence var1, int var2) {
         super();
         this.text = var1;
         this.width = var2;
      }

      // $FF: synthetic method
      TextWithWidth(FormattedCharSequence var1, int var2, Object var3) {
         this(var1, var2);
      }
   }
}
