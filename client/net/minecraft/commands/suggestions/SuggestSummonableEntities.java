package net.minecraft.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public class SuggestSummonableEntities implements SerializableCustomSuggestion {
   private static final SuggestSummonableEntities INSTANCE = new SuggestSummonableEntities();
   public static final SerializableCustomSuggestion.Type<SuggestSummonableEntities> TYPE;

   public SuggestSummonableEntities() {
      super();
   }

   public SerializableCustomSuggestion.Type<SuggestSummonableEntities> type() {
      return TYPE;
   }

   public static SuggestionProvider<CommandSourceStack> suggestSummonableEntities() {
      return INSTANCE.toBrigadier();
   }

   public CompletableFuture<Suggestions> generate(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      return SuggestionContext.suggestResource(BuiltInRegistries.ENTITY_TYPE.stream().filter((entityType) -> entityType.isEnabled(((SuggestionContext)context.getSource()).enabledFeatures()) && entityType.canSummon()), builder, EntityType::getKey, EntityType::getDescription);
   }

   static {
      TYPE = SerializableCustomSuggestion.<SuggestSummonableEntities>singletonType(INSTANCE);
   }
}
