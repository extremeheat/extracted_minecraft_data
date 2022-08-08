package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootingEnchantFunction extends LootItemConditionalFunction {
   public static final int NO_LIMIT = 0;
   final NumberProvider value;
   final int limit;

   LootingEnchantFunction(LootItemCondition[] var1, NumberProvider var2, int var3) {
      super(var1);
      this.value = var2;
      this.limit = var3;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.LOOTING_ENCHANT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(ImmutableSet.of(LootContextParams.KILLER_ENTITY), this.value.getReferencedContextParams());
   }

   boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Entity var3 = (Entity)var2.getParamOrNull(LootContextParams.KILLER_ENTITY);
      if (var3 instanceof LivingEntity) {
         int var4 = EnchantmentHelper.getMobLooting((LivingEntity)var3);
         if (var4 == 0) {
            return var1;
         }

         float var5 = (float)var4 * this.value.getFloat(var2);
         var1.grow(Math.round(var5));
         if (this.hasLimit() && var1.getCount() > this.limit) {
            var1.setCount(this.limit);
         }
      }

      return var1;
   }

   public static Builder lootingMultiplier(NumberProvider var0) {
      return new Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final NumberProvider count;
      private int limit = 0;

      public Builder(NumberProvider var1) {
         super();
         this.count = var1;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder setLimit(int var1) {
         this.limit = var1;
         return this;
      }

      public LootItemFunction build() {
         return new LootingEnchantFunction(this.getConditions(), this.count, this.limit);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<LootingEnchantFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LootingEnchantFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("count", var3.serialize(var2.value));
         if (var2.hasLimit()) {
            var1.add("limit", var3.serialize(var2.limit));
         }

      }

      public LootingEnchantFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         int var4 = GsonHelper.getAsInt(var1, "limit", 0);
         return new LootingEnchantFunction(var3, (NumberProvider)GsonHelper.getAsObject(var1, "count", var2, NumberProvider.class), var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
