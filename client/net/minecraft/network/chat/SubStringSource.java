package net.minecraft.network.chat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;

public class SubStringSource {
   private final String plainText;
   private final List<Style> charStyles;
   private final Int2IntFunction reverseCharModifier;

   private SubStringSource(String var1, List<Style> var2, Int2IntFunction var3) {
      super();
      this.plainText = var1;
      this.charStyles = ImmutableList.copyOf(var2);
      this.reverseCharModifier = var3;
   }

   public String getPlainText() {
      return this.plainText;
   }

   public List<FormattedCharSequence> substring(int var1, int var2, boolean var3) {
      if (var2 == 0) {
         return ImmutableList.of();
      } else {
         ArrayList var4 = Lists.newArrayList();
         Style var5 = (Style)this.charStyles.get(var1);
         int var6 = var1;

         for(int var7 = 1; var7 < var2; ++var7) {
            int var8 = var1 + var7;
            Style var9 = (Style)this.charStyles.get(var8);
            if (!var9.equals(var5)) {
               String var10 = this.plainText.substring(var6, var8);
               var4.add(var3 ? FormattedCharSequence.backward(var10, var5, this.reverseCharModifier) : FormattedCharSequence.forward(var10, var5));
               var5 = var9;
               var6 = var8;
            }
         }

         if (var6 < var1 + var2) {
            String var11 = this.plainText.substring(var6, var1 + var2);
            var4.add(var3 ? FormattedCharSequence.backward(var11, var5, this.reverseCharModifier) : FormattedCharSequence.forward(var11, var5));
         }

         return (List)(var3 ? Lists.reverse(var4) : var4);
      }
   }

   public static SubStringSource create(FormattedText var0) {
      return create(var0, (var0x) -> {
         return var0x;
      }, (var0x) -> {
         return var0x;
      });
   }

   public static SubStringSource create(FormattedText var0, Int2IntFunction var1, UnaryOperator<String> var2) {
      StringBuilder var3 = new StringBuilder();
      ArrayList var4 = Lists.newArrayList();
      var0.visit((var2x, var3x) -> {
         StringDecomposer.iterateFormatted(var3x, var2x, (var2, var3xx, var4x) -> {
            var3.appendCodePoint(var4x);
            int var5 = Character.charCount(var4x);

            for(int var6 = 0; var6 < var5; ++var6) {
               var4.add(var3xx);
            }

            return true;
         });
         return Optional.empty();
      }, Style.EMPTY);
      return new SubStringSource((String)var2.apply(var3.toString()), var4, var1);
   }
}
