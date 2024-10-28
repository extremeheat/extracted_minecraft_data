package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class StringSplitter {
   final WidthProvider widthProvider;

   public StringSplitter(WidthProvider var1) {
      super();
      this.widthProvider = var1;
   }

   public float stringWidth(@Nullable String var1) {
      if (var1 == null) {
         return 0.0F;
      } else {
         MutableFloat var2 = new MutableFloat();
         StringDecomposer.iterateFormatted(var1, Style.EMPTY, (var2x, var3, var4) -> {
            var2.add(this.widthProvider.getWidth(var4, var3));
            return true;
         });
         return var2.floatValue();
      }
   }

   public float stringWidth(FormattedText var1) {
      MutableFloat var2 = new MutableFloat();
      StringDecomposer.iterateFormatted(var1, Style.EMPTY, (var2x, var3, var4) -> {
         var2.add(this.widthProvider.getWidth(var4, var3));
         return true;
      });
      return var2.floatValue();
   }

   public float stringWidth(FormattedCharSequence var1) {
      MutableFloat var2 = new MutableFloat();
      var1.accept((var2x, var3, var4) -> {
         var2.add(this.widthProvider.getWidth(var4, var3));
         return true;
      });
      return var2.floatValue();
   }

   public int plainIndexAtWidth(String var1, int var2, Style var3) {
      WidthLimitedCharSink var4 = new WidthLimitedCharSink((float)var2);
      StringDecomposer.iterate(var1, var3, var4);
      return var4.getPosition();
   }

   public String plainHeadByWidth(String var1, int var2, Style var3) {
      return var1.substring(0, this.plainIndexAtWidth(var1, var2, var3));
   }

   public String plainTailByWidth(String var1, int var2, Style var3) {
      MutableFloat var4 = new MutableFloat();
      MutableInt var5 = new MutableInt(var1.length());
      StringDecomposer.iterateBackwards(var1, var3, (var4x, var5x, var6) -> {
         float var7 = var4.addAndGet(this.widthProvider.getWidth(var6, var5x));
         if (var7 > (float)var2) {
            return false;
         } else {
            var5.setValue(var4x);
            return true;
         }
      });
      return var1.substring(var5.intValue());
   }

   public int formattedIndexByWidth(String var1, int var2, Style var3) {
      WidthLimitedCharSink var4 = new WidthLimitedCharSink((float)var2);
      StringDecomposer.iterateFormatted((String)var1, var3, var4);
      return var4.getPosition();
   }

   @Nullable
   public Style componentStyleAtWidth(FormattedText var1, int var2) {
      WidthLimitedCharSink var3 = new WidthLimitedCharSink((float)var2);
      return (Style)var1.visit((var1x, var2x) -> {
         return StringDecomposer.iterateFormatted((String)var2x, var1x, var3) ? Optional.empty() : Optional.of(var1x);
      }, Style.EMPTY).orElse((Object)null);
   }

   @Nullable
   public Style componentStyleAtWidth(FormattedCharSequence var1, int var2) {
      WidthLimitedCharSink var3 = new WidthLimitedCharSink((float)var2);
      MutableObject var4 = new MutableObject();
      var1.accept((var2x, var3x, var4x) -> {
         if (!var3.accept(var2x, var3x, var4x)) {
            var4.setValue(var3x);
            return false;
         } else {
            return true;
         }
      });
      return (Style)var4.getValue();
   }

   public String formattedHeadByWidth(String var1, int var2, Style var3) {
      return var1.substring(0, this.formattedIndexByWidth(var1, var2, var3));
   }

   public FormattedText headByWidth(FormattedText var1, int var2, Style var3) {
      final WidthLimitedCharSink var4 = new WidthLimitedCharSink((float)var2);
      return (FormattedText)var1.visit(new FormattedText.StyledContentConsumer<FormattedText>(this) {
         private final ComponentCollector collector = new ComponentCollector();

         public Optional<FormattedText> accept(Style var1, String var2) {
            var4.resetPosition();
            if (!StringDecomposer.iterateFormatted((String)var2, var1, var4)) {
               String var3 = var2.substring(0, var4.getPosition());
               if (!var3.isEmpty()) {
                  this.collector.append(FormattedText.of(var3, var1));
               }

               return Optional.of(this.collector.getResultOrEmpty());
            } else {
               if (!var2.isEmpty()) {
                  this.collector.append(FormattedText.of(var2, var1));
               }

               return Optional.empty();
            }
         }
      }, var3).orElse(var1);
   }

   public int findLineBreak(String var1, int var2, Style var3) {
      LineBreakFinder var4 = new LineBreakFinder((float)var2);
      StringDecomposer.iterateFormatted((String)var1, var3, var4);
      return var4.getSplitPosition();
   }

   public static int getWordPosition(String var0, int var1, int var2, boolean var3) {
      int var4 = var2;
      boolean var5 = var1 < 0;
      int var6 = Math.abs(var1);

      for(int var7 = 0; var7 < var6; ++var7) {
         if (var5) {
            while(var3 && var4 > 0 && (var0.charAt(var4 - 1) == ' ' || var0.charAt(var4 - 1) == '\n')) {
               --var4;
            }

            while(var4 > 0 && var0.charAt(var4 - 1) != ' ' && var0.charAt(var4 - 1) != '\n') {
               --var4;
            }
         } else {
            int var8 = var0.length();
            int var9 = var0.indexOf(32, var4);
            int var10 = var0.indexOf(10, var4);
            if (var9 == -1 && var10 == -1) {
               var4 = -1;
            } else if (var9 != -1 && var10 != -1) {
               var4 = Math.min(var9, var10);
            } else if (var9 != -1) {
               var4 = var9;
            } else {
               var4 = var10;
            }

            if (var4 == -1) {
               var4 = var8;
            } else {
               while(var3 && var4 < var8 && (var0.charAt(var4) == ' ' || var0.charAt(var4) == '\n')) {
                  ++var4;
               }
            }
         }
      }

      return var4;
   }

   public void splitLines(String var1, int var2, Style var3, boolean var4, LinePosConsumer var5) {
      int var6 = 0;
      int var7 = var1.length();

      LineBreakFinder var9;
      for(Style var8 = var3; var6 < var7; var8 = var9.getSplitStyle()) {
         var9 = new LineBreakFinder((float)var2);
         boolean var10 = StringDecomposer.iterateFormatted(var1, var6, var8, var3, var9);
         if (var10) {
            var5.accept(var8, var6, var7);
            break;
         }

         int var11 = var9.getSplitPosition();
         char var12 = var1.charAt(var11);
         int var13 = var12 != '\n' && var12 != ' ' ? var11 : var11 + 1;
         var5.accept(var8, var6, var4 ? var13 : var11);
         var6 = var13;
      }

   }

   public List<FormattedText> splitLines(String var1, int var2, Style var3) {
      ArrayList var4 = Lists.newArrayList();
      this.splitLines(var1, var2, var3, false, (var2x, var3x, var4x) -> {
         var4.add(FormattedText.of(var1.substring(var3x, var4x), var2x));
      });
      return var4;
   }

   public List<FormattedText> splitLines(FormattedText var1, int var2, Style var3) {
      ArrayList var4 = Lists.newArrayList();
      this.splitLines(var1, var2, var3, (var1x, var2x) -> {
         var4.add(var1x);
      });
      return var4;
   }

   public List<FormattedText> splitLines(FormattedText var1, int var2, Style var3, FormattedText var4) {
      ArrayList var5 = Lists.newArrayList();
      this.splitLines(var1, var2, var3, (var2x, var3x) -> {
         var5.add(var3x ? FormattedText.composite(var4, var2x) : var2x);
      });
      return var5;
   }

   public void splitLines(FormattedText var1, int var2, Style var3, BiConsumer<FormattedText, Boolean> var4) {
      ArrayList var5 = Lists.newArrayList();
      var1.visit((var1x, var2x) -> {
         if (!var2x.isEmpty()) {
            var5.add(new LineComponent(var2x, var1x));
         }

         return Optional.empty();
      }, var3);
      FlatComponents var6 = new FlatComponents(var5);
      boolean var7 = true;
      boolean var8 = false;
      boolean var9 = false;

      while(true) {
         while(var7) {
            var7 = false;
            LineBreakFinder var10 = new LineBreakFinder((float)var2);
            Iterator var11 = var6.parts.iterator();

            while(var11.hasNext()) {
               LineComponent var12 = (LineComponent)var11.next();
               boolean var13 = StringDecomposer.iterateFormatted(var12.contents, 0, var12.style, var3, var10);
               if (!var13) {
                  int var14 = var10.getSplitPosition();
                  Style var15 = var10.getSplitStyle();
                  char var16 = var6.charAt(var14);
                  boolean var17 = var16 == '\n';
                  boolean var18 = var17 || var16 == ' ';
                  var8 = var17;
                  FormattedText var19 = var6.splitAt(var14, var18 ? 1 : 0, var15);
                  var4.accept(var19, var9);
                  var9 = !var17;
                  var7 = true;
                  break;
               }

               var10.addToOffset(var12.contents.length());
            }
         }

         FormattedText var20 = var6.getRemainder();
         if (var20 != null) {
            var4.accept(var20, var9);
         } else if (var8) {
            var4.accept(FormattedText.EMPTY, false);
         }

         return;
      }
   }

   @FunctionalInterface
   public interface WidthProvider {
      float getWidth(int var1, Style var2);
   }

   private class WidthLimitedCharSink implements FormattedCharSink {
      private float maxWidth;
      private int position;

      public WidthLimitedCharSink(final float var2) {
         super();
         this.maxWidth = var2;
      }

      public boolean accept(int var1, Style var2, int var3) {
         this.maxWidth -= StringSplitter.this.widthProvider.getWidth(var3, var2);
         if (this.maxWidth >= 0.0F) {
            this.position = var1 + Character.charCount(var3);
            return true;
         } else {
            return false;
         }
      }

      public int getPosition() {
         return this.position;
      }

      public void resetPosition() {
         this.position = 0;
      }
   }

   class LineBreakFinder implements FormattedCharSink {
      private final float maxWidth;
      private int lineBreak = -1;
      private Style lineBreakStyle;
      private boolean hadNonZeroWidthChar;
      private float width;
      private int lastSpace;
      private Style lastSpaceStyle;
      private int nextChar;
      private int offset;

      public LineBreakFinder(final float var2) {
         super();
         this.lineBreakStyle = Style.EMPTY;
         this.lastSpace = -1;
         this.lastSpaceStyle = Style.EMPTY;
         this.maxWidth = Math.max(var2, 1.0F);
      }

      public boolean accept(int var1, Style var2, int var3) {
         int var4 = var1 + this.offset;
         switch (var3) {
            case 10:
               return this.finishIteration(var4, var2);
            case 32:
               this.lastSpace = var4;
               this.lastSpaceStyle = var2;
            default:
               float var5 = StringSplitter.this.widthProvider.getWidth(var3, var2);
               this.width += var5;
               if (this.hadNonZeroWidthChar && this.width > this.maxWidth) {
                  return this.lastSpace != -1 ? this.finishIteration(this.lastSpace, this.lastSpaceStyle) : this.finishIteration(var4, var2);
               } else {
                  this.hadNonZeroWidthChar |= var5 != 0.0F;
                  this.nextChar = var4 + Character.charCount(var3);
                  return true;
               }
         }
      }

      private boolean finishIteration(int var1, Style var2) {
         this.lineBreak = var1;
         this.lineBreakStyle = var2;
         return false;
      }

      private boolean lineBreakFound() {
         return this.lineBreak != -1;
      }

      public int getSplitPosition() {
         return this.lineBreakFound() ? this.lineBreak : this.nextChar;
      }

      public Style getSplitStyle() {
         return this.lineBreakStyle;
      }

      public void addToOffset(int var1) {
         this.offset += var1;
      }
   }

   @FunctionalInterface
   public interface LinePosConsumer {
      void accept(Style var1, int var2, int var3);
   }

   static class FlatComponents {
      final List<LineComponent> parts;
      private String flatParts;

      public FlatComponents(List<LineComponent> var1) {
         super();
         this.parts = var1;
         this.flatParts = (String)var1.stream().map((var0) -> {
            return var0.contents;
         }).collect(Collectors.joining());
      }

      public char charAt(int var1) {
         return this.flatParts.charAt(var1);
      }

      public FormattedText splitAt(int var1, int var2, Style var3) {
         ComponentCollector var4 = new ComponentCollector();
         ListIterator var5 = this.parts.listIterator();
         int var6 = var1;
         boolean var7 = false;

         while(var5.hasNext()) {
            LineComponent var8 = (LineComponent)var5.next();
            String var9 = var8.contents;
            int var10 = var9.length();
            String var11;
            if (!var7) {
               if (var6 > var10) {
                  var4.append(var8);
                  var5.remove();
                  var6 -= var10;
               } else {
                  var11 = var9.substring(0, var6);
                  if (!var11.isEmpty()) {
                     var4.append(FormattedText.of(var11, var8.style));
                  }

                  var6 += var2;
                  var7 = true;
               }
            }

            if (var7) {
               if (var6 <= var10) {
                  var11 = var9.substring(var6);
                  if (var11.isEmpty()) {
                     var5.remove();
                  } else {
                     var5.set(new LineComponent(var11, var3));
                  }
                  break;
               }

               var5.remove();
               var6 -= var10;
            }
         }

         this.flatParts = this.flatParts.substring(var1 + var2);
         return var4.getResultOrEmpty();
      }

      @Nullable
      public FormattedText getRemainder() {
         ComponentCollector var1 = new ComponentCollector();
         List var10000 = this.parts;
         Objects.requireNonNull(var1);
         var10000.forEach(var1::append);
         this.parts.clear();
         return var1.getResult();
      }
   }

   private static class LineComponent implements FormattedText {
      final String contents;
      final Style style;

      public LineComponent(String var1, Style var2) {
         super();
         this.contents = var1;
         this.style = var2;
      }

      public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
         return var1.accept(this.contents);
      }

      public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
         return var1.accept(this.style.applyTo(var2), this.contents);
      }
   }
}
