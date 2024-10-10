package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public interface MultiLineLabel {
   MultiLineLabel EMPTY = new MultiLineLabel() {
      @Override
      public void renderCentered(GuiGraphics var1, int var2, int var3) {
      }

      @Override
      public void renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      }

      @Override
      public void renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      }

      @Override
      public int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5) {
         return var3;
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
         private List<MultiLineLabel.TextAndWidth> cachedTextAndWidth;
         @Nullable
         private Language splitWithLanguage;

         @Override
         public void renderCentered(GuiGraphics var1x, int var2x, int var3x) {
            this.renderCentered(var1x, var2x, var3x, 9, -1);
         }

         @Override
         public void renderCentered(GuiGraphics var1x, int var2x, int var3x, int var4, int var5) {
            int var6 = var3x;

            for (MultiLineLabel.TextAndWidth var8 : this.getSplitMessage()) {
               var1x.drawCenteredString(var0, var8.text, var2x, var6, var5);
               var6 += var4;
            }
         }

         @Override
         public void renderLeftAligned(GuiGraphics var1x, int var2x, int var3x, int var4, int var5) {
            int var6 = var3x;

            for (MultiLineLabel.TextAndWidth var8 : this.getSplitMessage()) {
               var1x.drawString(var0, var8.text, var2x, var6, var5);
               var6 += var4;
            }
         }

         @Override
         public int renderLeftAlignedNoShadow(GuiGraphics var1x, int var2x, int var3x, int var4, int var5) {
            int var6 = var3x;

            for (MultiLineLabel.TextAndWidth var8 : this.getSplitMessage()) {
               var1x.drawString(var0, var8.text, var2x, var6, var5, false);
               var6 += var4;
            }

            return var6;
         }

         private List<MultiLineLabel.TextAndWidth> getSplitMessage() {
            Language var1x = Language.getInstance();
            if (this.cachedTextAndWidth != null && var1x == this.splitWithLanguage) {
               return this.cachedTextAndWidth;
            } else {
               this.splitWithLanguage = var1x;
               ArrayList var2x = new ArrayList();

               for (Component var6 : var3) {
                  var2x.addAll(var0.split(var6, var1));
               }

               this.cachedTextAndWidth = new ArrayList<>();

               for (FormattedCharSequence var8 : var2x.subList(0, Math.min(var2x.size(), var2))) {
                  this.cachedTextAndWidth.add(new MultiLineLabel.TextAndWidth(var8, var0.width(var8)));
               }

               return this.cachedTextAndWidth;
            }
         }

         @Override
         public int getLineCount() {
            return this.getSplitMessage().size();
         }

         @Override
         public int getWidth() {
            return Math.min(var1, this.getSplitMessage().stream().mapToInt(MultiLineLabel.TextAndWidth::width).max().orElse(0));
         }
      };
   }

   void renderCentered(GuiGraphics var1, int var2, int var3);

   void renderCentered(GuiGraphics var1, int var2, int var3, int var4, int var5);

   void renderLeftAligned(GuiGraphics var1, int var2, int var3, int var4, int var5);

   int renderLeftAlignedNoShadow(GuiGraphics var1, int var2, int var3, int var4, int var5);

   int getLineCount();

   int getWidth();

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
