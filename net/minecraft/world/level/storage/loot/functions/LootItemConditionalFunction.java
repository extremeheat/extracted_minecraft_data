package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
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
   private final Predicate compositePredicates;

   protected LootItemConditionalFunction(LootItemCondition[] var1) {
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

   protected static LootItemConditionalFunction.Builder simpleBuilder(Function var0) {
      return new LootItemConditionalFunction.DummyBuilder(var0);
   }

   // $FF: synthetic method
   public Object apply(Object var1, Object var2) {
      return this.apply((ItemStack)var1, (LootContext)var2);
   }

   public abstract static class Serializer extends LootItemFunction.Serializer {
      public Serializer(ResourceLocation var1, Class var2) {
         super(var1, var2);
      }

      public void serialize(JsonObject var1, LootItemConditionalFunction var2, JsonSerializationContext var3) {
         if (!ArrayUtils.isEmpty(var2.predicates)) {
            var1.add("conditions", var3.serialize(var2.predicates));
         }

      }

      public final LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LootItemCondition[] var3 = (LootItemCondition[])GsonHelper.getAsObject(var1, "conditions", new LootItemCondition[0], var2, LootItemCondition[].class);
         return this.deserialize(var1, var2, var3);
      }

      public abstract LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);

      // $FF: synthetic method
      public LootItemFunction deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   static final class DummyBuilder extends LootItemConditionalFunction.Builder {
      private final Function constructor;

      public DummyBuilder(Function var1) {
         this.constructor = var1;
      }

      protected LootItemConditionalFunction.DummyBuilder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return (LootItemFunction)this.constructor.apply(this.getConditions());
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public abstract static class Builder implements LootItemFunction.Builder, ConditionUserBuilder {
      private final List conditions = Lists.newArrayList();

      public LootItemConditionalFunction.Builder when(LootItemCondition.Builder var1) {
         this.conditions.add(var1.build());
         return this.getThis();
      }

      public final LootItemConditionalFunction.Builder unwrap() {
         return this.getThis();
      }

      protected abstract LootItemConditionalFunction.Builder getThis();

      protected LootItemCondition[] getConditions() {
         return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
      }

      // $FF: synthetic method
      public Object unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public Object when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }
}
