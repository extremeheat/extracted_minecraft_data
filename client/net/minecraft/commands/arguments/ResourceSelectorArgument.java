package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.io.FilenameUtils;

public class ResourceSelectorArgument implements SerializableArgument.ContextFree<Result<?>> {
   private static final MapCodec<ResourceSelectorArgument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.REGISTRY_CODEC.fieldOf("registry").forGetter((o) -> o.registryKey)).apply(i, ResourceSelectorArgument::new));
   private static final StreamCodec<ByteBuf, ResourceSelectorArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<ResourceSelectorArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final Dynamic2CommandExceptionType ERROR_NO_MATCHES;
   private final ResourceKey<? extends Registry<?>> registryKey;

   private ResourceSelectorArgument(final ResourceKey<? extends Registry<?>> registryKey) {
      super();
      this.registryKey = registryKey;
   }

   public SerializableArgument.Type<ResourceSelectorArgument> type() {
      return TYPE;
   }

   public Result<?> parse(final StringReader reader) throws CommandSyntaxException {
      String pattern = ensureNamespaced(readPattern(reader));
      return new Result(this.registryKey, pattern);
   }

   public static <T> Collection<Holder.Reference<T>> parse(final StringReader reader, final HolderLookup<T> registry) {
      String pattern = ensureNamespaced(readPattern(reader));
      return registry.listElements().filter((element) -> matches(pattern, element.key().identifier())).toList();
   }

   private static String readPattern(final StringReader reader) {
      int start = reader.getCursor();

      while(reader.canRead() && isAllowedPatternCharacter(reader.peek())) {
         reader.skip();
      }

      return reader.getString().substring(start, reader.getCursor());
   }

   private static boolean isAllowedPatternCharacter(final char character) {
      return Identifier.isAllowedInIdentifier(character) || character == '*' || character == '?';
   }

   private static String ensureNamespaced(final String input) {
      return !input.contains(":") ? "minecraft:" + input : input;
   }

   private static boolean matches(final String pattern, final Identifier key) {
      return FilenameUtils.wildcardMatch(key.toString(), pattern);
   }

   public static ArgumentType<Result<?>> resourceSelector(final ResourceKey<? extends Registry<?>> registry) {
      return (new ResourceSelectorArgument(registry)).toBrigadier();
   }

   public static <T> Result<T> getSelectedResources(final CommandContext<?> context, final String name) {
      return (Result)context.getArgument(name, Result.class);
   }

   public static <T> Collection<Holder.Reference<T>> getAndLookupSelectedResources(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      Result<T> selectedResources = getSelectedResources(context, name);
      return selectedResources.selectFromRegistry(((CommandSourceStack)context.getSource()).holderLookups());
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return ((SuggestionContext)context.getSource()).suggestRegistryElements(context, this.registryKey, SuggestionContext.ElementSuggestionType.ELEMENTS, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceKey.REGISTRY_STREAM_CODEC, (o) -> o.registryKey, ResourceSelectorArgument::new);
      TYPE = new SerializableArgument.Type<ResourceSelectorArgument>(MAP_CODEC, STREAM_CODEC);
      EXAMPLES = List.of("minecraft:*", "*:asset", "*");
      ERROR_NO_MATCHES = new Dynamic2CommandExceptionType((selector, registry) -> Component.translatableEscape("argument.resource_selector.not_found", selector, registry));
   }

   public static class Result<T> {
      private final ResourceKey<? extends Registry<? extends T>> registryKey;
      private final String pattern;

      public Result(final ResourceKey<? extends Registry<? extends T>> registryKey, final String pattern) {
         super();
         this.registryKey = registryKey;
         this.pattern = pattern;
      }

      public Collection<Holder.Reference<T>> selectFromRegistry(final HolderLookup.Provider registries) throws CommandSyntaxException {
         HolderLookup.RegistryLookup<T> registryLookup = registries.lookupOrThrow(this.registryKey);
         List<Holder.Reference<T>> results = registryLookup.listElements().filter((element) -> ResourceSelectorArgument.matches(this.pattern, element.key().identifier())).toList();
         if (results.isEmpty()) {
            throw ResourceSelectorArgument.ERROR_NO_MATCHES.create(this.pattern, this.registryKey.identifier());
         } else {
            return results;
         }
      }
   }
}
