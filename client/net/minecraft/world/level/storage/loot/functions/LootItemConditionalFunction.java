package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootItemConditionalFunction implements LootItemFunction {
   protected final List<LootItemCondition> predicates;
   private final Predicate<LootContext> compositePredicates;

   protected LootItemConditionalFunction(List<LootItemCondition> var1) {
      super();
      this.predicates = var1;
      this.compositePredicates = Util.allOf(var1);
   }

   public abstract LootItemFunctionType<? extends LootItemConditionalFunction> getType();

   protected static <T extends LootItemConditionalFunction> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(RecordCodecBuilder.Instance<T> var0) {
      return var0.group(LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((var0x) -> {
         return var0x.predicates;
      }));
   }

   public final ItemStack apply(ItemStack var1, LootContext var2) {
      return this.compositePredicates.test(var2) ? this.run(var1, var2) : var1;
   }

   protected abstract ItemStack run(ItemStack var1, LootContext var2);

   public void validate(ValidationContext var1) {
      LootItemFunction.super.validate(var1);

      for(int var2 = 0; var2 < this.predicates.size(); ++var2) {
         ((LootItemCondition)this.predicates.get(var2)).validate(var1.forChild(".conditions[" + var2 + "]"));
      }

   }

   protected static Builder<?> simpleBuilder(Function<List<LootItemCondition>, LootItemFunction> var0) {
      return new DummyBuilder(var0);
   }

   // $FF: synthetic method
   public Object apply(final Object var1, final Object var2) {
      return this.apply((ItemStack)var1, (LootContext)var2);
   }

   static final class DummyBuilder extends Builder<DummyBuilder> {
      private final Function<List<LootItemCondition>, LootItemFunction> constructor;

      public DummyBuilder(Function<List<LootItemCondition>, LootItemFunction> var1) {
         super();
         this.constructor = var1;
      }

      protected DummyBuilder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return (LootItemFunction)this.constructor.apply(this.getConditions());
      }

      // $FF: synthetic method
      protected Builder getThis() {
         return this.getThis();
      }
   }

   public abstract static class Builder<T extends Builder<T>> implements LootItemFunction.Builder, ConditionUserBuilder<T> {
      private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();

      public Builder() {
         super();
      }

      public T when(LootItemCondition.Builder var1) {
         this.conditions.add(var1.build());
         return this.getThis();
      }

      public final T unwrap() {
         return this.getThis();
      }

      protected abstract T getThis();

      protected List<LootItemCondition> getConditions() {
         return this.conditions.build();
      }

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
