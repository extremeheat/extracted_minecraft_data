package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
   private final RandomIntGenerator levels;
   private final boolean treasure;

   private EnchantWithLevelsFunction(LootItemCondition[] var1, RandomIntGenerator var2, boolean var3) {
      super(var1);
      this.levels = var2;
      this.treasure = var3;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Random var3 = var2.getRandom();
      return EnchantmentHelper.enchantItem(var3, var1, this.levels.getInt(var3), this.treasure);
   }

   public static EnchantWithLevelsFunction.Builder enchantWithLevels(RandomIntGenerator var0) {
      return new EnchantWithLevelsFunction.Builder(var0);
   }

   // $FF: synthetic method
   EnchantWithLevelsFunction(LootItemCondition[] var1, RandomIntGenerator var2, boolean var3, Object var4) {
      this(var1, var2, var3);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantWithLevelsFunction> {
      public Serializer() {
         super(new ResourceLocation("enchant_with_levels"), EnchantWithLevelsFunction.class);
      }

      public void serialize(JsonObject var1, EnchantWithLevelsFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("levels", RandomIntGenerators.serialize(var2.levels, var3));
         var1.addProperty("treasure", var2.treasure);
      }

      public EnchantWithLevelsFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         RandomIntGenerator var4 = RandomIntGenerators.deserialize(var1.get("levels"), var2);
         boolean var5 = GsonHelper.getAsBoolean(var1, "treasure", false);
         return new EnchantWithLevelsFunction(var3, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantWithLevelsFunction.Builder> {
      private final RandomIntGenerator levels;
      private boolean treasure;

      public Builder(RandomIntGenerator var1) {
         super();
         this.levels = var1;
      }

      protected EnchantWithLevelsFunction.Builder getThis() {
         return this;
      }

      public EnchantWithLevelsFunction.Builder allowTreasure() {
         this.treasure = true;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
