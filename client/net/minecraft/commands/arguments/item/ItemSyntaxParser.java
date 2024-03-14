package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemSyntaxParser {
   static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("argument.item.id.invalid", var0)
   );
   static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("arguments.item.tag.unknown", var0)
   );
   static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("arguments.item.component.unknown", var0)
   );
   static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("arguments.item.component.malformed", var0, var1)
   );
   static final SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT = new SimpleCommandExceptionType(
      Component.translatable("arguments.item.component.expected")
   );
   static final DynamicCommandExceptionType ERROR_REPEATED_COMPONENT = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("arguments.item.component.repeated", var0)
   );
   private static final char SYNTAX_TAG = '#';
   public static final char SYNTAX_START_COMPONENTS = '[';
   public static final char SYNTAX_END_COMPONENTS = ']';
   public static final char SYNTAX_COMPONENT_SEPARATOR = ',';
   public static final char SYNTAX_COMPONENT_ASSIGNMENT = '=';
   public static final char SYNTAX_START_NBT = '{';
   static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
   final HolderLookup.RegistryLookup<Item> items;
   final DynamicOps<Tag> registryOps;
   final boolean acceptTags;

   public ItemSyntaxParser(HolderLookup.Provider var1, boolean var2) {
      super();
      this.items = var1.lookupOrThrow(Registries.ITEM);
      this.registryOps = var1.createSerializationContext(NbtOps.INSTANCE);
      this.acceptTags = var2;
   }

   public void parse(StringReader var1, ItemSyntaxParser.Visitor var2) throws CommandSyntaxException {
      int var3 = var1.getCursor();

      try {
         new ItemSyntaxParser.State(var1, var2).parse();
      } catch (CommandSyntaxException var5) {
         var1.setCursor(var3);
         throw var5;
      }
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder var1) {
      StringReader var2 = new StringReader(var1.getInput());
      var2.setCursor(var1.getStart());
      ItemSyntaxParser.SuggestionsVisitor var3 = new ItemSyntaxParser.SuggestionsVisitor();
      ItemSyntaxParser.State var4 = new ItemSyntaxParser.State(var2, var3);

      try {
         var4.parse();
      } catch (CommandSyntaxException var6) {
      }

      return var3.resolveSuggestions(var1, var2);
   }

   class State {
      private final StringReader reader;
      private final ItemSyntaxParser.Visitor visitor;
      private boolean hasComponents;

      State(StringReader var2, ItemSyntaxParser.Visitor var3) {
         super();
         this.reader = var2;
         this.visitor = var3;
      }

      public void parse() throws CommandSyntaxException {
         this.visitor.visitSuggestions(ItemSyntaxParser.this.acceptTags ? this::suggestItemIdOrTag : this::suggestItem);
         if (ItemSyntaxParser.this.acceptTags && this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
         } else {
            this.readItem();
         }

         this.visitor.visitSuggestions(this::suggestStartComponentsOrNbt);
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readComponents();
            this.hasComponents = true;
         }

         this.visitor.visitSuggestions(this::suggestStartComponentsOrNbt);
         if (this.reader.canRead() && this.reader.peek() == '{') {
            this.readCustomData();
         }
      }

      private void readItem() throws CommandSyntaxException {
         int var1 = this.reader.getCursor();
         ResourceLocation var2 = ResourceLocation.read(this.reader);
         this.visitor.visitItem(ItemSyntaxParser.this.items.get(ResourceKey.create(Registries.ITEM, var2)).orElseThrow(() -> {
            this.reader.setCursor(var1);
            return ItemSyntaxParser.ERROR_UNKNOWN_ITEM.createWithContext(this.reader, var2);
         }));
      }

      private void readTag() throws CommandSyntaxException {
         int var1 = this.reader.getCursor();
         this.reader.expect('#');
         this.visitor.visitSuggestions(this::suggestTag);
         ResourceLocation var2 = ResourceLocation.read(this.reader);
         HolderSet var3 = ItemSyntaxParser.this.items.get(TagKey.create(Registries.ITEM, var2)).orElseThrow(() -> {
            this.reader.setCursor(var1);
            return ItemSyntaxParser.ERROR_UNKNOWN_TAG.createWithContext(this.reader, var2);
         });
         this.visitor.visitTag(var3);
      }

      private void readComponents() throws CommandSyntaxException {
         this.reader.expect('[');
         this.visitor.visitSuggestions(this::suggestComponentAssignment);
         ReferenceArraySet var1 = new ReferenceArraySet();

         while(this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            DataComponentType var2 = readComponentType(this.reader);
            if (!var1.add(var2)) {
               throw ItemSyntaxParser.ERROR_REPEATED_COMPONENT.create(var2);
            }

            this.visitor.visitSuggestions(this::suggestAssignment);
            this.reader.skipWhitespace();
            this.reader.expect('=');
            this.visitor.visitSuggestions(ItemSyntaxParser.SUGGEST_NOTHING);
            this.reader.skipWhitespace();
            this.readComponent(var2);
            this.reader.skipWhitespace();
            this.visitor.visitSuggestions(this::suggestNextOrEndComponents);
            if (!this.reader.canRead() || this.reader.peek() != ',') {
               break;
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.visitor.visitSuggestions(this::suggestComponentAssignment);
            if (!this.reader.canRead()) {
               throw ItemSyntaxParser.ERROR_EXPECTED_COMPONENT.createWithContext(this.reader);
            }
         }

         this.reader.expect(']');
         this.visitor.visitSuggestions(ItemSyntaxParser.SUGGEST_NOTHING);
      }

      public static DataComponentType<?> readComponentType(StringReader var0) throws CommandSyntaxException {
         if (!var0.canRead()) {
            throw ItemSyntaxParser.ERROR_EXPECTED_COMPONENT.createWithContext(var0);
         } else {
            int var1 = var0.getCursor();
            ResourceLocation var2 = ResourceLocation.read(var0);
            DataComponentType var3 = BuiltInRegistries.DATA_COMPONENT_TYPE.get(var2);
            if (var3 != null && !var3.isTransient()) {
               return var3;
            } else {
               var0.setCursor(var1);
               throw ItemSyntaxParser.ERROR_UNKNOWN_COMPONENT.createWithContext(var0, var2);
            }
         }
      }

      private <T> void readComponent(DataComponentType<T> var1) throws CommandSyntaxException {
         int var2 = this.reader.getCursor();
         Tag var3 = new TagParser(this.reader).readValue();
         DataResult var4 = var1.codecOrThrow().parse(ItemSyntaxParser.this.registryOps, var3);
         this.visitor.visitComponent(var1, Util.getOrThrow(var4, var3x -> {
            this.reader.setCursor(var2);
            return ItemSyntaxParser.ERROR_MALFORMED_COMPONENT.createWithContext(this.reader, var1.toString(), var3x);
         }));
      }

      private void readCustomData() throws CommandSyntaxException {
         this.visitor.visitSuggestions(ItemSyntaxParser.SUGGEST_NOTHING);
         this.visitor.visitCustomData(new TagParser(this.reader).readStruct());
      }

      private CompletableFuture<Suggestions> suggestStartComponentsOrNbt(SuggestionsBuilder var1) {
         if (var1.getRemaining().isEmpty()) {
            if (!this.hasComponents) {
               var1.suggest(String.valueOf('['));
            }

            var1.suggest(String.valueOf('{'));
         }

         return var1.buildFuture();
      }

      private CompletableFuture<Suggestions> suggestNextOrEndComponents(SuggestionsBuilder var1) {
         if (var1.getRemaining().isEmpty()) {
            var1.suggest(String.valueOf(','));
            var1.suggest(String.valueOf(']'));
         }

         return var1.buildFuture();
      }

      private CompletableFuture<Suggestions> suggestAssignment(SuggestionsBuilder var1) {
         if (var1.getRemaining().isEmpty()) {
            var1.suggest(String.valueOf('='));
         }

         return var1.buildFuture();
      }

      private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder var1) {
         return SharedSuggestionProvider.suggestResource(ItemSyntaxParser.this.items.listElementIds().map(ResourceKey::location), var1);
      }

      private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder var1) {
         return SharedSuggestionProvider.suggestResource(ItemSyntaxParser.this.items.listTagIds().map(TagKey::location), var1, String.valueOf('#'));
      }

      private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder var1) {
         this.suggestTag(var1);
         return this.suggestItem(var1);
      }

      private CompletableFuture<Suggestions> suggestComponentAssignment(SuggestionsBuilder var1) {
         String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
         SharedSuggestionProvider.filterResources(BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet(), var2, var0 -> var0.getKey().location(), var1x -> {
            DataComponentType var2xx = var1x.getValue();
            if (var2xx.codec() != null) {
               ResourceLocation var3 = var1x.getKey().location();
               var1.suggest(var3.toString() + "=");
            }
         });
         return var1.buildFuture();
      }
   }

   static class SuggestionsVisitor implements ItemSyntaxParser.Visitor {
      private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = ItemSyntaxParser.SUGGEST_NOTHING;

      SuggestionsVisitor() {
         super();
      }

      @Override
      public void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> var1) {
         this.suggestions = var1;
      }

      public CompletableFuture<Suggestions> resolveSuggestions(SuggestionsBuilder var1, StringReader var2) {
         return this.suggestions.apply(var1.createOffset(var2.getCursor()));
      }
   }

   public interface Visitor {
      default void visitItem(Holder<Item> var1) {
      }

      default void visitTag(HolderSet<Item> var1) {
      }

      default <T> void visitComponent(DataComponentType<T> var1, T var2) {
      }

      default void visitCustomData(CompoundTag var1) {
      }

      default void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> var1) {
      }
   }
}
