package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class StringArgumentType implements ArgumentType<String> {
   private final StringArgumentType.StringType type;

   private StringArgumentType(StringArgumentType.StringType var1) {
      super();
      this.type = var1;
   }

   public static StringArgumentType word() {
      return new StringArgumentType(StringArgumentType.StringType.SINGLE_WORD);
   }

   public static StringArgumentType string() {
      return new StringArgumentType(StringArgumentType.StringType.QUOTABLE_PHRASE);
   }

   public static StringArgumentType greedyString() {
      return new StringArgumentType(StringArgumentType.StringType.GREEDY_PHRASE);
   }

   public static String getString(CommandContext<?> var0, String var1) {
      return (String)var0.getArgument(var1, String.class);
   }

   public StringArgumentType.StringType getType() {
      return this.type;
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      if (this.type == StringArgumentType.StringType.GREEDY_PHRASE) {
         String var2 = var1.getRemaining();
         var1.setCursor(var1.getTotalLength());
         return var2;
      } else {
         return this.type == StringArgumentType.StringType.SINGLE_WORD ? var1.readUnquotedString() : var1.readString();
      }
   }

   public String toString() {
      return "string()";
   }

   public Collection<String> getExamples() {
      return this.type.getExamples();
   }

   public static String escapeIfRequired(String var0) {
      char[] var1 = var0.toCharArray();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1[var3];
         if (!StringReader.isAllowedInUnquotedString(var4)) {
            return escape(var0);
         }
      }

      return var0;
   }

   private static String escape(String var0) {
      StringBuilder var1 = new StringBuilder("\"");

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 == '\\' || var3 == '"') {
            var1.append('\\');
         }

         var1.append(var3);
      }

      var1.append("\"");
      return var1.toString();
   }

   public static enum StringType {
      SINGLE_WORD(new String[]{"word", "words_with_underscores"}),
      QUOTABLE_PHRASE(new String[]{"\"quoted phrase\"", "word", "\"\""}),
      GREEDY_PHRASE(new String[]{"word", "words with spaces", "\"and symbols\""});

      private final Collection<String> examples;

      private StringType(String... var3) {
         this.examples = Arrays.asList(var3);
      }

      public Collection<String> getExamples() {
         return this.examples;
      }
   }
}
