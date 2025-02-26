package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.chars.CharList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.parsing.packrat.Control;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;
import net.minecraft.util.parsing.packrat.Term;

public interface StringReaderTerms {
   static Term<StringReader> word(String var0) {
      return new TerminalWord(var0);
   }

   static Term<StringReader> character(final char var0) {
      return new TerminalCharacters(CharList.of(var0)) {
         protected boolean isAccepted(char var1) {
            return var0 == var1;
         }
      };
   }

   static Term<StringReader> characters(final char var0, final char var1) {
      return new TerminalCharacters(CharList.of(var0, var1)) {
         protected boolean isAccepted(char var1x) {
            return var1x == var0 || var1x == var1;
         }
      };
   }

   static StringReader createReader(String var0, int var1) {
      StringReader var2 = new StringReader(var0);
      var2.setCursor(var1);
      return var2;
   }

   public static final class TerminalWord implements Term<StringReader> {
      private final String value;
      private final DelayedException<CommandSyntaxException> error;
      private final SuggestionSupplier<StringReader> suggestions;

      public TerminalWord(String var1) {
         super();
         this.value = var1;
         this.error = DelayedException.create(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), var1);
         this.suggestions = (var1x) -> Stream.of(var1);
      }

      public boolean parse(ParseState<StringReader> var1, Scope var2, Control var3) {
         ((StringReader)var1.input()).skipWhitespace();
         int var4 = var1.mark();
         String var5 = ((StringReader)var1.input()).readUnquotedString();
         if (!var5.equals(this.value)) {
            var1.errorCollector().store(var4, this.suggestions, this.error);
            return false;
         } else {
            return true;
         }
      }

      public String toString() {
         return "terminal[" + this.value + "]";
      }
   }

   public abstract static class TerminalCharacters implements Term<StringReader> {
      private final DelayedException<CommandSyntaxException> error;
      private final SuggestionSupplier<StringReader> suggestions;

      public TerminalCharacters(CharList var1) {
         super();
         String var2 = (String)var1.intStream().mapToObj(Character::toString).collect(Collectors.joining("|"));
         this.error = DelayedException.create(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), String.valueOf(var2));
         this.suggestions = (var1x) -> var1.intStream().mapToObj(Character::toString);
      }

      public boolean parse(ParseState<StringReader> var1, Scope var2, Control var3) {
         ((StringReader)var1.input()).skipWhitespace();
         int var4 = var1.mark();
         if (((StringReader)var1.input()).canRead() && this.isAccepted(((StringReader)var1.input()).read())) {
            return true;
         } else {
            var1.errorCollector().store(var4, this.suggestions, this.error);
            return false;
         }
      }

      protected abstract boolean isAccepted(char var1);
   }
}
