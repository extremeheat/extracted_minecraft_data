package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public abstract class NumberRunParseRule implements Rule<StringReader, String> {
   private final DelayedException<CommandSyntaxException> noValueError;
   private final DelayedException<CommandSyntaxException> underscoreNotAllowedError;

   public NumberRunParseRule(DelayedException<CommandSyntaxException> var1, DelayedException<CommandSyntaxException> var2) {
      super();
      this.noValueError = var1;
      this.underscoreNotAllowedError = var2;
   }

   @Nullable
   public String parse(ParseState<StringReader> var1) {
      StringReader var2 = (StringReader)var1.input();
      var2.skipWhitespace();
      String var3 = var2.getString();
      int var4 = var2.getCursor();

      int var5;
      for(var5 = var4; var5 < var3.length() && this.isAccepted(var3.charAt(var5)); ++var5) {
      }

      int var6 = var5 - var4;
      if (var6 == 0) {
         var1.errorCollector().store(var1.mark(), this.noValueError);
         return null;
      } else if (var3.charAt(var4) != '_' && var3.charAt(var5 - 1) != '_') {
         var2.setCursor(var5);
         return var3.substring(var4, var5);
      } else {
         var1.errorCollector().store(var1.mark(), this.underscoreNotAllowedError);
         return null;
      }
   }

   protected abstract boolean isAccepted(char var1);

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParseState var1) {
      return this.parse(var1);
   }
}
