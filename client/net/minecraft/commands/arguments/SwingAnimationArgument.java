package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SwingAnimationType;

public class SwingAnimationArgument implements ArgumentType<SwingAnimationType> {
   private static final Collection<String> EXAMPLES;
   private static final SwingAnimationType[] VALUES;
   private static final DynamicCommandExceptionType ERROR_INVALID;

   public SwingAnimationArgument() {
      super();
   }

   public SwingAnimationType parse(final StringReader reader) throws CommandSyntaxException {
      String swingAnimationString = reader.readUnquotedString();
      SwingAnimationType swingAnimation = SwingAnimationType.byName(swingAnimationString, (SwingAnimationType)null);
      if (swingAnimation == null) {
         throw ERROR_INVALID.createWithContext(reader, swingAnimationString);
      } else {
         return swingAnimation;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
      return context.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(Arrays.stream(VALUES).map(SwingAnimationType::getSerializedName), builder) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static SwingAnimationArgument swingAnimationType() {
      return new SwingAnimationArgument();
   }

   public static SwingAnimationType getSwingAnimationType(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return (SwingAnimationType)context.getArgument(name, SwingAnimationType.class);
   }

   static {
      EXAMPLES = (Collection)Stream.of(SwingAnimationType.WHACK, SwingAnimationType.STAB).map(SwingAnimationType::getSerializedName).collect(Collectors.toList());
      VALUES = SwingAnimationType.values();
      ERROR_INVALID = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.swing_animation.invalid", value));
   }
}
