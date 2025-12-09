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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

public class ResourceOrIdArgument<T> implements ArgumentType<Holder<T>> {
   private static final Collection<String> EXAMPLES = List.of("foo", "foo:bar", "012", "{}", "true");
   public static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.resource_or_id.failed_to_parse", var0));
   public static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ELEMENT = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("argument.resource_or_id.no_such_element", var0, var1));
   public static final DynamicOps<Tag> OPS;
   private final HolderLookup.Provider registryLookup;
   private final Optional<? extends HolderLookup.RegistryLookup<T>> elementLookup;
   private final Codec<T> codec;
   private final Grammar<Result<T, Tag>> grammar;
   private final ResourceKey<? extends Registry<T>> registryKey;

   protected ResourceOrIdArgument(CommandBuildContext var1, ResourceKey<? extends Registry<T>> var2, Codec<T> var3) {
      super();
      this.registryLookup = var1;
      this.elementLookup = var1.lookup(var2);
      this.registryKey = var2;
      this.codec = var3;
      this.grammar = createGrammar(var2, OPS);
   }

   public static <T, O> Grammar<Result<T, O>> createGrammar(ResourceKey<? extends Registry<T>> var0, DynamicOps<O> var1) {
      Grammar var2 = SnbtGrammar.createParser(var1);
      Dictionary var3 = new Dictionary();
      Atom var4 = Atom.of("result");
      Atom var5 = Atom.of("id");
      Atom var6 = Atom.of("value");
      var3.put(var5, IdentifierParseRule.INSTANCE);
      var3.put(var6, var2.top().value());
      NamedRule var7 = var3.put(var4, Term.alternative(var3.named(var5), var3.named(var6)), (var3x) -> {
         Identifier var4 = (Identifier)var3x.get(var5);
         if (var4 != null) {
            return new ReferenceResult(ResourceKey.create(var0, var4));
         } else {
            Object var5x = var3x.getOrThrow(var6);
            return new InlineResult(var5x);
         }
      });
      return new Grammar<Result<T, O>>(var3, var7);
   }

   public static LootTableArgument lootTable(CommandBuildContext var0) {
      return new LootTableArgument(var0);
   }

   public static Holder<LootTable> getLootTable(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1);
   }

   public static LootModifierArgument lootModifier(CommandBuildContext var0) {
      return new LootModifierArgument(var0);
   }

   public static Holder<LootItemFunction> getLootModifier(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   public static LootPredicateArgument lootPredicate(CommandBuildContext var0) {
      return new LootPredicateArgument(var0);
   }

   public static Holder<LootItemCondition> getLootPredicate(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   public static DialogArgument dialog(CommandBuildContext var0) {
      return new DialogArgument(var0);
   }

   public static Holder<Dialog> getDialog(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   private static <T> Holder<T> getResource(CommandContext<CommandSourceStack> var0, String var1) {
      return (Holder)var0.getArgument(var1, Holder.class);
   }

   public @Nullable Holder<T> parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1, this.grammar, OPS);
   }

   private <O> @Nullable Holder<T> parse(StringReader var1, Grammar<Result<T, O>> var2, DynamicOps<O> var3) throws CommandSyntaxException {
      Result var4 = (Result)var2.parseForCommands(var1);
      return this.elementLookup.isEmpty() ? null : var4.parse(var1, this.registryLookup, var3, this.codec, (HolderLookup.RegistryLookup)this.elementLookup.get());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.listSuggestions(var1, var2, this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public @Nullable Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      OPS = NbtOps.INSTANCE;
   }

   public static class LootTableArgument extends ResourceOrIdArgument<LootTable> {
      protected LootTableArgument(CommandBuildContext var1) {
         super(var1, Registries.LOOT_TABLE, LootTable.DIRECT_CODEC);
      }

      // $FF: synthetic method
      public @Nullable Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }

   public static class LootModifierArgument extends ResourceOrIdArgument<LootItemFunction> {
      protected LootModifierArgument(CommandBuildContext var1) {
         super(var1, Registries.ITEM_MODIFIER, LootItemFunctions.ROOT_CODEC);
      }

      // $FF: synthetic method
      public @Nullable Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }

   public static class LootPredicateArgument extends ResourceOrIdArgument<LootItemCondition> {
      protected LootPredicateArgument(CommandBuildContext var1) {
         super(var1, Registries.PREDICATE, LootItemCondition.DIRECT_CODEC);
      }

      // $FF: synthetic method
      public @Nullable Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }

   public static class DialogArgument extends ResourceOrIdArgument<Dialog> {
      protected DialogArgument(CommandBuildContext var1) {
         super(var1, Registries.DIALOG, Dialog.DIRECT_CODEC);
      }

      // $FF: synthetic method
      public @Nullable Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }

   public static record InlineResult<T, O>(O value) implements Result<T, O> {
      public InlineResult(O var1) {
         super();
         this.value = var1;
      }

      public Holder<T> parse(ImmutableStringReader var1, HolderLookup.Provider var2, DynamicOps<O> var3, Codec<T> var4, HolderLookup.RegistryLookup<T> var5) throws CommandSyntaxException {
         return Holder.<T>direct(var4.parse(var2.createSerializationContext(var3), this.value).getOrThrow((var1x) -> ResourceOrIdArgument.ERROR_FAILED_TO_PARSE.createWithContext(var1, var1x)));
      }
   }

   public static record ReferenceResult<T, O>(ResourceKey<T> key) implements Result<T, O> {
      public ReferenceResult(ResourceKey<T> var1) {
         super();
         this.key = var1;
      }

      public Holder<T> parse(ImmutableStringReader var1, HolderLookup.Provider var2, DynamicOps<O> var3, Codec<T> var4, HolderLookup.RegistryLookup<T> var5) throws CommandSyntaxException {
         return (Holder)var5.get(this.key).orElseThrow(() -> ResourceOrIdArgument.ERROR_NO_SUCH_ELEMENT.createWithContext(var1, this.key.identifier(), this.key.registry()));
      }
   }

   public sealed interface Result<T, O> permits ResourceOrIdArgument.InlineResult, ResourceOrIdArgument.ReferenceResult {
      Holder<T> parse(ImmutableStringReader var1, HolderLookup.Provider var2, DynamicOps<O> var3, Codec<T> var4, HolderLookup.RegistryLookup<T> var5) throws CommandSyntaxException;
   }
}
