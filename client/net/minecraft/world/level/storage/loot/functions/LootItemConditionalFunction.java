package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootItemConditionalFunction implements LootItemFunction {
   protected final Optional<Holder<LootItemCondition>> condition;

   protected LootItemConditionalFunction(final Optional<Holder<LootItemCondition>> condition) {
      super();
      this.condition = condition;
   }

   public abstract MapCodec<? extends LootItemConditionalFunction> codec();

   protected static <T extends LootItemConditionalFunction> Products.P1<RecordCodecBuilder.Mu<T>, Optional<Holder<LootItemCondition>>> commonFields(final RecordCodecBuilder.Instance<T> i) {
      return i.group(LootItemCondition.CODEC.optionalFieldOf("condition").forGetter((f) -> f.condition));
   }

   public final ItemStack apply(final ItemStack itemStack, final LootContext context) {
      return !this.condition.isEmpty() && !((LootItemCondition)((Holder)this.condition.get()).value()).test(context) ? itemStack : this.run(itemStack, context);
   }

   protected abstract ItemStack run(final ItemStack itemStack, final LootContext context);

   public void validate(final ValidationContext context) {
      LootItemFunction.super.validate(context);
      Validatable.validateHolder(context, "condition", this.condition);
   }

   protected static Builder<?> simpleBuilder(final Function<Optional<Holder<LootItemCondition>>, LootItemFunction> constructor) {
      return new DummyBuilder(constructor);
   }

   public abstract static class Builder<T extends Builder<T>> implements LootItemFunction.Builder, ConditionUserBuilder<T> {
      private final ImmutableList.Builder<Holder<LootItemCondition>> conditions = ImmutableList.builder();

      public Builder() {
         super();
      }

      public T when(final Holder<LootItemCondition> condition) {
         this.conditions.add(condition);
         return (T)this.getThis();
      }

      public final T unwrap() {
         return (T)this.getThis();
      }

      protected abstract T getThis();

      protected Optional<Holder<LootItemCondition>> getCondition() {
         return ConditionUserBuilder.buildCondition(this.conditions.build());
      }
   }

   private static final class DummyBuilder extends Builder<DummyBuilder> {
      private final Function<Optional<Holder<LootItemCondition>>, LootItemFunction> constructor;

      public DummyBuilder(final Function<Optional<Holder<LootItemCondition>>, LootItemFunction> constructor) {
         super();
         this.constructor = constructor;
      }

      protected DummyBuilder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return (LootItemFunction)this.constructor.apply(this.getCondition());
      }
   }
}
