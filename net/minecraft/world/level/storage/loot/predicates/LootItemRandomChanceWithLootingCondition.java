package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemRandomChanceWithLootingCondition implements LootItemCondition {
   private final float percent;
   private final float lootingMultiplier;

   private LootItemRandomChanceWithLootingCondition(float var1, float var2) {
      this.percent = var1;
      this.lootingMultiplier = var2;
   }

   public Set getReferencedContextParams() {
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

   // $FF: synthetic method
   LootItemRandomChanceWithLootingCondition(float var1, float var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("random_chance_with_looting"), LootItemRandomChanceWithLootingCondition.class);
      }

      public void serialize(JsonObject var1, LootItemRandomChanceWithLootingCondition var2, JsonSerializationContext var3) {
         var1.addProperty("chance", var2.percent);
         var1.addProperty("looting_multiplier", var2.lootingMultiplier);
      }

      public LootItemRandomChanceWithLootingCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return new LootItemRandomChanceWithLootingCondition(GsonHelper.getAsFloat(var1, "chance"), GsonHelper.getAsFloat(var1, "looting_multiplier"));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
