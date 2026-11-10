package net.minecraft.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public non-sealed interface ServerOnlyCustomSuggestion extends CustomSuggestion {
   default CompletableFuture<Suggestions> generate(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      MinecraftServer server = (MinecraftServer)Objects.requireNonNull(((SuggestionContext)context.getSource()).server(), "This class can only run in server suggestion context");
      return this.generate(server, context, builder);
   }

   CompletableFuture<Suggestions> generate(MinecraftServer server, CommandContext<? extends SuggestionContext> context, SuggestionsBuilder builder) throws CommandSyntaxException;

   default SerializableCustomSuggestion serializable() {
      return AskServer.INSTANCE;
   }
}
