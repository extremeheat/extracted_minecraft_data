package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.Style;

@FunctionalInterface
public interface FormattedCharSequence {
   FormattedCharSequence EMPTY = (var0) -> {
      return true;
   };

   boolean accept(FormattedCharSink var1);

   static FormattedCharSequence codepoint(int var0, Style var1) {
      return (var2) -> {
         return var2.accept(0, var1, var0);
      };
   }

   static FormattedCharSequence forward(String var0, Style var1) {
      return var0.isEmpty() ? EMPTY : (var2) -> {
         return StringDecomposer.iterate(var0, var1, var2);
      };
   }

   static FormattedCharSequence forward(String var0, Style var1, Int2IntFunction var2) {
      return var0.isEmpty() ? EMPTY : (var3) -> {
         return StringDecomposer.iterate(var0, var1, decorateOutput(var3, var2));
      };
   }

   static FormattedCharSequence backward(String var0, Style var1) {
      return var0.isEmpty() ? EMPTY : (var2) -> {
         return StringDecomposer.iterateBackwards(var0, var1, var2);
      };
   }

   static FormattedCharSequence backward(String var0, Style var1, Int2IntFunction var2) {
      return var0.isEmpty() ? EMPTY : (var3) -> {
         return StringDecomposer.iterateBackwards(var0, var1, decorateOutput(var3, var2));
      };
   }

   static FormattedCharSink decorateOutput(FormattedCharSink var0, Int2IntFunction var1) {
      return (var2, var3, var4) -> {
         return var0.accept(var2, var3, (Integer)var1.apply(var4));
      };
   }

   static FormattedCharSequence composite() {
      return EMPTY;
   }

   static FormattedCharSequence composite(FormattedCharSequence var0) {
      return var0;
   }

   static FormattedCharSequence composite(FormattedCharSequence var0, FormattedCharSequence var1) {
      return fromPair(var0, var1);
   }

   static FormattedCharSequence composite(FormattedCharSequence... var0) {
      return fromList(ImmutableList.copyOf(var0));
   }

   static FormattedCharSequence composite(List<FormattedCharSequence> var0) {
      int var1 = var0.size();
      switch(var1) {
      case 0:
         return EMPTY;
      case 1:
         return (FormattedCharSequence)var0.get(0);
      case 2:
         return fromPair((FormattedCharSequence)var0.get(0), (FormattedCharSequence)var0.get(1));
      default:
         return fromList(ImmutableList.copyOf(var0));
      }
   }

   static FormattedCharSequence fromPair(FormattedCharSequence var0, FormattedCharSequence var1) {
      return (var2) -> {
         return var0.accept(var2) && var1.accept(var2);
      };
   }

   static FormattedCharSequence fromList(List<FormattedCharSequence> var0) {
      return (var1) -> {
         Iterator var2 = var0.iterator();

         FormattedCharSequence var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (FormattedCharSequence)var2.next();
         } while(var3.accept(var1));

         return false;
      };
   }
}
