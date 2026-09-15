package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootContext;

public class AnyOfCondition extends CompositeLootItemCondition {
   public static final MapCodec<AnyOfCondition> MAP_CODEC = createCodec(AnyOfCondition::new);

   private AnyOfCondition(final HolderSet<LootItemCondition> terms) {
      super(terms, combine(terms));
   }

   private static Predicate<LootContext> combine(final HolderSet<LootItemCondition> terms) {
      return !terms.isBound() ? (context) -> {
         for(Holder<LootItemCondition> entry : terms) {
            if (((LootItemCondition)entry.value()).test(context)) {
               return true;
            }
         }

         return false;
      } : Util.anyOf(holdersToLazyPredicates(terms));
   }

   public MapCodec<AnyOfCondition> codec() {
      return MAP_CODEC;
   }

   public static Builder anyOf(final LootItemCondition.Builder... terms) {
      return new Builder(terms);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(final LootItemCondition.Builder... terms) {
         super(terms);
      }

      public Builder or(final LootItemCondition.Builder term) {
         this.addTerm(term);
         return this;
      }

      public Builder or(final Holder<LootItemCondition> term) {
         this.addTerm(term);
         return this;
      }

      protected LootItemCondition create(final HolderSet<LootItemCondition> terms) {
         return new AnyOfCondition(terms);
      }
   }
}
