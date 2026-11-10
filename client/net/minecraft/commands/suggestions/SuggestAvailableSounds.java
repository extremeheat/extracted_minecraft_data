package net.minecraft.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;

public class SuggestAvailableSounds implements SerializableCustomSuggestion {
   private static final SuggestAvailableSounds INSTANCE = new SuggestAvailableSounds();
   public static final SerializableCustomSuggestion.Type<SuggestAvailableSounds> TYPE;

   public SuggestAvailableSounds() {
      super();
   }

   public SerializableCustomSuggestion.Type<SuggestAvailableSounds> type() {
      return TYPE;
   }

   public static SuggestionProvider<CommandSourceStack> suggestAvailableSounds() {
      return INSTANCE.toBrigadier();
   }

   public CompletableFuture<Suggestions> generate(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      return SuggestionContext.suggestResource(((SuggestionContext)context.getSource()).availableSounds(), builder);
   }

   static {
      TYPE = SerializableCustomSuggestion.<SuggestAvailableSounds>singletonType(INSTANCE);
   }
}
