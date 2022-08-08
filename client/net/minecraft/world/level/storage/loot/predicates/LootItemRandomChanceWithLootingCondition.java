package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemRandomChanceWithLootingCondition implements LootItemCondition {
   final float percent;
   final float lootingMultiplier;

   LootItemRandomChanceWithLootingCondition(float var1, float var2) {
      super();
      this.percent = var1;
      this.lootingMultiplier = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE_WITH_LOOTING;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
   }

   public boolean test(LootContext var1) {
      Entity var2 = (Entity)var1.getParamOrNull(LootContextParams.KILLER_ENTITY);
      int var3 = 0;
      if (var2 instanceof LivingEntity) {
         var3 = EnchantmentHelper.getMobLooting((LivingEntity)var2);
      }

      return var1.getRandom().nextFloat() < this.percent + (float)var3 * this.lootingMultiplier;
   }

   public static LootItemCondition.Builder randomChanceAndLootingBoost(float var0, float var1) {
      return () -> {
         return new LootItemRandomChanceWithLootingCondition(var0, var1);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceWithLootingCondition> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LootItemRandomChanceWithLootingCondition var2, JsonSerializationContext var3) {
         var1.addProperty("chance", var2.percent);
         var1.addProperty("looting_multiplier", var2.lootingMultiplier);
      }

      public LootItemRandomChanceWithLootingCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return new LootItemRandomChanceWithLootingCondition(GsonHelper.getAsFloat(var1, "chance"), GsonHelper.getAsFloat(var1, "looting_multiplier"));
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
