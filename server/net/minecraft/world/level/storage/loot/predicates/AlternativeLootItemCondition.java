package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class AlternativeLootItemCondition implements LootItemCondition {
   private final LootItemCondition[] terms;
   private final Predicate<LootContext> composedPredicate;

   private AlternativeLootItemCondition(LootItemCondition[] var1) {
      super();
      this.terms = var1;
      this.composedPredicate = LootItemConditions.orConditions(var1);
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ALTERNATIVE;
   }

   public final boolean test(LootContext var1) {
      return this.composedPredicate.test(var1);
   }

   public void validate(ValidationContext var1) {
      LootItemCondition.super.validate(var1);

      for(int var2 = 0; var2 < this.terms.length; ++var2) {
         this.terms[var2].validate(var1.forChild(".term[" + var2 + "]"));
      }

   }

   public static AlternativeLootItemCondition.Builder alternative(LootItemCondition.Builder... var0) {
      return new AlternativeLootItemCondition.Builder(var0);
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   AlternativeLootItemCondition(LootItemCondition[] var1, Object var2) {
      this(var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AlternativeLootItemCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, AlternativeLootItemCondition var2, JsonSerializationContext var3) {
         var1.add("terms", var3.serialize(var2.terms));
      }

      public AlternativeLootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LootItemCondition[] var3 = (LootItemCondition[])GsonHelper.getAsObject(var1, "terms", var2, LootItemCondition[].class);
         return new AlternativeLootItemCondition(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   public static class Builder implements LootItemCondition.Builder {
      private final List<LootItemCondition> terms = Lists.newArrayList();

      public Builder(LootItemCondition.Builder... var1) {
         super();
         LootItemCondition.Builder[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootItemCondition.Builder var5 = var2[var4];
            this.terms.add(var5.build());
         }

      }

      public AlternativeLootItemCondition.Builder or(LootItemCondition.Builder var1) {
         this.terms.add(var1.build());
         return this;
      }

      public LootItemCondition build() {
         return new AlternativeLootItemCondition((LootItemCondition[])this.terms.toArray(new LootItemCondition[0]));
      }
   }
}
