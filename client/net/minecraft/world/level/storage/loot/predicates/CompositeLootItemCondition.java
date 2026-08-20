package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class CompositeLootItemCondition implements LootItemCondition {
   protected final HolderSet<LootItemCondition> terms;
   private final Predicate<LootContext> composedPredicate;

   protected CompositeLootItemCondition(final HolderSet<LootItemCondition> terms, final Predicate<LootContext> composedPredicate) {
      super();
      this.terms = terms;
      this.composedPredicate = composedPredicate;
   }

   protected static List<Predicate<LootContext>> holdersToLazyPredicates(final HolderSet<LootItemCondition> terms) {
      return terms.stream().map(CompositeLootItemCondition::holderToLazyPredicate).toList();
   }

   private static Predicate<LootContext> holderToLazyPredicate(final Holder<LootItemCondition> h) {
      return (context) -> ((LootItemCondition)h.value()).test(context);
   }

   public abstract MapCodec<? extends CompositeLootItemCondition> codec();

   protected static <T extends CompositeLootItemCondition> MapCodec<T> createCodec(final Function<HolderSet<LootItemCondition>, T> factory) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(LootItemCondition.LIST_CODEC.fieldOf("terms").forGetter((condition) -> condition.terms)).apply(i, factory));
   }

   public final boolean test(final LootContext context) {
      return this.composedPredicate.test(context);
   }

   public void validate(final ValidationContext output) {
      LootItemCondition.super.validate(output);
      Validatable.validateHolderSet(output, "terms", this.terms);
   }

   public abstract static class Builder implements LootItemCondition.Builder {
      private final ImmutableList.Builder<Holder<LootItemCondition>> terms = ImmutableList.builder();

      protected Builder(final LootItemCondition.Builder... terms) {
         super();

         for(LootItemCondition.Builder term : terms) {
            this.terms.add(Holder.direct(term.build()));
         }

      }

      public void addTerm(final LootItemCondition.Builder term) {
         this.addTerm(Holder.direct(term.build()));
      }

      public void addTerm(final Holder<LootItemCondition> term) {
         this.terms.add(term);
      }

      public LootItemCondition build() {
         return this.create(HolderSet.direct(this.terms.build()));
      }

      protected abstract LootItemCondition create(HolderSet<LootItemCondition> terms);
   }
}
