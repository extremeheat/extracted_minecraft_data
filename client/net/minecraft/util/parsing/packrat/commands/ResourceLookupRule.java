package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public abstract class ResourceLookupRule<C, V> implements Rule<StringReader, V>, ResourceSuggestion {
   private final NamedRule<StringReader, Identifier> idParser;
   protected final C context;
   private final DelayedException<CommandSyntaxException> error;

   protected ResourceLookupRule(NamedRule<StringReader, Identifier> var1, C var2) {
      super();
      this.idParser = var1;
      this.context = var2;
      this.error = DelayedException.create(Identifier.ERROR_INVALID);
   }

   public @Nullable V parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();
      int var2 = var1.mark();
      Identifier var3 = (Identifier)var1.parse(this.idParser);
      if (var3 != null) {
         try {
            return (V)this.validateElement((ImmutableStringReader)var1.input(), var3);
         } catch (Exception var5) {
            var1.errorCollector().store(var2, this, var5);
            return null;
         }
      } else {
         var1.errorCollector().store(var2, this, this.error);
         return null;
      }
   }

   protected abstract V validateElement(ImmutableStringReader var1, Identifier var2) throws Exception;
}
