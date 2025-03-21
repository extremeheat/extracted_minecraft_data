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
import net.minecraft.util.parsing.packrat.commands.Grammar;

public class TagParser<T> {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_COMPOUND = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.compound"));
   public static final char ELEMENT_SEPARATOR = ',';
   public static final char NAME_VALUE_SEPARATOR = ':';
   private static final TagParser<Tag> NBT_OPS_PARSER;
   public static final Codec<CompoundTag> FLATTENED_CODEC;
   public static final Codec<CompoundTag> LENIENT_CODEC;
   private final DynamicOps<T> ops;
   private final Grammar<T> grammar;

   private TagParser(DynamicOps<T> var1, Grammar<T> var2) {
      super();
      this.ops = var1;
      this.grammar = var2;
   }

   public DynamicOps<T> getOps() {
      return this.ops;
   }

   public static <T> TagParser<T> create(DynamicOps<T> var0) {
      return new TagParser<T>(var0, SnbtGrammar.createParser(var0));
   }

   private static CompoundTag castToCompoundOrThrow(StringReader var0, Tag var1) throws CommandSyntaxException {
      if (var1 instanceof CompoundTag var2) {
         return var2;
      } else {
         throw ERROR_EXPECTED_COMPOUND.createWithContext(var0);
      }
   }

   public static CompoundTag parseCompoundFully(String var0) throws CommandSyntaxException {
      StringReader var1 = new StringReader(var0);
      return castToCompoundOrThrow(var1, (Tag)NBT_OPS_PARSER.parseFully(var1));
   }

   public T parseFully(String var1) throws CommandSyntaxException {
      return (T)this.parseFully(new StringReader(var1));
   }

   public T parseFully(StringReader var1) throws CommandSyntaxException {
      Object var2 = this.grammar.parseForCommands(var1);
      var1.skipWhitespace();
      if (var1.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(var1);
      } else {
         return (T)var2;
      }
   }

   public T parseAsArgument(StringReader var1) throws CommandSyntaxException {
      return this.grammar.parseForCommands(var1);
   }

   public static CompoundTag parseCompoundAsArgument(StringReader var0) throws CommandSyntaxException {
      Tag var1 = NBT_OPS_PARSER.parseAsArgument(var0);
      return castToCompoundOrThrow(var0, var1);
   }

   static {
      NBT_OPS_PARSER = create(NbtOps.INSTANCE);
      FLATTENED_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            Tag var1 = (Tag)NBT_OPS_PARSER.parseFully(var0);
            if (var1 instanceof CompoundTag var2) {
               return DataResult.success(var2, Lifecycle.stable());
            } else {
               return DataResult.error(() -> "Expected compound tag, got " + String.valueOf(var1));
            }
         } catch (CommandSyntaxException var3) {
            Objects.requireNonNull(var3);
            return DataResult.error(var3::getMessage);
         }
      }, CompoundTag::toString);
      LENIENT_CODEC = Codec.withAlternative(FLATTENED_CODEC, CompoundTag.CODEC);
   }
}
