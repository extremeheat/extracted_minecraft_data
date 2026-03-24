package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer, Validatable {
   protected final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositeCondition;

   protected LootPoolEntryContainer(final List<LootItemCondition> conditions) {
      super();
      this.conditions = conditions;
      this.compositeCondition = Util.allOf(conditions);
   }

   protected static <T extends LootPoolEntryContainer> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(final RecordCodecBuilder.Instance<T> i) {
      return i.group(LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((e) -> e.conditions));
   }

   public void validate(final ValidationContext output) {
      Validatable.validate(output, "conditions", this.conditions);
   }

   protected final boolean canRun(final LootContext context) {
      return this.compositeCondition.test(context);
   }

   public abstract MapCodec<? extends LootPoolEntryContainer> codec();

   public abstract static class Builder<T extends Builder<T>> implements ConditionUserBuilder<T> {
      private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();

      public Builder() {
         super();
      }

      protected abstract T getThis();

      public T when(final LootItemCondition.Builder condition) {
         this.conditions.add(condition.build());
         return (T)this.getThis();
      }

      public final T unwrap() {
         return (T)this.getThis();
      }

      protected List<LootItemCondition> getConditions() {
         return this.conditions.build();
      }

      public AlternativesEntry.Builder otherwise(final Builder<?> other) {
         return new AlternativesEntry.Builder(new Builder[]{this, other});
      }

      public EntryGroup.Builder append(final Builder<?> other) {
         return new EntryGroup.Builder(new Builder[]{this, other});
      }

      public SequentialEntry.Builder then(final Builder<?> other) {
         return new SequentialEntry.Builder(new Builder[]{this, other});
      }

      public abstract LootPoolEntryContainer build();
   }
}
