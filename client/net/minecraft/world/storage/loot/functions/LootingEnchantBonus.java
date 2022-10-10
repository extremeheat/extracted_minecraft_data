package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootingEnchantBonus extends LootFunction {
   private final RandomValueRange field_186563_a;
   private final int field_189971_b;

   public LootingEnchantBonus(LootCondition[] var1, RandomValueRange var2, int var3) {
      super(var1);
      this.field_186563_a = var2;
      this.field_189971_b = var3;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      Entity var4 = var3.func_186492_c();
      if (var4 instanceof EntityLivingBase) {
         int var5 = EnchantmentHelper.func_185283_h((EntityLivingBase)var4);
         if (var5 == 0) {
            return var1;
         }

         float var6 = (float)var5 * this.field_186563_a.func_186507_b(var2);
         var1.func_190917_f(Math.round(var6));
         if (this.field_189971_b != 0 && var1.func_190916_E() > this.field_189971_b) {
            var1.func_190920_e(this.field_189971_b);
         }
      }

      return var1;
   }

   public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus> {
      protected Serializer() {
         super(new ResourceLocation("looting_enchant"), LootingEnchantBonus.class);
      }

      public void func_186532_a(JsonObject var1, LootingEnchantBonus var2, JsonSerializationContext var3) {
         var1.add("count", var3.serialize(var2.field_186563_a));
         if (var2.field_189971_b > 0) {
            var1.add("limit", var3.serialize(var2.field_189971_b));
         }

      }

      public LootingEnchantBonus func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         int var4 = JsonUtils.func_151208_a(var1, "limit", 0);
         return new LootingEnchantBonus(var3, (RandomValueRange)JsonUtils.func_188174_a(var1, "count", var2, RandomValueRange.class), var4);
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
