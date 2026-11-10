package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import net.minecraft.commands.ParseContext;

public interface CommandParserState {
   StringReader reader();

   static CommandParserState simple(final StringReader reader) {
      return new Simple(reader);
   }

   static CommandParserState simple(final String value) {
      return simple(new StringReader(value));
   }

   public static record Simple(StringReader reader) implements CommandParserState {
      public static final Factory<Simple> FACTORY = (var0, reader) -> new Simple(reader);

      public Simple {
         super();
      }
   }

   public interface Factory<S extends CommandParserState> {
      default S create(final ParseContext parseContext, final String input) {
         return (S)this.create(parseContext, new StringReader(input));
      }

      S create(ParseContext parseContext, StringReader reader);
   }
}
