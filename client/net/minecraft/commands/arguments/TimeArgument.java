package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class TimeArgument implements SerializableArgument.ContextFree<Integer> {
   private static final Integer MIN = -2147483648;
   private static final Integer MAX = 2147483647;
   private static final MapCodec<TimeArgument> MAP_CODEC;
   private static final StreamCodec<ByteBuf, TimeArgument> STREAM_CODEC;
   public static final SerializableArgument.Type<TimeArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   private static final SimpleCommandExceptionType ERROR_INVALID_UNIT;
   private static final Dynamic2CommandExceptionType ERROR_TICK_COUNT_TOO_LOW;
   private static final Dynamic2CommandExceptionType ERROR_TICK_COUNT_TOO_HIGH;
   private static final Object2IntMap<String> UNITS;
   private final MinMaxBounds.Bounds<Integer> bounds;

   private TimeArgument(final MinMaxBounds.Bounds<Integer> bounds) {
      super();
      this.bounds = bounds;
   }

   public SerializableArgument.Type<TimeArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Integer> time() {
      return (new TimeArgument(MinMaxBounds.Bounds.any())).toBrigadier();
   }

   public static ArgumentType<Integer> time(final int minimum) {
      return (new TimeArgument(MinMaxBounds.Bounds.atLeast(minimum))).toBrigadier();
   }

   public static ArgumentType<Integer> time(final int minimum, final int maximum) {
      return (new TimeArgument(MinMaxBounds.Bounds.between(minimum, maximum))).toBrigadier();
   }

   public Integer parse(final StringReader reader) throws CommandSyntaxException {
      double value = reader.readDouble();
      String unit = reader.readUnquotedString();
      int factor = UNITS.getOrDefault(unit, 0);
      if (factor == 0) {
         throw ERROR_INVALID_UNIT.createWithContext(reader);
      } else {
         long ticks = Math.round(value * (double)factor);
         Integer minimum = (Integer)this.bounds.min().orElse(MIN);
         if (ticks < (long)minimum) {
            throw ERROR_TICK_COUNT_TOO_LOW.createWithContext(reader, ticks, minimum);
         } else {
            Integer maximum = (Integer)this.bounds.max().orElse(MAX);
            if (ticks > (long)maximum) {
               throw ERROR_TICK_COUNT_TOO_HIGH.createWithContext(reader, ticks, maximum);
            } else {
               return (int)ticks;
            }
         }
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      StringReader reader = new StringReader(builder.getRemaining());

      try {
         reader.readFloat();
      } catch (CommandSyntaxException var5) {
         return builder.buildFuture();
      }

      return SuggestionContext.suggest(UNITS.keySet(), builder.createOffset(builder.getStart() + reader.getCursor()));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      MAP_CODEC = MinMaxBounds.Bounds.createMapCodec(Codec.INT).xmap(TimeArgument::new, (arg) -> arg.bounds);
      STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.INT).map(TimeArgument::new, (arg) -> arg.bounds);
      TYPE = new SerializableArgument.Type<TimeArgument>(MAP_CODEC, STREAM_CODEC);
      EXAMPLES = List.of("0d", "0s", "0t", "0");
      ERROR_INVALID_UNIT = new SimpleCommandExceptionType(Component.translatable("argument.time.invalid_unit"));
      ERROR_TICK_COUNT_TOO_LOW = new Dynamic2CommandExceptionType((value, limit) -> Component.translatableEscape("argument.time.tick_count_too_low", limit, value));
      ERROR_TICK_COUNT_TOO_HIGH = new Dynamic2CommandExceptionType((value, limit) -> Component.translatableEscape("argument.time.tick_count_too_high", limit, value));
      UNITS = Object2IntMap.ofEntries(new Object2IntMap.Entry[]{Object2IntMap.entry("d", 24000), Object2IntMap.entry("s", 20), Object2IntMap.entry("t", 1), Object2IntMap.entry("", 1)});
   }
}
