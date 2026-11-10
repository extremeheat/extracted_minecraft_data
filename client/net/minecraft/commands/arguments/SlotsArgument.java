package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public class SlotsArgument implements SerializableArgument.ContextFree<SlotRange> {
   private static final SlotsArgument INSTANCE = new SlotsArgument();
   public static final SerializableArgument.Type<SlotsArgument> TYPE;
   static final Collection<String> EXAMPLES;

   private SlotsArgument() {
      super();
   }

   public SerializableArgument.Type<SlotsArgument> type() {
      return TYPE;
   }

   public static ArgumentType<SlotRange> slots() {
      return INSTANCE.toBrigadier();
   }

   public static SlotRange getSlots(final CommandContext<?> context, final String name) {
      return (SlotRange)context.getArgument(name, SlotRange.class);
   }

   public SlotRange parse(final StringReader reader) throws CommandSyntaxException {
      return SlotRanges.read(reader);
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(SlotRanges.allNames(), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<SlotsArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("container.*", "container.5", "weapon");
   }
}
