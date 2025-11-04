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

   static <C, T> Stream<T> select(Stream<T> var0, Function<T, PriorityProvider<C, ?>> var1, C var2) {
      ArrayList var3 = new ArrayList();
      var0.forEach((var2x) -> {
         PriorityProvider var3x = (PriorityProvider)var1.apply(var2x);

         for(Selector var5 : var3x.selectors()) {
            var3.add(new UnpackedEntry(var2x, var5.priority(), (SelectorCondition)DataFixUtils.orElseGet(var5.condition(), SelectorCondition::alwaysTrue)));
         }

      });
      var3.sort(PriorityProvider.UnpackedEntry.HIGHEST_PRIORITY_FIRST);
      Iterator var4 = var3.iterator();
      int var5 = -2147483648;

      while(var4.hasNext()) {
         UnpackedEntry var6 = (UnpackedEntry)var4.next();
         if (var6.priority < var5) {
            var4.remove();
         } else if (var6.condition.test(var2)) {
            var5 = var6.priority;
         } else {
            var4.remove();
         }
      }

      return var3.stream().map(UnpackedEntry::entry);
   }

   static <C, T> Optional<T> pick(Stream<T> var0, Function<T, PriorityProvider<C, ?>> var1, RandomSource var2, C var3) {
      List var4 = select(var0, var1, var3).toList();
      return Util.<T>getRandomSafe(var4, var2);
   }

   static <Context, Condition extends SelectorCondition<Context>> List<Selector<Context, Condition>> single(Condition var0, int var1) {
      return List.of(new Selector(var0, var1));
   }

   static <Context, Condition extends SelectorCondition<Context>> List<Selector<Context, Condition>> alwaysTrue(int var0) {
      return List.of(new Selector(Optional.empty(), var0));
   }

   public static record Selector<Context, Condition extends SelectorCondition<Context>>(Optional<Condition> condition, int priority) {
      public Selector(Condition var1, int var2) {
         this(Optional.of(var1), var2);
      }

      public Selector(int var1) {
         this(Optional.empty(), var1);
      }

      public Selector(Optional<Condition> var1, int var2) {
         super();
         this.condition = var1;
         this.priority = var2;
      }

      public static <Context, Condition extends SelectorCondition<Context>> Codec<Selector<Context, Condition>> codec(Codec<Condition> var0) {
         return RecordCodecBuilder.create((var1) -> var1.group(var0.optionalFieldOf("condition").forGetter(Selector::condition), Codec.INT.fieldOf("priority").forGetter(Selector::priority)).apply(var1, Selector::new));
      }
   }

   @FunctionalInterface
   public interface SelectorCondition<C> extends Predicate<C> {
      static <C> SelectorCondition<C> alwaysTrue() {
         return (var0) -> true;
      }
   }

   public static record UnpackedEntry<C, T>(T entry, int priority, SelectorCondition<C> condition) {
      final int priority;
      final SelectorCondition<C> condition;
      public static final Comparator<UnpackedEntry<?, ?>> HIGHEST_PRIORITY_FIRST = Comparator.comparingInt(UnpackedEntry::priority).reversed();

      public UnpackedEntry(T var1, int var2, SelectorCondition<C> var3) {
         super();
         this.entry = var1;
         this.priority = var2;
         this.condition = var3;
      }
   }
}
