package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public final class GreedyPatternParseRule implements Rule<StringReader, String> {
   private final Pattern pattern;
   private final DelayedException<CommandSyntaxException> error;

   public GreedyPatternParseRule(Pattern var1, DelayedException<CommandSyntaxException> var2) {
      super();
      this.pattern = var1;
      this.error = var2;
   }

   public String parse(ParseState<StringReader> var1) {
      StringReader var2 = (StringReader)var1.input();
      String var3 = var2.getString();
      Matcher var4 = this.pattern.matcher(var3).region(var2.getCursor(), var3.length());
      if (!var4.lookingAt()) {
         var1.errorCollector().store(var1.mark(), this.error);
         return null;
      } else {
         var2.setCursor(var4.end());
         return var4.group(0);
      }
   }

   // $FF: synthetic method
   public Object parse(final ParseState var1) {
      return this.parse(var1);
   }
}
