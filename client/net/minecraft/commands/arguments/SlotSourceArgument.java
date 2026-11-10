package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.slot.RangeSlotSource;
import net.minecraft.world.item.slot.SlotSource;
import net.minecraft.world.item.slot.SlotSources;

public class SlotSourceArgument implements SerializableArgument<SlotResult>, ValidatableArgument {
   public static final SerializableArgument.Type<SlotSourceArgument> TYPE = ValidatableArgument.codecWithValidation(SlotSourceArgument::new);
   private static final Collection<String> EXAMPLES;
   private final RawArgument holderArgument;

   private SlotSourceArgument(final ContextKeySet contextKeySet) {
      super();
      this.holderArgument = new RawArgument(contextKeySet);
   }

   public SerializableArgument.Type<SlotSourceArgument> type() {
      return TYPE;
   }

   public ContextKeySet contextKeySet() {
      return this.holderArgument.contextKeySet();
   }

   public static ArgumentType<SlotResult> slotSource(final ContextKeySet contextKeySet) {
      return (new SlotSourceArgument(contextKeySet)).toBrigadier();
   }

   public static SlotResult getSlotSource(final CommandContext<?> context, final String name) {
      return (SlotResult)context.getArgument(name, SlotResult.class);
   }

   public SlotResult parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      int start = reader.getCursor();
      SlotRange slotRange = SlotRanges.tryRead(reader);
      if (slotRange != null) {
         return this.wrapRawSlotRange(context, slotRange);
      } else {
         reader.setCursor(start);
         ValidationWrapper<SlotSource> result = (ValidationWrapper)this.holderArgument.parse(reader, context);
         return new SlotResult(result, Optional.empty());
      }
   }

   private SlotResult wrapRawSlotRange(final ParseContext parseContext, final SlotRange slotRange) throws CommandSyntaxException {
      return new SlotResult(parseContext.getOrCreateValidationWrapper(Holder.direct(RangeSlotSource.slotRange(slotRange)), this.contextKeySet()), Optional.of(slotRange));
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      SuggestionsBuilder sub = builder.restart();
      SuggestionContext.suggest(SlotRanges.allNames(), sub);
      builder.add(sub);
      return this.holderArgument.listSuggestions(context, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      EXAMPLES = Util.<String>join(ResourceOrIdArgument.EXAMPLES, SlotsArgument.EXAMPLES);
   }

   public static record SlotResult(ValidationWrapper<SlotSource> value, Optional<SlotRange> originalSlotRange) {
      public SlotResult {
         super();
      }

      public Optional<String> name() {
         return this.originalSlotRange.isPresent() ? this.originalSlotRange.map(StringRepresentable::getSerializedName) : this.value.getRegisteredNameIfPresent();
      }
   }

   private static class RawArgument extends ValidatableResourceOrIdArgument<SlotSource> {
      public static final SerializableArgument.Type<RawArgument> TYPE = ValidatableArgument.codecWithValidation(RawArgument::new);

      private RawArgument(final ContextKeySet contextKeySet) {
         super(Registries.SLOT_SOURCE, SlotSources.DIRECT_CODEC, contextKeySet);
      }

      public SerializableArgument.Type<RawArgument> type() {
         return TYPE;
      }
   }
}
