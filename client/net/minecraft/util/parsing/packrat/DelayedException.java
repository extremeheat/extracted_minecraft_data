package net.minecraft.util.parsing.packrat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;

public interface DelayedException<T extends Exception> {
   T create(String var1, int var2);

   static DelayedException<CommandSyntaxException> create(SimpleCommandExceptionType var0) {
      return (var1, var2) -> var0.createWithContext(StringReaderTerms.createReader(var1, var2));
   }

   static DelayedException<CommandSyntaxException> create(DynamicCommandExceptionType var0, String var1) {
      return (var2, var3) -> var0.createWithContext(StringReaderTerms.createReader(var2, var3), var1);
   }
}
