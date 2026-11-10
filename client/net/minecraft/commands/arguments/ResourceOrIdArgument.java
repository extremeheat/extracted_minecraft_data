package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.CommandParserState;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;

public abstract class ResourceOrIdArgument<Raw, Value> implements SerializableArgument<Value> {
   static final Collection<String> EXAMPLES = List.of("foo", "foo:bar", "012", "{}", "true");
   public static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((error) -> Component.translatableEscape("argument.resource_or_id.failed_to_parse", error));
   public static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ELEMENT = new Dynamic2CommandExceptionType((id, registry) -> Component.translatableEscape("argument.resource_or_id.no_such_element", id, registry));
   public static final DynamicOps<Tag> OPS;
   private final ResourceKey<? extends Registry<Raw>> registryKey;
   private final Codec<Raw> codec;
   private final Grammar<CommandParserState, Result<Raw, Tag>> grammar;

   protected ResourceOrIdArgument(final ResourceKey<? extends Registry<Raw>> registryKey, final Codec<Raw> codec) {
      super();
      this.registryKey = registryKey;
      this.codec = codec;
      this.grammar = createGrammar(registryKey, OPS);
   }

   public abstract SerializableArgument.Type<? extends ResourceOrIdArgument<Raw, Value>> type();

   public static <T, O> Grammar<CommandParserState, Result<T, O>> createGrammar(final ResourceKey<? extends Registry<T>> registryKey, final DynamicOps<O> ops) {
      Grammar<CommandParserState, O> inlineValueGrammar = SnbtGrammar.createParser(ops);
      Dictionary<CommandParserState> rules = new Dictionary<CommandParserState>();
      Atom<Result<T, O>> result = Atom.<Result<T, O>>of("result");
      Atom<Identifier> id = Atom.<Identifier>of("id");
      Atom<O> value = Atom.<O>of("value");
      rules.put(id, IdentifierParseRule.INSTANCE);
      rules.put(value, inlineValueGrammar.top().value());
      NamedRule<CommandParserState, Result<T, O>> topRule = rules.put(result, Term.alternative(rules.named(id), rules.named(value)), (scope) -> {
         Identifier parsedId = (Identifier)scope.get(id);
         if (parsedId != null) {
            return new ReferenceResult(ResourceKey.create(registryKey, parsedId));
         } else {
            O parsedInline = (O)scope.getOrThrow(value);
            return new InlineResult(parsedInline);
         }
      });
      return new Grammar<CommandParserState, Result<T, O>>(rules, topRule);
   }

   public Value parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      Holder<Raw> result = this.parse(reader, context.holderGetters(), this.grammar, OPS);
      return (Value)this.convert(context, result);
   }

   protected abstract Value convert(ParseContext context, Holder<Raw> raw) throws CommandSyntaxException;

   private <O> Holder<Raw> parse(final StringReader reader, final HolderGetter.Provider registries, final Grammar<CommandParserState, Result<Raw, O>> grammar, final DynamicOps<O> ops) throws CommandSyntaxException {
      CommandParserState state = CommandParserState.simple(reader);
      Result<Raw, O> contents = grammar.parseForCommands(state);
      return contents.parse(state, registries, ops, this.codec, registries.lookupOrThrow(this.registryKey));
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return ((SuggestionContext)context.getSource()).suggestRegistryElements(context, this.registryKey, SuggestionContext.ElementSuggestionType.ELEMENTS, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      OPS = NbtOps.INSTANCE;
   }

   public static record InlineResult<T, O>(O value) implements Result<T, O> {
      public InlineResult {
         super();
      }

      public Holder<T> parse(final CommandParserState reader, final HolderGetter.Provider registries, final DynamicOps<O> ops, final Codec<T> codec, final HolderGetter<T> elementLookup) throws CommandSyntaxException {
         return Holder.<T>direct(codec.parse(registries.createSerializationContext(ops), this.value).getOrThrow((msg) -> ResourceOrIdArgument.ERROR_FAILED_TO_PARSE.createWithContext(reader.reader(), msg)));
      }
   }

   public static record ReferenceResult<T, O>(ResourceKey<T> key) implements Result<T, O> {
      public ReferenceResult {
         super();
      }

      public Holder<T> parse(final CommandParserState reader, final HolderGetter.Provider registries, final DynamicOps<O> ops, final Codec<T> codec, final HolderGetter<T> elementLookup) throws CommandSyntaxException {
         return (Holder)elementLookup.get(this.key).orElseThrow(() -> ResourceOrIdArgument.ERROR_NO_SUCH_ELEMENT.createWithContext(reader.reader(), this.key.identifier(), this.key.registry()));
      }
   }

   public abstract static class Simple<Value> extends ResourceOrIdArgument<Value, Holder<Value>> {
      public Simple(final ResourceKey<? extends Registry<Value>> registryKey, final Codec<Value> codec) {
         super(registryKey, codec);
      }

      protected Holder<Value> convert(final ParseContext context, final Holder<Value> raw) {
         return raw;
      }

      protected static <T> Holder<T> getResource(final CommandContext<?> context, final String name) {
         return (Holder)context.getArgument(name, Holder.class);
      }

      public abstract SerializableArgument.Type<? extends Simple<Value>> type();
   }

   public sealed interface Result<T, O> permits ResourceOrIdArgument.InlineResult, ResourceOrIdArgument.ReferenceResult {
      Holder<T> parse(CommandParserState reader, HolderGetter.Provider registries, DynamicOps<O> ops, Codec<T> codec, HolderGetter<T> elementLookup) throws CommandSyntaxException;
   }
}
