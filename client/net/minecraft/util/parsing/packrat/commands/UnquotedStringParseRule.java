package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class UnquotedStringParseRule implements Rule<StringReader, String> {
   private final int minSize;
   private final DelayedException<CommandSyntaxException> error;

   public UnquotedStringParseRule(int var1, DelayedException<CommandSyntaxException> var2) {
      super();
      this.minSize = var1;
      this.error = var2;
   }

   @Nullable
   public String parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();
      int var2 = var1.mark();
      String var3 = ((StringReader)var1.input()).readUnquotedString();
      if (var3.length() < this.minSize) {
         var1.errorCollector().store(var2, this.error);
         return null;
      } else {
         return var3;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParseState var1) {
      return this.parse(var1);
   }
}
