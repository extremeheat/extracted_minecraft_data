package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemParser {
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((id) -> Component.translatableEscape("argument.item.id.invalid", id));
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType((id) -> Component.translatableEscape("arguments.item.component.unknown", id));
   private static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType((type, message) -> Component.translatableEscape("arguments.item.component.malformed", type, message));
   private static final SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT = new SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"));
   private static final DynamicCommandExceptionType ERROR_REPEATED_COMPONENT = new DynamicCommandExceptionType((id) -> Component.translatableEscape("arguments.item.component.repeated", id));
   public static final char SYNTAX_START_COMPONENTS = '[';
   public static final char SYNTAX_END_COMPONENTS = ']';
   public static final char SYNTAX_COMPONENT_SEPARATOR = ',';
   public static final char SYNTAX_COMPONENT_ASSIGNMENT = '=';
   public static final char SYNTAX_REMOVED_COMPONENT = '!';
   private static final TagParser<Tag> TAG_PARSER;
   private final HolderGetter<Item> items;
   private final RegistryOps<Tag> registryOps;

   public ItemParser(final HolderGetter.Provider registries) {
      super();
      this.items = registries.<Item>lookupOrThrow(Registries.ITEM);
      this.registryOps = registries.<Tag>createSerializationContext(NbtOps.INSTANCE);
   }

   public ItemInput parse(final StringReader reader) throws CommandSyntaxException {
      final MutableObject<Holder<Item>> itemResult = new MutableObject();
      final DataComponentPatch.Builder componentsBuilder = DataComponentPatch.builder();
      this.parse(reader, new Visitor() {
         {
            Objects.requireNonNull(ItemParser.this);
         }

         public void visitItem(final Holder<Item> item) {
            itemResult.setValue(item);
         }

         public <T> void visitComponent(final DataComponentType<T> type, final T value) {
            componentsBuilder.set(type, value);
         }

         public <T> void visitRemovedComponent(final DataComponentType<T> type) {
            componentsBuilder.remove(type);
         }
      });
      Holder<Item> item = (Holder)Objects.requireNonNull((Holder)itemResult.get(), "Parser gave no item");
      DataComponentPatch components = componentsBuilder.build();
      return new ItemInput(item, components);
   }

   public void parse(final StringReader reader, final Visitor visitor) throws CommandSyntaxException {
      int cursor = reader.getCursor();

      try {
         (new State(reader, visitor)).parse();
      } catch (CommandSyntaxException e) {
         reader.setCursor(cursor);
         throw e;
      }
   }

   public CompletableFuture<Suggestions> fillSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      StringReader reader = new StringReader(builder.getInput());
      reader.setCursor(builder.getStart());
      SuggestionsVisitor handler = new SuggestionsVisitor();
      State state = new State(reader, handler);

      try {
         state.parse();
      } catch (CommandSyntaxException var7) {
      }

      return handler.resolveSuggestions(context, builder, reader);
   }

   static {
      TAG_PARSER = TagParser.<Tag>create(NbtOps.INSTANCE);
   }

   private class State {
      private final StringReader reader;
      private final Visitor visitor;

      private State(final StringReader reader, final Visitor visitor) {
         Objects.requireNonNull(ItemParser.this);
         super();
         this.reader = reader;
         this.visitor = visitor;
      }

      public void parse() throws CommandSyntaxException {
         this.visitor.visitSuggestions(State::suggestItem);
         this.readItem();
         this.visitor.visitSuggestions(State::suggestStartComponents);
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.visitor.visitSuggestions(SuggestionContext.Provider.suggestNothing());
            this.readComponents();
         }

      }

      private void readItem() throws CommandSyntaxException {
         int cursor = this.reader.getCursor();
         Identifier id = Identifier.read(this.reader);
         this.visitor.visitItem((Holder)ItemParser.this.items.get(ResourceKey.create(Registries.ITEM, id)).orElseThrow(() -> {
            this.reader.setCursor(cursor);
            return ItemParser.ERROR_UNKNOWN_ITEM.createWithContext(this.reader, id);
         }));
      }

      private void readComponents() throws CommandSyntaxException {
         this.reader.expect('[');
         this.visitor.visitSuggestions(State::suggestComponentAssignmentOrRemoval);
         Set<DataComponentType<?>> visitedComponents = new ReferenceArraySet();

         while(this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            if (this.reader.canRead() && this.reader.peek() == '!') {
               this.reader.skip();
               this.visitor.visitSuggestions(State::suggestComponent);
               DataComponentType<?> componentType = readComponentType(this.reader);
               if (!visitedComponents.add(componentType)) {
                  throw ItemParser.ERROR_REPEATED_COMPONENT.create(componentType);
               }

               this.visitor.visitRemovedComponent(componentType);
               this.visitor.visitSuggestions(SuggestionContext.Provider.suggestNothing());
               this.reader.skipWhitespace();
            } else {
               DataComponentType<?> componentType = readComponentType(this.reader);
               if (!visitedComponents.add(componentType)) {
                  throw ItemParser.ERROR_REPEATED_COMPONENT.create(componentType);
               }

               this.visitor.visitSuggestions(State::suggestAssignment);
               this.reader.skipWhitespace();
               this.reader.expect('=');
               this.visitor.visitSuggestions(SuggestionContext.Provider.suggestNothing());
               this.reader.skipWhitespace();
               this.readComponent(ItemParser.TAG_PARSER, ItemParser.this.registryOps, componentType);
               this.reader.skipWhitespace();
            }

            this.visitor.visitSuggestions(State::suggestNextOrEndComponents);
            if (!this.reader.canRead() || this.reader.peek() != ',') {
               break;
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.visitor.visitSuggestions(State::suggestComponentAssignmentOrRemoval);
            if (!this.reader.canRead()) {
               throw ItemParser.ERROR_EXPECTED_COMPONENT.createWithContext(this.reader);
            }
         }

         this.reader.expect(']');
         this.visitor.visitSuggestions(SuggestionContext.Provider.suggestNothing());
      }

      public static DataComponentType<?> readComponentType(final StringReader reader) throws CommandSyntaxException {
         if (!reader.canRead()) {
            throw ItemParser.ERROR_EXPECTED_COMPONENT.createWithContext(reader);
         } else {
            int cursor = reader.getCursor();
            Identifier id = Identifier.read(reader);
            DataComponentType<?> component = (DataComponentType)BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(id);
            if (component != null && !component.isTransient()) {
               return component;
            } else {
               reader.setCursor(cursor);
               throw ItemParser.ERROR_UNKNOWN_COMPONENT.createWithContext(reader, id);
            }
         }
      }

      private <T, O> void readComponent(final TagParser<O> tagParser, final RegistryOps<O> registryOps, final DataComponentType<T> componentType) throws CommandSyntaxException {
         int cursor = this.reader.getCursor();
         O tag = tagParser.parseAsArgument(this.reader);
         DataResult<T> result = componentType.codecOrThrow().parse(registryOps, tag);
         this.visitor.visitComponent(componentType, result.getOrThrow((message) -> {
            this.reader.setCursor(cursor);
            return ItemParser.ERROR_MALFORMED_COMPONENT.createWithContext(this.reader, componentType.toString(), message);
         }));
      }

      private static CompletableFuture<Suggestions> suggestStartComponents(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
         if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('['));
         }

         return builder.buildFuture();
      }

      private static CompletableFuture<Suggestions> suggestNextOrEndComponents(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
         if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(','));
            builder.suggest(String.valueOf(']'));
         }

         return builder.buildFuture();
      }

      private static CompletableFuture<Suggestions> suggestAssignment(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
         if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('='));
         }

         return builder.buildFuture();
      }

      private static CompletableFuture<Suggestions> suggestItem(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
         return ((SuggestionContext)context.getSource()).suggestRegistryElements(context, Registries.ITEM, SuggestionContext.ElementSuggestionType.ELEMENTS, builder);
      }

      private static CompletableFuture<Suggestions> suggestComponentAssignmentOrRemoval(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
         builder.suggest(String.valueOf('!'));
         return suggestComponent(builder, String.valueOf('='));
      }

      private static CompletableFuture<Suggestions> suggestComponent(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
         return suggestComponent(builder, "");
      }

      private static CompletableFuture<Suggestions> suggestComponent(final SuggestionsBuilder builder, final String suffix) {
         String contents = builder.getRemaining().toLowerCase(Locale.ROOT);
         SuggestionContext.filterResources(BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet(), contents, (entry) -> ((ResourceKey)entry.getKey()).identifier(), (entry) -> {
            DataComponentType<?> type = (DataComponentType)entry.getValue();
            if (type.codec() != null) {
               Identifier id = ((ResourceKey)entry.getKey()).identifier();
               String var10001 = String.valueOf(id);
               builder.suggest(var10001 + suffix);
            }

         });
         return builder.buildFuture();
      }
   }

   private static class SuggestionsVisitor implements Visitor {
      private SuggestionContext.Provider suggestions = SuggestionContext.Provider.suggestNothing();

      private SuggestionsVisitor() {
         super();
      }

      public void visitSuggestions(final SuggestionContext.Provider suggestions) {
         this.suggestions = suggestions;
      }

      public CompletableFuture<Suggestions> resolveSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder, final StringReader reader) throws CommandSyntaxException {
         return this.suggestions.getSuggestions(context, builder.createOffset(reader.getCursor()));
      }
   }

   public interface Visitor {
      default void visitItem(final Holder<Item> item) {
      }

      default <T> void visitComponent(final DataComponentType<T> type, final T value) {
      }

      default <T> void visitRemovedComponent(final DataComponentType<T> type) {
      }

      default void visitSuggestions(final SuggestionContext.Provider suggestions) {
      }
   }
}
