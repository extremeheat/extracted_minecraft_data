package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionArgument implements SerializableArgument.ContextFree<ResourceKey<Level>> {
   private static final DimensionArgument INSTANCE = new DimensionArgument();
   public static final SerializableArgument.Type<DimensionArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

   private DimensionArgument() {
      super();
   }

   public SerializableArgument.Type<DimensionArgument> type() {
      return TYPE;
   }

   public ResourceKey<Level> parse(final StringReader reader) throws CommandSyntaxException {
      return ResourceKey.create(Registries.DIMENSION, Identifier.read(reader));
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggestResource(((SuggestionContext)context.getSource()).levels().stream().map(ResourceKey::identifier), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static ArgumentType<ResourceKey<Level>> dimension() {
      return INSTANCE.toBrigadier();
   }

   public static ServerLevel getDimension(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      ResourceKey<Level> key = (ResourceKey)context.getArgument(name, ResourceKey.class);
      ServerLevel level = ((CommandSourceStack)context.getSource()).getServer().getLevel(key);
      if (level == null) {
         throw ERROR_INVALID_VALUE.create(key.identifier());
      } else {
         return level;
      }
   }

   static {
      TYPE = SerializableArgument.<DimensionArgument>singletonType(INSTANCE);
      EXAMPLES = (Collection)Stream.of(Level.OVERWORLD, Level.NETHER).map((key) -> key.identifier().toString()).collect(Collectors.toList());
      ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.dimension.invalid", value));
   }
}
