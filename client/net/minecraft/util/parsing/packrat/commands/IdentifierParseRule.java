package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public class IdentifierParseRule implements Rule<CommandParserState, Identifier> {
   public static final Rule<CommandParserState, Identifier> INSTANCE = new IdentifierParseRule();

   public static <S extends CommandParserState> Rule<S, Identifier> instance() {
      return INSTANCE;
   }

   private IdentifierParseRule() {
      super();
   }

   public @Nullable Identifier parse(final ParseState<CommandParserState> state) {
      StringReader reader = ((CommandParserState)state.input()).reader();
      reader.skipWhitespace();

      try {
         return Identifier.readNonEmpty(reader);
      } catch (CommandSyntaxException var4) {
         return null;
      }
   }
}
