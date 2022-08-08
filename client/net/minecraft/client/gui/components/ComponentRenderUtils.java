package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class ComponentRenderUtils {
   private static final FormattedCharSequence INDENT;

   public ComponentRenderUtils() {
      super();
   }

   private static String stripColor(String var0) {
      return (Boolean)Minecraft.getInstance().options.chatColors().get() ? var0 : ChatFormatting.stripFormatting(var0);
   }

   public static List<FormattedCharSequence> wrapComponents(FormattedText var0, int var1, Font var2) {
      ComponentCollector var3 = new ComponentCollector();
      var0.visit((var1x, var2x) -> {
         var3.append(FormattedText.of(stripColor(var2x), var1x));
         return Optional.empty();
      }, Style.EMPTY);
      ArrayList var4 = Lists.newArrayList();
      var2.getSplitter().splitLines(var3.getResultOrEmpty(), var1, Style.EMPTY, (var1x, var2x) -> {
         FormattedCharSequence var3 = Language.getInstance().getVisualOrder(var1x);
         var4.add(var2x ? FormattedCharSequence.composite(INDENT, var3) : var3);
      });
      return var4.isEmpty() ? Lists.newArrayList(new FormattedCharSequence[]{FormattedCharSequence.EMPTY}) : var4;
   }

   static {
      INDENT = FormattedCharSequence.codepoint(32, Style.EMPTY);
   }
}
