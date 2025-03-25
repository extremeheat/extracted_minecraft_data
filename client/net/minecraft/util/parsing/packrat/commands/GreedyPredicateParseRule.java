package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public abstract class GreedyPredicateParseRule implements Rule<StringReader, String> {
   private final int minSize;
   private final int maxSize;
   private final DelayedException<CommandSyntaxException> error;

   public GreedyPredicateParseRule(int var1, DelayedException<CommandSyntaxException> var2) {
      this(var1, 2147483647, var2);
   }

   public GreedyPredicateParseRule(int var1, int var2, DelayedException<CommandSyntaxException> var3) {
      super();
      this.minSize = var1;
      this.maxSize = var2;
      this.error = var3;
   }

   @Nullable
   public String parse(ParseState<StringReader> var1) {
      StringReader var2 = (StringReader)var1.input();
      String var3 = var2.getString();
      int var4 = var2.getCursor();

      int var5;
      for(var5 = var4; var5 < var3.length() && this.isAccepted(var3.charAt(var5)) && var5 - var4 < this.maxSize; ++var5) {
      }

      int var6 = var5 - var4;
      if (var6 < this.minSize) {
         var1.errorCollector().store(var1.mark(), this.error);
         return null;
      } else {
         var2.setCursor(var5);
         return var3.substring(var4, var5);
      }
   }

   protected abstract boolean isAccepted(char var1);

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParseState var1) {
      return this.parse(var1);
   }
}
