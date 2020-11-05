package net.minecraft.commands.arguments.blocks;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.block.tag.disallowed"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.block.id.invalid", new Object[]{var0});
   });
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.block.property.unknown", new Object[]{var0, var1});
   });
   public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.block.property.duplicate", new Object[]{var1, var0});
   });
   public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return new TranslatableComponent("argument.block.property.invalid", new Object[]{var0, var2, var1});
   });
   public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.block.property.novalue", new Object[]{var0, var1});
   });
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(new TranslatableComponent("argument.block.property.unclosed"));
   private static final BiFunction<SuggestionsBuilder, TagCollection<Block>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (var0, var1) -> {
      return var0.buildFuture();
   };
   private final StringReader reader;
   private final boolean forTesting;
   private final Map<Property<?>, Comparable<?>> properties = Maps.newHashMap();
   private final Map<String, String> vagueProperties = Maps.newHashMap();
   private ResourceLocation id = new ResourceLocation("");
   private StateDefinition<Block, BlockState> definition;
   private BlockState state;
   @Nullable
   private CompoundTag nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int tagCursor;
   private BiFunction<SuggestionsBuilder, TagCollection<Block>, CompletableFuture<Suggestions>> suggestions;

   public BlockStateParser(StringReader var1, boolean var2) {
      super();
      this.suggestions = SUGGEST_NOTHING;
      this.reader = var1;
      this.forTesting = var2;
   }

   public Map<Property<?>, Comparable<?>> getProperties() {
      return this.properties;
   }

   @Nullable
   public BlockState getState() {
      return this.state;
   }

   @Nullable
   public CompoundTag getNbt() {
      return this.nbt;
   }

   @Nullable
   public ResourceLocation getTag() {
      return this.tag;
   }

   public BlockStateParser parse(boolean var1) throws CommandSyntaxException {
      this.suggestions = this::suggestBlockIdOrTag;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
         this.suggestions = this::suggestOpenVaguePropertiesOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readVagueProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      } else {
         this.readBlock();
         this.suggestions = this::suggestOpenPropertiesOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      }

      if (var1 && this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf(']'));
      }

      return this.suggestPropertyName(var1, var2);
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf(']'));
      }

      return this.suggestVaguePropertyName(var1, var2);
   }

   private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder var1, TagCollection<Block> var2) {
      String var3 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var4 = this.state.getProperties().iterator();

      while(var4.hasNext()) {
         Property var5 = (Property)var4.next();
         if (!this.properties.containsKey(var5) && var5.getName().startsWith(var3)) {
            var1.suggest(var5.getName() + '=');
         }
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyName(SuggestionsBuilder var1, TagCollection<Block> var2) {
      String var3 = var1.getRemaining().toLowerCase(Locale.ROOT);
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag var4 = var2.getTag(this.tag);
         if (var4 != null) {
            Iterator var5 = var4.getValues().iterator();

            while(var5.hasNext()) {
               Block var6 = (Block)var5.next();
               Iterator var7 = var6.getStateDefinition().getProperties().iterator();

               while(var7.hasNext()) {
                  Property var8 = (Property)var7.next();
                  if (!this.vagueProperties.containsKey(var8.getName()) && var8.getName().startsWith(var3)) {
                     var1.suggest(var8.getName() + '=');
                  }
               }
            }
         }
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty() && this.hasBlockEntity(var2)) {
         var1.suggest(String.valueOf('{'));
      }

      return var1.buildFuture();
   }

   private boolean hasBlockEntity(TagCollection<Block> var1) {
      if (this.state != null) {
         return this.state.hasBlockEntity();
      } else {
         if (this.tag != null) {
            Tag var2 = var1.getTag(this.tag);
            if (var2 != null) {
               Iterator var3 = var2.getValues().iterator();

               while(var3.hasNext()) {
                  Block var4 = (Block)var3.next();
                  if (var4.defaultBlockState().hasBlockEntity()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf('='));
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty()) {
         var1.suggest(String.valueOf(']'));
      }

      if (var1.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
         var1.suggest(String.valueOf(','));
      }

      return var1.buildFuture();
   }

   private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(SuggestionsBuilder var0, Property<T> var1) {
      Iterator var2 = var1.getPossibleValues().iterator();

      while(var2.hasNext()) {
         Comparable var3 = (Comparable)var2.next();
         if (var3 instanceof Integer) {
            var0.suggest((Integer)var3);
         } else {
            var0.suggest(var1.getName(var3));
         }
      }

      return var0;
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyValue(SuggestionsBuilder var1, TagCollection<Block> var2, String var3) {
      boolean var4 = false;
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag var5 = var2.getTag(this.tag);
         if (var5 != null) {
            Iterator var6 = var5.getValues().iterator();

            label40:
            while(true) {
               while(true) {
                  Block var7;
                  do {
                     if (!var6.hasNext()) {
                        break label40;
                     }

                     var7 = (Block)var6.next();
                     Property var8 = var7.getStateDefinition().getProperty(var3);
                     if (var8 != null) {
                        addSuggestions(var1, var8);
                     }
                  } while(var4);

                  Iterator var9 = var7.getStateDefinition().getProperties().iterator();

                  while(var9.hasNext()) {
                     Property var10 = (Property)var9.next();
                     if (!this.vagueProperties.containsKey(var10.getName())) {
                        var4 = true;
                        break;
                     }
                  }
               }
            }
         }
      }

      if (var4) {
         var1.suggest(String.valueOf(','));
      }

      var1.suggest(String.valueOf(']'));
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenVaguePropertiesOrNbt(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty()) {
         Tag var3 = var2.getTag(this.tag);
         if (var3 != null) {
            boolean var4 = false;
            boolean var5 = false;
            Iterator var6 = var3.getValues().iterator();

            while(var6.hasNext()) {
               Block var7 = (Block)var6.next();
               var4 |= !var7.getStateDefinition().getProperties().isEmpty();
               var5 |= var7.defaultBlockState().hasBlockEntity();
               if (var4 && var5) {
                  break;
               }
            }

            if (var4) {
               var1.suggest(String.valueOf('['));
            }

            if (var5) {
               var1.suggest(String.valueOf('{'));
            }
         }
      }

      return this.suggestTag(var1, var2);
   }

   private CompletableFuture<Suggestions> suggestOpenPropertiesOrNbt(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (var1.getRemaining().isEmpty()) {
         if (!this.state.getBlock().getStateDefinition().getProperties().isEmpty()) {
            var1.suggest(String.valueOf('['));
         }

         if (this.state.hasBlockEntity()) {
            var1.suggest(String.valueOf('{'));
         }
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder var1, TagCollection<Block> var2) {
      return SharedSuggestionProvider.suggestResource((Iterable)var2.getAvailableTags(), var1.createOffset(this.tagCursor).add(var1));
   }

   private CompletableFuture<Suggestions> suggestBlockIdOrTag(SuggestionsBuilder var1, TagCollection<Block> var2) {
      if (this.forTesting) {
         SharedSuggestionProvider.suggestResource(var2.getAvailableTags(), var1, String.valueOf('#'));
      }

      SharedSuggestionProvider.suggestResource((Iterable)Registry.BLOCK.keySet(), var1);
      return var1.buildFuture();
   }

   public void readBlock() throws CommandSyntaxException {
      int var1 = this.reader.getCursor();
      this.id = ResourceLocation.read(this.reader);
      Block var2 = (Block)Registry.BLOCK.getOptional(this.id).orElseThrow(() -> {
         this.reader.setCursor(var1);
         return ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
      });
      this.definition = var2.getStateDefinition();
      this.state = var2.defaultBlockState();
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestPropertyNameOrEnd;
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != ']') {
         this.reader.skipWhitespace();
         int var1 = this.reader.getCursor();
         String var2 = this.reader.readString();
         Property var3 = this.definition.getProperty(var2);
         if (var3 == null) {
            this.reader.setCursor(var1);
            throw ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), var2);
         }

         if (this.properties.containsKey(var3)) {
            this.reader.setCursor(var1);
            throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), var2);
         }

         this.reader.skipWhitespace();
         this.suggestions = this::suggestEquals;
         if (this.reader.canRead() && this.reader.peek() == '=') {
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (var1x, var2x) -> {
               return addSuggestions(var1x, var3).buildFuture();
            };
            int var4 = this.reader.getCursor();
            this.setValue(var3, this.reader.readString(), var4);
            this.suggestions = this::suggestNextPropertyOrEnd;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestPropertyName;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
            break;
         }

         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), var2);
      }

      if (this.reader.canRead()) {
         this.reader.skip();
      } else {
         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readVagueProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestVaguePropertyNameOrEnd;
      int var1 = -1;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int var2 = this.reader.getCursor();
            String var3 = this.reader.readString();
            if (this.vagueProperties.containsKey(var3)) {
               this.reader.setCursor(var2);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), var3);
            }

            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(var2);
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), var3);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (var2x, var3x) -> {
               return this.suggestVaguePropertyValue(var2x, var3x, var3);
            };
            var1 = this.reader.getCursor();
            String var4 = this.reader.readString();
            this.vagueProperties.put(var3, var4);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            var1 = -1;
            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestVaguePropertyName;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         if (var1 >= 0) {
            this.reader.setCursor(var1);
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   private <T extends Comparable<T>> void setValue(Property<T> var1, String var2, int var3) throws CommandSyntaxException {
      Optional var4 = var1.getValue(var2);
      if (var4.isPresent()) {
         this.state = (BlockState)this.state.setValue(var1, (Comparable)var4.get());
         this.properties.put(var1, var4.get());
      } else {
         this.reader.setCursor(var3);
         throw ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), var1.getName(), var2);
      }
   }

   public static String serialize(BlockState var0) {
      StringBuilder var1 = new StringBuilder(Registry.BLOCK.getKey(var0.getBlock()).toString());
      if (!var0.getProperties().isEmpty()) {
         var1.append('[');
         boolean var2 = false;

         for(UnmodifiableIterator var3 = var0.getValues().entrySet().iterator(); var3.hasNext(); var2 = true) {
            Entry var4 = (Entry)var3.next();
            if (var2) {
               var1.append(',');
            }

            appendProperty(var1, (Property)var4.getKey(), (Comparable)var4.getValue());
         }

         var1.append(']');
      }

      return var1.toString();
   }

   private static <T extends Comparable<T>> void appendProperty(StringBuilder var0, Property<T> var1, Comparable<?> var2) {
      var0.append(var1.getName());
      var0.append('=');
      var0.append(var1.getName(var2));
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder var1, TagCollection<Block> var2) {
      return (CompletableFuture)this.suggestions.apply(var1.createOffset(this.reader.getCursor()), var2);
   }

   public Map<String, String> getVagueProperties() {
      return this.vagueProperties;
   }
}
