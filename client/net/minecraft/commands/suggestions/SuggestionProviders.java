package net.minecraft.commands.suggestions;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;

public class SuggestionProviders {
   public SuggestionProviders() {
      super();
   }

   public static SuggestionProvider<CommandSourceStack> serverOnly(final ServerOnlyCustomSuggestion provider) {
      return provider.toBrigadier();
   }
}
