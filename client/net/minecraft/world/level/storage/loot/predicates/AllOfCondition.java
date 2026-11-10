package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.TriBool;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jspecify.annotations.Nullable;

public class AllOfCondition extends CompositeLootItemCondition {
   public static final MapCodec<AllOfCondition> MAP_CODEC = createCodec(AllOfCondition::new);

   private AllOfCondition(final HolderSet<LootItemCondition> terms) {
      super(terms, combine(terms));
   }

   private static Predicate<LootContext> combine(final HolderSet<LootItemCondition> terms) {
      return !terms.isBound() ? (context) -> {
         for(Holder<LootItemCondition> entry : terms) {
            if (!((LootItemCondition)entry.value()).test(context)) {
               return false;
            }
         }

         return true;
      } : Util.allOf(holdersToLazyPredicates(terms));
   }

   public @Nullable Boolean constantValue(final LootItemCondition.ConstantConditionEvaluationContext context) {
      Boolean result = true;

      for(Holder<LootItemCondition> term : this.terms) {
         LootItemCondition subCondition = term.value();
         if (!context.push(subCondition)) {
            return null;
         }

         result = TriBool.and(result, subCondition.constantValue(context));
         context.pop(subCondition);
      }

      return result;
   }

   public static AllOfCondition allOf(final HolderSet<LootItemCondition> terms) {
      return new AllOfCondition(terms);
   }

   public MapCodec<AllOfCondition> codec() {
      return MAP_CODEC;
   }

   public static Builder allOf(final LootItemCondition.Builder... terms) {
      return new Builder(terms);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(final LootItemCondition.Builder... terms) {
         super(terms);
      }

      public Builder and(final LootItemCondition.Builder term) {
         this.addTerm(term);
         return this;
      }

      protected LootItemCondition create(final HolderSet<LootItemCondition> terms) {
         return new AllOfCondition(terms);
      }
   }
}
