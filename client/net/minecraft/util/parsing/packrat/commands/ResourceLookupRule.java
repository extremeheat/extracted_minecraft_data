package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import net.minecraft.util.parsing.packrat.Suggestion;
import org.jspecify.annotations.Nullable;

public abstract class ResourceLookupRule<S extends CommandParserState, V> implements Rule<S, V> {
   private final NamedRule<S, Identifier> idParser;
   private final DelayedException<CommandSyntaxException> error;

   protected ResourceLookupRule(final NamedRule<S, Identifier> idParser) {
      super();
      this.idParser = idParser;
      this.error = DelayedException.create(Identifier.ERROR_INVALID);
   }

   public @Nullable V parse(final ParseState<S> state) {
      S context = state.input();
      StringReader reader = context.reader();
      reader.skipWhitespace();
      int mark = state.mark();
      Identifier id = (Identifier)state.parse(this.idParser);
      if (id != null) {
         try {
            return (V)this.validateElement(context, id);
         } catch (Exception e) {
            state.errorCollector().store(mark, this.suggestion(context), e);
            return null;
         }
      } else {
         state.errorCollector().store(mark, this.suggestion(context), this.error);
         return null;
      }
   }

   protected abstract V validateElement(S context, Identifier id) throws Exception;

   protected abstract Suggestion suggestion(S context);
}
