package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootItemConditionalFunction implements LootItemFunction {
   protected final LootItemCondition[] predicates;
   private final Predicate<LootContext> compositePredicates;

   protected LootItemConditionalFunction(LootItemCondition[] var1) {
      super();
      this.predicates = var1;
      this.compositePredicates = LootItemConditions.andConditions(var1);
   }

   public final ItemStack apply(ItemStack var1, LootContext var2) {
      return this.compositePredicates.test(var2) ? this.run(var1, var2) : var1;
   }

   protected abstract ItemStack run(ItemStack var1, LootContext var2);

   public void validate(ValidationContext var1) {
      LootItemFunction.super.validate(var1);

      for(int var2 = 0; var2 < this.predicates.length; ++var2) {
         this.predicates[var2].validate(var1.forChild(".conditions[" + var2 + "]"));
      }

   }

   protected static Builder<?> simpleBuilder(Function<LootItemCondition[], LootItemFunction> var0) {
      return new DummyBuilder(var0);
   }

   // $FF: synthetic method
   public Object apply(Object var1, Object var2) {
      return this.apply((ItemStack)var1, (LootContext)var2);
   }

   static final class DummyBuilder extends Builder<DummyBuilder> {
      private final Function<LootItemCondition[], LootItemFunction> constructor;

      public DummyBuilder(Function<LootItemCondition[], LootItemFunction> var1) {
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

   public abstract static class Serializer<T extends LootItemConditionalFunction> implements net.minecraft.world.level.storage.loot.Serializer<T> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, T var2, JsonSerializationContext var3) {
         if (!ArrayUtils.isEmpty(var2.predicates)) {
            var1.add("conditions", var3.serialize(var2.predicates));
         }

      }

      public final T deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LootItemCondition[] var3 = (LootItemCondition[])GsonHelper.getAsObject(var1, "conditions", new LootItemCondition[0], var2, LootItemCondition[].class);
         return this.deserialize(var1, var2, var3);
      }

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   public abstract static class Builder<T extends Builder<T>> implements LootItemFunction.Builder, ConditionUserBuilder<T> {
      private final List<LootItemCondition> conditions = Lists.newArrayList();

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

      protected LootItemCondition[] getConditions() {
         return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
      }

      // $FF: synthetic method
      public ConditionUserBuilder unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public ConditionUserBuilder when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }
}
