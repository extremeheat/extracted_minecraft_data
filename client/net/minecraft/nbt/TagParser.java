package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.util.parsing.packrat.commands.CommandParserState;
import net.minecraft.util.parsing.packrat.commands.Grammar;

public class TagParser<T> {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_COMPOUND = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.compound"));
   private static final TagParser<Tag> NBT_OPS_PARSER;
   public static final Codec<CompoundTag> FLATTENED_CODEC;
   public static final Codec<CompoundTag> LENIENT_CODEC;
   private final DynamicOps<T> ops;
   private final Grammar<CommandParserState, T> grammar;

   private TagParser(final DynamicOps<T> ops, final Grammar<CommandParserState, T> grammar) {
      super();
      this.ops = ops;
      this.grammar = grammar;
   }

   public DynamicOps<T> getOps() {
      return this.ops;
   }

   public static <T> TagParser<T> create(final DynamicOps<T> ops) {
      return new TagParser<T>(ops, SnbtGrammar.createParser(ops));
   }

   private static CompoundTag castToCompoundOrThrow(final CommandParserState reader, final Tag result) throws CommandSyntaxException {
      if (result instanceof CompoundTag compoundTag) {
         return compoundTag;
      } else {
         throw ERROR_EXPECTED_COMPOUND.createWithContext(reader.reader());
      }
   }

   public static CompoundTag parseCompoundFully(final String input) throws CommandSyntaxException {
      CommandParserState reader = CommandParserState.simple(input);
      return castToCompoundOrThrow(reader, (Tag)NBT_OPS_PARSER.parseFully(reader));
   }

   public T parseFully(final String input) throws CommandSyntaxException {
      return (T)this.parseFully(CommandParserState.simple(input));
   }

   public T parseFully(final CommandParserState state) throws CommandSyntaxException {
      T result = this.grammar.parseForCommands(state);
      StringReader reader = state.reader();
      reader.skipWhitespace();
      if (reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(reader);
      } else {
         return result;
      }
   }

   public T parseAsArgument(final StringReader reader) throws CommandSyntaxException {
      return this.grammar.parseForCommands(CommandParserState.simple(reader));
   }

   public static CompoundTag parseCompoundAsArgument(final StringReader reader) throws CommandSyntaxException {
      CommandParserState state = CommandParserState.simple(reader);
      Tag result = (Tag)NBT_OPS_PARSER.grammar.parseForCommands(state);
      return castToCompoundOrThrow(state, result);
   }

   static {
      NBT_OPS_PARSER = create(NbtOps.INSTANCE);
      FLATTENED_CODEC = Codec.STRING.comapFlatMap((s) -> {
         try {
            Tag result = (Tag)NBT_OPS_PARSER.parseFully(s);
            if (result instanceof CompoundTag compoundTag) {
               return DataResult.success(compoundTag, Lifecycle.stable());
            } else {
               return DataResult.error(() -> "Expected compound tag, got " + String.valueOf(result));
            }
         } catch (CommandSyntaxException e) {
            Objects.requireNonNull(e);
            return DataResult.error(e::getMessage);
         }
      }, CompoundTag::toString);
      LENIENT_CODEC = Codec.withAlternative(FLATTENED_CODEC, CompoundTag.CODEC);
   }
}
