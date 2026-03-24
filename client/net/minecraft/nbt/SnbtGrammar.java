package net.minecraft.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.UnsignedBytes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
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
import org.jspecify.annotations.Nullable;

public class SnbtGrammar {
   private static final DynamicCommandExceptionType ERROR_NUMBER_PARSE_FAILURE = new DynamicCommandExceptionType((message) -> Component.translatableEscape("snbt.parser.number_parse_failure", message));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_HEX_ESCAPE = new DynamicCommandExceptionType((length) -> Component.translatableEscape("snbt.parser.expected_hex_escape", length));
   private static final DynamicCommandExceptionType ERROR_INVALID_CODEPOINT = new DynamicCommandExceptionType((codepoint) -> Component.translatableEscape("snbt.parser.invalid_codepoint", codepoint));
   private static final DynamicCommandExceptionType ERROR_NO_SUCH_OPERATION = new DynamicCommandExceptionType((operation) -> Component.translatableEscape("snbt.parser.no_such_operation", operation));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_INTEGER_TYPE = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_integer_type")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_FLOAT_TYPE = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_float_type")));
   private static final DelayedException<CommandSyntaxException> ERROR_EXPECTED_NON_NEGATIVE_NUMBER = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.expected_non_negative_number")));
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_CHARACTER_NAME = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_character_name")));
   private static final DelayedException<CommandSyntaxException> ERROR_INVALID_ARRAY_ELEMENT_TYPE = DelayedException.create(new SimpleCommandExceptionType(Component.translatable("snbt.parser.invalid_array_element_type")));
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

   private static DelayedException<CommandSyntaxException> createNumberParseError(final NumberFormatException ex) {
      return DelayedException.create(ERROR_NUMBER_PARSE_FAILURE, ex.getMessage());
   }

   public static @Nullable String escapeControlCharacters(final char c) {
      String var10000;
      switch (c) {
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
            var10000 = c < ' ' ? "x" + HEX_ESCAPE.toHexDigits((byte)c) : null;
            break;
         case '\f':
            var10000 = "f";
            break;
         case '\r':
            var10000 = "r";
      }

      return var10000;
   }

   private static boolean isAllowedToStartUnquotedString(final char c) {
      return !canStartNumber(c);
   }

   private static boolean canStartNumber(final char c) {
      boolean var10000;
      switch (c) {
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

   private static boolean needsUnderscoreRemoval(final String contents) {
      return contents.indexOf(95) != -1;
   }

   private static void cleanAndAppend(final StringBuilder output, final String contents) {
      cleanAndAppend(output, contents, needsUnderscoreRemoval(contents));
   }

   private static void cleanAndAppend(final StringBuilder output, final String contents, final boolean needsUnderscoreRemoval) {
      if (needsUnderscoreRemoval) {
         for(char c : contents.toCharArray()) {
            if (c != '_') {
               output.append(c);
            }
         }
      } else {
         output.append(contents);
      }

   }

   private static short parseUnsignedShort(final String string, final int radix) {
      int parse = Integer.parseInt(string, radix);
      if (parse >> 16 == 0) {
         return (short)parse;
      } else {
         throw new NumberFormatException("out of range: " + parse);
      }
   }

   private static <T> @Nullable T createFloat(final DynamicOps<T> ops, final Sign sign, final @Nullable String whole, final @Nullable String fraction, final @Nullable Signed<String> exponent, final @Nullable TypeSuffix typeSuffix, final ParseState<?> state) {
      StringBuilder result = new StringBuilder();
      sign.append(result);
      if (whole != null) {
         cleanAndAppend(result, whole);
      }

      if (fraction != null) {
         result.append('.');
         cleanAndAppend(result, fraction);
      }

      if (exponent != null) {
         result.append('e');
         exponent.sign().append(result);
         cleanAndAppend(result, (String)exponent.value);
      }

      try {
         String contents = result.toString();
         byte var10 = 0;
         Object var10000;
         //$FF: var10->value
         //0->FLOAT
         //1->DOUBLE
         switch (typeSuffix.enumSwitch<invokedynamic>(typeSuffix, var10)) {
            case -1:
               var10000 = convertDouble(ops, state, contents);
               break;
            case 0:
               var10000 = convertFloat(ops, state, contents);
               break;
            case 1:
               var10000 = convertDouble(ops, state, contents);
               break;
            default:
               state.errorCollector().store(state.mark(), ERROR_EXPECTED_FLOAT_TYPE);
               var10000 = null;
         }

         return (T)var10000;
      } catch (NumberFormatException e) {
         state.errorCollector().store(state.mark(), createNumberParseError(e));
         return null;
      }
   }

   private static <T> @Nullable T convertFloat(final DynamicOps<T> ops, final ParseState<?> state, final String contents) {
      float value = Float.parseFloat(contents);
      if (!Float.isFinite(value)) {
         state.errorCollector().store(state.mark(), ERROR_INFINITY_NOT_ALLOWED);
         return null;
      } else {
         return (T)ops.createFloat(value);
      }
   }

   private static <T> @Nullable T convertDouble(final DynamicOps<T> ops, final ParseState<?> state, final String contents) {
      double value = Double.parseDouble(contents);
      if (!Double.isFinite(value)) {
         state.errorCollector().store(state.mark(), ERROR_INFINITY_NOT_ALLOWED);
         return null;
      } else {
         return (T)ops.createDouble(value);
      }
   }

   private static String joinList(final List<String> list) {
      String var10000;
      switch (list.size()) {
         case 0 -> var10000 = "";
         case 1 -> var10000 = (String)list.getFirst();
         default -> var10000 = String.join("", list);
      }

      return var10000;
   }

   public static <T> Grammar<T> createParser(final DynamicOps<T> ops) {
      T trueValue = (T)ops.createBoolean(true);
      T falseValue = (T)ops.createBoolean(false);
      T emptyMapValue = (T)ops.emptyMap();
      T emptyList = (T)ops.emptyList();
      Dictionary<StringReader> rules = new Dictionary<StringReader>();
      Atom<Sign> sign = Atom.<Sign>of("sign");
      rules.put(sign, Term.alternative(Term.sequence(StringReaderTerms.character('+'), Term.marker(sign, SnbtGrammar.Sign.PLUS)), Term.sequence(StringReaderTerms.character('-'), Term.marker(sign, SnbtGrammar.Sign.MINUS))), (scope) -> (Sign)scope.getOrThrow(sign));
      Atom<IntegerSuffix> integerSuffix = Atom.<IntegerSuffix>of("integer_suffix");
      rules.put(integerSuffix, Term.alternative(Term.sequence(StringReaderTerms.characters('u', 'U'), Term.alternative(Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.BYTE))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.SHORT))), Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.INT))), Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.UNSIGNED, SnbtGrammar.TypeSuffix.LONG))))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.alternative(Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.BYTE))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.SHORT))), Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.INT))), Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker(integerSuffix, new IntegerSuffix(SnbtGrammar.SignedPrefix.SIGNED, SnbtGrammar.TypeSuffix.LONG))))), Term.sequence(StringReaderTerms.characters('b', 'B'), Term.marker(integerSuffix, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.BYTE))), Term.sequence(StringReaderTerms.characters('s', 'S'), Term.marker(integerSuffix, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.SHORT))), Term.sequence(StringReaderTerms.characters('i', 'I'), Term.marker(integerSuffix, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.INT))), Term.sequence(StringReaderTerms.characters('l', 'L'), Term.marker(integerSuffix, new IntegerSuffix((SignedPrefix)null, SnbtGrammar.TypeSuffix.LONG)))), (scope) -> (IntegerSuffix)scope.getOrThrow(integerSuffix));
      Atom<String> binaryNumeral = Atom.<String>of("binary_numeral");
      rules.put(binaryNumeral, BINARY_NUMERAL);
      Atom<String> decimalNumeral = Atom.<String>of("decimal_numeral");
      rules.put(decimalNumeral, DECIMAL_NUMERAL);
      Atom<String> hexNumeral = Atom.<String>of("hex_numeral");
      rules.put(hexNumeral, HEX_NUMERAL);
      Atom<IntegerLiteral> integerLiteral = Atom.<IntegerLiteral>of("integer_literal");
      NamedRule<StringReader, IntegerLiteral> integerLiteralRule = rules.put(integerLiteral, Term.sequence(Term.optional(rules.named(sign)), Term.alternative(Term.sequence(StringReaderTerms.character('0'), Term.cut(), Term.alternative(Term.sequence(StringReaderTerms.characters('x', 'X'), Term.cut(), rules.named(hexNumeral)), Term.sequence(StringReaderTerms.characters('b', 'B'), rules.named(binaryNumeral)), Term.sequence(rules.named(decimalNumeral), Term.cut(), Term.fail(ERROR_LEADING_ZERO_NOT_ALLOWED)), Term.marker(decimalNumeral, "0"))), rules.named(decimalNumeral)), Term.optional(rules.named(integerSuffix))), (scope) -> {
         IntegerSuffix suffix = (IntegerSuffix)scope.getOrDefault(integerSuffix, SnbtGrammar.IntegerSuffix.EMPTY);
         Sign signValue = (Sign)scope.getOrDefault(sign, SnbtGrammar.Sign.PLUS);
         String decimalContents = (String)scope.get(decimalNumeral);
         if (decimalContents != null) {
            return new IntegerLiteral(signValue, SnbtGrammar.Base.DECIMAL, decimalContents, suffix);
         } else {
            String hexContents = (String)scope.get(hexNumeral);
            if (hexContents != null) {
               return new IntegerLiteral(signValue, SnbtGrammar.Base.HEX, hexContents, suffix);
            } else {
               String binaryContents = (String)scope.getOrThrow(binaryNumeral);
               return new IntegerLiteral(signValue, SnbtGrammar.Base.BINARY, binaryContents, suffix);
            }
         }
      });
      Atom<TypeSuffix> floatTypeSuffix = Atom.<TypeSuffix>of("float_type_suffix");
      rules.put(floatTypeSuffix, Term.alternative(Term.sequence(StringReaderTerms.characters('f', 'F'), Term.marker(floatTypeSuffix, SnbtGrammar.TypeSuffix.FLOAT)), Term.sequence(StringReaderTerms.characters('d', 'D'), Term.marker(floatTypeSuffix, SnbtGrammar.TypeSuffix.DOUBLE))), (scope) -> (TypeSuffix)scope.getOrThrow(floatTypeSuffix));
      Atom<Signed<String>> floatExponentPart = Atom.<Signed<String>>of("float_exponent_part");
      rules.put(floatExponentPart, Term.sequence(StringReaderTerms.characters('e', 'E'), Term.optional(rules.named(sign)), rules.named(decimalNumeral)), (scope) -> new Signed((Sign)scope.getOrDefault(sign, SnbtGrammar.Sign.PLUS), (String)scope.getOrThrow(decimalNumeral)));
      Atom<String> floatWholePart = Atom.<String>of("float_whole_part");
      Atom<String> floatFractionPart = Atom.<String>of("float_fraction_part");
      Atom<T> floatLiteral = Atom.<T>of("float_literal");
      rules.putComplex(floatLiteral, Term.sequence(Term.optional(rules.named(sign)), Term.alternative(Term.sequence(rules.namedWithAlias(decimalNumeral, floatWholePart), StringReaderTerms.character('.'), Term.cut(), Term.optional(rules.namedWithAlias(decimalNumeral, floatFractionPart)), Term.optional(rules.named(floatExponentPart)), Term.optional(rules.named(floatTypeSuffix))), Term.sequence(StringReaderTerms.character('.'), Term.cut(), rules.namedWithAlias(decimalNumeral, floatFractionPart), Term.optional(rules.named(floatExponentPart)), Term.optional(rules.named(floatTypeSuffix))), Term.sequence(rules.namedWithAlias(decimalNumeral, floatWholePart), rules.named(floatExponentPart), Term.cut(), Term.optional(rules.named(floatTypeSuffix))), Term.sequence(rules.namedWithAlias(decimalNumeral, floatWholePart), Term.optional(rules.named(floatExponentPart)), rules.named(floatTypeSuffix)))), (state) -> {
         Scope scope = state.scope();
         Sign wholeSign = (Sign)scope.getOrDefault(sign, SnbtGrammar.Sign.PLUS);
         String whole = (String)scope.get(floatWholePart);
         String fraction = (String)scope.get(floatFractionPart);
         Signed<String> exponent = (Signed)scope.get(floatExponentPart);
         TypeSuffix typeSuffix = (TypeSuffix)scope.get(floatTypeSuffix);
         return createFloat(ops, wholeSign, whole, fraction, exponent, typeSuffix, state);
      });
      Atom<String> stringHex2 = Atom.<String>of("string_hex_2");
      rules.put(stringHex2, new SimpleHexLiteralParseRule(2));
      Atom<String> stringHex4 = Atom.<String>of("string_hex_4");
      rules.put(stringHex4, new SimpleHexLiteralParseRule(4));
      Atom<String> stringHex8 = Atom.<String>of("string_hex_8");
      rules.put(stringHex8, new SimpleHexLiteralParseRule(8));
      Atom<String> stringUnicodeName = Atom.<String>of("string_unicode_name");
      rules.put(stringUnicodeName, new GreedyPatternParseRule(UNICODE_NAME, ERROR_INVALID_CHARACTER_NAME));
      Atom<String> stringEscapeSequence = Atom.<String>of("string_escape_sequence");
      rules.putComplex(stringEscapeSequence, Term.alternative(Term.sequence(StringReaderTerms.character('b'), Term.marker(stringEscapeSequence, "\b")), Term.sequence(StringReaderTerms.character('s'), Term.marker(stringEscapeSequence, " ")), Term.sequence(StringReaderTerms.character('t'), Term.marker(stringEscapeSequence, "\t")), Term.sequence(StringReaderTerms.character('n'), Term.marker(stringEscapeSequence, "\n")), Term.sequence(StringReaderTerms.character('f'), Term.marker(stringEscapeSequence, "\f")), Term.sequence(StringReaderTerms.character('r'), Term.marker(stringEscapeSequence, "\r")), Term.sequence(StringReaderTerms.character('\\'), Term.marker(stringEscapeSequence, "\\")), Term.sequence(StringReaderTerms.character('\''), Term.marker(stringEscapeSequence, "'")), Term.sequence(StringReaderTerms.character('"'), Term.marker(stringEscapeSequence, "\"")), Term.sequence(StringReaderTerms.character('x'), rules.named(stringHex2)), Term.sequence(StringReaderTerms.character('u'), rules.named(stringHex4)), Term.sequence(StringReaderTerms.character('U'), rules.named(stringHex8)), Term.sequence(StringReaderTerms.character('N'), StringReaderTerms.character('{'), rules.named(stringUnicodeName), StringReaderTerms.character('}'))), (state) -> {
         Scope scope = state.scope();
         String plainEscape = (String)scope.getAny(stringEscapeSequence);
         if (plainEscape != null) {
            return plainEscape;
         } else {
            String hexEscape = (String)scope.getAny(stringHex2, stringHex4, stringHex8);
            if (hexEscape != null) {
               int codePoint = HexFormat.fromHexDigits(hexEscape);
               if (!Character.isValidCodePoint(codePoint)) {
                  state.errorCollector().store(state.mark(), DelayedException.create(ERROR_INVALID_CODEPOINT, String.format(Locale.ROOT, "U+%08X", codePoint)));
                  return null;
               } else {
                  return Character.toString(codePoint);
               }
            } else {
               String character = (String)scope.getOrThrow(stringUnicodeName);

               int codePoint;
               try {
                  codePoint = Character.codePointOf(character);
               } catch (IllegalArgumentException var12) {
                  state.errorCollector().store(state.mark(), ERROR_INVALID_CHARACTER_NAME);
                  return null;
               }

               return Character.toString(codePoint);
            }
         }
      });
      Atom<String> stringPlainContents = Atom.<String>of("string_plain_contents");
      rules.put(stringPlainContents, PLAIN_STRING_CHUNK);
      Atom<List<String>> stringChunks = Atom.<List<String>>of("string_chunks");
      Atom<String> stringContents = Atom.<String>of("string_contents");
      Atom<String> singleQuotedStringChunk = Atom.<String>of("single_quoted_string_chunk");
      NamedRule<StringReader, String> singleQuotedStringChunkRule = rules.put(singleQuotedStringChunk, Term.alternative(rules.namedWithAlias(stringPlainContents, stringContents), Term.sequence(StringReaderTerms.character('\\'), rules.namedWithAlias(stringEscapeSequence, stringContents)), Term.sequence(StringReaderTerms.character('"'), Term.marker(stringContents, "\""))), (scope) -> (String)scope.getOrThrow(stringContents));
      Atom<String> singleQuotedStringContents = Atom.<String>of("single_quoted_string_contents");
      rules.put(singleQuotedStringContents, Term.repeated(singleQuotedStringChunkRule, stringChunks), (scope) -> joinList((List)scope.getOrThrow(stringChunks)));
      Atom<String> doubleQuotedStringChunk = Atom.<String>of("double_quoted_string_chunk");
      NamedRule<StringReader, String> doubleQuotedStringChunkRule = rules.put(doubleQuotedStringChunk, Term.alternative(rules.namedWithAlias(stringPlainContents, stringContents), Term.sequence(StringReaderTerms.character('\\'), rules.namedWithAlias(stringEscapeSequence, stringContents)), Term.sequence(StringReaderTerms.character('\''), Term.marker(stringContents, "'"))), (scope) -> (String)scope.getOrThrow(stringContents));
      Atom<String> doubleQuotedStringContents = Atom.<String>of("double_quoted_string_contents");
      rules.put(doubleQuotedStringContents, Term.repeated(doubleQuotedStringChunkRule, stringChunks), (scope) -> joinList((List)scope.getOrThrow(stringChunks)));
      Atom<String> quotedStringLiteral = Atom.<String>of("quoted_string_literal");
      rules.put(quotedStringLiteral, Term.alternative(Term.sequence(StringReaderTerms.character('"'), Term.cut(), Term.optional(rules.namedWithAlias(doubleQuotedStringContents, stringContents)), StringReaderTerms.character('"')), Term.sequence(StringReaderTerms.character('\''), Term.optional(rules.namedWithAlias(singleQuotedStringContents, stringContents)), StringReaderTerms.character('\''))), (scope) -> (String)scope.getOrThrow(stringContents));
      Atom<String> unquotedString = Atom.<String>of("unquoted_string");
      rules.put(unquotedString, new UnquotedStringParseRule(1, ERROR_EXPECTED_UNQUOTED_STRING));
      Atom<T> literal = Atom.<T>of("literal");
      Atom<List<T>> argumentList = Atom.<List<T>>of("arguments");
      rules.put(argumentList, Term.repeatedWithTrailingSeparator(rules.forward(literal), argumentList, StringReaderTerms.character(',')), (scope) -> (List)scope.getOrThrow(argumentList));
      Atom<T> unquotedStringOrBuiltIn = Atom.<T>of("unquoted_string_or_builtin");
      rules.putComplex(unquotedStringOrBuiltIn, Term.sequence(rules.named(unquotedString), Term.optional(Term.sequence(StringReaderTerms.character('('), rules.named(argumentList), StringReaderTerms.character(')')))), (state) -> {
         Scope scope = state.scope();
         String contents = (String)scope.getOrThrow(unquotedString);
         if (!contents.isEmpty() && isAllowedToStartUnquotedString(contents.charAt(0))) {
            List<T> arguments = (List)scope.get(argumentList);
            if (arguments != null) {
               SnbtOperations.BuiltinKey key = new SnbtOperations.BuiltinKey(contents, arguments.size());
               SnbtOperations.BuiltinOperation operation = (SnbtOperations.BuiltinOperation)SnbtOperations.BUILTIN_OPERATIONS.get(key);
               if (operation != null) {
                  return operation.run(ops, arguments, state);
               } else {
                  state.errorCollector().store(state.mark(), DelayedException.create(ERROR_NO_SUCH_OPERATION, key.toString()));
                  return null;
               }
            } else if (contents.equalsIgnoreCase("true")) {
               return trueValue;
            } else {
               return contents.equalsIgnoreCase("false") ? falseValue : ops.createString(contents);
            }
         } else {
            state.errorCollector().store(state.mark(), SnbtOperations.BUILTIN_IDS, ERROR_INVALID_UNQUOTED_START);
            return null;
         }
      });
      Atom<String> mapKey = Atom.<String>of("map_key");
      rules.put(mapKey, Term.alternative(rules.named(quotedStringLiteral), rules.named(unquotedString)), (scope) -> (String)scope.getAnyOrThrow(quotedStringLiteral, unquotedString));
      Atom<Map.Entry<String, T>> mapEntry = Atom.<Map.Entry<String, T>>of("map_entry");
      NamedRule<StringReader, Map.Entry<String, T>> mapEntryRule = rules.putComplex(mapEntry, Term.sequence(rules.named(mapKey), StringReaderTerms.character(':'), rules.named(literal)), (state) -> {
         Scope scope = state.scope();
         String key = (String)scope.getOrThrow(mapKey);
         if (key.isEmpty()) {
            state.errorCollector().store(state.mark(), ERROR_EMPTY_KEY);
            return null;
         } else {
            T value = (T)scope.getOrThrow(literal);
            return Map.entry(key, value);
         }
      });
      Atom<List<Map.Entry<String, T>>> mapEntries = Atom.<List<Map.Entry<String, T>>>of("map_entries");
      rules.put(mapEntries, Term.repeatedWithTrailingSeparator(mapEntryRule, mapEntries, StringReaderTerms.character(',')), (scope) -> (List)scope.getOrThrow(mapEntries));
      Atom<T> mapLiteral = Atom.<T>of("map_literal");
      rules.put(mapLiteral, Term.sequence(StringReaderTerms.character('{'), rules.named(mapEntries), StringReaderTerms.character('}')), (scope) -> {
         List<Map.Entry<String, T>> entries = (List)scope.getOrThrow(mapEntries);
         if (entries.isEmpty()) {
            return emptyMapValue;
         } else {
            ImmutableMap.Builder<T, T> builder = ImmutableMap.builderWithExpectedSize(entries.size());

            for(Map.Entry<String, T> e : entries) {
               builder.put(ops.createString((String)e.getKey()), e.getValue());
            }

            return ops.createMap(builder.buildKeepingLast());
         }
      });
      Atom<List<T>> listEntries = Atom.<List<T>>of("list_entries");
      rules.put(listEntries, Term.repeatedWithTrailingSeparator(rules.forward(literal), listEntries, StringReaderTerms.character(',')), (scope) -> (List)scope.getOrThrow(listEntries));
      Atom<ArrayPrefix> arrayPrefix = Atom.<ArrayPrefix>of("array_prefix");
      rules.put(arrayPrefix, Term.alternative(Term.sequence(StringReaderTerms.character('B'), Term.marker(arrayPrefix, SnbtGrammar.ArrayPrefix.BYTE)), Term.sequence(StringReaderTerms.character('L'), Term.marker(arrayPrefix, SnbtGrammar.ArrayPrefix.LONG)), Term.sequence(StringReaderTerms.character('I'), Term.marker(arrayPrefix, SnbtGrammar.ArrayPrefix.INT))), (scope) -> (ArrayPrefix)scope.getOrThrow(arrayPrefix));
      Atom<List<IntegerLiteral>> intArrayEntries = Atom.<List<IntegerLiteral>>of("int_array_entries");
      rules.put(intArrayEntries, Term.repeatedWithTrailingSeparator(integerLiteralRule, intArrayEntries, StringReaderTerms.character(',')), (scope) -> (List)scope.getOrThrow(intArrayEntries));
      Atom<T> listLiteral = Atom.<T>of("list_literal");
      rules.putComplex(listLiteral, Term.sequence(StringReaderTerms.character('['), Term.alternative(Term.sequence(rules.named(arrayPrefix), StringReaderTerms.character(';'), rules.named(intArrayEntries)), rules.named(listEntries)), StringReaderTerms.character(']')), (state) -> {
         Scope scope = state.scope();
         ArrayPrefix arrayType = (ArrayPrefix)scope.get(arrayPrefix);
         if (arrayType != null) {
            List<IntegerLiteral> entries = (List)scope.getOrThrow(intArrayEntries);
            return entries.isEmpty() ? arrayType.create(ops) : arrayType.create(ops, entries, state);
         } else {
            List<T> entries = (List)scope.getOrThrow(listEntries);
            return entries.isEmpty() ? emptyList : ops.createList(entries.stream());
         }
      });
      NamedRule<StringReader, T> literalRule = rules.putComplex(literal, Term.alternative(Term.sequence(Term.positiveLookahead(NUMBER_LOOKEAHEAD), Term.alternative(rules.namedWithAlias(floatLiteral, literal), rules.named(integerLiteral))), Term.sequence(Term.positiveLookahead(StringReaderTerms.characters('"', '\'')), Term.cut(), rules.named(quotedStringLiteral)), Term.sequence(Term.positiveLookahead(StringReaderTerms.character('{')), Term.cut(), rules.namedWithAlias(mapLiteral, literal)), Term.sequence(Term.positiveLookahead(StringReaderTerms.character('[')), Term.cut(), rules.namedWithAlias(listLiteral, literal)), rules.namedWithAlias(unquotedStringOrBuiltIn, literal)), (state) -> {
         Scope scope = state.scope();
         String quotedString = (String)scope.get(quotedStringLiteral);
         if (quotedString != null) {
            return ops.createString(quotedString);
         } else {
            IntegerLiteral integer = (IntegerLiteral)scope.get(integerLiteral);
            return integer != null ? integer.create(ops, state) : scope.getOrThrow(literal);
         }
      });
      return new Grammar<T>(rules, literalRule);
   }

   static {
      BINARY_NUMERAL = new NumberRunParseRule(ERROR_EXPECTED_BINARY_NUMERAL, ERROR_UNDESCORE_NOT_ALLOWED) {
         protected boolean isAccepted(final char c) {
            boolean var10000;
            switch (c) {
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
         protected boolean isAccepted(final char c) {
            boolean var10000;
            switch (c) {
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
         protected boolean isAccepted(final char c) {
            boolean var10000;
            switch (c) {
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
         protected boolean isAccepted(final char c) {
            boolean var10000;
            switch (c) {
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
         protected boolean isAccepted(final char c) {
            return SnbtGrammar.canStartNumber(c);
         }
      };
      UNICODE_NAME = Pattern.compile("[-a-zA-Z0-9 ]+");
   }

   private static enum Sign {
      PLUS,
      MINUS;

      private Sign() {
      }

      public void append(final StringBuilder output) {
         if (this == MINUS) {
            output.append("-");
         }

      }

      // $FF: synthetic method
      private static Sign[] $values() {
         return new Sign[]{PLUS, MINUS};
      }
   }

   private static enum Base {
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

   private static enum TypeSuffix {
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

   private static enum SignedPrefix {
      SIGNED,
      UNSIGNED;

      private SignedPrefix() {
      }

      // $FF: synthetic method
      private static SignedPrefix[] $values() {
         return new SignedPrefix[]{SIGNED, UNSIGNED};
      }
   }

   private static record IntegerSuffix(@Nullable SignedPrefix signed, @Nullable TypeSuffix type) {
      public static final IntegerSuffix EMPTY = new IntegerSuffix((SignedPrefix)null, (TypeSuffix)null);

      private IntegerSuffix {
         super();
      }
   }

   private static enum ArrayPrefix {
      BYTE(SnbtGrammar.TypeSuffix.BYTE, new TypeSuffix[0]) {
         private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

         public <T> T create(final DynamicOps<T> ops) {
            return (T)ops.createByteList(EMPTY_BUFFER);
         }

         public <T> @Nullable T create(final DynamicOps<T> ops, final List<IntegerLiteral> entries, final ParseState<?> state) {
            ByteList result = new ByteArrayList();

            for(IntegerLiteral entry : entries) {
               Number parsedNumber = this.buildNumber(entry, state);
               if (parsedNumber == null) {
                  return null;
               }

               result.add(parsedNumber.byteValue());
            }

            return (T)ops.createByteList(ByteBuffer.wrap(result.toByteArray()));
         }
      },
      INT(SnbtGrammar.TypeSuffix.INT, new TypeSuffix[]{SnbtGrammar.TypeSuffix.BYTE, SnbtGrammar.TypeSuffix.SHORT}) {
         public <T> T create(final DynamicOps<T> ops) {
            return (T)ops.createIntList(IntStream.empty());
         }

         public <T> @Nullable T create(final DynamicOps<T> ops, final List<IntegerLiteral> entries, final ParseState<?> state) {
            IntStream.Builder result = IntStream.builder();

            for(IntegerLiteral entry : entries) {
               Number parsedNumber = this.buildNumber(entry, state);
               if (parsedNumber == null) {
                  return null;
               }

               result.add(parsedNumber.intValue());
            }

            return (T)ops.createIntList(result.build());
         }
      },
      LONG(SnbtGrammar.TypeSuffix.LONG, new TypeSuffix[]{SnbtGrammar.TypeSuffix.BYTE, SnbtGrammar.TypeSuffix.SHORT, SnbtGrammar.TypeSuffix.INT}) {
         public <T> T create(final DynamicOps<T> ops) {
            return (T)ops.createLongList(LongStream.empty());
         }

         public <T> @Nullable T create(final DynamicOps<T> ops, final List<IntegerLiteral> entries, final ParseState<?> state) {
            LongStream.Builder result = LongStream.builder();

            for(IntegerLiteral entry : entries) {
               Number parsedNumber = this.buildNumber(entry, state);
               if (parsedNumber == null) {
                  return null;
               }

               result.add(parsedNumber.longValue());
            }

            return (T)ops.createLongList(result.build());
         }
      };

      private final TypeSuffix defaultType;
      private final Set<TypeSuffix> additionalTypes;

      private ArrayPrefix(final TypeSuffix defaultType, final TypeSuffix... additionalTypes) {
         this.additionalTypes = Set.of(additionalTypes);
         this.defaultType = defaultType;
      }

      public boolean isAllowed(final TypeSuffix type) {
         return type == this.defaultType || this.additionalTypes.contains(type);
      }

      public abstract <T> T create(DynamicOps<T> ops);

      public abstract <T> @Nullable T create(DynamicOps<T> ops, List<IntegerLiteral> entries, ParseState<?> state);

      protected @Nullable Number buildNumber(final IntegerLiteral entry, final ParseState<?> state) {
         TypeSuffix actualType = this.computeType(entry.suffix);
         if (actualType == null) {
            state.errorCollector().store(state.mark(), SnbtGrammar.ERROR_INVALID_ARRAY_ELEMENT_TYPE);
            return null;
         } else {
            return (Number)entry.create(JavaOps.INSTANCE, actualType, state);
         }
      }

      private @Nullable TypeSuffix computeType(final IntegerSuffix value) {
         TypeSuffix type = value.type();
         if (type == null) {
            return this.defaultType;
         } else {
            return !this.isAllowed(type) ? null : type;
         }
      }

      // $FF: synthetic method
      private static ArrayPrefix[] $values() {
         return new ArrayPrefix[]{BYTE, INT, LONG};
      }
   }

   private static class SimpleHexLiteralParseRule extends GreedyPredicateParseRule {
      public SimpleHexLiteralParseRule(final int size) {
         super(size, size, DelayedException.create(SnbtGrammar.ERROR_EXPECTED_HEX_ESCAPE, String.valueOf(size)));
      }

      protected boolean isAccepted(final char c) {
         boolean var10000;
         switch (c) {
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

   private static record IntegerLiteral(Sign sign, Base base, String digits, IntegerSuffix suffix) {
      private IntegerLiteral {
         super();
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

      private String cleanupDigits(final Sign sign) {
         boolean needsUnderscoreRemoval = SnbtGrammar.needsUnderscoreRemoval(this.digits);
         if (sign != SnbtGrammar.Sign.MINUS && !needsUnderscoreRemoval) {
            return this.digits;
         } else {
            StringBuilder result = new StringBuilder();
            sign.append(result);
            SnbtGrammar.cleanAndAppend(result, this.digits, needsUnderscoreRemoval);
            return result.toString();
         }
      }

      public <T> @Nullable T create(final DynamicOps<T> ops, final ParseState<?> state) {
         return (T)this.create(ops, (TypeSuffix)Objects.requireNonNullElse(this.suffix.type, SnbtGrammar.TypeSuffix.INT), state);
      }

      public <T> @Nullable T create(final DynamicOps<T> ops, final TypeSuffix type, final ParseState<?> state) {
         boolean isSigned = this.signedOrDefault() == SnbtGrammar.SignedPrefix.SIGNED;
         if (!isSigned && this.sign == SnbtGrammar.Sign.MINUS) {
            state.errorCollector().store(state.mark(), SnbtGrammar.ERROR_EXPECTED_NON_NEGATIVE_NUMBER);
            return null;
         } else {
            String fixedDigits = this.cleanupDigits(this.sign);
            byte var10000;
            switch (this.base.ordinal()) {
               case 0 -> var10000 = 2;
               case 1 -> var10000 = 10;
               case 2 -> var10000 = 16;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            int radix = var10000;

            try {
               if (isSigned) {
                  Object var10;
                  switch (type.ordinal()) {
                     case 2:
                        var10 = ops.createByte(Byte.parseByte(fixedDigits, radix));
                        break;
                     case 3:
                        var10 = ops.createShort(Short.parseShort(fixedDigits, radix));
                        break;
                     case 4:
                        var10 = ops.createInt(Integer.parseInt(fixedDigits, radix));
                        break;
                     case 5:
                        var10 = ops.createLong(Long.parseLong(fixedDigits, radix));
                        break;
                     default:
                        state.errorCollector().store(state.mark(), SnbtGrammar.ERROR_EXPECTED_INTEGER_TYPE);
                        var10 = null;
                  }

                  return (T)var10;
               } else {
                  Object var9;
                  switch (type.ordinal()) {
                     case 2:
                        var9 = ops.createByte(UnsignedBytes.parseUnsignedByte(fixedDigits, radix));
                        break;
                     case 3:
                        var9 = ops.createShort(SnbtGrammar.parseUnsignedShort(fixedDigits, radix));
                        break;
                     case 4:
                        var9 = ops.createInt(Integer.parseUnsignedInt(fixedDigits, radix));
                        break;
                     case 5:
                        var9 = ops.createLong(Long.parseUnsignedLong(fixedDigits, radix));
                        break;
                     default:
                        state.errorCollector().store(state.mark(), SnbtGrammar.ERROR_EXPECTED_INTEGER_TYPE);
                        var9 = null;
                  }

                  return (T)var9;
               }
            } catch (NumberFormatException e) {
               state.errorCollector().store(state.mark(), SnbtGrammar.createNumberParseError(e));
               return null;
            }
         }
      }
   }

   private static record Signed<T>(Sign sign, T value) {
      private Signed {
         super();
      }
   }
}
