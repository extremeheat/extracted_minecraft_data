package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.chars.CharList;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.parsing.packrat.Control;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Suggestion;
import net.minecraft.util.parsing.packrat.Term;

public interface StringReaderTerms {
   static <S extends CommandParserState> Term<S> word(final String value) {
      return new TerminalWord<S>(value);
   }

   static <S extends CommandParserState> Term<S> character(final char value) {
      return new TerminalCharacters<S>(CharList.of(value)) {
         protected boolean isAccepted(final char v) {
            return value == v;
         }
      };
   }

   static <S extends CommandParserState> Term<S> characters(final char v1, final char v2) {
      return new TerminalCharacters<S>(CharList.of(v1, v2)) {
         protected boolean isAccepted(final char v) {
            return v == v1 || v == v2;
         }
      };
   }

   static StringReader createReader(final String contents, final int cursor) {
      StringReader reader = new StringReader(contents);
      reader.setCursor(cursor);
      return reader;
   }

   public static final class TerminalWord<S extends CommandParserState> implements Term<S> {
      private final String value;
      private final DelayedException<CommandSyntaxException> error;
      private final Suggestion suggestion;

      public TerminalWord(final String value) {
         super();
         this.value = value;
         this.error = DelayedException.create(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), value);
         this.suggestion = new Suggestion.Literals(Set.of(value));
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         StringReader reader = ((CommandParserState)state.input()).reader();
         reader.skipWhitespace();
         int cursor = state.mark();
         String value = reader.readUnquotedString();
         if (!value.equals(this.value)) {
            state.errorCollector().store(cursor, this.suggestion, this.error);
            return false;
         } else {
            return true;
         }
      }

      public String toString() {
         return "terminal[" + this.value + "]";
      }
   }

   public abstract static class TerminalCharacters<S extends CommandParserState> implements Term<S> {
      private final DelayedException<CommandSyntaxException> error;
      private final Suggestion suggestion;

      public TerminalCharacters(final CharList values) {
         super();
         String joinedValues = (String)values.intStream().mapToObj(Character::toString).collect(Collectors.joining("|"));
         this.error = DelayedException.create(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), joinedValues);
         this.suggestion = new Suggestion.Literals((Set)values.intStream().mapToObj(Character::toString).collect(Collectors.toSet()));
      }

      public boolean parse(final ParseState<S> state, final Scope scope, final Control control) {
         StringReader reader = ((CommandParserState)state.input()).reader();
         reader.skipWhitespace();
         int cursor = state.mark();
         if (reader.canRead() && this.isAccepted(reader.read())) {
            return true;
         } else {
            state.errorCollector().store(cursor, this.suggestion, this.error);
            return false;
         }
      }

      protected abstract boolean isAccepted(char value);
   }
}
