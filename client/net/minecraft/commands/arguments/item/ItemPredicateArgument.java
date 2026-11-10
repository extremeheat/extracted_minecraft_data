package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.parsing.packrat.Suggestion;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument extends ParserBasedArgument<Context, Result> {
   private static final ItemPredicateArgument INSTANCE = new ItemPredicateArgument();
   public static final SerializableArgument.Type<ItemPredicateArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM;
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG;
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT;
   private static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT;
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE;
   private static final Dynamic2CommandExceptionType ERROR_MALFORMED_PREDICATE;
   private static final Identifier COUNT_ID;
   private static final Map<Identifier, ComponentWrapper> PSEUDO_COMPONENTS;
   private static final Map<Identifier, PredicateWrapper> PSEUDO_PREDICATES;

   private static PredicateWrapper createComponentExistencePredicate(final Holder.Reference<DataComponentType<?>> componentId) {
      Predicate<ItemStack> componentExists = (itemStack) -> itemStack.has(componentId.value());
      return new PredicateWrapper(componentId.key().identifier(), Unit.CODEC.map((var1) -> componentExists));
   }

   private ItemPredicateArgument() {
      Grammar<Context, List<Predicate<ItemStack>>> grammar = ComponentPredicateParser.createGrammar();
      super((context, reader) -> new Context(reader, context.holderGetters()), grammar.mapResult((predicates) -> {
         Predicate var10000 = Util.allOf(predicates);
         Objects.requireNonNull(var10000);
         return var10000::test;
      }));
   }

   public SerializableArgument.Type<ItemPredicateArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Result> itemPredicate() {
      return INSTANCE.toBrigadier();
   }

   public static Result getItemPredicate(final CommandContext<?> context, final String name) {
      return (Result)context.getArgument(name, Result.class);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ItemPredicateArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
      ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((id) -> Component.translatableEscape("argument.item.id.invalid", id));
      ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((tag) -> Component.translatableEscape("arguments.item.tag.unknown", tag));
      ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType((id) -> Component.translatableEscape("arguments.item.component.unknown", id));
      ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType((type, message) -> Component.translatableEscape("arguments.item.component.malformed", type, message));
      ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((id) -> Component.translatableEscape("arguments.item.predicate.unknown", id));
      ERROR_MALFORMED_PREDICATE = new Dynamic2CommandExceptionType((type, message) -> Component.translatableEscape("arguments.item.predicate.malformed", type, message));
      COUNT_ID = Identifier.withDefaultNamespace("count");
      PSEUDO_COMPONENTS = (Map)Stream.of(new ComponentWrapper(COUNT_ID, (var0) -> true, MinMaxBounds.Ints.CODEC.map((range) -> (itemStack) -> range.matches(itemStack.getCount())))).collect(Collectors.toUnmodifiableMap(ComponentWrapper::id, (e) -> e));
      PSEUDO_PREDICATES = (Map)Stream.of(new PredicateWrapper(COUNT_ID, MinMaxBounds.Ints.CODEC.map((range) -> (itemStack) -> range.matches(itemStack.getCount())))).collect(Collectors.toUnmodifiableMap(PredicateWrapper::id, (e) -> e));
   }

   public static record ComponentWrapper(Identifier id, Predicate<ItemStack> presenceChecker, Decoder<? extends Predicate<ItemStack>> valueChecker) {
      public ComponentWrapper {
         super();
      }

      public static <T> ComponentWrapper create(final ImmutableStringReader reader, final Identifier id, final DataComponentType<T> type) throws CommandSyntaxException {
         Codec<T> codec = type.codec();
         if (codec == null) {
            throw ItemPredicateArgument.ERROR_UNKNOWN_COMPONENT.createWithContext(reader, id);
         } else {
            return new ComponentWrapper(id, (itemStack) -> itemStack.has(type), codec.map((expected) -> (itemStack) -> {
                  T actual = (T)itemStack.get(type);
                  return Objects.equals(expected, actual);
               }));
         }
      }

      public Predicate<ItemStack> decode(final ImmutableStringReader reader, final Dynamic<?> value) throws CommandSyntaxException {
         DataResult<? extends Predicate<ItemStack>> result = this.valueChecker.parse(value);
         return (Predicate)result.getOrThrow((message) -> ItemPredicateArgument.ERROR_MALFORMED_COMPONENT.createWithContext(reader, this.id.toString(), message));
      }
   }

   public static record PredicateWrapper(Identifier id, Decoder<? extends Predicate<ItemStack>> type) {
      public PredicateWrapper(final Holder.Reference<DataComponentPredicate.Type<?>> holder) {
         this(holder.key().identifier(), ((DataComponentPredicate.Type)holder.value()).codec().map((v) -> {
            Objects.requireNonNull(v);
            return v::matches;
         }));
      }

      public PredicateWrapper {
         super();
      }

      public Predicate<ItemStack> decode(final ImmutableStringReader reader, final Dynamic<?> value) throws CommandSyntaxException {
         DataResult<? extends Predicate<ItemStack>> result = this.type.parse(value);
         return (Predicate)result.getOrThrow((message) -> ItemPredicateArgument.ERROR_MALFORMED_PREDICATE.createWithContext(reader, this.id.toString(), message));
      }
   }

   public static class Context implements ComponentPredicateParser.Context<Predicate<ItemStack>, ComponentWrapper, PredicateWrapper> {
      private static final Suggestion ITEM_ID_SUGGESTION;
      private static final Suggestion ITEM_TAG_SUGGESTIONS;
      private static final Suggestion.Composite COMPONENT_ID_SUGGESTION;
      private static final Suggestion.Composite PREDICATE_ID_SUGGESTION;
      private final StringReader reader;
      private final HolderGetter.Provider registries;
      private final HolderGetter<Item> items;
      private final HolderGetter<DataComponentType<?>> components;
      private final HolderGetter<DataComponentPredicate.Type<?>> predicates;

      private Context(final StringReader reader, final HolderGetter.Provider registries) {
         super();
         this.reader = reader;
         this.registries = registries;
         this.items = registries.<Item>lookupOrThrow(Registries.ITEM);
         this.components = registries.<DataComponentType<?>>lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
         this.predicates = registries.<DataComponentPredicate.Type<?>>lookupOrThrow(Registries.DATA_COMPONENT_PREDICATE_TYPE);
      }

      public StringReader reader() {
         return this.reader;
      }

      public Predicate<ItemStack> forElementType(final Identifier id) throws CommandSyntaxException {
         Holder.Reference<Item> item = (Holder.Reference)this.items.get(ResourceKey.create(Registries.ITEM, id)).orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_ITEM.createWithContext(this.reader, id));
         return (itemStack) -> itemStack.is(item);
      }

      public Predicate<ItemStack> forTagType(final Identifier id) throws CommandSyntaxException {
         HolderSet<Item> tag = (HolderSet)this.items.get(TagKey.create(Registries.ITEM, id)).orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_TAG.createWithContext(this.reader, id));
         return (itemStack) -> itemStack.is(tag);
      }

      public ComponentWrapper lookupComponentType(final Identifier componentId) throws CommandSyntaxException {
         ComponentWrapper wrapper = (ComponentWrapper)ItemPredicateArgument.PSEUDO_COMPONENTS.get(componentId);
         if (wrapper != null) {
            return wrapper;
         } else {
            DataComponentType<?> componentType = (DataComponentType)this.components.get(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, componentId)).map(Holder::value).orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_COMPONENT.createWithContext(this.reader, componentId));
            return ItemPredicateArgument.ComponentWrapper.create(this.reader, componentId, componentType);
         }
      }

      public Predicate<ItemStack> createComponentTest(final ComponentWrapper componentType, final Dynamic<?> value) throws CommandSyntaxException {
         return componentType.decode(this.reader, RegistryOps.injectRegistryContext(value, this.registries));
      }

      public Predicate<ItemStack> createComponentTest(final ComponentWrapper componentType) {
         return componentType.presenceChecker;
      }

      public PredicateWrapper lookupPredicateType(final Identifier componentId) throws CommandSyntaxException {
         PredicateWrapper wrapper = (PredicateWrapper)ItemPredicateArgument.PSEUDO_PREDICATES.get(componentId);
         return wrapper != null ? wrapper : (PredicateWrapper)this.predicates.get(ResourceKey.create(Registries.DATA_COMPONENT_PREDICATE_TYPE, componentId)).map(PredicateWrapper::new).or(() -> this.components.get(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, componentId)).map(ItemPredicateArgument::createComponentExistencePredicate)).orElseThrow(() -> ItemPredicateArgument.ERROR_UNKNOWN_PREDICATE.createWithContext(this.reader, componentId));
      }

      public Predicate<ItemStack> createPredicateTest(final PredicateWrapper predicateType, final Dynamic<?> value) throws CommandSyntaxException {
         return predicateType.decode(this.reader, RegistryOps.injectRegistryContext(value, this.registries));
      }

      public Suggestion elementTypeSuggestion() {
         return ITEM_ID_SUGGESTION;
      }

      public Suggestion tagTypeSuggestion() {
         return ITEM_TAG_SUGGESTIONS;
      }

      public Suggestion componentTypeSuggestion() {
         return COMPONENT_ID_SUGGESTION;
      }

      public Suggestion predicateTypeSuggestion() {
         return PREDICATE_ID_SUGGESTION;
      }

      public Predicate<ItemStack> negate(final Predicate<ItemStack> value) {
         return value.negate();
      }

      public Predicate<ItemStack> anyOf(final List<Predicate<ItemStack>> alternatives) {
         return Util.anyOf(alternatives);
      }

      static {
         ITEM_ID_SUGGESTION = new Suggestion.RegistryElements(Registries.ITEM);
         ITEM_TAG_SUGGESTIONS = new Suggestion.RegistryTags(Registries.ITEM);
         COMPONENT_ID_SUGGESTION = new Suggestion.Composite(new Suggestion.Identifiers(ItemPredicateArgument.PSEUDO_PREDICATES.keySet()), new Suggestion.RegistryElements(Registries.DATA_COMPONENT_PREDICATE_TYPE));
         PREDICATE_ID_SUGGESTION = new Suggestion.Composite(new Suggestion.Identifiers(ItemPredicateArgument.PSEUDO_PREDICATES.keySet()), new Suggestion.RegistryElements(Registries.DATA_COMPONENT_TYPE, (e) -> !e.isTransient()));
      }
   }

   public interface Result extends Predicate<ItemStack> {
   }
}
