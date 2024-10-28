package net.minecraft.client.resources.language;

import com.google.common.collect.Lists;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.SubStringSource;
import net.minecraft.util.FormattedCharSequence;

public class FormattedBidiReorder {
   public FormattedBidiReorder() {
      super();
   }

   public static FormattedCharSequence reorder(FormattedText var0, boolean var1) {
      SubStringSource var2 = SubStringSource.create(var0, UCharacter::getMirror, FormattedBidiReorder::shape);
      Bidi var3 = new Bidi(var2.getPlainText(), var1 ? 127 : 126);
      var3.setReorderingMode(0);
      ArrayList var4 = Lists.newArrayList();
      int var5 = var3.countRuns();

      for(int var6 = 0; var6 < var5; ++var6) {
         BidiRun var7 = var3.getVisualRun(var6);
         var4.addAll(var2.substring(var7.getStart(), var7.getLength(), var7.isOddRun()));
      }

      return FormattedCharSequence.composite((List)var4);
   }

   private static String shape(String var0) {
      try {
         return (new ArabicShaping(8)).shape(var0);
      } catch (Exception var2) {
         return var0;
      }
   }
}
