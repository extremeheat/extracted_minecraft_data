package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemParser {
   private static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(
      Component.translatable("argument.item.tag.disallowed")
   );
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType(
      var0 -> Component.translatable("argument.item.id.invalid", var0)
   );
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(
      var0 -> Component.translatable("arguments.item.tag.unknown", var0)
   );
   private static final char SYNTAX_START_NBT = '{';
   private static final char SYNTAX_TAG = '#';
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
   private final HolderLookup<Item> items;
   private final StringReader reader;
   private final boolean allowTags;
   private Either<Holder<Item>, HolderSet<Item>> result;
   @Nullable
   private CompoundTag nbt;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   private ItemParser(HolderLookup<Item> var1, StringReader var2, boolean var3) {
      super();
      this.items = var1;
      this.reader = var2;
      this.allowTags = var3;
   }

   public static ItemParser.ItemResult parseForItem(HolderLookup<Item> var0, StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();

      try {
         ItemParser var3 = new ItemParser(var0, var1, false);
         var3.parse();
         Holder var4 = (Holder)var3.result.left().orElseThrow(() -> new IllegalStateException("Parser returned unexpected tag name"));
         return new ItemParser.ItemResult(var4, var3.nbt);
      } catch (CommandSyntaxException var5) {
         var1.setCursor(var2);
         throw var5;
      }
   }

   public static Either<ItemParser.ItemResult, ItemParser.TagResult> parseForTesting(HolderLookup<Item> var0, StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();

      try {
         ItemParser var3 = new ItemParser(var0, var1, true);
         var3.parse();
         return var3.result.mapBoth(var1x -> new ItemParser.ItemResult(var1x, var3.nbt), var1x -> new ItemParser.TagResult(var1x, var3.nbt));
      } catch (CommandSyntaxException var4) {
         var1.setCursor(var2);
         throw var4;
      }
   }

   public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Item> var0, SuggestionsBuilder var1, boolean var2) {
      StringReader var3 = new StringReader(var1.getInput());
      var3.setCursor(var1.getStart());
      ItemParser var4 = new ItemParser(var0, var3, var2);

      try {
         var4.parse();
      } catch (CommandSyntaxException var6) {
      }

      return var4.suggestions.apply(var1.createOffset(var3.getCursor()));
   }

   private void readItem() throws CommandSyntaxException {
      int var1 = this.reader.getCursor();
      ResourceLocation var2 = ResourceLocation.read(this.reader);
      Optional var3 = this.items.get(ResourceKey.create(Registry.ITEM_REGISTRY, var2));
      this.result = Either.left((Holder)var3.orElseThrow(() -> {
         this.reader.setCursor(var1);
         return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, var2);
      }));
   }

   private void readTag() throws CommandSyntaxException {
      if (!this.allowTags) {
         throw ERROR_NO_TAGS_ALLOWED.createWithContext(this.reader);
      } else {
         int var1 = this.reader.getCursor();
         this.reader.expect('#');
         this.suggestions = this::suggestTag;
         ResourceLocation var2 = ResourceLocation.read(this.reader);
         Optional var3 = this.items.get(TagKey.create(Registry.ITEM_REGISTRY, var2));
         this.result = Either.right((HolderSet)var3.orElseThrow(() -> {
            this.reader.setCursor(var1);
            return ERROR_UNKNOWN_TAG.createWithContext(this.reader, var2);
         }));
      }
   }

   private void readNbt() throws CommandSyntaxException {
      this.nbt = new TagParser(this.reader).readStruct();
   }

   private void parse() throws CommandSyntaxException {
      if (this.allowTags) {
         this.suggestions = this::suggestItemIdOrTag;
      } else {
         this.suggestions = this::suggestItem;
      }

      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
      } else {
         this.readItem();
      }

      this.suggestions = this::suggestOpenNbt;
      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }
   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder var1) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf('{'));
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder var1) {
      return SharedSuggestionProvider.suggestResource(this.items.listTags().map(TagKey::location), var1, String.valueOf('#'));
   }

   private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder var1) {
      return SharedSuggestionProvider.suggestResource(this.items.listElements().map(ResourceKey::location), var1);
   }

   private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder var1) {
      this.suggestTag(var1);
      return this.suggestItem(var1);
   }

   public static record ItemResult(Holder<Item> a, @Nullable CompoundTag b) {
      private final Holder<Item> item;
      @Nullable
      private final CompoundTag nbt;

      public ItemResult(Holder<Item> var1, @Nullable CompoundTag var2) {
         super();
         this.item = var1;
         this.nbt = var2;
      }
   }

   public static record TagResult(HolderSet<Item> a, @Nullable CompoundTag b) {
      private final HolderSet<Item> tag;
      @Nullable
      private final CompoundTag nbt;

      public TagResult(HolderSet<Item> var1, @Nullable CompoundTag var2) {
         super();
         this.tag = var1;
         this.nbt = var2;
      }
   }
}
