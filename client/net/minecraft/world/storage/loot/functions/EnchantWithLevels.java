package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class EnchantWithLevels extends LootFunction {
   private final RandomValueRange field_186577_a;
   private final boolean field_186578_b;

   public EnchantWithLevels(LootCondition[] var1, RandomValueRange var2, boolean var3) {
      super(var1);
      this.field_186577_a = var2;
      this.field_186578_b = var3;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      return EnchantmentHelper.func_77504_a(var2, var1, this.field_186577_a.func_186511_a(var2), this.field_186578_b);
   }

   public static class Serializer extends LootFunction.Serializer<EnchantWithLevels> {
      public Serializer() {
         super(new ResourceLocation("enchant_with_levels"), EnchantWithLevels.class);
      }

      public void func_186532_a(JsonObject var1, EnchantWithLevels var2, JsonSerializationContext var3) {
         var1.add("levels", var3.serialize(var2.field_186577_a));
         var1.addProperty("treasure", var2.field_186578_b);
      }

      public EnchantWithLevels func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         RandomValueRange var4 = (RandomValueRange)JsonUtils.func_188174_a(var1, "levels", var2, RandomValueRange.class);
         boolean var5 = JsonUtils.func_151209_a(var1, "treasure", false);
         return new EnchantWithLevels(var3, var4, var5);
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
