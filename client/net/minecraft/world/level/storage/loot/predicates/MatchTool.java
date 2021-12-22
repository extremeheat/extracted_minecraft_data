package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class MatchTool implements LootItemCondition {
   final ItemPredicate predicate;

   public MatchTool(ItemPredicate var1) {
      super();
      this.predicate = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.MATCH_TOOL;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext var1) {
      ItemStack var2 = (ItemStack)var1.getParamOrNull(LootContextParams.TOOL);
      return var2 != null && this.predicate.matches(var2);
   }

   public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder var0) {
      return () -> {
         return new MatchTool(var0.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MatchTool> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, MatchTool var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
      }

      public MatchTool deserialize(JsonObject var1, JsonDeserializationContext var2) {
         ItemPredicate var3 = ItemPredicate.fromJson(var1.get("predicate"));
         return new MatchTool(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
