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
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SwingAnimationType;

public class SwingAnimationArgument implements SerializableArgument.ContextFree<SwingAnimationType> {
   private static final SwingAnimationArgument INSTANCE = new SwingAnimationArgument();
   public static final SerializableArgument.Type<SwingAnimationArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final SwingAnimationType[] VALUES;
   private static final DynamicCommandExceptionType ERROR_INVALID;

   private SwingAnimationArgument() {
      super();
   }

   public SerializableArgument.Type<SwingAnimationArgument> type() {
      return TYPE;
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

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(Arrays.stream(VALUES).map(SwingAnimationType::getSerializedName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static ArgumentType<SwingAnimationType> swingAnimationType() {
      return INSTANCE.toBrigadier();
   }

   public static SwingAnimationType getSwingAnimationType(final CommandContext<?> context, final String name) {
      return (SwingAnimationType)context.getArgument(name, SwingAnimationType.class);
   }

   static {
      TYPE = SerializableArgument.<SwingAnimationArgument>singletonType(INSTANCE);
      EXAMPLES = (Collection)Stream.of(SwingAnimationType.WHACK, SwingAnimationType.STAB).map(SwingAnimationType::getSerializedName).collect(Collectors.toList());
      VALUES = SwingAnimationType.values();
      ERROR_INVALID = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.swing_animation.invalid", value));
   }
}
