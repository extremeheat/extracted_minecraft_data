package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
   protected final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositeCondition;

   protected LootPoolEntryContainer(List<LootItemCondition> var1) {
      super();
      this.conditions = var1;
      this.compositeCondition = Util.allOf(var1);
   }

   protected static <T extends LootPoolEntryContainer> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(RecordCodecBuilder.Instance<T> var0) {
      return var0.group(LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((var0x) -> var0x.conditions));
   }

   public void validate(ValidationContext var1) {
      for(int var2 = 0; var2 < this.conditions.size(); ++var2) {
         ((LootItemCondition)this.conditions.get(var2)).validate(var1.forChild(".condition[" + var2 + "]"));
      }

   }

   protected final boolean canRun(LootContext var1) {
      return this.compositeCondition.test(var1);
   }

   public abstract LootPoolEntryType getType();

   public abstract static class Builder<T extends Builder<T>> implements ConditionUserBuilder<T> {
      private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();

      public Builder() {
         super();
      }

      protected abstract T getThis();

      public T when(LootItemCondition.Builder var1) {
         this.conditions.add(var1.build());
         return (T)this.getThis();
      }

      public final T unwrap() {
         return (T)this.getThis();
      }

      protected List<LootItemCondition> getConditions() {
         return this.conditions.build();
      }

      public AlternativesEntry.Builder otherwise(Builder<?> var1) {
         return new AlternativesEntry.Builder(new Builder[]{this, var1});
      }

      public EntryGroup.Builder append(Builder<?> var1) {
         return new EntryGroup.Builder(new Builder[]{this, var1});
      }

      public SequentialEntry.Builder then(Builder<?> var1) {
         return new SequentialEntry.Builder(new Builder[]{this, var1});
      }

      public abstract LootPoolEntryContainer build();

      // $FF: synthetic method
      public ConditionUserBuilder unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public ConditionUserBuilder when(final LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }
}
