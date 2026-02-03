package net.minecraft.world.entity.variant;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public interface PriorityProvider<Context, Condition extends PriorityProvider.SelectorCondition<Context>> {
   List<Selector<Context, Condition>> selectors();

   static <C, T> Stream<T> select(final Stream<T> entries, final Function<T, PriorityProvider<C, ?>> extractor, final C context) {
      List<UnpackedEntry<C, T>> unpackedEntries = new ArrayList();
      entries.forEach((entryx) -> {
         PriorityProvider<C, ?> provider = (PriorityProvider)extractor.apply(entryx);

         for(Selector<C, ?> selector : provider.selectors()) {
            unpackedEntries.add(new UnpackedEntry(entryx, selector.priority(), (SelectorCondition)DataFixUtils.orElseGet(selector.condition(), SelectorCondition::alwaysTrue)));
         }

      });
      unpackedEntries.sort(PriorityProvider.UnpackedEntry.HIGHEST_PRIORITY_FIRST);
      Iterator<UnpackedEntry<C, T>> iterator = unpackedEntries.iterator();
      int highestMatchedPriority = -2147483648;

      while(iterator.hasNext()) {
         UnpackedEntry<C, T> entry = (UnpackedEntry)iterator.next();
         if (entry.priority < highestMatchedPriority) {
            iterator.remove();
         } else if (entry.condition.test(context)) {
            highestMatchedPriority = entry.priority;
         } else {
            iterator.remove();
         }
      }

      return unpackedEntries.stream().map(UnpackedEntry::entry);
   }

   static <C, T> Optional<T> pick(final Stream<T> entries, final Function<T, PriorityProvider<C, ?>> extractor, final RandomSource randomSource, final C context) {
      List<T> selected = select(entries, extractor, context).toList();
      return Util.<T>getRandomSafe(selected, randomSource);
   }

   static <Context, Condition extends SelectorCondition<Context>> List<Selector<Context, Condition>> single(final Condition check, final int priority) {
      return List.of(new Selector(check, priority));
   }

   static <Context, Condition extends SelectorCondition<Context>> List<Selector<Context, Condition>> alwaysTrue(final int priority) {
      return List.of(new Selector(Optional.empty(), priority));
   }

   public static record Selector<Context, Condition extends SelectorCondition<Context>>(Optional<Condition> condition, int priority) {
      public Selector(final Condition condition, final int priority) {
         this(Optional.of(condition), priority);
      }

      public Selector(final int priority) {
         this(Optional.empty(), priority);
      }

      public Selector {
         super();
      }

      public static <Context, Condition extends SelectorCondition<Context>> Codec<Selector<Context, Condition>> codec(final Codec<Condition> conditionCodec) {
         return RecordCodecBuilder.create((i) -> i.group(conditionCodec.optionalFieldOf("condition").forGetter(Selector::condition), Codec.INT.fieldOf("priority").forGetter(Selector::priority)).apply(i, Selector::new));
      }
   }

   @FunctionalInterface
   public interface SelectorCondition<C> extends Predicate<C> {
      static <C> SelectorCondition<C> alwaysTrue() {
         return (context) -> true;
      }
   }

   public static record UnpackedEntry<C, T>(T entry, int priority, SelectorCondition<C> condition) {
      public static final Comparator<UnpackedEntry<?, ?>> HIGHEST_PRIORITY_FIRST = Comparator.comparingInt(UnpackedEntry::priority).reversed();

      public UnpackedEntry {
         super();
      }
   }
}
