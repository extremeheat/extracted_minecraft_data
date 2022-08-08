package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ValueCheckCondition implements LootItemCondition {
   final NumberProvider provider;
   final IntRange range;

   ValueCheckCondition(NumberProvider var1, IntRange var2) {
      super();
      this.provider = var1;
      this.range = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.VALUE_CHECK;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.provider.getReferencedContextParams(), this.range.getReferencedContextParams());
   }

   public boolean test(LootContext var1) {
      return this.range.test(var1, this.provider.getInt(var1));
   }

   public static LootItemCondition.Builder hasValue(NumberProvider var0, IntRange var1) {
      return () -> {
         return new ValueCheckCondition(var0, var1);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ValueCheckCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ValueCheckCondition var2, JsonSerializationContext var3) {
         var1.add("value", var3.serialize(var2.provider));
         var1.add("range", var3.serialize(var2.range));
      }

      public ValueCheckCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         NumberProvider var3 = (NumberProvider)GsonHelper.getAsObject(var1, "value", var2, NumberProvider.class);
         IntRange var4 = (IntRange)GsonHelper.getAsObject(var1, "range", var2, IntRange.class);
         return new ValueCheckCondition(var3, var4);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
