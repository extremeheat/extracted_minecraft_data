package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import net.minecraft.util.parsing.packrat.Control;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;

public interface StringReaderTerms {
   static Term<StringReader> word(String var0) {
      return new TerminalWord(var0);
   }

   static Term<StringReader> character(char var0) {
      return new TerminalCharacter(var0);
   }

   public static record TerminalWord(String value) implements Term<StringReader> {
      public TerminalWord(String var1) {
         super();
         this.value = var1;
      }

      public boolean parse(ParseState<StringReader> var1, Scope var2, Control var3) {
         ((StringReader)var1.input()).skipWhitespace();
         int var4 = var1.mark();
         String var5 = ((StringReader)var1.input()).readUnquotedString();
         if (!var5.equals(this.value)) {
            var1.errorCollector().store(var4, (var1x) -> Stream.of(this.value), CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(this.value));
            return false;
         } else {
            return true;
         }
      }
   }

   public static record TerminalCharacter(char value) implements Term<StringReader> {
      public TerminalCharacter(char var1) {
         super();
         this.value = var1;
      }

      public boolean parse(ParseState<StringReader> var1, Scope var2, Control var3) {
         ((StringReader)var1.input()).skipWhitespace();
         int var4 = var1.mark();
         if (((StringReader)var1.input()).canRead() && ((StringReader)var1.input()).read() == this.value) {
            return true;
         } else {
            var1.errorCollector().store(var4, (var1x) -> Stream.of(String.valueOf(this.value)), CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().create(this.value));
            return false;
         }
      }
   }
}
