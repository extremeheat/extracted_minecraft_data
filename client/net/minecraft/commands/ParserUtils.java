package net.minecraft.commands;

import com.mojang.brigadier.StringReader;
import net.minecraft.CharPredicate;

public class ParserUtils {
   public ParserUtils() {
      super();
   }

   public static String readWhile(StringReader var0, CharPredicate var1) {
      int var2 = var0.getCursor();

      while(var0.canRead() && var1.test(var0.peek())) {
         var0.skip();
      }

      return var0.getString().substring(var2, var0.getCursor());
   }
}
