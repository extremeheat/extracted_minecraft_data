package net.minecraft.commands.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

public class ResourceOrIdArgument<T> implements ArgumentType<Holder<T>> {
   static final Collection<String> EXAMPLES = List.of("foo", "foo:bar", "012", "{}", "true");
   public static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((error) -> Component.translatableEscape("argument.resource_or_id.failed_to_parse", error));
   public static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ELEMENT = new Dynamic2CommandExceptionType((id, registry) -> Component.translatableEscape("argument.resource_or_id.no_such_element", id, registry));
   public static final DynamicOps<Tag> OPS;
   private final HolderLookup.Provider registryLookup;
   private final Optional<? extends HolderLookup.RegistryLookup<T>> elementLookup;
   private final Codec<T> codec;
   private final Grammar<Result<T, Tag>> grammar;
   private final ResourceKey<? extends Registry<T>> registryKey;

   protected ResourceOrIdArgument(final CommandBuildContext context, final ResourceKey<? extends Registry<T>> registryKey, final Codec<T> codec) {
      super();
      this.registryLookup = context;
      this.elementLookup = context.lookup(registryKey);
      this.registryKey = registryKey;
      this.codec = codec;
      this.grammar = createGrammar(registryKey, OPS);
   }

   public static <T, O> Grammar<Result<T, O>> createGrammar(final ResourceKey<? extends Registry<T>> registryKey, final DynamicOps<O> ops) {
      Grammar<O> inlineValueGrammar = SnbtGrammar.<O>createParser(ops);
      Dictionary<StringReader> rules = new Dictionary<StringReader>();
      Atom<Result<T, O>> result = Atom.<Result<T, O>>of("result");
      Atom<Identifier> id = Atom.<Identifier>of("id");
      Atom<O> value = Atom.<O>of("value");
      rules.put(id, IdentifierParseRule.INSTANCE);
      rules.put(value, inlineValueGrammar.top().value());
      NamedRule<StringReader, Result<T, O>> topRule = rules.put(result, Term.alternative(rules.named(id), rules.named(value)), (scope) -> {
         Identifier parsedId = (Identifier)scope.get(id);
         if (parsedId != null) {
            return new ReferenceResult(ResourceKey.create(registryKey, parsedId));
         } else {
            O parsedInline = (O)scope.getOrThrow(value);
            return new InlineResult(parsedInline);
         }
      });
      return new Grammar<Result<T, O>>(rules, topRule);
   }

   public static LootTableArgument lootTable(final CommandBuildContext context) {
      return new LootTableArgument(context);
   }

   public static Holder<LootTable> getLootTable(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return getResource(context, name);
   }

   public static LootModifierArgument lootModifier(final CommandBuildContext context) {
      return new LootModifierArgument(context);
   }

   public static Holder<LootItemFunction> getLootModifier(final CommandContext<CommandSourceStack> context, final String name) {
      return getResource(context, name);
   }

   public static LootPredicateArgument lootPredicate(final CommandBuildContext context) {
      return new LootPredicateArgument(context);
   }

   public static Holder<LootItemCondition> getLootPredicate(final CommandContext<CommandSourceStack> context, final String name) {
      return getResource(context, name);
   }

   public static FeatureArgument feature(final CommandBuildContext context) {
      return new FeatureArgument(context);
   }

   public static Holder<Feature> getFeature(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return getResource(context, name);
   }

   public static DialogArgument dialog(final CommandBuildContext context) {
      return new DialogArgument(context);
   }

   public static Holder<Dialog> getDialog(final CommandContext<CommandSourceStack> context, final String name) {
      return getResource(context, name);
   }

   private static <T> Holder<T> getResource(final CommandContext<CommandSourceStack> context, final String name) {
      return (Holder)context.getArgument(name, Holder.class);
   }

   public @Nullable Holder<T> parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader, this.grammar, OPS);
   }

   private <O> @Nullable Holder<T> parse(final StringReader reader, final Grammar<Result<T, O>> grammar, final DynamicOps<O> ops) throws CommandSyntaxException {
      Result<T, O> contents = grammar.parseForCommands(reader);
      return this.elementLookup.isEmpty() ? null : contents.parse(reader, this.registryLookup, ops, this.codec, (HolderLookup.RegistryLookup)this.elementLookup.get());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
      return SharedSuggestionProvider.listSuggestions(context, builder, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      OPS = NbtOps.INSTANCE;
   }

   public static class LootTableArgument extends ResourceOrIdArgument<LootTable> {
      protected LootTableArgument(final CommandBuildContext context) {
         super(context, Registries.LOOT_TABLE, LootTable.DIRECT_CODEC);
      }
   }

   public static class LootModifierArgument extends ResourceOrIdArgument<LootItemFunction> {
      protected LootModifierArgument(final CommandBuildContext context) {
         super(context, Registries.ITEM_MODIFIER, LootItemFunctions.DIRECT_CODEC);
      }
   }

   public static class LootPredicateArgument extends ResourceOrIdArgument<LootItemCondition> {
      protected LootPredicateArgument(final CommandBuildContext context) {
         super(context, Registries.PREDICATE, LootItemCondition.DIRECT_CODEC);
      }
   }

   public static class FeatureArgument extends ResourceOrIdArgument<Feature> {
      protected FeatureArgument(final CommandBuildContext context) {
         super(context, Registries.FEATURE, Feature.DIRECT_CODEC);
      }
   }

   public static class DialogArgument extends ResourceOrIdArgument<Dialog> {
      protected DialogArgument(final CommandBuildContext context) {
         super(context, Registries.DIALOG, Dialog.DIRECT_CODEC);
      }
   }

   public static record InlineResult<T, O>(O value) implements Result<T, O> {
      public InlineResult {
         super();
      }

      public Holder<T> parse(final ImmutableStringReader reader, final HolderLookup.Provider lookup, final DynamicOps<O> ops, final Codec<T> codec, final HolderLookup.RegistryLookup<T> elementLookup) throws CommandSyntaxException {
         return Holder.<T>direct(codec.parse(lookup.createSerializationContext(ops), this.value).getOrThrow((msg) -> ResourceOrIdArgument.ERROR_FAILED_TO_PARSE.createWithContext(reader, msg)));
      }
   }

   public static record ReferenceResult<T, O>(ResourceKey<T> key) implements Result<T, O> {
      public ReferenceResult {
         super();
      }

      public Holder<T> parse(final ImmutableStringReader reader, final HolderLookup.Provider lookup, final DynamicOps<O> ops, final Codec<T> codec, final HolderLookup.RegistryLookup<T> elementLookup) throws CommandSyntaxException {
         return (Holder)elementLookup.get(this.key).orElseThrow(() -> ResourceOrIdArgument.ERROR_NO_SUCH_ELEMENT.createWithContext(reader, this.key.identifier(), this.key.registry()));
      }
   }

   public sealed interface Result<T, O> permits ResourceOrIdArgument.InlineResult, ResourceOrIdArgument.ReferenceResult {
      Holder<T> parse(ImmutableStringReader reader, HolderLookup.Provider lookup, DynamicOps<O> ops, Codec<T> codec, HolderLookup.RegistryLookup<T> elementLookup) throws CommandSyntaxException;
   }
}
