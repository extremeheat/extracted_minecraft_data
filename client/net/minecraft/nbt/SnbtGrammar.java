package net.minecraft.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.UnsignedBytes;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.GreedyPatternParseRule;
import net.minecraft.util.parsing.packrat.commands.GreedyPredicateParseRule;
import net.minecraft.util.parsing.packrat.commands.NumberRunParseRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.UnquotedStringParseRule;

public class SnbtGrammar {
   private static final DynamicCommandExceptionType ERROR_NUMBER_PARSE_FAILURE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("snbt.parser.number_parse_failure", var0));
   static final DynamicCommandExceptionType ERROR_EXPECTED_HEX_ESCAPE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("snbt.parser.expected_hex_escape", var0));
   private static final DynamicCommandExceptionType ERROR_INVALID_CODEPOINT = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("snbt.parser.invalid_codepoint", var0));
   private static final DynamicCommandExceptionType ERROR_NO_SUCH_OPERATION = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("snbt.parser.no_such_operation", var0));
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_INTEGER_TYPE = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_integer_type")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_FLOAT_TYPE = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_float_type")));
   static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_NON_NEGATIVE_NUMBER = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_non_negative_number")));
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_CHARACTER_NAME = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_character_name")));
   static final DelayedException<CommandSyntaxException> ERROR_INVALID_ARRAY_ELEMENT_TYPE = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_array_element_type")));
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_UNQUOTED_START = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_unquoted_start")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_UNQUOTED_STRING = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_unquoted_string")));
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_STRING_CONTENTS = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_string_contents")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_BINARY_NUMERAL = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_binary_numeral")));
   private static final DelayedException<CommandSyntaxException> ERROR_UNDESCORE_NOT_ALLOWED = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.underscore_not_allowed")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_DECIMAL_NUMERAL = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_decimal_numeral")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_HEX_NUMERAL = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_hex_numeral")));
   private static final DelayedException<CommandSyntaxException> ERROR_EMPTY_KEY = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.empty_key")));
   private static final DelayedException<CommandSyntaxException> ERROR_LEADING_ZERO_NOT_ALLOWED = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.leading_zero_not_allowed")));
   private static final DelayedException<CommandSyntaxException> ERROR_INFINITY_NOT_ALLOWED = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.infinity_not_allowed")));
   private static final HexFormat HEX_ESCAPE = HexFormat.of().withUpperCase();
   private static final NumberRunParseRule BINARY_NUMERAL;
   private static final NumberRunParseRule DECIMAL_NUMERAL;
   private static final NumberRunParseRule HEX_NUMERAL;
   private static final GreedyPredicateParseRule PLAIN_STRING_CHUNK;
   private static final StringReaderTerms.TerminalCharacters NUMBER_LOOKEAHEAD;
   private static final Pattern UNICODE_NAME;

   public SnbtGrammar() {
      super();
   }

   static DelayedException<CommandSyntaxException> createNumberParseError(NumberFormatException var0) {
      return DelayedException.create(ERROR_NUMBER_PARSE_FAILURE, var0.getMessage());
   }

   @Nullable
   public static String escapeControlCharacters(char var0) {
      String var10000;
      switch (var0) {
         case '\b':
            var10000 = "b";
            break;
         case '\t':
            var10000 = "t";
            break;
         case '\n':
            var10000 = "n";
            break;
         case '\u000b':
         default:
            var10000 = var0 < ' ' ? "x" + HEX_ESCAPE.toHexDigits((byte)var0) : null;
            break;
         case '\f':
            var10000 = "f";
            break;
         case '\r':
            var10000 = "r";
      }

      return var10000;
   }

   private static boolean isAllowedToStartUnquotedString(char var0) {
      return !canStartNumber(var0);
   }

   static boolean canStartNumber(char var0) {
      boolean var10000;
      switch (var0) {
         case '+':
         case '-':
         case '.':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
            var10000 = true;
            break;
         case ',':
         case '/':
         default:
            var10000 = false;
      }

      return var10000;
   }

   static boolean needsUnderscoreRemoval(String var0) {
      return var0.indexOf(95) != -1;
   }

   private static void cleanAndAppend(StringBuilder var0, String var1) {
      cleanAndAppend(var0, var1, needsUnderscoreRemoval(var1));
   }

   static void cleanAndAppend(StringBuilder var0, String var1, boolean var2) {
      if (var2) {
         for(char var6 : var1.toCharArray()) {
            if (var6 != '_') {
               var0.append(var6);
            }
         }
      } else {
         var0.append(var1);
      }

   }

   static short parseUnsignedShort(String var0, int var1) {
      int var2 = Integer.parseInt(var0, var1);
      if (var2 >> 16 == 0) {
         return (short)var2;
      } else {
         throw new NumberFormatException("out of range: " + var2);
      }
   }

   @Nullable
   private static <T> T createFloat(DynamicOps<T> var0, Sign var1, @Nullable String var2, @Nullable String var3, @Nullable Signed<String> var4, @Nullable TypeSuffix var5, ParseState<?> var6) {
      StringBuilder var7 = new StringBuilder();
      var1.append(var7);
      if (var2 != null) {
         cleanAndAppend(var7, var2);
      }

      if (var3 != null) {
         var7.append('.');
         cleanAndAppend(var7, var3);
      }

      if (var4 != null) {
         var7.append('e');
         var4.sign().append(var7);
         cleanAndAppend(var7, (String)var4.value);
      }

      try {
         String var8 = var7.toString();
         byte var10 = 0;
         Object var10000;
         //$FF: var10->value
         //0->FLOAT
         //1->DOUBLE
         switch (var5.enumSwitch<invokedynamic>(var5, var10)) {
            case -1:
               var10000 = convertDouble(var0, var6, var8);
               break;
            case 0:
               var10000 = convertFloat(var0, var6, var8);
               break;
            case 1:
               var10000 = convertDouble(var0, var6, var8);
               break;
            default:
               var6.errorCollector().store(var6.mark(), ERROR_EXPECTED_FLOAT_TYPE);
               var10000 = null;
         }

         return (T)var10000;
      } catch (NumberFormatException var11) {
         var6.errorCollector().store(var6.mark(), createNumberParseError(var11));
         return null;
      }
   }

   @Nullable
   private static <T> T convertFloat(DynamicOps<T> var0, ParseState<?> var1, String var2) {
      float var3 = Float.parseFloat(var2);
      if (!Float.isFinite(var3)) {
         var1.errorCollector().store(var1.mark(), ERROR_INFINITY_NOT_ALLOWED);
         return null;
      } else {
         return (T)var0.createFloat(var3);
      }
   }

   @Nullable
   private static <T> T convertDouble(DynamicOps<T> var0, ParseState<?> var1, String var2) {
      double var3 = Double.parseDouble(var2);
      if (!Double.isFinite(var3)) {
         var1.errorCollector().store(var1.mark(), ERROR_INFINITY_NOT_ALLOWED);
         return null;
      } else {
         return (T)var0.createDouble(var3);
      }
   }

   private static String joinList(List<String> var0) {
      String var10000;
      switch (var0.size()) {
         case 0 -> var10000 = "";
         case 1 -> var10000 = (String)var0.getFirst();
         default -> var10000 = String.join("", var0);
      }

      return var10000;
   }

   public static <T> Grammar<T> createParser(DynamicOps<T> var0) {
      Object var1 = var0.createBoolean(true);
      Object var2 = var0.createBoolean(false);
      Object var3 = var0.emptyMap();
      Object var4 = var0.emptyList();
      Dictionary var5 = new Dictionary();
      Atom var6 = Atom.of("sign");
      var5.put(var6, Term.alternative(Term.sequence(StringReaderTerms.character('+'), Term.marker(var6, SnbtGrammar.Sign.PLUS)), Term.sequence(StringReaderTerms.character('-'), Term.marker(var6, SnbtGrammar.Sign.MINUS))), (var1x) -> (Sign)var1x.getOrThrow(var6));
      Atom var7 = Atom.of("integer_suffix");
      var5.put(var7, Term.alternative(Term.sequence(StringReaderTerms.characters('u', 'U'), Term.alternative(Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.BYTE))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.SHORT))), Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.INT))), Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.LONG))))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.alternative(Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.BYTE))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.SHORT))), Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.INT))), Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker(var7, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.LONG))))), Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker(var7, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.BYTE))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker(var7, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.SHORT))), Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker(var7, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.INT))), Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker(var7, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.LONG)))), (var1x) -> (IntegerSuffix)var1x.getOrThrow(var7));
      Atom var8 = Atom.of("binary_numeral");
      var5.put(var8, BINARY_NUMERAL);
      Atom var9 = Atom.of("decimal_numeral");
      var5.put(var9, DECIMAL_NUMERAL);
      Atom var10 = Atom.of("hex_numeral");
      var5.put(var10, HEX_NUMERAL);
      Atom var11 = Atom.of("integer_literal");
      NamedRule var12 = var5.put(var11, Term.sequence(Term.optional(var5.named(var6)), Term.alternative(Term.sequence(StringReaderTerms.character('0'), Term.cut(), Term.alternative(Term.sequence(StringReaderTerms.characters('x', 'X'), Term.cut(), var5.named(var10)), Term.sequence(StringReaderTerms.characters('b', 'B'), var5.named(var8)), Term.sequence(var5.named(var9), Term.cut(), Term.fail(ERROR_LEADING_ZERO_NOT_ALLOWED)), Term.marker(var9, "0"))), var5.named(var9)), Term.optional(var5.named(var7))), (var5x) -> {
         IntegerSuffix var6x = (IntegerSuffix)var5x.getOrDefault(var7, SnbtGrammar.IntegerSuffix.EMPTY);
         Sign var7x = (Sign)var5x.getOrDefault(var6, SnbtGrammar.Sign.PLUS);
         String var8x = (String)var5x.get(var9);
         if (var8x != null) {
            return new IntegerLiteral(var7x, SnbtGrammar.Base.DECIMAL, var8x, var6x);
         } else {
            String var9x = (String)var5x.get(var10);
            if (var9x != null) {
               return new IntegerLiteral(var7x, SnbtGrammar.Base.HEX, var9x, var6x);
            } else {
               String var10x = (String)var5x.getOrThrow(var8);
               return new IntegerLiteral(var7x, SnbtGrammar.Base.BINARY, var10x, var6x);
            }
         }
      });
      Atom var13 = Atom.of("float_type_suffix");
      var5.put(var13, Term.alternative(Term.sequence(StringReaderTerms.characters('f', 'F'), Term.marker(var13, SnbtGrammar.TypeSuffix.FLOAT)), Term.sequence(StringReaderTerms.characters('d', 'D'), Term.marker(var13, SnbtGrammar.TypeSuffix.DOUBLE))), (var1x) -> (TypeSuffix)var1x.getOrThrow(var13));
      Atom var14 = Atom.of("float_exponent_part");
      var5.put(var14, Term.sequence(StringReaderTerms.characters('e', 'E'), Term.optional(var5.named(var6)), var5.named(var9)), (var2x) -> new Signed((Sign)var2x.getOrDefault(var6, SnbtGrammar.Sign.PLUS), (String)var2x.getOrThrow(var9)));
      Atom var15 = Atom.of("float_whole_part");
      Atom var16 = Atom.of("float_fraction_part");
      Atom var17 = Atom.of("float_literal");
      var5.putComplex(var17, Term.sequence(Term.optional(var5.named(var6)), Term.alternative(Term.sequence(var5.namedWithAlias(var9, var15), StringReaderTerms.character('.'), Term.cut(), Term.optional(var5.namedWithAlias(var9, var16)), Term.optional(var5.named(var14)), Term.optional(var5.named(var13))), Term.sequence(StringReaderTerms.character('.'), Term.cut(), var5.namedWithAlias(var9, var16), Term.optional(var5.named(var14)), Term.optional(var5.named(var13))), Term.sequence(var5.namedWithAlias(var9, var15), var5.named(var14), Term.cut(), Term.optional(var5.named(var13))), Term.sequence(var5.namedWithAlias(var9, var15), Term.optional(var5.named(var14)), var5.named(var13)))), (var6x) -> {
         Scope var7 = var6x.scope();
         Sign var8 = (Sign)var7.getOrDefault(var6, SnbtGrammar.Sign.PLUS);
         String var9 = (String)var7.get(var15);
         String var10 = (String)var7.get(var16);
         Signed var11 = (Signed)var7.get(var14);
         TypeSuffix var12 = (TypeSuffix)var7.get(var13);
         return createFloat(var0, var8, var9, var10, var11, var12, var6x);
      });
      Atom var18 = Atom.of("string_hex_2");
      var5.put(var18, new SimpleHexLiteralParseRule(2));
      Atom var19 = Atom.of("string_hex_4");
      var5.put(var19, new SimpleHexLiteralParseRule(4));
      Atom var20 = Atom.of("string_hex_8");
      var5.put(var20, new SimpleHexLiteralParseRule(8));
      Atom var21 = Atom.of("string_unicode_name");
      var5.put(var21, new GreedyPatternParseRule(UNICODE_NAME, ERROR_INVALID_CHARACTER_NAME));
      Atom var22 = Atom.of("string_escape_sequence");
      var5.putComplex(var22, Term.alternative(Term.sequence(StringReaderTerms.character('b'), Term.marker(var22, "\b")), Term.sequence(StringReaderTerms.character('s'), Term.marker(var22, " ")), Term.sequence(StringReaderTerms.character('t'), Term.marker(var22, "\t")), Term.sequence(StringReaderTerms.character('n'), Term.marker(var22, "\n")), Term.sequence(StringReaderTerms.character('f'), Term.marker(var22, "\f")), Term.sequence(StringReaderTerms.character('r'), Term.marker(var22, "\r")), Term.sequence(StringReaderTerms.character('\\'), Term.marker(var22, "\\")), Term.sequence(StringReaderTerms.character('\''), Term.marker(var22, "'")), Term.sequence(StringReaderTerms.character('"'), Term.marker(var22, "\"")), Term.sequence(StringReaderTerms.character('x'), var5.named(var18)), Term.sequence(StringReaderTerms.character('u'), var5.named(var19)), Term.sequence(StringReaderTerms.character('U'), var5.named(var20)), Term.sequence(StringReaderTerms.character('N'), StringReaderTerms.character('{'), var5.named(var21), StringReaderTerms.character('}'))), (var5x) -> {
         Scope var6 = var5x.scope();
         String var7 = (String)var6.getAny(var22);
         if (var7 != null) {
            return var7;
         } else {
            String var8 = (String)var6.getAny(var18, var19, var20);
            if (var8 != null) {
               int var13 = HexFormat.fromHexDigits(var8);
               if (!Character.isValidCodePoint(var13)) {
                  var5x.errorCollector().store(var5x.mark(), DelayedException.create(ERROR_INVALID_CODEPOINT, String.format(Locale.ROOT, "U+%08X", var13)));
                  return null;
               } else {
                  return Character.toString(var13);
               }
            } else {
               String var9 = (String)var6.getOrThrow(var21);

               int var10;
               try {
                  var10 = Character.codePointOf(var9);
               } catch (IllegalArgumentException var12) {
                  var5x.errorCollector().store(var5x.mark(), ERROR_INVALID_CHARACTER_NAME);
                  return null;
               }

               return Character.toString(var10);
            }
         }
      });
      Atom var23 = Atom.of("string_plain_contents");
      var5.put(var23, PLAIN_STRING_CHUNK);
      Atom var24 = Atom.of("string_chunks");
      Atom var25 = Atom.of("string_contents");
      Atom var26 = Atom.of("single_quoted_string_chunk");
      NamedRule var27 = var5.put(var26, Term.alternative(var5.namedWithAlias(var23, var25), Term.sequence(StringReaderTerms.character('\\'), var5.namedWithAlias(var22, var25)), Term.sequence(StringReaderTerms.character('"'), Term.marker(var25, "\""))), (var1x) -> (String)var1x.getOrThrow(var25));
      Atom var28 = Atom.of("single_quoted_string_contents");
      var5.put(var28, Term.repeated(var27, var24), (var1x) -> joinList((List)var1x.getOrThrow(var24)));
      Atom var29 = Atom.of("double_quoted_string_chunk");
      NamedRule var30 = var5.put(var29, Term.alternative(var5.namedWithAlias(var23, var25), Term.sequence(StringReaderTerms.character('\\'), var5.namedWithAlias(var22, var25)), Term.sequence(StringReaderTerms.character('\''), Term.marker(var25, "'"))), (var1x) -> (String)var1x.getOrThrow(var25));
      Atom var31 = Atom.of("double_quoted_string_contents");
      var5.put(var31, Term.repeated(var30, var24), (var1x) -> joinList((List)var1x.getOrThrow(var24)));
      Atom var32 = Atom.of("quoted_string_literal");
      var5.put(var32, Term.alternative(Term.sequence(StringReaderTerms.character('"'), Term.cut(), Term.optional(var5.namedWithAlias(var31, var25)), StringReaderTerms.character('"')), Term.sequence(StringReaderTerms.character('\''), Term.optional(var5.namedWithAlias(var28, var25)), StringReaderTerms.character('\''))), (var1x) -> (String)var1x.getOrThrow(var25));
      Atom var33 = Atom.of("unquoted_string");
      var5.put(var33, new UnquotedStringParseRule(1, ERROR_EXPECTED_UNQUOTED_STRING));
      Atom var34 = Atom.of("literal");
      Atom var35 = Atom.of("arguments");
      var5.put(var35, Term.repeatedWithTrailingSeparator(var5.forward(var34), var35, StringReaderTerms.character(',')), (var1x) -> (List)var1x.getOrThrow(var35));
      Atom var36 = Atom.of("unquoted_string_or_builtin");
      var5.putComplex(var36, Term.sequence(var5.named(var33), Term.optional(Term.sequence(StringReaderTerms.character('('), var5.named(var35), StringReaderTerms.character(')')))), (var5x) -> {
         Scope var6 = var5x.scope();
         String var7 = (String)var6.getOrThrow(var33);
         if (!var7.isEmpty() && isAllowedToStartUnquotedString(var7.charAt(0))) {
            List var8 = (List)var6.get(var35);
            if (var8 != null) {
               SnbtOperations.BuiltinKey var9 = new SnbtOperations.BuiltinKey(var7, var8.size());
               SnbtOperations.BuiltinOperation var10 = (SnbtOperations.BuiltinOperation)SnbtOperations.BUILTIN_OPERATIONS.get(var9);
               if (var10 != null) {
                  return var10.run(var0, var8, var5x);
               } else {
                  var5x.errorCollector().store(var5x.mark(), DelayedException.create(ERROR_NO_SUCH_OPERATION, var9.toString()));
                  return null;
               }
            } else if (var7.equalsIgnoreCase("true")) {
               return var1;
            } else {
               return var7.equalsIgnoreCase("false") ? var2 : var0.createString(var7);
            }
         } else {
            var5x.errorCollector().store(var5x.mark(), SnbtOperations.BUILTIN_IDS, ERROR_INVALID_UNQUOTED_START);
            return null;
         }
      });
      Atom var37 = Atom.of("map_key");
      var5.put(var37, Term.alternative(var5.named(var32), var5.named(var33)), (var2x) -> (String)var2x.getAnyOrThrow(var32, var33));
      Atom var38 = Atom.of("map_entry");
      NamedRule var39 = var5.putComplex(var38, Term.sequence(var5.named(var37), StringReaderTerms.character(':'), var5.named(var34)), (var2x) -> {
         Scope var3 = var2x.scope();
         String var4 = (String)var3.getOrThrow(var37);
         if (var4.isEmpty()) {
            var2x.errorCollector().store(var2x.mark(), ERROR_EMPTY_KEY);
            return null;
         } else {
            Object var5 = var3.getOrThrow(var34);
            return Map.entry(var4, var5);
         }
      });
      Atom var40 = Atom.of("map_entries");
      var5.put(var40, Term.repeatedWithTrailingSeparator(var39, var40, StringReaderTerms.character(',')), (var1x) -> (List)var1x.getOrThrow(var40));
      Atom var41 = Atom.of("map_literal");
      var5.put(var41, Term.sequence(StringReaderTerms.character('{'), var5.named(var40), StringReaderTerms.character('}')), (var3x) -> {
         List var4 = (List)var3x.getOrThrow(var40);
         if (var4.isEmpty()) {
            return var3;
         } else {
            ImmutableMap.Builder var5 = ImmutableMap.builderWithExpectedSize(var4.size());

            for(Map.Entry var7 : var4) {
               var5.put(var0.createString((String)var7.getKey()), var7.getValue());
            }

            return var0.createMap(var5.buildKeepingLast());
         }
      });
      Atom var42 = Atom.of("list_entries");
      var5.put(var42, Term.repeatedWithTrailingSeparator(var5.forward(var34), var42, StringReaderTerms.character(',')), (var1x) -> (List)var1x.getOrThrow(var42));
      Atom var43 = Atom.of("array_prefix");
      var5.put(var43, Term.alternative(Term.sequence(StringReaderTerms.character('B'), Term.marker(var43, SnbtGrammar.ArrayPrefix.BYTE)), Term.sequence(StringReaderTerms.character('L'), Term.marker(var43, SnbtGrammar.ArrayPrefix.LONG)), Term.sequence(StringReaderTerms.character('I'), Term.marker(var43, SnbtGrammar.ArrayPrefix.INT))), (var1x) -> (ArrayPrefix)var1x.getOrThrow(var43));
      Atom var44 = Atom.of("int_array_entries");
      var5.put(var44, Term.repeatedWithTrailingSeparator(var12, var44, StringReaderTerms.character(',')), (var1x) -> (List)var1x.getOrThrow(var44));
      Atom var45 = Atom.of("list_literal");
      var5.putComplex(var45, Term.sequence(StringReaderTerms.character('['), Term.alternative(Term.sequence(var5.named(var43), StringReaderTerms.character(';'), var5.named(var44)), var5.named(var42)), StringReaderTerms.character(']')), (var5x) -> {
         Scope var6 = var5x.scope();
         ArrayPrefix var7 = (ArrayPrefix)var6.get(var43);
         if (var7 != null) {
            List var9 = (List)var6.getOrThrow(var44);
            return var9.isEmpty() ? var7.create(var0) : var7.create(var0, var9, var5x);
         } else {
            List var8 = (List)var6.getOrThrow(var42);
            return var8.isEmpty() ? var4 : var0.createList(var8.stream());
         }
      });
      NamedRule var46 = var5.putComplex(var34, Term.alternative(Term.sequence(Term.positiveLookahead(NUMBER_LOOKEAHEAD), Term.alternative(var5.namedWithAlias(var17, var34), var5.named(var11))), Term.sequence(Term.positiveLookahead(StringReaderTerms.characters('"', '\'')), Term.cut(), var5.named(var32)), Term.sequence(Term.positiveLookahead(StringReaderTerms.character('{')), Term.cut(), var5.namedWithAlias(var41, var34)), Term.sequence(Term.positiveLookahead(StringReaderTerms.character('[')), Term.cut(), var5.namedWithAlias(var45, var34)), var5.namedWithAlias(var36, var34)), (var4x) -> {
         Scope var5 = var4x.scope();
         String var6 = (String)var5.get(var32);
         if (var6 != null) {
            return var0.createString(var6);
         } else {
            IntegerLiteral var7 = (IntegerLiteral)var5.get(var11);
            return var7 != null ? var7.create(var0, var4x) : var5.getOrThrow(var34);
         }
      });
      return new Grammar<T>(var5, var46);
   }

   static {
      BINARY_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_BINARY_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
         protected boolean isAccepted(char var1) {
            boolean var10000;
            switch (var1) {
               case '0':
               case '1':
               case '_':
                  var10000 = true;
                  break;
               default:
                  var10000 = false;
            }

            return var10000;
         }
      };
      DECIMAL_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_DECIMAL_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
         protected boolean isAccepted(char var1) {
            boolean var10000;
            switch (var1) {
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case '_':
                  var10000 = true;
                  break;
               default:
                  var10000 = false;
            }

            return var10000;
         }
      };
      HEX_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_HEX_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
         protected boolean isAccepted(char var1) {
            boolean var10000;
            switch (var1) {
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case 'A':
               case 'B':
               case 'C':
               case 'D':
               case 'E':
               case 'F':
               case '_':
               case 'a':
               case 'b':
               case 'c':
               case 'd':
               case 'e':
               case 'f':
                  var10000 = true;
                  break;
               case ':':
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case 'G':
               case 'H':
               case 'I':
               case 'J':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'S':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               case 'Z':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '`':
               default:
                  var10000 = false;
            }

            return var10000;
         }
      };
      PLAIN_STRING_CHUNK = new GreedyPredicateParseRule(1, ERROR_INVALID_STRING_CONTENTS) {
         protected boolean isAccepted(char var1) {
            boolean var10000;
            switch (var1) {
               case '"':
               case '\'':
               case '\\':
                  var10000 = false;
                  break;
               default:
                  var10000 = true;
            }

            return var10000;
         }
      };
      NUMBER_LOOKEAHEAD = new StringReaderTerms.TerminalCharacters(CharList.of()) {
         protected boolean isAccepted(char var1) {
            return SnbtGrammar.canStartNumber(var1);
         }
      };
      UNICODE_NAME = Pattern.compile("[-a-zA-Z0-9 ]+");
   }

   static enum Sign {
      PLUS,
      MINUS;

      private Sign() {
      }

      public void append(StringBuilder var1) {
         if (this == MINUS) {
            var1.append("-");
         }

      }

      // $FF: synthetic method
      private static Sign[] $values() {
         return new Sign[]{PLUS, MINUS};
      }
   }

   static enum Base {
      BINARY,
      DECIMAL,
      HEX;

      private Base() {
      }

      // $FF: synthetic method
      private static Base[] $values() {
         return new Base[]{BINARY, DECIMAL, HEX};
      }
   }

   static enum TypeSuffix {
      FLOAT,
      DOUBLE,
      BYTE,
      SHORT,
      INT,
      LONG;

      private TypeSuffix() {
      }

      // $FF: synthetic method
      private static TypeSuffix[] $values() {
         return new TypeSuffix[]{FLOAT, DOUBLE, BYTE, SHORT, INT, LONG};
      }
   }

   static enum SignedPrefix {
      SIGNED,
      UNSIGNED;

      private SignedPrefix() {
      }

      // $FF: synthetic method
      private static SignedPrefix[] $values() {
         return new SignedPrefix[]{SIGNED, UNSIGNED};
      }
   }

   static record IntegerSuffix(@Nullable SignedPrefix signed, @Nullable TypeSuffix type) {
      @Nullable
      final SignedPrefix signed;
      @Nullable
      final TypeSuffix type;
      public static final IntegerSuffix EMPTY = new IntegerSuffix((SignedPrefix)null, (TypeSuffix)null);

      IntegerSuffix(@Nullable SignedPrefix var1, @Nullable TypeSuffix var2) {
         super();
         this.signed = var1;
         this.type = var2;
      }
   }

   static enum ArrayPrefix {
      BYTE(SnbtGrammar.TypeSuffix.BYTE, new TypeSuffix[0]) {
         private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

         public <T> T create(DynamicOps<T> var1) {
            return (T)var1.createByteList(EMPTY_BUFFER);
         }

         @Nullable
         public <T> T create(DynamicOps<T> var1, List<IntegerLiteral> var2, ParseState<?> var3) {
            ByteArrayList var4 = new ByteArrayList();

            for(IntegerLiteral var6 : var2) {
               Number var7 = this.buildNumber(var6, var3);
               if (var7 == null) {
                  return null;
               }

               var4.add(var7.byteValue());
            }

            return (T)var1.createByteList(ByteBuffer.wrap(var4.toByteArray()));
         }
      },
      INT(SnbtGrammar.TypeSuffix.INT, new TypeSuffix[]{SnbtGrammar.TypeSuffix.BYTE, SnbtGrammar.TypeSuffix.SHORT}) {
         public <T> T create(DynamicOps<T> var1) {
            return (T)var1.createIntList(IntStream.empty());
         }

         @Nullable
         public <T> T create(DynamicOps<T> var1, List<IntegerLiteral> var2, ParseState<?> var3) {
            IntStream.Builder var4 = IntStream.builder();

            for(IntegerLiteral var6 : var2) {
               Number var7 = this.buildNumber(var6, var3);
               if (var7 == null) {
                  return null;
               }

               var4.add(var7.intValue());
            }

            return (T)var1.createIntList(var4.build());
         }
      },
      LONG(SnbtGrammar.TypeSuffix.LONG, new TypeSuffix[]{SnbtGrammar.TypeSuffix.BYTE, SnbtGrammar.TypeSuffix.SHORT, SnbtGrammar.TypeSuffix.INT}) {
         public <T> T create(DynamicOps<T> var1) {
            return (T)var1.createLongList(LongStream.empty());
         }

         @Nullable
         public <T> T create(DynamicOps<T> var1, List<IntegerLiteral> var2, ParseState<?> var3) {
            LongStream.Builder var4 = LongStream.builder();

            for(IntegerLiteral var6 : var2) {
               Number var7 = this.buildNumber(var6, var3);
               if (var7 == null) {
                  return null;
               }

               var4.add(var7.longValue());
            }

            return (T)var1.createLongList(var4.build());
         }
      };

      private final TypeSuffix defaultType;
      private final Set<TypeSuffix> additionalTypes;

      ArrayPrefix(final TypeSuffix var3, final TypeSuffix... var4) {
         this.additionalTypes = Set.of(var4);
         this.defaultType = var3;
      }

      public boolean isAllowed(TypeSuffix var1) {
         return var1 == this.defaultType || this.additionalTypes.contains(var1);
      }

      public abstract <T> T create(DynamicOps<T> var1);

      @Nullable
      public abstract <T> T create(DynamicOps<T> var1, List<IntegerLiteral> var2, ParseState<?> var3);

      @Nullable
      protected Number buildNumber(IntegerLiteral var1, ParseState<?> var2) {
         TypeSuffix var3 = this.computeType(var1.suffix);
         if (var3 == null) {
            var2.errorCollector().store(var2.mark(), SnbtGrammar.ERROR_INVALID_ARRAY_ELEMENT_TYPE);
            return null;
         } else {
            return (Number)var1.create(JavaOps.INSTANCE, var3, var2);
         }
      }

      @Nullable
      private TypeSuffix computeType(IntegerSuffix var1) {
         TypeSuffix var2 = var1.type();
         if (var2 == null) {
            return this.defaultType;
         } else {
            return !this.isAllowed(var2) ? null : var2;
         }
      }

      // $FF: synthetic method
      private static ArrayPrefix[] $values() {
         return new ArrayPrefix[]{BYTE, INT, LONG};
      }
   }

   static class SimpleHexLiteralParseRule extends GreedyPredicateParseRule {
      public SimpleHexLiteralParseRule(int var1) {
         super(var1, var1, DelayedException.create(SnbtGrammar.ERROR_EXPECTED_HEX_ESCAPE, String.valueOf(var1)));
      }

      protected boolean isAccepted(char var1) {
         boolean var10000;
         switch (var1) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
               var10000 = true;
               break;
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            default:
               var10000 = false;
         }

         return var10000;
      }
   }

   static record IntegerLiteral(Sign sign, Base base, String digits, IntegerSuffix suffix) {
      final IntegerSuffix suffix;

      IntegerLiteral(Sign var1, Base var2, String var3, IntegerSuffix var4) {
         super();
         this.sign = var1;
         this.base = var2;
         this.digits = var3;
         this.suffix = var4;
      }

      private SignedPrefix signedOrDefault() {
         if (this.suffix.signed != null) {
            return this.suffix.signed;
         } else {
            SignedPrefix var10000;
            switch (this.base.ordinal()) {
               case 0:
               case 2:
                  var10000 = SnbtGrammar.SignedPrefix.UNSIGNED;
                  break;
               case 1:
                  var10000 = SnbtGrammar.SignedPrefix.SIGNED;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         }
      }

      private String cleanupDigits(Sign var1) {
         boolean var2 = SnbtGrammar.needsUnderscoreRemoval(this.digits);
         if (var1 != SnbtGrammar.Sign.MINUS && !var2) {
            return this.digits;
         } else {
            StringBuilder var3 = new StringBuilder();
            var1.append(var3);
            SnbtGrammar.cleanAndAppend(var3, this.digits, var2);
            return var3.toString();
         }
      }

      @Nullable
      public <T> T create(DynamicOps<T> var1, ParseState<?> var2) {
         return (T)this.create(var1, (TypeSuffix)Objects.requireNonNullElse(this.suffix.type, SnbtGrammar.TypeSuffix.INT), var2);
      }

      @Nullable
      public <T> T create(DynamicOps<T> var1, TypeSuffix var2, ParseState<?> var3) {
         boolean var4 = this.signedOrDefault() == SnbtGrammar.SignedPrefix.SIGNED;
         if (!var4 && this.sign == SnbtGrammar.Sign.MINUS) {
            var3.errorCollector().store(var3.mark(), SnbtGrammar.ERROR_EXPECTED_NON_NEGATIVE_NUMBER);
            return null;
         } else {
            String var5 = this.cleanupDigits(this.sign);
            byte var10000;
            switch (this.base.ordinal()) {
               case 0 -> var10000 = 2;
               case 1 -> var10000 = 10;
               case 2 -> var10000 = 16;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            byte var6 = var10000;

            try {
               if (var4) {
                  Object var10;
                  switch (var2.ordinal()) {
                     case 2:
                        var10 = var1.createByte(Byte.parseByte(var5, var6));
                        break;
                     case 3:
                        var10 = var1.createShort(Short.parseShort(var5, var6));
                        break;
                     case 4:
                        var10 = var1.createInt(Integer.parseInt(var5, var6));
                        break;
                     case 5:
                        var10 = var1.createLong(Long.parseLong(var5, var6));
                        break;
                     default:
                        var3.errorCollector().store(var3.mark(), SnbtGrammar.ERROR_EXPECTED_INTEGER_TYPE);
                        var10 = null;
                  }

                  return (T)var10;
               } else {
                  Object var9;
                  switch (var2.ordinal()) {
                     case 2:
                        var9 = var1.createByte(UnsignedBytes.parseUnsignedByte(var5, var6));
                        break;
                     case 3:
                        var9 = var1.createShort(SnbtGrammar.parseUnsignedShort(var5, var6));
                        break;
                     case 4:
                        var9 = var1.createInt(Integer.parseUnsignedInt(var5, var6));
                        break;
                     case 5:
                        var9 = var1.createLong(Long.parseUnsignedLong(var5, var6));
                        break;
                     default:
                        var3.errorCollector().store(var3.mark(), SnbtGrammar.ERROR_EXPECTED_INTEGER_TYPE);
                        var9 = null;
                  }

                  return (T)var9;
               }
            } catch (NumberFormatException var8) {
               var3.errorCollector().store(var3.mark(), SnbtGrammar.createNumberParseError(var8));
               return null;
            }
         }
      }
   }

   static record Signed<T>(Sign sign, T value) {
      final T value;

      Signed(Sign var1, T var2) {
         super();
         this.sign = var1;
         this.value = var2;
      }
   }
}
